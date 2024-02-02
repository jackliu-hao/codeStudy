/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.AbstractStreamSinkConduit;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RateLimitingStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private final long time;
/*     */   private final int bytes;
/*     */   private boolean writesResumed = false;
/*  49 */   private int byteCount = 0;
/*  50 */   private long startTime = 0L;
/*  51 */   private long nextSendTime = 0L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean scheduled = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RateLimitingStreamSinkConduit(StreamSinkConduit next, int bytes, long time, TimeUnit timeUnit) {
/*  62 */     super(next);
/*  63 */     this.writesResumed = next.isWriteResumed();
/*  64 */     this.time = timeUnit.toMillis(time);
/*  65 */     this.bytes = bytes;
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  70 */     if (!canSend()) {
/*  71 */       return 0;
/*     */     }
/*  73 */     int bytes = this.bytes - this.byteCount;
/*  74 */     int old = src.limit();
/*  75 */     if (src.remaining() > bytes) {
/*  76 */       src.limit(src.position() + bytes);
/*     */     }
/*     */     try {
/*  79 */       int written = super.write(src);
/*  80 */       handleWritten(written);
/*  81 */       return written;
/*     */     } finally {
/*  83 */       src.limit(old);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  89 */     if (!canSend()) {
/*  90 */       return 0L;
/*     */     }
/*  92 */     int bytes = this.bytes - this.byteCount;
/*  93 */     long written = super.transferFrom(src, position, Math.min(count, bytes));
/*  94 */     handleWritten(written);
/*  95 */     return written;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 100 */     if (!canSend()) {
/* 101 */       return 0L;
/*     */     }
/* 103 */     int bytes = this.bytes - this.byteCount;
/* 104 */     long written = super.transferFrom(source, Math.min(count, bytes), throughBuffer);
/* 105 */     handleWritten(written);
/* 106 */     return written;
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 111 */     if (!canSend()) {
/* 112 */       return 0L;
/*     */     }
/* 114 */     int old = 0;
/* 115 */     int adjPos = -1;
/* 116 */     long rem = (this.bytes - this.byteCount);
/* 117 */     for (int i = offs; i < offs + len; i++) {
/* 118 */       ByteBuffer buf = srcs[i];
/* 119 */       rem -= buf.remaining();
/* 120 */       if (rem < 0L) {
/* 121 */         adjPos = i;
/* 122 */         old = buf.limit();
/* 123 */         buf.limit((int)(buf.limit() + rem));
/*     */         break;
/*     */       } 
/*     */     } 
/*     */     try {
/*     */       long written;
/* 129 */       if (adjPos == -1) {
/* 130 */         written = super.write(srcs, offs, len);
/*     */       } else {
/* 132 */         written = super.write(srcs, offs, adjPos - offs + 1);
/*     */       } 
/* 134 */       handleWritten(written);
/* 135 */       return written;
/*     */     } finally {
/* 137 */       if (adjPos != -1) {
/* 138 */         ByteBuffer buf = srcs[adjPos];
/* 139 */         buf.limit(old);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 146 */     if (!canSend()) {
/* 147 */       return 0;
/*     */     }
/* 149 */     int bytes = this.bytes - this.byteCount;
/* 150 */     int old = src.limit();
/* 151 */     if (src.remaining() > bytes) {
/* 152 */       src.limit(src.position() + bytes);
/*     */     }
/*     */     try {
/* 155 */       int written = super.writeFinal(src);
/* 156 */       handleWritten(written);
/* 157 */       return written;
/*     */     } finally {
/* 159 */       src.limit(old);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 165 */     if (!canSend()) {
/* 166 */       return 0L;
/*     */     }
/* 168 */     int old = 0;
/* 169 */     int adjPos = -1;
/* 170 */     long rem = (this.bytes - this.byteCount);
/* 171 */     for (int i = offs; i < offs + len; i++) {
/* 172 */       ByteBuffer buf = srcs[i];
/* 173 */       rem -= buf.remaining();
/* 174 */       if (rem < 0L) {
/* 175 */         adjPos = i;
/* 176 */         old = buf.limit();
/* 177 */         buf.limit((int)(buf.limit() + rem));
/*     */         break;
/*     */       } 
/*     */     } 
/*     */     try {
/*     */       long written;
/* 183 */       if (adjPos == -1) {
/* 184 */         written = super.writeFinal(srcs, offs, len);
/*     */       } else {
/* 186 */         written = super.writeFinal(srcs, offs, adjPos - offs + 1);
/*     */       } 
/* 188 */       handleWritten(written);
/* 189 */       return written;
/*     */     } finally {
/* 191 */       if (adjPos != -1) {
/* 192 */         ByteBuffer buf = srcs[adjPos];
/* 193 */         buf.limit(old);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeWrites() {
/* 200 */     this.writesResumed = true;
/* 201 */     if (canSend()) {
/* 202 */       super.resumeWrites();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendWrites() {
/* 208 */     this.writesResumed = false;
/* 209 */     super.suspendWrites();
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupWrites() {
/* 214 */     this.writesResumed = true;
/* 215 */     if (canSend()) {
/* 216 */       super.wakeupWrites();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWriteResumed() {
/* 222 */     return this.writesResumed;
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 227 */     long toGo = this.nextSendTime - System.currentTimeMillis();
/* 228 */     if (toGo > 0L) {
/*     */       try {
/* 230 */         Thread.sleep(toGo);
/* 231 */       } catch (InterruptedException e) {
/* 232 */         Thread.currentThread().interrupt();
/* 233 */         throw new InterruptedIOException();
/*     */       } 
/*     */     }
/* 236 */     super.awaitWritable();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 242 */     long toGo = this.nextSendTime - System.currentTimeMillis();
/* 243 */     if (toGo > 0L) {
/*     */       try {
/* 245 */         Thread.sleep(Math.min(toGo, timeUnit.toMillis(time)));
/* 246 */       } catch (InterruptedException e) {
/* 247 */         Thread.currentThread().interrupt();
/* 248 */         throw new InterruptedIOException();
/*     */       } 
/*     */       return;
/*     */     } 
/* 252 */     super.awaitWritable(time, timeUnit);
/*     */   }
/*     */   
/*     */   private boolean canSend() {
/* 256 */     if (this.byteCount < this.bytes) {
/* 257 */       return true;
/*     */     }
/* 259 */     if (System.currentTimeMillis() > this.nextSendTime) {
/* 260 */       this.byteCount = 0;
/* 261 */       this.startTime = 0L;
/* 262 */       this.nextSendTime = 0L;
/* 263 */       return true;
/*     */     } 
/* 265 */     if (this.writesResumed) {
/* 266 */       handleWritesResumedWhenBlocked();
/*     */     }
/* 268 */     return false;
/*     */   }
/*     */   
/*     */   private void handleWritten(long written) {
/* 272 */     if (written == 0L) {
/*     */       return;
/*     */     }
/* 275 */     this.byteCount = (int)(this.byteCount + written);
/* 276 */     if (this.byteCount < this.bytes) {
/*     */       
/* 278 */       if (this.startTime == 0L) {
/* 279 */         this.startTime = System.currentTimeMillis();
/* 280 */         this.nextSendTime = System.currentTimeMillis() + this.time;
/*     */       } 
/*     */     } else {
/*     */       
/* 284 */       if (this.startTime == 0L) {
/* 285 */         this.startTime = System.currentTimeMillis();
/*     */       }
/* 287 */       this.nextSendTime = this.startTime + this.time;
/* 288 */       if (this.writesResumed) {
/* 289 */         handleWritesResumedWhenBlocked();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleWritesResumedWhenBlocked() {
/* 295 */     if (this.scheduled) {
/*     */       return;
/*     */     }
/* 298 */     this.scheduled = true;
/* 299 */     ((StreamSinkConduit)this.next).suspendWrites();
/* 300 */     long millis = this.nextSendTime - System.currentTimeMillis();
/* 301 */     WorkerUtils.executeAfter(getWriteThread(), new Runnable()
/*     */         {
/*     */           public void run() {
/* 304 */             RateLimitingStreamSinkConduit.this.scheduled = false;
/* 305 */             if (RateLimitingStreamSinkConduit.this.writesResumed)
/* 306 */               ((StreamSinkConduit)RateLimitingStreamSinkConduit.this.next).wakeupWrites(); 
/*     */           }
/*     */         },  millis, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\RateLimitingStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */