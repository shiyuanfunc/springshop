package com.soft863.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by huo on 2017/5/18.
 */
public interface GoodsDao {


    @Insert("insert into t_goods( name , detail,price, aprice ,goodsImg,createTime) values(" +
            "#{goodsName},#{goodsDesc},#{goodsPrice},#{goodsPrice2},#{photo},now())")
    int saveGoods(Map<String, Object> param);

    @Select("select * from t_goods")
    List<Map<String,Object>> queryGoods(Map<String, String> param);

    @Select("select * from t_goods where id = #{id}")
    Map<String,Object> queryGoodsById(Integer id);

    @Insert("insert into t_shopcard(goodsId,userId,goodsNum,createTime) values(#{goodsId},#{userId},#{goodsNum},now())")
    int saveShopCard(Map<String, String> param);

    @Select("select t.* , g.name ,g.price from t_shopcard t left join t_goods g on t.goodsId= g.id where t.userId = #{userId}")
    List<Map<String,Object>> queryShopcards(Map<String, String> param);

    @Delete("delete from t_shopcard where id = #{id} and userId=#{userId}")
    void removeShopcards(Map<String,String> param);

    @Update("update t_shopcard set goodsNum = #{goodsNum} ,updateTime =now() where id = #{carId}")
    int settingsGoodsNum(Map<String, String> param);

    List<Map<String,Object>> queryShopcard(String[] cardIds);

    int removeShopCard(String[] cardIds);

    @Update("update t_goods set name = #{goodsName} ,aprice = #{goodsPrice2} , price = #{goodsPrice} ,detail = #{goodsDesc} , updateTime = now() where id = #{id}")
    int updateGoods(Map<String, String> param);
}
