package com.xuecheng.checkcode.model;

import lombok.Data;

import java.util.Map;

/**
 * @author Mr.M
 * @version 1.0
 * @description Verification Code Generation Parameters Class
 * @date 2022/9/29 15:48
 */
@Data
public class CheckCodeParamsDto {

    /**
     * Verification Code Type: pic, sms, email, etc.
     */
    private String checkCodeType;

    /**
     * Business Parameters
     */
    private String param1;
    private String param2;
    private String param3;
}
