/*    */ package cn.hutool.log.dialect.console;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConsoleLogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public ConsoleLogFactory() {
/* 14 */     super("Hutool Console Logging");
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 19 */     return (Log)new ConsoleLog(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 24 */     return (Log)new ConsoleLog(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\console\ConsoleLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */