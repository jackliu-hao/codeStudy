/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class HttpRequestExecutor
/*     */ {
/*     */   public static final int DEFAULT_WAIT_FOR_CONTINUE = 3000;
/*     */   private final int waitForContinue;
/*     */   
/*     */   public HttpRequestExecutor(int waitForContinue) {
/*  72 */     this.waitForContinue = Args.positive(waitForContinue, "Wait for continue time");
/*     */   }
/*     */   
/*     */   public HttpRequestExecutor() {
/*  76 */     this(3000);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean canResponseHaveBody(HttpRequest request, HttpResponse response) {
/*  93 */     if ("HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
/*  94 */       return false;
/*     */     }
/*  96 */     int status = response.getStatusLine().getStatusCode();
/*  97 */     return (status >= 200 && status != 204 && status != 304 && status != 205);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
/* 119 */     Args.notNull(request, "HTTP request");
/* 120 */     Args.notNull(conn, "Client connection");
/* 121 */     Args.notNull(context, "HTTP context");
/*     */     try {
/* 123 */       HttpResponse response = doSendRequest(request, conn, context);
/* 124 */       if (response == null) {
/* 125 */         response = doReceiveResponse(request, conn, context);
/*     */       }
/* 127 */       return response;
/* 128 */     } catch (IOException ex) {
/* 129 */       closeConnection(conn);
/* 130 */       throw ex;
/* 131 */     } catch (HttpException ex) {
/* 132 */       closeConnection(conn);
/* 133 */       throw ex;
/* 134 */     } catch (RuntimeException ex) {
/* 135 */       closeConnection(conn);
/* 136 */       throw ex;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void closeConnection(HttpClientConnection conn) {
/*     */     try {
/* 142 */       conn.close();
/* 143 */     } catch (IOException ignore) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void preProcess(HttpRequest request, HttpProcessor processor, HttpContext context) throws HttpException, IOException {
/* 163 */     Args.notNull(request, "HTTP request");
/* 164 */     Args.notNull(processor, "HTTP processor");
/* 165 */     Args.notNull(context, "HTTP context");
/* 166 */     context.setAttribute("http.request", request);
/* 167 */     processor.process(request, context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpResponse doSendRequest(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
/* 196 */     Args.notNull(request, "HTTP request");
/* 197 */     Args.notNull(conn, "Client connection");
/* 198 */     Args.notNull(context, "HTTP context");
/*     */     
/* 200 */     HttpResponse response = null;
/*     */     
/* 202 */     context.setAttribute("http.connection", conn);
/* 203 */     context.setAttribute("http.request_sent", Boolean.FALSE);
/*     */     
/* 205 */     conn.sendRequestHeader(request);
/* 206 */     if (request instanceof HttpEntityEnclosingRequest) {
/*     */ 
/*     */ 
/*     */       
/* 210 */       boolean sendentity = true;
/* 211 */       ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
/*     */       
/* 213 */       if (((HttpEntityEnclosingRequest)request).expectContinue() && !ver.lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0)) {
/*     */ 
/*     */         
/* 216 */         conn.flush();
/*     */ 
/*     */         
/* 219 */         if (conn.isResponseAvailable(this.waitForContinue)) {
/* 220 */           response = conn.receiveResponseHeader();
/* 221 */           if (canResponseHaveBody(request, response)) {
/* 222 */             conn.receiveResponseEntity(response);
/*     */           }
/* 224 */           int status = response.getStatusLine().getStatusCode();
/* 225 */           if (status < 200) {
/* 226 */             if (status != 100) {
/* 227 */               throw new ProtocolException("Unexpected response: " + response.getStatusLine());
/*     */             }
/*     */ 
/*     */             
/* 231 */             response = null;
/*     */           } else {
/* 233 */             sendentity = false;
/*     */           } 
/*     */         } 
/*     */       } 
/* 237 */       if (sendentity) {
/* 238 */         conn.sendRequestEntity((HttpEntityEnclosingRequest)request);
/*     */       }
/*     */     } 
/* 241 */     conn.flush();
/* 242 */     context.setAttribute("http.request_sent", Boolean.TRUE);
/* 243 */     return response;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpResponse doReceiveResponse(HttpRequest request, HttpClientConnection conn, HttpContext context) throws HttpException, IOException {
/* 265 */     Args.notNull(request, "HTTP request");
/* 266 */     Args.notNull(conn, "Client connection");
/* 267 */     Args.notNull(context, "HTTP context");
/* 268 */     HttpResponse response = null;
/* 269 */     int statusCode = 0;
/*     */     
/* 271 */     while (response == null || statusCode < 200) {
/*     */       
/* 273 */       response = conn.receiveResponseHeader();
/* 274 */       statusCode = response.getStatusLine().getStatusCode();
/* 275 */       if (statusCode < 100) {
/* 276 */         throw new ProtocolException("Invalid response: " + response.getStatusLine());
/*     */       }
/* 278 */       if (canResponseHaveBody(request, response)) {
/* 279 */         conn.receiveResponseEntity(response);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 284 */     return response;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void postProcess(HttpResponse response, HttpProcessor processor, HttpContext context) throws HttpException, IOException {
/* 309 */     Args.notNull(response, "HTTP response");
/* 310 */     Args.notNull(processor, "HTTP processor");
/* 311 */     Args.notNull(context, "HTTP context");
/* 312 */     context.setAttribute("http.response", response);
/* 313 */     processor.process(response, context);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\HttpRequestExecutor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */