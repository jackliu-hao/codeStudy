/*    */ package ch.qos.logback.classic.pattern;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.classic.spi.StackTraceElementProxy;
/*    */ import ch.qos.logback.classic.spi.ThrowableProxyUtil;
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
/*    */ public class ExtendedThrowableProxyConverter
/*    */   extends ThrowableProxyConverter
/*    */ {
/*    */   protected void extraData(StringBuilder builder, StackTraceElementProxy step) {
/* 24 */     ThrowableProxyUtil.subjoinPackagingData(builder, step);
/*    */   }
/*    */   
/*    */   protected void prepareLoggingEvent(ILoggingEvent event) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\ExtendedThrowableProxyConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */