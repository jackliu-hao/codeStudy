package io.undertow.protocols.ajp;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.client.ClientConnection;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.AbstractFramedChannel;
import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
import io.undertow.server.protocol.framed.FrameHeaderData;
import io.undertow.util.Attachable;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;

public class AjpClientChannel extends AbstractFramedChannel<AjpClientChannel, AbstractAjpClientStreamSourceChannel, AbstractAjpClientStreamSinkChannel> {
   private final AjpResponseParser ajpParser = new AjpResponseParser();
   private AjpClientResponseStreamSourceChannel source;
   private AjpClientRequestClientStreamSinkChannel sink;
   private final List<ClientConnection.PingListener> pingListeners = new ArrayList();
   boolean sinkDone = true;
   boolean sourceDone = true;
   private boolean lastFrameSent;
   private boolean lastFrameReceived;

   public AjpClientChannel(StreamConnection connectedStreamChannel, ByteBufferPool bufferPool, OptionMap settings) {
      super(connectedStreamChannel, bufferPool, AjpClientFramePriority.INSTANCE, (PooledByteBuffer)null, settings);
   }

   protected AbstractAjpClientStreamSourceChannel createChannel(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) throws IOException {
      if (frameHeaderData instanceof SendHeadersResponse) {
         SendHeadersResponse h = (SendHeadersResponse)frameHeaderData;
         AjpClientResponseStreamSourceChannel sourceChannel = new AjpClientResponseStreamSourceChannel(this, h.headers, h.statusCode, h.reasonPhrase, frameData, (int)frameHeaderData.getFrameLength());
         this.source = sourceChannel;
         return sourceChannel;
      } else if (frameHeaderData instanceof RequestBodyChunk) {
         RequestBodyChunk r = (RequestBodyChunk)frameHeaderData;
         this.sink.chunkRequested(r.getLength());
         frameData.close();
         return null;
      } else if (frameHeaderData instanceof CpongResponse) {
         synchronized(this.pingListeners) {
            Iterator var4 = this.pingListeners.iterator();

            while(var4.hasNext()) {
               ClientConnection.PingListener i = (ClientConnection.PingListener)var4.next();

               try {
                  i.acknowledged();
               } catch (Throwable var8) {
                  UndertowLogger.ROOT_LOGGER.debugf("Exception notifying ping listener", var8);
               }
            }

            this.pingListeners.clear();
            return null;
         }
      } else {
         frameData.close();
         throw new RuntimeException("TODO: unknown frame");
      }
   }

   protected FrameHeaderData parseFrame(ByteBuffer data) throws IOException {
      this.ajpParser.parse(data);
      if (this.ajpParser.isComplete()) {
         try {
            AjpResponseParser parser = this.ajpParser;
            if (parser.prefix == 4) {
               SendHeadersResponse var11 = new SendHeadersResponse(parser.statusCode, parser.reasonPhrase, parser.headers);
               return var11;
            }

            if (parser.prefix == 6) {
               RequestBodyChunk var10 = new RequestBodyChunk(parser.readBodyChunkSize);
               return var10;
            }

            if (parser.prefix == 3) {
               SendBodyChunk var9 = new SendBodyChunk(parser.currentIntegerPart + 1);
               return var9;
            }

            if (parser.prefix == 5) {
               boolean persistent = parser.currentIntegerPart != 0;
               if (!persistent) {
                  this.lastFrameReceived = true;
                  this.lastFrameSent = true;
               }

               EndResponse var4 = new EndResponse();
               return var4;
            }

            if (parser.prefix == 9) {
               CpongResponse var3 = new CpongResponse();
               return var3;
            }

            UndertowLogger.ROOT_LOGGER.debug("UNKOWN FRAME");
         } finally {
            this.ajpParser.reset();
         }
      }

      return null;
   }

   public AjpClientRequestClientStreamSinkChannel sendRequest(HttpString method, String path, HttpString protocol, HeaderMap headers, Attachable attachable, ChannelListener<AjpClientRequestClientStreamSinkChannel> finishListener) {
      if (this.sinkDone && this.sourceDone) {
         this.sinkDone = false;
         this.sourceDone = false;
         AjpClientRequestClientStreamSinkChannel ajpClientRequestStreamSinkChannel = new AjpClientRequestClientStreamSinkChannel(this, finishListener, headers, path, method, protocol, attachable);
         this.sink = ajpClientRequestStreamSinkChannel;
         this.source = null;
         return ajpClientRequestStreamSinkChannel;
      } else {
         throw UndertowMessages.MESSAGES.ajpRequestAlreadyInProgress();
      }
   }

   protected boolean isLastFrameReceived() {
      return this.lastFrameReceived;
   }

   protected boolean isLastFrameSent() {
      return this.lastFrameSent;
   }

   protected void lastDataRead() {
      if (!this.lastFrameSent) {
         this.markReadsBroken(new ClosedChannelException());
         this.markWritesBroken(new ClosedChannelException());
      }

      this.lastFrameReceived = true;
      this.lastFrameSent = true;
      IoUtils.safeClose((Closeable)this);
   }

   protected void handleBrokenSourceChannel(Throwable e) {
   }

   protected void handleBrokenSinkChannel(Throwable e) {
   }

   protected void closeSubChannels() {
      IoUtils.safeClose(this.source, this.sink);
   }

   protected Collection<AbstractFramedStreamSourceChannel<AjpClientChannel, AbstractAjpClientStreamSourceChannel, AbstractAjpClientStreamSinkChannel>> getReceivers() {
      return (Collection)(this.source == null ? Collections.emptyList() : Collections.singleton(this.source));
   }

   protected OptionMap getSettings() {
      return super.getSettings();
   }

   void sinkDone() {
      this.sinkDone = true;
      if (this.sourceDone) {
         this.sink = null;
         this.source = null;
      }

   }

   void sourceDone() {
      this.sourceDone = true;
      if (this.sinkDone) {
         this.sink = null;
         this.source = null;
      } else {
         this.sink.startDiscard();
      }

   }

   public boolean isOpen() {
      return super.isOpen() && !this.lastFrameSent && !this.lastFrameReceived;
   }

   protected synchronized void recalculateHeldFrames() throws IOException {
      super.recalculateHeldFrames();
   }

   public void sendPing(final ClientConnection.PingListener pingListener, long timeout, TimeUnit timeUnit) {
      AjpClientCPingStreamSinkChannel pingChannel = new AjpClientCPingStreamSinkChannel(this);

      try {
         pingChannel.shutdownWrites();
         if (!pingChannel.flush()) {
            pingChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener((ChannelListener)null, new ChannelExceptionHandler<AbstractAjpClientStreamSinkChannel>() {
               public void handleException(AbstractAjpClientStreamSinkChannel channel, IOException exception) {
                  pingListener.failed(exception);
                  synchronized(AjpClientChannel.this.pingListeners) {
                     AjpClientChannel.this.pingListeners.remove(pingListener);
                  }
               }
            }));
            pingChannel.resumeWrites();
         }
      } catch (IOException var9) {
         pingListener.failed(var9);
         return;
      }

      synchronized(this.pingListeners) {
         this.pingListeners.add(pingListener);
      }

      this.getIoThread().executeAfter(() -> {
         synchronized(this.pingListeners) {
            if (this.pingListeners.contains(pingListener)) {
               this.pingListeners.remove(pingListener);
               pingListener.failed(UndertowMessages.MESSAGES.pingTimeout());
            }

         }
      }, timeout, timeUnit);
   }

   class CpongResponse implements FrameHeaderData {
      public long getFrameLength() {
         return 0L;
      }

      public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
         return null;
      }
   }

   class EndResponse implements FrameHeaderData {
      public long getFrameLength() {
         return 0L;
      }

      public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
         return AjpClientChannel.this.source;
      }
   }

   class SendBodyChunk implements FrameHeaderData {
      private final int length;

      SendBodyChunk(int length) {
         this.length = length;
      }

      public long getFrameLength() {
         return (long)this.length;
      }

      public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
         return AjpClientChannel.this.source;
      }
   }

   class RequestBodyChunk implements FrameHeaderData {
      private final int length;

      RequestBodyChunk(int length) {
         this.length = length;
      }

      public int getLength() {
         return this.length;
      }

      public long getFrameLength() {
         return 0L;
      }

      public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
         return null;
      }
   }

   class SendHeadersResponse implements FrameHeaderData {
      private final int statusCode;
      private final String reasonPhrase;
      private final HeaderMap headers;

      SendHeadersResponse(int statusCode, String reasonPhrase, HeaderMap headers) {
         this.statusCode = statusCode;
         this.reasonPhrase = reasonPhrase;
         this.headers = headers;
      }

      public long getFrameLength() {
         return 0L;
      }

      public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
         return null;
      }
   }
}
