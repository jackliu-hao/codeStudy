/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateNumberModel;
/*    */ import java.text.NumberFormat;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class JavaTemplateNumberFormat
/*    */   extends BackwardCompatibleTemplateNumberFormat
/*    */ {
/*    */   private final String formatString;
/*    */   private final NumberFormat javaNumberFormat;
/*    */   
/*    */   public JavaTemplateNumberFormat(NumberFormat javaNumberFormat, String formatString) {
/* 32 */     this.formatString = formatString;
/* 33 */     this.javaNumberFormat = javaNumberFormat;
/*    */   }
/*    */ 
/*    */   
/*    */   public String formatToPlainText(TemplateNumberModel numberModel) throws UnformattableValueException, TemplateModelException {
/* 38 */     Number number = TemplateFormatUtil.getNonNullNumber(numberModel);
/* 39 */     return format(number);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isLocaleBound() {
/* 44 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   String format(Number number) throws UnformattableValueException {
/*    */     try {
/* 50 */       return this.javaNumberFormat.format(number);
/* 51 */     } catch (ArithmeticException e) {
/* 52 */       throw new UnformattableValueException("This format can't format the " + number + " number. Reason: " + e
/* 53 */           .getMessage(), e);
/*    */     } 
/*    */   }
/*    */   
/*    */   public NumberFormat getJavaNumberFormat() {
/* 58 */     return this.javaNumberFormat;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDescription() {
/* 63 */     return this.formatString;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\JavaTemplateNumberFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */