package com.soft863.service;

import com.soft863.dao.GoodsDao;
import com.soft863.dao.OrderDao;
import com.soft863.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huo on 2017/5/18.
 */
@Service
@Transactional
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao ;

    @Autowired
    private OrderDao orderDao ;

    public Map<String, Object> saveGoods(Map<String, Object> param) {
        Map<String, Object> res = new HashMap<>();
        //根据商品名查询对应商品是否已经存在
        res.put("status" , "error") ;
        int len =  goodsDao.saveGoods(param);
        if(len ==1) {
            res.put("status" , "success");
            res.put("message" ,"操作成功");
        }

        return res ;
    }

    public List<Map<String, Object>> queryGoods(Map<String, String> param) {
        return goodsDao.queryGoods(param);
    }

    public Map<String, Object> queryGoodsById(Integer id) {
        return goodsDao.queryGoodsById(id);
    }

    public Map<String, Object> addShop(Map<String, String> param) {
        Map<String, Object> map = new HashMap<>();
        int len =  goodsDao.saveShopCard(param);
        if(len ==1) {
            map.put("status" , "success");
        }else{
            map.put("status" , "error");
            map.put("message" , "系统错误，请联系管理员");
        }
        return map ;
    }

    public List<Map<String, Object>> queryShopcards(Map<String, String> param) {
        return goodsDao.queryShopcards(param);
    }

    public void removeShopcards(Map<String,String> param) {
        goodsDao.removeShopcards(param);
    }


    public Map<String,Object> settingsGoodsNum(Map<String, String> param) {
        Map<String, Object> map = new HashMap<>();
        int len =  goodsDao.settingsGoodsNum(param);
        if(len ==1) {
            map.put("status" , "success");
        }else{
            map.put("status" , "error");
            map.put("message" , "系统错误，请联系管理员");
        }
        return map ;
    }

    public Map<String, Object> jiesuan(Map<String, String> param, String[] cardIds) {
        Map<String, Object>  map = new HashMap<>();

        //进行订单的插入
        int len = orderDao.saveOrder(param) ;
        if(len ==1) {
            //根据用户地址表ID查询 用户选中的地址信息
            Map<String,Object> addr = orderDao.queryAddrByID(param);
            addr.put("orderId" ,param.get("orderId"));
            //进行订单用户表的维护
            orderDao.saveOrderUser(addr);
            //根据购物车ID查询对应的商品信息
            List<Map<String,Object>> goods = goodsDao.queryShopcard(cardIds) ;
            goods.forEach(maps -> {
                maps.put("orderId" , param.get("orderId")) ;
            });
            //进行订单商品表的插入
            orderDao.saveOrderGoods(goods);
            //删除购物车中的信息
            goodsDao.removeShopCard(cardIds);

            map.put("status" , "success") ;
        }
        return map;
    }

    public Map<String, Object> updateGoods(Map<String, String> param) {
        Map<String, Object> res = new HashMap<String,Object>();
        int len = goodsDao.updateGoods(param);
        if(len ==1) {
            res.put("status" , "success");
        }else{
            res.put("status" , "error") ;
        }
        return res ;
    }
}
