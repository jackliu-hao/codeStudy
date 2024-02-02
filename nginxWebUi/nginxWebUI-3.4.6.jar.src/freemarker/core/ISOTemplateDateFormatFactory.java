/*    */ package freemarker.core;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import java.util.TimeZone;
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
/*    */ class ISOTemplateDateFormatFactory
/*    */   extends ISOLikeTemplateDateFormatFactory
/*    */ {
/* 27 */   static final ISOTemplateDateFormatFactory INSTANCE = new ISOTemplateDateFormatFactory();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
/* 37 */     return new ISOTemplateDateFormat(params, 3, dateType, zonelessInput, timeZone, this, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ISOTemplateDateFormatFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */