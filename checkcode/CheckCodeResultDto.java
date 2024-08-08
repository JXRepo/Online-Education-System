package com.xuecheng.checkcode.model;

import lombok.Data;

/**
 * @author Mr.M
 * @version 1.0
 * @description Verification Code Generation Result Class
 * @date 2022/9/29 15:48
 */
@Data
public class CheckCodeResultDto {

    /**
     * The key is used for verification.
     */
    private String key;

    /**
     * Obfuscated content
     * Examples:
     * 1. For image verification code: image base64 encoding
     * 2. For SMS verification code: null
     * 3. For email verification code: null
     * 4. For email link click verification: null
     * ...
     */
    private String aliasing;
}
