package api.development.platform.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 已登录用户视图（脱敏）
 **/
@Data
public class LoginUserVO implements Serializable {

    /**
     * 用户 id
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
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 性别
     */
    private String sex;

    /**
     * 年龄
     */
    private int  age;

    /**
     * 坤币
     */
    private int  kunCoin;

    /**
     * 邮箱
     */
    private String qq;

    /**
     * 联系方式
     */
    private String telephone;

    /**
     *
     */
    private String accessKey;
    /**
     * 密钥
     */
    private String secretKey;

    private static final long serialVersionUID = 1L;
}