/*    */ package org.wildfly.common.expression;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.List;
/*    */ import org.wildfly.common.function.ExceptionBiConsumer;
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
/*    */ abstract class Node
/*    */ {
/* 31 */   static final Node[] NO_NODES = new Node[0];
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static Node fromList(List<Node> list) {
/* 37 */     if (list == null || list.isEmpty())
/* 38 */       return NULL; 
/* 39 */     if (list.size() == 1) {
/* 40 */       return list.get(0);
/*    */     }
/* 42 */     return new CompositeNode(list);
/*    */   }
/*    */ 
/*    */   
/* 46 */   static final Node NULL = new Node()
/*    */     {
/*    */       <E extends Exception> void emit(ResolveContext<E> context, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {}
/*    */ 
/*    */       
/*    */       void catalog(HashSet<String> strings) {}
/*    */       
/*    */       public String toString() {
/* 54 */         return "<<null>>";
/*    */       }
/*    */     };
/*    */   
/*    */   abstract <E extends Exception> void emit(ResolveContext<E> paramResolveContext, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> paramExceptionBiConsumer) throws E;
/*    */   
/*    */   abstract void catalog(HashSet<String> paramHashSet);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\expression\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */