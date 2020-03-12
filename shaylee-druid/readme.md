# 配置文件参考

```yaml
# 数据源配置
spring:
  druid:
    datasource:
      type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/shaylee?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
        username: root
        password: root
        # 初始连接数
        initial-size: 10
        # 最大连接池数量
        max-active: 100
        # 最小连接池数量
        min-idle: 10
        # 配置获取连接等待超时的时间
        max-wait: 60000
        # 打开PSCache，并且指定每个连接上PSCache的大小
        pool-prepared-statements: true
        max-pool-prepared-statement-per-connection-size: 20
        # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        timeBetweenEvictionRunsMillis: 60000
        # 配置一个连接在池中最小生存的时间，单位是毫秒
        min-evictable-idle-time-millis: 300000
        validation-query: SELECT 1 FROM DUAL
        test-while-idle: true
        test-on-borrow: false
        test-on-return: false
        webStatFilter:
          enabled: true
        stat-view-servlet:
          enabled: true
          allow:
          url-pattern: /monitor/druid/*
          login-username: admin
          login-password: admin
          reset-enable: false
        filter:
          stat:
            enabled: true
            log-slow-sql: true
            slow-sql-millis: 1000
            merge-sql: true
          wall:
            config:
              multi-statement-allow: true
```
