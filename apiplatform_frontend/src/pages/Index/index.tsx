import { PageContainer } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';
import { List, message } from 'antd';
import {listInterfaceInfoByPageUsingGet} from "@/services/apiform_backend/interfaceInfoController";

/**
 * 主页
 * @constructor
 */
const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<API.InterfaceInfo[]>([]);
  const [total, setTotal] = useState<number>(0);

  // 请求后端拿到数据
  const loadData = async (current = 1, pageSize = 5) => {
    setLoading(true);
    try {
      const res = await listInterfaceInfoByPageUsingGet({ // 拿到数据
        current,
        pageSize,
      });
      setList(res?.data); // 设置数据
      setTotal(res?.data?.length ?? 0); // 设置长度，用来分页
    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false);
  };
  // 每次初始化页面都调用该函数
  useEffect(() => {
    loadData().then(() => {
      console.log("初始化数据")
    });
  }, []);

  return (
    <PageContainer title="在线接口开放平台">
      <List
        className="my-list"
        loading={loading}
        itemLayout="horizontal"
        dataSource={list}
        renderItem={(item) => {
          const apiLink = `/interface_info/${item.id}`;
          // 前端查看功能跳转
          return (
            <List.Item actions={[<a key={item.id} href={apiLink}>查看</a>]}>
              <List.Item.Meta
                title={<a href={apiLink}>{item.interfaceName}</a>}
                description={item.interfaceDescript}
              />
            </List.Item>
          );
        }}
        // 分页
        pagination={{
          // eslint-disable-next-line @typescript-eslint/no-shadow
          showTotal(total: number) {
            return '总数：' + total;
          },
          pageSize: 5,
          total,
          onChange(page, pageSize) {
            loadData(page, pageSize);
          },
        }}
      />
    </PageContainer>
  );
};

export default Index;
