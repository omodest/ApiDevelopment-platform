import {Card, message, QRCode, Radio, Spin} from 'antd';
import React, {useEffect, useState} from 'react';
import {history} from '@umijs/max';
import WxPay from "@/components/Icon/WxPay";
import ProCard from "@ant-design/pro-card";
import Alipay from "@/components/Icon/Alipay";
// import { valueLength } from "@/pages/User/UserInfo";
import {useParams} from "@@/exports";
import {
  createOrderUsingPost,
  getProductOrderByIdUsingGet,
  queryOrderStatusUsingPost
} from "@/services/apiplateform-backend/orderController";

const PayOrder: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [order, setOrder] = useState<API.ProductOrderVo>();
  const [total, setTotal] = useState<any>("0.00");
  const [status, setStatus] = useState<string>('active');
  const [payType, setPayType] = useState<string>("ALIPAY");
  const urlParams = new URL(window.location.href).searchParams;
  const codeUrl = urlParams.get("codeUrl")
  const urlPayType = urlParams.get("payType")
  const [qrCode, setQrCode] = useState<any>('暂未选择支付方式');
  const [isDisabled, setIsDisabled] = useState<boolean>(false); // 只允许支付宝；使用该属性禁用微信支付
  const params = useParams()

  /**
   * 创建订单
   */
  const createOrder = async () => {
    setLoading(true)
    setStatus("loading")
    const res = await createOrderUsingPost({productId: params.id, payType: payType})
    if (res.code === 0 && res.data) {
      setOrder(res.data)
      setTotal((res.data.total) / 100)
      setStatus("active")
      setLoading(false)
      setQrCode(res.data.codeUrl)
    }
    if (res.code === 50001) {
      history.back()
    }
  }
  /**
   * 查询订单状态
   */
  const queryOrderStatus = async () => {
    const currentTime = new Date();
    const expirationTime = new Date(order?.expirationTime as any);
    if (currentTime > expirationTime) {
      setStatus("expired")
    }
    return await queryOrderStatusUsingPost({orderNo: order?.orderNo})
  }
  /**
   * 前往收银台
   */
  const toAlipay = async () => {
    if (!params.id) {
      message.error('参数不存在');
      return;
    }
    setLoading(true)
    const res = await createOrderUsingPost({productId: params.id, payType: "ALIPAY"})
    if (res.code === 0 && res.data) {
      message.loading("正在前往收银台,请稍后....")
      setTimeout(() => {
        document.write(res?.data?.formData as string);
        setLoading(false)
      }, 2000)
    } else {
      setLoading(false)
    }
  }
  /**
   * 修改支付类型
   * @param value
   */
  const changePayType = (value: string) => {
    setPayType(value);
  };
  /**
   * 获取订单
   */
  const getOrder = async () => {
    const res = await getProductOrderByIdUsingGet({id:params.id})
    console.log(res)
    if (res.code === 0 && res.data) {
      const data={
        productInfo:res.data,
        orderNo:res.data.orderNo,
        codeUrl:res.data.codeUrl
      }
      setOrder(data)
      setTotal((res.data.total))
      setStatus("active")
      setLoading(false)
      setQrCode(res.data.codeUrl)
    }
  }
  /**
   * 因为只提供一种支付方式，这个页面的功能虽然需要，但是页面不需要展示，这里直接重定向到我的订单页；
   * 然后用户再点击支付支付订单
   */
  useEffect(() => {
    history.push('/order/list');
  }, []);
  useEffect(() => {
    if (urlPayType) {
      setPayType(urlPayType)
      getOrder()
    }
  }, [])

  /**
   * 页面加载，选择字符类型(这里默认只提供支付宝)
   */
  useEffect(() => {
    if (payType === "ALIPAY") {
      toAlipay().then(r => console.log(r));
    }
    if (payType === "WX" && !codeUrl) {
      createOrder().then(r => console.log(r));
    }
  }, [payType])

  useEffect(() => {
    if (order && order.orderNo && order.codeUrl) {
      const intervalId = setInterval(async () => {
        // 定时任务逻辑
        const res = await queryOrderStatus()
        if (res.data && res.code === 0) {
          setLoading(true)
          message.loading("支付成功,打款中....")
          clearInterval(intervalId);
          setTimeout(function () {
            setLoading(false)
            const urlParams = new URL(window.location.href).searchParams;
            history.push(urlParams.get('redirect') || '/account/center');
          }, 2000);
        } else {
          console.log("支付中...")
        }
      }, 3000);
      if (status === "expired") {
        clearInterval(intervalId);
      }
      return () => {
        clearInterval(intervalId);
      };
    }
  }, [order, status])

  useEffect(() => {
    if (!params.id) {
      message.error('参数不存在');
      return;
    }
    // 判断是否为手机设备
    const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
    if (codeUrl) {
      if (isMobile) {
        window.location.href = codeUrl
        return;
      }
      setQrCode(codeUrl)
      setStatus("active")
      setPayType("WX")
      return;
    }
    if (!urlPayType && !payType) {
      setPayType("WX")
      setStatus("loading")
      return
    }
    if (urlPayType) {
      setPayType(urlPayType)
      return;
    }
    createOrder()
  }, [])
  return (
    <>
      <Card style={{minWidth: 385}}>
        <Spin spinning={loading}>
          <Card title={<strong>商品信息</strong>}>
            <div style={{marginLeft: 10}}>
              <h3>{order?.productInfo?.name}</h3>
              {/*<h4>{valueLength(order?.productInfo?.description) ? order?.productInfo?.description : "暂无商品描述信息"}</h4>*/}
            </div>
          </Card>
          <br/>
          <ProCard
            bordered
            headerBordered
            layout={"center"}
            title={<strong>支付方式</strong>}
          >
            <Radio.Group name="payType" value={payType}>
              <ProCard wrap gutter={18}>
                <ProCard
                  onClick={() => {
                    if (!isDisabled) { // `isDisabled` 是一个状态来控制是否禁用
                      changePayType("WX");
                    }
                  }}
                  hoverable
                  style={{
                    border: payType === "WX" ? '1px solid #1890ff' : '1px solid rgba(128, 128, 128, 0.5)',
                    maxWidth: 260,
                    minWidth: 210,
                    margin: 10,
                    cursor: isDisabled ? 'not-allowed' : 'pointer', // 显示不同的鼠标光标
                    opacity: isDisabled ? 0.5 : 1, // 调整透明度来表示禁用状态
                  }}
                  colSpan={
                    {
                      xs: 24,
                      sm: 12,
                      md: 12,
                      lg: 12,
                      xl: 12
                    }
                  }>
                  <Radio value={"WX"} style={{fontSize: "1.12rem"}}>
                    <WxPay/> 微信支付
                  </Radio>
                </ProCard>
                <ProCard
                  onClick={() => {
                    changePayType("ALIPAY")
                  }}
                  hoverable
                  style={{
                    margin: 10,
                    maxWidth: 260,
                    minWidth: 210,
                    border: payType === "ALIPAY" ? '1px solid #1890ff' : '1px solid rgba(128, 128, 128, 0.5)',
                  }}
                  colSpan={
                    {
                      xs: 24,
                      sm: 12,
                      md: 12,
                      lg: 12,
                      xl: 12
                    }
                  }
                >
                  <Radio value={"ALIPAY"} style={{fontSize: "1.2rem"}}>
                    <Alipay/> 支付宝
                  </Radio>
                </ProCard>
              </ProCard>
            </Radio.Group>
          </ProCard>
          <br/>
          <Card title={"支付二维码"}>
            <br/>
            <ProCard
              style={{marginTop: -30}}
              layout={"center"}>
              <QRCode
                errorLevel="H"
                size={240}
                value={qrCode}
                // @ts-ignore
                status={status}
                onRefresh={() => {
                  if (!payType) {
                    message.error("请先选择支付方式")
                    return
                  }
                  createOrder()
                }}
              />
            </ProCard>
            <ProCard style={{
              marginTop: -30,
              color: "#f55f4e",
              fontSize: 22,
              display: 'flex',
              fontWeight: "bold",
            }} layout={"center"}>
              ￥{total}
            </ProCard>
          </Card>
        </Spin>
      </Card>
    </>
  )
}

export default PayOrder;
