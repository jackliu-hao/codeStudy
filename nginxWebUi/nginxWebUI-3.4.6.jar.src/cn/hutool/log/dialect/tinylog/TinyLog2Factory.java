/*    */ package cn.hutool.log.dialect.tinylog;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import org.tinylog.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TinyLog2Factory
/*    */   extends LogFactory
/*    */ {
/*    */   public TinyLog2Factory() {
/* 18 */     super("TinyLog");
/* 19 */     checkLogExist(Logger.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 24 */     return (Log)new TinyLog2(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 29 */     return (Log)new TinyLog2(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\tinylog\TinyLog2Factory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */