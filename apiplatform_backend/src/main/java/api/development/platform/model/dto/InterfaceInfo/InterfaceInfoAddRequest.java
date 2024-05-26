package api.development.platform.model.dto.InterfaceInfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建请求
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 接口描述
     */
    private String interfaceDescript;

    /**
     * 接口地址
     */
    private String interfaceUrl;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responceHeader;

    /**
     * 请求类型
     */
    private String interfaceType;

}