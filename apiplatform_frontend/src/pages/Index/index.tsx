import { PageContainer } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';
import { List, message, Tag, Pagination, Card, Spin, Avatar, Button } from 'antd';
import { listInterfaceInfoVoByPageUsingPost } from "@/services/apiform_backend/interfaceInfoController";
import './index.css';

const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [interfaces, setInterfaces] = useState<API.InterfaceInfo[]>([]);
  const [totalInterfaces, setTotalInterfaces] = useState<number>(0);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const pageSize = 5;
  const [searchValue, setSearchValue] = useState<string>('');

  const loadInterfaceData = async (page: number, searchQuery: string = '') => {
    setLoading(true);
    try {
      const response = await listInterfaceInfoVoByPageUsingPost({ current: page, pageSize, name: searchQuery });
      if (response && response.data && Array.isArray(response.data.records)) {
        setInterfaces(response.data.records);
        setTotalInterfaces(response.data.size);
      } else {
        setInterfaces([]);
        setTotalInterfaces(0);
        message.error('返回数据格式不正确');
      }
    } catch (error: any) {
      message.error(`请求失败: ${error.message || '未知错误'}`);
      setInterfaces([]);
      setTotalInterfaces(0);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadInterfaceData(currentPage, searchValue);
  }, [currentPage, searchValue]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const getSwaggerUrl = () => {
    return `http://localhost:8123/api/swagger-ui/index.html`;
  };

  return (
    <PageContainer title="在线接口开放平台" ghost>
      <div className="interface-list-container">
        {loading ? (
          <Spin tip="加载中..." size="large" />
        ) : (
          <List
            grid={{ gutter: 16, column: 1 }}
            dataSource={interfaces}
            renderItem={(item) => (
              <List.Item>
                <Card
                  hoverable
                  actions={[
                    <Button>
                      <a href={`/interface_info/${item.id}`}>查看</a>
                    </Button>,
                    <Button type="primary">
                      <a href={getSwaggerUrl()} target="_blank" rel="noopener noreferrer">查看Swagger文档</a>
                    </Button>
                  ]}
                  className="interface-card"
                >
                  <Card.Meta
                    avatar={<Avatar src={item.avatarUrl} />} // 显示头像
                    title={<a href={`/interface_info/${item.id}`}>{item.interfaceName}</a>}
                    description={
                      <>
                        <p>{item.interfaceDescript}</p>
                        <Tag color="blue">调用次数: {item.totalInvokes}</Tag>
                      </>
                    }
                  />
                </Card>
              </List.Item>
            )}
          />
        )}
      </div>
      <Pagination
        total={totalInterfaces}
        pageSize={pageSize}
        current={currentPage}
        onChange={handlePageChange}
        showTotal={(total) => `总数：${total}`}
        // showSizeChanger // 允许用户选择每页条目数
        style={{ marginTop: 16, textAlign: 'center' }}
      />
    </PageContainer>
  );
};

export default Index;
