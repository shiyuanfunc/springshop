package com.soft863.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.soft863.config.AlipayClientAutoSetter;
import com.soft863.service.GoodsService;
import com.soft863.service.UserService;
import com.soft863.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by huo on 2017/5/18.
 */
@Controller
@RequestMapping("/goods")
@Scope("prototype")
public class GoodsController extends BaseController{

    @Autowired
    private GoodsService goodsService ;
    @Autowired
    private UserService userService ;

    @Autowired
    private AlipayClientAutoSetter client ;

    private Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @RequestMapping(value="/save" , method= RequestMethod.POST)
    public ModelAndView saveGoods(RedirectAttributes r ,MultipartFile photo ,
                                  @RequestParam("goodsName")String goodsName ,
                                  @RequestParam("goodsPrice")String goodsPrice ,
                                  @RequestParam("goodsDesc")String goodsDesc,
                                  @RequestParam("goodsPrice2")String goodsPrice2) throws Exception{
        ModelAndView mav = new ModelAndView("redirect:/c/goods/home");
        Map<String,Object> param = new HashMap<>();
        if(photo!=null && !photo.isEmpty()) {
            //存放到网站目录
            String path = request.getSession().getServletContext().getRealPath("/public/upload") ;
            String fileName = UUID.randomUUID().toString()+"." + photo.getOriginalFilename().split("\\.")[1] ;
            File file = new File(path , fileName);
            file.getParentFile().mkdirs();
            photo.transferTo(file);
            param.put("photo" , "upload/"+ fileName) ;
        }
        param.put("goodsName",goodsName);
        param.put("goodsPrice",goodsPrice);
        param.put("goodsDesc",goodsDesc);
        param.put("goodsPrice2",goodsPrice2);
        Map<String,Object> map = goodsService.saveGoods(param);
        r.addFlashAttribute("map" , map) ;
        return mav ;
    }

    @RequestMapping(value="/query")
    public ModelAndView queryAdminGoods() {
        ModelAndView mav = new ModelAndView("goods/list");
        Map<String,String> param = getParam() ;
        List<Map<String,Object>> list = goodsService.queryGoods(param);
        mav.addObject("list",list) ;
        return mav ;
    }

    @RequestMapping(value="/update" , method=RequestMethod.POST)
    public @ResponseBody Map<String,Object> updateGoods(@RequestParam("id") Integer id) {
        Map<String,String> param = getParam() ;
        return  goodsService.updateGoods(param);
    }

    @RequestMapping(value="/index")
    public ModelAndView queryGoods() {
        ModelAndView mav = new ModelAndView("goods/index");
        Map<String,String> param = getParam() ;
        List<Map<String,Object>> list = goodsService.queryGoods(param);
        mav.addObject("list",list) ;
        return mav ;
    }

    @RequestMapping(value="/detail/{id}")
    public ModelAndView queryGoodsDetail(@PathVariable("id")Integer id){
        ModelAndView mav = new ModelAndView("goods/detail");
        mav.addAllObjects(goodsService.queryGoodsById(id));
        return mav ;
    }


    @RequestMapping(value="/addShop" ,method=RequestMethod.POST)
    public @ResponseBody Map<String,Object> addShop(){
        Map<String,String> param = getParam();
        param.put("userId" , String.valueOf(HttpUtils.getUserId(request))) ;
        return goodsService.addShop(param);
    }

    @RequestMapping(value="/cards")
    public ModelAndView queryShopcards() {
        ModelAndView mav =new ModelAndView("goods/cards");
        Map<String,String> param = getParam();
        param.put("userId" , String.valueOf(HttpUtils.getUserId(request))) ;
        List<Map<String,Object>> list = goodsService.queryShopcards(param);
        mav.addObject("list" , list) ;
        //查询个人信息
        //Map<String,Object> maps = userService.queryUserById(HttpUtils.getUserId(request)) ;
        List<Map<String,Object>> maps = userService.queryUserAddressMessage(HttpUtils.getUserId(request));
        mav.addObject("maps" , maps) ;
        return mav ;
    }

    @RequestMapping(value="/remove/{id}")
    public ModelAndView removeShopCards(@PathVariable("id")Integer id){
        ModelAndView mav = new ModelAndView("redirect:/goods/cards");
        Map<String,String> param = getParam();
        param.put("id" , id+"") ;
        param.put("userId" , String.valueOf(HttpUtils.getUserId(request))) ;
        goodsService.removeShopcards(param);
        return mav ;
    }

    @RequestMapping(value="/setNum",method=RequestMethod.POST)
    public @ResponseBody Map<String,Object> setGoodsNum(){
        Map<String,String> param = getParam();
        return goodsService.settingsGoodsNum(param);
    }

    @RequestMapping(value="/jiesuan",method=RequestMethod.POST)
    public void jiesuan(
            @RequestParam String addrId ,
            @RequestParam String money ,
            @RequestParam("cardIds[]") String[] cardIds ,
            HttpServletResponse response) throws Exception{
        Map<String,String> param = new HashMap<>() ;
        param.put("addrId" , addrId);
        param.put("money" , money) ;
        param.put("userId" , String.valueOf(HttpUtils.getUserId(request))) ;
        //生成订单信息
        String orderNum = HttpUtils.generateOrderNum() ;
        param.put("orderNum" , orderNum) ;

        Map<String,Object> maps = goodsService.jiesuan(param, cardIds);
        if("success".equals(maps.get("status"))){

            AlipayClient alipayClient =  new DefaultAlipayClient(client.getUrl(), client.getAppid(),
                    client.getPrivateKey() ,"json" , "UTF-8" , client.getPublicKey() , "RSA2");

            AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();// 创建API对应的request

            alipayRequest.setNotifyUrl(client.getReturnUrl());
            alipayRequest.setReturnUrl(client.getReturnUrl());
            String title = URLEncoder.encode("福襄缘酒支付","UTF-8");
            //开始进行订单处理
            alipayRequest.setBizContent("{"
                    + " \"out_trade_no\":\""+ orderNum +"\","
                    + " \"total_amount\":\""+money+"\","
                    + " \"subject\":\""+ title +"\","
                    + " \"product_code\":\"QUICK_WAP_PAY\"" + " }");// 填充业务参数
            String form = alipayClient.pageExecute(alipayRequest).getBody(); // 调用SDK生成表单

            logger.error("--------------------------------------------------");
            logger.error(form);
            logger.error("--------------------------------------------------");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(form);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        }
    }
}
