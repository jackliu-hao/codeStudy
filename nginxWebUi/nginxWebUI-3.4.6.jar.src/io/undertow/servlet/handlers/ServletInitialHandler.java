/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.server.BlockingHttpExchange;
/*     */ import io.undertow.server.DefaultByteBufferPool;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.server.SSLSessionInfo;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.XnioBufferPoolAdaptor;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.ExceptionHandler;
/*     */ import io.undertow.servlet.api.LoggingExceptionHandler;
/*     */ import io.undertow.servlet.api.ServletDispatcher;
/*     */ import io.undertow.servlet.api.ThreadSetupHandler;
/*     */ import io.undertow.servlet.core.ApplicationListeners;
/*     */ import io.undertow.servlet.core.ServletBlockingHttpExchange;
/*     */ import io.undertow.servlet.spec.AsyncContextImpl;
/*     */ import io.undertow.servlet.spec.HttpServletRequestImpl;
/*     */ import io.undertow.servlet.spec.HttpServletResponseImpl;
/*     */ import io.undertow.servlet.spec.RequestDispatcherImpl;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ public class ServletInitialHandler
/*     */   implements HttpHandler, ServletDispatcher
/*     */ {
/*  81 */   private static final RuntimePermission PERMISSION = new RuntimePermission("io.undertow.servlet.CREATE_INITIAL_HANDLER");
/*     */   
/*     */   private final HttpHandler next;
/*     */   
/*     */   private final ThreadSetupHandler.Action<Object, ServletRequestContext> firstRequestHandler;
/*     */   
/*     */   private final ServletContextImpl servletContext;
/*     */   
/*     */   private final ApplicationListeners listeners;
/*     */   
/*     */   private final ServletPathMatches paths;
/*     */   
/*     */   private final ExceptionHandler exceptionHandler;
/*     */   
/*  95 */   private final HttpHandler dispatchHandler = new HttpHandler()
/*     */     {
/*     */       public void handleRequest(final HttpServerExchange exchange) throws Exception {
/*  98 */         final ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  99 */         if (System.getSecurityManager() == null) {
/* 100 */           ServletInitialHandler.this.dispatchRequest(exchange, servletRequestContext, servletRequestContext.getOriginalServletPathMatch().getServletChain(), DispatcherType.REQUEST);
/*     */         } else {
/*     */           
/* 103 */           AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */               {
/*     */                 public Object run() throws Exception {
/* 106 */                   ServletInitialHandler.this.dispatchRequest(exchange, servletRequestContext, servletRequestContext.getOriginalServletPathMatch().getServletChain(), DispatcherType.REQUEST);
/* 107 */                   return null;
/*     */                 }
/*     */               });
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*     */   public ServletInitialHandler(ServletPathMatches paths, HttpHandler next, Deployment deployment, ServletContextImpl servletContext) {
/* 115 */     this.next = next;
/* 116 */     this.servletContext = servletContext;
/* 117 */     this.paths = paths;
/* 118 */     this.listeners = servletContext.getDeployment().getApplicationListeners();
/* 119 */     SecurityManager sm = System.getSecurityManager();
/* 120 */     if (sm != null)
/*     */     {
/*     */       
/* 123 */       sm.checkPermission(PERMISSION);
/*     */     }
/* 125 */     ExceptionHandler handler = servletContext.getDeployment().getDeploymentInfo().getExceptionHandler();
/* 126 */     if (handler != null) {
/* 127 */       this.exceptionHandler = handler;
/*     */     } else {
/* 129 */       this.exceptionHandler = (ExceptionHandler)LoggingExceptionHandler.DEFAULT;
/*     */     } 
/* 131 */     this.firstRequestHandler = deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Object, ServletRequestContext>()
/*     */         {
/*     */           public Object call(HttpServerExchange exchange, ServletRequestContext context) throws Exception {
/* 134 */             ServletInitialHandler.this.handleFirstRequest(exchange, context);
/* 135 */             return null;
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 142 */     String path = exchange.getRelativePath();
/* 143 */     if (Paths.isForbidden(path)) {
/* 144 */       exchange.setStatusCode(404);
/*     */       return;
/*     */     } 
/* 147 */     ServletPathMatch info = this.paths.getServletHandlerByPath(path);
/* 148 */     if (info.getType() == ServletPathMatch.Type.REWRITE) {
/*     */ 
/*     */       
/* 151 */       exchange.setRelativePath(info.getRewriteLocation());
/* 152 */       exchange.setRequestPath(exchange.getResolvedPath() + info.getRewriteLocation());
/*     */     } 
/* 154 */     HttpServletResponseImpl response = new HttpServletResponseImpl(exchange, this.servletContext);
/* 155 */     HttpServletRequestImpl request = new HttpServletRequestImpl(exchange, this.servletContext);
/* 156 */     ServletRequestContext servletRequestContext = new ServletRequestContext(this.servletContext.getDeployment(), request, response, info);
/*     */     
/* 158 */     if (info.getServletChain().getManagedServlet().getMaxRequestSize() > 0L && isMultiPartExchange(exchange)) {
/* 159 */       exchange.setMaxEntitySize(info.getServletChain().getManagedServlet().getMaxRequestSize());
/*     */     }
/* 161 */     exchange.putAttachment(ServletRequestContext.ATTACHMENT_KEY, servletRequestContext);
/*     */     
/* 163 */     exchange.startBlocking((BlockingHttpExchange)new ServletBlockingHttpExchange(exchange));
/* 164 */     servletRequestContext.setServletPathMatch(info);
/*     */     
/* 166 */     Executor executor = info.getServletChain().getExecutor();
/* 167 */     if (executor == null) {
/* 168 */       executor = this.servletContext.getDeployment().getExecutor();
/*     */     }
/*     */     
/* 171 */     if (exchange.isInIoThread() || executor != null) {
/*     */       
/* 173 */       exchange.dispatch(executor, this.dispatchHandler);
/*     */     } else {
/* 175 */       dispatchRequest(exchange, servletRequestContext, info.getServletChain(), DispatcherType.REQUEST);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void dispatchToPath(HttpServerExchange exchange, ServletPathMatch pathInfo, DispatcherType dispatcherType) throws Exception {
/* 180 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 181 */     servletRequestContext.setServletPathMatch(pathInfo);
/* 182 */     dispatchRequest(exchange, servletRequestContext, pathInfo.getServletChain(), dispatcherType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispatchToServlet(HttpServerExchange exchange, ServletChain servletchain, DispatcherType dispatcherType) throws Exception {
/* 187 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*     */     
/* 189 */     DispatcherType oldDispatch = servletRequestContext.getDispatcherType();
/* 190 */     ServletChain oldChain = servletRequestContext.getCurrentServlet();
/*     */     try {
/* 192 */       dispatchRequest(exchange, servletRequestContext, servletchain, dispatcherType);
/*     */     } finally {
/* 194 */       servletRequestContext.setDispatcherType(oldDispatch);
/* 195 */       servletRequestContext.setCurrentServlet(oldChain);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispatchMockRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
/*     */     String relative;
/* 202 */     DefaultByteBufferPool bufferPool = new DefaultByteBufferPool(false, 1024, 0, 0);
/* 203 */     MockServerConnection connection = new MockServerConnection((ByteBufferPool)bufferPool);
/* 204 */     HttpServerExchange exchange = new HttpServerExchange(connection);
/* 205 */     exchange.setRequestScheme(request.getScheme());
/* 206 */     exchange.setRequestMethod(new HttpString(request.getMethod()));
/* 207 */     exchange.setProtocol(Protocols.HTTP_1_0);
/* 208 */     exchange.setResolvedPath(request.getContextPath());
/*     */     
/* 210 */     if (request.getPathInfo() == null) {
/* 211 */       relative = request.getServletPath();
/*     */     } else {
/* 213 */       relative = request.getServletPath() + request.getPathInfo();
/*     */     } 
/* 215 */     exchange.setRelativePath(relative);
/* 216 */     ServletPathMatch info = this.paths.getServletHandlerByPath(request.getServletPath());
/* 217 */     HttpServletResponseImpl oResponse = new HttpServletResponseImpl(exchange, this.servletContext);
/* 218 */     HttpServletRequestImpl oRequest = new HttpServletRequestImpl(exchange, this.servletContext);
/* 219 */     ServletRequestContext servletRequestContext = new ServletRequestContext(this.servletContext.getDeployment(), oRequest, oResponse, info);
/* 220 */     servletRequestContext.setServletRequest((ServletRequest)request);
/* 221 */     servletRequestContext.setServletResponse((ServletResponse)response);
/*     */     
/* 223 */     if (info.getServletChain().getManagedServlet().getMaxRequestSize() > 0L && isMultiPartExchange(exchange)) {
/* 224 */       exchange.setMaxEntitySize(info.getServletChain().getManagedServlet().getMaxRequestSize());
/*     */     }
/* 226 */     exchange.putAttachment(ServletRequestContext.ATTACHMENT_KEY, servletRequestContext);
/*     */     
/* 228 */     exchange.startBlocking((BlockingHttpExchange)new ServletBlockingHttpExchange(exchange));
/* 229 */     servletRequestContext.setServletPathMatch(info);
/*     */     
/*     */     try {
/* 232 */       dispatchRequest(exchange, servletRequestContext, info.getServletChain(), DispatcherType.REQUEST);
/* 233 */     } catch (Exception e) {
/* 234 */       if (e instanceof RuntimeException) {
/* 235 */         throw (RuntimeException)e;
/*     */       }
/* 237 */       throw new ServletException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isMultiPartExchange(HttpServerExchange exhange) {
/* 243 */     HeaderValues contentTypeHeaders = exhange.getRequestHeaders().get("Content-Type");
/* 244 */     if (contentTypeHeaders != null && contentTypeHeaders.size() > 0) {
/* 245 */       return contentTypeHeaders.getFirst().startsWith("multipart");
/*     */     }
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void dispatchRequest(HttpServerExchange exchange, ServletRequestContext servletRequestContext, ServletChain servletChain, DispatcherType dispatcherType) throws Exception {
/* 252 */     servletRequestContext.setDispatcherType(dispatcherType);
/* 253 */     servletRequestContext.setCurrentServlet(servletChain);
/* 254 */     if (dispatcherType == DispatcherType.REQUEST || dispatcherType == DispatcherType.ASYNC) {
/* 255 */       this.firstRequestHandler.call(exchange, servletRequestContext);
/*     */     } else {
/* 257 */       this.next.handleRequest(exchange);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleFirstRequest(HttpServerExchange exchange, ServletRequestContext servletRequestContext) throws Exception {
/* 262 */     ServletRequest request = servletRequestContext.getServletRequest();
/* 263 */     ServletResponse response = servletRequestContext.getServletResponse();
/*     */ 
/*     */     
/* 266 */     Map<String, String> attrs = (Map<String, String>)exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
/* 267 */     if (attrs != null) {
/* 268 */       for (Map.Entry<String, String> entry : attrs.entrySet()) {
/* 269 */         request.setAttribute(entry.getKey(), entry.getValue());
/*     */       }
/*     */     }
/* 272 */     servletRequestContext.setRunningInsideHandler(true);
/*     */     try {
/* 274 */       this.listeners.requestInitialized(request);
/* 275 */       this.next.handleRequest(exchange);
/* 276 */       AsyncContextImpl asyncContextInternal = servletRequestContext.getOriginalRequest().getAsyncContextInternal();
/* 277 */       if (asyncContextInternal != null && asyncContextInternal.isCompletedBeforeInitialRequestDone()) {
/* 278 */         asyncContextInternal.handleCompletedBeforeInitialRequestDone();
/*     */       }
/*     */       
/* 281 */       if (servletRequestContext.getErrorCode() > 0) {
/* 282 */         servletRequestContext.getOriginalResponse().doErrorDispatch(servletRequestContext.getErrorCode(), servletRequestContext.getErrorMessage());
/*     */       }
/* 284 */     } catch (Throwable t) {
/*     */       
/* 286 */       servletRequestContext.setRunningInsideHandler(false);
/* 287 */       AsyncContextImpl asyncContextInternal = servletRequestContext.getOriginalRequest().getAsyncContextInternal();
/* 288 */       if (asyncContextInternal != null && asyncContextInternal.isCompletedBeforeInitialRequestDone()) {
/* 289 */         asyncContextInternal.handleCompletedBeforeInitialRequestDone();
/*     */       }
/* 291 */       if (asyncContextInternal != null) {
/* 292 */         asyncContextInternal.initialRequestFailed();
/*     */       }
/*     */       
/* 295 */       boolean handled = this.exceptionHandler.handleThrowable(exchange, request, response, t);
/*     */       
/* 297 */       if (handled) {
/* 298 */         exchange.endExchange();
/* 299 */       } else if (request.isAsyncStarted() || request.getDispatcherType() == DispatcherType.ASYNC) {
/* 300 */         exchange.unDispatch();
/* 301 */         servletRequestContext.getOriginalRequest().getAsyncContextInternal().handleError(t);
/*     */       }
/* 303 */       else if (!exchange.isResponseStarted()) {
/* 304 */         response.reset();
/* 305 */         exchange.setStatusCode(500);
/* 306 */         exchange.getResponseHeaders().clear();
/* 307 */         String location = this.servletContext.getDeployment().getErrorPages().getErrorLocation(t);
/* 308 */         if (location == null) {
/* 309 */           location = this.servletContext.getDeployment().getErrorPages().getErrorLocation(500);
/*     */         }
/* 311 */         if (location != null) {
/* 312 */           RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(location, this.servletContext);
/*     */           try {
/* 314 */             dispatcher.error(servletRequestContext, request, response, servletRequestContext.getOriginalServletPathMatch().getServletChain().getManagedServlet().getServletInfo().getName(), t);
/* 315 */           } catch (Exception e) {
/* 316 */             UndertowLogger.REQUEST_LOGGER.exceptionGeneratingErrorPage(e, location);
/*     */           }
/*     */         
/* 319 */         } else if (servletRequestContext.displayStackTraces()) {
/* 320 */           ServletDebugPageHandler.handleRequest(exchange, servletRequestContext, t);
/*     */         } else {
/* 322 */           servletRequestContext.getOriginalResponse().doErrorDispatch(500, "Internal Server Error");
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     } finally {
/*     */       
/* 329 */       servletRequestContext.setRunningInsideHandler(false);
/* 330 */       this.listeners.requestDestroyed(request);
/*     */     } 
/*     */     
/* 333 */     if (!exchange.isDispatched() && !(exchange.getConnection() instanceof MockServerConnection)) {
/* 334 */       servletRequestContext.getOriginalResponse().responseDone();
/*     */     }
/* 336 */     if (!exchange.isDispatched()) {
/* 337 */       AsyncContextImpl ctx = servletRequestContext.getOriginalRequest().getAsyncContextInternal();
/* 338 */       if (ctx != null) {
/* 339 */         ctx.complete();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public HttpHandler getNext() {
/* 345 */     return this.next;
/*     */   }
/*     */   
/*     */   private static class MockServerConnection
/*     */     extends ServerConnection {
/*     */     private final ByteBufferPool bufferPool;
/*     */     
/*     */     private MockServerConnection(ByteBufferPool bufferPool) {
/* 353 */       this.bufferPool = bufferPool;
/*     */     }
/*     */     private SSLSessionInfo sslSessionInfo; private XnioBufferPoolAdaptor poolAdaptor;
/*     */     
/*     */     public Pool<ByteBuffer> getBufferPool() {
/* 358 */       if (this.poolAdaptor == null) {
/* 359 */         this.poolAdaptor = new XnioBufferPoolAdaptor(getByteBufferPool());
/*     */       }
/* 361 */       return (Pool<ByteBuffer>)this.poolAdaptor;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ByteBufferPool getByteBufferPool() {
/* 367 */       return this.bufferPool;
/*     */     }
/*     */ 
/*     */     
/*     */     public XnioWorker getWorker() {
/* 372 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public XnioIoThread getIoThread() {
/* 377 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpServerExchange sendOutOfBandResponse(HttpServerExchange exchange) {
/* 382 */       throw UndertowMessages.MESSAGES.outOfBandResponseNotSupported();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isContinueResponseSupported() {
/* 387 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void terminateRequestChannel(HttpServerExchange exchange) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isOpen() {
/* 397 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean supportsOption(Option<?> option) {
/* 402 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> T getOption(Option<T> option) throws IOException {
/* 407 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 412 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws IOException {}
/*     */ 
/*     */     
/*     */     public SocketAddress getPeerAddress() {
/* 421 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/* 426 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public ChannelListener.Setter<? extends ConnectedChannel> getCloseSetter() {
/* 431 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public SocketAddress getLocalAddress() {
/* 436 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 441 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public OptionMap getUndertowOptions() {
/* 446 */       return OptionMap.EMPTY;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getBufferSize() {
/* 451 */       return 1024;
/*     */     }
/*     */ 
/*     */     
/*     */     public SSLSessionInfo getSslSessionInfo() {
/* 456 */       return this.sslSessionInfo;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setSslSessionInfo(SSLSessionInfo sessionInfo) {
/* 461 */       this.sslSessionInfo = sessionInfo;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void addCloseListener(ServerConnection.CloseListener listener) {}
/*     */ 
/*     */     
/*     */     public StreamConnection upgradeChannel() {
/* 470 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public ConduitStreamSinkChannel getSinkChannel() {
/* 475 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public ConduitStreamSourceChannel getSourceChannel() {
/* 480 */       return new ConduitStreamSourceChannel(null, null);
/*     */     }
/*     */ 
/*     */     
/*     */     protected StreamSinkConduit getSinkConduit(HttpServerExchange exchange, StreamSinkConduit conduit) {
/* 485 */       return conduit;
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isUpgradeSupported() {
/* 490 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean isConnectSupported() {
/* 495 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void exchangeComplete(HttpServerExchange exchange) {}
/*     */ 
/*     */ 
/*     */     
/*     */     protected void setUpgradeListener(HttpUpgradeListener upgradeListener) {}
/*     */ 
/*     */ 
/*     */     
/*     */     protected void setConnectListener(HttpUpgradeListener connectListener) {}
/*     */ 
/*     */ 
/*     */     
/*     */     protected void maxEntitySizeUpdated(HttpServerExchange exchange) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public String getTransportProtocol() {
/* 518 */       return "mock";
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isRequestTrailerFieldsSupported() {
/* 523 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\ServletInitialHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */