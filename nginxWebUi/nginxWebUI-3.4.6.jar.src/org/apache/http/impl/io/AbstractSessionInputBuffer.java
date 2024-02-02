/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.io.BufferInfo;
/*     */ import org.apache.http.io.HttpTransportMetrics;
/*     */ import org.apache.http.io.SessionInputBuffer;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Deprecated
/*     */ public abstract class AbstractSessionInputBuffer
/*     */   implements SessionInputBuffer, BufferInfo
/*     */ {
/*     */   private InputStream inStream;
/*     */   private byte[] buffer;
/*     */   private ByteArrayBuffer lineBuffer;
/*     */   private Charset charset;
/*     */   private boolean ascii;
/*     */   private int maxLineLen;
/*     */   private int minChunkLimit;
/*     */   private HttpTransportMetricsImpl metrics;
/*     */   private CodingErrorAction onMalformedCharAction;
/*     */   private CodingErrorAction onUnmappableCharAction;
/*     */   private int bufferPos;
/*     */   private int bufferLen;
/*     */   private CharsetDecoder decoder;
/*     */   private CharBuffer cbuf;
/*     */   
/*     */   protected void init(InputStream inputStream, int bufferSize, HttpParams params) {
/*  94 */     Args.notNull(inputStream, "Input stream");
/*  95 */     Args.notNegative(bufferSize, "Buffer size");
/*  96 */     Args.notNull(params, "HTTP parameters");
/*  97 */     this.inStream = inputStream;
/*  98 */     this.buffer = new byte[bufferSize];
/*  99 */     this.bufferPos = 0;
/* 100 */     this.bufferLen = 0;
/* 101 */     this.lineBuffer = new ByteArrayBuffer(bufferSize);
/* 102 */     String charset = (String)params.getParameter("http.protocol.element-charset");
/* 103 */     this.charset = (charset != null) ? Charset.forName(charset) : Consts.ASCII;
/* 104 */     this.ascii = this.charset.equals(Consts.ASCII);
/* 105 */     this.decoder = null;
/* 106 */     this.maxLineLen = params.getIntParameter("http.connection.max-line-length", -1);
/* 107 */     this.minChunkLimit = params.getIntParameter("http.connection.min-chunk-limit", 512);
/* 108 */     this.metrics = createTransportMetrics();
/* 109 */     CodingErrorAction a1 = (CodingErrorAction)params.getParameter("http.malformed.input.action");
/*     */     
/* 111 */     this.onMalformedCharAction = (a1 != null) ? a1 : CodingErrorAction.REPORT;
/* 112 */     CodingErrorAction a2 = (CodingErrorAction)params.getParameter("http.unmappable.input.action");
/*     */     
/* 114 */     this.onUnmappableCharAction = (a2 != null) ? a2 : CodingErrorAction.REPORT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpTransportMetricsImpl createTransportMetrics() {
/* 121 */     return new HttpTransportMetricsImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 129 */     return this.buffer.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int length() {
/* 137 */     return this.bufferLen - this.bufferPos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() {
/* 145 */     return capacity() - length();
/*     */   }
/*     */ 
/*     */   
/*     */   protected int fillBuffer() throws IOException {
/* 150 */     if (this.bufferPos > 0) {
/* 151 */       int i = this.bufferLen - this.bufferPos;
/* 152 */       if (i > 0) {
/* 153 */         System.arraycopy(this.buffer, this.bufferPos, this.buffer, 0, i);
/*     */       }
/* 155 */       this.bufferPos = 0;
/* 156 */       this.bufferLen = i;
/*     */     } 
/*     */     
/* 159 */     int off = this.bufferLen;
/* 160 */     int len = this.buffer.length - off;
/* 161 */     int readLen = this.inStream.read(this.buffer, off, len);
/* 162 */     if (readLen == -1) {
/* 163 */       return -1;
/*     */     }
/* 165 */     this.bufferLen = off + readLen;
/* 166 */     this.metrics.incrementBytesTransferred(readLen);
/* 167 */     return readLen;
/*     */   }
/*     */   
/*     */   protected boolean hasBufferedData() {
/* 171 */     return (this.bufferPos < this.bufferLen);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 177 */     while (!hasBufferedData()) {
/* 178 */       int noRead = fillBuffer();
/* 179 */       if (noRead == -1) {
/* 180 */         return -1;
/*     */       }
/*     */     } 
/* 183 */     return this.buffer[this.bufferPos++] & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 188 */     if (b == null) {
/* 189 */       return 0;
/*     */     }
/* 191 */     if (hasBufferedData()) {
/* 192 */       int i = Math.min(len, this.bufferLen - this.bufferPos);
/* 193 */       System.arraycopy(this.buffer, this.bufferPos, b, off, i);
/* 194 */       this.bufferPos += i;
/* 195 */       return i;
/*     */     } 
/*     */ 
/*     */     
/* 199 */     if (len > this.minChunkLimit) {
/* 200 */       int read = this.inStream.read(b, off, len);
/* 201 */       if (read > 0) {
/* 202 */         this.metrics.incrementBytesTransferred(read);
/*     */       }
/* 204 */       return read;
/*     */     } 
/*     */     
/* 207 */     while (!hasBufferedData()) {
/* 208 */       int noRead = fillBuffer();
/* 209 */       if (noRead == -1) {
/* 210 */         return -1;
/*     */       }
/*     */     } 
/* 213 */     int chunk = Math.min(len, this.bufferLen - this.bufferPos);
/* 214 */     System.arraycopy(this.buffer, this.bufferPos, b, off, chunk);
/* 215 */     this.bufferPos += chunk;
/* 216 */     return chunk;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 221 */     if (b == null) {
/* 222 */       return 0;
/*     */     }
/* 224 */     return read(b, 0, b.length);
/*     */   }
/*     */   
/*     */   private int locateLF() {
/* 228 */     for (int i = this.bufferPos; i < this.bufferLen; i++) {
/* 229 */       if (this.buffer[i] == 10) {
/* 230 */         return i;
/*     */       }
/*     */     } 
/* 233 */     return -1;
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
/* 253 */     Args.notNull(charbuffer, "Char array buffer");
/* 254 */     int noRead = 0;
/* 255 */     boolean retry = true;
/* 256 */     while (retry) {
/*     */       
/* 258 */       int i = locateLF();
/* 259 */       if (i != -1) {
/*     */         
/* 261 */         if (this.lineBuffer.isEmpty())
/*     */         {
/* 263 */           return lineFromReadBuffer(charbuffer, i);
/*     */         }
/* 265 */         retry = false;
/* 266 */         int len = i + 1 - this.bufferPos;
/* 267 */         this.lineBuffer.append(this.buffer, this.bufferPos, len);
/* 268 */         this.bufferPos = i + 1;
/*     */       } else {
/*     */         
/* 271 */         if (hasBufferedData()) {
/* 272 */           int len = this.bufferLen - this.bufferPos;
/* 273 */           this.lineBuffer.append(this.buffer, this.bufferPos, len);
/* 274 */           this.bufferPos = this.bufferLen;
/*     */         } 
/* 276 */         noRead = fillBuffer();
/* 277 */         if (noRead == -1) {
/* 278 */           retry = false;
/*     */         }
/*     */       } 
/* 281 */       if (this.maxLineLen > 0 && this.lineBuffer.length() >= this.maxLineLen) {
/* 282 */         throw new IOException("Maximum line length limit exceeded");
/*     */       }
/*     */     } 
/* 285 */     if (noRead == -1 && this.lineBuffer.isEmpty())
/*     */     {
/* 287 */       return -1;
/*     */     }
/* 289 */     return lineFromLineBuffer(charbuffer);
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
/* 308 */     int len = this.lineBuffer.length();
/* 309 */     if (len > 0) {
/* 310 */       if (this.lineBuffer.byteAt(len - 1) == 10) {
/* 311 */         len--;
/*     */       }
/*     */       
/* 314 */       if (len > 0 && 
/* 315 */         this.lineBuffer.byteAt(len - 1) == 13) {
/* 316 */         len--;
/*     */       }
/*     */     } 
/*     */     
/* 320 */     if (this.ascii) {
/* 321 */       charbuffer.append(this.lineBuffer, 0, len);
/*     */     } else {
/* 323 */       ByteBuffer bbuf = ByteBuffer.wrap(this.lineBuffer.buffer(), 0, len);
/* 324 */       len = appendDecoded(charbuffer, bbuf);
/*     */     } 
/* 326 */     this.lineBuffer.clear();
/* 327 */     return len;
/*     */   }
/*     */ 
/*     */   
/*     */   private int lineFromReadBuffer(CharArrayBuffer charbuffer, int position) throws IOException {
/* 332 */     int off = this.bufferPos;
/* 333 */     int i = position;
/* 334 */     this.bufferPos = i + 1;
/* 335 */     if (i > off && this.buffer[i - 1] == 13)
/*     */     {
/* 337 */       i--;
/*     */     }
/* 339 */     int len = i - off;
/* 340 */     if (this.ascii) {
/* 341 */       charbuffer.append(this.buffer, off, len);
/*     */     } else {
/* 343 */       ByteBuffer bbuf = ByteBuffer.wrap(this.buffer, off, len);
/* 344 */       len = appendDecoded(charbuffer, bbuf);
/*     */     } 
/* 346 */     return len;
/*     */   }
/*     */ 
/*     */   
/*     */   private int appendDecoded(CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
/* 351 */     if (!bbuf.hasRemaining()) {
/* 352 */       return 0;
/*     */     }
/* 354 */     if (this.decoder == null) {
/* 355 */       this.decoder = this.charset.newDecoder();
/* 356 */       this.decoder.onMalformedInput(this.onMalformedCharAction);
/* 357 */       this.decoder.onUnmappableCharacter(this.onUnmappableCharAction);
/*     */     } 
/* 359 */     if (this.cbuf == null) {
/* 360 */       this.cbuf = CharBuffer.allocate(1024);
/*     */     }
/* 362 */     this.decoder.reset();
/* 363 */     int len = 0;
/* 364 */     while (bbuf.hasRemaining()) {
/* 365 */       CoderResult coderResult = this.decoder.decode(bbuf, this.cbuf, true);
/* 366 */       len += handleDecodingResult(coderResult, charbuffer, bbuf);
/*     */     } 
/* 368 */     CoderResult result = this.decoder.flush(this.cbuf);
/* 369 */     len += handleDecodingResult(result, charbuffer, bbuf);
/* 370 */     this.cbuf.clear();
/* 371 */     return len;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int handleDecodingResult(CoderResult result, CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
/* 378 */     if (result.isError()) {
/* 379 */       result.throwException();
/*     */     }
/* 381 */     this.cbuf.flip();
/* 382 */     int len = this.cbuf.remaining();
/* 383 */     while (this.cbuf.hasRemaining()) {
/* 384 */       charbuffer.append(this.cbuf.get());
/*     */     }
/* 386 */     this.cbuf.compact();
/* 387 */     return len;
/*     */   }
/*     */ 
/*     */   
/*     */   public String readLine() throws IOException {
/* 392 */     CharArrayBuffer charbuffer = new CharArrayBuffer(64);
/* 393 */     int readLen = readLine(charbuffer);
/* 394 */     if (readLen != -1) {
/* 395 */       return charbuffer.toString();
/*     */     }
/* 397 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpTransportMetrics getMetrics() {
/* 402 */     return this.metrics;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\AbstractSessionInputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */