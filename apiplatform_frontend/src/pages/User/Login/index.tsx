import { Footer } from '@/components';
import { Tooltip } from 'antd';
import { getFakeCaptcha } from '@/services/ant-design-pro/login';
import {
  AlipayCircleOutlined,
  LockOutlined,
  MobileOutlined,
  TaobaoCircleOutlined,
  UserOutlined,
  WeiboCircleOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormCaptcha,
  ProFormText,
} from '@ant-design/pro-components';
import { FormattedMessage, history, SelectLang, useIntl, useModel, Helmet } from '@umijs/max';
import { Alert, message, Tabs } from 'antd';
import Settings from '../../../../config/defaultSettings';
import React, { useState } from 'react';
import { createStyles } from 'antd-style';
import {userLoginUsingPost} from "@/services/apiform_backend/userController";
import {Link} from "react-router-dom";

const useStyles = createStyles(({ token }) => {
  return {
    action: {
      marginLeft: '8px',
      color: 'rgba(0, 0, 0, 0.2)',
      fontSize: '24px',
      verticalAlign: 'middle',
      cursor: 'pointer',
      transition: 'color 0.3s',
      '&:hover': {
        color: token.colorPrimaryActive,
      },
    },
    lang: {
      width: 42,
      height: 42,
      lineHeight: '42px',
      position: 'fixed',
      right: 16,
      borderRadius: token.borderRadius,
      ':hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
    container: {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      backgroundImage:
        "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
      backgroundSize: '100% 100%',
    },
  };
});

const ActionIcons = () => {
  const { styles } = useStyles();

  return (
    <>
      <AlipayCircleOutlined key="AlipayCircleOutlined" className={styles.action} />
      <TaobaoCircleOutlined key="TaobaoCircleOutlined" className={styles.action} />
      <WeiboCircleOutlined key="WeiboCircleOutlined" className={styles.action} />
    </>
  );
};

const Lang = () => {
  const { styles } = useStyles();

  return (
    <div className={styles.lang} data-lang>
      {SelectLang && <SelectLang />}
    </div>
  );
};

const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => {
  return (
    <Alert
      style={{
        marginBottom: 24,
      }}
      message={content}
      type="error"
      showIcon
    />
  );
};

const Login: React.FC = () => {
  const [userLoginState] = useState<API.LoginResult>({});
  const [type, setType] = useState<string>('account');
  const { initialState, setInitialState } = useModel('@@initialState');
  const { styles } = useStyles();
  const intl = useIntl();
  const handleSubmit = async (values: API.UserLoginRequest) => {
    try {
      // 尝试登录
      const res = await userLoginUsingPost({ ...values });

      if (res.data) {
        // 登录成功
        const urlParams = new URL(window.location.href).searchParams;
        setTimeout(() => {
          history.push(urlParams.get('redirect') || '/');
        }, 100);

        setInitialState({
          loginUser: res.data,
        });
      }
      else if (values.userPassword?.length < 8){
        // 登录失败，提示用户
        message.error('密码长度不够！');
      }
      else {
        // 登录失败，提示用户
        message.error('用户名或密码错误，请重试！');
      }
    } catch (error) {
      // 处理登录异常情况
      const defaultLoginFailureMessage = '登录失败，请重试！';
      console.log(error);
      message.error(defaultLoginFailureMessage);
    }
  };

  const { status, type: loginType } = userLoginState;

  return (
    <div className={styles.container}>
      <Helmet>
        <title>
          {intl.formatMessage({
            id: 'menu.login',
            defaultMessage: '登录页',
          })}
          - {Settings.title}
        </title>
      </Helmet>
      <Lang />
      <div
        style={{
          flex: '1',
          padding: '32px 0',
        }}
      >
        <LoginForm
          contentStyle={{
            minWidth: 280,
            maxWidth: '75vw',
          }}
          logo={<img alt="logo" src="/logo.jpg" />}
            title="谱啄 API"
          subTitle={intl.formatMessage({ id: 'pages.layouts.userLayout.title' })}
          initialValues={{
            autoLogin: true,
          }}
          actions={[
            <FormattedMessage
              key="loginWith"
              id="pages.login.loginWith"
              defaultMessage="其他登录方式"
            />,
            <ActionIcons key="icons" />,
          ]}
          onFinish={async (values) => {
            await handleSubmit(values as API.UserLoginRequest);
          }}
        >
          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'account',
                label: intl.formatMessage({
                  id: 'pages.login.accountLogin.tab',
                  defaultMessage: '账户密码登录',
                }),
              },
              // {
              //   key: 'mobile',
              //   label: intl.formatMessage({
              //     id: 'pages.login.phoneLogin.tab',
              //     defaultMessage: 'QQ邮箱登录',
              //   }),
              // },
            ]}
          />

          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                placeholder={intl.formatMessage({
                  id: 'pages.login.username.placeholder',
                  defaultMessage: '请输入用户名',
                })}
                rules={[
                  {
                    required: true,
                    message: (
                      <FormattedMessage
                        id="pages.login.username.required"
                        defaultMessage="请输入用户名!"
                      />
                    ),
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder={intl.formatMessage({
                  id: 'pages.login.password.placeholder',
                  defaultMessage: '请输入密码  ',
                })}
                rules={[
                  {
                    required: true,
                    message: (
                      <FormattedMessage
                        id="pages.login.password.required"
                        defaultMessage="请输入密码！"
                      />
                    ),
                  },
                ]}
              />
            </>
          )}

          {/*{status === 'error' && loginType === 'mobile' && <LoginMessage content="验证码错误" />}*/}
          {/*{type === 'mobile' && (*/}
          {/*  <>*/}
          {/*    <ProFormText*/}
          {/*      fieldProps={{*/}
          {/*        size: 'large',*/}
          {/*        prefix: <MobileOutlined />,*/}
          {/*      }}*/}
          {/*      name="mobile"*/}
          {/*      placeholder={intl.formatMessage({*/}
          {/*        id: 'pages.login.phoneNumber.placeholder',*/}
          {/*        defaultMessage: 'QQ邮箱',*/}
          {/*      })}*/}
          {/*      rules={[*/}
          {/*        {*/}
          {/*          required: true,*/}
          {/*          message: (*/}
          {/*            <FormattedMessage*/}
          {/*              id="pages.login.phoneNumber.required"*/}
          {/*              defaultMessage="请输入QQ邮箱！"*/}
          {/*            />*/}
          {/*          ),*/}
          {/*        },*/}
          {/*        {*/}
          {/*          pattern: /^[1-9][0-9]{4,14}@qq\.com$/,*/}
          {/*          message: (*/}
          {/*            <FormattedMessage*/}
          {/*              id="pages.login.phoneNumber.invalid"*/}
          {/*              defaultMessage="QQ邮箱格式错误！"*/}
          {/*            />*/}
          {/*          ),*/}
          {/*        },*/}
          {/*      ]}*/}
          {/*    />*/}
          {/*    <ProFormCaptcha*/}
          {/*      fieldProps={{*/}
          {/*        size: 'large',*/}
          {/*        prefix: <LockOutlined />,*/}
          {/*      }}*/}
          {/*      captchaProps={{*/}
          {/*        size: 'large',*/}
          {/*      }}*/}
          {/*      placeholder={intl.formatMessage({*/}
          {/*        id: 'pages.login.captcha.placeholder',*/}
          {/*        defaultMessage: '请输入验证码',*/}
          {/*      })}*/}
          {/*      captchaTextRender={(timing, count) => {*/}
          {/*        if (timing) {*/}
          {/*          return `${count} ${intl.formatMessage({*/}
          {/*            id: 'pages.getCaptchaSecondText',*/}
          {/*            defaultMessage: '获取验证码',*/}
          {/*          })}`;*/}
          {/*        }*/}
          {/*        return intl.formatMessage({*/}
          {/*          id: 'pages.login.phoneLogin.getVerificationCode',*/}
          {/*          defaultMessage: '获取验证码',*/}
          {/*        });*/}
          {/*      }}*/}
          {/*      name="captcha"*/}
          {/*      rules={[*/}
          {/*        {*/}
          {/*          required: true,*/}
          {/*          message: (*/}
          {/*            <FormattedMessage*/}
          {/*              id="pages.login.captcha.required"*/}
          {/*              defaultMessage="请输入验证码！"*/}
          {/*            />*/}
          {/*          ),*/}
          {/*        },*/}
          {/*      ]}*/}
          {/*      onGetCaptcha={async (phone) => {*/}
          {/*        const result = await getFakeCaptcha({*/}
          {/*          phone,*/}
          {/*        });*/}
          {/*        if (!result) {*/}
          {/*          return;*/}
          {/*        }*/}
          {/*        message.success('获取验证码成功！注册成功后密码：12345678，记得及时修改');*/}
          {/*      }}*/}
          {/*    />*/}
          {/*  </>*/}
          {/*)}*/}
          <div
            style={{
              marginBottom: 24,
              display: 'flex',
              justifyContent: 'space-between', // 保持两侧对齐，可根据需要调整
              alignItems: 'center', // 垂直居中对齐
            }}
          >
            {/*<ProFormCheckbox noStyle name="autoLogin">*/}
            {/*  <FormattedMessage id="pages.login.rememberMe" defaultMessage="自动登录" />*/}
            {/*</ProFormCheckbox>*/}

            {/* 跳转到注册页面 */}
            <div style={{ textAlign: 'left', flex: 1 }}>
              <Link to="/user/register">
                <FormattedMessage id="page.register.goto" defaultMessage="去注册" />
              </Link>
            </div>

            <Tooltip title={<FormattedMessage id="pages.login.contactAdmin" defaultMessage="qq:2500822924" />}>
              <a
                href="https://www.douyin.com"
                target="_blank"
                rel="noopener noreferrer"
                style={{
                  float: 'right',
                  textDecoration: 'none', // 移除下划线
                  color: '#333', // 默认文字颜色
                }}
              >
                <FormattedMessage id="pages.login.forgotPassword" defaultMessage="忘记密码" />
              </a>
            </Tooltip>


          </div>

        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
