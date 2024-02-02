/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.protocol.http.HttpAttachments;
/*     */ import io.undertow.server.protocol.http.HttpServerConnection;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.PooledAdaptor;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*     */ import org.xnio.conduits.ConduitReadableByteChannel;
/*     */ import org.xnio.conduits.PushBackStreamSourceConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChunkedStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */ {
/*     */   @Deprecated
/*  56 */   public static final AttachmentKey<HeaderMap> TRAILERS = HttpAttachments.REQUEST_TRAILERS;
/*     */   
/*     */   private final BufferWrapper bufferWrapper;
/*     */   
/*     */   private final ConduitListener<? super ChunkedStreamSourceConduit> finishListener;
/*     */   
/*     */   private final HttpServerExchange exchange;
/*     */   private final Closeable closeable;
/*     */   private boolean closed;
/*     */   private boolean finishListenerInvoked;
/*     */   private long remainingAllowed;
/*     */   private final ChunkReader chunkReader;
/*     */   private final PushBackStreamSourceConduit channel;
/*     */   
/*     */   public ChunkedStreamSourceConduit(StreamSourceConduit next, PushBackStreamSourceConduit channel, ByteBufferPool pool, ConduitListener<? super ChunkedStreamSourceConduit> finishListener, Attachable attachable, Closeable closeable) {
/*  71 */     this(next, new BufferWrapper(pool, channel)
/*     */         {
/*     */           public PooledByteBuffer allocate() {
/*  74 */             return pool.allocate();
/*     */           }
/*     */ 
/*     */           
/*     */           public void pushBack(PooledByteBuffer pooled) {
/*  79 */             channel.pushBack((Pooled)new PooledAdaptor(pooled));
/*     */           }
/*     */         }finishListener, attachable, null, closeable, channel);
/*     */   }
/*     */   
/*     */   public ChunkedStreamSourceConduit(StreamSourceConduit next, HttpServerExchange exchange, ConduitListener<? super ChunkedStreamSourceConduit> finishListener) {
/*  85 */     this(next, new BufferWrapper(exchange)
/*     */         {
/*     */           public PooledByteBuffer allocate() {
/*  88 */             return exchange.getConnection().getByteBufferPool().allocate();
/*     */           }
/*     */ 
/*     */           
/*     */           public void pushBack(PooledByteBuffer pooled) {
/*  93 */             ((HttpServerConnection)exchange.getConnection()).ungetRequestBytes(pooled);
/*     */           }
/*  95 */         },  finishListener, (Attachable)exchange, exchange, (Closeable)exchange.getConnection(), null);
/*     */   }
/*     */   
/*     */   protected ChunkedStreamSourceConduit(StreamSourceConduit next, BufferWrapper bufferWrapper, ConduitListener<? super ChunkedStreamSourceConduit> finishListener, Attachable attachable, HttpServerExchange exchange, Closeable closeable, PushBackStreamSourceConduit channel) {
/*  99 */     super(next);
/* 100 */     this.bufferWrapper = bufferWrapper;
/* 101 */     this.finishListener = finishListener;
/* 102 */     this.remainingAllowed = Long.MIN_VALUE;
/* 103 */     this.chunkReader = new ChunkReader<>(attachable, HttpAttachments.REQUEST_TRAILERS, this);
/* 104 */     this.exchange = exchange;
/* 105 */     this.closeable = closeable;
/* 106 */     this.channel = channel;
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*     */     try {
/* 111 */       return target.transferFrom((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), position, count);
/* 112 */     } catch (IOException|RuntimeException|Error e) {
/* 113 */       IoUtils.safeClose(this.closeable);
/* 114 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void updateRemainingAllowed(int written) throws IOException {
/* 119 */     if (this.remainingAllowed == Long.MIN_VALUE) {
/* 120 */       if (this.exchange == null) {
/*     */         return;
/*     */       }
/* 123 */       long maxEntitySize = this.exchange.getMaxEntitySize();
/* 124 */       if (maxEntitySize <= 0L) {
/*     */         return;
/*     */       }
/* 127 */       this.remainingAllowed = maxEntitySize;
/*     */     } 
/*     */     
/* 130 */     this.remainingAllowed -= written;
/* 131 */     if (this.remainingAllowed < 0L) {
/*     */       
/* 133 */       Connectors.terminateRequest(this.exchange);
/* 134 */       this.closed = true;
/* 135 */       this.exchange.setPersistent(false);
/* 136 */       this.finishListener.handleEvent(this);
/* 137 */       throw UndertowMessages.MESSAGES.requestEntityWasTooLarge(this.exchange.getMaxEntitySize());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*     */     try {
/* 144 */       return IoUtils.transfer((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), count, throughBuffer, (WritableByteChannel)target);
/* 145 */     } catch (IOException|RuntimeException|Error e) {
/* 146 */       IoUtils.safeClose(this.closeable);
/* 147 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 153 */     for (int i = offset; i < length; i++) {
/* 154 */       if (dsts[i].hasRemaining()) {
/* 155 */         return read(dsts[i]);
/*     */       }
/*     */     } 
/* 158 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 163 */     super.terminateReads();
/* 164 */     if (this.channel != null)
/* 165 */       this.channel.terminateReads(); 
/* 166 */     if (!isFinished()) {
/* 167 */       this.exchange.setPersistent(false);
/* 168 */       super.terminateReads();
/* 169 */       throw UndertowMessages.MESSAGES.chunkedChannelClosedMidChunk();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 175 */     boolean invokeFinishListener = false;
/*     */     try {
/* 177 */       long chunkRemaining = this.chunkReader.getChunkRemaining();
/*     */       
/* 179 */       if (chunkRemaining == -1L) {
/* 180 */         if (!this.finishListenerInvoked) {
/* 181 */           invokeFinishListener = true;
/*     */         }
/* 183 */         return -1;
/*     */       } 
/* 185 */       if (this.closed) {
/* 186 */         throw new ClosedChannelException();
/*     */       }
/* 188 */       PooledByteBuffer pooled = this.bufferWrapper.allocate();
/* 189 */       ByteBuffer buf = pooled.getBuffer();
/* 190 */       boolean free = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 289 */     catch (IOException|RuntimeException|Error e) {
/* 290 */       IoUtils.safeClose(this.closeable);
/* 291 */       throw e;
/*     */     } finally {
/* 293 */       if (invokeFinishListener) {
/* 294 */         this.finishListenerInvoked = true;
/* 295 */         this.finishListener.handleEvent(this);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFinished() {
/* 302 */     return (this.closed || this.chunkReader.getChunkRemaining() == -1L);
/*     */   }
/*     */   
/*     */   static interface BufferWrapper {
/*     */     PooledByteBuffer allocate();
/*     */     
/*     */     void pushBack(PooledByteBuffer param1PooledByteBuffer);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\ChunkedStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */