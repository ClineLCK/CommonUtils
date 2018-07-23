package com.lck.demo.commonutils.thread;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池属性配置类
 *
 * @author ckli01
 * @date 2018/7/2
 */
@Configuration
@ConfigurationProperties(prefix = "executor")
@Getter
@Setter
public class TaskExecutorProperties {


    /**
     * 核心线程数
     */
    private Integer corePoolSize;

    /**
     * 任务队列最大值
     */
    private Integer queueCapacity;

    /**
     * 最大运行线程数
     */
    private Integer maxPoolSize;

    /**
     * 线程存活时间
     */
    private Integer keepAliveTime;



}
