/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import org.apache.http.io.BufferInfo;
/*     */ import org.apache.http.io.HttpTransportMetrics;
/*     */ import org.apache.http.io.SessionOutputBuffer;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
/*     */ import org.apache.http.util.ByteArrayBuffer;
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
/*     */ public class SessionOutputBufferImpl
/*     */   implements SessionOutputBuffer, BufferInfo
/*     */ {
/*  58 */   private static final byte[] CRLF = new byte[] { 13, 10 };
/*     */ 
/*     */ 
/*     */   
/*     */   private final HttpTransportMetricsImpl metrics;
/*     */ 
/*     */ 
/*     */   
/*     */   private final ByteArrayBuffer buffer;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int fragementSizeHint;
/*     */ 
/*     */ 
/*     */   
/*     */   private final CharsetEncoder encoder;
/*     */ 
/*     */   
/*     */   private OutputStream outStream;
/*     */ 
/*     */   
/*     */   private ByteBuffer bbuf;
/*     */ 
/*     */ 
/*     */   
/*     */   public SessionOutputBufferImpl(HttpTransportMetricsImpl metrics, int bufferSize, int fragementSizeHint, CharsetEncoder charEncoder) {
/*  85 */     Args.positive(bufferSize, "Buffer size");
/*  86 */     Args.notNull(metrics, "HTTP transport metrcis");
/*  87 */     this.metrics = metrics;
/*  88 */     this.buffer = new ByteArrayBuffer(bufferSize);
/*  89 */     this.fragementSizeHint = (fragementSizeHint >= 0) ? fragementSizeHint : 0;
/*  90 */     this.encoder = charEncoder;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SessionOutputBufferImpl(HttpTransportMetricsImpl metrics, int bufferSize) {
/*  96 */     this(metrics, bufferSize, bufferSize, null);
/*     */   }
/*     */   
/*     */   public void bind(OutputStream outStream) {
/* 100 */     this.outStream = outStream;
/*     */   }
/*     */   
/*     */   public boolean isBound() {
/* 104 */     return (this.outStream != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 109 */     return this.buffer.capacity();
/*     */   }
/*     */ 
/*     */   
/*     */   public int length() {
/* 114 */     return this.buffer.length();
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() {
/* 119 */     return capacity() - length();
/*     */   }
/*     */   
/*     */   private void streamWrite(byte[] b, int off, int len) throws IOException {
/* 123 */     Asserts.notNull(this.outStream, "Output stream");
/* 124 */     this.outStream.write(b, off, len);
/*     */   }
/*     */   
/*     */   private void flushStream() throws IOException {
/* 128 */     if (this.outStream != null) {
/* 129 */       this.outStream.flush();
/*     */     }
/*     */   }
/*     */   
/*     */   private void flushBuffer() throws IOException {
/* 134 */     int len = this.buffer.length();
/* 135 */     if (len > 0) {
/* 136 */       streamWrite(this.buffer.buffer(), 0, len);
/* 137 */       this.buffer.clear();
/* 138 */       this.metrics.incrementBytesTransferred(len);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 144 */     flushBuffer();
/* 145 */     flushStream();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 150 */     if (b == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 156 */     if (len > this.fragementSizeHint || len > this.buffer.capacity()) {
/*     */       
/* 158 */       flushBuffer();
/*     */       
/* 160 */       streamWrite(b, off, len);
/* 161 */       this.metrics.incrementBytesTransferred(len);
/*     */     } else {
/*     */       
/* 164 */       int freecapacity = this.buffer.capacity() - this.buffer.length();
/* 165 */       if (len > freecapacity)
/*     */       {
/* 167 */         flushBuffer();
/*     */       }
/*     */       
/* 170 */       this.buffer.append(b, off, len);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 176 */     if (b == null) {
/*     */       return;
/*     */     }
/* 179 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 184 */     if (this.fragementSizeHint > 0) {
/* 185 */       if (this.buffer.isFull()) {
/* 186 */         flushBuffer();
/*     */       }
/* 188 */       this.buffer.append(b);
/*     */     } else {
/* 190 */       flushBuffer();
/* 191 */       this.outStream.write(b);
/*     */     } 
/*     */   }
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
/*     */   public void writeLine(String s) throws IOException {
/* 206 */     if (s == null) {
/*     */       return;
/*     */     }
/* 209 */     if (s.length() > 0) {
/* 210 */       if (this.encoder == null) {
/* 211 */         for (int i = 0; i < s.length(); i++) {
/* 212 */           write(s.charAt(i));
/*     */         }
/*     */       } else {
/* 215 */         CharBuffer cbuf = CharBuffer.wrap(s);
/* 216 */         writeEncoded(cbuf);
/*     */       } 
/*     */     }
/* 219 */     write(CRLF);
/*     */   }
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
/*     */   public void writeLine(CharArrayBuffer charbuffer) throws IOException {
/* 233 */     if (charbuffer == null) {
/*     */       return;
/*     */     }
/* 236 */     if (this.encoder == null) {
/* 237 */       int off = 0;
/* 238 */       int remaining = charbuffer.length();
/* 239 */       while (remaining > 0) {
/* 240 */         int chunk = this.buffer.capacity() - this.buffer.length();
/* 241 */         chunk = Math.min(chunk, remaining);
/* 242 */         if (chunk > 0) {
/* 243 */           this.buffer.append(charbuffer, off, chunk);
/*     */         }
/* 245 */         if (this.buffer.isFull()) {
/* 246 */           flushBuffer();
/*     */         }
/* 248 */         off += chunk;
/* 249 */         remaining -= chunk;
/*     */       } 
/*     */     } else {
/* 252 */       CharBuffer cbuf = CharBuffer.wrap(charbuffer.buffer(), 0, charbuffer.length());
/* 253 */       writeEncoded(cbuf);
/*     */     } 
/* 255 */     write(CRLF);
/*     */   }
/*     */   
/*     */   private void writeEncoded(CharBuffer cbuf) throws IOException {
/* 259 */     if (!cbuf.hasRemaining()) {
/*     */       return;
/*     */     }
/* 262 */     if (this.bbuf == null) {
/* 263 */       this.bbuf = ByteBuffer.allocate(1024);
/*     */     }
/* 265 */     this.encoder.reset();
/* 266 */     while (cbuf.hasRemaining()) {
/* 267 */       CoderResult coderResult = this.encoder.encode(cbuf, this.bbuf, true);
/* 268 */       handleEncodingResult(coderResult);
/*     */     } 
/* 270 */     CoderResult result = this.encoder.flush(this.bbuf);
/* 271 */     handleEncodingResult(result);
/* 272 */     this.bbuf.clear();
/*     */   }
/*     */   
/*     */   private void handleEncodingResult(CoderResult result) throws IOException {
/* 276 */     if (result.isError()) {
/* 277 */       result.throwException();
/*     */     }
/* 279 */     this.bbuf.flip();
/* 280 */     while (this.bbuf.hasRemaining()) {
/* 281 */       write(this.bbuf.get());
/*     */     }
/* 283 */     this.bbuf.compact();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpTransportMetrics getMetrics() {
/* 288 */     return this.metrics;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\SessionOutputBufferImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */