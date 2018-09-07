package com.lck.demo.commonutils.utils.mq;//package com.nfsq.terminal.customer.common.mq;
//
//import com.nfsq.terminal.customer.common.bean.Constant;
//import com.nfsq.terminal.customer.dao.cosmetic.domain.LogEntity;
//
///**
// * 日志发送mq封装类
// *
// * @author ckli01
// * @date 2018/8/31
// */
//public class LogEntityQueue {
//
//    private LogEntity logEntity;
//
//    private Integer retryTimes = 0;
//
//
//    /**
//     * 是否还可以重试
//     *
//     * @return
//     */
//    public boolean retryTimes() {
//        return retryTimes <= Constant.RETRY_TIMES;
//    }
//
//    public void retryTimesAdd() {
//        retryTimes++;
//    }
//
//    public LogEntity getLogEntity() {
//        return logEntity;
//    }
//
//    public void setLogEntity(LogEntity logEntity) {
//        this.logEntity = logEntity;
//    }
//
//    public Integer getRetryTimes() {
//        return retryTimes;
//    }
//
//    public void setRetryTimes(Integer retryTimes) {
//        this.retryTimes = retryTimes;
//    }
//}
