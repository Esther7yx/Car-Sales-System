import request from '@/utils/request'

// 客户管理API
export const customerApi = {
  // 分页查询客户列表
  getCustomerList(params) {
    return request({
      url: '/api/customer/page',
      method: 'get',
      params
    })
  },

  // 根据ID查询客户
  getCustomerById(id) {
    return request({
      url: `/api/customer/${id}`,
      method: 'get'
    })
  },

  // 根据身份证号查询客户
  getCustomerByIdNumber(idNumber) {
    return request({
      url: `/api/customer/id-number/${idNumber}`,
      method: 'get'
    })
  },

  // 根据手机号查询客户
  getCustomerByPhone(phone) {
    return request({
      url: `/api/customer/phone/${phone}`,
      method: 'get'
    })
  },

  // 添加客户
  addCustomer(data) {
    return request({
      url: '/api/customer',
      method: 'post',
      data
    })
  },

  // 修改客户
  updateCustomer(data) {
    return request({
      url: '/api/customer',
      method: 'put',
      data
    })
  },

  // 删除客户
  deleteCustomer(id) {
    return request({
      url: `/api/customer/${id}`,
      method: 'delete'
    })
  },

  // 批量删除客户
  batchDeleteCustomer(ids) {
    return request({
      url: '/api/customer/batch',
      method: 'delete',
      data: ids
    })
  },

  // 获取所有客户列表
  getAllCustomers() {
    return request({
      url: '/api/customer/list',
      method: 'get'
    })
  }
}