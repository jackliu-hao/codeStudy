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
/*    */ public final class PlainTextOutputFormat
/*    */   extends OutputFormat
/*    */ {
/* 35 */   public static final PlainTextOutputFormat INSTANCE = new PlainTextOutputFormat();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isOutputFormatMixingAllowed() {
/* 43 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 48 */     return "plainText";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMimeType() {
/* 53 */     return "text/plain";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\PlainTextOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */