/*    */ package freemarker.core;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TemplateCombinedMarkupOutputModel
/*    */   extends CommonTemplateMarkupOutputModel<TemplateCombinedMarkupOutputModel>
/*    */ {
/*    */   private final CombinedMarkupOutputFormat outputFormat;
/*    */   
/*    */   TemplateCombinedMarkupOutputModel(String plainTextContent, String markupContent, CombinedMarkupOutputFormat outputFormat) {
/* 41 */     super(plainTextContent, markupContent);
/* 42 */     this.outputFormat = outputFormat;
/*    */   }
/*    */ 
/*    */   
/*    */   public CombinedMarkupOutputFormat getOutputFormat() {
/* 47 */     return this.outputFormat;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateCombinedMarkupOutputModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */