/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.http.ConnectionClosedException;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.MalformedChunkCodingException;
/*     */ import org.apache.http.TruncatedChunkException;
/*     */ import org.apache.http.config.MessageConstraints;
/*     */ import org.apache.http.io.BufferInfo;
/*     */ import org.apache.http.io.SessionInputBuffer;
/*     */ import org.apache.http.util.Args;
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
/*     */ public class ChunkedInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private static final int CHUNK_LEN = 1;
/*     */   private static final int CHUNK_DATA = 2;
/*     */   private static final int CHUNK_CRLF = 3;
/*     */   private static final int CHUNK_INVALID = 2147483647;
/*     */   private static final int BUFFER_SIZE = 2048;
/*     */   private final SessionInputBuffer in;
/*     */   private final CharArrayBuffer buffer;
/*     */   private final MessageConstraints constraints;
/*     */   private int state;
/*     */   private long chunkSize;
/*     */   private long pos;
/*     */   private boolean eof = false;
/*     */   private boolean closed = false;
/*  88 */   private Header[] footers = new Header[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedInputStream(SessionInputBuffer in, MessageConstraints constraints) {
/* 101 */     this.in = (SessionInputBuffer)Args.notNull(in, "Session input buffer");
/* 102 */     this.pos = 0L;
/* 103 */     this.buffer = new CharArrayBuffer(16);
/* 104 */     this.constraints = (constraints != null) ? constraints : MessageConstraints.DEFAULT;
/* 105 */     this.state = 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedInputStream(SessionInputBuffer in) {
/* 114 */     this(in, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 119 */     if (this.in instanceof BufferInfo) {
/* 120 */       int len = ((BufferInfo)this.in).length();
/* 121 */       return (int)Math.min(len, this.chunkSize - this.pos);
/*     */     } 
/* 123 */     return 0;
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
/*     */   public int read() throws IOException {
/* 140 */     if (this.closed) {
/* 141 */       throw new IOException("Attempted read from closed stream.");
/*     */     }
/* 143 */     if (this.eof) {
/* 144 */       return -1;
/*     */     }
/* 146 */     if (this.state != 2) {
/* 147 */       nextChunk();
/* 148 */       if (this.eof) {
/* 149 */         return -1;
/*     */       }
/*     */     } 
/* 152 */     int b = this.in.read();
/* 153 */     if (b != -1) {
/* 154 */       this.pos++;
/* 155 */       if (this.pos >= this.chunkSize) {
/* 156 */         this.state = 3;
/*     */       }
/*     */     } 
/* 159 */     return b;
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
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 175 */     if (this.closed) {
/* 176 */       throw new IOException("Attempted read from closed stream.");
/*     */     }
/*     */     
/* 179 */     if (this.eof) {
/* 180 */       return -1;
/*     */     }
/* 182 */     if (this.state != 2) {
/* 183 */       nextChunk();
/* 184 */       if (this.eof) {
/* 185 */         return -1;
/*     */       }
/*     */     } 
/* 188 */     int readLen = this.in.read(b, off, (int)Math.min(len, this.chunkSize - this.pos));
/* 189 */     if (readLen != -1) {
/* 190 */       this.pos += readLen;
/* 191 */       if (this.pos >= this.chunkSize) {
/* 192 */         this.state = 3;
/*     */       }
/* 194 */       return readLen;
/*     */     } 
/* 196 */     this.eof = true;
/* 197 */     throw new TruncatedChunkException("Truncated chunk (expected size: %,d; actual size: %,d)", new Object[] { Long.valueOf(this.chunkSize), Long.valueOf(this.pos) });
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
/*     */   public int read(byte[] b) throws IOException {
/* 210 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void nextChunk() throws IOException {
/* 218 */     if (this.state == Integer.MAX_VALUE) {
/* 219 */       throw new MalformedChunkCodingException("Corrupt data stream");
/*     */     }
/*     */     try {
/* 222 */       this.chunkSize = getChunkSize();
/* 223 */       if (this.chunkSize < 0L) {
/* 224 */         throw new MalformedChunkCodingException("Negative chunk size");
/*     */       }
/* 226 */       this.state = 2;
/* 227 */       this.pos = 0L;
/* 228 */       if (this.chunkSize == 0L) {
/* 229 */         this.eof = true;
/* 230 */         parseTrailerHeaders();
/*     */       } 
/* 232 */     } catch (MalformedChunkCodingException ex) {
/* 233 */       this.state = Integer.MAX_VALUE;
/* 234 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long getChunkSize() throws IOException {
/*     */     int bytesRead1, bytesRead2, separator;
/*     */     String s;
/* 244 */     int st = this.state;
/* 245 */     switch (st) {
/*     */       case 3:
/* 247 */         this.buffer.clear();
/* 248 */         bytesRead1 = this.in.readLine(this.buffer);
/* 249 */         if (bytesRead1 == -1) {
/* 250 */           throw new MalformedChunkCodingException("CRLF expected at end of chunk");
/*     */         }
/*     */         
/* 253 */         if (!this.buffer.isEmpty()) {
/* 254 */           throw new MalformedChunkCodingException("Unexpected content at the end of chunk");
/*     */         }
/*     */         
/* 257 */         this.state = 1;
/*     */       
/*     */       case 1:
/* 260 */         this.buffer.clear();
/* 261 */         bytesRead2 = this.in.readLine(this.buffer);
/* 262 */         if (bytesRead2 == -1) {
/* 263 */           throw new ConnectionClosedException("Premature end of chunk coded message body: closing chunk expected");
/*     */         }
/*     */         
/* 266 */         separator = this.buffer.indexOf(59);
/* 267 */         if (separator < 0) {
/* 268 */           separator = this.buffer.length();
/*     */         }
/* 270 */         s = this.buffer.substringTrimmed(0, separator);
/*     */         try {
/* 272 */           return Long.parseLong(s, 16);
/* 273 */         } catch (NumberFormatException e) {
/* 274 */           throw new MalformedChunkCodingException("Bad chunk header: " + s);
/*     */         } 
/*     */     } 
/* 277 */     throw new IllegalStateException("Inconsistent codec state");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseTrailerHeaders() throws IOException {
/*     */     try {
/* 287 */       this.footers = AbstractMessageParser.parseHeaders(this.in, this.constraints.getMaxHeaderCount(), this.constraints.getMaxLineLength(), null);
/*     */ 
/*     */     
/*     */     }
/* 291 */     catch (HttpException ex) {
/* 292 */       MalformedChunkCodingException malformedChunkCodingException = new MalformedChunkCodingException("Invalid footer: " + ex.getMessage());
/*     */       
/* 294 */       malformedChunkCodingException.initCause((Throwable)ex);
/* 295 */       throw malformedChunkCodingException;
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
/*     */   public void close() throws IOException {
/* 307 */     if (!this.closed) {
/*     */       try {
/* 309 */         if (!this.eof && this.state != Integer.MAX_VALUE) {
/*     */           
/* 311 */           byte[] buff = new byte[2048];
/* 312 */           while (read(buff) >= 0);
/*     */         } 
/*     */       } finally {
/*     */         
/* 316 */         this.eof = true;
/* 317 */         this.closed = true;
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public Header[] getFooters() {
/* 323 */     return (Header[])this.footers.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\ChunkedInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */