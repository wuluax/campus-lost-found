import request from './http'

export function getAdminNotifications(params) {
  return request({
    url: '/admin/notifications',
    method: 'get',
    params,
  })
}

export function deleteAdminNotification(id) {
  return request({
    url: `/admin/notifications/${id}`,
    method: 'delete',
  })
}
