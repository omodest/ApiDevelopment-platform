package api.development.apiplatform_client_sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前用户的签名和密钥
 * 每个client,都需要有这两个字段
 */
@Data // 提供字段的get set 方法
@AllArgsConstructor
@NoArgsConstructor
public class UserSignature {

    private String accessKey;

    private String secretKey;
}
