/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
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
/*     */ public class AbstractFramedStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*  51 */   private final Deque<Frame> frameQueue = new ArrayDeque<>();
/*     */ 
/*     */ 
/*     */   
/*  55 */   private long queuedData = 0L;
/*     */ 
/*     */ 
/*     */   
/*  59 */   private int bufferCount = 0;
/*     */ 
/*     */   
/*     */   private int state;
/*     */ 
/*     */   
/*     */   private static final int FLAG_WRITES_TERMINATED = 1;
/*     */ 
/*     */   
/*     */   private static final int FLAG_DELEGATE_SHUTDOWN = 2;
/*     */ 
/*     */   
/*     */   protected AbstractFramedStreamSinkConduit(StreamSinkConduit next) {
/*  72 */     super(next);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void queueFrame(FrameCallBack callback, ByteBuffer... data) {
/*  82 */     this.queuedData += Buffers.remaining((Buffer[])data);
/*  83 */     this.bufferCount += data.length;
/*  84 */     this.frameQueue.add(new Frame(callback, data, 0, data.length));
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  88 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  92 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel((StreamSinkConduit)this));
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  97 */     if (Bits.anyAreSet(this.state, 1)) {
/*  98 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 100 */     return (int)doWrite(new ByteBuffer[] { src }, 0, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 105 */     if (Bits.anyAreSet(this.state, 1)) {
/* 106 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 108 */     return doWrite(srcs, offs, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 113 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 118 */     return Conduits.writeFinalBasic((StreamSinkConduit)this, srcs, offs, len);
/*     */   }
/*     */ 
/*     */   
/*     */   private long doWrite(ByteBuffer[] additionalData, int offs, int len) throws IOException {
/* 123 */     ByteBuffer[] buffers = new ByteBuffer[this.bufferCount + ((additionalData == null) ? 0 : len)];
/* 124 */     int count = 0;
/* 125 */     for (Frame frame : this.frameQueue) {
/* 126 */       for (int i = frame.offs; i < frame.offs + frame.len; i++) {
/* 127 */         buffers[count++] = frame.data[i];
/*     */       }
/*     */     } 
/*     */     
/* 131 */     if (additionalData != null) {
/* 132 */       for (int i = offs; i < offs + len; i++) {
/* 133 */         buffers[count++] = additionalData[i];
/*     */       }
/*     */     }
/*     */     try {
/* 137 */       long written = ((StreamSinkConduit)this.next).write(buffers, 0, buffers.length);
/* 138 */       if (written > this.queuedData) {
/* 139 */         this.queuedData = 0L;
/*     */       } else {
/* 141 */         this.queuedData -= written;
/*     */       } 
/* 143 */       long toAllocate = written;
/* 144 */       Frame frame = this.frameQueue.peek();
/* 145 */       while (frame != null) {
/* 146 */         if (frame.remaining > toAllocate) {
/* 147 */           frame.remaining -= toAllocate;
/* 148 */           return 0L;
/*     */         } 
/* 150 */         this.frameQueue.poll();
/*     */ 
/*     */         
/* 153 */         FrameCallBack cb = frame.callback;
/* 154 */         if (cb != null) {
/* 155 */           cb.done();
/*     */         }
/* 157 */         this.bufferCount -= frame.len;
/* 158 */         toAllocate -= frame.remaining;
/*     */         
/* 160 */         frame = this.frameQueue.peek();
/*     */       } 
/* 162 */       return toAllocate;
/*     */     }
/* 164 */     catch (IOException|RuntimeException|Error e) {
/* 165 */       IOException ioe = (e instanceof IOException) ? (IOException)e : new IOException(e);
/*     */ 
/*     */       
/* 168 */       try { for (Frame frame : this.frameQueue) {
/* 169 */           FrameCallBack cb = frame.callback;
/* 170 */           if (cb != null) {
/* 171 */             cb.failed(ioe);
/*     */           }
/*     */         } 
/* 174 */         this.frameQueue.clear();
/* 175 */         this.bufferCount = 0;
/* 176 */         this.queuedData = 0L;
/*     */         
/* 178 */         throw e; } finally { Exception exception = null; }
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   protected long queuedDataLength() {
/* 184 */     return this.queuedData;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 190 */     if (Bits.anyAreSet(this.state, 1)) {
/*     */       return;
/*     */     }
/* 193 */     queueCloseFrames();
/* 194 */     this.state |= 0x1;
/* 195 */     if (this.queuedData == 0L) {
/* 196 */       this.state |= 0x2;
/* 197 */       doTerminateWrites();
/* 198 */       finished();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void doTerminateWrites() throws IOException {
/* 203 */     ((StreamSinkConduit)this.next).terminateWrites();
/*     */   }
/*     */   
/*     */   protected boolean flushQueuedData() throws IOException {
/* 207 */     if (this.queuedData > 0L) {
/* 208 */       doWrite(null, 0, 0);
/*     */     }
/* 210 */     if (this.queuedData > 0L) {
/* 211 */       return false;
/*     */     }
/* 213 */     if (Bits.anyAreSet(this.state, 1) && Bits.allAreClear(this.state, 2)) {
/* 214 */       doTerminateWrites();
/* 215 */       this.state |= 0x2;
/* 216 */       finished();
/*     */     } 
/* 218 */     return ((StreamSinkConduit)this.next).flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 223 */     for (Frame frame : this.frameQueue) {
/* 224 */       FrameCallBack cb = frame.callback;
/* 225 */       if (cb != null) {
/* 226 */         cb.failed(UndertowMessages.MESSAGES.channelIsClosed());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean isWritesTerminated() {
/* 232 */     return Bits.anyAreSet(this.state, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void queueCloseFrames() {}
/*     */ 
/*     */ 
/*     */   
/*     */   protected void finished() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface FrameCallBack
/*     */   {
/*     */     void done();
/*     */ 
/*     */ 
/*     */     
/*     */     void failed(IOException param1IOException);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Frame
/*     */   {
/*     */     final AbstractFramedStreamSinkConduit.FrameCallBack callback;
/*     */ 
/*     */     
/*     */     final ByteBuffer[] data;
/*     */ 
/*     */     
/*     */     final int offs;
/*     */     
/*     */     final int len;
/*     */     
/*     */     long remaining;
/*     */ 
/*     */     
/*     */     private Frame(AbstractFramedStreamSinkConduit.FrameCallBack callback, ByteBuffer[] data, int offs, int len) {
/* 272 */       this.callback = callback;
/* 273 */       this.data = data;
/* 274 */       this.offs = offs;
/* 275 */       this.len = len;
/* 276 */       this.remaining = Buffers.remaining((Buffer[])data, offs, len);
/*     */     }
/*     */   }
/*     */   
/*     */   protected static class PooledBufferFrameCallback
/*     */     implements FrameCallBack {
/*     */     private final PooledByteBuffer buffer;
/*     */     
/*     */     public PooledBufferFrameCallback(PooledByteBuffer buffer) {
/* 285 */       this.buffer = buffer;
/*     */     }
/*     */ 
/*     */     
/*     */     public void done() {
/* 290 */       this.buffer.close();
/*     */     }
/*     */ 
/*     */     
/*     */     public void failed(IOException e) {
/* 295 */       this.buffer.close();
/*     */     }
/*     */   }
/*     */   
/*     */   protected static class PooledBuffersFrameCallback
/*     */     implements FrameCallBack
/*     */   {
/*     */     private final PooledByteBuffer[] buffers;
/*     */     
/*     */     public PooledBuffersFrameCallback(PooledByteBuffer... buffers) {
/* 305 */       this.buffers = buffers;
/*     */     }
/*     */ 
/*     */     
/*     */     public void done() {
/* 310 */       for (PooledByteBuffer buffer : this.buffers) {
/* 311 */         buffer.close();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void failed(IOException e) {
/* 317 */       done();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\AbstractFramedStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */