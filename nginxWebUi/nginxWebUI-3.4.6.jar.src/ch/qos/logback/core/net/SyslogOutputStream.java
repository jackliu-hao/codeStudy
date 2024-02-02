/*    */ package ch.qos.logback.core.net;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.net.DatagramPacket;
/*    */ import java.net.DatagramSocket;
/*    */ import java.net.InetAddress;
/*    */ import java.net.SocketException;
/*    */ import java.net.UnknownHostException;
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
/*    */ public class SyslogOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   private static final int MAX_LEN = 1024;
/*    */   private InetAddress address;
/*    */   private DatagramSocket ds;
/* 39 */   private ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*    */   private final int port;
/*    */   
/*    */   public SyslogOutputStream(String syslogHost, int port) throws UnknownHostException, SocketException {
/* 43 */     this.address = InetAddress.getByName(syslogHost);
/* 44 */     this.port = port;
/* 45 */     this.ds = new DatagramSocket();
/*    */   }
/*    */   
/*    */   public void write(byte[] byteArray, int offset, int len) throws IOException {
/* 49 */     this.baos.write(byteArray, offset, len);
/*    */   }
/*    */   
/*    */   public void flush() throws IOException {
/* 53 */     byte[] bytes = this.baos.toByteArray();
/* 54 */     DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.address, this.port);
/*    */ 
/*    */     
/* 57 */     if (this.baos.size() > 1024) {
/* 58 */       this.baos = new ByteArrayOutputStream();
/*    */     } else {
/* 60 */       this.baos.reset();
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 65 */     if (bytes.length == 0) {
/*    */       return;
/*    */     }
/* 68 */     if (this.ds != null) {
/* 69 */       this.ds.send(packet);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 75 */     this.address = null;
/* 76 */     this.ds = null;
/*    */   }
/*    */   
/*    */   public int getPort() {
/* 80 */     return this.port;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 85 */     this.baos.write(b);
/*    */   }
/*    */   
/*    */   int getSendBufferSize() throws SocketException {
/* 89 */     return this.ds.getSendBufferSize();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\SyslogOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */