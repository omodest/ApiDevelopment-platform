package api.development.apiplatform_client_sdk.model.request;


import api.development.apiplatform_client_sdk.model.enums.RequestMethodEnum;
import api.development.apiplatform_client_sdk.model.params.PoisonousChickenSoupParams;
import api.development.apiplatform_client_sdk.model.response.PoisonousChickenSoupResponse;
import lombok.experimental.Accessors;

/**
 * 鸡汤
 */
@Accessors(chain = true)
public class PoisonousChickenSoupRequest extends BaseRequest<PoisonousChickenSoupParams, PoisonousChickenSoupResponse> {
    // 不同请求不同路径
    @Override
    public String getUrl() {
        return "/poisonousChickenSoup";
    }

    /**
     * 获取响应类
     */
    @Override
    public Class<PoisonousChickenSoupResponse> getResponseClass() {
        return PoisonousChickenSoupResponse.class;
    }

    @Override
    public String getMethod() {
        return RequestMethodEnum.GET.getValue();
    }
}
