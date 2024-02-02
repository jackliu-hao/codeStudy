/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateSequenceModel;
/*    */ import freemarker.template.utility.CollectionUtils;
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
/*    */ public class NonSequenceException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 33 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateSequenceModel.class };
/*    */   
/*    */   public NonSequenceException(Environment env) {
/* 36 */     super(env, "Expecting sequence value here");
/*    */   }
/*    */   
/*    */   public NonSequenceException(String description, Environment env) {
/* 40 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonSequenceException(Environment env, _ErrorDescriptionBuilder description) {
/* 44 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonSequenceException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 50 */     this(blamed, model, CollectionUtils.EMPTY_OBJECT_ARRAY, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonSequenceException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 57 */     this(blamed, model, new Object[] { tip }, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonSequenceException(Expression blamed, TemplateModel model, Object[] tips, Environment env) throws InvalidReferenceException {
/* 62 */     super(blamed, model, "sequence", EXPECTED_TYPES, tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonSequenceException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */