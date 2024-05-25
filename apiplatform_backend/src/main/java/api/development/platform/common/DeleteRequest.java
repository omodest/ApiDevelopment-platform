package api.development.platform.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    // 显示声明serialVersionUID，如果没有显示声明；
    // JVM会基于类的详细信息自动生成一个，但这可能会导致在不同环境或不同版本的JVM中生成不同的值，从而引起序列化兼容性问题。
    private static final long serialVersionUID = 1L;
}