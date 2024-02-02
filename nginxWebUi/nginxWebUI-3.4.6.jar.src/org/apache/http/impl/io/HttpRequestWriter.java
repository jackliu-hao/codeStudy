/*    */ package org.apache.http.impl.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpMessage;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.io.SessionOutputBuffer;
/*    */ import org.apache.http.message.LineFormatter;
/*    */ import org.apache.http.params.HttpParams;
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
/*    */ public class HttpRequestWriter
/*    */   extends AbstractMessageWriter<HttpRequest>
/*    */ {
/*    */   public HttpRequestWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
/* 51 */     super(buffer, formatter, params);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeHeadLine(HttpRequest message) throws IOException {
/* 56 */     this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
/* 57 */     this.sessionBuffer.writeLine(this.lineBuf);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\HttpRequestWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */