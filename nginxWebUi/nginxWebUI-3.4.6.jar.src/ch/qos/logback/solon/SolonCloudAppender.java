/*    */ package ch.qos.logback.solon;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.classic.spi.IThrowableProxy;
/*    */ import ch.qos.logback.classic.spi.ThrowableProxyUtil;
/*    */ import ch.qos.logback.core.AppenderBase;
/*    */ import org.noear.solon.logging.AppenderHolder;
/*    */ import org.noear.solon.logging.AppenderManager;
/*    */ import org.noear.solon.logging.event.Level;
/*    */ import org.noear.solon.logging.event.LogEvent;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SolonCloudAppender
/*    */   extends AppenderBase<ILoggingEvent>
/*    */ {
/*    */   AppenderHolder appender;
/*    */   
/*    */   protected void append(ILoggingEvent e) {
/* 24 */     if (this.appender == null) {
/* 25 */       this.appender = AppenderManager.getInstance().get("cloud");
/*    */       
/* 27 */       if (this.appender == null) {
/*    */         return;
/*    */       }
/*    */     } 
/*    */     
/* 32 */     Level level = Level.INFO;
/*    */     
/* 34 */     switch (e.getLevel().toInt()) {
/*    */       case 5000:
/* 36 */         level = Level.TRACE;
/*    */         break;
/*    */       case 10000:
/* 39 */         level = Level.DEBUG;
/*    */         break;
/*    */       case 30000:
/* 42 */         level = Level.WARN;
/*    */         break;
/*    */       case 40000:
/* 45 */         level = Level.ERROR;
/*    */         break;
/*    */     } 
/*    */     
/* 49 */     String message = e.getFormattedMessage();
/* 50 */     IThrowableProxy throwableProxy = e.getThrowableProxy();
/* 51 */     if (throwableProxy != null) {
/* 52 */       String errorStr = ThrowableProxyUtil.asString(throwableProxy);
/*    */       
/* 54 */       if (message.contains("{}")) {
/* 55 */         message = message.replace("{}", errorStr);
/*    */       } else {
/* 57 */         message = message + "\n" + errorStr;
/*    */       } 
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 67 */     LogEvent event = new LogEvent(e.getLoggerName(), level, e.getMDCPropertyMap(), message, e.getTimeStamp(), e.getThreadName(), null);
/*    */ 
/*    */ 
/*    */     
/* 71 */     this.appender.append(event);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\solon\SolonCloudAppender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */