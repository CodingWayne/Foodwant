
function isValidUsername (str) {
  return ['admin', 'editor'].indexOf(str.trim()) >= 0;
}

function isExternal (path) {
  return /^(https?:|mailto:|tel:)/.test(path);
}

function isCellPhone (val) {
  if (!/^09[0-9]{8}$/.test(val)) {
    return false
  } else {
    return true
  }
}

//校驗賬號
function checkUserName (rule, value, callback){
  if (value == "") {
    callback(new Error("輸入帳號"))
  } else if (value.length > 20 || value.length <3) {
    callback(new Error("帳號長度為3-20"))
  } else {
    callback()
  }
}

//校驗姓名
function checkName (rule, value, callback){
  if (value == "") {
    callback(new Error("輸入姓名"))
  } else if (value.length > 12) {
    callback(new Error("姓名長度為1-12"))
  } else {
    callback()
  }
}

function checkPhone (rule, value, callback){
  // let phoneReg = /(^1[3|4|5|6|7|8|9]\d{9}$)|(^09\d{8}$)/;
  if (value == "") {
    callback(new Error("輸入手機號碼"))
  } else if (!isCellPhone(value)) {//引入methods中封裝的檢查手機格式的方法
    callback(new Error("請輸入正確的手機號碼!"))
  } else {
    callback()
  }
}


function validID (rule,value,callback) {

  let reg = /^[A-Za-z][12]\d{8}$/
  if(value == '') {
    callback(new Error('請輸入身分證號碼'))
  } else if (reg.test(value)) {
    callback()
  } else {
    callback(new Error('身分證號碼不正確'))
  }
}