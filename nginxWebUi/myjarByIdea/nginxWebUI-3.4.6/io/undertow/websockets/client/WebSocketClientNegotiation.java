package io.undertow.websockets.client;

import io.undertow.websockets.WebSocketExtension;
import java.util.List;
import java.util.Map;

public class WebSocketClientNegotiation {
   private final List<String> supportedSubProtocols;
   private final List<WebSocketExtension> supportedExtensions;
   private volatile String selectedSubProtocol;
   private volatile List<WebSocketExtension> selectedExtensions;

   public WebSocketClientNegotiation(List<String> supportedSubProtocols, List<WebSocketExtension> supportedExtensions) {
      this.supportedSubProtocols = supportedSubProtocols;
      this.supportedExtensions = supportedExtensions;
   }

   public List<String> getSupportedSubProtocols() {
      return this.supportedSubProtocols;
   }

   public List<WebSocketExtension> getSupportedExtensions() {
      return this.supportedExtensions;
   }

   public String getSelectedSubProtocol() {
      return this.selectedSubProtocol;
   }

   public List<WebSocketExtension> getSelectedExtensions() {
      return this.selectedExtensions;
   }

   public void beforeRequest(Map<String, List<String>> headers) {
   }

   public void afterRequest(Map<String, List<String>> headers) {
   }

   public void handshakeComplete(String selectedProtocol, List<WebSocketExtension> selectedExtensions) {
      this.selectedExtensions = selectedExtensions;
      this.selectedSubProtocol = selectedProtocol;
   }
}
