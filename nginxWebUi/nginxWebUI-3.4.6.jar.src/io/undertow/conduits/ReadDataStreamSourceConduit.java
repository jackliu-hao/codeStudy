/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.AbstractServerConnection;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*     */ import org.xnio.conduits.ConduitReadableByteChannel;
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
/*     */ public class ReadDataStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */ {
/*     */   private final AbstractServerConnection connection;
/*     */   
/*     */   public ReadDataStreamSourceConduit(StreamSourceConduit next, AbstractServerConnection connection) {
/*  43 */     super(next);
/*  44 */     this.connection = connection;
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  48 */     return target.transferFrom((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), position, count);
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  52 */     return IoUtils.transfer((ReadableByteChannel)new ConduitReadableByteChannel((StreamSourceConduit)this), count, throughBuffer, (WritableByteChannel)target);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*  57 */     PooledByteBuffer eb = this.connection.getExtraBytes();
/*  58 */     if (eb != null) {
/*  59 */       ByteBuffer buffer = eb.getBuffer();
/*  60 */       int result = Buffers.copy(dst, buffer);
/*  61 */       if (!buffer.hasRemaining()) {
/*  62 */         eb.close();
/*  63 */         this.connection.setExtraBytes(null);
/*     */       } 
/*  65 */       return result;
/*     */     } 
/*  67 */     return super.read(dst);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*  73 */     PooledByteBuffer eb = this.connection.getExtraBytes();
/*  74 */     if (eb != null) {
/*  75 */       ByteBuffer buffer = eb.getBuffer();
/*  76 */       int result = Buffers.copy(dsts, offs, len, buffer);
/*  77 */       if (!buffer.hasRemaining()) {
/*  78 */         eb.close();
/*  79 */         this.connection.setExtraBytes(null);
/*     */       } 
/*  81 */       return result;
/*     */     } 
/*  83 */     return super.read(dsts, offs, len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/*  89 */     if (this.connection.getExtraBytes() != null) {
/*  90 */       wakeupReads();
/*     */     } else {
/*  92 */       super.resumeReads();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable() throws IOException {
/*  98 */     if (this.connection.getExtraBytes() != null) {
/*     */       return;
/*     */     }
/* 101 */     super.awaitReadable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 106 */     if (this.connection.getExtraBytes() != null) {
/*     */       return;
/*     */     }
/* 109 */     super.awaitReadable(time, timeUnit);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\ReadDataStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */