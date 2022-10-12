package com.foodwant.foodwant.filter;

import com.alibaba.fastjson.JSON;
import com.foodwant.foodwant.common.BaseContext;
import com.foodwant.foodwant.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 檢查使用者是否完成登入
 *
 * @author JIA-YANG, LAI
 * @create 2022-10-07 下午 10:12
 */
@Slf4j
@Component
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")//宣告這是一個過濾器類別
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        1. 獲取本次請求的URI
        String requestURI = request.getRequestURI();
        log.info("攔截請求: {}", request.getRequestURI());
        //定義不須處理的請求
        //1.登入請求
        //2.登出請求
        //3.backend下的靜態資源，直接放行，只攔controller的動態請求
        //4.front下的靜態資源

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"

        };
//        2. 判斷本次請求是否需要處理
        boolean check = check(urls, requestURI);
//        3. 如果不須處理，直接放行
        if(check){
            log.info("本次請求不須處理");
            filterChain.doFilter(request, response);//放行
            return;
        }
//        4. 判斷員工登入狀態，如果已登入，則直接放行
        if(request.getSession().getAttribute("employee") != null){
            long id = Thread.currentThread().getId();
            log.info("目前線程id為: {}",id);
            //替此線程存入session->使用者id
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            log.info("員工已登入, id: {}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);//放行

            return;
        }
        //  5. 判斷使用者登入狀態，如果已登入，則直接放行
        if(request.getSession().getAttribute("user") != null){

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            log.info("使用者已登入, id: {}",request.getSession().getAttribute("user"));
            filterChain.doFilter(request, response);//放行

            return;
        }

        log.info("未登入");
//        5. 如果未登入則返回未登入結果，通過輸出流response數據
        response.getWriter().write(JSON.toJSONString(R.error("NOT LOGGIN")));

        return;

    }

    /**
     * 路徑比較，檢查本次請求是否需要放行
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }

        }
        return false;
    }
}
