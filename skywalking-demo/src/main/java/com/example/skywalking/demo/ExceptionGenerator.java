package com.example.skywalking.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionGenerator {
    static Logger logger = LoggerFactory.getLogger(ExceptionGenerator.class);
    public static void generate(){
        try {
            Long.parseLong("abcdrefg");
        }catch (Exception e){
            logger.error(" parse error.....",e);
            throw new BizException(e);
        }
    }
}
