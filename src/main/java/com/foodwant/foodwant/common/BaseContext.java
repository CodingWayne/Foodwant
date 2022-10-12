package com.foodwant.foodwant.common;

/**
 * 封裝ThreadLocal的工具類，使用者保存和或取當前瀏覽器session中的使用者id
 * 不同請求就是不同的線程，當來操作此類時，操作的是他們自己線程儲存空間
 * 為線程安全
 * @author JIA-YANG, LAI
 * @create 2022-10-08 下午 04:36
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal= new ThreadLocal<>();//generic為存id的long

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return  threadLocal.get();
    }

}
