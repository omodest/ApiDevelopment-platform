package api.development.apiplatform_client_sdk.model.params;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *  随机壁纸参数
 */
@Data
@Accessors(chain = true)
public class RandomWallpaperParams implements Serializable {

    private static final long serialVersionUID = 3815188540434269370L;

    private String lx;

    private String method;
}
