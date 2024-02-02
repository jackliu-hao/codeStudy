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
/*    */ public class _DelayedJoinWithComma
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedJoinWithComma(String[] items) {
/* 26 */     super(items);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/* 31 */     String[] items = (String[])obj;
/*    */     
/* 33 */     int totalLength = 0;
/* 34 */     for (int i = 0; i < items.length; i++) {
/* 35 */       if (i != 0) totalLength += 2; 
/* 36 */       totalLength += items[i].length();
/*    */     } 
/*    */     
/* 39 */     StringBuilder sb = new StringBuilder(totalLength);
/* 40 */     for (int j = 0; j < items.length; j++) {
/* 41 */       if (j != 0) sb.append(", "); 
/* 42 */       sb.append(items[j]);
/*    */     } 
/*    */     
/* 45 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedJoinWithComma.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */