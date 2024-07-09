package api.development.platform.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新请求
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

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