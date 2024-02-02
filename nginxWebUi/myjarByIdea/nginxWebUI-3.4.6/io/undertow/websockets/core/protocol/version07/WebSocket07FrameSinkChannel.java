package io.undertow.websockets.core.protocol.version07;

import io.undertow.UndertowLogger;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.ImmediatePooledByteBuffer;
import io.undertow.websockets.core.StreamSinkFrameChannel;
import io.undertow.websockets.core.WebSocketFrameType;
import io.undertow.websockets.core.WebSocketMessages;
import io.undertow.websockets.extensions.ExtensionFunction;
import io.undertow.websockets.extensions.NoopExtensionFunction;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

public abstract class WebSocket07FrameSinkChannel extends StreamSinkFrameChannel {
   private final Masker masker;
   private volatile boolean dataWritten = false;
   protected final ExtensionFunction extensionFunction;

   protected WebSocket07FrameSinkChannel(WebSocket07Channel wsChannel, WebSocketFrameType type) {
      super(wsChannel, type);
      if (wsChannel.isClient()) {
         this.masker = new Masker(0);
      } else {
         this.masker = null;
      }

      if (!wsChannel.areExtensionsSupported() || type != WebSocketFrameType.TEXT && type != WebSocketFrameType.BINARY) {
         this.extensionFunction = NoopExtensionFunction.INSTANCE;
         this.setRsv(0);
      } else {
         this.extensionFunction = wsChannel.getExtensionFunction();
         this.setRsv(this.extensionFunction.writeRsv(0));
      }

   }

   protected void handleFlushComplete(boolean finalFrame) {
      this.dataWritten = true;
   }

   private byte opCode() {
      if (this.dataWritten) {
         return 0;
      } else {
         switch (this.getType()) {
            case CONTINUATION:
               return 0;
            case TEXT:
               return 1;
            case BINARY:
               return 2;
            case CLOSE:
               return 8;
            case PING:
               return 9;
            case PONG:
               return 10;
            default:
               throw WebSocketMessages.MESSAGES.unsupportedFrameType(this.getType());
         }
      }
   }

   protected SendFrameHeader createFrameHeader() {
      byte b0 = 0;
      if (this.isFinalFrameQueued()) {
         b0 = (byte)(b0 | 128);
      }

      byte opCode = this.opCode();
      int rsv = opCode == 0 ? 0 : this.getRsv();
      b0 = (byte)(b0 | (rsv & 7) << 4);
      b0 = (byte)(b0 | opCode & 15);
      ByteBuffer header = ByteBuffer.allocate(14);
      byte maskKey = 0;
      if (this.masker != null) {
         maskKey = (byte)(maskKey | 128);
      }

      long payloadSize = (long)this.getBuffer().remaining();
      if (payloadSize > 125L && opCode == 9) {
         throw WebSocketMessages.MESSAGES.invalidPayloadLengthForPing(payloadSize);
      } else {
         if (payloadSize <= 125L) {
            header.put(b0);
            header.put((byte)((int)((payloadSize | (long)maskKey) & 255L)));
         } else if (payloadSize <= 65535L) {
            header.put(b0);
            header.put((byte)((126 | maskKey) & 255));
            header.put((byte)((int)(payloadSize >>> 8 & 255L)));
            header.put((byte)((int)(payloadSize & 255L)));
         } else {
            header.put(b0);
            header.put((byte)((127 | maskKey) & 255));
            header.putLong(payloadSize);
         }

         if (this.masker != null) {
            int maskingKey = ThreadLocalRandom.current().nextInt();
            header.put((byte)(maskingKey >> 24 & 255));
            header.put((byte)(maskingKey >> 16 & 255));
            header.put((byte)(maskingKey >> 8 & 255));
            header.put((byte)(maskingKey & 255));
            this.masker.setMaskingKey(maskingKey);
            ByteBuffer buf = this.getBuffer();
            this.masker.beforeWrite(buf, buf.position(), buf.remaining());
         }

         header.flip();
         return new SendFrameHeader(0, new ImmediatePooledByteBuffer(header));
      }
   }

   protected PooledByteBuffer preWriteTransform(PooledByteBuffer body) {
      try {
         return super.preWriteTransform(this.extensionFunction.transformForWrite(body, this, this.isFinalFrameQueued()));
      } catch (IOException var3) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var3);
         this.markBroken();
         throw new RuntimeException(var3);
      }
   }
}
