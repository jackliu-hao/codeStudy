/*    */ package cn.hutool.log;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GlobalLogFactory
/*    */ {
/*    */   private static volatile LogFactory currentLogFactory;
/* 12 */   private static final Object lock = new Object();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static LogFactory get() {
/* 20 */     if (null == currentLogFactory) {
/* 21 */       synchronized (lock) {
/* 22 */         if (null == currentLogFactory) {
/* 23 */           currentLogFactory = LogFactory.create();
/*    */         }
/*    */       } 
/*    */     }
/* 27 */     return currentLogFactory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static LogFactory set(Class<? extends LogFactory> logFactoryClass) {
/*    */     try {
/* 45 */       return set(logFactoryClass.newInstance());
/* 46 */     } catch (Exception e) {
/* 47 */       throw new IllegalArgumentException("Can not instance LogFactory class!", e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static LogFactory set(LogFactory logFactory) {
/* 65 */     logFactory.getLog(GlobalLogFactory.class).debug("Custom Use [{}] Logger.", new Object[] { logFactory.name });
/* 66 */     currentLogFactory = logFactory;
/* 67 */     return currentLogFactory;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\GlobalLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */