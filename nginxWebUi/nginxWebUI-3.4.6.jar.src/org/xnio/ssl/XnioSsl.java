/*     */ package org.xnio.ssl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Xnio;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.channels.BoundChannel;
/*     */ import org.xnio.channels.ConnectedSslStreamChannel;
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
/*     */ public abstract class XnioSsl
/*     */ {
/*  46 */   private static final InetSocketAddress ANY_INET_ADDRESS = new InetSocketAddress(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Xnio xnio;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SSLContext sslContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected XnioSsl(Xnio xnio, SSLContext sslContext, OptionMap optionMap) {
/*  67 */     this.xnio = xnio;
/*  68 */     this.sslContext = sslContext;
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
/*     */   @Deprecated
/*     */   public IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker worker, InetSocketAddress destination, ChannelListener<? super ConnectedSslStreamChannel> openListener, OptionMap optionMap) {
/*  83 */     return connectSsl(worker, ANY_INET_ADDRESS, destination, openListener, null, optionMap);
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
/*     */   @Deprecated
/*     */   public IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker worker, InetSocketAddress destination, ChannelListener<? super ConnectedSslStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  98 */     return connectSsl(worker, ANY_INET_ADDRESS, destination, openListener, bindListener, optionMap);
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
/*     */   @Deprecated
/*     */   public IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super ConnectedSslStreamChannel> openListener, OptionMap optionMap) {
/* 113 */     return connectSsl(worker, bindAddress, destination, openListener, null, optionMap);
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
/*     */   
/*     */   @Deprecated
/*     */   public abstract IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker paramXnioWorker, InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2, ChannelListener<? super ConnectedSslStreamChannel> paramChannelListener, ChannelListener<? super BoundChannel> paramChannelListener1, OptionMap paramOptionMap);
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
/*     */   public IoFuture<SslConnection> openSslConnection(XnioWorker worker, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, OptionMap optionMap) {
/* 141 */     return openSslConnection(worker, ANY_INET_ADDRESS, destination, openListener, (ChannelListener<? super BoundChannel>)null, optionMap);
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
/*     */   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, OptionMap optionMap) {
/* 155 */     return openSslConnection(ioThread, ANY_INET_ADDRESS, destination, openListener, (ChannelListener<? super BoundChannel>)null, optionMap);
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
/*     */   public IoFuture<SslConnection> openSslConnection(XnioWorker worker, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 169 */     return openSslConnection(worker, ANY_INET_ADDRESS, destination, openListener, bindListener, optionMap);
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
/*     */   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 183 */     return openSslConnection(ioThread, ANY_INET_ADDRESS, destination, openListener, bindListener, optionMap);
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
/*     */   public IoFuture<SslConnection> openSslConnection(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, OptionMap optionMap) {
/* 197 */     return openSslConnection(worker, bindAddress, destination, openListener, (ChannelListener<? super BoundChannel>)null, optionMap);
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
/*     */   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, OptionMap optionMap) {
/* 211 */     return openSslConnection(ioThread, bindAddress, destination, openListener, (ChannelListener<? super BoundChannel>)null, optionMap);
/*     */   }
/*     */   
/*     */   public abstract IoFuture<SslConnection> openSslConnection(XnioWorker paramXnioWorker, InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2, ChannelListener<? super SslConnection> paramChannelListener, ChannelListener<? super BoundChannel> paramChannelListener1, OptionMap paramOptionMap);
/*     */   
/*     */   public abstract IoFuture<SslConnection> openSslConnection(XnioIoThread paramXnioIoThread, InetSocketAddress paramInetSocketAddress1, InetSocketAddress paramInetSocketAddress2, ChannelListener<? super SslConnection> paramChannelListener, ChannelListener<? super BoundChannel> paramChannelListener1, OptionMap paramOptionMap);
/*     */   
/*     */   @Deprecated
/*     */   public abstract AcceptingChannel<ConnectedSslStreamChannel> createSslTcpServer(XnioWorker paramXnioWorker, InetSocketAddress paramInetSocketAddress, ChannelListener<? super AcceptingChannel<ConnectedSslStreamChannel>> paramChannelListener, OptionMap paramOptionMap) throws IOException;
/*     */   
/*     */   public abstract AcceptingChannel<SslConnection> createSslConnectionServer(XnioWorker paramXnioWorker, InetSocketAddress paramInetSocketAddress, ChannelListener<? super AcceptingChannel<SslConnection>> paramChannelListener, OptionMap paramOptionMap) throws IOException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\XnioSsl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */