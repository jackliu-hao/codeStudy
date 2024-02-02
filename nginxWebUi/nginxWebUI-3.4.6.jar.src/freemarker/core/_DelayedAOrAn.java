/*    */ package freemarker.core;
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
/*    */ public class _DelayedAOrAn
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedAOrAn(Object object) {
/* 26 */     super(object);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/* 31 */     String s = obj.toString();
/* 32 */     return _MessageUtil.getAOrAn(s) + " " + s;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedAOrAn.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */