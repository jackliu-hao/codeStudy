/*     */ package cn.hutool.socket.nio;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.log.Log;
/*     */ import cn.hutool.log.StaticLog;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NioServer
/*     */   implements Closeable
/*     */ {
/*  24 */   private static final Log log = Log.get();
/*     */   
/*  26 */   private static final AcceptHandler ACCEPT_HANDLER = new AcceptHandler();
/*     */ 
/*     */   
/*     */   private Selector selector;
/*     */ 
/*     */   
/*     */   private ServerSocketChannel serverSocketChannel;
/*     */   
/*     */   private ChannelHandler handler;
/*     */ 
/*     */   
/*     */   public NioServer(int port) {
/*  38 */     init(new InetSocketAddress(port));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioServer init(InetSocketAddress address) {
/*     */     try {
/*  50 */       this.serverSocketChannel = ServerSocketChannel.open();
/*     */       
/*  52 */       this.serverSocketChannel.configureBlocking(false);
/*     */       
/*  54 */       this.serverSocketChannel.bind(address);
/*     */ 
/*     */       
/*  57 */       this.selector = Selector.open();
/*     */       
/*  59 */       this.serverSocketChannel.register(this.selector, 16);
/*  60 */     } catch (IOException e) {
/*  61 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/*  64 */     log.debug("Server listen on: [{}]...", new Object[] { address });
/*     */     
/*  66 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NioServer setChannelHandler(ChannelHandler handler) {
/*  76 */     this.handler = handler;
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Selector getSelector() {
/*  86 */     return this.selector;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  95 */     listen();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void listen() {
/*     */     try {
/* 103 */       doListen();
/* 104 */     } catch (IOException e) {
/* 105 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doListen() throws IOException {
/* 115 */     while (this.selector.isOpen() && 0 != this.selector.select()) {
/*     */       
/* 117 */       Iterator<SelectionKey> keyIter = this.selector.selectedKeys().iterator();
/* 118 */       while (keyIter.hasNext()) {
/* 119 */         handle(keyIter.next());
/* 120 */         keyIter.remove();
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
/* 132 */     if (key.isAcceptable()) {
/* 133 */       ACCEPT_HANDLER.completed((ServerSocketChannel)key.channel(), this);
/*     */     }
/*     */ 
/*     */     
/* 137 */     if (key.isReadable()) {
/* 138 */       SocketChannel socketChannel = (SocketChannel)key.channel();
/*     */       try {
/* 140 */         this.handler.handle(socketChannel);
/* 141 */       } catch (Exception e) {
/* 142 */         IoUtil.close(socketChannel);
/* 143 */         StaticLog.error(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 150 */     IoUtil.close(this.selector);
/* 151 */     IoUtil.close(this.serverSocketChannel);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\nio\NioServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */