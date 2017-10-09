package com.soft863.dao;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/22.
 */
public interface UserDao {
    List<Map<String,Object>> queryUsers(Map<String, String> params);

    List<Map<String,Object>> queryUsersByCurrentUserId(Integer userId);

    @Select("select t.*  from t_user t  where t.id = #{userId}")
    Map<String,Object> queryUserById(Integer userId);

    int queryUsersCount(Map<String, String> params);

    @Select("select * from t_user_address where userId = #{userId}")
    List<Map<String,Object>> queryUserAddressMessage(Integer userId);

    @Insert("insert into t_user_address (userId,realName,tel,detail,is_default,createTime) values(#{userId},#{realName},#{tel},#{addr},1,now())")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()",keyProperty = "addrId" , resultType = int.class ,before=false)
    int saveAddrs(Map<String, String> params);

    @Update("update t_user_address set is_default = 0 where userId=#{userId}")
    void updateDefaultAddress(Map<String, String> params);

    @Delete("delete from t_user_address where id = #{id}")
    int removeAddr(Integer id);

    List<Map<String,Object>> queryUsersReate(Map<String, String> params);

    int queryUsersReateCount(Map<String, String> params);

    @Select("select * from t_user_rebate where userId = #{userId} order by createTime desc")
    List<Map<String,Object>> queryUserRebate(Integer userId);
}
