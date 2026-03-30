import request from './index'

export function getExaminationList(params) {
  return request({
    url: '/cloudfilm/examination/list',
    method: 'get',
    params
  })
}

export function getExamination(examId) {
  return request({
    url: `/cloudfilm/examination/${examId}`,
    method: 'get'
  })
}

export function getPatientExaminations(patientId) {
  return request({
    url: `/cloudfilm/examination/patient/${patientId}`,
    method: 'get'
  })
}

export function addExamination(data) {
  return request({
    url: '/cloudfilm/examination',
    method: 'post',
    data
  })
}

export function updateExamination(data) {
  return request({
    url: '/cloudfilm/examination',
    method: 'put',
    data
  })
}

export function updateExaminationStatus(examId, status) {
  return request({
    url: `/cloudfilm/examination/status/${examId}`,
    method: 'put',
    data: { status }
  })
}

export function deleteExamination(examIds) {
  return request({
    url: `/cloudfilm/examination/${examIds}`,
    method: 'delete'
  })
}
