/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.api.ThreadSetupHandler;
/*     */ import io.undertow.servlet.handlers.ServletChain;
/*     */ import io.undertow.servlet.handlers.ServletPathMatch;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.util.QueryParameterUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Deque;
/*     */ import java.util.Map;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.RequestDispatcher;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
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
/*     */ public class RequestDispatcherImpl
/*     */   implements RequestDispatcher
/*     */ {
/*     */   private final String path;
/*     */   private final ServletContextImpl servletContext;
/*     */   private final ServletChain chain;
/*     */   private final ServletPathMatch pathMatch;
/*     */   private final boolean named;
/*     */   
/*     */   public RequestDispatcherImpl(String path, ServletContextImpl servletContext) {
/*  62 */     this.path = path;
/*  63 */     this.servletContext = servletContext;
/*  64 */     String basePath = path;
/*  65 */     int qPos = basePath.indexOf("?");
/*  66 */     if (qPos != -1) {
/*  67 */       basePath = basePath.substring(0, qPos);
/*     */     }
/*  69 */     int mPos = basePath.indexOf(";");
/*  70 */     if (mPos != -1) {
/*  71 */       basePath = basePath.substring(0, mPos);
/*     */     }
/*  73 */     this.pathMatch = servletContext.getDeployment().getServletPaths().getServletHandlerByPath(basePath);
/*  74 */     this.chain = this.pathMatch.getServletChain();
/*  75 */     this.named = false;
/*     */   }
/*     */   
/*     */   public RequestDispatcherImpl(ServletChain chain, ServletContextImpl servletContext) {
/*  79 */     this.chain = chain;
/*  80 */     this.named = true;
/*  81 */     this.servletContext = servletContext;
/*  82 */     this.path = null;
/*  83 */     this.pathMatch = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void forward(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
/*  89 */     if (System.getSecurityManager() != null) {
/*     */       try {
/*  91 */         AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */             {
/*     */               public Object run() throws Exception {
/*  94 */                 RequestDispatcherImpl.this.forwardImplSetup(request, response);
/*  95 */                 return null;
/*     */               }
/*     */             });
/*  98 */       } catch (PrivilegedActionException e) {
/*  99 */         if (e.getCause() instanceof ServletException)
/* 100 */           throw (ServletException)e.getCause(); 
/* 101 */         if (e.getCause() instanceof IOException)
/* 102 */           throw (IOException)e.getCause(); 
/* 103 */         if (e.getCause() instanceof RuntimeException) {
/* 104 */           throw (RuntimeException)e.getCause();
/*     */         }
/* 106 */         throw new RuntimeException(e.getCause());
/*     */       } 
/*     */     } else {
/*     */       
/* 110 */       forwardImplSetup(request, response);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void forwardImplSetup(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
/* 115 */     final ServletRequestContext servletRequestContext = SecurityActions.currentServletRequestContext();
/* 116 */     if (servletRequestContext == null) {
/* 117 */       UndertowLogger.REQUEST_LOGGER.debugf("No servlet request context for %s, dispatching mock request", request);
/* 118 */       mock(request, response);
/*     */       
/*     */       return;
/*     */     } 
/* 122 */     ServletContextImpl oldServletContext = null;
/* 123 */     HttpSessionImpl oldSession = null;
/* 124 */     if (servletRequestContext.getCurrentServletContext() != this.servletContext) {
/*     */ 
/*     */       
/*     */       try {
/* 128 */         oldServletContext = servletRequestContext.getCurrentServletContext();
/* 129 */         oldSession = servletRequestContext.getSession();
/* 130 */         servletRequestContext.setSession(null);
/* 131 */         servletRequestContext.setCurrentServletContext(this.servletContext);
/* 132 */         this.servletContext.invokeAction(servletRequestContext.getExchange(), new ThreadSetupHandler.Action<Void, Object>()
/*     */             {
/*     */               public Void call(HttpServerExchange exchange, Object context) throws Exception {
/* 135 */                 RequestDispatcherImpl.this.forwardImpl(request, response, servletRequestContext);
/* 136 */                 return null;
/*     */               }
/*     */             });
/*     */       } finally {
/*     */         
/* 141 */         servletRequestContext.setSession(oldSession);
/* 142 */         servletRequestContext.setCurrentServletContext(oldServletContext);
/*     */         
/* 144 */         servletRequestContext.getCurrentServletContext().updateSessionAccessTime(servletRequestContext.getExchange());
/*     */       } 
/*     */     } else {
/* 147 */       forwardImpl(request, response, servletRequestContext);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void forwardImpl(ServletRequest request, ServletResponse response, ServletRequestContext servletRequestContext) throws ServletException, IOException {
/* 153 */     HttpServletRequestImpl requestImpl = servletRequestContext.getOriginalRequest();
/* 154 */     HttpServletResponseImpl responseImpl = servletRequestContext.getOriginalResponse();
/* 155 */     if (!this.servletContext.getDeployment().getDeploymentInfo().isAllowNonStandardWrappers()) {
/* 156 */       if (servletRequestContext.getOriginalRequest() != request && 
/* 157 */         !(request instanceof javax.servlet.ServletRequestWrapper)) {
/* 158 */         throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(request);
/*     */       }
/*     */       
/* 161 */       if (servletRequestContext.getOriginalResponse() != response && 
/* 162 */         !(response instanceof javax.servlet.ServletResponseWrapper)) {
/* 163 */         throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(response);
/*     */       }
/*     */     } 
/*     */     
/* 167 */     response.resetBuffer();
/*     */     
/* 169 */     ServletRequest oldRequest = servletRequestContext.getServletRequest();
/* 170 */     ServletResponse oldResponse = servletRequestContext.getServletResponse();
/*     */     
/* 172 */     Map<String, Deque<String>> queryParameters = requestImpl.getQueryParameters();
/*     */     
/* 174 */     request.removeAttribute("javax.servlet.include.request_uri");
/* 175 */     request.removeAttribute("javax.servlet.include.context_path");
/* 176 */     request.removeAttribute("javax.servlet.include.servlet_path");
/* 177 */     request.removeAttribute("javax.servlet.include.path_info");
/* 178 */     request.removeAttribute("javax.servlet.include.query_string");
/*     */     
/* 180 */     String oldURI = requestImpl.getExchange().getRequestURI();
/* 181 */     String oldRequestPath = requestImpl.getExchange().getRequestPath();
/* 182 */     String oldPath = requestImpl.getExchange().getRelativePath();
/* 183 */     ServletPathMatch oldServletPathMatch = ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletPathMatch();
/* 184 */     if (!this.named) {
/*     */ 
/*     */       
/* 187 */       if (request.getAttribute("javax.servlet.forward.request_uri") == null) {
/* 188 */         requestImpl.setAttribute("javax.servlet.forward.request_uri", requestImpl.getRequestURI());
/* 189 */         requestImpl.setAttribute("javax.servlet.forward.context_path", requestImpl.getContextPath());
/* 190 */         requestImpl.setAttribute("javax.servlet.forward.servlet_path", requestImpl.getServletPath());
/* 191 */         requestImpl.setAttribute("javax.servlet.forward.path_info", requestImpl.getPathInfo());
/* 192 */         requestImpl.setAttribute("javax.servlet.forward.query_string", requestImpl.getQueryString());
/*     */       } 
/*     */       
/* 195 */       int qsPos = this.path.indexOf("?");
/* 196 */       String newServletPath = this.path;
/* 197 */       if (qsPos != -1) {
/* 198 */         String newQueryString = newServletPath.substring(qsPos + 1);
/* 199 */         newServletPath = newServletPath.substring(0, qsPos);
/*     */         
/* 201 */         String encoding = QueryParameterUtils.getQueryParamEncoding(servletRequestContext.getExchange());
/* 202 */         Map<String, Deque<String>> newQueryParameters = QueryParameterUtils.mergeQueryParametersWithNewQueryString(queryParameters, newQueryString, encoding);
/* 203 */         requestImpl.getExchange().setQueryString(newQueryString);
/* 204 */         requestImpl.setQueryParameters(newQueryParameters);
/*     */       } 
/* 206 */       String newRequestUri = this.servletContext.getContextPath() + newServletPath;
/*     */ 
/*     */ 
/*     */       
/* 210 */       requestImpl.getExchange().setRelativePath(newServletPath);
/* 211 */       requestImpl.getExchange().setRequestPath(newRequestUri);
/* 212 */       requestImpl.getExchange().setRequestURI(newRequestUri);
/* 213 */       ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setServletPathMatch(this.pathMatch);
/* 214 */       requestImpl.setServletContext(this.servletContext);
/* 215 */       responseImpl.setServletContext(this.servletContext);
/*     */     } 
/*     */     
/*     */     try {
/*     */       try {
/* 220 */         servletRequestContext.setServletRequest(request);
/* 221 */         servletRequestContext.setServletResponse(response);
/* 222 */         if (this.named) {
/* 223 */           this.servletContext.getDeployment().getServletDispatcher().dispatchToServlet(requestImpl.getExchange(), this.chain, DispatcherType.FORWARD);
/*     */         } else {
/* 225 */           this.servletContext.getDeployment().getServletDispatcher().dispatchToPath(requestImpl.getExchange(), this.pathMatch, DispatcherType.FORWARD);
/*     */         } 
/*     */ 
/*     */         
/* 229 */         if (!request.isAsyncStarted()) {
/* 230 */           if (response instanceof HttpServletResponseImpl) {
/* 231 */             responseImpl.closeStreamAndWriter();
/*     */           } else {
/*     */             try {
/* 234 */               PrintWriter writer = response.getWriter();
/* 235 */               writer.flush();
/* 236 */               writer.close();
/* 237 */             } catch (IllegalStateException e) {
/* 238 */               ServletOutputStream outputStream = response.getOutputStream();
/* 239 */               outputStream.flush();
/* 240 */               outputStream.close();
/*     */             } 
/*     */           } 
/*     */         }
/* 244 */       } catch (ServletException e) {
/* 245 */         throw e;
/* 246 */       } catch (IOException e) {
/* 247 */         throw e;
/* 248 */       } catch (Exception e) {
/* 249 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } finally {
/* 252 */       servletRequestContext.setServletRequest(oldRequest);
/* 253 */       servletRequestContext.setServletResponse(oldResponse);
/* 254 */       boolean preservePath = servletRequestContext.getDeployment().getDeploymentInfo().isPreservePathOnForward();
/* 255 */       if (preservePath) {
/* 256 */         requestImpl.getExchange().setRelativePath(oldPath);
/* 257 */         ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setServletPathMatch(oldServletPathMatch);
/* 258 */         requestImpl.getExchange().setRequestPath(oldRequestPath);
/* 259 */         requestImpl.getExchange().setRequestURI(oldURI);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void include(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
/* 267 */     if (System.getSecurityManager() != null) {
/*     */       try {
/* 269 */         AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */             {
/*     */               public Object run() throws Exception {
/* 272 */                 RequestDispatcherImpl.this.setupIncludeImpl(request, response);
/* 273 */                 return null;
/*     */               }
/*     */             });
/* 276 */       } catch (PrivilegedActionException e) {
/* 277 */         if (e.getCause() instanceof ServletException)
/* 278 */           throw (ServletException)e.getCause(); 
/* 279 */         if (e.getCause() instanceof IOException)
/* 280 */           throw (IOException)e.getCause(); 
/* 281 */         if (e.getCause() instanceof RuntimeException) {
/* 282 */           throw (RuntimeException)e.getCause();
/*     */         }
/* 284 */         throw new RuntimeException(e.getCause());
/*     */       } 
/*     */     } else {
/*     */       
/* 288 */       setupIncludeImpl(request, response);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setupIncludeImpl(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
/* 293 */     final ServletRequestContext servletRequestContext = SecurityActions.currentServletRequestContext();
/* 294 */     if (servletRequestContext == null) {
/* 295 */       UndertowLogger.REQUEST_LOGGER.debugf("No servlet request context for %s, dispatching mock request", request);
/* 296 */       mock(request, response);
/*     */       return;
/*     */     } 
/* 299 */     final HttpServletRequestImpl requestImpl = servletRequestContext.getOriginalRequest();
/* 300 */     final HttpServletResponseImpl responseImpl = servletRequestContext.getOriginalResponse();
/* 301 */     ServletContextImpl oldServletContext = null;
/* 302 */     HttpSessionImpl oldSession = null;
/* 303 */     if (servletRequestContext.getCurrentServletContext() != this.servletContext) {
/*     */       
/* 305 */       oldServletContext = servletRequestContext.getCurrentServletContext();
/* 306 */       oldSession = servletRequestContext.getSession();
/* 307 */       servletRequestContext.setSession(null);
/* 308 */       servletRequestContext.setCurrentServletContext(this.servletContext);
/*     */       try {
/* 310 */         servletRequestContext.getCurrentServletContext().invokeAction(servletRequestContext.getExchange(), new ThreadSetupHandler.Action<Void, Object>()
/*     */             {
/*     */               public Void call(HttpServerExchange exchange, Object context) throws Exception {
/* 313 */                 RequestDispatcherImpl.this.includeImpl(request, response, servletRequestContext, requestImpl, responseImpl);
/* 314 */                 return null;
/*     */               }
/*     */             });
/*     */       } finally {
/*     */         
/* 319 */         servletRequestContext.getCurrentServletContext().updateSessionAccessTime(servletRequestContext.getExchange());
/* 320 */         servletRequestContext.setSession(oldSession);
/* 321 */         servletRequestContext.setCurrentServletContext(oldServletContext);
/*     */       } 
/*     */     } else {
/* 324 */       includeImpl(request, response, servletRequestContext, requestImpl, responseImpl);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void includeImpl(ServletRequest request, ServletResponse response, ServletRequestContext servletRequestContext, HttpServletRequestImpl requestImpl, HttpServletResponseImpl responseImpl) throws ServletException, IOException {
/* 329 */     if (!this.servletContext.getDeployment().getDeploymentInfo().isAllowNonStandardWrappers()) {
/* 330 */       if (servletRequestContext.getOriginalRequest() != request && 
/* 331 */         !(request instanceof javax.servlet.ServletRequestWrapper)) {
/* 332 */         throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(request);
/*     */       }
/*     */       
/* 335 */       if (servletRequestContext.getOriginalResponse() != response && 
/* 336 */         !(response instanceof javax.servlet.ServletResponseWrapper)) {
/* 337 */         throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(response);
/*     */       }
/*     */     } 
/*     */     
/* 341 */     ServletRequest oldRequest = servletRequestContext.getServletRequest();
/* 342 */     ServletResponse oldResponse = servletRequestContext.getServletResponse();
/*     */     
/* 344 */     Object requestUri = null;
/* 345 */     Object contextPath = null;
/* 346 */     Object servletPath = null;
/* 347 */     Object pathInfo = null;
/* 348 */     Object queryString = null;
/* 349 */     Map<String, Deque<String>> queryParameters = requestImpl.getQueryParameters();
/*     */     
/* 351 */     if (!this.named) {
/* 352 */       requestUri = request.getAttribute("javax.servlet.include.request_uri");
/* 353 */       contextPath = request.getAttribute("javax.servlet.include.context_path");
/* 354 */       servletPath = request.getAttribute("javax.servlet.include.servlet_path");
/* 355 */       pathInfo = request.getAttribute("javax.servlet.include.path_info");
/* 356 */       queryString = request.getAttribute("javax.servlet.include.query_string");
/*     */       
/* 358 */       int qsPos = this.path.indexOf("?");
/* 359 */       String newServletPath = this.path;
/* 360 */       if (qsPos != -1) {
/* 361 */         String newQueryString = newServletPath.substring(qsPos + 1);
/* 362 */         newServletPath = newServletPath.substring(0, qsPos);
/*     */         
/* 364 */         String encoding = QueryParameterUtils.getQueryParamEncoding(servletRequestContext.getExchange());
/* 365 */         Map<String, Deque<String>> newQueryParameters = QueryParameterUtils.mergeQueryParametersWithNewQueryString(queryParameters, newQueryString, encoding);
/* 366 */         requestImpl.setQueryParameters(newQueryParameters);
/* 367 */         requestImpl.setAttribute("javax.servlet.include.query_string", newQueryString);
/*     */       } else {
/* 369 */         requestImpl.setAttribute("javax.servlet.include.query_string", "");
/*     */       } 
/* 371 */       String newRequestUri = this.servletContext.getContextPath() + newServletPath;
/*     */       
/* 373 */       requestImpl.setAttribute("javax.servlet.include.request_uri", newRequestUri);
/* 374 */       requestImpl.setAttribute("javax.servlet.include.context_path", this.servletContext.getContextPath());
/* 375 */       requestImpl.setAttribute("javax.servlet.include.servlet_path", this.pathMatch.getMatched());
/* 376 */       requestImpl.setAttribute("javax.servlet.include.path_info", this.pathMatch.getRemaining());
/*     */     } 
/* 378 */     boolean inInclude = responseImpl.isInsideInclude();
/* 379 */     responseImpl.setInsideInclude(true);
/* 380 */     DispatcherType oldDispatcherType = servletRequestContext.getDispatcherType();
/*     */     
/* 382 */     ServletContextImpl oldContext = requestImpl.getServletContext();
/*     */     try {
/* 384 */       requestImpl.setServletContext(this.servletContext);
/* 385 */       responseImpl.setServletContext(this.servletContext);
/*     */       try {
/* 387 */         servletRequestContext.setServletRequest(request);
/* 388 */         servletRequestContext.setServletResponse(response);
/* 389 */         this.servletContext.getDeployment().getServletDispatcher().dispatchToServlet(requestImpl.getExchange(), this.chain, DispatcherType.INCLUDE);
/* 390 */       } catch (ServletException e) {
/* 391 */         throw e;
/* 392 */       } catch (IOException e) {
/* 393 */         throw e;
/* 394 */       } catch (Exception e) {
/* 395 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } finally {
/* 398 */       responseImpl.setInsideInclude(inInclude);
/* 399 */       requestImpl.setServletContext(oldContext);
/* 400 */       responseImpl.setServletContext(oldContext);
/*     */       
/* 402 */       servletRequestContext.setServletRequest(oldRequest);
/* 403 */       servletRequestContext.setServletResponse(oldResponse);
/* 404 */       servletRequestContext.setDispatcherType(oldDispatcherType);
/* 405 */       if (!this.named) {
/* 406 */         requestImpl.setAttribute("javax.servlet.include.request_uri", requestUri);
/* 407 */         requestImpl.setAttribute("javax.servlet.include.context_path", contextPath);
/* 408 */         requestImpl.setAttribute("javax.servlet.include.servlet_path", servletPath);
/* 409 */         requestImpl.setAttribute("javax.servlet.include.path_info", pathInfo);
/* 410 */         requestImpl.setAttribute("javax.servlet.include.query_string", queryString);
/* 411 */         requestImpl.setQueryParameters(queryParameters);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void error(ServletRequestContext servletRequestContext, ServletRequest request, ServletResponse response, String servletName, String message) throws ServletException, IOException {
/* 417 */     error(servletRequestContext, request, response, servletName, null, message);
/*     */   }
/*     */   
/*     */   public void error(ServletRequestContext servletRequestContext, ServletRequest request, ServletResponse response, String servletName) throws ServletException, IOException {
/* 421 */     error(servletRequestContext, request, response, servletName, null, null);
/*     */   }
/*     */   
/*     */   public void error(ServletRequestContext servletRequestContext, ServletRequest request, ServletResponse response, String servletName, Throwable exception) throws ServletException, IOException {
/* 425 */     error(servletRequestContext, request, response, servletName, exception, exception.getMessage());
/*     */   }
/*     */   
/*     */   private void error(ServletRequestContext servletRequestContext, ServletRequest request, ServletResponse response, String servletName, Throwable exception, String message) throws ServletException, IOException {
/* 429 */     if (request.getDispatcherType() == DispatcherType.ERROR) {
/*     */ 
/*     */ 
/*     */       
/* 433 */       UndertowServletLogger.REQUEST_LOGGER.errorGeneratingErrorPage(servletRequestContext.getExchange().getRequestPath(), request.getAttribute("javax.servlet.error.exception"), servletRequestContext.getExchange().getStatusCode(), exception);
/* 434 */       servletRequestContext.getExchange().endExchange();
/*     */       
/*     */       return;
/*     */     } 
/* 438 */     HttpServletRequestImpl requestImpl = servletRequestContext.getOriginalRequest();
/* 439 */     HttpServletResponseImpl responseImpl = servletRequestContext.getOriginalResponse();
/* 440 */     if (!this.servletContext.getDeployment().getDeploymentInfo().isAllowNonStandardWrappers()) {
/* 441 */       if (servletRequestContext.getOriginalRequest() != request && 
/* 442 */         !(request instanceof javax.servlet.ServletRequestWrapper)) {
/* 443 */         throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(request);
/*     */       }
/*     */       
/* 446 */       if (servletRequestContext.getOriginalResponse() != response && 
/* 447 */         !(response instanceof javax.servlet.ServletResponseWrapper)) {
/* 448 */         throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(response);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 453 */     ServletRequest oldRequest = servletRequestContext.getServletRequest();
/* 454 */     ServletResponse oldResponse = servletRequestContext.getServletResponse();
/* 455 */     servletRequestContext.setDispatcherType(DispatcherType.ERROR);
/*     */ 
/*     */     
/* 458 */     if (request.getAttribute("javax.servlet.forward.request_uri") == null) {
/* 459 */       requestImpl.setAttribute("javax.servlet.forward.request_uri", requestImpl.getRequestURI());
/* 460 */       requestImpl.setAttribute("javax.servlet.forward.context_path", requestImpl.getContextPath());
/* 461 */       requestImpl.setAttribute("javax.servlet.forward.servlet_path", requestImpl.getServletPath());
/* 462 */       requestImpl.setAttribute("javax.servlet.forward.path_info", requestImpl.getPathInfo());
/* 463 */       requestImpl.setAttribute("javax.servlet.forward.query_string", requestImpl.getQueryString());
/*     */     } 
/* 465 */     requestImpl.setAttribute("javax.servlet.error.request_uri", requestImpl.getRequestURI());
/* 466 */     requestImpl.setAttribute("javax.servlet.error.servlet_name", servletName);
/* 467 */     if (exception != null) {
/* 468 */       if (exception instanceof ServletException && ((ServletException)exception).getRootCause() != null) {
/* 469 */         requestImpl.setAttribute("javax.servlet.error.exception", ((ServletException)exception).getRootCause());
/* 470 */         requestImpl.setAttribute("javax.servlet.error.exception_type", ((ServletException)exception).getRootCause().getClass());
/*     */       } else {
/* 472 */         requestImpl.setAttribute("javax.servlet.error.exception", exception);
/* 473 */         requestImpl.setAttribute("javax.servlet.error.exception_type", exception.getClass());
/*     */       } 
/*     */     }
/* 476 */     requestImpl.setAttribute("javax.servlet.error.message", message);
/* 477 */     requestImpl.setAttribute("javax.servlet.error.status_code", Integer.valueOf(responseImpl.getStatus()));
/*     */     
/* 479 */     int qsPos = this.path.indexOf("?");
/* 480 */     String newServletPath = this.path;
/* 481 */     if (qsPos != -1) {
/* 482 */       Map<String, Deque<String>> queryParameters = requestImpl.getQueryParameters();
/* 483 */       String newQueryString = newServletPath.substring(qsPos + 1);
/* 484 */       newServletPath = newServletPath.substring(0, qsPos);
/*     */       
/* 486 */       String encoding = QueryParameterUtils.getQueryParamEncoding(servletRequestContext.getExchange());
/* 487 */       Map<String, Deque<String>> newQueryParameters = QueryParameterUtils.mergeQueryParametersWithNewQueryString(queryParameters, newQueryString, encoding);
/* 488 */       requestImpl.getExchange().setQueryString(newQueryString);
/* 489 */       requestImpl.setQueryParameters(newQueryParameters);
/*     */     } 
/* 491 */     String newRequestUri = this.servletContext.getContextPath() + newServletPath;
/*     */     
/* 493 */     requestImpl.getExchange().setRelativePath(newServletPath);
/* 494 */     requestImpl.getExchange().setRequestPath(newRequestUri);
/* 495 */     requestImpl.getExchange().setRequestURI(newRequestUri);
/* 496 */     ((ServletRequestContext)requestImpl.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY)).setServletPathMatch(this.pathMatch);
/* 497 */     requestImpl.setServletContext(this.servletContext);
/* 498 */     responseImpl.setServletContext(this.servletContext);
/*     */     
/*     */     try {
/*     */       try {
/* 502 */         servletRequestContext.setServletRequest(request);
/* 503 */         servletRequestContext.setServletResponse(response);
/* 504 */         this.servletContext.getDeployment().getServletDispatcher().dispatchToPath(requestImpl.getExchange(), this.pathMatch, DispatcherType.ERROR);
/* 505 */       } catch (ServletException e) {
/* 506 */         throw e;
/* 507 */       } catch (IOException e) {
/* 508 */         throw e;
/* 509 */       } catch (Exception e) {
/* 510 */         throw new RuntimeException(e);
/*     */       } 
/*     */     } finally {
/* 513 */       AsyncContextImpl ac = servletRequestContext.getOriginalRequest().getAsyncContextInternal();
/* 514 */       if (ac != null) {
/* 515 */         ac.complete();
/*     */       }
/* 517 */       servletRequestContext.setServletRequest(oldRequest);
/* 518 */       servletRequestContext.setServletResponse(oldResponse);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void mock(ServletRequest request, ServletResponse response) throws ServletException, IOException {
/* 523 */     if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
/* 524 */       HttpServletRequest req = (HttpServletRequest)request;
/* 525 */       HttpServletResponse resp = (HttpServletResponse)response;
/* 526 */       this.servletContext.getDeployment().getServletDispatcher().dispatchMockRequest(req, resp);
/*     */     } else {
/* 528 */       throw UndertowServletMessages.MESSAGES.invalidRequestResponseType(request, response);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\RequestDispatcherImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */