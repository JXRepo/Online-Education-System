package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Mr.M
 * @version 1.0
 * @description Verification code interface
 * @date 2022/9/29 15:59
 */
public interface CheckCodeService {


    /**
     * @description Generate verification code
     * @param checkCodeParamsDto Generate verification code parameters
     * @return com.xuecheng.checkcode.model.CheckCodeResultDto Verification code result
     * @author Mr.M
     * @date 2022/9/29 18:21
    */
     CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto);

     /**
      * @description Verify the verification code
      * @param key
      * @param code
      * @return boolean
      * @author Mr.M
      * @date 2022/9/29 18:46
     */
    public boolean verify(String key, String code);


    /**
     * @description Verification code generator
     * @author Mr.M
     * @date 2022/9/29 16:34
    */
    public interface CheckCodeGenerator{
        /**
         * Verification code generation
         * @return Verification code
         */
        String generate(int length);
        

    }

    /**
     * @description Key generator
     * @author Mr.M
     * @date 2022/9/29 16:34
     */
    public interface KeyGenerator{

        /**
         * Key generation
         * @return Verification code
         */
        String generate(String prefix);
    }


    /**
     * @description Verification code storage
     * @author Mr.M
     * @date 2022/9/29 16:34
     */
    public interface CheckCodeStore{

        /**
         * @description Set key in the cache
         * @param key key
         * @param value value
         * @param expire Expiration time, in seconds
         * @return void
         * @author Mr.M
         * @date 2022/9/29 17:15
        */
        void set(String key, String value, Integer expire);

        String get(String key);

        void remove(String key);
    }
}
