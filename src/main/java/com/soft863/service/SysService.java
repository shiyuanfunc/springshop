package com.soft863.service;

import com.soft863.dao.SysDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/30.
 */
@Service
@Transactional
public class SysService {

    @Autowired
    private SysDao sysDao ;

    public Map<String, Object> sysConfigSetting(Map<String, Object> params) {
        Map<String, Object> res = new HashMap<String,Object>();
        //根据系统配置ID查询是否存在对应的信息
        Map<String,Object> map = sysDao.queryConfigById(params);
        if(map==null || map.isEmpty()) {
            sysDao.saveSysConfigSetting(params);
            res.put("status" , "success") ;
        }else{
            sysDao.updateSysConfigSetting(params);
            res.put("status" , "success") ;
        }

        return res;
    }

    public List<Map<String, Object>> querySysConfigs() {
        return sysDao.querySysConfigs();
    }

    public Map<String, Object> sysLevelConfigSetting(Map<String, Object> params) {
        Map<String, Object> res = new HashMap<String,Object>();
        //根据系统配置ID查询是否存在对应的信息
        Map<String,Object> map = sysDao.queryLevelConfigByLevel(params);
        if(map==null || map.isEmpty()) {
            sysDao.saveSysLevelConfigSetting(params);
            res.put("status" , "success") ;
        }else{
            sysDao.updateSysLevelConfigSetting(params);
            res.put("status" , "success") ;
        }
        return res;
    }

    public List<Map<String, Object>> querySysLevelConfigs() {

        return sysDao.querySysLevelConfigs();
    }

    public String querySysName() {
        return sysDao.querySysName();
    }
}
