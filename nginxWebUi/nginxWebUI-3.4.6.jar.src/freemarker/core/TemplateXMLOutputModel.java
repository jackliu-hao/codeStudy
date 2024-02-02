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
/*    */ public class TemplateXMLOutputModel
/*    */   extends CommonTemplateMarkupOutputModel<TemplateXMLOutputModel>
/*    */ {
/*    */   protected TemplateXMLOutputModel(String plainTextContent, String markupContent) {
/* 35 */     super(plainTextContent, markupContent);
/*    */   }
/*    */ 
/*    */   
/*    */   public XMLOutputFormat getOutputFormat() {
/* 40 */     return XMLOutputFormat.INSTANCE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateXMLOutputModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */