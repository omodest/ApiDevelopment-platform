package api.development.platform.model.dto.InterfaceInfo;

import api.development.platform.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询请求
 */
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

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

    /**
     * 接口状态(0-关闭，1-开启)
     */
    private Integer interfaceStatus;

    /**
     * 用户id
     */
    private Long userId;

}