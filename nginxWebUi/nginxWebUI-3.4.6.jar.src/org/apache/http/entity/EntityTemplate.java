/*    */ package org.apache.http.entity;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.http.util.Args;
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
/*    */ public class EntityTemplate
/*    */   extends AbstractHttpEntity
/*    */ {
/*    */   private final ContentProducer contentproducer;
/*    */   
/*    */   public EntityTemplate(ContentProducer contentproducer) {
/* 50 */     this.contentproducer = (ContentProducer)Args.notNull(contentproducer, "Content producer");
/*    */   }
/*    */ 
/*    */   
/*    */   public long getContentLength() {
/* 55 */     return -1L;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getContent() throws IOException {
/* 60 */     ByteArrayOutputStream buf = new ByteArrayOutputStream();
/* 61 */     writeTo(buf);
/* 62 */     return new ByteArrayInputStream(buf.toByteArray());
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isRepeatable() {
/* 67 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeTo(OutputStream outStream) throws IOException {
/* 72 */     Args.notNull(outStream, "Output stream");
/* 73 */     this.contentproducer.writeTo(outStream);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isStreaming() {
/* 78 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\EntityTemplate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */