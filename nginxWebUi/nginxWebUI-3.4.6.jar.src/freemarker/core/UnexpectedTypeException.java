/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateCollectionModelEx;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UnexpectedTypeException
/*     */   extends TemplateException
/*     */ {
/*     */   public UnexpectedTypeException(Environment env, String description) {
/*  38 */     super(description, env);
/*     */   }
/*     */   
/*     */   UnexpectedTypeException(Environment env, _ErrorDescriptionBuilder description) {
/*  42 */     super(null, env, null, description);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Environment env) throws InvalidReferenceException {
/*  48 */     super(null, env, blamed, newDescriptionBuilder(blamed, null, model, expectedTypesDesc, expectedTypes, env));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, String tip, Environment env) throws InvalidReferenceException {
/*  55 */     super(null, env, blamed, newDescriptionBuilder(blamed, null, model, expectedTypesDesc, expectedTypes, env)
/*  56 */         .tip(tip));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Object[] tips, Environment env) throws InvalidReferenceException {
/*  63 */     super(null, env, blamed, newDescriptionBuilder(blamed, null, model, expectedTypesDesc, expectedTypes, env)
/*  64 */         .tips(tips));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   UnexpectedTypeException(String blamedAssignmentTargetVarName, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Object[] tips, Environment env) throws InvalidReferenceException {
/*  72 */     super(null, env, null, newDescriptionBuilder(null, blamedAssignmentTargetVarName, model, expectedTypesDesc, expectedTypes, env)
/*  73 */         .tips(tips));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static _ErrorDescriptionBuilder newDescriptionBuilder(Expression blamed, String blamedAssignmentTargetVarName, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Environment env) throws InvalidReferenceException {
/*  85 */     if (model == null) throw InvalidReferenceException.getInstance(blamed, env);
/*     */ 
/*     */ 
/*     */     
/*  89 */     _ErrorDescriptionBuilder errorDescBuilder = (new _ErrorDescriptionBuilder(unexpectedTypeErrorDescription(expectedTypesDesc, blamed, blamedAssignmentTargetVarName, model))).blame(blamed).showBlamer(true);
/*  90 */     if (model instanceof _UnexpectedTypeErrorExplainerTemplateModel) {
/*  91 */       Object[] tip = ((_UnexpectedTypeErrorExplainerTemplateModel)model).explainTypeError(expectedTypes);
/*  92 */       if (tip != null) {
/*  93 */         errorDescBuilder.tip(tip);
/*     */       }
/*     */     } 
/*  96 */     if (model instanceof freemarker.template.TemplateCollectionModel && (
/*  97 */       Arrays.<Class<?>[]>asList((Class<?>[][])expectedTypes).contains(TemplateSequenceModel.class) || 
/*  98 */       Arrays.<Class<?>[]>asList((Class<?>[][])expectedTypes).contains(TemplateCollectionModelEx.class))) {
/*  99 */       errorDescBuilder.tip("As the problematic value contains a collection of items, you could convert it to a sequence like someValue?sequence. Be sure though that you won't have a large number of items, as all will be held in memory the same time.");
/*     */     }
/*     */ 
/*     */     
/* 103 */     return errorDescBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Object[] unexpectedTypeErrorDescription(String expectedTypesDesc, Expression blamed, String blamedAssignmentTargetVarName, TemplateModel model) {
/* 110 */     (new Object[7])[0] = "Expected "; (new Object[7])[1] = new _DelayedAOrAn(expectedTypesDesc); (new Object[7])[2] = ", but "; (new Object[2])[0] = "assignment target variable "; (new Object[2])[1] = new _DelayedJQuote(blamedAssignmentTargetVarName); return new Object[] { null, null, null, (blamedAssignmentTargetVarName == null) ? ((blamed != null) ? "this" : "the expression") : new Object[2], " has evaluated to ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(model)), (blamed != null) ? ":" : "." };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\UnexpectedTypeException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */