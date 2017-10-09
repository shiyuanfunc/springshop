package com.soft863.controller;

import com.soft863.service.UserService;
import com.soft863.utils.HttpUtils;
import org.apache.catalina.util.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/22.
 */
@Controller
@RequestMapping("/main")
public class MainController extends BaseController {

    @Autowired
    private UserService userService ;
    @RequestMapping("/")
    public ModelAndView main(){
        ModelAndView mav = new ModelAndView("home");

        Map<String,Object> user = HttpUtils.getSessionBean(request) ;
        String atype = String.valueOf(user.get("atype"));

        if("1".equals(atype)) { //管理员身份
            mav.addObject("atype" , atype) ;
            return mav ;
        }else{ //普通人身份
            mav.setViewName("redirect:/goods/index");
            return mav ;
        }
     }


    @RequestMapping("/admin")
    public ModelAndView admin(){
        ModelAndView mav = new ModelAndView("admin");
        //根据登录账号进行数据的查询
        Integer userId = HttpUtils.getUserId(request) ;
        //查询对应用户的角色
        Map<String,Object> user = HttpUtils.getSessionBean(request) ;
        String atype = String.valueOf(user.get("atype"));
        //查询所有用户，每页显示10条
        Map<String,String> params = getParam() ;

        if("1".equals(atype)) { //管理员身份
            List<Map<String,Object>> list = userService.queryUsers(params);
            mav.addObject("users" , list) ;
            //查询满足条件的记录数
            int total = userService.queryUsersCount(params);
            //根据总条数，算出来总页数
            int totalPage = (total - 1 )/Integer.parseInt(params.get("rows")) +1 ;
            mav.addObject("total" , totalPage) ;
            mav.addAllObjects(params);
            mav.addObject("atype" , atype) ;
            return mav ;
        }else{ //普通人身份
            mav.setViewName("redirect:/goods/index");
            return mav ;
        }
    }



    @RequestMapping("/next")
    public ModelAndView next(){
        ModelAndView mav = new ModelAndView("user/home");
        //根据登录账号进行数据的查询
        Integer userId = HttpUtils.getUserId(request) ;
        List<Map<String,Object>> list = userService.queryUsersByCurrentUserId(userId);
        mav.addObject("users" , list) ;
        return mav ;
    }
}
