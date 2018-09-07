package com.lck.demo.commonutils.utils.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.lck.demo.commonutils.bean.LogEntity;
import com.lck.demo.commonutils.utils.mq.config.RocketMqConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * rocketMq 消息处理器
 *
 * @author ckli01
 * @date 2018/9/3
 */
@Slf4j
@Service
public class RocketMqConsumerMsgListenerProcessor implements MessageListenerConcurrently {


    @Autowired
    private RocketMqConsumerConfig rocketMqConsumerConfig;
//    @Autowired
//    private LogEntityService logEntityService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        if (CollectionUtils.isEmpty(msgs)) {
            // 空消息直接返回消费成功
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = msgs.get(0);
        if (messageExt.getReconsumeTimes() > rocketMqConsumerConfig.getRetryTimes()) {
            // 重复一定次数直接返回消费成功
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        try {
            String str = new String(messageExt.getBody());
            LogEntity logEntity = JSONObject.parseObject(str, LogEntity.class);
            log.info("rocketMq consumer msgs: {}", str);
//            logEntityService.insert(logEntity);
        } catch (Exception e) {
            //RECONSUME_LATER 消费失败，需要稍后重新消费
            log.error("rocketMq consumer msgs failed: {}", e.getMessage(), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        //CONSUME_SUCCESS 消费成功
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
