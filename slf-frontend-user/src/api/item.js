import request from '@/utils/request'

// 获取物品列表
export function getItems(params) {
  return request({
    url: '/items',
    method: 'get',
    params
  })
}

// 发布物品
export function createItem(data) {
  return request({
    url: '/items',
    method: 'post',
    data
  })
}

// 获取物品详情
export function getItemDetail(id) {
  return request({
    url: `/items/${id}`,
    method: 'get'
  })
}

// 修改物品
export function updateItem(id, data) {
  return request({
    url: `/items/${id}`,
    method: 'put',
    data
  })
}

// 删除物品（仅发布者本人可删除）
export function deleteItem(id) {
  return request({
    url: `/items/${id}`,
    method: 'delete'
  })
}

export function getMyItems(params) {
  return request({
    url: '/items/my',
    method: 'get',
    params
  })
}

export function getMyItemsCount(params) {
  return request({
    url: '/items/my/count',
    method: 'get',
    params
  })
}

// 修改物品状态：0 下架，不可见；2 已找回/已被认领
export function updateItemStatus(id, status) {
  return request({
    url: `/items/${id}/status`,
    method: 'put',
    data: { status }
  })
}
