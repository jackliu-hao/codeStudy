package org.xnio.http;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Pooled;
import org.xnio.StreamConnection;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;
import org.xnio.channels.BoundChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.PushBackStreamSourceConduit;
import org.xnio.conduits.StreamSourceConduit;
import org.xnio.ssl.SslConnection;
import org.xnio.ssl.XnioSsl;

public class HttpUpgrade {
   public static IoFuture<SslConnection> performUpgrade(XnioWorker worker, XnioSsl ssl, InetSocketAddress bindAddress, URI uri, Map<String, String> headers, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, HandshakeChecker handshakeChecker) {
      return (new HttpUpgradeState(worker, ssl, bindAddress, uri, headers, openListener, bindListener, optionMap, handshakeChecker)).doUpgrade();
   }

   public static IoFuture<SslConnection> performUpgrade(XnioWorker worker, XnioSsl ssl, InetSocketAddress bindAddress, URI uri, Map<String, List<String>> headers, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, ExtendedHandshakeChecker handshakeChecker) {
      return (new HttpUpgradeState(worker, ssl, bindAddress, uri, headers, openListener, bindListener, optionMap, handshakeChecker)).doUpgrade();
   }

   public static IoFuture<StreamConnection> performUpgrade(XnioWorker worker, InetSocketAddress bindAddress, URI uri, Map<String, String> headers, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, HandshakeChecker handshakeChecker) {
      return (new HttpUpgradeState(worker, (XnioSsl)null, bindAddress, uri, headers, openListener, bindListener, optionMap, handshakeChecker)).doUpgrade();
   }

   public static IoFuture<StreamConnection> performUpgrade(XnioWorker worker, InetSocketAddress bindAddress, URI uri, Map<String, List<String>> headers, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, ExtendedHandshakeChecker handshakeChecker) {
      return (new HttpUpgradeState(worker, (XnioSsl)null, bindAddress, uri, headers, openListener, bindListener, optionMap, handshakeChecker)).doUpgrade();
   }

   public static <T extends StreamConnection> IoFuture<T> performUpgrade(T connection, URI uri, Map<String, String> headers, ChannelListener<? super StreamConnection> openListener, HandshakeChecker handshakeChecker) {
      return (new HttpUpgradeState(connection, uri, headers, openListener, handshakeChecker)).upgradeExistingConnection();
   }

   public static <T extends StreamConnection> IoFuture<T> performUpgrade(T connection, URI uri, Map<String, List<String>> headers, ChannelListener<? super StreamConnection> openListener, ExtendedHandshakeChecker handshakeChecker) {
      return (new HttpUpgradeState(connection, uri, headers, openListener, handshakeChecker)).upgradeExistingConnection();
   }

   private HttpUpgrade() {
   }

   private static class HttpUpgradeState<T extends StreamConnection> {
      private final XnioWorker worker;
      private final XnioSsl ssl;
      private final InetSocketAddress bindAddress;
      private final URI uri;
      private final Map<String, List<String>> headers;
      private final ChannelListener<? super T> openListener;
      private final ChannelListener<? super BoundChannel> bindListener;
      private final OptionMap optionMap;
      private final Object handshakeChecker;
      private final FutureResult<T> future;
      private T connection;

      private HttpUpgradeState(XnioWorker worker, XnioSsl ssl, InetSocketAddress bindAddress, URI uri, Map<String, String> headers, ChannelListener<? super T> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, HandshakeChecker handshakeChecker) {
         this.future = new FutureResult();
         this.worker = worker;
         this.ssl = ssl;
         this.bindAddress = bindAddress;
         this.uri = uri;
         this.openListener = openListener;
         this.bindListener = bindListener;
         this.optionMap = optionMap;
         this.handshakeChecker = handshakeChecker;
         Map<String, List<String>> newHeaders = new HashMap();
         Iterator var11 = headers.entrySet().iterator();

         while(var11.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var11.next();
            newHeaders.put(entry.getKey(), Collections.singletonList(entry.getValue()));
         }

         this.headers = newHeaders;
      }

      private HttpUpgradeState(XnioWorker worker, XnioSsl ssl, InetSocketAddress bindAddress, URI uri, Map<String, List<String>> headers, ChannelListener<? super T> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap, ExtendedHandshakeChecker handshakeChecker) {
         this.future = new FutureResult();
         this.worker = worker;
         this.ssl = ssl;
         this.bindAddress = bindAddress;
         this.uri = uri;
         this.headers = headers;
         this.openListener = openListener;
         this.bindListener = bindListener;
         this.optionMap = optionMap;
         this.handshakeChecker = handshakeChecker;
      }

      public HttpUpgradeState(T connection, URI uri, Map<String, String> headers, ChannelListener<? super StreamConnection> openListener, HandshakeChecker handshakeChecker) {
         this.future = new FutureResult();
         this.worker = connection.getWorker();
         this.ssl = null;
         this.bindAddress = null;
         this.uri = uri;
         this.openListener = openListener;
         this.bindListener = null;
         this.optionMap = OptionMap.EMPTY;
         this.handshakeChecker = handshakeChecker;
         this.connection = connection;
         Map<String, List<String>> newHeaders = new HashMap();
         Iterator var7 = headers.entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var7.next();
            newHeaders.put(entry.getKey(), Collections.singletonList(entry.getValue()));
         }

         this.headers = newHeaders;
      }

      public HttpUpgradeState(T connection, URI uri, Map<String, List<String>> headers, ChannelListener<? super StreamConnection> openListener, ExtendedHandshakeChecker handshakeChecker) {
         this.future = new FutureResult();
         this.worker = connection.getWorker();
         this.ssl = null;
         this.bindAddress = null;
         this.uri = uri;
         this.headers = headers;
         this.openListener = openListener;
         this.bindListener = null;
         this.optionMap = OptionMap.EMPTY;
         this.handshakeChecker = handshakeChecker;
         this.connection = connection;
      }

      private IoFuture<T> doUpgrade() {
         InetSocketAddress address = new InetSocketAddress(this.uri.getHost(), this.uri.getPort());
         ChannelListener<StreamConnection> connectListener = new ConnectionOpenListener();
         String scheme = this.uri.getScheme();
         if (scheme.equals("http")) {
            if (this.bindAddress == null) {
               this.worker.openStreamConnection(address, connectListener, this.bindListener, this.optionMap).addNotifier(new FailureNotifier(), (Object)null);
            } else {
               this.worker.openStreamConnection(this.bindAddress, address, connectListener, this.bindListener, this.optionMap).addNotifier(new FailureNotifier(), (Object)null);
            }
         } else {
            if (!scheme.equals("https")) {
               throw Messages.msg.invalidURLScheme(scheme);
            }

            if (this.ssl == null) {
               throw Messages.msg.missingSslProvider();
            }

            if (this.bindAddress == null) {
               this.ssl.openSslConnection((XnioWorker)this.worker, address, (ChannelListener)connectListener, this.bindListener, this.optionMap).addNotifier(new FailureNotifier(), (Object)null);
            } else {
               this.ssl.openSslConnection((XnioWorker)this.worker, this.bindAddress, address, connectListener, this.bindListener, this.optionMap).addNotifier(new FailureNotifier(), (Object)null);
            }
         }

         return this.future.getIoFuture();
      }

      private String buildHttpRequest() {
         StringBuilder builder = new StringBuilder();
         builder.append("GET ");
         builder.append(this.uri.getPath().isEmpty() ? "/" : this.uri.getPath());
         if (this.uri.getQuery() != null && !this.uri.getQuery().isEmpty()) {
            builder.append('?');
            builder.append(this.uri.getQuery());
         }

         builder.append(" HTTP/1.1\r\n");
         Set<String> seen = new HashSet();
         Iterator var3 = this.headers.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<String, List<String>> headerEntry = (Map.Entry)var3.next();
            Iterator var5 = ((List)headerEntry.getValue()).iterator();

            while(var5.hasNext()) {
               String value = (String)var5.next();
               builder.append((String)headerEntry.getKey());
               builder.append(": ");
               builder.append(value);
               builder.append("\r\n");
               seen.add(((String)headerEntry.getKey()).toLowerCase(Locale.ENGLISH));
            }
         }

         if (!seen.contains("host")) {
            builder.append("Host: ");
            builder.append(this.getHost());
            builder.append("\r\n");
         }

         if (!seen.contains("connection")) {
            builder.append("Connection: upgrade\r\n");
         }

         if (!seen.contains("upgrade")) {
            throw new IllegalArgumentException("Upgrade: header was not supplied in header arguments");
         } else {
            builder.append("\r\n");
            return builder.toString();
         }
      }

      private String getHost() {
         String scheme = this.uri.getScheme();
         int port = this.uri.getPort();
         return port >= 0 && (!"http".equals(scheme) || port != 80) && (!"https".equals(scheme) || port != 443) ? this.uri.getHost() + ":" + port : this.uri.getHost();
      }

      public IoFuture<T> upgradeExistingConnection() {
         ChannelListener<StreamConnection> connectListener = new ConnectionOpenListener();
         connectListener.handleEvent(this.connection);
         return this.future.getIoFuture();
      }

      private void flushUpgradeChannel() {
         try {
            if (!this.connection.getSinkChannel().flush()) {
               this.connection.getSinkChannel().getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>() {
                  public void handleEvent(StreamSinkChannel channel) {
                     channel.suspendWrites();
                     (HttpUpgradeState.this.new UpgradeResultListener()).handleEvent((StreamSourceChannel)HttpUpgradeState.this.connection.getSourceChannel());
                  }
               }, new ChannelExceptionHandler<StreamSinkChannel>() {
                  public void handleException(StreamSinkChannel channel, IOException exception) {
                     IoUtils.safeClose((Closeable)channel);
                     HttpUpgradeState.this.future.setException(exception);
                  }
               }));
               this.connection.getSinkChannel().resumeWrites();
               return;
            }
         } catch (IOException var2) {
            IoUtils.safeClose((Closeable)this.connection);
            this.future.setException(var2);
            return;
         }

         (new UpgradeResultListener()).handleEvent((StreamSourceChannel)this.connection.getSourceChannel());
      }

      private void handleUpgrade(HttpUpgradeParser parser) {
         Map<String, String> simpleHeaders = new HashMap();
         Iterator var3 = parser.getHeaders().entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<String, List<String>> e = (Map.Entry)var3.next();
            simpleHeaders.put(e.getKey(), ((List)e.getValue()).get(0));
         }

         String contentLength = (String)simpleHeaders.get("content-length");
         if (contentLength != null && !"0".equals(contentLength)) {
            this.future.setException(new IOException("Upgrade responses must have a content length of zero."));
         } else {
            String transferCoding = (String)simpleHeaders.get("transfer-encoding");
            if (transferCoding != null) {
               this.future.setException(new IOException("Upgrade responses cannot have a transfer coding"));
            } else {
               if (this.handshakeChecker != null) {
                  try {
                     if (this.handshakeChecker instanceof ExtendedHandshakeChecker) {
                        ((ExtendedHandshakeChecker)this.handshakeChecker).checkHandshakeExtended(parser.getHeaders());
                     } else {
                        ((HandshakeChecker)this.handshakeChecker).checkHandshake(simpleHeaders);
                     }
                  } catch (IOException var6) {
                     IoUtils.safeClose((Closeable)this.connection);
                     this.future.setException(var6);
                     return;
                  }
               }

               this.future.setResult(this.connection);
               ChannelListeners.invokeChannelListener(this.connection, this.openListener);
            }
         }
      }

      private void handleRedirect(HttpUpgradeParser parser) {
         List<String> location = (List)parser.getHeaders().get("location");
         this.future.setException(new RedirectException(Messages.msg.redirect(), parser.getResponseCode(), location == null ? null : (String)location.get(0)));
      }

      // $FF: synthetic method
      HttpUpgradeState(XnioWorker x0, XnioSsl x1, InetSocketAddress x2, URI x3, Map x4, ChannelListener x5, ChannelListener x6, OptionMap x7, HandshakeChecker x8, Object x9) {
         this(x0, x1, x2, x3, x4, x5, x6, x7, x8);
      }

      // $FF: synthetic method
      HttpUpgradeState(XnioWorker x0, XnioSsl x1, InetSocketAddress x2, URI x3, Map x4, ChannelListener x5, ChannelListener x6, OptionMap x7, ExtendedHandshakeChecker x8, Object x9) {
         this(x0, x1, x2, x3, x4, x5, x6, x7, x8);
      }

      private class FailureNotifier extends IoFuture.HandlingNotifier<StreamConnection, Object> {
         private FailureNotifier() {
         }

         public void handleFailed(IOException exception, Object attachment) {
            HttpUpgradeState.this.future.setException(exception);
         }

         public void handleCancelled(Object attachment) {
            HttpUpgradeState.this.future.setCancelled();
         }

         // $FF: synthetic method
         FailureNotifier(Object x1) {
            this();
         }
      }

      private final class UpgradeResultListener implements ChannelListener<StreamSourceChannel> {
         private final HttpUpgradeParser parser;
         private ByteBuffer buffer;

         private UpgradeResultListener() {
            this.parser = new HttpUpgradeParser();
            this.buffer = ByteBuffer.allocate(1024);
         }

         public void handleEvent(StreamSourceChannel channel) {
            do {
               try {
                  int r = channel.read(this.buffer);
                  if (r == 0) {
                     channel.getReadSetter().set(this);
                     channel.resumeReads();
                     return;
                  }

                  if (r == -1) {
                     throw new ConnectionClosedEarlyException(Messages.msg.connectionClosedEarly().getMessage());
                  }

                  this.buffer.flip();
                  this.parser.parse(this.buffer);
                  if (!this.parser.isComplete()) {
                     this.buffer.compact();
                  }
               } catch (IOException var5) {
                  IoUtils.safeClose((Closeable)channel);
                  HttpUpgradeState.this.future.setException(var5);
                  return;
               }
            } while(!this.parser.isComplete());

            channel.suspendReads();
            if (this.buffer.hasRemaining()) {
               StreamSourceConduit orig = HttpUpgradeState.this.connection.getSourceChannel().getConduit();
               PushBackStreamSourceConduit pushBack = new PushBackStreamSourceConduit(orig);
               pushBack.pushBack(new Pooled<ByteBuffer>() {
                  public void discard() {
                     UpgradeResultListener.this.buffer = null;
                  }

                  public void free() {
                     UpgradeResultListener.this.buffer = null;
                  }

                  public ByteBuffer getResource() throws IllegalStateException {
                     return UpgradeResultListener.this.buffer;
                  }

                  public void close() {
                     this.free();
                  }
               });
               HttpUpgradeState.this.connection.getSourceChannel().setConduit(pushBack);
            }

            if (this.parser.getResponseCode() == 101) {
               HttpUpgradeState.this.handleUpgrade(this.parser);
            } else if (this.parser.getResponseCode() != 301 && this.parser.getResponseCode() != 302 && this.parser.getResponseCode() != 303 && this.parser.getResponseCode() != 307 && this.parser.getResponseCode() != 308) {
               IoUtils.safeClose((Closeable)HttpUpgradeState.this.connection);
               HttpUpgradeState.this.future.setException(new UpgradeFailedException("Invalid response code " + this.parser.getResponseCode()));
            } else {
               IoUtils.safeClose((Closeable)HttpUpgradeState.this.connection);
               HttpUpgradeState.this.handleRedirect(this.parser);
            }

         }

         // $FF: synthetic method
         UpgradeResultListener(Object x1) {
            this();
         }
      }

      private final class StringWriteListener implements ChannelListener<StreamSinkChannel> {
         final ByteBuffer buffer;

         private StringWriteListener(ByteBuffer buffer) {
            this.buffer = buffer;
         }

         public void handleEvent(StreamSinkChannel channel) {
            do {
               try {
                  int r = channel.write(this.buffer);
                  if (r == 0) {
                     return;
                  }
               } catch (IOException var4) {
                  IoUtils.safeClose((Closeable)channel);
                  HttpUpgradeState.this.future.setException(var4);
                  return;
               }
            } while(this.buffer.hasRemaining());

            channel.suspendWrites();
            HttpUpgradeState.this.flushUpgradeChannel();
         }

         // $FF: synthetic method
         StringWriteListener(ByteBuffer x1, Object x2) {
            this(x1);
         }
      }

      private class ConnectionOpenListener implements ChannelListener<StreamConnection> {
         private ConnectionOpenListener() {
         }

         public void handleEvent(StreamConnection channel) {
            HttpUpgradeState.this.connection = channel;
            ByteBuffer buffer = ByteBuffer.wrap(HttpUpgradeState.this.buildHttpRequest().getBytes());

            do {
               try {
                  int r = channel.getSinkChannel().write(buffer);
                  if (r == 0) {
                     channel.getSinkChannel().getWriteSetter().set(HttpUpgradeState.this.new StringWriteListener(buffer));
                     channel.getSinkChannel().resumeWrites();
                     return;
                  }
               } catch (IOException var5) {
                  IoUtils.safeClose((Closeable)channel);
                  HttpUpgradeState.this.future.setException(var5);
                  return;
               }
            } while(buffer.hasRemaining());

            HttpUpgradeState.this.flushUpgradeChannel();
         }

         // $FF: synthetic method
         ConnectionOpenListener(Object x1) {
            this();
         }
      }
   }
}
