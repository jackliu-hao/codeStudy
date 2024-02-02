package io.undertow.protocols.ajp;

import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.client.ProxiedRequestAttachments;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.Attachable;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HexConverter;
import io.undertow.util.HttpString;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.xnio.ChannelListener;

public class AjpClientRequestClientStreamSinkChannel extends AbstractAjpClientStreamSinkChannel {
   private final ChannelListener<AjpClientRequestClientStreamSinkChannel> finishListener;
   public static final int DEFAULT_MAX_DATA_SIZE = 8192;
   private final HeaderMap headers;
   private final String path;
   private final HttpString method;
   private final HttpString protocol;
   private final Attachable attachable;
   private boolean firstFrameWritten = false;
   private long dataSize;
   private int requestedChunkSize = -1;
   private SendFrameHeader header;
   private boolean discardMode = false;

   AjpClientRequestClientStreamSinkChannel(AjpClientChannel channel, ChannelListener<AjpClientRequestClientStreamSinkChannel> finishListener, HeaderMap headers, String path, HttpString method, HttpString protocol, Attachable attachable) {
      super(channel);
      this.finishListener = finishListener;
      this.headers = headers;
      this.path = path;
      this.method = method;
      this.protocol = protocol;
      this.attachable = attachable;
   }

   private SendFrameHeader createFrameHeaderImpl() {
      if (this.discardMode) {
         this.getBuffer().clear();
         this.getBuffer().flip();
         return new SendFrameHeader(new ImmediatePooledByteBuffer(ByteBuffer.wrap(new byte[0])));
      } else {
         PooledByteBuffer pooledHeaderBuffer = ((AjpClientChannel)this.getChannel()).getBufferPool().allocate();

         try {
            ByteBuffer buffer = pooledHeaderBuffer.getBuffer();
            ByteBuffer dataBuffer = this.getBuffer();
            int dataInBuffer = dataBuffer.remaining();
            if (!this.firstFrameWritten && this.requestedChunkSize == 0) {
               return new SendFrameHeader(dataInBuffer, (PooledByteBuffer)null);
            } else {
               int maxData = ((AjpClientChannel)this.getChannel()).getSettings().get(UndertowOptions.MAX_AJP_PACKET_SIZE, 8192) - 6;
               if (!this.firstFrameWritten) {
                  String contentLength = this.headers.getFirst(Headers.CONTENT_LENGTH);
                  if (contentLength != null) {
                     this.dataSize = Long.parseLong(contentLength);
                     this.requestedChunkSize = maxData;
                     if ((long)dataInBuffer > this.dataSize) {
                        throw UndertowMessages.MESSAGES.fixedLengthOverflow();
                     }
                  } else if (this.isWritesShutdown() && !this.headers.contains(Headers.TRANSFER_ENCODING)) {
                     this.headers.put(Headers.CONTENT_LENGTH, (long)dataInBuffer);
                     this.dataSize = (long)dataInBuffer;
                     this.requestedChunkSize = maxData;
                  } else {
                     this.headers.put(Headers.TRANSFER_ENCODING, Headers.CHUNKED.toString());
                     this.dataSize = -1L;
                     this.requestedChunkSize = 0;
                  }

                  this.firstFrameWritten = true;
                  int qsIndex = this.path.indexOf(63);
                  String path;
                  String queryString;
                  if (qsIndex == -1) {
                     path = this.path;
                     queryString = null;
                  } else {
                     path = this.path.substring(0, qsIndex);
                     queryString = this.path.substring(qsIndex + 1);
                  }

                  buffer.put((byte)18);
                  buffer.put((byte)52);
                  buffer.put((byte)0);
                  buffer.put((byte)0);
                  buffer.put((byte)2);
                  boolean storeMethod = false;
                  Integer methodNp = (Integer)AjpConstants.HTTP_METHODS_MAP.get(this.method);
                  if (methodNp == null) {
                     methodNp = 255;
                     storeMethod = true;
                  }

                  buffer.put((byte)methodNp);
                  AjpUtils.putHttpString(buffer, this.protocol);
                  AjpUtils.putString(buffer, path);
                  AjpUtils.putString(buffer, AjpUtils.notNull((String)this.attachable.getAttachment(ProxiedRequestAttachments.REMOTE_ADDRESS)));
                  AjpUtils.putString(buffer, AjpUtils.notNull((String)this.attachable.getAttachment(ProxiedRequestAttachments.REMOTE_HOST)));
                  AjpUtils.putString(buffer, AjpUtils.notNull((String)this.attachable.getAttachment(ProxiedRequestAttachments.SERVER_NAME)));
                  AjpUtils.putInt(buffer, AjpUtils.notNull((Integer)this.attachable.getAttachment(ProxiedRequestAttachments.SERVER_PORT)));
                  buffer.put((byte)(AjpUtils.notNull((Boolean)this.attachable.getAttachment(ProxiedRequestAttachments.IS_SSL)) ? 1 : 0));
                  int headers = 0;
                  HeaderMap responseHeaders = this.headers;

                  Iterator var14;
                  HttpString header;
                  for(var14 = responseHeaders.getHeaderNames().iterator(); var14.hasNext(); headers += responseHeaders.get(header).size()) {
                     header = (HttpString)var14.next();
                  }

                  AjpUtils.putInt(buffer, headers);
                  var14 = responseHeaders.getHeaderNames().iterator();

                  String sslCert;
                  while(var14.hasNext()) {
                     header = (HttpString)var14.next();

                     for(Iterator var16 = responseHeaders.get(header).iterator(); var16.hasNext(); AjpUtils.putString(buffer, sslCert)) {
                        sslCert = (String)var16.next();
                        Integer headerCode = (Integer)AjpConstants.HEADER_MAP.get(header);
                        if (headerCode != null) {
                           AjpUtils.putInt(buffer, headerCode);
                        } else {
                           AjpUtils.putHttpString(buffer, header);
                        }
                     }
                  }

                  if (queryString != null) {
                     buffer.put((byte)5);
                     AjpUtils.putString(buffer, queryString);
                  }

                  String remoteUser = (String)this.attachable.getAttachment(ProxiedRequestAttachments.REMOTE_USER);
                  if (remoteUser != null) {
                     buffer.put((byte)3);
                     AjpUtils.putString(buffer, remoteUser);
                  }

                  String authType = (String)this.attachable.getAttachment(ProxiedRequestAttachments.AUTH_TYPE);
                  if (authType != null) {
                     buffer.put((byte)4);
                     AjpUtils.putString(buffer, authType);
                  }

                  String route = (String)this.attachable.getAttachment(ProxiedRequestAttachments.ROUTE);
                  if (route != null) {
                     buffer.put((byte)6);
                     AjpUtils.putString(buffer, route);
                  }

                  sslCert = (String)this.attachable.getAttachment(ProxiedRequestAttachments.SSL_CERT);
                  if (sslCert != null) {
                     buffer.put((byte)7);
                     AjpUtils.putString(buffer, sslCert);
                  }

                  String sslCypher = (String)this.attachable.getAttachment(ProxiedRequestAttachments.SSL_CYPHER);
                  if (sslCypher != null) {
                     buffer.put((byte)8);
                     AjpUtils.putString(buffer, sslCypher);
                  }

                  byte[] sslSession = (byte[])this.attachable.getAttachment(ProxiedRequestAttachments.SSL_SESSION_ID);
                  if (sslSession != null) {
                     buffer.put((byte)9);
                     AjpUtils.putString(buffer, HexConverter.convertToHexString(sslSession));
                  }

                  Integer sslKeySize = (Integer)this.attachable.getAttachment(ProxiedRequestAttachments.SSL_KEY_SIZE);
                  if (sslKeySize != null) {
                     buffer.put((byte)11);
                     AjpUtils.putString(buffer, sslKeySize.toString());
                  }

                  String secret = (String)this.attachable.getAttachment(ProxiedRequestAttachments.SECRET);
                  if (secret != null) {
                     buffer.put((byte)12);
                     AjpUtils.putString(buffer, secret);
                  }

                  if (storeMethod) {
                     buffer.put((byte)13);
                     AjpUtils.putString(buffer, this.method.toString());
                  }

                  buffer.put((byte)-1);
                  int dataLength = buffer.position() - 4;
                  buffer.put(2, (byte)(dataLength >> 8 & 255));
                  buffer.put(3, (byte)(dataLength & 255));
               }

               if (this.dataSize == 0L) {
                  buffer.flip();
                  return new SendFrameHeader(pooledHeaderBuffer);
               } else if (this.requestedChunkSize > 0) {
                  if (this.isWritesShutdown() && dataInBuffer == 0) {
                     buffer.put((byte)18);
                     buffer.put((byte)52);
                     buffer.put((byte)0);
                     buffer.put((byte)2);
                     buffer.put((byte)0);
                     buffer.put((byte)0);
                     buffer.flip();
                     return new SendFrameHeader(pooledHeaderBuffer);
                  } else {
                     int remaining = Math.min(dataInBuffer, maxData);
                     remaining = Math.min(remaining, this.requestedChunkSize);
                     int bodySize = remaining + 2;
                     buffer.put((byte)18);
                     buffer.put((byte)52);
                     buffer.put((byte)(bodySize >> 8 & 255));
                     buffer.put((byte)(bodySize & 255));
                     buffer.put((byte)(remaining >> 8 & 255));
                     buffer.put((byte)(remaining & 255));
                     this.requestedChunkSize = 0;
                     if (remaining < dataInBuffer) {
                        dataBuffer.limit(this.getBuffer().position() + remaining);
                        buffer.flip();
                        return new SendFrameHeader(dataInBuffer - remaining, pooledHeaderBuffer, this.dataSize < 0L);
                     } else {
                        buffer.flip();
                        return new SendFrameHeader(0, pooledHeaderBuffer, this.dataSize < 0L);
                     }
                  }
               } else {
                  buffer.flip();
                  if (buffer.remaining() == 0) {
                     pooledHeaderBuffer.close();
                     return new SendFrameHeader(dataInBuffer, (PooledByteBuffer)null, true);
                  } else {
                     dataBuffer.limit(dataBuffer.position());
                     return new SendFrameHeader(dataInBuffer, pooledHeaderBuffer, true);
                  }
               }
            }
         } catch (BufferOverflowException var23) {
            pooledHeaderBuffer.close();
            this.markBroken();
            throw var23;
         }
      }
   }

   SendFrameHeader generateSendFrameHeader() {
      this.header = this.createFrameHeaderImpl();
      return this.header;
   }

   void chunkRequested(int size) throws IOException {
      this.requestedChunkSize = size;
      ((AjpClientChannel)this.getChannel()).recalculateHeldFrames();
   }

   public void startDiscard() {
      this.discardMode = true;

      try {
         ((AjpClientChannel)this.getChannel()).recalculateHeldFrames();
      } catch (IOException var2) {
         this.markBroken();
      }

   }

   protected final SendFrameHeader createFrameHeader() {
      SendFrameHeader header = this.header;
      this.header = null;
      return header;
   }

   protected void handleFlushComplete(boolean finalFrame) {
      super.handleFlushComplete(finalFrame);
      if (finalFrame) {
         ((AjpClientChannel)this.getChannel()).sinkDone();
      }

      if (finalFrame && this.finishListener != null) {
         this.finishListener.handleEvent(this);
      }

   }

   protected void channelForciblyClosed() throws IOException {
      super.channelForciblyClosed();
      ((AjpClientChannel)this.getChannel()).sinkDone();
      this.finishListener.handleEvent(this);
   }

   public void clearHeader() {
      this.header = null;
   }
}
