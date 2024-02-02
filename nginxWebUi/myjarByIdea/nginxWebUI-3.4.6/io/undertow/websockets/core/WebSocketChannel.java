package io.undertow.websockets.core;

import io.undertow.conduits.IdleTimeoutConduit;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.AbstractFramedChannel;
import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
import io.undertow.server.protocol.framed.FrameHeaderData;
import io.undertow.websockets.extensions.ExtensionFunction;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.channels.StreamSinkChannel;

public abstract class WebSocketChannel extends AbstractFramedChannel<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel> {
   private final boolean client;
   private final WebSocketVersion version;
   private final String wsUrl;
   private volatile boolean closeFrameReceived;
   private volatile boolean closeFrameSent;
   private volatile boolean closeInitiatedByRemotePeer;
   private volatile int closeCode = -1;
   private volatile String closeReason;
   private final String subProtocol;
   protected final boolean extensionsSupported;
   protected final ExtensionFunction extensionFunction;
   protected final boolean hasReservedOpCode;
   private volatile PartialFrame partialFrame;
   private final Map<String, Object> attributes = Collections.synchronizedMap(new HashMap());
   protected StreamSourceFrameChannel fragmentedChannel;
   private final Set<WebSocketChannel> peerConnections;

   protected WebSocketChannel(StreamConnection connectedStreamChannel, ByteBufferPool bufferPool, WebSocketVersion version, String wsUrl, String subProtocol, boolean client, boolean extensionsSupported, final ExtensionFunction extensionFunction, Set<WebSocketChannel> peerConnections, OptionMap options) {
      super(connectedStreamChannel, bufferPool, new WebSocketFramePriority(), (PooledByteBuffer)null, options);
      this.client = client;
      this.version = version;
      this.wsUrl = wsUrl;
      this.extensionsSupported = extensionsSupported;
      this.extensionFunction = extensionFunction;
      this.hasReservedOpCode = extensionFunction.hasExtensionOpCode();
      this.subProtocol = subProtocol;
      this.peerConnections = peerConnections;
      this.addCloseTask(new ChannelListener<WebSocketChannel>() {
         public void handleEvent(WebSocketChannel channel) {
            extensionFunction.dispose();
            WebSocketChannel.this.peerConnections.remove(WebSocketChannel.this);
         }
      });
   }

   protected Collection<AbstractFramedStreamSourceChannel<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel>> getReceivers() {
      return (Collection)(this.fragmentedChannel == null ? Collections.emptyList() : Collections.singleton(this.fragmentedChannel));
   }

   protected IdleTimeoutConduit createIdleTimeoutChannel(StreamConnection connectedStreamChannel) {
      return new IdleTimeoutConduit(connectedStreamChannel) {
         protected void doClose() {
            WebSockets.sendClose(1001, (String)null, WebSocketChannel.this, (WebSocketCallback)null);
         }
      };
   }

   protected boolean isLastFrameSent() {
      return this.closeFrameSent;
   }

   protected boolean isLastFrameReceived() {
      return this.closeFrameReceived;
   }

   protected void markReadsBroken(Throwable cause) {
      super.markReadsBroken(cause);
   }

   protected void lastDataRead() {
      if (!this.closeFrameReceived && !this.closeFrameSent) {
         this.closeFrameReceived = true;

         try {
            this.sendClose();
         } catch (IOException var2) {
            IoUtils.safeClose((Closeable)this);
         }
      }

   }

   protected boolean isReadsBroken() {
      return super.isReadsBroken();
   }

   protected FrameHeaderData parseFrame(ByteBuffer data) throws IOException {
      if (this.partialFrame == null) {
         this.partialFrame = this.receiveFrame();
      }

      try {
         this.partialFrame.handle(data);
      } catch (WebSocketException var3) {
         WebSockets.sendClose((ByteBuffer)(new CloseMessage(1002, var3.getMessage())).toByteBuffer(), this, (WebSocketCallback)null);
         this.markReadsBroken(var3);
         if (WebSocketLogger.REQUEST_LOGGER.isDebugEnabled()) {
            WebSocketLogger.REQUEST_LOGGER.debugf(var3, "receive failed due to Exception", new Object[0]);
         }

         throw new IOException(var3);
      }

      if (this.partialFrame.isDone()) {
         PartialFrame p = this.partialFrame;
         this.partialFrame = null;
         return p;
      } else {
         return null;
      }
   }

   protected abstract PartialFrame receiveFrame();

   protected StreamSourceFrameChannel createChannel(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) {
      PartialFrame partialFrame = (PartialFrame)frameHeaderData;
      StreamSourceFrameChannel channel = partialFrame.getChannel(frameData);
      if (channel.getType() == WebSocketFrameType.CLOSE) {
         if (!this.closeFrameSent) {
            this.closeInitiatedByRemotePeer = true;
         }

         this.closeFrameReceived = true;
      }

      return channel;
   }

   public final boolean setAttribute(String key, Object value) {
      if (value == null) {
         return this.attributes.remove(key) != null;
      } else {
         return this.attributes.put(key, value) == null;
      }
   }

   public final Object getAttribute(String key) {
      return this.attributes.get(key);
   }

   public boolean areExtensionsSupported() {
      return this.extensionsSupported;
   }

   protected void handleBrokenSourceChannel(Throwable e) {
      if (e instanceof UnsupportedEncodingException) {
         this.getFramePriority().immediateCloseFrame();
         WebSockets.sendClose((ByteBuffer)(new CloseMessage(1007, e.getMessage())).toByteBuffer(), this, (WebSocketCallback)null);
      } else if (e instanceof WebSocketInvalidCloseCodeException) {
         WebSockets.sendClose((ByteBuffer)(new CloseMessage(1002, e.getMessage())).toByteBuffer(), this, (WebSocketCallback)null);
      } else if (e instanceof WebSocketFrameCorruptedException) {
         this.getFramePriority().immediateCloseFrame();
         WebSockets.sendClose((ByteBuffer)(new CloseMessage(1002, e.getMessage())).toByteBuffer(), this, (WebSocketCallback)null);
      }

   }

   protected void handleBrokenSinkChannel(Throwable e) {
   }

   /** @deprecated */
   @Deprecated
   public Set<String> getSubProtocols() {
      return Collections.singleton(this.subProtocol);
   }

   public String getSubProtocol() {
      return this.subProtocol;
   }

   public boolean isCloseFrameReceived() {
      return this.closeFrameReceived;
   }

   public boolean isCloseFrameSent() {
      return this.closeFrameSent;
   }

   public String getRequestScheme() {
      return this.getUrl().startsWith("wss:") ? "wss" : "ws";
   }

   public boolean isSecure() {
      return "wss".equals(this.getRequestScheme());
   }

   public String getUrl() {
      return this.wsUrl;
   }

   public WebSocketVersion getVersion() {
      return this.version;
   }

   public InetSocketAddress getSourceAddress() {
      return (InetSocketAddress)this.getPeerAddress(InetSocketAddress.class);
   }

   public InetSocketAddress getDestinationAddress() {
      return (InetSocketAddress)this.getLocalAddress(InetSocketAddress.class);
   }

   public boolean isClient() {
      return this.client;
   }

   public final StreamSinkFrameChannel send(WebSocketFrameType type) throws IOException {
      if (this.closeFrameSent || this.closeFrameReceived && type != WebSocketFrameType.CLOSE) {
         throw WebSocketMessages.MESSAGES.channelClosed();
      } else if (this.isWritesBroken()) {
         throw WebSocketMessages.MESSAGES.streamIsBroken();
      } else {
         StreamSinkFrameChannel ch = this.createStreamSinkChannel(type);
         this.getFramePriority().addToOrderQueue(ch);
         if (type == WebSocketFrameType.CLOSE) {
            this.closeFrameSent = true;
         }

         return ch;
      }
   }

   public void sendClose() throws IOException {
      this.closeReason = "";
      this.closeCode = 1000;
      StreamSinkFrameChannel closeChannel = this.send(WebSocketFrameType.CLOSE);
      closeChannel.shutdownWrites();
      if (!closeChannel.flush()) {
         closeChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener((ChannelListener)null, new ChannelExceptionHandler<StreamSinkChannel>() {
            public void handleException(StreamSinkChannel channel, IOException exception) {
               IoUtils.safeClose((Closeable)WebSocketChannel.this);
            }
         }));
         closeChannel.resumeWrites();
      }

   }

   protected abstract StreamSinkFrameChannel createStreamSinkChannel(WebSocketFrameType var1);

   protected WebSocketFramePriority getFramePriority() {
      return (WebSocketFramePriority)super.getFramePriority();
   }

   public Set<WebSocketChannel> getPeerConnections() {
      return Collections.unmodifiableSet(this.peerConnections);
   }

   public boolean isCloseInitiatedByRemotePeer() {
      return this.closeInitiatedByRemotePeer;
   }

   public String getCloseReason() {
      return this.closeReason;
   }

   public void setCloseReason(String closeReason) {
      this.closeReason = closeReason;
   }

   public int getCloseCode() {
      return this.closeCode;
   }

   public void setCloseCode(int closeCode) {
      this.closeCode = closeCode;
   }

   public ExtensionFunction getExtensionFunction() {
      return this.extensionFunction;
   }

   public interface PartialFrame extends FrameHeaderData {
      StreamSourceFrameChannel getChannel(PooledByteBuffer var1);

      void handle(ByteBuffer var1) throws WebSocketException;

      boolean isDone();
   }
}
