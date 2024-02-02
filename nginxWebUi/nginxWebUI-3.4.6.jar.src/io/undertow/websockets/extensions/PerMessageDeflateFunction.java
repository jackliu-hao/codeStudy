/*     */ package io.undertow.websockets.extensions;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import io.undertow.websockets.core.StreamSinkFrameChannel;
/*     */ import io.undertow.websockets.core.StreamSourceFrameChannel;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.core.WebSocketLogger;
/*     */ import io.undertow.websockets.core.WebSocketMessages;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Deflater;
/*     */ import java.util.zip.Inflater;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.IoUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PerMessageDeflateFunction
/*     */   implements ExtensionFunction
/*     */ {
/*  52 */   private static final byte[] TAIL = new byte[] { 0, 0, -1, -1 };
/*     */ 
/*     */   
/*     */   private final int deflaterLevel;
/*     */   
/*     */   private final boolean compressContextTakeover;
/*     */   
/*     */   private final boolean decompressContextTakeover;
/*     */   
/*     */   private final Inflater decompress;
/*     */   
/*     */   private final Deflater compress;
/*     */   
/*     */   private StreamSourceFrameChannel currentReadChannel;
/*     */ 
/*     */   
/*     */   public PerMessageDeflateFunction(int deflaterLevel, boolean compressContextTakeover, boolean decompressContextTakeover) {
/*  69 */     this.deflaterLevel = deflaterLevel;
/*  70 */     this.decompress = new Inflater(true);
/*  71 */     this.compress = new Deflater(this.deflaterLevel, true);
/*  72 */     this.compressContextTakeover = compressContextTakeover;
/*  73 */     this.decompressContextTakeover = decompressContextTakeover;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeRsv(int rsv) {
/*  78 */     return rsv | 0x4;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasExtensionOpCode() {
/*  83 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized PooledByteBuffer transformForWrite(PooledByteBuffer pooledBuffer, StreamSinkFrameChannel channel, boolean lastFrame) throws IOException {
/*  88 */     ByteBuffer buffer = pooledBuffer.getBuffer();
/*  89 */     PooledByteBuffer inputBuffer = null;
/*  90 */     if (buffer.hasArray()) {
/*  91 */       this.compress.setInput(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
/*     */     } else {
/*  93 */       inputBuffer = toArrayBacked(buffer, channel.getWebSocketChannel().getBufferPool());
/*  94 */       this.compress.setInput(inputBuffer.getBuffer().array(), inputBuffer.getBuffer().arrayOffset() + inputBuffer.getBuffer().position(), inputBuffer.getBuffer().remaining());
/*     */     } 
/*     */     
/*  97 */     PooledByteBuffer output = allocateBufferWithArray(channel.getWebSocketChannel(), 0);
/*  98 */     ByteBuffer outputBuffer = output.getBuffer();
/*     */     
/* 100 */     boolean onceOnly = true;
/*     */     try {
/* 102 */       while ((!this.compress.needsInput() && !this.compress.finished()) || !outputBuffer.hasRemaining() || (onceOnly && lastFrame)) {
/* 103 */         onceOnly = false;
/*     */         
/* 105 */         if (!outputBuffer.hasRemaining()) {
/* 106 */           output = largerBuffer(output, channel.getWebSocketChannel(), outputBuffer.capacity() * 2);
/* 107 */           outputBuffer = output.getBuffer();
/*     */         } 
/*     */         
/* 110 */         int n = this.compress.deflate(outputBuffer
/* 111 */             .array(), outputBuffer
/* 112 */             .arrayOffset() + outputBuffer.position(), outputBuffer
/* 113 */             .remaining(), lastFrame ? 2 : 0);
/* 114 */         outputBuffer.position(outputBuffer.position() + n);
/*     */       } 
/*     */     } finally {
/*     */       
/* 118 */       IoUtils.safeClose(new Closeable[] { (Closeable)pooledBuffer, (Closeable)inputBuffer });
/*     */     } 
/*     */     
/* 121 */     if (lastFrame) {
/* 122 */       outputBuffer.put((byte)0);
/* 123 */       if (!this.compressContextTakeover) {
/* 124 */         this.compress.reset();
/*     */       }
/*     */     } 
/* 127 */     outputBuffer.flip();
/* 128 */     return output;
/*     */   }
/*     */   
/*     */   private PooledByteBuffer toArrayBacked(ByteBuffer buffer, ByteBufferPool pool) {
/* 132 */     if (pool.getBufferSize() < buffer.remaining()) {
/* 133 */       return (PooledByteBuffer)new ImmediatePooledByteBuffer(ByteBuffer.wrap(Buffers.take(buffer)));
/*     */     }
/* 135 */     PooledByteBuffer newBuf = pool.getArrayBackedPool().allocate();
/* 136 */     newBuf.getBuffer().put(buffer);
/* 137 */     newBuf.getBuffer().flip();
/* 138 */     return newBuf;
/*     */   }
/*     */   
/*     */   private PooledByteBuffer largerBuffer(PooledByteBuffer smaller, WebSocketChannel channel, int newSize) {
/* 142 */     ByteBuffer smallerBuffer = smaller.getBuffer();
/*     */     
/* 144 */     smallerBuffer.flip();
/*     */     
/* 146 */     PooledByteBuffer larger = allocateBufferWithArray(channel, newSize);
/* 147 */     larger.getBuffer().put(smallerBuffer);
/*     */     
/* 149 */     smaller.close();
/* 150 */     return larger;
/*     */   }
/*     */   
/*     */   private PooledByteBuffer allocateBufferWithArray(WebSocketChannel channel, int size) {
/* 154 */     if (size > 0 && 
/* 155 */       size > channel.getBufferPool().getBufferSize())
/*     */     {
/* 157 */       return (PooledByteBuffer)new ImmediatePooledByteBuffer(ByteBuffer.allocate(size));
/*     */     }
/*     */ 
/*     */     
/* 161 */     return channel.getBufferPool().getArrayBackedPool().allocate();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized PooledByteBuffer transformForRead(PooledByteBuffer pooledBuffer, StreamSourceFrameChannel channel, boolean lastFragmentOfMessage) throws IOException {
/* 166 */     if ((channel.getRsv() & 0x4) == 0)
/*     */     {
/* 168 */       return pooledBuffer;
/*     */     }
/* 170 */     PooledByteBuffer output = allocateBufferWithArray(channel.getWebSocketChannel(), 0);
/* 171 */     PooledByteBuffer inputBuffer = null;
/* 172 */     if (this.currentReadChannel != null && this.currentReadChannel != channel) {
/*     */ 
/*     */       
/* 175 */       this.decompress.setInput(TAIL);
/* 176 */       output = decompress(channel.getWebSocketChannel(), output);
/*     */     } 
/* 178 */     ByteBuffer buffer = pooledBuffer.getBuffer();
/*     */     
/* 180 */     if (buffer.hasArray()) {
/* 181 */       this.decompress.setInput(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
/*     */     } else {
/* 183 */       inputBuffer = toArrayBacked(buffer, channel.getWebSocketChannel().getBufferPool());
/* 184 */       this.decompress.setInput(inputBuffer.getBuffer().array(), inputBuffer.getBuffer().arrayOffset() + inputBuffer.getBuffer().position(), inputBuffer.getBuffer().remaining());
/*     */     } 
/*     */     try {
/* 187 */       output = decompress(channel.getWebSocketChannel(), output);
/*     */     } finally {
/*     */       
/* 190 */       IoUtils.safeClose(new Closeable[] { (Closeable)inputBuffer, (Closeable)pooledBuffer });
/*     */     } 
/*     */     
/* 193 */     if (lastFragmentOfMessage) {
/* 194 */       this.decompress.setInput(TAIL);
/* 195 */       output = decompress(channel.getWebSocketChannel(), output);
/* 196 */       this.currentReadChannel = null;
/*     */     } else {
/* 198 */       this.currentReadChannel = channel;
/*     */     } 
/*     */     
/* 201 */     output.getBuffer().flip();
/* 202 */     return output;
/*     */   }
/*     */   
/*     */   private PooledByteBuffer decompress(WebSocketChannel channel, PooledByteBuffer pooled) throws IOException {
/* 206 */     ByteBuffer buffer = pooled.getBuffer();
/*     */     
/* 208 */     while (!this.decompress.needsInput() && !this.decompress.finished()) {
/* 209 */       int n; if (!buffer.hasRemaining()) {
/* 210 */         pooled = largerBuffer(pooled, channel, buffer.capacity() * 2);
/* 211 */         buffer = pooled.getBuffer();
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 217 */         n = this.decompress.inflate(buffer.array(), buffer
/* 218 */             .arrayOffset() + buffer.position(), buffer
/* 219 */             .remaining());
/* 220 */       } catch (DataFormatException e) {
/* 221 */         WebSocketLogger.EXTENSION_LOGGER.debug(e.getMessage(), e);
/* 222 */         throw WebSocketMessages.MESSAGES.badCompressedPayload(e);
/*     */       } 
/* 224 */       buffer.position(buffer.position() + n);
/*     */     } 
/*     */     
/* 227 */     return pooled;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispose() {
/* 233 */     this.compress.end();
/* 234 */     this.decompress.end();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\extensions\PerMessageDeflateFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */