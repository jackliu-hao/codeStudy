package io.undertow.protocols.http2;

import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.HeaderMap;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;

public class Http2DataStreamSinkChannel extends Http2StreamSinkChannel implements Http2Stream {
   private final HeaderMap headers;
   private boolean first;
   private final HpackEncoder encoder;
   private volatile ChannelListener<Http2DataStreamSinkChannel> completionListener;
   private final int frameType;
   private boolean completionListenerReady;
   private volatile boolean completionListenerFailure;
   private TrailersProducer trailersProducer;

   Http2DataStreamSinkChannel(Http2Channel channel, int streamId, int frameType) {
      this(channel, streamId, new HeaderMap(), frameType);
   }

   Http2DataStreamSinkChannel(Http2Channel channel, int streamId, HeaderMap headers, int frameType) {
      super(channel, streamId);
      this.first = true;
      this.encoder = channel.getEncoder();
      this.headers = headers;
      this.frameType = frameType;
   }

   public TrailersProducer getTrailersProducer() {
      return this.trailersProducer;
   }

   public void setTrailersProducer(TrailersProducer trailersProducer) {
      this.trailersProducer = trailersProducer;
   }

   protected SendFrameHeader createFrameHeaderImpl() {
      int dataPaddingBytes = ((Http2Channel)this.getChannel()).getPaddingBytes();
      int attempted = this.getBuffer().remaining() + dataPaddingBytes + (dataPaddingBytes > 0 ? 1 : 0);
      int fcWindow = this.grabFlowControlBytes(attempted);
      if (fcWindow == 0 && this.getBuffer().hasRemaining()) {
         return new SendFrameHeader(this.getBuffer().remaining(), (PooledByteBuffer)null);
      } else {
         if (fcWindow <= dataPaddingBytes + 1) {
            if (this.getBuffer().remaining() >= fcWindow) {
               dataPaddingBytes = 0;
            } else if (this.getBuffer().remaining() == dataPaddingBytes) {
               dataPaddingBytes = 1;
            } else {
               dataPaddingBytes = fcWindow - this.getBuffer().remaining() - 1;
            }
         }

         boolean finalFrame = this.isFinalFrameQueued() && fcWindow >= this.getBuffer().remaining() + (dataPaddingBytes > 0 ? dataPaddingBytes + 1 : 0);
         PooledByteBuffer firstHeaderBuffer = ((Http2Channel)this.getChannel()).getBufferPool().allocate();
         PooledByteBuffer[] allHeaderBuffers = null;
         ByteBuffer firstBuffer = firstHeaderBuffer.getBuffer();
         boolean firstFrame = false;
         HeaderMap trailers = null;
         if (finalFrame && this.trailersProducer != null) {
            trailers = this.trailersProducer.getTrailers();
            if (trailers != null && trailers.size() == 0) {
               trailers = null;
            }
         }

         int i;
         if (this.first) {
            firstFrame = true;
            this.first = false;
            firstBuffer.put((byte)0);
            firstBuffer.put((byte)0);
            firstBuffer.put((byte)0);
            firstBuffer.put((byte)this.frameType);
            firstBuffer.put((byte)0);
            Http2ProtocolUtils.putInt(firstBuffer, this.getStreamId());
            int paddingBytes = ((Http2Channel)this.getChannel()).getPaddingBytes();
            if (paddingBytes > 0) {
               firstBuffer.put((byte)(paddingBytes & 255));
            }

            this.writeBeforeHeaderBlock(firstBuffer);
            HeaderMap headers = this.headers;
            HpackEncoder.State result = this.encoder.encode(headers, firstBuffer);
            PooledByteBuffer current = firstHeaderBuffer;
            int headerFrameLength = firstBuffer.position() - 9 + paddingBytes;
            firstBuffer.put(0, (byte)(headerFrameLength >> 16 & 255));
            firstBuffer.put(1, (byte)(headerFrameLength >> 8 & 255));
            firstBuffer.put(2, (byte)(headerFrameLength & 255));
            firstBuffer.put(4, (byte)((this.isFinalFrameQueued() && !this.getBuffer().hasRemaining() && this.frameType == 1 && trailers == null ? 1 : 0) | (result == HpackEncoder.State.COMPLETE ? 4 : 0) | (paddingBytes > 0 ? 8 : 0)));
            ByteBuffer currentBuffer = firstBuffer;
            if (firstBuffer.remaining() < paddingBytes) {
               allHeaderBuffers = this.allocateAll(allHeaderBuffers, firstHeaderBuffer);
               current = allHeaderBuffers[allHeaderBuffers.length - 1];
               currentBuffer = current.getBuffer();
            }

            for(i = 0; i < paddingBytes; ++i) {
               currentBuffer.put((byte)0);
            }

            while(result != HpackEncoder.State.COMPLETE) {
               allHeaderBuffers = this.allocateAll(allHeaderBuffers, current);
               current = allHeaderBuffers[allHeaderBuffers.length - 1];
               result = this.encodeContinuationFrame(headers, current);
            }
         }

         PooledByteBuffer currentPooled = allHeaderBuffers == null ? firstHeaderBuffer : allHeaderBuffers[allHeaderBuffers.length - 1];
         ByteBuffer currentBuffer = ((PooledByteBuffer)currentPooled).getBuffer();
         ByteBuffer trailer = null;
         int remainingInBuffer = 0;
         boolean requiresTrailers = false;
         int length;
         if (this.getBuffer().remaining() > 0) {
            if (fcWindow > 0) {
               if (currentBuffer.remaining() < 10) {
                  allHeaderBuffers = this.allocateAll(allHeaderBuffers, (PooledByteBuffer)currentPooled);
                  currentPooled = allHeaderBuffers == null ? firstHeaderBuffer : allHeaderBuffers[allHeaderBuffers.length - 1];
                  currentBuffer = ((PooledByteBuffer)currentPooled).getBuffer();
               }

               length = fcWindow - dataPaddingBytes - (dataPaddingBytes > 0 ? 1 : 0);
               remainingInBuffer = this.getBuffer().remaining() - length;
               this.getBuffer().limit(this.getBuffer().position() + length);
               currentBuffer.put((byte)(fcWindow >> 16 & 255));
               currentBuffer.put((byte)(fcWindow >> 8 & 255));
               currentBuffer.put((byte)(fcWindow & 255));
               currentBuffer.put((byte)0);
               if (trailers == null) {
                  currentBuffer.put((byte)((finalFrame ? 1 : 0) | (dataPaddingBytes > 0 ? 8 : 0)));
               } else {
                  if (finalFrame) {
                     requiresTrailers = true;
                  }

                  currentBuffer.put((byte)(dataPaddingBytes > 0 ? 8 : 0));
               }

               Http2ProtocolUtils.putInt(currentBuffer, this.getStreamId());
               if (dataPaddingBytes > 0) {
                  currentBuffer.put((byte)(dataPaddingBytes & 255));
                  trailer = ByteBuffer.allocate(dataPaddingBytes);
               }
            } else {
               remainingInBuffer = this.getBuffer().remaining();
            }
         } else if (finalFrame && !firstFrame) {
            currentBuffer.put((byte)(fcWindow >> 16 & 255));
            currentBuffer.put((byte)(fcWindow >> 8 & 255));
            currentBuffer.put((byte)(fcWindow & 255));
            currentBuffer.put((byte)0);
            if (trailers == null) {
               currentBuffer.put((byte)(1 | (dataPaddingBytes > 0 ? 8 : 0)));
            } else {
               requiresTrailers = true;
               currentBuffer.put((byte)(dataPaddingBytes > 0 ? 8 : 0));
            }

            Http2ProtocolUtils.putInt(currentBuffer, this.getStreamId());
            if (dataPaddingBytes > 0) {
               currentBuffer.put((byte)(dataPaddingBytes & 255));
               trailer = ByteBuffer.allocate(dataPaddingBytes);
            }
         } else if (finalFrame && trailers != null) {
            requiresTrailers = true;
         }

         int i;
         int i;
         if (requiresTrailers) {
            PooledByteBuffer firstTrailerBuffer = ((Http2Channel)this.getChannel()).getBufferPool().allocate();
            if (trailer != null) {
               firstTrailerBuffer.getBuffer().put(trailer);
            }

            firstTrailerBuffer.getBuffer().put((byte)0);
            firstTrailerBuffer.getBuffer().put((byte)0);
            firstTrailerBuffer.getBuffer().put((byte)0);
            firstTrailerBuffer.getBuffer().put((byte)1);
            firstTrailerBuffer.getBuffer().put((byte)5);
            Http2ProtocolUtils.putInt(firstTrailerBuffer.getBuffer(), this.getStreamId());
            HpackEncoder.State result = this.encoder.encode(trailers, firstTrailerBuffer.getBuffer());
            if (result != HpackEncoder.State.COMPLETE) {
               throw UndertowMessages.MESSAGES.http2TrailerToLargeForSingleBuffer();
            }

            i = firstTrailerBuffer.getBuffer().position() - 9;
            firstTrailerBuffer.getBuffer().put(0, (byte)(i >> 16 & 255));
            firstTrailerBuffer.getBuffer().put(1, (byte)(i >> 8 & 255));
            firstTrailerBuffer.getBuffer().put(2, (byte)(i & 255));
            firstTrailerBuffer.getBuffer().flip();
            i = firstTrailerBuffer.getBuffer().remaining();
            trailer = ByteBuffer.allocate(i);
            trailer.put(firstTrailerBuffer.getBuffer());
            trailer.flip();
            firstTrailerBuffer.close();
         }

         if (allHeaderBuffers == null) {
            currentBuffer.flip();
            return new SendFrameHeader(remainingInBuffer, (PooledByteBuffer)currentPooled, false, trailer);
         } else {
            length = 0;

            for(i = 0; i < allHeaderBuffers.length; ++i) {
               length += allHeaderBuffers[i].getBuffer().position();
               allHeaderBuffers[i].getBuffer().flip();
            }

            boolean var22 = false;

            SendFrameHeader var33;
            try {
               var22 = true;
               ByteBuffer newBuf = ByteBuffer.allocate(length);
               i = 0;

               while(true) {
                  if (i >= allHeaderBuffers.length) {
                     newBuf.flip();
                     var33 = new SendFrameHeader(remainingInBuffer, new ImmediatePooledByteBuffer(newBuf), false, trailer);
                     var22 = false;
                     break;
                  }

                  newBuf.put(allHeaderBuffers[i].getBuffer());
                  ++i;
               }
            } finally {
               if (var22) {
                  for(int i = 0; i < allHeaderBuffers.length; ++i) {
                     allHeaderBuffers[i].close();
                  }

               }
            }

            for(i = 0; i < allHeaderBuffers.length; ++i) {
               allHeaderBuffers[i].close();
            }

            return var33;
         }
      }
   }

   private HpackEncoder.State encodeContinuationFrame(HeaderMap headers, PooledByteBuffer current) {
      ByteBuffer currentBuffer = current.getBuffer();
      currentBuffer.put((byte)0);
      currentBuffer.put((byte)0);
      currentBuffer.put((byte)0);
      currentBuffer.put((byte)9);
      currentBuffer.put((byte)0);
      Http2ProtocolUtils.putInt(currentBuffer, this.getStreamId());
      HpackEncoder.State result = this.encoder.encode(headers, currentBuffer);
      int contFrameLength = currentBuffer.position() - 9;
      currentBuffer.put(0, (byte)(contFrameLength >> 16 & 255));
      currentBuffer.put(1, (byte)(contFrameLength >> 8 & 255));
      currentBuffer.put(2, (byte)(contFrameLength & 255));
      currentBuffer.put(4, (byte)(result == HpackEncoder.State.COMPLETE ? 4 : 0));
      return result;
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      this.handleFailedChannel();
      return super.write(srcs, offset, length);
   }

   private void handleFailedChannel() {
      if (this.completionListenerFailure && this.completionListener != null) {
         ChannelListeners.invokeChannelListener(this, this.completionListener);
         this.completionListener = null;
      }

   }

   public long write(ByteBuffer[] srcs) throws IOException {
      this.handleFailedChannel();
      return super.write(srcs);
   }

   public int write(ByteBuffer src) throws IOException {
      this.handleFailedChannel();
      return super.write(src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      this.handleFailedChannel();
      return super.writeFinal(srcs, offset, length);
   }

   public long writeFinal(ByteBuffer[] srcs) throws IOException {
      this.handleFailedChannel();
      return super.writeFinal(srcs);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      this.handleFailedChannel();
      return super.writeFinal(src);
   }

   public boolean flush() throws IOException {
      this.handleFailedChannel();
      if (this.completionListenerReady && this.completionListener != null) {
         ChannelListeners.invokeChannelListener(this, this.completionListener);
         this.completionListener = null;
      }

      return super.flush();
   }

   protected void writeBeforeHeaderBlock(ByteBuffer buffer) {
   }

   protected boolean isFlushRequiredOnEmptyBuffer() {
      return this.first;
   }

   public HeaderMap getHeaders() {
      return this.headers;
   }

   protected void handleFlushComplete(boolean finalFrame) {
      super.handleFlushComplete(finalFrame);
      if (finalFrame && this.completionListener != null) {
         this.completionListenerReady = true;
      }

   }

   protected void channelForciblyClosed() throws IOException {
      super.channelForciblyClosed();
      if (this.completionListener != null) {
         this.completionListenerFailure = true;
      }

   }

   public ChannelListener<Http2DataStreamSinkChannel> getCompletionListener() {
      return this.completionListener;
   }

   public void setCompletionListener(ChannelListener<Http2DataStreamSinkChannel> completionListener) {
      this.completionListener = completionListener;
   }

   public interface TrailersProducer {
      HeaderMap getTrailers();
   }
}
