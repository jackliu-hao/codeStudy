/*    */ package cn.hutool.core.lang;
/*    */ 
/*    */ import cn.hutool.core.lang.hash.Hash32;
/*    */ import cn.hutool.core.util.HashUtil;
/*    */ import java.io.Serializable;
/*    */ import java.util.Collection;
/*    */ import java.util.SortedMap;
/*    */ import java.util.TreeMap;
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
/*    */ public class ConsistentHash<T>
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   Hash32<Object> hashFunc;
/*    */   private final int numberOfReplicas;
/* 27 */   private final SortedMap<Integer, T> circle = new TreeMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConsistentHash(int numberOfReplicas, Collection<T> nodes) {
/* 35 */     this.numberOfReplicas = numberOfReplicas;
/* 36 */     this.hashFunc = (key -> HashUtil.fnvHash(key.toString()));
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 41 */     for (T node : nodes) {
/* 42 */       add(node);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConsistentHash(Hash32<Object> hashFunc, int numberOfReplicas, Collection<T> nodes) {
/* 53 */     this.numberOfReplicas = numberOfReplicas;
/* 54 */     this.hashFunc = hashFunc;
/*    */     
/* 56 */     for (T node : nodes) {
/* 57 */       add(node);
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
/*    */   public void add(T node) {
/* 69 */     for (int i = 0; i < this.numberOfReplicas; i++) {
/* 70 */       this.circle.put(Integer.valueOf(this.hashFunc.hash32(node.toString() + i)), node);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void remove(T node) {
/* 79 */     for (int i = 0; i < this.numberOfReplicas; i++) {
/* 80 */       this.circle.remove(Integer.valueOf(this.hashFunc.hash32(node.toString() + i)));
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T get(Object key) {
/* 90 */     if (this.circle.isEmpty()) {
/* 91 */       return null;
/*    */     }
/* 93 */     int hash = this.hashFunc.hash32(key);
/* 94 */     if (false == this.circle.containsKey(Integer.valueOf(hash))) {
/* 95 */       SortedMap<Integer, T> tailMap = this.circle.tailMap(Integer.valueOf(hash));
/* 96 */       hash = tailMap.isEmpty() ? ((Integer)this.circle.firstKey()).intValue() : ((Integer)tailMap.firstKey()).intValue();
/*    */     } 
/*    */     
/* 99 */     return this.circle.get(Integer.valueOf(hash));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ConsistentHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */