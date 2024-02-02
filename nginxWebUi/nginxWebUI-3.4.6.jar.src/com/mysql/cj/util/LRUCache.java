/*    */ package com.mysql.cj.util;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LRUCache<K, V>
/*    */   extends LinkedHashMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected int maxElements;
/*    */   
/*    */   public LRUCache(int maxSize) {
/* 40 */     super(maxSize, 0.75F, true);
/* 41 */     this.maxElements = maxSize;
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
/* 46 */     return (size() > this.maxElements);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\LRUCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */