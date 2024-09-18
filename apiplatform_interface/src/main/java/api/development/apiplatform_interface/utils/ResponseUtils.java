package api.development.apiplatform_interface.utils;

import api.development.apiplatform_client_sdk.common.BusinessException;
import api.development.apiplatform_client_sdk.common.ErrorCode;
import api.development.apiplatform_client_sdk.model.response.ResultResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import static api.development.apiplatform_interface.utils.RequestUtils.get;

public class ResponseUtils {
    public static Map<String, Object> responseToMap(String response) {
        return new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static <T> ResultResponse baseResponse(String baseUrl, T params) {
        String response = null;
        try {
            response = get(baseUrl, params);
            Map<String, Object> fromResponse = responseToMap(response);
            boolean success = (boolean) fromResponse.get("success");
            ResultResponse baseResponse = new ResultResponse();
            if (!success) {
                baseResponse.setData(fromResponse);
                return baseResponse;
            }
            fromResponse.remove("success");
            baseResponse.setData(fromResponse);
            return baseResponse;
        } catch (BusinessException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "构建url异常");
        }
    }
}

