import request from './http'

export function getAdminCategories(params) {
  return request({
    url: '/admin/categories',
    method: 'get',
    params,
  })
}

export function getAdminCategoryList(params) {
  return request({
    url: '/admin/categories/all',
    method: 'get',
    params,
  })
}

export function createAdminCategory(data) {
  return request({
    url: '/admin/categories',
    method: 'post',
    data,
  })
}

export function updateAdminCategory(id, data) {
  return request({
    url: `/admin/categories/${id}`,
    method: 'put',
    data,
  })
}

export function deleteAdminCategory(id) {
  return request({
    url: `/admin/categories/${id}`,
    method: 'delete',
  })
}
