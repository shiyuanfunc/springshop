package com.soft863.listener;

import com.soft863.dao.SysDao;
import com.soft863.utils.HttpUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/4/22.
 */
public class ApplicationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //初始化数据字段
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext()) ;
        JdbcTemplate template = ctx.getBean(JdbcTemplate.class) ;
        String sql = "select * from t_bank where enable = 1";
        List<Map<String,Object>> list = template.queryForList(sql) ;
        HttpUtils.setBanks(servletContextEvent.getServletContext() , list);
        //查询网站名称，并初始话
        SysDao sysDao = ctx.getBean(SysDao.class);
        String sysName = sysDao.querySysName();
        servletContextEvent.getServletContext().setAttribute("SITE_SYS_NAME" , sysName);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {}
}
