package api.development.platform.model.dto.user;

import api.development.platform.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 */
@EqualsAndHashCode(callSuper = true) // 可以自动生成equals和hashCode方法，且比@Data注解提供了更多的自定义选项。
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 开放平台id
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String mpOpenId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 性别
     */
    private String sex;

    /**
     * 年龄
     */
    private int  age;

    /**
     * 邮箱
     */
    private String qq;

    /**
     * 联系方式
     */
    private String telephone;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}