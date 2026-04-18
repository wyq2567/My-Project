
import request from '@/utils/request'

export function updTbProductImg(formData) {
    return request({
        url: '/file/upload',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export function listImg(query) {
    return request({
        url: '/product/productImage/list',
        method: 'get',
        params: query
    })
}

export function delImg(url) {
    return request({
        url: '/file/delete',
        method: 'delete',
        params: url
    })
}

export function addImg(params) {
    return request({
        url: '/product/productImage',
        method: 'post',
        data: params
    })
}

export function delDbImg(id) {
    return request({
        url: '/product/productImage/' + id,
        method: 'delete'
    })
}
