/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import java.io.IOException;
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
/*     */ class EscapeBlock
/*     */   extends TemplateElement
/*     */ {
/*     */   private final String variable;
/*     */   private final Expression expr;
/*     */   private Expression escapedExpr;
/*     */   
/*     */   EscapeBlock(String variable, Expression expr, Expression escapedExpr) {
/*  38 */     this.variable = variable;
/*  39 */     this.expr = expr;
/*  40 */     this.escapedExpr = escapedExpr;
/*     */   }
/*     */   
/*     */   void setContent(TemplateElements children) {
/*  44 */     setChildren(children);
/*     */     
/*  46 */     this.escapedExpr = null;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  51 */     return getChildBuffer();
/*     */   }
/*     */   
/*     */   Expression doEscape(Expression expression) throws ParseException {
/*     */     try {
/*  56 */       return this.escapedExpr.deepCloneWithIdentifierReplaced(this.variable, expression, new Expression.ReplacemenetState());
/*  57 */     } catch (UncheckedParseException e) {
/*  58 */       throw e.getParseException();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  64 */     StringBuilder sb = new StringBuilder();
/*  65 */     if (canonical) sb.append('<'); 
/*  66 */     sb.append(getNodeTypeSymbol())
/*  67 */       .append(' ').append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.variable))
/*  68 */       .append(" as ").append(this.expr.getCanonicalForm());
/*  69 */     if (canonical) {
/*  70 */       sb.append('>');
/*  71 */       sb.append(getChildrenCanonicalForm());
/*  72 */       sb.append("</").append(getNodeTypeSymbol()).append('>');
/*     */     } 
/*  74 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  79 */     return "#escape";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  84 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  89 */     switch (idx) { case 0:
/*  90 */         return this.variable;
/*  91 */       case 1: return this.expr; }
/*  92 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/*  98 */     switch (idx) { case 0:
/*  99 */         return ParameterRole.PLACEHOLDER_VARIABLE;
/* 100 */       case 1: return ParameterRole.EXPRESSION_TEMPLATE; }
/* 101 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isOutputCacheable() {
/* 107 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 112 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\EscapeBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */