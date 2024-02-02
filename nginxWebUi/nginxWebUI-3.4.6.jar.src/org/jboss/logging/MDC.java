/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class MDC
/*    */ {
/*    */   public static Object put(String key, Object val) {
/* 41 */     return LoggerProviders.PROVIDER.putMdc(key, val);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Object get(String key) {
/* 52 */     return LoggerProviders.PROVIDER.getMdc(key);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void remove(String key) {
/* 61 */     LoggerProviders.PROVIDER.removeMdc(key);
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
/*    */   public static Map<String, Object> getMap() {
/* 75 */     return LoggerProviders.PROVIDER.getMdcMap();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void clear() {
/* 82 */     LoggerProviders.PROVIDER.clearMdc();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\MDC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */