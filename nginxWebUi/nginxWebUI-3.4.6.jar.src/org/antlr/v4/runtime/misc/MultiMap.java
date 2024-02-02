/*    */ package org.antlr.v4.runtime.misc;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.List;
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
/*    */ public class MultiMap<K, V>
/*    */   extends LinkedHashMap<K, List<V>>
/*    */ {
/*    */   public void map(K key, V value) {
/* 39 */     List<V> elementsForKey = get(key);
/* 40 */     if (elementsForKey == null) {
/* 41 */       elementsForKey = new ArrayList<V>();
/* 42 */       put(key, (V)elementsForKey);
/*    */     } 
/* 44 */     elementsForKey.add(value);
/*    */   }
/*    */   
/*    */   public List<Pair<K, V>> getPairs() {
/* 48 */     List<Pair<K, V>> pairs = new ArrayList<Pair<K, V>>();
/* 49 */     for (K key : keySet()) {
/* 50 */       for (V value : get(key)) {
/* 51 */         pairs.add(new Pair<K, V>(key, value));
/*    */       }
/*    */     } 
/* 54 */     return pairs;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\MultiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */