//獲取所有地址
function addressListApi() {
    return $axios({
        'url': '/addressBook/list',
        'method': 'get',
    })
}

//獲取最新地址
function addressLastUpdateApi() {
    return $axios({
        'url': '/addressBook/lastUpdate',
        'method': 'get',
    })
}

//新增地址
function  addAddressApi(data){
    return $axios({
        'url': '/addressBook',
        'method': 'post',
        data
    })
}

//修改地址
function  updateAddressApi(data){
    return $axios({
        'url': '/addressBook',
        'method': 'put',
        data
    })
}

//刪除地址
function deleteAddressApi(params) {
    return $axios({
        'url': '/addressBook',
        'method': 'delete',
        params
    })
}

//查詢單個地址
function addressFindOneApi(id) {
    return $axios({
        'url': `/addressBook/${id}`,
        'method': 'get',
    })
}

//設置默認地址
function  setDefaultAddressApi(data){
    return $axios({
        'url': '/addressBook/default',
        'method': 'put',
        data
    })
}

//獲取默認地址
function getDefaultAddressApi() {
    return $axios({
        'url': `/addressBook/default`,
        'method': 'get',
    })
}