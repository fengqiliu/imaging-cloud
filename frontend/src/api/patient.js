import request from './index'

export function getPatientList(params) {
  return request({
    url: '/cloudfilm/patient/list',
    method: 'get',
    params
  })
}

export function getPatient(patientId) {
  return request({
    url: `/cloudfilm/patient/${patientId}`,
    method: 'get'
  })
}

export function addPatient(data) {
  return request({
    url: '/cloudfilm/patient',
    method: 'post',
    data
  })
}

export function updatePatient(data) {
  return request({
    url: '/cloudfilm/patient',
    method: 'put',
    data
  })
}

export function deletePatient(patientIds) {
  return request({
    url: `/cloudfilm/patient/${patientIds}`,
    method: 'delete'
  })
}
