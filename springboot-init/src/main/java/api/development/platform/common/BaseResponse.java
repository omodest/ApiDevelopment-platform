package api.development.platform.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 通用返回类
 * 序列化的目的是是允许对象转换为字节流，当需要在网络上传输对象，或将对象保存到文件系统中，或在不同JVM实例之间传递对象时，经常使用。
 */
@Data // lombok注解,会为类中所有的变量生成get、set、toString、equals这些方法。
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
