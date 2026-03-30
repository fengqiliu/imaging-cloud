import request from './index'

// User management
export function getUserList(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

export function getUser(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

export function deleteUser(userIds) {
  return request({
    url: `/system/user/${userIds}`,
    method: 'delete'
  })
}

export function resetUserPwd(userId, password) {
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    data: { userId, password }
  })
}

// Role management
export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

export function getRole(roleId) {
  return request({
    url: `/system/role/${roleId}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

export function deleteRole(roleIds) {
  return request({
    url: `/system/role/${roleIds}`,
    method: 'delete'
  })
}

// Department management
export function getDeptList(params) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params
  })
}

export function getDeptTreeselect() {
  return request({
    url: '/system/dept/treeselect',
    method: 'get'
  })
}

export function getDept(deptId) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'get'
  })
}

export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

export function deleteDept(deptId) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'delete'
  })
}

// Dictionary management
export function getDictTypeList(params) {
  return request({
    url: '/system/dict/type/list',
    method: 'get',
    params
  })
}

export function getConfigList(params) {
  return request({
    url: '/system/config/list',
    method: 'get',
    params
  })
}

// Log management
export function getOperLogList(params) {
  return request({
    url: '/monitor/operlog/list',
    method: 'get',
    params
  })
}

export function deleteOperLog(operIds) {
  return request({
    url: `/monitor/operlog/${operIds}`,
    method: 'delete'
  })
}

export function cleanOperLog() {
  return request({
    url: '/monitor/operlog/clean',
    method: 'delete'
  })
}
