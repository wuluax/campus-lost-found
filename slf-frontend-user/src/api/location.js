import request from '@/utils/request'

export function getLocationList() {
  return request({
    url: '/locations',
    method: 'get'
  })
}
