package api.development.apiplatform_client_sdk.services;

import api.development.apiplatform_client_sdk.common.BusinessException;
import api.development.apiplatform_client_sdk.common.ErrorCode;
import api.development.apiplatform_client_sdk.model.UserSignature;
import api.development.apiplatform_client_sdk.model.request.BaseRequest;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import api.development.apiplatform_client_sdk.utils.SignUtils;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用API接口时的公共服务
 */
@Slf4j
@Data
public abstract class BaseServices implements ApiServices {

    // 签名字段
    private UserSignature userSignature;

    /**
     * todo 后续换成真实网关HOST
     */
    private String gatewayHost = "https://locahot:8111/api";
//    private String gatewayHost = "https://gateway.qimuu.icu/api";

    /**
     * 签名校验
     */
    public void checkSignature(UserSignature userSignature){
        if (userSignature == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "请检查是否传递用户签名");
        }
        if (!StringUtils.isAnyBlank(userSignature.getAccessKey(),userSignature.getSecretKey()) ){
            this.setUserSignature(userSignature);
        }else {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户签名或密钥存在空");
        }
    }

    /**
     * 通过请求方法获取http 响应
     */
    private <O, T extends ResultResponse> HttpRequest getHttpRequestBuRequestMethod(BaseRequest<O, T> request){
        // 1. 参数校验
        if (ObjectUtils.isEmpty(request)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String method = request.getMethod();
        String url = request.getUrl();
        if (StringUtils.isAnyBlank(method,url)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求类型或请求路径为空");
        }
        // 2.  拿到请求参数，例如这里的：/aaa。 a.com/api/aaa
        url = url.substring(gatewayHost.length());
        log.info("请求方法：{}，请求路径：{}，请求参数：{}", method, url, request.getRequestParams());
        // 3. 判断请求类型
        HttpRequest httpRequest;
        switch (method){
            case "GET":
                httpRequest = HttpRequest.get(splicingGetRequest(request, url));
                break;
            case "POST":
                httpRequest = HttpRequest.post(gatewayHost + url);
                break;
            default: {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持该请求");
            }
        }
        /**
         * 解决：.....反射.....
         */
        httpRequest.addHeaders(this.getHeaders(JSONUtil.toJsonStr(request), this.userSignature));
        if (method.equals("POST")) {
            httpRequest.body(JSONUtil.toJsonStr(request.getRequestParams()));
        }
        return httpRequest;
    }

    /**
     * 拼接Get请求
     */
    private <O, T extends ResultResponse> String splicingGetRequest(BaseRequest<O, T> request, String url) {
        StringBuilder urlBuilder = new StringBuilder(gatewayHost);
        // urlBuilder最后是/结尾且path以/开头的情况下，去掉urlBuilder结尾的/
        if (urlBuilder.toString().endsWith("/") && url.startsWith("/")) {
            urlBuilder.setLength(urlBuilder.length() - 1);
        }
        urlBuilder.append(url);
        if (!request.getRequestParams().isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, Object> entry : request.getRequestParams().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                urlBuilder.append(key).append("=").append(value).append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        log.info("GET请求路径：{}", urlBuilder);
        return urlBuilder.toString();
    }

    /**
     * 执行请求
     */
    private <O, T extends ResultResponse> HttpResponse doRequest(BaseRequest<O, T> request) throws BusinessException {
        try (HttpResponse httpResponse = getHttpRequestBuRequestMethod(request).execute()) {
            return httpResponse;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }


    /**
     * 获取请求头
     */
    private Map<String, String> getHeaders(String body, UserSignature userSignature) {
        Map<String, String> hashMap = new HashMap<>(4);
        hashMap.put("accessKey", userSignature.getAccessKey());
        String encodedBody = SecureUtil.md5(body);
        hashMap.put("body", encodedBody);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", SignUtils.getSignUtils(encodedBody, userSignature.getSecretKey()));
        return hashMap;
    }

    /**
     * 获取响应数据
     */
    public <O, T extends ResultResponse> T res(BaseRequest<O, T> request)  {
        if (userSignature == null || StringUtils.isAnyBlank(userSignature.getAccessKey(), userSignature.getSecretKey())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "请先配置密钥AccessKey/SecretKey");
        }
        T rsp;
        try {
            Class<T> clazz = request.getResponseClass();
            rsp = clazz.newInstance();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
        HttpResponse httpResponse = doRequest(request);
        String body = httpResponse.body();
        Map<String, Object> data = new HashMap<>();
        if (httpResponse.getStatus() != 200) {
            ErrorCode errorResponse = JSONUtil.toBean(body, ErrorCode.class);
            data.put("errorMessage", errorResponse.getMessage());
            data.put("code", errorResponse.getCode());
        } else {
            try {
                // 尝试解析为JSON对象
                data = new Gson().fromJson(body, new TypeToken<Map<String, Object>>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                // 解析失败，将body作为普通字符串处理
                data.put("value", body);
            }
        }
        rsp.setData(data);
        return rsp;
    }

    @Override
    public <O, T extends ResultResponse> T request(BaseRequest<O, T> request){
        try {
            return res(request);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    @Override
    public <O, T extends ResultResponse> T request(UserSignature userSignature, BaseRequest<O, T> request)  {
        checkSignature(userSignature);
        return request(request);
    }
}
