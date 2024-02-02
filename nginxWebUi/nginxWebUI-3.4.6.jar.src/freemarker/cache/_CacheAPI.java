/*    */ package freemarker.cache;
/*    */ 
/*    */ import freemarker.template.MalformedTemplateNameException;
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
/*    */ public final class _CacheAPI
/*    */ {
/*    */   public static String toRootBasedName(TemplateNameFormat templateNameFormat, String baseName, String targetName) throws MalformedTemplateNameException {
/* 37 */     return templateNameFormat.toRootBasedName(baseName, targetName);
/*    */   }
/*    */ 
/*    */   
/*    */   public static String normalizeRootBasedName(TemplateNameFormat templateNameFormat, String name) throws MalformedTemplateNameException {
/* 42 */     return templateNameFormat.normalizeRootBasedName(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public static String rootBasedNameToAbsoluteName(TemplateNameFormat templateNameFormat, String rootBasedName) throws MalformedTemplateNameException {
/* 47 */     return templateNameFormat.rootBasedNameToAbsoluteName(rootBasedName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\_CacheAPI.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */