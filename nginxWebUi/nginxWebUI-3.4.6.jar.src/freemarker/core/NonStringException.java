/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateBooleanModel;
/*    */ import freemarker.template.TemplateDateModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateNumberModel;
/*    */ import freemarker.template.TemplateScalarModel;
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
/*    */ public class NonStringException
/*    */   extends UnexpectedTypeException
/*    */ {
/*    */   static final String STRING_COERCABLE_TYPES_DESC = "string or something automatically convertible to string (number, date or boolean)";
/* 37 */   static final Class[] STRING_COERCABLE_TYPES = new Class[] { TemplateScalarModel.class, TemplateNumberModel.class, TemplateDateModel.class, TemplateBooleanModel.class };
/*    */ 
/*    */   
/*    */   private static final String DEFAULT_DESCRIPTION = "Expecting string or something automatically convertible to string (number, date or boolean) value here";
/*    */ 
/*    */ 
/*    */   
/*    */   public NonStringException(Environment env) {
/* 45 */     super(env, "Expecting string or something automatically convertible to string (number, date or boolean) value here");
/*    */   }
/*    */   
/*    */   public NonStringException(String description, Environment env) {
/* 49 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonStringException(Environment env, _ErrorDescriptionBuilder description) {
/* 53 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonStringException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 59 */     super(blamed, model, "string or something automatically convertible to string (number, date or boolean)", STRING_COERCABLE_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonStringException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 66 */     super(blamed, model, "string or something automatically convertible to string (number, date or boolean)", STRING_COERCABLE_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonStringException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 71 */     super(blamed, model, "string or something automatically convertible to string (number, date or boolean)", STRING_COERCABLE_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonStringException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */