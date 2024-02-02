/*    */ package io.undertow.client.http;
/*    */ 
/*    */ import io.undertow.client.ClientResponse;
/*    */ import io.undertow.util.HeaderMap;
/*    */ import io.undertow.util.HttpString;
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
/*    */ final class HttpResponseBuilder
/*    */ {
/* 32 */   private final ResponseParseState parseState = new ResponseParseState();
/*    */   
/*    */   private int statusCode;
/*    */   private HttpString protocol;
/*    */   private String reasonPhrase;
/* 37 */   private final HeaderMap responseHeaders = new HeaderMap();
/*    */   
/*    */   public ResponseParseState getParseState() {
/* 40 */     return this.parseState;
/*    */   }
/*    */   
/*    */   HeaderMap getResponseHeaders() {
/* 44 */     return this.responseHeaders;
/*    */   }
/*    */   
/*    */   int getStatusCode() {
/* 48 */     return this.statusCode;
/*    */   }
/*    */   
/*    */   void setStatusCode(int statusCode) {
/* 52 */     this.statusCode = statusCode;
/*    */   }
/*    */   
/*    */   String getReasonPhrase() {
/* 56 */     return this.reasonPhrase;
/*    */   }
/*    */   
/*    */   void setReasonPhrase(String reasonPhrase) {
/* 60 */     this.reasonPhrase = reasonPhrase;
/*    */   }
/*    */   
/*    */   HttpString getProtocol() {
/* 64 */     return this.protocol;
/*    */   }
/*    */ 
/*    */   
/*    */   void setProtocol(HttpString protocol) {
/* 69 */     this.protocol = protocol;
/*    */   }
/*    */   
/*    */   public ClientResponse build() {
/* 73 */     return new ClientResponse(this.statusCode, this.reasonPhrase, this.protocol, this.responseHeaders);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http\HttpResponseBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */