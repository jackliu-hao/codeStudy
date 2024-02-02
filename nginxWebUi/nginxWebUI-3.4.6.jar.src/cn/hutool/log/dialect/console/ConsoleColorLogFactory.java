/*    */ package cn.hutool.log.dialect.console;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConsoleColorLogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public ConsoleColorLogFactory() {
/* 15 */     super("Hutool Console Color Logging");
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 20 */     return (Log)new ConsoleColorLog(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 25 */     return (Log)new ConsoleColorLog(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\console\ConsoleColorLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */