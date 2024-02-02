/*    */ package io.undertow.client;
/*    */ 
/*    */ import io.undertow.util.AbstractAttachable;
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
/*    */ public final class ClientResponse
/*    */   extends AbstractAttachable
/*    */ {
/*    */   private final HeaderMap responseHeaders;
/*    */   private final int responseCode;
/*    */   private final String status;
/*    */   private final HttpString protocol;
/*    */   
/*    */   public ClientResponse(int responseCode, String status, HttpString protocol) {
/* 39 */     this.responseCode = responseCode;
/* 40 */     this.status = status;
/* 41 */     this.protocol = protocol;
/* 42 */     this.responseHeaders = new HeaderMap();
/*    */   }
/*    */   
/*    */   public ClientResponse(int responseCode, String status, HttpString protocol, HeaderMap headers) {
/* 46 */     this.responseCode = responseCode;
/* 47 */     this.status = status;
/* 48 */     this.protocol = protocol;
/* 49 */     this.responseHeaders = headers;
/*    */   }
/*    */   public HeaderMap getResponseHeaders() {
/* 52 */     return this.responseHeaders;
/*    */   }
/*    */   
/*    */   public HttpString getProtocol() {
/* 56 */     return this.protocol;
/*    */   }
/*    */   
/*    */   public int getResponseCode() {
/* 60 */     return this.responseCode;
/*    */   }
/*    */   
/*    */   public String getStatus() {
/* 64 */     return this.status;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 69 */     return "ClientResponse{responseHeaders=" + this.responseHeaders + ", responseCode=" + this.responseCode + ", status='" + this.status + '\'' + ", protocol=" + this.protocol + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ClientResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */