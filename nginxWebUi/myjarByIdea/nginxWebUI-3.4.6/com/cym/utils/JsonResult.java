package com.cym.utils;

public class JsonResult<T> {
   private boolean success;
   private String status;
   private String msg;
   private T obj;

   public String getMsg() {
      return this.msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }

   public boolean isSuccess() {
      return this.success;
   }

   public void setSuccess(boolean success) {
      this.success = success;
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public T getObj() {
      return this.obj;
   }

   public void setObj(T obj) {
      this.obj = obj;
   }
}
