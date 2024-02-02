/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import java.util.List;
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
/*     */ final class LocalLambdaExpression
/*     */   extends Expression
/*     */ {
/*     */   private final LambdaParameterList lho;
/*     */   private final Expression rho;
/*     */   
/*     */   LocalLambdaExpression(LambdaParameterList lho, Expression rho) {
/*  41 */     this.lho = lho;
/*  42 */     this.rho = rho;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/*  47 */     return this.lho.getCanonicalForm() + " -> " + this.rho.getCanonicalForm();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  52 */     return "->";
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  57 */     throw new TemplateException("Can't get lambda expression as a value: Lambdas currently can only be used on a few special places.", env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateModel invokeLambdaDefinedFunction(TemplateModel argValue, Environment env) throws TemplateException {
/*  66 */     return env.evaluateWithNewLocal(this.rho, ((Identifier)this.lho.getParameters().get(0)).getName(), (argValue != null) ? argValue : TemplateNullModel.INSTANCE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/*  73 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*  79 */     for (Identifier parameter : this.lho.getParameters()) {
/*  80 */       if (parameter.getName().equals(replacedIdentifier))
/*     */       {
/*     */         
/*  83 */         throw new UncheckedParseException(new ParseException("Escape placeholder (" + replacedIdentifier + ") can't be used in the parameter list of a lambda expressions.", this));
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  89 */     return new LocalLambdaExpression(this.lho, this.rho
/*     */         
/*  91 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  96 */     return this.lho.getParameters().size() + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 101 */     int paramCount = getParameterCount();
/* 102 */     if (idx < paramCount - 1)
/* 103 */       return this.lho.getParameters().get(idx); 
/* 104 */     if (idx == paramCount - 1) {
/* 105 */       return this.rho;
/*     */     }
/* 107 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 113 */     int paramCount = getParameterCount();
/* 114 */     if (idx < paramCount - 1)
/* 115 */       return ParameterRole.ARGUMENT_NAME; 
/* 116 */     if (idx == paramCount - 1) {
/* 117 */       return ParameterRole.VALUE;
/*     */     }
/* 119 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   LambdaParameterList getLambdaParameterList() {
/* 124 */     return this.lho;
/*     */   }
/*     */   
/*     */   static class LambdaParameterList
/*     */   {
/*     */     private final Token openingParenthesis;
/*     */     private final Token closingParenthesis;
/*     */     private final List<Identifier> parameters;
/*     */     
/*     */     public LambdaParameterList(Token openingParenthesis, List<Identifier> parameters, Token closingParenthesis) {
/* 134 */       this.openingParenthesis = openingParenthesis;
/* 135 */       this.closingParenthesis = closingParenthesis;
/* 136 */       this.parameters = parameters;
/*     */     }
/*     */ 
/*     */     
/*     */     public Token getOpeningParenthesis() {
/* 141 */       return this.openingParenthesis;
/*     */     }
/*     */ 
/*     */     
/*     */     public Token getClosingParenthesis() {
/* 146 */       return this.closingParenthesis;
/*     */     }
/*     */     
/*     */     public List<Identifier> getParameters() {
/* 150 */       return this.parameters;
/*     */     }
/*     */     
/*     */     public String getCanonicalForm() {
/* 154 */       if (this.parameters.size() == 1) {
/* 155 */         return ((Identifier)this.parameters.get(0)).getCanonicalForm();
/*     */       }
/* 157 */       StringBuilder sb = new StringBuilder();
/* 158 */       sb.append('(');
/* 159 */       for (int i = 0; i < this.parameters.size(); i++) {
/* 160 */         if (i != 0) {
/* 161 */           sb.append(", ");
/*     */         }
/* 163 */         Identifier parameter = this.parameters.get(i);
/* 164 */         sb.append(parameter.getCanonicalForm());
/*     */       } 
/* 166 */       sb.append(')');
/* 167 */       return sb.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LocalLambdaExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */