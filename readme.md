# 專案-外賣網

## 目標用戶
1. 後台管理員: 有總權限
2. 後台員工: 登入進行菜品、套餐、訂單管理
3. 使用者: 登入進行瀏覽、增加購物車、設置地址及下單


## 功能架構

- 管理後台: 提供給餐飲業內部員工使用
    - 分類管理 -->OK
    - 菜色管理 -->OK
    - 套餐管理 -->OK
    - 口味管理 -->OK
    - 員工登入 -->OK
    - 員工退出 -->OK
    - 員工管理 -->OK
    - 訂單管理 
- 訂購前台: 提供給消費者使用
    - 手機號碼登入
    - google登入
    - 地址管理
    - 歷史訂單
    - 菜品規格
    - 購物車
    - 下單
    - 菜品瀏覽

## 專案內容
- 名稱: Foodwant
- 特殊功能: 使用者可以下單想要吃的菜，可以指定口味，由餐廳選擇接單
- DB: foodwant
## 登入系統
### 1. 管理者登入


|          |         discription          |
|:--------:|:----------------------------:|
|   URI    |       /employee/login        |
|  method  |             POST             |
| request  | {username,password} json格式 |
| response |         R<Employee>          |


處理邏輯: controller->service->Mapper->dao (Employee表)

==response==: 返回通用類 R<>
1. code: 是否登入成功 1表示成功
2. data: 從employee表撈出的管理者數據，儲存在頁面上
3. msg: response一個錯誤碼

**Controller登入邏輯**
1. 將頁面提交的密碼password進行md5加密處理
    - `password = DigestUtils.md5DigestAsHex(password.getBytes());` 
3. 根據頁面提交的username查詢數據庫
4. 如果沒有則顯示登入失敗
5. 密碼比對，不一致則顯示登入失敗
    - 明文相同，md5加密結果也相同，且DB中存放的是加密後結果，因此只要一樣便表示密碼相同
7. 觀察員工狀態，如禁用則顯示已禁用
8. 登入成功，將員工id存入Session {employee:empId}並返回Employee物件
    - 由於已設置全域ID自增`      id-type: ASSIGN_ID`，employee表的Id使用雪花演算法SnowFlake，由64位整數組成，生成後會返回給Employee類中的自增主鍵
9. 前端將Employee轉成json {userInfo:Employee} 存到localStorage並跳轉後台首頁
    
>注意使用雪花演算法可能導致精度損失
>JavaScript只能保證16位精度，所以導致剩餘精度損失，而無法配對ID
>解決: Response時將Long類型的數據統一轉換成String
>這樣ID在Session中就可以完整保存
>1. 提供對象轉換器JacksonObjectMapper，使用Jackson進行jaca對象到json格式的轉換
>2. 在WebMvcConfig配置類中設置Spring mvc的消息轉換器，此消息轉換器中使用提供的對象轉換器進行java對象到json格式的轉換

### 2. 退出功能
    
|          |   discription    |
|:--------:|:----------------:|
|   URI    | /employee/logout |
|  method  |       POST       |
| request  |                  |
| response |   R<String>    |
    

1. 返回字符串"退出成功"
2. 前端從localStorage移除userInfo


### 問題分析
#### 1. 如何防止未登入直接連接後台
只有登入成功才能進入系統後台，沒有登入的話要跳轉到登入介面
>加上過濾器或是攔截器 

攔截器
1. 建立自定義過濾器LoginCheckFilter
2. 在啟動器上加上註解`@Component` `@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")`
3. 實現`Filter`並重寫`doFilter`
4. 過濾器的處理邏輯


處理邏輯:
1. 獲取本次請求的URI
2. 判斷本次請求是否需要處理
3. 如果不須處理，直接放行
4. 判斷登入狀態，如果Session中有empId表示已登入，則直接放行
5. 如果未登入則返回未登入結果

>注意: spring框架使用Ant風格來解析路徑
>{@code *} matches zero or more characters
>{@code **} matches zero or more <em>directories</em> in a path
>在此 * 不能代替 ** ，此時我們想要訪問/admin/index，也要訪問其底下資料夾的靜態資源/admin/director/xxx.css
>那麼攔截器要攔/admin/ * 還是/admin/ **呢?
>此時需要使用AntPathMatcher類的match方法進行比較
>`public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();`
>`antPathMatcher.match("/admin/index", "/admin/**")`
    
>這邊在登入成功時以Threadlocal存入empId，以便在此線程中，自定義類可以不通過request而直接去Threadlocal中來獲得當前瀏覽器session中的使用者id

## 員工管理
功能: 各員工的CRUD、員工資訊分頁查詢

### 1. 新增員工
    
|          |   discription    |
|:--------:|:----------------:|
|   URI    |    /employee     |
|  method  |       POST       |
| request  | 員工數據 by ajax |
| response |    R<String>     |
    

1. 頁面發送ajax，將新員工數據以json格式傳送到伺服器
2. 伺服器Controller接收數據並調用Service進行保存
3. Service調用Mapper操作DB來持久化數據


#### 要處理username同名的問題
入門:使用異常攔截器攔截全域的
    `SQLIntegrityConstraintViolationException`，再處理其中的`Duplicate entry`，在之後各種表的操作中，如果有UNI鍵同名的異常，都會被我們自定義的ExceptionHandler捕獲，然後向前端發送錯誤碼

### 2. 員工資訊分頁查詢
    
|          |     discription     |
|:--------:|:-------------------:|
|   URI    |        /page        |
|  method  |         GET         |
| request  | (page,pageNum,name) |
| response |      R<Page>      |
    


1. 瀏覽器傳送ajax請求，將分頁查詢所需數據(page,pageSize,name)傳送給伺服器
2. Controller接收數據後調用Service查詢數據
3. Service調用Mapper查詢分頁數據
4. Controller將查詢到的分頁數據response給瀏覽器
5. 瀏覽器接收分頁數據並通過Elemnet UI的Table展示

### 3. 禁用員工帳號
    
|          |   discription    |
|:--------:|:----------------:|
|   URI    |    /employee     |
|  method  |       PUT        |
| request  | 員工資訊 by ajax   |
| response |    R<String>    |
    

禁用的員工帳號不能登入系統
只有管理員可以對普通員工進行禁用跟啟用，普通員工頁面不顯示禁用鈕(by前端)

### 4. 編輯員工資訊

    
**先查**
|          |   discription    |
|:--------:|:----------------:|
|   URI    |    /employee/id     |
|  method  |       PUT        |
| request  | 員工Id by Restful   |
| response |    R<employee>    |
    
**後改**
|          |   discription    |
|:--------:|:----------------:|
|   URI    |    /employee     |
|  method  |       PUT        |
| request  | 員工資訊 by ajax   |
| response |    R<String>    |

邏輯:
1. 點擊編輯按鈕，跳轉add.html，並在url中攜帶參數(id)
2. 在add.html頁面讀取url中的參數(員工id)
3. 發送ajax請求，請求伺服器，同時提交員工id參數
4. 伺服器接收請求，根據員工id查詢員工資訊，將員工資訊以json格式reponse
5. 頁面接收json格式數據，通過vue的數據綁定填入員工資訊
6. 點擊保存，發送ajax，將頁面中的員工資訊以json發給伺服器
7. 伺服器接收員工資訊保存並response

>add.html為公共頁面，新增與編輯都在此操作

## 分類管理

### 1. 公共字段自動填充
問題: 在新增和編輯員工時需要設定創建時間、創建人、修改時間。修改人等等，這些屬於公共字段，很多表都有這些字段，能不能對這些公共字段統一處理呢?
>MybatisPlus提供公共字段填充功能: 在插入或更新時為指定字段賦予指定的值
>步驟:
>1.在實體類的屬性上加上@TableField，指定自動填充的策略
>2. 按照框架要求編寫元數據對象處理器，在此類統一為公共字段賦值，此類需實現MetaObjectHandler介面
>此類會在SQL新增或修改時傳入原始物件
>或是AOP?
>>MySQL也有, `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP `當更新時自動更新時間
>>`DEFAULT CURRENT_TIMESTAMP` 創建時自動填充時間

**發現問題**: 在公共字段填充時，要自定填充創建者與修改者，也就是當前使用者id，那麼在自定義類中沒有request要怎麼獲得瀏覽器中Session裡的empId?
>如何在自定義類中獲得瀏覽器的session?
>使用ThreadLoacl 也常用解決高併發

首先梳理線程怎麼跑，以update請求為例
每當瀏覽器發送請求時，在伺服器都會分配一個線程來處理，順序為
1. LoginCheckFilter的doFilter方法
2. EmployeeController的updatez方法
3. MyMetaObjectHandler的updateFill方法

>測試: 在上面三個方法中獲取當前線程id
>`long id = Thread.currentThread(),getId();`

**ThreadLocal**
ThreadLocal並不是一個Thread，而是Thread的局部變量。當使用ThreadLocal維護變量時，ThreadLocal為每個使用該變量的線程提供獨立的變量副本，所以每一個線程都可以獨立地改變自己的副本，而不會影響其它線程所對應的副本。

==ThreadLocal為每個線程提供單獨一份存儲空間，具有線程隔離的效果，只有在線程內才能獲取到對應的值，線程外則不能訪問當前線程對應的值。==



**ThreadLocal常用方法：**

A. public void set(T value) : 設置當前線程的線程局部變量的值

B. public T get() : 返回當前線程所對應的線程局部變量的值

C. public void remove() : 刪除當前線程所對應的線程局部變量的值 



我們可以在LoginCheckFilter的doFilter方法中獲取當前登入用戶id，並調用ThreadLocal的set方法來設置當前線程的線程局部變量的值（用戶id），然後在MyMetaObjectHandler的updateFill方法中調用ThreadLocal的get方法來獲得當前線程所對應的線程局部變量的值（用戶id）。如果在後續的操作中, 我們需要在Controller / Service中要使用當前登錄用戶的ID, 可以直接從ThreadLocal直接獲取。

實現步驟：

1). 編寫BaseContext工具類 -> 基於ThreadLocal封裝的工具類

2). 在LoginCheckFilter的doFilter方法中調用BaseContext來設置當前登錄用戶的id

3). 在MyMetaObjectHandler的方法中調用BaseContext獲取登錄用戶的id

### 2. 新增分類
分類分為
1. 菜色分類: 增加菜品時要選擇菜色分類
2. 套餐分類: 增加套餐時要選擇套餐分類

在使用者介面，也會按照菜色分類和套餐分類來展示

DB: category表
- id: PRI
- type: 1表示菜色分類，2為套餐分類
- name: UNI 名
- sort: 在客戶端的排序大小
- 新增和修改的人員及時間

Entity: Category
Mapper: CategoryMapper
Service: CategoryService
ServiceImpl: CategoryServiceImpl
Controller: CategoryController
    
|          |   discription    |
|:--------:|:----------------:|
|   URI    |    /category     |
|  method  |       POST        |
| request  | name,type,sort by ajax   |
| response |    R<String>    |


**邏輯**
1. 在頁面(backend/page/category/list.html)的新增分類表單中填寫數據，點擊 "確定" 發送ajax請求，將新增分類窗口輸入的數據以json形式提交到服務端
2. 服務端Controller接收頁面提交的數據並調用Service將數據進行保存
3. Service調用Mapper操作數據庫，保存數據

### 2. 分類的分頁查詢
與員工分頁查詢相同
|          |         discription          |
|:--------:|:----------------------------:|
|   URI    |        /category/page        |
|  method  |             GET              |
| request  | (page,pageSize,name) by ajax |
| response |          R<Page>           |


1. 瀏覽器傳送ajax請求，將分頁查詢所需數據(page,pageSize,name)傳送給伺服器
2. Controller接收數據後調用Service查詢數據
3. Service調用Mapper查詢分頁數據
4. Controller將查詢到的分頁數據response給瀏覽器
5. 瀏覽器接收分頁數據並通過Elemnet UI的Table展示

### 3. 刪除分類
在分類管理頁面中可以進行菜品或套餐的刪除
但是當菜品或套餐與==子類別進行關聯==後，不能刪除
=>去對應的表查詢是否有關連，有的話就不能刪除
    
|          | discription |
|:--------:|:-----------:|
|   URI    |  /category  |
|  method  |   Delete    |
| request  | id by ajax  |
| response |  R<String>  |


**邏輯**
1. 點擊刪除，頁面發送ajax請求，將參數(id)提交到服務端
2. 服務端Controller接收頁面提交的數據並調用Service刪除數據
3. Service調用Mapper操作數據庫

>為什麼不用外鍵?



### 4. 修改分類
修改分類名稱和排序等級
>修改框內的數據同步顯示由vue的editHandle進行v-modle處理了
    
|          | discription |
|:--------:|:-----------:|
|   URI    |  /category  |
|  method  |   PUT    |
| request  | (id, name, sort) by ajax  |
| response |  R<String>  |



## 菜色管理
- 文件上下傳
- 新增菜色
- 菜色資訊修改
- 菜色分頁查詢
- 菜色起/停售&刪除

### 1. 文件上下傳 - IO流
文件上傳的form表單必須滿足
1. method = POST
    -  使用POST提交數據
2. entype = "mutilpart/form-data"
    - 使用mutilpart格式上傳文件 
3. type = "file"
    - 使用input的file上傳

```htmlembedded
<form method="post" action="/common/upload" enctype="multipart/form-data">
    <input name="myFile" type="file"  />
    <input type="submit" value="提交" /> 
</form>
```

Restful: /common/upload
==Request==: file

**伺服器如何接收客戶端上傳的文件?**

通常使用Apache的兩個組件
- commons-fileupload
- commons-io

但因操作繁瑣，在Spring框的spring-web套件對文件上傳進行封裝，簡化這兩個類的使用，只需要在Controller方法中宣告一個==MultiFile==類型的參數即可接收上傳的文件
```java
@PostMapping("/upload")
public R<String> upload(MultipartFile file){
    System.out.println(file);
    return R.success(fileName);
}
```

**文件下載**

將文件從Server傳輸到本地的過程
有兩種形式:
1. 以附件形式下載，存到本地
2. 直接在瀏覽器打開

>下載本質上就是Server將文件以流的方式寫回瀏覽器

### 2. 新增菜品
添加新菜品時，需要上傳圖片，且選擇當前菜品所屬的分類
如果增加口味，需要向dish_flavor表插入數據
    
雙表操作:
1. dish
2. disg_flavor

**邏輯**:
1. backend/page/food/add.html加載時就發送ajax，請求伺服器獲的菜品分類數據並展示到下拉框中
2. 頁面發送請求上傳圖片。伺服器將圖片保存，並response圖片名
3. 頁面根據圖片名進行圖片下載，顯示在網頁上
4. 點保存會發ajax，將菜品數據以json發給伺服器

總共4次請求
1. 獲取菜品分類列表
Restful: /category/list -->GET
==Request==: type, 1=菜品分類2=套餐分類，用來判斷要查哪個表

2. 保存菜品
Restful: /dish -->POST
==param==: 菜品資訊

保存菜品同時也要保存菜品對應的口味->雙表操作
**如何處理請求參數個數>實體類filed而無法封裝的問題?**
使用DTO(Data Transer Object)，也就是新稱xxxDTO類去繼承原實體類，等於多套一層來封裝數據

### 菜品資訊分頁查詢
較麻煩，要給瀏覽器圖片顯示，也要根據菜品的category_id去category表查這是什麼風味

邏輯:
1. 頁面在加載時會發送ajax請求，將分頁查詢相關參數(page, pageSize, name)送給伺服器
2. 頁面發送請求，請求下載圖片，用於分頁展示

### 修改菜品
修改頁面要同步顯示該菜品的資訊

與新增菜品相同，使用add.html
1. 頁面發送ajax，請求獲得分類數據，用來顯示在下拉框
2. 頁面發送ajax，請球伺服器根據id查詢當前菜品資訊，用來顯示頁面上的菜品資訊
    - Restful: /dish/{id} -->GET
3. 頁面發送請求,下載圖片並顯示
4. 保存發送ajax，將宿改後的菜品資訊以json提交
    - Request: /dish -->PUT
    - param: json

共4次請求

### 停售
URL: /dish -->delete

## 套餐管理

雙表操做
setmeal + setmeal_dish

**套餐分頁查訊的問題**

每個套餐都有對應的套餐分類，也就是Setmeal表中的category_id。在進行套餐資訊分頁查詢時，SetmealServiceImpl中會注入CategoryService，拿Setmeal表中的category_id去使用CategoryService去查category表中對應的套餐分類的名子，封裝後返回瀏覽器

但是，有另外一個業務邏輯，在CategoryService中要刪除套餐分類，必須先判斷該套餐分類是否已經有連結了的套餐，因此需要注入SetmealService來去setmeal表查詢該套餐分類在套餐表是否有連結的套餐

如此一來，SetmealService中`@Autowired`CategoryService，而
CategoryService中又`Autowired`SetmealService，造成依賴循環

解決方法: 在如此一來，SetmealService中`@Autowired`CategoryService時將上`@Lazy(true)`註解
```java
@Autowired
    @Lazy(true)
    private CategoryService categoryService;
```
功用是讓此bean延遲加載，等到要用的時候再加載

### 刪除套餐
只能刪除停售狀態的套餐


## 使用者介面
    
待
    

## 優化
>如果只剩下一道肉，廚師不做了，這時候，張三點了份肉，李四也點了份肉，但是現在只剩下一份了，這種情況如何處理呢？如何保證數據的統一，不出現重複和負數
->樂觀鎖
###### tags: `SSM framwork`