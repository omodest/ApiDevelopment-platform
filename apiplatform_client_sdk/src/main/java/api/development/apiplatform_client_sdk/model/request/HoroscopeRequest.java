package api.development.apiplatform_client_sdk.model.request;

import api.development.apiplatform_client_sdk.model.enums.RequestMethodEnum;
import api.development.apiplatform_client_sdk.model.params.HoroscopeParams;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import lombok.experimental.Accessors;

/**
 *  随机情话
 */
@Accessors(chain = true)
public class HoroscopeRequest extends BaseRequest<HoroscopeParams, ResultResponse> {

    @Override
    public String getUrl() {
        return "/horoscope";
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
