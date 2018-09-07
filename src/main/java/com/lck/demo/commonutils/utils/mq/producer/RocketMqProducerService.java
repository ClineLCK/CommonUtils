package com.lck.demo.commonutils.utils.mq.producer;


import com.lck.demo.commonutils.bean.LogEntity;

/**
 * rocketMq 生产者基础服务
 *
 * @author ckli01
 * @date 2018/8/31
 */
public interface RocketMqProducerService {


    /**
     * 将发送数据添加到队列
     *
     * @param logEntity
     */
    void addLogEntity(LogEntity logEntity);


}
