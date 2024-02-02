/*    */ package org.apache.http.impl.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpMessage;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.io.SessionOutputBuffer;
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
/*    */ public class DefaultHttpRequestWriter
/*    */   extends AbstractMessageWriter<HttpRequest>
/*    */ {
/*    */   public DefaultHttpRequestWriter(SessionOutputBuffer buffer, LineFormatter formatter) {
/* 54 */     super(buffer, formatter);
/*    */   }
/*    */   
/*    */   public DefaultHttpRequestWriter(SessionOutputBuffer buffer) {
/* 58 */     this(buffer, (LineFormatter)null);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeHeadLine(HttpRequest message) throws IOException {
/* 63 */     this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
/* 64 */     this.sessionBuffer.writeLine(this.lineBuf);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\DefaultHttpRequestWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */