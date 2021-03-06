# 工程简介
## 功能列表
1. 日志打印traceId  
com.example.skywalking.demo.SkywalkingDemoApplication.LogController

2. 用@Tag标签自定义tag  
com.example.skywalking.demo.SkywalkingDemoApplication.TagInfoController

3. 内部方法调用生成本地span  
com.example.skywalking.demo.SkywalkingDemoApplication.MethodTraceController

4. 接口如何排除被追踪  
com.example.skywalking.demo.SkywalkingDemoApplication.ExcludeController

5. 告警演示  
com.example.skywalking.demo.SkywalkingDemoApplication.AlarmController  
参考：
https://blog.csdn.net/weixin_42528266/article/details/107841176
https://skywalking.apache.org/docs/main/v8.8.1/en/setup/backend/backend-alarm/
https://skywalking-handbook.netlify.app/alarm/
https://www.cnblogs.com/ssgeek/p/14586397.html  
附：在一次Skywalking线上分享会上记录的关于使用Skywalking定位问题的思路：  
纵览全局，Skywalking拓扑图  
监控告警，metric/tracing确定问题存在故障（根据metric做告警，根据tracing统计作比较）  
确定故障在哪，tracing调用关系，确定故障出现在哪个service或者endpoint  
profile手段（skywalking新能力）或者常见传统性能定位方法，定位单节点问题所在（比如CPU、内存、io、网络 ——> 动态追踪采样 ——> 火焰图）基本可以解决99.9%的问题  
SkyWalking - 实现微服务监控告警 https://www.jianshu.com/p/5cc42569af6f/ （邮件告警实现）

6. 将http调用请求和响应信息作为Tag信息上报SW  
com.example.skywalking.demo.filter.ApmHttpInvokeFilter  
参考：https://www.jianshu.com/p/1c843983d34c

7. 客户化非侵入式增强实现追踪 （未实现）  
com.example.skywalking.demo.SkywalkingDemoApplication.CustomizeEnhanceController  
agent目录下的agent.config文件添加配置：  
路径：D:\program\middleware\apache-skywalking-apm-es7-8.2.0\apache-skywalking-apm-bin-es7\agent\config\agent.config
plugin.customize.enhance_file=/f/learn/learn-skywalking/skywalking-demo/src/main/resources/customize_enhance.xml  
参考：  
https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/customize-enhance-trace/  
https://string.quest/read/6527310  
https://skyapm.github.io/document-cn-translation-of-skywalking/zh/6.2.0/setup/service-agent/java-agent/Customize-enhance-trace.html  

8. skywalking profile线程方法栈分析  
com.example.skywalking.demo.SkywalkingDemoApplication.ProfileController   
注意：  
    1. /profile/{seconds} 这种endpoint无法profile  
    2. 一个时刻只允许一个task ，否则在点击创建任务时候，会提示：current service already has monitor task execute at this time  
    3. 创建task时，填写Endpoint Name，需加上Http Method，如：GET:/slowProfile  

9. log 功能 (采用logback+gRpc上报方式)  
    在apm-toolkit-logback-1.x的版本8.4.0之后才有org.apache.skywalking.apm.toolkit.log.logback.v1.x.log.GRPCLogClientAppender类，才能通过集成logback以gRpc方式将日志信息上报到Skywalking系统上
    参考：  
        1. https://github.com/apache/skywalking-java/tree/v8.4.0/apm-application-toolkit/apm-toolkit-logback-1.x/src/main/java/org/apache/skywalking/apm/toolkit/log/logback/v1/x/log   
        2. https://skywalking.apache.org/docs/main/v8.9.1/en/setup/backend/log-analyzer/  
    推荐skywalking 使用8.4.0及以上，这里采用skywalking 8.9.1版本 ，及：
    ```
    <dependency>
        <groupId>org.apache.skywalking</groupId>
        <artifactId>apm-toolkit-trace</artifactId>
        <version>8.9.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.skywalking</groupId>
        <artifactId>apm-toolkit-logback-1.x</artifactId>
        <version>8.9.0</version>
    </dependency>
    ```
    logback 配置：
    ```
    <appender name="grpc-log" class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.log.GRPCLogClientAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.mdc.TraceIdMDCPatternLogbackLayout">
                <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{tid}] [%thread] %-5level %logger{36} -%msg%n</Pattern>
            </layout>
        </encoder>
    </appender>
    ```
    测试成功版本： skywalking 8.9.1 、apm-toolkit-trace 8.9.0、apm-toolkit-logback-1.x 8.9.0  
    总结：  
    用skywalking 统一管理log ，结合trace功能，实现一个平台来管理 log 和 trace  

10. skywalking的稳定性对业务系统的影响测试  
测试描述：将启动参数：SW_AGENT_COLLECTOR_BACKEND_SERVICES=192.168.33.10:91800 修改为其他端口，启动demo应用正常；测试http接口正常，只是没有skywalking的功能而已，比如：traceID没有，但接口功能正常，控制台输出日志也是有的；  

11. Dynamic Configurations at Agent Side  
begin from v8.4.0  



## 部署演示


# 延伸阅读
## Compatibility: Skywalking Agent with Skywalking OAP Server 
https://skywalking.apache.org/docs/main/latest/en/setup/service-agent/agent-compatibility/

## Apache SkyWalking 8.4: Logs, VM Monitoring, and Dynamic Configurations at Agent Side
https://www.tetrate.io/blog/skywalking-8-4/ 

## Logback Extensions
https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/reference/html/boot-features-logging.html#boot-features-logback-extensions

## Spring参数错误时输出Http内容
https://www.jianshu.com/p/63dafc907765

## 性能分析工具SkyWalking插件开发指南  
https://insights.thoughtworks.cn/skywalking-plugin-guide/

## Issues
1. Skywalking 8.9.1 (docker-compose.yml) create task for profiling : Exception while fetching data (/getProfileTaskList) : Can't split service id into 2 parts  
https://github.com/apache/skywalking/issues/9269

