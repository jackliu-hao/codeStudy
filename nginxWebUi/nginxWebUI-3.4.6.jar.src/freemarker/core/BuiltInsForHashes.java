/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateCollectionModel;
/*    */ import freemarker.template.TemplateHashModelEx;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
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
/*    */ class BuiltInsForHashes
/*    */ {
/*    */   static class keysBI
/*    */     extends BuiltInForHashEx
/*    */   {
/*    */     TemplateModel calculateResult(TemplateHashModelEx hashExModel, Environment env) throws TemplateModelException, InvalidReferenceException {
/* 38 */       TemplateCollectionModel keys = hashExModel.keys();
/* 39 */       if (keys == null) throw newNullPropertyException("keys", hashExModel, env); 
/* 40 */       return (keys instanceof freemarker.template.TemplateSequenceModel) ? (TemplateModel)keys : (TemplateModel)new CollectionAndSequence(keys);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   static class valuesBI
/*    */     extends BuiltInForHashEx
/*    */   {
/*    */     TemplateModel calculateResult(TemplateHashModelEx hashExModel, Environment env) throws TemplateModelException, InvalidReferenceException {
/* 49 */       TemplateCollectionModel values = hashExModel.values();
/* 50 */       if (values == null) throw newNullPropertyException("values", hashExModel, env); 
/* 51 */       return (values instanceof freemarker.template.TemplateSequenceModel) ? (TemplateModel)values : (TemplateModel)new CollectionAndSequence(values);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForHashes.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */