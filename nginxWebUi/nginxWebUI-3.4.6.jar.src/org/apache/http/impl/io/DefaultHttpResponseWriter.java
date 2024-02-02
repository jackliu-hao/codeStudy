/*    */ package org.apache.http.impl.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpMessage;
/*    */ import org.apache.http.HttpResponse;
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
/*    */ public class DefaultHttpResponseWriter
/*    */   extends AbstractMessageWriter<HttpResponse>
/*    */ {
/*    */   public DefaultHttpResponseWriter(SessionOutputBuffer buffer, LineFormatter formatter) {
/* 54 */     super(buffer, formatter);
/*    */   }
/*    */   
/*    */   public DefaultHttpResponseWriter(SessionOutputBuffer buffer) {
/* 58 */     super(buffer, null);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeHeadLine(HttpResponse message) throws IOException {
/* 63 */     this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine());
/* 64 */     this.sessionBuffer.writeLine(this.lineBuf);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\DefaultHttpResponseWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */