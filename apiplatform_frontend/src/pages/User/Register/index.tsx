import { Footer } from '@/components';
import {
  LoginForm,
  ProFormText,
} from '@ant-design/pro-components';
import { history, SelectLang, useIntl, Helmet } from '@umijs/max';
import { message, Tabs } from 'antd';
import Settings from '../../../../config/defaultSettings';
import React, { useState } from 'react';
import { createStyles } from 'antd-style';
import {Link} from "react-router-dom";
import {
  getCaptchaUsingGet,
  userEmailRegisterUsingPost
} from "@/services/apiplateform-backend/userController";
import {ProFormCaptcha} from "@ant-design/pro-form";
import {UserOutlined} from "@ant-design/icons";
import {FormattedMessage} from "@@/exports";

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
const Lang = () => {
  const { styles } = useStyles();

  return (
    <div className={styles.lang} data-lang>
      {SelectLang && <SelectLang />}
    </div>
  );
};
const Register: React.FC = () => {
  const [type, setType] = useState<string>('account');
  const { styles } = useStyles();
  const intl = useIntl();
  const handleSubmit = async (values: API.UserEmailRegisterRequest) => {
    try {
      // 注册接口
      const res = await userEmailRegisterUsingPost({ ...values});
      if (res.data) {
        setTimeout(() => {
          history.push('/');
        },100)
        message.success("注册成功了,默认密码:12345678");
        return;
      }else {
        message.error(res.message); // 注册失败
      }
    } catch (error) {
      const defaultLoginFailureMessage = '注册失败，请重试！';
      console.log(error);
      message.error(defaultLoginFailureMessage);
    }
  };
  return (
    <div className={styles.container}>
      <Helmet>
        <title>
          {intl.formatMessage({
            id: 'menu.register',
            defaultMessage: '注册页',
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
          // 修改按钮样式
          submitter={{
            searchConfig: {
              submitText: '注册'
            }
          }}
          contentStyle={{
            minWidth: 280,
            maxWidth: '75vw',
          }}
          logo={<img alt="logo" src="/logo.jpg" />}
          title="谱啄 API"
          subTitle={
            <Link to="/user/login">
              {intl.formatMessage({ id: 'pages.login.goto' })}
            </Link>
          }

          initialValues={{
            autoLogin: true,
          }}
          onFinish={async (values) => {
            await handleSubmit(values as API.UserRegisterRequest);
          }}

        >
          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[ // 切换栏,如果需要添加 手机号注册这类，可以先在这个添加一个tab
              {
                key: 'account',
                label: intl.formatMessage({
                  id: 'pages.register.accountRegister.tab',
                  defaultMessage: '注册账号',
                }),
              },
            ]}
          />

          {type === 'account' && (
            <>
              <ProFormText
                name="userName"
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

              <ProFormText
                fieldProps={{
                  size: 'large',
                }}
                name="emailAccount"
                placeholder={'请输入邮箱账号！'}
                rules={[
                  {
                    required: true,
                    message: '邮箱账号是必填项！',
                  },
                  {
                    pattern: /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/,
                    message: '不合法的邮箱账号！',
                  },
                ]}
              />
              <ProFormText
                name="invitationCode"
                fieldProps={{
                  size: 'large',
                }}
                placeholder={'请输入邀请码,没有可不填'}
              />
              <ProFormCaptcha
                fieldProps={{
                  size: 'large',
                }}
                captchaProps={{
                  size: 'large',
                }}
                placeholder={'请输入验证码！'}
                captchaTextRender={(timing, count) => {
                  if (timing) {
                    return `${count} ${'秒后重新获取'}`;
                  }
                  return '获取验证码';
                }}
                phoneName={"emailAccount"}
                name="captcha"
                rules={[
                  {
                    required: true,
                    message: '验证码是必填项！',
                  },
                ]}
                onGetCaptcha={async (emailAccount) => {
                  const res = await getCaptchaUsingGet({ emailAccount });
                  if (res.data && res.code === 0) {
                    message.success("验证码发送成功");
                    return;
                  }
                }}
              />

            </>
          )}
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};

export default Register;
