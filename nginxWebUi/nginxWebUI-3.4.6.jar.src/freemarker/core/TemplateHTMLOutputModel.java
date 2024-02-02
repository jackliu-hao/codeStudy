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
/*    */ public class TemplateHTMLOutputModel
/*    */   extends CommonTemplateMarkupOutputModel<TemplateHTMLOutputModel>
/*    */ {
/*    */   protected TemplateHTMLOutputModel(String plainTextContent, String markupContent) {
/* 35 */     super(plainTextContent, markupContent);
/*    */   }
/*    */ 
/*    */   
/*    */   public HTMLOutputFormat getOutputFormat() {
/* 40 */     return HTMLOutputFormat.INSTANCE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateHTMLOutputModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */