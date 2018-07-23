package com.lck.demo.commonutils.service;


import com.lck.demo.commonutils.bean.page.PageList;

import java.util.List;
import java.util.Map;

/**
 * 基础业务服务类
 *
 * @author ckli01
 * @date 2018/6/27
 */
public interface BaseService<T> {


    /**
     * 根据主键id获取数据
     *
     * @param id
     * @return
     */
    T getById(Long id) throws Exception;

    /**
     * 根据Map对应key-value 查询信息
     *
     * @param map
     * @return
     */
    PageList<T> selectByCondition(Map map);

    /**
     * 插入数据
     *
     * @param t
     */
    Integer insert(T t);

    /**
     * 批量插入
     */
    void insertBatch(List<T> list);

    /**
     * 更新数据
     *
     * @param t
     */
    void update(T t) throws Exception;

    /**
     * 有选择的更新 即更新 值不为null的部分
     *
     * @param t
     */
    int updateByPrimaryKeySelective(T t) throws Exception;


    /**
     * 根据主键 删除数据 物理清除
     *
     * @param id
     */
    void deleteByPrimaryKey(Long id);


    /**
     * 根据id 删除数据 逻辑清除
     * 即更新操作
     * 例：将数据 status 字段置为0
     *
     * @param id
     */
    void logicDeleteById(Long id);

    /**
     * 批量根据id 删除数据 逻辑清除
     * 即更新操作
     * 例：将数据 status 字段置为0
     *
     * @param ids
     */
    void logicDeleteBatchByIds(List<Long> ids);

    /**
     * 根据IDS 批量获取信息
     *
     * @param ids
     * @return
     */
    List<T> selectByPrimaryKeys(List<Long> ids) throws Exception;

}
