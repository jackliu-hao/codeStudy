/*    */ package com.mysql.cj.jdbc;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*    */ import java.sql.NClob;
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
/*    */ public class NClob
/*    */   extends Clob
/*    */   implements NClob
/*    */ {
/*    */   NClob(ExceptionInterceptor exceptionInterceptor) {
/* 40 */     super(exceptionInterceptor);
/*    */   }
/*    */   
/*    */   public NClob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
/* 44 */     super(charDataInit, exceptionInterceptor);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\NClob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */