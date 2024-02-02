/*    */ package org.noear.solon.logging;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ import org.noear.solon.core.util.PrintUtil;
/*    */ import org.noear.solon.logging.appender.ConsoleAppender;
/*    */ import org.noear.solon.logging.event.Appender;
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
/*    */ public class AppenderManager
/*    */ {
/*    */   private static AppenderManager instance;
/*    */   
/*    */   public static AppenderManager getInstance() {
/* 24 */     if (instance == null) {
/* 25 */       synchronized (AppenderManager.class) {
/* 26 */         if (instance == null) {
/* 27 */           instance = new AppenderManager();
/*    */         }
/*    */       } 
/*    */     }
/*    */     
/* 32 */     return instance;
/*    */   }
/*    */   
/* 35 */   protected Map<String, AppenderHolder> appenderMap = new LinkedHashMap<>();
/*    */ 
/*    */   
/*    */   private AppenderManager() {
/* 39 */     register("console", (Appender)new ConsoleAppender());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void register(String name, Appender appender) {
/* 49 */     this.appenderMap.putIfAbsent(name, new AppenderHolder(name, appender));
/*    */     
/* 51 */     PrintUtil.info("Logging", "LogAppender registered from the " + appender.getClass().getTypeName() + "#" + name);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AppenderHolder get(String name) {
/* 58 */     return this.appenderMap.get(name);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void append(LogEvent logEvent) {
/* 67 */     for (AppenderHolder appender : this.appenderMap.values()) {
/* 68 */       appender.append(logEvent);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void stop() {
/* 76 */     for (AppenderHolder appender : this.appenderMap.values())
/* 77 */       appender.stop(); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\AppenderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */