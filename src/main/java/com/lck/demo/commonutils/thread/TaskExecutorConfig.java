package com.lck.demo.commonutils.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author ckli01
 * @date 2018/7/2
 */
@Configuration
public class TaskExecutorConfig {

    @Bean(name = "myExecutor")
    public Executor custExecutor(TaskExecutorProperties taskExecutorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(taskExecutorProperties.getCorePoolSize());

        executor.setMaxPoolSize(taskExecutorProperties.getMaxPoolSize());

        executor.setQueueCapacity(taskExecutorProperties.getQueueCapacity());

        executor.setKeepAliveSeconds(taskExecutorProperties.getKeepAliveTime());

        executor.setThreadNamePrefix("myExecutor-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        return executor;
    }

}
