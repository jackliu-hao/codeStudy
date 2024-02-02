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
/*    */ public class CSSOutputFormat
/*    */   extends OutputFormat
/*    */ {
/* 31 */   public static final CSSOutputFormat INSTANCE = new CSSOutputFormat();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 39 */     return "CSS";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMimeType() {
/* 44 */     return "text/css";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isOutputFormatMixingAllowed() {
/* 49 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\CSSOutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */