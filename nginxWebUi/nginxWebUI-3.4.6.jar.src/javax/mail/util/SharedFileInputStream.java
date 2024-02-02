/*     */ package javax.mail.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import javax.mail.internet.SharedInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SharedFileInputStream
/*     */   extends BufferedInputStream
/*     */   implements SharedInputStream
/*     */ {
/*  71 */   private static int defaultBufferSize = 2048;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected RandomAccessFile in;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int bufsize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long bufpos;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  93 */   protected long start = 0L;
/*     */ 
/*     */   
/*     */   protected long datalen;
/*     */ 
/*     */   
/*     */   private boolean master = true;
/*     */ 
/*     */   
/*     */   private SharedFile sf;
/*     */ 
/*     */ 
/*     */   
/*     */   static class SharedFile
/*     */   {
/*     */     private int cnt;
/*     */ 
/*     */     
/*     */     private RandomAccessFile in;
/*     */ 
/*     */ 
/*     */     
/*     */     SharedFile(String file) throws IOException {
/* 116 */       this.in = new RandomAccessFile(file, "r");
/*     */     }
/*     */     
/*     */     SharedFile(File file) throws IOException {
/* 120 */       this.in = new RandomAccessFile(file, "r");
/*     */     }
/*     */     
/*     */     public synchronized RandomAccessFile open() {
/* 124 */       this.cnt++;
/* 125 */       return this.in;
/*     */     }
/*     */     
/*     */     public synchronized void close() throws IOException {
/* 129 */       if (this.cnt > 0 && --this.cnt <= 0)
/* 130 */         this.in.close(); 
/*     */     }
/*     */     
/*     */     public synchronized void forceClose() throws IOException {
/* 134 */       if (this.cnt > 0) {
/*     */         
/* 136 */         this.cnt = 0;
/* 137 */         this.in.close();
/*     */       } else {
/*     */         
/*     */         try {
/* 141 */           this.in.close();
/* 142 */         } catch (IOException ioex) {}
/*     */       } 
/*     */     }
/*     */     
/*     */     protected void finalize() throws Throwable {
/* 147 */       super.finalize();
/* 148 */       this.in.close();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureOpen() throws IOException {
/* 158 */     if (this.in == null) {
/* 159 */       throw new IOException("Stream closed");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SharedFileInputStream(File file) throws IOException {
/* 169 */     this(file, defaultBufferSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SharedFileInputStream(String file) throws IOException {
/* 179 */     this(file, defaultBufferSize);
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
/*     */   public SharedFileInputStream(File file, int size) throws IOException {
/* 191 */     super(null);
/* 192 */     if (size <= 0)
/* 193 */       throw new IllegalArgumentException("Buffer size <= 0"); 
/* 194 */     init(new SharedFile(file), size);
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
/*     */   public SharedFileInputStream(String file, int size) throws IOException {
/* 206 */     super(null);
/* 207 */     if (size <= 0)
/* 208 */       throw new IllegalArgumentException("Buffer size <= 0"); 
/* 209 */     init(new SharedFile(file), size);
/*     */   }
/*     */   
/*     */   private void init(SharedFile sf, int size) throws IOException {
/* 213 */     this.sf = sf;
/* 214 */     this.in = sf.open();
/* 215 */     this.start = 0L;
/* 216 */     this.datalen = this.in.length();
/* 217 */     this.bufsize = size;
/* 218 */     this.buf = new byte[size];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SharedFileInputStream(SharedFile sf, long start, long len, int bufsize) {
/* 226 */     super(null);
/* 227 */     this.master = false;
/* 228 */     this.sf = sf;
/* 229 */     this.in = sf.open();
/* 230 */     this.start = start;
/* 231 */     this.bufpos = start;
/* 232 */     this.datalen = len;
/* 233 */     this.bufsize = bufsize;
/* 234 */     this.buf = new byte[bufsize];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fill() throws IOException {
/* 245 */     if (this.markpos < 0) {
/* 246 */       this.pos = 0;
/* 247 */       this.bufpos += this.count;
/* 248 */     } else if (this.pos >= this.buf.length) {
/* 249 */       if (this.markpos > 0) {
/* 250 */         int sz = this.pos - this.markpos;
/* 251 */         System.arraycopy(this.buf, this.markpos, this.buf, 0, sz);
/* 252 */         this.pos = sz;
/* 253 */         this.bufpos += this.markpos;
/* 254 */         this.markpos = 0;
/* 255 */       } else if (this.buf.length >= this.marklimit) {
/* 256 */         this.markpos = -1;
/* 257 */         this.pos = 0;
/* 258 */         this.bufpos += this.count;
/*     */       } else {
/* 260 */         int nsz = this.pos * 2;
/* 261 */         if (nsz > this.marklimit)
/* 262 */           nsz = this.marklimit; 
/* 263 */         byte[] nbuf = new byte[nsz];
/* 264 */         System.arraycopy(this.buf, 0, nbuf, 0, this.pos);
/* 265 */         this.buf = nbuf;
/*     */       } 
/* 267 */     }  this.count = this.pos;
/* 268 */     this.in.seek(this.bufpos + this.pos);
/*     */     
/* 270 */     int len = this.buf.length - this.pos;
/* 271 */     if (this.bufpos - this.start + this.pos + len > this.datalen)
/* 272 */       len = (int)(this.datalen - this.bufpos - this.start + this.pos); 
/* 273 */     int n = this.in.read(this.buf, this.pos, len);
/* 274 */     if (n > 0) {
/* 275 */       this.count = n + this.pos;
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
/*     */   public synchronized int read() throws IOException {
/* 287 */     ensureOpen();
/* 288 */     if (this.pos >= this.count) {
/* 289 */       fill();
/* 290 */       if (this.pos >= this.count)
/* 291 */         return -1; 
/*     */     } 
/* 293 */     return this.buf[this.pos++] & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int read1(byte[] b, int off, int len) throws IOException {
/* 301 */     int avail = this.count - this.pos;
/* 302 */     if (avail <= 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 313 */       fill();
/* 314 */       avail = this.count - this.pos;
/* 315 */       if (avail <= 0) return -1; 
/*     */     } 
/* 317 */     int cnt = (avail < len) ? avail : len;
/* 318 */     System.arraycopy(this.buf, this.pos, b, off, cnt);
/* 319 */     this.pos += cnt;
/* 320 */     return cnt;
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
/*     */   
/*     */   public synchronized int read(byte[] b, int off, int len) throws IOException {
/* 341 */     ensureOpen();
/* 342 */     if ((off | len | off + len | b.length - off + len) < 0)
/* 343 */       throw new IndexOutOfBoundsException(); 
/* 344 */     if (len == 0) {
/* 345 */       return 0;
/*     */     }
/*     */     
/* 348 */     int n = read1(b, off, len);
/* 349 */     if (n <= 0) return n; 
/* 350 */     while (n < len) {
/* 351 */       int n1 = read1(b, off + n, len - n);
/* 352 */       if (n1 <= 0)
/* 353 */         break;  n += n1;
/*     */     } 
/* 355 */     return n;
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
/*     */   public synchronized long skip(long n) throws IOException {
/* 367 */     ensureOpen();
/* 368 */     if (n <= 0L) {
/* 369 */       return 0L;
/*     */     }
/* 371 */     long avail = (this.count - this.pos);
/*     */     
/* 373 */     if (avail <= 0L) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 381 */       fill();
/* 382 */       avail = (this.count - this.pos);
/* 383 */       if (avail <= 0L) {
/* 384 */         return 0L;
/*     */       }
/*     */     } 
/* 387 */     long skipped = (avail < n) ? avail : n;
/* 388 */     this.pos = (int)(this.pos + skipped);
/* 389 */     return skipped;
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
/*     */   public synchronized int available() throws IOException {
/* 401 */     ensureOpen();
/* 402 */     return this.count - this.pos + in_available();
/*     */   }
/*     */ 
/*     */   
/*     */   private int in_available() throws IOException {
/* 407 */     return (int)(this.start + this.datalen - this.bufpos + this.count);
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
/*     */   public synchronized void mark(int readlimit) {
/* 419 */     this.marklimit = readlimit;
/* 420 */     this.markpos = this.pos;
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
/*     */   public synchronized void reset() throws IOException {
/* 438 */     ensureOpen();
/* 439 */     if (this.markpos < 0)
/* 440 */       throw new IOException("Resetting to invalid mark"); 
/* 441 */     this.pos = this.markpos;
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
/*     */   public boolean markSupported() {
/* 456 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 466 */     if (this.in == null)
/*     */       return; 
/*     */     
/* 469 */     try { if (this.master) {
/* 470 */         this.sf.forceClose();
/*     */       } else {
/* 472 */         this.sf.close();
/*     */       }  }
/* 474 */     finally { this.sf = null;
/* 475 */       this.in = null;
/* 476 */       this.buf = null; }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getPosition() {
/* 488 */     if (this.in == null)
/* 489 */       throw new RuntimeException("Stream closed"); 
/* 490 */     return this.bufpos + this.pos - this.start;
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
/*     */   public synchronized InputStream newStream(long start, long end) {
/* 506 */     if (this.in == null)
/* 507 */       throw new RuntimeException("Stream closed"); 
/* 508 */     if (start < 0L)
/* 509 */       throw new IllegalArgumentException("start < 0"); 
/* 510 */     if (end == -1L)
/* 511 */       end = this.datalen; 
/* 512 */     return new SharedFileInputStream(this.sf, this.start + (int)start, (int)(end - start), this.bufsize);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 538 */     super.finalize();
/* 539 */     close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mai\\util\SharedFileInputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */