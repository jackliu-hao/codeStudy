package io.undertow.websockets.extensions;

import io.undertow.websockets.WebSocketExtension;
import java.util.List;

public interface ExtensionHandshake {
  String getName();
  
  WebSocketExtension accept(WebSocketExtension paramWebSocketExtension);
  
  boolean isIncompatible(List<ExtensionHandshake> paramList);
  
  ExtensionFunction create();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\extensions\ExtensionHandshake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */