/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.SuspendableReadChannel;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Transfer
/*     */ {
/*     */   public static <I extends StreamSourceChannel, O extends StreamSinkChannel> void initiateTransfer(I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super I> readExceptionHandler, ChannelExceptionHandler<? super O> writeExceptionHandler, ByteBufferPool pool) {
/*  54 */     if (pool == null) {
/*  55 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("pool");
/*     */     }
/*  57 */     PooledByteBuffer allocated = pool.allocate();
/*  58 */     boolean free = true; try {
/*     */       long read;
/*  60 */       ByteBuffer buffer = allocated.getBuffer();
/*     */       
/*     */       while (true) {
/*     */         try {
/*  64 */           read = source.read(buffer);
/*  65 */           buffer.flip();
/*  66 */         } catch (IOException e) {
/*  67 */           ChannelListeners.invokeChannelExceptionHandler((Channel)source, readExceptionHandler, e);
/*     */           return;
/*     */         } 
/*  70 */         if (read == 0L && !buffer.hasRemaining()) {
/*     */           break;
/*     */         }
/*  73 */         if (read == -1L && !buffer.hasRemaining()) {
/*  74 */           done(source, sink, sourceListener, sinkListener);
/*     */           return;
/*     */         } 
/*  77 */         while (buffer.hasRemaining()) {
/*     */           int res;
/*     */           try {
/*  80 */             res = sink.write(buffer);
/*  81 */           } catch (IOException e) {
/*  82 */             ChannelListeners.invokeChannelExceptionHandler((Channel)sink, writeExceptionHandler, e);
/*     */             return;
/*     */           } 
/*  85 */           if (res == 0) {
/*     */             break;
/*     */           }
/*     */         } 
/*  89 */         if (buffer.hasRemaining()) {
/*     */           break;
/*     */         }
/*  92 */         buffer.clear();
/*     */       } 
/*  94 */       PooledByteBuffer pooledByteBuffer = null;
/*  95 */       if (buffer.hasRemaining()) {
/*  96 */         pooledByteBuffer = allocated;
/*  97 */         free = false;
/*     */       } 
/*     */       
/* 100 */       TransferListener<I, O> listener = new TransferListener<>(pool, pooledByteBuffer, source, sink, sourceListener, sinkListener, writeExceptionHandler, readExceptionHandler, (read == -1L));
/* 101 */       sink.getWriteSetter().set(listener);
/* 102 */       source.getReadSetter().set(listener);
/*     */       
/* 104 */       if (pooledByteBuffer == null || buffer.capacity() != buffer.remaining())
/*     */       {
/* 106 */         source.resumeReads();
/*     */       }
/* 108 */       if (pooledByteBuffer != null)
/*     */       {
/* 110 */         sink.resumeWrites();
/*     */       }
/*     */     } finally {
/* 113 */       if (free) {
/* 114 */         allocated.close();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static <I extends StreamSourceChannel, O extends StreamSinkChannel> void done(I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener) {
/* 120 */     Channels.setReadListener((SuspendableReadChannel)source, sourceListener);
/* 121 */     if (sourceListener == null) {
/* 122 */       source.suspendReads();
/*     */     } else {
/* 124 */       source.wakeupReads();
/*     */     } 
/*     */     
/* 127 */     Channels.setWriteListener((SuspendableWriteChannel)sink, sinkListener);
/* 128 */     if (sinkListener == null) {
/* 129 */       sink.suspendWrites();
/*     */     } else {
/* 131 */       sink.wakeupWrites();
/*     */     } 
/*     */   }
/*     */   
/*     */   static final class TransferListener<I extends StreamSourceChannel, O extends StreamSinkChannel> implements ChannelListener<Channel> {
/*     */     private PooledByteBuffer pooledBuffer;
/*     */     private final ByteBufferPool pool;
/*     */     private final I source;
/*     */     private final O sink;
/*     */     private final ChannelListener<? super I> sourceListener;
/*     */     private final ChannelListener<? super O> sinkListener;
/*     */     private final ChannelExceptionHandler<? super O> writeExceptionHandler;
/*     */     private final ChannelExceptionHandler<? super I> readExceptionHandler;
/*     */     private boolean sourceDone;
/*     */     private boolean done = false;
/*     */     
/*     */     TransferListener(ByteBufferPool pool, PooledByteBuffer pooledBuffer, I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super O> writeExceptionHandler, ChannelExceptionHandler<? super I> readExceptionHandler, boolean sourceDone) {
/* 148 */       this.pool = pool;
/* 149 */       this.pooledBuffer = pooledBuffer;
/* 150 */       this.source = source;
/* 151 */       this.sink = sink;
/* 152 */       this.sourceListener = sourceListener;
/* 153 */       this.sinkListener = sinkListener;
/* 154 */       this.writeExceptionHandler = writeExceptionHandler;
/* 155 */       this.readExceptionHandler = readExceptionHandler;
/* 156 */       this.sourceDone = sourceDone;
/*     */     }
/*     */     
/*     */     public void handleEvent(Channel channel) {
/* 160 */       if (this.done) {
/* 161 */         if (channel instanceof StreamSinkChannel) {
/* 162 */           ((StreamSinkChannel)channel).suspendWrites();
/* 163 */         } else if (channel instanceof StreamSourceChannel) {
/* 164 */           ((StreamSourceChannel)channel).suspendReads();
/*     */         } 
/*     */         return;
/*     */       } 
/* 168 */       boolean noWrite = false;
/* 169 */       if (this.pooledBuffer == null) {
/* 170 */         this.pooledBuffer = this.pool.allocate();
/* 171 */         noWrite = true;
/* 172 */       } else if (channel instanceof StreamSourceChannel) {
/* 173 */         noWrite = true;
/* 174 */         this.pooledBuffer.getBuffer().compact();
/*     */       } 
/*     */       
/* 177 */       ByteBuffer buffer = this.pooledBuffer.getBuffer();
/*     */ 
/*     */       
/*     */       try {
/*     */         while (true) {
/* 182 */           boolean writeFailed = false;
/*     */           
/* 184 */           if (!noWrite) {
/* 185 */             while (buffer.hasRemaining()) {
/*     */               int res;
/*     */               try {
/* 188 */                 res = this.sink.write(buffer);
/* 189 */               } catch (IOException e) {
/* 190 */                 this.pooledBuffer.close();
/* 191 */                 this.pooledBuffer = null;
/* 192 */                 this.done = true;
/* 193 */                 ChannelListeners.invokeChannelExceptionHandler((Channel)this.sink, this.writeExceptionHandler, e);
/*     */                 return;
/*     */               } 
/* 196 */               if (res == 0) {
/* 197 */                 writeFailed = true;
/*     */                 break;
/*     */               } 
/*     */             } 
/* 201 */             if (this.sourceDone && !buffer.hasRemaining()) {
/* 202 */               this.done = true;
/* 203 */               Transfer.done(this.source, this.sink, this.sourceListener, this.sinkListener);
/*     */               return;
/*     */             } 
/* 206 */             buffer.compact();
/*     */           } 
/* 208 */           noWrite = false;
/*     */           
/* 210 */           if (buffer.hasRemaining() && !this.sourceDone) {
/*     */             long read; try {
/* 212 */               read = this.source.read(buffer);
/* 213 */               buffer.flip();
/* 214 */             } catch (IOException e) {
/* 215 */               IOException iOException1; this.pooledBuffer.close();
/* 216 */               this.pooledBuffer = null;
/* 217 */               this.done = true;
/* 218 */               ChannelListeners.invokeChannelExceptionHandler((Channel)this.source, this.readExceptionHandler, iOException1);
/*     */               return;
/*     */             } 
/* 221 */             if (read == 0L)
/*     */               break; 
/* 223 */             if (read == -1L) {
/* 224 */               this.sourceDone = true;
/* 225 */               if (!buffer.hasRemaining()) {
/* 226 */                 this.done = true;
/* 227 */                 Transfer.done(this.source, this.sink, this.sourceListener, this.sinkListener); return;
/*     */               } 
/*     */             } 
/*     */             continue;
/*     */           } 
/* 232 */           buffer.flip();
/* 233 */           if (writeFailed) {
/*     */             break;
/*     */           }
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 240 */         if (!buffer.hasRemaining()) {
/* 241 */           this.sink.suspendWrites();
/* 242 */         } else if (!this.sink.isWriteResumed()) {
/* 243 */           this.sink.resumeWrites();
/*     */         } 
/*     */         
/* 246 */         if (buffer.remaining() == buffer.capacity()) {
/* 247 */           this.source.suspendReads();
/* 248 */         } else if (!this.source.isReadResumed()) {
/* 249 */           this.source.resumeReads();
/*     */         } 
/*     */       } finally {
/* 252 */         if (this.pooledBuffer != null && !buffer.hasRemaining()) {
/* 253 */           this.pooledBuffer.close();
/* 254 */           this.pooledBuffer = null;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public String toString() {
/* 260 */       return "Transfer channel listener (" + this.source + " to " + this.sink + ") -> (" + this.sourceListener + " and " + this.sinkListener + ")";
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\Transfer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */