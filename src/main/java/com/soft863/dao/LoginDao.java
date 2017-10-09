package com.soft863.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * Created by admin on 2017/4/22.
 */
public interface LoginDao {

    @Select("select * from t_user where tel = #{tel}")
    Map<String,Object> queryUserByTel(String tel);

    @Insert("insert into t_user_login_history(userId,ip,createTime) values(#{userId} , #{ip} , now())")
    int reChangeHistory(Map<String, String> params);

    void saveUser(Map<String, String> params);
    @Insert("insert into t_user_address(userId ,tel ,detail,realName,is_default,createTime) values(#{userId} , #{tel},concat(#{city} , #{detail}),#{realName} ,1, now())")
    void saveAddress(Map<String, String> params);
    @Select("select ((select paramValue from t_sys_config where id =3)-count(1)) num from t_user where pid = #{pid}")
    int queryNumByPid(String pid);

    @Select("select * from t_user where zfb_open_id = #{zfb_open_id}")
    Map<String,Object> queryUserByZfbOpenId(String zfb_open_id);

    @Update("update t_user set zfb_open_id = #{user_id} ,avatar =#{avatar},province=#{province},city=#{city},nick_name=#{nick_name} where tel = #{tel}")
    void updateUserByTel(JSONObject jsonObject);

    //void registBindZfb(JSONObject jsonObject);

    @Select("select count(1) from t_user where tel = #{tel} and zfb_open_id is null")
    int checkUserBindZfbByTel(JSONObject jsonObject);
}
