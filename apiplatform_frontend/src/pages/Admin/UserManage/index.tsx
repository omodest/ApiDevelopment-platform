import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import {
  FooterToolbar,
  PageContainer,
  ProDescriptions,
  ProTable,
} from '@ant-design/pro-components';
import { FormattedMessage, useIntl } from 'umi';
import { Button, Drawer, message, Image } from 'antd';
import React, {useEffect, useRef, useState} from 'react';
import CreateModal from './components/CreateModal'; // 假设路径需要根据实际情况修改
import UpdateModal from './components/UpdateModal'; // 假设路径需要根据实际情况修改
import {
  addUserUsingPost,
  deleteUserUsingPost,
  listUserVoByPageUsingPost,
  updateUserUsingPost,
} from '@/services/apiplateform-backend/userController'; // 假设路径需要根据实际情况修改

interface UserVO {
  id: string;
  userName: string;
  avatarUrl: string;
  userRole: string;
  userProfile: string;
  createTime: string;
}

const TableList: React.FC = () => {
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [showDetail, setShowDetail] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<UserVO | undefined>();
  const [selectedRowsState, setSelectedRows] = useState<UserVO[]>([]);
  const [dataSource, setDataSource] = useState<UserVO[]>([]);
  useEffect(() => {
    // eslint-disable-next-line @typescript-eslint/no-use-before-define
    fetchData().then(() =>
      console.log("加载数据")
    );
  }, []);
  // 加载所有用户数据
  const fetchData = async () => {
    try {
      const response = await listUserVoByPageUsingPost({});
      if (response?.code === 0) {
        // 如果返回的 code 是 0，表示成功
        setDataSource(response.data.records); // 假设 records 是你需要的数据数组
      } else {
        message.error(response.message || '请求失败');
      }
    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
  };


  const handleAdd = async (fields: Partial<UserVO>) => {
    const hide = message.loading('正在添加');
    try {
      await addUserUsingPost({
        ...fields,
      } as API.UserAddRequest);
      hide();
      message.success('创建成功');
      handleModalVisible(false);
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('创建失败，' + error.message);
      return false;
    }
  };

  const handleUpdate = async (fields: API.UserVO) => {
    if (!currentRow) {
      return;
    }
    const hide = message.loading('修改中');
    try {
      await updateUserUsingPost({
        id: currentRow.id,
        ...fields,
      });
      hide();
      message.success('操作成功');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('操作失败，' + error.message);
      return false;
    }
  };

  const handleRemove = async (record: UserVO | UserVO[]) => {
    const hide = message.loading('正在删除');
    try {
      // 判断是否为单个记录还是多个记录删除
      if (Array.isArray(record)) {
        await Promise.all(record.map(item => deleteUserUsingPost({id: item.id})));
      } else {
        await deleteUserUsingPost({ id: record.id });
      }
      hide();
      message.success('删除成功');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('删除失败，' + error.message);
      return false;
    }
  };

  const intl = useIntl();
  const columns: ProColumns<UserVO>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      valueType: 'text',
      formItemProps: {
        rules: [{ required: true }],
      },
    },
    {
      title: '用户名',
      dataIndex: 'userName',
      valueType: 'text',
    },
    {
      title: '头像',
      dataIndex: 'avatarUrl',
      render: (_, record) => (
        <div>
          <Image src={record.userAvatar} width={100} />
        </div>
      ),
    },
    {
      title: '权限',
      dataIndex: 'userRole',
      valueType: 'text',
    },
    {
      title: '简介',
      dataIndex: 'userProfile',
      valueType: 'text',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
    },
    {
      title: '操作',
      valueType: 'option',
      render: (_, record) => [
        <a
          key={`update-${record.id}`}
          onClick={() => {
            handleUpdateModalVisible(true);
            setCurrentRow(record);
          }}
        >
          修改
        </a>,
        <Button
          type="text"
          key={`delete-${record.id}`}
          danger
          onClick={() => {
            handleRemove(record);
          }}
        >
          删除
        </Button>,
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<UserVO, API.PageParams>
        headerTitle={intl.formatMessage({
          id: 'pages.searchTable.title',
          defaultMessage: '查询表格',
        })}
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            key="primary"
            type="primary"
            onClick={() => {
              handleModalVisible(true);
            }}
          >
            <PlusOutlined /> <FormattedMessage id="pages.searchTable.new" defaultMessage="新建" />
          </Button>,
        ]}
        dataSource={dataSource}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => {
            setSelectedRows(selectedRows);
          },
        }}
      />
      {selectedRowsState.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              <FormattedMessage id="pages.searchTable.chosen" defaultMessage="已选择" />{' '}
              <a style={{ fontWeight: 600 }}>{selectedRowsState.length}</a>{' '}
              <FormattedMessage id="pages.searchTable.item" defaultMessage="项" />
            </div>
          }
        >
          <Button
            onClick={async () => {
              await handleRemove(selectedRowsState);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            <FormattedMessage id="pages.searchTable.batchDeletion" defaultMessage="批量删除" />
          </Button>
        </FooterToolbar>
      )}
      <CreateModal
        onCancel={() => {
          handleModalVisible(false);
        }}
        onSubmit={(values) => {
          handleAdd(values);
        }}
        visible={createModalVisible}
      />
      <UpdateModal
        onSubmit={async (values) => {
          const success = await handleUpdate(values);
          if (success) {
            handleUpdateModalVisible(false);
            setCurrentRow(undefined);
          }
        }}
        onCancel={() => {
          handleUpdateModalVisible(false);
          setCurrentRow(undefined);
        }}
        visible={updateModalVisible}
        values={currentRow}
      />
      <Drawer
        width={600}
        visible={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow && (
          <ProDescriptions<UserVO>
            column={2}
            title={currentRow.userName}
            request={async () => ({
              data: currentRow || {},
            })}
            params={{
              id: currentRow?.id,
            }}
            columns={columns as ProDescriptionsItemProps<UserVO>[]}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default TableList;
