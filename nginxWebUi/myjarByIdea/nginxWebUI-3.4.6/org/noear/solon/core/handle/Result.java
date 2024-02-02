package org.noear.solon.core.handle;

import java.io.Serializable;
import org.noear.solon.annotation.Note;

public class Result<T> implements Serializable {
   public static int SUCCEED_CODE = 200;
   public static int FAILURE_CODE = 400;
   private int code;
   private String description;
   private T data;

   public int getCode() {
      return this.code;
   }

   public void setCode(int code) {
      this.code = code;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      if (description == null) {
         this.description = "";
      } else {
         this.description = description;
      }

   }

   public void setData(T data) {
      this.data = data;
   }

   public T getData() {
      return this.data;
   }

   public Result() {
      this.code = SUCCEED_CODE;
      this.description = "";
   }

   public Result(T data) {
      this.code = SUCCEED_CODE;
      this.description = "";
      this.data = data;
   }

   public Result(int code, String description) {
      this.code = code;
      this.description = description;
   }

   public Result(int code, String description, T data) {
      this.code = code;
      this.description = description;
      this.data = data;
   }

   @Note("成功的空结果")
   public static <T> Result<T> succeed() {
      return new Result(SUCCEED_CODE, "");
   }

   @Note("成功的结果")
   public static <T> Result<T> succeed(T data) {
      return new Result(data);
   }

   @Note("成功的结果")
   public static <T> Result<T> succeed(T data, String description) {
      return new Result(SUCCEED_CODE, description, data);
   }

   @Note("成功的结果")
   public static <T> Result<T> succeed(T data, int code) {
      return new Result(code, "", data);
   }

   @Note("失败的空结果")
   public static <T> Result<T> failure() {
      return new Result(FAILURE_CODE, "");
   }

   @Note("失败的结果")
   public static <T> Result<T> failure(int code) {
      return failure(code, "");
   }

   @Note("失败的结果")
   public static <T> Result<T> failure(int code, String description) {
      return new Result(code, description);
   }

   @Note("失败的结果")
   public static <T> Result<T> failure(String description) {
      return new Result(FAILURE_CODE, description);
   }
}
