/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateMethodModel;
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
/*    */ public class NonMethodException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 32 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateMethodModel.class };
/* 33 */   private static final Class[] EXPECTED_TYPES_WITH_FUNCTION = new Class[] { TemplateMethodModel.class, Macro.class };
/*    */   
/*    */   public NonMethodException(Environment env) {
/* 36 */     super(env, "Expecting method value here");
/*    */   }
/*    */   
/*    */   public NonMethodException(String description, Environment env) {
/* 40 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonMethodException(Environment env, _ErrorDescriptionBuilder description) {
/* 44 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonMethodException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 50 */     super(blamed, model, "method", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonMethodException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 57 */     super(blamed, model, "method", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonMethodException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 62 */     this(blamed, model, false, false, tips, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonMethodException(Expression blamed, TemplateModel model, boolean allowFTLFunction, boolean allowLambdaExp, String[] tips, Environment env) throws InvalidReferenceException {
/* 74 */     super(blamed, model, "method" + (allowFTLFunction ? " or function" : "") + (allowLambdaExp ? " or lambda expression" : ""), allowFTLFunction ? EXPECTED_TYPES_WITH_FUNCTION : EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonMethodException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */