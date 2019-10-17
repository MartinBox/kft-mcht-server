package com.mcoder.kft.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Configuration
@MapperScan(basePackages = "com.mcoder.kft.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
public class DatasourceConfiguration {
    @Bean
    public DataSource dataSource(DruidConfiguration druidConfiguration) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(druidConfiguration.getUrl());
        dataSource.setUsername(druidConfiguration.getUsername());
        dataSource.setPassword(druidConfiguration.getPassword());
        dataSource.setName(druidConfiguration.getName());
        dataSource.setInitialSize(druidConfiguration.getInitialSize());
        dataSource.setMaxActive(druidConfiguration.getMaxActive());
        dataSource.setMinIdle(druidConfiguration.getMinIdle());
        dataSource.setTestOnReturn(druidConfiguration.isTestOnReturn());
        dataSource.setValidationQuery(druidConfiguration.getValidationQuery());
        dataSource.setTimeBetweenEvictionRunsMillis(druidConfiguration.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(druidConfiguration.getMinEvictableIdleTimeMillis());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(druidConfiguration.getMaxPoolPreparedStatementPerConnectionSize());
        dataSource.setRemoveAbandoned(druidConfiguration.isRemoveAbandoned());
        dataSource.setRemoveAbandonedTimeout(druidConfiguration.getRemoveAbandonedTimeoutMillis());
        dataSource.setLogAbandoned(druidConfiguration.isLogAbandoned());
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, Environment environment) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        /*bean.setTypeAliasesPackage(environment.getProperty("mapper.basepackage"));*/
        bean.setConfigLocation(new ClassPathResource(environment.getProperty("mybatis.configLocation")));
        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Interceptor[] plugins = new Interceptor[]{pageHelper};
        bean.setPlugins(plugins);
        bean.setMapperLocations(resolver.getResources(environment.getProperty("mybatis.mapperLocations")));
        return bean.getObject();
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory);
        return template;
    }
}
