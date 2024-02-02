/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.Template;
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.utility.ClassUtil;
/*    */ import freemarker.template.utility.Execute;
/*    */ import freemarker.template.utility.ObjectConstructor;
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
/*    */ public interface TemplateClassResolver
/*    */ {
/* 45 */   public static final TemplateClassResolver UNRESTRICTED_RESOLVER = new TemplateClassResolver()
/*    */     {
/*    */       
/*    */       public Class resolve(String className, Environment env, Template template) throws TemplateException
/*    */       {
/*    */         try {
/* 51 */           return ClassUtil.forName(className);
/* 52 */         } catch (ClassNotFoundException e) {
/* 53 */           throw new _MiscTemplateException(e, env);
/*    */         } 
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 63 */   public static final TemplateClassResolver SAFER_RESOLVER = new TemplateClassResolver()
/*    */     {
/*    */       
/*    */       public Class resolve(String className, Environment env, Template template) throws TemplateException
/*    */       {
/* 68 */         if (className.equals(ObjectConstructor.class.getName()) || className
/* 69 */           .equals(Execute.class.getName()) || className
/* 70 */           .equals("freemarker.template.utility.JythonRuntime")) {
/* 71 */           throw _MessageUtil.newInstantiatingClassNotAllowedException(className, env);
/*    */         }
/*    */         try {
/* 74 */           return ClassUtil.forName(className);
/* 75 */         } catch (ClassNotFoundException e) {
/* 76 */           throw new _MiscTemplateException(e, env);
/*    */         } 
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 85 */   public static final TemplateClassResolver ALLOWS_NOTHING_RESOLVER = new TemplateClassResolver()
/*    */     {
/*    */       
/*    */       public Class resolve(String className, Environment env, Template template) throws TemplateException
/*    */       {
/* 90 */         throw _MessageUtil.newInstantiatingClassNotAllowedException(className, env);
/*    */       }
/*    */     };
/*    */   
/*    */   Class resolve(String paramString, Environment paramEnvironment, Template paramTemplate) throws TemplateException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateClassResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */