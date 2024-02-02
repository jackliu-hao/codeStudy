/*     */ package org.apache.http.impl.bootstrap;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import javax.net.ServerSocketFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import org.apache.http.ConnectionReuseStrategy;
/*     */ import org.apache.http.ExceptionLogger;
/*     */ import org.apache.http.HttpConnectionFactory;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponseFactory;
/*     */ import org.apache.http.HttpResponseInterceptor;
/*     */ import org.apache.http.config.ConnectionConfig;
/*     */ import org.apache.http.config.SocketConfig;
/*     */ import org.apache.http.impl.DefaultBHttpServerConnection;
/*     */ import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
/*     */ import org.apache.http.impl.DefaultConnectionReuseStrategy;
/*     */ import org.apache.http.impl.DefaultHttpResponseFactory;
/*     */ import org.apache.http.protocol.HttpExpectationVerifier;
/*     */ import org.apache.http.protocol.HttpProcessor;
/*     */ import org.apache.http.protocol.HttpProcessorBuilder;
/*     */ import org.apache.http.protocol.HttpRequestHandler;
/*     */ import org.apache.http.protocol.HttpRequestHandlerMapper;
/*     */ import org.apache.http.protocol.HttpService;
/*     */ import org.apache.http.protocol.ResponseConnControl;
/*     */ import org.apache.http.protocol.ResponseContent;
/*     */ import org.apache.http.protocol.ResponseDate;
/*     */ import org.apache.http.protocol.ResponseServer;
/*     */ import org.apache.http.protocol.UriHttpRequestHandlerMapper;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerBootstrap
/*     */ {
/*     */   private int listenerPort;
/*     */   private InetAddress localAddress;
/*     */   private SocketConfig socketConfig;
/*     */   private ConnectionConfig connectionConfig;
/*     */   private LinkedList<HttpRequestInterceptor> requestFirst;
/*     */   private LinkedList<HttpRequestInterceptor> requestLast;
/*     */   private LinkedList<HttpResponseInterceptor> responseFirst;
/*     */   private LinkedList<HttpResponseInterceptor> responseLast;
/*     */   private String serverInfo;
/*     */   private HttpProcessor httpProcessor;
/*     */   private ConnectionReuseStrategy connStrategy;
/*     */   private HttpResponseFactory responseFactory;
/*     */   private HttpRequestHandlerMapper handlerMapper;
/*     */   private Map<String, HttpRequestHandler> handlerMap;
/*     */   private HttpExpectationVerifier expectationVerifier;
/*     */   private ServerSocketFactory serverSocketFactory;
/*     */   private SSLContext sslContext;
/*     */   private SSLServerSetupHandler sslSetupHandler;
/*     */   private HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
/*     */   private ExceptionLogger exceptionLogger;
/*     */   
/*     */   public static ServerBootstrap bootstrap() {
/*  91 */     return new ServerBootstrap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setListenerPort(int listenerPort) {
/* 100 */     this.listenerPort = listenerPort;
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setLocalAddress(InetAddress localAddress) {
/* 110 */     this.localAddress = localAddress;
/* 111 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setSocketConfig(SocketConfig socketConfig) {
/* 120 */     this.socketConfig = socketConfig;
/* 121 */     return this;
/*     */   }
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
/*     */   public final ServerBootstrap setConnectionConfig(ConnectionConfig connectionConfig) {
/* 134 */     this.connectionConfig = connectionConfig;
/* 135 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setHttpProcessor(HttpProcessor httpProcessor) {
/* 144 */     this.httpProcessor = httpProcessor;
/* 145 */     return this;
/*     */   }
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
/*     */   public final ServerBootstrap addInterceptorFirst(HttpResponseInterceptor itcp) {
/* 158 */     if (itcp == null) {
/* 159 */       return this;
/*     */     }
/* 161 */     if (this.responseFirst == null) {
/* 162 */       this.responseFirst = new LinkedList<HttpResponseInterceptor>();
/*     */     }
/* 164 */     this.responseFirst.addFirst(itcp);
/* 165 */     return this;
/*     */   }
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
/*     */   public final ServerBootstrap addInterceptorLast(HttpResponseInterceptor itcp) {
/* 178 */     if (itcp == null) {
/* 179 */       return this;
/*     */     }
/* 181 */     if (this.responseLast == null) {
/* 182 */       this.responseLast = new LinkedList<HttpResponseInterceptor>();
/*     */     }
/* 184 */     this.responseLast.addLast(itcp);
/* 185 */     return this;
/*     */   }
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
/*     */   public final ServerBootstrap addInterceptorFirst(HttpRequestInterceptor itcp) {
/* 198 */     if (itcp == null) {
/* 199 */       return this;
/*     */     }
/* 201 */     if (this.requestFirst == null) {
/* 202 */       this.requestFirst = new LinkedList<HttpRequestInterceptor>();
/*     */     }
/* 204 */     this.requestFirst.addFirst(itcp);
/* 205 */     return this;
/*     */   }
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
/*     */   public final ServerBootstrap addInterceptorLast(HttpRequestInterceptor itcp) {
/* 218 */     if (itcp == null) {
/* 219 */       return this;
/*     */     }
/* 221 */     if (this.requestLast == null) {
/* 222 */       this.requestLast = new LinkedList<HttpRequestInterceptor>();
/*     */     }
/* 224 */     this.requestLast.addLast(itcp);
/* 225 */     return this;
/*     */   }
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
/*     */   public final ServerBootstrap setServerInfo(String serverInfo) {
/* 238 */     this.serverInfo = serverInfo;
/* 239 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setConnectionReuseStrategy(ConnectionReuseStrategy connStrategy) {
/* 248 */     this.connStrategy = connStrategy;
/* 249 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setResponseFactory(HttpResponseFactory responseFactory) {
/* 258 */     this.responseFactory = responseFactory;
/* 259 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setHandlerMapper(HttpRequestHandlerMapper handlerMapper) {
/* 268 */     this.handlerMapper = handlerMapper;
/* 269 */     return this;
/*     */   }
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
/*     */   public final ServerBootstrap registerHandler(String pattern, HttpRequestHandler handler) {
/* 285 */     if (pattern == null || handler == null) {
/* 286 */       return this;
/*     */     }
/* 288 */     if (this.handlerMap == null) {
/* 289 */       this.handlerMap = new HashMap<String, HttpRequestHandler>();
/*     */     }
/* 291 */     this.handlerMap.put(pattern, handler);
/* 292 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setExpectationVerifier(HttpExpectationVerifier expectationVerifier) {
/* 301 */     this.expectationVerifier = expectationVerifier;
/* 302 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setConnectionFactory(HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory) {
/* 312 */     this.connectionFactory = connectionFactory;
/* 313 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setSslSetupHandler(SSLServerSetupHandler sslSetupHandler) {
/* 322 */     this.sslSetupHandler = sslSetupHandler;
/* 323 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setServerSocketFactory(ServerSocketFactory serverSocketFactory) {
/* 332 */     this.serverSocketFactory = serverSocketFactory;
/* 333 */     return this;
/*     */   }
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
/*     */   public final ServerBootstrap setSslContext(SSLContext sslContext) {
/* 346 */     this.sslContext = sslContext;
/* 347 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ServerBootstrap setExceptionLogger(ExceptionLogger exceptionLogger) {
/* 356 */     this.exceptionLogger = exceptionLogger;
/* 357 */     return this; } public HttpServer create() {
/*     */     UriHttpRequestHandlerMapper uriHttpRequestHandlerMapper;
/*     */     DefaultConnectionReuseStrategy defaultConnectionReuseStrategy;
/*     */     DefaultHttpResponseFactory defaultHttpResponseFactory;
/*     */     DefaultBHttpServerConnectionFactory defaultBHttpServerConnectionFactory;
/* 362 */     HttpProcessor httpProcessorCopy = this.httpProcessor;
/* 363 */     if (httpProcessorCopy == null) {
/*     */       
/* 365 */       HttpProcessorBuilder b = HttpProcessorBuilder.create();
/* 366 */       if (this.requestFirst != null) {
/* 367 */         for (HttpRequestInterceptor i : this.requestFirst) {
/* 368 */           b.addFirst(i);
/*     */         }
/*     */       }
/* 371 */       if (this.responseFirst != null) {
/* 372 */         for (HttpResponseInterceptor i : this.responseFirst) {
/* 373 */           b.addFirst(i);
/*     */         }
/*     */       }
/*     */       
/* 377 */       String serverInfoCopy = this.serverInfo;
/* 378 */       if (serverInfoCopy == null) {
/* 379 */         serverInfoCopy = "Apache-HttpCore/1.1";
/*     */       }
/*     */       
/* 382 */       b.addAll(new HttpResponseInterceptor[] { (HttpResponseInterceptor)new ResponseDate(), (HttpResponseInterceptor)new ResponseServer(serverInfoCopy), (HttpResponseInterceptor)new ResponseContent(), (HttpResponseInterceptor)new ResponseConnControl() });
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 387 */       if (this.requestLast != null) {
/* 388 */         for (HttpRequestInterceptor i : this.requestLast) {
/* 389 */           b.addLast(i);
/*     */         }
/*     */       }
/* 392 */       if (this.responseLast != null) {
/* 393 */         for (HttpResponseInterceptor i : this.responseLast) {
/* 394 */           b.addLast(i);
/*     */         }
/*     */       }
/* 397 */       httpProcessorCopy = b.build();
/*     */     } 
/*     */     
/* 400 */     HttpRequestHandlerMapper handlerMapperCopy = this.handlerMapper;
/* 401 */     if (handlerMapperCopy == null) {
/* 402 */       UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
/* 403 */       if (this.handlerMap != null) {
/* 404 */         for (Map.Entry<String, HttpRequestHandler> entry : this.handlerMap.entrySet()) {
/* 405 */           reqistry.register(entry.getKey(), entry.getValue());
/*     */         }
/*     */       }
/* 408 */       uriHttpRequestHandlerMapper = reqistry;
/*     */     } 
/*     */     
/* 411 */     ConnectionReuseStrategy connStrategyCopy = this.connStrategy;
/* 412 */     if (connStrategyCopy == null) {
/* 413 */       defaultConnectionReuseStrategy = DefaultConnectionReuseStrategy.INSTANCE;
/*     */     }
/*     */     
/* 416 */     HttpResponseFactory responseFactoryCopy = this.responseFactory;
/* 417 */     if (responseFactoryCopy == null) {
/* 418 */       defaultHttpResponseFactory = DefaultHttpResponseFactory.INSTANCE;
/*     */     }
/*     */     
/* 421 */     HttpService httpService = new HttpService(httpProcessorCopy, (ConnectionReuseStrategy)defaultConnectionReuseStrategy, (HttpResponseFactory)defaultHttpResponseFactory, (HttpRequestHandlerMapper)uriHttpRequestHandlerMapper, this.expectationVerifier);
/*     */ 
/*     */ 
/*     */     
/* 425 */     ServerSocketFactory serverSocketFactoryCopy = this.serverSocketFactory;
/* 426 */     if (serverSocketFactoryCopy == null) {
/* 427 */       if (this.sslContext != null) {
/* 428 */         serverSocketFactoryCopy = this.sslContext.getServerSocketFactory();
/*     */       } else {
/* 430 */         serverSocketFactoryCopy = ServerSocketFactory.getDefault();
/*     */       } 
/*     */     }
/*     */     
/* 434 */     HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactoryCopy = this.connectionFactory;
/* 435 */     if (connectionFactoryCopy == null) {
/* 436 */       if (this.connectionConfig != null) {
/* 437 */         defaultBHttpServerConnectionFactory = new DefaultBHttpServerConnectionFactory(this.connectionConfig);
/*     */       } else {
/* 439 */         defaultBHttpServerConnectionFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
/*     */       } 
/*     */     }
/*     */     
/* 443 */     ExceptionLogger exceptionLoggerCopy = this.exceptionLogger;
/* 444 */     if (exceptionLoggerCopy == null) {
/* 445 */       exceptionLoggerCopy = ExceptionLogger.NO_OP;
/*     */     }
/*     */     
/* 448 */     return new HttpServer((this.listenerPort > 0) ? this.listenerPort : 0, this.localAddress, (this.socketConfig != null) ? this.socketConfig : SocketConfig.DEFAULT, serverSocketFactoryCopy, httpService, (HttpConnectionFactory<? extends DefaultBHttpServerConnection>)defaultBHttpServerConnectionFactory, this.sslSetupHandler, exceptionLoggerCopy);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\bootstrap\ServerBootstrap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */