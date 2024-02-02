/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.api.Deployment;
/*     */ import io.undertow.servlet.api.DeploymentManager;
/*     */ import io.undertow.servlet.api.ExceptionHandler;
/*     */ import io.undertow.servlet.api.InstanceFactory;
/*     */ import io.undertow.servlet.api.InstanceHandle;
/*     */ import io.undertow.servlet.api.LoggingExceptionHandler;
/*     */ import io.undertow.servlet.api.ServletContainer;
/*     */ import io.undertow.servlet.api.ServletDispatcher;
/*     */ import io.undertow.servlet.handlers.ServletDebugPageHandler;
/*     */ import io.undertow.servlet.handlers.ServletPathMatch;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.SameThreadExecutor;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.servlet.AsyncContext;
/*     */ import javax.servlet.AsyncEvent;
/*     */ import javax.servlet.AsyncListener;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioWorker;
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
/*     */ public class AsyncContextImpl
/*     */   implements AsyncContext
/*     */ {
/*  74 */   private final List<BoundAsyncListener> asyncListeners = new CopyOnWriteArrayList<>();
/*     */   
/*     */   private final HttpServerExchange exchange;
/*     */   private final ServletRequest servletRequest;
/*     */   private final ServletResponse servletResponse;
/*  79 */   private final TimeoutTask timeoutTask = new TimeoutTask();
/*     */   
/*     */   private final ServletRequestContext servletRequestContext;
/*     */   
/*     */   private final boolean requestSupplied;
/*     */   
/*     */   private AsyncContextImpl previousAsyncContext;
/*     */   
/*  87 */   private volatile long timeout = 30000L;
/*     */   
/*     */   private volatile XnioExecutor.Key timeoutKey;
/*     */   
/*     */   private boolean dispatched;
/*     */   
/*     */   private boolean initialRequestDone;
/*     */   private Thread initiatingThread;
/*  95 */   private final Deque<Runnable> asyncTaskQueue = new ArrayDeque<>();
/*     */   private boolean processingAsyncTask = false;
/*     */   private volatile boolean complete = false;
/*     */   private volatile boolean completedBeforeInitialRequestDone = false;
/*     */   
/*     */   public AsyncContextImpl(final HttpServerExchange exchange, ServletRequest servletRequest, ServletResponse servletResponse, ServletRequestContext servletRequestContext, boolean requestSupplied, AsyncContextImpl previousAsyncContext) {
/* 101 */     this.exchange = exchange;
/* 102 */     this.servletRequest = servletRequest;
/* 103 */     this.servletResponse = servletResponse;
/* 104 */     this.servletRequestContext = servletRequestContext;
/* 105 */     this.requestSupplied = requestSupplied;
/* 106 */     this.previousAsyncContext = previousAsyncContext;
/* 107 */     this.initiatingThread = Thread.currentThread();
/* 108 */     exchange.dispatch(SameThreadExecutor.INSTANCE, new Runnable()
/*     */         {
/*     */           public void run() {
/* 111 */             exchange.setDispatchExecutor(null);
/* 112 */             AsyncContextImpl.this.initialRequestDone();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void updateTimeout() {
/* 118 */     XnioExecutor.Key key = this.timeoutKey;
/* 119 */     if (key != null) {
/* 120 */       if (!key.remove()) {
/*     */         return;
/*     */       }
/* 123 */       this.timeoutKey = null;
/*     */     } 
/*     */     
/* 126 */     if (this.timeout > 0L && !this.complete) {
/* 127 */       this.timeoutKey = WorkerUtils.executeAfter(this.exchange.getIoThread(), this.timeoutTask, this.timeout, TimeUnit.MILLISECONDS);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletRequest getRequest() {
/* 133 */     return this.servletRequest;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletResponse getResponse() {
/* 138 */     return this.servletResponse;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasOriginalRequestAndResponse() {
/* 143 */     return (this.servletRequest instanceof HttpServletRequestImpl && this.servletResponse instanceof HttpServletResponseImpl);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInitialRequestDone() {
/* 148 */     return this.initialRequestDone;
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispatch() {
/* 153 */     if (this.dispatched) {
/* 154 */       throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyDispatched();
/*     */     }
/* 156 */     HttpServletRequestImpl requestImpl = this.servletRequestContext.getOriginalRequest();
/* 157 */     Deployment deployment = requestImpl.getServletContext().getDeployment();
/*     */     
/* 159 */     if (this.requestSupplied && this.servletRequest instanceof HttpServletRequest) {
/* 160 */       ServletContainer container = deployment.getServletContainer();
/* 161 */       String requestURI = ((HttpServletRequest)this.servletRequest).getRequestURI();
/* 162 */       DeploymentManager context = container.getDeploymentByPath(requestURI);
/* 163 */       if (context == null) {
/* 164 */         throw UndertowServletMessages.MESSAGES.couldNotFindContextToDispatchTo(requestImpl.getOriginalContextPath());
/*     */       }
/* 166 */       String toDispatch = requestURI.substring(context.getDeployment().getServletContext().getContextPath().length());
/* 167 */       String qs = ((HttpServletRequest)this.servletRequest).getQueryString();
/* 168 */       if (qs != null && !qs.isEmpty()) {
/* 169 */         toDispatch = toDispatch + "?" + qs;
/*     */       }
/* 171 */       dispatch(context.getDeployment().getServletContext(), toDispatch);
/*     */     }
/*     */     else {
/*     */       
/* 175 */       ServletContainer container = deployment.getServletContainer();
/* 176 */       DeploymentManager context = container.getDeploymentByPath(requestImpl.getOriginalContextPath());
/* 177 */       if (context == null)
/*     */       {
/* 179 */         throw UndertowServletMessages.MESSAGES.couldNotFindContextToDispatchTo(requestImpl.getOriginalContextPath());
/*     */       }
/*     */       
/* 182 */       String toDispatch = requestImpl.getExchange().getRelativePath();
/* 183 */       String qs = requestImpl.getOriginalQueryString();
/* 184 */       if (qs != null && !qs.isEmpty()) {
/* 185 */         toDispatch = toDispatch + "?" + qs;
/*     */       }
/* 187 */       dispatch(context.getDeployment().getServletContext(), toDispatch);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void dispatchAsyncRequest(final ServletDispatcher servletDispatcher, final ServletPathMatch pathInfo, final HttpServerExchange exchange) {
/* 192 */     doDispatch(new Runnable()
/*     */         {
/*     */           public void run() {
/* 195 */             Connectors.executeRootHandler(new HttpHandler()
/*     */                 {
/*     */                   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 198 */                     ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 199 */                     src.setServletRequest(AsyncContextImpl.this.servletRequest);
/* 200 */                     src.setServletResponse(AsyncContextImpl.this.servletResponse);
/* 201 */                     servletDispatcher.dispatchToPath(exchange, pathInfo, DispatcherType.ASYNC);
/*     */                   }
/*     */                 }exchange);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispatch(String path) {
/* 210 */     dispatch(this.servletRequest.getServletContext(), path);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void dispatch(ServletContext context, String path) {
/* 216 */     if (this.dispatched) {
/* 217 */       throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyDispatched();
/*     */     }
/*     */     
/* 220 */     HttpServletRequestImpl requestImpl = this.servletRequestContext.getOriginalRequest();
/* 221 */     HttpServletResponseImpl responseImpl = this.servletRequestContext.getOriginalResponse();
/* 222 */     HttpServerExchange exchange = requestImpl.getExchange();
/*     */     
/* 224 */     ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setDispatcherType(DispatcherType.ASYNC);
/*     */     
/* 226 */     requestImpl.setAttribute("javax.servlet.async.request_uri", requestImpl.getOriginalRequestURI());
/* 227 */     requestImpl.setAttribute("javax.servlet.async.context_path", requestImpl.getOriginalContextPath());
/* 228 */     requestImpl.setAttribute("javax.servlet.async.servlet_path", requestImpl.getOriginalServletPath());
/* 229 */     requestImpl.setAttribute("javax.servlet.async.query_string", requestImpl.getOriginalQueryString());
/*     */     
/* 231 */     String newQueryString = "";
/* 232 */     int qsPos = path.indexOf("?");
/* 233 */     String newServletPath = path;
/* 234 */     if (qsPos != -1) {
/* 235 */       newQueryString = newServletPath.substring(qsPos + 1);
/* 236 */       newServletPath = newServletPath.substring(0, qsPos);
/*     */     } 
/* 238 */     String newRequestUri = context.getContextPath() + newServletPath;
/*     */ 
/*     */     
/* 241 */     Map<String, Deque<String>> newQueryParameters = new HashMap<>();
/* 242 */     for (String part : newQueryString.split("&")) {
/* 243 */       String name = part;
/* 244 */       String value = "";
/* 245 */       int equals = part.indexOf('=');
/* 246 */       if (equals != -1) {
/* 247 */         name = part.substring(0, equals);
/* 248 */         value = part.substring(equals + 1);
/*     */       } 
/* 250 */       Deque<String> queue = newQueryParameters.get(name);
/* 251 */       if (queue == null) {
/* 252 */         newQueryParameters.put(name, queue = new ArrayDeque<>(1));
/*     */       }
/* 254 */       queue.add(value);
/*     */     } 
/* 256 */     requestImpl.setQueryParameters(newQueryParameters);
/*     */     
/* 258 */     requestImpl.getExchange().setRelativePath(newServletPath);
/* 259 */     requestImpl.getExchange().setQueryString(newQueryString);
/* 260 */     requestImpl.getExchange().setRequestPath(newRequestUri);
/* 261 */     requestImpl.getExchange().setRequestURI(newRequestUri);
/* 262 */     requestImpl.setServletContext((ServletContextImpl)context);
/* 263 */     responseImpl.setServletContext((ServletContextImpl)context);
/*     */     
/* 265 */     Deployment deployment = requestImpl.getServletContext().getDeployment();
/* 266 */     ServletPathMatch info = deployment.getServletPaths().getServletHandlerByPath(newServletPath);
/* 267 */     ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setServletPathMatch(info);
/*     */     
/* 269 */     dispatchAsyncRequest(deployment.getServletDispatcher(), info, exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void complete() {
/* 274 */     if (this.complete) {
/* 275 */       UndertowLogger.REQUEST_LOGGER.trace("Ignoring call to AsyncContext.complete() as it has already been called");
/*     */       return;
/*     */     } 
/* 278 */     this.complete = true;
/* 279 */     if (this.timeoutKey != null) {
/* 280 */       this.timeoutKey.remove();
/* 281 */       this.timeoutKey = null;
/*     */     } 
/* 283 */     if (!this.dispatched) {
/* 284 */       completeInternal(false);
/*     */     } else {
/* 286 */       onAsyncComplete();
/*     */     } 
/* 288 */     if (this.previousAsyncContext != null) {
/* 289 */       this.previousAsyncContext.complete();
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void completeInternal(boolean forceComplete) {
/* 294 */     Thread currentThread = Thread.currentThread();
/* 295 */     if (!forceComplete && !this.initialRequestDone && currentThread == this.initiatingThread) {
/* 296 */       this.completedBeforeInitialRequestDone = true;
/* 297 */       if (this.dispatched) {
/* 298 */         throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyDispatched();
/*     */       }
/*     */     } else {
/* 301 */       this.servletRequestContext.getOriginalRequest().asyncRequestDispatched();
/* 302 */       if (forceComplete || currentThread == this.exchange.getIoThread()) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 307 */         onAsyncCompleteAndRespond();
/*     */       } else {
/* 309 */         this.servletRequestContext.getOriginalRequest().asyncRequestDispatched();
/* 310 */         doDispatch(new Runnable()
/*     */             {
/*     */               public void run() {
/* 313 */                 AsyncContextImpl.this.onAsyncCompleteAndRespond();
/*     */               }
/*     */             });
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void start(final Runnable run) {
/* 322 */     Executor executor = asyncExecutor();
/* 323 */     executor.execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 326 */             AsyncContextImpl.this.servletRequestContext.getCurrentServletContext().invokeRunnable(AsyncContextImpl.this.exchange, run);
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private Executor asyncExecutor() {
/*     */     XnioWorker xnioWorker;
/* 333 */     Executor executor = this.servletRequestContext.getDeployment().getAsyncExecutor();
/* 334 */     if (executor == null) {
/* 335 */       executor = this.servletRequestContext.getDeployment().getExecutor();
/*     */     }
/* 337 */     if (executor == null) {
/* 338 */       xnioWorker = this.exchange.getConnection().getWorker();
/*     */     }
/* 340 */     return (Executor)xnioWorker;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addListener(AsyncListener listener) {
/* 346 */     this.asyncListeners.add(new BoundAsyncListener(listener, this.servletRequest, this.servletResponse));
/*     */   }
/*     */ 
/*     */   
/*     */   public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
/* 351 */     this.asyncListeners.add(new BoundAsyncListener(listener, servletRequest, servletResponse));
/*     */   }
/*     */   
/*     */   public boolean isDispatched() {
/* 355 */     return this.dispatched;
/*     */   }
/*     */   
/*     */   public boolean isCompletedBeforeInitialRequestDone() {
/* 359 */     return this.completedBeforeInitialRequestDone;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
/*     */     try {
/* 365 */       InstanceFactory<T> factory = ((ServletContextImpl)this.servletRequest.getServletContext()).getDeployment().getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz);
/*     */       
/* 367 */       final InstanceHandle<T> instance = factory.createInstance();
/* 368 */       this.exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */           {
/*     */             public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/*     */               try {
/* 372 */                 instance.release();
/*     */               } finally {
/* 374 */                 nextListener.proceed();
/*     */               } 
/*     */             }
/*     */           });
/* 378 */       return (T)instance.getInstance();
/* 379 */     } catch (Exception e) {
/* 380 */       throw new ServletException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setTimeout(long timeout) {
/* 386 */     if (this.initialRequestDone) {
/* 387 */       throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyReturnedToContainer();
/*     */     }
/* 389 */     this.timeout = timeout;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTimeout() {
/* 394 */     return this.timeout;
/*     */   }
/*     */   
/*     */   public void handleError(Throwable error) {
/* 398 */     this.dispatched = false;
/* 399 */     onAsyncError(error);
/* 400 */     if (!this.dispatched) {
/* 401 */       if (!this.exchange.isResponseStarted()) {
/* 402 */         this.exchange.setStatusCode(500);
/* 403 */         this.exchange.getResponseHeaders().clear();
/*     */       } 
/* 405 */       this.servletRequest.setAttribute("javax.servlet.error.exception", error);
/* 406 */       if (!this.exchange.isResponseStarted()) {
/*     */         try {
/* 408 */           boolean errorPage = this.servletRequestContext.displayStackTraces();
/* 409 */           if (errorPage) {
/* 410 */             ServletDebugPageHandler.handleRequest(this.exchange, this.servletRequestContext, error);
/*     */           }
/* 412 */           else if (this.servletResponse instanceof HttpServletResponse) {
/* 413 */             ((HttpServletResponse)this.servletResponse).sendError(500);
/*     */           } else {
/* 415 */             this.servletRequestContext.getOriginalResponse().sendError(500);
/*     */           }
/*     */         
/* 418 */         } catch (IOException e) {
/* 419 */           UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 420 */         } catch (Throwable t) {
/* 421 */           UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/*     */         } 
/* 423 */       } else if (error instanceof IOException) {
/* 424 */         UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)error);
/*     */       } else {
/* 426 */         LoggingExceptionHandler loggingExceptionHandler; ExceptionHandler exceptionHandler = this.servletRequestContext.getDeployment().getDeploymentInfo().getExceptionHandler();
/* 427 */         if (exceptionHandler == null) {
/* 428 */           loggingExceptionHandler = LoggingExceptionHandler.DEFAULT;
/*     */         }
/* 430 */         boolean handled = loggingExceptionHandler.handleThrowable(this.exchange, getRequest(), getResponse(), error);
/* 431 */         if (!handled) {
/* 432 */           this.exchange.endExchange();
/*     */         }
/*     */       } 
/* 435 */       if (!this.dispatched) {
/* 436 */         complete();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void initialRequestDone() {
/* 447 */     this.initialRequestDone = true;
/* 448 */     if (this.previousAsyncContext != null) {
/* 449 */       this.previousAsyncContext.onAsyncStart(this);
/* 450 */       this.previousAsyncContext = null;
/*     */     } 
/* 452 */     if (!this.processingAsyncTask) {
/* 453 */       processAsyncTask();
/*     */     }
/* 455 */     this.initiatingThread = null;
/*     */   }
/*     */   
/*     */   public synchronized void initialRequestFailed() {
/* 459 */     this.initialRequestDone = true;
/*     */   }
/*     */   
/*     */   private synchronized void doDispatch(final Runnable runnable) {
/* 463 */     if (this.dispatched) {
/* 464 */       throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyDispatched();
/*     */     }
/* 466 */     this.dispatched = true;
/* 467 */     final HttpServletRequestImpl request = this.servletRequestContext.getOriginalRequest();
/* 468 */     addAsyncTask(new Runnable()
/*     */         {
/*     */           public void run() {
/* 471 */             request.asyncRequestDispatched();
/* 472 */             runnable.run();
/*     */           }
/*     */         });
/* 475 */     if (this.timeoutKey != null) {
/* 476 */       this.timeoutKey.remove();
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleCompletedBeforeInitialRequestDone() {
/* 481 */     assert this.completedBeforeInitialRequestDone;
/* 482 */     completeInternal(true);
/* 483 */     this.dispatched = true;
/*     */   }
/*     */   
/*     */   private final class TimeoutTask
/*     */     implements Runnable {
/*     */     private TimeoutTask() {}
/*     */     
/*     */     public void run() {
/* 491 */       synchronized (AsyncContextImpl.this) {
/* 492 */         if (!AsyncContextImpl.this.dispatched && !AsyncContextImpl.this.complete) {
/* 493 */           AsyncContextImpl.this.addAsyncTask(new Runnable()
/*     */               {
/*     */                 public void run()
/*     */                 {
/* 497 */                   final boolean setupRequired = (SecurityActions.currentServletRequestContext() == null);
/* 498 */                   UndertowServletLogger.REQUEST_LOGGER.debug("Async request timed out");
/* 499 */                   AsyncContextImpl.this.servletRequestContext.getCurrentServletContext().invokeRunnable(AsyncContextImpl.this.servletRequestContext.getExchange(), new Runnable()
/*     */                       {
/*     */                         
/*     */                         public void run()
/*     */                         {
/* 504 */                           AsyncContextImpl.this.setupRequestContext(setupRequired);
/*     */                           try {
/* 506 */                             AsyncContextImpl.this.onAsyncTimeout();
/* 507 */                             if (!AsyncContextImpl.this.dispatched) {
/* 508 */                               if (!AsyncContextImpl.this.getResponse().isCommitted()) {
/*     */                                 
/* 510 */                                 AsyncContextImpl.this.exchange.setPersistent(false);
/* 511 */                                 AsyncContextImpl.this.exchange.getResponseHeaders().put(Headers.CONNECTION, Headers.CLOSE.toString());
/* 512 */                                 Connectors.executeRootHandler(new HttpHandler()
/*     */                                     {
/*     */                                       public void handleRequest(HttpServerExchange exchange) throws Exception
/*     */                                       {
/*     */                                         try {
/* 517 */                                           if (AsyncContextImpl.this.servletResponse instanceof HttpServletResponse) {
/* 518 */                                             ((HttpServletResponse)AsyncContextImpl.this.servletResponse).sendError(500);
/*     */                                           } else {
/* 520 */                                             AsyncContextImpl.this.servletRequestContext.getOriginalResponse().sendError(500);
/*     */                                           } 
/* 522 */                                         } catch (IOException e) {
/* 523 */                                           UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 524 */                                         } catch (Throwable t) {
/* 525 */                                           UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/*     */                                         } 
/*     */                                       }
/* 528 */                                     },  AsyncContextImpl.this.exchange);
/*     */                               } else {
/*     */                                 
/* 531 */                                 IoUtils.safeClose((Closeable)AsyncContextImpl.this.exchange.getConnection());
/*     */                               } 
/* 533 */                               if (!AsyncContextImpl.this.dispatched) {
/* 534 */                                 AsyncContextImpl.this.complete();
/*     */                               }
/*     */                             } 
/*     */                           } finally {
/* 538 */                             AsyncContextImpl.this.tearDownRequestContext(setupRequired);
/*     */                           } 
/*     */                         }
/*     */                       });
/*     */                 }
/*     */               });
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized void processAsyncTask() {
/* 550 */     if (!this.initialRequestDone) {
/*     */       return;
/*     */     }
/* 553 */     updateTimeout();
/* 554 */     Runnable task = this.asyncTaskQueue.poll();
/* 555 */     if (task != null) {
/* 556 */       this.processingAsyncTask = true;
/* 557 */       asyncExecutor().execute(new TaskDispatchRunnable(task));
/*     */     } else {
/* 559 */       this.processingAsyncTask = false;
/*     */     } 
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
/*     */   public synchronized void addAsyncTask(Runnable runnable) {
/* 574 */     this.asyncTaskQueue.add(runnable);
/* 575 */     if (!this.processingAsyncTask)
/* 576 */       processAsyncTask(); 
/*     */   }
/*     */   
/*     */   private class TaskDispatchRunnable
/*     */     implements Runnable
/*     */   {
/*     */     private final Runnable task;
/*     */     
/*     */     private TaskDispatchRunnable(Runnable task) {
/* 585 */       this.task = task;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/*     */       try {
/* 591 */         this.task.run();
/*     */       } finally {
/* 593 */         AsyncContextImpl.this.processAsyncTask();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void onAsyncCompleteAndRespond() {
/* 599 */     HttpServletResponseImpl response = this.servletRequestContext.getOriginalResponse();
/*     */     try {
/* 601 */       onAsyncComplete();
/* 602 */     } catch (RuntimeException e) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 612 */       UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(e);
/*     */     } finally {
/* 614 */       response.responseDone();
/*     */       try {
/* 616 */         this.servletRequestContext.getOriginalRequest().closeAndDrainRequest();
/* 617 */       } catch (IOException e) {
/* 618 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 619 */       } catch (Throwable t) {
/* 620 */         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void onAsyncComplete() {
/* 626 */     final boolean setupRequired = (SecurityActions.currentServletRequestContext() == null);
/* 627 */     this.servletRequestContext.getCurrentServletContext().invokeRunnable(this.servletRequestContext.getExchange(), new Runnable()
/*     */         {
/*     */           
/*     */           public void run()
/*     */           {
/* 632 */             AsyncContextImpl.this.setupRequestContext(setupRequired);
/*     */             try {
/* 634 */               for (AsyncContextImpl.BoundAsyncListener listener : AsyncContextImpl.this.asyncListeners) {
/* 635 */                 AsyncEvent event = new AsyncEvent(AsyncContextImpl.this, listener.servletRequest, listener.servletResponse);
/*     */                 try {
/* 637 */                   listener.asyncListener.onComplete(event);
/* 638 */                 } catch (IOException e) {
/* 639 */                   UndertowServletLogger.REQUEST_LOGGER.ioExceptionDispatchingAsyncEvent(e);
/* 640 */                 } catch (Throwable t) {
/* 641 */                   UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(t);
/*     */                 } 
/*     */               } 
/*     */             } finally {
/* 645 */               AsyncContextImpl.this.tearDownRequestContext(setupRequired);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private void onAsyncTimeout() {
/* 652 */     for (BoundAsyncListener listener : this.asyncListeners) {
/* 653 */       AsyncEvent event = new AsyncEvent(this, listener.servletRequest, listener.servletResponse);
/*     */       try {
/* 655 */         listener.asyncListener.onTimeout(event);
/* 656 */       } catch (IOException e) {
/* 657 */         UndertowServletLogger.REQUEST_LOGGER.ioExceptionDispatchingAsyncEvent(e);
/* 658 */       } catch (Throwable t) {
/* 659 */         UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(t);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void onAsyncStart(final AsyncContext newAsyncContext) {
/* 665 */     final boolean setupRequired = (SecurityActions.currentServletRequestContext() == null);
/*     */     
/* 667 */     this.servletRequestContext.getCurrentServletContext().invokeRunnable(this.servletRequestContext.getExchange(), new Runnable()
/*     */         {
/*     */           
/*     */           public void run()
/*     */           {
/* 672 */             AsyncContextImpl.this.setupRequestContext(setupRequired);
/*     */             try {
/* 674 */               for (AsyncContextImpl.BoundAsyncListener listener : AsyncContextImpl.this.asyncListeners) {
/*     */                 
/* 676 */                 AsyncEvent event = new AsyncEvent(newAsyncContext, listener.servletRequest, listener.servletResponse);
/*     */                 try {
/* 678 */                   listener.asyncListener.onStartAsync(event);
/* 679 */                 } catch (IOException e) {
/* 680 */                   UndertowServletLogger.REQUEST_LOGGER.ioExceptionDispatchingAsyncEvent(e);
/* 681 */                 } catch (Throwable t) {
/* 682 */                   UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(t);
/*     */                 } 
/*     */               } 
/*     */             } finally {
/* 686 */               AsyncContextImpl.this.tearDownRequestContext(setupRequired);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private void onAsyncError(final Throwable t) {
/* 693 */     final boolean setupRequired = (SecurityActions.currentServletRequestContext() == null);
/* 694 */     this.servletRequestContext.getCurrentServletContext().invokeRunnable(this.servletRequestContext.getExchange(), new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 698 */             AsyncContextImpl.this.setupRequestContext(setupRequired);
/*     */             try {
/* 700 */               for (AsyncContextImpl.BoundAsyncListener listener : AsyncContextImpl.this.asyncListeners) {
/* 701 */                 AsyncEvent event = new AsyncEvent(AsyncContextImpl.this, listener.servletRequest, listener.servletResponse, t);
/*     */                 try {
/* 703 */                   listener.asyncListener.onError(event);
/* 704 */                 } catch (IOException e) {
/* 705 */                   UndertowServletLogger.REQUEST_LOGGER.ioExceptionDispatchingAsyncEvent(e);
/* 706 */                 } catch (Throwable t) {
/* 707 */                   UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(t);
/*     */                 } 
/*     */               } 
/*     */             } finally {
/* 711 */               AsyncContextImpl.this.tearDownRequestContext(setupRequired);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private void setupRequestContext(boolean setupRequired) {
/* 718 */     if (setupRequired) {
/* 719 */       this.servletRequestContext.getDeployment().getApplicationListeners().requestInitialized(this.servletRequest);
/* 720 */       SecurityActions.setCurrentRequestContext(this.servletRequestContext);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void tearDownRequestContext(boolean setupRequired) {
/* 725 */     if (setupRequired) {
/* 726 */       this.servletRequestContext.getDeployment().getApplicationListeners().requestDestroyed(this.servletRequest);
/* 727 */       SecurityActions.clearCurrentServletAttachments();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class BoundAsyncListener {
/*     */     final AsyncListener asyncListener;
/*     */     final ServletRequest servletRequest;
/*     */     final ServletResponse servletResponse;
/*     */     
/*     */     private BoundAsyncListener(AsyncListener asyncListener, ServletRequest servletRequest, ServletResponse servletResponse) {
/* 737 */       this.asyncListener = asyncListener;
/* 738 */       this.servletRequest = servletRequest;
/* 739 */       this.servletResponse = servletResponse;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\AsyncContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */