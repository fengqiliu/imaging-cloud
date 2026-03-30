import request from './index'

// Authenticated share endpoints
export function getShareList(params) {
  return request({
    url: '/cloudfilm/share/list',
    method: 'get',
    params
  })
}

export function getShare(shareId) {
  return request({
    url: `/cloudfilm/share/${shareId}`,
    method: 'get'
  })
}

export function createShare(data) {
  return request({
    url: '/cloudfilm/share',
    method: 'post',
    data
  })
}

export function deleteShare(shareId) {
  return request({
    url: `/cloudfilm/share/${shareId}`,
    method: 'delete'
  })
}

// Public share endpoints (no auth required)
export function getShareInfo(shareNo) {
  return request({
    url: `/cloudfilm/share/info/${shareNo}`,
    method: 'get',
    headers: {} // Override auth header for public endpoint
  })
}

export function verifyShareAccess(data) {
  return request({
    url: '/cloudfilm/share/verify',
    method: 'post',
    data,
    headers: {} // Override auth header for public endpoint
  })
}

export function checkShareValid(shareNo) {
  return request({
    url: `/cloudfilm/share/check/${shareNo}`,
    method: 'get',
    headers: {} // Override auth header for public endpoint
  })
}
