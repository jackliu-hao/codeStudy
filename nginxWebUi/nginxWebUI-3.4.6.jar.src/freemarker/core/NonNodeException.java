/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateNodeModel;
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
/*    */ public class NonNodeException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 32 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateNodeModel.class };
/*    */   
/*    */   public NonNodeException(Environment env) {
/* 35 */     super(env, "Expecting node value here");
/*    */   }
/*    */   
/*    */   public NonNodeException(String description, Environment env) {
/* 39 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonNodeException(Environment env, _ErrorDescriptionBuilder description) {
/* 43 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonNodeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 49 */     super(blamed, model, "node", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonNodeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 56 */     super(blamed, model, "node", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonNodeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 61 */     super(blamed, model, "node", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonNodeException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */