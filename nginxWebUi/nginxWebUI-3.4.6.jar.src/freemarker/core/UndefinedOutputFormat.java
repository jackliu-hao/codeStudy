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
/*    */ 
/*    */ public final class UndefinedOutputFormat
/*    */   extends OutputFormat
/*    */ {
/* 36 */   public static final UndefinedOutputFormat INSTANCE = new UndefinedOutputFormat();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isOutputFormatMixingAllowed() {
/* 44 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 49 */     return "undefined";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMimeType() {
/* 54 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\UndefinedOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */