/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateDateModel;
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
/*    */ public abstract class TemplateDateFormat
/*    */   extends TemplateValueFormat
/*    */ {
/*    */   public abstract String formatToPlainText(TemplateDateModel paramTemplateDateModel) throws TemplateValueFormatException, TemplateModelException;
/*    */   
/*    */   public Object format(TemplateDateModel dateModel) throws TemplateValueFormatException, TemplateModelException {
/* 70 */     return formatToPlainText(dateModel);
/*    */   }
/*    */   
/*    */   public abstract Object parse(String paramString, int paramInt) throws TemplateValueFormatException;
/*    */   
/*    */   public abstract boolean isLocaleBound();
/*    */   
/*    */   public abstract boolean isTimeZoneBound();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateDateFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */