package api.development.apiplatform_client_sdk.model.request;

import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import lombok.experimental.Accessors;

/**
 *
 */
@Accessors(chain = true)
public class CurrencyRequest extends BaseRequest<Object, ResultResponse> {
    private String method;
    private String path;

    /**
     * get方法
     *
     * @return {@link String}
     */
    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 获取路径
     *
     * @return {@link String}
     */
    @Override
    public String getUrl() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取响应类
     */
    @Override
    public Class<ResultResponse> getResponseClass() {
        return ResultResponse.class;
    }
}
