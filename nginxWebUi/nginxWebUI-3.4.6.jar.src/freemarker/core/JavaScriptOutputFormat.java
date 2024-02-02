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
/*    */ public class JavaScriptOutputFormat
/*    */   extends OutputFormat
/*    */ {
/* 32 */   public static final JavaScriptOutputFormat INSTANCE = new JavaScriptOutputFormat();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 40 */     return "JavaScript";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMimeType() {
/* 45 */     return "application/javascript";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isOutputFormatMixingAllowed() {
/* 50 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\JavaScriptOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */