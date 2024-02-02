/*    */ package org.antlr.v4.runtime.misc;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
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
/*    */ public class DoubleKeyMap<Key1, Key2, Value>
/*    */ {
/* 43 */   Map<Key1, Map<Key2, Value>> data = new LinkedHashMap<Key1, Map<Key2, Value>>();
/*    */   
/*    */   public Value put(Key1 k1, Key2 k2, Value v) {
/* 46 */     Map<Key2, Value> data2 = this.data.get(k1);
/* 47 */     Value prev = null;
/* 48 */     if (data2 == null) {
/* 49 */       data2 = new LinkedHashMap<Key2, Value>();
/* 50 */       this.data.put(k1, data2);
/*    */     } else {
/*    */       
/* 53 */       prev = data2.get(k2);
/*    */     } 
/* 55 */     data2.put(k2, v);
/* 56 */     return prev;
/*    */   }
/*    */   
/*    */   public Value get(Key1 k1, Key2 k2) {
/* 60 */     Map<Key2, Value> data2 = this.data.get(k1);
/* 61 */     if (data2 == null) return null; 
/* 62 */     return data2.get(k2);
/*    */   }
/*    */   public Map<Key2, Value> get(Key1 k1) {
/* 65 */     return this.data.get(k1);
/*    */   }
/*    */   
/*    */   public Collection<Value> values(Key1 k1) {
/* 69 */     Map<Key2, Value> data2 = this.data.get(k1);
/* 70 */     if (data2 == null) return null; 
/* 71 */     return data2.values();
/*    */   }
/*    */ 
/*    */   
/*    */   public Set<Key1> keySet() {
/* 76 */     return this.data.keySet();
/*    */   }
/*    */ 
/*    */   
/*    */   public Set<Key2> keySet(Key1 k1) {
/* 81 */     Map<Key2, Value> data2 = this.data.get(k1);
/* 82 */     if (data2 == null) return null; 
/* 83 */     return data2.keySet();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\DoubleKeyMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */