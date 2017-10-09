package com.soft863.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/5/21.
 */
public interface OrderDao {
    int saveOrder(Map<String, String> param);

    void saveOrderUser(Map<String, Object> addr);

    void saveOrderGoods(List<Map<String, Object>> goods);

    @Select("select t.* ,s.name as status from t_order t left join " +
            " t_order_status s on t.orderStatus=s.id " +
            "where t.userId = #{userId} and ostatus = 1 order by t.createTime desc")
    List<Map<String,Object>> queryOders(Map<String, String> param);

    @Update("update t_order set ostatus = -1 where id = #{id} and userId = #{userId}")
    void cancelOrder(Map<String, String> params);

    @Select("select g.* ,f.orderMoney from t_order_goods g left join t_order f on g.orderId=f.id where g.orderId = #{id}")
    List<Map<String,Object>> queryOderById(Map<String, String> params);


    List<Map<String,Object>> queryAdminOrders(Map<String, String> param);

    @Select("update t_order set orderStatus = #{status} ,payTime =#{payTime} where id = #{id}")
    void settingConfig(Map<String, String> param);

    int queryAdminOrdersCount(Map<String, String> param);

    @Select("select * from t_user_address where id = #{addrId}")
    Map<String,Object> queryAddrByID(Map<String, String> param);

    void queryRebateByUserId(Map<String,Object> param);


    @Insert("insert into t_user_rebate(userId, rebate ,account ,ip ,createTime) values(#{userId},#{money},#{account},#{ip},now())")
    void saveRebateRecord(Map<String, Object> params);


    @Select("select count(1) from t_user_rebate where userId = #{userId} and DATE_FORMAT(createTime,'%y-%m') = DATE_FORMAT(NOW(),'%y-%m')")
    int queryRebateRecord(Map<String, Object> params);

    void updateOrderPayStatus(JSONObject jsonObject);

    @Update("update t_order set orderStatus = -1 , payStatus = '退款中' where id = #{orderId} and orderStatus = 2 and ostatus = 1")
    int backMoney(Map<String, Object> params);

    @Select("SELECT * FROM t_order where id = #{id} and ostatus = 1 and orderStatus = -1 and trade_status = 'TRADE_SUCCESS'")
    Map<String,Object> queryOderByOrderId(Map<String, String> param);
}
