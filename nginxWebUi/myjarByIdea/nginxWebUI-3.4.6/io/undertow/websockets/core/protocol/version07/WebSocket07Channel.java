package io.undertow.websockets.core.protocol.version07;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
import io.undertow.websockets.core.StreamSinkFrameChannel;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketException;
import io.undertow.websockets.core.WebSocketFrame;
import io.undertow.websockets.core.WebSocketFrameCorruptedException;
import io.undertow.websockets.core.WebSocketFrameType;
import io.undertow.websockets.core.WebSocketLogger;
import io.undertow.websockets.core.WebSocketMessages;
import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.extensions.ExtensionFunction;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Set;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;

public class WebSocket07Channel extends WebSocketChannel {
   private int fragmentedFramesCount;
   private final ByteBuffer lengthBuffer = ByteBuffer.allocate(8);
   private UTF8Checker checker;
   protected static final byte OPCODE_CONT = 0;
   protected static final byte OPCODE_TEXT = 1;
   protected static final byte OPCODE_BINARY = 2;
   protected static final byte OPCODE_CLOSE = 8;
   protected static final byte OPCODE_PING = 9;
   protected static final byte OPCODE_PONG = 10;

   public WebSocket07Channel(StreamConnection channel, ByteBufferPool bufferPool, String wsUrl, String subProtocol, boolean client, boolean allowExtensions, ExtensionFunction extensionFunction, Set<WebSocketChannel> openConnections, OptionMap options) {
      super(channel, bufferPool, WebSocketVersion.V08, wsUrl, subProtocol, client, allowExtensions, extensionFunction, openConnections, options);
   }

   protected WebSocketChannel.PartialFrame receiveFrame() {
      return new WebSocketFrameHeader();
   }

   protected void markReadsBroken(Throwable cause) {
      super.markReadsBroken(cause);
   }

   protected void closeSubChannels() {
      IoUtils.safeClose((Closeable)this.fragmentedChannel);
   }

   protected StreamSinkFrameChannel createStreamSinkChannel(WebSocketFrameType type) {
      switch (type) {
         case TEXT:
            return new WebSocket07TextFrameSinkChannel(this);
         case BINARY:
            return new WebSocket07BinaryFrameSinkChannel(this);
         case CLOSE:
            return new WebSocket07CloseFrameSinkChannel(this);
         case PONG:
            return new WebSocket07PongFrameSinkChannel(this);
         case PING:
            return new WebSocket07PingFrameSinkChannel(this);
         default:
            throw WebSocketMessages.MESSAGES.unsupportedFrameType(type);
      }
   }

   class WebSocketFrameHeader implements WebSocketFrame {
      private boolean frameFinalFlag;
      private int frameRsv;
      private int frameOpcode;
      private int maskingKey;
      private boolean frameMasked;
      private long framePayloadLength;
      private State state;
      private int framePayloadLen1;
      private boolean done;

      WebSocketFrameHeader() {
         this.state = WebSocket07Channel.State.READING_FIRST;
         this.done = false;
      }

      public StreamSourceFrameChannel getChannel(PooledByteBuffer pooled) {
         StreamSourceFrameChannel channel = this.createChannel(pooled);
         if (this.frameFinalFlag) {
            channel.finalFrame();
         } else {
            WebSocket07Channel.this.fragmentedChannel = channel;
         }

         return channel;
      }

      public StreamSourceFrameChannel createChannel(PooledByteBuffer pooled) {
         if (this.frameOpcode == 9) {
            return this.frameMasked ? new WebSocket07PingFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, new Masker(this.maskingKey), pooled, this.framePayloadLength) : new WebSocket07PingFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, pooled, this.framePayloadLength);
         } else if (this.frameOpcode == 10) {
            return this.frameMasked ? new WebSocket07PongFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, new Masker(this.maskingKey), pooled, this.framePayloadLength) : new WebSocket07PongFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, pooled, this.framePayloadLength);
         } else if (this.frameOpcode == 8) {
            return this.frameMasked ? new WebSocket07CloseFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, new Masker(this.maskingKey), pooled, this.framePayloadLength) : new WebSocket07CloseFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, pooled, this.framePayloadLength);
         } else if (this.frameOpcode == 1) {
            UTF8Checker checker = WebSocket07Channel.this.checker;
            if (checker == null) {
               checker = new UTF8Checker();
            }

            if (!this.frameFinalFlag) {
               WebSocket07Channel.this.checker = checker;
            } else {
               WebSocket07Channel.this.checker = null;
            }

            return this.frameMasked ? new WebSocket07TextFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, new Masker(this.maskingKey), checker, pooled, this.framePayloadLength) : new WebSocket07TextFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, checker, pooled, this.framePayloadLength);
         } else if (this.frameOpcode == 2) {
            return this.frameMasked ? new WebSocket07BinaryFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, new Masker(this.maskingKey), pooled, this.framePayloadLength) : new WebSocket07BinaryFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, pooled, this.framePayloadLength);
         } else if (this.frameOpcode == 0) {
            throw new RuntimeException();
         } else if (WebSocket07Channel.this.hasReservedOpCode) {
            return this.frameMasked ? new WebSocket07BinaryFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, new Masker(this.maskingKey), pooled, this.framePayloadLength) : new WebSocket07BinaryFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, pooled, this.framePayloadLength);
         } else {
            throw WebSocketMessages.MESSAGES.unsupportedOpCode(this.frameOpcode);
         }
      }

      public void handle(ByteBuffer buffer) throws WebSocketException {
         if (buffer.hasRemaining()) {
            while(this.state != WebSocket07Channel.State.DONE) {
               byte b;
               switch (this.state) {
                  case READING_FIRST:
                     b = buffer.get();
                     this.frameFinalFlag = (b & 128) != 0;
                     this.frameRsv = (b & 112) >> 4;
                     this.frameOpcode = b & 15;
                     if (WebSocketLogger.REQUEST_LOGGER.isDebugEnabled()) {
                        WebSocketLogger.REQUEST_LOGGER.decodingFrameWithOpCode(this.frameOpcode);
                     }

                     this.state = WebSocket07Channel.State.READING_SECOND;
                     WebSocket07Channel.this.lengthBuffer.clear();
                  case READING_SECOND:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     this.frameMasked = (b & 128) != 0;
                     this.framePayloadLen1 = b & 127;
                     if (this.frameRsv != 0 && !WebSocket07Channel.this.areExtensionsSupported()) {
                        throw WebSocketMessages.MESSAGES.extensionsNotAllowed(this.frameRsv);
                     }

                     if (this.frameOpcode > 7) {
                        this.validateControlFrame();
                     } else {
                        this.validateDataFrame();
                     }

                     if (this.framePayloadLen1 != 126 && this.framePayloadLen1 != 127) {
                        this.framePayloadLength = (long)this.framePayloadLen1;
                        if (this.frameMasked) {
                           this.state = WebSocket07Channel.State.READING_MASK_1;
                        } else {
                           this.state = WebSocket07Channel.State.DONE;
                        }
                        break;
                     } else {
                        this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE1;
                     }
                  case READING_EXTENDED_SIZE1:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     WebSocket07Channel.this.lengthBuffer.put(b);
                     this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE2;
                  case READING_EXTENDED_SIZE2:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     WebSocket07Channel.this.lengthBuffer.put(b);
                     if (this.framePayloadLen1 == 126) {
                        WebSocket07Channel.this.lengthBuffer.flip();
                        this.framePayloadLength = (long)(WebSocket07Channel.this.lengthBuffer.getShort() & '\uffff');
                        if (this.frameMasked) {
                           this.state = WebSocket07Channel.State.READING_MASK_1;
                        } else {
                           this.state = WebSocket07Channel.State.DONE;
                        }
                        break;
                     } else {
                        this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE3;
                     }
                  case READING_EXTENDED_SIZE3:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     WebSocket07Channel.this.lengthBuffer.put(b);
                     this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE4;
                  case READING_EXTENDED_SIZE4:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     WebSocket07Channel.this.lengthBuffer.put(b);
                     this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE5;
                  case READING_EXTENDED_SIZE5:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     WebSocket07Channel.this.lengthBuffer.put(b);
                     this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE6;
                  case READING_EXTENDED_SIZE6:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     WebSocket07Channel.this.lengthBuffer.put(b);
                     this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE7;
                  case READING_EXTENDED_SIZE7:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     WebSocket07Channel.this.lengthBuffer.put(b);
                     this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE8;
                  case READING_EXTENDED_SIZE8:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     WebSocket07Channel.this.lengthBuffer.put(b);
                     WebSocket07Channel.this.lengthBuffer.flip();
                     this.framePayloadLength = WebSocket07Channel.this.lengthBuffer.getLong();
                     if (!this.frameMasked) {
                        this.state = WebSocket07Channel.State.DONE;
                        break;
                     } else {
                        this.state = WebSocket07Channel.State.READING_MASK_1;
                        this.state = WebSocket07Channel.State.READING_MASK_1;
                     }
                  case READING_MASK_1:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     this.maskingKey = b & 255;
                     this.state = WebSocket07Channel.State.READING_MASK_2;
                  case READING_MASK_2:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     this.maskingKey = this.maskingKey << 8 | b & 255;
                     this.state = WebSocket07Channel.State.READING_MASK_3;
                  case READING_MASK_3:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     this.maskingKey = this.maskingKey << 8 | b & 255;
                     this.state = WebSocket07Channel.State.READING_MASK_4;
                  case READING_MASK_4:
                     if (!buffer.hasRemaining()) {
                        return;
                     }

                     b = buffer.get();
                     this.maskingKey = this.maskingKey << 8 | b & 255;
                     this.state = WebSocket07Channel.State.DONE;
                     break;
                  default:
                     throw new IllegalStateException(this.state.toString());
               }
            }

            if (this.frameFinalFlag) {
               if (this.frameOpcode != 9 && this.frameOpcode != 10) {
                  WebSocket07Channel.this.fragmentedFramesCount = 0;
               }
            } else {
               WebSocket07Channel.this.fragmentedFramesCount++;
            }

            this.done = true;
         }
      }

      private void validateDataFrame() throws WebSocketFrameCorruptedException {
         if (!WebSocket07Channel.this.isClient() && !this.frameMasked) {
            throw WebSocketMessages.MESSAGES.frameNotMasked();
         } else if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
            throw WebSocketMessages.MESSAGES.reservedOpCodeInDataFrame(this.frameOpcode);
         } else if (WebSocket07Channel.this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
            throw WebSocketMessages.MESSAGES.continuationFrameOutsideFragmented();
         } else if (WebSocket07Channel.this.fragmentedFramesCount != 0 && this.frameOpcode != 0) {
            throw WebSocketMessages.MESSAGES.nonContinuationFrameInsideFragmented();
         }
      }

      private void validateControlFrame() throws WebSocketFrameCorruptedException {
         if (!this.frameFinalFlag) {
            throw WebSocketMessages.MESSAGES.fragmentedControlFrame();
         } else if (this.framePayloadLen1 > 125) {
            throw WebSocketMessages.MESSAGES.toBigControlFrame();
         } else if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
            throw WebSocketMessages.MESSAGES.reservedOpCodeInControlFrame(this.frameOpcode);
         } else if (this.frameOpcode == 8 && this.framePayloadLen1 == 1) {
            throw WebSocketMessages.MESSAGES.controlFrameWithPayloadLen1();
         }
      }

      public boolean isDone() {
         return this.done;
      }

      public long getFrameLength() {
         return this.framePayloadLength;
      }

      int getMaskingKey() {
         return this.maskingKey;
      }

      public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
         if (this.frameOpcode == 0) {
            StreamSourceFrameChannel ret = WebSocket07Channel.this.fragmentedChannel;
            if (this.frameFinalFlag) {
               WebSocket07Channel.this.fragmentedChannel = null;
            }

            return ret;
         } else {
            return null;
         }
      }

      public boolean isFinalFragment() {
         return this.frameFinalFlag;
      }
   }

   private static enum State {
      READING_FIRST,
      READING_SECOND,
      READING_EXTENDED_SIZE1,
      READING_EXTENDED_SIZE2,
      READING_EXTENDED_SIZE3,
      READING_EXTENDED_SIZE4,
      READING_EXTENDED_SIZE5,
      READING_EXTENDED_SIZE6,
      READING_EXTENDED_SIZE7,
      READING_EXTENDED_SIZE8,
      READING_MASK_1,
      READING_MASK_2,
      READING_MASK_3,
      READING_MASK_4,
      DONE;
   }
}
