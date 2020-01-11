package com.tang.dbtable.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.api.config.rule.ShardingRuleConfiguration;
import io.shardingsphere.api.config.rule.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.core.keygen.DefaultKeyGenerator;
import io.shardingsphere.core.keygen.KeyGenerator;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Classname DataSourceConfig
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/1/10 23:38
 * @Created by ASUS
 */
@Configuration
public class DataSourceShardingConfig {

    @Bean(name = "hikariConfig0")
    @ConfigurationProperties(prefix = "spring.datasource.database0")
    public HikariConfig hikariConfig0() {
        return new HikariConfig();
    }

    @Bean(name = "hikariConfig1")
    @ConfigurationProperties(prefix = "spring.datasource.database1")
    public HikariConfig hikariConfig1() {
        return new HikariConfig();
    }

    @Bean("dataSource0")
    public DataSource dataSource0(
            @Qualifier(value = "hikariConfig0") HikariConfig hikariConfig0) {
        return new HikariDataSource(hikariConfig0);
    }

    @Bean("dataSource1")
    public DataSource dataSource1(
            @Qualifier(value = "hikariConfig1") HikariConfig hikariConfig1) {
        return new HikariDataSource(hikariConfig1);
    }

    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("dataSource0") DataSource dataSource0,
                                 @Qualifier("dataSource1") DataSource dataSource1) throws SQLException {
        return buildDataSource(dataSource0, dataSource1);
    }

    /**
     *@MethodName buildDataSource
     *@Description [ 构建数据源 ]
     *@Date 2020/1/11 9:53
     *@Param [dataSource0, dataSource1]
     *@return
     **/
    private DataSource buildDataSource(DataSource dataSource0,DataSource dataSource1) throws SQLException {

        // 分库设置
        Map<String, DataSource> datasourceMap = new HashMap<>();

        // 添加两个数据库
        datasourceMap.put("db_0", dataSource0);
        datasourceMap.put("db_1", dataSource1);

        // 设置分片规则配置
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();

        // 默认数据库名
        shardingRuleConfiguration.setDefaultDataSourceName("db_0");

        // 添加分表规则  多个表就添加多个
        shardingRuleConfiguration.getTableRuleConfigs().add(getTableRule01());

        // 构建数据源
        return ShardingDataSourceFactory.createDataSource(
                datasourceMap,
                shardingRuleConfiguration,
                new HashMap<>(16),
                new Properties()
        );
    }

    /**
     *@MethodName getTableRule01
     *@Description [ 配置goods分表配置  多个表就配置]
     *@Date 2020/1/11 9:22
     *@Param []
     *@return
     **/
    private TableRuleConfiguration getTableRule01() {

        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration();

        // 配置逻辑表名
        tableRuleConfiguration.setLogicTable("goods");

        // 实际数据结点
        tableRuleConfiguration.setActualDataNodes("db_${0..1}.goods_${0..1}");

        // 配置数据库 分库规则
        tableRuleConfiguration.setDatabaseShardingStrategyConfig(
                new StandardShardingStrategyConfiguration("goods_id", new DatabaseShardingRule()));

        // 配置表  分表规则
        tableRuleConfiguration.setTableShardingStrategyConfig(
                new StandardShardingStrategyConfiguration("goods_type",new TableShardingRule()));

        return tableRuleConfiguration;

    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new DefaultKeyGenerator();
    }

}