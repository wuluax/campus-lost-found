import request from './http'

export function getAdminMessages(params) {
  return request({
    url: '/admin/messages',
    method: 'get',
    params,
  })
}

export function deleteAdminMessage(id) {
  return request({
    url: `/admin/messages/${id}`,
    method: 'delete',
  })
}
