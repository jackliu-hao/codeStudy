/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.XnioByteBufferPool;
/*     */ import io.undertow.websockets.core.UTF8Output;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Pool;
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
/*     */ public abstract class StringReadChannelListener
/*     */   implements ChannelListener<StreamSourceChannel>
/*     */ {
/*  42 */   private final UTF8Output string = new UTF8Output();
/*     */   private final ByteBufferPool bufferPool;
/*     */   
/*     */   public StringReadChannelListener(ByteBufferPool bufferPool) {
/*  46 */     this.bufferPool = bufferPool;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public StringReadChannelListener(Pool<ByteBuffer> bufferPool) {
/*  51 */     this.bufferPool = (ByteBufferPool)new XnioByteBufferPool(bufferPool);
/*     */   }
/*     */   
/*     */   public void setup(StreamSourceChannel channel) {
/*  55 */     PooledByteBuffer resource = this.bufferPool.allocate();
/*  56 */     ByteBuffer buffer = resource.getBuffer();
/*     */     try {
/*  58 */       int r = 0;
/*     */       do {
/*  60 */         r = channel.read(buffer);
/*  61 */         if (r == 0) {
/*  62 */           channel.getReadSetter().set(this);
/*  63 */           channel.resumeReads();
/*  64 */         } else if (r == -1) {
/*  65 */           stringDone(this.string.extract());
/*  66 */           IoUtils.safeClose((Closeable)channel);
/*     */         } else {
/*  68 */           buffer.flip();
/*  69 */           this.string.write(new ByteBuffer[] { buffer });
/*     */         } 
/*  71 */       } while (r > 0);
/*  72 */     } catch (IOException e) {
/*  73 */       error(e);
/*     */     } finally {
/*  75 */       resource.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleEvent(StreamSourceChannel channel) {
/*  81 */     PooledByteBuffer resource = this.bufferPool.allocate();
/*  82 */     ByteBuffer buffer = resource.getBuffer();
/*     */     try {
/*  84 */       int r = 0;
/*     */       do {
/*  86 */         r = channel.read(buffer);
/*  87 */         if (r == 0)
/*     */           return; 
/*  89 */         if (r == -1) {
/*  90 */           stringDone(this.string.extract());
/*  91 */           IoUtils.safeClose((Closeable)channel);
/*     */         } else {
/*  93 */           buffer.flip();
/*  94 */           this.string.write(new ByteBuffer[] { buffer });
/*     */         } 
/*  96 */       } while (r > 0);
/*  97 */     } catch (IOException e) {
/*  98 */       error(e);
/*     */     } finally {
/* 100 */       resource.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract void stringDone(String paramString);
/*     */   
/*     */   protected abstract void error(IOException paramIOException);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\StringReadChannelListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */