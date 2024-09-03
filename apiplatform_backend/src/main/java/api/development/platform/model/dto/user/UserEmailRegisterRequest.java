package api.development.platform.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮箱注册请求
 */
@Data
public class UserEmailRegisterRequest implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * QQ 邮箱账号
     */
    private String emailAccount;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 邀请码
     */
    private String invitationCode;

}