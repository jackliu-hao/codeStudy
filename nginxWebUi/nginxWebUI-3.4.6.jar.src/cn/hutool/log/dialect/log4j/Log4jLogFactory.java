/*    */ package cn.hutool.log.dialect.log4j;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Log4jLogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public Log4jLogFactory() {
/* 14 */     super("Log4j");
/* 15 */     checkLogExist(Logger.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 20 */     return (Log)new Log4jLog(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 25 */     return (Log)new Log4jLog(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\log4j\Log4jLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */