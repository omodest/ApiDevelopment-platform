import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import {
  FooterToolbar,
  PageContainer,
  ProDescriptions,
  ProTable,
} from '@ant-design/pro-components';
import { Image } from "antd";
import { FormattedMessage, useIntl } from '@umijs/max';
import { Button, Drawer, message } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import CreateModal from "@/pages/Admin/InterfaceInfo/components/CreateModal";
import UpdateModal from "@/pages/Admin/InterfaceInfo/components/UpdateModal";
import {
  addUserUsingPost,
  deleteUserUsingPost,
  listUserVoByPageUsingPost,
  updateUserUsingPost
} from "@/services/apiplateform-backend/userController";

interface UserVO {
  id: number;
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
  const [currentRow, setCurrentRow] = useState<UserVO>();
  const [selectedRowsState, setSelectedRows] = useState<UserVO[]>([]);
  const [dataSource, setDataSource] = useState<UserVO[]>([]);
  const [isDisabled, setIsDisabled] = useState<boolean>(true); // 状态控制按钮是否禁用

  useEffect(() => {
    fetchData(); // 查询功能有点小问题；因为页面每次查询都会展示所有数据
  }, []);

  // 初始化数据
  const fetchData = async () => {
    try {
      const response = await listUserVoByPageUsingPost({});
      if (response?.code === 0) {
        setDataSource(response.data.records);
      } else {
        message.error(response.message || '请求失败');
      }
    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
  };
  // 添加（已废除）
  const handleAdd = async (fields: API.UserVO) => {
    const hide = message.loading('正在添加');
    try {
      await addUserUsingPost({
        ...fields,
      } as API.UserAddRequest);
      hide();
      message.success('创建成功');
      handleModalVisible(false);
      return true;
    } catch (error: any) {
      hide();
      message.error('创建失败，' + error.message);
      return false;
    }
  };
  // 修改（已废除）
  const handleUpdate = async (fields: API.UserUpdateRequest) => {
    if (!currentRow) {
      return;
    }
    const hide = message.loading('修改中');
    try {
      // await updateUserUsingPost({
      //   id: currentRow.id,
      //   ...fields
      // });
      hide();
      message.success('操作成功');
      return true;
    } catch (error: any) {
      hide();
      message.error('操作失败，' + error.message);
      return false;
    }
  };
  // 删除
  const handleRemove = async (record: API.UserVO) => {
    const hide = message.loading('正在删除');
    if (!record) return true;
    try {
      await deleteUserUsingPost({
        id: record.id
      });
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

  const tableColumns: ProColumns<UserVO>[] = [
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
          <Image src={record.userAvatar || ''} width={100} />
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
            if (!isDisabled) {
              handleUpdateModalVisible(true);
              setCurrentRow(record);
            }
          }}
          style={{ pointerEvents: isDisabled ? 'none' : 'auto', color: isDisabled ? 'grey' : 'blue' }}
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

  const modalColumns: ProColumns<UserVO>[] = [
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
  ];

  return (
    <PageContainer>
      <ProTable<UserVO>
        headerTitle={intl.formatMessage({
          id: 'pages.searchTable.title',
          defaultMessage: 'Enquiry form',
        })}
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              handleModalVisible(true);
            }}
            disabled={true} // 设置为 true 禁用按钮
          >
            <PlusOutlined/> <FormattedMessage id="pages.searchTable.new" defaultMessage="New"/>
          </Button>,
        ]}
        dataSource={dataSource}
        columns={tableColumns}
        request={listUserVoByPageUsingPost}
        rowSelection={{
          onChange: (_, selectedRows) => {
            setSelectedRows(selectedRows);
          },
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              <FormattedMessage id="pages.searchTable.chosen" defaultMessage="Chosen"/>{' '}
              <a style={{fontWeight: 600}}>{selectedRowsState.length}</a>{' '}
              <FormattedMessage id="pages.searchTable.item" defaultMessage="项"/>
            </div>
          }
        >
          <Button
            onClick={async () => {
              await handleRemove(selectedRowsState);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
            disabled={true}
          >
            <FormattedMessage
              id="pages.searchTable.batchDeletion"
              defaultMessage="Batch deletion"
            />
          </Button>
          <Button type="primary" disabled={true}>
            <FormattedMessage
              id="pages.searchTable.batchApproval"
              defaultMessage="Batch approval"
            />
          </Button>
        </FooterToolbar>
      )}
      <CreateModal
        columns={modalColumns}
        onCancel={() => {
          handleModalVisible(false);
        }}
        onSubmit={(values) => {
          handleAdd(values);
        }}
        visible={createModalVisible}
      />
      <UpdateModal
        columns={modalColumns}
        onSubmit={async (value) => {
          const success = await handleUpdate(value);
          if (success) {
            handleUpdateModalVisible(false);
            setCurrentRow(undefined);
            if (actionRef.current) {
              actionRef.current?.reload();
            }
          }
        }}
        onCancel={() => {
          handleUpdateModalVisible(false);
          if (!showDetail) {
            setCurrentRow(undefined);
          }
        }}
        visible={updateModalVisible}
        values={currentRow || {}}
      />
      <Drawer
        width={600}
        open={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.name && (
          <ProDescriptions<UserVO>
            column={2}
            title={currentRow?.name}
            request={async () => ({
              data: currentRow || {},
            })}
            params={{
              id: currentRow?.name,
            }}
            columns={tableColumns as ProDescriptionsItemProps<UserVO>[]}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default TableList;
