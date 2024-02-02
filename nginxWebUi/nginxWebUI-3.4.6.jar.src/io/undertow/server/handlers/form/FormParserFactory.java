/*     */ package io.undertow.server.handlers.form;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
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
/*     */ public class FormParserFactory
/*     */ {
/*  38 */   private static final AttachmentKey<FormDataParser> ATTACHMENT_KEY = AttachmentKey.create(FormDataParser.class);
/*     */   
/*     */   private final ParserDefinition[] parserDefinitions;
/*     */   
/*     */   FormParserFactory(List<ParserDefinition> parserDefinitions) {
/*  43 */     this.parserDefinitions = parserDefinitions.<ParserDefinition>toArray(new ParserDefinition[parserDefinitions.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FormDataParser createParser(HttpServerExchange exchange) {
/*  54 */     FormDataParser existing = (FormDataParser)exchange.getAttachment(ATTACHMENT_KEY);
/*  55 */     if (existing != null) {
/*  56 */       return existing;
/*     */     }
/*  58 */     for (int i = 0; i < this.parserDefinitions.length; i++) {
/*  59 */       FormDataParser parser = this.parserDefinitions[i].create(exchange);
/*  60 */       if (parser != null) {
/*  61 */         exchange.putAttachment(ATTACHMENT_KEY, parser);
/*  62 */         return parser;
/*     */       } 
/*     */     } 
/*  65 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder builder() {
/*  76 */     return builder(true);
/*     */   }
/*     */   
/*     */   public static Builder builder(boolean includeDefault) {
/*  80 */     Builder builder = new Builder();
/*  81 */     if (includeDefault) {
/*  82 */       builder.addParsers(new ParserDefinition[] { new FormEncodedDataDefinition(), new MultiPartParserDefinition() });
/*     */     }
/*  84 */     return builder;
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */   {
/*  89 */     private List<FormParserFactory.ParserDefinition> parsers = new ArrayList<>();
/*     */     
/*  91 */     private String defaultCharset = null;
/*     */     
/*     */     public Builder addParser(FormParserFactory.ParserDefinition definition) {
/*  94 */       this.parsers.add(definition);
/*  95 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addParsers(FormParserFactory.ParserDefinition... definition) {
/*  99 */       this.parsers.addAll(Arrays.asList(definition));
/* 100 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addParsers(List<FormParserFactory.ParserDefinition> definition) {
/* 104 */       this.parsers.addAll(definition);
/* 105 */       return this;
/*     */     }
/*     */     
/*     */     public List<FormParserFactory.ParserDefinition> getParsers() {
/* 109 */       return this.parsers;
/*     */     }
/*     */     
/*     */     public void setParsers(List<FormParserFactory.ParserDefinition> parsers) {
/* 113 */       this.parsers = parsers;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withParsers(List<FormParserFactory.ParserDefinition> parsers) {
/* 120 */       setParsers(parsers);
/* 121 */       return this;
/*     */     }
/*     */     
/*     */     public String getDefaultCharset() {
/* 125 */       return this.defaultCharset;
/*     */     }
/*     */     
/*     */     public void setDefaultCharset(String defaultCharset) {
/* 129 */       this.defaultCharset = defaultCharset;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder withDefaultCharset(String defaultCharset) {
/* 136 */       setDefaultCharset(defaultCharset);
/* 137 */       return this;
/*     */     }
/*     */     
/*     */     public FormParserFactory build() {
/* 141 */       if (this.defaultCharset != null) {
/* 142 */         for (FormParserFactory.ParserDefinition parser : this.parsers) {
/* 143 */           parser.setDefaultEncoding(this.defaultCharset);
/*     */         }
/*     */       }
/* 146 */       return new FormParserFactory(this.parsers);
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface ParserDefinition<T> {
/*     */     FormDataParser create(HttpServerExchange param1HttpServerExchange);
/*     */     
/*     */     T setDefaultEncoding(String param1String);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\form\FormParserFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */