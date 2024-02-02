package io.undertow.protocols.http2;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
import io.undertow.server.protocol.framed.FrameHeaderData;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.Bits;

class Http2FrameHeaderParser implements FrameHeaderData {
   final byte[] header = new byte[9];
   int read = 0;
   int length;
   int type;
   int flags;
   int streamId;
   Http2PushBackParser parser = null;
   Http2HeadersParser continuationParser = null;
   private static final int SECOND_RESERVED_MASK = -129;
   private Http2Channel http2Channel;

   Http2FrameHeaderParser(Http2Channel http2Channel, Http2HeadersParser continuationParser) {
      this.http2Channel = http2Channel;
      this.continuationParser = continuationParser;
   }

   public boolean handle(ByteBuffer byteBuffer) throws IOException {
      if (this.parser == null) {
         if (!this.parseFrameHeader(byteBuffer)) {
            return false;
         }

         if (this.continuationParser != null && this.type != 9) {
            throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.expectedContinuationFrame());
         }

         switch (this.type) {
            case 0:
               if (this.streamId == 0) {
                  throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustNotBeZeroForFrameType(0));
               }

               this.parser = new Http2DataFrameParser(this.length);
               break;
            case 1:
               if (this.streamId == 0) {
                  throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustNotBeZeroForFrameType(1));
               }

               this.parser = new Http2HeadersParser(this.length, this.http2Channel.getDecoder(), this.http2Channel.isClient(), this.http2Channel.getMaxHeaders(), this.streamId, this.http2Channel.getMaxHeaderListSize());
               if (Bits.allAreClear(this.flags, 4)) {
                  this.continuationParser = (Http2HeadersParser)this.parser;
               }
               break;
            case 2:
               if (this.length != 5) {
                  throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.incorrectFrameSize());
               }

               if (this.streamId == 0) {
                  throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustNotBeZeroForFrameType(2));
               }

               this.parser = new Http2PriorityParser(this.length);
               break;
            case 3:
               if (this.length != 4) {
                  throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.incorrectFrameSize());
               }

               this.parser = new Http2RstStreamParser(this.length);
               break;
            case 4:
               if (this.length % 6 != 0) {
                  throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.incorrectFrameSize());
               }

               if (this.streamId != 0) {
                  throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustBeZeroForFrameType(4));
               }

               this.parser = new Http2SettingsParser(this.length);
               break;
            case 5:
               this.parser = new Http2PushPromiseParser(this.length, this.http2Channel.getDecoder(), this.http2Channel.isClient(), this.http2Channel.getMaxHeaders(), this.streamId, this.http2Channel.getMaxHeaderListSize());
               if (Bits.allAreClear(this.flags, 4)) {
                  this.continuationParser = (Http2HeadersParser)this.parser;
               }
               break;
            case 6:
               if (this.length != 8) {
                  throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.invalidPingSize());
               }

               if (this.streamId != 0) {
                  throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustBeZeroForFrameType(6));
               }

               this.parser = new Http2PingParser(this.length);
               break;
            case 7:
               if (this.streamId != 0) {
                  throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustBeZeroForFrameType(7));
               }

               this.parser = new Http2GoAwayParser(this.length);
               break;
            case 8:
               if (this.length != 4) {
                  throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.incorrectFrameSize());
               }

               this.parser = new Http2WindowUpdateParser(this.length);
               break;
            case 9:
               if (this.continuationParser == null) {
                  this.http2Channel.sendGoAway(1);
                  throw UndertowMessages.MESSAGES.http2ContinuationFrameNotExpected();
               }

               if (this.continuationParser.getStreamId() != this.streamId) {
                  this.http2Channel.sendGoAway(1);
                  throw UndertowMessages.MESSAGES.http2ContinuationFrameNotExpected();
               }

               this.parser = this.continuationParser;
               this.continuationParser.moreData(this.length);
               break;
            default:
               this.parser = new Http2DiscardParser(this.length);
         }
      }

      this.parser.parse(byteBuffer, this);
      if (this.continuationParser != null && Bits.anyAreSet(this.flags, 4)) {
         this.continuationParser = null;
      }

      return this.parser.isFinished();
   }

   private boolean parseFrameHeader(ByteBuffer byteBuffer) {
      while(this.read < 9 && byteBuffer.hasRemaining()) {
         this.header[this.read++] = byteBuffer.get();
      }

      if (this.read != 9) {
         return false;
      } else {
         this.length = (this.header[0] & 255) << 16;
         this.length += (this.header[1] & 255) << 8;
         this.length += this.header[2] & 255;
         this.type = this.header[3] & 255;
         this.flags = this.header[4] & 255;
         this.streamId = (this.header[5] & -129 & 255) << 24;
         this.streamId += (this.header[6] & 255) << 16;
         this.streamId += (this.header[7] & 255) << 8;
         this.streamId += this.header[8] & 255;
         return true;
      }
   }

   public long getFrameLength() {
      return this.type != 0 ? 0L : (long)this.length;
   }

   int getActualLength() {
      return this.length;
   }

   public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
      if (this.type != 0 && this.type != 9 && this.type != 2) {
         if (this.type == 1) {
            Http2StreamSourceChannel channel = this.http2Channel.getIncomingStream(this.streamId);
            if (channel != null) {
               if (Bits.anyAreClear(this.flags, 1)) {
                  UndertowLogger.REQUEST_IO_LOGGER.debug("Received HTTP/2 trailers header without end stream set");
                  this.http2Channel.sendGoAway(1);
               }

               if (!channel.isHeadersEndStream() && Bits.anyAreSet(this.flags, 4)) {
                  this.http2Channel.removeStreamSource(this.streamId);
               }
            }

            return channel;
         } else {
            return null;
         }
      } else {
         Http2StreamSourceChannel http2StreamSourceChannel;
         if (Bits.anyAreSet(this.flags, 1)) {
            http2StreamSourceChannel = this.http2Channel.removeStreamSource(this.streamId);
         } else if (this.type == 9) {
            http2StreamSourceChannel = this.http2Channel.getIncomingStream(this.streamId);
            if (http2StreamSourceChannel != null && http2StreamSourceChannel.isHeadersEndStream() && Bits.anyAreSet(this.flags, 4)) {
               this.http2Channel.removeStreamSource(this.streamId);
            }
         } else {
            http2StreamSourceChannel = this.http2Channel.getIncomingStream(this.streamId);
         }

         if (this.type == 0 && http2StreamSourceChannel != null) {
            Http2DataFrameParser dataFrameParser = (Http2DataFrameParser)this.parser;
            http2StreamSourceChannel.updateContentSize(this.getFrameLength() - (long)dataFrameParser.getPadding(), Bits.anyAreSet(this.flags, 1));
         }

         return http2StreamSourceChannel;
      }
   }

   Http2PushBackParser getParser() {
      return this.parser;
   }

   Http2HeadersParser getContinuationParser() {
      return this.continuationParser;
   }
}
