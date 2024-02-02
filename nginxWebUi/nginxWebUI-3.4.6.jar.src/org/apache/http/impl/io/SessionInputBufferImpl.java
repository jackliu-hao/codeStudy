/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import org.apache.http.MessageConstraintException;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.io.BufferInfo;
/*     */ import org.apache.http.io.HttpTransportMetrics;
/*     */ import org.apache.http.io.SessionInputBuffer;
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
/*     */ public class SessionInputBufferImpl
/*     */   implements SessionInputBuffer, BufferInfo
/*     */ {
/*     */   private final HttpTransportMetricsImpl metrics;
/*     */   private final byte[] buffer;
/*     */   private final ByteArrayBuffer lineBuffer;
/*     */   private final int minChunkLimit;
/*     */   private final MessageConstraints constraints;
/*     */   private final CharsetDecoder decoder;
/*     */   private InputStream inStream;
/*     */   private int bufferPos;
/*     */   private int bufferLen;
/*     */   private CharBuffer cbuf;
/*     */   
/*     */   public SessionInputBufferImpl(HttpTransportMetricsImpl metrics, int bufferSize, int minChunkLimit, MessageConstraints constraints, CharsetDecoder charDecoder) {
/*  94 */     Args.notNull(metrics, "HTTP transport metrcis");
/*  95 */     Args.positive(bufferSize, "Buffer size");
/*  96 */     this.metrics = metrics;
/*  97 */     this.buffer = new byte[bufferSize];
/*  98 */     this.bufferPos = 0;
/*  99 */     this.bufferLen = 0;
/* 100 */     this.minChunkLimit = (minChunkLimit >= 0) ? minChunkLimit : 512;
/* 101 */     this.constraints = (constraints != null) ? constraints : MessageConstraints.DEFAULT;
/* 102 */     this.lineBuffer = new ByteArrayBuffer(bufferSize);
/* 103 */     this.decoder = charDecoder;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SessionInputBufferImpl(HttpTransportMetricsImpl metrics, int bufferSize) {
/* 109 */     this(metrics, bufferSize, bufferSize, null, null);
/*     */   }
/*     */   
/*     */   public void bind(InputStream inputStream) {
/* 113 */     this.inStream = inputStream;
/*     */   }
/*     */   
/*     */   public boolean isBound() {
/* 117 */     return (this.inStream != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 122 */     return this.buffer.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int length() {
/* 127 */     return this.bufferLen - this.bufferPos;
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() {
/* 132 */     return capacity() - length();
/*     */   }
/*     */   
/*     */   private int streamRead(byte[] b, int off, int len) throws IOException {
/* 136 */     Asserts.notNull(this.inStream, "Input stream");
/* 137 */     return this.inStream.read(b, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public int fillBuffer() throws IOException {
/* 142 */     if (this.bufferPos > 0) {
/* 143 */       int i = this.bufferLen - this.bufferPos;
/* 144 */       if (i > 0) {
/* 145 */         System.arraycopy(this.buffer, this.bufferPos, this.buffer, 0, i);
/*     */       }
/* 147 */       this.bufferPos = 0;
/* 148 */       this.bufferLen = i;
/*     */     } 
/*     */     
/* 151 */     int off = this.bufferLen;
/* 152 */     int len = this.buffer.length - off;
/* 153 */     int readLen = streamRead(this.buffer, off, len);
/* 154 */     if (readLen == -1) {
/* 155 */       return -1;
/*     */     }
/* 157 */     this.bufferLen = off + readLen;
/* 158 */     this.metrics.incrementBytesTransferred(readLen);
/* 159 */     return readLen;
/*     */   }
/*     */   
/*     */   public boolean hasBufferedData() {
/* 163 */     return (this.bufferPos < this.bufferLen);
/*     */   }
/*     */   
/*     */   public void clear() {
/* 167 */     this.bufferPos = 0;
/* 168 */     this.bufferLen = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 174 */     while (!hasBufferedData()) {
/* 175 */       int noRead = fillBuffer();
/* 176 */       if (noRead == -1) {
/* 177 */         return -1;
/*     */       }
/*     */     } 
/* 180 */     return this.buffer[this.bufferPos++] & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 185 */     if (b == null) {
/* 186 */       return 0;
/*     */     }
/* 188 */     if (hasBufferedData()) {
/* 189 */       int i = Math.min(len, this.bufferLen - this.bufferPos);
/* 190 */       System.arraycopy(this.buffer, this.bufferPos, b, off, i);
/* 191 */       this.bufferPos += i;
/* 192 */       return i;
/*     */     } 
/*     */ 
/*     */     
/* 196 */     if (len > this.minChunkLimit) {
/* 197 */       int readLen = streamRead(b, off, len);
/* 198 */       if (readLen > 0) {
/* 199 */         this.metrics.incrementBytesTransferred(readLen);
/*     */       }
/* 201 */       return readLen;
/*     */     } 
/*     */     
/* 204 */     while (!hasBufferedData()) {
/* 205 */       int noRead = fillBuffer();
/* 206 */       if (noRead == -1) {
/* 207 */         return -1;
/*     */       }
/*     */     } 
/* 210 */     int chunk = Math.min(len, this.bufferLen - this.bufferPos);
/* 211 */     System.arraycopy(this.buffer, this.bufferPos, b, off, chunk);
/* 212 */     this.bufferPos += chunk;
/* 213 */     return chunk;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 218 */     if (b == null) {
/* 219 */       return 0;
/*     */     }
/* 221 */     return read(b, 0, b.length);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int readLine(CharArrayBuffer charbuffer) throws IOException {
/* 241 */     Args.notNull(charbuffer, "Char array buffer");
/* 242 */     int maxLineLen = this.constraints.getMaxLineLength();
/* 243 */     int noRead = 0;
/* 244 */     boolean retry = true;
/* 245 */     while (retry) {
/*     */       
/* 247 */       int pos = -1;
/* 248 */       for (int i = this.bufferPos; i < this.bufferLen; i++) {
/* 249 */         if (this.buffer[i] == 10) {
/* 250 */           pos = i;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 255 */       if (maxLineLen > 0) {
/* 256 */         int currentLen = this.lineBuffer.length() + ((pos >= 0) ? pos : this.bufferLen) - this.bufferPos;
/*     */         
/* 258 */         if (currentLen >= maxLineLen) {
/* 259 */           throw new MessageConstraintException("Maximum line length limit exceeded");
/*     */         }
/*     */       } 
/*     */       
/* 263 */       if (pos != -1) {
/*     */         
/* 265 */         if (this.lineBuffer.isEmpty())
/*     */         {
/* 267 */           return lineFromReadBuffer(charbuffer, pos);
/*     */         }
/* 269 */         retry = false;
/* 270 */         int len = pos + 1 - this.bufferPos;
/* 271 */         this.lineBuffer.append(this.buffer, this.bufferPos, len);
/* 272 */         this.bufferPos = pos + 1;
/*     */         continue;
/*     */       } 
/* 275 */       if (hasBufferedData()) {
/* 276 */         int len = this.bufferLen - this.bufferPos;
/* 277 */         this.lineBuffer.append(this.buffer, this.bufferPos, len);
/* 278 */         this.bufferPos = this.bufferLen;
/*     */       } 
/* 280 */       noRead = fillBuffer();
/* 281 */       if (noRead == -1) {
/* 282 */         retry = false;
/*     */       }
/*     */     } 
/*     */     
/* 286 */     if (noRead == -1 && this.lineBuffer.isEmpty())
/*     */     {
/* 288 */       return -1;
/*     */     }
/* 290 */     return lineFromLineBuffer(charbuffer);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int lineFromLineBuffer(CharArrayBuffer charbuffer) throws IOException {
/* 309 */     int len = this.lineBuffer.length();
/* 310 */     if (len > 0) {
/* 311 */       if (this.lineBuffer.byteAt(len - 1) == 10) {
/* 312 */         len--;
/*     */       }
/*     */       
/* 315 */       if (len > 0 && 
/* 316 */         this.lineBuffer.byteAt(len - 1) == 13) {
/* 317 */         len--;
/*     */       }
/*     */     } 
/*     */     
/* 321 */     if (this.decoder == null) {
/* 322 */       charbuffer.append(this.lineBuffer, 0, len);
/*     */     } else {
/* 324 */       ByteBuffer bbuf = ByteBuffer.wrap(this.lineBuffer.buffer(), 0, len);
/* 325 */       len = appendDecoded(charbuffer, bbuf);
/*     */     } 
/* 327 */     this.lineBuffer.clear();
/* 328 */     return len;
/*     */   }
/*     */ 
/*     */   
/*     */   private int lineFromReadBuffer(CharArrayBuffer charbuffer, int position) throws IOException {
/* 333 */     int pos = position;
/* 334 */     int off = this.bufferPos;
/*     */     
/* 336 */     this.bufferPos = pos + 1;
/* 337 */     if (pos > off && this.buffer[pos - 1] == 13)
/*     */     {
/* 339 */       pos--;
/*     */     }
/* 341 */     int len = pos - off;
/* 342 */     if (this.decoder == null) {
/* 343 */       charbuffer.append(this.buffer, off, len);
/*     */     } else {
/* 345 */       ByteBuffer bbuf = ByteBuffer.wrap(this.buffer, off, len);
/* 346 */       len = appendDecoded(charbuffer, bbuf);
/*     */     } 
/* 348 */     return len;
/*     */   }
/*     */ 
/*     */   
/*     */   private int appendDecoded(CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
/* 353 */     if (!bbuf.hasRemaining()) {
/* 354 */       return 0;
/*     */     }
/* 356 */     if (this.cbuf == null) {
/* 357 */       this.cbuf = CharBuffer.allocate(1024);
/*     */     }
/* 359 */     this.decoder.reset();
/* 360 */     int len = 0;
/* 361 */     while (bbuf.hasRemaining()) {
/* 362 */       CoderResult coderResult = this.decoder.decode(bbuf, this.cbuf, true);
/* 363 */       len += handleDecodingResult(coderResult, charbuffer, bbuf);
/*     */     } 
/* 365 */     CoderResult result = this.decoder.flush(this.cbuf);
/* 366 */     len += handleDecodingResult(result, charbuffer, bbuf);
/* 367 */     this.cbuf.clear();
/* 368 */     return len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int handleDecodingResult(CoderResult result, CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
/* 375 */     if (result.isError()) {
/* 376 */       result.throwException();
/*     */     }
/* 378 */     this.cbuf.flip();
/* 379 */     int len = this.cbuf.remaining();
/* 380 */     while (this.cbuf.hasRemaining()) {
/* 381 */       charbuffer.append(this.cbuf.get());
/*     */     }
/* 383 */     this.cbuf.compact();
/* 384 */     return len;
/*     */   }
/*     */ 
/*     */   
/*     */   public String readLine() throws IOException {
/* 389 */     CharArrayBuffer charbuffer = new CharArrayBuffer(64);
/* 390 */     int readLen = readLine(charbuffer);
/* 391 */     return (readLen != -1) ? charbuffer.toString() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDataAvailable(int timeout) throws IOException {
/* 396 */     return hasBufferedData();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpTransportMetrics getMetrics() {
/* 401 */     return this.metrics;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\SessionInputBufferImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */