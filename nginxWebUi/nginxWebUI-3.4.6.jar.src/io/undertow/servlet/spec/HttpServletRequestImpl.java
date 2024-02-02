/*      */ package io.undertow.servlet.spec;
/*      */ 
/*      */ import io.undertow.security.api.SecurityContext;
/*      */ import io.undertow.security.idm.Account;
/*      */ import io.undertow.server.HttpServerExchange;
/*      */ import io.undertow.server.HttpUpgradeListener;
/*      */ import io.undertow.server.RequestTooBigException;
/*      */ import io.undertow.server.handlers.Cookie;
/*      */ import io.undertow.server.handlers.form.FormData;
/*      */ import io.undertow.server.handlers.form.FormDataParser;
/*      */ import io.undertow.server.protocol.http.HttpAttachments;
/*      */ import io.undertow.server.session.Session;
/*      */ import io.undertow.server.session.SessionConfig;
/*      */ import io.undertow.servlet.UndertowServletMessages;
/*      */ import io.undertow.servlet.api.AuthorizationManager;
/*      */ import io.undertow.servlet.api.Deployment;
/*      */ import io.undertow.servlet.api.InstanceFactory;
/*      */ import io.undertow.servlet.api.InstanceHandle;
/*      */ import io.undertow.servlet.core.ManagedServlet;
/*      */ import io.undertow.servlet.core.ServletUpgradeListener;
/*      */ import io.undertow.servlet.handlers.ServletChain;
/*      */ import io.undertow.servlet.handlers.ServletPathMatch;
/*      */ import io.undertow.servlet.handlers.ServletRequestContext;
/*      */ import io.undertow.servlet.util.EmptyEnumeration;
/*      */ import io.undertow.servlet.util.IteratorEnumeration;
/*      */ import io.undertow.util.AttachmentKey;
/*      */ import io.undertow.util.DateUtils;
/*      */ import io.undertow.util.HeaderMap;
/*      */ import io.undertow.util.HeaderValues;
/*      */ import io.undertow.util.Headers;
/*      */ import io.undertow.util.HttpString;
/*      */ import io.undertow.util.LocaleUtils;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.security.AccessController;
/*      */ import java.security.Principal;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.Deque;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.servlet.AsyncContext;
/*      */ import javax.servlet.DispatcherType;
/*      */ import javax.servlet.MultipartConfigElement;
/*      */ import javax.servlet.RequestDispatcher;
/*      */ import javax.servlet.ServletContext;
/*      */ import javax.servlet.ServletException;
/*      */ import javax.servlet.ServletInputStream;
/*      */ import javax.servlet.ServletRequest;
/*      */ import javax.servlet.ServletResponse;
/*      */ import javax.servlet.http.Cookie;
/*      */ import javax.servlet.http.HttpServletMapping;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.servlet.http.HttpServletResponse;
/*      */ import javax.servlet.http.HttpSession;
/*      */ import javax.servlet.http.MappingMatch;
/*      */ import javax.servlet.http.Part;
/*      */ import javax.servlet.http.PushBuilder;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class HttpServletRequestImpl
/*      */   implements HttpServletRequest
/*      */ {
/*      */   @Deprecated
/*  103 */   public static final AttachmentKey<Boolean> SECURE_REQUEST = HttpServerExchange.SECURE_REQUEST;
/*      */   
/*  105 */   static final AttachmentKey<Boolean> REQUESTED_SESSION_ID_SET = AttachmentKey.create(Boolean.class);
/*  106 */   static final AttachmentKey<String> REQUESTED_SESSION_ID = AttachmentKey.create(String.class);
/*      */   
/*      */   private final HttpServerExchange exchange;
/*      */   
/*      */   private final ServletContextImpl originalServletContext;
/*      */   private ServletContextImpl servletContext;
/*  112 */   private Map<String, Object> attributes = null;
/*      */   
/*      */   private ServletInputStream servletInputStream;
/*      */   
/*      */   private BufferedReader reader;
/*      */   private Cookie[] cookies;
/*  118 */   private List<Part> parts = null;
/*      */   private volatile boolean asyncStarted = false;
/*  120 */   private volatile AsyncContextImpl asyncContext = null;
/*      */   private Map<String, Deque<String>> queryParameters;
/*      */   private FormData parsedFormData;
/*      */   private RuntimeException formParsingException;
/*      */   private Charset characterEncoding;
/*      */   private boolean readStarted;
/*      */   private SessionConfig.SessionCookieSource sessionCookieSource;
/*      */   
/*      */   public HttpServletRequestImpl(HttpServerExchange exchange, ServletContextImpl servletContext) {
/*  129 */     this.exchange = exchange;
/*  130 */     this.servletContext = servletContext;
/*  131 */     this.originalServletContext = servletContext;
/*      */   }
/*      */   
/*      */   public HttpServerExchange getExchange() {
/*  135 */     return this.exchange;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getAuthType() {
/*  140 */     SecurityContext securityContext = this.exchange.getSecurityContext();
/*      */     
/*  142 */     return (securityContext != null) ? securityContext.getMechanismName() : null;
/*      */   }
/*      */ 
/*      */   
/*      */   public Cookie[] getCookies() {
/*  147 */     if (this.cookies == null) {
/*  148 */       Iterable<Cookie> cookies = this.exchange.requestCookies();
/*  149 */       int count = 0;
/*  150 */       for (Cookie cookie : cookies) {
/*  151 */         count++;
/*      */       }
/*  153 */       if (count == 0) {
/*  154 */         return null;
/*      */       }
/*  156 */       Cookie[] value = new Cookie[count];
/*  157 */       int i = 0;
/*  158 */       for (Cookie cookie : cookies) {
/*      */         try {
/*  160 */           Cookie c = new Cookie(cookie.getName(), cookie.getValue());
/*  161 */           if (cookie.getDomain() != null) {
/*  162 */             c.setDomain(cookie.getDomain());
/*      */           }
/*  164 */           c.setHttpOnly(cookie.isHttpOnly());
/*  165 */           if (cookie.getMaxAge() != null) {
/*  166 */             c.setMaxAge(cookie.getMaxAge().intValue());
/*      */           }
/*  168 */           if (cookie.getPath() != null) {
/*  169 */             c.setPath(cookie.getPath());
/*      */           }
/*  171 */           c.setSecure(cookie.isSecure());
/*  172 */           c.setVersion(cookie.getVersion());
/*  173 */           value[i++] = c;
/*  174 */         } catch (IllegalArgumentException illegalArgumentException) {}
/*      */       } 
/*      */ 
/*      */       
/*  178 */       if (i < count) {
/*  179 */         Cookie[] shrunkCookies = new Cookie[i];
/*  180 */         System.arraycopy(value, 0, shrunkCookies, 0, i);
/*  181 */         value = shrunkCookies;
/*      */       } 
/*  183 */       this.cookies = value;
/*      */     } 
/*  185 */     return this.cookies;
/*      */   }
/*      */ 
/*      */   
/*      */   public long getDateHeader(String name) {
/*  190 */     String header = this.exchange.getRequestHeaders().getFirst(name);
/*  191 */     if (header == null) {
/*  192 */       return -1L;
/*      */     }
/*  194 */     Date date = DateUtils.parseDate(header);
/*  195 */     if (date == null) {
/*  196 */       throw UndertowServletMessages.MESSAGES.headerCannotBeConvertedToDate(header);
/*      */     }
/*  198 */     return date.getTime();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getHeader(String name) {
/*  203 */     HeaderMap headers = this.exchange.getRequestHeaders();
/*  204 */     return headers.getFirst(name);
/*      */   }
/*      */   
/*      */   public String getHeader(HttpString name) {
/*  208 */     HeaderMap headers = this.exchange.getRequestHeaders();
/*  209 */     return headers.getFirst(name);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration<String> getHeaders(String name) {
/*  215 */     HeaderValues headerValues = this.exchange.getRequestHeaders().get(name);
/*  216 */     if (headerValues == null) {
/*  217 */       return EmptyEnumeration.instance();
/*      */     }
/*  219 */     return (Enumeration<String>)new IteratorEnumeration(headerValues.iterator());
/*      */   }
/*      */ 
/*      */   
/*      */   public Enumeration<String> getHeaderNames() {
/*  224 */     Set<String> headers = new HashSet<>();
/*  225 */     for (HttpString i : this.exchange.getRequestHeaders().getHeaderNames()) {
/*  226 */       headers.add(i.toString());
/*      */     }
/*  228 */     return (Enumeration<String>)new IteratorEnumeration(headers.iterator());
/*      */   }
/*      */   public HttpServletMapping getHttpServletMapping() {
/*      */     String matched, matchString;
/*      */     int startIndex, endIndex;
/*  233 */     ServletRequestContext src = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  234 */     ServletPathMatch match = src.getOriginalServletPathMatch();
/*  235 */     DispatcherType dispatcherType = getDispatcherType();
/*      */     
/*  237 */     if (dispatcherType == DispatcherType.FORWARD || dispatcherType == DispatcherType.ERROR) {
/*  238 */       match = src.getServletPathMatch();
/*      */     }
/*      */     
/*  241 */     switch (match.getMappingMatch())
/*      */     { case EXACT:
/*  243 */         matchValue = match.getMatched();
/*  244 */         if (matchValue.startsWith("/")) {
/*  245 */           matchValue = matchValue.substring(1);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  270 */         return new MappingImpl(matchValue, match.getMatchString(), match.getMappingMatch(), match.getServletChain().getManagedServlet().getServletInfo().getName());case DEFAULT: case CONTEXT_ROOT: matchValue = ""; return new MappingImpl(matchValue, match.getMatchString(), match.getMappingMatch(), match.getServletChain().getManagedServlet().getServletInfo().getName());case PATH: matchValue = match.getRemaining(); if (matchValue == null) { matchValue = ""; } else if (matchValue.startsWith("/")) { matchValue = matchValue.substring(1); }  return new MappingImpl(matchValue, match.getMatchString(), match.getMappingMatch(), match.getServletChain().getManagedServlet().getServletInfo().getName());case EXTENSION: matched = match.getMatched(); matchString = match.getMatchString(); startIndex = matched.startsWith("/") ? 1 : 0; endIndex = matched.length() - matchString.length() + 1; matchValue = matched.substring(startIndex, endIndex); return new MappingImpl(matchValue, match.getMatchString(), match.getMappingMatch(), match.getServletChain().getManagedServlet().getServletInfo().getName()); }  String matchValue = match.getRemaining(); return new MappingImpl(matchValue, match.getMatchString(), match.getMappingMatch(), match.getServletChain().getManagedServlet().getServletInfo().getName());
/*      */   }
/*      */ 
/*      */   
/*      */   public int getIntHeader(String name) {
/*  275 */     String header = getHeader(name);
/*  276 */     if (header == null) {
/*  277 */       return -1;
/*      */     }
/*  279 */     return Integer.parseInt(header);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getMethod() {
/*  284 */     return this.exchange.getRequestMethod().toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getPathInfo() {
/*  289 */     ServletPathMatch match = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletPathMatch();
/*  290 */     if (match != null) {
/*  291 */       return match.getRemaining();
/*      */     }
/*  293 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getPathTranslated() {
/*  298 */     return getRealPath(getPathInfo());
/*      */   }
/*      */ 
/*      */   
/*      */   public String getContextPath() {
/*  303 */     return this.servletContext.getContextPath();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getQueryString() {
/*  308 */     return this.exchange.getQueryString().isEmpty() ? null : this.exchange.getQueryString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getRemoteUser() {
/*  313 */     Principal userPrincipal = getUserPrincipal();
/*      */     
/*  315 */     return (userPrincipal != null) ? userPrincipal.getName() : null;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isUserInRole(String role) {
/*  320 */     if (role == null) {
/*  321 */       return false;
/*      */     }
/*      */     
/*  324 */     if (role.equals("*")) {
/*  325 */       return false;
/*      */     }
/*  327 */     SecurityContext sc = this.exchange.getSecurityContext();
/*  328 */     Account account = (sc != null) ? sc.getAuthenticatedAccount() : null;
/*  329 */     if (account == null) {
/*  330 */       return false;
/*      */     }
/*  332 */     ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*      */     
/*  334 */     if (role.equals("**")) {
/*  335 */       Set<String> roles = servletRequestContext.getDeployment().getDeploymentInfo().getSecurityRoles();
/*  336 */       if (!roles.contains("**")) {
/*  337 */         return true;
/*      */       }
/*      */     } 
/*      */     
/*  341 */     ServletChain servlet = servletRequestContext.getCurrentServlet();
/*  342 */     Deployment deployment = this.servletContext.getDeployment();
/*  343 */     AuthorizationManager authorizationManager = deployment.getDeploymentInfo().getAuthorizationManager();
/*  344 */     return authorizationManager.isUserInRole(role, account, servlet.getManagedServlet().getServletInfo(), this, deployment);
/*      */   }
/*      */ 
/*      */   
/*      */   public Principal getUserPrincipal() {
/*  349 */     SecurityContext securityContext = this.exchange.getSecurityContext();
/*  350 */     Principal result = null;
/*  351 */     Account account = null;
/*  352 */     if (securityContext != null && (account = securityContext.getAuthenticatedAccount()) != null) {
/*  353 */       result = account.getPrincipal();
/*      */     }
/*  355 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getRequestedSessionId() {
/*  360 */     Boolean isRequestedSessionIdSaved = (Boolean)this.exchange.getAttachment(REQUESTED_SESSION_ID_SET);
/*  361 */     if (isRequestedSessionIdSaved != null && isRequestedSessionIdSaved.booleanValue()) {
/*  362 */       return (String)this.exchange.getAttachment(REQUESTED_SESSION_ID);
/*      */     }
/*  364 */     SessionConfig config = this.originalServletContext.getSessionConfig();
/*  365 */     if (config instanceof ServletContextImpl.ServletContextSessionConfig) {
/*  366 */       return ((ServletContextImpl.ServletContextSessionConfig)config).getDelegate().findSessionId(this.exchange);
/*      */     }
/*  368 */     return config.findSessionId(this.exchange);
/*      */   }
/*      */   
/*      */   public String changeSessionId() {
/*      */     Session underlyingSession;
/*  373 */     HttpSessionImpl session = this.servletContext.getSession(this.originalServletContext, this.exchange, false);
/*  374 */     if (session == null) {
/*  375 */       throw UndertowServletMessages.MESSAGES.noSession();
/*      */     }
/*  377 */     String oldId = session.getId();
/*      */     
/*  379 */     if (System.getSecurityManager() == null) {
/*  380 */       underlyingSession = session.getSession();
/*      */     } else {
/*  382 */       underlyingSession = AccessController.<Session>doPrivileged(new HttpSessionImpl.UnwrapSessionAction(session));
/*      */     } 
/*  384 */     String newId = underlyingSession.changeSessionId(this.exchange, this.originalServletContext.getSessionConfig());
/*  385 */     this.servletContext.getDeployment().getApplicationListeners().httpSessionIdChanged(session, oldId);
/*  386 */     return newId;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getRequestURI() {
/*  392 */     if (this.exchange.isHostIncludedInRequestURI()) {
/*      */       
/*  394 */       String uri = this.exchange.getRequestURI();
/*  395 */       int slashes = 0;
/*  396 */       for (int i = 0; i < uri.length(); i++) {
/*  397 */         if (uri.charAt(i) == '/' && 
/*  398 */           ++slashes == 3) {
/*  399 */           return uri.substring(i);
/*      */         }
/*      */       } 
/*      */       
/*  403 */       return "/";
/*      */     } 
/*  405 */     return this.exchange.getRequestURI();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public StringBuffer getRequestURL() {
/*  411 */     return new StringBuffer(this.exchange.getRequestURL());
/*      */   }
/*      */ 
/*      */   
/*      */   public String getServletPath() {
/*  416 */     ServletPathMatch match = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletPathMatch();
/*  417 */     if (match != null) {
/*  418 */       return match.getMatched();
/*      */     }
/*  420 */     return "";
/*      */   }
/*      */ 
/*      */   
/*      */   public HttpSession getSession(boolean create) {
/*  425 */     return this.servletContext.getSession(this.originalServletContext, this.exchange, create);
/*      */   }
/*      */ 
/*      */   
/*      */   public HttpSession getSession() {
/*  430 */     return getSession(true);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isRequestedSessionIdValid() {
/*  436 */     HttpSessionImpl session = this.servletContext.getSession(this.originalServletContext, this.exchange, false);
/*  437 */     if (session == null) {
/*  438 */       return false;
/*      */     }
/*  440 */     if (session.isInvalid()) {
/*  441 */       return false;
/*      */     }
/*  443 */     return session.getId().equals(getRequestedSessionId());
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isRequestedSessionIdFromCookie() {
/*  448 */     return (sessionCookieSource() == SessionConfig.SessionCookieSource.COOKIE);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isRequestedSessionIdFromURL() {
/*  453 */     return (sessionCookieSource() == SessionConfig.SessionCookieSource.URL);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isRequestedSessionIdFromUrl() {
/*  458 */     return isRequestedSessionIdFromURL();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
/*  463 */     if (response.isCommitted()) {
/*  464 */       throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
/*      */     }
/*      */     
/*  467 */     SecurityContext sc = this.exchange.getSecurityContext();
/*  468 */     if (sc == null) {
/*  469 */       throw UndertowServletMessages.MESSAGES.noSecurityContextAvailable();
/*      */     }
/*      */     
/*  472 */     sc.setAuthenticationRequired();
/*      */ 
/*      */     
/*  475 */     if (sc.authenticate()) {
/*  476 */       if (sc.isAuthenticated()) {
/*  477 */         return true;
/*      */       }
/*  479 */       throw UndertowServletMessages.MESSAGES.authenticationFailed();
/*      */     } 
/*      */     
/*  482 */     if (!this.exchange.isResponseStarted() && this.exchange.getStatusCode() == 200) {
/*  483 */       throw UndertowServletMessages.MESSAGES.authenticationFailed();
/*      */     }
/*  485 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void login(String username, String password) throws ServletException {
/*  492 */     if (username == null || password == null) {
/*  493 */       throw UndertowServletMessages.MESSAGES.loginFailed();
/*      */     }
/*  495 */     SecurityContext sc = this.exchange.getSecurityContext();
/*  496 */     if (sc == null)
/*  497 */       throw UndertowServletMessages.MESSAGES.noSecurityContextAvailable(); 
/*  498 */     if (sc.isAuthenticated()) {
/*  499 */       throw UndertowServletMessages.MESSAGES.userAlreadyLoggedIn();
/*      */     }
/*  501 */     boolean login = false;
/*      */     try {
/*  503 */       login = sc.login(username, password);
/*      */     }
/*  505 */     catch (SecurityException se) {
/*  506 */       if (se.getCause() instanceof ServletException)
/*  507 */         throw (ServletException)se.getCause(); 
/*  508 */       throw new ServletException(se);
/*      */     } 
/*  510 */     if (!login) {
/*  511 */       throw UndertowServletMessages.MESSAGES.loginFailed();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void logout() throws ServletException {
/*  517 */     SecurityContext sc = this.exchange.getSecurityContext();
/*  518 */     if (sc == null) {
/*  519 */       throw UndertowServletMessages.MESSAGES.noSecurityContextAvailable();
/*      */     }
/*  521 */     sc.logout();
/*  522 */     if (this.servletContext.getDeployment().getDeploymentInfo().isInvalidateSessionOnLogout()) {
/*  523 */       HttpSession session = getSession(false);
/*  524 */       if (session != null) {
/*  525 */         session.invalidate();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public Collection<Part> getParts() throws IOException, ServletException {
/*  532 */     verifyMultipartServlet();
/*  533 */     if (this.parts == null) {
/*  534 */       loadParts();
/*      */     }
/*  536 */     return this.parts;
/*      */   }
/*      */   
/*      */   private void verifyMultipartServlet() {
/*  540 */     ServletRequestContext src = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  541 */     MultipartConfigElement multipart = src.getServletPathMatch().getServletChain().getManagedServlet().getMultipartConfig();
/*  542 */     if (multipart == null) {
/*  543 */       throw UndertowServletMessages.MESSAGES.multipartConfigNotPresent();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public Part getPart(String name) throws IOException, ServletException {
/*  549 */     verifyMultipartServlet();
/*  550 */     if (this.parts == null) {
/*  551 */       loadParts();
/*      */     }
/*  553 */     for (Part part : this.parts) {
/*  554 */       if (part.getName().equals(name)) {
/*  555 */         return part;
/*      */       }
/*      */     } 
/*  558 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public <T extends javax.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException {
/*      */     try {
/*  564 */       InstanceFactory<T> factory = this.servletContext.getDeployment().getDeploymentInfo().getClassIntrospecter().createInstanceFactory(handlerClass);
/*  565 */       InstanceHandle<T> instance = factory.createInstance();
/*  566 */       this.exchange.upgradeChannel((HttpUpgradeListener)new ServletUpgradeListener(instance, this.servletContext.getDeployment(), this.exchange));
/*  567 */       return (T)instance.getInstance();
/*  568 */     } catch (InstantiationException e) {
/*  569 */       throw new RuntimeException(e);
/*  570 */     } catch (NoSuchMethodException e) {
/*  571 */       throw new RuntimeException(e);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void loadParts() throws IOException, ServletException {
/*  576 */     ServletRequestContext requestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*      */     
/*  578 */     if (this.parts == null) {
/*  579 */       List<Part> parts = new ArrayList<>();
/*  580 */       String mimeType = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
/*  581 */       if (mimeType != null && mimeType.startsWith("multipart/form-data")) {
/*      */         
/*  583 */         FormData formData = parseFormData();
/*  584 */         if (formData != null) {
/*  585 */           for (String namedPart : formData) {
/*  586 */             for (FormData.FormValue part : formData.get(namedPart)) {
/*  587 */               parts.add(new PartImpl(namedPart, part, requestContext
/*      */                     
/*  589 */                     .getOriginalServletPathMatch().getServletChain().getManagedServlet().getMultipartConfig(), this.servletContext, this));
/*      */             }
/*      */           } 
/*      */         }
/*      */       } else {
/*      */         
/*  595 */         throw UndertowServletMessages.MESSAGES.notAMultiPartRequest();
/*      */       } 
/*  597 */       this.parts = parts;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getAttribute(String name) {
/*  603 */     if (this.attributes == null) {
/*  604 */       return null;
/*      */     }
/*  606 */     return this.attributes.get(name);
/*      */   }
/*      */ 
/*      */   
/*      */   public Enumeration<String> getAttributeNames() {
/*  611 */     if (this.attributes == null) {
/*  612 */       return EmptyEnumeration.instance();
/*      */     }
/*  614 */     return (Enumeration<String>)new IteratorEnumeration(this.attributes.keySet().iterator());
/*      */   }
/*      */ 
/*      */   
/*      */   public String getCharacterEncoding() {
/*  619 */     if (this.characterEncoding != null) {
/*  620 */       return this.characterEncoding.name();
/*      */     }
/*      */     
/*  623 */     String characterEncodingFromHeader = getCharacterEncodingFromHeader();
/*  624 */     if (characterEncodingFromHeader != null) {
/*  625 */       return characterEncodingFromHeader;
/*      */     }
/*      */     
/*  628 */     if (this.servletContext.getDeployment().getDeploymentInfo().getDefaultRequestEncoding() != null) {
/*  629 */       return this.servletContext.getDeployment().getDeploymentInfo().getDefaultRequestEncoding();
/*      */     }
/*      */     
/*  632 */     if (this.servletContext.getDeployment().getDeploymentInfo().getDefaultEncoding() != null) {
/*  633 */       return this.servletContext.getDeployment().getDeploymentInfo().getDefaultEncoding();
/*      */     }
/*  635 */     return null;
/*      */   }
/*      */   
/*      */   private String getCharacterEncodingFromHeader() {
/*  639 */     String contentType = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
/*  640 */     if (contentType == null) {
/*  641 */       return null;
/*      */     }
/*      */     
/*  644 */     return Headers.extractQuotedValueFromHeader(contentType, "charset");
/*      */   }
/*      */ 
/*      */   
/*      */   public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
/*  649 */     if (this.readStarted) {
/*      */       return;
/*      */     }
/*      */     try {
/*  653 */       this.characterEncoding = Charset.forName(env);
/*      */       
/*  655 */       ManagedServlet originalServlet = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getOriginalServletPathMatch().getServletChain().getManagedServlet();
/*  656 */       FormDataParser parser = originalServlet.getFormParserFactory().createParser(this.exchange);
/*  657 */       if (parser != null) {
/*  658 */         parser.setCharacterEncoding(env);
/*      */       }
/*  660 */     } catch (UnsupportedCharsetException e) {
/*  661 */       throw new UnsupportedEncodingException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int getContentLength() {
/*  667 */     long length = getContentLengthLong();
/*  668 */     if (length > 2147483647L) {
/*  669 */       return -1;
/*      */     }
/*  671 */     return (int)length;
/*      */   }
/*      */ 
/*      */   
/*      */   public long getContentLengthLong() {
/*  676 */     String contentLength = getHeader(Headers.CONTENT_LENGTH);
/*  677 */     if (contentLength == null || contentLength.isEmpty()) {
/*  678 */       return -1L;
/*      */     }
/*  680 */     return Long.parseLong(contentLength);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getContentType() {
/*  685 */     return getHeader(Headers.CONTENT_TYPE);
/*      */   }
/*      */ 
/*      */   
/*      */   public ServletInputStream getInputStream() throws IOException {
/*  690 */     if (this.reader != null) {
/*  691 */       throw UndertowServletMessages.MESSAGES.getReaderAlreadyCalled();
/*      */     }
/*  693 */     if (this.servletInputStream == null) {
/*  694 */       this.servletInputStream = new ServletInputStreamImpl(this);
/*      */     }
/*  696 */     this.readStarted = true;
/*  697 */     return this.servletInputStream;
/*      */   }
/*      */   
/*      */   public void closeAndDrainRequest() throws IOException {
/*  701 */     if (this.reader != null) {
/*  702 */       this.reader.close();
/*      */     }
/*  704 */     if (this.servletInputStream == null) {
/*  705 */       this.servletInputStream = new ServletInputStreamImpl(this);
/*      */     }
/*  707 */     this.servletInputStream.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void freeResources() throws IOException {
/*      */     try {
/*  716 */       if (this.reader != null) {
/*  717 */         this.reader.close();
/*      */       }
/*  719 */       if (this.servletInputStream != null) {
/*  720 */         this.servletInputStream.close();
/*      */       }
/*      */     } finally {
/*  723 */       clearAttributes();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public String getParameter(String name) {
/*  729 */     if (this.queryParameters == null) {
/*  730 */       this.queryParameters = this.exchange.getQueryParameters();
/*      */     }
/*  732 */     Deque<String> params = this.queryParameters.get(name);
/*  733 */     if (params == null) {
/*  734 */       FormData parsedFormData = parseFormData();
/*  735 */       if (parsedFormData != null) {
/*  736 */         FormData.FormValue res = parsedFormData.getFirst(name);
/*  737 */         if (res == null || res.isFileItem()) {
/*  738 */           return null;
/*      */         }
/*  740 */         return res.getValue();
/*      */       } 
/*      */       
/*  743 */       return null;
/*      */     } 
/*  745 */     return params.getFirst();
/*      */   }
/*      */ 
/*      */   
/*      */   public Enumeration<String> getParameterNames() {
/*  750 */     if (this.queryParameters == null) {
/*  751 */       this.queryParameters = this.exchange.getQueryParameters();
/*      */     }
/*  753 */     Set<String> parameterNames = new HashSet<>(this.queryParameters.keySet());
/*  754 */     FormData parsedFormData = parseFormData();
/*  755 */     if (parsedFormData != null) {
/*  756 */       Iterator<String> it = parsedFormData.iterator();
/*  757 */       while (it.hasNext()) {
/*  758 */         String name = it.next();
/*  759 */         for (FormData.FormValue param : parsedFormData.get(name)) {
/*  760 */           if (!param.isFileItem()) {
/*  761 */             parameterNames.add(name);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  767 */     return (Enumeration<String>)new IteratorEnumeration(parameterNames.iterator());
/*      */   }
/*      */ 
/*      */   
/*      */   public String[] getParameterValues(String name) {
/*  772 */     if (this.queryParameters == null) {
/*  773 */       this.queryParameters = this.exchange.getQueryParameters();
/*      */     }
/*  775 */     List<String> ret = new ArrayList<>();
/*  776 */     Deque<String> params = this.queryParameters.get(name);
/*  777 */     if (params != null) {
/*  778 */       for (String param : params) {
/*  779 */         ret.add(param);
/*      */       }
/*      */     }
/*  782 */     FormData parsedFormData = parseFormData();
/*  783 */     if (parsedFormData != null) {
/*  784 */       Deque<FormData.FormValue> res = parsedFormData.get(name);
/*  785 */       if (res != null) {
/*  786 */         for (FormData.FormValue value : res) {
/*  787 */           if (!value.isFileItem()) {
/*  788 */             ret.add(value.getValue());
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*  793 */     if (ret.isEmpty()) {
/*  794 */       return null;
/*      */     }
/*  796 */     return ret.<String>toArray(new String[ret.size()]);
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<String, String[]> getParameterMap() {
/*  801 */     if (this.queryParameters == null) {
/*  802 */       this.queryParameters = this.exchange.getQueryParameters();
/*      */     }
/*  804 */     Map<String, ArrayList<String>> arrayMap = new HashMap<>();
/*  805 */     for (Map.Entry<String, Deque<String>> entry : this.queryParameters.entrySet()) {
/*  806 */       arrayMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
/*      */     }
/*      */     
/*  809 */     FormData parsedFormData = parseFormData();
/*  810 */     if (parsedFormData != null) {
/*  811 */       Iterator<String> it = parsedFormData.iterator();
/*  812 */       while (it.hasNext()) {
/*  813 */         String name = it.next();
/*  814 */         Deque<FormData.FormValue> val = parsedFormData.get(name);
/*  815 */         if (arrayMap.containsKey(name)) {
/*  816 */           ArrayList<String> existing = arrayMap.get(name);
/*  817 */           for (FormData.FormValue v : val) {
/*  818 */             if (!v.isFileItem())
/*  819 */               existing.add(v.getValue()); 
/*      */           } 
/*      */           continue;
/*      */         } 
/*  823 */         ArrayList<String> values = new ArrayList<>();
/*  824 */         for (FormData.FormValue v : val) {
/*  825 */           if (!v.isFileItem()) {
/*  826 */             values.add(v.getValue());
/*      */           }
/*      */         } 
/*  829 */         if (!values.isEmpty()) {
/*  830 */           arrayMap.put(name, values);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  835 */     Map<String, String[]> ret = (Map)new HashMap<>();
/*  836 */     for (Map.Entry<String, ArrayList<String>> entry : arrayMap.entrySet()) {
/*  837 */       ret.put(entry.getKey(), (String[])((ArrayList)entry.getValue()).toArray((Object[])new String[((ArrayList)entry.getValue()).size()]));
/*      */     }
/*  839 */     return ret;
/*      */   }
/*      */   
/*      */   private FormData parseFormData() {
/*  843 */     if (this.formParsingException != null) {
/*  844 */       throw this.formParsingException;
/*      */     }
/*  846 */     if (this.parsedFormData == null) {
/*  847 */       if (this.readStarted) {
/*  848 */         return null;
/*      */       }
/*  850 */       ManagedServlet originalServlet = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getCurrentServlet().getManagedServlet();
/*  851 */       FormDataParser parser = originalServlet.getFormParserFactory().createParser(this.exchange);
/*  852 */       if (parser == null) {
/*  853 */         return null;
/*      */       }
/*  855 */       this.readStarted = true;
/*      */       try {
/*  857 */         return this.parsedFormData = parser.parseBlocking();
/*  858 */       } catch (RequestTooBigException|io.undertow.server.handlers.form.MultiPartParserDefinition.FileTooLargeException e) {
/*  859 */         throw this.formParsingException = new IllegalStateException(e);
/*  860 */       } catch (RuntimeException e) {
/*  861 */         throw this.formParsingException = e;
/*  862 */       } catch (IOException e) {
/*  863 */         throw this.formParsingException = new RuntimeException(e);
/*      */       } 
/*      */     } 
/*  866 */     return this.parsedFormData;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getProtocol() {
/*  871 */     return this.exchange.getProtocol().toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getScheme() {
/*  876 */     return this.exchange.getRequestScheme();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getServerName() {
/*  881 */     return this.exchange.getHostName();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getServerPort() {
/*  886 */     return this.exchange.getHostPort();
/*      */   }
/*      */ 
/*      */   
/*      */   public BufferedReader getReader() throws IOException {
/*  891 */     if (this.reader == null) {
/*  892 */       if (this.servletInputStream != null) {
/*  893 */         throw UndertowServletMessages.MESSAGES.getInputStreamAlreadyCalled();
/*      */       }
/*  895 */       Charset charSet = null;
/*  896 */       if (this.characterEncoding != null) {
/*  897 */         charSet = this.characterEncoding;
/*      */       } else {
/*  899 */         String c = getCharacterEncoding();
/*  900 */         if (c != null) {
/*      */           try {
/*  902 */             charSet = Charset.forName(c);
/*  903 */           } catch (UnsupportedCharsetException e) {
/*  904 */             throw new UnsupportedEncodingException(e.getMessage());
/*      */           } 
/*      */         }
/*      */       } 
/*      */       
/*  909 */       this
/*  910 */         .reader = new BufferedReader((charSet == null) ? new InputStreamReader(this.exchange.getInputStream(), StandardCharsets.ISO_8859_1) : new InputStreamReader(this.exchange.getInputStream(), charSet));
/*      */     } 
/*  912 */     this.readStarted = true;
/*  913 */     return this.reader;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getRemoteAddr() {
/*  918 */     InetSocketAddress sourceAddress = this.exchange.getSourceAddress();
/*  919 */     if (sourceAddress == null) {
/*  920 */       return "";
/*      */     }
/*  922 */     InetAddress address = sourceAddress.getAddress();
/*  923 */     if (address == null)
/*      */     {
/*      */ 
/*      */       
/*  927 */       return sourceAddress.getHostString();
/*      */     }
/*  929 */     return address.getHostAddress();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getRemoteHost() {
/*  934 */     InetSocketAddress sourceAddress = this.exchange.getSourceAddress();
/*  935 */     if (sourceAddress == null) {
/*  936 */       return "";
/*      */     }
/*  938 */     return sourceAddress.getHostString();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAttribute(String name, Object object) {
/*  943 */     if (object == null) {
/*  944 */       removeAttribute(name);
/*      */       return;
/*      */     } 
/*  947 */     if (this.attributes == null) {
/*  948 */       this.attributes = new HashMap<>();
/*      */     }
/*  950 */     Object existing = this.attributes.put(name, object);
/*  951 */     if (existing != null) {
/*  952 */       this.servletContext.getDeployment().getApplicationListeners().servletRequestAttributeReplaced(this, name, existing);
/*      */     } else {
/*  954 */       this.servletContext.getDeployment().getApplicationListeners().servletRequestAttributeAdded(this, name, object);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void removeAttribute(String name) {
/*  960 */     if (this.attributes == null) {
/*      */       return;
/*      */     }
/*  963 */     Object exiting = this.attributes.remove(name);
/*  964 */     this.servletContext.getDeployment().getApplicationListeners().servletRequestAttributeRemoved(this, name, exiting);
/*      */   }
/*      */ 
/*      */   
/*      */   public Locale getLocale() {
/*  969 */     return getLocales().nextElement();
/*      */   }
/*      */ 
/*      */   
/*      */   public Enumeration<Locale> getLocales() {
/*  974 */     HeaderValues headerValues = this.exchange.getRequestHeaders().get(Headers.ACCEPT_LANGUAGE);
/*  975 */     List<Locale> ret = LocaleUtils.getLocalesFromHeader((List)headerValues);
/*  976 */     if (ret.isEmpty()) {
/*  977 */       return (Enumeration<Locale>)new IteratorEnumeration(Collections.<Locale>singletonList(Locale.getDefault()).iterator());
/*      */     }
/*  979 */     return (Enumeration<Locale>)new IteratorEnumeration(ret.iterator());
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isSecure() {
/*  984 */     return this.exchange.isSecure();
/*      */   }
/*      */   
/*      */   public RequestDispatcher getRequestDispatcher(String path) {
/*      */     String realPath;
/*  989 */     if (path == null) {
/*  990 */       return null;
/*      */     }
/*      */     
/*  993 */     if (path.startsWith("/")) {
/*  994 */       realPath = path;
/*      */     } else {
/*  996 */       String current = this.exchange.getRelativePath();
/*  997 */       int lastSlash = current.lastIndexOf("/");
/*  998 */       if (lastSlash != -1) {
/*  999 */         current = current.substring(0, lastSlash + 1);
/*      */       }
/* 1001 */       realPath = current + path;
/*      */     } 
/* 1003 */     return this.servletContext.getRequestDispatcher(realPath);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getRealPath(String path) {
/* 1008 */     return this.servletContext.getRealPath(path);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getRemotePort() {
/* 1013 */     return this.exchange.getSourceAddress().getPort();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getLocalName() {
/* 1025 */     return this.exchange.getDestinationAddress().getHostName();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getLocalAddr() {
/* 1030 */     InetSocketAddress destinationAddress = this.exchange.getDestinationAddress();
/* 1031 */     if (destinationAddress == null) {
/* 1032 */       return "";
/*      */     }
/* 1034 */     InetAddress address = destinationAddress.getAddress();
/* 1035 */     if (address == null)
/*      */     {
/* 1037 */       return destinationAddress.getHostString();
/*      */     }
/* 1039 */     return address.getHostAddress();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getLocalPort() {
/* 1044 */     return this.exchange.getDestinationAddress().getPort();
/*      */   }
/*      */ 
/*      */   
/*      */   public ServletContextImpl getServletContext() {
/* 1049 */     return this.servletContext;
/*      */   }
/*      */ 
/*      */   
/*      */   public AsyncContext startAsync() throws IllegalStateException {
/* 1054 */     if (!isAsyncSupported())
/* 1055 */       throw UndertowServletMessages.MESSAGES.startAsyncNotAllowed(); 
/* 1056 */     if (this.asyncStarted) {
/* 1057 */       throw UndertowServletMessages.MESSAGES.asyncAlreadyStarted();
/*      */     }
/* 1059 */     this.asyncStarted = true;
/* 1060 */     ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 1061 */     return this.asyncContext = new AsyncContextImpl(this.exchange, servletRequestContext.getServletRequest(), servletRequestContext.getServletResponse(), servletRequestContext, false, this.asyncContext);
/*      */   }
/*      */ 
/*      */   
/*      */   public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
/* 1066 */     ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 1067 */     if (!this.servletContext.getDeployment().getDeploymentInfo().isAllowNonStandardWrappers()) {
/* 1068 */       if (servletRequestContext.getOriginalRequest() != servletRequest && 
/* 1069 */         !(servletRequest instanceof javax.servlet.ServletRequestWrapper)) {
/* 1070 */         throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(servletRequest);
/*      */       }
/*      */       
/* 1073 */       if (servletRequestContext.getOriginalResponse() != servletResponse && 
/* 1074 */         !(servletResponse instanceof javax.servlet.ServletResponseWrapper)) {
/* 1075 */         throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(servletResponse);
/*      */       }
/*      */     } 
/*      */     
/* 1079 */     if (!isAsyncSupported())
/* 1080 */       throw UndertowServletMessages.MESSAGES.startAsyncNotAllowed(); 
/* 1081 */     if (this.asyncStarted) {
/* 1082 */       throw UndertowServletMessages.MESSAGES.asyncAlreadyStarted();
/*      */     }
/* 1084 */     this.asyncStarted = true;
/* 1085 */     servletRequestContext.setServletRequest(servletRequest);
/* 1086 */     servletRequestContext.setServletResponse(servletResponse);
/* 1087 */     return this.asyncContext = new AsyncContextImpl(this.exchange, servletRequest, servletResponse, servletRequestContext, true, this.asyncContext);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isAsyncStarted() {
/* 1092 */     return this.asyncStarted;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isAsyncSupported() {
/* 1097 */     return ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).isAsyncSupported();
/*      */   }
/*      */ 
/*      */   
/*      */   public AsyncContextImpl getAsyncContext() {
/* 1102 */     if (!isAsyncStarted()) {
/* 1103 */       throw UndertowServletMessages.MESSAGES.asyncNotStarted();
/*      */     }
/* 1105 */     return this.asyncContext;
/*      */   }
/*      */   
/*      */   public AsyncContextImpl getAsyncContextInternal() {
/* 1109 */     return this.asyncContext;
/*      */   }
/*      */ 
/*      */   
/*      */   public DispatcherType getDispatcherType() {
/* 1114 */     return ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getDispatcherType();
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<String, Deque<String>> getQueryParameters() {
/* 1119 */     if (this.queryParameters == null) {
/* 1120 */       this.queryParameters = this.exchange.getQueryParameters();
/*      */     }
/* 1122 */     return this.queryParameters;
/*      */   }
/*      */   
/*      */   public void setQueryParameters(Map<String, Deque<String>> queryParameters) {
/* 1126 */     this.queryParameters = queryParameters;
/*      */   }
/*      */   
/*      */   public void setServletContext(ServletContextImpl servletContext) {
/* 1130 */     this.servletContext = servletContext;
/*      */   }
/*      */   
/*      */   void asyncRequestDispatched() {
/* 1134 */     this.asyncStarted = false;
/*      */   }
/*      */   
/*      */   public String getOriginalRequestURI() {
/* 1138 */     String uri = (String)getAttribute("javax.servlet.forward.request_uri");
/* 1139 */     if (uri != null) {
/* 1140 */       return uri;
/*      */     }
/* 1142 */     uri = (String)getAttribute("javax.servlet.async.request_uri");
/* 1143 */     if (uri != null) {
/* 1144 */       return uri;
/*      */     }
/* 1146 */     return getRequestURI();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getOriginalServletPath() {
/* 1151 */     String uri = (String)getAttribute("javax.servlet.forward.servlet_path");
/* 1152 */     if (uri != null) {
/* 1153 */       return uri;
/*      */     }
/* 1155 */     uri = (String)getAttribute("javax.servlet.async.servlet_path");
/* 1156 */     if (uri != null) {
/* 1157 */       return uri;
/*      */     }
/* 1159 */     return getServletPath();
/*      */   }
/*      */   
/*      */   public String getOriginalPathInfo() {
/* 1163 */     String uri = (String)getAttribute("javax.servlet.forward.path_info");
/* 1164 */     if (uri != null) {
/* 1165 */       return uri;
/*      */     }
/* 1167 */     uri = (String)getAttribute("javax.servlet.async.path_info");
/* 1168 */     if (uri != null) {
/* 1169 */       return uri;
/*      */     }
/* 1171 */     return getPathInfo();
/*      */   }
/*      */   
/*      */   public String getOriginalContextPath() {
/* 1175 */     String uri = (String)getAttribute("javax.servlet.forward.context_path");
/* 1176 */     if (uri != null) {
/* 1177 */       return uri;
/*      */     }
/* 1179 */     uri = (String)getAttribute("javax.servlet.async.context_path");
/* 1180 */     if (uri != null) {
/* 1181 */       return uri;
/*      */     }
/* 1183 */     return getContextPath();
/*      */   }
/*      */   
/*      */   public String getOriginalQueryString() {
/* 1187 */     String uri = (String)getAttribute("javax.servlet.forward.query_string");
/* 1188 */     if (uri != null) {
/* 1189 */       return uri;
/*      */     }
/* 1191 */     uri = (String)getAttribute("javax.servlet.async.query_string");
/* 1192 */     if (uri != null) {
/* 1193 */       return uri;
/*      */     }
/* 1195 */     return getQueryString();
/*      */   }
/*      */   
/*      */   private SessionConfig.SessionCookieSource sessionCookieSource() {
/* 1199 */     HttpSession session = getSession(false);
/* 1200 */     if (session == null) {
/* 1201 */       return SessionConfig.SessionCookieSource.NONE;
/*      */     }
/* 1203 */     if (this.sessionCookieSource == null) {
/* 1204 */       this.sessionCookieSource = this.originalServletContext.getSessionConfig().sessionCookieSource(this.exchange);
/*      */     }
/* 1206 */     return this.sessionCookieSource;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1211 */     return "HttpServletRequestImpl [ " + getMethod() + ' ' + getRequestURI() + " ]";
/*      */   }
/*      */   
/*      */   public void clearAttributes() {
/* 1215 */     if (this.attributes != null) {
/* 1216 */       this.attributes.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public PushBuilder newPushBuilder() {
/* 1222 */     if (this.exchange.getConnection().isPushSupported()) {
/* 1223 */       return new PushBuilderImpl(this);
/*      */     }
/* 1225 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<String, String> getTrailerFields() {
/* 1230 */     HeaderMap trailers = (HeaderMap)this.exchange.getAttachment(HttpAttachments.REQUEST_TRAILERS);
/* 1231 */     if (trailers == null) {
/* 1232 */       return Collections.emptyMap();
/*      */     }
/* 1234 */     Map<String, String> ret = new HashMap<>();
/* 1235 */     for (HeaderValues entry : trailers) {
/* 1236 */       ret.put(entry.getHeaderName().toString().toLowerCase(Locale.ENGLISH), entry.getFirst());
/*      */     }
/* 1238 */     return ret;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isTrailerFieldsReady() {
/* 1243 */     if (this.exchange.isRequestComplete()) {
/* 1244 */       return true;
/*      */     }
/* 1246 */     return !this.exchange.getConnection().isRequestTrailerFieldsSupported();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\HttpServletRequestImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */