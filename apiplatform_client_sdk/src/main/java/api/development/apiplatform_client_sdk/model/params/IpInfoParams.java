package api.development.apiplatform_client_sdk.model.params;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 获取ip地址请求参数
 */
@Data
@Accessors(chain = true)
public class IpInfoParams implements Serializable {

    private static final long serialVersionUID = 3815188540434269370L;

    private String ip;
}
