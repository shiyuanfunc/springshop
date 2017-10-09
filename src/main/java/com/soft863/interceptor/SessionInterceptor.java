package com.soft863.interceptor;

import com.soft863.utils.HttpUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by huo on 2017/5/3.
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //判断请求类型
       /* if(o instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) o ;

        }*/
        if(HttpUtils.getSessionBean(request)==null || HttpUtils.getSessionBean(request).isEmpty()) {
            //从Cookie中获取对应的信息
            Cookie[] cookies = request.getCookies();

            boolean b = false , f = false ;
            if(cookies!=null) {
                for (Cookie c : cookies) {
                    if ("fxy_login_flag_tel".equals(c.getName())){
                      b = true ;
                    }
                    if("fxy_login_flag_ss".equals(c.getName())){
                        f = true ;
                    }
                }
                if(b && f) {
                    response.sendRedirect(request.getContextPath()+"/login/");
                    return false ;
                }
            }
            response.sendRedirect(request.getContextPath()+"/c/login");
            return false ;
        }
        return true ;
    }
}
