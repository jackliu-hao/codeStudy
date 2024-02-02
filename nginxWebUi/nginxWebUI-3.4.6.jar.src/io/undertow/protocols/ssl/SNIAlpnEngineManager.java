/*    */ package io.undertow.protocols.ssl;
/*    */ 
/*    */ import io.undertow.protocols.alpn.ALPNEngineManager;
/*    */ import java.util.function.Function;
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
/*    */ public class SNIAlpnEngineManager
/*    */   implements ALPNEngineManager
/*    */ {
/*    */   public int getPriority() {
/* 27 */     return 100;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean registerEngine(SSLEngine engine, Function<SSLEngine, SSLEngine> selectedFunction) {
/* 32 */     if (!(engine instanceof SNISSLEngine)) {
/* 33 */       return false;
/*    */     }
/* 35 */     SNISSLEngine snisslEngine = (SNISSLEngine)engine;
/* 36 */     snisslEngine.setSelectionCallback(selectedFunction);
/* 37 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\SNIAlpnEngineManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */