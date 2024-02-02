/*    */ package freemarker.cache;
/*    */ 
/*    */ import freemarker.template.Configuration;
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
/*    */ final class TemplateLoaderUtils
/*    */ {
/*    */   public static String getClassNameForToString(TemplateLoader templateLoader) {
/* 31 */     Class<?> tlClass = templateLoader.getClass();
/* 32 */     Package tlPackage = tlClass.getPackage();
/* 33 */     return (tlPackage == Configuration.class.getPackage() || tlPackage == TemplateLoader.class.getPackage()) ? tlClass
/* 34 */       .getSimpleName() : tlClass.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\TemplateLoaderUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */