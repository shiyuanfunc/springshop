package com.soft863.controller;


import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/4/22.
 */
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    protected final String getIpAddress() {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    protected final Map<String,String> getParam() {
        Map<String,String>  res = new HashMap<String,String>();
        //
        Map<String, String[]> map = request.getParameterMap() ;
        //
        if(map!=null && !map.isEmpty()) {
            for(Map.Entry<String, String[]> entry : map.entrySet()) {
                String[] values = (String[])entry.getValue() ;
                res.put(entry.getKey(), values[0]) ;
            }
        }
        //mysql
        Integer page = res.get("page")!=null ? Integer.parseInt(String.valueOf(res.get("page"))):1;
        Integer rows = res.get("rows")!=null ? Integer.parseInt(String.valueOf(res.get("rows"))):10;
        if(page!=null && rows !=null) {
            if(page <1 ) page = 1 ;
            int start = (page-1) * rows ;
            int end = page*rows ;
            res.put("page" , page+"") ;
            res.put("start", start+"") ;
            res.put("end" ,  end+"") ;
            res.put("rows" ,  rows+"") ;
        }
        //
        String sort = res.get("sort")!=null ? String.valueOf(res.get("sort")) : null ;
        String column = res.get("order")!=null ? String.valueOf(res.get("order")) : null ;
        if(sort!=null && column!=null) {
            String sortColumn = sort + " " + column ;
            res.put("sortColumn", sortColumn) ;
        }
        return res ;
    }
}
