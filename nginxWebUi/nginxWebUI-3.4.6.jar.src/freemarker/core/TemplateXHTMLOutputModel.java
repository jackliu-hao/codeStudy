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
/*    */ public class TemplateXHTMLOutputModel
/*    */   extends TemplateXMLOutputModel
/*    */ {
/*    */   protected TemplateXHTMLOutputModel(String plainTextContent, String markupContent) {
/* 35 */     super(plainTextContent, markupContent);
/*    */   }
/*    */ 
/*    */   
/*    */   public XHTMLOutputFormat getOutputFormat() {
/* 40 */     return XHTMLOutputFormat.INSTANCE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateXHTMLOutputModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */