package com.soft863.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.soft863.config.AlipayClientAutoSetter;
import com.soft863.dao.OrderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/5/21.
 */
@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderDao orderDao ;
    @Autowired
    private AlipayClientAutoSetter client ;

    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    public List<Map<String, Object>> queryOrders(Map<String, String> param) {
        return orderDao.queryOders(param);
    }

    public void cancelOrder(Map<String, String> params) {
        orderDao.cancelOrder(params);
    }

    public List<Map<String, Object>> detailOrder(Map<String, String> params) {
        return orderDao.queryOderById(params);
    }

    public List<Map<String, Object>> queryAdminOrders(Map<String, String> param) {
        return orderDao.queryAdminOrders(param);
    }

    public void settingConfig(Map<String, String> param) {
        if("0".equals(param.get("status"))) {
            //根据ID查询订单信息
            Map<String,Object> data = orderDao.queryOderByOrderId(param) ;
            if(data!=null && !data.isEmpty()) {
                //完成支付宝退款流程
                AlipayClient alipayClient = new DefaultAlipayClient(client.getUrl(), client.getAppid(),
                        client.getPrivateKey(), "JSON", "utf-8", client.getZfbPublicKey(), "RSA2");
                AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

                String moeny = "0.00".equals(data.get("receipt_amount")) ? String.valueOf(data.get("orderMoney")) : String.valueOf(data.get("receipt_amount")) ;
                request.setBizContent("{" +
                        "\"out_trade_no\":\"" + data.get("orderNum") + "\"," +
                        "\"trade_no\":\"" + data.get("trade_no") + "\"," +
                        "\"refund_amount\":\"" + moeny + "\"," +
                        "\"refund_reason\":\"正常退款\"" +
                        "  }");
                try {
                    AlipayTradeRefundResponse response = alipayClient.execute(request);
                    if (response.isSuccess()) {
                        String json = response.getBody();
                        logger.error("------------------------------------------------");
                        logger.error(json);
                        logger.error("------------------------------------------------");
                        JSONObject jsonObject = JSONArray.parseObject(json).getJSONObject("alipay_trade_refund_response");
                        if ("10000".equals(jsonObject.get("code"))) {
                            logger.error("------------------------------------------------");
                            logger.error("退款业务流程成功");
                            logger.error("------------------------------------------------");
                            orderDao.settingConfig(param);
                        }
                    } else {
                        logger.error("------------------------------------------------");
                        logger.error("退款业务流程失败,错误码为：" + response.getCode());
                        logger.error("------------------------------------------------");
                    }
                } catch (AlipayApiException e) {
                    e.printStackTrace();
                }
            }
        }else {
            orderDao.settingConfig(param);
        }
    }

    public int queryAdminOrdersCount(Map<String, String> param) {
        return orderDao.queryAdminOrdersCount(param);
    }

    public Map<String, Object> queryRebate(Integer userId) {
        Map<String, Object> res = new HashMap<>();
        res.put("userId" , userId);
        res.put("status" , "success");
        orderDao.queryRebateByUserId(res);
        return res;
    }



    public Map<String,Object> saveRebate(Map<String, Object> params) {
        Map<String,Object> res = new HashMap<>();

        //查询对应用户当月是否已经进行过返现
        int count = orderDao.queryRebateRecord(params);
        if(count > 0) {
            res.put("status","error");
            res.put("message" , "该用户本月已经返现，不能重复返现操作");
            return res;
        }
        //获取返现金额
        orderDao.queryRebateByUserId(params);
        double money = Double.parseDouble(String.valueOf(params.get("money"))) ;
        if(money == 0) {
            res.put("status","error");
            res.put("message" , "没有可以返现的金额");
            return res;
        }
        // 开始进行返现操作
        orderDao.saveRebateRecord(params);
        res.put("status","success");
        return res ;
    }

    public void updateOrderPayStatus(JSONObject jsonObject) {
        orderDao.updateOrderPayStatus(jsonObject) ;
    }

    public Map<String, Object> backMoney(Map<String, Object> params) {
        Map<String,Object> res = new HashMap<>();
        int len = orderDao.backMoney(params);
        if(len ==1) {
            res.put("status" , "success") ;
        }
        return res ;
    }
}
