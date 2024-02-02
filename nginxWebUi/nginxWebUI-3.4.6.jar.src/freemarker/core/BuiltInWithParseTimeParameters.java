/*     */ package freemarker.core;
/*     */ 
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
/*     */ abstract class BuiltInWithParseTimeParameters
/*     */   extends SpecialBuiltIn
/*     */ {
/*     */   abstract void bindToParameters(List<Expression> paramList, Token paramToken1, Token paramToken2) throws ParseException;
/*     */   
/*     */   public String getCanonicalForm() {
/*  34 */     StringBuilder buf = new StringBuilder();
/*     */     
/*  36 */     buf.append(super.getCanonicalForm());
/*     */     
/*  38 */     buf.append("(");
/*  39 */     List<Expression> args = getArgumentsAsList();
/*  40 */     int size = args.size();
/*  41 */     for (int i = 0; i < size; i++) {
/*  42 */       if (i != 0) {
/*  43 */         buf.append(", ");
/*     */       }
/*  45 */       Expression arg = args.get(i);
/*  46 */       buf.append(arg.getCanonicalForm());
/*     */     } 
/*  48 */     buf.append(")");
/*     */     
/*  50 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  55 */     return super.getNodeTypeSymbol() + "(...)";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  60 */     return super.getParameterCount() + getArgumentsCount();
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  65 */     int superParamCnt = super.getParameterCount();
/*  66 */     if (idx < superParamCnt) {
/*  67 */       return super.getParameterValue(idx);
/*     */     }
/*     */     
/*  70 */     int argIdx = idx - superParamCnt;
/*  71 */     return getArgumentParameterValue(argIdx);
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/*  76 */     int superParamCnt = super.getParameterCount();
/*  77 */     if (idx < superParamCnt) {
/*  78 */       return super.getParameterRole(idx);
/*     */     }
/*     */     
/*  81 */     if (idx - superParamCnt < getArgumentsCount()) {
/*  82 */       return ParameterRole.ARGUMENT_VALUE;
/*     */     }
/*  84 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected final ParseException newArgumentCountException(String ordinalityDesc, Token openParen, Token closeParen) {
/*  89 */     return new ParseException("?" + this.key + "(...) " + ordinalityDesc + " parameters", 
/*  90 */         getTemplate(), openParen.beginLine, openParen.beginColumn, closeParen.endLine, closeParen.endColumn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void checkLocalLambdaParamCount(LocalLambdaExpression localLambdaExp, int expectedParamCount) throws ParseException {
/*  97 */     int actualParamCount = localLambdaExp.getLambdaParameterList().getParameters().size();
/*  98 */     if (actualParamCount != expectedParamCount) {
/*  99 */       throw new ParseException("?" + this.key + "(...) parameter lambda expression must declare exactly " + expectedParamCount + " parameter" + ((expectedParamCount > 1) ? "s" : "") + ", but it declared " + actualParamCount + ".", localLambdaExp);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 110 */     Expression clone = super.deepCloneWithIdentifierReplaced_inner(replacedIdentifier, replacement, replacementState);
/* 111 */     cloneArguments(clone, replacedIdentifier, replacement, replacementState);
/* 112 */     return clone;
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract List<Expression> getArgumentsAsList();
/*     */ 
/*     */   
/*     */   protected abstract int getArgumentsCount();
/*     */ 
/*     */   
/*     */   protected abstract Expression getArgumentParameterValue(int paramInt);
/*     */ 
/*     */   
/*     */   protected abstract void cloneArguments(Expression paramExpression1, String paramString, Expression paramExpression2, Expression.ReplacemenetState paramReplacemenetState);
/*     */   
/*     */   protected boolean isLocalLambdaParameterSupported() {
/* 128 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInWithParseTimeParameters.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */