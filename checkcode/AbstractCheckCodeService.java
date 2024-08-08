package com.xuecheng.checkcode.service;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @author Mr.M
 * @version 1.0
 * @description Captcha API
 * @date 2022/9/29 15:59
 */
@Slf4j
public abstract class AbstractCheckCodeService implements CheckCodeService {

    protected CheckCodeGenerator checkCodeGenerator;
    protected KeyGenerator keyGenerator;
    protected CheckCodeStore checkCodeStore;

    public abstract void  setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator);
    public abstract void  setKeyGenerator(KeyGenerator keyGenerator);
    public abstract void  setCheckCodeStore(CheckCodeStore CheckCodeStore);


    /**
     * @description Generate common method for verification
     * @param checkCodeParamsDto Parameters for generating the verification code
     * @param code_length Length of the verification code
     * @param keyPrefix Prefix for the key
     * @param expire Expiration time
     * @return com.xuecheng.checkcode.service.AbstractCheckCodeService.GenerateResult Generation result
     * @author Mr.M
     * @date 2022/9/30 6:07
    */
    public GenerateResult generate(CheckCodeParamsDto checkCodeParamsDto,Integer code_length,String keyPrefix,Integer expire){
        //Generate a four-digit verification code
        String code = checkCodeGenerator.generate(code_length);
        log.debug("Generate a verification code:{}",code);
        //Generate a key
        String key = keyGenerator.generate(keyPrefix);

        //Store the verification code
        checkCodeStore.set(key,code,expire);
        //Return the verification code generation result
        GenerateResult generateResult = new GenerateResult();
        generateResult.setKey(key);
        generateResult.setCode(code);
        return generateResult;
    }

    @Data
    protected class GenerateResult{
        String key;
        String code;
    }


    public abstract CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto);


    public boolean verify(String key, String code){
        if (StringUtils.isBlank(key) || StringUtils.isBlank(code)){
            return false;
        }
        String code_l = checkCodeStore.get(key);
        if (code_l == null){
            return false;
        }
        boolean result = code_l.equalsIgnoreCase(code);
        if(result){
            //Delete the verification code
            checkCodeStore.remove(key);
        }
        return result;
    }


}
