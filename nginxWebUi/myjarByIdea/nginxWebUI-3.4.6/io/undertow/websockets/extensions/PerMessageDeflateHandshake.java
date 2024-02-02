package io.undertow.websockets.extensions;

import io.undertow.websockets.WebSocketExtension;
import io.undertow.websockets.core.WebSocketLogger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PerMessageDeflateHandshake implements ExtensionHandshake {
   private static final String PERMESSAGE_DEFLATE = "permessage-deflate";
   private static final String SERVER_NO_CONTEXT_TAKEOVER = "server_no_context_takeover";
   private static final String CLIENT_NO_CONTEXT_TAKEOVER = "client_no_context_takeover";
   private static final String SERVER_MAX_WINDOW_BITS = "server_max_window_bits";
   private static final String CLIENT_MAX_WINDOW_BITS = "client_max_window_bits";
   private final Set<String> incompatibleExtensions;
   private boolean compressContextTakeover;
   private boolean decompressContextTakeover;
   private final boolean client;
   private final int deflaterLevel;
   public static final int DEFAULT_DEFLATER = -1;

   public PerMessageDeflateHandshake() {
      this(false);
   }

   public PerMessageDeflateHandshake(boolean client) {
      this(client, -1);
   }

   public PerMessageDeflateHandshake(boolean client, int deflaterLevel) {
      this(client, deflaterLevel, true, true);
   }

   public PerMessageDeflateHandshake(boolean client, boolean compressContextTakeover, boolean decompressContextTakeover) {
      this(client, -1, compressContextTakeover, decompressContextTakeover);
   }

   public PerMessageDeflateHandshake(boolean client, int deflaterLevel, boolean compressContextTakeover, boolean decompressContextTakeover) {
      this.incompatibleExtensions = new HashSet();
      this.client = client;
      this.deflaterLevel = deflaterLevel;
      this.incompatibleExtensions.add("permessage-deflate");
      this.compressContextTakeover = compressContextTakeover;
      this.decompressContextTakeover = decompressContextTakeover;
   }

   public String getName() {
      return "permessage-deflate";
   }

   public WebSocketExtension accept(WebSocketExtension extension) {
      if (extension != null && extension.getName().equals(this.getName())) {
         WebSocketExtension negotiated = new WebSocketExtension(extension.getName());
         if (extension.getParameters() != null && extension.getParameters().size() != 0) {
            Iterator var3 = extension.getParameters().iterator();

            while(var3.hasNext()) {
               WebSocketExtension.Parameter parameter = (WebSocketExtension.Parameter)var3.next();
               if (!parameter.getName().equals("server_max_window_bits") && !parameter.getName().equals("client_max_window_bits")) {
                  if (parameter.getName().equals("server_no_context_takeover")) {
                     negotiated.getParameters().add(parameter);
                     if (this.client) {
                        this.decompressContextTakeover = false;
                     } else {
                        this.compressContextTakeover = false;
                     }
                  } else {
                     if (!parameter.getName().equals("client_no_context_takeover")) {
                        WebSocketLogger.EXTENSION_LOGGER.incorrectExtensionParameter(parameter);
                        return null;
                     }

                     negotiated.getParameters().add(parameter);
                     if (this.client) {
                        this.compressContextTakeover = false;
                     } else {
                        this.decompressContextTakeover = false;
                     }
                  }
               }
            }

            WebSocketLogger.EXTENSION_LOGGER.debugf("Negotiated extension %s for handshake %s", negotiated, extension);
            return negotiated;
         } else {
            return negotiated;
         }
      } else {
         return null;
      }
   }

   public boolean isIncompatible(List<ExtensionHandshake> extensions) {
      Iterator var2 = extensions.iterator();

      ExtensionHandshake extension;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         extension = (ExtensionHandshake)var2.next();
      } while(!this.incompatibleExtensions.contains(extension.getName()));

      return true;
   }

   public ExtensionFunction create() {
      return new PerMessageDeflateFunction(this.deflaterLevel, this.compressContextTakeover, this.decompressContextTakeover);
   }
}
