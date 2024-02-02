/*    */ package org.apache.http.message;
/*    */ 
/*    */ import org.apache.http.Header;
/*    */ import org.apache.http.HttpEntity;
/*    */ import org.apache.http.HttpEntityEnclosingRequest;
/*    */ import org.apache.http.ProtocolVersion;
/*    */ import org.apache.http.RequestLine;
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
/*    */ public class BasicHttpEntityEnclosingRequest
/*    */   extends BasicHttpRequest
/*    */   implements HttpEntityEnclosingRequest
/*    */ {
/*    */   private HttpEntity entity;
/*    */   
/*    */   public BasicHttpEntityEnclosingRequest(String method, String uri) {
/* 48 */     super(method, uri);
/*    */   }
/*    */ 
/*    */   
/*    */   public BasicHttpEntityEnclosingRequest(String method, String uri, ProtocolVersion ver) {
/* 53 */     super(method, uri, ver);
/*    */   }
/*    */   
/*    */   public BasicHttpEntityEnclosingRequest(RequestLine requestline) {
/* 57 */     super(requestline);
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpEntity getEntity() {
/* 62 */     return this.entity;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setEntity(HttpEntity entity) {
/* 67 */     this.entity = entity;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean expectContinue() {
/* 72 */     Header expect = getFirstHeader("Expect");
/* 73 */     return (expect != null && "100-continue".equalsIgnoreCase(expect.getValue()));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicHttpEntityEnclosingRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */