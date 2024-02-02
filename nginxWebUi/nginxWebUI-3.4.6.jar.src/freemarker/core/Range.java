/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template._TemplateAPI;
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
/*     */ final class Range
/*     */   extends Expression
/*     */ {
/*     */   static final int END_INCLUSIVE = 0;
/*     */   static final int END_EXCLUSIVE = 1;
/*     */   static final int END_UNBOUND = 2;
/*     */   static final int END_SIZE_LIMITED = 3;
/*     */   final Expression lho;
/*     */   final Expression rho;
/*     */   final int endType;
/*     */   
/*     */   Range(Expression lho, Expression rho, int endType) {
/*  41 */     this.lho = lho;
/*  42 */     this.rho = rho;
/*  43 */     this.endType = endType;
/*     */   }
/*     */   
/*     */   int getEndType() {
/*  47 */     return this.endType;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  52 */     int begin = this.lho.evalToNumber(env).intValue();
/*  53 */     if (this.endType != 2) {
/*  54 */       int lhoValue = this.rho.evalToNumber(env).intValue();
/*  55 */       return (TemplateModel)new BoundedRangeModel(begin, (this.endType != 3) ? lhoValue : (begin + lhoValue), (this.endType == 0), (this.endType == 3));
/*     */     } 
/*     */ 
/*     */     
/*  59 */     return (_TemplateAPI.getTemplateLanguageVersionAsInt(this) >= _TemplateAPI.VERSION_INT_2_3_21) ? (TemplateModel)new ListableRightUnboundedRangeModel(begin) : (TemplateModel)new NonListableRightUnboundedRangeModel(begin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean evalToBoolean(Environment env) throws TemplateException {
/*  68 */     throw new NonBooleanException(this, new BoundedRangeModel(0, 0, false, false), env);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/*  73 */     String rhs = (this.rho != null) ? this.rho.getCanonicalForm() : "";
/*  74 */     return this.lho.getCanonicalForm() + getNodeTypeSymbol() + rhs;
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  79 */     switch (this.endType) { case 1:
/*  80 */         return "..<";
/*  81 */       case 0: return "..";
/*  82 */       case 2: return "..";
/*  83 */       case 3: return "..*"; }
/*  84 */      throw new BugException(this.endType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/*  90 */     boolean rightIsLiteral = (this.rho == null || this.rho.isLiteral());
/*  91 */     return (this.constantValue != null || (this.lho.isLiteral() && rightIsLiteral));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*  97 */     return new Range(this.lho
/*  98 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho
/*  99 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.endType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 105 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 110 */     switch (idx) { case 0:
/* 111 */         return this.lho;
/* 112 */       case 1: return this.rho; }
/* 113 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 119 */     return ParameterRole.forBinaryOperatorOperand(idx);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Range.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */