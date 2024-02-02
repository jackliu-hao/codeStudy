/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import java.util.ArrayList;
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
/*     */ final class BuiltInsWithLazyConditionals
/*     */ {
/*     */   static class then_BI
/*     */     extends BuiltInWithParseTimeParameters
/*     */   {
/*     */     private Expression whenTrueExp;
/*     */     private Expression whenFalseExp;
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/*  40 */       boolean lho = this.target.evalToBoolean(env);
/*  41 */       return (lho ? this.whenTrueExp : this.whenFalseExp).evalToNonMissing(env);
/*     */     }
/*     */ 
/*     */     
/*     */     void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
/*  46 */       if (parameters.size() != 2) {
/*  47 */         throw newArgumentCountException("requires exactly 2", openParen, closeParen);
/*     */       }
/*  49 */       this.whenTrueExp = parameters.get(0);
/*  50 */       this.whenFalseExp = parameters.get(1);
/*     */     }
/*     */ 
/*     */     
/*     */     protected Expression getArgumentParameterValue(int argIdx) {
/*  55 */       switch (argIdx) { case 0:
/*  56 */           return this.whenTrueExp;
/*  57 */         case 1: return this.whenFalseExp; }
/*  58 */        throw new IndexOutOfBoundsException();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected int getArgumentsCount() {
/*  64 */       return 2;
/*     */     }
/*     */ 
/*     */     
/*     */     protected List<Expression> getArgumentsAsList() {
/*  69 */       ArrayList<Expression> args = new ArrayList<>(2);
/*  70 */       args.add(this.whenTrueExp);
/*  71 */       args.add(this.whenFalseExp);
/*  72 */       return args;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void cloneArguments(Expression cloneExp, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*  78 */       then_BI clone = (then_BI)cloneExp;
/*  79 */       clone.whenTrueExp = this.whenTrueExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
/*  80 */       clone.whenFalseExp = this.whenFalseExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class switch_BI
/*     */     extends BuiltInWithParseTimeParameters
/*     */   {
/*     */     private List<Expression> parameters;
/*     */ 
/*     */ 
/*     */     
/*     */     void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
/*  95 */       if (parameters.size() < 2) {
/*  96 */         throw newArgumentCountException("must have at least 2", openParen, closeParen);
/*     */       }
/*  98 */       this.parameters = parameters;
/*     */     }
/*     */ 
/*     */     
/*     */     protected List<Expression> getArgumentsAsList() {
/* 103 */       return this.parameters;
/*     */     }
/*     */ 
/*     */     
/*     */     protected int getArgumentsCount() {
/* 108 */       return this.parameters.size();
/*     */     }
/*     */ 
/*     */     
/*     */     protected Expression getArgumentParameterValue(int argIdx) {
/* 113 */       return this.parameters.get(argIdx);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void cloneArguments(Expression clone, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 119 */       List<Expression> parametersClone = new ArrayList<>(this.parameters.size());
/* 120 */       for (Expression parameter : this.parameters) {
/* 121 */         parametersClone.add(parameter
/* 122 */             .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*     */       }
/* 124 */       ((switch_BI)clone).parameters = parametersClone;
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 129 */       TemplateModel targetValue = this.target.evalToNonMissing(env);
/*     */       
/* 131 */       List<Expression> parameters = this.parameters;
/* 132 */       int paramCnt = parameters.size();
/* 133 */       for (int i = 0; i + 1 < paramCnt; i += 2) {
/* 134 */         Expression caseExp = parameters.get(i);
/* 135 */         TemplateModel caseValue = caseExp.evalToNonMissing(env);
/* 136 */         if (EvalUtil.compare(targetValue, this.target, 1, "==", caseValue, caseExp, this, true, false, false, false, env))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 143 */           return ((Expression)parameters.get(i + 1)).evalToNonMissing(env);
/*     */         }
/*     */       } 
/*     */       
/* 147 */       if (paramCnt % 2 == 0) {
/* 148 */         throw new _MiscTemplateException(this.target, new Object[] { "The value before ?", this.key, "(case1, value1, case2, value2, ...) didn't match any of the case parameters, and there was no default value parameter (an additional last parameter) eithter. " });
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 153 */       return ((Expression)parameters.get(paramCnt - 1)).evalToNonMissing(env);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsWithLazyConditionals.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */