package io.undertow.server.protocol.http;

import io.undertow.UndertowMessages;
import io.undertow.conduits.ReadDataStreamSourceConduit;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.AbstractServerConnection;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.ConnectionSSLSessionInfo;
import io.undertow.server.ConnectorStatisticsImpl;
import io.undertow.server.Connectors;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.server.SSLSessionInfo;
import io.undertow.server.ServerConnection;
import io.undertow.util.ConduitFactory;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.ImmediatePooledByteBuffer;
import io.undertow.util.Methods;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Iterator;
import javax.net.ssl.SSLSession;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.channels.SslChannel;
import org.xnio.conduits.StreamSinkConduit;

public final class HttpServerConnection extends AbstractServerConnection {
   private SSLSessionInfo sslSessionInfo;
   private HttpReadListener readListener;
   private PipeliningBufferingStreamSinkConduit pipelineBuffer;
   private HttpResponseConduit responseConduit;
   private ServerFixedLengthStreamSinkConduit fixedLengthStreamSinkConduit;
   private ReadDataStreamSourceConduit readDataStreamSourceConduit;
   private HttpUpgradeListener upgradeListener;
   private boolean connectHandled;

   public HttpServerConnection(StreamConnection channel, ByteBufferPool bufferPool, HttpHandler rootHandler, OptionMap undertowOptions, int bufferSize, final ConnectorStatisticsImpl connectorStatistics) {
      super(channel, bufferPool, rootHandler, undertowOptions, bufferSize);
      if (channel instanceof SslChannel) {
         this.sslSessionInfo = new ConnectionSSLSessionInfo((SslChannel)channel, this);
      }

      this.responseConduit = new HttpResponseConduit(channel.getSinkChannel().getConduit(), bufferPool, this);
      this.fixedLengthStreamSinkConduit = new ServerFixedLengthStreamSinkConduit(this.responseConduit, false, false);
      this.readDataStreamSourceConduit = new ReadDataStreamSourceConduit(channel.getSourceChannel().getConduit(), this);
      this.addCloseListener(new ServerConnection.CloseListener() {
         public void closed(ServerConnection connection) {
            if (connectorStatistics != null) {
               connectorStatistics.decrementConnectionCount();
            }

            HttpServerConnection.this.responseConduit.freeBuffers();
         }
      });
   }

   public HttpServerExchange sendOutOfBandResponse(HttpServerExchange exchange) {
      if (exchange != null && HttpContinue.requiresContinueResponse(exchange)) {
         final AbstractServerConnection.ConduitState state = this.resetChannel();
         HttpServerExchange newExchange = new HttpServerExchange(this);
         Iterator var4 = exchange.getRequestHeaders().getHeaderNames().iterator();

         while(var4.hasNext()) {
            HttpString header = (HttpString)var4.next();
            newExchange.getRequestHeaders().putAll(header, exchange.getRequestHeaders().get(header));
         }

         newExchange.setProtocol(exchange.getProtocol());
         newExchange.setRequestMethod(exchange.getRequestMethod());
         exchange.setRequestURI(exchange.getRequestURI(), exchange.isHostIncludedInRequestURI());
         exchange.setRequestPath(exchange.getRequestPath());
         exchange.setRelativePath(exchange.getRelativePath());
         newExchange.getRequestHeaders().put(Headers.CONNECTION, Headers.KEEP_ALIVE.toString());
         newExchange.getRequestHeaders().put(Headers.CONTENT_LENGTH, 0L);
         newExchange.setPersistent(true);
         Connectors.terminateRequest(newExchange);
         newExchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>() {
            public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
               final HttpResponseConduit httpResponseConduit = new HttpResponseConduit(HttpServerConnection.this.getSinkChannel().getConduit(), HttpServerConnection.this.getByteBufferPool(), HttpServerConnection.this, exchange);
               exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
                  public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
                     httpResponseConduit.freeContinueResponse();
                     nextListener.proceed();
                  }
               });
               ServerFixedLengthStreamSinkConduit fixed = new ServerFixedLengthStreamSinkConduit(httpResponseConduit, false, false);
               fixed.reset(0L, exchange);
               return fixed;
            }
         });
         this.channel.getSourceChannel().setConduit(source(state));
         newExchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
            public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
               HttpServerConnection.this.restoreChannel(state);
            }
         });
         return newExchange;
      } else {
         throw UndertowMessages.MESSAGES.outOfBandResponseOnlyAllowedFor100Continue();
      }
   }

   public boolean isContinueResponseSupported() {
      return true;
   }

   public void terminateRequestChannel(HttpServerExchange exchange) {
      if (!exchange.isPersistent()) {
         IoUtils.safeClose((Closeable)this.getChannel().getSourceChannel());
      }

   }

   public void ungetRequestBytes(PooledByteBuffer unget) {
      if (this.getExtraBytes() == null) {
         this.setExtraBytes(unget);
      } else {
         PooledByteBuffer eb = this.getExtraBytes();
         ByteBuffer buf = eb.getBuffer();
         ByteBuffer ugBuffer = unget.getBuffer();
         if (ugBuffer.limit() - ugBuffer.remaining() > buf.remaining()) {
            ugBuffer.compact();
            ugBuffer.put(buf);
            ugBuffer.flip();
            eb.close();
            this.setExtraBytes(unget);
         } else {
            byte[] data = new byte[ugBuffer.remaining() + buf.remaining()];
            int first = ugBuffer.remaining();
            ugBuffer.get(data, 0, ugBuffer.remaining());
            buf.get(data, first, buf.remaining());
            eb.close();
            unget.close();
            ByteBuffer newBuffer = ByteBuffer.wrap(data);
            this.setExtraBytes(new ImmediatePooledByteBuffer(newBuffer));
         }
      }

   }

   public SSLSessionInfo getSslSessionInfo() {
      return this.sslSessionInfo;
   }

   public void setSslSessionInfo(SSLSessionInfo sessionInfo) {
      this.sslSessionInfo = sessionInfo;
   }

   public SSLSession getSslSession() {
      return this.channel instanceof SslChannel ? ((SslChannel)this.channel).getSslSession() : null;
   }

   protected StreamConnection upgradeChannel() {
      this.clearChannel();
      if (this.extraBytes != null) {
         this.channel.getSourceChannel().setConduit(new ReadDataStreamSourceConduit(this.channel.getSourceChannel().getConduit(), this));
      }

      return this.channel;
   }

   protected StreamSinkConduit getSinkConduit(HttpServerExchange exchange, StreamSinkConduit conduit) {
      if (exchange.getRequestMethod().equals(Methods.CONNECT) && !this.connectHandled) {
         exchange.setPersistent(false);
         exchange.getResponseHeaders().put(Headers.CONNECTION, "close");
      }

      return HttpTransferEncoding.createSinkConduit(exchange);
   }

   protected boolean isUpgradeSupported() {
      return true;
   }

   protected boolean isConnectSupported() {
      return true;
   }

   void setReadListener(HttpReadListener readListener) {
      this.readListener = readListener;
   }

   protected void exchangeComplete(HttpServerExchange exchange) {
      if (this.fixedLengthStreamSinkConduit != null) {
         this.fixedLengthStreamSinkConduit.clearExchange();
      }

      if (this.pipelineBuffer == null) {
         this.readListener.exchangeComplete(exchange);
      } else {
         this.pipelineBuffer.exchangeComplete(exchange);
      }

   }

   HttpReadListener getReadListener() {
      return this.readListener;
   }

   ReadDataStreamSourceConduit getReadDataStreamSourceConduit() {
      return this.readDataStreamSourceConduit;
   }

   public PipeliningBufferingStreamSinkConduit getPipelineBuffer() {
      return this.pipelineBuffer;
   }

   public HttpResponseConduit getResponseConduit() {
      return this.responseConduit;
   }

   ServerFixedLengthStreamSinkConduit getFixedLengthStreamSinkConduit() {
      return this.fixedLengthStreamSinkConduit;
   }

   protected HttpUpgradeListener getUpgradeListener() {
      return this.upgradeListener;
   }

   protected void setUpgradeListener(HttpUpgradeListener upgradeListener) {
      this.upgradeListener = upgradeListener;
   }

   protected void setConnectListener(HttpUpgradeListener connectListener) {
      this.upgradeListener = connectListener;
      this.connectHandled = true;
   }

   void setCurrentExchange(HttpServerExchange exchange) {
      this.current = exchange;
   }

   public void setPipelineBuffer(PipeliningBufferingStreamSinkConduit pipelineBuffer) {
      this.pipelineBuffer = pipelineBuffer;
      this.responseConduit = new HttpResponseConduit(pipelineBuffer, this.bufferPool, this);
      this.fixedLengthStreamSinkConduit = new ServerFixedLengthStreamSinkConduit(this.responseConduit, false, false);
   }

   public String getTransportProtocol() {
      return "http/1.1";
   }

   public boolean isRequestTrailerFieldsSupported() {
      if (this.current == null) {
         return false;
      } else {
         String te = this.current.getRequestHeaders().getFirst(Headers.TRANSFER_ENCODING);
         return te == null ? false : te.equalsIgnoreCase(Headers.CHUNKED.toString());
      }
   }

   boolean isConnectHandled() {
      return this.connectHandled;
   }
}
