/*    */ package cn.hutool.log.dialect.tinylog;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import org.pmw.tinylog.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TinyLogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public TinyLogFactory() {
/* 18 */     super("TinyLog");
/* 19 */     checkLogExist(Logger.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 24 */     return (Log)new TinyLog(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 29 */     return (Log)new TinyLog(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\tinylog\TinyLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */