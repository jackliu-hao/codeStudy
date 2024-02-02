/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.UndeclaredThrowableException;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.BackoffManager;
/*     */ import org.apache.http.client.ConnectionBackoffStrategy;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.HttpExecutionAware;
/*     */ import org.apache.http.client.methods.HttpRequestWrapper;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.conn.routing.HttpRoute;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class BackoffStrategyExec
/*     */   implements ClientExecChain
/*     */ {
/*     */   private final ClientExecChain requestExecutor;
/*     */   private final ConnectionBackoffStrategy connectionBackoffStrategy;
/*     */   private final BackoffManager backoffManager;
/*     */   
/*     */   public BackoffStrategyExec(ClientExecChain requestExecutor, ConnectionBackoffStrategy connectionBackoffStrategy, BackoffManager backoffManager) {
/*  60 */     Args.notNull(requestExecutor, "HTTP client request executor");
/*  61 */     Args.notNull(connectionBackoffStrategy, "Connection backoff strategy");
/*  62 */     Args.notNull(backoffManager, "Backoff manager");
/*  63 */     this.requestExecutor = requestExecutor;
/*  64 */     this.connectionBackoffStrategy = connectionBackoffStrategy;
/*  65 */     this.backoffManager = backoffManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
/*  74 */     Args.notNull(route, "HTTP route");
/*  75 */     Args.notNull(request, "HTTP request");
/*  76 */     Args.notNull(context, "HTTP context");
/*  77 */     CloseableHttpResponse out = null;
/*     */     try {
/*  79 */       out = this.requestExecutor.execute(route, request, context, execAware);
/*  80 */     } catch (Exception ex) {
/*  81 */       if (out != null) {
/*  82 */         out.close();
/*     */       }
/*  84 */       if (this.connectionBackoffStrategy.shouldBackoff(ex)) {
/*  85 */         this.backoffManager.backOff(route);
/*     */       }
/*  87 */       if (ex instanceof RuntimeException) {
/*  88 */         throw (RuntimeException)ex;
/*     */       }
/*  90 */       if (ex instanceof HttpException) {
/*  91 */         throw (HttpException)ex;
/*     */       }
/*  93 */       if (ex instanceof IOException) {
/*  94 */         throw (IOException)ex;
/*     */       }
/*  96 */       throw new UndeclaredThrowableException(ex);
/*     */     } 
/*  98 */     if (this.connectionBackoffStrategy.shouldBackoff((HttpResponse)out)) {
/*  99 */       this.backoffManager.backOff(route);
/*     */     } else {
/* 101 */       this.backoffManager.probe(route);
/*     */     } 
/* 103 */     return out;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\BackoffStrategyExec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */