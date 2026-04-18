import request from '@/utils/request'

// 查询商品管理列表
export function listProduct(query) {
  return request({
    url: '/product/product/list',
    method: 'get',
    params: query
  })
}

// 查询商品管理详细
export function getProduct(productNo) {
  return request({
    url: '/product/product/' + productNo,
    method: 'get'
  })
}

// 新增商品管理
export function addProduct(data) {
  return request({
    url: '/product/product',
    method: 'post',
    data: data
  })
}

// 修改商品管理
export function updateProduct(data) {
  return request({
    url: '/product/product',
    method: 'put',
    data: data
  })
}

// 删除商品管理
export function delProduct(productNo) {
  return request({
    url: '/product/product/' + productNo,
    method: 'delete'
  })
}
