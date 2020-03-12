# 配置文件参考

```
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: app
    password: app
    virtual-host: shaylee
    cache:
      connection:
        mode: connection
        size: 100
      channel:
        size: 100
        checkout-timeout: 200
```