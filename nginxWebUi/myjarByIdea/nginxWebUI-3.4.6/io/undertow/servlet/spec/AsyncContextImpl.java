package io.undertow.servlet.spec;

import io.undertow.UndertowLogger;
import io.undertow.server.Connectors;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ExceptionHandler;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.api.LoggingExceptionHandler;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletDispatcher;
import io.undertow.servlet.handlers.ServletDebugPageHandler;
import io.undertow.servlet.handlers.ServletPathMatch;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.util.Headers;
import io.undertow.util.SameThreadExecutor;
import io.undertow.util.WorkerUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xnio.IoUtils;
import org.xnio.XnioExecutor;

public class AsyncContextImpl implements AsyncContext {
   private final List<BoundAsyncListener> asyncListeners = new CopyOnWriteArrayList();
   private final HttpServerExchange exchange;
   private final ServletRequest servletRequest;
   private final ServletResponse servletResponse;
   private final TimeoutTask timeoutTask = new TimeoutTask();
   private final ServletRequestContext servletRequestContext;
   private final boolean requestSupplied;
   private AsyncContextImpl previousAsyncContext;
   private volatile long timeout = 30000L;
   private volatile XnioExecutor.Key timeoutKey;
   private boolean dispatched;
   private boolean initialRequestDone;
   private Thread initiatingThread;
   private final Deque<Runnable> asyncTaskQueue = new ArrayDeque();
   private boolean processingAsyncTask = false;
   private volatile boolean complete = false;
   private volatile boolean completedBeforeInitialRequestDone = false;

   public AsyncContextImpl(final HttpServerExchange exchange, ServletRequest servletRequest, ServletResponse servletResponse, ServletRequestContext servletRequestContext, boolean requestSupplied, AsyncContextImpl previousAsyncContext) {
      this.exchange = exchange;
      this.servletRequest = servletRequest;
      this.servletResponse = servletResponse;
      this.servletRequestContext = servletRequestContext;
      this.requestSupplied = requestSupplied;
      this.previousAsyncContext = previousAsyncContext;
      this.initiatingThread = Thread.currentThread();
      exchange.dispatch(SameThreadExecutor.INSTANCE, new Runnable() {
         public void run() {
            exchange.setDispatchExecutor((Executor)null);
            AsyncContextImpl.this.initialRequestDone();
         }
      });
   }

   public void updateTimeout() {
      XnioExecutor.Key key = this.timeoutKey;
      if (key != null) {
         if (!key.remove()) {
            return;
         }

         this.timeoutKey = null;
      }

      if (this.timeout > 0L && !this.complete) {
         this.timeoutKey = WorkerUtils.executeAfter(this.exchange.getIoThread(), this.timeoutTask, this.timeout, TimeUnit.MILLISECONDS);
      }

   }

   public ServletRequest getRequest() {
      return this.servletRequest;
   }

   public ServletResponse getResponse() {
      return this.servletResponse;
   }

   public boolean hasOriginalRequestAndResponse() {
      return this.servletRequest instanceof HttpServletRequestImpl && this.servletResponse instanceof HttpServletResponseImpl;
   }

   public boolean isInitialRequestDone() {
      return this.initialRequestDone;
   }

   public void dispatch() {
      if (this.dispatched) {
         throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyDispatched();
      } else {
         HttpServletRequestImpl requestImpl = this.servletRequestContext.getOriginalRequest();
         Deployment deployment = requestImpl.getServletContext().getDeployment();
         ServletContainer container;
         String qs;
         if (this.requestSupplied && this.servletRequest instanceof HttpServletRequest) {
            container = deployment.getServletContainer();
            String requestURI = ((HttpServletRequest)this.servletRequest).getRequestURI();
            DeploymentManager context = container.getDeploymentByPath(requestURI);
            if (context == null) {
               throw UndertowServletMessages.MESSAGES.couldNotFindContextToDispatchTo(requestImpl.getOriginalContextPath());
            }

            qs = requestURI.substring(context.getDeployment().getServletContext().getContextPath().length());
            String qs = ((HttpServletRequest)this.servletRequest).getQueryString();
            if (qs != null && !qs.isEmpty()) {
               qs = qs + "?" + qs;
            }

            this.dispatch(context.getDeployment().getServletContext(), qs);
         } else {
            container = deployment.getServletContainer();
            DeploymentManager context = container.getDeploymentByPath(requestImpl.getOriginalContextPath());
            if (context == null) {
               throw UndertowServletMessages.MESSAGES.couldNotFindContextToDispatchTo(requestImpl.getOriginalContextPath());
            }

            String toDispatch = requestImpl.getExchange().getRelativePath();
            qs = requestImpl.getOriginalQueryString();
            if (qs != null && !qs.isEmpty()) {
               toDispatch = toDispatch + "?" + qs;
            }

            this.dispatch(context.getDeployment().getServletContext(), toDispatch);
         }

      }
   }

   private void dispatchAsyncRequest(final ServletDispatcher servletDispatcher, final ServletPathMatch pathInfo, final HttpServerExchange exchange) {
      this.doDispatch(new Runnable() {
         public void run() {
            Connectors.executeRootHandler(new HttpHandler() {
               public void handleRequest(HttpServerExchange exchangex) throws Exception {
                  ServletRequestContext src = (ServletRequestContext)exchangex.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
                  src.setServletRequest(AsyncContextImpl.this.servletRequest);
                  src.setServletResponse(AsyncContextImpl.this.servletResponse);
                  servletDispatcher.dispatchToPath(exchangex, pathInfo, DispatcherType.ASYNC);
               }
            }, exchange);
         }
      });
   }

   public void dispatch(String path) {
      this.dispatch(this.servletRequest.getServletContext(), path);
   }

   public void dispatch(ServletContext context, String path) {
      if (this.dispatched) {
         throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyDispatched();
      } else {
         HttpServletRequestImpl requestImpl = this.servletRequestContext.getOriginalRequest();
         HttpServletResponseImpl responseImpl = this.servletRequestContext.getOriginalResponse();
         HttpServerExchange exchange = requestImpl.getExchange();
         ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setDispatcherType(DispatcherType.ASYNC);
         requestImpl.setAttribute("javax.servlet.async.request_uri", requestImpl.getOriginalRequestURI());
         requestImpl.setAttribute("javax.servlet.async.context_path", requestImpl.getOriginalContextPath());
         requestImpl.setAttribute("javax.servlet.async.servlet_path", requestImpl.getOriginalServletPath());
         requestImpl.setAttribute("javax.servlet.async.query_string", requestImpl.getOriginalQueryString());
         String newQueryString = "";
         int qsPos = path.indexOf("?");
         String newServletPath = path;
         if (qsPos != -1) {
            newQueryString = path.substring(qsPos + 1);
            newServletPath = path.substring(0, qsPos);
         }

         String newRequestUri = context.getContextPath() + newServletPath;
         Map<String, Deque<String>> newQueryParameters = new HashMap();
         String[] var11 = newQueryString.split("&");
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            String part = var11[var13];
            String name = part;
            String value = "";
            int equals = part.indexOf(61);
            if (equals != -1) {
               name = part.substring(0, equals);
               value = part.substring(equals + 1);
            }

            Deque<String> queue = (Deque)newQueryParameters.get(name);
            if (queue == null) {
               newQueryParameters.put(name, queue = new ArrayDeque(1));
            }

            ((Deque)queue).add(value);
         }

         requestImpl.setQueryParameters(newQueryParameters);
         requestImpl.getExchange().setRelativePath(newServletPath);
         requestImpl.getExchange().setQueryString(newQueryString);
         requestImpl.getExchange().setRequestPath(newRequestUri);
         requestImpl.getExchange().setRequestURI(newRequestUri);
         requestImpl.setServletContext((ServletContextImpl)context);
         responseImpl.setServletContext((ServletContextImpl)context);
         Deployment deployment = requestImpl.getServletContext().getDeployment();
         ServletPathMatch info = deployment.getServletPaths().getServletHandlerByPath(newServletPath);
         ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setServletPathMatch(info);
         this.dispatchAsyncRequest(deployment.getServletDispatcher(), info, exchange);
      }
   }

   public synchronized void complete() {
      if (this.complete) {
         UndertowLogger.REQUEST_LOGGER.trace("Ignoring call to AsyncContext.complete() as it has already been called");
      } else {
         this.complete = true;
         if (this.timeoutKey != null) {
            this.timeoutKey.remove();
            this.timeoutKey = null;
         }

         if (!this.dispatched) {
            this.completeInternal(false);
         } else {
            this.onAsyncComplete();
         }

         if (this.previousAsyncContext != null) {
            this.previousAsyncContext.complete();
         }

      }
   }

   public synchronized void completeInternal(boolean forceComplete) {
      Thread currentThread = Thread.currentThread();
      if (!forceComplete && !this.initialRequestDone && currentThread == this.initiatingThread) {
         this.completedBeforeInitialRequestDone = true;
         if (this.dispatched) {
            throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyDispatched();
         }
      } else {
         this.servletRequestContext.getOriginalRequest().asyncRequestDispatched();
         if (!forceComplete && currentThread != this.exchange.getIoThread()) {
            this.servletRequestContext.getOriginalRequest().asyncRequestDispatched();
            this.doDispatch(new Runnable() {
               public void run() {
                  AsyncContextImpl.this.onAsyncCompleteAndRespond();
               }
            });
         } else {
            this.onAsyncCompleteAndRespond();
         }
      }

   }

   public void start(final Runnable run) {
      Executor executor = this.asyncExecutor();
      executor.execute(new Runnable() {
         public void run() {
            AsyncContextImpl.this.servletRequestContext.getCurrentServletContext().invokeRunnable(AsyncContextImpl.this.exchange, run);
         }
      });
   }

   private Executor asyncExecutor() {
      Executor executor = this.servletRequestContext.getDeployment().getAsyncExecutor();
      if (executor == null) {
         executor = this.servletRequestContext.getDeployment().getExecutor();
      }

      if (executor == null) {
         executor = this.exchange.getConnection().getWorker();
      }

      return (Executor)executor;
   }

   public void addListener(AsyncListener listener) {
      this.asyncListeners.add(new BoundAsyncListener(listener, this.servletRequest, this.servletResponse));
   }

   public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
      this.asyncListeners.add(new BoundAsyncListener(listener, servletRequest, servletResponse));
   }

   public boolean isDispatched() {
      return this.dispatched;
   }

   public boolean isCompletedBeforeInitialRequestDone() {
      return this.completedBeforeInitialRequestDone;
   }

   public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
      try {
         InstanceFactory<T> factory = ((ServletContextImpl)this.servletRequest.getServletContext()).getDeployment().getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz);
         final InstanceHandle<T> instance = factory.createInstance();
         this.exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
            public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
               try {
                  instance.release();
               } finally {
                  nextListener.proceed();
               }

            }
         });
         return (AsyncListener)instance.getInstance();
      } catch (Exception var4) {
         throw new ServletException(var4);
      }
   }

   public synchronized void setTimeout(long timeout) {
      if (this.initialRequestDone) {
         throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyReturnedToContainer();
      } else {
         this.timeout = timeout;
      }
   }

   public long getTimeout() {
      return this.timeout;
   }

   public void handleError(Throwable error) {
      this.dispatched = false;
      this.onAsyncError(error);
      if (!this.dispatched) {
         if (!this.exchange.isResponseStarted()) {
            this.exchange.setStatusCode(500);
            this.exchange.getResponseHeaders().clear();
         }

         this.servletRequest.setAttribute("javax.servlet.error.exception", error);
         if (!this.exchange.isResponseStarted()) {
            try {
               boolean errorPage = this.servletRequestContext.displayStackTraces();
               if (errorPage) {
                  ServletDebugPageHandler.handleRequest(this.exchange, this.servletRequestContext, error);
               } else if (this.servletResponse instanceof HttpServletResponse) {
                  ((HttpServletResponse)this.servletResponse).sendError(500);
               } else {
                  this.servletRequestContext.getOriginalResponse().sendError(500);
               }
            } catch (IOException var4) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException(var4);
            } catch (Throwable var5) {
               UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var5);
            }
         } else if (error instanceof IOException) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)error);
         } else {
            ExceptionHandler exceptionHandler = this.servletRequestContext.getDeployment().getDeploymentInfo().getExceptionHandler();
            if (exceptionHandler == null) {
               exceptionHandler = LoggingExceptionHandler.DEFAULT;
            }

            boolean handled = ((ExceptionHandler)exceptionHandler).handleThrowable(this.exchange, this.getRequest(), this.getResponse(), error);
            if (!handled) {
               this.exchange.endExchange();
            }
         }

         if (!this.dispatched) {
            this.complete();
         }
      }

   }

   public synchronized void initialRequestDone() {
      this.initialRequestDone = true;
      if (this.previousAsyncContext != null) {
         this.previousAsyncContext.onAsyncStart(this);
         this.previousAsyncContext = null;
      }

      if (!this.processingAsyncTask) {
         this.processAsyncTask();
      }

      this.initiatingThread = null;
   }

   public synchronized void initialRequestFailed() {
      this.initialRequestDone = true;
   }

   private synchronized void doDispatch(final Runnable runnable) {
      if (this.dispatched) {
         throw UndertowServletMessages.MESSAGES.asyncRequestAlreadyDispatched();
      } else {
         this.dispatched = true;
         final HttpServletRequestImpl request = this.servletRequestContext.getOriginalRequest();
         this.addAsyncTask(new Runnable() {
            public void run() {
               request.asyncRequestDispatched();
               runnable.run();
            }
         });
         if (this.timeoutKey != null) {
            this.timeoutKey.remove();
         }

      }
   }

   public void handleCompletedBeforeInitialRequestDone() {
      assert this.completedBeforeInitialRequestDone;

      this.completeInternal(true);
      this.dispatched = true;
   }

   private synchronized void processAsyncTask() {
      if (this.initialRequestDone) {
         this.updateTimeout();
         Runnable task = (Runnable)this.asyncTaskQueue.poll();
         if (task != null) {
            this.processingAsyncTask = true;
            this.asyncExecutor().execute(new TaskDispatchRunnable(task));
         } else {
            this.processingAsyncTask = false;
         }

      }
   }

   public synchronized void addAsyncTask(Runnable runnable) {
      this.asyncTaskQueue.add(runnable);
      if (!this.processingAsyncTask) {
         this.processAsyncTask();
      }

   }

   private void onAsyncCompleteAndRespond() {
      HttpServletResponseImpl response = this.servletRequestContext.getOriginalResponse();

      try {
         this.onAsyncComplete();
      } catch (RuntimeException var15) {
         UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(var15);
      } finally {
         response.responseDone();

         try {
            this.servletRequestContext.getOriginalRequest().closeAndDrainRequest();
         } catch (IOException var13) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(var13);
         } catch (Throwable var14) {
            UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var14);
         }

      }

   }

   private void onAsyncComplete() {
      final boolean setupRequired = SecurityActions.currentServletRequestContext() == null;
      this.servletRequestContext.getCurrentServletContext().invokeRunnable(this.servletRequestContext.getExchange(), new Runnable() {
         public void run() {
            AsyncContextImpl.this.setupRequestContext(setupRequired);

            try {
               Iterator var1 = AsyncContextImpl.this.asyncListeners.iterator();

               while(var1.hasNext()) {
                  BoundAsyncListener listener = (BoundAsyncListener)var1.next();
                  AsyncEvent event = new AsyncEvent(AsyncContextImpl.this, listener.servletRequest, listener.servletResponse);

                  try {
                     listener.asyncListener.onComplete(event);
                  } catch (IOException var9) {
                     UndertowServletLogger.REQUEST_LOGGER.ioExceptionDispatchingAsyncEvent(var9);
                  } catch (Throwable var10) {
                     UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(var10);
                  }
               }
            } finally {
               AsyncContextImpl.this.tearDownRequestContext(setupRequired);
            }

         }
      });
   }

   private void onAsyncTimeout() {
      Iterator var1 = this.asyncListeners.iterator();

      while(var1.hasNext()) {
         BoundAsyncListener listener = (BoundAsyncListener)var1.next();
         AsyncEvent event = new AsyncEvent(this, listener.servletRequest, listener.servletResponse);

         try {
            listener.asyncListener.onTimeout(event);
         } catch (IOException var5) {
            UndertowServletLogger.REQUEST_LOGGER.ioExceptionDispatchingAsyncEvent(var5);
         } catch (Throwable var6) {
            UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(var6);
         }
      }

   }

   private void onAsyncStart(final AsyncContext newAsyncContext) {
      final boolean setupRequired = SecurityActions.currentServletRequestContext() == null;
      this.servletRequestContext.getCurrentServletContext().invokeRunnable(this.servletRequestContext.getExchange(), new Runnable() {
         public void run() {
            AsyncContextImpl.this.setupRequestContext(setupRequired);

            try {
               Iterator var1 = AsyncContextImpl.this.asyncListeners.iterator();

               while(var1.hasNext()) {
                  BoundAsyncListener listener = (BoundAsyncListener)var1.next();
                  AsyncEvent event = new AsyncEvent(newAsyncContext, listener.servletRequest, listener.servletResponse);

                  try {
                     listener.asyncListener.onStartAsync(event);
                  } catch (IOException var9) {
                     UndertowServletLogger.REQUEST_LOGGER.ioExceptionDispatchingAsyncEvent(var9);
                  } catch (Throwable var10) {
                     UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(var10);
                  }
               }
            } finally {
               AsyncContextImpl.this.tearDownRequestContext(setupRequired);
            }

         }
      });
   }

   private void onAsyncError(final Throwable t) {
      final boolean setupRequired = SecurityActions.currentServletRequestContext() == null;
      this.servletRequestContext.getCurrentServletContext().invokeRunnable(this.servletRequestContext.getExchange(), new Runnable() {
         public void run() {
            AsyncContextImpl.this.setupRequestContext(setupRequired);

            try {
               Iterator var1 = AsyncContextImpl.this.asyncListeners.iterator();

               while(var1.hasNext()) {
                  BoundAsyncListener listener = (BoundAsyncListener)var1.next();
                  AsyncEvent event = new AsyncEvent(AsyncContextImpl.this, listener.servletRequest, listener.servletResponse, t);

                  try {
                     listener.asyncListener.onError(event);
                  } catch (IOException var9) {
                     UndertowServletLogger.REQUEST_LOGGER.ioExceptionDispatchingAsyncEvent(var9);
                  } catch (Throwable var10) {
                     UndertowServletLogger.REQUEST_LOGGER.failureDispatchingAsyncEvent(var10);
                  }
               }
            } finally {
               AsyncContextImpl.this.tearDownRequestContext(setupRequired);
            }

         }
      });
   }

   private void setupRequestContext(boolean setupRequired) {
      if (setupRequired) {
         this.servletRequestContext.getDeployment().getApplicationListeners().requestInitialized(this.servletRequest);
         SecurityActions.setCurrentRequestContext(this.servletRequestContext);
      }

   }

   private void tearDownRequestContext(boolean setupRequired) {
      if (setupRequired) {
         this.servletRequestContext.getDeployment().getApplicationListeners().requestDestroyed(this.servletRequest);
         SecurityActions.clearCurrentServletAttachments();
      }

   }

   private static final class BoundAsyncListener {
      final AsyncListener asyncListener;
      final ServletRequest servletRequest;
      final ServletResponse servletResponse;

      private BoundAsyncListener(AsyncListener asyncListener, ServletRequest servletRequest, ServletResponse servletResponse) {
         this.asyncListener = asyncListener;
         this.servletRequest = servletRequest;
         this.servletResponse = servletResponse;
      }

      // $FF: synthetic method
      BoundAsyncListener(AsyncListener x0, ServletRequest x1, ServletResponse x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private class TaskDispatchRunnable implements Runnable {
      private final Runnable task;

      private TaskDispatchRunnable(Runnable task) {
         this.task = task;
      }

      public void run() {
         try {
            this.task.run();
         } finally {
            AsyncContextImpl.this.processAsyncTask();
         }

      }

      // $FF: synthetic method
      TaskDispatchRunnable(Runnable x1, Object x2) {
         this(x1);
      }
   }

   private final class TimeoutTask implements Runnable {
      private TimeoutTask() {
      }

      public void run() {
         synchronized(AsyncContextImpl.this) {
            if (!AsyncContextImpl.this.dispatched && !AsyncContextImpl.this.complete) {
               AsyncContextImpl.this.addAsyncTask(new Runnable() {
                  public void run() {
                     final boolean setupRequired = SecurityActions.currentServletRequestContext() == null;
                     UndertowServletLogger.REQUEST_LOGGER.debug("Async request timed out");
                     AsyncContextImpl.this.servletRequestContext.getCurrentServletContext().invokeRunnable(AsyncContextImpl.this.servletRequestContext.getExchange(), new Runnable() {
                        public void run() {
                           AsyncContextImpl.this.setupRequestContext(setupRequired);

                           try {
                              AsyncContextImpl.this.onAsyncTimeout();
                              if (!AsyncContextImpl.this.dispatched) {
                                 if (!AsyncContextImpl.this.getResponse().isCommitted()) {
                                    AsyncContextImpl.this.exchange.setPersistent(false);
                                    AsyncContextImpl.this.exchange.getResponseHeaders().put(Headers.CONNECTION, Headers.CLOSE.toString());
                                    Connectors.executeRootHandler(new HttpHandler() {
                                       public void handleRequest(HttpServerExchange exchange) throws Exception {
                                          try {
                                             if (AsyncContextImpl.this.servletResponse instanceof HttpServletResponse) {
                                                ((HttpServletResponse)AsyncContextImpl.this.servletResponse).sendError(500);
                                             } else {
                                                AsyncContextImpl.this.servletRequestContext.getOriginalResponse().sendError(500);
                                             }
                                          } catch (IOException var3) {
                                             UndertowLogger.REQUEST_IO_LOGGER.ioException(var3);
                                          } catch (Throwable var4) {
                                             UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var4);
                                          }

                                       }
                                    }, AsyncContextImpl.this.exchange);
                                 } else {
                                    IoUtils.safeClose((Closeable)AsyncContextImpl.this.exchange.getConnection());
                                 }

                                 if (!AsyncContextImpl.this.dispatched) {
                                    AsyncContextImpl.this.complete();
                                 }
                              }
                           } finally {
                              AsyncContextImpl.this.tearDownRequestContext(setupRequired);
                           }

                        }
                     });
                  }
               });
            }

         }
      }

      // $FF: synthetic method
      TimeoutTask(Object x1) {
         this();
      }
   }
}
