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
/*    */ public class _DelayedOrdinal
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedOrdinal(Object object) {
/* 26 */     super(object);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/* 31 */     if (obj instanceof Number) {
/* 32 */       long n = ((Number)obj).longValue();
/* 33 */       if (n % 10L == 1L && n % 100L != 11L)
/* 34 */         return n + "st"; 
/* 35 */       if (n % 10L == 2L && n % 100L != 12L)
/* 36 */         return n + "nd"; 
/* 37 */       if (n % 10L == 3L && n % 100L != 13L) {
/* 38 */         return n + "rd";
/*    */       }
/* 40 */       return n + "th";
/*    */     } 
/*    */     
/* 43 */     return "" + obj;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedOrdinal.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */