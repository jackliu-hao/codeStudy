/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.utility.StringUtil;
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
/*    */ public class _ObjectBuilderSettingEvaluationException
/*    */   extends Exception
/*    */ {
/*    */   public _ObjectBuilderSettingEvaluationException(String message, Throwable cause) {
/* 31 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public _ObjectBuilderSettingEvaluationException(String message) {
/* 35 */     super(message);
/*    */   }
/*    */   
/*    */   public _ObjectBuilderSettingEvaluationException(String expected, String src, int location) {
/* 39 */     super("Expression syntax error: Expected a(n) " + expected + ", but " + (
/* 40 */         (location < src.length()) ? ("found character " + 
/* 41 */         StringUtil.jQuote("" + src.charAt(location)) + " at position " + (location + 1) + ".") : "the end of the parsed string was reached."));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_ObjectBuilderSettingEvaluationException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */