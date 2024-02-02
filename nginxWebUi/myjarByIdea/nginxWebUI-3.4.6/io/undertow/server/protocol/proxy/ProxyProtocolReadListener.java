package io.undertow.server.protocol.proxy;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.protocols.ssl.UndertowXnioSsl;
import io.undertow.server.DelegateOpenListener;
import io.undertow.server.OpenListener;
import io.undertow.util.NetworkUtils;
import io.undertow.util.PooledAdaptor;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.PushBackStreamSourceConduit;
import org.xnio.ssl.SslConnection;

class ProxyProtocolReadListener implements ChannelListener<StreamSourceChannel> {
   private static final int MAX_HEADER_LENGTH = 107;
   private static final byte[] NAME;
   private static final String UNKNOWN = "UNKNOWN";
   private static final String TCP4 = "TCP4";
   private static final String TCP6 = "TCP6";
   private static final byte[] SIG;
   private final StreamConnection streamConnection;
   private final OpenListener openListener;
   private final UndertowXnioSsl ssl;
   private final ByteBufferPool bufferPool;
   private final OptionMap sslOptionMap;
   private int byteCount;
   private String protocol;
   private InetAddress sourceAddress;
   private InetAddress destAddress;
   private int sourcePort = -1;
   private int destPort = -1;
   private StringBuilder stringBuilder = new StringBuilder();
   private boolean carriageReturnSeen = false;
   private boolean parsingUnknown = false;

   ProxyProtocolReadListener(StreamConnection streamConnection, OpenListener openListener, UndertowXnioSsl ssl, ByteBufferPool bufferPool, OptionMap sslOptionMap) {
      this.streamConnection = streamConnection;
      this.openListener = openListener;
      this.ssl = ssl;
      this.bufferPool = bufferPool;
      this.sslOptionMap = sslOptionMap;
      if (bufferPool.getBufferSize() < 107) {
         throw UndertowMessages.MESSAGES.bufferPoolTooSmall(107);
      }
   }

   public void handleEvent(StreamSourceChannel streamSourceChannel) {
      PooledByteBuffer buffer = this.bufferPool.allocate();
      AtomicBoolean freeBuffer = new AtomicBoolean(true);

      try {
         int res = streamSourceChannel.read(buffer.getBuffer());
         if (res == -1) {
            IoUtils.safeClose((Closeable)this.streamConnection);
            return;
         }

         if (res != 0) {
            buffer.getBuffer().flip();
            if (buffer.getBuffer().hasRemaining()) {
               byte firstByte = buffer.getBuffer().get();
               ++this.byteCount;
               if (firstByte == SIG[0]) {
                  this.parseProxyProtocolV2(buffer, freeBuffer);
               } else {
                  if ((char)firstByte != NAME[0]) {
                     throw UndertowMessages.MESSAGES.invalidProxyHeader();
                  }

                  this.parseProxyProtocolV1(buffer, freeBuffer);
               }

               return;
            }

            return;
         }

         return;
      } catch (IOException var10) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var10);
         IoUtils.safeClose((Closeable)this.streamConnection);
      } catch (Exception var11) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(var11));
         IoUtils.safeClose((Closeable)this.streamConnection);
      } finally {
         if (freeBuffer.get()) {
            buffer.close();
         }

      }

   }

   private void parseProxyProtocolV2(PooledByteBuffer buffer, AtomicBoolean freeBuffer) throws Exception {
      int ver_cmd;
      while(this.byteCount < SIG.length) {
         ver_cmd = buffer.getBuffer().get();
         if (ver_cmd != SIG[this.byteCount]) {
            throw UndertowMessages.MESSAGES.invalidProxyHeader();
         }

         ++this.byteCount;
      }

      ver_cmd = buffer.getBuffer().get();
      byte fam = buffer.getBuffer().get();
      int len = buffer.getBuffer().getShort() & '\uffff';
      if ((ver_cmd & 240) != 32) {
         throw UndertowMessages.MESSAGES.invalidProxyHeader();
      } else {
         switch (ver_cmd & 15) {
            case 0:
               if (len > 0) {
                  int currentPosition = buffer.getBuffer().position();
                  buffer.getBuffer().position(currentPosition + len);
               }

               if (buffer.getBuffer().hasRemaining()) {
                  freeBuffer.set(false);
                  this.proxyAccept((SocketAddress)null, (SocketAddress)null, buffer);
               } else {
                  this.proxyAccept((SocketAddress)null, (SocketAddress)null, (PooledByteBuffer)null);
               }

               return;
            case 1:
               byte[] sourceAddressBytes;
               byte[] dstAddressBytes;
               int skipAhead;
               int currentPosition;
               switch (fam) {
                  case 17:
                     if (len < 12) {
                        throw UndertowMessages.MESSAGES.invalidProxyHeader();
                     }

                     sourceAddressBytes = new byte[4];
                     buffer.getBuffer().get(sourceAddressBytes);
                     this.sourceAddress = InetAddress.getByAddress(sourceAddressBytes);
                     dstAddressBytes = new byte[4];
                     buffer.getBuffer().get(dstAddressBytes);
                     this.destAddress = InetAddress.getByAddress(dstAddressBytes);
                     this.sourcePort = buffer.getBuffer().getShort() & '\uffff';
                     this.destPort = buffer.getBuffer().getShort() & '\uffff';
                     if (len > 12) {
                        skipAhead = len - 12;
                        currentPosition = buffer.getBuffer().position();
                        buffer.getBuffer().position(currentPosition + skipAhead);
                     }
                     break;
                  case 33:
                     if (len < 36) {
                        throw UndertowMessages.MESSAGES.invalidProxyHeader();
                     }

                     sourceAddressBytes = new byte[16];
                     buffer.getBuffer().get(sourceAddressBytes);
                     this.sourceAddress = InetAddress.getByAddress(sourceAddressBytes);
                     dstAddressBytes = new byte[16];
                     buffer.getBuffer().get(dstAddressBytes);
                     this.destAddress = InetAddress.getByAddress(dstAddressBytes);
                     this.sourcePort = buffer.getBuffer().getShort() & '\uffff';
                     this.destPort = buffer.getBuffer().getShort() & '\uffff';
                     if (len > 36) {
                        skipAhead = len - 36;
                        currentPosition = buffer.getBuffer().position();
                        buffer.getBuffer().position(currentPosition + skipAhead);
                     }
                     break;
                  default:
                     throw UndertowMessages.MESSAGES.invalidProxyHeader();
               }

               SocketAddress s = new InetSocketAddress(this.sourceAddress, this.sourcePort);
               SocketAddress d = new InetSocketAddress(this.destAddress, this.destPort);
               if (buffer.getBuffer().hasRemaining()) {
                  freeBuffer.set(false);
                  this.proxyAccept(s, d, buffer);
               } else {
                  this.proxyAccept(s, d, (PooledByteBuffer)null);
               }

               return;
            default:
               throw UndertowMessages.MESSAGES.invalidProxyHeader();
         }
      }
   }

   private void parseProxyProtocolV1(PooledByteBuffer buffer, AtomicBoolean freeBuffer) throws Exception {
      while(buffer.getBuffer().hasRemaining()) {
         char c = (char)buffer.getBuffer().get();
         if (this.byteCount < NAME.length) {
            if (c != NAME[this.byteCount]) {
               throw UndertowMessages.MESSAGES.invalidProxyHeader();
            }
         } else if (this.parsingUnknown) {
            if (c == '\r') {
               this.carriageReturnSeen = true;
            } else {
               if (c == '\n') {
                  if (!this.carriageReturnSeen) {
                     throw UndertowMessages.MESSAGES.invalidProxyHeader();
                  }

                  if (buffer.getBuffer().hasRemaining()) {
                     freeBuffer.set(false);
                     this.proxyAccept((SocketAddress)null, (SocketAddress)null, buffer);
                  } else {
                     this.proxyAccept((SocketAddress)null, (SocketAddress)null, (PooledByteBuffer)null);
                  }

                  return;
               }

               if (this.carriageReturnSeen) {
                  throw UndertowMessages.MESSAGES.invalidProxyHeader();
               }
            }
         } else {
            if (this.carriageReturnSeen) {
               if (c == '\n') {
                  SocketAddress s = new InetSocketAddress(this.sourceAddress, this.sourcePort);
                  SocketAddress d = new InetSocketAddress(this.destAddress, this.destPort);
                  if (buffer.getBuffer().hasRemaining()) {
                     freeBuffer.set(false);
                     this.proxyAccept(s, d, buffer);
                  } else {
                     this.proxyAccept(s, d, (PooledByteBuffer)null);
                  }

                  return;
               }

               throw UndertowMessages.MESSAGES.invalidProxyHeader();
            }

            switch (c) {
               case '\n':
                  throw UndertowMessages.MESSAGES.invalidProxyHeader();
               case '\r':
                  if (this.destPort == -1 && this.sourcePort != -1 && !this.carriageReturnSeen && this.stringBuilder.length() > 0) {
                     this.destPort = Integer.parseInt(this.stringBuilder.toString());
                     this.stringBuilder.setLength(0);
                     this.carriageReturnSeen = true;
                  } else {
                     if (this.protocol != null) {
                        throw UndertowMessages.MESSAGES.invalidProxyHeader();
                     }

                     if ("UNKNOWN".equals(this.stringBuilder.toString())) {
                        this.parsingUnknown = true;
                        this.carriageReturnSeen = true;
                     }
                  }
                  break;
               case ' ':
                  if (this.sourcePort == -1 && this.stringBuilder.length() != 0) {
                     if (this.protocol == null) {
                        this.protocol = this.stringBuilder.toString();
                        this.stringBuilder.setLength(0);
                        if (this.protocol.equals("UNKNOWN")) {
                           this.parsingUnknown = true;
                        } else if (!this.protocol.equals("TCP4") && !this.protocol.equals("TCP6")) {
                           throw UndertowMessages.MESSAGES.invalidProxyHeader();
                        }
                     } else if (this.sourceAddress == null) {
                        this.sourceAddress = parseAddress(this.stringBuilder.toString(), this.protocol);
                        this.stringBuilder.setLength(0);
                     } else if (this.destAddress == null) {
                        this.destAddress = parseAddress(this.stringBuilder.toString(), this.protocol);
                        this.stringBuilder.setLength(0);
                     } else {
                        this.sourcePort = Integer.parseInt(this.stringBuilder.toString());
                        this.stringBuilder.setLength(0);
                     }
                     break;
                  }

                  throw UndertowMessages.MESSAGES.invalidProxyHeader();
               default:
                  this.stringBuilder.append(c);
            }
         }

         ++this.byteCount;
         if (this.byteCount == 107) {
            throw UndertowMessages.MESSAGES.headerSizeToLarge();
         }
      }

   }

   private void proxyAccept(SocketAddress source, SocketAddress dest, PooledByteBuffer additionalData) {
      StreamConnection streamConnection = this.streamConnection;
      if (source != null) {
         streamConnection = new AddressWrappedConnection((StreamConnection)streamConnection, source, dest);
      }

      if (this.ssl != null) {
         if (additionalData != null) {
            PushBackStreamSourceConduit conduit = new PushBackStreamSourceConduit(((StreamConnection)streamConnection).getSourceChannel().getConduit());
            conduit.pushBack(new PooledAdaptor(additionalData));
            ((StreamConnection)streamConnection).getSourceChannel().setConduit(conduit);
         }

         SslConnection sslConnection = this.ssl.wrapExistingConnection((StreamConnection)streamConnection, this.sslOptionMap == null ? OptionMap.EMPTY : this.sslOptionMap, false);
         this.callOpenListener(sslConnection, (PooledByteBuffer)null);
      } else {
         this.callOpenListener((StreamConnection)streamConnection, additionalData);
      }

   }

   private void callOpenListener(StreamConnection streamConnection, PooledByteBuffer buffer) {
      if (this.openListener instanceof DelegateOpenListener) {
         ((DelegateOpenListener)this.openListener).handleEvent(streamConnection, buffer);
      } else {
         if (buffer != null) {
            PushBackStreamSourceConduit conduit = new PushBackStreamSourceConduit(streamConnection.getSourceChannel().getConduit());
            conduit.pushBack(new PooledAdaptor(buffer));
            streamConnection.getSourceChannel().setConduit(conduit);
         }

         this.openListener.handleEvent(streamConnection);
      }

   }

   static InetAddress parseAddress(String addressString, String protocol) throws IOException {
      return protocol.equals("TCP4") ? NetworkUtils.parseIpv4Address(addressString) : NetworkUtils.parseIpv6Address(addressString);
   }

   static {
      NAME = "PROXY ".getBytes(StandardCharsets.US_ASCII);
      SIG = new byte[]{13, 10, 13, 10, 0, 13, 10, 81, 85, 73, 84, 10};
   }

   private static final class AddressWrappedConnection extends StreamConnection {
      private final StreamConnection delegate;
      private final SocketAddress source;
      private final SocketAddress dest;

      AddressWrappedConnection(StreamConnection delegate, SocketAddress source, SocketAddress dest) {
         super(delegate.getIoThread());
         this.delegate = delegate;
         this.source = source;
         this.dest = dest;
         this.setSinkConduit(delegate.getSinkChannel().getConduit());
         this.setSourceConduit(delegate.getSourceChannel().getConduit());
      }

      protected void notifyWriteClosed() {
         IoUtils.safeClose((Closeable)this.delegate.getSinkChannel());
      }

      protected void notifyReadClosed() {
         IoUtils.safeClose((Closeable)this.delegate.getSourceChannel());
      }

      public SocketAddress getPeerAddress() {
         return this.source;
      }

      public SocketAddress getLocalAddress() {
         return this.dest;
      }
   }
}
