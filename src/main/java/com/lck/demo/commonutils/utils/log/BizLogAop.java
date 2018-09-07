package com.lck.demo.commonutils.utils.log;

import com.alibaba.fastjson.JSONObject;
import com.lck.demo.commonutils.bean.LogEntity;
import com.lck.demo.commonutils.service.BaseService;
import com.lck.demo.commonutils.spring.SpringContextUtil;
import com.lck.demo.commonutils.utils.mq.producer.RocketMqProducerService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 业务日志Aop
 * // todo 业务日志 分离到单独文件
 *
 * @author ckli01
 * @date 2018/8/31
 */
@Aspect
@Configuration
@Slf4j
public class BizLogAop {

    @Autowired
    private RocketMqProducerService rocketMqProducerService;

    @Autowired
    private ThreadPoolTaskExecutor custExecutor;


    /**
     * 日志实体内容分割
     */
    public static final String LOGENTITY_SPLIT_MARK_SPACE = " ";


    /**
     * 当前注解类信息
     */
    private static InheritableThreadLocal<Class<?>> classThreadLocal = new InheritableThreadLocal<>();
//    /**
//     * 当前登录人信息
//     */
//    private static InheritableThreadLocal<UserInfo> userInfoThreadLocal = new InheritableThreadLocal<>();
//    /**
//     * 当前登录人Ip地址信息
//     */
//    private static InheritableThreadLocal<String> ipThreadLocal = new InheritableThreadLocal<>();

    /**
     * 切点
     */
    @Pointcut("@annotation(com.lck.demo.commonutils.utils.log.BizLog)")
    public void annotationAspect() {
    }


    @Around(value = "annotationAspect()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Class<?> clazz = pjp.getTarget().getClass();

        // 当前注解类信息
        classThreadLocal.set(clazz);
        // 当前登录人信息
//        userInfoThreadLocal.set(AuthUtil.getUserInfo());
        // 当前登录人Ip地址信息
//        ipThreadLocal.set(AuthUtil.getUserIp());

        //todo  maybe can use ForkJoinPool Next try ？
        Future<ThreadLogEntity> future = custExecutor.submit(() -> {
            ThreadLogEntity threadLogEntity = new ThreadLogEntity();
            try {
                threadLogEntity.setLogEntity(logEntity(pjp));
                threadLogEntity.setFlag(true);
            } catch (Exception e) {
                log.error("BizAop logEntity catchException : {}", e.getMessage(), e);
            }
            return threadLogEntity;
        });

        // 记录日志，不得影响正常程序运行，包括耗时
        Object o = pjp.proceed();

        try {
            ThreadLogEntity threadLogEntity = future.get();
            // 方法返回信息封装
            if (threadLogEntity.isFlag()) {
                userDefinedLogEntity(o, threadLogEntity.getLogEntity());
            }
        } catch (Exception e) {
            log.error("BizAop last logEntity packaging catchException : {}", e.getMessage(), e);
        } finally {
//            ipThreadLocal.remove();
//            userInfoThreadLocal.remove();
            classThreadLocal.remove();
        }
        return o;
    }


    /**
     * 在类上获取日志模块
     *
     * @param pjp
     * @return
     */
    private LogModuleEnum getClassLogModule(ProceedingJoinPoint pjp) {
//        Class<?> clazz = pjp.getTarget().getClass();
        Class<?> clazz = classThreadLocal.get();
        BizLog bizLog = clazz.getAnnotation(BizLog.class);
        if (null != bizLog) {
            return bizLog.logModule();
        }
        return null;
    }

    /**
     * 在方法上获取日志注解信息
     *
     * @param pjp
     * @return
     */
    private BizLog getMethodBizLog(ProceedingJoinPoint pjp) throws Exception {
//        Class<?> clazz = pjp.getTarget().getClass();
        Class<?> clazz = classThreadLocal.get();
        Class<?>[] par = ((MethodSignature) pjp.getSignature()).getParameterTypes();
        String methodName = pjp.getSignature().getName();
        Method method = clazz.getMethod(methodName, par);
        return method.getAnnotation(BizLog.class);
    }

    /**
     * 生成日志信息实体
     *
     * @param pjp
     * @return
     * @throws Exception
     */
    private LogEntity logEntity(ProceedingJoinPoint pjp) throws Exception {
        BizLog bizLog = getMethodBizLog(pjp);
        if (null == bizLog) {
            throw new Exception("BizAop cant find BizLog annotation");
        }
        LogEntity logEntity = new LogEntity();
        LogModuleEnum logModuleEnum = bizLog.logModule();
        LogEventEnum logEventEnum = bizLog.logEvent();

        if (LogModuleEnum.DEFAULT.equals(bizLog.logModule())) {
            LogModuleEnum logModule = getClassLogModule(pjp);
            logModuleEnum = logModule != null ? logModule : logModuleEnum;
        }

        logEntity.setModule(logModuleEnum.getType());
        logEntity.setEvent(bizLog.logEvent().getType());

//        UserInfo userInfo = userInfoThreadLocal.get();
//        Long personId = 0L;
        String personName = "SYSTEM";

//        if (null != userInfo) {
//            personId = userInfo.getPersonId() != null ? userInfo.getPersonId() : personId;
//            personName = StringUtils.isEmpty(userInfo.getName()) ? personName : userInfo.getName();
//        }

//        logEntity.setOperId(personId);
        // 前缀信息
        logEntity.setEntity(logModuleEnum.getDesc() + ": " + personName + LOGENTITY_SPLIT_MARK_SPACE + logEventEnum.getDesc());
        // 操作参数
        setArgsIntoLogEntity(logEventEnum, logEntity, pjp.getArgs());

        // 获取操作人Ip地址
//        logEntity.setOperIp(ipThreadLocal.get());

        return logEntity;
    }

    /**
     * 参数获取并写入日志信息
     * 目前只考虑第一个参数作为校验值
     *
     * @param logEventEnum
     * @param logEntity
     * @param args
     */
    private void setArgsIntoLogEntity(LogEventEnum logEventEnum, LogEntity logEntity, Object[] args) {
        if (null == args || args.length == 0) {
            return;
        }
        String str = "";
        switch (logEventEnum) {
            case DELETE:
            case UPDATE:
                str = getPrefixBizLogStr(args[0]);
                break;
            default:
                break;
        }
        formateLogEntityStr(logEntity, str);
    }


    /**
     * 根据不同返回类型，做不同日志记录
     *
     * @param o
     * @param logEntity
     */
    private void userDefinedLogEntity(Object o, LogEntity logEntity) {
        try {
            formateLogEntityStr(logEntity, getSuffixBizLogStr(o));
            logEntity.setDate(Calendar.getInstance().getTime());
            rocketMqProducerService.addLogEntity(logEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取实体自定义日志信息，后缀
     * // todo 考虑不同返回值
     *
     * @param o
     * @return
     */
    private String getSuffixBizLogStr(Object o) {
        String str;
        if (isSubClassOfBizlogStr(o)) {
            str = ((AbstractBizLogStr) o).getSuffixBizLogStr();
        } else {
            str = getUsualLogStr(o);
        }
        return str;
    }

    /**
     * 获取实体自定义日志信息，前缀
     * // todo 考虑不同传值
     *
     * @param o
     * @return
     */
    private String getPrefixBizLogStr(Object o) {
        String str;
        if (isSubClassOfBizlogStr(o)) {
            str = ((AbstractBizLogStr) o).getPrefixBizLogStr();
            Class<?> clazz = classThreadLocal.get();
            BaseService baseService = (BaseService) SpringContextUtil.getBean(clazz);
            try {
                // 获取修改之前do
                Object oldObj = baseService.getById(((AbstractBizLogStr) o).getId());
                AbstractBizLogStr bizLogStr = (AbstractBizLogStr) oldObj;
                // 将do 转化为 VO
                Object oldVo = bizLogStr.convertDoToVo(o.getClass());
                // 获取变更结果
                String compareFields = ((AbstractBizLogStr) oldVo).compareFields(o);

                if (!StringUtils.isEmpty(compareFields)) {
                    str += "\r\n" + compareFields;
                }
            } catch (Exception e) {
                log.error("getPrefixBizLogStr error for class: {} message: {}", classThreadLocal.get().getName(),
                        e.getMessage(), e);
            }
        } else {
            str = getUsualLogStr(o);
        }
        return str;
    }

    private String getUsualLogStr(Object o) {
        String str = "";
        if (null == o) {
            return str;
        } else if (o instanceof Collection) {
            str = JSONObject.toJSONString(o);
        } else if (o instanceof Map) {
            str = JSONObject.toJSONString(o);
        } else {
            str = JSONObject.toJSONString(o);
        }
        return str;
    }


    /**
     * 是否是BizLogStr 的子类
     *
     * @param o
     * @return
     */
    private boolean isSubClassOfBizlogStr(Object o) {
        return o instanceof AbstractBizLogStr;
    }


    /**
     * 对日志信息给定格式封装
     *
     * @param logEntity
     * @param str
     */
    private void formateLogEntityStr(LogEntity logEntity, String str) {
        if (!StringUtils.isEmpty(str)) {
            logEntity.setEntity(logEntity.getEntity() + "\r\n" + str);
        }
    }

    /**
     * 线程处理Aop 日志处理
     */
    private class ThreadLogEntity extends LogEntity {
        private boolean flag;
        private LogEntity logEntity;

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public LogEntity getLogEntity() {
            return logEntity;
        }

        public void setLogEntity(LogEntity logEntity) {
            this.logEntity = logEntity;
        }
    }

}
