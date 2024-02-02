/*    */ package cn.hutool.log.dialect.logtube;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import io.github.logtube.Logtube;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LogTubeLogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public LogTubeLogFactory() {
/* 15 */     super("LogTube");
/* 16 */     checkLogExist(Logtube.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 21 */     return (Log)new LogTubeLog(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 26 */     return (Log)new LogTubeLog(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\logtube\LogTubeLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */