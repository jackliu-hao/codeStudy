/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.HttpResponseFactory;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.config.MessageConstraints;
/*    */ import org.apache.http.impl.DefaultHttpResponseFactory;
/*    */ import org.apache.http.io.HttpMessageParser;
/*    */ import org.apache.http.io.HttpMessageParserFactory;
/*    */ import org.apache.http.io.SessionInputBuffer;
/*    */ import org.apache.http.message.BasicLineParser;
/*    */ import org.apache.http.message.LineParser;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*    */ public class DefaultHttpResponseParserFactory
/*    */   implements HttpMessageParserFactory<HttpResponse>
/*    */ {
/* 50 */   public static final DefaultHttpResponseParserFactory INSTANCE = new DefaultHttpResponseParserFactory();
/*    */ 
/*    */   
/*    */   private final LineParser lineParser;
/*    */   
/*    */   private final HttpResponseFactory responseFactory;
/*    */ 
/*    */   
/*    */   public DefaultHttpResponseParserFactory(LineParser lineParser, HttpResponseFactory responseFactory) {
/* 59 */     this.lineParser = (lineParser != null) ? lineParser : (LineParser)BasicLineParser.INSTANCE;
/* 60 */     this.responseFactory = (responseFactory != null) ? responseFactory : (HttpResponseFactory)DefaultHttpResponseFactory.INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DefaultHttpResponseParserFactory(HttpResponseFactory responseFactory) {
/* 66 */     this(null, responseFactory);
/*    */   }
/*    */   
/*    */   public DefaultHttpResponseParserFactory() {
/* 70 */     this(null, null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpMessageParser<HttpResponse> create(SessionInputBuffer buffer, MessageConstraints constraints) {
/* 76 */     return (HttpMessageParser<HttpResponse>)new DefaultHttpResponseParser(buffer, this.lineParser, this.responseFactory, constraints);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultHttpResponseParserFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */