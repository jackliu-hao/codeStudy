/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateNodeModelEx;
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
/*    */ public class NonExtendedNodeException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 32 */   private static final Class<?>[] EXPECTED_TYPES = new Class[] { TemplateNodeModelEx.class };
/*    */   
/*    */   public NonExtendedNodeException(Environment env) {
/* 35 */     super(env, "Expecting extended node value here");
/*    */   }
/*    */   
/*    */   public NonExtendedNodeException(String description, Environment env) {
/* 39 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonExtendedNodeException(Environment env, _ErrorDescriptionBuilder description) {
/* 43 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonExtendedNodeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 49 */     super(blamed, model, "extended node", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonExtendedNodeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 56 */     super(blamed, model, "extended node", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonExtendedNodeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 61 */     super(blamed, model, "extended node", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonExtendedNodeException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */