/*     */ package cn.hutool.socket.aio;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.thread.ThreadFactoryBuilder;
/*     */ import cn.hutool.core.thread.ThreadUtil;
/*     */ import cn.hutool.log.Log;
/*     */ import cn.hutool.log.LogFactory;
/*     */ import cn.hutool.socket.SocketConfig;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketOption;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AsynchronousChannelGroup;
/*     */ import java.nio.channels.AsynchronousServerSocketChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AioServer
/*     */   implements Closeable
/*     */ {
/*  25 */   private static final Log log = LogFactory.get();
/*  26 */   private static final AcceptHandler ACCEPT_HANDLER = new AcceptHandler();
/*     */ 
/*     */   
/*     */   private AsynchronousChannelGroup group;
/*     */ 
/*     */   
/*     */   private AsynchronousServerSocketChannel channel;
/*     */   
/*     */   protected IoAction<ByteBuffer> ioAction;
/*     */   
/*     */   protected final SocketConfig config;
/*     */ 
/*     */   
/*     */   public AioServer(int port) {
/*  40 */     this(new InetSocketAddress(port), new SocketConfig());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioServer(InetSocketAddress address, SocketConfig config) {
/*  50 */     this.config = config;
/*  51 */     init(address);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioServer init(InetSocketAddress address) {
/*     */     try {
/*  62 */       this.group = AsynchronousChannelGroup.withFixedThreadPool(this.config
/*  63 */           .getThreadPoolSize(), 
/*  64 */           ThreadFactoryBuilder.create().setNamePrefix("Hutool-socket-").build());
/*     */       
/*  66 */       this.channel = AsynchronousServerSocketChannel.open(this.group).bind(address);
/*  67 */     } catch (IOException e) {
/*  68 */       throw new IORuntimeException(e);
/*     */     } 
/*  70 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start(boolean sync) {
/*  79 */     doStart(sync);
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
/*     */ 
/*     */   
/*     */   public <T> AioServer setOption(SocketOption<T> name, T value) throws IOException {
/*  93 */     this.channel.setOption(name, value);
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IoAction<ByteBuffer> getIoAction() {
/* 103 */     return this.ioAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioServer setIoAction(IoAction<ByteBuffer> ioAction) {
/* 113 */     this.ioAction = ioAction;
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsynchronousServerSocketChannel getChannel() {
/* 123 */     return this.channel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioServer accept() {
/* 132 */     this.channel.accept(this, ACCEPT_HANDLER);
/* 133 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 142 */     return (null != this.channel && this.channel.isOpen());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 150 */     IoUtil.close(this.channel);
/*     */     
/* 152 */     if (null != this.group && false == this.group.isShutdown()) {
/*     */       try {
/* 154 */         this.group.shutdownNow();
/* 155 */       } catch (IOException iOException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 161 */     synchronized (this) {
/* 162 */       notify();
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
/*     */   private void doStart(boolean sync) {
/* 174 */     log.debug("Aio Server started, waiting for accept.", new Object[0]);
/*     */ 
/*     */     
/* 177 */     accept();
/*     */     
/* 179 */     if (sync)
/* 180 */       ThreadUtil.sync(this); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\aio\AioServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */