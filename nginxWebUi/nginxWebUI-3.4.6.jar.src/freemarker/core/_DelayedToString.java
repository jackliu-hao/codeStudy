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
/*    */ public class _DelayedToString
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedToString(Object object) {
/* 25 */     super(object);
/*    */   }
/*    */   
/*    */   public _DelayedToString(int object) {
/* 29 */     super(Integer.valueOf(object));
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/* 34 */     return String.valueOf(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedToString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */