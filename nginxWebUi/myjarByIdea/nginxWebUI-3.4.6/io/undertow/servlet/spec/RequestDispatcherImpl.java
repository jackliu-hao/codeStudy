package io.undertow.servlet.spec;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.handlers.ServletChain;
import io.undertow.servlet.handlers.ServletPathMatch;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.util.QueryParameterUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Deque;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestDispatcherImpl implements RequestDispatcher {
   private final String path;
   private final ServletContextImpl servletContext;
   private final ServletChain chain;
   private final ServletPathMatch pathMatch;
   private final boolean named;

   public RequestDispatcherImpl(String path, ServletContextImpl servletContext) {
      this.path = path;
      this.servletContext = servletContext;
      String basePath = path;
      int qPos = path.indexOf("?");
      if (qPos != -1) {
         basePath = path.substring(0, qPos);
      }

      int mPos = basePath.indexOf(";");
      if (mPos != -1) {
         basePath = basePath.substring(0, mPos);
      }

      this.pathMatch = servletContext.getDeployment().getServletPaths().getServletHandlerByPath(basePath);
      this.chain = this.pathMatch.getServletChain();
      this.named = false;
   }

   public RequestDispatcherImpl(ServletChain chain, ServletContextImpl servletContext) {
      this.chain = chain;
      this.named = true;
      this.servletContext = servletContext;
      this.path = null;
      this.pathMatch = null;
   }

   public void forward(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
      if (System.getSecurityManager() != null) {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() throws Exception {
                  RequestDispatcherImpl.this.forwardImplSetup(request, response);
                  return null;
               }
            });
         } catch (PrivilegedActionException var4) {
            if (var4.getCause() instanceof ServletException) {
               throw (ServletException)var4.getCause();
            }

            if (var4.getCause() instanceof IOException) {
               throw (IOException)var4.getCause();
            }

            if (var4.getCause() instanceof RuntimeException) {
               throw (RuntimeException)var4.getCause();
            }

            throw new RuntimeException(var4.getCause());
         }
      } else {
         this.forwardImplSetup(request, response);
      }

   }

   private void forwardImplSetup(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
      final ServletRequestContext servletRequestContext = SecurityActions.currentServletRequestContext();
      if (servletRequestContext == null) {
         UndertowLogger.REQUEST_LOGGER.debugf("No servlet request context for %s, dispatching mock request", request);
         this.mock(request, response);
      } else {
         ServletContextImpl oldServletContext = null;
         HttpSessionImpl oldSession = null;
         if (servletRequestContext.getCurrentServletContext() != this.servletContext) {
            try {
               oldServletContext = servletRequestContext.getCurrentServletContext();
               oldSession = servletRequestContext.getSession();
               servletRequestContext.setSession((HttpSessionImpl)null);
               servletRequestContext.setCurrentServletContext(this.servletContext);
               this.servletContext.invokeAction(servletRequestContext.getExchange(), new ThreadSetupHandler.Action<Void, Object>() {
                  public Void call(HttpServerExchange exchange, Object context) throws Exception {
                     RequestDispatcherImpl.this.forwardImpl(request, response, servletRequestContext);
                     return null;
                  }
               });
            } finally {
               servletRequestContext.setSession(oldSession);
               servletRequestContext.setCurrentServletContext(oldServletContext);
               servletRequestContext.getCurrentServletContext().updateSessionAccessTime(servletRequestContext.getExchange());
            }
         } else {
            this.forwardImpl(request, response, servletRequestContext);
         }

      }
   }

   private void forwardImpl(ServletRequest request, ServletResponse response, ServletRequestContext servletRequestContext) throws ServletException, IOException {
      HttpServletRequestImpl requestImpl = servletRequestContext.getOriginalRequest();
      HttpServletResponseImpl responseImpl = servletRequestContext.getOriginalResponse();
      if (!this.servletContext.getDeployment().getDeploymentInfo().isAllowNonStandardWrappers()) {
         if (servletRequestContext.getOriginalRequest() != request && !(request instanceof ServletRequestWrapper)) {
            throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(request);
         }

         if (servletRequestContext.getOriginalResponse() != response && !(response instanceof ServletResponseWrapper)) {
            throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(response);
         }
      }

      response.resetBuffer();
      ServletRequest oldRequest = servletRequestContext.getServletRequest();
      ServletResponse oldResponse = servletRequestContext.getServletResponse();
      Map<String, Deque<String>> queryParameters = requestImpl.getQueryParameters();
      request.removeAttribute("javax.servlet.include.request_uri");
      request.removeAttribute("javax.servlet.include.context_path");
      request.removeAttribute("javax.servlet.include.servlet_path");
      request.removeAttribute("javax.servlet.include.path_info");
      request.removeAttribute("javax.servlet.include.query_string");
      String oldURI = requestImpl.getExchange().getRequestURI();
      String oldRequestPath = requestImpl.getExchange().getRequestPath();
      String oldPath = requestImpl.getExchange().getRelativePath();
      ServletPathMatch oldServletPathMatch = ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletPathMatch();
      if (!this.named) {
         if (request.getAttribute("javax.servlet.forward.request_uri") == null) {
            requestImpl.setAttribute("javax.servlet.forward.request_uri", requestImpl.getRequestURI());
            requestImpl.setAttribute("javax.servlet.forward.context_path", requestImpl.getContextPath());
            requestImpl.setAttribute("javax.servlet.forward.servlet_path", requestImpl.getServletPath());
            requestImpl.setAttribute("javax.servlet.forward.path_info", requestImpl.getPathInfo());
            requestImpl.setAttribute("javax.servlet.forward.query_string", requestImpl.getQueryString());
         }

         int qsPos = this.path.indexOf("?");
         String newServletPath = this.path;
         String newRequestUri;
         if (qsPos != -1) {
            newRequestUri = newServletPath.substring(qsPos + 1);
            newServletPath = newServletPath.substring(0, qsPos);
            String encoding = QueryParameterUtils.getQueryParamEncoding(servletRequestContext.getExchange());
            Map<String, Deque<String>> newQueryParameters = QueryParameterUtils.mergeQueryParametersWithNewQueryString(queryParameters, newRequestUri, encoding);
            requestImpl.getExchange().setQueryString(newRequestUri);
            requestImpl.setQueryParameters(newQueryParameters);
         }

         newRequestUri = this.servletContext.getContextPath() + newServletPath;
         requestImpl.getExchange().setRelativePath(newServletPath);
         requestImpl.getExchange().setRequestPath(newRequestUri);
         requestImpl.getExchange().setRequestURI(newRequestUri);
         ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setServletPathMatch(this.pathMatch);
         requestImpl.setServletContext(this.servletContext);
         responseImpl.setServletContext(this.servletContext);
      }

      boolean var25 = false;

      try {
         var25 = true;
         servletRequestContext.setServletRequest(request);
         servletRequestContext.setServletResponse(response);
         if (this.named) {
            this.servletContext.getDeployment().getServletDispatcher().dispatchToServlet(requestImpl.getExchange(), this.chain, DispatcherType.FORWARD);
         } else {
            this.servletContext.getDeployment().getServletDispatcher().dispatchToPath(requestImpl.getExchange(), this.pathMatch, DispatcherType.FORWARD);
         }

         if (!request.isAsyncStarted()) {
            if (response instanceof HttpServletResponseImpl) {
               responseImpl.closeStreamAndWriter();
               var25 = false;
            } else {
               try {
                  PrintWriter writer = response.getWriter();
                  writer.flush();
                  writer.close();
                  var25 = false;
               } catch (IllegalStateException var26) {
                  ServletOutputStream outputStream = response.getOutputStream();
                  outputStream.flush();
                  outputStream.close();
                  var25 = false;
               }
            }
         } else {
            var25 = false;
         }
      } catch (ServletException var27) {
         throw var27;
      } catch (IOException var28) {
         throw var28;
      } catch (Exception var29) {
         throw new RuntimeException(var29);
      } finally {
         if (var25) {
            servletRequestContext.setServletRequest(oldRequest);
            servletRequestContext.setServletResponse(oldResponse);
            boolean var19 = servletRequestContext.getDeployment().getDeploymentInfo().isPreservePathOnForward();
            if (var19) {
               requestImpl.getExchange().setRelativePath(oldPath);
               ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setServletPathMatch(oldServletPathMatch);
               requestImpl.getExchange().setRequestPath(oldRequestPath);
               requestImpl.getExchange().setRequestURI(oldURI);
            }

         }
      }

      servletRequestContext.setServletRequest(oldRequest);
      servletRequestContext.setServletResponse(oldResponse);
      boolean preservePath = servletRequestContext.getDeployment().getDeploymentInfo().isPreservePathOnForward();
      if (preservePath) {
         requestImpl.getExchange().setRelativePath(oldPath);
         ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setServletPathMatch(oldServletPathMatch);
         requestImpl.getExchange().setRequestPath(oldRequestPath);
         requestImpl.getExchange().setRequestURI(oldURI);
      }

   }

   public void include(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
      if (System.getSecurityManager() != null) {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() throws Exception {
                  RequestDispatcherImpl.this.setupIncludeImpl(request, response);
                  return null;
               }
            });
         } catch (PrivilegedActionException var4) {
            if (var4.getCause() instanceof ServletException) {
               throw (ServletException)var4.getCause();
            }

            if (var4.getCause() instanceof IOException) {
               throw (IOException)var4.getCause();
            }

            if (var4.getCause() instanceof RuntimeException) {
               throw (RuntimeException)var4.getCause();
            }

            throw new RuntimeException(var4.getCause());
         }
      } else {
         this.setupIncludeImpl(request, response);
      }

   }

   private void setupIncludeImpl(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
      final ServletRequestContext servletRequestContext = SecurityActions.currentServletRequestContext();
      if (servletRequestContext == null) {
         UndertowLogger.REQUEST_LOGGER.debugf("No servlet request context for %s, dispatching mock request", request);
         this.mock(request, response);
      } else {
         final HttpServletRequestImpl requestImpl = servletRequestContext.getOriginalRequest();
         final HttpServletResponseImpl responseImpl = servletRequestContext.getOriginalResponse();
         ServletContextImpl oldServletContext = null;
         HttpSessionImpl oldSession = null;
         if (servletRequestContext.getCurrentServletContext() != this.servletContext) {
            oldServletContext = servletRequestContext.getCurrentServletContext();
            oldSession = servletRequestContext.getSession();
            servletRequestContext.setSession((HttpSessionImpl)null);
            servletRequestContext.setCurrentServletContext(this.servletContext);

            try {
               servletRequestContext.getCurrentServletContext().invokeAction(servletRequestContext.getExchange(), new ThreadSetupHandler.Action<Void, Object>() {
                  public Void call(HttpServerExchange exchange, Object context) throws Exception {
                     RequestDispatcherImpl.this.includeImpl(request, response, servletRequestContext, requestImpl, responseImpl);
                     return null;
                  }
               });
            } finally {
               servletRequestContext.getCurrentServletContext().updateSessionAccessTime(servletRequestContext.getExchange());
               servletRequestContext.setSession(oldSession);
               servletRequestContext.setCurrentServletContext(oldServletContext);
            }
         } else {
            this.includeImpl(request, response, servletRequestContext, requestImpl, responseImpl);
         }

      }
   }

   private void includeImpl(ServletRequest request, ServletResponse response, ServletRequestContext servletRequestContext, HttpServletRequestImpl requestImpl, HttpServletResponseImpl responseImpl) throws ServletException, IOException {
      if (!this.servletContext.getDeployment().getDeploymentInfo().isAllowNonStandardWrappers()) {
         if (servletRequestContext.getOriginalRequest() != request && !(request instanceof ServletRequestWrapper)) {
            throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(request);
         }

         if (servletRequestContext.getOriginalResponse() != response && !(response instanceof ServletResponseWrapper)) {
            throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(response);
         }
      }

      ServletRequest oldRequest = servletRequestContext.getServletRequest();
      ServletResponse oldResponse = servletRequestContext.getServletResponse();
      Object requestUri = null;
      Object contextPath = null;
      Object servletPath = null;
      Object pathInfo = null;
      Object queryString = null;
      Map<String, Deque<String>> queryParameters = requestImpl.getQueryParameters();
      if (!this.named) {
         requestUri = request.getAttribute("javax.servlet.include.request_uri");
         contextPath = request.getAttribute("javax.servlet.include.context_path");
         servletPath = request.getAttribute("javax.servlet.include.servlet_path");
         pathInfo = request.getAttribute("javax.servlet.include.path_info");
         queryString = request.getAttribute("javax.servlet.include.query_string");
         int qsPos = this.path.indexOf("?");
         String newServletPath = this.path;
         String newRequestUri;
         if (qsPos != -1) {
            newRequestUri = newServletPath.substring(qsPos + 1);
            newServletPath = newServletPath.substring(0, qsPos);
            String encoding = QueryParameterUtils.getQueryParamEncoding(servletRequestContext.getExchange());
            Map<String, Deque<String>> newQueryParameters = QueryParameterUtils.mergeQueryParametersWithNewQueryString(queryParameters, newRequestUri, encoding);
            requestImpl.setQueryParameters(newQueryParameters);
            requestImpl.setAttribute("javax.servlet.include.query_string", newRequestUri);
         } else {
            requestImpl.setAttribute("javax.servlet.include.query_string", "");
         }

         newRequestUri = this.servletContext.getContextPath() + newServletPath;
         requestImpl.setAttribute("javax.servlet.include.request_uri", newRequestUri);
         requestImpl.setAttribute("javax.servlet.include.context_path", this.servletContext.getContextPath());
         requestImpl.setAttribute("javax.servlet.include.servlet_path", this.pathMatch.getMatched());
         requestImpl.setAttribute("javax.servlet.include.path_info", this.pathMatch.getRemaining());
      }

      boolean inInclude = responseImpl.isInsideInclude();
      responseImpl.setInsideInclude(true);
      DispatcherType oldDispatcherType = servletRequestContext.getDispatcherType();
      ServletContextImpl oldContext = requestImpl.getServletContext();

      try {
         requestImpl.setServletContext(this.servletContext);
         responseImpl.setServletContext(this.servletContext);

         try {
            servletRequestContext.setServletRequest(request);
            servletRequestContext.setServletResponse(response);
            this.servletContext.getDeployment().getServletDispatcher().dispatchToServlet(requestImpl.getExchange(), this.chain, DispatcherType.INCLUDE);
         } catch (ServletException var24) {
            throw var24;
         } catch (IOException var25) {
            throw var25;
         } catch (Exception var26) {
            throw new RuntimeException(var26);
         }
      } finally {
         responseImpl.setInsideInclude(inInclude);
         requestImpl.setServletContext(oldContext);
         responseImpl.setServletContext(oldContext);
         servletRequestContext.setServletRequest(oldRequest);
         servletRequestContext.setServletResponse(oldResponse);
         servletRequestContext.setDispatcherType(oldDispatcherType);
         if (!this.named) {
            requestImpl.setAttribute("javax.servlet.include.request_uri", requestUri);
            requestImpl.setAttribute("javax.servlet.include.context_path", contextPath);
            requestImpl.setAttribute("javax.servlet.include.servlet_path", servletPath);
            requestImpl.setAttribute("javax.servlet.include.path_info", pathInfo);
            requestImpl.setAttribute("javax.servlet.include.query_string", queryString);
            requestImpl.setQueryParameters(queryParameters);
         }

      }

   }

   public void error(ServletRequestContext servletRequestContext, ServletRequest request, ServletResponse response, String servletName, String message) throws ServletException, IOException {
      this.error(servletRequestContext, request, response, servletName, (Throwable)null, message);
   }

   public void error(ServletRequestContext servletRequestContext, ServletRequest request, ServletResponse response, String servletName) throws ServletException, IOException {
      this.error(servletRequestContext, request, response, servletName, (Throwable)null, (String)null);
   }

   public void error(ServletRequestContext servletRequestContext, ServletRequest request, ServletResponse response, String servletName, Throwable exception) throws ServletException, IOException {
      this.error(servletRequestContext, request, response, servletName, exception, exception.getMessage());
   }

   private void error(ServletRequestContext servletRequestContext, ServletRequest request, ServletResponse response, String servletName, Throwable exception, String message) throws ServletException, IOException {
      if (request.getDispatcherType() == DispatcherType.ERROR) {
         UndertowServletLogger.REQUEST_LOGGER.errorGeneratingErrorPage(servletRequestContext.getExchange().getRequestPath(), request.getAttribute("javax.servlet.error.exception"), servletRequestContext.getExchange().getStatusCode(), exception);
         servletRequestContext.getExchange().endExchange();
      } else {
         HttpServletRequestImpl requestImpl = servletRequestContext.getOriginalRequest();
         HttpServletResponseImpl responseImpl = servletRequestContext.getOriginalResponse();
         if (!this.servletContext.getDeployment().getDeploymentInfo().isAllowNonStandardWrappers()) {
            if (servletRequestContext.getOriginalRequest() != request && !(request instanceof ServletRequestWrapper)) {
               throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(request);
            }

            if (servletRequestContext.getOriginalResponse() != response && !(response instanceof ServletResponseWrapper)) {
               throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(response);
            }
         }

         ServletRequest oldRequest = servletRequestContext.getServletRequest();
         ServletResponse oldResponse = servletRequestContext.getServletResponse();
         servletRequestContext.setDispatcherType(DispatcherType.ERROR);
         if (request.getAttribute("javax.servlet.forward.request_uri") == null) {
            requestImpl.setAttribute("javax.servlet.forward.request_uri", requestImpl.getRequestURI());
            requestImpl.setAttribute("javax.servlet.forward.context_path", requestImpl.getContextPath());
            requestImpl.setAttribute("javax.servlet.forward.servlet_path", requestImpl.getServletPath());
            requestImpl.setAttribute("javax.servlet.forward.path_info", requestImpl.getPathInfo());
            requestImpl.setAttribute("javax.servlet.forward.query_string", requestImpl.getQueryString());
         }

         requestImpl.setAttribute("javax.servlet.error.request_uri", requestImpl.getRequestURI());
         requestImpl.setAttribute("javax.servlet.error.servlet_name", servletName);
         if (exception != null) {
            if (exception instanceof ServletException && ((ServletException)exception).getRootCause() != null) {
               requestImpl.setAttribute("javax.servlet.error.exception", ((ServletException)exception).getRootCause());
               requestImpl.setAttribute("javax.servlet.error.exception_type", ((ServletException)exception).getRootCause().getClass());
            } else {
               requestImpl.setAttribute("javax.servlet.error.exception", exception);
               requestImpl.setAttribute("javax.servlet.error.exception_type", exception.getClass());
            }
         }

         requestImpl.setAttribute("javax.servlet.error.message", message);
         requestImpl.setAttribute("javax.servlet.error.status_code", responseImpl.getStatus());
         int qsPos = this.path.indexOf("?");
         String newServletPath = this.path;
         if (qsPos != -1) {
            Map<String, Deque<String>> queryParameters = requestImpl.getQueryParameters();
            String newQueryString = newServletPath.substring(qsPos + 1);
            newServletPath = newServletPath.substring(0, qsPos);
            String encoding = QueryParameterUtils.getQueryParamEncoding(servletRequestContext.getExchange());
            Map<String, Deque<String>> newQueryParameters = QueryParameterUtils.mergeQueryParametersWithNewQueryString(queryParameters, newQueryString, encoding);
            requestImpl.getExchange().setQueryString(newQueryString);
            requestImpl.setQueryParameters(newQueryParameters);
         }

         String newRequestUri = this.servletContext.getContextPath() + newServletPath;
         requestImpl.getExchange().setRelativePath(newServletPath);
         requestImpl.getExchange().setRequestPath(newRequestUri);
         requestImpl.getExchange().setRequestURI(newRequestUri);
         ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setServletPathMatch(this.pathMatch);
         requestImpl.setServletContext(this.servletContext);
         responseImpl.setServletContext(this.servletContext);
         boolean var23 = false;

         try {
            var23 = true;
            servletRequestContext.setServletRequest(request);
            servletRequestContext.setServletResponse(response);
            this.servletContext.getDeployment().getServletDispatcher().dispatchToPath(requestImpl.getExchange(), this.pathMatch, DispatcherType.ERROR);
            var23 = false;
         } catch (ServletException var24) {
            throw var24;
         } catch (IOException var25) {
            throw var25;
         } catch (Exception var26) {
            throw new RuntimeException(var26);
         } finally {
            if (var23) {
               AsyncContextImpl ac = servletRequestContext.getOriginalRequest().getAsyncContextInternal();
               if (ac != null) {
                  ac.complete();
               }

               servletRequestContext.setServletRequest(oldRequest);
               servletRequestContext.setServletResponse(oldResponse);
            }
         }

         AsyncContextImpl ac = servletRequestContext.getOriginalRequest().getAsyncContextInternal();
         if (ac != null) {
            ac.complete();
         }

         servletRequestContext.setServletRequest(oldRequest);
         servletRequestContext.setServletResponse(oldResponse);
      }
   }

   public void mock(ServletRequest request, ServletResponse response) throws ServletException, IOException {
      if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
         HttpServletRequest req = (HttpServletRequest)request;
         HttpServletResponse resp = (HttpServletResponse)response;
         this.servletContext.getDeployment().getServletDispatcher().dispatchMockRequest(req, resp);
      } else {
         throw UndertowServletMessages.MESSAGES.invalidRequestResponseType(request, response);
      }
   }
}
