package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

/**
 * @description Generic result type
 * @author Mr.M
 * @date 2022/9/13 14:44
 * @version 1.0
 */

 @Data
 @ToString
public class RestResponse<T> {

  /**
   * Response code: 0 for success, -1 for error
   */
  private int code;

  /**
   * Response message
   */
  private String msg;

  /**
   * Response content
   */
  private T result;


  public RestResponse() {
   this(0, "success");
  }

  public RestResponse(int code, String msg) {
   this.code = code;
   this.msg = msg;
  }

  /**
   * Encapsulation of error information
   *
   * @param msg
   * @param <T>
   * @return
   */
  public static <T> RestResponse<T> validfail(String msg) {
   RestResponse<T> response = new RestResponse<T>();
   response.setCode(-1);
   response.setMsg(msg);
   return response;
  }
  public static <T> RestResponse<T> validfail(T result,String msg) {
   RestResponse<T> response = new RestResponse<T>();
   response.setCode(-1);
   response.setResult(result);
   response.setMsg(msg);
   return response;
  }



  /**
   * Add normal response data (including response content)
   *
   * @return RestResponse RestService wraps response data
   */
  public static <T> RestResponse<T> success(T result) {
   RestResponse<T> response = new RestResponse<T>();
   response.setResult(result);
   return response;
  }
  public static <T> RestResponse<T> success(T result,String msg) {
   RestResponse<T> response = new RestResponse<T>();
   response.setResult(result);
   response.setMsg(msg);
   return response;
  }

  /**
   * Add normal response data (excluding response content)
   *
   * @return RestResponse Rest service encapsulates response data
   */
  public static <T> RestResponse<T> success() {
   return new RestResponse<T>();
  }


  public Boolean isSuccessful() {
   return this.code == 0;
  }

 }