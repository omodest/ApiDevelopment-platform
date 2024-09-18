package api.development.apiplatform_interface.controller;

import api.development.apiplatform_client_sdk.common.BusinessException;
import api.development.apiplatform_client_sdk.model.params.*;
import api.development.apiplatform_client_sdk.model.response.NameResponse;
import api.development.apiplatform_client_sdk.model.response.RandomWallpaperResponse;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import cn.hutool.json.JSONUtil;
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
 */
@RestController
@RequestMapping("/")
@Slf4j
public class ServiceController {
    @GetMapping("/name")
    public NameResponse getName(NameParams nameParams) {
        return JSONUtil.toBean(JSONUtil.toJsonStr(nameParams), NameResponse.class);
    }

    @GetMapping("/loveTalk")
    public String randomLoveTalk() {
        return get("https://api.vvhan.com/api/love");
    }

    @GetMapping("/poisonousChickenSoup")
    public String getPoisonousChickenSoup() {
        return get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
    }

    @GetMapping("/randomWallpaper")
    public RandomWallpaperResponse randomWallpaper(RandomWallpaperParams randomWallpaperParams) throws BusinessException {
        String baseUrl = "https://api.btstu.cn/sjbz/api.php";
        String url = buildUrl(baseUrl, randomWallpaperParams);
        if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
            url = url + "?format=json";
        } else {
            url = url + "&format=json";
        }
        return JSONUtil.toBean(get(url), RandomWallpaperResponse.class);
    }
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

    @GetMapping("/horoscope")
    public ResultResponse getHoroscope(HoroscopeParams horoscopeParams) throws BusinessException {
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

    @GetMapping("/ipInfo")
    public ResultResponse getIpInfo(IpInfoParams ipInfoParams) {
        return baseResponse("https://api.vvhan.com/api/getIpInfo", ipInfoParams);
    }

    @GetMapping("/weather")
    public ResultResponse getWeatherInfo(WeatherParams weatherParams) {
        return baseResponse("https://api.vvhan.com/api/weather", weatherParams);
    }
}

