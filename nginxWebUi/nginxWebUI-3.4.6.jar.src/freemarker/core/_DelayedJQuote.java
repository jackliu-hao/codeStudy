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
/*    */ public class _DelayedJQuote
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedJQuote(Object object) {
/* 28 */     super(object);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/* 33 */     return StringUtil.jQuote(_ErrorDescriptionBuilder.toString(obj));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedJQuote.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */