/**
 * 私信 WebSocket 客户端
 * 使用方法：
 *   import { connectChat, disconnectChat, onChatMessage } from '@/utils/websocket'
 *   connectChat(token)
 *   onChatMessage((msg) => { ... })
 */

let ws = null
let reconnectTimer = null
let messageHandler = null
let connectionToken = null

const getWsUrl = (token) => {
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  return `${protocol}//${location.host}/ws/chat?token=${token}`
}

export function connectChat(token) {
  if (ws && ws.readyState === WebSocket.OPEN) return
  connectionToken = token

  ws = new WebSocket(getWsUrl(token))

  ws.onopen = () => {
    console.log('[WS] 连接已建立')
    clearReconnectTimer()
  }

  ws.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if (messageHandler) messageHandler(data)
    } catch (e) {
      console.error('[WS] 消息解析失败', e)
    }
  }

  ws.onclose = () => {
    console.log('[WS] 连接已关闭')
    scheduleReconnect()
  }

  ws.onerror = (err) => {
    console.error('[WS] 连接错误', err)
  }
}

export function disconnectChat() {
  connectionToken = null
  clearReconnectTimer()
  if (ws) {
    ws.onclose = null
    ws.close()
    ws = null
  }
}

export function sendWsMessage(data) {
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify(data))
  }
}

export function onChatMessage(handler) {
  messageHandler = handler
}

function scheduleReconnect() {
  if (!connectionToken) return
  clearReconnectTimer()
  reconnectTimer = setTimeout(() => {
    if (connectionToken) {
      console.log('[WS] 正在重连...')
      connectChat(connectionToken)
    }
  }, 3000)
}

function clearReconnectTimer() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
}
