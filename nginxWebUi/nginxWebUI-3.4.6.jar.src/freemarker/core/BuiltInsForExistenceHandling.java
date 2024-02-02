/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
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
/*     */ class BuiltInsForExistenceHandling
/*     */ {
/*     */   private static abstract class ExistenceBuiltIn
/*     */     extends BuiltIn
/*     */   {
/*     */     private ExistenceBuiltIn() {}
/*     */     
/*     */     protected TemplateModel evalMaybeNonexistentTarget(Environment env) throws TemplateException {
/*     */       TemplateModel tm;
/*  42 */       if (this.target instanceof ParentheticalExpression) {
/*  43 */         boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);
/*     */         try {
/*  45 */           tm = this.target.eval(env);
/*  46 */         } catch (InvalidReferenceException ire) {
/*  47 */           tm = null;
/*     */         } finally {
/*  49 */           env.setFastInvalidReferenceExceptions(lastFIRE);
/*     */         } 
/*     */       } else {
/*  52 */         tm = this.target.eval(env);
/*     */       } 
/*  54 */       return tm;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class defaultBI
/*     */     extends ExistenceBuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/*  63 */       TemplateModel model = evalMaybeNonexistentTarget(env);
/*  64 */       return (model == null) ? (TemplateModel)FIRST_NON_NULL_METHOD : (TemplateModel)new ConstantMethod(model);
/*     */     }
/*     */     
/*     */     private static class ConstantMethod implements TemplateMethodModelEx {
/*     */       private final TemplateModel constant;
/*     */       
/*     */       ConstantMethod(TemplateModel constant) {
/*  71 */         this.constant = constant;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) {
/*  76 */         return this.constant;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     private static final TemplateMethodModelEx FIRST_NON_NULL_METHOD = new TemplateMethodModelEx()
/*     */       {
/*     */         public Object exec(List<TemplateModel> args) throws TemplateModelException
/*     */         {
/*  88 */           int argCnt = args.size();
/*  89 */           if (argCnt == 0) throw _MessageUtil.newArgCntError("?default", argCnt, 1, 2147483647); 
/*  90 */           for (int i = 0; i < argCnt; i++) {
/*  91 */             TemplateModel result = args.get(i);
/*  92 */             if (result != null) return result; 
/*     */           } 
/*  94 */           return null;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   static class existsBI
/*     */     extends ExistenceBuiltIn {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 102 */       return (evalMaybeNonexistentTarget(env) == null) ? (TemplateModel)TemplateBooleanModel.FALSE : (TemplateModel)TemplateBooleanModel.TRUE;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean evalToBoolean(Environment env) throws TemplateException {
/* 107 */       return (_eval(env) == TemplateBooleanModel.TRUE);
/*     */     }
/*     */   }
/*     */   
/*     */   static class has_contentBI
/*     */     extends ExistenceBuiltIn {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 114 */       return Expression.isEmpty(evalMaybeNonexistentTarget(env)) ? (TemplateModel)TemplateBooleanModel.FALSE : (TemplateModel)TemplateBooleanModel.TRUE;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     boolean evalToBoolean(Environment env) throws TemplateException {
/* 121 */       return (_eval(env) == TemplateBooleanModel.TRUE);
/*     */     }
/*     */   }
/*     */   
/*     */   static class if_existsBI
/*     */     extends ExistenceBuiltIn
/*     */   {
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/* 129 */       TemplateModel model = evalMaybeNonexistentTarget(env);
/* 130 */       return (model == null) ? TemplateModel.NOTHING : model;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForExistenceHandling.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */