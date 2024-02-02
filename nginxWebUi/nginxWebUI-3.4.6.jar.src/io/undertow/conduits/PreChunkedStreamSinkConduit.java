/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.protocol.http.HttpAttachments;
/*     */ import io.undertow.util.Attachable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.AbstractStreamSinkConduit;
/*     */ import org.xnio.conduits.ConduitWritableByteChannel;
/*     */ import org.xnio.conduits.Conduits;
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
/*     */ public class PreChunkedStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private final ConduitListener<? super PreChunkedStreamSinkConduit> finishListener;
/*     */   private static final int FLAG_WRITES_SHUTDOWN = 1;
/*     */   private static final int FLAG_FINISHED = 4;
/*  55 */   int state = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final ChunkReader<PreChunkedStreamSinkConduit> chunkReader;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PreChunkedStreamSinkConduit(StreamSinkConduit next, ConduitListener<? super PreChunkedStreamSinkConduit> finishListener, Attachable attachable) {
/*  66 */     super(next);
/*     */     
/*  68 */     this.chunkReader = new ChunkReader<>(attachable, HttpAttachments.RESPONSE_TRAILERS, this);
/*  69 */     this.finishListener = finishListener;
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  74 */     return doWrite(src);
/*     */   }
/*     */ 
/*     */   
/*     */   int doWrite(ByteBuffer src) throws IOException {
/*  79 */     if (Bits.anyAreSet(this.state, 1)) {
/*  80 */       throw new ClosedChannelException();
/*     */     }
/*  82 */     if (this.chunkReader.getChunkRemaining() == -1L) {
/*  83 */       throw UndertowMessages.MESSAGES.extraDataWrittenAfterChunkEnd();
/*     */     }
/*  85 */     if (src.remaining() == 0) {
/*  86 */       return 0;
/*     */     }
/*  88 */     int oldPos = src.position();
/*  89 */     int oldLimit = src.limit();
/*  90 */     int ret = ((StreamSinkConduit)this.next).write(src);
/*  91 */     if (ret == 0) {
/*  92 */       return ret;
/*     */     }
/*  94 */     int newPos = src.position();
/*  95 */     src.position(oldPos);
/*  96 */     src.limit(oldPos + ret); try {
/*     */       do {
/*     */         int remaining;
/*  99 */         long chunkRemaining = this.chunkReader.readChunk(src);
/* 100 */         if (chunkRemaining == -1L) {
/* 101 */           if (src.remaining() == 0) {
/* 102 */             remaining = ret; return remaining;
/*     */           } 
/* 104 */           throw UndertowMessages.MESSAGES.extraDataWrittenAfterChunkEnd();
/*     */         } 
/* 106 */         if (chunkRemaining == 0L) {
/* 107 */           remaining = ret; return remaining;
/*     */         } 
/*     */         
/* 110 */         if (src.remaining() >= chunkRemaining) {
/* 111 */           src.position((int)(src.position() + chunkRemaining));
/* 112 */           remaining = 0;
/*     */         } else {
/* 114 */           remaining = (int)(chunkRemaining - src.remaining());
/* 115 */           src.position(src.limit());
/*     */         } 
/* 117 */         this.chunkReader.setChunkRemaining(remaining);
/* 118 */       } while (src.hasRemaining());
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/* 123 */       src.position(newPos);
/* 124 */       src.limit(oldLimit);
/*     */     } 
/* 126 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 132 */     for (int i = offset; i < length; i++) {
/* 133 */       if (srcs[i].hasRemaining()) {
/* 134 */         return write(srcs[i]);
/*     */       }
/*     */     } 
/* 137 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 142 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 147 */     if (!src.hasRemaining()) {
/* 148 */       terminateWrites();
/* 149 */       return 0;
/*     */     } 
/* 151 */     int ret = doWrite(src);
/* 152 */     terminateWrites();
/* 153 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 158 */     if (Bits.anyAreSet(this.state, 1)) {
/* 159 */       throw new ClosedChannelException();
/*     */     }
/* 161 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 166 */     if (Bits.anyAreSet(this.state, 1)) {
/* 167 */       throw new ClosedChannelException();
/*     */     }
/* 169 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 174 */     if (Bits.anyAreSet(this.state, 1)) {
/* 175 */       boolean val = ((StreamSinkConduit)this.next).flush();
/* 176 */       if (val && Bits.allAreClear(this.state, 4)) {
/* 177 */         invokeFinishListener();
/*     */       }
/* 179 */       return val;
/*     */     } 
/* 181 */     return ((StreamSinkConduit)this.next).flush();
/*     */   }
/*     */ 
/*     */   
/*     */   private void invokeFinishListener() {
/* 186 */     this.state |= 0x4;
/* 187 */     if (this.finishListener != null) {
/* 188 */       this.finishListener.handleEvent(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 194 */     if (Bits.anyAreSet(this.state, 1)) {
/*     */       return;
/*     */     }
/* 197 */     if (this.chunkReader.getChunkRemaining() != -1L) {
/* 198 */       throw UndertowMessages.MESSAGES.chunkedChannelClosedMidChunk();
/*     */     }
/* 200 */     this.state |= 0x1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 205 */     ((StreamSinkConduit)this.next).awaitWritable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 210 */     ((StreamSinkConduit)this.next).awaitWritable(time, timeUnit);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\PreChunkedStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */