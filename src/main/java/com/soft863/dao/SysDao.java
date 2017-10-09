package com.soft863.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/30.
 */
public interface SysDao {

    @Select("select * from t_sys_config where id = #{id}")
    Map<String,Object> queryConfigById(Map<String, Object> params);

    @Insert("insert into t_sys_config (id , paramName , paramDesc , paramValue , createTime) values(#{id},#{paramName} , #{paramDesc} , #{paramValue} , now())")
    int saveSysConfigSetting(Map<String, Object> params);

    @Update("update t_sys_config set paramValue = #{paramValue} ,updateTime = now() where id = #{id}")
    int updateSysConfigSetting(Map<String, Object> params);

    @Select("select * from t_sys_config")
    List<Map<String,Object>> querySysConfigs();

    @Select("select * from t_sys_level_config where ls = #{ls}")
    Map<String,Object> queryLevelConfigByLevel(Map<String, Object> params);

    @Insert("insert into t_sys_level_config(ls , rate , createTime) values(#{ls},#{rate},now())")
    void saveSysLevelConfigSetting(Map<String, Object> params);

    @Update("update t_sys_level_config set rate = #{rate} , updateTime = now() where ls = #{ls}")
    void updateSysLevelConfigSetting(Map<String, Object> params);

    @Select("select * from t_sys_level_config")
    List<Map<String,Object>> querySysLevelConfigs();

    @Select("select paramValue from t_sys_config where id = 1")
    String querySysName();
}
