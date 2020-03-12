# 配置文件参考

```yaml
spring:
  redis:
    database: 0
    host: 127.0.0.1
    port: 6379
    password:
    timeout: 6000ms
    lettuce:
      pool:
        # 最大空闲数
        max-idle: 300
        # 最小空闲数
        min-idle: 5
        # 连接池的最大数据库连接数。设为0表示无限制
        max-active: 1000
        # 最大建立连接等待时间。如果超过此时间将接到异常。设为-1表示无限制。
        max-wait: 20000ms
```

