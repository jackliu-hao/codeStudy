package io.undertow.server.handlers;

import io.undertow.UndertowMessages;
import io.undertow.attribute.ExchangeAttribute;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AccessControlListHandler implements HttpHandler {
   private volatile HttpHandler next;
   private volatile boolean defaultAllow = false;
   private final ExchangeAttribute attribute;
   private final List<AclMatch> acl = new CopyOnWriteArrayList();

   public AccessControlListHandler(HttpHandler next, ExchangeAttribute attribute) {
      this.next = next;
      this.attribute = attribute;
   }

   public AccessControlListHandler(ExchangeAttribute attribute) {
      this.attribute = attribute;
      this.next = ResponseCodeHandler.HANDLE_404;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      String attribute = this.attribute.readAttribute(exchange);
      if (this.isAllowed(attribute)) {
         this.next.handleRequest(exchange);
      } else {
         exchange.setStatusCode(403);
         exchange.endExchange();
      }

   }

   boolean isAllowed(String attribute) {
      if (attribute != null) {
         Iterator var2 = this.acl.iterator();

         while(var2.hasNext()) {
            AclMatch rule = (AclMatch)var2.next();
            if (rule.matches(attribute)) {
               return !rule.isDeny();
            }
         }
      }

      return this.defaultAllow;
   }

   public boolean isDefaultAllow() {
      return this.defaultAllow;
   }

   public AccessControlListHandler setDefaultAllow(boolean defaultAllow) {
      this.defaultAllow = defaultAllow;
      return this;
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public AccessControlListHandler setNext(HttpHandler next) {
      this.next = next;
      return this;
   }

   public AccessControlListHandler addAllow(String pattern) {
      return this.addRule(pattern, false);
   }

   public AccessControlListHandler addDeny(String pattern) {
      return this.addRule(pattern, true);
   }

   public AccessControlListHandler clearRules() {
      this.acl.clear();
      return this;
   }

   private AccessControlListHandler addRule(String userAgent, boolean deny) {
      this.acl.add(new AclMatch(deny, userAgent));
      return this;
   }

   private static class Wrapper implements HandlerWrapper {
      private final List<AclMatch> peerMatches;
      private final boolean defaultAllow;
      private final ExchangeAttribute attribute;

      private Wrapper(List<AclMatch> peerMatches, boolean defaultAllow, ExchangeAttribute attribute) {
         this.peerMatches = peerMatches;
         this.defaultAllow = defaultAllow;
         this.attribute = attribute;
      }

      public HttpHandler wrap(HttpHandler handler) {
         AccessControlListHandler res = new AccessControlListHandler(handler, this.attribute);
         Iterator var3 = this.peerMatches.iterator();

         while(var3.hasNext()) {
            AclMatch match = (AclMatch)var3.next();
            if (match.deny) {
               res.addDeny(match.pattern.pattern());
            } else {
               res.addAllow(match.pattern.pattern());
            }
         }

         res.setDefaultAllow(this.defaultAllow);
         return res;
      }

      // $FF: synthetic method
      Wrapper(List x0, boolean x1, ExchangeAttribute x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "access-control";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("acl", String[].class);
         params.put("default-allow", Boolean.TYPE);
         params.put("attribute", ExchangeAttribute.class);
         return params;
      }

      public Set<String> requiredParameters() {
         HashSet<String> ret = new HashSet();
         ret.add("acl");
         ret.add("attribute");
         return ret;
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         String[] acl = (String[])((String[])config.get("acl"));
         Boolean defaultAllow = (Boolean)config.get("default-allow");
         ExchangeAttribute attribute = (ExchangeAttribute)config.get("attribute");
         List<AclMatch> peerMatches = new ArrayList();
         String[] var6 = acl;
         int var7 = acl.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String rule = var6[var8];
            String[] parts = rule.split(" ");
            if (parts.length != 2) {
               throw UndertowMessages.MESSAGES.invalidAclRule(rule);
            }

            if (parts[1].trim().equals("allow")) {
               peerMatches.add(new AclMatch(false, parts[0].trim()));
            } else {
               if (!parts[1].trim().equals("deny")) {
                  throw UndertowMessages.MESSAGES.invalidAclRule(rule);
               }

               peerMatches.add(new AclMatch(true, parts[0].trim()));
            }
         }

         return new Wrapper(peerMatches, defaultAllow == null ? false : defaultAllow, attribute);
      }
   }

   static class AclMatch {
      private final boolean deny;
      private final Pattern pattern;

      protected AclMatch(boolean deny, String pattern) {
         this.deny = deny;
         this.pattern = this.createPattern(pattern);
      }

      private Pattern createPattern(String pattern) {
         try {
            return Pattern.compile(pattern);
         } catch (PatternSyntaxException var3) {
            throw UndertowMessages.MESSAGES.notAValidRegularExpressionPattern(pattern);
         }
      }

      boolean matches(String attribute) {
         return this.pattern.matcher(attribute).matches();
      }

      boolean isDeny() {
         return this.deny;
      }

      public String toString() {
         return this.getClass().getSimpleName() + "{deny=" + this.deny + ", pattern='" + this.pattern + '\'' + '}';
      }
   }
}
