/*    */ package ch.qos.logback.classic.pattern;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.core.CoreConstants;
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
/*    */ public class LineSeparatorConverter
/*    */   extends ClassicConverter
/*    */ {
/*    */   public String convert(ILoggingEvent event) {
/* 22 */     return CoreConstants.LINE_SEPARATOR;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\LineSeparatorConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */