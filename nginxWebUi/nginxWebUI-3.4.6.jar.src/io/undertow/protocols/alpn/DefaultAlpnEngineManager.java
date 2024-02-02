/*    */ package io.undertow.protocols.alpn;
/*    */ 
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
/*    */ public class DefaultAlpnEngineManager
/*    */   implements ALPNEngineManager
/*    */ {
/*    */   public int getPriority() {
/* 26 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean registerEngine(SSLEngine engine, Function<SSLEngine, SSLEngine> selectedFunction) {
/* 31 */     selectedFunction.apply(engine);
/* 32 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\alpn\DefaultAlpnEngineManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */