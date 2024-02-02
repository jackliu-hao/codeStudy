/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.ConnectException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.SSLException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.HttpRequestRetryHandler;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class DefaultHttpRequestRetryHandler
/*     */   implements HttpRequestRetryHandler
/*     */ {
/*  59 */   public static final DefaultHttpRequestRetryHandler INSTANCE = new DefaultHttpRequestRetryHandler();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int retryCount;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean requestSentRetryEnabled;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Set<Class<? extends IOException>> nonRetriableClasses;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected DefaultHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled, Collection<Class<? extends IOException>> clazzes) {
/*  82 */     this.retryCount = retryCount;
/*  83 */     this.requestSentRetryEnabled = requestSentRetryEnabled;
/*  84 */     this.nonRetriableClasses = new HashSet<Class<? extends IOException>>();
/*  85 */     for (Class<? extends IOException> clazz : clazzes) {
/*  86 */       this.nonRetriableClasses.add(clazz);
/*     */     }
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
/*     */   public DefaultHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
/* 104 */     this(retryCount, requestSentRetryEnabled, Arrays.asList((Class<? extends IOException>[])new Class[] { InterruptedIOException.class, UnknownHostException.class, ConnectException.class, SSLException.class }));
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
/*     */   public DefaultHttpRequestRetryHandler() {
/* 122 */     this(3, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
/* 133 */     Args.notNull(exception, "Exception parameter");
/* 134 */     Args.notNull(context, "HTTP context");
/* 135 */     if (executionCount > this.retryCount)
/*     */     {
/* 137 */       return false;
/*     */     }
/* 139 */     if (this.nonRetriableClasses.contains(exception.getClass())) {
/* 140 */       return false;
/*     */     }
/* 142 */     for (Class<? extends IOException> rejectException : this.nonRetriableClasses) {
/* 143 */       if (rejectException.isInstance(exception)) {
/* 144 */         return false;
/*     */       }
/*     */     } 
/* 147 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/* 148 */     HttpRequest request = clientContext.getRequest();
/*     */     
/* 150 */     if (requestIsAborted(request)) {
/* 151 */       return false;
/*     */     }
/*     */     
/* 154 */     if (handleAsIdempotent(request))
/*     */     {
/* 156 */       return true;
/*     */     }
/*     */     
/* 159 */     if (!clientContext.isRequestSent() || this.requestSentRetryEnabled)
/*     */     {
/*     */       
/* 162 */       return true;
/*     */     }
/*     */     
/* 165 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRequestSentRetryEnabled() {
/* 173 */     return this.requestSentRetryEnabled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRetryCount() {
/* 180 */     return this.retryCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean handleAsIdempotent(HttpRequest request) {
/* 187 */     return !(request instanceof org.apache.http.HttpEntityEnclosingRequest);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected boolean requestIsAborted(HttpRequest request) {
/* 197 */     HttpRequest req = request;
/* 198 */     if (request instanceof RequestWrapper) {
/* 199 */       req = ((RequestWrapper)request).getOriginal();
/*     */     }
/* 201 */     return (req instanceof HttpUriRequest && ((HttpUriRequest)req).isAborted());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultHttpRequestRetryHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */