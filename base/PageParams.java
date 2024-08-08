package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author Mr.M
 * @version 1.0
 * @description Pagination query pagination parameters
 * @date 2023/2/11 15:33
 */
@Data
@ToString
public class PageParams {

    //Current page number
    @ApiModelProperty("Page number")
    private Long pageNo = 1L;
    //Records per page
    @ApiModelProperty("Records per page")
    private Long pageSize = 30L;

    public PageParams() {
    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
