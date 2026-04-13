import request from '@/utils/request'

export function uploadItemImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/item',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function uploadAiImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/ai',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
