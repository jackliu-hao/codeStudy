/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.utility.ClassUtil;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class OutputFormat
/*    */ {
/*    */   public abstract String getName();
/*    */   
/*    */   public abstract String getMimeType();
/*    */   
/*    */   public abstract boolean isOutputFormatMixingAllowed();
/*    */   
/*    */   public final String toString() {
/* 68 */     String extras = toStringExtraProperties();
/* 69 */     return getName() + "(mimeType=" + 
/* 70 */       StringUtil.jQuote(getMimeType()) + ", class=" + 
/* 71 */       ClassUtil.getShortClassNameOfObject(this, true) + (
/* 72 */       (extras.length() != 0) ? ", " : "") + extras + ")";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected String toStringExtraProperties() {
/* 81 */     return "";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\OutputFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */