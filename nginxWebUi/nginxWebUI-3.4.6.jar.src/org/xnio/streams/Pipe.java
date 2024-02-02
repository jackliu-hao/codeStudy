/*     */ package org.xnio.streams;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.xnio._private.Messages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Pipe
/*     */ {
/*  37 */   private final Object lock = new Object();
/*     */ 
/*     */   
/*     */   private int tail;
/*     */ 
/*     */   
/*     */   private int size;
/*     */ 
/*     */   
/*     */   private final byte[] buffer;
/*     */ 
/*     */   
/*     */   private boolean writeClosed;
/*     */ 
/*     */   
/*     */   private boolean readClosed;
/*     */   
/*     */   private final InputStream in;
/*     */   
/*     */   private final OutputStream out;
/*     */ 
/*     */   
/*     */   public void await() {
/*  60 */     boolean intr = false;
/*  61 */     Object lock = this.lock;
/*     */     try {
/*  63 */       synchronized (lock) {
/*  64 */         while (!this.readClosed) {
/*     */           try {
/*  66 */             lock.wait();
/*  67 */           } catch (InterruptedException e) {
/*  68 */             intr = true;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } finally {
/*  73 */       if (intr)
/*  74 */         Thread.currentThread().interrupt(); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public Pipe(int bufferSize) {
/*  79 */     this.in = new InputStream() {
/*     */         public int read() throws IOException {
/*  81 */           Object lock = Pipe.this.lock;
/*  82 */           synchronized (lock) {
/*  83 */             if (Pipe.this.writeClosed && Pipe.this.size == 0) {
/*  84 */               return -1;
/*     */             }
/*  86 */             while (Pipe.this.size == 0) {
/*     */               try {
/*  88 */                 lock.wait();
/*  89 */                 if (Pipe.this.writeClosed && Pipe.this.size == 0) {
/*  90 */                   return -1;
/*     */                 }
/*  92 */               } catch (InterruptedException e) {
/*  93 */                 Thread.currentThread().interrupt();
/*  94 */                 throw Messages.msg.interruptedIO();
/*     */               } 
/*     */             } 
/*  97 */             lock.notifyAll();
/*  98 */             int tail = Pipe.this.tail;
/*     */             try {
/* 100 */               return Pipe.this.buffer[tail++] & 0xFF;
/*     */             } finally {
/* 102 */               Pipe.this.tail = (tail == Pipe.this.buffer.length) ? 0 : tail;
/* 103 */               Pipe.this.size--;
/*     */             } 
/*     */           } 
/*     */         }
/*     */         
/*     */         public int read(byte[] b, int off, int len) throws IOException {
/* 109 */           Object lock = Pipe.this.lock;
/* 110 */           synchronized (lock) {
/* 111 */             int cnt; if (Pipe.this.writeClosed && Pipe.this.size == 0) {
/* 112 */               return -1;
/*     */             }
/* 114 */             if (len == 0) {
/* 115 */               return 0;
/*     */             }
/*     */             int size;
/* 118 */             while ((size = Pipe.this.size) == 0) {
/*     */               try {
/* 120 */                 lock.wait();
/* 121 */                 if (Pipe.this.writeClosed && (size = Pipe.this.size) == 0) {
/* 122 */                   return -1;
/*     */                 }
/* 124 */               } catch (InterruptedException e) {
/* 125 */                 Thread.currentThread().interrupt();
/* 126 */                 throw Messages.msg.interruptedIO();
/*     */               } 
/*     */             } 
/* 129 */             byte[] buffer = Pipe.this.buffer;
/* 130 */             int bufLen = buffer.length;
/*     */             
/* 132 */             int tail = Pipe.this.tail;
/* 133 */             if (size + tail > bufLen) {
/*     */               
/* 135 */               int lastLen = bufLen - tail;
/* 136 */               if (lastLen < len) {
/* 137 */                 int firstLen = tail + size - bufLen;
/* 138 */                 System.arraycopy(buffer, tail, b, off, lastLen);
/* 139 */                 int rem = Math.min(len - lastLen, firstLen);
/* 140 */                 System.arraycopy(buffer, 0, b, off + lastLen, rem);
/* 141 */                 cnt = rem + lastLen;
/*     */               } else {
/* 143 */                 System.arraycopy(buffer, tail, b, off, len);
/* 144 */                 cnt = len;
/*     */               } 
/*     */             } else {
/*     */               
/* 148 */               cnt = Math.min(len, size);
/* 149 */               System.arraycopy(buffer, tail, b, off, cnt);
/*     */             } 
/* 151 */             tail += cnt;
/* 152 */             size -= cnt;
/* 153 */             Pipe.this.tail = (tail >= bufLen) ? (tail - bufLen) : tail;
/* 154 */             Pipe.this.size = size;
/* 155 */             lock.notifyAll();
/* 156 */             return cnt;
/*     */           } 
/*     */         }
/*     */         
/*     */         public void close() throws IOException {
/* 161 */           Object lock = Pipe.this.lock;
/* 162 */           synchronized (lock) {
/* 163 */             Pipe.this.writeClosed = true;
/* 164 */             Pipe.this.readClosed = true;
/*     */             
/* 166 */             Pipe.this.size = 0;
/* 167 */             lock.notifyAll();
/*     */             return;
/*     */           } 
/*     */         }
/*     */         
/*     */         public String toString() {
/* 173 */           return "Pipe read half";
/*     */         }
/*     */       };
/* 176 */     this.out = new OutputStream() {
/*     */         public void write(int b) throws IOException {
/* 178 */           Object lock = Pipe.this.lock;
/* 179 */           synchronized (lock) {
/* 180 */             if (Pipe.this.writeClosed) {
/* 181 */               throw Messages.msg.streamClosed();
/*     */             }
/* 183 */             byte[] buffer = Pipe.this.buffer;
/* 184 */             int bufLen = buffer.length;
/* 185 */             while (Pipe.this.size == bufLen) {
/*     */               try {
/* 187 */                 lock.wait();
/* 188 */                 if (Pipe.this.writeClosed) {
/* 189 */                   throw Messages.msg.streamClosed();
/*     */                 }
/* 191 */               } catch (InterruptedException e) {
/* 192 */                 Thread.currentThread().interrupt();
/* 193 */                 throw Messages.msg.interruptedIO();
/*     */               } 
/*     */             } 
/* 196 */             int tail = Pipe.this.tail;
/* 197 */             int startPos = tail + Pipe.this.size;
/* 198 */             if (startPos >= bufLen) {
/* 199 */               buffer[startPos - bufLen] = (byte)b;
/*     */             } else {
/* 201 */               buffer[startPos] = (byte)b;
/*     */             } 
/* 203 */             Pipe.this.size++;
/* 204 */             lock.notifyAll();
/*     */           } 
/*     */         }
/*     */         
/*     */         public void write(byte[] b, int off, int len) throws IOException {
/* 209 */           int remaining = len;
/* 210 */           Object lock = Pipe.this.lock;
/* 211 */           synchronized (lock) {
/* 212 */             if (Pipe.this.writeClosed) {
/* 213 */               throw Messages.msg.streamClosed();
/*     */             }
/* 215 */             byte[] buffer = Pipe.this.buffer;
/* 216 */             int bufLen = buffer.length;
/*     */ 
/*     */ 
/*     */             
/* 220 */             while (remaining > 0) {
/* 221 */               int cnt, size; while ((size = Pipe.this.size) == bufLen) {
/*     */                 try {
/* 223 */                   lock.wait();
/* 224 */                   if (Pipe.this.writeClosed) {
/* 225 */                     throw Messages.msg.streamClosed();
/*     */                   }
/* 227 */                 } catch (InterruptedException e) {
/* 228 */                   Thread.currentThread().interrupt();
/* 229 */                   throw Messages.msg.interruptedIO(len - remaining);
/*     */                 } 
/*     */               } 
/* 232 */               int tail = Pipe.this.tail;
/* 233 */               int startPos = tail + size;
/* 234 */               if (startPos >= bufLen) {
/*     */                 
/* 236 */                 startPos -= bufLen;
/* 237 */                 cnt = Math.min(remaining, bufLen - size);
/* 238 */                 System.arraycopy(b, off, buffer, startPos, cnt);
/* 239 */                 remaining -= cnt;
/* 240 */                 off += cnt;
/*     */               } else {
/*     */                 
/* 243 */                 int firstPart = Math.min(remaining, bufLen - tail + size);
/* 244 */                 System.arraycopy(b, off, buffer, startPos, firstPart);
/* 245 */                 off += firstPart;
/* 246 */                 remaining -= firstPart;
/* 247 */                 if (remaining > 0) {
/* 248 */                   int latter = Math.min(remaining, tail);
/* 249 */                   System.arraycopy(b, off, buffer, 0, latter);
/* 250 */                   cnt = firstPart + latter;
/* 251 */                   off += latter;
/* 252 */                   remaining -= latter;
/*     */                 } else {
/* 254 */                   cnt = firstPart;
/*     */                 } 
/*     */               } 
/* 257 */               Pipe pipe = Pipe.this; pipe.size = pipe.size + cnt;
/* 258 */               lock.notifyAll();
/*     */             } 
/*     */           } 
/*     */         }
/*     */         
/*     */         public void close() throws IOException {
/* 264 */           Object lock = Pipe.this.lock;
/* 265 */           synchronized (lock) {
/* 266 */             Pipe.this.writeClosed = true;
/* 267 */             lock.notifyAll();
/*     */             return;
/*     */           } 
/*     */         }
/*     */         
/*     */         public String toString() {
/* 273 */           return "Pipe write half";
/*     */         }
/*     */       };
/*     */     this.buffer = new byte[bufferSize];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getIn() {
/* 283 */     return this.in;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getOut() {
/* 292 */     return this.out;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\Pipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */