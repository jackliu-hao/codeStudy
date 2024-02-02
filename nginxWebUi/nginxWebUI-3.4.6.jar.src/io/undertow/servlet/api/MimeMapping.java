/*    */ package io.undertow.servlet.api;
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
/*    */ public class MimeMapping
/*    */ {
/*    */   private final String extension;
/*    */   private final String mimeType;
/*    */   
/*    */   public MimeMapping(String extension, String mimeType) {
/* 29 */     this.extension = extension;
/* 30 */     this.mimeType = mimeType;
/*    */   }
/*    */   
/*    */   public String getExtension() {
/* 34 */     return this.extension;
/*    */   }
/*    */   
/*    */   public String getMimeType() {
/* 38 */     return this.mimeType;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\MimeMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */