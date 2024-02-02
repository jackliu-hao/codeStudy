/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.util.Collections;
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
/*     */ abstract class IntermediateStreamOperationLikeBuiltIn
/*     */   extends BuiltInWithParseTimeParameters
/*     */ {
/*     */   private Expression elementTransformerExp;
/*     */   private ElementTransformer precreatedElementTransformer;
/*     */   private boolean lazilyGeneratedResultEnabled;
/*     */   
/*     */   void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
/*  48 */     if (parameters.size() != 1) {
/*  49 */       throw newArgumentCountException("requires exactly 1", openParen, closeParen);
/*     */     }
/*  51 */     Expression elementTransformerExp = parameters.get(0);
/*  52 */     setElementTransformerExp(elementTransformerExp);
/*     */   }
/*     */   
/*     */   private void setElementTransformerExp(Expression elementTransformerExp) throws ParseException {
/*  56 */     this.elementTransformerExp = elementTransformerExp;
/*  57 */     if (this.elementTransformerExp instanceof LocalLambdaExpression) {
/*  58 */       LocalLambdaExpression localLambdaExp = (LocalLambdaExpression)this.elementTransformerExp;
/*  59 */       checkLocalLambdaParamCount(localLambdaExp, 1);
/*     */ 
/*     */       
/*  62 */       this.precreatedElementTransformer = new LocalLambdaElementTransformer(localLambdaExp);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected final boolean isLocalLambdaParameterSupported() {
/*  68 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   final void enableLazilyGeneratedResult() {
/*  73 */     this.lazilyGeneratedResultEnabled = true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final boolean isLazilyGeneratedResultEnabled() {
/*  78 */     return this.lazilyGeneratedResultEnabled;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setTarget(Expression target) {
/*  83 */     super.setTarget(target);
/*  84 */     target.enableLazilyGeneratedResult();
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<Expression> getArgumentsAsList() {
/*  89 */     return Collections.singletonList(this.elementTransformerExp);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getArgumentsCount() {
/*  94 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Expression getArgumentParameterValue(int argIdx) {
/*  99 */     if (argIdx != 0) {
/* 100 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 102 */     return this.elementTransformerExp;
/*     */   }
/*     */   
/*     */   protected Expression getElementTransformerExp() {
/* 106 */     return this.elementTransformerExp;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void cloneArguments(Expression clone, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*     */     try {
/* 113 */       ((IntermediateStreamOperationLikeBuiltIn)clone).setElementTransformerExp(this.elementTransformerExp
/* 114 */           .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*     */     }
/* 116 */     catch (ParseException e) {
/* 117 */       throw new BugException("Deep-clone elementTransformerExp failed", e);
/*     */     } 
/*     */   }
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*     */     TemplateModelIterator targetIterator;
/*     */     boolean targetIsSequence;
/* 123 */     TemplateModel targetValue = this.target.eval(env);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 128 */     if (targetValue instanceof TemplateCollectionModel) {
/*     */ 
/*     */       
/* 131 */       targetIterator = isLazilyGeneratedResultEnabled() ? new LazyCollectionTemplateModelIterator((TemplateCollectionModel)targetValue) : ((TemplateCollectionModel)targetValue).iterator();
/*     */       
/* 133 */       targetIsSequence = (targetValue instanceof LazilyGeneratedCollectionModel) ? ((LazilyGeneratedCollectionModel)targetValue).isSequence() : (targetValue instanceof TemplateSequenceModel);
/*     */     }
/* 135 */     else if (targetValue instanceof TemplateSequenceModel) {
/* 136 */       targetIterator = new LazySequenceIterator((TemplateSequenceModel)targetValue);
/* 137 */       targetIsSequence = true;
/*     */     } else {
/* 139 */       throw new NonSequenceOrCollectionException(this.target, targetValue, env);
/*     */     } 
/*     */ 
/*     */     
/* 143 */     return calculateResult(targetIterator, targetValue, targetIsSequence, 
/*     */         
/* 145 */         evalElementTransformerExp(env), env);
/*     */   }
/*     */ 
/*     */   
/*     */   private ElementTransformer evalElementTransformerExp(Environment env) throws TemplateException {
/* 150 */     if (this.precreatedElementTransformer != null) {
/* 151 */       return this.precreatedElementTransformer;
/*     */     }
/*     */     
/* 154 */     TemplateModel elementTransformerModel = this.elementTransformerExp.eval(env);
/* 155 */     if (elementTransformerModel instanceof TemplateMethodModel)
/* 156 */       return new MethodElementTransformer((TemplateMethodModel)elementTransformerModel); 
/* 157 */     if (elementTransformerModel instanceof Macro) {
/* 158 */       return new FunctionElementTransformer((Macro)elementTransformerModel, this.elementTransformerExp);
/*     */     }
/* 160 */     throw new NonMethodException(this.elementTransformerExp, elementTransformerModel, true, true, null, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract TemplateModel calculateResult(TemplateModelIterator paramTemplateModelIterator, TemplateModel paramTemplateModel, boolean paramBoolean, ElementTransformer paramElementTransformer, Environment paramEnvironment) throws TemplateException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static interface ElementTransformer
/*     */   {
/*     */     TemplateModel transformElement(TemplateModel param1TemplateModel, Environment param1Environment) throws TemplateException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class LocalLambdaElementTransformer
/*     */     implements ElementTransformer
/*     */   {
/*     */     private final LocalLambdaExpression elementTransformerExp;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public LocalLambdaElementTransformer(LocalLambdaExpression elementTransformerExp) {
/* 191 */       this.elementTransformerExp = elementTransformerExp;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel transformElement(TemplateModel element, Environment env) throws TemplateException {
/* 196 */       return this.elementTransformerExp.invokeLambdaDefinedFunction(element, env);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class MethodElementTransformer
/*     */     implements ElementTransformer {
/*     */     private final TemplateMethodModel elementTransformer;
/*     */     
/*     */     public MethodElementTransformer(TemplateMethodModel elementTransformer) {
/* 205 */       this.elementTransformer = elementTransformer;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateModel transformElement(TemplateModel element, Environment env) throws TemplateModelException {
/* 211 */       Object result = this.elementTransformer.exec(Collections.singletonList(element));
/* 212 */       return (result instanceof TemplateModel) ? (TemplateModel)result : env.getObjectWrapper().wrap(result);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class FunctionElementTransformer
/*     */     implements ElementTransformer {
/*     */     private final Macro templateTransformer;
/*     */     private final Expression elementTransformerExp;
/*     */     
/*     */     public FunctionElementTransformer(Macro templateTransformer, Expression elementTransformerExp) {
/* 222 */       this.templateTransformer = templateTransformer;
/* 223 */       this.elementTransformerExp = elementTransformerExp;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateModel transformElement(TemplateModel element, Environment env) throws TemplateException {
/* 231 */       ExpressionWithFixedResult functionArgExp = new ExpressionWithFixedResult(element, this.elementTransformerExp);
/*     */       
/* 233 */       return env.invokeFunction(env, this.templateTransformer, 
/* 234 */           Collections.singletonList(functionArgExp), this.elementTransformerExp);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\IntermediateStreamOperationLikeBuiltIn.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */