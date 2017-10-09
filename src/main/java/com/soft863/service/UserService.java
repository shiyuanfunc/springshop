package com.soft863.service;

import com.soft863.dao.SysDao;
import com.soft863.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/22.
 */
@Service
@Transactional
public class UserService {
    @Autowired
    private UserDao userDao ;

    @Autowired
    private SysDao sysDao ;

    public List<Map<String, Object>> queryUsers(Map<String, String> params) {
        return userDao.queryUsers(params);
    }

    public List<Map<String, Object>> queryUsersByCurrentUserId(Integer userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("id" , 2) ;
        //查询会员等级
        Map<String, Object> map = sysDao.queryConfigById(param);
        int level = Integer.parseInt(String.valueOf(map.get("paramValue")));

        //查询用户下的用户信息
        List<Map<String,Object>> data =  queryUsers(userId);
        list.addAll(data);
        for (int i = 1; i < level ; i++) {
            List<Map<String,Object>> temp = data ;
            List<Map<String,Object>> temp2 = new ArrayList<>();
            //获取下级用户信息
            for(Map<String,Object> maps : temp) {
                userId = Integer.parseInt(String.valueOf(maps.get("id")));
                data = queryUsers(userId) ;
                temp2.addAll(data);
                list.addAll(data);
            }
            data = temp2 ;
        }


        return list ;
    }

    public Map<String, Object> queryUserById(Integer userId) {
        return userDao.queryUserById(userId);
    }

    public int queryUsersCount(Map<String, String> params) {
        return userDao.queryUsersCount(params);
    }

    /**
     * 查询指定用户下的用户信息
     * @param userId
     * @return
     */
    public List<Map<String,Object>> queryUsers(Integer userId){
        return userDao.queryUsersByCurrentUserId(userId);
        //其他情况的时候
    }

    public List<Map<String, Object>> queryUserAddressMessage(Integer userId) {
        return userDao.queryUserAddressMessage(userId);
    }

    public Map<String, Object> saveAddrs(Map<String, String> params) {
        Map<String, Object> ret = new HashMap<>();
        //更新已经存在的用户地址信息
        userDao.updateDefaultAddress(params);
        int len = userDao.saveAddrs(params);
        if(len ==1) {
            ret.put("status" , "success") ;
            ret.putAll(params);
        }
        return ret ;
    }

    public Map<String, Object> removeAddr(Integer id) {
        Map<String, Object> ret = new HashMap<>();
        int len = userDao.removeAddr(id);
        if(len ==1) {
            ret.put("status" , "success") ;

        }
        return ret ;
    }

    public List<Map<String,Object>> queryUsersReate(Map<String, String> params) {
        return userDao.queryUsersReate(params);
    }


    public int queryUsersReateCount(Map<String, String> params) {
        return userDao.queryUsersReateCount(params);
    }

    public List<Map<String, Object>> queryUserRebate(Integer userId) {
        return userDao.queryUserRebate(userId);
    }
}
