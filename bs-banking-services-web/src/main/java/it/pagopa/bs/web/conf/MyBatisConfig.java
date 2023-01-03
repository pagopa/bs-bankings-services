package it.pagopa.bs.web.conf;

import javax.sql.DataSource;

import org.springframework.core.io.Resource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
@MapperScan("it.pagopa.bs.web.mapper")
public class MyBatisConfig {

    private final DataSource dataSource;

    @Value("classpath*:it/pagopa/**/mapper/**/*.xml")
    private Resource[] mapperResources;
    
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(mapperResources);
        return factoryBean.getObject();
    }
}
