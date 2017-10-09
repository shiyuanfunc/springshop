package com.soft863.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.soft863.config.AlipayClientAutoSetter;
import com.soft863.service.LoginService;
import com.soft863.service.SysService;
import com.soft863.utils.HttpUtils;
import com.soft863.utils.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by admin on 2017/4/22.
 */
@Controller
@RequestMapping("/login")
@Scope("prototype")
public class LoginController extends BaseController{

    @Autowired
    private LoginService loginService ;

    @Autowired
    private AlipayClientAutoSetter client ;

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value="/")
    public ModelAndView login(RedirectAttributes attributes ,HttpServletResponse response){
        ModelAndView mav = new ModelAndView("redirect:/main/");
        Map<String,String> params = getParam() ;

        String _psw = null ;
        if(!params.containsKey("tel") && !params.containsKey("password")) {
            //从Cookie中获取对应的信息
            Cookie[] cookies = request.getCookies();
            if(cookies!=null) {
                for (Cookie c : cookies) {
                    if ("fxy_login_flag_tel".equals(c.getName())){
                        params.put("tel" , c.getValue()) ;
                    }
                    if("fxy_login_flag_ss".equals(c.getName())){
                        _psw = c.getValue() ;
                    }
                }
            }
        }

        //进行登录
        Map<String,Object> user = loginService.loginUser(params);
        if(user==null || user.isEmpty()) {
            params.put("flag" ,"0");
            params.put("msg","用户名不存在");
            attributes.addFlashAttribute("QP" ,params);
            mav.setViewName("redirect:/c/login");
            return mav ;
        }
        String psw = "" ; //初始密码
        //管理人员
        if("1".equals(user.get("atype"))) {
            psw = _psw!=null ? _psw : String.valueOf(params.get("password"));
        }else{
            if(_psw==null) {
                psw = MD5Utils.md5(params.get("password"));
            }else{
                psw = _psw ; //从cookie中获取的值
            }
        }
        if(!psw.equals(user.get("password"))) {
            params.put("flag" ,"1");
            params.put("msg","密码不正确");
            attributes.addFlashAttribute("QP" ,params);
            mav.setViewName("redirect:/c/login");
            return mav ;
        }
        //登录成功的时候，获取登录者IP地址
        params.put("ip" , getIpAddress()) ;
        params.put("userId", String.valueOf(user.get("id")));
        //记录登录ip地址
        loginService.reChangeHistory(params);

        params.put("atype" , String.valueOf(user.get("atype"))) ;
        //设置登录成功表示
        HttpUtils.setSessionBean(request , params);

        if(!"1".equals(user.get("atype"))) {
            //设置cookie信息
            Cookie u = new Cookie("fxy_login_flag_tel", params.get("tel"));
            Cookie p = new Cookie("fxy_login_flag_ss", psw);
            u.setMaxAge(365 * 24 * 60 * 60);
            // u.setPath("/");
            p.setMaxAge(365 * 24 * 60 * 60);
            // p.setPath("/");
            response.addCookie(u);
            response.addCookie(p);
        }
        return mav ;
    }

    @RequestMapping(value="/out")
    public ModelAndView logout(HttpServletResponse response){
        ModelAndView mav = new ModelAndView("redirect:/c/login");
        //退出系统
        request.getSession().invalidate();
        //删除Cookie
        //设置cookie信息
        Cookie u = new Cookie("fxy_login_flag_tel", "");
        Cookie p = new Cookie("fxy_login_flag_ss", "");
        u.setMaxAge(0);
        // u.setPath("/");
        p.setMaxAge(0);
        // p.setPath("/");
        response.addCookie(u);
        response.addCookie(p);

        return mav ;
    }
    @RequestMapping(value="/res",method= RequestMethod.POST)
    public ModelAndView regist(RedirectAttributes attributes){
        String uri = request.getRequestURI();
        StringBuffer url = request.getRequestURL();
        String sz = url.toString();
        ModelAndView mav = new ModelAndView("redirect:/c/login");
        //获取页面参数
        Map<String,String> params = getParam() ;
        params.put("password" , MD5Utils.md5(params.get("password")));
        //查询用户名是否存在
        Map<String,Object> user = loginService.loginUser(params);

        String pid = params.get("pid") ;
        String viewName = "redirect:/c/register" ;
        if(pid!=null && !"".equals(pid)){
            mav.addObject("pid", pid) ;
            viewName = "redirect:/user/reg{pid}" ;
        }


        if(user!=null && !user.isEmpty()) {
            mav.setViewName(viewName);
            attributes.addFlashAttribute("msg","用户名已经存在");
            return mav ;
        }
       boolean flag =  loginService.saveUser(params); //保存用户
        if(!flag) {
            mav.setViewName(viewName);
            attributes.addFlashAttribute("msg","当前会员下线已经达到满员，不能继续发展会员，请更换上线");
            return mav ;
        }
        return mav ;
    }

    @RequestMapping(value="/thirdLogin" ,method=RequestMethod.GET)
    public void thirdLogin(HttpServletResponse response) throws IOException{
        String url = client.getLoginUrl()+"?app_id="+client.getAppid()+"&scope=auth_user&redirect_uri="+client.getThridLoginUrl() ;
        response.sendRedirect(url);
    }

    @RequestMapping(value="/thridLoginAccess" ,method=RequestMethod.GET)
    public ModelAndView thridLoginAccess(HttpServletResponse response , ModelMap map) throws Exception{
        ModelAndView mav = new ModelAndView("thridLogin");
        //获取参数
        Map<String, String> param = getParam();
        mav.addAllObjects(param) ;
        //获取user_id
        AlipayClient alipayClient = new DefaultAlipayClient(client.getUrl(), client.getAppid(),
                client.getPrivateKey(), "JSON", "utf-8", client.getZfbPublicKey(), "RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(param.get("auth_code"));
        request.setGrantType("authorization_code");
        AlipaySystemOauthTokenResponse resp = alipayClient.execute(request) ;
        if(resp.isSuccess()) {
            String json = resp.getBody() ;
            logger.error("------------------------------------------------");
            logger.error(json);
            logger.error("------------------------------------------------");
            JSONObject jsonObject = JSONArray.parseObject(json).getJSONObject("alipay_system_oauth_token_response");
            mav.addObject("access_token" , jsonObject.get("access_token"));
            //查询当前支付宝用户是否已经绑定系统账号
            String zfb_open_id = String.valueOf(jsonObject.get("user_id"));
            //查询用户ID是否已经存在
            Map<String,Object> user = loginService.checkUserBindZfb(zfb_open_id);
            if(user!=null && !user.isEmpty()) { //已绑定账号
                mav.setViewName("redirect:/login/");
                if(!"1".equals(user.get("atype"))) {
                    //设置cookie信息
                    Cookie u = new Cookie("fxy_login_flag_tel", String.valueOf(user.get("tel")));
                    Cookie p = new Cookie("fxy_login_flag_ss", String.valueOf(user.get("password")));
                    u.setMaxAge(365 * 24 * 60 * 60);
                    p.setMaxAge(365 * 24 * 60 * 60);
                    response.addCookie(u);
                    response.addCookie(p);
                }
            }
        }else{
            mav.addAllObjects(map);
        }
        return mav ;
    }

    @RequestMapping(value="thridLoginSubmit" , method=RequestMethod.POST)
    public ModelAndView thridLoginSubmit(@RequestParam("access_token") String access_token ,
                                        /* @RequestParam("account")Integer account ,*/
                                         @RequestParam("tel")String tel,
                                         HttpServletResponse response,
                                         RedirectAttributes attributes) throws Exception{
        ModelAndView mav = new ModelAndView("redirect:/login/");

        Map<String,String> param = getParam() ;

        AlipayClient alipayClient = new DefaultAlipayClient(client.getUrl(), client.getAppid(),
                client.getPrivateKey(), "JSON", "utf-8", client.getZfbPublicKey(), "RSA2");
        AlipayUserInfoShareRequest req = new AlipayUserInfoShareRequest();
        AlipayUserInfoShareResponse resp = alipayClient.execute(req,access_token);
        String json = resp.getBody() ;
        logger.error("-----------------------查询支付宝用户信息-------------------------");
        logger.error(json);
        logger.error("------------------------------------------------");
        JSONObject jsonObject = JSONArray.parseObject(json).getJSONObject("alipay_user_info_share_response");
        if(resp.isSuccess()) {
           // if(account==2){ //已有账号，根据tel更新用户信息
                jsonObject.put("tel" , tel) ;
            Map<String, Object> res = loginService.updateUserByTel(jsonObject);
            if("error".equals(res.get("status"))) {
                attributes.addFlashAttribute("access_token" ,access_token) ;
                attributes.addFlashAttribute("msg" , res.get("msg"));
                mav.setViewName("redirect:/login/thridLoginAccess");
                return mav ;
            }
          /*  }else{ //注册用户。。。
                jsonObject.putAll(param);
                loginService.registBindZfb(jsonObject);
            }*/
            //添加信息
            String zfb_open_id = String.valueOf(jsonObject.get("user_id"));
            //查询用户ID是否已经存在
            Map<String,Object> user = loginService.checkUserBindZfb(zfb_open_id);
            if(user!=null && !user.isEmpty()) { //已绑定账号
                mav.setViewName("redirect:/login/");
                if(!"1".equals(user.get("atype"))) {
                    //设置cookie信息
                    Cookie u = new Cookie("fxy_login_flag_tel", String.valueOf(user.get("tel")));
                    Cookie p = new Cookie("fxy_login_flag_ss", String.valueOf(user.get("password")));
                    u.setMaxAge(365 * 24 * 60 * 60);
                    p.setMaxAge(365 * 24 * 60 * 60);
                    response.addCookie(u);
                    response.addCookie(p);
                }
            }

        }else{
            attributes.addFlashAttribute("access_token" ,access_token) ;
            attributes.addFlashAttribute("msg" , jsonObject.get("sub_msg"));
            mav.setViewName("redirect:/login/thridLoginAccess");
            return mav ;
        }

       /* //查询用户信息
        */
        return mav ;
    }
}
