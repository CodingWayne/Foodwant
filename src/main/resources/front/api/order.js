//提交訂單
function  addOrderApi(data){
    return $axios({
        'url': '/order/submit',
        'method': 'post',
        data
    })
}

//查詢所有訂單
function orderListApi() {
    return $axios({
        'url': '/order/list',
        'method': 'get',
    })
}

//分頁查詢訂單
function orderPagingApi(data) {
    return $axios({
        'url': '/order/userPage',
        'method': 'get',
        params:{...data}
    })
}

//再來一單
function orderAgainApi(data) {
    return $axios({
        'url': '/order/again',
        'method': 'post',
        data
    })
}