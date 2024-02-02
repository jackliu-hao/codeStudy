/*     */ package org.antlr.v4.runtime.tree;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractParseTreeVisitor<T>
/*     */   implements ParseTreeVisitor<T>
/*     */ {
/*     */   public T visit(ParseTree tree) {
/*  42 */     return tree.accept(this);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T visitChildren(RuleNode node) {
/*  62 */     T result = defaultResult();
/*  63 */     int n = node.getChildCount();
/*  64 */     for (int i = 0; i < n && 
/*  65 */       shouldVisitNextChild(node, result); i++) {
/*     */ 
/*     */ 
/*     */       
/*  69 */       ParseTree c = node.getChild(i);
/*  70 */       T childResult = c.accept(this);
/*  71 */       result = aggregateResult(result, childResult);
/*     */     } 
/*     */     
/*  74 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T visitTerminal(TerminalNode node) {
/*  85 */     return defaultResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T visitErrorNode(ErrorNode node) {
/*  96 */     return defaultResult();
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected T defaultResult() {
/* 111 */     return null;
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
/*     */   protected T aggregateResult(T aggregate, T nextResult) {
/* 134 */     return nextResult;
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
/*     */   protected boolean shouldVisitNextChild(RuleNode node, T currentResult) {
/* 162 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\AbstractParseTreeVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */