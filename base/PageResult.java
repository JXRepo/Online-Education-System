package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description Pagination query result model class
 * @date 2023/2/11 15:40
 */
@Data
@ToString
public class PageResult<T> implements Serializable {

    //Data list
    private List<T> items;

    //Total record count
    private long counts;

    //Current page number
    private long page;

    //Records per page
    private long pageSize;

    public PageResult(List<T> items, long counts, long page, long pageSize) {
        this.items = items;
        this.counts = counts;
        this.page = page;
        this.pageSize = pageSize;
    }


}
