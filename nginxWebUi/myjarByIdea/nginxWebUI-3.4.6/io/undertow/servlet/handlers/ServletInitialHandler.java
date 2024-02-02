package io.undertow.servlet.handlers;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.server.SSLSessionInfo;
import io.undertow.server.ServerConnection;
import io.undertow.server.XnioBufferPoolAdaptor;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.ExceptionHandler;
import io.undertow.servlet.api.LoggingExceptionHandler;
import io.undertow.servlet.api.ServletDispatcher;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.core.ApplicationListeners;
import io.undertow.servlet.core.ServletBlockingHttpExchange;
import io.undertow.servlet.spec.AsyncContextImpl;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.servlet.spec.HttpServletResponseImpl;
import io.undertow.servlet.spec.RequestDispatcherImpl;
import io.undertow.servlet.spec.ServletContextImpl;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import io.undertow.util.Protocols;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.Configurable;
import org.xnio.channels.ConnectedChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

public class ServletInitialHandler implements HttpHandler, ServletDispatcher {
   private static final RuntimePermission PERMISSION = new RuntimePermission("io.undertow.servlet.CREATE_INITIAL_HANDLER");
   private final HttpHandler next;
   private final ThreadSetupHandler.Action<Object, ServletRequestContext> firstRequestHandler;
   private final ServletContextImpl servletContext;
   private final ApplicationListeners listeners;
   private final ServletPathMatches paths;
   private final ExceptionHandler exceptionHandler;
   private final HttpHandler dispatchHandler = new HttpHandler() {
      public void handleRequest(final HttpServerExchange exchange) throws Exception {
         final ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
         if (System.getSecurityManager() == null) {
            ServletInitialHandler.this.dispatchRequest(exchange, servletRequestContext, servletRequestContext.getOriginalServletPathMatch().getServletChain(), DispatcherType.REQUEST);
         } else {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() throws Exception {
                  ServletInitialHandler.this.dispatchRequest(exchange, servletRequestContext, servletRequestContext.getOriginalServletPathMatch().getServletChain(), DispatcherType.REQUEST);
                  return null;
               }
            });
         }

      }
   };

   public ServletInitialHandler(ServletPathMatches paths, HttpHandler next, Deployment deployment, ServletContextImpl servletContext) {
      this.next = next;
      this.servletContext = servletContext;
      this.paths = paths;
      this.listeners = servletContext.getDeployment().getApplicationListeners();
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(PERMISSION);
      }

      ExceptionHandler handler = servletContext.getDeployment().getDeploymentInfo().getExceptionHandler();
      if (handler != null) {
         this.exceptionHandler = handler;
      } else {
         this.exceptionHandler = LoggingExceptionHandler.DEFAULT;
      }

      this.firstRequestHandler = deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Object, ServletRequestContext>() {
         public Object call(HttpServerExchange exchange, ServletRequestContext context) throws Exception {
            ServletInitialHandler.this.handleFirstRequest(exchange, context);
            return null;
         }
      });
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      String path = exchange.getRelativePath();
      if (Paths.isForbidden(path)) {
         exchange.setStatusCode(404);
      } else {
         ServletPathMatch info = this.paths.getServletHandlerByPath(path);
         if (info.getType() == ServletPathMatch.Type.REWRITE) {
            exchange.setRelativePath(info.getRewriteLocation());
            exchange.setRequestPath(exchange.getResolvedPath() + info.getRewriteLocation());
         }

         HttpServletResponseImpl response = new HttpServletResponseImpl(exchange, this.servletContext);
         HttpServletRequestImpl request = new HttpServletRequestImpl(exchange, this.servletContext);
         ServletRequestContext servletRequestContext = new ServletRequestContext(this.servletContext.getDeployment(), request, response, info);
         if (info.getServletChain().getManagedServlet().getMaxRequestSize() > 0L && this.isMultiPartExchange(exchange)) {
            exchange.setMaxEntitySize(info.getServletChain().getManagedServlet().getMaxRequestSize());
         }

         exchange.putAttachment(ServletRequestContext.ATTACHMENT_KEY, servletRequestContext);
         exchange.startBlocking(new ServletBlockingHttpExchange(exchange));
         servletRequestContext.setServletPathMatch(info);
         Executor executor = info.getServletChain().getExecutor();
         if (executor == null) {
            executor = this.servletContext.getDeployment().getExecutor();
         }

         if (!exchange.isInIoThread() && executor == null) {
            this.dispatchRequest(exchange, servletRequestContext, info.getServletChain(), DispatcherType.REQUEST);
         } else {
            exchange.dispatch(executor, this.dispatchHandler);
         }

      }
   }

   public void dispatchToPath(HttpServerExchange exchange, ServletPathMatch pathInfo, DispatcherType dispatcherType) throws Exception {
      ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      servletRequestContext.setServletPathMatch(pathInfo);
      this.dispatchRequest(exchange, servletRequestContext, pathInfo.getServletChain(), dispatcherType);
   }

   public void dispatchToServlet(HttpServerExchange exchange, ServletChain servletchain, DispatcherType dispatcherType) throws Exception {
      ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      DispatcherType oldDispatch = servletRequestContext.getDispatcherType();
      ServletChain oldChain = servletRequestContext.getCurrentServlet();

      try {
         this.dispatchRequest(exchange, servletRequestContext, servletchain, dispatcherType);
      } finally {
         servletRequestContext.setDispatcherType(oldDispatch);
         servletRequestContext.setCurrentServlet(oldChain);
      }

   }

   public void dispatchMockRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
      DefaultByteBufferPool bufferPool = new DefaultByteBufferPool(false, 1024, 0, 0);
      MockServerConnection connection = new MockServerConnection(bufferPool);
      HttpServerExchange exchange = new HttpServerExchange(connection);
      exchange.setRequestScheme(request.getScheme());
      exchange.setRequestMethod(new HttpString(request.getMethod()));
      exchange.setProtocol(Protocols.HTTP_1_0);
      exchange.setResolvedPath(request.getContextPath());
      String relative;
      if (request.getPathInfo() == null) {
         relative = request.getServletPath();
      } else {
         relative = request.getServletPath() + request.getPathInfo();
      }

      exchange.setRelativePath(relative);
      ServletPathMatch info = this.paths.getServletHandlerByPath(request.getServletPath());
      HttpServletResponseImpl oResponse = new HttpServletResponseImpl(exchange, this.servletContext);
      HttpServletRequestImpl oRequest = new HttpServletRequestImpl(exchange, this.servletContext);
      ServletRequestContext servletRequestContext = new ServletRequestContext(this.servletContext.getDeployment(), oRequest, oResponse, info);
      servletRequestContext.setServletRequest(request);
      servletRequestContext.setServletResponse(response);
      if (info.getServletChain().getManagedServlet().getMaxRequestSize() > 0L && this.isMultiPartExchange(exchange)) {
         exchange.setMaxEntitySize(info.getServletChain().getManagedServlet().getMaxRequestSize());
      }

      exchange.putAttachment(ServletRequestContext.ATTACHMENT_KEY, servletRequestContext);
      exchange.startBlocking(new ServletBlockingHttpExchange(exchange));
      servletRequestContext.setServletPathMatch(info);

      try {
         this.dispatchRequest(exchange, servletRequestContext, info.getServletChain(), DispatcherType.REQUEST);
      } catch (Exception var12) {
         if (var12 instanceof RuntimeException) {
            throw (RuntimeException)var12;
         } else {
            throw new ServletException(var12);
         }
      }
   }

   private boolean isMultiPartExchange(HttpServerExchange exhange) {
      HeaderValues contentTypeHeaders = exhange.getRequestHeaders().get("Content-Type");
      return contentTypeHeaders != null && contentTypeHeaders.size() > 0 ? contentTypeHeaders.getFirst().startsWith("multipart") : false;
   }

   private void dispatchRequest(HttpServerExchange exchange, ServletRequestContext servletRequestContext, ServletChain servletChain, DispatcherType dispatcherType) throws Exception {
      servletRequestContext.setDispatcherType(dispatcherType);
      servletRequestContext.setCurrentServlet(servletChain);
      if (dispatcherType != DispatcherType.REQUEST && dispatcherType != DispatcherType.ASYNC) {
         this.next.handleRequest(exchange);
      } else {
         this.firstRequestHandler.call(exchange, servletRequestContext);
      }

   }

   private void handleFirstRequest(HttpServerExchange exchange, ServletRequestContext servletRequestContext) throws Exception {
      ServletRequest request = servletRequestContext.getServletRequest();
      ServletResponse response = servletRequestContext.getServletResponse();
      Map<String, String> attrs = (Map)exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
      if (attrs != null) {
         Iterator var6 = attrs.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var6.next();
            request.setAttribute((String)entry.getKey(), entry.getValue());
         }
      }

      servletRequestContext.setRunningInsideHandler(true);

      AsyncContextImpl ctx;
      try {
         this.listeners.requestInitialized(request);
         this.next.handleRequest(exchange);
         ctx = servletRequestContext.getOriginalRequest().getAsyncContextInternal();
         if (ctx != null && ctx.isCompletedBeforeInitialRequestDone()) {
            ctx.handleCompletedBeforeInitialRequestDone();
         }

         if (servletRequestContext.getErrorCode() > 0) {
            servletRequestContext.getOriginalResponse().doErrorDispatch(servletRequestContext.getErrorCode(), servletRequestContext.getErrorMessage());
         }
      } catch (Throwable var17) {
         Throwable t = var17;
         servletRequestContext.setRunningInsideHandler(false);
         AsyncContextImpl asyncContextInternal = servletRequestContext.getOriginalRequest().getAsyncContextInternal();
         if (asyncContextInternal != null && asyncContextInternal.isCompletedBeforeInitialRequestDone()) {
            asyncContextInternal.handleCompletedBeforeInitialRequestDone();
         }

         if (asyncContextInternal != null) {
            asyncContextInternal.initialRequestFailed();
         }

         boolean handled = this.exceptionHandler.handleThrowable(exchange, request, response, var17);
         if (handled) {
            exchange.endExchange();
         } else if (!request.isAsyncStarted() && request.getDispatcherType() != DispatcherType.ASYNC) {
            if (!exchange.isResponseStarted()) {
               response.reset();
               exchange.setStatusCode(500);
               exchange.getResponseHeaders().clear();
               String location = this.servletContext.getDeployment().getErrorPages().getErrorLocation(var17);
               if (location == null) {
                  location = this.servletContext.getDeployment().getErrorPages().getErrorLocation(500);
               }

               if (location != null) {
                  RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(location, this.servletContext);

                  try {
                     dispatcher.error(servletRequestContext, request, response, servletRequestContext.getOriginalServletPathMatch().getServletChain().getManagedServlet().getServletInfo().getName(), t);
                  } catch (Exception var16) {
                     UndertowLogger.REQUEST_LOGGER.exceptionGeneratingErrorPage(var16, location);
                  }
               } else if (servletRequestContext.displayStackTraces()) {
                  ServletDebugPageHandler.handleRequest(exchange, servletRequestContext, var17);
               } else {
                  servletRequestContext.getOriginalResponse().doErrorDispatch(500, "Internal Server Error");
               }
            }
         } else {
            exchange.unDispatch();
            servletRequestContext.getOriginalRequest().getAsyncContextInternal().handleError(var17);
         }
      } finally {
         servletRequestContext.setRunningInsideHandler(false);
         this.listeners.requestDestroyed(request);
      }

      if (!exchange.isDispatched() && !(exchange.getConnection() instanceof MockServerConnection)) {
         servletRequestContext.getOriginalResponse().responseDone();
      }

      if (!exchange.isDispatched()) {
         ctx = servletRequestContext.getOriginalRequest().getAsyncContextInternal();
         if (ctx != null) {
            ctx.complete();
         }
      }

   }

   public HttpHandler getNext() {
      return this.next;
   }

   private static class MockServerConnection extends ServerConnection {
      private final ByteBufferPool bufferPool;
      private SSLSessionInfo sslSessionInfo;
      private XnioBufferPoolAdaptor poolAdaptor;

      private MockServerConnection(ByteBufferPool bufferPool) {
         this.bufferPool = bufferPool;
      }

      public Pool<ByteBuffer> getBufferPool() {
         if (this.poolAdaptor == null) {
            this.poolAdaptor = new XnioBufferPoolAdaptor(this.getByteBufferPool());
         }

         return this.poolAdaptor;
      }

      public ByteBufferPool getByteBufferPool() {
         return this.bufferPool;
      }

      public XnioWorker getWorker() {
         return null;
      }

      public XnioIoThread getIoThread() {
         return null;
      }

      public HttpServerExchange sendOutOfBandResponse(HttpServerExchange exchange) {
         throw UndertowMessages.MESSAGES.outOfBandResponseNotSupported();
      }

      public boolean isContinueResponseSupported() {
         return false;
      }

      public void terminateRequestChannel(HttpServerExchange exchange) {
      }

      public boolean isOpen() {
         return true;
      }

      public boolean supportsOption(Option<?> option) {
         return false;
      }

      public <T> T getOption(Option<T> option) throws IOException {
         return null;
      }

      public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
         return null;
      }

      public void close() throws IOException {
      }

      public SocketAddress getPeerAddress() {
         return null;
      }

      public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
         return null;
      }

      public ChannelListener.Setter<? extends ConnectedChannel> getCloseSetter() {
         return null;
      }

      public SocketAddress getLocalAddress() {
         return null;
      }

      public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
         return null;
      }

      public OptionMap getUndertowOptions() {
         return OptionMap.EMPTY;
      }

      public int getBufferSize() {
         return 1024;
      }

      public SSLSessionInfo getSslSessionInfo() {
         return this.sslSessionInfo;
      }

      public void setSslSessionInfo(SSLSessionInfo sessionInfo) {
         this.sslSessionInfo = sessionInfo;
      }

      public void addCloseListener(ServerConnection.CloseListener listener) {
      }

      public StreamConnection upgradeChannel() {
         return null;
      }

      public ConduitStreamSinkChannel getSinkChannel() {
         return null;
      }

      public ConduitStreamSourceChannel getSourceChannel() {
         return new ConduitStreamSourceChannel((Configurable)null, (StreamSourceConduit)null);
      }

      protected StreamSinkConduit getSinkConduit(HttpServerExchange exchange, StreamSinkConduit conduit) {
         return conduit;
      }

      protected boolean isUpgradeSupported() {
         return false;
      }

      protected boolean isConnectSupported() {
         return false;
      }

      protected void exchangeComplete(HttpServerExchange exchange) {
      }

      protected void setUpgradeListener(HttpUpgradeListener upgradeListener) {
      }

      protected void setConnectListener(HttpUpgradeListener connectListener) {
      }

      protected void maxEntitySizeUpdated(HttpServerExchange exchange) {
      }

      public String getTransportProtocol() {
         return "mock";
      }

      public boolean isRequestTrailerFieldsSupported() {
         return false;
      }

      // $FF: synthetic method
      MockServerConnection(ByteBufferPool x0, Object x1) {
         this(x0);
      }
   }
}
