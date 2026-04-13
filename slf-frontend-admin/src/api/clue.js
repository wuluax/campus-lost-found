import request from './http'

export function getAdminClues(params) {
  return request({
    url: '/admin/clues',
    method: 'get',
    params,
  })
}

export function deleteAdminClue(id) {
  return request({
    url: `/admin/clues/${id}`,
    method: 'delete',
  })
}
