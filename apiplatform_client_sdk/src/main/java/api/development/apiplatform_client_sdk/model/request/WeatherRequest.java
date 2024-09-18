package api.development.apiplatform_client_sdk.model.request;

import api.development.apiplatform_client_sdk.model.enums.RequestMethodEnum;
import api.development.apiplatform_client_sdk.model.params.WeatherParams;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import lombok.experimental.Accessors;

/**
 *  获取天气请求
 */
@Accessors(chain = true)
public class WeatherRequest extends BaseRequest<WeatherParams, ResultResponse> {
    // 不同接口使用不同路径
    @Override
    public String getUrl() {
        return "/weather";
    }

    /**
     * 获取响应类
     */
    @Override
    public Class<ResultResponse> getResponseClass() {
        return ResultResponse.class;
    }


    @Override
    public String getMethod() {
        return RequestMethodEnum.GET.getValue();
    }
}
