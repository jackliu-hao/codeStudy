package com.google.protobuf;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

final class MessageLiteToString {
   private static final String LIST_SUFFIX = "List";
   private static final String BUILDER_LIST_SUFFIX = "OrBuilderList";
   private static final String MAP_SUFFIX = "Map";
   private static final String BYTES_SUFFIX = "Bytes";

   static String toString(MessageLite messageLite, String commentString) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("# ").append(commentString);
      reflectivePrintWithIndent(messageLite, buffer, 0);
      return buffer.toString();
   }

   private static void reflectivePrintWithIndent(MessageLite messageLite, StringBuilder buffer, int indent) {
      Map<String, java.lang.reflect.Method> nameToNoArgMethod = new HashMap();
      Map<String, java.lang.reflect.Method> nameToMethod = new HashMap();
      Set<String> getters = new TreeSet();
      java.lang.reflect.Method[] var6 = messageLite.getClass().getDeclaredMethods();
      int var7 = var6.length;

      java.lang.reflect.Method setter;
      for(int var8 = 0; var8 < var7; ++var8) {
         setter = var6[var8];
         nameToMethod.put(setter.getName(), setter);
         if (setter.getParameterTypes().length == 0) {
            nameToNoArgMethod.put(setter.getName(), setter);
            if (setter.getName().startsWith("get")) {
               getters.add(setter.getName());
            }
         }
      }

      Iterator iter = getters.iterator();

      while(iter.hasNext()) {
         String getter = (String)iter.next();
         String suffix = getter.startsWith("get") ? getter.substring(3) : getter;
         java.lang.reflect.Method mapMethod;
         String camelCase;
         if (suffix.endsWith("List") && !suffix.endsWith("OrBuilderList") && !suffix.equals("List")) {
            camelCase = suffix.substring(0, 1).toLowerCase() + suffix.substring(1, suffix.length() - "List".length());
            mapMethod = (java.lang.reflect.Method)nameToNoArgMethod.get(getter);
            if (mapMethod != null && mapMethod.getReturnType().equals(List.class)) {
               printField(buffer, indent, camelCaseToSnakeCase(camelCase), GeneratedMessageLite.invokeOrDie(mapMethod, messageLite));
               continue;
            }
         }

         if (suffix.endsWith("Map") && !suffix.equals("Map")) {
            camelCase = suffix.substring(0, 1).toLowerCase() + suffix.substring(1, suffix.length() - "Map".length());
            mapMethod = (java.lang.reflect.Method)nameToNoArgMethod.get(getter);
            if (mapMethod != null && mapMethod.getReturnType().equals(Map.class) && !mapMethod.isAnnotationPresent(Deprecated.class) && Modifier.isPublic(mapMethod.getModifiers())) {
               printField(buffer, indent, camelCaseToSnakeCase(camelCase), GeneratedMessageLite.invokeOrDie(mapMethod, messageLite));
               continue;
            }
         }

         setter = (java.lang.reflect.Method)nameToMethod.get("set" + suffix);
         if (setter != null && (!suffix.endsWith("Bytes") || !nameToNoArgMethod.containsKey("get" + suffix.substring(0, suffix.length() - "Bytes".length())))) {
            String camelCase = suffix.substring(0, 1).toLowerCase() + suffix.substring(1);
            java.lang.reflect.Method getMethod = (java.lang.reflect.Method)nameToNoArgMethod.get("get" + suffix);
            java.lang.reflect.Method hasMethod = (java.lang.reflect.Method)nameToNoArgMethod.get("has" + suffix);
            if (getMethod != null) {
               Object value = GeneratedMessageLite.invokeOrDie(getMethod, messageLite);
               boolean hasValue = hasMethod == null ? !isDefaultValue(value) : (Boolean)GeneratedMessageLite.invokeOrDie(hasMethod, messageLite);
               if (hasValue) {
                  printField(buffer, indent, camelCaseToSnakeCase(camelCase), value);
               }
            }
         }
      }

      if (messageLite instanceof GeneratedMessageLite.ExtendableMessage) {
         iter = ((GeneratedMessageLite.ExtendableMessage)messageLite).extensions.iterator();

         while(iter.hasNext()) {
            Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object> entry = (Map.Entry)iter.next();
            printField(buffer, indent, "[" + ((GeneratedMessageLite.ExtensionDescriptor)entry.getKey()).getNumber() + "]", entry.getValue());
         }
      }

      if (((GeneratedMessageLite)messageLite).unknownFields != null) {
         ((GeneratedMessageLite)messageLite).unknownFields.printWithIndent(buffer, indent);
      }

   }

   private static boolean isDefaultValue(Object o) {
      if (o instanceof Boolean) {
         return !(Boolean)o;
      } else if (o instanceof Integer) {
         return (Integer)o == 0;
      } else if (o instanceof Float) {
         return (Float)o == 0.0F;
      } else if (o instanceof Double) {
         return (Double)o == 0.0;
      } else if (o instanceof String) {
         return o.equals("");
      } else if (o instanceof ByteString) {
         return o.equals(ByteString.EMPTY);
      } else if (o instanceof MessageLite) {
         return o == ((MessageLite)o).getDefaultInstanceForType();
      } else if (o instanceof java.lang.Enum) {
         return ((java.lang.Enum)o).ordinal() == 0;
      } else {
         return false;
      }
   }

   static final void printField(StringBuilder buffer, int indent, String name, Object object) {
      Iterator var9;
      if (object instanceof List) {
         List<?> list = (List)object;
         var9 = list.iterator();

         while(var9.hasNext()) {
            Object entry = var9.next();
            printField(buffer, indent, name, entry);
         }

      } else if (object instanceof Map) {
         Map<?, ?> map = (Map)object;
         var9 = map.entrySet().iterator();

         while(var9.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry)var9.next();
            printField(buffer, indent, name, entry);
         }

      } else {
         buffer.append('\n');

         int i;
         for(i = 0; i < indent; ++i) {
            buffer.append(' ');
         }

         buffer.append(name);
         if (object instanceof String) {
            buffer.append(": \"").append(TextFormatEscaper.escapeText((String)object)).append('"');
         } else if (object instanceof ByteString) {
            buffer.append(": \"").append(TextFormatEscaper.escapeBytes((ByteString)object)).append('"');
         } else if (object instanceof GeneratedMessageLite) {
            buffer.append(" {");
            reflectivePrintWithIndent((GeneratedMessageLite)object, buffer, indent + 2);
            buffer.append("\n");

            for(i = 0; i < indent; ++i) {
               buffer.append(' ');
            }

            buffer.append("}");
         } else if (object instanceof Map.Entry) {
            buffer.append(" {");
            Map.Entry<?, ?> entry = (Map.Entry)object;
            printField(buffer, indent + 2, "key", entry.getKey());
            printField(buffer, indent + 2, "value", entry.getValue());
            buffer.append("\n");

            for(int i = 0; i < indent; ++i) {
               buffer.append(' ');
            }

            buffer.append("}");
         } else {
            buffer.append(": ").append(object.toString());
         }

      }
   }

   private static final String camelCaseToSnakeCase(String camelCase) {
      StringBuilder builder = new StringBuilder();

      for(int i = 0; i < camelCase.length(); ++i) {
         char ch = camelCase.charAt(i);
         if (Character.isUpperCase(ch)) {
            builder.append("_");
         }

         builder.append(Character.toLowerCase(ch));
      }

      return builder.toString();
   }
}
