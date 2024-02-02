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
/*    */ class XSTemplateDateFormatFactory
/*    */   extends ISOLikeTemplateDateFormatFactory
/*    */ {
/* 27 */   static final XSTemplateDateFormatFactory INSTANCE = new XSTemplateDateFormatFactory();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
/* 37 */     return new XSTemplateDateFormat(params, 2, dateType, zonelessInput, timeZone, this, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\XSTemplateDateFormatFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */