/*    */ package io.undertow.client;
/*    */ 
/*    */ import io.undertow.util.AbstractAttachable;
/*    */ import io.undertow.util.HeaderMap;
/*    */ import io.undertow.util.HttpString;
/*    */ import io.undertow.util.Methods;
/*    */ import io.undertow.util.Protocols;
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
/*    */ public final class ClientRequest
/*    */   extends AbstractAttachable
/*    */ {
/* 38 */   private final HeaderMap requestHeaders = new HeaderMap();
/* 39 */   private String path = "/";
/* 40 */   private HttpString method = Methods.GET;
/* 41 */   private HttpString protocol = Protocols.HTTP_1_1;
/*    */   
/*    */   public HeaderMap getRequestHeaders() {
/* 44 */     return this.requestHeaders;
/*    */   }
/*    */   
/*    */   public String getPath() {
/* 48 */     return this.path;
/*    */   }
/*    */   
/*    */   public HttpString getMethod() {
/* 52 */     return this.method;
/*    */   }
/*    */   
/*    */   public HttpString getProtocol() {
/* 56 */     return this.protocol;
/*    */   }
/*    */   
/*    */   public ClientRequest setPath(String path) {
/* 60 */     this.path = path;
/* 61 */     return this;
/*    */   }
/*    */   
/*    */   public ClientRequest setMethod(HttpString method) {
/* 65 */     this.method = method;
/* 66 */     return this;
/*    */   }
/*    */   
/*    */   public ClientRequest setProtocol(HttpString protocol) {
/* 70 */     this.protocol = protocol;
/* 71 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "ClientRequest{path='" + this.path + '\'' + ", method=" + this.method + ", protocol=" + this.protocol + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ClientRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */