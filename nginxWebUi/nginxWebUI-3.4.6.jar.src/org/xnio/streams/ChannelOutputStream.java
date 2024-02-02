/*     */ package org.xnio.streams;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChannelOutputStream
/*     */   extends OutputStream
/*     */ {
/*     */   protected final StreamSinkChannel channel;
/*     */   private volatile int flags;
/*     */   private volatile long timeout;
/*  48 */   private static final AtomicIntegerFieldUpdater<ChannelOutputStream> flagsUpdater = AtomicIntegerFieldUpdater.newUpdater(ChannelOutputStream.class, "flags");
/*     */ 
/*     */   
/*     */   private static final int FLAG_CLOSED = 2;
/*     */ 
/*     */   
/*     */   private static final int FLAG_ENTERED = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelOutputStream(StreamSinkChannel channel) {
/*  59 */     if (channel == null) {
/*  60 */       throw Messages.msg.nullParameter("channel");
/*     */     }
/*  62 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelOutputStream(StreamSinkChannel channel, long timeout, TimeUnit unit) {
/*  73 */     if (channel == null) {
/*  74 */       throw Messages.msg.nullParameter("channel");
/*     */     }
/*  76 */     if (unit == null) {
/*  77 */       throw Messages.msg.nullParameter("unit");
/*     */     }
/*  79 */     if (timeout < 0L) {
/*  80 */       throw Messages.msg.parameterOutOfRange("timeout");
/*     */     }
/*  82 */     this.channel = channel;
/*  83 */     long calcTimeout = unit.toNanos(timeout);
/*  84 */     this.timeout = (timeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
/*     */   }
/*     */   
/*     */   private boolean enter() {
/*  88 */     int old = this.flags;
/*     */     while (true) {
/*  90 */       if (Bits.allAreSet(old, 1)) {
/*  91 */         throw Messages.msg.concurrentAccess();
/*     */       }
/*  93 */       if (flagsUpdater.compareAndSet(this, old, old | 0x1))
/*  94 */         return Bits.allAreSet(old, 2); 
/*     */     } 
/*     */   } private void exit(boolean setEof) {
/*     */     int oldFlags;
/*     */     int newFlags;
/*     */     do {
/* 100 */       oldFlags = this.flags;
/* 101 */       newFlags = oldFlags & 0xFFFFFFFE;
/* 102 */       if (!setEof)
/* 103 */         continue;  newFlags |= 0x2;
/*     */     }
/* 105 */     while (!flagsUpdater.compareAndSet(this, oldFlags, newFlags));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getWriteTimeout(TimeUnit unit) {
/* 115 */     if (unit == null) {
/* 116 */       throw Messages.msg.nullParameter("unit");
/*     */     }
/* 118 */     return unit.convert(this.timeout, TimeUnit.NANOSECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteTimeout(long timeout, TimeUnit unit) {
/* 128 */     if (timeout < 0L) {
/* 129 */       throw Messages.msg.parameterOutOfRange("timeout");
/*     */     }
/* 131 */     if (unit == null) {
/* 132 */       throw Messages.msg.nullParameter("unit");
/*     */     }
/* 134 */     long calcTimeout = unit.toNanos(timeout);
/* 135 */     this.timeout = (timeout == 0L) ? 0L : ((calcTimeout < 1L) ? 1L : calcTimeout);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 140 */     boolean closed = enter();
/*     */     try {
/* 142 */       if (closed) throw Messages.msg.streamClosed(); 
/* 143 */       StreamSinkChannel channel = this.channel;
/* 144 */       ByteBuffer buffer = ByteBuffer.wrap(new byte[] { (byte)b });
/* 145 */       int res = channel.write(buffer);
/* 146 */       if (res == 0) {
/*     */         
/* 148 */         long start = System.nanoTime();
/* 149 */         long elapsed = 0L;
/*     */         do {
/* 151 */           long timeout = this.timeout;
/* 152 */           if (timeout == 0L)
/* 153 */           { channel.awaitWritable(); }
/* 154 */           else { if (timeout < elapsed) {
/* 155 */               throw Messages.msg.writeTimeout();
/*     */             }
/* 157 */             channel.awaitWritable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */           
/* 159 */           elapsed = System.nanoTime() - start;
/* 160 */           res = channel.write(buffer);
/* 161 */         } while (res == 0);
/*     */       } 
/*     */     } finally {
/* 164 */       exit(closed);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 170 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 175 */     if (len < 1) {
/*     */       return;
/*     */     }
/* 178 */     boolean closed = enter();
/*     */     
/* 180 */     try { if (closed) throw Messages.msg.streamClosed(); 
/* 181 */       StreamSinkChannel channel = this.channel;
/* 182 */       ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
/*     */       
/* 184 */       label31: while (buffer.hasRemaining()) {
/* 185 */         int res = channel.write(buffer);
/* 186 */         if (res == 0) {
/*     */           
/* 188 */           long start = System.nanoTime();
/* 189 */           long elapsed = 0L;
/*     */           while (true)
/* 191 */           { long timeout = this.timeout;
/*     */             try {
/* 193 */               if (timeout == 0L)
/* 194 */               { channel.awaitWritable(); }
/* 195 */               else { if (timeout < elapsed) {
/* 196 */                   throw Messages.msg.writeTimeout();
/*     */                 }
/* 198 */                 channel.awaitWritable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */             
/* 200 */             } catch (InterruptedIOException e) {
/* 201 */               e.bytesTransferred = buffer.position() - off;
/* 202 */               throw e;
/*     */             } 
/* 204 */             elapsed = System.nanoTime() - start;
/* 205 */             res = channel.write(buffer);
/* 206 */             if (res != 0)
/*     */               continue label31;  } 
/*     */         } 
/*     */       }  }
/* 210 */     finally { exit(closed); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 216 */     boolean closed = enter();
/*     */     try {
/* 218 */       StreamSinkChannel channel = this.channel;
/* 219 */       if (!channel.flush()) {
/*     */         
/* 221 */         long start = System.nanoTime();
/* 222 */         long elapsed = 0L;
/*     */         do {
/* 224 */           long timeout = this.timeout;
/* 225 */           if (timeout == 0L)
/* 226 */           { channel.awaitWritable(); }
/* 227 */           else { if (timeout < elapsed) {
/* 228 */               throw Messages.msg.writeTimeout();
/*     */             }
/* 230 */             channel.awaitWritable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */           
/* 232 */           elapsed = System.nanoTime() - start;
/* 233 */         } while (!channel.flush());
/*     */       } 
/*     */     } finally {
/* 236 */       exit(closed);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 242 */     boolean closed = enter();
/*     */     try {
/* 244 */       if (closed)
/* 245 */         return;  StreamSinkChannel channel = this.channel;
/* 246 */       channel.shutdownWrites();
/* 247 */       if (!channel.flush()) {
/*     */         
/* 249 */         long start = System.nanoTime();
/* 250 */         long elapsed = 0L;
/*     */         do {
/* 252 */           long timeout = this.timeout;
/* 253 */           if (timeout == 0L)
/* 254 */           { channel.awaitWritable(); }
/* 255 */           else { if (timeout < elapsed) {
/* 256 */               throw Messages.msg.writeTimeout();
/*     */             }
/* 258 */             channel.awaitWritable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */           
/* 260 */           elapsed = System.nanoTime() - start;
/* 261 */         } while (!channel.flush());
/*     */       } 
/*     */     } finally {
/* 264 */       exit(true);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\ChannelOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */