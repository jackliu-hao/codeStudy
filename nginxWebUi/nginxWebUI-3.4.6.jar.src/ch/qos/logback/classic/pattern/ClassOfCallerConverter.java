/*    */ package ch.qos.logback.classic.pattern;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
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
/*    */ public class ClassOfCallerConverter
/*    */   extends NamedConverter
/*    */ {
/*    */   protected String getFullyQualifiedName(ILoggingEvent event) {
/* 23 */     StackTraceElement[] cda = event.getCallerData();
/* 24 */     if (cda != null && cda.length > 0) {
/* 25 */       return cda[0].getClassName();
/*    */     }
/* 27 */     return "?";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\ClassOfCallerConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */