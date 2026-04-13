import request from '@/utils/request'

export function getItemClues(itemId) {
  return request({
    url: `/items/${itemId}/clues`,
    method: 'get'
  })
}

export function createClue(itemId, data) {
  return request({
    url: `/items/${itemId}/clues`,
    method: 'post',
    data
  })
}

export function deleteClue(id) {
  return request({
    url: `/clues/${id}`,
    method: 'delete'
  })
}
