package com.lck.demo.commonutils.utils.log;


import com.lck.demo.commonutils.utils.WrappedBeanCopier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 日志服务 实体 功能类
 * 需要写日志服务且需要自定义日志实体内容的类，重写getPrefixBizLogStr()
 *
 * @author ckli01
 * @date 2018/9/3
 */
@Slf4j
public abstract class AbstractBizLogStr {

    /**
     * 获取主键Id
     *
     * @return
     */
    public abstract Long getId();

    /**
     * 获取自定义日志取值,前缀
     *
     * @return
     */
    public String getPrefixBizLogStr() {
        return getId() != null ? getId().toString() : "";
    }

    /**
     * 获取自定义日志取值,后缀
     *
     * @return
     */
    public String getSuffixBizLogStr() {
        return getId() != null ? getId().toString() : "";
    }


    /**
     * 转换类型
     *
     * @param clazz
     * @return
     */
    protected Object convertDoToVo(Class<?> clazz) {
        return WrappedBeanCopier.copyProperties(this, clazz);
    }

    /**
     * 对象比较，如果需要自定义比较方式，必须子类实现
     * 规则：
     * 1、若比较类含有 BizLogVsClass  注解，比较所有属性
     * 2、若没有，则只比较属性上有 BizLogVsField 注解
     *
     * @param o
     * @return
     */
    protected String compareFields(Object o) {
        StringBuilder stringBuilder = new StringBuilder();

        if (null != o) {
            // 校验是同一个类
            if (o instanceof AbstractBizLogStr && this.getClass().equals(o.getClass())) {
                Class<?> clazz = this.getClass();
                Annotation bizLogVsClass = clazz.getAnnotation(BizLogVsClass.class);
                Field[] fields = clazz.getDeclaredFields();

                boolean flag = false;
                if (null != bizLogVsClass) {
                    flag = true;
                }

                for (Field field : fields) {
                    try {
                        // 若类上没有注解 则以属性上注解为依据
                        if (!flag) {
                            BizLogVsField bizLogVsField = field.getAnnotation(BizLogVsField.class);
                            if (null == bizLogVsField) {
                                continue;
                            }
                        }
                        stringBuilder.append(compareSingleField(clazz, field, this, o));
                    } catch (Exception e) {
                        log.error("compareSingleField for class: {} filed: {} error: {}", clazz.getName(), field.getName(), e.getMessage(), e);
                    }
                }
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 比较相同类相同属性值
     * 规则：
     * 1、先取值，若值相同则不比较
     * 2、取注解   BizLogVsField 标示日志显示别名以及别名取值，若没有给定别名或者别名取值方法，则使用属性名称，以及属性对应getter方法
     * 3、校验是否是BizLogStr 子类，子类相比较可以调用子类compareFields方法比较两个对象
     *
     * @param clazz
     * @param field
     * @param oldValue
     * @param newValue
     * @return
     * @throws Exception
     */
    private StringBuilder compareSingleField(Class<?> clazz, Field field, Object oldValue, Object newValue) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Method method = null;
        String fieldName = field.getName();

        field.setAccessible(true);
        Object oldReturnValue = field.get(oldValue);
        Object newReturnValue = field.get(newValue);

        // 判断是否相同，若相同返回，不同取别名
        if (!compareField(oldReturnValue, newReturnValue)) {
            return stringBuilder;
        }

        BizLogVsField bizLogVsField = field.getAnnotation(BizLogVsField.class);
        if (null != bizLogVsField) {
            // 若配置别名，使用属性别名做日志记录
            if (!StringUtils.isEmpty(bizLogVsField.fieldNameStr())) {
                fieldName = bizLogVsField.fieldNameStr();
            }
            // 优先根据注解根据method名称 取值
            if (!StringUtils.isEmpty(bizLogVsField.strMethodName())) {
                method = clazz.getMethod(bizLogVsField.strMethodName());
                // 获取别名属性值
                if (null != method) {
                    log.debug("compareSingleField for class: {} method: {}", clazz.getName(), method.getName());
                    oldReturnValue = method.invoke(oldValue);
                    newReturnValue = method.invoke(newValue);
                }
            }
        }

        // 判断方法的返回类型 是否是BizLogStr 子类
        Class<?> fieldType = field.getType();
        if (AbstractBizLogStr.class.isAssignableFrom(fieldType)) {
            // 防止nullPointException
            if (null == oldReturnValue) {
                oldReturnValue = fieldType.newInstance();
            }
            if (null == newReturnValue) {
                newReturnValue = fieldType.newInstance();
            }
            // 调用compareFields 循环
            AbstractBizLogStr bizLogStr = (AbstractBizLogStr) oldReturnValue;
            stringBuilder.append(bizLogStr.compareFields(newReturnValue));
        } else {
            stringBuilder.append(formaterChangeStr(fieldName, oldReturnValue, newReturnValue));
        }
        log.debug("compareSingleField for class: {} filed: {} result: {}", clazz.getName(), field.getName(), stringBuilder.toString());
        return stringBuilder;

    }


    /**
     * 转换比较类型结果为字符串输出
     *
     * @param label
     * @param oldValue
     * @param newValue
     * @return
     */
    protected StringBuilder formaterChangeStr(String label, Object oldValue, Object newValue) {

        return new StringBuilder().append(label)
                .append(": ")
                .append(oldValue)
                .append(" -> ")
                .append(newValue)
                .append("\r\n");
    }

    /**
     * 基本类型比较，如String、Long、Integer....
     *
     * @param o1
     * @param o2
     * @return
     */
    protected boolean compareField(Object o1, Object o2) {
        if (null == o1 && null == o2 ||
                null != o1 && o1.equals(o2)) {
            return false;
        }
        return true;
    }


}
