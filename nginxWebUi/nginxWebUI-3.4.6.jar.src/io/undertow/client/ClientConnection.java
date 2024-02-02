/*     */ package io.undertow.client;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
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
/*     */ public interface ClientConnection
/*     */   extends Channel
/*     */ {
/*     */   void sendRequest(ClientRequest paramClientRequest, ClientCallback<ClientExchange> paramClientCallback);
/*     */   
/*     */   StreamConnection performUpgrade() throws IOException;
/*     */   
/*     */   ByteBufferPool getBufferPool();
/*     */   
/*     */   SocketAddress getPeerAddress();
/*     */   
/*     */   <A extends SocketAddress> A getPeerAddress(Class<A> paramClass);
/*     */   
/*     */   ChannelListener.Setter<? extends ClientConnection> getCloseSetter();
/*     */   
/*     */   SocketAddress getLocalAddress();
/*     */   
/*     */   <A extends SocketAddress> A getLocalAddress(Class<A> paramClass);
/*     */   
/*     */   XnioWorker getWorker();
/*     */   
/*     */   XnioIoThread getIoThread();
/*     */   
/*     */   boolean isOpen();
/*     */   
/*     */   boolean supportsOption(Option<?> paramOption);
/*     */   
/*     */   <T> T getOption(Option<T> paramOption) throws IOException;
/*     */   
/*     */   <T> T setOption(Option<T> paramOption, T paramT) throws IllegalArgumentException, IOException;
/*     */   
/*     */   boolean isUpgraded();
/*     */   
/*     */   boolean isPushSupported();
/*     */   
/*     */   boolean isMultiplexingSupported();
/*     */   
/*     */   ClientStatistics getStatistics();
/*     */   
/*     */   boolean isUpgradeSupported();
/*     */   
/*     */   void addCloseListener(ChannelListener<ClientConnection> paramChannelListener);
/*     */   
/*     */   default boolean isPingSupported() {
/* 131 */     return false;
/*     */   }
/*     */   
/*     */   default void sendPing(PingListener listener, long timeout, TimeUnit timeUnit) {
/* 135 */     listener.failed(UndertowMessages.MESSAGES.pingNotSupported());
/*     */   }
/*     */   
/*     */   public static interface PingListener {
/*     */     void acknowledged();
/*     */     
/*     */     void failed(IOException param1IOException);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ClientConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */