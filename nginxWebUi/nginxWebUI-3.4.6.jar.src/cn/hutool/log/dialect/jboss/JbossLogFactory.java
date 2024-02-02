/*    */ package cn.hutool.log.dialect.jboss;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import org.jboss.logging.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JbossLogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public JbossLogFactory() {
/* 18 */     super("JBoss Logging");
/* 19 */     checkLogExist(Logger.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 24 */     return (Log)new JbossLog(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 29 */     return (Log)new JbossLog(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\jboss\JbossLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */