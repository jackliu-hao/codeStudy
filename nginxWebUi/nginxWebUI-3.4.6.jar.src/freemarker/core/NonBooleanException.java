/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateBooleanModel;
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
/*    */ public class NonBooleanException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 30 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateBooleanModel.class };
/*    */   
/*    */   public NonBooleanException(Environment env) {
/* 33 */     super(env, "Expecting boolean value here");
/*    */   }
/*    */   
/*    */   public NonBooleanException(String description, Environment env) {
/* 37 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonBooleanException(Environment env, _ErrorDescriptionBuilder description) {
/* 41 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonBooleanException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 47 */     super(blamed, model, "boolean", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonBooleanException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 54 */     super(blamed, model, "boolean", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonBooleanException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 59 */     super(blamed, model, "boolean", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonBooleanException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */