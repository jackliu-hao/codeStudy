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
/*    */ class NonNamespaceException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 31 */   private static final Class[] EXPECTED_TYPES = new Class[] { Environment.Namespace.class };
/*    */   
/*    */   public NonNamespaceException(Environment env) {
/* 34 */     super(env, "Expecting namespace value here");
/*    */   }
/*    */   
/*    */   public NonNamespaceException(String description, Environment env) {
/* 38 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonNamespaceException(Environment env, _ErrorDescriptionBuilder description) {
/* 42 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonNamespaceException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 48 */     super(blamed, model, "namespace", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonNamespaceException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 55 */     super(blamed, model, "namespace", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonNamespaceException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 60 */     super(blamed, model, "namespace", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonNamespaceException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */