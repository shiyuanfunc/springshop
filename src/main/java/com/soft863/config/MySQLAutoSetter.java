package com.soft863.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by huo on 2017/6/6.
 */

@Component
@ConfigurationProperties(prefix="spring.db")
public class MySQLAutoSetter {

    private String driver ;

    private String url ;

    private String user ;

    private String psw ;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }


    @Bean
    @ConditionalOnMissingBean
    public DataSource getDruidDataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(psw);
        dataSource.setMaxActive(100);
        dataSource.setMaxIdle(5);
        dataSource.setMinIdle(2);
        return dataSource ;
    }
}
