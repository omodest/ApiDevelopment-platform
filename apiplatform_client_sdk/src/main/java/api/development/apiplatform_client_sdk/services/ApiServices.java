package api.development.apiplatform_client_sdk.services;

import api.development.apiplatform_client_sdk.common.BusinessException;
import api.development.apiplatform_client_sdk.model.UserSignature;
import api.development.apiplatform_client_sdk.model.request.*;
import api.development.apiplatform_client_sdk.model.response.LoveResponse;
import api.development.apiplatform_client_sdk.model.response.PoisonousChickenSoupResponse;
import api.development.apiplatform_client_sdk.model.response.RandomWallpaperResponse;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;

public interface ApiServices {
    /**
     * 通用请求
     */

    <O, T extends ResultResponse> T request(BaseRequest<O, T> request) throws BusinessException;

    /**
     * 通用请求
     */
    <O, T extends ResultResponse> T request(UserSignature userSignature, BaseRequest<O, T> request) throws BusinessException;


    /**
     * 随机毒鸡汤
     */
    PoisonousChickenSoupResponse getPoisonousChickenSoup() throws BusinessException;

    /**
     * 喝毒鸡汤
     */
    PoisonousChickenSoupResponse getPoisonousChickenSoup(UserSignature userSignature) throws BusinessException;

    /**
     * 获取随机壁纸
     */
    RandomWallpaperResponse getRandomWallpaper(RandomWallpaperRequest request) throws BusinessException;

    /**
     * 获取随机壁纸
     */
    RandomWallpaperResponse getRandomWallpaper(UserSignature userSignature, RandomWallpaperRequest request) throws BusinessException;

    /**
     * 随意情话
     */
    LoveResponse randomLoveTalk() throws BusinessException;

    /**
     * 随意情话
     */
    LoveResponse randomLoveTalk(UserSignature userSignature) throws BusinessException;

    /**
     * 星座运势
     */
    ResultResponse horoscope(HoroscopeRequest request) throws BusinessException;

    /**
     * 星座运势
     */
    ResultResponse horoscope(UserSignature userSignature, HoroscopeRequest request) throws BusinessException;

    /**
     * 获取ip信息
     */
    ResultResponse getIpInfo(UserSignature userSignature, IpInfoRequest request) throws BusinessException;

    /**
     * 获取ip信息
     */
    ResultResponse getIpInfo(IpInfoRequest request) throws BusinessException;

    /**
     * 获取天气信息
     */
    ResultResponse getWeatherInfo(UserSignature userSignature, WeatherRequest request) throws BusinessException;

    /**
     * 获取天气信息
     */
    ResultResponse getWeatherInfo(WeatherRequest request) throws BusinessException;
}
