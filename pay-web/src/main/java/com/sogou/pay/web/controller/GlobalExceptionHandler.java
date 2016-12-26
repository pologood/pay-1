package com.sogou.pay.web.controller;

import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.ResourceAccessException;

import com.google.common.base.Throwables;
import com.sogou.pay.common.types.Result;
import com.sogou.pay.common.types.ResultStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public Result<?> internalServerError(Exception e) {
    e.printStackTrace();
    return new Result<>(ResultStatus.SYSTEM_ERROR, Throwables.getStackTraceAsString(e));
  }

  @ExceptionHandler(DataAccessException.class)
  @ResponseBody
  public Result<?> dataAccessExcption(Exception e) {
    e.printStackTrace();
    return new Result<>(ResultStatus.SYSTEM_DB_ERROR, "interal db error");
  }

  @ExceptionHandler(ServletRequestBindingException.class)
  @ResponseBody
  public Result<?> servletRequestBindingException(Exception e) {
    return new Result<>(ResultStatus.PARAM_ERROR, Throwables.getStackTraceAsString(e));
  }

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseBody
  public Result<?> typeMismatchException(Exception e) {
    return new Result<>(ResultStatus.PARAM_ERROR, e.toString());
  }

  @ExceptionHandler(ResourceAccessException.class)
  @ResponseBody
  public Result<?> resourceAccessException(Exception e) {
    e.printStackTrace();
    return new Result<>(ResultStatus.SYSTEM_ERROR, e.getMessage());
  }
}
