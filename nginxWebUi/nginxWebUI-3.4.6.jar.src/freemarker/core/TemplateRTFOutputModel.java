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
/*    */ public class TemplateRTFOutputModel
/*    */   extends CommonTemplateMarkupOutputModel<TemplateRTFOutputModel>
/*    */ {
/*    */   protected TemplateRTFOutputModel(String plainTextContent, String markupContent) {
/* 35 */     super(plainTextContent, markupContent);
/*    */   }
/*    */ 
/*    */   
/*    */   public RTFOutputFormat getOutputFormat() {
/* 40 */     return RTFOutputFormat.INSTANCE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateRTFOutputModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */