package com.soft863.controller;

import com.soft863.service.OrderService;
import com.soft863.service.UserService;
import com.soft863.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huo on 2017/5/17.
 */
@Controller
@RequestMapping("/order")
@Scope("prototype")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService ;
    @Autowired
    private UserService userService ;



    @RequestMapping(value="/query")
    public ModelAndView queryOrders() {
        ModelAndView mav = new ModelAndView("order/home");
        Map<String,String> param = this.getParam();
        param.put("userId" , String.valueOf(HttpUtils.getUserId(request)));
        //查询订单信息
        List<Map<String,Object>> list = orderService.queryOrders(param) ;
        mav.addObject("list" , list) ;
        return mav ;
    }

    @RequestMapping(value="/cancel/{id}",method= RequestMethod.GET)
    public ModelAndView cancelOrder(@PathVariable("id")Integer id){
        ModelAndView mav = new ModelAndView("redirect:/order/query");
        Map<String,String> params = this.getParam();
        params.put("id" , id+"") ;
        params.put("userId" , String.valueOf(HttpUtils.getUserId(request)));
        orderService.cancelOrder(params);
        return mav ;
    }

    @RequestMapping(value="/detail/{id}",method= RequestMethod.GET)
    public ModelAndView detailOrder(@PathVariable("id")Integer id){
        ModelAndView mav = new ModelAndView("order/detail");
        Map<String,String> params = this.getParam();
        params.put("id" , id+"") ;
        params.put("userId" , String.valueOf(HttpUtils.getUserId(request)));
        List<Map<String,Object>> list = orderService.detailOrder(params);
        mav.addObject("list" , list) ;

        return mav ;
    }


    @RequestMapping(value="/index")
    public ModelAndView queryAdminOrders() {
        ModelAndView mav = new ModelAndView("order/index");
        Map<String,String> param = this.getParam();
        //查询订单信息
        List<Map<String,Object>> list = orderService.queryAdminOrders(param) ;
        mav.addObject("list" , list) ;
        //查询满足条件的总条数
        int total = orderService.queryAdminOrdersCount(param);
        mav.addAllObjects(param) ;
        int totalPage = (total - 1 )/Integer.parseInt(param.get("rows")) +1 ;
        mav.addObject("total",totalPage);
        return mav ;
    }

    @RequestMapping(value="/setting/{id}/config/{status}")
    public ModelAndView queryAdminOrders(@PathVariable("id")Integer id ,@PathVariable("status")Integer status) {
        ModelAndView mav = new ModelAndView("redirect:/order/index");
        Map<String,String> param = this.getParam();
        param.put("id" , id+"") ;
        param.put("status" , status+"");
        if(status ==2) {
            param.put("payTime" , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }

        //查询订单信息
        orderService.settingConfig(param);


        return mav ;
    }

    /**
     * 返利管理
     * @return
     */
    @RequestMapping(value="/rebate")
    public ModelAndView rebate(){
        Map<String,String> params = getParam() ;
        ModelAndView mav = new ModelAndView("order/rebate");
        //查询所有用户信息，并获取用户近一个月应返利金额
        List<Map<String,Object>> list = userService.queryUsersReate(params);
        /*
        for(Map<String,Object> map : list) {

        }*/

        mav.addObject("users" , list) ;
        //查询满足条件的记录数
        int total = userService.queryUsersReateCount(params);
        //根据总条数，算出来总页数
        int totalPage = (total - 1 )/Integer.parseInt(params.get("rows")) +1 ;
        mav.addObject("total" , totalPage) ;
        mav.addAllObjects(params);
        return mav ;
    }

    @RequestMapping(value="/queryRebate/{userId}")
    public @ResponseBody Map<String,Object> queryRebate(@PathVariable("userId")Integer userId){

        return orderService.queryRebate(userId) ;
    }

    /**
     * 设置返现金额
     * @param userId
     * @return
     */
    @RequestMapping(value="/setRebate/{userId}")
    public @ResponseBody Map<String,Object> setRebate(@PathVariable("userId") Integer userId){
        Map<String,Object> params = new HashMap<>();
        params.put("ip" , this.getIpAddress());
        params.put("userId" , userId);
        //获取当前登录这账号
        params.put("account" , HttpUtils.getUserId(request));

        return orderService.saveRebate(params);
    }


    @RequestMapping(value="/{orderId}back")
    public @ResponseBody Map<String,Object> backMoney(@PathVariable("orderId") Integer orderId){
        Map<String,Object> params = new HashMap<>();

        params.put("orderId" , orderId);

        return orderService.backMoney(params);
    }

}
