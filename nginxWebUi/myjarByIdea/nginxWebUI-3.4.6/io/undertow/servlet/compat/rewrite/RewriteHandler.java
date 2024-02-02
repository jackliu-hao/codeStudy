package io.undertow.servlet.compat.rewrite;

import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.servlet.spec.HttpServletResponseImpl;
import io.undertow.util.Headers;
import io.undertow.util.QueryParameterUtils;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;

public class RewriteHandler implements HttpHandler {
   private final RewriteConfig config;
   private final HttpHandler next;
   protected ThreadLocal<Boolean> invoked = new ThreadLocal();

   public RewriteHandler(RewriteConfig config, HttpHandler next) {
      this.config = config;
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      RewriteRule[] rules = this.config.getRules();
      if (rules != null && rules.length != 0) {
         if (Boolean.TRUE.equals(this.invoked.get())) {
            this.next.handleRequest(exchange);
            this.invoked.set((Object)null);
         } else {
            ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
            HttpServletRequestImpl request = src.getOriginalRequest();
            HttpServletResponseImpl response = src.getOriginalResponse();
            UndertowResolver resolver = new UndertowResolver(src, src.getOriginalRequest());
            this.invoked.set(Boolean.TRUE);
            CharSequence url = exchange.getRelativePath();
            CharSequence host = request.getServerName();
            boolean rewritten = false;
            boolean done = false;

            String urlString;
            for(int i = 0; i < rules.length; ++i) {
               CharSequence test = rules[i].isHost() ? host : url;
               CharSequence newtest = rules[i].evaluate((CharSequence)test, resolver);
               if (newtest != null && !test.equals(newtest.toString())) {
                  if (UndertowServletLogger.REQUEST_LOGGER.isDebugEnabled()) {
                     UndertowServletLogger.REQUEST_LOGGER.debug("Rewrote " + test + " as " + newtest + " with rule pattern " + rules[i].getPatternString());
                  }

                  if (rules[i].isHost()) {
                     host = newtest;
                  } else {
                     url = newtest;
                  }

                  rewritten = true;
               }

               if (rules[i].isForbidden() && newtest != null) {
                  response.sendError(403);
                  done = true;
                  break;
               }

               if (rules[i].isGone() && newtest != null) {
                  response.sendError(410);
                  done = true;
                  break;
               }

               String queryString;
               if (rules[i].isRedirect() && newtest != null) {
                  queryString = request.getQueryString();
                  StringBuffer urlString = new StringBuffer((CharSequence)url);
                  if (queryString != null && queryString.length() > 0) {
                     int index = urlString.indexOf("?");
                     if (index != -1) {
                        if (rules[i].isQsappend()) {
                           urlString.append('&');
                           urlString.append(queryString);
                        } else if (index == urlString.length() - 1) {
                           urlString.deleteCharAt(index);
                        }
                     } else {
                        urlString.append('?');
                        urlString.append(queryString);
                     }
                  }

                  if (urlString.charAt(0) == '/' && !hasScheme(urlString)) {
                     urlString.insert(0, request.getContextPath());
                  }

                  response.sendRedirect(urlString.toString());
                  response.setStatus(rules[i].getRedirectCode());
                  done = true;
                  break;
               }

               if (rules[i].isCookie() && newtest != null) {
                  Cookie cookie = new Cookie(rules[i].getCookieName(), rules[i].getCookieResult());
                  cookie.setDomain(rules[i].getCookieDomain());
                  cookie.setMaxAge(rules[i].getCookieLifetime());
                  cookie.setPath(rules[i].getCookiePath());
                  cookie.setSecure(rules[i].isCookieSecure());
                  cookie.setHttpOnly(rules[i].isCookieHttpOnly());
                  response.addCookie(cookie);
               }

               if (rules[i].isEnv() && newtest != null) {
                  Map<String, String> attrs = (Map)exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
                  if (attrs == null) {
                     attrs = new HashMap();
                     exchange.putAttachment(HttpServerExchange.REQUEST_ATTRIBUTES, attrs);
                  }

                  for(int j = 0; j < rules[i].getEnvSize(); ++j) {
                     String envName = rules[i].getEnvName(j);
                     String envResult = rules[i].getEnvResult(j);
                     ((Map)attrs).put(envName, envResult);
                     request.setAttribute(envName, envResult);
                  }
               }

               if (rules[i].isType() && newtest != null) {
                  exchange.getRequestHeaders().put(Headers.CONTENT_TYPE, rules[i].getTypeValue());
               }

               if (rules[i].isQsappend() && newtest != null) {
                  queryString = request.getQueryString();
                  urlString = ((CharSequence)url).toString();
                  if (urlString.indexOf(63) != -1 && queryString != null) {
                     url = urlString + "&" + queryString;
                  }
               }

               if (rules[i].isChain() && newtest == null) {
                  for(int j = i; j < rules.length; ++j) {
                     if (!rules[j].isChain()) {
                        i = j;
                        break;
                     }
                  }
               } else {
                  if (rules[i].isLast() && newtest != null) {
                     break;
                  }

                  if (rules[i].isNext() && newtest != null) {
                     i = 0;
                  } else if (newtest != null) {
                     i += rules[i].getSkip();
                  }
               }
            }

            if (rewritten) {
               if (!done) {
                  String urlString = ((CharSequence)url).toString();
                  String queryString = null;
                  int queryIndex = urlString.indexOf(63);
                  if (queryIndex != -1) {
                     queryString = urlString.substring(queryIndex + 1);
                     urlString = urlString.substring(0, queryIndex);
                  }

                  StringBuilder chunk = new StringBuilder();
                  chunk.append(request.getContextPath());
                  chunk.append(urlString);
                  urlString = chunk.toString();
                  exchange.setRequestURI(urlString);
                  exchange.setRequestPath(urlString);
                  exchange.setRelativePath(urlString);
                  if (queryString != null) {
                     exchange.setQueryString(queryString);
                     exchange.getQueryParameters().clear();
                     exchange.getQueryParameters().putAll(QueryParameterUtils.parseQueryString(queryString, (String)exchange.getConnection().getUndertowOptions().get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name())));
                  }

                  if (!host.equals(request.getServerName())) {
                     exchange.getRequestHeaders().put(Headers.HOST, host + ":" + exchange.getHostPort());
                  }

                  src.getDeployment().getHandler().handleRequest(exchange);
               }
            } else {
               this.next.handleRequest(exchange);
            }

            this.invoked.set((Object)null);
         }
      } else {
         this.next.handleRequest(exchange);
      }
   }

   protected static boolean hasScheme(StringBuffer uri) {
      int len = uri.length();

      for(int i = 0; i < len; ++i) {
         char c = uri.charAt(i);
         if (c == ':') {
            return i > 0;
         }

         if (!isSchemeChar(c)) {
            return false;
         }
      }

      return false;
   }

   private static boolean isSchemeChar(char c) {
      return Character.isLetterOrDigit(c) || c == '+' || c == '-' || c == '.';
   }
}
