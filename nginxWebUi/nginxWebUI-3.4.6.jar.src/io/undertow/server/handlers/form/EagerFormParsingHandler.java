/*     */ package io.undertow.server.handlers.form;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import java.util.Collections;
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
/*     */ public class EagerFormParsingHandler
/*     */   implements HttpHandler
/*     */ {
/*  46 */   private volatile HttpHandler next = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*     */   private final FormParserFactory formParserFactory;
/*     */   
/*  49 */   public static final HandlerWrapper WRAPPER = new HandlerWrapper()
/*     */     {
/*     */       public HttpHandler wrap(HttpHandler handler) {
/*  52 */         return new EagerFormParsingHandler(handler);
/*     */       }
/*     */     };
/*     */   
/*     */   public EagerFormParsingHandler(FormParserFactory formParserFactory) {
/*  57 */     this.formParserFactory = formParserFactory;
/*     */   }
/*     */   
/*     */   public EagerFormParsingHandler() {
/*  61 */     this.formParserFactory = FormParserFactory.builder().build();
/*     */   }
/*     */   
/*     */   public EagerFormParsingHandler(HttpHandler next) {
/*  65 */     this();
/*  66 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  71 */     FormDataParser parser = this.formParserFactory.createParser(exchange);
/*  72 */     if (parser == null) {
/*  73 */       this.next.handleRequest(exchange);
/*     */       return;
/*     */     } 
/*  76 */     if (exchange.isBlocking()) {
/*  77 */       exchange.putAttachment(FormDataParser.FORM_DATA, parser.parseBlocking());
/*  78 */       this.next.handleRequest(exchange);
/*     */     } else {
/*  80 */       parser.parse(this.next);
/*     */     } 
/*     */   }
/*     */   
/*     */   public HttpHandler getNext() {
/*  85 */     return this.next;
/*     */   }
/*     */   
/*     */   public EagerFormParsingHandler setNext(HttpHandler next) {
/*  89 */     Handlers.handlerNotNull(next);
/*  90 */     this.next = next;
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  96 */     return "eager-form-parser()";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 104 */       return "eager-form-parser";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 109 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 114 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 119 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 124 */       return EagerFormParsingHandler.WRAPPER;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\form\EagerFormParsingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */