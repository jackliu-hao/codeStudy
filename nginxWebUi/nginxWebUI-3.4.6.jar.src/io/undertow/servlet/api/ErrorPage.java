/*    */ package io.undertow.servlet.api;
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
/*    */ public class ErrorPage
/*    */ {
/*    */   private final String location;
/*    */   private final Integer errorCode;
/*    */   private final Class<? extends Throwable> exceptionType;
/*    */   
/*    */   public ErrorPage(String location, Class<? extends Throwable> exceptionType) {
/* 34 */     this.location = location;
/* 35 */     this.errorCode = null;
/* 36 */     this.exceptionType = exceptionType;
/*    */   }
/*    */   public ErrorPage(String location, int errorCode) {
/* 39 */     this.location = location;
/* 40 */     this.errorCode = Integer.valueOf(errorCode);
/* 41 */     this.exceptionType = null;
/*    */   }
/*    */   
/*    */   public ErrorPage(String location) {
/* 45 */     this.location = location;
/* 46 */     this.errorCode = null;
/* 47 */     this.exceptionType = null;
/*    */   }
/*    */   
/*    */   public String getLocation() {
/* 51 */     return this.location;
/*    */   }
/*    */   
/*    */   public Integer getErrorCode() {
/* 55 */     return this.errorCode;
/*    */   }
/*    */   
/*    */   public Class<? extends Throwable> getExceptionType() {
/* 59 */     return this.exceptionType;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ErrorPage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */