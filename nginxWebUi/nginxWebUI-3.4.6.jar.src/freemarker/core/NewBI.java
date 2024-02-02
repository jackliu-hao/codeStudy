/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.ext.beans.BeanModel;
/*    */ import freemarker.ext.beans.BeansWrapper;
/*    */ import freemarker.template.ObjectWrapper;
/*    */ import freemarker.template.Template;
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateMethodModelEx;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class NewBI
/*    */   extends BuiltIn
/*    */ {
/*    */   static Class<?> JYTHON_MODEL_CLASS;
/*    */   
/*    */   static {
/*    */     try {
/* 42 */       JYTHON_MODEL_CLASS = Class.forName("freemarker.ext.jython.JythonModel");
/* 43 */     } catch (Throwable e) {
/* 44 */       JYTHON_MODEL_CLASS = null;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   TemplateModel _eval(Environment env) throws TemplateException {
/* 51 */     return (TemplateModel)new ConstructorFunction(this.target.evalAndCoerceToPlainText(env), env, this.target.getTemplate());
/*    */   }
/*    */   
/*    */   class ConstructorFunction
/*    */     implements TemplateMethodModelEx {
/*    */     private final Class<?> cl;
/*    */     private final Environment env;
/*    */     
/*    */     public ConstructorFunction(String classname, Environment env, Template template) throws TemplateException {
/* 60 */       this.env = env;
/* 61 */       this.cl = env.getNewBuiltinClassResolver().resolve(classname, env, template);
/* 62 */       if (!TemplateModel.class.isAssignableFrom(this.cl)) {
/* 63 */         throw new _MiscTemplateException(NewBI.this, env, new Object[] { "Class ", this.cl
/* 64 */               .getName(), " does not implement freemarker.template.TemplateModel" });
/*    */       }
/* 66 */       if (BeanModel.class.isAssignableFrom(this.cl)) {
/* 67 */         throw new _MiscTemplateException(NewBI.this, env, new Object[] { "Bean Models cannot be instantiated using the ?", this$0.key, " built-in" });
/*    */       }
/*    */       
/* 70 */       if (NewBI.JYTHON_MODEL_CLASS != null && NewBI.JYTHON_MODEL_CLASS.isAssignableFrom(this.cl)) {
/* 71 */         throw new _MiscTemplateException(NewBI.this, env, new Object[] { "Jython Models cannot be instantiated using the ?", this$0.key, " built-in" });
/*    */       }
/*    */     }
/*    */ 
/*    */ 
/*    */     
/*    */     public Object exec(List arguments) throws TemplateModelException {
/* 78 */       ObjectWrapper ow = this.env.getObjectWrapper();
/*    */ 
/*    */ 
/*    */       
/* 82 */       BeansWrapper bw = (ow instanceof BeansWrapper) ? (BeansWrapper)ow : BeansWrapper.getDefaultInstance();
/* 83 */       return bw.newInstance(this.cl, arguments);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NewBI.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */