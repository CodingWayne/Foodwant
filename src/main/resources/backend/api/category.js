// 查詢列表介面
const getCategoryPage = (params) => {
  return $axios({
    url: '/category/page',
    method: 'get',
    params
  })
}

// 編輯頁面反查詳情介面
const queryCategoryById = (id) => {
  return $axios({
    url: `/category/${id}`,
    method: 'get'
  })
}

// 刪除當前列的介面
const deleCategory = (id) => {
  return $axios({
    url: '/category',
    method: 'delete',
    params: {id}
  })
}

// 修改介面
const editCategory = (params) => {
  return $axios({
    url: '/category',
    method: 'put',
    data: { ...params }
  })
}

// 新增介面
const addCategory = (params) => {
  return $axios({
    url: '/category',
    method: 'post',
    data: { ...params }
  })
}