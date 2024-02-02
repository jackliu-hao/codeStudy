/*    */ package org.antlr.v4.runtime.tree;
/*    */ 
/*    */ import java.util.IdentityHashMap;
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
/*    */ public class ParseTreeProperty<V>
/*    */ {
/* 53 */   protected Map<ParseTree, V> annotations = new IdentityHashMap<ParseTree, V>();
/*    */   
/* 55 */   public V get(ParseTree node) { return this.annotations.get(node); }
/* 56 */   public void put(ParseTree node, V value) { this.annotations.put(node, value); } public V removeFrom(ParseTree node) {
/* 57 */     return this.annotations.remove(node);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\ParseTreeProperty.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */