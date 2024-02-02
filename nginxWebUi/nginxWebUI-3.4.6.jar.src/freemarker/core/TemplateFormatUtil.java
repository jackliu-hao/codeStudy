/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateDateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateNumberModel;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TemplateFormatUtil
/*    */ {
/*    */   public static void checkHasNoParameters(String params) throws InvalidFormatParametersException {
/* 41 */     if (params.length() != 0) {
/* 42 */       throw new InvalidFormatParametersException("This number format doesn't support any parameters.");
/*    */     }
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
/*    */   public static Number getNonNullNumber(TemplateNumberModel numberModel) throws TemplateModelException, UnformattableValueException {
/* 55 */     Number number = numberModel.getAsNumber();
/* 56 */     if (number == null) {
/* 57 */       throw EvalUtil.newModelHasStoredNullException(Number.class, numberModel, null);
/*    */     }
/* 59 */     return number;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Date getNonNullDate(TemplateDateModel dateModel) throws TemplateModelException {
/* 69 */     Date date = dateModel.getAsDate();
/* 70 */     if (date == null) {
/* 71 */       throw EvalUtil.newModelHasStoredNullException(Date.class, dateModel, null);
/*    */     }
/* 73 */     return date;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateFormatUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */