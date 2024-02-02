/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import org.apache.http.HttpRequestInterceptor;
/*    */ import org.apache.http.HttpResponseInterceptor;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.client.protocol.RequestAcceptEncoding;
/*    */ import org.apache.http.client.protocol.ResponseContentEncoding;
/*    */ import org.apache.http.conn.ClientConnectionManager;
/*    */ import org.apache.http.params.HttpParams;
/*    */ import org.apache.http.protocol.BasicHttpProcessor;
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
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*    */ public class ContentEncodingHttpClient
/*    */   extends DefaultHttpClient
/*    */ {
/*    */   public ContentEncodingHttpClient(ClientConnectionManager conman, HttpParams params) {
/* 64 */     super(conman, params);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ContentEncodingHttpClient(HttpParams params) {
/* 71 */     this(null, params);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ContentEncodingHttpClient() {
/* 78 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected BasicHttpProcessor createHttpProcessor() {
/* 86 */     BasicHttpProcessor result = super.createHttpProcessor();
/*    */     
/* 88 */     result.addRequestInterceptor((HttpRequestInterceptor)new RequestAcceptEncoding());
/* 89 */     result.addResponseInterceptor((HttpResponseInterceptor)new ResponseContentEncoding());
/*    */     
/* 91 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\ContentEncodingHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */