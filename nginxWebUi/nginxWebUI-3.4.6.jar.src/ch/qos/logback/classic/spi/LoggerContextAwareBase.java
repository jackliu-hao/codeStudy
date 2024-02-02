/*    */ package ch.qos.logback.classic.spi;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.spi.ContextAwareBase;
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
/*    */ public class LoggerContextAwareBase
/*    */   extends ContextAwareBase
/*    */   implements LoggerContextAware
/*    */ {
/*    */   public void setLoggerContext(LoggerContext context) {
/* 27 */     super.setContext((Context)context);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void setContext(Context context) {
/* 33 */     if (context instanceof LoggerContext || context == null) {
/* 34 */       super.setContext(context);
/*    */     } else {
/* 36 */       throw new IllegalArgumentException("LoggerContextAwareBase only accepts contexts of type c.l.classic.LoggerContext");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LoggerContext getLoggerContext() {
/* 46 */     return (LoggerContext)this.context;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\LoggerContextAwareBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */