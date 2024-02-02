/*    */ package org.apache.http.impl.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpMessage;
/*    */ import org.apache.http.HttpResponseFactory;
/*    */ import org.apache.http.NoHttpResponseException;
/*    */ import org.apache.http.ParseException;
/*    */ import org.apache.http.StatusLine;
/*    */ import org.apache.http.io.SessionInputBuffer;
/*    */ import org.apache.http.message.LineParser;
/*    */ import org.apache.http.message.ParserCursor;
/*    */ import org.apache.http.params.HttpParams;
/*    */ import org.apache.http.util.Args;
/*    */ import org.apache.http.util.CharArrayBuffer;
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
/*    */ public class HttpResponseParser
/*    */   extends AbstractMessageParser<HttpMessage>
/*    */ {
/*    */   private final HttpResponseFactory responseFactory;
/*    */   private final CharArrayBuffer lineBuf;
/*    */   
/*    */   public HttpResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory, HttpParams params) {
/* 80 */     super(buffer, parser, params);
/* 81 */     this.responseFactory = (HttpResponseFactory)Args.notNull(responseFactory, "Response factory");
/* 82 */     this.lineBuf = new CharArrayBuffer(128);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected HttpMessage parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
/* 90 */     this.lineBuf.clear();
/* 91 */     int readLen = sessionBuffer.readLine(this.lineBuf);
/* 92 */     if (readLen == -1) {
/* 93 */       throw new NoHttpResponseException("The target server failed to respond");
/*    */     }
/*    */     
/* 96 */     ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
/* 97 */     StatusLine statusline = this.lineParser.parseStatusLine(this.lineBuf, cursor);
/* 98 */     return (HttpMessage)this.responseFactory.newHttpResponse(statusline, null);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\HttpResponseParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */