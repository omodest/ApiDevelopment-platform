package api.development.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息表
 * @TableName interface_info
 */
@TableName(value ="interface_info")
@Data
public class InterfaceInfo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
     * {"Content-Type": "application/json"}
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responceHeader;

    /**
     * 请求参数
     * [
     *  {
     *      "name": "userName",
     *      "type": "String"
     *  }
     * ]
     */
    private String requestParams;

    /**
     * 请求类型
     *
     */
    private String interfaceType;

    /**
     * 接口状态(0-关闭，1-开启)
     */
    private Integer interfaceStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    private Integer is_deleted;

    /**
     * 用户id
     */
    private Long userId;


    private String requestExample;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}