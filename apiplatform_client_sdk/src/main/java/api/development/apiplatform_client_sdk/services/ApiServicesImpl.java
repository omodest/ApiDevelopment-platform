package api.development.apiplatform_client_sdk.services;


import api.development.apiplatform_client_sdk.common.BusinessException;
import api.development.apiplatform_client_sdk.model.UserSignature;
import api.development.apiplatform_client_sdk.model.request.*;
import api.development.apiplatform_client_sdk.model.response.LoveResponse;
import api.development.apiplatform_client_sdk.model.response.PoisonousChickenSoupResponse;
import api.development.apiplatform_client_sdk.model.response.RandomWallpaperResponse;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;

public class ApiServicesImpl extends BaseServices implements ApiServices {

    @Override
    public PoisonousChickenSoupResponse getPoisonousChickenSoup() throws BusinessException {
        PoisonousChickenSoupRequest request = new PoisonousChickenSoupRequest();
        return request(request);
    }

    @Override
    public PoisonousChickenSoupResponse getPoisonousChickenSoup(UserSignature userSignature) throws BusinessException {
        PoisonousChickenSoupRequest request = new PoisonousChickenSoupRequest();
        return request(userSignature, request);
    }

    @Override
    public RandomWallpaperResponse getRandomWallpaper(RandomWallpaperRequest request) throws BusinessException {
        return request(request);
    }

    @Override
    public RandomWallpaperResponse getRandomWallpaper(UserSignature userSignature, RandomWallpaperRequest request) throws BusinessException {
        return request(userSignature, request);
    }

    @Override
    public LoveResponse randomLoveTalk() throws BusinessException {
        LoveRequest request = new LoveRequest();
        return request(request);
    }

    @Override
    public LoveResponse randomLoveTalk(UserSignature userSignature) throws BusinessException {
        LoveRequest request = new LoveRequest();
        return request(userSignature, request);
    }

    @Override
    public ResultResponse horoscope(HoroscopeRequest request) throws BusinessException {
        return request(request);
    }

    @Override
    public ResultResponse horoscope(UserSignature userSignature, HoroscopeRequest request) throws BusinessException {
        return request(userSignature, request);
    }

    @Override
    public ResultResponse getIpInfo(UserSignature userSignature, IpInfoRequest request) throws BusinessException {
        return request(userSignature, request);
    }

    @Override
    public ResultResponse getIpInfo(IpInfoRequest request) throws BusinessException {
        return request(request);
    }

    @Override
    public ResultResponse getWeatherInfo(UserSignature userSignature, WeatherRequest request) throws BusinessException {
        return request(userSignature, request);
    }

    @Override
    public ResultResponse getWeatherInfo(WeatherRequest request) throws BusinessException {
        return request(request);
    }
}
