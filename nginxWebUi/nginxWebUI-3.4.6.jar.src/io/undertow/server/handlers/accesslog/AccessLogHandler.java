/*     */ package io.undertow.server.handlers.accesslog;
/*     */ 
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributeWrapper;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.attribute.SubstituteEmptyWrapper;
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.predicate.Predicates;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class AccessLogHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final AccessLogReceiver accessLogReceiver;
/*     */   private final String formatString;
/*     */   private final ExchangeAttribute tokens;
/* 102 */   private final ExchangeCompletionListener exchangeCompletionListener = new AccessLogCompletionListener();
/*     */   private final Predicate predicate;
/*     */   
/*     */   public AccessLogHandler(HttpHandler next, AccessLogReceiver accessLogReceiver, String formatString, ClassLoader classLoader) {
/* 106 */     this(next, accessLogReceiver, formatString, classLoader, Predicates.truePredicate());
/*     */   }
/*     */   
/*     */   public AccessLogHandler(HttpHandler next, AccessLogReceiver accessLogReceiver, String formatString, ClassLoader classLoader, Predicate predicate) {
/* 110 */     this.next = next;
/* 111 */     this.accessLogReceiver = accessLogReceiver;
/* 112 */     this.predicate = predicate;
/* 113 */     this.formatString = handleCommonNames(formatString);
/* 114 */     this.tokens = ExchangeAttributes.parser(classLoader, new ExchangeAttributeWrapper[] { (ExchangeAttributeWrapper)new SubstituteEmptyWrapper("-") }).parse(this.formatString);
/*     */   }
/*     */   
/*     */   public AccessLogHandler(HttpHandler next, AccessLogReceiver accessLogReceiver, String formatString, ExchangeAttribute attribute) {
/* 118 */     this(next, accessLogReceiver, formatString, attribute, Predicates.truePredicate());
/*     */   }
/*     */   
/*     */   public AccessLogHandler(HttpHandler next, AccessLogReceiver accessLogReceiver, String formatString, ExchangeAttribute attribute, Predicate predicate) {
/* 122 */     this.next = next;
/* 123 */     this.accessLogReceiver = accessLogReceiver;
/* 124 */     this.predicate = predicate;
/* 125 */     this.formatString = handleCommonNames(formatString);
/* 126 */     this.tokens = attribute;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String handleCommonNames(String formatString) {
/* 132 */     if (formatString.equals("common"))
/* 133 */       return "%h %l %u %t \"%r\" %s %b"; 
/* 134 */     if (formatString.equals("combined"))
/* 135 */       return "%h %l %u %t \"%r\" %s %b \"%{i,Referer}\" \"%{i,User-Agent}\""; 
/* 136 */     if (formatString.equals("commonobf"))
/* 137 */       return "%o %l %u %t \"%r\" %s %b"; 
/* 138 */     if (formatString.equals("combinedobf")) {
/* 139 */       return "%o %l %u %t \"%r\" %s %b \"%{i,Referer}\" \"%{i,User-Agent}\"";
/*     */     }
/* 141 */     return formatString;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 147 */     exchange.addExchangeCompleteListener(this.exchangeCompletionListener);
/* 148 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private class AccessLogCompletionListener
/*     */     implements ExchangeCompletionListener {
/*     */     public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/*     */       try {
/* 155 */         if (AccessLogHandler.this.predicate == null || AccessLogHandler.this.predicate.resolve(exchange)) {
/* 156 */           AccessLogHandler.this.accessLogReceiver.logMessage(AccessLogHandler.this.tokens.readAttribute(exchange));
/*     */         }
/*     */       } finally {
/* 159 */         nextListener.proceed();
/*     */       } 
/*     */     }
/*     */     
/*     */     private AccessLogCompletionListener() {} }
/*     */   
/*     */   public String toString() {
/* 166 */     return "AccessLogHandler{formatString='" + this.formatString + '\'' + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 175 */       return "access-log";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 180 */       Map<String, Class<?>> params = new HashMap<>();
/* 181 */       params.put("format", String.class);
/* 182 */       params.put("category", String.class);
/* 183 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 188 */       return Collections.singleton("format");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 193 */       return "format";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 198 */       return new AccessLogHandler.Wrapper((String)config.get("format"), (String)config.get("category"));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final String format;
/*     */     private final String category;
/*     */     
/*     */     private Wrapper(String format, String category) {
/* 209 */       this.format = format;
/* 210 */       this.category = category;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 215 */       if (this.category == null || this.category.trim().isEmpty()) {
/* 216 */         return new AccessLogHandler(handler, new JBossLoggingAccessLogReceiver(), this.format, Wrapper.class.getClassLoader());
/*     */       }
/* 218 */       return new AccessLogHandler(handler, new JBossLoggingAccessLogReceiver(this.category), this.format, Wrapper.class.getClassLoader());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\accesslog\AccessLogHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */