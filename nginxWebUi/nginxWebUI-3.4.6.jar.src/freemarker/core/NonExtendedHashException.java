/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateHashModelEx;
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
/*    */ public class NonExtendedHashException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 30 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateHashModelEx.class };
/*    */   
/*    */   public NonExtendedHashException(Environment env) {
/* 33 */     super(env, "Expecting extended hash value here");
/*    */   }
/*    */   
/*    */   public NonExtendedHashException(String description, Environment env) {
/* 37 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonExtendedHashException(Environment env, _ErrorDescriptionBuilder description) {
/* 41 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonExtendedHashException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 47 */     super(blamed, model, "extended hash", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonExtendedHashException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 54 */     super(blamed, model, "extended hash", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonExtendedHashException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 59 */     super(blamed, model, "extended hash", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonExtendedHashException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */