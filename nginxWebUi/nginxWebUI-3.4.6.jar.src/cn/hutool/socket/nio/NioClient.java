/*     */ package cn.hutool.socket.nio;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.thread.ThreadUtil;
/*     */ import cn.hutool.socket.SocketRuntimeException;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Iterator;
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
/*     */ public class NioClient
/*     */   implements Closeable
/*     */ {
/*     */   private Selector selector;
/*     */   private SocketChannel channel;
/*     */   private ChannelHandler handler;
/*     */   
/*     */   public NioClient(String host, int port) {
/*  36 */     init(new InetSocketAddress(host, port));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioClient(InetSocketAddress address) {
/*  45 */     init(address);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioClient init(InetSocketAddress address) {
/*     */     try {
/*  57 */       this.channel = SocketChannel.open();
/*  58 */       this.channel.configureBlocking(false);
/*  59 */       this.channel.connect(address);
/*     */ 
/*     */       
/*  62 */       this.selector = Selector.open();
/*  63 */       this.channel.register(this.selector, 1);
/*     */ 
/*     */ 
/*     */       
/*  67 */       while (false == this.channel.finishConnect());
/*  68 */     } catch (IOException e) {
/*  69 */       close();
/*  70 */       throw new IORuntimeException(e);
/*     */     } 
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioClient setChannelHandler(ChannelHandler handler) {
/*  82 */     this.handler = handler;
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void listen() {
/*  90 */     ThreadUtil.execute(() -> {
/*     */           try {
/*     */             doListen();
/*  93 */           } catch (IOException e) {
/*     */             e.printStackTrace();
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doListen() throws IOException {
/* 105 */     while (this.selector.isOpen() && 0 != this.selector.select()) {
/*     */       
/* 107 */       Iterator<SelectionKey> keyIter = this.selector.selectedKeys().iterator();
/* 108 */       while (keyIter.hasNext()) {
/* 109 */         handle(keyIter.next());
/* 110 */         keyIter.remove();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void handle(SelectionKey key) {
/* 122 */     if (key.isReadable()) {
/* 123 */       SocketChannel socketChannel = (SocketChannel)key.channel();
/*     */       try {
/* 125 */         this.handler.handle(socketChannel);
/* 126 */       } catch (Exception e) {
/* 127 */         throw new SocketRuntimeException(e);
/*     */       } 
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
/*     */   public NioClient write(ByteBuffer... datas) {
/*     */     try {
/* 141 */       this.channel.write(datas);
/* 142 */     } catch (IOException e) {
/* 143 */       throw new IORuntimeException(e);
/*     */     } 
/* 145 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SocketChannel getChannel() {
/* 155 */     return this.channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 160 */     IoUtil.close(this.selector);
/* 161 */     IoUtil.close(this.channel);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\nio\NioClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */