# 配置文件参考

```yaml
# 数据源配置
spring:
  main:
    # 不开启会导致shardingsphere的DataSource与druid的DataSource冲突无法注册bean
    allow-bean-definition-overriding: false
  shardingsphere:
    datasource:
      names: master,slave0,slave1
      master:
        # 数据源 主库
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/member?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
        username: root
        password: root
      slave0:
        # 数据源 从库0
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/member_slave?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
        username: root
        password: root
      slave1:
        # 数据源 从库1
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/member_slave1?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
        username: root
        password: root
    sharding:
      master-slave-rules:
        ds0:
          master-data-source-name: master
          slave-data-source-names: slave0,slave1
      tables:
        tab_user:
          actual-data-nodes: ds0.tab_user$->{0..2}
          key-generator:
            column: id
            type: SNOWFLAKE
          table-strategy:
            inline:
              sharding-column: id
              algorithm-expression: tab_user$->{id % 3}
#            standard:
#              sharding-column: id
#              precise-algorithm-class-name: com.shaylee.demo.algorithm.FastShardingAlgorithm
    props:
      sql.show: true
```

