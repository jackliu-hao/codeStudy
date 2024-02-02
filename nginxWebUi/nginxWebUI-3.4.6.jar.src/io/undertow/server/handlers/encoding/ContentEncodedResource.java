/*    */ package io.undertow.server.handlers.encoding;
/*    */ 
/*    */ import io.undertow.server.handlers.resource.Resource;
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
/*    */ public class ContentEncodedResource
/*    */ {
/*    */   private final Resource resource;
/*    */   private final String contentEncoding;
/*    */   
/*    */   public ContentEncodedResource(Resource resource, String contentEncoding) {
/* 34 */     this.resource = resource;
/* 35 */     this.contentEncoding = contentEncoding;
/*    */   }
/*    */   
/*    */   public Resource getResource() {
/* 39 */     return this.resource;
/*    */   }
/*    */   
/*    */   public String getContentEncoding() {
/* 43 */     return this.contentEncoding;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\encoding\ContentEncodedResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */