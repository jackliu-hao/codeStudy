package io.undertow.server.protocol.ajp;

import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.AbstractServerConnection;
import io.undertow.server.BasicSSLSessionInfo;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.server.SSLSessionInfo;
import io.undertow.util.DateUtils;
import java.io.Closeable;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.WriteReadyHandler;

public final class AjpServerConnection extends AbstractServerConnection {
   private SSLSessionInfo sslSessionInfo;
   private WriteReadyHandler.ChannelListenerHandler<ConduitStreamSinkChannel> writeReadyHandler;
   private AjpReadListener ajpReadListener;

   public AjpServerConnection(StreamConnection channel, ByteBufferPool bufferPool, HttpHandler rootHandler, OptionMap undertowOptions, int bufferSize) {
      super(channel, bufferPool, rootHandler, undertowOptions, bufferSize);
      this.writeReadyHandler = new WriteReadyHandler.ChannelListenerHandler(channel.getSinkChannel());
   }

   public HttpServerExchange sendOutOfBandResponse(HttpServerExchange exchange) {
      throw UndertowMessages.MESSAGES.outOfBandResponseNotSupported();
   }

   public boolean isContinueResponseSupported() {
      return false;
   }

   public void terminateRequestChannel(HttpServerExchange exchange) {
      if (!exchange.isPersistent()) {
         IoUtils.safeClose((Closeable)this.getChannel().getSourceChannel());
      }

   }

   public void restoreChannel(AbstractServerConnection.ConduitState state) {
      super.restoreChannel(state);
      this.channel.getSinkChannel().getConduit().setWriteReadyHandler(this.writeReadyHandler);
   }

   public AbstractServerConnection.ConduitState resetChannel() {
      AbstractServerConnection.ConduitState state = super.resetChannel();
      this.channel.getSinkChannel().getConduit().setWriteReadyHandler(this.writeReadyHandler);
      return state;
   }

   public void clearChannel() {
      super.clearChannel();
      this.channel.getSinkChannel().getConduit().setWriteReadyHandler(this.writeReadyHandler);
   }

   public SSLSessionInfo getSslSessionInfo() {
      return this.sslSessionInfo;
   }

   public void setSslSessionInfo(SSLSessionInfo sessionInfo) {
      this.sslSessionInfo = sessionInfo;
   }

   void setSSLSessionInfo(BasicSSLSessionInfo sslSessionInfo) {
      this.sslSessionInfo = sslSessionInfo;
   }

   protected StreamConnection upgradeChannel() {
      throw UndertowMessages.MESSAGES.upgradeNotSupported();
   }

   protected StreamSinkConduit getSinkConduit(HttpServerExchange exchange, StreamSinkConduit conduit) {
      DateUtils.addDateHeaderIfRequired(exchange);
      return conduit;
   }

   protected boolean isUpgradeSupported() {
      return false;
   }

   protected boolean isConnectSupported() {
      return false;
   }

   void setAjpReadListener(AjpReadListener ajpReadListener) {
      this.ajpReadListener = ajpReadListener;
   }

   protected void exchangeComplete(HttpServerExchange exchange) {
      this.ajpReadListener.exchangeComplete(exchange);
   }

   protected void setConnectListener(HttpUpgradeListener connectListener) {
      throw UndertowMessages.MESSAGES.connectNotSupported();
   }

   void setCurrentExchange(HttpServerExchange exchange) {
      this.current = exchange;
   }

   public String getTransportProtocol() {
      return "ajp";
   }

   public boolean isRequestTrailerFieldsSupported() {
      return false;
   }
}
