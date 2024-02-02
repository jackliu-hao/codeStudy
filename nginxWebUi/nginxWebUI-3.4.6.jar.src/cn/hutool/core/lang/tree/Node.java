/*    */ package cn.hutool.core.lang.tree;
/*    */ 
/*    */ import cn.hutool.core.comparator.CompareUtil;
/*    */ import java.io.Serializable;
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
/*    */ 
/*    */ 
/*    */ public interface Node<T>
/*    */   extends Comparable<Node<T>>, Serializable
/*    */ {
/*    */   default int compareTo(Node node) {
/* 79 */     if (null == node) {
/* 80 */       return 1;
/*    */     }
/* 82 */     Comparable<?> weight = getWeight();
/* 83 */     Comparable<?> weightOther = node.getWeight();
/* 84 */     return CompareUtil.compare(weight, weightOther);
/*    */   }
/*    */   
/*    */   Node<T> setWeight(Comparable<?> paramComparable);
/*    */   
/*    */   Comparable<?> getWeight();
/*    */   
/*    */   Node<T> setName(CharSequence paramCharSequence);
/*    */   
/*    */   CharSequence getName();
/*    */   
/*    */   Node<T> setParentId(T paramT);
/*    */   
/*    */   T getParentId();
/*    */   
/*    */   Node<T> setId(T paramT);
/*    */   
/*    */   T getId();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\tree\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */