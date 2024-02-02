/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseFactory;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.ReasonPhraseCatalog;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.message.BasicHttpResponse;
/*     */ import org.apache.http.message.BasicStatusLine;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class DefaultHttpResponseFactory
/*     */   implements HttpResponseFactory
/*     */ {
/*  52 */   public static final DefaultHttpResponseFactory INSTANCE = new DefaultHttpResponseFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ReasonPhraseCatalog reasonCatalog;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpResponseFactory(ReasonPhraseCatalog catalog) {
/*  64 */     this.reasonCatalog = (ReasonPhraseCatalog)Args.notNull(catalog, "Reason phrase catalog");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpResponseFactory() {
/*  72 */     this(EnglishReasonPhraseCatalog.INSTANCE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse newHttpResponse(ProtocolVersion ver, int status, HttpContext context) {
/*  82 */     Args.notNull(ver, "HTTP version");
/*  83 */     Locale loc = determineLocale(context);
/*  84 */     String reason = this.reasonCatalog.getReason(status, loc);
/*  85 */     BasicStatusLine basicStatusLine = new BasicStatusLine(ver, status, reason);
/*  86 */     return (HttpResponse)new BasicHttpResponse((StatusLine)basicStatusLine, this.reasonCatalog, loc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse newHttpResponse(StatusLine statusline, HttpContext context) {
/*  95 */     Args.notNull(statusline, "Status line");
/*  96 */     return (HttpResponse)new BasicHttpResponse(statusline, this.reasonCatalog, determineLocale(context));
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
/*     */   protected Locale determineLocale(HttpContext context) {
/* 109 */     return Locale.getDefault();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\DefaultHttpResponseFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */