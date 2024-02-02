package io.undertow.client;

import io.undertow.connector.ByteBufferPool;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.ssl.XnioSsl;

public final class UndertowClient {
   private final Map<String, ClientProvider> clientProviders;
   private static final UndertowClient INSTANCE = new UndertowClient();

   private UndertowClient() {
      this(UndertowClient.class.getClassLoader());
   }

   private UndertowClient(ClassLoader classLoader) {
      ServiceLoader<ClientProvider> providers = (ServiceLoader)AccessController.doPrivileged(() -> {
         return ServiceLoader.load(ClientProvider.class, classLoader);
      });
      Map<String, ClientProvider> map = new HashMap();
      Iterator var4 = providers.iterator();

      while(var4.hasNext()) {
         ClientProvider provider = (ClientProvider)var4.next();
         Iterator var6 = provider.handlesSchemes().iterator();

         while(var6.hasNext()) {
            String scheme = (String)var6.next();
            map.put(scheme, provider);
         }
      }

      this.clientProviders = Collections.unmodifiableMap(map);
   }

   public IoFuture<ClientConnection> connect(URI uri, XnioWorker worker, ByteBufferPool bufferPool, OptionMap options) {
      return this.connect((URI)uri, (XnioWorker)worker, (XnioSsl)null, bufferPool, options);
   }

   public IoFuture<ClientConnection> connect(InetSocketAddress bindAddress, URI uri, XnioWorker worker, ByteBufferPool bufferPool, OptionMap options) {
      return this.connect((InetSocketAddress)bindAddress, (URI)uri, (XnioWorker)worker, (XnioSsl)null, bufferPool, options);
   }

   public IoFuture<ClientConnection> connect(URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      return this.connect((InetSocketAddress)null, uri, worker, ssl, bufferPool, options);
   }

   public IoFuture<ClientConnection> connect(InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      ClientProvider provider = this.getClientProvider(uri);
      final FutureResult<ClientConnection> result = new FutureResult();
      provider.connect(new ClientCallback<ClientConnection>() {
         public void completed(ClientConnection r) {
            result.setResult(r);
         }

         public void failed(IOException e) {
            result.setException(e);
         }
      }, bindAddress, uri, worker, ssl, bufferPool, options);
      return result.getIoFuture();
   }

   public IoFuture<ClientConnection> connect(URI uri, XnioIoThread ioThread, ByteBufferPool bufferPool, OptionMap options) {
      return this.connect((InetSocketAddress)((InetSocketAddress)null), (URI)uri, (XnioIoThread)ioThread, (XnioSsl)null, bufferPool, options);
   }

   public IoFuture<ClientConnection> connect(InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, ByteBufferPool bufferPool, OptionMap options) {
      return this.connect((InetSocketAddress)bindAddress, (URI)uri, (XnioIoThread)ioThread, (XnioSsl)null, bufferPool, options);
   }

   public IoFuture<ClientConnection> connect(URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      return this.connect((InetSocketAddress)null, uri, ioThread, ssl, bufferPool, options);
   }

   public IoFuture<ClientConnection> connect(InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      ClientProvider provider = this.getClientProvider(uri);
      final FutureResult<ClientConnection> result = new FutureResult();
      provider.connect(new ClientCallback<ClientConnection>() {
         public void completed(ClientConnection r) {
            result.setResult(r);
         }

         public void failed(IOException e) {
            result.setException(e);
         }
      }, bindAddress, uri, ioThread, ssl, bufferPool, options);
      return result.getIoFuture();
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, ByteBufferPool bufferPool, OptionMap options) {
      this.connect((ClientCallback)listener, (URI)uri, (XnioWorker)worker, (XnioSsl)null, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, bindAddress, uri, (XnioWorker)worker, (XnioSsl)null, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      ClientProvider provider = this.getClientProvider(uri);
      provider.connect(listener, uri, worker, ssl, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      ClientProvider provider = this.getClientProvider(uri);
      provider.connect(listener, bindAddress, uri, worker, ssl, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, ByteBufferPool bufferPool, OptionMap options) {
      this.connect((ClientCallback)listener, (URI)uri, (XnioIoThread)ioThread, (XnioSsl)null, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, bindAddress, uri, (XnioIoThread)ioThread, (XnioSsl)null, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      ClientProvider provider = this.getClientProvider(uri);
      provider.connect(listener, uri, ioThread, ssl, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      ClientProvider provider = this.getClientProvider(uri);
      provider.connect(listener, bindAddress, uri, ioThread, ssl, bufferPool, options);
   }

   private ClientProvider getClientProvider(URI uri) {
      ClientProvider provider = (ClientProvider)this.clientProviders.get(uri.getScheme());
      if (provider == null) {
         throw UndertowClientMessages.MESSAGES.unknownScheme(uri);
      } else {
         return provider;
      }
   }

   public static UndertowClient getInstance() {
      return INSTANCE;
   }

   public static UndertowClient getInstance(ClassLoader classLoader) {
      return new UndertowClient(classLoader);
   }
}
