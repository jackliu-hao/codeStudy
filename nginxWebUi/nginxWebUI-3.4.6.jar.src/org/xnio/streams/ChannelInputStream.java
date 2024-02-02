/*     */ package org.xnio.streams;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChannelInputStream
/*     */   extends InputStream
/*     */ {
/*     */   protected final StreamSourceChannel channel;
/*     */   private volatile int flags;
/*     */   private volatile long timeout;
/*  48 */   private static final AtomicIntegerFieldUpdater<ChannelInputStream> flagsUpdater = AtomicIntegerFieldUpdater.newUpdater(ChannelInputStream.class, "flags");
/*     */ 
/*     */   
/*     */   private static final int FLAG_EOF = 2;
/*     */ 
/*     */   
/*     */   private static final int FLAG_ENTERED = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelInputStream(StreamSourceChannel channel) {
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
/*     */   public ChannelInputStream(StreamSourceChannel channel, long timeout, TimeUnit timeoutUnit) {
/*  73 */     if (channel == null) {
/*  74 */       throw Messages.msg.nullParameter("channel");
/*     */     }
/*  76 */     if (timeoutUnit == null) {
/*  77 */       throw Messages.msg.nullParameter("timeoutUnit");
/*     */     }
/*  79 */     if (timeout < 0L) {
/*  80 */       throw Messages.msg.parameterOutOfRange("timeout");
/*     */     }
/*  82 */     this.channel = channel;
/*  83 */     long calcTimeout = timeoutUnit.toNanos(timeout);
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
/*     */   public long getReadTimeout(TimeUnit unit) {
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
/*     */   public void setReadTimeout(long timeout, TimeUnit unit) {
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
/*     */   public int read() throws IOException {
/* 140 */     boolean eof = enter();
/*     */     try {
/* 142 */       if (eof) return -1; 
/* 143 */       byte[] array = new byte[1];
/* 144 */       ByteBuffer buffer = ByteBuffer.wrap(array);
/* 145 */       int res = this.channel.read(buffer);
/* 146 */       if (res == 0) {
/*     */         
/* 148 */         long start = System.nanoTime();
/* 149 */         long elapsed = 0L;
/*     */         do {
/* 151 */           long timeout = this.timeout;
/* 152 */           if (timeout == 0L)
/* 153 */           { this.channel.awaitReadable(); }
/* 154 */           else { if (timeout < elapsed) {
/* 155 */               throw Messages.msg.readTimeout();
/*     */             }
/* 157 */             this.channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */           
/* 159 */           elapsed = System.nanoTime() - start;
/* 160 */           res = this.channel.read(buffer);
/* 161 */         } while (res == 0);
/*     */       } 
/* 163 */       return (eof = (res == -1)) ? -1 : (array[0] & 0xFF);
/*     */     } finally {
/* 165 */       exit(eof);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 171 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 176 */     if (len < 1 || off + len > b.length) {
/* 177 */       return 0;
/*     */     }
/* 179 */     boolean eof = enter();
/*     */     try {
/* 181 */       if (eof) return -1; 
/* 182 */       ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
/* 183 */       int res = this.channel.read(buffer);
/* 184 */       if (res == 0) {
/*     */         
/* 186 */         long start = System.nanoTime();
/* 187 */         long elapsed = 0L;
/*     */         do {
/* 189 */           long timeout = this.timeout;
/* 190 */           if (timeout == 0L)
/* 191 */           { this.channel.awaitReadable(); }
/* 192 */           else { if (timeout < elapsed) {
/* 193 */               throw Messages.msg.readTimeout();
/*     */             }
/* 195 */             this.channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */           
/* 197 */           elapsed = System.nanoTime() - start;
/* 198 */           res = this.channel.read(buffer);
/* 199 */         } while (res == 0);
/*     */       } 
/* 201 */       return (eof = (res == -1)) ? -1 : (buffer.position() - off);
/*     */     } finally {
/* 203 */       exit(eof);
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
/*     */   public long skip(long n) throws IOException {
/* 215 */     if (n < 1L) {
/* 216 */       return 0L;
/*     */     }
/* 218 */     boolean eof = enter();
/*     */     try {
/* 220 */       if (eof) return 0L;
/*     */       
/* 222 */       n = Math.min(n, 2147483647L);
/* 223 */       long total = 0L;
/*     */       
/* 225 */       long start = System.nanoTime();
/* 226 */       long elapsed = 0L;
/*     */       
/*     */       while (true) {
/* 229 */         if (n == 0L) return total; 
/* 230 */         long res = Channels.drain(this.channel, n);
/* 231 */         if (res == -1L)
/* 232 */           return total; 
/* 233 */         if (res == 0L) {
/* 234 */           long timeout = this.timeout;
/*     */           try {
/* 236 */             if (timeout == 0L)
/* 237 */             { this.channel.awaitReadable(); }
/* 238 */             else { if (timeout < elapsed) {
/* 239 */                 throw Messages.msg.readTimeout();
/*     */               }
/* 241 */               this.channel.awaitReadable(timeout - elapsed, TimeUnit.NANOSECONDS); }
/*     */           
/* 243 */           } catch (InterruptedIOException e) {
/* 244 */             assert total < 2147483647L;
/* 245 */             e.bytesTransferred = (int)total;
/* 246 */             throw e;
/*     */           } 
/* 248 */           elapsed = System.nanoTime() - start; continue;
/*     */         } 
/* 250 */         total += res;
/* 251 */         n -= res;
/*     */       } 
/*     */     } finally {
/*     */       
/* 255 */       exit(eof);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 261 */     enter();
/*     */     try {
/* 263 */       this.channel.shutdownReads();
/*     */     } finally {
/* 265 */       exit(true);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\ChannelInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */