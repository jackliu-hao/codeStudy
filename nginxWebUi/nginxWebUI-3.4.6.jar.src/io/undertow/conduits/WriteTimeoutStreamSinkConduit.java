/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.server.OpenListener;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.AbstractStreamSinkConduit;
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
/*     */ public final class WriteTimeoutStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private XnioExecutor.Key handle;
/*     */   private final StreamConnection connection;
/*  52 */   private volatile long expireTime = -1L;
/*     */   
/*     */   private final OpenListener openListener;
/*     */   private static final int FUZZ_FACTOR = 50;
/*     */   
/*  57 */   private final Runnable timeoutCommand = new Runnable()
/*     */     {
/*     */       public void run() {
/*  60 */         WriteTimeoutStreamSinkConduit.this.handle = null;
/*  61 */         if (WriteTimeoutStreamSinkConduit.this.expireTime == -1L) {
/*     */           return;
/*     */         }
/*  64 */         long current = System.currentTimeMillis();
/*  65 */         if (current < WriteTimeoutStreamSinkConduit.this.expireTime) {
/*     */           
/*  67 */           WriteTimeoutStreamSinkConduit.this.handle = WorkerUtils.executeAfter(WriteTimeoutStreamSinkConduit.this.getWriteThread(), WriteTimeoutStreamSinkConduit.this.timeoutCommand, WriteTimeoutStreamSinkConduit.this.expireTime - current + 50L, TimeUnit.MILLISECONDS);
/*     */           return;
/*     */         } 
/*  70 */         UndertowLogger.REQUEST_LOGGER.tracef("Timing out channel %s due to inactivity", WriteTimeoutStreamSinkConduit.this.connection.getSinkChannel());
/*  71 */         IoUtils.safeClose((Closeable)WriteTimeoutStreamSinkConduit.this.connection);
/*  72 */         if (WriteTimeoutStreamSinkConduit.this.connection.getSourceChannel().isReadResumed()) {
/*  73 */           ChannelListeners.invokeChannelListener((Channel)WriteTimeoutStreamSinkConduit.this.connection.getSourceChannel(), WriteTimeoutStreamSinkConduit.this.connection.getSourceChannel().getReadListener());
/*     */         }
/*  75 */         if (WriteTimeoutStreamSinkConduit.this.connection.getSinkChannel().isWriteResumed()) {
/*  76 */           ChannelListeners.invokeChannelListener((Channel)WriteTimeoutStreamSinkConduit.this.connection.getSinkChannel(), WriteTimeoutStreamSinkConduit.this.connection.getSinkChannel().getWriteListener());
/*     */         }
/*     */       }
/*     */     };
/*     */   
/*     */   public WriteTimeoutStreamSinkConduit(StreamSinkConduit delegate, StreamConnection connection, OpenListener openListener) {
/*  82 */     super(delegate);
/*  83 */     this.connection = connection;
/*  84 */     this.openListener = openListener;
/*     */   }
/*     */   
/*     */   private void handleWriteTimeout(long ret) throws IOException {
/*  88 */     if (!this.connection.isOpen()) {
/*     */       return;
/*     */     }
/*  91 */     if (ret == 0L && this.handle != null) {
/*     */       return;
/*     */     }
/*  94 */     Integer timeout = getTimeout();
/*  95 */     if (timeout == null || timeout.intValue() <= 0) {
/*     */       return;
/*     */     }
/*  98 */     long currentTime = System.currentTimeMillis();
/*  99 */     long expireTimeVar = this.expireTime;
/* 100 */     if (expireTimeVar != -1L && currentTime > expireTimeVar) {
/* 101 */       IoUtils.safeClose((Closeable)this.connection);
/* 102 */       throw new ClosedChannelException();
/*     */     } 
/* 104 */     this.expireTime = currentTime + timeout.intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 109 */     int ret = super.write(src);
/* 110 */     handleWriteTimeout(ret);
/* 111 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 116 */     long ret = super.write(srcs, offset, length);
/* 117 */     handleWriteTimeout(ret);
/* 118 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 123 */     int ret = super.writeFinal(src);
/* 124 */     handleWriteTimeout(ret);
/* 125 */     if (!src.hasRemaining() && 
/* 126 */       this.handle != null) {
/* 127 */       this.handle.remove();
/* 128 */       this.handle = null;
/*     */     } 
/*     */     
/* 131 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 136 */     long ret = super.writeFinal(srcs, offset, length);
/* 137 */     handleWriteTimeout(ret);
/* 138 */     if (!Buffers.hasRemaining((Buffer[])srcs) && 
/* 139 */       this.handle != null) {
/* 140 */       this.handle.remove();
/* 141 */       this.handle = null;
/*     */     } 
/*     */     
/* 144 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 149 */     long ret = super.transferFrom(src, position, count);
/* 150 */     handleWriteTimeout(ret);
/* 151 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 156 */     long ret = super.transferFrom(source, count, throughBuffer);
/* 157 */     handleWriteTimeout(ret);
/* 158 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 163 */     Integer timeout = getTimeout();
/* 164 */     if (timeout != null && timeout.intValue() > 0) {
/* 165 */       super.awaitWritable((timeout.intValue() + 50), TimeUnit.MILLISECONDS);
/*     */     } else {
/* 167 */       super.awaitWritable();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 173 */     Integer timeout = getTimeout();
/* 174 */     if (timeout != null && timeout.intValue() > 0) {
/* 175 */       long millis = timeUnit.toMillis(time);
/* 176 */       super.awaitWritable(Math.min(millis, (timeout.intValue() + 50)), TimeUnit.MILLISECONDS);
/*     */     } else {
/* 178 */       super.awaitWritable(time, timeUnit);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Integer getTimeout() {
/* 183 */     Integer timeout = Integer.valueOf(0);
/*     */     try {
/* 185 */       timeout = (Integer)this.connection.getSourceChannel().getOption(Options.WRITE_TIMEOUT);
/* 186 */     } catch (IOException iOException) {}
/*     */ 
/*     */     
/* 189 */     Integer idleTimeout = (Integer)this.openListener.getUndertowOptions().get(UndertowOptions.IDLE_TIMEOUT);
/* 190 */     if ((timeout == null || timeout.intValue() <= 0) && idleTimeout != null) {
/* 191 */       timeout = idleTimeout;
/* 192 */     } else if (timeout != null && idleTimeout != null && idleTimeout.intValue() > 0) {
/* 193 */       timeout = Integer.valueOf(Math.min(timeout.intValue(), idleTimeout.intValue()));
/*     */     } 
/* 195 */     return timeout;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 201 */     super.terminateWrites();
/* 202 */     if (this.handle != null) {
/* 203 */       this.handle.remove();
/* 204 */       this.handle = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 210 */     super.truncateWrites();
/* 211 */     if (this.handle != null) {
/* 212 */       this.handle.remove();
/* 213 */       this.handle = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeWrites() {
/* 219 */     super.resumeWrites();
/* 220 */     handleResumeTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendWrites() {
/* 225 */     super.suspendWrites();
/* 226 */     XnioExecutor.Key handle = this.handle;
/* 227 */     if (handle != null) {
/* 228 */       handle.remove();
/* 229 */       this.handle = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupWrites() {
/* 235 */     super.wakeupWrites();
/* 236 */     handleResumeTimeout();
/*     */   }
/*     */   
/*     */   private void handleResumeTimeout() {
/* 240 */     Integer timeout = getTimeout();
/* 241 */     if (timeout == null || timeout.intValue() <= 0) {
/*     */       return;
/*     */     }
/* 244 */     long currentTime = System.currentTimeMillis();
/* 245 */     this.expireTime = currentTime + timeout.intValue();
/* 246 */     XnioExecutor.Key key = this.handle;
/* 247 */     if (key == null)
/* 248 */       this.handle = this.connection.getIoThread().executeAfter(this.timeoutCommand, timeout.intValue(), TimeUnit.MILLISECONDS); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\WriteTimeoutStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */