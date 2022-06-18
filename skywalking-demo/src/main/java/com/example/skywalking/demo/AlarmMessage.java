package com.example.skywalking.demo;

import lombok.Data;

@Data
public class AlarmMessage {
    private Integer scopeId;
    private String name;
    private String id0;
    private String id1;
    private String alarmMessage;
    private Long startTime;

}
