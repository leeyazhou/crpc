# CRPC服务治理框架

[CRPC](http://www.github.com/leeyazhou)致力于提供高性能和透明化的RPC远程服务调用方案，使应用可通过高性能的RPC实现服务的输出和输入功能。[CRPC](http://www.github.cn/)有自己的部署环境，也可部署在servlet容器中。CRPC项目编码格式UTF-8，基础依赖环境： JDK1.6+、[Netty4.1.x](http://netty.io/)、[Kryo4](https://github.com/EsotericSoftware/kryo)、[ProtoBuff](https://github.com/google/protobuf/)。

* 基于[Netty](http://netty.io/)进行网络通讯，Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers &amp;clients.所以[CRPC](http://www.github.cn/)是站在巨人的肩膀上，天生骄傲，具有高性能、高可靠性。
* 配置可选多种序列化方案，支持[Kryo](https://github.com/EsotericSoftware/kryo)、[ProtoBuf](https://github.com/google/protobuf/)和JDK序列化,易于扩展。
* [Zookeeper](https://zookeeper.apache.org/)注册中心集群，简化服务配置
* 心跳监测和断线重连功能，及时清除挂掉的服务
* 两种部署方式：单独部署和web容器部署

## 环境配置

开发IDE可以使用eclipse或idea，下面以eclipse说明：

打开eclipse导入项目，打开File&gt;Import,然后选择maven下的Existing Maven Projects，点击下一步，选择crpc项目地址后，点击确定即可导入整个项目。

## 示例说明

示例项目包含接口项目(crpc-showcase-api)、服务提供者(crpc-showcase-provider)和服务消费者(crpc-showcase-consumer)三部分。

项目结构：
```
crpc-showcase/
├── crpc-showcase-api
├── crpc-showcase-consumer
├── crpc-showcase-provider
├── pom.xml
└── src
```

项目依赖：

```
<dependency>
    <groupId>com.github.leeyazhou</groupId>
    <artifactId>crpc</artifactId>
    <version>1.0.2.RELEASE</version>
</dependency>
```


1. crpc-showcase-api包含model类和接口类，其中model类需要添加@ CRPCSerializable注解，并实现Serializable（非必须，但jdk序列化时需要）。

2. crpc-showcase-provider 依赖crpc-showcase-api项目，包含接口实现类，crpc默认读取classpath下的crpc.xml配置文件，配置文件名字暂时不能修改，文件内容如下:

```
<?xml version="1.0" encoding="UTF-8"?>
<crpc xmlns="http://www.github.cn/crpc/crpc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.github.cn/crpc/crpc http://tw.github.cn/crpc/crpc.xsd">
    <server name="userservice" address="tcp://127.0.0.1:12200" worker="100">
        <scan basepackage="com.github.crpc.showcase;org.junit" />
        <filters>
            <filter class="com.github.crpc.server.filter.IpFilter" />
            <filter class="com.github.crpc.server.filter.MonitorFilter" />
        </filters>
    </server>
</crpc>
```


* Server标签配置当前server的属性，项目名称、端口和业务线程数；
* Registry标签配置注册中心协议及注册中心地址
* Scan标签配置项目需要扫描的包名，多个包名用&quot;；&quot;隔开；
* Filters标签配置server端的过滤器。

3． crpc-showcase-consumer项目也依赖crpc-showcase-api，通过cprc远程调用crpc-showcase-provider提供的接口功能。该项目也需要在classpath下添加crpc.xml配置文件。内容如下：

```
<?xml version="1.0" encoding="UTF-8"?>
<crpc xmlns="http://www.github.cn/crpc/crpc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.github.cn/crpc/crpc crpc.xsd">
    <service name="userservice" codec="KRYO_CODEC" timeout="3000" loadbalance="ROUND_ROBIN">
        <options>
            <option name="TCP_NODELAY" value="true" />
            <option name="SO_REUSEADDR" value="true" />
        </options>
        <server address="tcp://127.0.0.1:12200" />
    </service>
</crpc>
```


crpc标签可以包含多个service标签，用于调用不同的服务提供者。Service标签配置名称，编码/解码方案，超时时间，里面包含server标签，用于配置服务提供者的地址。

4． 运行项目，查看调用效果。

运行cprc-showcase-provider下的Provider类，出现如下结果即启动成功；

```
[11:12:29,069 INFO ] [main] c.c.c.config.crpc.Configuration[131] - crpc configration location : /D:/code/crpc/crpc-showcase/crpc-showcase-provider/target/test-classes/conf/crpc.xml
[11:12:29,112 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[51] - NettyServer init
[11:12:29,113 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[77] - os.name : Windows 8.1
[11:12:29,114 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[78] - os.version : 6.3
[11:12:29,114 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[79] - os.arch : amd64
[11:12:29,115 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[80] - java.home : D:\code\Java\jdk1.7.0_80\jre
[11:12:29,116 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[81] - java.io.tmpdir : C:\Users\lee_y\AppData\Local\Temp\
[11:12:29,116 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[82] - vm.version : 1.7.0_80-b15
[11:12:29,116 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[83] - vm.vendor : Oracle Corporation
[11:12:29,116 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[84] - crpc.home : null
[11:12:29,116 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[85] - crpc.version : 0.0.1-SNAPSHOT
[11:12:29,116 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[86] - crpc.encoding : UTF-8
[11:12:29,117 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[91] - location : null
[11:12:29,120 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[100] - scan service at basepackage : c.c.c.showcase
[11:12:29,165 INFO ] [main] c.c.c.server.ServerFactory[90] - found service : class c.c.c.showcase.service.impl.HelloServiceImpl
[11:12:29,176 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[100] - scan service at basepackage : org.junit
[11:12:29,448 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[116] - begin scan filters.
[11:12:30,723 INFO ] [main] c.c.c.server.netty.NettyServer[65] - Server is running at http://127.0.0.1:12200, businessThreads is 100
[11:12:30,724 INFO ] [main] c.c.c.container.Bootstrap[48] - server start in 1662 ms
```

5. 运行crpc-showcase-consumer下的Consumer类，控制台查看调用结果。

```
[11:16:21,809 INFO ] [main] c.c.c.config.crpc.Configuration[131] - crpc configration location : /D:/code/svn/component/frame/crpc/crpc-showcase/crpc-showcase-consumer/target/classes/conf/crpc.xml
[11:16:22,300 INFO ] [bussiness-1-1] c.c.c.client.factory.AbstractClientFactory[175] - addClient success, serviceName:userservice, client : c.c.c.client.netty.NettyClient@63fe8666
[11:16:22,300 INFO ] [bussiness-1-1] c.c.c.client.netty.NettyClient[117] - Client connect server(127.0.0.1:12200) success ! 
发送成功:false
```
## 参与开发

欢迎你参与到CRPC的开发中，[如何参与](CONTRIBUTING.md)?

## 版本号

[版本格式](https://semver.org/lang/zh-CN/)：主版本号.次版本号.修订号，版本号递增规则如下：

* 主版本号：做了不兼容的 API 修改
* 次版本号：做了向下兼容的功能性新增
* 修订号：做了向下兼容的问题修正

## License

Flower is released under the [Apache License 2.0](https://github.com/leeyazhou/crpc/blob/master/LICENSE.txt)