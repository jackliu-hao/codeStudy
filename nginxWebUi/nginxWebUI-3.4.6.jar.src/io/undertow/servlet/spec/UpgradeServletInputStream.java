/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.servlet.ReadListener;
/*     */ import javax.servlet.ServletInputStream;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
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
/*     */ 
/*     */ public class UpgradeServletInputStream
/*     */   extends ServletInputStream
/*     */ {
/*     */   private final StreamSourceChannel channel;
/*     */   private final ByteBufferPool bufferPool;
/*     */   private final Executor ioExecutor;
/*     */   private volatile ReadListener listener;
/*     */   private static final int FLAG_READY = 1;
/*     */   private static final int FLAG_CLOSED = 2;
/*     */   private static final int FLAG_FINISHED = 4;
/*     */   private static final int FLAG_ON_DATA_READ_CALLED = 8;
/*     */   private int state;
/*     */   private PooledByteBuffer pooled;
/*     */   
/*     */   public UpgradeServletInputStream(StreamSourceChannel channel, ByteBufferPool bufferPool, Executor ioExecutor) {
/*  64 */     this.channel = channel;
/*  65 */     this.bufferPool = bufferPool;
/*  66 */     this.ioExecutor = ioExecutor;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFinished() {
/*  71 */     return Bits.anyAreSet(this.state, 4);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReady() {
/*  76 */     return (Bits.anyAreSet(this.state, 1) && !isFinished());
/*     */   }
/*     */ 
/*     */   
/*     */   public void setReadListener(ReadListener readListener) {
/*  81 */     if (readListener == null) {
/*  82 */       throw UndertowServletMessages.MESSAGES.listenerCannotBeNull();
/*     */     }
/*  84 */     if (this.listener != null) {
/*  85 */       throw UndertowServletMessages.MESSAGES.listenerAlreadySet();
/*     */     }
/*     */     
/*  88 */     this.listener = readListener;
/*  89 */     this.channel.getReadSetter().set(new ServletInputStreamChannelListener());
/*     */ 
/*     */     
/*  92 */     this.ioExecutor.execute(new Runnable()
/*     */         {
/*     */           public void run() {
/*  95 */             UpgradeServletInputStream.this.channel.wakeupReads();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 102 */     byte[] b = new byte[1];
/* 103 */     int read = read(b);
/* 104 */     if (read == -1) {
/* 105 */       return -1;
/*     */     }
/* 107 */     return b[0] & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/* 112 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 117 */     if (Bits.anyAreSet(this.state, 2)) {
/* 118 */       throw UndertowServletMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 120 */     if (this.listener != null) {
/* 121 */       if (Bits.anyAreClear(this.state, 1)) {
/* 122 */         throw UndertowServletMessages.MESSAGES.streamNotReady();
/*     */       }
/*     */     } else {
/* 125 */       readIntoBuffer();
/*     */     } 
/* 127 */     if (Bits.anyAreSet(this.state, 4)) {
/* 128 */       return -1;
/*     */     }
/* 130 */     if (len == 0) {
/* 131 */       return 0;
/*     */     }
/* 133 */     ByteBuffer buffer = this.pooled.getBuffer();
/* 134 */     int copied = Math.min(buffer.remaining(), len);
/* 135 */     buffer.get(b, off, copied);
/* 136 */     if (!buffer.hasRemaining()) {
/* 137 */       this.pooled.close();
/* 138 */       this.pooled = null;
/* 139 */       if (this.listener != null) {
/* 140 */         readIntoBufferNonBlocking();
/*     */       }
/*     */     } 
/* 143 */     return copied;
/*     */   }
/*     */   
/*     */   private void readIntoBuffer() throws IOException {
/* 147 */     if (this.pooled == null && !Bits.anyAreSet(this.state, 4)) {
/* 148 */       this.pooled = this.bufferPool.allocate();
/*     */       
/* 150 */       int res = Channels.readBlocking((ReadableByteChannel)this.channel, this.pooled.getBuffer());
/* 151 */       this.pooled.getBuffer().flip();
/* 152 */       if (res == -1) {
/* 153 */         this.state |= 0x4;
/* 154 */         this.pooled.close();
/* 155 */         this.pooled = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void readIntoBufferNonBlocking() throws IOException {
/* 161 */     if (this.pooled == null && !Bits.anyAreSet(this.state, 6)) {
/* 162 */       this.pooled = this.bufferPool.allocate();
/* 163 */       if (this.listener == null) {
/* 164 */         int res = this.channel.read(this.pooled.getBuffer());
/* 165 */         if (res == 0) {
/* 166 */           this.pooled.close();
/* 167 */           this.pooled = null;
/*     */           return;
/*     */         } 
/* 170 */         this.pooled.getBuffer().flip();
/* 171 */         if (res == -1) {
/* 172 */           this.state |= 0x4;
/* 173 */           this.pooled.close();
/* 174 */           this.pooled = null;
/*     */         } 
/*     */       } else {
/* 177 */         if (Bits.anyAreClear(this.state, 1)) {
/* 178 */           throw UndertowServletMessages.MESSAGES.streamNotReady();
/*     */         }
/* 180 */         int res = this.channel.read(this.pooled.getBuffer());
/* 181 */         this.pooled.getBuffer().flip();
/* 182 */         if (res == -1) {
/* 183 */           this.state |= 0x4;
/* 184 */           this.pooled.close();
/* 185 */           this.pooled = null;
/* 186 */         } else if (res == 0) {
/* 187 */           this.state &= 0xFFFFFFFE;
/* 188 */           this.pooled.close();
/* 189 */           this.pooled = null;
/* 190 */           if (Thread.currentThread() == this.channel.getIoThread()) {
/* 191 */             this.channel.resumeReads();
/*     */           } else {
/* 193 */             this.ioExecutor.execute(new Runnable()
/*     */                 {
/*     */                   public void run() {
/* 196 */                     UpgradeServletInputStream.this.channel.resumeReads();
/*     */                   }
/*     */                 });
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 207 */     if (Bits.anyAreSet(this.state, 2)) {
/* 208 */       throw UndertowServletMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 210 */     readIntoBufferNonBlocking();
/* 211 */     if (Bits.anyAreSet(this.state, 4)) {
/* 212 */       return 0;
/*     */     }
/* 214 */     if (this.pooled == null) {
/* 215 */       return 0;
/*     */     }
/* 217 */     return this.pooled.getBuffer().remaining();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 222 */     if (Bits.anyAreSet(this.state, 2)) {
/*     */       return;
/*     */     }
/* 225 */     this.state |= 0x6;
/* 226 */     if (this.pooled != null) {
/* 227 */       this.pooled.close();
/* 228 */       this.pooled = null;
/*     */     } 
/* 230 */     this.channel.suspendReads();
/* 231 */     this.channel.shutdownReads();
/*     */   }
/*     */   
/*     */   private class ServletInputStreamChannelListener
/*     */     implements ChannelListener<StreamSourceChannel> {
/*     */     public void handleEvent(StreamSourceChannel channel) {
/* 237 */       if (Bits.anyAreSet(UpgradeServletInputStream.this.state, 4)) {
/*     */         return;
/*     */       }
/* 240 */       UpgradeServletInputStream.this.state = UpgradeServletInputStream.this.state | 0x1;
/*     */       try {
/* 242 */         UpgradeServletInputStream.this.readIntoBufferNonBlocking();
/* 243 */         if (UpgradeServletInputStream.this.pooled != null) {
/* 244 */           UpgradeServletInputStream.this.state = UpgradeServletInputStream.this.state | 0x1;
/* 245 */           if (!Bits.anyAreSet(UpgradeServletInputStream.this.state, 4)) {
/* 246 */             UpgradeServletInputStream.this.listener.onDataAvailable();
/*     */           }
/*     */         } 
/* 249 */       } catch (Exception e) {
/* 250 */         if (UpgradeServletInputStream.this.pooled != null) {
/* 251 */           UpgradeServletInputStream.this.pooled.close();
/* 252 */           UpgradeServletInputStream.this.pooled = null;
/*     */         } 
/* 254 */         UpgradeServletInputStream.this.listener.onError(e);
/* 255 */         IoUtils.safeClose((Closeable)channel);
/*     */       } 
/* 257 */       if (Bits.anyAreSet(UpgradeServletInputStream.this.state, 4)) {
/* 258 */         if (Bits.anyAreClear(UpgradeServletInputStream.this.state, 8)) {
/*     */           try {
/* 260 */             UpgradeServletInputStream.this.state = UpgradeServletInputStream.this.state | 0x8;
/* 261 */             channel.shutdownReads();
/* 262 */             UpgradeServletInputStream.this.listener.onAllDataRead();
/* 263 */           } catch (IOException e) {
/* 264 */             if (UpgradeServletInputStream.this.pooled != null) {
/* 265 */               UpgradeServletInputStream.this.pooled.close();
/* 266 */               UpgradeServletInputStream.this.pooled = null;
/*     */             } 
/* 268 */             UpgradeServletInputStream.this.listener.onError(e);
/* 269 */             IoUtils.safeClose((Closeable)channel);
/*     */           } 
/*     */         }
/* 272 */       } else if (UpgradeServletInputStream.this.isReady()) {
/* 273 */         channel.suspendReads();
/*     */       } 
/*     */     }
/*     */     
/*     */     private ServletInputStreamChannelListener() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\UpgradeServletInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */