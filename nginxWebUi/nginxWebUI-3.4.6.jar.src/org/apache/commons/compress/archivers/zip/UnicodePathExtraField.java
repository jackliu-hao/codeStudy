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
/*    */ public class UnicodePathExtraField
/*    */   extends AbstractUnicodeExtraField
/*    */ {
/* 34 */   public static final ZipShort UPATH_ID = new ZipShort(28789);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnicodePathExtraField() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnicodePathExtraField(String text, byte[] bytes, int off, int len) {
/* 50 */     super(text, bytes, off, len);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnicodePathExtraField(String name, byte[] bytes) {
/* 61 */     super(name, bytes);
/*    */   }
/*    */ 
/*    */   
/*    */   public ZipShort getHeaderId() {
/* 66 */     return UPATH_ID;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\UnicodePathExtraField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */