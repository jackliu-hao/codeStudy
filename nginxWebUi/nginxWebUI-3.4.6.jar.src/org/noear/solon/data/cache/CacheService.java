/*    */ package org.noear.solon.data.cache;
/*    */ 
/*    */ import java.util.function.Supplier;
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
/*    */ public interface CacheService
/*    */ {
/*    */   void store(String paramString, Object paramObject, int paramInt);
/*    */   
/*    */   void remove(String paramString);
/*    */   
/*    */   Object get(String paramString);
/*    */   
/*    */   default <T> T getOrStore(String key, int seconds, Supplier supplier) {
/* 44 */     Object obj = get(key);
/* 45 */     if (obj == null) {
/* 46 */       obj = supplier.get();
/* 47 */       store(key, obj, seconds);
/*    */     } 
/*    */     
/* 50 */     return (T)obj;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\CacheService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */