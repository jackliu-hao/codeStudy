package io.undertow.protocols.http2;

import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.SendFrameHeader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.IoUtils;

public abstract class Http2StreamSinkChannel extends AbstractHttp2StreamSinkChannel {
   private final int streamId;
   private volatile boolean reset = false;
   private int flowControlWindow;
   private int initialWindowSize;
   private SendFrameHeader header;
   private static final Object flowControlLock = new Object();

   Http2StreamSinkChannel(Http2Channel channel, int streamId) {
      super(channel);
      this.streamId = streamId;
      this.flowControlWindow = channel.getInitialSendWindowSize();
      this.initialWindowSize = this.flowControlWindow;
   }

   public int getStreamId() {
      return this.streamId;
   }

   SendFrameHeader generateSendFrameHeader() {
      this.header = this.createFrameHeaderImpl();
      return this.header;
   }

   protected abstract SendFrameHeader createFrameHeaderImpl();

   void clearHeader() {
      this.header = null;
   }

   protected void channelForciblyClosed() throws IOException {
      ((Http2Channel)this.getChannel()).removeStreamSink(this.getStreamId());
      if (!this.reset) {
         this.reset = true;
         if (this.streamId % 2 == (((Http2Channel)this.getChannel()).isClient() ? 1 : 0)) {
            if (this.isFirstDataWritten() && !((Http2Channel)this.getChannel()).isThisGoneAway()) {
               ((Http2Channel)this.getChannel()).sendRstStream(this.streamId, 8);
            }
         } else if (!((Http2Channel)this.getChannel()).isThisGoneAway()) {
            ((Http2Channel)this.getChannel()).sendRstStream(this.streamId, 5);
         }

         this.markBroken();
      }
   }

   protected final SendFrameHeader createFrameHeader() {
      SendFrameHeader header = this.header;
      this.header = null;
      return header;
   }

   protected void handleFlushComplete(boolean channelClosed) {
      if (channelClosed) {
         ((Http2Channel)this.getChannel()).removeStreamSink(this.getStreamId());
      }

      if (this.reset) {
         IoUtils.safeClose((Closeable)this);
      }

   }

   protected int grabFlowControlBytes(int toSend) {
      synchronized(flowControlLock) {
         if (toSend == 0) {
            return 0;
         } else {
            int newWindowSize = ((Http2Channel)this.getChannel()).getInitialSendWindowSize();
            int settingsDelta = newWindowSize - this.initialWindowSize;
            this.initialWindowSize = newWindowSize;
            this.flowControlWindow += settingsDelta;
            int min = Math.min(toSend, this.flowControlWindow);
            int actualBytes = ((Http2Channel)this.getChannel()).grabFlowControlBytes(min);
            this.flowControlWindow -= actualBytes;
            return actualBytes;
         }
      }
   }

   void updateFlowControlWindow(int delta) throws IOException {
      boolean exhausted;
      synchronized(flowControlLock) {
         exhausted = this.flowControlWindow <= 0;
         long ld = (long)delta;
         ld += (long)this.flowControlWindow;
         if (ld > 2147483647L) {
            ((Http2Channel)this.getChannel()).sendRstStream(this.streamId, 3);
            this.markBroken();
            return;
         }

         this.flowControlWindow += delta;
      }

      if (exhausted) {
         ((Http2Channel)this.getChannel()).notifyFlowControlAllowed();
         if (this.isWriteResumed()) {
            this.resumeWritesInternal(true);
         }
      }

   }

   protected PooledByteBuffer[] allocateAll(PooledByteBuffer[] allHeaderBuffers, PooledByteBuffer currentBuffer) {
      PooledByteBuffer[] ret;
      ByteBuffer newBuffer;
      if (allHeaderBuffers == null) {
         ret = new PooledByteBuffer[]{currentBuffer, ((Http2Channel)this.getChannel()).getBufferPool().allocate()};
         newBuffer = ret[1].getBuffer();
         if (newBuffer.remaining() > ((Http2Channel)this.getChannel()).getSendMaxFrameSize()) {
            newBuffer.limit(newBuffer.position() + ((Http2Channel)this.getChannel()).getSendMaxFrameSize());
         }
      } else {
         ret = new PooledByteBuffer[allHeaderBuffers.length + 1];
         System.arraycopy(allHeaderBuffers, 0, ret, 0, allHeaderBuffers.length);
         ret[ret.length - 1] = ((Http2Channel)this.getChannel()).getBufferPool().allocate();
         newBuffer = ret[ret.length - 1].getBuffer();
         if (newBuffer.remaining() > ((Http2Channel)this.getChannel()).getSendMaxFrameSize()) {
            newBuffer.limit(newBuffer.position() + ((Http2Channel)this.getChannel()).getSendMaxFrameSize());
         }
      }

      return ret;
   }

   public void awaitWritable() throws IOException {
      int flowControlWindow;
      synchronized(flowControlLock) {
         flowControlWindow = this.flowControlWindow;
      }

      super.awaitWritable();
      synchronized(flowControlLock) {
         if (this.isReadyForFlush() && flowControlWindow <= 0 && flowControlWindow == this.flowControlWindow) {
            throw UndertowMessages.MESSAGES.noWindowUpdate(this.getAwaitWritableTimeout());
         }
      }
   }

   void rstStream() {
      if (!this.reset) {
         this.reset = true;
         if (!this.isReadyForFlush()) {
            IoUtils.safeClose((Closeable)this);
         }

         ((Http2Channel)this.getChannel()).removeStreamSink(this.getStreamId());
      }
   }
}
