/*    */ package org.jboss.logging;
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
/*    */ final class JDKLoggerProvider
/*    */   extends AbstractMdcLoggerProvider
/*    */   implements LoggerProvider
/*    */ {
/*    */   public Logger getLogger(String name) {
/* 24 */     return new JDKLogger(name);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\JDKLoggerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */