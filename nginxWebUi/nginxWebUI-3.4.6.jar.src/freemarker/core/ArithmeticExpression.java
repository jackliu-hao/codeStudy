/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
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
/*     */ final class ArithmeticExpression
/*     */   extends Expression
/*     */ {
/*     */   static final int TYPE_SUBSTRACTION = 0;
/*     */   static final int TYPE_MULTIPLICATION = 1;
/*     */   static final int TYPE_DIVISION = 2;
/*     */   static final int TYPE_MODULO = 3;
/*  37 */   private static final char[] OPERATOR_IMAGES = new char[] { '-', '*', '/', '%' };
/*     */   
/*     */   private final Expression lho;
/*     */   private final Expression rho;
/*     */   private final int operator;
/*     */   
/*     */   ArithmeticExpression(Expression lho, Expression rho, int operator) {
/*  44 */     this.lho = lho;
/*  45 */     this.rho = rho;
/*  46 */     this.operator = operator;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  51 */     return _eval(env, this, this.lho.evalToNumber(env), this.operator, this.rho.evalToNumber(env));
/*     */   }
/*     */ 
/*     */   
/*     */   static TemplateModel _eval(Environment env, TemplateObject parent, Number lhoNumber, int operator, Number rhoNumber) throws TemplateException, _MiscTemplateException {
/*  56 */     ArithmeticEngine ae = EvalUtil.getArithmeticEngine(env, parent);
/*     */     try {
/*  58 */       switch (operator) {
/*     */         case 0:
/*  60 */           return (TemplateModel)new SimpleNumber(ae.subtract(lhoNumber, rhoNumber));
/*     */         case 1:
/*  62 */           return (TemplateModel)new SimpleNumber(ae.multiply(lhoNumber, rhoNumber));
/*     */         case 2:
/*  64 */           return (TemplateModel)new SimpleNumber(ae.divide(lhoNumber, rhoNumber));
/*     */         case 3:
/*  66 */           return (TemplateModel)new SimpleNumber(ae.modulus(lhoNumber, rhoNumber));
/*     */       } 
/*  68 */       if (parent instanceof Expression) {
/*  69 */         throw new _MiscTemplateException((Expression)parent, new Object[] { "Unknown operation: ", 
/*  70 */               Integer.valueOf(operator) });
/*     */       }
/*  72 */       throw new _MiscTemplateException(new Object[] { "Unknown operation: ", Integer.valueOf(operator) });
/*     */     
/*     */     }
/*  75 */     catch (ArithmeticException e) {
/*  76 */       (new Object[2])[0] = "Arithmetic operation failed";
/*     */       
/*  78 */       (new String[2])[0] = ": "; (new String[2])[1] = e.getMessage(); throw new _MiscTemplateException(e, env, new Object[] { null, (e.getMessage() != null) ? new String[2] : " (see cause exception)" });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/*  84 */     return this.lho.getCanonicalForm() + ' ' + getOperatorSymbol(this.operator) + ' ' + this.rho.getCanonicalForm();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  89 */     return String.valueOf(getOperatorSymbol(this.operator));
/*     */   }
/*     */   
/*     */   static char getOperatorSymbol(int operator) {
/*  93 */     return OPERATOR_IMAGES[operator];
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/*  98 */     return (this.constantValue != null || (this.lho.isLiteral() && this.rho.isLiteral()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 104 */     return new ArithmeticExpression(this.lho
/* 105 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho
/* 106 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.operator);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 112 */     return 3;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 117 */     switch (idx) { case 0:
/* 118 */         return this.lho;
/* 119 */       case 1: return this.rho;
/* 120 */       case 2: return Integer.valueOf(this.operator); }
/* 121 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 127 */     switch (idx) { case 0:
/* 128 */         return ParameterRole.LEFT_HAND_OPERAND;
/* 129 */       case 1: return ParameterRole.RIGHT_HAND_OPERAND;
/* 130 */       case 2: return ParameterRole.AST_NODE_SUBTYPE; }
/* 131 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ArithmeticExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */