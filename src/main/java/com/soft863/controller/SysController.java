package com.soft863.controller;

import com.soft863.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/30.
 */
@Controller
@RequestMapping("/sys")
@Scope("prototype")
public class SysController extends BaseController {

    @Autowired
    private SysService sysService ;

    @RequestMapping(value="/config/{tagId}" , method= RequestMethod.POST)
    public @ResponseBody Map<String,Object> sysConfigSetting(@PathVariable("tagId")Integer tagId ,@RequestParam String content, @RequestParam String paramName){
        Map<String,Object> params = new HashMap<>() ;
        params.put("id" , tagId) ;
        params.put("paramValue" , content);
        params.put("paramName" , paramName);
        params.put("paramDesc" , paramName);
        Map<String,Object> res =  sysService.sysConfigSetting(params);
        if("success".equals(res.get("status")) && "1".equals(String.valueOf(tagId))){
            request.getSession().getServletContext().setAttribute("SITE_SYS_NAME" , content);
        }
        return res  ;
    }

    @RequestMapping(value="/home" ,method=RequestMethod.GET)
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView("sys/home");
        //查询系统配置信息
        List<Map<String,Object>> list = sysService.querySysConfigs();

        //遍历map，并存储对应的信息到 mav中
        list.forEach((map) -> {
            switch (String.valueOf(map.get("id"))) {
                case "1" : mav.addObject("sysName" , map) ; break ;
                case "2" : mav.addObject("level" , map) ;break ;
                case "3" : mav.addObject("userNum" , map) ;break ;
            }
        });
        //查询用户等级返利配置信息
        List<Map<String,Object>> rates = sysService.querySysLevelConfigs();
        mav.addObject("rates" , rates) ;
        return mav ;
    }

    @RequestMapping(value="/levelConfig" , method= RequestMethod.POST)
    public @ResponseBody Map<String,Object> sysLevelConfigSetting(@RequestParam String ls, @RequestParam String rate){
        Map<String,Object> params = new HashMap<>() ;
        params.put("ls" , ls);
        params.put("rate" , rate);
        return sysService.sysLevelConfigSetting(params);
    }

}
