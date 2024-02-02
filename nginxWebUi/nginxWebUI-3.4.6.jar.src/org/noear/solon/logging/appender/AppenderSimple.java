/*    */ package org.noear.solon.logging.appender;
/*    */ 
/*    */ import java.time.LocalDateTime;
/*    */ import java.time.ZoneId;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ import java.util.Date;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.logging.event.AppenderBase;
/*    */ import org.noear.solon.logging.event.Level;
/*    */ import org.noear.solon.logging.event.LogEvent;
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
/*    */ public class AppenderSimple
/*    */   extends AppenderBase
/*    */ {
/*    */   protected boolean allowAppend() {
/* 25 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void append(LogEvent logEvent) {
/* 35 */     if (!allowAppend()) {
/*    */       return;
/*    */     }
/*    */     
/* 39 */     LocalDateTime dateTime = LocalDateTime.ofInstant((new Date(logEvent.getTimeStamp())).toInstant(), ZoneId.systemDefault());
/* 40 */     DateTimeFormatter dateTimeF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
/*    */     
/* 42 */     StringBuilder buf = new StringBuilder();
/* 43 */     buf.append(logEvent.getLevel().name()).append(" ");
/* 44 */     buf.append(dateTimeF.format(dateTime)).append(" ");
/* 45 */     buf.append("[-").append(Thread.currentThread().getName()).append("]");
/*    */     
/* 47 */     if (logEvent.getMetainfo() != null) {
/* 48 */       String traceId = (String)logEvent.getMetainfo().get("traceId");
/*    */       
/* 50 */       if (Utils.isNotEmpty(traceId)) {
/* 51 */         buf.append("[*").append(traceId).append("]");
/*    */       }
/*    */       
/* 54 */       logEvent.getMetainfo().forEach((k, v) -> {
/*    */             if (!"traceId".equals(k)) {
/*    */               buf.append("[@").append(k).append(":").append(v).append("]");
/*    */             }
/*    */           });
/*    */     } 
/*    */     
/* 61 */     buf.append(" ").append(logEvent.getLoggerName());
/*    */     
/* 63 */     buf.append("#").append(getName());
/* 64 */     buf.append(": ");
/*    */     
/* 66 */     appendDo(logEvent.getLevel(), buf.toString(), logEvent.getContent());
/*    */   }
/*    */   
/*    */   protected void appendDo(Level level, String title, Object content) {
/* 70 */     System.out.println(title);
/* 71 */     System.out.println(content);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\appender\AppenderSimple.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */