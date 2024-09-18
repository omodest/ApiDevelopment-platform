package api.development.apiplatform_client_sdk.model.request;


import api.development.apiplatform_client_sdk.model.enums.RequestMethodEnum;
import api.development.apiplatform_client_sdk.model.params.RandomWallpaperParams;
import api.development.apiplatform_client_sdk.model.response.RandomWallpaperResponse;
import lombok.experimental.Accessors;

/**
 * 随机壁纸
 */
@Accessors(chain = true)
public class RandomWallpaperRequest extends BaseRequest<RandomWallpaperParams, RandomWallpaperResponse> {
    // 不同请求不同路径
    @Override
    public String getUrl() {
        return "/randomWallpaper";
    }

    /**
     * 获取响应类
     */
    @Override
    public Class<RandomWallpaperResponse> getResponseClass() {
        return RandomWallpaperResponse.class;
    }


    @Override
    public String getMethod() {
        return RequestMethodEnum.GET.getValue();
    }
}
