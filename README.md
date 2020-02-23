# CRPC服务治理框架

[CRPC](http://www.github.com/leeyazhou)致力于提供高性能和透明化的RPC远程服务调用方案，使应用可通过高性能的RPC实现服务的输出和输入功能。[CRPC](http://www.github.cn/)有自己的部署环境，也可部署在servlet容器中。CRPC项目编码格式UTF-8，基础依赖环境： JDK1.6+、[Netty4.1.x](http://netty.io/)、[Kryo4](https://github.com/EsotericSoftware/kryo)、[ProtoBuff](https://github.com/google/protobuf/)。

* 基于[Netty](http://netty.io/)进行网络通讯，Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers &amp;clients.所以[CRPC](http://www.github.cn/)是站在巨人的肩膀上，天生骄傲，具有高性能、高可靠性。
* 配置可选多种序列化方案，支持[Kryo](https://github.com/EsotericSoftware/kryo)、[ProtoBuf](https://github.com/google/protobuf/)和JDK序列化,易于扩展。
* [Zookeeper](https://zookeeper.apache.org/)注册中心集群，简化服务配置
* 心跳监测和断线重连功能，及时清除挂掉的服务
* 两种部署方式：单独部署和web容器部署

## 项目结构
[CRPC](http://www.github.cn/)项目结构图，主要分为该框架项目代码和示例crpc-demo。项目需要发布时，在crpc-all项目下运行mvn clean install 即可把所有项目模块打包。如果需要打包crpc项目的部署环境，只需在crpc项目下运行mvn clean assembly:assembly，运行成功后，在target目录下生成crpc-0.0.1-SNAPSHOT-bin.tar.gz。crpc项目各个模块说明如下：

```
crpc/
├── crpc
├── crpc-client
├── crpc-common
├── crpc-console
├── crpc-demo
├── crpc-protocol
├── crpc-registry
├── crpc-server
├── pom.xml
├── README.md
└── src
```


* 项目crpc : 主要负责打包部署环境，**ALL IN ONE**, 包含crpc-client、crpc-protocol，cprc-common和cprc-server的代码
* 项目crpc-all ：父项目
* 项目crpc-client ：客户端业务处理
* 项目crpc-common：通用代码
* 项目crpc-protocol ：负责编码/解码
* 项目crpc-server ：服务端业务处理
* 项目crpc-demo ： 示例项目的父项目
* 项目crpc-demo-api ：示例项目的公共部分，如实体类
* 项目crpc-demo-consumer ：示例项目的消费者/客户端
* 项目crpc-demo-provider ：示例项目的生产者/服务端

---

## 开发环境配置

开发IDE可以使用eclipse或idea，下面以eclipse说明：

打开eclipse导入项目，打开File&gt;Import,然后选择maven下的Existing Maven Projects，点击下一步，选择crpc项目地址后，点击确定即可导入整个项目。

---

## 示例说明

示例项目包含接口项目(crpc-demo-api)、服务提供者(crpc-demo-provider)和服务消费者(crpc-demo-consumer)三部分。

项目结构：
```
crpc-demo/
├── crpc-demo-api
├── crpc-demo-consumer
├── crpc-demo-provider
├── pom.xml
└── src
```

项目依赖：

```
<dependency>
    <groupId>com.github.crpc</groupId>
    <artifactId>crpc</artifactId>
    <version>0.0.2-SNAPSHOT</version>
</dependency>
```


1. crpc-demo-api包含model类和接口类，其中model类需要添加@ CRPCSerializable注解，并实现Serializable（非必须，但jdk序列化时需要）。

2. crpc-demo-provider 依赖crpc-demo-api项目，包含接口实现类，crpc默认读取classpath下的crpc.xml配置文件，配置文件名字暂时不能修改，文件内容如下:

```
<?xml version="1.0" encoding="UTF-8"?>
<crpc xmlns="http://www.github.cn/crpc/crpc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.github.cn/crpc/crpc http://tw.github.cn/crpc/crpc.xsd">
    <server name="userservice" address="tcp://127.0.0.1:12200" worker="100">
        <scan basepackage="com.github.crpc.demo;org.junit" />
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

3． crpc-demo-consumer项目也依赖crpc-demo-api，通过cprc远程调用crpc-demo-provider提供的接口功能。该项目也需要在classpath下添加crpc.xml配置文件。内容如下：

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

运行cprc-demo-provider下的Provider类，出现如下结果即启动成功；

```
[11:12:29,069 INFO ] [main] c.c.c.config.crpc.Configuration[131] - crpc configration location : /D:/code/crpc/crpc-demo/crpc-demo-provider/target/test-classes/conf/crpc.xml
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
[11:12:29,120 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[100] - scan service at basepackage : c.c.c.demo
[11:12:29,165 INFO ] [main] c.c.c.server.ServerFactory[90] - found service : class c.c.c.demo.service.impl.HelloServiceImpl
[11:12:29,176 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[100] - scan service at basepackage : org.junit
[11:12:29,448 INFO ] [main] c.c.c.server.netty.AbstractNettyServer[116] - begin scan filters.
[11:12:30,723 INFO ] [main] c.c.c.server.netty.NettyServer[65] - Server is running at http://127.0.0.1:12200, businessThreads is 100
[11:12:30,724 INFO ] [main] c.c.c.container.Bootstrap[48] - server start in 1662 ms
```

5. 运行crpc-demo-consumer下的Consumer类，控制台查看调用结果。

```
[11:16:21,809 INFO ] [main] c.c.c.config.crpc.Configuration[131] - crpc configration location : /D:/code/svn/component/frame/crpc/crpc-demo/crpc-demo-consumer/target/classes/conf/crpc.xml
[11:16:22,300 INFO ] [bussiness-1-1] c.c.c.client.factory.AbstractClientFactory[175] - addClient success, serviceName:userservice, client : c.c.c.client.netty.NettyClient@63fe8666
[11:16:22,300 INFO ] [bussiness-1-1] c.c.c.client.netty.NettyClient[117] - Client connect server(127.0.0.1:12200) success ! 
发送成功:false
```