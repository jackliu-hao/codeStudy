/*     */ package cn.hutool.socket.aio;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.socket.SocketConfig;
/*     */ import cn.hutool.socket.SocketUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AsynchronousSocketChannel;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AioSession
/*     */   implements Closeable
/*     */ {
/*  26 */   private static final ReadHandler READ_HANDLER = new ReadHandler();
/*     */ 
/*     */   
/*     */   private final AsynchronousSocketChannel channel;
/*     */ 
/*     */   
/*     */   private final IoAction<ByteBuffer> ioAction;
/*     */ 
/*     */   
/*     */   private ByteBuffer readBuffer;
/*     */   
/*     */   private ByteBuffer writeBuffer;
/*     */   
/*     */   private final long readTimeout;
/*     */   
/*     */   private final long writeTimeout;
/*     */ 
/*     */   
/*     */   public AioSession(AsynchronousSocketChannel channel, IoAction<ByteBuffer> ioAction, SocketConfig config) {
/*  45 */     this.channel = channel;
/*  46 */     this.ioAction = ioAction;
/*     */     
/*  48 */     this.readBuffer = ByteBuffer.allocate(config.getReadBufferSize());
/*  49 */     this.writeBuffer = ByteBuffer.allocate(config.getWriteBufferSize());
/*  50 */     this.readTimeout = config.getReadTimeout();
/*  51 */     this.writeTimeout = config.getWriteTimeout();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsynchronousSocketChannel getChannel() {
/*  60 */     return this.channel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer getReadBuffer() {
/*  69 */     return this.readBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer getWriteBuffer() {
/*  78 */     return this.writeBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IoAction<ByteBuffer> getIoAction() {
/*  87 */     return this.ioAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SocketAddress getRemoteAddress() {
/*  96 */     return SocketUtil.getRemoteAddress(this.channel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioSession read() {
/* 105 */     return read(READ_HANDLER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioSession read(CompletionHandler<Integer, AioSession> handler) {
/* 115 */     if (isOpen()) {
/* 116 */       this.readBuffer.clear();
/* 117 */       this.channel.read(this.readBuffer, Math.max(this.readTimeout, 0L), TimeUnit.MILLISECONDS, this, handler);
/*     */     } 
/* 119 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioSession writeAndClose(ByteBuffer data) {
/* 129 */     write(data);
/* 130 */     return closeOut();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Future<Integer> write(ByteBuffer data) {
/* 140 */     return this.channel.write(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioSession write(ByteBuffer data, CompletionHandler<Integer, AioSession> handler) {
/* 151 */     this.channel.write(data, Math.max(this.writeTimeout, 0L), TimeUnit.MILLISECONDS, this, handler);
/* 152 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 162 */     return (null != this.channel && this.channel.isOpen());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioSession closeIn() {
/* 171 */     if (null != this.channel) {
/*     */       try {
/* 173 */         this.channel.shutdownInput();
/* 174 */       } catch (IOException e) {
/* 175 */         throw new IORuntimeException(e);
/*     */       } 
/*     */     }
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioSession closeOut() {
/* 187 */     if (null != this.channel) {
/*     */       try {
/* 189 */         this.channel.shutdownOutput();
/* 190 */       } catch (IOException e) {
/* 191 */         throw new IORuntimeException(e);
/*     */       } 
/*     */     }
/* 194 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 202 */     IoUtil.close(this.channel);
/* 203 */     this.readBuffer = null;
/* 204 */     this.writeBuffer = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void callbackRead() {
/* 211 */     this.readBuffer.flip();
/* 212 */     this.ioAction.doAction(this, this.readBuffer);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\aio\AioSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */