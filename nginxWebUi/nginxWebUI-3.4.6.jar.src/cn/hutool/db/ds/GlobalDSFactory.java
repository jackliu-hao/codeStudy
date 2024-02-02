/*    */ package cn.hutool.db.ds;
/*    */ 
/*    */ import cn.hutool.log.StaticLog;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GlobalDSFactory
/*    */ {
/*    */   private static volatile DSFactory factory;
/* 15 */   private static final Object lock = new Object();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static {
/* 22 */     Runtime.getRuntime().addShutdownHook(new Thread()
/*    */         {
/*    */           public void run() {
/* 25 */             if (null != GlobalDSFactory.factory) {
/* 26 */               GlobalDSFactory.factory.destroy();
/* 27 */               StaticLog.debug("DataSource: [{}] destroyed.", new Object[] { (GlobalDSFactory.access$000()).dataSourceName });
/* 28 */               GlobalDSFactory.factory = null;
/*    */             } 
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static DSFactory get() {
/* 42 */     if (null == factory) {
/* 43 */       synchronized (lock) {
/* 44 */         if (null == factory) {
/* 45 */           factory = DSFactory.create(null);
/*    */         }
/*    */       } 
/*    */     }
/* 49 */     return factory;
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
/*    */   public static DSFactory set(DSFactory customDSFactory) {
/* 66 */     synchronized (lock) {
/* 67 */       if (null != factory) {
/* 68 */         if (factory.equals(customDSFactory)) {
/* 69 */           return factory;
/*    */         }
/*    */         
/* 72 */         factory.destroy();
/*    */       } 
/*    */       
/* 75 */       StaticLog.debug("Custom use [{}] DataSource.", new Object[] { customDSFactory.dataSourceName });
/* 76 */       factory = customDSFactory;
/*    */     } 
/* 78 */     return factory;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\GlobalDSFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */