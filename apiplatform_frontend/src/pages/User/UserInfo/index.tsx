import React, { useState, useEffect } from 'react';
import {
  getLoginUserUsingGet,
  updateAsKeyUsingGet
} from '@/services/apiplateform-backend/userController';
import {Card, Spin, Button, message, Upload, UploadFile, Modal, Descriptions, UploadProps} from 'antd';
import {history} from "@@/core/history";
import {updateMyUserUsingPost} from "@/services/apiform_backend/userController";
import {RcFile} from "antd/es/upload";
import {PlusOutlined} from "@ant-design/icons";
import ImgCrop from "antd-img-crop";
import {requestConfig} from "@/requestConfig";
import initialState from "@@/plugin-initialState/@@initialState";

const Login: React.FC = () => {
  const unloadFileTypeList = ["image/jpeg", "image/jpg", "image/svg", "image/png", "image/webp", "image/jfif"]
  const [previewOpen, setPreviewOpen] = useState(false);
  const handleCancel = () => setPreviewOpen(false);
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [previewImage, setPreviewImage] = useState('');
  const [previewTitle, setPreviewTitle] = useState('');
  const {loginUser} = initialState || {}
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
        // 这里是初始化头像哦！！！！
        const updatedFileList = [...fileList];
        if (result.data  && result.data.userAvatar) {
          updatedFileList[0] = {
            // @ts-ignore
            uid: result.data.userAccount,
            // @ts-ignore
            name: result.data.userAvatar.substring(result.data.userAvatar!.lastIndexOf('-') + 1),
            status: "done",
            percent: 100,
            url: result.data.userAvatar
          }
          setFileList(updatedFileList);
        }
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

  // 编辑状态
  const handleEditToggle = () => { // 展示为编辑框
    if (editable){
      setEditable(!editable);
      message.info("取消编辑");
    }else {
      setEditable(!editable);
      message.info("请开始编辑");
    }

  };

  let handleChange = (e) => {
    const { name, value } = e.target;
    console.log(`Updating ${name} to ${value}`);
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

  // region 头像处理

  const getBase64 = (file: RcFile): Promise<string> =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = (error) => reject(error);
    });
  // 预览图片功能在这
  const handlePreview = async (file: UploadFile) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj as RcFile);
    }
    const previewImageUrl = file.preview || file.url || data?.data?.userAvatar;
    setPreviewImage(previewImageUrl);
    setPreviewOpen(true);
    setPreviewTitle(file.name || file.url!.substring(file.url!.lastIndexOf('-') + 1));
  };

  const uploadButton = () => {
    return (
      <div>
        <PlusOutlined/>
        <div style={{marginTop: 8}}>Upload</div>
      </div>
    );
  }
  // 上传前的图片修改
  const beforeUpload = async (file: RcFile) => {
    const fileType = unloadFileTypeList.includes(file.type);
    if (!fileType) {
      message.error('图片类型有误，请上传 jpg/png/svg/jpeg/webp 格式！');
    }
    const isLt2M = file.size / 1024 / 1024 < 1;
    if (!isLt2M) {
      message.error('文件大小不能超过 1M！');
    }
    if (!isLt2M || !fileType) {
      const updatedFileList = [...fileList];
      updatedFileList[0] = {
        uid: loginUser?.userAccount,
        name: "error",
        status: "error",
        percent: 100
      };
      setFileList(updatedFileList);
      return false;
    }
    return true;
  };


  const props: UploadProps = {
    name: 'file',
    withCredentials: true,
    action: `${requestConfig.baseURL}/api/file/upload?biz=user_avatar`, // 调用后端上传文件的接口
    onChange: async ({ file, fileList: newFileList }) => {
      const { response } = file;
      if (response && response.data) {
        const { status, url } = response.data;
        const updatedFileList = [...fileList];
        if (response.code !== 0 || status === 'error') {
          message.error(response.message);
          file.status = "error";
          updatedFileList[0] = {
            uid: loginUser?.userAccount,
            name: loginUser?.userAvatar ? loginUser?.userAvatar?.substring(loginUser?.userAvatar!.lastIndexOf('-') + 1) : "error",
            status: "error",
            percent: 100
          };
          setFileList(updatedFileList);
        } else {
          file.status = status;
          updatedFileList[0] = {
            uid: loginUser?.userAccount,
            name: loginUser?.userAvatar?.substring(loginUser?.userAvatar!.lastIndexOf('-') + 1),
            status: status,
            url: response.data,
            percent: 100
          };
          setFileList(updatedFileList);
          // 更新 editedData 中的 userAvatar（因为我前端和后端的头像字段名对应不上，所以在这里更新一下）
          setEditedData(prevState => ({
            ...prevState,
            userAvatar: response.data
          }));
        }
      } else {
        setFileList(newFileList);
      }
    },
    listType: "picture-circle",
    onPreview: handlePreview,
    fileList: fileList,
    beforeUpload: beforeUpload,
    maxCount: 1,
    progress: {
      strokeColor: {
        '0%': '#108ee9',
        '100%': '#87d068',
      },
      strokeWidth: 3,
      format: percent => percent && `${parseFloat(percent.toFixed(2))}%`,
    },
  };
  // endregion


  // 如果数据加载完成，渲染用户信息卡片
  return (
      <div  style={{ margin: '0 auto', width: '80%' }}>
        {/*用户信息展示*/}
        <div className="site-card-border-less-wrapper">
          <Card hoverable={true} type="inner" title="用户简介：" bordered={false}>
            {data?.data && (
              <>
                <Descriptions.Item>
                  <ImgCrop
                    rotationSlider
                    quality={1}
                    aspectSlider
                    maxZoom={4}
                    cropShape={"round"}
                    zoomSlider
                    showReset
                  >
                    <Upload {...props}>
                      {fileList.length >= 1 ? undefined : uploadButton()}
                    </Upload>
                  </ImgCrop>
                  <Modal open={previewOpen} title={previewTitle} footer={null} onCancel={handleCancel}>
                    <img alt="example" style={{width: '100%'}} src={previewImage}/>
                  </Modal>
                </Descriptions.Item>
                <p><strong>Id: </strong>{data.data.id}</p>
                <p><strong>用户名: </strong>
                  {editable ? (
                    <input
                      className="edit-input"
                      type="text"
                      name="userName"
                      value={editedData.userName}
                      onChange={handleChange}
                    />
                  ) : (
                    <span>{editedData.userName}</span>
                  )}
                </p>
                <p><strong>性别: </strong>
                  {editable ? (
                    <input
                      className="edit-input"
                      type="text"
                      name="sex"
                      value={editedData.sex}
                      onChange={handleChange}
                    />
                  ) : (
                    <span>{editedData.sex}</span>
                  )}
                </p>
                <p><strong>电话号码: </strong>
                  {editable ? (
                    <input
                      className="edit-input"
                      type="text"
                      name="telephone"
                      value={editedData.telephone}
                      onChange={handleChange}
                    />
                  ) : (
                    <span>{editedData.telephone}</span>
                  )}
                </p>
                <p><strong>邮箱: </strong>
                  {editable ? (
                    <input
                      className="edit-input"
                      type="text"
                      name="qq"
                      value={editedData.qq}
                      onChange={handleChange}
                    />
                  ) : (
                    <span>{editedData.qq}</span>
                  )}
                </p>
                <p><strong>坤币: </strong>{data.data.kunCoin}</p>
                <p><strong>个人简介: </strong>
                  {editable ? (
                    <textarea
                      className="edit-textarea"
                      type="text"
                      name="userProfile"
                      value={editedData.userProfile}
                      onChange={handleChange}
                    />
                  ) : (
                    <span>{editedData.userProfile}</span>
                  )}
                </p>
                <p><strong>用户等级: </strong>{data.data.userRole === 'admin' ? '管理员' : '普通用户'}</p>
                <p><strong>注册时间: </strong>{formattedDateTime}</p>
              </>
            )}
          </Card>

          <Card  type="inner" >
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 20 }}>
              <Button type="primary" onClick={handleEditToggle}>
                点我修改个人信息
              </Button>
              {editable && (
                <div>
                  <Button type="primary" onClick={handleSave}>
                    确认修改
                  </Button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  <Button type="primary" onClick={handleEditToggle}>
                    不修改了
                  </Button>
                </div>
              )}
            </div>

          </Card>
          <Card title="用户操作："  type="inner" hoverable={true} >
            <div >
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
      </div>
  );
};

export default Login;
