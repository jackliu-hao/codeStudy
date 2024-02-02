/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
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
/*    */ public class NonStringOrTemplateOutputException
/*    */   extends UnexpectedTypeException
/*    */ {
/*    */   static final String STRING_COERCABLE_TYPES_OR_TOM_DESC = "string or something automatically convertible to string (number, date or boolean), or \"template output\" ";
/* 36 */   static final Class[] STRING_COERCABLE_TYPES_AND_TOM = new Class[NonStringException.STRING_COERCABLE_TYPES.length + 1]; static {
/*    */     int i;
/* 38 */     for (i = 0; i < NonStringException.STRING_COERCABLE_TYPES.length; i++) {
/* 39 */       STRING_COERCABLE_TYPES_AND_TOM[i] = NonStringException.STRING_COERCABLE_TYPES[i];
/*    */     }
/* 41 */     STRING_COERCABLE_TYPES_AND_TOM[i] = TemplateMarkupOutputModel.class;
/*    */   }
/*    */ 
/*    */   
/*    */   private static final String DEFAULT_DESCRIPTION = "Expecting string or something automatically convertible to string (number, date or boolean), or \"template output\"  value here";
/*    */   
/*    */   public NonStringOrTemplateOutputException(Environment env) {
/* 48 */     super(env, "Expecting string or something automatically convertible to string (number, date or boolean), or \"template output\"  value here");
/*    */   }
/*    */   
/*    */   public NonStringOrTemplateOutputException(String description, Environment env) {
/* 52 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonStringOrTemplateOutputException(Environment env, _ErrorDescriptionBuilder description) {
/* 56 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonStringOrTemplateOutputException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 62 */     super(blamed, model, "string or something automatically convertible to string (number, date or boolean), or \"template output\" ", STRING_COERCABLE_TYPES_AND_TOM, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonStringOrTemplateOutputException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 69 */     super(blamed, model, "string or something automatically convertible to string (number, date or boolean), or \"template output\" ", STRING_COERCABLE_TYPES_AND_TOM, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonStringOrTemplateOutputException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 74 */     super(blamed, model, "string or something automatically convertible to string (number, date or boolean), or \"template output\" ", STRING_COERCABLE_TYPES_AND_TOM, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonStringOrTemplateOutputException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */