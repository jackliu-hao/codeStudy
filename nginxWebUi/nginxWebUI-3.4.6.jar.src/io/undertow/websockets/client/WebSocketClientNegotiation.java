/*    */ package io.undertow.websockets.client;
/*    */ 
/*    */ import io.undertow.websockets.WebSocketExtension;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class WebSocketClientNegotiation
/*    */ {
/*    */   private final List<String> supportedSubProtocols;
/*    */   private final List<WebSocketExtension> supportedExtensions;
/*    */   private volatile String selectedSubProtocol;
/*    */   private volatile List<WebSocketExtension> selectedExtensions;
/*    */   
/*    */   public WebSocketClientNegotiation(List<String> supportedSubProtocols, List<WebSocketExtension> supportedExtensions) {
/* 37 */     this.supportedSubProtocols = supportedSubProtocols;
/* 38 */     this.supportedExtensions = supportedExtensions;
/*    */   }
/*    */   
/*    */   public List<String> getSupportedSubProtocols() {
/* 42 */     return this.supportedSubProtocols;
/*    */   }
/*    */   
/*    */   public List<WebSocketExtension> getSupportedExtensions() {
/* 46 */     return this.supportedExtensions;
/*    */   }
/*    */   
/*    */   public String getSelectedSubProtocol() {
/* 50 */     return this.selectedSubProtocol;
/*    */   }
/*    */   
/*    */   public List<WebSocketExtension> getSelectedExtensions() {
/* 54 */     return this.selectedExtensions;
/*    */   }
/*    */ 
/*    */   
/*    */   public void beforeRequest(Map<String, List<String>> headers) {}
/*    */ 
/*    */   
/*    */   public void afterRequest(Map<String, List<String>> headers) {}
/*    */ 
/*    */   
/*    */   public void handshakeComplete(String selectedProtocol, List<WebSocketExtension> selectedExtensions) {
/* 65 */     this.selectedExtensions = selectedExtensions;
/* 66 */     this.selectedSubProtocol = selectedProtocol;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\client\WebSocketClientNegotiation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */