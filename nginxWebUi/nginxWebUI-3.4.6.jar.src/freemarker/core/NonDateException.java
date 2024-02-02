/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateDateModel;
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
/*    */ public class NonDateException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 30 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateDateModel.class };
/*    */   
/*    */   public NonDateException(Environment env) {
/* 33 */     super(env, "Expecting date/time value here");
/*    */   }
/*    */   
/*    */   public NonDateException(String description, Environment env) {
/* 37 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonDateException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 43 */     super(blamed, model, "date/time", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonDateException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 50 */     super(blamed, model, "date/time", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonDateException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 55 */     super(blamed, model, "date/time", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonDateException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */