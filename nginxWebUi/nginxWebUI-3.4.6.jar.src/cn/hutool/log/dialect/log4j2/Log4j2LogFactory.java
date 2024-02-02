/*    */ package cn.hutool.log.dialect.log4j2;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import org.apache.logging.log4j.LogManager;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Log4j2LogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public Log4j2LogFactory() {
/* 14 */     super("Log4j2");
/* 15 */     checkLogExist(LogManager.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 20 */     return (Log)new Log4j2Log(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 25 */     return (Log)new Log4j2Log(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\log4j2\Log4j2LogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */