package com.lck.demo.commonutils.bean.page;

/**
 * 前端请求分页类
 *
 * @author ckli01
 * @date 2018/6/25
 */
public class Page {


    /**
     * 默认页面开始页码
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;

    /**
     * 默认页面显示条数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 30;


    public static final String CURRENT_PAGE = "currentPage";
    public static final String PAGE_SIZE = "pageSize";


    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 页面显示条数
     */
    private Integer pageSize = 10;

    /**
     * 总条数
     */
    private Long totalCount;

    /**
     * 总页数
     */
    private Integer totalPages;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        int p = this.totalCount.intValue() / this.getPageSize().intValue();
        this.totalPages = (p * getPageSize().intValue() == this.totalCount.intValue()) ? p : p + 1;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
