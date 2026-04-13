import request from './http'

export function getAdminLocations(params) {
  return request({
    url: '/admin/locations',
    method: 'get',
    params,
  })
}

export function getAdminLocationList(params) {
  return request({
    url: '/admin/locations/all',
    method: 'get',
    params,
  })
}

export function createAdminLocation(data) {
  return request({
    url: '/admin/locations',
    method: 'post',
    data,
  })
}

export function updateAdminLocation(id, data) {
  return request({
    url: `/admin/locations/${id}`,
    method: 'put',
    data,
  })
}

export function deleteAdminLocation(id) {
  return request({
    url: `/admin/locations/${id}`,
    method: 'delete',
  })
}
