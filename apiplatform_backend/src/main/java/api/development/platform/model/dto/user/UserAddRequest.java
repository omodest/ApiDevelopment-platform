package api.development.platform.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户创建请求
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

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
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}