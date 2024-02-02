/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CoderResult;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.io.BufferInfo;
/*     */ import org.apache.http.io.HttpTransportMetrics;
/*     */ import org.apache.http.io.SessionOutputBuffer;
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
/*     */ @Deprecated
/*     */ public abstract class AbstractSessionOutputBuffer
/*     */   implements SessionOutputBuffer, BufferInfo
/*     */ {
/*  66 */   private static final byte[] CRLF = new byte[] { 13, 10 };
/*     */   
/*     */   private OutputStream outStream;
/*     */   
/*     */   private ByteArrayBuffer buffer;
/*     */   
/*     */   private Charset charset;
/*     */   
/*     */   private boolean ascii;
/*     */   
/*     */   private int minChunkLimit;
/*     */   
/*     */   private HttpTransportMetricsImpl metrics;
/*     */   
/*     */   private CodingErrorAction onMalformedCharAction;
/*     */   
/*     */   private CodingErrorAction onUnmappableCharAction;
/*     */   
/*     */   private CharsetEncoder encoder;
/*     */   private ByteBuffer bbuf;
/*     */   
/*     */   protected AbstractSessionOutputBuffer(OutputStream outStream, int bufferSize, Charset charset, int minChunkLimit, CodingErrorAction malformedCharAction, CodingErrorAction unmappableCharAction) {
/*  88 */     Args.notNull(outStream, "Input stream");
/*  89 */     Args.notNegative(bufferSize, "Buffer size");
/*  90 */     this.outStream = outStream;
/*  91 */     this.buffer = new ByteArrayBuffer(bufferSize);
/*  92 */     this.charset = (charset != null) ? charset : Consts.ASCII;
/*  93 */     this.ascii = this.charset.equals(Consts.ASCII);
/*  94 */     this.encoder = null;
/*  95 */     this.minChunkLimit = (minChunkLimit >= 0) ? minChunkLimit : 512;
/*  96 */     this.metrics = createTransportMetrics();
/*  97 */     this.onMalformedCharAction = (malformedCharAction != null) ? malformedCharAction : CodingErrorAction.REPORT;
/*     */     
/*  99 */     this.onUnmappableCharAction = (unmappableCharAction != null) ? unmappableCharAction : CodingErrorAction.REPORT;
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractSessionOutputBuffer() {}
/*     */ 
/*     */   
/*     */   protected void init(OutputStream outStream, int bufferSize, HttpParams params) {
/* 107 */     Args.notNull(outStream, "Input stream");
/* 108 */     Args.notNegative(bufferSize, "Buffer size");
/* 109 */     Args.notNull(params, "HTTP parameters");
/* 110 */     this.outStream = outStream;
/* 111 */     this.buffer = new ByteArrayBuffer(bufferSize);
/* 112 */     String charset = (String)params.getParameter("http.protocol.element-charset");
/* 113 */     this.charset = (charset != null) ? Charset.forName(charset) : Consts.ASCII;
/* 114 */     this.ascii = this.charset.equals(Consts.ASCII);
/* 115 */     this.encoder = null;
/* 116 */     this.minChunkLimit = params.getIntParameter("http.connection.min-chunk-limit", 512);
/* 117 */     this.metrics = createTransportMetrics();
/* 118 */     CodingErrorAction a1 = (CodingErrorAction)params.getParameter("http.malformed.input.action");
/*     */     
/* 120 */     this.onMalformedCharAction = (a1 != null) ? a1 : CodingErrorAction.REPORT;
/* 121 */     CodingErrorAction a2 = (CodingErrorAction)params.getParameter("http.unmappable.input.action");
/*     */     
/* 123 */     this.onUnmappableCharAction = (a2 != null) ? a2 : CodingErrorAction.REPORT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpTransportMetricsImpl createTransportMetrics() {
/* 130 */     return new HttpTransportMetricsImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int capacity() {
/* 138 */     return this.buffer.capacity();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int length() {
/* 146 */     return this.buffer.length();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() {
/* 154 */     return capacity() - length();
/*     */   }
/*     */   
/*     */   protected void flushBuffer() throws IOException {
/* 158 */     int len = this.buffer.length();
/* 159 */     if (len > 0) {
/* 160 */       this.outStream.write(this.buffer.buffer(), 0, len);
/* 161 */       this.buffer.clear();
/* 162 */       this.metrics.incrementBytesTransferred(len);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 168 */     flushBuffer();
/* 169 */     this.outStream.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 174 */     if (b == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 180 */     if (len > this.minChunkLimit || len > this.buffer.capacity()) {
/*     */       
/* 182 */       flushBuffer();
/*     */       
/* 184 */       this.outStream.write(b, off, len);
/* 185 */       this.metrics.incrementBytesTransferred(len);
/*     */     } else {
/*     */       
/* 188 */       int freecapacity = this.buffer.capacity() - this.buffer.length();
/* 189 */       if (len > freecapacity)
/*     */       {
/* 191 */         flushBuffer();
/*     */       }
/*     */       
/* 194 */       this.buffer.append(b, off, len);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 200 */     if (b == null) {
/*     */       return;
/*     */     }
/* 203 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 208 */     if (this.buffer.isFull()) {
/* 209 */       flushBuffer();
/*     */     }
/* 211 */     this.buffer.append(b);
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
/* 225 */     if (s == null) {
/*     */       return;
/*     */     }
/* 228 */     if (s.length() > 0) {
/* 229 */       if (this.ascii) {
/* 230 */         for (int i = 0; i < s.length(); i++) {
/* 231 */           write(s.charAt(i));
/*     */         }
/*     */       } else {
/* 234 */         CharBuffer cbuf = CharBuffer.wrap(s);
/* 235 */         writeEncoded(cbuf);
/*     */       } 
/*     */     }
/* 238 */     write(CRLF);
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
/* 252 */     if (charbuffer == null) {
/*     */       return;
/*     */     }
/* 255 */     if (this.ascii) {
/* 256 */       int off = 0;
/* 257 */       int remaining = charbuffer.length();
/* 258 */       while (remaining > 0) {
/* 259 */         int chunk = this.buffer.capacity() - this.buffer.length();
/* 260 */         chunk = Math.min(chunk, remaining);
/* 261 */         if (chunk > 0) {
/* 262 */           this.buffer.append(charbuffer, off, chunk);
/*     */         }
/* 264 */         if (this.buffer.isFull()) {
/* 265 */           flushBuffer();
/*     */         }
/* 267 */         off += chunk;
/* 268 */         remaining -= chunk;
/*     */       } 
/*     */     } else {
/* 271 */       CharBuffer cbuf = CharBuffer.wrap(charbuffer.buffer(), 0, charbuffer.length());
/* 272 */       writeEncoded(cbuf);
/*     */     } 
/* 274 */     write(CRLF);
/*     */   }
/*     */   
/*     */   private void writeEncoded(CharBuffer cbuf) throws IOException {
/* 278 */     if (!cbuf.hasRemaining()) {
/*     */       return;
/*     */     }
/* 281 */     if (this.encoder == null) {
/* 282 */       this.encoder = this.charset.newEncoder();
/* 283 */       this.encoder.onMalformedInput(this.onMalformedCharAction);
/* 284 */       this.encoder.onUnmappableCharacter(this.onUnmappableCharAction);
/*     */     } 
/* 286 */     if (this.bbuf == null) {
/* 287 */       this.bbuf = ByteBuffer.allocate(1024);
/*     */     }
/* 289 */     this.encoder.reset();
/* 290 */     while (cbuf.hasRemaining()) {
/* 291 */       CoderResult coderResult = this.encoder.encode(cbuf, this.bbuf, true);
/* 292 */       handleEncodingResult(coderResult);
/*     */     } 
/* 294 */     CoderResult result = this.encoder.flush(this.bbuf);
/* 295 */     handleEncodingResult(result);
/* 296 */     this.bbuf.clear();
/*     */   }
/*     */   
/*     */   private void handleEncodingResult(CoderResult result) throws IOException {
/* 300 */     if (result.isError()) {
/* 301 */       result.throwException();
/*     */     }
/* 303 */     this.bbuf.flip();
/* 304 */     while (this.bbuf.hasRemaining()) {
/* 305 */       write(this.bbuf.get());
/*     */     }
/* 307 */     this.bbuf.compact();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpTransportMetrics getMetrics() {
/* 312 */     return this.metrics;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\AbstractSessionOutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */