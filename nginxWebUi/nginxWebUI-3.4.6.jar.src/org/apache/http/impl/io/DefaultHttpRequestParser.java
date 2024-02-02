/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.ConnectionClosedException;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestFactory;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.RequestLine;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.impl.DefaultHttpRequestFactory;
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
/*     */ public class DefaultHttpRequestParser
/*     */   extends AbstractMessageParser<HttpRequest>
/*     */ {
/*     */   private final HttpRequestFactory requestFactory;
/*     */   private final CharArrayBuffer lineBuf;
/*     */   
/*     */   @Deprecated
/*     */   public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser lineParser, HttpRequestFactory requestFactory, HttpParams params) {
/*  78 */     super(buffer, lineParser, params);
/*  79 */     this.requestFactory = (HttpRequestFactory)Args.notNull(requestFactory, "Request factory");
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
/*     */   public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser lineParser, HttpRequestFactory requestFactory, MessageConstraints constraints) {
/* 101 */     super(buffer, lineParser, constraints);
/* 102 */     this.requestFactory = (requestFactory != null) ? requestFactory : (HttpRequestFactory)DefaultHttpRequestFactory.INSTANCE;
/*     */     
/* 104 */     this.lineBuf = new CharArrayBuffer(128);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpRequestParser(SessionInputBuffer buffer, MessageConstraints constraints) {
/* 113 */     this(buffer, (LineParser)null, (HttpRequestFactory)null, constraints);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpRequestParser(SessionInputBuffer buffer) {
/* 120 */     this(buffer, (LineParser)null, (HttpRequestFactory)null, MessageConstraints.DEFAULT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpRequest parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
/* 128 */     this.lineBuf.clear();
/* 129 */     int readLen = sessionBuffer.readLine(this.lineBuf);
/* 130 */     if (readLen == -1) {
/* 131 */       throw new ConnectionClosedException("Client closed connection");
/*     */     }
/* 133 */     ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
/* 134 */     RequestLine requestline = this.lineParser.parseRequestLine(this.lineBuf, cursor);
/* 135 */     return this.requestFactory.newHttpRequest(requestline);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\DefaultHttpRequestParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */