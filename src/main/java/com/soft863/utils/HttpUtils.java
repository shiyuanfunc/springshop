package com.soft863.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by admin on 2017/4/22.
 */
public class HttpUtils {

    private static final String LOGIN_FLAG_LOCAL_SESSION_VALUE ="LOGIN_FLAG_LOCAL_SESSION_VALUE";

    private static final String BANK_NAME_INIT_CONFIG = "BANK_NAME_INIT_CONFIG" ;

    public static Map<String,Object> getSessionBean(HttpServletRequest request){
        return (Map<String,Object>)request.getSession().getAttribute(LOGIN_FLAG_LOCAL_SESSION_VALUE) ;
    }

    public static HttpSession getSession(HttpServletRequest request){
        return request.getSession();
    }

    public static void setSessionBean(HttpServletRequest request , Map<String,?> params){
        request.getSession().setAttribute(LOGIN_FLAG_LOCAL_SESSION_VALUE , params);
    }

    public static Integer getUserId(HttpServletRequest request) {
        Object obj = getSessionBean(request).get("userId") ;
        return Integer.parseInt(String.valueOf(obj));
    }

    public static String getEncoderUserId(Integer userId) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(String.valueOf(userId).getBytes()) ;
    }
    public static Integer  getDecoderUserId(String encoderUserId) {

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            String str = new String(decoder.decodeBuffer(encoderUserId));
            return Integer.parseInt(str) ;
        } catch (IOException e) {
            throw new RuntimeException("decoder失败");
        }
    }

    public static void setBanks(ServletContext context , Object obj) {
        context.setAttribute(BANK_NAME_INIT_CONFIG , obj);
    }

    public static List<Map<String,Object>> getBanks(ServletContext context) {
        return (List<Map<String,Object>>) context.getAttribute(BANK_NAME_INIT_CONFIG) ;
    }

    /**
     * 生成订单号
     * @return
     */
    public static String generateOrderNum(){
        StringBuilder sb = new StringBuilder("FXY");
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssS");
        sb.append(df.format(new Date()));
        //随机产生4个随机数
        sb.append(generateRandom(4));
        return sb.toString();
    }

    public static String generateRandom(int n) {
        StringBuilder sb = new StringBuilder("");
        for(int i=0 ; i< n ; i++) {
            sb.append(new Random().nextInt(10));
        }
        return sb.toString();
    }
}
