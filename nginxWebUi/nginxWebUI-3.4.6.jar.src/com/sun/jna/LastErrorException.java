/*    */ package com.sun.jna;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LastErrorException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int errorCode;
/*    */   
/*    */   private static String formatMessage(int code) {
/* 37 */     return Platform.isWindows() ? ("GetLastError() returned " + code) : ("errno was " + code);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static String parseMessage(String m) {
/*    */     try {
/* 44 */       return formatMessage(Integer.parseInt(m));
/* 45 */     } catch (NumberFormatException e) {
/* 46 */       return m;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getErrorCode() {
/* 54 */     return this.errorCode;
/*    */   }
/*    */   
/*    */   public LastErrorException(String msg) {
/* 58 */     super(parseMessage(msg.trim()));
/*    */     try {
/* 60 */       if (msg.startsWith("[")) {
/* 61 */         msg = msg.substring(1, msg.indexOf("]"));
/*    */       }
/* 63 */       this.errorCode = Integer.parseInt(msg);
/* 64 */     } catch (NumberFormatException e) {
/* 65 */       this.errorCode = -1;
/*    */     } 
/*    */   }
/*    */   
/*    */   public LastErrorException(int code) {
/* 70 */     this(code, formatMessage(code));
/*    */   }
/*    */   
/*    */   protected LastErrorException(int code, String msg) {
/* 74 */     super(msg);
/* 75 */     this.errorCode = code;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\LastErrorException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */