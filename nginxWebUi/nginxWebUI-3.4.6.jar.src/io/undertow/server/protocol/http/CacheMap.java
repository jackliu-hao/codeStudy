/*    */ package io.undertow.server.protocol.http;
/*    */ 
/*    */ import java.util.LinkedHashMap;
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
/*    */ public class CacheMap<K, V>
/*    */   extends LinkedHashMap<K, V>
/*    */ {
/*    */   static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int capacity;
/*    */   
/*    */   public CacheMap(int capacity) {
/* 36 */     super(capacity, 0.75F, true);
/* 37 */     this.capacity = capacity;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
/* 46 */     return (size() > this.capacity);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\CacheMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */