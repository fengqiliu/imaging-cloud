import request from './index'

export function getImageList(params) {
  return request({
    url: '/cloudfilm/image/list',
    method: 'get',
    params
  })
}

export function getExamImages(examId) {
  return request({
    url: `/cloudfilm/image/exam/${examId}`,
    method: 'get'
  })
}

export function getImage(imageId) {
  return request({
    url: `/cloudfilm/image/${imageId}`,
    method: 'get'
  })
}

export function getImageUrl(imageId) {
  return request({
    url: `/cloudfilm/image/url/${imageId}`,
    method: 'get'
  })
}

export function uploadImage(formData) {
  return request({
    url: '/cloudfilm/image/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function batchUploadImages(formData) {
  return request({
    url: '/cloudfilm/image/batch/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deleteImage(imageIds) {
  return request({
    url: `/cloudfilm/image/${imageIds}`,
    method: 'delete'
  })
}

export function setKeyImage(imageId) {
  return request({
    url: `/cloudfilm/image/key/${imageId}`,
    method: 'put'
  })
}
