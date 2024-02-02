/*    */ package org.slf4j.helpers;
/*    */ 
/*    */ import org.slf4j.ILoggerFactory;
/*    */ import org.slf4j.Logger;
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
/*    */ public class NOPLoggerFactory
/*    */   implements ILoggerFactory
/*    */ {
/*    */   public Logger getLogger(String name) {
/* 45 */     return NOPLogger.NOP_LOGGER;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\helpers\NOPLoggerFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */