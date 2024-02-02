/*    */ package com.mysql.cj.exceptions;
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
/*    */ public class DataConversionException
/*    */   extends DataReadException
/*    */ {
/*    */   private static final long serialVersionUID = -863576663404236982L;
/*    */   
/*    */   public DataConversionException(String msg) {
/* 39 */     super(msg);
/* 40 */     setSQLState("22018");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\DataConversionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */