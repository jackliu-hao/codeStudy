/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateHashModel;
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
/*    */ public class NonHashException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 32 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateHashModel.class };
/*    */   
/*    */   public NonHashException(Environment env) {
/* 35 */     super(env, "Expecting hash value here");
/*    */   }
/*    */   
/*    */   public NonHashException(String description, Environment env) {
/* 39 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonHashException(Environment env, _ErrorDescriptionBuilder description) {
/* 43 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonHashException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 49 */     super(blamed, model, "hash", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonHashException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 56 */     super(blamed, model, "hash", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonHashException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 61 */     super(blamed, model, "hash", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonHashException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */