package api.development.apiplatform_interface.controller;

import api.development.apiplatform_client_sdk.common.BusinessException;
import api.development.apiplatform_client_sdk.model.params.*;
import api.development.apiplatform_client_sdk.model.response.NameResponse;
import api.development.apiplatform_client_sdk.model.response.RandomWallpaperResponse;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static api.development.apiplatform_interface.utils.RequestUtils.buildUrl;
import static api.development.apiplatform_interface.utils.RequestUtils.get;
import static api.development.apiplatform_interface.utils.ResponseUtils.baseResponse;
import static api.development.apiplatform_interface.utils.ResponseUtils.responseToMap;

/**
 * 后端通过这里调用SDK里的各种接口
 *
 * http://localhost:8123/swagger-ui.html
 */
@RestController
@RequestMapping("/")
@Slf4j
public class ServiceController {

    @Operation(summary = "获取名字", description = "根据提供的参数返回名字响应")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功返回名字"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @GetMapping("/name")
    public NameResponse getName(@Parameter(description = "名字参数", in = ParameterIn.QUERY) NameParams nameParams) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(nameParams), NameResponse.class);
    }

    @Operation(summary = "随机爱情语录", description = "获取随机的爱情语录")
    @GetMapping("/loveTalk")
    public String randomLoveTalk() {
        return get("https://api.vvhan.com/api/love");
    }

    @Operation(summary = "获取毒鸡汤", description = "获取随机的毒鸡汤")
    @GetMapping("/poisonousChickenSoup")
    public String getPoisonousChickenSoup() {
        return get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
    }

    @Operation(summary = "获取随机壁纸", description = "根据提供的参数返回随机壁纸")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功返回壁纸"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("/randomWallpaper")
    public RandomWallpaperResponse randomWallpaper(@Parameter(description = "壁纸参数") RandomWallpaperParams randomWallpaperParams) throws BusinessException {
        String baseUrl = "https://api.btstu.cn/sjbz/api.php";
        String url = buildUrl(baseUrl, randomWallpaperParams);
        if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
            url = url + "?format=json";
        } else {
            url = url + "&format=json";
        }
        return JSONUtil.toBean(get(url), RandomWallpaperResponse.class);
    }

    @Operation(summary = "获取随机壁纸（POST）", description = "根据提供的参数返回随机壁纸")
    @PostMapping("/randomWallpaper")
    public RandomWallpaperResponse postAndomWallpaper(@RequestBody RandomWallpaperParams randomWallpaperParams) throws BusinessException {
        String baseUrl = "https://api.btstu.cn/sjbz/api.php";
        String url = buildUrl(baseUrl, randomWallpaperParams);
        if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
            url = url + "?format=json";
        } else {
            url = url + "&format=json";
        }
        return JSONUtil.toBean(get(url), RandomWallpaperResponse.class);
    }

    @Operation(summary = "获取星座运势", description = "根据提供的参数返回星座运势")
    @GetMapping("/horoscope")
    public ResultResponse getHoroscope(@Parameter(description = "星座参数") HoroscopeParams horoscopeParams) throws BusinessException {
        String response = get("https://api.vvhan.com/api/horoscope", horoscopeParams);
        Map<String, Object> fromResponse = responseToMap(response);
        boolean success = (boolean) fromResponse.get("success");
        if (!success) {
            ResultResponse baseResponse = new ResultResponse();
            baseResponse.setData(fromResponse);
            return baseResponse;
        }
        return JSONUtil.toBean(response, ResultResponse.class);
    }

    @Operation(summary = "获取IP信息", description = "根据提供的参数获取IP信息")
    @GetMapping("/ipInfo")
    public ResultResponse getIpInfo(@Parameter(description = "IP参数") IpInfoParams ipInfoParams) {
        return baseResponse("https://api.vvhan.com/api/getIpInfo", ipInfoParams);
    }

    @Operation(summary = "获取天气信息", description = "根据提供的参数获取天气信息")
    @GetMapping("/weather")
    public ResultResponse getWeatherInfo(@Parameter(description = "天气参数") WeatherParams weatherParams) {
        return baseResponse("https://api.vvhan.com/api/weather", weatherParams);
    }
}
