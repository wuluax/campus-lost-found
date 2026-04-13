import request from '@/utils/request'

/** 发送私信（HTTP 方式） */
export function sendMessage(data) {
  return request.post('/messages', data)
}

/** 获取会话列表 */
export function getConversations() {
  return request.get('/messages/conversations')
}

/** 获取与某用户的聊天记录 */
export function getChatHistory(otherUserId, params) {
  return request.get(`/messages/chat/${otherUserId}`, { params })
}

/** 标记与某用户的消息为已读 */
export function markAsRead(otherUserId) {
  return request.put(`/messages/read/${otherUserId}`)
}

/** 获取未读消息总数 */
export function getUnreadCount() {
  return request.get('/messages/unread-count')
}
