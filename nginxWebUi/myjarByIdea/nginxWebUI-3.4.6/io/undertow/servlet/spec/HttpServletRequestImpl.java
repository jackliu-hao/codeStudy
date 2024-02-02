package io.undertow.servlet.spec;

import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RequestTooBigException;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.AuthorizationManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.core.ManagedServlet;
import io.undertow.servlet.core.ServletUpgradeListener;
import io.undertow.servlet.handlers.ServletChain;
import io.undertow.servlet.handlers.ServletPathMatch;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.util.EmptyEnumeration;
import io.undertow.servlet.util.IteratorEnumeration;
import io.undertow.util.AttachmentKey;
import io.undertow.util.DateUtils;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.LocaleUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.servlet.http.PushBuilder;

public final class HttpServletRequestImpl implements HttpServletRequest {
   /** @deprecated */
   @Deprecated
   public static final AttachmentKey<Boolean> SECURE_REQUEST;
   static final AttachmentKey<Boolean> REQUESTED_SESSION_ID_SET;
   static final AttachmentKey<String> REQUESTED_SESSION_ID;
   private final HttpServerExchange exchange;
   private final ServletContextImpl originalServletContext;
   private ServletContextImpl servletContext;
   private Map<String, Object> attributes = null;
   private ServletInputStream servletInputStream;
   private BufferedReader reader;
   private Cookie[] cookies;
   private List<Part> parts = null;
   private volatile boolean asyncStarted = false;
   private volatile AsyncContextImpl asyncContext = null;
   private Map<String, Deque<String>> queryParameters;
   private FormData parsedFormData;
   private RuntimeException formParsingException;
   private Charset characterEncoding;
   private boolean readStarted;
   private SessionConfig.SessionCookieSource sessionCookieSource;

   public HttpServletRequestImpl(HttpServerExchange exchange, ServletContextImpl servletContext) {
      this.exchange = exchange;
      this.servletContext = servletContext;
      this.originalServletContext = servletContext;
   }

   public HttpServerExchange getExchange() {
      return this.exchange;
   }

   public String getAuthType() {
      SecurityContext securityContext = this.exchange.getSecurityContext();
      return securityContext != null ? securityContext.getMechanismName() : null;
   }

   public Cookie[] getCookies() {
      if (this.cookies == null) {
         Iterable<io.undertow.server.handlers.Cookie> cookies = this.exchange.requestCookies();
         int count = 0;

         for(Iterator var3 = cookies.iterator(); var3.hasNext(); ++count) {
            io.undertow.server.handlers.Cookie cookie = (io.undertow.server.handlers.Cookie)var3.next();
         }

         if (count == 0) {
            return null;
         }

         Cookie[] value = new Cookie[count];
         int i = 0;
         Iterator var5 = cookies.iterator();

         while(var5.hasNext()) {
            io.undertow.server.handlers.Cookie cookie = (io.undertow.server.handlers.Cookie)var5.next();

            try {
               Cookie c = new Cookie(cookie.getName(), cookie.getValue());
               if (cookie.getDomain() != null) {
                  c.setDomain(cookie.getDomain());
               }

               c.setHttpOnly(cookie.isHttpOnly());
               if (cookie.getMaxAge() != null) {
                  c.setMaxAge(cookie.getMaxAge());
               }

               if (cookie.getPath() != null) {
                  c.setPath(cookie.getPath());
               }

               c.setSecure(cookie.isSecure());
               c.setVersion(cookie.getVersion());
               value[i++] = c;
            } catch (IllegalArgumentException var8) {
            }
         }

         if (i < count) {
            Cookie[] shrunkCookies = new Cookie[i];
            System.arraycopy(value, 0, shrunkCookies, 0, i);
            value = shrunkCookies;
         }

         this.cookies = value;
      }

      return this.cookies;
   }

   public long getDateHeader(String name) {
      String header = this.exchange.getRequestHeaders().getFirst(name);
      if (header == null) {
         return -1L;
      } else {
         Date date = DateUtils.parseDate(header);
         if (date == null) {
            throw UndertowServletMessages.MESSAGES.headerCannotBeConvertedToDate(header);
         } else {
            return date.getTime();
         }
      }
   }

   public String getHeader(String name) {
      HeaderMap headers = this.exchange.getRequestHeaders();
      return headers.getFirst(name);
   }

   public String getHeader(HttpString name) {
      HeaderMap headers = this.exchange.getRequestHeaders();
      return headers.getFirst(name);
   }

   public Enumeration<String> getHeaders(String name) {
      List<String> headers = this.exchange.getRequestHeaders().get(name);
      return (Enumeration)(headers == null ? EmptyEnumeration.instance() : new IteratorEnumeration(headers.iterator()));
   }

   public Enumeration<String> getHeaderNames() {
      Set<String> headers = new HashSet();
      Iterator var2 = this.exchange.getRequestHeaders().getHeaderNames().iterator();

      while(var2.hasNext()) {
         HttpString i = (HttpString)var2.next();
         headers.add(i.toString());
      }

      return new IteratorEnumeration(headers.iterator());
   }

   public HttpServletMapping getHttpServletMapping() {
      ServletRequestContext src = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      ServletPathMatch match = src.getOriginalServletPathMatch();
      DispatcherType dispatcherType = this.getDispatcherType();
      if (dispatcherType == DispatcherType.FORWARD || dispatcherType == DispatcherType.ERROR) {
         match = src.getServletPathMatch();
      }

      String matchValue;
      switch (match.getMappingMatch()) {
         case EXACT:
            matchValue = match.getMatched();
            if (matchValue.startsWith("/")) {
               matchValue = matchValue.substring(1);
            }
            break;
         case DEFAULT:
         case CONTEXT_ROOT:
            matchValue = "";
            break;
         case PATH:
            matchValue = match.getRemaining();
            if (matchValue == null) {
               matchValue = "";
            } else if (matchValue.startsWith("/")) {
               matchValue = matchValue.substring(1);
            }
            break;
         case EXTENSION:
            String matched = match.getMatched();
            String matchString = match.getMatchString();
            int startIndex = matched.startsWith("/") ? 1 : 0;
            int endIndex = matched.length() - matchString.length() + 1;
            matchValue = matched.substring(startIndex, endIndex);
            break;
         default:
            matchValue = match.getRemaining();
      }

      return new MappingImpl(matchValue, match.getMatchString(), match.getMappingMatch(), match.getServletChain().getManagedServlet().getServletInfo().getName());
   }

   public int getIntHeader(String name) {
      String header = this.getHeader(name);
      return header == null ? -1 : Integer.parseInt(header);
   }

   public String getMethod() {
      return this.exchange.getRequestMethod().toString();
   }

   public String getPathInfo() {
      ServletPathMatch match = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletPathMatch();
      return match != null ? match.getRemaining() : null;
   }

   public String getPathTranslated() {
      return this.getRealPath(this.getPathInfo());
   }

   public String getContextPath() {
      return this.servletContext.getContextPath();
   }

   public String getQueryString() {
      return this.exchange.getQueryString().isEmpty() ? null : this.exchange.getQueryString();
   }

   public String getRemoteUser() {
      Principal userPrincipal = this.getUserPrincipal();
      return userPrincipal != null ? userPrincipal.getName() : null;
   }

   public boolean isUserInRole(String role) {
      if (role == null) {
         return false;
      } else if (role.equals("*")) {
         return false;
      } else {
         SecurityContext sc = this.exchange.getSecurityContext();
         Account account = sc != null ? sc.getAuthenticatedAccount() : null;
         if (account == null) {
            return false;
         } else {
            ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
            if (role.equals("**")) {
               Set<String> roles = servletRequestContext.getDeployment().getDeploymentInfo().getSecurityRoles();
               if (!roles.contains("**")) {
                  return true;
               }
            }

            ServletChain servlet = servletRequestContext.getCurrentServlet();
            Deployment deployment = this.servletContext.getDeployment();
            AuthorizationManager authorizationManager = deployment.getDeploymentInfo().getAuthorizationManager();
            return authorizationManager.isUserInRole(role, account, servlet.getManagedServlet().getServletInfo(), this, deployment);
         }
      }
   }

   public Principal getUserPrincipal() {
      SecurityContext securityContext = this.exchange.getSecurityContext();
      Principal result = null;
      Account account = null;
      if (securityContext != null && (account = securityContext.getAuthenticatedAccount()) != null) {
         result = account.getPrincipal();
      }

      return result;
   }

   public String getRequestedSessionId() {
      Boolean isRequestedSessionIdSaved = (Boolean)this.exchange.getAttachment(REQUESTED_SESSION_ID_SET);
      if (isRequestedSessionIdSaved != null && isRequestedSessionIdSaved) {
         return (String)this.exchange.getAttachment(REQUESTED_SESSION_ID);
      } else {
         SessionConfig config = this.originalServletContext.getSessionConfig();
         return config instanceof ServletContextImpl.ServletContextSessionConfig ? ((ServletContextImpl.ServletContextSessionConfig)config).getDelegate().findSessionId(this.exchange) : config.findSessionId(this.exchange);
      }
   }

   public String changeSessionId() {
      HttpSessionImpl session = this.servletContext.getSession(this.originalServletContext, this.exchange, false);
      if (session == null) {
         throw UndertowServletMessages.MESSAGES.noSession();
      } else {
         String oldId = session.getId();
         Session underlyingSession;
         if (System.getSecurityManager() == null) {
            underlyingSession = session.getSession();
         } else {
            underlyingSession = (Session)AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(session));
         }

         String newId = underlyingSession.changeSessionId(this.exchange, this.originalServletContext.getSessionConfig());
         this.servletContext.getDeployment().getApplicationListeners().httpSessionIdChanged(session, oldId);
         return newId;
      }
   }

   public String getRequestURI() {
      if (this.exchange.isHostIncludedInRequestURI()) {
         String uri = this.exchange.getRequestURI();
         int slashes = 0;

         for(int i = 0; i < uri.length(); ++i) {
            if (uri.charAt(i) == '/') {
               ++slashes;
               if (slashes == 3) {
                  return uri.substring(i);
               }
            }
         }

         return "/";
      } else {
         return this.exchange.getRequestURI();
      }
   }

   public StringBuffer getRequestURL() {
      return new StringBuffer(this.exchange.getRequestURL());
   }

   public String getServletPath() {
      ServletPathMatch match = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletPathMatch();
      return match != null ? match.getMatched() : "";
   }

   public HttpSession getSession(boolean create) {
      return this.servletContext.getSession(this.originalServletContext, this.exchange, create);
   }

   public HttpSession getSession() {
      return this.getSession(true);
   }

   public boolean isRequestedSessionIdValid() {
      HttpSessionImpl session = this.servletContext.getSession(this.originalServletContext, this.exchange, false);
      if (session == null) {
         return false;
      } else {
         return session.isInvalid() ? false : session.getId().equals(this.getRequestedSessionId());
      }
   }

   public boolean isRequestedSessionIdFromCookie() {
      return this.sessionCookieSource() == SessionConfig.SessionCookieSource.COOKIE;
   }

   public boolean isRequestedSessionIdFromURL() {
      return this.sessionCookieSource() == SessionConfig.SessionCookieSource.URL;
   }

   public boolean isRequestedSessionIdFromUrl() {
      return this.isRequestedSessionIdFromURL();
   }

   public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
      if (response.isCommitted()) {
         throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
      } else {
         SecurityContext sc = this.exchange.getSecurityContext();
         if (sc == null) {
            throw UndertowServletMessages.MESSAGES.noSecurityContextAvailable();
         } else {
            sc.setAuthenticationRequired();
            if (sc.authenticate()) {
               if (sc.isAuthenticated()) {
                  return true;
               } else {
                  throw UndertowServletMessages.MESSAGES.authenticationFailed();
               }
            } else if (!this.exchange.isResponseStarted() && this.exchange.getStatusCode() == 200) {
               throw UndertowServletMessages.MESSAGES.authenticationFailed();
            } else {
               return false;
            }
         }
      }
   }

   public void login(String username, String password) throws ServletException {
      if (username != null && password != null) {
         SecurityContext sc = this.exchange.getSecurityContext();
         if (sc == null) {
            throw UndertowServletMessages.MESSAGES.noSecurityContextAvailable();
         } else if (sc.isAuthenticated()) {
            throw UndertowServletMessages.MESSAGES.userAlreadyLoggedIn();
         } else {
            boolean login = false;

            try {
               login = sc.login(username, password);
            } catch (SecurityException var6) {
               if (var6.getCause() instanceof ServletException) {
                  throw (ServletException)var6.getCause();
               }

               throw new ServletException(var6);
            }

            if (!login) {
               throw UndertowServletMessages.MESSAGES.loginFailed();
            }
         }
      } else {
         throw UndertowServletMessages.MESSAGES.loginFailed();
      }
   }

   public void logout() throws ServletException {
      SecurityContext sc = this.exchange.getSecurityContext();
      if (sc == null) {
         throw UndertowServletMessages.MESSAGES.noSecurityContextAvailable();
      } else {
         sc.logout();
         if (this.servletContext.getDeployment().getDeploymentInfo().isInvalidateSessionOnLogout()) {
            HttpSession session = this.getSession(false);
            if (session != null) {
               session.invalidate();
            }
         }

      }
   }

   public Collection<Part> getParts() throws IOException, ServletException {
      this.verifyMultipartServlet();
      if (this.parts == null) {
         this.loadParts();
      }

      return this.parts;
   }

   private void verifyMultipartServlet() {
      ServletRequestContext src = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      MultipartConfigElement multipart = src.getServletPathMatch().getServletChain().getManagedServlet().getMultipartConfig();
      if (multipart == null) {
         throw UndertowServletMessages.MESSAGES.multipartConfigNotPresent();
      }
   }

   public Part getPart(String name) throws IOException, ServletException {
      this.verifyMultipartServlet();
      if (this.parts == null) {
         this.loadParts();
      }

      Iterator var2 = this.parts.iterator();

      Part part;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         part = (Part)var2.next();
      } while(!part.getName().equals(name));

      return part;
   }

   public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException {
      try {
         InstanceFactory<T> factory = this.servletContext.getDeployment().getDeploymentInfo().getClassIntrospecter().createInstanceFactory(handlerClass);
         InstanceHandle<T> instance = factory.createInstance();
         this.exchange.upgradeChannel(new ServletUpgradeListener(instance, this.servletContext.getDeployment(), this.exchange));
         return (HttpUpgradeHandler)instance.getInstance();
      } catch (InstantiationException var4) {
         throw new RuntimeException(var4);
      } catch (NoSuchMethodException var5) {
         throw new RuntimeException(var5);
      }
   }

   private void loadParts() throws IOException, ServletException {
      ServletRequestContext requestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (this.parts == null) {
         List<Part> parts = new ArrayList();
         String mimeType = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
         if (mimeType == null || !mimeType.startsWith("multipart/form-data")) {
            throw UndertowServletMessages.MESSAGES.notAMultiPartRequest();
         }

         FormData formData = this.parseFormData();
         if (formData != null) {
            Iterator var5 = formData.iterator();

            while(var5.hasNext()) {
               String namedPart = (String)var5.next();
               Iterator var7 = formData.get(namedPart).iterator();

               while(var7.hasNext()) {
                  FormData.FormValue part = (FormData.FormValue)var7.next();
                  parts.add(new PartImpl(namedPart, part, requestContext.getOriginalServletPathMatch().getServletChain().getManagedServlet().getMultipartConfig(), this.servletContext, this));
               }
            }
         }

         this.parts = parts;
      }

   }

   public Object getAttribute(String name) {
      return this.attributes == null ? null : this.attributes.get(name);
   }

   public Enumeration<String> getAttributeNames() {
      return (Enumeration)(this.attributes == null ? EmptyEnumeration.instance() : new IteratorEnumeration(this.attributes.keySet().iterator()));
   }

   public String getCharacterEncoding() {
      if (this.characterEncoding != null) {
         return this.characterEncoding.name();
      } else {
         String characterEncodingFromHeader = this.getCharacterEncodingFromHeader();
         if (characterEncodingFromHeader != null) {
            return characterEncodingFromHeader;
         } else if (this.servletContext.getDeployment().getDeploymentInfo().getDefaultRequestEncoding() != null) {
            return this.servletContext.getDeployment().getDeploymentInfo().getDefaultRequestEncoding();
         } else {
            return this.servletContext.getDeployment().getDeploymentInfo().getDefaultEncoding() != null ? this.servletContext.getDeployment().getDeploymentInfo().getDefaultEncoding() : null;
         }
      }
   }

   private String getCharacterEncodingFromHeader() {
      String contentType = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
      return contentType == null ? null : Headers.extractQuotedValueFromHeader(contentType, "charset");
   }

   public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
      if (!this.readStarted) {
         try {
            this.characterEncoding = Charset.forName(env);
            ManagedServlet originalServlet = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getOriginalServletPathMatch().getServletChain().getManagedServlet();
            FormDataParser parser = originalServlet.getFormParserFactory().createParser(this.exchange);
            if (parser != null) {
               parser.setCharacterEncoding(env);
            }

         } catch (UnsupportedCharsetException var4) {
            throw new UnsupportedEncodingException();
         }
      }
   }

   public int getContentLength() {
      long length = this.getContentLengthLong();
      return length > 2147483647L ? -1 : (int)length;
   }

   public long getContentLengthLong() {
      String contentLength = this.getHeader(Headers.CONTENT_LENGTH);
      return contentLength != null && !contentLength.isEmpty() ? Long.parseLong(contentLength) : -1L;
   }

   public String getContentType() {
      return this.getHeader(Headers.CONTENT_TYPE);
   }

   public ServletInputStream getInputStream() throws IOException {
      if (this.reader != null) {
         throw UndertowServletMessages.MESSAGES.getReaderAlreadyCalled();
      } else {
         if (this.servletInputStream == null) {
            this.servletInputStream = new ServletInputStreamImpl(this);
         }

         this.readStarted = true;
         return this.servletInputStream;
      }
   }

   public void closeAndDrainRequest() throws IOException {
      if (this.reader != null) {
         this.reader.close();
      }

      if (this.servletInputStream == null) {
         this.servletInputStream = new ServletInputStreamImpl(this);
      }

      this.servletInputStream.close();
   }

   public void freeResources() throws IOException {
      try {
         if (this.reader != null) {
            this.reader.close();
         }

         if (this.servletInputStream != null) {
            this.servletInputStream.close();
         }
      } finally {
         this.clearAttributes();
      }

   }

   public String getParameter(String name) {
      if (this.queryParameters == null) {
         this.queryParameters = this.exchange.getQueryParameters();
      }

      Deque<String> params = (Deque)this.queryParameters.get(name);
      if (params == null) {
         FormData parsedFormData = this.parseFormData();
         if (parsedFormData != null) {
            FormData.FormValue res = parsedFormData.getFirst(name);
            return res != null && !res.isFileItem() ? res.getValue() : null;
         } else {
            return null;
         }
      } else {
         return (String)params.getFirst();
      }
   }

   public Enumeration<String> getParameterNames() {
      if (this.queryParameters == null) {
         this.queryParameters = this.exchange.getQueryParameters();
      }

      Set<String> parameterNames = new HashSet(this.queryParameters.keySet());
      FormData parsedFormData = this.parseFormData();
      if (parsedFormData != null) {
         Iterator<String> it = parsedFormData.iterator();

         while(true) {
            while(it.hasNext()) {
               String name = (String)it.next();
               Iterator var5 = parsedFormData.get(name).iterator();

               while(var5.hasNext()) {
                  FormData.FormValue param = (FormData.FormValue)var5.next();
                  if (!param.isFileItem()) {
                     parameterNames.add(name);
                     break;
                  }
               }
            }

            return new IteratorEnumeration(parameterNames.iterator());
         }
      } else {
         return new IteratorEnumeration(parameterNames.iterator());
      }
   }

   public String[] getParameterValues(String name) {
      if (this.queryParameters == null) {
         this.queryParameters = this.exchange.getQueryParameters();
      }

      List<String> ret = new ArrayList();
      Deque<String> params = (Deque)this.queryParameters.get(name);
      if (params != null) {
         Iterator var4 = params.iterator();

         while(var4.hasNext()) {
            String param = (String)var4.next();
            ret.add(param);
         }
      }

      FormData parsedFormData = this.parseFormData();
      if (parsedFormData != null) {
         Deque<FormData.FormValue> res = parsedFormData.get(name);
         if (res != null) {
            Iterator var6 = res.iterator();

            while(var6.hasNext()) {
               FormData.FormValue value = (FormData.FormValue)var6.next();
               if (!value.isFileItem()) {
                  ret.add(value.getValue());
               }
            }
         }
      }

      return ret.isEmpty() ? null : (String[])ret.toArray(new String[ret.size()]);
   }

   public Map<String, String[]> getParameterMap() {
      if (this.queryParameters == null) {
         this.queryParameters = this.exchange.getQueryParameters();
      }

      Map<String, ArrayList<String>> arrayMap = new HashMap();
      Iterator var2 = this.queryParameters.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, Deque<String>> entry = (Map.Entry)var2.next();
         arrayMap.put(entry.getKey(), new ArrayList((Collection)entry.getValue()));
      }

      FormData parsedFormData = this.parseFormData();
      if (parsedFormData != null) {
         Iterator<String> it = parsedFormData.iterator();

         label61:
         while(true) {
            while(true) {
               if (!it.hasNext()) {
                  break label61;
               }

               String name = (String)it.next();
               Deque<FormData.FormValue> val = parsedFormData.get(name);
               ArrayList values;
               Iterator var7;
               FormData.FormValue v;
               if (arrayMap.containsKey(name)) {
                  values = (ArrayList)arrayMap.get(name);
                  var7 = val.iterator();

                  while(var7.hasNext()) {
                     v = (FormData.FormValue)var7.next();
                     if (!v.isFileItem()) {
                        values.add(v.getValue());
                     }
                  }
               } else {
                  values = new ArrayList();
                  var7 = val.iterator();

                  while(var7.hasNext()) {
                     v = (FormData.FormValue)var7.next();
                     if (!v.isFileItem()) {
                        values.add(v.getValue());
                     }
                  }

                  if (!values.isEmpty()) {
                     arrayMap.put(name, values);
                  }
               }
            }
         }
      }

      Map<String, String[]> ret = new HashMap();
      Iterator var12 = arrayMap.entrySet().iterator();

      while(var12.hasNext()) {
         Map.Entry<String, ArrayList<String>> entry = (Map.Entry)var12.next();
         ret.put(entry.getKey(), ((ArrayList)entry.getValue()).toArray(new String[((ArrayList)entry.getValue()).size()]));
      }

      return ret;
   }

   private FormData parseFormData() {
      if (this.formParsingException != null) {
         throw this.formParsingException;
      } else if (this.parsedFormData == null) {
         if (this.readStarted) {
            return null;
         } else {
            ManagedServlet originalServlet = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getCurrentServlet().getManagedServlet();
            FormDataParser parser = originalServlet.getFormParserFactory().createParser(this.exchange);
            if (parser == null) {
               return null;
            } else {
               this.readStarted = true;

               try {
                  return this.parsedFormData = parser.parseBlocking();
               } catch (MultiPartParserDefinition.FileTooLargeException | RequestTooBigException var4) {
                  throw this.formParsingException = new IllegalStateException(var4);
               } catch (RuntimeException var5) {
                  throw this.formParsingException = var5;
               } catch (IOException var6) {
                  throw this.formParsingException = new RuntimeException(var6);
               }
            }
         }
      } else {
         return this.parsedFormData;
      }
   }

   public String getProtocol() {
      return this.exchange.getProtocol().toString();
   }

   public String getScheme() {
      return this.exchange.getRequestScheme();
   }

   public String getServerName() {
      return this.exchange.getHostName();
   }

   public int getServerPort() {
      return this.exchange.getHostPort();
   }

   public BufferedReader getReader() throws IOException {
      if (this.reader == null) {
         if (this.servletInputStream != null) {
            throw UndertowServletMessages.MESSAGES.getInputStreamAlreadyCalled();
         }

         Charset charSet = null;
         if (this.characterEncoding != null) {
            charSet = this.characterEncoding;
         } else {
            String c = this.getCharacterEncoding();
            if (c != null) {
               try {
                  charSet = Charset.forName(c);
               } catch (UnsupportedCharsetException var4) {
                  throw new UnsupportedEncodingException(var4.getMessage());
               }
            }
         }

         this.reader = new BufferedReader(charSet == null ? new InputStreamReader(this.exchange.getInputStream(), StandardCharsets.ISO_8859_1) : new InputStreamReader(this.exchange.getInputStream(), charSet));
      }

      this.readStarted = true;
      return this.reader;
   }

   public String getRemoteAddr() {
      InetSocketAddress sourceAddress = this.exchange.getSourceAddress();
      if (sourceAddress == null) {
         return "";
      } else {
         InetAddress address = sourceAddress.getAddress();
         return address == null ? sourceAddress.getHostString() : address.getHostAddress();
      }
   }

   public String getRemoteHost() {
      InetSocketAddress sourceAddress = this.exchange.getSourceAddress();
      return sourceAddress == null ? "" : sourceAddress.getHostString();
   }

   public void setAttribute(String name, Object object) {
      if (object == null) {
         this.removeAttribute(name);
      } else {
         if (this.attributes == null) {
            this.attributes = new HashMap();
         }

         Object existing = this.attributes.put(name, object);
         if (existing != null) {
            this.servletContext.getDeployment().getApplicationListeners().servletRequestAttributeReplaced(this, name, existing);
         } else {
            this.servletContext.getDeployment().getApplicationListeners().servletRequestAttributeAdded(this, name, object);
         }

      }
   }

   public void removeAttribute(String name) {
      if (this.attributes != null) {
         Object exiting = this.attributes.remove(name);
         this.servletContext.getDeployment().getApplicationListeners().servletRequestAttributeRemoved(this, name, exiting);
      }
   }

   public Locale getLocale() {
      return (Locale)this.getLocales().nextElement();
   }

   public Enumeration<Locale> getLocales() {
      List<String> acceptLanguage = this.exchange.getRequestHeaders().get(Headers.ACCEPT_LANGUAGE);
      List<Locale> ret = LocaleUtils.getLocalesFromHeader((List)acceptLanguage);
      return ret.isEmpty() ? new IteratorEnumeration(Collections.singletonList(Locale.getDefault()).iterator()) : new IteratorEnumeration(ret.iterator());
   }

   public boolean isSecure() {
      return this.exchange.isSecure();
   }

   public RequestDispatcher getRequestDispatcher(String path) {
      if (path == null) {
         return null;
      } else {
         String realPath;
         if (path.startsWith("/")) {
            realPath = path;
         } else {
            String current = this.exchange.getRelativePath();
            int lastSlash = current.lastIndexOf("/");
            if (lastSlash != -1) {
               current = current.substring(0, lastSlash + 1);
            }

            realPath = current + path;
         }

         return this.servletContext.getRequestDispatcher(realPath);
      }
   }

   public String getRealPath(String path) {
      return this.servletContext.getRealPath(path);
   }

   public int getRemotePort() {
      return this.exchange.getSourceAddress().getPort();
   }

   public String getLocalName() {
      return this.exchange.getDestinationAddress().getHostName();
   }

   public String getLocalAddr() {
      InetSocketAddress destinationAddress = this.exchange.getDestinationAddress();
      if (destinationAddress == null) {
         return "";
      } else {
         InetAddress address = destinationAddress.getAddress();
         return address == null ? destinationAddress.getHostString() : address.getHostAddress();
      }
   }

   public int getLocalPort() {
      return this.exchange.getDestinationAddress().getPort();
   }

   public ServletContextImpl getServletContext() {
      return this.servletContext;
   }

   public AsyncContext startAsync() throws IllegalStateException {
      if (!this.isAsyncSupported()) {
         throw UndertowServletMessages.MESSAGES.startAsyncNotAllowed();
      } else if (this.asyncStarted) {
         throw UndertowServletMessages.MESSAGES.asyncAlreadyStarted();
      } else {
         this.asyncStarted = true;
         ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
         return this.asyncContext = new AsyncContextImpl(this.exchange, servletRequestContext.getServletRequest(), servletRequestContext.getServletResponse(), servletRequestContext, false, this.asyncContext);
      }
   }

   public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
      ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (!this.servletContext.getDeployment().getDeploymentInfo().isAllowNonStandardWrappers()) {
         if (servletRequestContext.getOriginalRequest() != servletRequest && !(servletRequest instanceof ServletRequestWrapper)) {
            throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(servletRequest);
         }

         if (servletRequestContext.getOriginalResponse() != servletResponse && !(servletResponse instanceof ServletResponseWrapper)) {
            throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(servletResponse);
         }
      }

      if (!this.isAsyncSupported()) {
         throw UndertowServletMessages.MESSAGES.startAsyncNotAllowed();
      } else if (this.asyncStarted) {
         throw UndertowServletMessages.MESSAGES.asyncAlreadyStarted();
      } else {
         this.asyncStarted = true;
         servletRequestContext.setServletRequest(servletRequest);
         servletRequestContext.setServletResponse(servletResponse);
         return this.asyncContext = new AsyncContextImpl(this.exchange, servletRequest, servletResponse, servletRequestContext, true, this.asyncContext);
      }
   }

   public boolean isAsyncStarted() {
      return this.asyncStarted;
   }

   public boolean isAsyncSupported() {
      return ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).isAsyncSupported();
   }

   public AsyncContextImpl getAsyncContext() {
      if (!this.isAsyncStarted()) {
         throw UndertowServletMessages.MESSAGES.asyncNotStarted();
      } else {
         return this.asyncContext;
      }
   }

   public AsyncContextImpl getAsyncContextInternal() {
      return this.asyncContext;
   }

   public DispatcherType getDispatcherType() {
      return ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getDispatcherType();
   }

   public Map<String, Deque<String>> getQueryParameters() {
      if (this.queryParameters == null) {
         this.queryParameters = this.exchange.getQueryParameters();
      }

      return this.queryParameters;
   }

   public void setQueryParameters(Map<String, Deque<String>> queryParameters) {
      this.queryParameters = queryParameters;
   }

   public void setServletContext(ServletContextImpl servletContext) {
      this.servletContext = servletContext;
   }

   void asyncRequestDispatched() {
      this.asyncStarted = false;
   }

   public String getOriginalRequestURI() {
      String uri = (String)this.getAttribute("javax.servlet.forward.request_uri");
      if (uri != null) {
         return uri;
      } else {
         uri = (String)this.getAttribute("javax.servlet.async.request_uri");
         return uri != null ? uri : this.getRequestURI();
      }
   }

   public String getOriginalServletPath() {
      String uri = (String)this.getAttribute("javax.servlet.forward.servlet_path");
      if (uri != null) {
         return uri;
      } else {
         uri = (String)this.getAttribute("javax.servlet.async.servlet_path");
         return uri != null ? uri : this.getServletPath();
      }
   }

   public String getOriginalPathInfo() {
      String uri = (String)this.getAttribute("javax.servlet.forward.path_info");
      if (uri != null) {
         return uri;
      } else {
         uri = (String)this.getAttribute("javax.servlet.async.path_info");
         return uri != null ? uri : this.getPathInfo();
      }
   }

   public String getOriginalContextPath() {
      String uri = (String)this.getAttribute("javax.servlet.forward.context_path");
      if (uri != null) {
         return uri;
      } else {
         uri = (String)this.getAttribute("javax.servlet.async.context_path");
         return uri != null ? uri : this.getContextPath();
      }
   }

   public String getOriginalQueryString() {
      String uri = (String)this.getAttribute("javax.servlet.forward.query_string");
      if (uri != null) {
         return uri;
      } else {
         uri = (String)this.getAttribute("javax.servlet.async.query_string");
         return uri != null ? uri : this.getQueryString();
      }
   }

   private SessionConfig.SessionCookieSource sessionCookieSource() {
      HttpSession session = this.getSession(false);
      if (session == null) {
         return SessionConfig.SessionCookieSource.NONE;
      } else {
         if (this.sessionCookieSource == null) {
            this.sessionCookieSource = this.originalServletContext.getSessionConfig().sessionCookieSource(this.exchange);
         }

         return this.sessionCookieSource;
      }
   }

   public String toString() {
      return "HttpServletRequestImpl [ " + this.getMethod() + ' ' + this.getRequestURI() + " ]";
   }

   public void clearAttributes() {
      if (this.attributes != null) {
         this.attributes.clear();
      }

   }

   public PushBuilder newPushBuilder() {
      return this.exchange.getConnection().isPushSupported() ? new PushBuilderImpl(this) : null;
   }

   public Map<String, String> getTrailerFields() {
      HeaderMap trailers = (HeaderMap)this.exchange.getAttachment(HttpAttachments.REQUEST_TRAILERS);
      if (trailers == null) {
         return Collections.emptyMap();
      } else {
         Map<String, String> ret = new HashMap();
         Iterator var3 = trailers.iterator();

         while(var3.hasNext()) {
            HeaderValues entry = (HeaderValues)var3.next();
            ret.put(entry.getHeaderName().toString().toLowerCase(Locale.ENGLISH), entry.getFirst());
         }

         return ret;
      }
   }

   public boolean isTrailerFieldsReady() {
      if (this.exchange.isRequestComplete()) {
         return true;
      } else {
         return !this.exchange.getConnection().isRequestTrailerFieldsSupported();
      }
   }

   static {
      SECURE_REQUEST = HttpServerExchange.SECURE_REQUEST;
      REQUESTED_SESSION_ID_SET = AttachmentKey.create(Boolean.class);
      REQUESTED_SESSION_ID = AttachmentKey.create(String.class);
   }
}
