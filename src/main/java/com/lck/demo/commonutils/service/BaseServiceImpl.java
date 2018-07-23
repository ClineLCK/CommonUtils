package com.lck.demo.commonutils.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lck.demo.commonutils.bean.page.Page;
import com.lck.demo.commonutils.bean.page.PageList;
import com.lck.demo.commonutils.dao.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 基础业务服务实现类
 *
 * @author ckli01
 * @date 2018/6/27
 */
@Configuration
@Slf4j
public class BaseServiceImpl<T, M extends BaseMapper<T>> implements BaseService<T> {


    @Autowired
    private M baseMapper;

    private Class<T> persistentClass;

    /**
     * 总数为null时数量即为0
     */
    private static final Long COUNT_ZERO = 0L;

    /**
     * 插入条数最多为1000
     */
    private static final Integer MAX_INSERT_COUNT = 1000;


    @Override
    public T getById(Long id) throws Exception {
        if (id != null) {
            log.debug("getById entity( {} )  by id : {}", persistentClass.getName(), id);
            return baseMapper.selectByPrimaryKey(id);
        } else {
            log.warn("getById entity( {} )  error: cant be null, so do not execute getById operate!", persistentClass.getName());
            throw new Exception("getById entity( " + persistentClass.getName() + " ) error: cant be null");
        }
    }

    public M getBaseMapper() {
        return baseMapper;
    }

    @Override
    public PageList<T> selectByCondition(Map map) {
        Integer currentPage = getPageNumber(map);
        Integer pageSize = getPageSize(map);

        //使用分页插件
        PageHelper.startPage(currentPage, pageSize);

        // 获取分页后数据
        PageInfo<T> pageInfo = new PageInfo<>(baseMapper.selectByCondition(map));

        // 获取总条数
        PageList<T> pageList = new PageList<>(pageInfo.getList(), pageInfo.getTotal());

        pageList.setCurrentPage(currentPage);
        pageList.setPageSize(pageSize);
        log.debug("selectByCondition entity( {} )  by {} ,result : {}", persistentClass.getName(),
                JSONObject.toJSONString(map), JSONObject.toJSONString(pageList));
        return pageList;
    }


    @Override
    public Integer insert(T t) {
        if (t != null) {
            log.debug("inset entity( {} ) : {}", persistentClass.getName(), JSONObject.toJSONString(t));
            return baseMapper.insert(t);
        }
        log.warn("inset entity( {} ) error: cant be null , so do not execute insert operate!", persistentClass.getName());
        return null;
    }

    @Override
    public void insertBatch(List<T> list) {
        if (!CollectionUtils.isEmpty(list)) {
            //分批处理
            Integer size = list.size();
            //判断是否需要分批
            if (MAX_INSERT_COUNT < size) {
                //分批数
                int part = size / MAX_INSERT_COUNT;
                log.info("共有 ：{}  条 ！ 分为 ： {} 批", size, part);
                for (int i = 0; i < part; i++) {
                    List<T> listPage = list.subList(0, MAX_INSERT_COUNT);
                    insetBatchs(listPage);
                    //去除
                    list.subList(0, MAX_INSERT_COUNT).clear();
                }
                if (!list.isEmpty()) {
                    //处理最后剩下的数据
                    insetBatchs(list);
                }
            } else {
                insetBatchs(list);
            }
        } else {
            log.warn("insertBatch entityList( {} ) error: cant be null, so do not execute insertBatch operate!", persistentClass.getName());
        }
    }

    /**
     * 批量插入
     *
     * @param list
     */
    private void insetBatchs(List<T> list) {
        log.debug("insertBatch entityList( {} ) : {}", persistentClass.getName(), JSONObject.toJSONString(list));
        baseMapper.insertBatch(list);
    }


    @Override
    public void logicDeleteById(Long id) {
        if (id != null) {
            log.debug("logicDelete entity( {} )  by id : {}", persistentClass.getName(), id);
            baseMapper.updateStatusById(id);
        } else {
            log.warn("logicDelete entity( {} )  error: cant be null, so do not execute logicDelete operate!", persistentClass.getName());
        }
    }

    @Override
    public void deleteByPrimaryKey(Long id) {
        if (id != null) {
            log.debug("delete entity( {} ) by id : {}", persistentClass.getName(), id);
            baseMapper.deleteByPrimaryKey(id);
        } else {
            log.warn("delete entity( {} ) error: cant be null, so do not execute delete operate!", persistentClass.getName());
        }
    }

    @Override
    public void logicDeleteBatchByIds(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            log.debug("logicDeleteBatchByIds entity( {} ) by id : {}", persistentClass.getName(), JSONObject.toJSONString(ids));
            baseMapper.updateStatusByIds(ids);
        } else {
            log.warn("logicDeleteBatchByIds entity( {} ) error: cant be null, so do not execute logicDeleteBatchByIds operate!", persistentClass.getName());
        }
    }

    @Override
    public void update(T t) throws Exception {
        if (t != null) {
            log.debug("update entity( {} ) : {}", persistentClass.getName(), JSONObject.toJSONString(t));
            baseMapper.updateByPrimaryKey(t);
        } else {
            log.warn("update entity( {} ) error: cant be null , so do not execute update operate!", persistentClass.getName());
            throw new Exception("update entity( " + persistentClass.getName() + " ) error: cant be null");
        }
    }

    @Override
    public int updateByPrimaryKeySelective(T t) throws Exception {
        if (t != null) {
            log.debug("updateByPrimaryKeySelective entity( {} ) : {}", persistentClass.getName(), JSONObject.toJSONString(t));
            return baseMapper.updateByPrimaryKeySelective(t);
        } else {
            log.warn("updateByPrimaryKeySelective entity( {} ) error: cant be null , so do not execute update operate!", persistentClass.getName());
            throw new Exception("update entity( " + persistentClass.getName() + " ) error: cant be null");
        }
    }

    @Override
    public List<T> selectByPrimaryKeys(List<Long> ids) throws Exception {
        if (!CollectionUtils.isEmpty(ids)) {
            log.debug("selectByPrimaryKeys entity( {} ) by id : {}", persistentClass.getName(), JSONObject.toJSONString(ids));
            return baseMapper.selectByPrimaryKeys(ids);
        } else {
            log.warn("selectByPrimaryKeys entity( {} ) error: cant be null, so do not execute selectByPrimaryKeys operate!", persistentClass.getName());
            throw new Exception("selectByIds entity( " + persistentClass.getName() + " ) , ids  error: cant be null");
        }
    }

    /**
     * 获取页码
     *
     * @param map
     * @return
     */
    private Integer getPageNumber(Map map) {
        try {
            if (!CollectionUtils.isEmpty(map) && null != map.get(Page.CURRENT_PAGE)) {
                return Integer.valueOf(map.get(Page.CURRENT_PAGE).toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("use pageNumber by default : {}", Page.DEFAULT_PAGE_NUM);
        }
        return Page.DEFAULT_PAGE_NUM;
    }

    /**
     * 获取页面条数
     *
     * @param map
     * @return
     */
    private Integer getPageSize(Map map) {
        try {
            if (!CollectionUtils.isEmpty(map) && null != map.get(Page.PAGE_SIZE)) {
                return Integer.valueOf(map.get(Page.PAGE_SIZE).toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("use pageSize by default : {}", Page.DEFAULT_PAGE_SIZE);

        }
        return Page.DEFAULT_PAGE_SIZE;
    }


    @SuppressWarnings("unchecked")
    public BaseServiceImpl() {
        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的超类的 Class。
        this.persistentClass = (Class<T>) getSuperClassGenricType(getClass(), 0);
    }


    /**
     * 通过反射, 获得定义Class时声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be
     * determined
     */
    @SuppressWarnings("unchecked")
    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }

}
