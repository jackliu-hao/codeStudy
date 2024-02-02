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
/*    */ public class NonMarkupOutputException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 31 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateMarkupOutputModel.class };
/*    */   
/*    */   public NonMarkupOutputException(Environment env) {
/* 34 */     super(env, "Expecting markup output value here");
/*    */   }
/*    */   
/*    */   public NonMarkupOutputException(String description, Environment env) {
/* 38 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonMarkupOutputException(Environment env, _ErrorDescriptionBuilder description) {
/* 42 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonMarkupOutputException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 48 */     super(blamed, model, "markup output", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonMarkupOutputException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 55 */     super(blamed, model, "markup output", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonMarkupOutputException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 60 */     super(blamed, model, "markup output", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonMarkupOutputException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */