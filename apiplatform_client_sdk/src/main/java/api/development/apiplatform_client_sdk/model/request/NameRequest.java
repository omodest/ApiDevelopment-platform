package api.development.apiplatform_client_sdk.model.request;


import api.development.apiplatform_client_sdk.model.enums.RequestMethodEnum;
import api.development.apiplatform_client_sdk.model.params.NameParams;
import api.development.apiplatform_client_sdk.model.response.NameResponse;
import lombok.experimental.Accessors;

/**
 * 获取用户名 demo
 */
@Accessors(chain = true)
public class NameRequest extends BaseRequest<NameParams, NameResponse> {
    // 不同请求不同路径
    @Override
    public String getUrl() {
        return "/name";
    }

    /**
     * 获取响应类
     */
    @Override
    public Class<NameResponse> getResponseClass() {
        return NameResponse.class;
    }


    @Override
    public String getMethod() {
        return RequestMethodEnum.GET.getValue();
    }
}
