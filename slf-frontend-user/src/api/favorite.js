import request from '@/utils/request'

// 获取收藏列表（分页）
export function getFavorites(params) {
  return request({
    url: '/favorites',
    method: 'get',
    params
  })
}

// 收藏指定物品
export function addFavorite(itemId) {
  return request({
    url: `/favorites/${itemId}`,
    method: 'post'
  })
}

// 取消收藏指定物品
export function removeFavorite(itemId) {
  return request({
    url: `/favorites/${itemId}`,
    method: 'delete'
  })
}

// 校验指定物品是否已被当前用户收藏
export function checkFavorite(itemId) {
  return request({
    url: `/favorites/${itemId}/check`,
    method: 'get'
  })
}
