/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateDateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import java.text.DateFormat;
/*    */ import java.text.ParseException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class JavaTemplateDateFormat
/*    */   extends TemplateDateFormat
/*    */ {
/*    */   private final DateFormat javaDateFormat;
/*    */   
/*    */   public JavaTemplateDateFormat(DateFormat javaDateFormat) {
/* 38 */     this.javaDateFormat = javaDateFormat;
/*    */   }
/*    */ 
/*    */   
/*    */   public String formatToPlainText(TemplateDateModel dateModel) throws TemplateModelException {
/* 43 */     return this.javaDateFormat.format(TemplateFormatUtil.getNonNullDate(dateModel));
/*    */   }
/*    */ 
/*    */   
/*    */   public Date parse(String s, int dateType) throws UnparsableValueException {
/*    */     try {
/* 49 */       return this.javaDateFormat.parse(s);
/* 50 */     } catch (ParseException e) {
/* 51 */       throw new UnparsableValueException(e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDescription() {
/* 57 */     return (this.javaDateFormat instanceof SimpleDateFormat) ? ((SimpleDateFormat)this.javaDateFormat)
/* 58 */       .toPattern() : this.javaDateFormat
/* 59 */       .toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isLocaleBound() {
/* 64 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTimeZoneBound() {
/* 69 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\JavaTemplateDateFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */