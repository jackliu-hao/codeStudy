/*    */ package ch.qos.logback.core.net;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.io.OutputStream;
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
/*    */ public class ObjectWriterFactory
/*    */ {
/*    */   public AutoFlushingObjectWriter newAutoFlushingObjectWriter(OutputStream outputStream) throws IOException {
/* 37 */     return new AutoFlushingObjectWriter(new ObjectOutputStream(outputStream), 70);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ObjectWriterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */