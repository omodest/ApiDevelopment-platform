package api.development.apiplatform_client_sdk.model.request;

import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * api统一前置请求
 * @param <O>
 * @param <T> 必须是 ResultResponse的子类
 */
public abstract class BaseRequest <O, T extends ResultResponse>{

    private Map<String, Object> requestParams = new HashMap<>();

    /**
     * 获取请求类型
     */
    public abstract String getMethod();

    /**
     * 拿到请求路径
     */
    public abstract String getUrl();

    /**
     * 获取响应类
     */
    public abstract Class<T> getResponseClass();

    @JsonAnyGetter // 将Java对象序列化为JSON时动态地添加属性
    public Map<String, Object> getRequestParams(){
        return  requestParams;
    }

    public void setRequestParams(O params) {
        this.requestParams = new Gson().fromJson(JSONUtil.toJsonStr(params), new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}
