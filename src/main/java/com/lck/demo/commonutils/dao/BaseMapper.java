package com.lck.demo.commonutils.dao;

import java.util.List;
import java.util.Map;

/**
 * mapper 基础接口
 *
 * @author ckli01
 * @date 2018/6/27
 */
public interface BaseMapper<T> {


    /**
     * 根据主键Id 删除
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入数据
     *
     * @param record
     * @return
     */
    int insert(T record);

    /**
     * 有选择的插入数据，
     * 根据字段是否有值 ， 插入数据  若为 null 即不插入数据
     *
     * @param record
     * @return
     */
    int insertSelective(T record);

    /**
     * 根据主键Id 获取数据
     *
     * @param id
     * @return
     */
    T selectByPrimaryKey(Long id);

    /**
     * 有选择的根据主键Id更新数据，
     * 根据字段是否有值 ，更新数据  若为 null 即不更新数据
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(T record);

    /**
     * 更新数据
     *
     * @param record
     * @return
     */
    void updateByPrimaryKey(T record);

    /**
     * 根据条件查询信息
     *
     * @param map
     * @return
     */
    List<T> selectByCondition(Map map);


    /**
     * 根据条件查询总数
     *
     * @param map
     * @return
     */
    Long countByCondition(Map map);

    /**
     * 批量插入
     *
     * @param list
     */
    void insertBatch(List<T> list);

    /**
     * 更新数据库数据状态字段 即逻辑删除
     *
     * @param id
     */
    void updateStatusById(Long id);

    /**
     * 批量更新数据库数据状态字段 即逻辑删除
     *
     * @param ids
     */
    void updateStatusByIds(List<Long> ids);


    /**
     * 根据IDS 批量获取数据
     *
     * @param ids
     * @return
     */
    List<T> selectByPrimaryKeys(List<Long> ids);
}
