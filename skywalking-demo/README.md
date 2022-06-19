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

## 部署演示




# 延伸阅读
## Logback Extensions
https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/reference/html/boot-features-logging.html#boot-features-logback-extensions

## Spring参数错误时输出Http内容
https://www.jianshu.com/p/63dafc907765
