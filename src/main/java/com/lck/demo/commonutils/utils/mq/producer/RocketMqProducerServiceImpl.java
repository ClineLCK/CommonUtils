package com.lck.demo.commonutils.utils.mq.producer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.lck.demo.commonutils.bean.LogEntity;
import com.lck.demo.commonutils.utils.mq.config.RocketMqProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * mq 基础服务实现类
 *
 * @author ckli01
 * @date 2018/8/31
 */
@Service
@Slf4j
public class RocketMqProducerServiceImpl implements RocketMqProducerService, InitializingBean {


    @Autowired
    private RocketMqProducerConfig rocketMqProducerConfig;

    private DefaultMQProducer defaultMQProducer;

    private ConcurrentLinkedQueue<LogEntity> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();

    private void init() throws MQClientException {
        defaultMQProducer = new DefaultMQProducer(rocketMqProducerConfig.getGroupName());
        defaultMQProducer.setNamesrvAddr(rocketMqProducerConfig.getNamesrvAddr());
        defaultMQProducer.setRetryTimesWhenSendFailed(rocketMqProducerConfig.getRetryTimes());
        defaultMQProducer.start();
        log.info("init rocketMq producer success...");
        start();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    /**
     * 启动线程处理消息
     */
    public void start() {
        Executor executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (concurrentLinkedQueue.size() > 0) {
                            send(concurrentLinkedQueue.poll());
                        } else {
                            // 若没有消息可以处理，休眠500ms
                            Thread.sleep(500);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        });
        log.info("start rocketMq producer success...");
    }


    /**
     * 发送信息
     */
    public void send(LogEntity logEntity) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {

        //发送消息
        String str = JSONObject.toJSONString(logEntity);

        Message msg = new Message(rocketMqProducerConfig.getTopic(), str.getBytes());

        //调用producer的send()方法发送消息,重试2次，即最多发送三次
        SendResult sendResult = defaultMQProducer.send(msg);
        if (!SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.error("send logEntity error : {}", str);
        }

    }


    /**
     * 将发送数据添加到队列
     *
     * @param logEntity
     */
    @Override
    public void addLogEntity(LogEntity logEntity) {
        if (null != logEntity) {
            concurrentLinkedQueue.offer(logEntity);
        }
    }


}
