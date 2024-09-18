package api.development.apiplatform_client_sdk.model.request;


import api.development.apiplatform_client_sdk.model.enums.RequestMethodEnum;
import api.development.apiplatform_client_sdk.model.params.IpInfoParams;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import lombok.experimental.Accessors;

/**
 * 获取ip地址请求
 */
@Accessors(chain = true)
public class IpInfoRequest extends BaseRequest<IpInfoParams, ResultResponse> {
    // 不同请求不同路径
    @Override
    public String getUrl() {
        return "/ipInfo";
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
