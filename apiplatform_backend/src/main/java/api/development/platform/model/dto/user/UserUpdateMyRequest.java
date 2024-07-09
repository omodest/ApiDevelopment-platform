package api.development.platform.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新个人信息请求
 */
@Data
public class UserUpdateMyRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}