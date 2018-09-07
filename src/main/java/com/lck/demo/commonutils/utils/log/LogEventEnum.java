package com.lck.demo.commonutils.utils.log;

/**
 * 日志事件枚举
 *
 * @author ckli01
 * @date 2018/8/31
 */
public enum LogEventEnum {


    ADD(1, "添加"),
    UPDATE(2, "修改"),
    DELETE(3, "删除"),


    ;


    private Integer type;

    private String desc;

    LogEventEnum(Integer type, String desc) {
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
