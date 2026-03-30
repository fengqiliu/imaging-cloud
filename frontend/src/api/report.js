import request from './index'

export function getReportList(params) {
  return request({
    url: '/cloudfilm/report/list',
    method: 'get',
    params
  })
}

export function getReport(reportId) {
  return request({
    url: `/cloudfilm/report/${reportId}`,
    method: 'get'
  })
}

export function getReportByExam(examId) {
  return request({
    url: `/cloudfilm/report/exam/${examId}`,
    method: 'get'
  })
}

export function addReport(data) {
  return request({
    url: '/cloudfilm/report',
    method: 'post',
    data
  })
}

export function updateReport(data) {
  return request({
    url: '/cloudfilm/report',
    method: 'put',
    data
  })
}

export function deleteReport(reportIds) {
  return request({
    url: `/cloudfilm/report/${reportIds}`,
    method: 'delete'
  })
}

export function reviewReport(reportId, data) {
  return request({
    url: `/cloudfilm/report/review/${reportId}`,
    method: 'put',
    data
  })
}
