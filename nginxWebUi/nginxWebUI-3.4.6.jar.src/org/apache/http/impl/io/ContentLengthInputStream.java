/*     */ package org.apache.http.impl.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.http.ConnectionClosedException;
/*     */ import org.apache.http.io.BufferInfo;
/*     */ import org.apache.http.io.SessionInputBuffer;
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
/*     */ public class ContentLengthInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private static final int BUFFER_SIZE = 2048;
/*     */   private final long contentLength;
/*  64 */   private long pos = 0L;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean closed = false;
/*     */ 
/*     */ 
/*     */   
/*  72 */   private SessionInputBuffer in = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ContentLengthInputStream(SessionInputBuffer in, long contentLength) {
/*  84 */     this.in = (SessionInputBuffer)Args.notNull(in, "Session input buffer");
/*  85 */     this.contentLength = Args.notNegative(contentLength, "Content length");
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
/*     */   public void close() throws IOException {
/*  97 */     if (!this.closed) {
/*     */       try {
/*  99 */         if (this.pos < this.contentLength) {
/* 100 */           byte[] buffer = new byte[2048];
/* 101 */           while (read(buffer) >= 0);
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */       finally {
/*     */         
/* 108 */         this.closed = true;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 115 */     if (this.in instanceof BufferInfo) {
/* 116 */       int len = ((BufferInfo)this.in).length();
/* 117 */       return Math.min(len, (int)(this.contentLength - this.pos));
/*     */     } 
/* 119 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 130 */     if (this.closed) {
/* 131 */       throw new IOException("Attempted read from closed stream.");
/*     */     }
/*     */     
/* 134 */     if (this.pos >= this.contentLength) {
/* 135 */       return -1;
/*     */     }
/* 137 */     int b = this.in.read();
/* 138 */     if (b == -1) {
/* 139 */       if (this.pos < this.contentLength) {
/* 140 */         throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: %,d; received: %,d)", new Object[] { Long.valueOf(this.contentLength), Long.valueOf(this.pos) });
/*     */       }
/*     */     }
/*     */     else {
/*     */       
/* 145 */       this.pos++;
/*     */     } 
/* 147 */     return b;
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
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 164 */     if (this.closed) {
/* 165 */       throw new IOException("Attempted read from closed stream.");
/*     */     }
/*     */     
/* 168 */     if (this.pos >= this.contentLength) {
/* 169 */       return -1;
/*     */     }
/*     */     
/* 172 */     int chunk = len;
/* 173 */     if (this.pos + len > this.contentLength) {
/* 174 */       chunk = (int)(this.contentLength - this.pos);
/*     */     }
/* 176 */     int readLen = this.in.read(b, off, chunk);
/* 177 */     if (readLen == -1 && this.pos < this.contentLength) {
/* 178 */       throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: %,d; received: %,d)", new Object[] { Long.valueOf(this.contentLength), Long.valueOf(this.pos) });
/*     */     }
/*     */ 
/*     */     
/* 182 */     if (readLen > 0) {
/* 183 */       this.pos += readLen;
/*     */     }
/* 185 */     return readLen;
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
/* 198 */     return read(b, 0, b.length);
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
/*     */   public long skip(long n) throws IOException {
/* 211 */     if (n <= 0L) {
/* 212 */       return 0L;
/*     */     }
/* 214 */     byte[] buffer = new byte[2048];
/*     */ 
/*     */     
/* 217 */     long remaining = Math.min(n, this.contentLength - this.pos);
/*     */     
/* 219 */     long count = 0L;
/* 220 */     while (remaining > 0L) {
/* 221 */       int readLen = read(buffer, 0, (int)Math.min(2048L, remaining));
/* 222 */       if (readLen == -1) {
/*     */         break;
/*     */       }
/* 225 */       count += readLen;
/* 226 */       remaining -= readLen;
/*     */     } 
/* 228 */     return count;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\ContentLengthInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */