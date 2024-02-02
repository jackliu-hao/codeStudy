/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.io.EofSensor;
/*     */ import org.apache.http.io.HttpTransportMetrics;
/*     */ import org.apache.http.io.SessionInputBuffer;
/*     */ import org.apache.http.util.CharArrayBuffer;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class LoggingSessionInputBuffer
/*     */   implements SessionInputBuffer, EofSensor
/*     */ {
/*     */   private final SessionInputBuffer in;
/*     */   private final EofSensor eofSensor;
/*     */   private final Wire wire;
/*     */   private final String charset;
/*     */   
/*     */   public LoggingSessionInputBuffer(SessionInputBuffer in, Wire wire, String charset) {
/*  69 */     this.in = in;
/*  70 */     this.eofSensor = (in instanceof EofSensor) ? (EofSensor)in : null;
/*  71 */     this.wire = wire;
/*  72 */     this.charset = (charset != null) ? charset : Consts.ASCII.name();
/*     */   }
/*     */   
/*     */   public LoggingSessionInputBuffer(SessionInputBuffer in, Wire wire) {
/*  76 */     this(in, wire, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDataAvailable(int timeout) throws IOException {
/*  81 */     return this.in.isDataAvailable(timeout);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  86 */     int readLen = this.in.read(b, off, len);
/*  87 */     if (this.wire.enabled() && readLen > 0) {
/*  88 */       this.wire.input(b, off, readLen);
/*     */     }
/*  90 */     return readLen;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  95 */     int b = this.in.read();
/*  96 */     if (this.wire.enabled() && b != -1) {
/*  97 */       this.wire.input(b);
/*     */     }
/*  99 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 104 */     int readLen = this.in.read(b);
/* 105 */     if (this.wire.enabled() && readLen > 0) {
/* 106 */       this.wire.input(b, 0, readLen);
/*     */     }
/* 108 */     return readLen;
/*     */   }
/*     */ 
/*     */   
/*     */   public String readLine() throws IOException {
/* 113 */     String s = this.in.readLine();
/* 114 */     if (this.wire.enabled() && s != null) {
/* 115 */       String tmp = s + "\r\n";
/* 116 */       this.wire.input(tmp.getBytes(this.charset));
/*     */     } 
/* 118 */     return s;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readLine(CharArrayBuffer buffer) throws IOException {
/* 123 */     int readLen = this.in.readLine(buffer);
/* 124 */     if (this.wire.enabled() && readLen >= 0) {
/* 125 */       int pos = buffer.length() - readLen;
/* 126 */       String s = new String(buffer.buffer(), pos, readLen);
/* 127 */       String tmp = s + "\r\n";
/* 128 */       this.wire.input(tmp.getBytes(this.charset));
/*     */     } 
/* 130 */     return readLen;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpTransportMetrics getMetrics() {
/* 135 */     return this.in.getMetrics();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEof() {
/* 140 */     if (this.eofSensor != null) {
/* 141 */       return this.eofSensor.isEof();
/*     */     }
/* 143 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\LoggingSessionInputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */