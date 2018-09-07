package com.lck.demo.commonutils.bean;

import java.util.Date;

/**
 * 日志类
 *
 * @author ckli01
 * @date 2018/8/31
 */
public class LogEntity {

    private Long id;

    /**
     * 日志模块
     */
    private Integer module;

    /**
     * 日志事件
     */
    private Integer event;

    /**
     * 日志实体
     */
    private String entity;

    /**
     * 操作人
     */
    private Long operId;

    /**
     * 操作日期
     */
    private Date date;

    /**
     * 操作人Ip地址
     */
    private String operIp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getModule() {
        return module;
    }

    public void setModule(Integer module) {
        this.module = module;
    }

    public Integer getEvent() {
        return event;
    }

    public void setEvent(Integer event) {
        this.event = event;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOperIp() {
        return operIp;
    }

    public void setOperIp(String operIp) {
        this.operIp = operIp;
    }
}
