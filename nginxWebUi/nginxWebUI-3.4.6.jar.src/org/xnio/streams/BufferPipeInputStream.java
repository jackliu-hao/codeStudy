/*     */ package org.xnio.streams;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Queue;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio.Xnio;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferPipeInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final Queue<Pooled<ByteBuffer>> queue;
/*     */   private final InputHandler inputHandler;
/*     */   private boolean eof;
/*     */   private IOException failure;
/*     */   
/*     */   public BufferPipeInputStream(InputHandler inputHandler) {
/*  53 */     this.inputHandler = inputHandler;
/*  54 */     this.queue = new ArrayDeque<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void push(ByteBuffer buffer) {
/*  64 */     synchronized (this) {
/*  65 */       if (buffer.hasRemaining() && !this.eof && this.failure == null) {
/*  66 */         this.queue.add(Buffers.pooledWrapper(buffer));
/*  67 */         notifyAll();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void push(Pooled<ByteBuffer> pooledBuffer) {
/*  79 */     synchronized (this) {
/*  80 */       if (((ByteBuffer)pooledBuffer.getResource()).hasRemaining() && !this.eof && this.failure == null) {
/*  81 */         this.queue.add(pooledBuffer);
/*  82 */         notifyAll();
/*     */       } else {
/*  84 */         pooledBuffer.free();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pushException(IOException e) {
/*  96 */     synchronized (this) {
/*  97 */       if (!this.eof) {
/*  98 */         this.failure = e;
/*  99 */         notifyAll();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pushEof() {
/* 109 */     synchronized (this) {
/* 110 */       this.eof = true;
/* 111 */       notifyAll();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 117 */     Queue<Pooled<ByteBuffer>> queue = this.queue;
/* 118 */     synchronized (this) {
/* 119 */       while (queue.isEmpty()) {
/* 120 */         if (this.eof) {
/* 121 */           return -1;
/*     */         }
/* 123 */         checkFailure();
/* 124 */         Xnio.checkBlockingAllowed();
/*     */         try {
/* 126 */           wait();
/* 127 */         } catch (InterruptedException e) {
/* 128 */           Thread.currentThread().interrupt();
/* 129 */           throw Messages.msg.interruptedIO();
/*     */         } 
/*     */       } 
/* 132 */       Pooled<ByteBuffer> entry = queue.peek();
/* 133 */       ByteBuffer buf = (ByteBuffer)entry.getResource();
/* 134 */       int v = buf.get() & 0xFF;
/* 135 */       if (buf.remaining() == 0) {
/* 136 */         queue.poll();
/*     */         try {
/* 138 */           this.inputHandler.acknowledge(entry);
/* 139 */         } catch (IOException iOException) {
/*     */         
/*     */         } finally {
/* 142 */           entry.free();
/*     */         } 
/*     */       } 
/* 145 */       return v;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void clearQueue() {
/* 150 */     synchronized (this) {
/*     */       Pooled<ByteBuffer> entry;
/* 152 */       while ((entry = this.queue.poll()) != null) {
/* 153 */         entry.free();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 160 */     if (len == 0) {
/* 161 */       return 0;
/*     */     }
/* 163 */     Queue<Pooled<ByteBuffer>> queue = this.queue;
/* 164 */     synchronized (this) {
/* 165 */       while (queue.isEmpty()) {
/* 166 */         if (this.eof) {
/* 167 */           return -1;
/*     */         }
/* 169 */         checkFailure();
/* 170 */         Xnio.checkBlockingAllowed();
/*     */         try {
/* 172 */           wait();
/* 173 */         } catch (InterruptedException e) {
/* 174 */           Thread.currentThread().interrupt();
/* 175 */           throw Messages.msg.interruptedIO();
/*     */         } 
/*     */       } 
/* 178 */       int total = 0;
/* 179 */       while (len > 0) {
/* 180 */         Pooled<ByteBuffer> entry = queue.peek();
/* 181 */         if (entry == null) {
/*     */           break;
/*     */         }
/* 184 */         ByteBuffer buffer = (ByteBuffer)entry.getResource();
/* 185 */         int byteCnt = Math.min(buffer.remaining(), len);
/* 186 */         buffer.get(b, off, byteCnt);
/* 187 */         off += byteCnt;
/* 188 */         total += byteCnt;
/* 189 */         len -= byteCnt;
/* 190 */         if (buffer.remaining() == 0) {
/* 191 */           queue.poll();
/*     */           try {
/* 193 */             this.inputHandler.acknowledge(entry);
/* 194 */           } catch (IOException iOException) {
/*     */           
/*     */           } finally {
/* 197 */             entry.free();
/*     */           } 
/*     */         } 
/*     */       } 
/* 201 */       return total;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 207 */     synchronized (this) {
/* 208 */       int total = 0;
/* 209 */       for (Pooled<ByteBuffer> entry : this.queue) {
/* 210 */         total += ((ByteBuffer)entry.getResource()).remaining();
/* 211 */         if (total < 0) {
/* 212 */           return Integer.MAX_VALUE;
/*     */         }
/*     */       } 
/* 215 */       return total;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long skip(long qty) throws IOException {
/* 220 */     Queue<Pooled<ByteBuffer>> queue = this.queue;
/* 221 */     synchronized (this) {
/* 222 */       while (queue.isEmpty()) {
/* 223 */         if (this.eof) {
/* 224 */           return 0L;
/*     */         }
/* 226 */         checkFailure();
/* 227 */         Xnio.checkBlockingAllowed();
/*     */         try {
/* 229 */           wait();
/* 230 */         } catch (InterruptedException e) {
/* 231 */           Thread.currentThread().interrupt();
/* 232 */           throw Messages.msg.interruptedIO();
/*     */         } 
/*     */       } 
/* 235 */       long skipped = 0L;
/* 236 */       while (qty > 0L) {
/* 237 */         Pooled<ByteBuffer> entry = queue.peek();
/* 238 */         if (entry == null) {
/*     */           break;
/*     */         }
/* 241 */         ByteBuffer buffer = (ByteBuffer)entry.getResource();
/* 242 */         int byteCnt = (int)Math.min(buffer.remaining(), Math.max(2147483647L, qty));
/* 243 */         buffer.position(buffer.position() + byteCnt);
/* 244 */         skipped += byteCnt;
/* 245 */         qty -= byteCnt;
/* 246 */         if (buffer.remaining() == 0) {
/* 247 */           queue.poll();
/*     */           try {
/* 249 */             this.inputHandler.acknowledge(entry);
/* 250 */           } catch (IOException iOException) {
/*     */           
/*     */           } finally {
/* 253 */             entry.free();
/*     */           } 
/*     */         } 
/*     */       } 
/* 257 */       return skipped;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 263 */     synchronized (this) {
/* 264 */       if (!this.eof) {
/* 265 */         clearQueue();
/* 266 */         this.eof = true;
/* 267 */         this.failure = null;
/* 268 */         notifyAll();
/* 269 */         this.inputHandler.close();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkFailure() throws IOException {
/* 275 */     assert Thread.holdsLock(this);
/* 276 */     IOException failure = this.failure;
/* 277 */     if (failure != null) {
/* 278 */       failure.fillInStackTrace();
/*     */       try {
/* 280 */         throw failure;
/*     */       } finally {
/* 282 */         clearQueue();
/* 283 */         notifyAll();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static interface InputHandler extends Closeable {
/*     */     void acknowledge(Pooled<ByteBuffer> param1Pooled) throws IOException;
/*     */     
/*     */     void close() throws IOException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\BufferPipeInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */