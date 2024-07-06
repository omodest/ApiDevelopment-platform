package api.development.apiplatform_gateway;

import api.development.apiplatform_client_sdk.utils.SignUtils;
import api.development.apiplatform_interface.model.entity.InterfaceInfo;
import api.development.apiplatform_interface.model.entity.User;
import api.development.apiplatform_interface.service.InnerInterfaceInfoService;
import api.development.apiplatform_interface.service.InnerUserInterfaceInfoService;
import api.development.apiplatform_interface.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 全局过滤器
 */
@Slf4j
@Component
public class CustomGlobalFilter  implements
        GlobalFilter, // 全局异常接口
        Ordered // 执行顺序
{
    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8123";



    // 可以通过后台访问api网关项目 在调用模拟接口，说明api网关设置成功

    /**
     * 过滤器逻辑
     * @param exchange 路由交换机，所有的请求、响应信息、请求体都能从这里获取
     * @param chain 责任负责链，因为我们的所有过滤器是按照从上到下的顺序依次执行，形成了一个链条。所以这里用了一个 chain ，
     *              如果当前过滤器对请求进行了过滤后发现可以放行，就要调用责任链中的 next 方法，相当于直接找到下一个过滤器，
     *              这里称为 filter 。有时候我们需要在责任链中使用 next，而在这里它使用了 filter 来找到下一个过滤器，
     *              从而正常地放行请求。
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        // 2. 访问控制 - 黑白名单
        ServerHttpResponse response = exchange.getResponse();
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 3. 用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        // 获取当前用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }

        if (Long.parseLong(nonce) > 10000L) {
            return handleNoAuth(response);
        }
        // 时间和当前时间不能超过 5 分钟
        Long currentTime = System.currentTimeMillis() / 1000; // 拿到时间戳
        final Long FIVE_MINUTES = 60 * 5L; // 5分钟时间间隔
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        // 实际情况中是从数据库中查出 secretKey
        String serverSign = invokeUser.getSecretKey();
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response);
        }
        // 4. 判断接口是否存在
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }

        // 5. 请求转发，调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);
        // 响应日志
//        log.info("接口状态：" + response.getStatusCode());
//        if (response.getStatusCode() == HttpStatus.OK){
//
//        }else {
//            return handleInvokeError(response); // 调用失败，返回状态码
//        }

        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
    }

    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * 用来抛出无权限的状态码
     * @param response
     * @return
     */
    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * 用来抛出错误 500状态码
     * @param response
     * @return
     */
    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }


}
