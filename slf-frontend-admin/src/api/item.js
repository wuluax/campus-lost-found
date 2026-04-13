import request from './http'

// 分页获取物品列表（管理端）
export function getAdminItems(params) {
  return request({
    url: '/admin/items',
    method: 'get',
    params,
  })
}

// 获取物品详情（管理端）
export function getAdminItemDetail(id) {
  return request({
    url: `/admin/items/${id}`,
    method: 'get',
  })
}

// 修改物品状态（管理端）：0下架 1正常 2完成
export function updateAdminItemStatus(id, status) {
  return request({
    url: `/admin/items/${id}/status`,
    method: 'put',
    data: { status },
  })
}

// 删除物品（管理端）
export function deleteAdminItem(id) {
  return request({
    url: `/admin/items/${id}`,
    method: 'delete',
  })
}
