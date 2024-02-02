/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateNumberModel;
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
/*     */ final class UnaryPlusMinusExpression
/*     */   extends Expression
/*     */ {
/*     */   private static final int TYPE_MINUS = 0;
/*     */   private static final int TYPE_PLUS = 1;
/*     */   private final Expression target;
/*     */   private final boolean isMinus;
/*  34 */   private static final Integer MINUS_ONE = Integer.valueOf(-1);
/*     */   
/*     */   UnaryPlusMinusExpression(Expression target, boolean isMinus) {
/*  37 */     this.target = target;
/*  38 */     this.isMinus = isMinus;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  43 */     TemplateNumberModel targetModel = null;
/*  44 */     TemplateModel tm = this.target.eval(env);
/*     */     try {
/*  46 */       targetModel = (TemplateNumberModel)tm;
/*  47 */     } catch (ClassCastException cce) {
/*  48 */       throw new NonNumericalException(this.target, tm, env);
/*     */     } 
/*  50 */     if (!this.isMinus) {
/*  51 */       return (TemplateModel)targetModel;
/*     */     }
/*  53 */     this.target.assertNonNull((TemplateModel)targetModel, env);
/*  54 */     Number n = targetModel.getAsNumber();
/*  55 */     n = ArithmeticEngine.CONSERVATIVE_ENGINE.multiply(MINUS_ONE, n);
/*  56 */     return (TemplateModel)new SimpleNumber(n);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/*  61 */     String op = this.isMinus ? "-" : "+";
/*  62 */     return op + this.target.getCanonicalForm();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  67 */     return this.isMinus ? "-..." : "+...";
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/*  72 */     return this.target.isLiteral();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*  78 */     return new UnaryPlusMinusExpression(this.target
/*  79 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.isMinus);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  85 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  90 */     switch (idx) { case 0:
/*  91 */         return this.target;
/*  92 */       case 1: return Integer.valueOf(this.isMinus ? 0 : 1); }
/*  93 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/*  99 */     switch (idx) { case 0:
/* 100 */         return ParameterRole.RIGHT_HAND_OPERAND;
/* 101 */       case 1: return ParameterRole.AST_NODE_SUBTYPE; }
/* 102 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\UnaryPlusMinusExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */