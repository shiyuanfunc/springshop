package com.soft863.service;

import com.alibaba.fastjson.JSONObject;
import com.soft863.dao.LoginDao;
import com.soft863.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/4/22.
 */
@Service
@Transactional
public class LoginService {
    @Autowired
    private LoginDao loginDao ;

    public Map<String,Object> loginUser(Map<String, String> params) {
        return loginDao.queryUserByTel(params.get("tel")) ;
    }

    public int reChangeHistory(Map<String, String> params) {
        return loginDao.reChangeHistory(params);
    }

    public boolean saveUser(Map<String, String> params) {
        boolean f = true ;
        //获取PID
        String pid = params.get("pid") ;
        if(pid!=null &&!"".equals(pid)) {
            params.put("pid" , HttpUtils.getDecoderUserId(pid)+"");
            //查询系统下线配置，并查询当前用户下还能添加多少个下线
            int num = loginDao.queryNumByPid(params.get("pid"));
            if(num <=0) {
                f = false ;
            }
        }
        if(f) {
            loginDao.saveUser(params);
            //获取插入的主键ID并存储到用户地址表
            loginDao.saveAddress(params);
            return true ;
        }else{
            return false ;
        }
    }

    public Map<String, Object> checkUserBindZfb(String zfb_open_id) {
        return loginDao.queryUserByZfbOpenId(zfb_open_id);
    }

    public Map<String,Object> updateUserByTel(JSONObject jsonObject) {
        Map<String,Object> res = new HashMap<>();
        //根据手机号查询是否已经绑定过支付宝
        int count = loginDao.checkUserBindZfbByTel(jsonObject) ;
        if(count ==1) //未绑定
            loginDao.updateUserByTel(jsonObject);
        else{
            res.put("status" , "error");
            res.put("msg" , "该账号不存在或已经绑定支付宝");
        }
        return res ;
    }

    public void registBindZfb(JSONObject jsonObject) {
        //loginDao.registBindZfb(jsonObject);
    }
}
