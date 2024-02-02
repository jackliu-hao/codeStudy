/*     */ package io.undertow.protocols.alpn;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.eclipse.jetty.alpn.ALPN;
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
/*     */ public class JettyAlpnProvider
/*     */   implements ALPNProvider
/*     */ {
/*  37 */   private static final String PROTOCOL_KEY = JettyAlpnProvider.class.getName() + ".protocol";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  42 */   private static final boolean ENABLED = ((Boolean)AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>()
/*     */       {
/*     */         public Boolean run() {
/*     */           try {
/*  46 */             Class.forName("org.eclipse.jetty.alpn.ALPN", true, JettyAlpnProvider.class.getClassLoader());
/*  47 */             return Boolean.valueOf(true);
/*  48 */           } catch (ClassNotFoundException e) {
/*  49 */             return Boolean.valueOf(false);
/*     */           } 
/*     */         }
/*     */       })).booleanValue();
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEnabled(SSLEngine sslEngine) {
/*  57 */     return ENABLED;
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLEngine setProtocols(SSLEngine engine, String[] protocols) {
/*  62 */     return Impl.setProtocols(engine, protocols);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSelectedProtocol(SSLEngine engine) {
/*  67 */     SSLSession handshake = engine.getHandshakeSession();
/*  68 */     if (handshake != null) {
/*  69 */       return (String)handshake.getValue(PROTOCOL_KEY);
/*     */     }
/*  71 */     handshake = engine.getSession();
/*  72 */     if (handshake != null) {
/*  73 */       return (String)handshake.getValue(PROTOCOL_KEY);
/*     */     }
/*  75 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/*  80 */     return 100;
/*     */   }
/*     */   
/*     */   private static class Impl
/*     */   {
/*     */     static SSLEngine setProtocols(final SSLEngine engine, final String[] protocols) {
/*  86 */       if (engine.getUseClientMode()) {
/*  87 */         ALPN.put(engine, (ALPN.Provider)new JettyAlpnProvider.ALPNClientSelectionProvider(Arrays.asList(protocols), engine));
/*     */       } else {
/*  89 */         ALPN.put(engine, (ALPN.Provider)new ALPN.ServerProvider()
/*     */             {
/*     */               public void unsupported() {
/*  92 */                 ALPN.remove(engine);
/*     */               }
/*     */ 
/*     */               
/*     */               public String select(List<String> strings) {
/*  97 */                 ALPN.remove(engine);
/*  98 */                 for (String p : protocols) {
/*  99 */                   if (strings.contains(p)) {
/* 100 */                     engine.getHandshakeSession().putValue(JettyAlpnProvider.PROTOCOL_KEY, p);
/* 101 */                     return p;
/*     */                   } 
/*     */                 } 
/* 104 */                 return null;
/*     */               }
/*     */             });
/*     */       } 
/* 108 */       return engine;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ALPNClientSelectionProvider
/*     */     implements ALPN.ClientProvider {
/*     */     final List<String> protocols;
/*     */     private String selected;
/*     */     private final SSLEngine sslEngine;
/*     */     
/*     */     private ALPNClientSelectionProvider(List<String> protocols, SSLEngine sslEngine) {
/* 119 */       this.protocols = protocols;
/* 120 */       this.sslEngine = sslEngine;
/*     */     }
/*     */     
/*     */     public boolean supports() {
/* 124 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> protocols() {
/* 129 */       return this.protocols;
/*     */     }
/*     */ 
/*     */     
/*     */     public void unsupported() {
/* 134 */       ALPN.remove(this.sslEngine);
/* 135 */       this.selected = "";
/*     */     }
/*     */ 
/*     */     
/*     */     public void selected(String s) {
/* 140 */       ALPN.remove(this.sslEngine);
/* 141 */       this.selected = s;
/* 142 */       this.sslEngine.getHandshakeSession().putValue(JettyAlpnProvider.PROTOCOL_KEY, this.selected);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 148 */     return "JettyAlpnProvider";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\alpn\JettyAlpnProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */