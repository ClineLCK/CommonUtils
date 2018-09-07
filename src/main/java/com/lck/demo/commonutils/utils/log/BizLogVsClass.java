package com.lck.demo.commonutils.utils.log;


import java.lang.annotation.*;

/**
 * 日志服务 全属性校验
 *
 * @author ckli01
 * @date 2018/9/5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizLogVsClass {
}
