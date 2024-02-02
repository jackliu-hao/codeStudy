/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
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
/*    */ abstract class AbstractMdcLoggerProvider
/*    */   extends AbstractLoggerProvider
/*    */ {
/* 27 */   private final ThreadLocal<Map<String, Object>> mdcMap = new ThreadLocal<>();
/*    */   
/*    */   public void clearMdc() {
/* 30 */     Map<String, Object> map = this.mdcMap.get();
/* 31 */     if (map != null) {
/* 32 */       map.clear();
/*    */     }
/*    */   }
/*    */   
/*    */   public Object getMdc(String key) {
/* 37 */     return (this.mdcMap.get() == null) ? null : ((Map)this.mdcMap.get()).get(key);
/*    */   }
/*    */   
/*    */   public Map<String, Object> getMdcMap() {
/* 41 */     Map<String, Object> map = this.mdcMap.get();
/* 42 */     return (map == null) ? Collections.<String, Object>emptyMap() : map;
/*    */   }
/*    */   
/*    */   public Object putMdc(String key, Object value) {
/* 46 */     Map<String, Object> map = this.mdcMap.get();
/* 47 */     if (map == null) {
/* 48 */       map = new HashMap<>();
/* 49 */       this.mdcMap.set(map);
/*    */     } 
/* 51 */     return map.put(key, value);
/*    */   }
/*    */   
/*    */   public void removeMdc(String key) {
/* 55 */     Map<String, Object> map = this.mdcMap.get();
/* 56 */     if (map == null)
/*    */       return; 
/* 58 */     map.remove(key);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\AbstractMdcLoggerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */