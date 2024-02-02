/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateNumberModel;
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
/*    */ public class NonNumericalException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 30 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateNumberModel.class };
/*    */   
/*    */   public NonNumericalException(Environment env) {
/* 33 */     super(env, "Expecting numerical value here");
/*    */   }
/*    */   
/*    */   public NonNumericalException(String description, Environment env) {
/* 37 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonNumericalException(_ErrorDescriptionBuilder description, Environment env) {
/* 41 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonNumericalException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 47 */     super(blamed, model, "number", EXPECTED_TYPES, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonNumericalException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 54 */     super(blamed, model, "number", EXPECTED_TYPES, tip, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonNumericalException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 59 */     super(blamed, model, "number", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonNumericalException(String assignmentTargetVarName, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
/* 65 */     super(assignmentTargetVarName, model, "number", EXPECTED_TYPES, (Object[])tips, env);
/*    */   }
/*    */   static NonNumericalException newMalformedNumberException(Expression blamed, String text, Environment env) {
/* 68 */     return new NonNumericalException((new _ErrorDescriptionBuilder(new Object[] { "Can't convert this string to number: ", new _DelayedJQuote(text)
/*    */           },
/* 70 */         )).blame(blamed), env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonNumericalException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */