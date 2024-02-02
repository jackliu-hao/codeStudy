/*     */ package io.undertow.client;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.security.AccessController;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.ServiceLoader;
/*     */ import org.xnio.FutureResult;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.ssl.XnioSsl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class UndertowClient
/*     */ {
/*     */   private final Map<String, ClientProvider> clientProviders;
/*  50 */   private static final UndertowClient INSTANCE = new UndertowClient();
/*     */   
/*     */   private UndertowClient() {
/*  53 */     this(UndertowClient.class.getClassLoader());
/*     */   }
/*     */   
/*     */   private UndertowClient(ClassLoader classLoader) {
/*  57 */     ServiceLoader<ClientProvider> providers = AccessController.<ServiceLoader<ClientProvider>>doPrivileged(() -> ServiceLoader.load(ClientProvider.class, classLoader));
/*     */     
/*  59 */     Map<String, ClientProvider> map = new HashMap<>();
/*  60 */     for (ClientProvider provider : providers) {
/*  61 */       for (String scheme : provider.handlesSchemes()) {
/*  62 */         map.put(scheme, provider);
/*     */       }
/*     */     } 
/*  65 */     this.clientProviders = Collections.unmodifiableMap(map);
/*     */   }
/*     */   
/*     */   public IoFuture<ClientConnection> connect(URI uri, XnioWorker worker, ByteBufferPool bufferPool, OptionMap options) {
/*  69 */     return connect(uri, worker, (XnioSsl)null, bufferPool, options);
/*     */   }
/*     */   
/*     */   public IoFuture<ClientConnection> connect(InetSocketAddress bindAddress, URI uri, XnioWorker worker, ByteBufferPool bufferPool, OptionMap options) {
/*  73 */     return connect(bindAddress, uri, worker, (XnioSsl)null, bufferPool, options);
/*     */   }
/*     */   
/*     */   public IoFuture<ClientConnection> connect(URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  77 */     return connect((InetSocketAddress)null, uri, worker, ssl, bufferPool, options);
/*     */   }
/*     */   
/*     */   public IoFuture<ClientConnection> connect(InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/*  81 */     ClientProvider provider = getClientProvider(uri);
/*  82 */     final FutureResult<ClientConnection> result = new FutureResult();
/*  83 */     provider.connect(new ClientCallback<ClientConnection>()
/*     */         {
/*     */           public void completed(ClientConnection r) {
/*  86 */             result.setResult(r);
/*     */           }
/*     */ 
/*     */           
/*     */           public void failed(IOException e) {
/*  91 */             result.setException(e);
/*     */           }
/*     */         },  bindAddress, uri, worker, ssl, bufferPool, options);
/*  94 */     return result.getIoFuture();
/*     */   }
/*     */   
/*     */   public IoFuture<ClientConnection> connect(URI uri, XnioIoThread ioThread, ByteBufferPool bufferPool, OptionMap options) {
/*  98 */     return connect((InetSocketAddress)null, uri, ioThread, (XnioSsl)null, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public IoFuture<ClientConnection> connect(InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, ByteBufferPool bufferPool, OptionMap options) {
/* 103 */     return connect(bindAddress, uri, ioThread, (XnioSsl)null, bufferPool, options);
/*     */   }
/*     */   
/*     */   public IoFuture<ClientConnection> connect(URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/* 107 */     return connect((InetSocketAddress)null, uri, ioThread, ssl, bufferPool, options);
/*     */   }
/*     */   
/*     */   public IoFuture<ClientConnection> connect(InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/* 111 */     ClientProvider provider = getClientProvider(uri);
/* 112 */     final FutureResult<ClientConnection> result = new FutureResult();
/* 113 */     provider.connect(new ClientCallback<ClientConnection>()
/*     */         {
/*     */           public void completed(ClientConnection r) {
/* 116 */             result.setResult(r);
/*     */           }
/*     */ 
/*     */           
/*     */           public void failed(IOException e) {
/* 121 */             result.setException(e);
/*     */           }
/*     */         },  bindAddress, uri, ioThread, ssl, bufferPool, options);
/* 124 */     return result.getIoFuture();
/*     */   }
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, ByteBufferPool bufferPool, OptionMap options) {
/* 128 */     connect(listener, uri, worker, (XnioSsl)null, bufferPool, options);
/*     */   }
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, ByteBufferPool bufferPool, OptionMap options) {
/* 132 */     connect(listener, bindAddress, uri, worker, (XnioSsl)null, bufferPool, options);
/*     */   }
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/* 136 */     ClientProvider provider = getClientProvider(uri);
/* 137 */     provider.connect(listener, uri, worker, ssl, bufferPool, options);
/*     */   }
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/* 141 */     ClientProvider provider = getClientProvider(uri);
/* 142 */     provider.connect(listener, bindAddress, uri, worker, ssl, bufferPool, options);
/*     */   }
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, ByteBufferPool bufferPool, OptionMap options) {
/* 146 */     connect(listener, uri, ioThread, (XnioSsl)null, bufferPool, options);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, ByteBufferPool bufferPool, OptionMap options) {
/* 151 */     connect(listener, bindAddress, uri, ioThread, (XnioSsl)null, bufferPool, options);
/*     */   }
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/* 155 */     ClientProvider provider = getClientProvider(uri);
/* 156 */     provider.connect(listener, uri, ioThread, ssl, bufferPool, options);
/*     */   }
/*     */   
/*     */   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
/* 160 */     ClientProvider provider = getClientProvider(uri);
/* 161 */     provider.connect(listener, bindAddress, uri, ioThread, ssl, bufferPool, options);
/*     */   }
/*     */   
/*     */   private ClientProvider getClientProvider(URI uri) {
/* 165 */     ClientProvider provider = this.clientProviders.get(uri.getScheme());
/* 166 */     if (provider == null) {
/* 167 */       throw UndertowClientMessages.MESSAGES.unknownScheme(uri);
/*     */     }
/* 169 */     return provider;
/*     */   }
/*     */   
/*     */   public static UndertowClient getInstance() {
/* 173 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   public static UndertowClient getInstance(ClassLoader classLoader) {
/* 177 */     return new UndertowClient(classLoader);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\UndertowClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */