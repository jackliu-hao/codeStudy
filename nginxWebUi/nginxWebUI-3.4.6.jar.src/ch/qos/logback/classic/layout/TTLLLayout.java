/*    */ package ch.qos.logback.classic.layout;
/*    */ 
/*    */ import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.classic.spi.IThrowableProxy;
/*    */ import ch.qos.logback.core.CoreConstants;
/*    */ import ch.qos.logback.core.LayoutBase;
/*    */ import ch.qos.logback.core.util.CachingDateFormatter;
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
/*    */ public class TTLLLayout
/*    */   extends LayoutBase<ILoggingEvent>
/*    */ {
/* 25 */   CachingDateFormatter cachingDateFormatter = new CachingDateFormatter("HH:mm:ss.SSS");
/* 26 */   ThrowableProxyConverter tpc = new ThrowableProxyConverter();
/*    */ 
/*    */   
/*    */   public void start() {
/* 30 */     this.tpc.start();
/* 31 */     super.start();
/*    */   }
/*    */ 
/*    */   
/*    */   public String doLayout(ILoggingEvent event) {
/* 36 */     if (!isStarted()) {
/* 37 */       return "";
/*    */     }
/* 39 */     StringBuilder sb = new StringBuilder();
/*    */     
/* 41 */     long timestamp = event.getTimeStamp();
/*    */     
/* 43 */     sb.append(this.cachingDateFormatter.format(timestamp));
/* 44 */     sb.append(" [");
/* 45 */     sb.append(event.getThreadName());
/* 46 */     sb.append("] ");
/* 47 */     sb.append(event.getLevel().toString());
/* 48 */     sb.append(" ");
/* 49 */     sb.append(event.getLoggerName());
/* 50 */     sb.append(" - ");
/* 51 */     sb.append(event.getFormattedMessage());
/* 52 */     sb.append(CoreConstants.LINE_SEPARATOR);
/* 53 */     IThrowableProxy tp = event.getThrowableProxy();
/* 54 */     if (tp != null) {
/* 55 */       String stackTrace = this.tpc.convert(event);
/* 56 */       sb.append(stackTrace);
/*    */     } 
/* 58 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\layout\TTLLLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */