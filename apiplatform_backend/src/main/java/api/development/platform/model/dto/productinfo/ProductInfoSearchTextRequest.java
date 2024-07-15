package api.development.platform.model.dto.productinfo;

import api.development.platform.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Description: 产品信息搜索文本请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductInfoSearchTextRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = -6337349622479990038L;

    private String searchText;
}