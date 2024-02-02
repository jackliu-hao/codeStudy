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
/*    */ final class CompositeNode
/*    */   extends Node
/*    */ {
/*    */   private final Node[] subNodes;
/*    */   
/*    */   CompositeNode(Node[] subNodes) {
/* 33 */     this.subNodes = subNodes;
/*    */   }
/*    */   
/*    */   CompositeNode(List<Node> subNodes) {
/* 37 */     this.subNodes = subNodes.<Node>toArray(NO_NODES);
/*    */   }
/*    */   
/*    */   <E extends Exception> void emit(ResolveContext<E> context, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
/* 41 */     for (Node subNode : this.subNodes) {
/* 42 */       subNode.emit(context, resolveFunction);
/*    */     }
/*    */   }
/*    */   
/*    */   void catalog(HashSet<String> strings) {
/* 47 */     for (Node node : this.subNodes) {
/* 48 */       node.catalog(strings);
/*    */     }
/*    */   }
/*    */   
/*    */   public String toString() {
/* 53 */     StringBuilder b = new StringBuilder();
/* 54 */     b.append('*');
/* 55 */     for (Node subNode : this.subNodes) {
/* 56 */       b.append('<').append(subNode.toString()).append('>');
/*    */     }
/* 58 */     return b.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\expression\CompositeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */