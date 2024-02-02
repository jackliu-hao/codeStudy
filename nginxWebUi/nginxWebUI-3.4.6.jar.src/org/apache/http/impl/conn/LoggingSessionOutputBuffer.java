/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.io.HttpTransportMetrics;
/*     */ import org.apache.http.io.SessionOutputBuffer;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class LoggingSessionOutputBuffer
/*     */   implements SessionOutputBuffer
/*     */ {
/*     */   private final SessionOutputBuffer out;
/*     */   private final Wire wire;
/*     */   private final String charset;
/*     */   
/*     */   public LoggingSessionOutputBuffer(SessionOutputBuffer out, Wire wire, String charset) {
/*  64 */     this.out = out;
/*  65 */     this.wire = wire;
/*  66 */     this.charset = (charset != null) ? charset : Consts.ASCII.name();
/*     */   }
/*     */   
/*     */   public LoggingSessionOutputBuffer(SessionOutputBuffer out, Wire wire) {
/*  70 */     this(out, wire, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  75 */     this.out.write(b, off, len);
/*  76 */     if (this.wire.enabled()) {
/*  77 */       this.wire.output(b, off, len);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/*  83 */     this.out.write(b);
/*  84 */     if (this.wire.enabled()) {
/*  85 */       this.wire.output(b);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/*  91 */     this.out.write(b);
/*  92 */     if (this.wire.enabled()) {
/*  93 */       this.wire.output(b);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/*  99 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeLine(CharArrayBuffer buffer) throws IOException {
/* 104 */     this.out.writeLine(buffer);
/* 105 */     if (this.wire.enabled()) {
/* 106 */       String s = new String(buffer.buffer(), 0, buffer.length());
/* 107 */       String tmp = s + "\r\n";
/* 108 */       this.wire.output(tmp.getBytes(this.charset));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeLine(String s) throws IOException {
/* 114 */     this.out.writeLine(s);
/* 115 */     if (this.wire.enabled()) {
/* 116 */       String tmp = s + "\r\n";
/* 117 */       this.wire.output(tmp.getBytes(this.charset));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpTransportMetrics getMetrics() {
/* 123 */     return this.out.getMetrics();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\LoggingSessionOutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */