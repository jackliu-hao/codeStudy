/*    */ package com.mysql.cj.jdbc.exceptions;
/*    */ 
/*    */ import java.sql.DataTruncation;
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
/*    */ public class MysqlDataTruncation
/*    */   extends DataTruncation
/*    */ {
/*    */   static final long serialVersionUID = 3263928195256986226L;
/*    */   private String message;
/*    */   private int vendorErrorCode;
/*    */   
/*    */   public MysqlDataTruncation(String message, int index, boolean parameter, boolean read, int dataSize, int transferSize, int vendorErrorCode) {
/* 64 */     super(index, parameter, read, dataSize, transferSize);
/*    */     
/* 66 */     this.message = message;
/* 67 */     this.vendorErrorCode = vendorErrorCode;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getErrorCode() {
/* 72 */     return this.vendorErrorCode;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 77 */     return super.getMessage() + ": " + this.message;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\MysqlDataTruncation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */