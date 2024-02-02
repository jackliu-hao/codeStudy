/*     */ package io.undertow.websockets.extensions;
/*     */ 
/*     */ import io.undertow.websockets.WebSocketExtension;
/*     */ import io.undertow.websockets.core.WebSocketLogger;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
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
/*     */ public class PerMessageDeflateHandshake
/*     */   implements ExtensionHandshake
/*     */ {
/*     */   private static final String PERMESSAGE_DEFLATE = "permessage-deflate";
/*     */   private static final String SERVER_NO_CONTEXT_TAKEOVER = "server_no_context_takeover";
/*     */   private static final String CLIENT_NO_CONTEXT_TAKEOVER = "client_no_context_takeover";
/*     */   private static final String SERVER_MAX_WINDOW_BITS = "server_max_window_bits";
/*     */   private static final String CLIENT_MAX_WINDOW_BITS = "client_max_window_bits";
/*  48 */   private final Set<String> incompatibleExtensions = new HashSet<>();
/*     */   
/*     */   private boolean compressContextTakeover;
/*     */   
/*     */   private boolean decompressContextTakeover;
/*     */   
/*     */   private final boolean client;
/*     */   
/*     */   private final int deflaterLevel;
/*     */   
/*     */   public static final int DEFAULT_DEFLATER = -1;
/*     */ 
/*     */   
/*     */   public PerMessageDeflateHandshake() {
/*  62 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PerMessageDeflateHandshake(boolean client) {
/*  71 */     this(client, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PerMessageDeflateHandshake(boolean client, int deflaterLevel) {
/*  81 */     this(client, deflaterLevel, true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PerMessageDeflateHandshake(boolean client, boolean compressContextTakeover, boolean decompressContextTakeover) {
/*  92 */     this(client, -1, compressContextTakeover, decompressContextTakeover);
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
/*     */   public PerMessageDeflateHandshake(boolean client, int deflaterLevel, boolean compressContextTakeover, boolean decompressContextTakeover) {
/* 104 */     this.client = client;
/* 105 */     this.deflaterLevel = deflaterLevel;
/*     */ 
/*     */ 
/*     */     
/* 109 */     this.incompatibleExtensions.add("permessage-deflate");
/* 110 */     this.compressContextTakeover = compressContextTakeover;
/* 111 */     this.decompressContextTakeover = decompressContextTakeover;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 116 */     return "permessage-deflate";
/*     */   }
/*     */ 
/*     */   
/*     */   public WebSocketExtension accept(WebSocketExtension extension) {
/* 121 */     if (extension == null || !extension.getName().equals(getName())) return null;
/*     */     
/* 123 */     WebSocketExtension negotiated = new WebSocketExtension(extension.getName());
/*     */     
/* 125 */     if (extension.getParameters() == null || extension.getParameters().size() == 0) return negotiated; 
/* 126 */     for (WebSocketExtension.Parameter parameter : extension.getParameters()) {
/* 127 */       if (parameter.getName().equals("server_max_window_bits")) {
/*     */         continue;
/*     */       }
/*     */       
/* 131 */       if (parameter.getName().equals("client_max_window_bits")) {
/*     */         continue;
/*     */       }
/*     */       
/* 135 */       if (parameter.getName().equals("server_no_context_takeover")) {
/* 136 */         negotiated.getParameters().add(parameter);
/* 137 */         if (this.client) {
/* 138 */           this.decompressContextTakeover = false; continue;
/*     */         } 
/* 140 */         this.compressContextTakeover = false; continue;
/*     */       } 
/* 142 */       if (parameter.getName().equals("client_no_context_takeover")) {
/* 143 */         negotiated.getParameters().add(parameter);
/* 144 */         if (this.client) {
/* 145 */           this.compressContextTakeover = false; continue;
/*     */         } 
/* 147 */         this.decompressContextTakeover = false;
/*     */         continue;
/*     */       } 
/* 150 */       WebSocketLogger.EXTENSION_LOGGER.incorrectExtensionParameter(parameter);
/* 151 */       return null;
/*     */     } 
/*     */     
/* 154 */     WebSocketLogger.EXTENSION_LOGGER.debugf("Negotiated extension %s for handshake %s", negotiated, extension);
/* 155 */     return negotiated;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIncompatible(List<ExtensionHandshake> extensions) {
/* 160 */     for (ExtensionHandshake extension : extensions) {
/* 161 */       if (this.incompatibleExtensions.contains(extension.getName())) {
/* 162 */         return true;
/*     */       }
/*     */     } 
/* 165 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ExtensionFunction create() {
/* 170 */     return new PerMessageDeflateFunction(this.deflaterLevel, this.compressContextTakeover, this.decompressContextTakeover);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\extensions\PerMessageDeflateHandshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */