package com.soft863.config;

import com.soft863.interceptor.SessionInterceptor;
import com.soft863.listener.ApplicationListener;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huo on 2017/4/19.
 */
@Configuration
@EnableTransactionManagement
public class RootConfig extends WebMvcConfigurerAdapter {


    @Bean
    public DataSourceTransactionManager getTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager dm = new DataSourceTransactionManager();
        dm.setDataSource(dataSource);
        return dm ;
    }

    /**
     * 配置mybatis相关信息
     */
    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(DataSource dataSource) throws Exception{
        SqlSessionFactoryBean sf = new SqlSessionFactoryBean();
        sf.setDataSource(dataSource);
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:com/soft863/**/mapper/*.xml");
        sf.setMapperLocations(resources);
        SqlSessionFactory s = sf.getObject() ;
        s.getConfiguration().setCallSettersOnNulls(true); //设置查询字段空值显示
        return s;
    }


    /**配置扫描接口**/
    @Bean
    public MapperScannerConfigurer getConfigure(){
        MapperScannerConfigurer config = new MapperScannerConfigurer();
        config.setSqlSessionFactoryBeanName("sqlSessionFactory");
        config.setBasePackage("com.soft863.dao");
        return config ;
    }

    /**
     * 配置消息转换器
     */
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        supportedMediaTypes.add(MediaType.valueOf("text/html;charset=utf-8"));
        //中文乱码
        StringHttpMessageConverter smc = new StringHttpMessageConverter();
        smc.setSupportedMediaTypes(supportedMediaTypes) ;
        converters.add(smc) ;

        MappingJackson2HttpMessageConverter mjmc = new MappingJackson2HttpMessageConverter();
        mjmc.setSupportedMediaTypes(supportedMediaTypes);
        converters.add(mjmc) ;

        super.configureMessageConverters(converters);
    }


    /**
     * 配置文件上传视图解析器
     * @return
     */
    @Bean("multipartResolver")
    public CommonsMultipartResolver resolver(){
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        return resolver ;
    }

    @Bean
    public ServletListenerRegistrationBean<ApplicationListener> servletListenerRegistrationBean(){
        return new ServletListenerRegistrationBean<ApplicationListener>(new ApplicationListener());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SessionInterceptor sessionInterceptor = new SessionInterceptor();
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/**/*").
                excludePathPatterns("/c/login", "/c/register", "/user/reg*", "/login/*" ) ;
        super.addInterceptors(registry);
    }
}
