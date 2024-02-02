/*    */ package org.apache.http.impl.io;
/*    */ 
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.HttpRequestFactory;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.config.MessageConstraints;
/*    */ import org.apache.http.impl.DefaultHttpRequestFactory;
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
/*    */ public class DefaultHttpRequestParserFactory
/*    */   implements HttpMessageParserFactory<HttpRequest>
/*    */ {
/* 50 */   public static final DefaultHttpRequestParserFactory INSTANCE = new DefaultHttpRequestParserFactory();
/*    */   
/*    */   private final LineParser lineParser;
/*    */   
/*    */   private final HttpRequestFactory requestFactory;
/*    */ 
/*    */   
/*    */   public DefaultHttpRequestParserFactory(LineParser lineParser, HttpRequestFactory requestFactory) {
/* 58 */     this.lineParser = (lineParser != null) ? lineParser : (LineParser)BasicLineParser.INSTANCE;
/* 59 */     this.requestFactory = (requestFactory != null) ? requestFactory : (HttpRequestFactory)DefaultHttpRequestFactory.INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public DefaultHttpRequestParserFactory() {
/* 64 */     this(null, null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpMessageParser<HttpRequest> create(SessionInputBuffer buffer, MessageConstraints constraints) {
/* 70 */     return new DefaultHttpRequestParser(buffer, this.lineParser, this.requestFactory, constraints);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\DefaultHttpRequestParserFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */