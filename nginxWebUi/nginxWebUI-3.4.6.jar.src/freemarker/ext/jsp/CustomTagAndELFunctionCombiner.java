/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.core.BugException;
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.core._UnexpectedTypeErrorExplainerTemplateModel;
/*     */ import freemarker.ext.beans.SimpleMethodModel;
/*     */ import freemarker.template.TemplateDirectiveBody;
/*     */ import freemarker.template.TemplateDirectiveModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.TemplateTransformModel;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ class CustomTagAndELFunctionCombiner
/*     */ {
/*     */   static TemplateModel combine(TemplateModel customTag, TemplateMethodModelEx elFunction) {
/*  55 */     if (customTag instanceof TemplateDirectiveModel) {
/*  56 */       return (elFunction instanceof SimpleMethodModel) ? (TemplateModel)new TemplateDirectiveModelAndSimpleMethodModel((TemplateDirectiveModel)customTag, (SimpleMethodModel)elFunction) : (TemplateModel)new TemplateDirectiveModelAndTemplateMethodModelEx((TemplateDirectiveModel)customTag, elFunction);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  61 */     if (customTag instanceof TemplateTransformModel) {
/*  62 */       return (elFunction instanceof SimpleMethodModel) ? (TemplateModel)new TemplateTransformModelAndSimpleMethodModel((TemplateTransformModel)customTag, (SimpleMethodModel)elFunction) : (TemplateModel)new TemplateTransformModelAndTemplateMethodModelEx((TemplateTransformModel)customTag, elFunction);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  68 */     throw new BugException("Unexpected custom JSP tag class: " + 
/*  69 */         ClassUtil.getShortClassNameOfObject(customTag));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean canBeCombinedAsCustomTag(TemplateModel tm) {
/*  78 */     return ((tm instanceof TemplateDirectiveModel || tm instanceof TemplateTransformModel) && !(tm instanceof CombinedTemplateModel));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean canBeCombinedAsELFunction(TemplateModel tm) {
/*  87 */     return (tm instanceof TemplateMethodModelEx && !(tm instanceof CombinedTemplateModel));
/*     */   }
/*     */   
/*     */   private static class CombinedTemplateModel
/*     */   {
/*     */     private CombinedTemplateModel() {}
/*     */   }
/*     */   
/*     */   private static class TemplateDirectiveModelAndSimpleMethodModel
/*     */     extends CombinedTemplateModel
/*     */     implements TemplateDirectiveModel, TemplateMethodModelEx, TemplateSequenceModel, _UnexpectedTypeErrorExplainerTemplateModel
/*     */   {
/*     */     private final TemplateDirectiveModel templateDirectiveModel;
/*     */     private final SimpleMethodModel simpleMethodModel;
/*     */     
/*     */     public TemplateDirectiveModelAndSimpleMethodModel(TemplateDirectiveModel templateDirectiveModel, SimpleMethodModel simpleMethodModel) {
/* 103 */       this.templateDirectiveModel = templateDirectiveModel;
/* 104 */       this.simpleMethodModel = simpleMethodModel;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object exec(List arguments) throws TemplateModelException {
/* 109 */       return this.simpleMethodModel.exec(arguments);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
/* 115 */       this.templateDirectiveModel.execute(env, params, loopVars, body);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object[] explainTypeError(Class[] expectedClasses) {
/* 120 */       return this.simpleMethodModel.explainTypeError(expectedClasses);
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 125 */       return this.simpleMethodModel.get(index);
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 130 */       return this.simpleMethodModel.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class TemplateDirectiveModelAndTemplateMethodModelEx
/*     */     extends CombinedTemplateModel
/*     */     implements TemplateDirectiveModel, TemplateMethodModelEx
/*     */   {
/*     */     private final TemplateDirectiveModel templateDirectiveModel;
/*     */     private final TemplateMethodModelEx templateMethodModelEx;
/*     */     
/*     */     public TemplateDirectiveModelAndTemplateMethodModelEx(TemplateDirectiveModel templateDirectiveModel, TemplateMethodModelEx templateMethodModelEx) {
/* 143 */       this.templateDirectiveModel = templateDirectiveModel;
/* 144 */       this.templateMethodModelEx = templateMethodModelEx;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object exec(List arguments) throws TemplateModelException {
/* 149 */       return this.templateMethodModelEx.exec(arguments);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
/* 155 */       this.templateDirectiveModel.execute(env, params, loopVars, body);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class TemplateTransformModelAndTemplateMethodModelEx
/*     */     extends CombinedTemplateModel
/*     */     implements TemplateTransformModel, TemplateMethodModelEx
/*     */   {
/*     */     private final TemplateTransformModel templateTransformModel;
/*     */     private final TemplateMethodModelEx templateMethodModelEx;
/*     */     
/*     */     public TemplateTransformModelAndTemplateMethodModelEx(TemplateTransformModel templateTransformModel, TemplateMethodModelEx templateMethodModelEx) {
/* 168 */       this.templateTransformModel = templateTransformModel;
/* 169 */       this.templateMethodModelEx = templateMethodModelEx;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object exec(List arguments) throws TemplateModelException {
/* 174 */       return this.templateMethodModelEx.exec(arguments);
/*     */     }
/*     */ 
/*     */     
/*     */     public Writer getWriter(Writer out, Map args) throws TemplateModelException, IOException {
/* 179 */       return this.templateTransformModel.getWriter(out, args);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class TemplateTransformModelAndSimpleMethodModel
/*     */     extends CombinedTemplateModel
/*     */     implements TemplateTransformModel, TemplateMethodModelEx, TemplateSequenceModel, _UnexpectedTypeErrorExplainerTemplateModel
/*     */   {
/*     */     private final TemplateTransformModel templateTransformModel;
/*     */     
/*     */     private final SimpleMethodModel simpleMethodModel;
/*     */     
/*     */     public TemplateTransformModelAndSimpleMethodModel(TemplateTransformModel templateTransformModel, SimpleMethodModel simpleMethodModel) {
/* 193 */       this.templateTransformModel = templateTransformModel;
/* 194 */       this.simpleMethodModel = simpleMethodModel;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object exec(List arguments) throws TemplateModelException {
/* 199 */       return this.simpleMethodModel.exec(arguments);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object[] explainTypeError(Class[] expectedClasses) {
/* 204 */       return this.simpleMethodModel.explainTypeError(expectedClasses);
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(int index) throws TemplateModelException {
/* 209 */       return this.simpleMethodModel.get(index);
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 214 */       return this.simpleMethodModel.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public Writer getWriter(Writer out, Map args) throws TemplateModelException, IOException {
/* 219 */       return this.templateTransformModel.getWriter(out, args);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\CustomTagAndELFunctionCombiner.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */