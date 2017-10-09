package com.soft863.controller;

import com.soft863.service.UserService;
import com.soft863.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/26.
 */
@Controller
@RequestMapping("/user")
@Scope("prototype")
public class UserController extends BaseController {

    @Autowired
    private UserService userService ;

    @RequestMapping(value="/qudl")
    public ModelAndView queryUserDetail() {
        ModelAndView mav = new ModelAndView("user/user") ;
        //获取登录用户ID
        Integer userId = HttpUtils.getUserId(request) ;
        //根据ID查询用户信息
        Map<String,Object> maps = userService.queryUserById(userId) ;
        mav.addObject("user" , maps) ;
        mav.addObject("master" , HttpUtils.getEncoderUserId(userId)) ;
        return mav ;
    }

    @RequestMapping(value="/reg{master}")
    public ModelAndView registByPID(@PathVariable("master") String master , Model model) {
        ModelAndView mav = new ModelAndView("user/reg") ;
        mav.addObject("master" , master) ;
        return mav ;
    }

    @RequestMapping(value="/detail/{userId}")
    public ModelAndView registByPID(@PathVariable("userId") Integer userId) {
        ModelAndView mav = new ModelAndView("user/home") ;
        List<Map<String,Object>> users = userService.queryUsersByCurrentUserId(userId) ;
        mav.addObject("users" , users) ;
        return mav ;
    }

    @RequestMapping(value="/saveAddr" ,method= RequestMethod.POST)
    public @ResponseBody Map<String,Object> saveAddr(){
        Map<String,String> params = this.getParam() ;
        params.put("userId" , String.valueOf(HttpUtils.getUserId(request))) ;
        return userService.saveAddrs(params);
    }

    @RequestMapping(value="/remove{id}",method=RequestMethod.POST)
    public @ResponseBody Map<String,Object> removeAddr(@PathVariable("id")Integer id){
        return userService.removeAddr(id);
    }



    @RequestMapping(value="/rebate")
    public ModelAndView registByPID() {
        ModelAndView mav = new ModelAndView("user/rebate") ;
        Integer userId = HttpUtils.getUserId(request);
        List<Map<String,Object>> list = userService.queryUserRebate(userId) ;
        mav.addObject("list" , list) ;
        return mav ;
    }
}
