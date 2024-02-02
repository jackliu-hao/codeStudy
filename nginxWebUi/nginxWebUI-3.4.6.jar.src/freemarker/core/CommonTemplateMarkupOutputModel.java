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
/*    */ public abstract class CommonTemplateMarkupOutputModel<MO extends CommonTemplateMarkupOutputModel<MO>>
/*    */   implements TemplateMarkupOutputModel<MO>
/*    */ {
/*    */   private final String plainTextContent;
/*    */   private String markupContent;
/*    */   
/*    */   protected CommonTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
/* 42 */     this.plainTextContent = plainTextContent;
/* 43 */     this.markupContent = markupContent;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   final String getPlainTextContent() {
/* 51 */     return this.plainTextContent;
/*    */   }
/*    */ 
/*    */   
/*    */   final String getMarkupContent() {
/* 56 */     return this.markupContent;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   final void setMarkupContent(String markupContent) {
/* 64 */     this.markupContent = markupContent;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "markupOutput(format=" + getOutputFormat().getName() + ", " + ((this.plainTextContent != null) ? ("plainText=" + this.plainTextContent) : ("markup=" + this.markupContent)) + ")";
/*    */   }
/*    */   
/*    */   public abstract CommonMarkupOutputFormat<MO> getOutputFormat();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\CommonTemplateMarkupOutputModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */