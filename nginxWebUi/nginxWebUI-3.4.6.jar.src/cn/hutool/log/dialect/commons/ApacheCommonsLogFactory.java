/*    */ package cn.hutool.log.dialect.commons;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ApacheCommonsLogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public ApacheCommonsLogFactory() {
/* 14 */     super("Apache Common Logging");
/* 15 */     checkLogExist(LogFactory.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/*    */     try {
/* 21 */       return (Log)new ApacheCommonsLog4JLog(name);
/* 22 */     } catch (Exception e) {
/* 23 */       return (Log)new ApacheCommonsLog(name);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/*    */     try {
/* 30 */       return (Log)new ApacheCommonsLog4JLog(clazz);
/* 31 */     } catch (Exception e) {
/* 32 */       return (Log)new ApacheCommonsLog(clazz);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   protected void checkLogExist(Class<?> logClassName) {
/* 38 */     super.checkLogExist(logClassName);
/*    */     
/* 40 */     getLog(ApacheCommonsLogFactory.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\commons\ApacheCommonsLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */