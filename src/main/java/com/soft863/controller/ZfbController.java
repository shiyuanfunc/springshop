package com.soft863.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.soft863.config.AlipayClientAutoSetter;
import com.soft863.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by huokundian on 2017/8/10.
 */

@Controller
@RequestMapping(value="/zfb")
@Scope("prototype")
public class ZfbController extends BaseController {

    @Autowired
    private OrderService orderService ;

    @Autowired
    private AlipayClientAutoSetter client ;

    private Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @RequestMapping(value="callback")
    public ModelAndView callback() throws  Exception {
        ModelAndView mav = new ModelAndView("redirect:/order/query");
        //获取支付宝支付成功的回调函数
        Map<String, String> param = this.getParam();
        //查询订单是否已经支付成功
        AlipayClient alipayClient = new DefaultAlipayClient(client.getUrl(), client.getAppid(),
                client.getPrivateKey() ,"JSON" , "utf-8" , client.getZfbPublicKey() ,"RSA2");

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        request.setBizContent("{" +
                "\"out_trade_no\":\""+param.get("out_trade_no")+"\"," +
                "\"trade_no\":\""+param.get("trade_no")+"\"" +
                "  }");
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            String json = response.getBody() ;
            logger.error("------------------------------------------------");
            logger.error(json);
            logger.error("------------------------------------------------");
            JSONObject jsonObject = JSONArray.parseObject(json).getJSONObject("alipay_trade_query_response");
            jsonObject.put("payStatus" , "已支付");
            jsonObject.put("orderStatus" , 2) ;
            //修改订单的状态为已支付状态
           orderService.updateOrderPayStatus(jsonObject);
        } else {
            logger.error("--------------------查询订单失败----------------------------");
        }



        return mav ;
    }
}
