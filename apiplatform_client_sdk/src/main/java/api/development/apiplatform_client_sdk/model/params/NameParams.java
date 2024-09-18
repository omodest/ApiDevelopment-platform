package api.development.apiplatform_client_sdk.model.params;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 获取用户名 demo
 */
@Data
@Accessors(chain = true)
public class NameParams implements Serializable {
    private static final long serialVersionUID = 3815188540434269370L;
    private String name;
}
