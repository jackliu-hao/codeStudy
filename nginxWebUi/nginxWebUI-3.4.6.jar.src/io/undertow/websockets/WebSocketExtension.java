/*     */ package io.undertow.websockets;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
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
/*     */ public class WebSocketExtension
/*     */ {
/*     */   private final String name;
/*     */   private final List<Parameter> parameters;
/*     */   
/*     */   public WebSocketExtension(String name) {
/*  35 */     this.name = name;
/*  36 */     this.parameters = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public WebSocketExtension(String name, List<Parameter> parameters) {
/*  40 */     this.name = name;
/*  41 */     this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
/*     */   }
/*     */   
/*     */   public String getName() {
/*  45 */     return this.name;
/*     */   }
/*     */   
/*     */   public List<Parameter> getParameters() {
/*  49 */     return this.parameters;
/*     */   }
/*     */   
/*     */   public static final class Parameter {
/*     */     private final String name;
/*     */     private final String value;
/*     */     
/*     */     public Parameter(String name, String value) {
/*  57 */       this.name = name;
/*  58 */       this.value = value;
/*     */     }
/*     */     
/*     */     public String getName() {
/*  62 */       return this.name;
/*     */     }
/*     */     
/*     */     public String getValue() {
/*  66 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/*  71 */       return "{'" + this.name + '\'' + ": '" + this.value + '\'' + '}';
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  79 */     return "WebSocketExtension{name='" + this.name + '\'' + ", parameters=" + this.parameters + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<WebSocketExtension> parse(String extensionHeader) {
/*  86 */     if (extensionHeader == null || extensionHeader.isEmpty()) {
/*  87 */       return Collections.emptyList();
/*     */     }
/*  89 */     List<WebSocketExtension> extensions = new ArrayList<>();
/*     */     
/*  91 */     String[] parts = extensionHeader.split(",");
/*  92 */     for (String part : parts) {
/*  93 */       String[] items = part.split(";");
/*  94 */       if (items.length > 0) {
/*  95 */         List<Parameter> params = new ArrayList<>(items.length - 1);
/*  96 */         String name = items[0].trim();
/*  97 */         for (int i = 1; i < items.length; i++) {
/*     */ 
/*     */ 
/*     */           
/* 101 */           if (items[i].contains("=")) {
/* 102 */             String[] param = items[i].split("=");
/* 103 */             if (param.length == 2) {
/* 104 */               params.add(new Parameter(param[0].trim(), param[1].trim()));
/*     */             }
/*     */           } else {
/* 107 */             params.add(new Parameter(items[i].trim(), null));
/*     */           } 
/*     */         } 
/* 110 */         extensions.add(new WebSocketExtension(name, params));
/*     */       } 
/*     */     } 
/* 113 */     return extensions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toExtensionHeader(List<WebSocketExtension> extensions) {
/* 125 */     StringBuilder extensionsHeader = new StringBuilder();
/* 126 */     if (extensions != null && extensions.size() > 0) {
/* 127 */       Iterator<WebSocketExtension> it = extensions.iterator();
/* 128 */       while (it.hasNext()) {
/* 129 */         WebSocketExtension extension = it.next();
/* 130 */         extensionsHeader.append(extension.getName());
/* 131 */         for (Parameter param : extension.getParameters()) {
/* 132 */           extensionsHeader.append("; ").append(param.getName());
/* 133 */           if (param.getValue() != null && param.getValue().length() > 0) {
/* 134 */             extensionsHeader.append("=").append(param.getValue());
/*     */           }
/*     */         } 
/* 137 */         if (it.hasNext()) {
/* 138 */           extensionsHeader.append(", ");
/*     */         }
/*     */       } 
/*     */     } 
/* 142 */     return extensionsHeader.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\WebSocketExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */