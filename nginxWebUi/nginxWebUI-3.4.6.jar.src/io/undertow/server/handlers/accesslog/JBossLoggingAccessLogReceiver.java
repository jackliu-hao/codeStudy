/*    */ package io.undertow.server.handlers.accesslog;
/*    */ 
/*    */ import org.jboss.logging.Logger;
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
/*    */ public class JBossLoggingAccessLogReceiver
/*    */   implements AccessLogReceiver
/*    */ {
/*    */   public static final String DEFAULT_CATEGORY = "io.undertow.accesslog";
/*    */   private final Logger logger;
/*    */   
/*    */   public JBossLoggingAccessLogReceiver(String category) {
/* 35 */     this.logger = Logger.getLogger(category);
/*    */   }
/*    */   
/*    */   public JBossLoggingAccessLogReceiver() {
/* 39 */     this.logger = Logger.getLogger("io.undertow.accesslog");
/*    */   }
/*    */ 
/*    */   
/*    */   public void logMessage(String message) {
/* 44 */     this.logger.info(message);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\accesslog\JBossLoggingAccessLogReceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */