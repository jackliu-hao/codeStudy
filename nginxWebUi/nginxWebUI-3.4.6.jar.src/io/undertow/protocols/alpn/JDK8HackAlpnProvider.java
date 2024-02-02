/*    */ package io.undertow.protocols.alpn;
/*    */ 
/*    */ import io.undertow.protocols.ssl.ALPNHackSSLEngine;
/*    */ import java.util.Arrays;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JDK8HackAlpnProvider
/*    */   implements ALPNProvider
/*    */ {
/*    */   public boolean isEnabled(SSLEngine sslEngine) {
/* 36 */     return ALPNHackSSLEngine.isEnabled(sslEngine);
/*    */   }
/*    */ 
/*    */   
/*    */   public SSLEngine setProtocols(SSLEngine engine, String[] protocols) {
/* 41 */     ALPNHackSSLEngine newEngine = (engine instanceof ALPNHackSSLEngine) ? (ALPNHackSSLEngine)engine : new ALPNHackSSLEngine(engine);
/* 42 */     newEngine.setApplicationProtocols(Arrays.asList(protocols));
/* 43 */     return (SSLEngine)newEngine;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSelectedProtocol(SSLEngine engine) {
/* 48 */     return ((ALPNHackSSLEngine)engine).getSelectedApplicationProtocol();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getPriority() {
/* 53 */     return 300;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 58 */     return "JDK8AlpnProvider";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\alpn\JDK8HackAlpnProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */