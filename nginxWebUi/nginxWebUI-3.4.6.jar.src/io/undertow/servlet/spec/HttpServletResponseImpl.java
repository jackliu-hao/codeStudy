/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.protocol.http.HttpAttachments;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.util.CanonicalPathUtils;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Protocols;
/*     */ import io.undertow.util.RedirectBuilder;
/*     */ import io.undertow.util.StatusCodes;
/*     */ import io.undertow.util.URLUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Supplier;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.SessionTrackingMode;
/*     */ import javax.servlet.http.Cookie;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
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
/*     */ public final class HttpServletResponseImpl
/*     */   implements HttpServletResponse
/*     */ {
/*     */   private final HttpServerExchange exchange;
/*     */   private final ServletContextImpl originalServletContext;
/*     */   private volatile ServletContextImpl servletContext;
/*     */   private ServletOutputStreamImpl servletOutputStream;
/*  72 */   private ResponseState responseState = ResponseState.NONE;
/*     */   private PrintWriter writer;
/*     */   private Integer bufferSize;
/*  75 */   private long contentLength = -1L;
/*     */   
/*     */   private boolean insideInclude = false;
/*     */   
/*     */   private Locale locale;
/*     */   
/*     */   private boolean responseDone = false;
/*     */   private boolean ignoredFlushPerformed = false;
/*     */   private boolean treatAsCommitted = false;
/*     */   private boolean charsetSet = false;
/*     */   private String contentType;
/*     */   private String charset;
/*     */   private Supplier<Map<String, String>> trailerSupplier;
/*     */   
/*     */   public HttpServletResponseImpl(HttpServerExchange exchange, ServletContextImpl servletContext) {
/*  90 */     this.exchange = exchange;
/*  91 */     this.servletContext = servletContext;
/*  92 */     this.originalServletContext = servletContext;
/*     */   }
/*     */   
/*     */   public HttpServerExchange getExchange() {
/*  96 */     return this.exchange;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addCookie(Cookie newCookie) {
/* 101 */     if (this.insideInclude) {
/*     */       return;
/*     */     }
/* 104 */     ServletCookieAdaptor servletCookieAdaptor = new ServletCookieAdaptor(newCookie);
/* 105 */     if (newCookie.getVersion() == 0) {
/* 106 */       servletCookieAdaptor.setVersion(this.servletContext.getDeployment().getDeploymentInfo().getDefaultCookieVersion());
/*     */     }
/* 108 */     this.exchange.setResponseCookie(servletCookieAdaptor);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsHeader(String name) {
/* 113 */     return this.exchange.getResponseHeaders().contains(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String encodeUrl(String url) {
/* 118 */     return encodeURL(url);
/*     */   }
/*     */ 
/*     */   
/*     */   public String encodeRedirectUrl(String url) {
/* 123 */     return encodeRedirectURL(url);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendError(int sc, String msg) throws IOException {
/* 128 */     if (this.insideInclude) {
/*     */       return;
/*     */     }
/*     */     
/* 132 */     ServletRequestContext src = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 133 */     if (responseStarted()) {
/* 134 */       if (src.getErrorCode() > 0) {
/*     */         return;
/*     */       }
/* 137 */       throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
/*     */     } 
/* 139 */     if (this.servletContext.getDeployment().getDeploymentInfo().isSendCustomReasonPhraseOnError()) {
/* 140 */       this.exchange.setReasonPhrase(msg);
/*     */     }
/* 142 */     this.writer = null;
/* 143 */     this.responseState = ResponseState.NONE;
/* 144 */     this.exchange.setStatusCode(sc);
/* 145 */     if (src.isRunningInsideHandler()) {
/*     */       
/* 147 */       this.treatAsCommitted = true;
/* 148 */       src.setError(sc, msg);
/*     */     } else {
/*     */       
/* 151 */       doErrorDispatch(sc, msg);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void doErrorDispatch(int sc, String error) throws IOException {
/* 156 */     this.writer = null;
/* 157 */     this.responseState = ResponseState.NONE;
/* 158 */     resetBuffer();
/* 159 */     this.treatAsCommitted = false;
/* 160 */     String location = this.servletContext.getDeployment().getErrorPages().getErrorLocation(sc);
/* 161 */     if (location != null) {
/* 162 */       RequestDispatcherImpl requestDispatcher = new RequestDispatcherImpl(location, this.servletContext);
/* 163 */       ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*     */       try {
/* 165 */         requestDispatcher.error(servletRequestContext, servletRequestContext.getServletRequest(), servletRequestContext.getServletResponse(), ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getCurrentServlet().getManagedServlet().getServletInfo().getName(), error);
/* 166 */       } catch (ServletException e) {
/* 167 */         throw new RuntimeException(e);
/*     */       } 
/* 169 */     } else if (error != null) {
/* 170 */       setContentType("text/html");
/* 171 */       setCharacterEncoding("UTF-8");
/* 172 */       if (this.servletContext.getDeployment().getDeploymentInfo().isEscapeErrorMessage()) {
/* 173 */         getWriter().write("<html><head><title>Error</title></head><body>" + escapeHtml(error) + "</body></html>");
/*     */       } else {
/* 175 */         getWriter().write("<html><head><title>Error</title></head><body>" + error + "</body></html>");
/*     */       } 
/* 177 */       getWriter().close();
/*     */     } 
/* 179 */     responseDone();
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendError(int sc) throws IOException {
/* 184 */     sendError(sc, StatusCodes.getReason(sc));
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendRedirect(String location) throws IOException {
/* 189 */     if (responseStarted()) {
/* 190 */       throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
/*     */     }
/* 192 */     resetBuffer();
/* 193 */     setStatus(302);
/*     */     
/* 195 */     if (URLUtils.isAbsoluteUrl(location)) {
/* 196 */       this.exchange.getResponseHeaders().put(Headers.LOCATION, location);
/*     */     } else {
/* 198 */       String realPath; if (location.startsWith("/")) {
/* 199 */         realPath = location;
/*     */       } else {
/*     */         
/* 202 */         String current = this.exchange.getRequestURI().substring(getServletContext().getContextPath().length());
/* 203 */         int lastSlash = current.lastIndexOf("/");
/* 204 */         if (lastSlash != -1) {
/* 205 */           current = current.substring(0, lastSlash + 1);
/*     */         }
/* 207 */         realPath = CanonicalPathUtils.canonicalize(this.servletContext.getContextPath() + current + location);
/*     */       } 
/* 209 */       String loc = this.exchange.getRequestScheme() + "://" + this.exchange.getHostAndPort() + realPath;
/* 210 */       this.exchange.getResponseHeaders().put(Headers.LOCATION, loc);
/*     */     } 
/* 212 */     responseDone();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDateHeader(String name, long date) {
/* 217 */     setHeader(name, DateUtils.toDateString(new Date(date)));
/*     */   }
/*     */ 
/*     */   
/*     */   public void addDateHeader(String name, long date) {
/* 222 */     addHeader(name, DateUtils.toDateString(new Date(date)));
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHeader(String name, String value) {
/* 227 */     if (name == null) {
/* 228 */       throw UndertowServletMessages.MESSAGES.headerNameWasNull();
/*     */     }
/* 230 */     setHeader(HttpString.tryFromString(name), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHeader(HttpString name, String value) {
/* 235 */     if (name == null) {
/* 236 */       throw UndertowServletMessages.MESSAGES.headerNameWasNull();
/*     */     }
/* 238 */     if (this.insideInclude || this.ignoredFlushPerformed) {
/*     */       return;
/*     */     }
/* 241 */     if (name.equals(Headers.CONTENT_TYPE)) {
/* 242 */       setContentType(value);
/*     */     } else {
/* 244 */       this.exchange.getResponseHeaders().put(name, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addHeader(String name, String value) {
/* 250 */     if (name == null) {
/* 251 */       throw UndertowServletMessages.MESSAGES.headerNameWasNull();
/*     */     }
/* 253 */     addHeader(HttpString.tryFromString(name), value);
/*     */   }
/*     */   
/*     */   public void addHeader(HttpString name, String value) {
/* 257 */     if (name == null) {
/* 258 */       throw UndertowServletMessages.MESSAGES.headerNameWasNull();
/*     */     }
/* 260 */     if (this.insideInclude || this.ignoredFlushPerformed || this.treatAsCommitted) {
/*     */       return;
/*     */     }
/* 263 */     if (name.equals(Headers.CONTENT_TYPE) && !this.exchange.getResponseHeaders().contains(Headers.CONTENT_TYPE)) {
/* 264 */       setContentType(value);
/*     */     } else {
/* 266 */       this.exchange.getResponseHeaders().add(name, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIntHeader(String name, int value) {
/* 272 */     setHeader(name, Integer.toString(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void addIntHeader(String name, int value) {
/* 277 */     addHeader(name, Integer.toString(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatus(int sc) {
/* 282 */     if (this.insideInclude || this.treatAsCommitted) {
/*     */       return;
/*     */     }
/* 285 */     if (responseStarted()) {
/*     */       return;
/*     */     }
/* 288 */     this.exchange.setStatusCode(sc);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatus(int sc, String sm) {
/* 293 */     setStatus(sc);
/* 294 */     if (!this.insideInclude && this.servletContext.getDeployment().getDeploymentInfo().isSendCustomReasonPhraseOnError()) {
/* 295 */       this.exchange.setReasonPhrase(sm);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStatus() {
/* 301 */     return this.exchange.getStatusCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHeader(String name) {
/* 306 */     return this.exchange.getResponseHeaders().getFirst(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<String> getHeaders(String name) {
/* 311 */     HeaderValues headers = this.exchange.getResponseHeaders().get(name);
/* 312 */     if (headers == null) {
/* 313 */       return Collections.emptySet();
/*     */     }
/* 315 */     return new ArrayList<>((Collection<? extends String>)headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<String> getHeaderNames() {
/* 320 */     Set<String> headers = new HashSet<>();
/* 321 */     for (HttpString i : this.exchange.getResponseHeaders().getHeaderNames()) {
/* 322 */       headers.add(i.toString());
/*     */     }
/* 324 */     return headers;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCharacterEncoding() {
/* 329 */     if (this.charset != null) {
/* 330 */       return this.charset;
/*     */     }
/*     */     
/* 333 */     if (this.servletContext.getDeployment().getDeploymentInfo().getDefaultResponseEncoding() != null) {
/* 334 */       return this.servletContext.getDeployment().getDeploymentInfo().getDefaultResponseEncoding();
/*     */     }
/*     */     
/* 337 */     if (this.servletContext.getDeployment().getDeploymentInfo().getDefaultEncoding() != null) {
/* 338 */       return this.servletContext.getDeployment().getDeploymentInfo().getDefaultEncoding();
/*     */     }
/*     */ 
/*     */     
/* 342 */     return StandardCharsets.ISO_8859_1.name();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentType() {
/* 347 */     if (this.contentType != null) {
/* 348 */       if (this.charsetSet) {
/* 349 */         return this.contentType + ";charset=" + getCharacterEncoding();
/*     */       }
/* 351 */       return this.contentType;
/*     */     } 
/*     */     
/* 354 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public ServletOutputStream getOutputStream() {
/* 359 */     if (this.responseState == ResponseState.WRITER) {
/* 360 */       throw UndertowServletMessages.MESSAGES.getWriterAlreadyCalled();
/*     */     }
/* 362 */     this.responseState = ResponseState.STREAM;
/* 363 */     createOutputStream();
/* 364 */     return this.servletOutputStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter getWriter() throws IOException {
/* 369 */     if (this.writer == null) {
/* 370 */       if (!this.charsetSet)
/*     */       {
/* 372 */         setCharacterEncoding(getCharacterEncoding());
/*     */       }
/* 374 */       if (this.responseState == ResponseState.STREAM) {
/* 375 */         throw UndertowServletMessages.MESSAGES.getOutputStreamAlreadyCalled();
/*     */       }
/* 377 */       this.responseState = ResponseState.WRITER;
/* 378 */       createOutputStream();
/* 379 */       ServletPrintWriter servletPrintWriter = new ServletPrintWriter(this.servletOutputStream, getCharacterEncoding());
/* 380 */       this.writer = ServletPrintWriterDelegate.newInstance(servletPrintWriter);
/*     */     } 
/* 382 */     return this.writer;
/*     */   }
/*     */   
/*     */   private void createOutputStream() {
/* 386 */     if (this.servletOutputStream == null) {
/* 387 */       if (this.bufferSize == null) {
/* 388 */         this.servletOutputStream = new ServletOutputStreamImpl((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY));
/*     */       } else {
/* 390 */         this.servletOutputStream = new ServletOutputStreamImpl((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY), this.bufferSize.intValue());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterEncoding(String charset) {
/* 397 */     if (this.insideInclude || responseStarted() || this.writer != null || isCommitted()) {
/*     */       return;
/*     */     }
/* 400 */     this.charsetSet = (charset != null);
/* 401 */     this.charset = charset;
/* 402 */     if (this.contentType != null) {
/* 403 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, getContentType());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContentLength(int len) {
/* 409 */     setContentLengthLong(len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContentLengthLong(long len) {
/* 414 */     if (this.insideInclude || responseStarted()) {
/*     */       return;
/*     */     }
/* 417 */     if (len >= 0L) {
/* 418 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, Long.toString(len));
/*     */     } else {
/* 420 */       this.exchange.getResponseHeaders().remove(Headers.CONTENT_LENGTH);
/*     */     } 
/* 422 */     this.contentLength = len;
/*     */   }
/*     */   
/*     */   boolean isIgnoredFlushPerformed() {
/* 426 */     return this.ignoredFlushPerformed;
/*     */   }
/*     */   
/*     */   void setIgnoredFlushPerformed(boolean ignoredFlushPerformed) {
/* 430 */     this.ignoredFlushPerformed = ignoredFlushPerformed;
/*     */   }
/*     */   
/*     */   private boolean responseStarted() {
/* 434 */     return (this.exchange.isResponseStarted() || this.ignoredFlushPerformed || this.treatAsCommitted);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContentType(String type) {
/* 439 */     if (type == null || this.insideInclude || responseStarted()) {
/*     */       return;
/*     */     }
/* 442 */     ContentTypeInfo ct = this.servletContext.parseContentType(type);
/* 443 */     this.contentType = ct.getContentType();
/* 444 */     boolean useCharset = false;
/* 445 */     if (ct.getCharset() != null && this.writer == null && !isCommitted()) {
/* 446 */       this.charset = ct.getCharset();
/* 447 */       this.charsetSet = true;
/* 448 */       useCharset = true;
/*     */     } 
/* 450 */     if (useCharset || !this.charsetSet) {
/* 451 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ct.getHeader());
/* 452 */     } else if (ct.getCharset() == null) {
/* 453 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ct.getHeader() + "; charset=" + this.charset);
/*     */     } else {
/* 455 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ct.getContentType() + "; charset=" + this.charset);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBufferSize(int size) {
/* 461 */     if (this.servletOutputStream != null) {
/* 462 */       this.servletOutputStream.setBufferSize(size);
/*     */     }
/* 464 */     this.bufferSize = Integer.valueOf(size);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBufferSize() {
/* 469 */     if (this.bufferSize == null) {
/* 470 */       return this.exchange.getConnection().getBufferSize();
/*     */     }
/* 472 */     return this.bufferSize.intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flushBuffer() throws IOException {
/* 477 */     if (this.writer != null) {
/* 478 */       this.writer.flush();
/* 479 */     } else if (this.servletOutputStream != null) {
/* 480 */       this.servletOutputStream.flush();
/*     */     } else {
/* 482 */       createOutputStream();
/* 483 */       this.servletOutputStream.flush();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void closeStreamAndWriter() throws IOException {
/* 488 */     if (this.treatAsCommitted) {
/*     */       return;
/*     */     }
/* 491 */     if (this.writer != null) {
/* 492 */       this.writer.close();
/*     */     } else {
/* 494 */       if (this.servletOutputStream == null) {
/* 495 */         createOutputStream();
/*     */       }
/*     */       
/* 498 */       this.servletOutputStream.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void freeResources() throws IOException {
/* 503 */     if (this.writer != null) {
/* 504 */       this.writer.close();
/*     */     }
/* 506 */     if (this.servletOutputStream != null) {
/* 507 */       this.servletOutputStream.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetBuffer() {
/* 513 */     if (this.servletOutputStream != null) {
/* 514 */       this.servletOutputStream.resetBuffer();
/*     */     }
/* 516 */     if (this.writer != null) {
/*     */       
/*     */       try {
/* 519 */         ServletPrintWriter servletPrintWriter = new ServletPrintWriter(this.servletOutputStream, getCharacterEncoding());
/* 520 */         this.writer = ServletPrintWriterDelegate.newInstance(servletPrintWriter);
/* 521 */       } catch (UnsupportedEncodingException e) {
/* 522 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCommitted() {
/* 529 */     return responseStarted();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 534 */     if (this.servletOutputStream != null) {
/* 535 */       this.servletOutputStream.resetBuffer();
/*     */     }
/* 537 */     this.writer = null;
/* 538 */     this.responseState = ResponseState.NONE;
/* 539 */     this.exchange.getResponseHeaders().clear();
/* 540 */     this.exchange.setStatusCode(200);
/* 541 */     this.treatAsCommitted = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLocale(Locale loc) {
/* 546 */     if (this.insideInclude || responseStarted()) {
/*     */       return;
/*     */     }
/* 549 */     this.locale = loc;
/* 550 */     this.exchange.getResponseHeaders().put(Headers.CONTENT_LANGUAGE, loc.getLanguage() + "-" + loc.getCountry());
/* 551 */     if (!this.charsetSet && this.writer == null) {
/* 552 */       Map<String, String> localeCharsetMapping = this.servletContext.getDeployment().getDeploymentInfo().getLocaleCharsetMapping();
/*     */ 
/*     */       
/* 555 */       String charset = localeCharsetMapping.get(this.locale.toString());
/* 556 */       if (charset == null) {
/* 557 */         charset = localeCharsetMapping.get(this.locale.getLanguage() + "_" + this.locale
/* 558 */             .getCountry());
/* 559 */         if (charset == null) {
/* 560 */           charset = localeCharsetMapping.get(this.locale.getLanguage());
/*     */         }
/*     */       } 
/* 563 */       if (charset != null) {
/* 564 */         this.charset = charset;
/* 565 */         if (this.contentType != null) {
/* 566 */           this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, getContentType());
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Locale getLocale() {
/* 575 */     if (this.locale != null) {
/* 576 */       return this.locale;
/*     */     }
/* 578 */     return Locale.getDefault();
/*     */   }
/*     */   
/*     */   public void responseDone() {
/* 582 */     if (this.responseDone || this.treatAsCommitted) {
/*     */       return;
/*     */     }
/* 585 */     this.responseDone = true;
/*     */     try {
/* 587 */       closeStreamAndWriter();
/* 588 */     } catch (IOException e) {
/* 589 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/*     */     } finally {
/* 591 */       this.servletContext.updateSessionAccessTime(this.exchange);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isInsideInclude() {
/* 596 */     return this.insideInclude;
/*     */   }
/*     */   
/*     */   public void setInsideInclude(boolean insideInclude) {
/* 600 */     this.insideInclude = insideInclude;
/*     */   }
/*     */   
/*     */   public void setServletContext(ServletContextImpl servletContext) {
/* 604 */     this.servletContext = servletContext;
/*     */   }
/*     */   
/*     */   public ServletContextImpl getServletContext() {
/* 608 */     return this.servletContext;
/*     */   }
/*     */   
/*     */   public String encodeURL(String url) {
/* 612 */     String absolute = toAbsolute(url);
/* 613 */     if (isEncodeable(absolute)) {
/*     */       
/* 615 */       if (url.equalsIgnoreCase("")) {
/* 616 */         url = absolute;
/*     */       }
/* 618 */       return this.originalServletContext.getSessionConfig().rewriteUrl(url, this.servletContext.getSession(this.originalServletContext, this.exchange, true).getId());
/*     */     } 
/* 620 */     return url;
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
/*     */   public String encodeRedirectURL(String url) {
/* 632 */     if (isEncodeable(toAbsolute(url))) {
/* 633 */       return this.originalServletContext.getSessionConfig().rewriteUrl(url, this.servletContext.getSession(this.originalServletContext, this.exchange, true).getId());
/*     */     }
/* 635 */     return url;
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
/*     */   private String toAbsolute(String location) {
/* 650 */     if (location == null) {
/* 651 */       return location;
/*     */     }
/*     */     
/* 654 */     boolean leadingSlash = location.startsWith("/");
/*     */     
/* 656 */     if (leadingSlash || !hasScheme(location)) {
/* 657 */       return RedirectBuilder.redirect(this.exchange, location, false);
/*     */     }
/* 659 */     return location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasScheme(String uri) {
/* 668 */     int len = uri.length();
/* 669 */     for (int i = 0; i < len; i++) {
/* 670 */       char c = uri.charAt(i);
/* 671 */       if (c == ':')
/* 672 */         return (i > 0); 
/* 673 */       if (!Character.isLetterOrDigit(c) && c != '+' && c != '-' && c != '.')
/*     */       {
/* 675 */         return false;
/*     */       }
/*     */     } 
/* 678 */     return false;
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
/*     */ 
/*     */   
/*     */   private boolean isEncodeable(String location) {
/* 696 */     if (location == null) {
/* 697 */       return false;
/*     */     }
/*     */     
/* 700 */     if (location.startsWith("#")) {
/* 701 */       return false;
/*     */     }
/*     */     
/* 704 */     HttpServletRequestImpl hreq = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getOriginalRequest();
/*     */ 
/*     */     
/* 707 */     if (!this.originalServletContext.getEffectiveSessionTrackingModes().contains(SessionTrackingMode.URL)) {
/* 708 */       return false;
/*     */     }
/*     */     
/* 711 */     HttpSession session = hreq.getSession(false);
/* 712 */     if (session == null)
/* 713 */       return false; 
/* 714 */     if (hreq.isRequestedSessionIdFromCookie())
/* 715 */       return false; 
/* 716 */     if (!hreq.isRequestedSessionIdFromURL() && !session.isNew()) {
/* 717 */       return false;
/*     */     }
/*     */     
/* 720 */     return doIsEncodeable(hreq, session, location);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean doIsEncodeable(HttpServletRequestImpl hreq, HttpSession session, String location) {
/* 726 */     URL url = null;
/*     */     try {
/* 728 */       url = new URL(location);
/* 729 */     } catch (MalformedURLException e) {
/* 730 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 734 */     if (!hreq.getScheme().equalsIgnoreCase(url.getProtocol())) {
/* 735 */       return false;
/*     */     }
/* 737 */     if (!hreq.getServerName().equalsIgnoreCase(url.getHost())) {
/* 738 */       return false;
/*     */     }
/* 740 */     int serverPort = hreq.getServerPort();
/* 741 */     if (serverPort == -1) {
/* 742 */       if ("https".equals(hreq.getScheme())) {
/* 743 */         serverPort = 443;
/*     */       } else {
/* 745 */         serverPort = 80;
/*     */       } 
/*     */     }
/* 748 */     int urlPort = url.getPort();
/* 749 */     if (urlPort == -1) {
/* 750 */       if ("https".equals(url.getProtocol())) {
/* 751 */         urlPort = 443;
/*     */       } else {
/* 753 */         urlPort = 80;
/*     */       } 
/*     */     }
/* 756 */     if (serverPort != urlPort) {
/* 757 */       return false;
/*     */     }
/*     */     
/* 760 */     String file = url.getFile();
/* 761 */     if (file == null) {
/* 762 */       return false;
/*     */     }
/* 764 */     String tok = this.originalServletContext.getSessionCookieConfig().getName().toLowerCase() + "=" + session.getId();
/* 765 */     if (file.contains(tok)) {
/* 766 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 770 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/* 775 */     return this.contentLength;
/*     */   }
/*     */   
/*     */   public enum ResponseState {
/* 779 */     NONE,
/* 780 */     STREAM,
/* 781 */     WRITER;
/*     */   }
/*     */   
/*     */   private static String escapeHtml(String msg) {
/* 785 */     return msg.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
/*     */   }
/*     */   
/*     */   public boolean isTreatAsCommitted() {
/* 789 */     return this.treatAsCommitted;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTrailerFields(Supplier<Map<String, String>> supplier) {
/* 794 */     if (this.exchange.isResponseStarted()) {
/* 795 */       throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
/*     */     }
/* 797 */     if (this.exchange.getProtocol() == Protocols.HTTP_1_0)
/* 798 */       throw UndertowServletMessages.MESSAGES.trailersNotSupported("HTTP/1.0 request"); 
/* 799 */     if (this.exchange.getProtocol() == Protocols.HTTP_1_1 && 
/* 800 */       this.exchange.getResponseHeaders().contains(Headers.CONTENT_LENGTH)) {
/* 801 */       throw UndertowServletMessages.MESSAGES.trailersNotSupported("not chunked");
/*     */     }
/*     */     
/* 804 */     this.trailerSupplier = supplier;
/* 805 */     this.exchange.putAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER, () -> {
/*     */           HeaderMap trailers = new HeaderMap();
/*     */           Map<String, String> map = supplier.get();
/*     */           for (Map.Entry<String, String> e : map.entrySet()) {
/*     */             trailers.put(new HttpString(e.getKey()), e.getValue());
/*     */           }
/*     */           return trailers;
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public Supplier<Map<String, String>> getTrailerFields() {
/* 817 */     return this.trailerSupplier;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\HttpServletResponseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */