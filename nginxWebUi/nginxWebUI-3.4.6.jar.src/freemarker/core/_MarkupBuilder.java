/*    */ package freemarker.core;
/*    */ 
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
/*    */ 
/*    */ public class _MarkupBuilder<MO extends TemplateMarkupOutputModel>
/*    */ {
/*    */   private final String markupSource;
/*    */   private final MarkupOutputFormat<MO> markupOutputFormat;
/*    */   
/*    */   public _MarkupBuilder(MarkupOutputFormat<MO> markupOutputFormat, String markupSource) {
/* 36 */     this.markupOutputFormat = markupOutputFormat;
/* 37 */     this.markupSource = markupSource;
/*    */   }
/*    */   
/*    */   public MO build() throws TemplateModelException {
/* 41 */     return this.markupOutputFormat.fromMarkup(this.markupSource);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_MarkupBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */