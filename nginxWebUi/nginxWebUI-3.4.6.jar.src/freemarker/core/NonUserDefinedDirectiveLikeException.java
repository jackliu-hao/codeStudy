/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateDirectiveModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateTransformModel;
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
/*    */ class NonUserDefinedDirectiveLikeException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 34 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateDirectiveModel.class, TemplateTransformModel.class, Macro.class };
/*    */ 
/*    */   
/*    */   public NonUserDefinedDirectiveLikeException(Environment env) {
/* 38 */     super(env, "Expecting user-defined directive, transform or macro value here");
/*    */   }
/*    */   
/*    */   public NonUserDefinedDirectiveLikeException(String description, Environment env) {
/* 42 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonUserDefinedDirectiveLikeException(Environment env, _ErrorDescriptionBuilder description) {
/* 46 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonUserDefinedDirectiveLikeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 52 */     super(blamed, model, "user-defined directive, transform or macro", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonUserDefinedDirectiveLikeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 59 */     super(blamed, model, "user-defined directive, transform or macro", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonUserDefinedDirectiveLikeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 64 */     super(blamed, model, "user-defined directive, transform or macro", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonUserDefinedDirectiveLikeException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */