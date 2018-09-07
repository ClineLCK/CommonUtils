package com.lck.demo.commonutils.utils.mq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * rocketMq 生产者配置类
 *
 * @author ckli01
 * @date 2018/9/3
 */
@Configuration
@ConfigurationProperties(prefix = "rocketMq.producer")
@Getter
@Setter
public class RocketMqProducerConfig {

    /**
     * 生产者组名
     */
    private String groupName;

    /**
     * mq 地址
     */
    private String namesrvAddr;

    /**
     * topic
     */
    private String topic;

    /**
     * 发送失败重试次数
     */
    private int retryTimes = 2;

}
