/*    */ package io.undertow.servlet.spec;
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
/*    */ class ContentTypeInfo
/*    */ {
/*    */   private final String header;
/*    */   private final String charset;
/*    */   private final String contentType;
/*    */   
/*    */   ContentTypeInfo(String header, String charset, String contentType) {
/* 30 */     this.header = header;
/* 31 */     this.charset = charset;
/* 32 */     this.contentType = contentType;
/*    */   }
/*    */   
/*    */   public String getHeader() {
/* 36 */     return this.header;
/*    */   }
/*    */   
/*    */   public String getCharset() {
/* 40 */     return this.charset;
/*    */   }
/*    */   
/*    */   public String getContentType() {
/* 44 */     return this.contentType;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\ContentTypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */