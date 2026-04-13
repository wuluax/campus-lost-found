import request from '@/utils/request'

/** 获取通知列表 */
export function getNotifications(params) {
  return request.get('/notifications', { params })
}

/** 获取未读通知数 */
export function getNotificationUnreadCount() {
  return request.get('/notifications/unread-count')
}

/** 标记单条通知为已读 */
export function markNotificationRead(id) {
  return request.put(`/notifications/${id}/read`)
}

/** 全部标记为已读 */
export function markAllNotificationsRead() {
  return request.put('/notifications/read-all')
}
