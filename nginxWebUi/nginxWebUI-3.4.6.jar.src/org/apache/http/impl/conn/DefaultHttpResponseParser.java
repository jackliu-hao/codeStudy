/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseFactory;
/*     */ import org.apache.http.NoHttpResponseException;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.impl.DefaultHttpResponseFactory;
/*     */ import org.apache.http.impl.io.AbstractMessageParser;
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
/*     */ public class DefaultHttpResponseParser
/*     */   extends AbstractMessageParser<HttpResponse>
/*     */ {
/*  59 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   private final HttpResponseFactory responseFactory;
/*     */ 
/*     */ 
/*     */   
/*     */   private final CharArrayBuffer lineBuf;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory, HttpParams params) {
/*  74 */     super(buffer, parser, params);
/*  75 */     Args.notNull(responseFactory, "Response factory");
/*  76 */     this.responseFactory = responseFactory;
/*  77 */     this.lineBuf = new CharArrayBuffer(128);
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
/*  98 */     super(buffer, lineParser, constraints);
/*  99 */     this.responseFactory = (responseFactory != null) ? responseFactory : (HttpResponseFactory)DefaultHttpResponseFactory.INSTANCE;
/*     */     
/* 101 */     this.lineBuf = new CharArrayBuffer(128);
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
/*     */   public DefaultHttpResponseParser(SessionInputBuffer buffer, MessageConstraints constraints) {
/* 115 */     this(buffer, (LineParser)null, (HttpResponseFactory)null, constraints);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpResponseParser(SessionInputBuffer buffer) {
/* 126 */     this(buffer, (LineParser)null, (HttpResponseFactory)null, MessageConstraints.DEFAULT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpResponse parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException {
/* 133 */     int count = 0;
/* 134 */     ParserCursor cursor = null;
/*     */     
/*     */     while (true) {
/* 137 */       this.lineBuf.clear();
/* 138 */       int i = sessionBuffer.readLine(this.lineBuf);
/* 139 */       if (i == -1 && count == 0)
/*     */       {
/* 141 */         throw new NoHttpResponseException("The target server failed to respond");
/*     */       }
/* 143 */       cursor = new ParserCursor(0, this.lineBuf.length());
/* 144 */       if (this.lineParser.hasProtocolVersion(this.lineBuf, cursor)) {
/*     */         break;
/*     */       }
/* 147 */       if (i == -1 || reject(this.lineBuf, count))
/*     */       {
/* 149 */         throw new ProtocolException("The server failed to respond with a valid HTTP response");
/*     */       }
/*     */       
/* 152 */       if (this.log.isDebugEnabled()) {
/* 153 */         this.log.debug("Garbage in response: " + this.lineBuf.toString());
/*     */       }
/* 155 */       count++;
/*     */     } 
/*     */     
/* 158 */     StatusLine statusline = this.lineParser.parseStatusLine(this.lineBuf, cursor);
/* 159 */     return this.responseFactory.newHttpResponse(statusline, null);
/*     */   }
/*     */   
/*     */   protected boolean reject(CharArrayBuffer line, int count) {
/* 163 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultHttpResponseParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */