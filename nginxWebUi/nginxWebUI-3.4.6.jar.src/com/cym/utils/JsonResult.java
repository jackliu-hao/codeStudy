/*    */ package com.cym.utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JsonResult<T>
/*    */ {
/*    */   private boolean success;
/*    */   private String status;
/*    */   private String msg;
/*    */   private T obj;
/*    */   
/*    */   public String getMsg() {
/* 25 */     return this.msg;
/*    */   }
/*    */   
/*    */   public void setMsg(String msg) {
/* 29 */     this.msg = msg;
/*    */   }
/*    */   
/*    */   public boolean isSuccess() {
/* 33 */     return this.success;
/*    */   }
/*    */   
/*    */   public void setSuccess(boolean success) {
/* 37 */     this.success = success;
/*    */   }
/*    */   
/*    */   public String getStatus() {
/* 41 */     return this.status;
/*    */   }
/*    */   
/*    */   public void setStatus(String status) {
/* 45 */     this.status = status;
/*    */   }
/*    */   
/*    */   public T getObj() {
/* 49 */     return this.obj;
/*    */   }
/*    */   
/*    */   public void setObj(T obj) {
/* 53 */     this.obj = obj;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\JsonResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */