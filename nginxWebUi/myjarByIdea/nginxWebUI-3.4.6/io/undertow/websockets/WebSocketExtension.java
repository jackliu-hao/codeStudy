package io.undertow.websockets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class WebSocketExtension {
   private final String name;
   private final List<Parameter> parameters;

   public WebSocketExtension(String name) {
      this.name = name;
      this.parameters = new ArrayList();
   }

   public WebSocketExtension(String name, List<Parameter> parameters) {
      this.name = name;
      this.parameters = Collections.unmodifiableList(new ArrayList(parameters));
   }

   public String getName() {
      return this.name;
   }

   public List<Parameter> getParameters() {
      return this.parameters;
   }

   public String toString() {
      return "WebSocketExtension{name='" + this.name + '\'' + ", parameters=" + this.parameters + '}';
   }

   public static List<WebSocketExtension> parse(String extensionHeader) {
      if (extensionHeader != null && !extensionHeader.isEmpty()) {
         List<WebSocketExtension> extensions = new ArrayList();
         String[] parts = extensionHeader.split(",");
         String[] var3 = parts;
         int var4 = parts.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String part = var3[var5];
            String[] items = part.split(";");
            if (items.length > 0) {
               List<Parameter> params = new ArrayList(items.length - 1);
               String name = items[0].trim();

               for(int i = 1; i < items.length; ++i) {
                  if (items[i].contains("=")) {
                     String[] param = items[i].split("=");
                     if (param.length == 2) {
                        params.add(new Parameter(param[0].trim(), param[1].trim()));
                     }
                  } else {
                     params.add(new Parameter(items[i].trim(), (String)null));
                  }
               }

               extensions.add(new WebSocketExtension(name, params));
            }
         }

         return extensions;
      } else {
         return Collections.emptyList();
      }
   }

   public static String toExtensionHeader(List<WebSocketExtension> extensions) {
      StringBuilder extensionsHeader = new StringBuilder();
      if (extensions != null && extensions.size() > 0) {
         Iterator<WebSocketExtension> it = extensions.iterator();

         while(it.hasNext()) {
            WebSocketExtension extension = (WebSocketExtension)it.next();
            extensionsHeader.append(extension.getName());
            Iterator var4 = extension.getParameters().iterator();

            while(var4.hasNext()) {
               Parameter param = (Parameter)var4.next();
               extensionsHeader.append("; ").append(param.getName());
               if (param.getValue() != null && param.getValue().length() > 0) {
                  extensionsHeader.append("=").append(param.getValue());
               }
            }

            if (it.hasNext()) {
               extensionsHeader.append(", ");
            }
         }
      }

      return extensionsHeader.toString();
   }

   public static final class Parameter {
      private final String name;
      private final String value;

      public Parameter(String name, String value) {
         this.name = name;
         this.value = value;
      }

      public String getName() {
         return this.name;
      }

      public String getValue() {
         return this.value;
      }

      public String toString() {
         return "{'" + this.name + '\'' + ": '" + this.value + '\'' + '}';
      }
   }
}
