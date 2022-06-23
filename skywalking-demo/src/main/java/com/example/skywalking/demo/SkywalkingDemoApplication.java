package com.example.skywalking.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * VM options:-javaagent:D:\program\middleware\apache-skywalking-apm-es7-8.2.0\apache-skywalking-apm-bin-es7\agent\skywalking-agent.jar
 *
 * Environment Variables:
 * SW_AGENT_NAME=skywalking-demo;SW_AGENT_COLLECTOR_BACKEND_SERVICES=192.168.33.10:11800
 *
 * 端点排除追踪配置：-Dskywalking.trace.ignore_path=/exclude/**
 *
 */
@Slf4j
@SpringBootApplication
public class SkywalkingDemoApplication {

    static Logger logger = LoggerFactory.getLogger(SkywalkingDemoApplication.class);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SkywalkingDemoApplication.class, args);
        //logger.info(" ----- test...............");
		/*while (true){
			try{
				System.out.println(1/0);
			}catch (Exception e){
				logger.error("error msg: ",e);
			}
			Thread.sleep(1000*3);
		}*/
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @RestController
    class LogController{
        @GetMapping(value = "/log")
        public String log(){
            ZonedDateTime zdt = ZonedDateTime.now();
            logger.info("----------log test-------" + zdt.format(dateTimeFormatter));
            return "ok";
        }

        @GetMapping(value = "/logerror")
        public String logerror(){
            ExceptionGenerator.generate();
            return "logerror : NumberFormatException ";
        }

        @GetMapping(value = "/traceId")
        public String getTraceId(){
            ActiveSpan.error(new RuntimeException(" ActiveSpan error...."));
            ActiveSpan.info("ActiveSpan info ....");
            ActiveSpan.debug("ActiveSpan debug....");
            return TraceContext.traceId();
        }
    }

    /**
     * Span中添加Tag信息
     */
    @RestController
    class TagInfoController{
        @Autowired
        RestTemplate restTemplate;

        @Value("${server.port}")
        private Integer port;

        @Tag(key = "name",value = "arg[0]")
        @GetMapping(value = "/tag/hello")
        public String hello(@RequestParam(name = "name") String name){
            ZonedDateTime zdt = ZonedDateTime.now();
            logger.info("111111----------hello:"+name+"---" + zdt.format(dateTimeFormatter));
            ResponseEntity<String> responseEntity = restTemplate.exchange("http://127.0.0.1:"+port+"/log", HttpMethod.GET, null,String.class);
            logger.info("invoke /log endpoint , response body:{}",responseEntity.getBody());
            return "hello : "+name;
        }

        @Tag(key = "price",value = "arg[0]")
        @GetMapping(value = "/tag/exception")
        public String exception(@RequestParam String price){
            return "price : "+ Long.parseLong(price);
        }


    }

    /**
     * 内部方法调用trace
     */
    @RestController
    class MethodTraceController{
        @GetMapping(value = "/log/method")
        public String logMethod(){
            ZonedDateTime zdt = ZonedDateTime.now();
            logger.info("----------logMethod-------" + zdt.format(dateTimeFormatter));
            getUser("kim");
            return "ok";
        }

        /**
         * 内部方法调用，必须加上@Trace 才能生成span
         * @param name
         * @return
         */
        @Trace
        @Tag(key = "name",value = "arg[0]")
        @Tag(key = "user.name",value = "returnedObj.name")
        @Tag(key = "user.age",value = "returnedObj.age")
        private User getUser(String name){
            return new User("kim",30);
        }

        @Data
        class User{
            private String name;
            private Integer age;

            public User(String name,Integer age){
                this.name = name;
                this.age = age;
            }
        }
    }

    /**
     * 告警示例
     */
    @RestController
    class AlarmController{
        @Autowired
        private ObjectMapper objectMapper;

        @GetMapping(value="/alarmResponseTimeout")
        public String alarmResponseTimeout(){
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "alarmResponseTimeout sleep 1500ms";
        }

        /**
         * skywalking oap的告警回调端点
         * @param alarmMessages
         * @return
         * @throws JsonProcessingException
         */
        @RequestMapping(value="/alarmWebhook")
        public String alarmWebhook(@RequestBody List<AlarmMessage> alarmMessages) throws JsonProcessingException {
            String msg = "alarmWebhook, alarmMessages: "+ objectMapper.writeValueAsString(alarmMessages);
            logger.info( msg);
            return msg;
        }
    }


    @RestController
    class CustomizeEnhanceController{
        @GetMapping(value="/customize")
        public String customize(){
            doSkyPlugins("kim","hangzhou",27);
            return getName(12);
        }

        public String getName(Integer id){
            return "kim";
        }

        public User doSkyPlugins(String name, String address, int id){
            User user = new User();
            user.setAge("18");
            user.setName(name);
            return user;
        }
        @Data
        class User{
            private String name;
            private String age;

        }
    }



    /**
     * 排除追踪的接口
     */
    @RestController
    class ExcludeController{
        /**
         * 端点排除追踪测试1
         * @return
         */
        @GetMapping(value="/exclude/path1")
        public String excludePath1(){
            return "/exclude/path1";
        }
        /**
         * 端点排除追踪测试2
         * @return
         */
        @GetMapping(value="/exclude/dir/path2")
        public String excludePath2(){
            return "/exclude/dir/path2";
        }

    }

    /**
     * SW的Profile功能测试
     */
    @RestController
    class ProfileController{

        @GetMapping(value="/profile/{seconds}")
        public String slowDealSeconds(@PathVariable(name = "seconds") Integer seconds){
            sleep(seconds);
            process();
            return "sleepSomeTime (s)："+seconds;
        }
        @GetMapping(value="/profile/slow")
        public String slow(){
            sleep(1);
            process();
            return "slow";
        }
        @Trace(operationName = "threadSleep")
        private void sleep(Integer seconds){
            try {
                Thread.sleep(seconds*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Trace(operationName = "service/processWithThreadPool")
        private void process() {
            final ExecutorService threadPool = Executors.newFixedThreadPool(2);
            final CountDownLatch countDownLatch = new CountDownLatch(2);
            threadPool.submit(new Task1(countDownLatch));
            threadPool.submit(new Task2(countDownLatch));
            try {
                countDownLatch.await(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        class Task1 implements Runnable {
            private final CountDownLatch countDownLatch;
            public Task1(CountDownLatch countDownLatch) {
                this.countDownLatch = countDownLatch;
            }
            @Override
            public void run() {
                countDownLatch.countDown();
            }
        }

        class Task2 implements Runnable {
            private final CountDownLatch countDownLatch;
            public Task2(CountDownLatch countDownLatch) {
                this.countDownLatch = countDownLatch;
            }
            @Override
            public void run() {
                //            countDownLatch.countDown();
            }
        }
    }
}
