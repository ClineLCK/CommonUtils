package com.lck.demo.commonutils.utils.log;

/**
 * 业务日志模块枚举
 *
 * @author ckli01
 * @date 2018/8/31
 */
public enum LogModuleEnum {

    DEFAULT(0,"系统"),
    CUST_INFO(1, "门店管理"),
    ROUTE(2, "片区管理"),

    ;

    private Integer type;

    private String desc;

    LogModuleEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
