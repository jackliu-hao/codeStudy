package io.undertow.websockets.client;

import io.undertow.UndertowMessages;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.UndertowClient;
import io.undertow.connector.ByteBufferPool;
import io.undertow.protocols.ssl.UndertowXnioSsl;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.Protocols;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSocketLogger;
import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.extensions.ExtensionHandshake;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xnio.Cancellable;
import org.xnio.ChannelListener;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.XnioWorker;
import org.xnio.http.ExtendedHandshakeChecker;
import org.xnio.http.HttpUpgrade;
import org.xnio.http.RedirectException;
import org.xnio.ssl.XnioSsl;

public class WebSocketClient {
   public static final String BIND_PROPERTY = "io.undertow.websockets.BIND_ADDRESS";
   private static final int MAX_REDIRECTS = Integer.getInteger("io.undertow.websockets.max-redirects", 5);

   /** @deprecated */
   @Deprecated
   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version) {
      return connect(worker, (ByteBufferPool)bufferPool, (OptionMap)optionMap, (URI)uri, (WebSocketVersion)version, (WebSocketClientNegotiation)null);
   }

   /** @deprecated */
   @Deprecated
   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version) {
      return connect(worker, ssl, bufferPool, optionMap, uri, version, (WebSocketClientNegotiation)null);
   }

   /** @deprecated */
   @Deprecated
   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version, WebSocketClientNegotiation clientNegotiation) {
      return connect(worker, (XnioSsl)null, bufferPool, optionMap, uri, version, clientNegotiation);
   }

   /** @deprecated */
   @Deprecated
   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version, WebSocketClientNegotiation clientNegotiation) {
      return connect(worker, ssl, bufferPool, optionMap, uri, version, clientNegotiation, (Set)null);
   }

   /** @deprecated */
   @Deprecated
   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap optionMap, URI uri, WebSocketVersion version, WebSocketClientNegotiation clientNegotiation, Set<ExtensionHandshake> clientExtensions) {
      return connect(worker, ssl, bufferPool, optionMap, (InetSocketAddress)null, uri, version, clientNegotiation, clientExtensions);
   }

   /** @deprecated */
   @Deprecated
   public static IoFuture<WebSocketChannel> connect(XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap optionMap, InetSocketAddress bindAddress, URI uri, WebSocketVersion version, WebSocketClientNegotiation clientNegotiation, Set<ExtensionHandshake> clientExtensions) {
      return connectionBuilder(worker, bufferPool, uri).setSsl(ssl).setOptionMap(optionMap).setBindAddress(bindAddress).setVersion(version).setClientNegotiation(clientNegotiation).setClientExtensions(clientExtensions).connect();
   }

   public static ConnectionBuilder connectionBuilder(XnioWorker worker, ByteBufferPool bufferPool, URI uri) {
      return new ConnectionBuilder(worker, bufferPool, uri);
   }

   private WebSocketClient() {
   }

   public static class ConnectionBuilder {
      private final XnioWorker worker;
      private final ByteBufferPool bufferPool;
      private final URI uri;
      private XnioSsl ssl;
      private OptionMap optionMap;
      private InetSocketAddress bindAddress;
      private WebSocketVersion version;
      private WebSocketClientNegotiation clientNegotiation;
      private Set<ExtensionHandshake> clientExtensions;
      private URI proxyUri;
      private XnioSsl proxySsl;

      public ConnectionBuilder(XnioWorker worker, ByteBufferPool bufferPool, URI uri) {
         this.optionMap = OptionMap.EMPTY;
         this.version = WebSocketVersion.V13;
         this.worker = worker;
         this.bufferPool = bufferPool;
         this.uri = uri;
      }

      public XnioWorker getWorker() {
         return this.worker;
      }

      public URI getUri() {
         return this.uri;
      }

      public XnioSsl getSsl() {
         return this.ssl;
      }

      public ConnectionBuilder setSsl(XnioSsl ssl) {
         this.ssl = ssl;
         return this;
      }

      public ByteBufferPool getBufferPool() {
         return this.bufferPool;
      }

      public OptionMap getOptionMap() {
         return this.optionMap;
      }

      public ConnectionBuilder setOptionMap(OptionMap optionMap) {
         this.optionMap = optionMap;
         return this;
      }

      public InetSocketAddress getBindAddress() {
         return this.bindAddress;
      }

      public ConnectionBuilder setBindAddress(InetSocketAddress bindAddress) {
         this.bindAddress = bindAddress;
         return this;
      }

      public WebSocketVersion getVersion() {
         return this.version;
      }

      public ConnectionBuilder setVersion(WebSocketVersion version) {
         this.version = version;
         return this;
      }

      public WebSocketClientNegotiation getClientNegotiation() {
         return this.clientNegotiation;
      }

      public ConnectionBuilder setClientNegotiation(WebSocketClientNegotiation clientNegotiation) {
         this.clientNegotiation = clientNegotiation;
         return this;
      }

      public Set<ExtensionHandshake> getClientExtensions() {
         return this.clientExtensions;
      }

      public ConnectionBuilder setClientExtensions(Set<ExtensionHandshake> clientExtensions) {
         this.clientExtensions = clientExtensions;
         return this;
      }

      public URI getProxyUri() {
         return this.proxyUri;
      }

      public ConnectionBuilder setProxyUri(URI proxyUri) {
         this.proxyUri = proxyUri;
         return this;
      }

      public XnioSsl getProxySsl() {
         return this.proxySsl;
      }

      public ConnectionBuilder setProxySsl(XnioSsl proxySsl) {
         this.proxySsl = proxySsl;
         return this;
      }

      public IoFuture<WebSocketChannel> connect() {
         return this.connectImpl(this.uri, new FutureResult(), 0);
      }

      private IoFuture<WebSocketChannel> connectImpl(final URI uri, final FutureResult<WebSocketChannel> ioFuture, final int redirectCount) {
         WebSocketLogger.REQUEST_LOGGER.debugf("Opening websocket connection to %s", uri);
         String scheme = this.isSecure(uri) ? "https" : "http";

         final URI newUri;
         try {
            newUri = new URI(scheme, uri.getUserInfo(), uri.getHost(), uri.getPort() == -1 ? (this.isSecure(uri) ? 443 : 80) : uri.getPort(), uri.getPath().isEmpty() ? "/" : uri.getPath(), uri.getQuery(), uri.getFragment());
         } catch (URISyntaxException var12) {
            throw new RuntimeException(var12);
         }

         final WebSocketClientHandshake handshake = WebSocketClientHandshake.create(this.version, newUri, this.clientNegotiation, this.clientExtensions);
         Map<String, String> originalHeaders = handshake.createHeaders();
         originalHeaders.put("Host", uri.getHost() + ":" + newUri.getPort());
         final Map<String, List<String>> headers = new HashMap();
         Iterator var9 = originalHeaders.entrySet().iterator();

         while(var9.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var9.next();
            List<String> list = new ArrayList();
            list.add(entry.getValue());
            headers.put(entry.getKey(), list);
         }

         if (this.clientNegotiation != null) {
            this.clientNegotiation.beforeRequest(headers);
         }

         InetSocketAddress toBind = this.bindAddress;
         String sysBind = System.getProperty("io.undertow.websockets.BIND_ADDRESS");
         if (toBind == null && sysBind != null) {
            toBind = new InetSocketAddress(sysBind, 0);
         }

         if (this.proxyUri != null) {
            UndertowClient.getInstance().connect(new ClientCallback<ClientConnection>() {
               public void completed(final ClientConnection connection) {
                  int port = uri.getPort() > 0 ? uri.getPort() : (ConnectionBuilder.this.isSecure(uri) ? 443 : 80);
                  ClientRequest cr = (new ClientRequest()).setMethod(Methods.CONNECT).setPath(uri.getHost() + ":" + port).setProtocol(Protocols.HTTP_1_1);
                  cr.getRequestHeaders().put(Headers.HOST, ConnectionBuilder.this.proxyUri.getHost() + ":" + (ConnectionBuilder.this.proxyUri.getPort() > 0 ? ConnectionBuilder.this.proxyUri.getPort() : 80));
                  connection.sendRequest(cr, new ClientCallback<ClientExchange>() {
                     public void completed(ClientExchange result) {
                        result.setResponseListener(new ClientCallback<ClientExchange>() {
                           public void completed(ClientExchange response) {
                              try {
                                 if (response.getResponse().getResponseCode() == 200) {
                                    try {
                                       StreamConnection targetConnection = connection.performUpgrade();
                                       WebSocketLogger.REQUEST_LOGGER.debugf("Established websocket connection to %s", uri);
                                       if (ConnectionBuilder.this.isSecure(uri)) {
                                          this.handleConnectionWithExistingConnection(((UndertowXnioSsl)ConnectionBuilder.this.ssl).wrapExistingConnection(targetConnection, ConnectionBuilder.this.optionMap, uri));
                                       } else {
                                          this.handleConnectionWithExistingConnection(targetConnection);
                                       }
                                    } catch (IOException var3) {
                                       ioFuture.setException(var3);
                                    } catch (Exception var4) {
                                       ioFuture.setException(new IOException(var4));
                                    }
                                 } else {
                                    ioFuture.setException(UndertowMessages.MESSAGES.proxyConnectionFailed(response.getResponse().getResponseCode()));
                                 }
                              } catch (Exception var5) {
                                 ioFuture.setException(new IOException(var5));
                              }

                           }

                           private void handleConnectionWithExistingConnection(StreamConnection targetConnection) {
                              final IoFuture<?> result = HttpUpgrade.performUpgrade(targetConnection, newUri, headers, ConnectionBuilder.this.new WebsocketConnectionListener(ConnectionBuilder.this.optionMap, handshake, newUri, ioFuture), (ExtendedHandshakeChecker)handshake.handshakeChecker(newUri, headers));
                              result.addNotifier(new IoFuture.Notifier<Object, Object>() {
                                 public void notify(IoFuture<?> res, Object attachment) {
                                    if (res.getStatus() == IoFuture.Status.FAILED) {
                                       ioFuture.setException(res.getException());
                                    }

                                 }
                              }, (Object)null);
                              ioFuture.addCancelHandler(new Cancellable() {
                                 public Cancellable cancel() {
                                    result.cancel();
                                    return null;
                                 }
                              });
                           }

                           public void failed(IOException e) {
                              ioFuture.setException(e);
                           }
                        });
                     }

                     public void failed(IOException e) {
                        ioFuture.setException(e);
                     }
                  });
               }

               public void failed(IOException e) {
                  ioFuture.setException(e);
               }
            }, this.bindAddress, this.proxyUri, this.worker, this.proxySsl, this.bufferPool, this.optionMap);
         } else {
            final IoFuture result;
            if (this.ssl != null) {
               result = HttpUpgrade.performUpgrade(this.worker, this.ssl, toBind, newUri, headers, new WebsocketConnectionListener(this.optionMap, handshake, newUri, ioFuture), (ChannelListener)null, this.optionMap, (ExtendedHandshakeChecker)handshake.handshakeChecker(newUri, headers));
            } else {
               result = HttpUpgrade.performUpgrade(this.worker, toBind, newUri, headers, new WebsocketConnectionListener(this.optionMap, handshake, newUri, ioFuture), (ChannelListener)null, this.optionMap, (ExtendedHandshakeChecker)handshake.handshakeChecker(newUri, headers));
            }

            result.addNotifier(new IoFuture.Notifier<Object, Object>() {
               public void notify(IoFuture<?> res, Object attachment) {
                  if (res.getStatus() == IoFuture.Status.FAILED) {
                     IOException exception = res.getException();
                     if (exception instanceof RedirectException) {
                        if (redirectCount == WebSocketClient.MAX_REDIRECTS) {
                           ioFuture.setException(UndertowMessages.MESSAGES.tooManyRedirects(exception));
                        } else {
                           String path = ((RedirectException)exception).getLocation();

                           try {
                              ConnectionBuilder.this.connectImpl(new URI(path), ioFuture, redirectCount + 1);
                           } catch (URISyntaxException var6) {
                              ioFuture.setException(new IOException(var6));
                           }
                        }
                     } else {
                        ioFuture.setException(exception);
                     }
                  }

               }
            }, (Object)null);
            ioFuture.addCancelHandler(new Cancellable() {
               public Cancellable cancel() {
                  result.cancel();
                  return null;
               }
            });
         }

         return ioFuture.getIoFuture();
      }

      private boolean isSecure(URI uri) {
         return uri.getScheme().equals("wss") || uri.getScheme().equals("https");
      }

      private class WebsocketConnectionListener implements ChannelListener<StreamConnection> {
         private final OptionMap options;
         private final WebSocketClientHandshake handshake;
         private final URI newUri;
         private final FutureResult<WebSocketChannel> ioFuture;

         WebsocketConnectionListener(OptionMap options, WebSocketClientHandshake handshake, URI newUri, FutureResult<WebSocketChannel> ioFuture) {
            this.options = options;
            this.handshake = handshake;
            this.newUri = newUri;
            this.ioFuture = ioFuture;
         }

         public void handleEvent(StreamConnection channel) {
            WebSocketChannel result = this.handshake.createChannel(channel, this.newUri.toString(), ConnectionBuilder.this.bufferPool, this.options);
            this.ioFuture.setResult(result);
         }
      }
   }
}
