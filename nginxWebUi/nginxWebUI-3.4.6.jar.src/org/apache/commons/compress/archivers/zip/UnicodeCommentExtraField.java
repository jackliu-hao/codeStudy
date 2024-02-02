/*    */ package org.apache.commons.compress.archivers.zip;
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
/*    */ public class UnicodeCommentExtraField
/*    */   extends AbstractUnicodeExtraField
/*    */ {
/* 34 */   public static final ZipShort UCOM_ID = new ZipShort(25461);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnicodeCommentExtraField() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnicodeCommentExtraField(String text, byte[] bytes, int off, int len) {
/* 51 */     super(text, bytes, off, len);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnicodeCommentExtraField(String comment, byte[] bytes) {
/* 62 */     super(comment, bytes);
/*    */   }
/*    */ 
/*    */   
/*    */   public ZipShort getHeaderId() {
/* 67 */     return UCOM_ID;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\UnicodeCommentExtraField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */