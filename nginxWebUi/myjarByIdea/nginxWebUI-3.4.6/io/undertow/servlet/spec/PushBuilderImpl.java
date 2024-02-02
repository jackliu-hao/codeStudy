package io.undertow.servlet.spec;

import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.Cookie;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.servlet.http.PushBuilder;

public class PushBuilderImpl implements PushBuilder {
   private static final Set<HttpString> IGNORE;
   private static final Set<HttpString> CONDITIONAL;
   private static final Set<String> INVALID_METHOD;
   private final HttpServletRequestImpl servletRequest;
   private String method;
   private String queryString;
   private String sessionId;
   private final HeaderMap headers = new HeaderMap();
   private String path;

   public PushBuilderImpl(HttpServletRequestImpl servletRequest) {
      this.servletRequest = servletRequest;
      this.method = "GET";
      this.queryString = servletRequest.getQueryString();
      HttpSession session = servletRequest.getSession(false);
      if (session != null) {
         this.sessionId = session.getId();
      } else {
         this.sessionId = servletRequest.getRequestedSessionId();
      }

      Iterator var3 = servletRequest.getExchange().getRequestHeaders().iterator();

      while(var3.hasNext()) {
         HeaderValues header = (HeaderValues)var3.next();
         if (!IGNORE.contains(header.getHeaderName())) {
            this.headers.addAll(header.getHeaderName(), header);
         }
      }

      if (servletRequest.getQueryString() == null) {
         this.headers.add(Headers.REFERER, servletRequest.getRequestURL().toString());
      } else {
         this.headers.add(Headers.REFERER, servletRequest.getRequestURL() + "?" + servletRequest.getQueryString());
      }

      this.path = null;
      var3 = servletRequest.getExchange().responseCookies().iterator();

      while(true) {
         HeaderValues existing;
         Cookie cookie;
         label47:
         do {
            while(var3.hasNext()) {
               cookie = (Cookie)var3.next();
               if (cookie.getMaxAge() != null && cookie.getMaxAge() <= 0) {
                  existing = this.headers.get(Headers.COOKIE);
                  continue label47;
               }

               if (!cookie.getName().equals(servletRequest.getServletContext().getSessionCookieConfig().getName())) {
                  this.headers.add(Headers.COOKIE, cookie.getName() + "=" + cookie.getValue());
               }
            }

            return;
         } while(existing == null);

         Iterator<String> it = existing.iterator();

         while(it.hasNext()) {
            String val = (String)it.next();
            if (val.startsWith(cookie.getName() + "=")) {
               it.remove();
            }
         }
      }
   }

   public PushBuilder method(String method) {
      if (method == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNullNPE("method");
      } else if (INVALID_METHOD.contains(method)) {
         throw UndertowServletMessages.MESSAGES.invalidMethodForPushRequest(method);
      } else {
         this.method = method;
         return this;
      }
   }

   public PushBuilder queryString(String queryString) {
      this.queryString = queryString;
      return this;
   }

   public PushBuilder sessionId(String sessionId) {
      this.sessionId = sessionId;
      return this;
   }

   public PushBuilder setHeader(String name, String value) {
      this.headers.put(new HttpString(name), value);
      return this;
   }

   public PushBuilder addHeader(String name, String value) {
      this.headers.add(new HttpString(name), value);
      return this;
   }

   public PushBuilder removeHeader(String name) {
      this.headers.remove(name);
      return this;
   }

   public PushBuilder path(String path) {
      this.path = path;
      return this;
   }

   public void push() {
      if (this.path == null) {
         throw UndertowServletMessages.MESSAGES.pathWasNotSet();
      } else {
         ServerConnection con = this.servletRequest.getExchange().getConnection();
         if (con.isPushSupported()) {
            HeaderMap newHeaders = new HeaderMap();
            Iterator var3 = this.headers.iterator();

            while(var3.hasNext()) {
               HeaderValues entry = (HeaderValues)var3.next();
               newHeaders.addAll(entry.getHeaderName(), entry);
            }

            if (this.sessionId != null) {
               newHeaders.put(Headers.COOKIE, "JSESSIONID=" + this.sessionId);
            }

            String path = this.path;
            if (!path.startsWith("/")) {
               path = this.servletRequest.getContextPath() + "/" + path;
            }

            if (this.queryString != null && !this.queryString.isEmpty()) {
               if (path.contains("?")) {
                  path = path + "&" + this.queryString;
               } else {
                  path = path + "?" + this.queryString;
               }
            }

            con.pushResource(path, new HttpString(this.method), newHeaders);
         }

         this.path = null;
         Iterator var5 = CONDITIONAL.iterator();

         while(var5.hasNext()) {
            HttpString h = (HttpString)var5.next();
            this.headers.remove(h);
         }

      }
   }

   public String getMethod() {
      return this.method;
   }

   public String getQueryString() {
      return this.queryString;
   }

   public String getSessionId() {
      return this.sessionId;
   }

   public Set<String> getHeaderNames() {
      Set<String> names = new HashSet();
      Iterator var2 = this.headers.iterator();

      while(var2.hasNext()) {
         HeaderValues name = (HeaderValues)var2.next();
         names.add(name.getHeaderName().toString());
      }

      return names;
   }

   public String getHeader(String name) {
      return this.headers.getFirst(name);
   }

   public String getPath() {
      return this.path;
   }

   static {
      Set<HttpString> ignore = new HashSet();
      ignore.add(Headers.IF_MATCH);
      ignore.add(Headers.IF_NONE_MATCH);
      ignore.add(Headers.IF_MODIFIED_SINCE);
      ignore.add(Headers.IF_UNMODIFIED_SINCE);
      ignore.add(Headers.IF_RANGE);
      ignore.add(Headers.RANGE);
      ignore.add(Headers.ACCEPT_RANGES);
      ignore.add(Headers.EXPECT);
      ignore.add(Headers.REFERER);
      IGNORE = Collections.unmodifiableSet(ignore);
      Set<HttpString> conditional = new HashSet();
      conditional.add(Headers.IF_MATCH);
      conditional.add(Headers.IF_NONE_MATCH);
      conditional.add(Headers.IF_MODIFIED_SINCE);
      conditional.add(Headers.IF_UNMODIFIED_SINCE);
      conditional.add(Headers.IF_RANGE);
      CONDITIONAL = Collections.unmodifiableSet(conditional);
      Set<String> invalid = new HashSet();
      invalid.add("OPTIONS");
      invalid.add("PUT");
      invalid.add("POST");
      invalid.add("DELETE");
      invalid.add("CONNECT");
      invalid.add("TRACE");
      invalid.add("");
      INVALID_METHOD = Collections.unmodifiableSet(invalid);
   }
}
