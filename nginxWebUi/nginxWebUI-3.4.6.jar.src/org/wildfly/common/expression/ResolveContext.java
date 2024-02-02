/*     */ package org.wildfly.common.expression;
/*     */ 
/*     */ import org.wildfly.common.function.ExceptionBiConsumer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ResolveContext<E extends Exception>
/*     */ {
/*     */   private final ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> function;
/*     */   private StringBuilder builder;
/*     */   private ExpressionNode current;
/*     */   
/*     */   ResolveContext(ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> function, StringBuilder builder) {
/*  39 */     this.function = function;
/*  40 */     this.builder = builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getKey() throws E {
/*  52 */     if (this.current == null) throw (E)new IllegalStateException(); 
/*  53 */     Node key = this.current.getKey();
/*  54 */     if (key instanceof LiteralNode)
/*  55 */       return key.toString(); 
/*  56 */     if (key == Node.NULL) {
/*  57 */       return "";
/*     */     }
/*  59 */     StringBuilder b = new StringBuilder();
/*  60 */     emitToBuilder(b, key);
/*  61 */     return b.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void expandDefault(StringBuilder target) throws E {
/*  73 */     if (this.current == null) throw (E)new IllegalStateException(); 
/*  74 */     emitToBuilder(target, this.current.getDefaultValue());
/*     */   }
/*     */   
/*     */   private void emitToBuilder(StringBuilder target, Node node) throws E {
/*  78 */     if (node == Node.NULL)
/*     */       return; 
/*  80 */     if (node instanceof LiteralNode) {
/*  81 */       target.append(node.toString());
/*     */       return;
/*     */     } 
/*  84 */     StringBuilder old = this.builder;
/*     */     try {
/*  86 */       this.builder = target;
/*  87 */       node.emit(this, this.function);
/*     */     } finally {
/*  89 */       this.builder = old;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void expandDefault() throws E {
/* 102 */     expandDefault(this.builder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getExpandedDefault() throws E {
/* 114 */     if (this.current == null) throw (E)new IllegalStateException(); 
/* 115 */     Node defaultValue = this.current.getDefaultValue();
/* 116 */     if (defaultValue instanceof LiteralNode)
/* 117 */       return defaultValue.toString(); 
/* 118 */     if (defaultValue == Node.NULL) {
/* 119 */       return "";
/*     */     }
/* 121 */     StringBuilder b = new StringBuilder();
/* 122 */     emitToBuilder(b, defaultValue);
/* 123 */     return b.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasDefault() {
/* 132 */     return (this.current.getDefaultValue() != Node.NULL);
/*     */   }
/*     */   
/*     */   StringBuilder getStringBuilder() {
/* 136 */     return this.builder;
/*     */   }
/*     */   
/*     */   ExpressionNode setCurrent(ExpressionNode current) {
/*     */     try {
/* 141 */       return this.current;
/*     */     } finally {
/* 143 */       this.current = current;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\expression\ResolveContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */