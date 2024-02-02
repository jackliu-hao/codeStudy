/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.regex.PatternSyntaxException;
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
/*     */ 
/*     */ public class AccessControlListHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private volatile HttpHandler next;
/*     */   private volatile boolean defaultAllow = false;
/*     */   private final ExchangeAttribute attribute;
/*  51 */   private final List<AclMatch> acl = new CopyOnWriteArrayList<>();
/*     */   
/*     */   public AccessControlListHandler(HttpHandler next, ExchangeAttribute attribute) {
/*  54 */     this.next = next;
/*  55 */     this.attribute = attribute;
/*     */   }
/*     */   
/*     */   public AccessControlListHandler(ExchangeAttribute attribute) {
/*  59 */     this.attribute = attribute;
/*  60 */     this.next = ResponseCodeHandler.HANDLE_404;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  65 */     String attribute = this.attribute.readAttribute(exchange);
/*  66 */     if (isAllowed(attribute)) {
/*  67 */       this.next.handleRequest(exchange);
/*     */     } else {
/*  69 */       exchange.setStatusCode(403);
/*  70 */       exchange.endExchange();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isAllowed(String attribute) {
/*  76 */     if (attribute != null) {
/*  77 */       for (AclMatch rule : this.acl) {
/*  78 */         if (rule.matches(attribute)) {
/*  79 */           return !rule.isDeny();
/*     */         }
/*     */       } 
/*     */     }
/*  83 */     return this.defaultAllow;
/*     */   }
/*     */   
/*     */   public boolean isDefaultAllow() {
/*  87 */     return this.defaultAllow;
/*     */   }
/*     */   
/*     */   public AccessControlListHandler setDefaultAllow(boolean defaultAllow) {
/*  91 */     this.defaultAllow = defaultAllow;
/*  92 */     return this;
/*     */   }
/*     */   
/*     */   public HttpHandler getNext() {
/*  96 */     return this.next;
/*     */   }
/*     */   
/*     */   public AccessControlListHandler setNext(HttpHandler next) {
/* 100 */     this.next = next;
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AccessControlListHandler addAllow(String pattern) {
/* 112 */     return addRule(pattern, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AccessControlListHandler addDeny(String pattern) {
/* 123 */     return addRule(pattern, true);
/*     */   }
/*     */   
/*     */   public AccessControlListHandler clearRules() {
/* 127 */     this.acl.clear();
/* 128 */     return this;
/*     */   }
/*     */   
/*     */   private AccessControlListHandler addRule(String userAgent, boolean deny) {
/* 132 */     this.acl.add(new AclMatch(deny, userAgent));
/* 133 */     return this;
/*     */   }
/*     */   
/*     */   static class AclMatch
/*     */   {
/*     */     private final boolean deny;
/*     */     private final Pattern pattern;
/*     */     
/*     */     protected AclMatch(boolean deny, String pattern) {
/* 142 */       this.deny = deny;
/* 143 */       this.pattern = createPattern(pattern);
/*     */     }
/*     */     
/*     */     private Pattern createPattern(String pattern) {
/*     */       try {
/* 148 */         return Pattern.compile(pattern);
/* 149 */       } catch (PatternSyntaxException e) {
/* 150 */         throw UndertowMessages.MESSAGES.notAValidRegularExpressionPattern(pattern);
/*     */       } 
/*     */     }
/*     */     
/*     */     boolean matches(String attribute) {
/* 155 */       return this.pattern.matcher(attribute).matches();
/*     */     }
/*     */     
/*     */     boolean isDeny() {
/* 159 */       return this.deny;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 164 */       return getClass().getSimpleName() + "{deny=" + this.deny + ", pattern='" + this.pattern + '\'' + '}';
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 176 */       return "access-control";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 181 */       Map<String, Class<?>> params = new HashMap<>();
/* 182 */       params.put("acl", String[].class);
/* 183 */       params.put("default-allow", boolean.class);
/* 184 */       params.put("attribute", ExchangeAttribute.class);
/* 185 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 190 */       HashSet<String> ret = new HashSet<>();
/* 191 */       ret.add("acl");
/* 192 */       ret.add("attribute");
/* 193 */       return ret;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 198 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 204 */       String[] acl = (String[])config.get("acl");
/* 205 */       Boolean defaultAllow = (Boolean)config.get("default-allow");
/* 206 */       ExchangeAttribute attribute = (ExchangeAttribute)config.get("attribute");
/*     */       
/* 208 */       List<AccessControlListHandler.AclMatch> peerMatches = new ArrayList<>();
/* 209 */       for (String rule : acl) {
/* 210 */         String[] parts = rule.split(" ");
/* 211 */         if (parts.length != 2) {
/* 212 */           throw UndertowMessages.MESSAGES.invalidAclRule(rule);
/*     */         }
/* 214 */         if (parts[1].trim().equals("allow")) {
/* 215 */           peerMatches.add(new AccessControlListHandler.AclMatch(false, parts[0].trim()));
/* 216 */         } else if (parts[1].trim().equals("deny")) {
/* 217 */           peerMatches.add(new AccessControlListHandler.AclMatch(true, parts[0].trim()));
/*     */         } else {
/* 219 */           throw UndertowMessages.MESSAGES.invalidAclRule(rule);
/*     */         } 
/*     */       } 
/* 222 */       return new AccessControlListHandler.Wrapper(peerMatches, (defaultAllow == null) ? false : defaultAllow.booleanValue(), attribute);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final List<AccessControlListHandler.AclMatch> peerMatches;
/*     */     private final boolean defaultAllow;
/*     */     private final ExchangeAttribute attribute;
/*     */     
/*     */     private Wrapper(List<AccessControlListHandler.AclMatch> peerMatches, boolean defaultAllow, ExchangeAttribute attribute) {
/* 235 */       this.peerMatches = peerMatches;
/* 236 */       this.defaultAllow = defaultAllow;
/* 237 */       this.attribute = attribute;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 243 */       AccessControlListHandler res = new AccessControlListHandler(handler, this.attribute);
/* 244 */       for (AccessControlListHandler.AclMatch match : this.peerMatches) {
/* 245 */         if (match.deny) {
/* 246 */           res.addDeny(match.pattern.pattern()); continue;
/*     */         } 
/* 248 */         res.addAllow(match.pattern.pattern());
/*     */       } 
/*     */       
/* 251 */       res.setDefaultAllow(this.defaultAllow);
/* 252 */       return res;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\AccessControlListHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */