/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import org.apache.http.io.EofSensor;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class SocketInputBuffer
/*     */   extends AbstractSessionInputBuffer
/*     */   implements EofSensor
/*     */ {
/*     */   private final Socket socket;
/*     */   private boolean eof;
/*     */   
/*     */   public SocketInputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
/*  67 */     Args.notNull(socket, "Socket");
/*  68 */     this.socket = socket;
/*  69 */     this.eof = false;
/*  70 */     int n = bufferSize;
/*  71 */     if (n < 0) {
/*  72 */       n = socket.getReceiveBufferSize();
/*     */     }
/*  74 */     if (n < 1024) {
/*  75 */       n = 1024;
/*     */     }
/*  77 */     init(socket.getInputStream(), n, params);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int fillBuffer() throws IOException {
/*  82 */     int i = super.fillBuffer();
/*  83 */     this.eof = (i == -1);
/*  84 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDataAvailable(int timeout) throws IOException {
/*  89 */     boolean result = hasBufferedData();
/*  90 */     if (!result) {
/*  91 */       int oldtimeout = this.socket.getSoTimeout();
/*     */       try {
/*  93 */         this.socket.setSoTimeout(timeout);
/*  94 */         fillBuffer();
/*  95 */         result = hasBufferedData();
/*     */       } finally {
/*  97 */         this.socket.setSoTimeout(oldtimeout);
/*     */       } 
/*     */     } 
/* 100 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEof() {
/* 105 */     return this.eof;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\SocketInputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */