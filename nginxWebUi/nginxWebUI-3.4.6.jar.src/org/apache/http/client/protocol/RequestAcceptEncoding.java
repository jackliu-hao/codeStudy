/*    */ package org.apache.http.client.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.HttpRequestInterceptor;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.client.config.RequestConfig;
/*    */ import org.apache.http.protocol.HttpContext;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class RequestAcceptEncoding
/*    */   implements HttpRequestInterceptor
/*    */ {
/*    */   private final String acceptEncoding;
/*    */   
/*    */   public RequestAcceptEncoding(List<String> encodings) {
/* 58 */     if (encodings != null && !encodings.isEmpty()) {
/* 59 */       StringBuilder buf = new StringBuilder();
/* 60 */       for (int i = 0; i < encodings.size(); i++) {
/* 61 */         if (i > 0) {
/* 62 */           buf.append(",");
/*    */         }
/* 64 */         buf.append(encodings.get(i));
/*    */       } 
/* 66 */       this.acceptEncoding = buf.toString();
/*    */     } else {
/* 68 */       this.acceptEncoding = "gzip,deflate";
/*    */     } 
/*    */   }
/*    */   
/*    */   public RequestAcceptEncoding() {
/* 73 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/* 81 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/* 82 */     RequestConfig requestConfig = clientContext.getRequestConfig();
/*    */ 
/*    */     
/* 85 */     if (!request.containsHeader("Accept-Encoding") && requestConfig.isContentCompressionEnabled())
/* 86 */       request.addHeader("Accept-Encoding", this.acceptEncoding); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\RequestAcceptEncoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */