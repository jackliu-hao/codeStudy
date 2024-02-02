/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateMethodModel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MethodCall
/*     */   extends Expression
/*     */ {
/*     */   private final Expression target;
/*     */   private final ListLiteral arguments;
/*     */   
/*     */   MethodCall(Expression target, ArrayList<Expression> arguments) {
/*  45 */     this(target, new ListLiteral(arguments));
/*     */   }
/*     */   
/*     */   private MethodCall(Expression target, ListLiteral arguments) {
/*  49 */     this.target = target;
/*  50 */     this.arguments = arguments;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  55 */     TemplateModel targetModel = this.target.eval(env);
/*  56 */     if (targetModel instanceof TemplateMethodModel) {
/*  57 */       TemplateMethodModel targetMethod = (TemplateMethodModel)targetModel;
/*     */ 
/*     */ 
/*     */       
/*  61 */       List argumentStrings = (targetMethod instanceof freemarker.template.TemplateMethodModelEx) ? this.arguments.getModelList(env) : this.arguments.getValueList(env);
/*  62 */       Object result = targetMethod.exec(argumentStrings);
/*  63 */       return env.getObjectWrapper().wrap(result);
/*  64 */     }  if (targetModel instanceof Macro) {
/*  65 */       return env.invokeFunction(env, (Macro)targetModel, this.arguments.items, this);
/*     */     }
/*  67 */     throw new NonMethodException(this.target, targetModel, true, false, null, env);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/*  73 */     StringBuilder buf = new StringBuilder();
/*  74 */     buf.append(this.target.getCanonicalForm());
/*  75 */     buf.append("(");
/*  76 */     String list = this.arguments.getCanonicalForm();
/*  77 */     buf.append(list.substring(1, list.length() - 1));
/*  78 */     buf.append(")");
/*  79 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  84 */     return "...(...)";
/*     */   }
/*     */   
/*     */   TemplateModel getConstantValue() {
/*  88 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/*  93 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*  99 */     return new MethodCall(this.target
/* 100 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), (ListLiteral)this.arguments
/* 101 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 106 */     return 1 + this.arguments.items.size();
/*     */   }
/*     */   
/*     */   Expression getTarget() {
/* 110 */     return this.target;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 115 */     if (idx == 0)
/* 116 */       return this.target; 
/* 117 */     if (idx < getParameterCount()) {
/* 118 */       return this.arguments.items.get(idx - 1);
/*     */     }
/* 120 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 126 */     if (idx == 0)
/* 127 */       return ParameterRole.CALLEE; 
/* 128 */     if (idx < getParameterCount()) {
/* 129 */       return ParameterRole.ARGUMENT_VALUE;
/*     */     }
/* 131 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\MethodCall.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */