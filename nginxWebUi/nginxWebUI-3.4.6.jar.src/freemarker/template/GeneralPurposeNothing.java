/*    */ package freemarker.template;
/*    */ 
/*    */ import freemarker.template.utility.Constants;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ final class GeneralPurposeNothing
/*    */   implements TemplateBooleanModel, TemplateScalarModel, TemplateSequenceModel, TemplateHashModelEx2, TemplateMethodModelEx
/*    */ {
/* 35 */   private static final TemplateModel instance = new GeneralPurposeNothing();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static TemplateModel getInstance() {
/* 41 */     return instance;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAsString() {
/* 46 */     return "";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getAsBoolean() {
/* 51 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 56 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 61 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel get(int i) throws TemplateModelException {
/* 66 */     throw new TemplateModelException("Can't get item from an empty sequence.");
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel get(String key) {
/* 71 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object exec(List args) {
/* 76 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateCollectionModel keys() {
/* 81 */     return Constants.EMPTY_COLLECTION;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateCollectionModel values() {
/* 86 */     return Constants.EMPTY_COLLECTION;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
/* 91 */     return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\GeneralPurposeNothing.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */