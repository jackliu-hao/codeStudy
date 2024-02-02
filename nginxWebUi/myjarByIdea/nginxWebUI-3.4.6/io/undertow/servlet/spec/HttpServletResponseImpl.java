package io.undertow.servlet.spec;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.util.CanonicalPathUtils;
import io.undertow.util.DateUtils;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Protocols;
import io.undertow.util.RedirectBuilder;
import io.undertow.util.StatusCodes;
import io.undertow.util.URLUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class HttpServletResponseImpl implements HttpServletResponse {
   private final HttpServerExchange exchange;
   private final ServletContextImpl originalServletContext;
   private volatile ServletContextImpl servletContext;
   private ServletOutputStreamImpl servletOutputStream;
   private ResponseState responseState;
   private PrintWriter writer;
   private Integer bufferSize;
   private long contentLength;
   private boolean insideInclude;
   private Locale locale;
   private boolean responseDone;
   private boolean ignoredFlushPerformed;
   private boolean treatAsCommitted;
   private boolean charsetSet;
   private String contentType;
   private String charset;
   private Supplier<Map<String, String>> trailerSupplier;

   public HttpServletResponseImpl(HttpServerExchange exchange, ServletContextImpl servletContext) {
      this.responseState = HttpServletResponseImpl.ResponseState.NONE;
      this.contentLength = -1L;
      this.insideInclude = false;
      this.responseDone = false;
      this.ignoredFlushPerformed = false;
      this.treatAsCommitted = false;
      this.charsetSet = false;
      this.exchange = exchange;
      this.servletContext = servletContext;
      this.originalServletContext = servletContext;
   }

   public HttpServerExchange getExchange() {
      return this.exchange;
   }

   public void addCookie(Cookie newCookie) {
      if (!this.insideInclude) {
         ServletCookieAdaptor servletCookieAdaptor = new ServletCookieAdaptor(newCookie);
         if (newCookie.getVersion() == 0) {
            servletCookieAdaptor.setVersion(this.servletContext.getDeployment().getDeploymentInfo().getDefaultCookieVersion());
         }

         this.exchange.setResponseCookie(servletCookieAdaptor);
      }
   }

   public boolean containsHeader(String name) {
      return this.exchange.getResponseHeaders().contains(name);
   }

   public String encodeUrl(String url) {
      return this.encodeURL(url);
   }

   public String encodeRedirectUrl(String url) {
      return this.encodeRedirectURL(url);
   }

   public void sendError(int sc, String msg) throws IOException {
      if (!this.insideInclude) {
         ServletRequestContext src = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
         if (this.responseStarted()) {
            if (src.getErrorCode() <= 0) {
               throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
            }
         } else {
            if (this.servletContext.getDeployment().getDeploymentInfo().isSendCustomReasonPhraseOnError()) {
               this.exchange.setReasonPhrase(msg);
            }

            this.writer = null;
            this.responseState = HttpServletResponseImpl.ResponseState.NONE;
            this.exchange.setStatusCode(sc);
            if (src.isRunningInsideHandler()) {
               this.treatAsCommitted = true;
               src.setError(sc, msg);
            } else {
               this.doErrorDispatch(sc, msg);
            }

         }
      }
   }

   public void doErrorDispatch(int sc, String error) throws IOException {
      this.writer = null;
      this.responseState = HttpServletResponseImpl.ResponseState.NONE;
      this.resetBuffer();
      this.treatAsCommitted = false;
      String location = this.servletContext.getDeployment().getErrorPages().getErrorLocation(sc);
      if (location != null) {
         RequestDispatcherImpl requestDispatcher = new RequestDispatcherImpl(location, this.servletContext);
         ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);

         try {
            requestDispatcher.error(servletRequestContext, servletRequestContext.getServletRequest(), servletRequestContext.getServletResponse(), ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getCurrentServlet().getManagedServlet().getServletInfo().getName(), error);
         } catch (ServletException var7) {
            throw new RuntimeException(var7);
         }
      } else if (error != null) {
         this.setContentType("text/html");
         this.setCharacterEncoding("UTF-8");
         if (this.servletContext.getDeployment().getDeploymentInfo().isEscapeErrorMessage()) {
            this.getWriter().write("<html><head><title>Error</title></head><body>" + escapeHtml(error) + "</body></html>");
         } else {
            this.getWriter().write("<html><head><title>Error</title></head><body>" + error + "</body></html>");
         }

         this.getWriter().close();
      }

      this.responseDone();
   }

   public void sendError(int sc) throws IOException {
      this.sendError(sc, StatusCodes.getReason(sc));
   }

   public void sendRedirect(String location) throws IOException {
      if (this.responseStarted()) {
         throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
      } else {
         this.resetBuffer();
         this.setStatus(302);
         if (URLUtils.isAbsoluteUrl(location)) {
            this.exchange.getResponseHeaders().put(Headers.LOCATION, location);
         } else {
            String realPath;
            String current;
            if (location.startsWith("/")) {
               realPath = location;
            } else {
               current = this.exchange.getRequestURI().substring(this.getServletContext().getContextPath().length());
               int lastSlash = current.lastIndexOf("/");
               if (lastSlash != -1) {
                  current = current.substring(0, lastSlash + 1);
               }

               realPath = CanonicalPathUtils.canonicalize(this.servletContext.getContextPath() + current + location);
            }

            current = this.exchange.getRequestScheme() + "://" + this.exchange.getHostAndPort() + realPath;
            this.exchange.getResponseHeaders().put(Headers.LOCATION, current);
         }

         this.responseDone();
      }
   }

   public void setDateHeader(String name, long date) {
      this.setHeader(name, DateUtils.toDateString(new Date(date)));
   }

   public void addDateHeader(String name, long date) {
      this.addHeader(name, DateUtils.toDateString(new Date(date)));
   }

   public void setHeader(String name, String value) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.headerNameWasNull();
      } else {
         this.setHeader(HttpString.tryFromString(name), value);
      }
   }

   public void setHeader(HttpString name, String value) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.headerNameWasNull();
      } else if (!this.insideInclude && !this.ignoredFlushPerformed) {
         if (name.equals(Headers.CONTENT_TYPE)) {
            this.setContentType(value);
         } else {
            this.exchange.getResponseHeaders().put(name, value);
         }

      }
   }

   public void addHeader(String name, String value) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.headerNameWasNull();
      } else {
         this.addHeader(HttpString.tryFromString(name), value);
      }
   }

   public void addHeader(HttpString name, String value) {
      if (name == null) {
         throw UndertowServletMessages.MESSAGES.headerNameWasNull();
      } else if (!this.insideInclude && !this.ignoredFlushPerformed && !this.treatAsCommitted) {
         if (name.equals(Headers.CONTENT_TYPE) && !this.exchange.getResponseHeaders().contains(Headers.CONTENT_TYPE)) {
            this.setContentType(value);
         } else {
            this.exchange.getResponseHeaders().add(name, value);
         }

      }
   }

   public void setIntHeader(String name, int value) {
      this.setHeader(name, Integer.toString(value));
   }

   public void addIntHeader(String name, int value) {
      this.addHeader(name, Integer.toString(value));
   }

   public void setStatus(int sc) {
      if (!this.insideInclude && !this.treatAsCommitted) {
         if (!this.responseStarted()) {
            this.exchange.setStatusCode(sc);
         }
      }
   }

   public void setStatus(int sc, String sm) {
      this.setStatus(sc);
      if (!this.insideInclude && this.servletContext.getDeployment().getDeploymentInfo().isSendCustomReasonPhraseOnError()) {
         this.exchange.setReasonPhrase(sm);
      }

   }

   public int getStatus() {
      return this.exchange.getStatusCode();
   }

   public String getHeader(String name) {
      return this.exchange.getResponseHeaders().getFirst(name);
   }

   public Collection<String> getHeaders(String name) {
      HeaderValues headers = this.exchange.getResponseHeaders().get(name);
      return (Collection)(headers == null ? Collections.emptySet() : new ArrayList(headers));
   }

   public Collection<String> getHeaderNames() {
      Set<String> headers = new HashSet();
      Iterator var2 = this.exchange.getResponseHeaders().getHeaderNames().iterator();

      while(var2.hasNext()) {
         HttpString i = (HttpString)var2.next();
         headers.add(i.toString());
      }

      return headers;
   }

   public String getCharacterEncoding() {
      if (this.charset != null) {
         return this.charset;
      } else if (this.servletContext.getDeployment().getDeploymentInfo().getDefaultResponseEncoding() != null) {
         return this.servletContext.getDeployment().getDeploymentInfo().getDefaultResponseEncoding();
      } else {
         return this.servletContext.getDeployment().getDeploymentInfo().getDefaultEncoding() != null ? this.servletContext.getDeployment().getDeploymentInfo().getDefaultEncoding() : StandardCharsets.ISO_8859_1.name();
      }
   }

   public String getContentType() {
      if (this.contentType != null) {
         return this.charsetSet ? this.contentType + ";charset=" + this.getCharacterEncoding() : this.contentType;
      } else {
         return null;
      }
   }

   public ServletOutputStream getOutputStream() {
      if (this.responseState == HttpServletResponseImpl.ResponseState.WRITER) {
         throw UndertowServletMessages.MESSAGES.getWriterAlreadyCalled();
      } else {
         this.responseState = HttpServletResponseImpl.ResponseState.STREAM;
         this.createOutputStream();
         return this.servletOutputStream;
      }
   }

   public PrintWriter getWriter() throws IOException {
      if (this.writer == null) {
         if (!this.charsetSet) {
            this.setCharacterEncoding(this.getCharacterEncoding());
         }

         if (this.responseState == HttpServletResponseImpl.ResponseState.STREAM) {
            throw UndertowServletMessages.MESSAGES.getOutputStreamAlreadyCalled();
         }

         this.responseState = HttpServletResponseImpl.ResponseState.WRITER;
         this.createOutputStream();
         ServletPrintWriter servletPrintWriter = new ServletPrintWriter(this.servletOutputStream, this.getCharacterEncoding());
         this.writer = ServletPrintWriterDelegate.newInstance(servletPrintWriter);
      }

      return this.writer;
   }

   private void createOutputStream() {
      if (this.servletOutputStream == null) {
         if (this.bufferSize == null) {
            this.servletOutputStream = new ServletOutputStreamImpl((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY));
         } else {
            this.servletOutputStream = new ServletOutputStreamImpl((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY), this.bufferSize);
         }
      }

   }

   public void setCharacterEncoding(String charset) {
      if (!this.insideInclude && !this.responseStarted() && this.writer == null && !this.isCommitted()) {
         this.charsetSet = charset != null;
         this.charset = charset;
         if (this.contentType != null) {
            this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, this.getContentType());
         }

      }
   }

   public void setContentLength(int len) {
      this.setContentLengthLong((long)len);
   }

   public void setContentLengthLong(long len) {
      if (!this.insideInclude && !this.responseStarted()) {
         if (len >= 0L) {
            this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, Long.toString(len));
         } else {
            this.exchange.getResponseHeaders().remove(Headers.CONTENT_LENGTH);
         }

         this.contentLength = len;
      }
   }

   boolean isIgnoredFlushPerformed() {
      return this.ignoredFlushPerformed;
   }

   void setIgnoredFlushPerformed(boolean ignoredFlushPerformed) {
      this.ignoredFlushPerformed = ignoredFlushPerformed;
   }

   private boolean responseStarted() {
      return this.exchange.isResponseStarted() || this.ignoredFlushPerformed || this.treatAsCommitted;
   }

   public void setContentType(String type) {
      if (type != null && !this.insideInclude && !this.responseStarted()) {
         ContentTypeInfo ct = this.servletContext.parseContentType(type);
         this.contentType = ct.getContentType();
         boolean useCharset = false;
         if (ct.getCharset() != null && this.writer == null && !this.isCommitted()) {
            this.charset = ct.getCharset();
            this.charsetSet = true;
            useCharset = true;
         }

         if (!useCharset && this.charsetSet) {
            if (ct.getCharset() == null) {
               this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ct.getHeader() + "; charset=" + this.charset);
            } else {
               this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ct.getContentType() + "; charset=" + this.charset);
            }
         } else {
            this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ct.getHeader());
         }

      }
   }

   public void setBufferSize(int size) {
      if (this.servletOutputStream != null) {
         this.servletOutputStream.setBufferSize(size);
      }

      this.bufferSize = size;
   }

   public int getBufferSize() {
      return this.bufferSize == null ? this.exchange.getConnection().getBufferSize() : this.bufferSize;
   }

   public void flushBuffer() throws IOException {
      if (this.writer != null) {
         this.writer.flush();
      } else if (this.servletOutputStream != null) {
         this.servletOutputStream.flush();
      } else {
         this.createOutputStream();
         this.servletOutputStream.flush();
      }

   }

   public void closeStreamAndWriter() throws IOException {
      if (!this.treatAsCommitted) {
         if (this.writer != null) {
            this.writer.close();
         } else {
            if (this.servletOutputStream == null) {
               this.createOutputStream();
            }

            this.servletOutputStream.close();
         }

      }
   }

   public void freeResources() throws IOException {
      if (this.writer != null) {
         this.writer.close();
      }

      if (this.servletOutputStream != null) {
         this.servletOutputStream.close();
      }

   }

   public void resetBuffer() {
      if (this.servletOutputStream != null) {
         this.servletOutputStream.resetBuffer();
      }

      if (this.writer != null) {
         try {
            ServletPrintWriter servletPrintWriter = new ServletPrintWriter(this.servletOutputStream, this.getCharacterEncoding());
            this.writer = ServletPrintWriterDelegate.newInstance(servletPrintWriter);
         } catch (UnsupportedEncodingException var3) {
            throw new RuntimeException(var3);
         }
      }

   }

   public boolean isCommitted() {
      return this.responseStarted();
   }

   public void reset() {
      if (this.servletOutputStream != null) {
         this.servletOutputStream.resetBuffer();
      }

      this.writer = null;
      this.responseState = HttpServletResponseImpl.ResponseState.NONE;
      this.exchange.getResponseHeaders().clear();
      this.exchange.setStatusCode(200);
      this.treatAsCommitted = false;
   }

   public void setLocale(Locale loc) {
      if (!this.insideInclude && !this.responseStarted()) {
         this.locale = loc;
         this.exchange.getResponseHeaders().put(Headers.CONTENT_LANGUAGE, loc.getLanguage() + "-" + loc.getCountry());
         if (!this.charsetSet && this.writer == null) {
            Map<String, String> localeCharsetMapping = this.servletContext.getDeployment().getDeploymentInfo().getLocaleCharsetMapping();
            String charset = (String)localeCharsetMapping.get(this.locale.toString());
            if (charset == null) {
               charset = (String)localeCharsetMapping.get(this.locale.getLanguage() + "_" + this.locale.getCountry());
               if (charset == null) {
                  charset = (String)localeCharsetMapping.get(this.locale.getLanguage());
               }
            }

            if (charset != null) {
               this.charset = charset;
               if (this.contentType != null) {
                  this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, this.getContentType());
               }
            }
         }

      }
   }

   public Locale getLocale() {
      return this.locale != null ? this.locale : Locale.getDefault();
   }

   public void responseDone() {
      if (!this.responseDone && !this.treatAsCommitted) {
         this.responseDone = true;

         try {
            this.closeStreamAndWriter();
         } catch (IOException var5) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(var5);
         } finally {
            this.servletContext.updateSessionAccessTime(this.exchange);
         }

      }
   }

   public boolean isInsideInclude() {
      return this.insideInclude;
   }

   public void setInsideInclude(boolean insideInclude) {
      this.insideInclude = insideInclude;
   }

   public void setServletContext(ServletContextImpl servletContext) {
      this.servletContext = servletContext;
   }

   public ServletContextImpl getServletContext() {
      return this.servletContext;
   }

   public String encodeURL(String url) {
      String absolute = this.toAbsolute(url);
      if (this.isEncodeable(absolute)) {
         if (url.equalsIgnoreCase("")) {
            url = absolute;
         }

         return this.originalServletContext.getSessionConfig().rewriteUrl(url, this.servletContext.getSession(this.originalServletContext, this.exchange, true).getId());
      } else {
         return url;
      }
   }

   public String encodeRedirectURL(String url) {
      return this.isEncodeable(this.toAbsolute(url)) ? this.originalServletContext.getSessionConfig().rewriteUrl(url, this.servletContext.getSession(this.originalServletContext, this.exchange, true).getId()) : url;
   }

   private String toAbsolute(String location) {
      if (location == null) {
         return location;
      } else {
         boolean leadingSlash = location.startsWith("/");
         return !leadingSlash && this.hasScheme(location) ? location : RedirectBuilder.redirect(this.exchange, location, false);
      }
   }

   private boolean hasScheme(String uri) {
      int len = uri.length();

      for(int i = 0; i < len; ++i) {
         char c = uri.charAt(i);
         if (c == ':') {
            return i > 0;
         }

         if (!Character.isLetterOrDigit(c) && c != '+' && c != '-' && c != '.') {
            return false;
         }
      }

      return false;
   }

   private boolean isEncodeable(String location) {
      if (location == null) {
         return false;
      } else if (location.startsWith("#")) {
         return false;
      } else {
         HttpServletRequestImpl hreq = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getOriginalRequest();
         if (!this.originalServletContext.getEffectiveSessionTrackingModes().contains(SessionTrackingMode.URL)) {
            return false;
         } else {
            HttpSession session = hreq.getSession(false);
            if (session == null) {
               return false;
            } else if (hreq.isRequestedSessionIdFromCookie()) {
               return false;
            } else {
               return !hreq.isRequestedSessionIdFromURL() && !session.isNew() ? false : this.doIsEncodeable(hreq, session, location);
            }
         }
      }
   }

   private boolean doIsEncodeable(HttpServletRequestImpl hreq, HttpSession session, String location) {
      URL url = null;

      try {
         url = new URL(location);
      } catch (MalformedURLException var9) {
         return false;
      }

      if (!hreq.getScheme().equalsIgnoreCase(url.getProtocol())) {
         return false;
      } else if (!hreq.getServerName().equalsIgnoreCase(url.getHost())) {
         return false;
      } else {
         int serverPort = hreq.getServerPort();
         if (serverPort == -1) {
            if ("https".equals(hreq.getScheme())) {
               serverPort = 443;
            } else {
               serverPort = 80;
            }
         }

         int urlPort = url.getPort();
         if (urlPort == -1) {
            if ("https".equals(url.getProtocol())) {
               urlPort = 443;
            } else {
               urlPort = 80;
            }
         }

         if (serverPort != urlPort) {
            return false;
         } else {
            String file = url.getFile();
            if (file == null) {
               return false;
            } else {
               String tok = this.originalServletContext.getSessionCookieConfig().getName().toLowerCase() + "=" + session.getId();
               return !file.contains(tok);
            }
         }
      }
   }

   public long getContentLength() {
      return this.contentLength;
   }

   private static String escapeHtml(String msg) {
      return msg.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
   }

   public boolean isTreatAsCommitted() {
      return this.treatAsCommitted;
   }

   public void setTrailerFields(Supplier<Map<String, String>> supplier) {
      if (this.exchange.isResponseStarted()) {
         throw UndertowServletMessages.MESSAGES.responseAlreadyCommited();
      } else if (this.exchange.getProtocol() == Protocols.HTTP_1_0) {
         throw UndertowServletMessages.MESSAGES.trailersNotSupported("HTTP/1.0 request");
      } else if (this.exchange.getProtocol() == Protocols.HTTP_1_1 && this.exchange.getResponseHeaders().contains(Headers.CONTENT_LENGTH)) {
         throw UndertowServletMessages.MESSAGES.trailersNotSupported("not chunked");
      } else {
         this.trailerSupplier = supplier;
         this.exchange.putAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER, () -> {
            HeaderMap trailers = new HeaderMap();
            Map<String, String> map = (Map)supplier.get();
            Iterator var3 = map.entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry<String, String> e = (Map.Entry)var3.next();
               trailers.put(new HttpString((String)e.getKey()), (String)e.getValue());
            }

            return trailers;
         });
      }
   }

   public Supplier<Map<String, String>> getTrailerFields() {
      return this.trailerSupplier;
   }

   public static enum ResponseState {
      NONE,
      STREAM,
      WRITER;
   }
}
