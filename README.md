# cloud-demo

## seata
[在nacos中配置好命名空间和相关配置之后在启动seata](https://seata.apache.org/zh-cn/docs/ops/deploy-by-docker-compose/#nacos-db)
```properties
service.vgroupMapping.default_tx_group=default
service.enableDegrade=false
service.disableGlobalTransaction=false

store.mode=db
store.lock.model=db
store.session.model=db
#-----db-----
store.db.datasource=druid
store.db.dbType=mysql
# 需要根据mysql的版本调整driverClassName
# mysql8及以上版本对应的driver：com.mysql.cj.jdbc.Driver
# mysql8以下版本的driver：com.mysql.jdbc.Driver
store.db.driverClassName=com.mysql.cj.jdbc.Driver
store.db.url=jdbc:mysql://mysql:3306/seata?useUnicode=true&characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useSSL=false
store.db.user= root
store.db.password=123456
# 数据库初始连接数
store.db.minConn=1
# 数据库最大连接数
store.db.maxConn=20
# 获取连接时最大等待时间 默认5000，单位毫秒
store.db.maxWait=5000
# 全局事务表名 默认global_table
store.db.globalTable=global_table
# 分支事务表名 默认branch_table
store.db.branchTable=branch_table
# 全局锁表名 默认lock_table
store.db.lockTable=lock_table
store.db.distributedLockTable=distributed_lock
# 查询全局事务一次的最大条数 默认100
store.db.queryLimit=100


# undo保留天数 默认7天,log_status=1（附录3）和未正常清理的undo
server.undo.logSaveDays=7
# undo清理线程间隔时间 默认86400000，单位毫秒
server.undo.logDeletePeriod=86400000
# 二阶段提交重试超时时长 单位ms,s,m,h,d,对应毫秒,秒,分,小时,天,默认毫秒。默认值-1表示无限重试
# 公式: timeout>=now-globalTransactionBeginTime,true表示超时则不再重试
# 注: 达到超时时间后将不会做任何重试,有数据不一致风险,除非业务自行可校准数据,否者慎用
server.maxCommitRetryTimeout=-1
# 二阶段回滚重试超时时长
server.maxRollbackRetryTimeout=-1
# 二阶段提交未完成状态全局事务重试提交线程间隔时间 默认1000，单位毫秒
server.recovery.committingRetryPeriod=1000
# 二阶段异步提交状态重试提交线程间隔时间 默认1000，单位毫秒
server.recovery.asynCommittingRetryPeriod=1000
# 二阶段回滚状态重试回滚线程间隔时间  默认1000，单位毫秒
server.recovery.rollbackingRetryPeriod=1000
# 超时状态检测重试线程间隔时间 默认1000，单位毫秒，检测出超时将全局事务置入回滚会话管理器
server.recovery.timeoutRetryPeriod=1000
```

## 文档
- [02-分布式基础-从单体到架构](doc/notes/02-分布式基础-从单体到架构.md)
- [03-分布式基础-从集群到分布式架构](doc/notes/03-分布式基础-从集群到分布式架构.md)
- [04-分布式基础-创建微服务项目](doc/notes/04-分布式基础-创建微服务项目.md)
- [05-Nacos-安装](doc/notes/05-Nacos-安装.md)
- [06-Nacos-服务注册.md](doc/notes/06-Nacos-服务注册.md)
- [07-Nacos-服务发现](doc/notes/07-Nacos-服务发现.md)
- [08-微服务API](doc/notes/08-微服务API.md)
- [12-经典面试题](doc/notes/12-经典面试题.md)
- [13-配置中心](doc/notes/13-配置中心.md)
- [16-Nacos经典面试题](doc/notes/16-Nacos经典面试题.md)
- [17-数据中心-配置隔离](doc/notes/17-数据中心-配置隔离.md)
- [20-OpenFeign-远程调用-声明式](doc/notes/20-OpenFeign-远程调用-声明式.md)
- [21-远程调用-第三方API](doc/notes/21-远程调用-第三方API.md)
- [24-OpenFeign-超时控制](doc/notes/24-OpenFeign-超时控制.md)
- [27-OpenFeign-拦截器](doc/notes/27-OpenFeign-拦截器.md)
- [30-Sentinel基础](doc/notes/30-Sentinel基础.md)
- [32-Sentinel-异常处理](doc/notes/32-Sentinel-异常处理.md)
- [38-Sentinel流控规则](doc/notes/38-Sentinel流控规则.md)
- [40-流控效果](doc/notes/40-流控效果.md)
- [43-熔断降级](doc/notes/43-熔断降级.md)
- [47-热点规则](doc/notes/47-热点规则.md)
- [51-网关](doc/notes/51-网关.md)
- [64-Seata](doc/notes/64-Seata.md)
- [68-seata基础架构](doc/notes/68-seata基础架构.md)
- [70-seata低层工作原理](doc/notes/70-seata低层工作原理.md)
- [71-seata4种事务模式](doc/notes/71-seata4种事务模式.md)