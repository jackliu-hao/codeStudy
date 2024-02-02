/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.ext.util.WrapperTemplateModel;
/*    */ import freemarker.template.TemplateCollectionModel;
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
/*    */ 
/*    */ public class NonSequenceOrCollectionException
/*    */   extends UnexpectedTypeException
/*    */ {
/* 36 */   private static final Class[] EXPECTED_TYPES = new Class[] { TemplateSequenceModel.class, TemplateCollectionModel.class };
/*    */ 
/*    */   
/*    */   private static final String ITERABLE_SUPPORT_HINT = "The problematic value is a java.lang.Iterable. Using DefaultObjectWrapper(..., iterableSupport=true) as the object_wrapper setting of the FreeMarker configuration should solve this.";
/*    */ 
/*    */ 
/*    */   
/*    */   public NonSequenceOrCollectionException(Environment env) {
/* 44 */     super(env, "Expecting sequence or collection value here");
/*    */   }
/*    */   
/*    */   public NonSequenceOrCollectionException(String description, Environment env) {
/* 48 */     super(env, description);
/*    */   }
/*    */   
/*    */   NonSequenceOrCollectionException(Environment env, _ErrorDescriptionBuilder description) {
/* 52 */     super(env, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   NonSequenceOrCollectionException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
/* 58 */     this(blamed, model, CollectionUtils.EMPTY_OBJECT_ARRAY, env);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   NonSequenceOrCollectionException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
/* 65 */     this(blamed, model, new Object[] { tip }, env);
/*    */   }
/*    */ 
/*    */   
/*    */   NonSequenceOrCollectionException(Expression blamed, TemplateModel model, Object[] tips, Environment env) throws InvalidReferenceException {
/* 70 */     super(blamed, model, "sequence or collection", EXPECTED_TYPES, extendTipsIfIterable(model, tips), env);
/*    */   }
/*    */   
/*    */   private static Object[] extendTipsIfIterable(TemplateModel model, Object[] tips) {
/* 74 */     if (isWrappedIterable(model)) {
/* 75 */       int tipsLen = (tips != null) ? tips.length : 0;
/* 76 */       Object[] extendedTips = new Object[tipsLen + 1];
/* 77 */       for (int i = 0; i < tipsLen; i++) {
/* 78 */         extendedTips[i] = tips[i];
/*    */       }
/* 80 */       extendedTips[tipsLen] = "The problematic value is a java.lang.Iterable. Using DefaultObjectWrapper(..., iterableSupport=true) as the object_wrapper setting of the FreeMarker configuration should solve this.";
/* 81 */       return extendedTips;
/*    */     } 
/* 83 */     return tips;
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean isWrappedIterable(TemplateModel model) {
/* 88 */     return (model instanceof WrapperTemplateModel && ((WrapperTemplateModel)model)
/* 89 */       .getWrappedObject() instanceof Iterable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonSequenceOrCollectionException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */