/*     */ package cn.hutool.socket.aio;
/*     */ 
/*     */ import cn.hutool.socket.ChannelUtil;
/*     */ import cn.hutool.socket.SocketConfig;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketOption;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AsynchronousSocketChannel;
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
/*     */ public class AioClient
/*     */   implements Closeable
/*     */ {
/*     */   private final AioSession session;
/*     */   
/*     */   public AioClient(InetSocketAddress address, IoAction<ByteBuffer> ioAction) {
/*  30 */     this(address, ioAction, new SocketConfig());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioClient(InetSocketAddress address, IoAction<ByteBuffer> ioAction, SocketConfig config) {
/*  41 */     this(createChannel(address, config.getThreadPoolSize()), ioAction, config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioClient(AsynchronousSocketChannel channel, IoAction<ByteBuffer> ioAction, SocketConfig config) {
/*  52 */     this.session = new AioSession(channel, ioAction, config);
/*  53 */     ioAction.accept(this.session);
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
/*     */   public <T> AioClient setOption(SocketOption<T> name, T value) throws IOException {
/*  67 */     this.session.getChannel().setOption(name, value);
/*  68 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IoAction<ByteBuffer> getIoAction() {
/*  77 */     return this.session.getIoAction();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioClient read() {
/*  86 */     this.session.read();
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AioClient write(ByteBuffer data) {
/*  97 */     this.session.write(data);
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 106 */     this.session.close();
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
/*     */   private static AsynchronousSocketChannel createChannel(InetSocketAddress address, int poolSize) {
/* 118 */     return ChannelUtil.connect(ChannelUtil.createFixedGroup(poolSize), address);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\aio\AioClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */