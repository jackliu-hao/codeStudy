/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.ext.beans.BeanModel;
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.utility.UndeclaredThrowableException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class Expression
/*     */   extends TemplateObject
/*     */ {
/*     */   TemplateModel constantValue;
/*     */   
/*     */   abstract TemplateModel _eval(Environment paramEnvironment) throws TemplateException;
/*     */   
/*     */   abstract boolean isLiteral();
/*     */   
/*     */   final void setLocation(Template template, int beginColumn, int beginLine, int endColumn, int endLine) {
/*  63 */     super.setLocation(template, beginColumn, beginLine, endColumn, endLine);
/*  64 */     if (isLiteral()) {
/*     */       try {
/*  66 */         this.constantValue = _eval((Environment)null);
/*  67 */       } catch (Exception exception) {}
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final TemplateModel getAsTemplateModel(Environment env) throws TemplateException {
/*  78 */     return eval(env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void enableLazilyGeneratedResult() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final TemplateModel eval(Environment env) throws TemplateException {
/*     */     try {
/* 101 */       return (this.constantValue != null) ? this.constantValue : _eval(env);
/* 102 */     } catch (FlowControlException|TemplateException e) {
/* 103 */       throw e;
/* 104 */     } catch (Exception e) {
/* 105 */       if (env != null && EvalUtil.shouldWrapUncheckedException(e, env)) {
/* 106 */         throw new _MiscTemplateException(this, e, env, "Expression has thrown an unchecked exception; see the cause exception.");
/*     */       }
/* 108 */       if (e instanceof RuntimeException) {
/* 109 */         throw (RuntimeException)e;
/*     */       }
/* 111 */       throw new UndeclaredThrowableException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   String evalAndCoerceToPlainText(Environment env) throws TemplateException {
/* 117 */     return EvalUtil.coerceModelToPlainText(eval(env), this, null, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String evalAndCoerceToPlainText(Environment env, String seqTip) throws TemplateException {
/* 124 */     return EvalUtil.coerceModelToPlainText(eval(env), this, seqTip, env);
/*     */   }
/*     */   
/*     */   Object evalAndCoerceToStringOrMarkup(Environment env) throws TemplateException {
/* 128 */     return EvalUtil.coerceModelToStringOrMarkup(eval(env), this, null, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object evalAndCoerceToStringOrMarkup(Environment env, String seqTip) throws TemplateException {
/* 135 */     return EvalUtil.coerceModelToStringOrMarkup(eval(env), this, seqTip, env);
/*     */   }
/*     */   
/*     */   String evalAndCoerceToStringOrUnsupportedMarkup(Environment env) throws TemplateException {
/* 139 */     return EvalUtil.coerceModelToStringOrUnsupportedMarkup(eval(env), this, null, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String evalAndCoerceToStringOrUnsupportedMarkup(Environment env, String seqTip) throws TemplateException {
/* 146 */     return EvalUtil.coerceModelToStringOrUnsupportedMarkup(eval(env), this, seqTip, env);
/*     */   }
/*     */   
/*     */   Number evalToNumber(Environment env) throws TemplateException {
/* 150 */     TemplateModel model = eval(env);
/* 151 */     return modelToNumber(model, env);
/*     */   }
/*     */   
/*     */   final Number modelToNumber(TemplateModel model, Environment env) throws TemplateException {
/* 155 */     if (model instanceof TemplateNumberModel) {
/* 156 */       return EvalUtil.modelToNumber((TemplateNumberModel)model, this);
/*     */     }
/* 158 */     throw new NonNumericalException(this, model, env);
/*     */   }
/*     */ 
/*     */   
/*     */   boolean evalToBoolean(Environment env) throws TemplateException {
/* 163 */     return evalToBoolean(env, (Configuration)null);
/*     */   }
/*     */   
/*     */   boolean evalToBoolean(Configuration cfg) throws TemplateException {
/* 167 */     return evalToBoolean((Environment)null, cfg);
/*     */   }
/*     */   
/*     */   final TemplateModel evalToNonMissing(Environment env) throws TemplateException {
/* 171 */     TemplateModel result = eval(env);
/* 172 */     assertNonNull(result, env);
/* 173 */     return result;
/*     */   }
/*     */   
/*     */   private boolean evalToBoolean(Environment env, Configuration cfg) throws TemplateException {
/* 177 */     TemplateModel model = eval(env);
/* 178 */     return modelToBoolean(model, env, cfg);
/*     */   }
/*     */   
/*     */   final boolean modelToBoolean(TemplateModel model, Environment env) throws TemplateException {
/* 182 */     return modelToBoolean(model, env, (Configuration)null);
/*     */   }
/*     */   
/*     */   final boolean modelToBoolean(TemplateModel model, Configuration cfg) throws TemplateException {
/* 186 */     return modelToBoolean(model, (Environment)null, cfg);
/*     */   }
/*     */   
/*     */   private boolean modelToBoolean(TemplateModel model, Environment env, Configuration cfg) throws TemplateException {
/* 190 */     if (model instanceof TemplateBooleanModel)
/* 191 */       return ((TemplateBooleanModel)model).getAsBoolean(); 
/* 192 */     if ((env != null) ? env.isClassicCompatible() : cfg.isClassicCompatible()) {
/* 193 */       return (model != null && !isEmpty(model));
/*     */     }
/* 195 */     throw new NonBooleanException(this, model, env);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final Expression deepCloneWithIdentifierReplaced(String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
/* 201 */     Expression clone = deepCloneWithIdentifierReplaced_inner(replacedIdentifier, replacement, replacementState);
/* 202 */     if (clone.beginLine == 0) {
/* 203 */       clone.copyLocationFrom(this);
/*     */     }
/* 205 */     return clone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Expression deepCloneWithIdentifierReplaced_inner(String paramString, Expression paramExpression, ReplacemenetState paramReplacemenetState);
/*     */ 
/*     */ 
/*     */   
/*     */   static class ReplacemenetState
/*     */   {
/*     */     boolean replacementAlreadyInUse;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean isEmpty(TemplateModel model) throws TemplateModelException {
/* 223 */     if (model instanceof BeanModel)
/* 224 */       return ((BeanModel)model).isEmpty(); 
/* 225 */     if (model instanceof TemplateSequenceModel)
/* 226 */       return (((TemplateSequenceModel)model).size() == 0); 
/* 227 */     if (model instanceof TemplateScalarModel) {
/* 228 */       String s = ((TemplateScalarModel)model).getAsString();
/* 229 */       return (s == null || s.length() == 0);
/* 230 */     }  if (model == null)
/* 231 */       return true; 
/* 232 */     if (model instanceof TemplateMarkupOutputModel) {
/* 233 */       TemplateMarkupOutputModel<TemplateMarkupOutputModel> mo = (TemplateMarkupOutputModel)model;
/* 234 */       return mo.getOutputFormat().isEmpty(mo);
/* 235 */     }  if (model instanceof TemplateCollectionModel)
/* 236 */       return !((TemplateCollectionModel)model).iterator().hasNext(); 
/* 237 */     if (model instanceof TemplateHashModel)
/* 238 */       return ((TemplateHashModel)model).isEmpty(); 
/* 239 */     if (model instanceof TemplateNumberModel || model instanceof freemarker.template.TemplateDateModel || model instanceof TemplateBooleanModel)
/*     */     {
/*     */       
/* 242 */       return false;
/*     */     }
/* 244 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   final void assertNonNull(TemplateModel model, Environment env) throws InvalidReferenceException {
/* 249 */     if (model == null) throw InvalidReferenceException.getInstance(this, env); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Expression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */