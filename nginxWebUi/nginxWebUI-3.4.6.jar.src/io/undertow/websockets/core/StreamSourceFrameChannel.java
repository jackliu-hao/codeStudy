/*     */ package io.undertow.websockets.core;
/*     */ 
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.AbstractFramedChannel;
/*     */ import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
/*     */ import io.undertow.server.protocol.framed.FrameHeaderData;
/*     */ import io.undertow.websockets.core.function.ChannelFunction;
/*     */ import io.undertow.websockets.core.function.ChannelFunctionFileChannel;
/*     */ import io.undertow.websockets.core.protocol.version07.Masker;
/*     */ import io.undertow.websockets.core.protocol.version07.UTF8Checker;
/*     */ import io.undertow.websockets.extensions.ExtensionFunction;
/*     */ import io.undertow.websockets.extensions.NoopExtensionFunction;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class StreamSourceFrameChannel
/*     */   extends AbstractFramedStreamSourceChannel<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel>
/*     */ {
/*     */   protected final WebSocketFrameType type;
/*     */   private boolean finalFragment;
/*     */   private final int rsv;
/*     */   private final ChannelFunction[] functions;
/*     */   private final ExtensionFunction extensionFunction;
/*     */   private Masker masker;
/*     */   private UTF8Checker checker;
/*     */   
/*     */   protected StreamSourceFrameChannel(WebSocketChannel wsChannel, WebSocketFrameType type, PooledByteBuffer pooled, long frameLength) {
/*  55 */     this(wsChannel, type, 0, true, pooled, frameLength, (Masker)null, new ChannelFunction[0]);
/*     */   }
/*     */   
/*     */   protected StreamSourceFrameChannel(WebSocketChannel wsChannel, WebSocketFrameType type, int rsv, boolean finalFragment, PooledByteBuffer pooled, long frameLength, Masker masker, ChannelFunction... functions) {
/*  59 */     super(wsChannel, pooled, frameLength);
/*  60 */     this.type = type;
/*  61 */     this.finalFragment = finalFragment;
/*  62 */     this.rsv = rsv;
/*     */     
/*  64 */     this.functions = functions;
/*  65 */     this.masker = masker;
/*  66 */     this.checker = null;
/*  67 */     for (ChannelFunction func : functions) {
/*  68 */       if (func instanceof UTF8Checker) {
/*  69 */         this.checker = (UTF8Checker)func;
/*     */       }
/*     */     } 
/*  72 */     if (rsv > 0) {
/*  73 */       this.extensionFunction = wsChannel.getExtensionFunction();
/*     */     } else {
/*  75 */       this.extensionFunction = NoopExtensionFunction.INSTANCE;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketFrameType getType() {
/*  83 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFinalFragment() {
/*  91 */     return this.finalFragment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRsv() {
/*  98 */     return this.rsv;
/*     */   }
/*     */   
/*     */   int getWebSocketFrameCount() {
/* 102 */     return getReadFrameCount();
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketChannel getFramedChannel() {
/* 107 */     return (WebSocketChannel)super.getFramedChannel();
/*     */   }
/*     */   
/*     */   public WebSocketChannel getWebSocketChannel() {
/* 111 */     return getFramedChannel();
/*     */   }
/*     */   
/*     */   public void finalFrame() {
/* 115 */     lastFrame();
/* 116 */     this.finalFragment = true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleHeaderData(FrameHeaderData headerData) {
/* 121 */     super.handleHeaderData(headerData);
/* 122 */     if (((WebSocketFrame)headerData).isFinalFragment()) {
/* 123 */       finalFrame();
/*     */     }
/* 125 */     if (this.masker != null) {
/* 126 */       this.masker.newFrame(headerData);
/*     */     }
/* 128 */     if (this.functions != null) {
/* 129 */       for (ChannelFunction func : this.functions) {
/* 130 */         func.newFrame(headerData);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final long transferTo(long position, long count, FileChannel target) throws IOException {
/*     */     long r;
/* 139 */     if (this.functions != null && this.functions.length > 0) {
/* 140 */       r = super.transferTo(position, count, (FileChannel)new ChannelFunctionFileChannel(target, this.functions));
/*     */     } else {
/* 142 */       r = super.transferTo(position, count, target);
/*     */     } 
/* 144 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 151 */     return WebSocketUtils.transfer((ReadableByteChannel)this, count, throughBuffer, (WritableByteChannel)target);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 156 */     int position = dst.position();
/* 157 */     int r = super.read(dst);
/* 158 */     if (r > 0) {
/* 159 */       checker(dst, position, dst.position() - position, false);
/* 160 */     } else if (r == -1) {
/* 161 */       checkComplete();
/*     */     } 
/* 163 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public final long read(ByteBuffer[] dsts) throws IOException {
/* 168 */     return read(dsts, 0, dsts.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 173 */     Bounds[] old = new Bounds[length];
/* 174 */     for (int i = offset; i < length; i++) {
/* 175 */       ByteBuffer dst = dsts[i];
/* 176 */       old[i - offset] = new Bounds(dst.position(), dst.limit());
/*     */     } 
/* 178 */     long b = super.read(dsts, offset, length);
/* 179 */     if (b > 0L) {
/* 180 */       for (int j = offset; j < length; j++) {
/* 181 */         ByteBuffer dst = dsts[j];
/* 182 */         int oldPos = (old[j - offset]).position;
/* 183 */         afterRead(dst, oldPos, dst.position() - oldPos);
/*     */       } 
/* 185 */     } else if (b == -1L) {
/* 186 */       checkComplete();
/*     */     } 
/* 188 */     return b;
/*     */   }
/*     */   
/*     */   private void checkComplete() throws IOException {
/*     */     try {
/* 193 */       for (ChannelFunction func : this.functions) {
/* 194 */         func.complete();
/*     */       }
/* 196 */     } catch (UnsupportedEncodingException e) {
/* 197 */       getFramedChannel().markReadsBroken(e);
/* 198 */       throw e;
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
/*     */   
/*     */   protected void afterRead(ByteBuffer buffer, int position, int length) throws IOException {
/*     */     try {
/* 212 */       for (ChannelFunction func : this.functions) {
/* 213 */         func.afterRead(buffer, position, length);
/*     */       }
/* 215 */       if (isComplete()) {
/* 216 */         checkComplete();
/*     */       }
/* 218 */     } catch (UnsupportedEncodingException e) {
/* 219 */       getFramedChannel().markReadsBroken(e);
/* 220 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void checker(ByteBuffer buffer, int position, int length, boolean complete) throws IOException {
/* 225 */     if (this.checker == null) {
/*     */       return;
/*     */     }
/*     */     try {
/* 229 */       this.checker.afterRead(buffer, position, length);
/* 230 */       if (complete) {
/*     */         try {
/* 232 */           this.checker.complete();
/* 233 */         } catch (UnsupportedEncodingException e) {
/* 234 */           getFramedChannel().markReadsBroken(e);
/* 235 */           throw e;
/*     */         } 
/*     */       }
/* 238 */     } catch (UnsupportedEncodingException e) {
/* 239 */       getFramedChannel().markReadsBroken(e);
/* 240 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected PooledByteBuffer processFrameData(PooledByteBuffer frameData, boolean lastFragmentOfFrame) throws IOException {
/* 246 */     if (this.masker != null) {
/* 247 */       this.masker.afterRead(frameData.getBuffer(), frameData.getBuffer().position(), frameData.getBuffer().remaining());
/*     */     }
/*     */     try {
/* 250 */       return this.extensionFunction.transformForRead(frameData, this, (lastFragmentOfFrame && isFinalFragment()));
/* 251 */     } catch (IOException e) {
/* 252 */       getWebSocketChannel().markReadsBroken(new WebSocketFrameCorruptedException(e));
/* 253 */       throw e;
/* 254 */     } catch (Exception e) {
/* 255 */       getWebSocketChannel().markReadsBroken(new WebSocketFrameCorruptedException(e));
/* 256 */       throw new IOException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class Bounds {
/*     */     final int position;
/*     */     final int limit;
/*     */     
/*     */     Bounds(int position, int limit) {
/* 265 */       this.position = position;
/* 266 */       this.limit = limit;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\StreamSourceFrameChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */