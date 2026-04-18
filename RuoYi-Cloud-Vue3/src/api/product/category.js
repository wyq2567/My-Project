import request from '@/utils/request'

// 查询商品分类列表
export function listCategory(query) {
  return request({
    url: '/product/category/list',
    method: 'get',
    params: query
  })
}

// 查询商品分类详细
export function getCategory(parentId) {
  return request({
    url: '/product/category/' + parentId,
    method: 'get'
  })
}

// 查询一级商品分类
export function getTopLevelList(parentId) {
  return request({
    url: '/product/category/topLevelList/' + parentId,
    method: 'get'
  })
}

// 新增商品分类
export function addCategory(data) {
  return request({
    url: '/product/category',
    method: 'post',
    data: data
  })
}

// 修改商品分类
export function updateCategory(data) {
  return request({
    url: '/product/category',
    method: 'put',
    data: data
  })
}

// 删除商品分类
export function delCategory(parentId) {
  return request({
    url: '/product/category/' + parentId,
    method: 'delete'
  })
}
