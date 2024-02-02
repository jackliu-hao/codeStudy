/*    */ package org.apache.http.impl.io;
/*    */ 
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.io.HttpMessageWriter;
/*    */ import org.apache.http.io.HttpMessageWriterFactory;
/*    */ import org.apache.http.io.SessionOutputBuffer;
/*    */ import org.apache.http.message.BasicLineFormatter;
/*    */ import org.apache.http.message.LineFormatter;
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
/*    */ public class DefaultHttpRequestWriterFactory
/*    */   implements HttpMessageWriterFactory<HttpRequest>
/*    */ {
/* 47 */   public static final DefaultHttpRequestWriterFactory INSTANCE = new DefaultHttpRequestWriterFactory();
/*    */   
/*    */   private final LineFormatter lineFormatter;
/*    */ 
/*    */   
/*    */   public DefaultHttpRequestWriterFactory(LineFormatter lineFormatter) {
/* 53 */     this.lineFormatter = (lineFormatter != null) ? lineFormatter : (LineFormatter)BasicLineFormatter.INSTANCE;
/*    */   }
/*    */   
/*    */   public DefaultHttpRequestWriterFactory() {
/* 57 */     this(null);
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpMessageWriter<HttpRequest> create(SessionOutputBuffer buffer) {
/* 62 */     return new DefaultHttpRequestWriter(buffer, this.lineFormatter);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\DefaultHttpRequestWriterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */