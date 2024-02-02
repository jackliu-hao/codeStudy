/*    */ package cn.hutool.log.dialect.commons;
/*    */ 
/*    */ import cn.hutool.log.dialect.log4j.Log4jLog;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.impl.Log4JLogger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ApacheCommonsLog4JLog
/*    */   extends Log4jLog
/*    */ {
/*    */   private static final long serialVersionUID = -6843151523380063975L;
/*    */   
/*    */   public ApacheCommonsLog4JLog(Log logger) {
/* 18 */     super(((Log4JLogger)logger).getLogger());
/*    */   }
/*    */   
/*    */   public ApacheCommonsLog4JLog(Class<?> clazz) {
/* 22 */     super(clazz);
/*    */   }
/*    */   
/*    */   public ApacheCommonsLog4JLog(String name) {
/* 26 */     super(name);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\commons\ApacheCommonsLog4JLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */