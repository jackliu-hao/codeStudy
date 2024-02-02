/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.util.AbstractAttachable;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
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
/*     */ public abstract class ServerConnection
/*     */   extends AbstractAttachable
/*     */   implements ConnectedChannel
/*     */ {
/*     */   @Deprecated
/*     */   public abstract Pool<ByteBuffer> getBufferPool();
/*     */   
/*     */   public abstract ByteBufferPool getByteBufferPool();
/*     */   
/*     */   public abstract XnioWorker getWorker();
/*     */   
/*     */   public abstract XnioIoThread getIoThread();
/*     */   
/*     */   public abstract HttpServerExchange sendOutOfBandResponse(HttpServerExchange paramHttpServerExchange);
/*     */   
/*     */   public abstract boolean isContinueResponseSupported();
/*     */   
/*     */   public abstract void terminateRequestChannel(HttpServerExchange paramHttpServerExchange);
/*     */   
/*     */   public abstract boolean isOpen();
/*     */   
/*     */   public abstract boolean supportsOption(Option<?> paramOption);
/*     */   
/*     */   public abstract <T> T getOption(Option<T> paramOption) throws IOException;
/*     */   
/*     */   public abstract <T> T setOption(Option<T> paramOption, T paramT) throws IllegalArgumentException, IOException;
/*     */   
/*     */   public abstract void close() throws IOException;
/*     */   
/*     */   public SSLSession getSslSession() {
/* 129 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract SocketAddress getPeerAddress();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract <A extends SocketAddress> A getPeerAddress(Class<A> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract SocketAddress getLocalAddress();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract <A extends SocketAddress> A getLocalAddress(Class<A> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract OptionMap getUndertowOptions();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getBufferSize();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract SSLSessionInfo getSslSessionInfo();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void setSslSessionInfo(SSLSessionInfo paramSSLSessionInfo);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void addCloseListener(CloseListener paramCloseListener);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract StreamConnection upgradeChannel();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract ConduitStreamSinkChannel getSinkChannel();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract ConduitStreamSourceChannel getSourceChannel();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract StreamSinkConduit getSinkConduit(HttpServerExchange paramHttpServerExchange, StreamSinkConduit paramStreamSinkConduit);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isUpgradeSupported();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isConnectSupported();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void exchangeComplete(HttpServerExchange paramHttpServerExchange);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void setUpgradeListener(HttpUpgradeListener paramHttpUpgradeListener);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void setConnectListener(HttpUpgradeListener paramHttpUpgradeListener);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void maxEntitySizeUpdated(HttpServerExchange paramHttpServerExchange);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getTransportProtocol();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean pushResource(String path, HttpString method, HeaderMap requestHeaders) {
/* 259 */     return false;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean pushResource(String path, HttpString method, HeaderMap requestHeaders, HttpHandler handler) {
/* 277 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isPushSupported() {
/* 281 */     return false;
/*     */   }
/*     */   
/*     */   public abstract boolean isRequestTrailerFieldsSupported();
/*     */   
/*     */   public static interface CloseListener {
/*     */     void closed(ServerConnection param1ServerConnection);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\ServerConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */