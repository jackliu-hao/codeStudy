/*    */ package org.wildfly.common.expression;
/*    */ 
/*    */ import java.util.HashSet;
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
/*    */ class ExpressionNode
/*    */   extends Node
/*    */ {
/*    */   private final boolean generalExpression;
/*    */   private final Node key;
/*    */   private final Node defaultValue;
/*    */   
/*    */   ExpressionNode(boolean generalExpression, Node key, Node defaultValue) {
/* 34 */     this.generalExpression = generalExpression;
/* 35 */     this.key = key;
/* 36 */     this.defaultValue = defaultValue;
/*    */   }
/*    */   
/*    */   <E extends Exception> void emit(ResolveContext<E> context, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
/* 40 */     ExpressionNode oldCurrent = context.setCurrent(this);
/*    */     try {
/* 42 */       resolveFunction.accept(context, context.getStringBuilder());
/*    */     } finally {
/* 44 */       context.setCurrent(oldCurrent);
/*    */     } 
/*    */   }
/*    */   
/*    */   void catalog(HashSet<String> strings) {
/* 49 */     if (this.key instanceof LiteralNode) {
/* 50 */       strings.add(this.key.toString());
/*    */     } else {
/* 52 */       this.key.catalog(strings);
/*    */     } 
/* 54 */     this.defaultValue.catalog(strings);
/*    */   }
/*    */   
/*    */   boolean isGeneralExpression() {
/* 58 */     return this.generalExpression;
/*    */   }
/*    */   
/*    */   Node getKey() {
/* 62 */     return this.key;
/*    */   }
/*    */   
/*    */   Node getDefaultValue() {
/* 66 */     return this.defaultValue;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 70 */     return String.format("Expr<%s:%s>", new Object[] { this.key, this.defaultValue });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\expression\ExpressionNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */