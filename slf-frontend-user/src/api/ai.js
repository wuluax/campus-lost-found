import request from '@/utils/request'

// 获取AI历史记录（当前用户）
export function getAiHistory() {
  return request({
    url: '/ai/history',
    method: 'get'
  })
}

// 发送消息给AI
export function sendAiMessage(data) {
  return request({
    url: '/ai/send',
    method: 'post',
    data,
    timeout: 120000
  })
}
