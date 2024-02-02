package io.undertow.protocols.http2;

import io.undertow.UndertowLogger;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.FrameHeaderData;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;

public class Http2StreamSourceChannel extends AbstractHttp2StreamSourceChannel implements Http2Stream {
   private boolean headersEndStream = false;
   private boolean rst = false;
   private final HeaderMap headers;
   private final int streamId;
   private Http2HeadersStreamSinkChannel response;
   private int flowControlWindow;
   private ChannelListener<Http2StreamSourceChannel> completionListener;
   private int remainingPadding;
   private boolean ignoreForceClose = false;
   private long contentLengthRemaining;
   private TrailersHandler trailersHandler;

   Http2StreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining, HeaderMap headers, int streamId) {
      super(framedChannel, data, frameDataRemaining);
      this.headers = headers;
      this.streamId = streamId;
      this.flowControlWindow = framedChannel.getInitialReceiveWindowSize();
      String contentLengthString = headers.getFirst(Headers.CONTENT_LENGTH);
      if (contentLengthString != null) {
         this.contentLengthRemaining = Long.parseLong(contentLengthString);
      } else {
         this.contentLengthRemaining = -1L;
      }

   }

   protected void handleHeaderData(FrameHeaderData headerData) {
      Http2FrameHeaderParser data = (Http2FrameHeaderParser)headerData;
      Http2PushBackParser parser = data.getParser();
      if (parser instanceof Http2DataFrameParser) {
         this.remainingPadding = ((Http2DataFrameParser)parser).getPadding();
         if (this.remainingPadding > 0) {
            try {
               this.updateFlowControlWindow(this.remainingPadding + 1);
            } catch (IOException var5) {
               IoUtils.safeClose((Closeable)this.getFramedChannel());
               throw new RuntimeException(var5);
            }
         }
      } else if (parser instanceof Http2HeadersParser && this.trailersHandler != null) {
         this.trailersHandler.handleTrailers(((Http2HeadersParser)parser).getHeaderMap());
      }

      this.handleFinalFrame(data);
   }

   protected long updateFrameDataRemaining(PooledByteBuffer data, long frameDataRemaining) {
      long actualDataRemaining = frameDataRemaining - (long)this.remainingPadding;
      if ((long)data.getBuffer().remaining() > actualDataRemaining) {
         long paddingThisBuffer = (long)data.getBuffer().remaining() - actualDataRemaining;
         data.getBuffer().limit((int)((long)data.getBuffer().position() + actualDataRemaining));
         this.remainingPadding = (int)((long)this.remainingPadding - paddingThisBuffer);
         return frameDataRemaining - paddingThisBuffer;
      } else {
         return frameDataRemaining;
      }
   }

   void handleFinalFrame(Http2FrameHeaderParser headerData) {
      if (headerData.type == 0) {
         if (Bits.anyAreSet(headerData.flags, 1)) {
            this.lastFrame();
         }
      } else if (headerData.type == 1) {
         if (Bits.allAreSet(headerData.flags, 1)) {
            if (Bits.allAreSet(headerData.flags, 4)) {
               this.lastFrame();
            } else {
               this.headersEndStream = true;
            }
         }
      } else if (this.headersEndStream && headerData.type == 9 && Bits.anyAreSet(headerData.flags, 4)) {
         this.lastFrame();
      }

   }

   public Http2HeadersStreamSinkChannel getResponseChannel() {
      if (this.response != null) {
         return this.response;
      } else {
         this.response = new Http2HeadersStreamSinkChannel(this.getHttp2Channel(), this.streamId);
         this.getHttp2Channel().registerStreamSink(this.response);
         return this.response;
      }
   }

   public int read(ByteBuffer dst) throws IOException {
      int read = super.read(dst);
      this.updateFlowControlWindow(read);
      return read;
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      long read = super.read(dsts, offset, length);
      this.updateFlowControlWindow((int)read);
      return read;
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      long read = super.read(dsts);
      this.updateFlowControlWindow((int)read);
      return read;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel streamSinkChannel) throws IOException {
      long read = super.transferTo(count, throughBuffer, streamSinkChannel);
      this.updateFlowControlWindow((int)read + throughBuffer.remaining());
      return read;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      long read = super.transferTo(position, count, target);
      this.updateFlowControlWindow((int)read);
      return read;
   }

   private void updateFlowControlWindow(int read) throws IOException {
      if (read > 0) {
         this.flowControlWindow -= read;
         Http2Channel http2Channel = this.getHttp2Channel();
         http2Channel.updateReceiveFlowControlWindow(read);
         int initialWindowSize = http2Channel.getInitialReceiveWindowSize();
         if (this.flowControlWindow < initialWindowSize / 2) {
            int delta = initialWindowSize - this.flowControlWindow;
            this.flowControlWindow += delta;
            http2Channel.sendUpdateWindowSize(this.streamId, delta);
         }

      }
   }

   protected void complete() throws IOException {
      super.complete();
      if (this.completionListener != null) {
         ChannelListeners.invokeChannelListener(this, this.completionListener);
      }

   }

   public HeaderMap getHeaders() {
      return this.headers;
   }

   public ChannelListener<Http2StreamSourceChannel> getCompletionListener() {
      return this.completionListener;
   }

   public void setCompletionListener(ChannelListener<Http2StreamSourceChannel> completionListener) {
      this.completionListener = completionListener;
      if (this.isComplete()) {
         ChannelListeners.invokeChannelListener(this, completionListener);
      }

   }

   void rstStream(int error) {
      if (!this.rst) {
         this.rst = true;
         this.markStreamBroken();
      }
   }

   protected void channelForciblyClosed() {
      if (this.completionListener != null) {
         this.completionListener.handleEvent(this);
      }

      if (!this.ignoreForceClose) {
         this.getHttp2Channel().sendRstStream(this.streamId, 8);
      }

      this.markStreamBroken();
   }

   public void setIgnoreForceClose(boolean ignoreForceClose) {
      this.ignoreForceClose = ignoreForceClose;
   }

   public boolean isIgnoreForceClose() {
      return this.ignoreForceClose;
   }

   public int getStreamId() {
      return this.streamId;
   }

   boolean isHeadersEndStream() {
      return this.headersEndStream;
   }

   public TrailersHandler getTrailersHandler() {
      return this.trailersHandler;
   }

   public void setTrailersHandler(TrailersHandler trailersHandler) {
      this.trailersHandler = trailersHandler;
   }

   public String toString() {
      return "Http2StreamSourceChannel{headers=" + this.headers + '}';
   }

   void updateContentSize(long frameLength, boolean last) {
      if (this.contentLengthRemaining != -1L) {
         this.contentLengthRemaining -= frameLength;
         if (this.contentLengthRemaining < 0L) {
            UndertowLogger.REQUEST_IO_LOGGER.debugf("Closing stream %s on %s as data length exceeds content size", this.streamId, this.getFramedChannel());
            this.getFramedChannel().sendRstStream(this.streamId, 1);
         } else if (last && this.contentLengthRemaining != 0L) {
            UndertowLogger.REQUEST_IO_LOGGER.debugf("Closing stream %s on %s as data length was less than content size", this.streamId, this.getFramedChannel());
            this.getFramedChannel().sendRstStream(this.streamId, 1);
         }
      }

   }

   public interface TrailersHandler {
      void handleTrailers(HeaderMap var1);
   }
}
