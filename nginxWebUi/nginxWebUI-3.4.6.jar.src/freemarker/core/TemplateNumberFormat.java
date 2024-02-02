/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateNumberModel;
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
/*    */ public abstract class TemplateNumberFormat
/*    */   extends TemplateValueFormat
/*    */ {
/*    */   public abstract String formatToPlainText(TemplateNumberModel paramTemplateNumberModel) throws TemplateValueFormatException, TemplateModelException;
/*    */   
/*    */   public Object format(TemplateNumberModel numberModel) throws TemplateValueFormatException, TemplateModelException {
/* 71 */     return formatToPlainText(numberModel);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract boolean isLocaleBound();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final Object parse(String s) throws TemplateValueFormatException {
/* 86 */     throw new ParsingNotSupportedException("Number formats currenly don't support parsing");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateNumberFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */