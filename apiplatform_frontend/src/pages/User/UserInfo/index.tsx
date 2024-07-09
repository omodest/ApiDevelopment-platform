import React, { useState, useEffect } from 'react';
import {
  getLoginUserUsingGet,
  updateAsKeyUsingGet
} from '@/services/apiplateform-backend/userController';
import {Card, Avatar, Spin, Button, message} from 'antd';
import {history} from "@@/core/history";
import {updateMyUserUsingPost} from "@/services/apiform_backend/userController";

const Login: React.FC = () => {
  const [data, setData] = useState<API.UserVO | null>(null);
  const [loading, setLoading] = useState<boolean>(true); // 添加loading状态
  const [isUpdating, setIsUpdating] = useState(false);
  const [editable, setEditable] = useState(false);
  const [reloadPage, setReloadPage] = useState(false); // 新增重新加载页面的状态
  const [editedData, setEditedData] = useState({
    userName: '',
    sex: '',
    telephone: '',
    qq: '',
    userProfile: '',
    userAvatar: ''
  });
  // 页面加载执行的钩子
  useEffect(() => {
    const fetchData = async () => {
      try {
        const result = await getLoginUserUsingGet();
        setData(result);
        setEditedData({ // 初始化抽屉数据
          userAvatar: result?.data.userAvatar || "",
          userName: result?.data.userName || '',
          sex: result?.data.sex || '',
          telephone: result?.data.telephone || '',
          qq: result?.data.qq || '',
          userProfile: result?.data.userProfile || ''
        });
        setLoading(false); // 请求成功后设置loading为false
      } catch (error) {
        console.error('Error fetching data:', error);
        setLoading(false); // 请求失败也要设置loading为false
      }
    };
    fetchData();
  }, [reloadPage]);

  // 如果数据正在加载中，可以显示一个加载中的状态
  if (loading) {
    return <Spin />;
  }
  // 修改后端传来的，日期格式
  const isoDateTimeString = data?.data?.createTime;
  const dat = new Date(isoDateTimeString);
  const year = dat.getFullYear();
  const month = String(dat.getMonth() + 1).padStart(2, '0');
  const day = String(dat.getDate()).padStart(2, '0');
  const hours = String(dat.getHours()).padStart(2, '0');
  const minutes = String(dat.getMinutes()).padStart(2, '0');
  const seconds = String(dat.getSeconds()).padStart(2, '0');
  const formattedDateTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  // 修改签名
  const updateASKey = async () => {
    if (isUpdating) return; // 如果正在更新，直接返回，不执行下面的代码
    setIsUpdating(true); // 设置为正在更新状态
    try {
      await updateAsKeyUsingGet();
      // 在成功修改后，设置一个定时器，在30秒后将按钮重新设为可点击状态
      setTimeout(() => {
        setIsUpdating(false); // 30秒后恢复按钮可点击状态
        message.success("修改成功");
      }, 1000); // 1秒 = 1000毫秒
    } catch (error) {
      // 处理错误情况
      console.error('修改失败:', error);
    }
  }
  // 回到上一个页面
  const handleGoBack = () => {
      history.push('/'); // 使用原生API返回上一个页面
  };

  // 修改个人信息
  const handleEditToggle = () => { // 展示为编辑框
    if (editable){
      setEditable(!editable);
      message.info("取消编辑");
    }else {
      setEditable(!editable);
      message.info("请开始编辑");
    }

  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEditedData(prevState => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSave = async () => { //  保存修改按钮
    try {
      await updateMyUserUsingPost(editedData);
      // 切换回非编辑状态
      setEditable(false);
      setReloadPage(prevState => !prevState); // 设置重新加载页面的状态
      message.success("修改成功");
    }catch (error) {
      // 处理错误情况
      console.error('修改失败:', error);
    }
  };

  // 如果数据加载完成，渲染用户信息卡片
  return (
    <div className="site-card-border-less-wrapper">
      <Card title="用户简介：" bordered={false} style={{ width: '100%'}}>
        {data.data && (
          <>
            <div style={{ textAlign: 'center', marginBottom: 10 }}>
              <Avatar size={200} src={data.data.userAvatar} />
            </div>
            <p><strong>Id: </strong> {data.data.id}</p>
            <p><strong>用户名: </strong>
              {editable ? (
                <input
                  type="text"
                  name="userName"
                  value={editedData.userName}
                  onChange={handleChange}
                />
              ) : (
                <span>{editedData.userName}</span>
              )}</p>
            <p><strong>性别: </strong>
              {editable ? (
                <input
                  type="text"
                  name="sex"
                  value={editedData.sex}
                  onChange={handleChange}
                />
              ) : (
                <span>{editedData.sex}</span>
              )}</p>
            <p><strong>电话号码: </strong>
              {editable ? (
                <input
                  type="text"
                  name="telephone"
                  value={editedData.telephone}
                  onChange={handleChange}
                />
              ) : (
                <span>{editedData.telephone}</span>
              )}</p>
            <p><strong>邮箱: </strong>
              {editable ? (
                <input
                  type="text"
                  name="qq"
                  value={editedData.qq}
                  onChange={handleChange}
                />
              ) : (
                <span>{editedData.qq}</span>
              )}</p>
            <p><strong>坤币: </strong>{data.data.kunCoin}</p>
            <p><strong>个人简介: </strong>
              {editable ? (
                <textarea
                  type="text"
                  name="userProfile"
                  value={editedData.userProfile}
                  onChange={handleChange}
                />
              ) : (
                <span>{editedData.userProfile}</span>
              )}</p>
            <p><strong>用户等级: </strong>{data.data.userRole === 'admin' ? '管理员' : '普通用户'}</p>
            <p><strong>注册时间: </strong>{formattedDateTime}</p>
            <p><strong>头像链接: </strong>
              {editable ? (
                <input
                  type="text"
                  name="userAvatar"
                  value={editedData.userAvatar}
                  onChange={handleChange}
                />
              ) : (
                <span>{editedData.userAvatar}</span>
              )}</p>
          </>
        )}
      </Card>

      <Card>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <Button type="primary" onClick={handleEditToggle} style={{ marginRight: 10 }}>
            点我修改个人信息
          </Button>
          {editable && (
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <Button type="primary" onClick={handleSave} style={{ marginRight: 10 }}>
                确认
              </Button>
              <Button type="primary" onClick={handleEditToggle}>
                取消
              </Button>
            </div>
          )}
        </div>

      </Card>
      <Card>
        <div>
          <Button type="primary" onClick={updateASKey} disabled={isUpdating}>
            {isUpdating ? '正在更新...' : '点我更换签名和密钥'}
          </Button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <Button type="primary">点我去充值</Button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <Button type="primary" onClick={handleGoBack}>点我回主页</Button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <Button type="primary">查看我的订单</Button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <Button type="primary">邀请好友</Button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <Button type="primary">签到</Button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </div>

      </Card>
    </div>
  );
};

export default Login;
