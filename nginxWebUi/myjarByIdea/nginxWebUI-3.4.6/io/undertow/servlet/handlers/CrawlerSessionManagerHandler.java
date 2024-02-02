package io.undertow.servlet.handlers;

import io.undertow.UndertowLogger;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.CrawlerSessionManagerConfig;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;

public class CrawlerSessionManagerHandler implements HttpHandler {
   private static final String SESSION_ATTRIBUTE_NAME = "listener_" + CrawlerSessionManagerHandler.class.getName();
   private final Map<String, String> clientIpSessionId = new ConcurrentHashMap();
   private final Map<String, String> sessionIdClientIp = new ConcurrentHashMap();
   private final CrawlerSessionManagerConfig config;
   private final Pattern uaPattern;
   private final HttpHandler next;

   public CrawlerSessionManagerHandler(CrawlerSessionManagerConfig config, HttpHandler next) {
      this.config = config;
      this.next = next;
      this.uaPattern = Pattern.compile(config.getCrawlerUserAgents());
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      boolean isBot = false;
      final String sessionId = null;
      final String clientIp = null;
      ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (src.getOriginalRequest().getSession(false) == null) {
         HeaderValues userAgentHeaders = exchange.getRequestHeaders().get(Headers.USER_AGENT);
         if (userAgentHeaders != null) {
            Iterator<String> uaHeaders = userAgentHeaders.iterator();
            String uaHeader = null;
            if (uaHeaders.hasNext()) {
               uaHeader = (String)uaHeaders.next();
            }

            if (uaHeader != null && !uaHeaders.hasNext() && this.uaPattern.matcher(uaHeader).matches()) {
               isBot = true;
               if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
                  UndertowLogger.REQUEST_LOGGER.debug(exchange + ": Bot found. UserAgent=" + uaHeader);
               }
            }

            if (isBot) {
               clientIp = src.getServletRequest().getRemoteAddr();
               sessionId = (String)this.clientIpSessionId.get(clientIp);
               if (sessionId != null) {
                  src.setOverridenSessionId(sessionId);
                  if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
                     UndertowLogger.REQUEST_LOGGER.debug(exchange + ": SessionID=" + sessionId);
                  }
               }
            }
         }
      }

      if (isBot) {
         exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
            public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
               try {
                  ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
                  if (sessionId == null) {
                     HttpSession s = src.getOriginalRequest().getSession(false);
                     if (s != null) {
                        CrawlerSessionManagerHandler.this.clientIpSessionId.put(clientIp, s.getId());
                        CrawlerSessionManagerHandler.this.sessionIdClientIp.put(s.getId(), clientIp);
                        s.setAttribute(CrawlerSessionManagerHandler.SESSION_ATTRIBUTE_NAME, new CrawlerBindingListener(CrawlerSessionManagerHandler.this.clientIpSessionId, CrawlerSessionManagerHandler.this.sessionIdClientIp));
                        s.setMaxInactiveInterval(CrawlerSessionManagerHandler.this.config.getSessionInactiveInterval());
                        if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
                           UndertowLogger.REQUEST_LOGGER.debug(exchange + ": New bot session. SessionID=" + s.getId());
                        }
                     }
                  } else if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
                     UndertowLogger.REQUEST_LOGGER.debug(exchange + ": Bot session accessed. SessionID=" + sessionId);
                  }
               } finally {
                  nextListener.proceed();
               }

            }
         });
      }

      this.next.handleRequest(exchange);
   }
}
