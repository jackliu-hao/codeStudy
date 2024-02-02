package io.undertow.protocols.http2;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.server.Connectors;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.xnio.Bits;

abstract class Http2HeaderBlockParser extends Http2PushBackParser implements HpackDecoder.HeaderEmitter {
   private final HeaderMap headerMap = new HeaderMap();
   private boolean beforeHeadersHandled = false;
   private final HpackDecoder decoder;
   private int frameRemaining = -1;
   private boolean invalid = false;
   private boolean processingPseudoHeaders = true;
   private final boolean client;
   private final int maxHeaders;
   private final int maxHeaderListSize;
   private int currentPadding;
   private final int streamId;
   private int headerSize;
   private static final Set<HttpString> SERVER_HEADERS;

   Http2HeaderBlockParser(int frameLength, HpackDecoder decoder, boolean client, int maxHeaders, int streamId, int maxHeaderListSize) {
      super(frameLength);
      this.decoder = decoder;
      this.client = client;
      this.maxHeaders = maxHeaders;
      this.streamId = streamId;
      this.maxHeaderListSize = maxHeaderListSize;
   }

   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser header) throws IOException {
      boolean continuationFramesComing = Bits.anyAreClear(header.flags, 4);
      if (this.frameRemaining == -1) {
         this.frameRemaining = header.length;
      }

      boolean moreDataThisFrame = resource.remaining() < this.frameRemaining;
      int pos = resource.position();
      int readInBeforeHeader = 0;
      boolean var13 = false;

      int oldLimit;
      label147: {
         try {
            var13 = true;
            if (!this.beforeHeadersHandled) {
               if (!this.handleBeforeHeader(resource, header)) {
                  var13 = false;
                  break label147;
               }

               this.currentPadding = this.getPaddingLength();
               readInBeforeHeader = resource.position() - pos;
            }

            this.beforeHeadersHandled = true;
            this.decoder.setHeaderEmitter(this);
            oldLimit = -1;
            int paddingInBuffer;
            if (this.currentPadding > 0) {
               paddingInBuffer = this.frameRemaining - readInBeforeHeader - this.currentPadding;
               if (paddingInBuffer < 0) {
                  throw new ConnectionErrorException(1);
               }

               if (resource.remaining() > paddingInBuffer) {
                  oldLimit = resource.limit();
                  resource.limit(resource.position() + paddingInBuffer);
               }
            }

            try {
               this.decoder.decode(resource, moreDataThisFrame || continuationFramesComing);
            } catch (HpackException var14) {
               throw new ConnectionErrorException(var14.getCloseCode(), var14);
            }

            if (this.maxHeaders > 0 && this.headerMap.size() > this.maxHeaders) {
               throw new StreamErrorException(6);
            }

            if (oldLimit != -1) {
               if (resource.remaining() == 0) {
                  paddingInBuffer = oldLimit - resource.limit();
                  this.currentPadding -= paddingInBuffer;
                  resource.limit(oldLimit);
                  resource.position(oldLimit);
                  var13 = false;
               } else {
                  resource.limit(oldLimit);
                  var13 = false;
               }
            } else {
               var13 = false;
            }
         } finally {
            if (var13) {
               int used = resource.position() - pos;
               this.frameRemaining -= used;
            }
         }

         oldLimit = resource.position() - pos;
         this.frameRemaining -= oldLimit;
         return;
      }

      oldLimit = resource.position() - pos;
      this.frameRemaining -= oldLimit;
   }

   protected abstract boolean handleBeforeHeader(ByteBuffer var1, Http2FrameHeaderParser var2);

   HeaderMap getHeaderMap() {
      return this.headerMap;
   }

   public void emitHeader(HttpString name, String value, boolean neverIndex) throws HpackException {
      if (this.maxHeaderListSize > 0) {
         this.headerSize += name.length() + value.length() + 32;
         if (this.headerSize > this.maxHeaderListSize) {
            throw new HpackException(UndertowMessages.MESSAGES.headerBlockTooLarge(), 1);
         }
      }

      if (this.maxHeaders <= 0 || this.headerMap.size() <= this.maxHeaders) {
         this.headerMap.add(name, value);
         if (name.length() == 0) {
            throw UndertowMessages.MESSAGES.invalidHeader();
         } else if (name.equals(Headers.TRANSFER_ENCODING)) {
            throw new HpackException(1);
         } else {
            if (name.byteAt(0) == 58) {
               if (this.client) {
                  if (!name.equals(Http2Channel.STATUS)) {
                     this.invalid = true;
                  }
               } else if (!SERVER_HEADERS.contains(name)) {
                  this.invalid = true;
               }

               if (!this.processingPseudoHeaders) {
                  throw new HpackException(UndertowMessages.MESSAGES.pseudoHeaderInWrongOrder(name), 1);
               }
            } else {
               this.processingPseudoHeaders = false;
            }

            for(int i = 0; i < name.length(); ++i) {
               byte c = name.byteAt(i);
               if (c >= 65 && c <= 90) {
                  this.invalid = true;
                  UndertowLogger.REQUEST_LOGGER.debugf("Malformed request, header %s contains uppercase characters", name);
               } else if (c != 58 && !Connectors.isValidTokenCharacter(c)) {
                  this.invalid = true;
                  UndertowLogger.REQUEST_LOGGER.debugf("Malformed request, header %s contains invalid token character", name);
               }
            }

         }
      }
   }

   protected abstract int getPaddingLength();

   protected void moreData(int data) {
      super.moreData(data);
      this.frameRemaining += data;
   }

   public boolean isInvalid() {
      return this.invalid;
   }

   public int getStreamId() {
      return this.streamId;
   }

   static {
      Set<HttpString> server = new HashSet();
      server.add(Http2Channel.METHOD);
      server.add(Http2Channel.AUTHORITY);
      server.add(Http2Channel.SCHEME);
      server.add(Http2Channel.PATH);
      SERVER_HEADERS = Collections.unmodifiableSet(server);
   }
}
