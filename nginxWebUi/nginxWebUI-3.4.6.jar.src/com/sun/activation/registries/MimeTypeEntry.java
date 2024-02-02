/*    */ package com.sun.activation.registries;
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
/*    */ public class MimeTypeEntry
/*    */ {
/*    */   private String type;
/*    */   private String extension;
/*    */   
/*    */   public MimeTypeEntry(String mime_type, String file_ext) {
/* 37 */     this.type = mime_type;
/* 38 */     this.extension = file_ext;
/*    */   }
/*    */   
/*    */   public String getMIMEType() {
/* 42 */     return this.type;
/*    */   }
/*    */   
/*    */   public String getFileExtension() {
/* 46 */     return this.extension;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 50 */     return "MIMETypeEntry: " + this.type + ", " + this.extension;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\registries\MimeTypeEntry.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */