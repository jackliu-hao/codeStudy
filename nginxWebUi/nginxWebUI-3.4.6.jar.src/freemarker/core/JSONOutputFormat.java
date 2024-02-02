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
/*    */ public class JSONOutputFormat
/*    */   extends OutputFormat
/*    */ {
/* 31 */   public static final JSONOutputFormat INSTANCE = new JSONOutputFormat();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 39 */     return "JSON";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMimeType() {
/* 44 */     return "application/json";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isOutputFormatMixingAllowed() {
/* 49 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\JSONOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */