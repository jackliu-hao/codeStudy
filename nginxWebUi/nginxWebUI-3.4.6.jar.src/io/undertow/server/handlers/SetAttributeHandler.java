/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributeParser;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.attribute.NullAttribute;
/*     */ import io.undertow.attribute.ReadOnlyAttributeException;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ResponseCommitListener;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
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
/*     */ public class SetAttributeHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final ExchangeAttribute attribute;
/*     */   private final ExchangeAttribute value;
/*     */   private final boolean preCommit;
/*     */   
/*     */   public SetAttributeHandler(HttpHandler next, ExchangeAttribute attribute, ExchangeAttribute value) {
/*  52 */     this(next, attribute, value, false);
/*     */   }
/*     */   
/*     */   public SetAttributeHandler(HttpHandler next, String attribute, String value) {
/*  56 */     this.next = next;
/*  57 */     ExchangeAttributeParser parser = ExchangeAttributes.parser(getClass().getClassLoader());
/*  58 */     this.attribute = parser.parseSingleToken(attribute);
/*  59 */     this.value = parser.parse(value);
/*  60 */     this.preCommit = false;
/*     */   }
/*     */   
/*     */   public SetAttributeHandler(HttpHandler next, String attribute, String value, ClassLoader classLoader) {
/*  64 */     this.next = next;
/*  65 */     ExchangeAttributeParser parser = ExchangeAttributes.parser(classLoader);
/*  66 */     this.attribute = parser.parseSingleToken(attribute);
/*  67 */     this.value = parser.parse(value);
/*  68 */     this.preCommit = false;
/*     */   }
/*     */   
/*     */   public SetAttributeHandler(HttpHandler next, ExchangeAttribute attribute, ExchangeAttribute value, boolean preCommit) {
/*  72 */     this.next = next;
/*  73 */     this.attribute = attribute;
/*  74 */     this.value = value;
/*  75 */     this.preCommit = preCommit;
/*     */   }
/*     */   
/*     */   public SetAttributeHandler(HttpHandler next, String attribute, String value, boolean preCommit) {
/*  79 */     this.next = next;
/*  80 */     this.preCommit = preCommit;
/*  81 */     ExchangeAttributeParser parser = ExchangeAttributes.parser(getClass().getClassLoader());
/*  82 */     this.attribute = parser.parseSingleToken(attribute);
/*  83 */     this.value = parser.parse(value);
/*     */   }
/*     */   
/*     */   public SetAttributeHandler(HttpHandler next, String attribute, String value, ClassLoader classLoader, boolean preCommit) {
/*  87 */     this.next = next;
/*  88 */     this.preCommit = preCommit;
/*  89 */     ExchangeAttributeParser parser = ExchangeAttributes.parser(classLoader);
/*  90 */     this.attribute = parser.parseSingleToken(attribute);
/*  91 */     this.value = parser.parse(value);
/*     */   }
/*     */   
/*     */   public ExchangeAttribute getValue() {
/*  95 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 100 */     return "set( attribute='" + this.attribute.toString() + "', value='" + this.value.toString() + "' )";
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 105 */     if (this.preCommit) {
/* 106 */       exchange.addResponseCommitListener(new ResponseCommitListener()
/*     */           {
/*     */             public void beforeCommit(HttpServerExchange exchange) {
/*     */               try {
/* 110 */                 SetAttributeHandler.this.attribute.writeAttribute(exchange, SetAttributeHandler.this.value.readAttribute(exchange));
/* 111 */               } catch (ReadOnlyAttributeException e) {
/* 112 */                 throw new RuntimeException(e);
/*     */               } 
/*     */             }
/*     */           });
/*     */     } else {
/* 117 */       this.attribute.writeAttribute(exchange, this.value.readAttribute(exchange));
/*     */     } 
/* 119 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder {
/*     */     public String name() {
/* 125 */       return "set";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 130 */       Map<String, Class<?>> parameters = new HashMap<>();
/* 131 */       parameters.put("value", ExchangeAttribute.class);
/* 132 */       parameters.put("attribute", ExchangeAttribute.class);
/* 133 */       parameters.put("pre-commit", Boolean.class);
/*     */       
/* 135 */       return parameters;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 140 */       Set<String> req = new HashSet<>();
/* 141 */       req.add("value");
/* 142 */       req.add("attribute");
/* 143 */       return req;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 148 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 153 */       final ExchangeAttribute value = (ExchangeAttribute)config.get("value");
/* 154 */       final ExchangeAttribute attribute = (ExchangeAttribute)config.get("attribute");
/* 155 */       final Boolean preCommit = (Boolean)config.get("pre-commit");
/*     */       
/* 157 */       return new HandlerWrapper()
/*     */         {
/*     */           public HttpHandler wrap(HttpHandler handler) {
/* 160 */             return new SetAttributeHandler(handler, attribute, value, (preCommit == null) ? false : preCommit.booleanValue());
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ClearBuilder
/*     */     implements HandlerBuilder {
/*     */     public String name() {
/* 169 */       return "clear";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 174 */       Map<String, Class<?>> parameters = new HashMap<>();
/* 175 */       parameters.put("attribute", ExchangeAttribute.class);
/* 176 */       parameters.put("pre-commit", Boolean.class);
/* 177 */       return parameters;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 182 */       Set<String> req = new HashSet<>();
/* 183 */       req.add("attribute");
/* 184 */       return req;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 189 */       return "attribute";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 194 */       final ExchangeAttribute attribute = (ExchangeAttribute)config.get("attribute");
/* 195 */       final Boolean preCommit = (Boolean)config.get("pre-commit");
/*     */       
/* 197 */       return new HandlerWrapper()
/*     */         {
/*     */           public HttpHandler wrap(HttpHandler handler) {
/* 200 */             return new SetAttributeHandler(handler, attribute, (ExchangeAttribute)NullAttribute.INSTANCE, (preCommit == null) ? false : preCommit.booleanValue());
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\SetAttributeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */