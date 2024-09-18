package api.development.apiplatform_client_sdk.model.request;


import api.development.apiplatform_client_sdk.model.enums.RequestMethodEnum;
import api.development.apiplatform_client_sdk.model.response.LoveResponse;
import lombok.experimental.Accessors;

/**
 *  随机情话
 */
@Accessors(chain = true)
public class LoveRequest extends BaseRequest<String, LoveResponse> {
    // 不同请求不同路径
    @Override
    public String getUrl() {
        return "/loveTalk";
    }

    /**
     * 获取响应类
     */
    @Override
    public Class<LoveResponse> getResponseClass() {
        return LoveResponse.class;
    }


    @Override
    public String getMethod() {
        return RequestMethodEnum.GET.getValue();
    }
}
