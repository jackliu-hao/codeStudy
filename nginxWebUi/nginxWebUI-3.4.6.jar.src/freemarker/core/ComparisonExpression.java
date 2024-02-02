/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
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
/*     */ final class ComparisonExpression
/*     */   extends BooleanExpression
/*     */ {
/*     */   private final Expression left;
/*     */   private final Expression right;
/*     */   private final int operation;
/*     */   private final String opString;
/*     */   
/*     */   ComparisonExpression(Expression left, Expression right, String opString) {
/*  35 */     this.left = left;
/*  36 */     this.right = right;
/*  37 */     opString = opString.intern();
/*  38 */     this.opString = opString;
/*  39 */     if (opString == "==" || opString == "=") {
/*  40 */       this.operation = 1;
/*  41 */     } else if (opString == "!=") {
/*  42 */       this.operation = 2;
/*  43 */     } else if (opString == "gt" || opString == "\\gt" || opString == ">" || opString == "&gt;") {
/*  44 */       this.operation = 4;
/*  45 */     } else if (opString == "gte" || opString == "\\gte" || opString == ">=" || opString == "&gt;=") {
/*  46 */       this.operation = 6;
/*  47 */     } else if (opString == "lt" || opString == "\\lt" || opString == "<" || opString == "&lt;") {
/*  48 */       this.operation = 3;
/*  49 */     } else if (opString == "lte" || opString == "\\lte" || opString == "<=" || opString == "&lt;=") {
/*  50 */       this.operation = 5;
/*     */     } else {
/*  52 */       throw new BugException("Unknown comparison operator " + opString);
/*     */     } 
/*     */     
/*  55 */     Expression cleanedLeft = MiscUtil.peelParentheses(left);
/*  56 */     Expression cleanedRight = MiscUtil.peelParentheses(right);
/*  57 */     if (cleanedLeft instanceof BuiltInsForMultipleTypes.sizeBI) {
/*  58 */       if (cleanedRight instanceof NumberLiteral) {
/*  59 */         ((BuiltInsForMultipleTypes.sizeBI)cleanedLeft).setCountingLimit(this.operation, (NumberLiteral)cleanedRight);
/*     */       
/*     */       }
/*     */     }
/*  63 */     else if (cleanedRight instanceof BuiltInsForMultipleTypes.sizeBI && 
/*  64 */       cleanedLeft instanceof NumberLiteral) {
/*  65 */       ((BuiltInsForMultipleTypes.sizeBI)cleanedRight).setCountingLimit(
/*  66 */           EvalUtil.mirrorCmpOperator(this.operation), (NumberLiteral)cleanedLeft);
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
/*     */   boolean evalToBoolean(Environment env) throws TemplateException {
/*  78 */     return EvalUtil.compare(this.left, this.operation, this.opString, this.right, this, env);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/*  83 */     return this.left.getCanonicalForm() + ' ' + this.opString + ' ' + this.right.getCanonicalForm();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  88 */     return this.opString;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/*  93 */     return (this.constantValue != null || (this.left.isLiteral() && this.right.isLiteral()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*  99 */     return new ComparisonExpression(this.left
/* 100 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.right
/* 101 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.opString);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 107 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 112 */     return (idx == 0) ? this.left : this.right;
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 117 */     return ParameterRole.forBinaryOperatorOperand(idx);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ComparisonExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */