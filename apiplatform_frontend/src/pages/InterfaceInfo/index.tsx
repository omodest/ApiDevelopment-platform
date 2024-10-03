import { PageContainer } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';
import { Button, Card, Descriptions, Form, message, Input, Divider } from 'antd';
import { useParams } from '@@/exports';
import { getInterfaceInfoVoByIdUsingGet } from "@/services/apiform_backend/interfaceInfoController";
import { invokeInterfaceInfoUsingPost } from "../../services/apiplateform-backend/interfaceInfoController";
import SwaggerUIComponent from '@/components/InterfaceSwagger/SwaggerUI'; // 导入 Swagger 组件

const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfo | null>(null);
  const [invokeRes, setInvokeRes] = useState<string | null>(null);
  const [invokeLoading, setInvokeLoading] = useState(false);
  const [requestParams, setRequestParams] = useState<any>([]);

  const params = useParams();

  const loadData = async () => {
    if (!params.id) {
      message.error('参数不存在');
      return;
    }
    setLoading(true);
    try {
      const res = await getInterfaceInfoVoByIdUsingGet({ id: Number(params.id) });
      setData(res.data);
    } catch (error: any) {
      message.error('请求失败，' + error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleRequestParams = (values: any) => {
    try {
      const parsedParams = values ? JSON.parse(values.requestParams) : [];
      setRequestParams(parsedParams);
    } catch (e) {
      message.error('JSON 格式不正确，请检查！');
      console.log("JSON 转换失败", e);
      setRequestParams([]); // 清空请求参数
    }
  };

  const onFinish = async (values: any) => {
    if (!params.id) {
      message.error('接口不存在');
      return;
    }
    handleRequestParams(values);
    setInvokeLoading(true);
    try {
      const res = await invokeInterfaceInfoUsingPost({
        id: params.id,
        requestParams,
      });
      setInvokeRes(JSON.stringify(res.data, null, 4)); // 格式化输出
      message.success('请求成功');
    } catch (error: any) {
      message.error('操作失败，' + error.message);
    } finally {
      setInvokeLoading(false);
    }
  };

  return (
    <PageContainer title="查看接口文档">
      <Card loading={loading}>
        {data ? (
          <Descriptions title={data.interfaceName} column={1}>
            <Descriptions.Item label="接口状态">{data.interfaceStatus ? '开启' : '关闭'}</Descriptions.Item>
            <Descriptions.Item label="描述">{data.interfaceDescript}</Descriptions.Item>
            <Descriptions.Item label="请求地址">{data.interfaceUrl}</Descriptions.Item>
            <Descriptions.Item label="请求方法">{data.interfaceType}</Descriptions.Item>
            <Descriptions.Item label="请求参数">{data.requestParams}</Descriptions.Item>
            <Descriptions.Item label="请求头">{data.requestHeader}</Descriptions.Item>
            <Descriptions.Item label="响应头">{data.responceHeader}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{data.createTime}</Descriptions.Item>
            <Descriptions.Item label="更新时间">{data.update_time}</Descriptions.Item>
          </Descriptions>
        ) : (
          <>接口不存在</>
        )}
      </Card>
      <Divider />
      <Card title="在线测试">
        <Form name="invoke" layout="vertical" onFinish={onFinish}>
          <Form.Item label="请求参数" name="requestParams">
            <Input.TextArea
              style={{ height: '200px' }}
              disabled={!data || !data.interfaceStatus}
            />
          </Form.Item>
          <Form.Item wrapperCol={{ span: 16 }}>
            <Button type="primary" htmlType="submit" disabled={!data || !data.interfaceStatus || invokeLoading}>
              调用
            </Button>
          </Form.Item>
        </Form>
      </Card>
      <Divider />
      <Card title="返回结果" loading={invokeLoading}>
        <pre>{invokeRes || '无返回结果'}</pre>
      </Card>
      <Divider />
    </PageContainer>
  );
};

export default Index;
