package com.soft863.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by huokundian on 2017/8/9.
 */
@Component
@ConfigurationProperties(prefix="spring.zfb")
public class AlipayClientAutoSetter {

    private String url ;

    private String appid ;

    private String privateKey ;

    private String publicKey ;

    private String returnUrl ;

    private String zfbPublicKey ;

    private String loginUrl ;

    private String thridLoginUrl ;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getZfbPublicKey() {
        return zfbPublicKey;
    }

    public void setZfbPublicKey(String zfbPublicKey) {
        this.zfbPublicKey = zfbPublicKey;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getThridLoginUrl() {
        return thridLoginUrl;
    }

    public void setThridLoginUrl(String thridLoginUrl) {
        this.thridLoginUrl = thridLoginUrl;
    }
}
