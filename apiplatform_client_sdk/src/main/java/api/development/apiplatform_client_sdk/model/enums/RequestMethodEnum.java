package api.development.apiplatform_client_sdk.model.enums;

/**
 * 请求类型枚举
 */
public enum RequestMethodEnum {
    /**
     * GET请求
     */
    GET("GET", "GET"),
    POST("POST", "POST");
    private final String text;
    private final String value;

    RequestMethodEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
