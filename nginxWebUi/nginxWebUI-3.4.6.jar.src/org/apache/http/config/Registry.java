/*    */ package org.apache.http.config;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
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
/*    */ @Contract(threading = ThreadingBehavior.SAFE)
/*    */ public final class Registry<I>
/*    */   implements Lookup<I>
/*    */ {
/*    */   private final Map<String, I> map;
/*    */   
/*    */   Registry(Map<String, I> map) {
/* 49 */     this.map = new ConcurrentHashMap<String, I>(map);
/*    */   }
/*    */ 
/*    */   
/*    */   public I lookup(String key) {
/* 54 */     if (key == null) {
/* 55 */       return null;
/*    */     }
/* 57 */     return this.map.get(key.toLowerCase(Locale.ROOT));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 62 */     return this.map.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\config\Registry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */