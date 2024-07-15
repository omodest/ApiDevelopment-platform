// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** listProductInfoByPage GET /api/rechargeRecord/list/page */
export async function listProductInfoByPageUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listProductInfoByPageUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageProductInfo_>('/api/rechargeRecord/list/page', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
