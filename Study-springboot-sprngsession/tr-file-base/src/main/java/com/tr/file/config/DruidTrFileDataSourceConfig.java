package com.tr.file.config;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


/**
 * Created by chenhualong on 2017/7/21.
 */
@Configuration
// 扫描 Mapper 接口并容器管理
@MapperScan(basePackages = DruidTrFileDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "fileSqlSessionFactory")
public class DruidTrFileDataSourceConfig {


    //master dao 所在的包
    public static final String PACKAGE = "com.tr.file.dao.mapper.file";

    //mapper所在目录
    private static final String MAPPER_LOCATION = "classpath:mapper/file/*.xml";


    @Value("${spring.datasource.druid.one.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.druid.one.url}")
    private String url;

    @Value("${spring.datasource.druid.one.username}")
    private String username;

    @Value("${spring.datasource.druid.one.password}")
    private String password;

    //初始化数据库连接
    @Bean(name = "fileDataSource")
    @Primary
    public DataSource masterDataSource() {

        return CommnDataSourceUtil.commonDataSource(driverClassName, url, username, password);
    }

    //数据源事务管理器
    @Bean(name = "fileDataSourceTransactionManager")
    public DataSourceTransactionManager masterDataSourceTransactionManager() {
        return new DataSourceTransactionManager(masterDataSource());
    }

    //创建Session
    @Bean(name = "fileSqlSessionFactory")
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("fileDataSource") DataSource masterDataSource) throws Exception {
        final SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(masterDataSource);
        //MapperLocations(Resource[] mapperLocations)
         sqlSessionFactoryBean.setPlugins(new Interceptor[]{CommnDataSourceUtil.setpageSource()});
        Resource[] mapperLocations = new PathMatchingResourcePatternResolver().getResources(DruidTrFileDataSourceConfig.MAPPER_LOCATION);
        sqlSessionFactoryBean.setMapperLocations(mapperLocations);
        return sqlSessionFactoryBean.getObject();
    }

}
