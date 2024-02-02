/*    */ package cn.hutool.core.lang;
/*    */ 
/*    */ import cn.hutool.core.clone.CloneSupport;
/*    */ import java.io.Serializable;
/*    */ import java.util.Objects;
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
/*    */ public class Pair<K, V>
/*    */   extends CloneSupport<Pair<K, V>>
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected K key;
/*    */   protected V value;
/*    */   
/*    */   public static <K, V> Pair<K, V> of(K key, V value) {
/* 33 */     return new Pair<>(key, value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Pair(K key, V value) {
/* 43 */     this.key = key;
/* 44 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public K getKey() {
/* 53 */     return this.key;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public V getValue() {
/* 62 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return "Pair [key=" + this.key + ", value=" + this.value + "]";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 72 */     if (this == o)
/* 73 */       return true; 
/* 74 */     if (o instanceof Pair) {
/* 75 */       Pair<?, ?> pair = (Pair<?, ?>)o;
/* 76 */       return (Objects.equals(getKey(), pair.getKey()) && 
/* 77 */         Objects.equals(getValue(), pair.getValue()));
/*    */     } 
/* 79 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 85 */     return Objects.hashCode(this.key) ^ Objects.hashCode(this.value);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */