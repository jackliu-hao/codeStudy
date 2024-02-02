/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseFactory;
/*     */ import org.apache.http.NoHttpResponseException;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.impl.DefaultHttpResponseFactory;
/*     */ import org.apache.http.io.SessionInputBuffer;
/*     */ import org.apache.http.message.LineParser;
/*     */ import org.apache.http.message.ParserCursor;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
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
/*     */ 
/*     */ public class DefaultHttpResponseParser
/*     */   extends AbstractMessageParser<HttpResponse>
/*     */ {
/*     */   private final HttpResponseFactory responseFactory;
/*     */   private final CharArrayBuffer lineBuf;
/*     */   
/*     */   @Deprecated
/*     */   public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser lineParser, HttpResponseFactory responseFactory, HttpParams params) {
/*  78 */     super(buffer, lineParser, params);
/*  79 */     this.responseFactory = (HttpResponseFactory)Args.notNull(responseFactory, "Response factory");
/*  80 */     this.lineBuf = new CharArrayBuffer(128);
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
/*     */   public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser lineParser, HttpResponseFactory responseFactory, MessageConstraints constraints) {
/* 101 */     super(buffer, lineParser, constraints);
/* 102 */     this.responseFactory = (responseFactory != null) ? responseFactory : (HttpResponseFactory)DefaultHttpResponseFactory.INSTANCE;
/*     */     
/* 104 */     this.lineBuf = new CharArrayBuffer(128);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpResponseParser(SessionInputBuffer buffer, MessageConstraints constraints) {
/* 113 */     this(buffer, (LineParser)null, (HttpResponseFactory)null, constraints);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpResponseParser(SessionInputBuffer buffer) {
/* 120 */     this(buffer, (LineParser)null, (HttpResponseFactory)null, MessageConstraints.DEFAULT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpResponse parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
/* 128 */     this.lineBuf.clear();
/* 129 */     int readLen = sessionBuffer.readLine(this.lineBuf);
/* 130 */     if (readLen == -1) {
/* 131 */       throw new NoHttpResponseException("The target server failed to respond");
/*     */     }
/*     */     
/* 134 */     ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
/* 135 */     StatusLine statusline = this.lineParser.parseStatusLine(this.lineBuf, cursor);
/* 136 */     return this.responseFactory.newHttpResponse(statusline, null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\DefaultHttpResponseParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */