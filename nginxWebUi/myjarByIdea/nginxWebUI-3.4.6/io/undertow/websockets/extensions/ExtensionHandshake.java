package io.undertow.websockets.extensions;

import io.undertow.websockets.WebSocketExtension;
import java.util.List;

public interface ExtensionHandshake {
   String getName();

   WebSocketExtension accept(WebSocketExtension var1);

   boolean isIncompatible(List<ExtensionHandshake> var1);

   ExtensionFunction create();
}
