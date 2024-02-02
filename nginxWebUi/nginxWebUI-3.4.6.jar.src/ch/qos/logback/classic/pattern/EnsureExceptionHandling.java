/*    */ package ch.qos.logback.classic.pattern;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.pattern.Converter;
/*    */ import ch.qos.logback.core.pattern.ConverterUtil;
/*    */ import ch.qos.logback.core.pattern.PostCompileProcessor;
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
/*    */ public class EnsureExceptionHandling
/*    */   implements PostCompileProcessor<ILoggingEvent>
/*    */ {
/*    */   public void process(Context context, Converter<ILoggingEvent> head) {
/* 41 */     if (head == null)
/*    */     {
/* 43 */       throw new IllegalArgumentException("cannot process empty chain");
/*    */     }
/* 45 */     if (!chainHandlesThrowable(head)) {
/* 46 */       ThrowableProxyConverter throwableProxyConverter; Converter<ILoggingEvent> tail = ConverterUtil.findTail(head);
/* 47 */       Converter<ILoggingEvent> exConverter = null;
/* 48 */       LoggerContext loggerContext = (LoggerContext)context;
/* 49 */       if (loggerContext.isPackagingDataEnabled()) {
/* 50 */         throwableProxyConverter = new ExtendedThrowableProxyConverter();
/*    */       } else {
/* 52 */         throwableProxyConverter = new ThrowableProxyConverter();
/*    */       } 
/* 54 */       tail.setNext((Converter)throwableProxyConverter);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean chainHandlesThrowable(Converter<ILoggingEvent> head) {
/* 67 */     Converter<ILoggingEvent> c = head;
/* 68 */     while (c != null) {
/* 69 */       if (c instanceof ThrowableHandlingConverter) {
/* 70 */         return true;
/*    */       }
/* 72 */       c = c.getNext();
/*    */     } 
/* 74 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\EnsureExceptionHandling.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */