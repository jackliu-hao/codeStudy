/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MessageLiteToString
/*     */ {
/*     */   private static final String LIST_SUFFIX = "List";
/*     */   private static final String BUILDER_LIST_SUFFIX = "OrBuilderList";
/*     */   private static final String MAP_SUFFIX = "Map";
/*     */   private static final String BYTES_SUFFIX = "Bytes";
/*     */   
/*     */   static String toString(MessageLite messageLite, String commentString) {
/*  59 */     StringBuilder buffer = new StringBuilder();
/*  60 */     buffer.append("# ").append(commentString);
/*  61 */     reflectivePrintWithIndent(messageLite, buffer, 0);
/*  62 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void reflectivePrintWithIndent(MessageLite messageLite, StringBuilder buffer, int indent) {
/*  76 */     Map<String, Method> nameToNoArgMethod = new HashMap<>();
/*  77 */     Map<String, Method> nameToMethod = new HashMap<>();
/*  78 */     Set<String> getters = new TreeSet<>();
/*  79 */     for (Method method : messageLite.getClass().getDeclaredMethods()) {
/*  80 */       nameToMethod.put(method.getName(), method);
/*  81 */       if ((method.getParameterTypes()).length == 0) {
/*  82 */         nameToNoArgMethod.put(method.getName(), method);
/*     */         
/*  84 */         if (method.getName().startsWith("get")) {
/*  85 */           getters.add(method.getName());
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  90 */     for (String getter : getters) {
/*  91 */       String suffix = getter.startsWith("get") ? getter.substring(3) : getter;
/*  92 */       if (suffix.endsWith("List") && 
/*  93 */         !suffix.endsWith("OrBuilderList") && 
/*     */         
/*  95 */         !suffix.equals("List")) {
/*     */ 
/*     */         
/*  98 */         String str = suffix.substring(0, 1).toLowerCase() + suffix.substring(1, suffix.length() - "List".length());
/*     */ 
/*     */         
/* 101 */         Method listMethod = nameToNoArgMethod.get(getter);
/* 102 */         if (listMethod != null && listMethod.getReturnType().equals(List.class)) {
/* 103 */           printField(buffer, indent, 
/*     */ 
/*     */               
/* 106 */               camelCaseToSnakeCase(str), 
/* 107 */               GeneratedMessageLite.invokeOrDie(listMethod, messageLite, new Object[0]));
/*     */           continue;
/*     */         } 
/*     */       } 
/* 111 */       if (suffix.endsWith("Map") && 
/*     */         
/* 113 */         !suffix.equals("Map")) {
/*     */ 
/*     */         
/* 116 */         String str = suffix.substring(0, 1).toLowerCase() + suffix.substring(1, suffix.length() - "Map".length());
/*     */ 
/*     */         
/* 119 */         Method mapMethod = nameToNoArgMethod.get(getter);
/* 120 */         if (mapMethod != null && mapMethod
/* 121 */           .getReturnType().equals(Map.class) && 
/*     */ 
/*     */           
/* 124 */           !mapMethod.isAnnotationPresent((Class)Deprecated.class) && 
/*     */           
/* 126 */           Modifier.isPublic(mapMethod.getModifiers())) {
/* 127 */           printField(buffer, indent, 
/*     */ 
/*     */               
/* 130 */               camelCaseToSnakeCase(str), 
/* 131 */               GeneratedMessageLite.invokeOrDie(mapMethod, messageLite, new Object[0]));
/*     */           
/*     */           continue;
/*     */         } 
/*     */       } 
/* 136 */       Method setter = nameToMethod.get("set" + suffix);
/* 137 */       if (setter == null) {
/*     */         continue;
/*     */       }
/* 140 */       if (suffix.endsWith("Bytes")) if (nameToNoArgMethod
/* 141 */           .containsKey("get" + suffix
/* 142 */             .substring(0, suffix.length() - "Bytes".length()))) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */       
/* 147 */       String camelCase = suffix.substring(0, 1).toLowerCase() + suffix.substring(1);
/*     */ 
/*     */ 
/*     */       
/* 151 */       Method getMethod = nameToNoArgMethod.get("get" + suffix);
/* 152 */       Method hasMethod = nameToNoArgMethod.get("has" + suffix);
/*     */       
/* 154 */       if (getMethod != null) {
/* 155 */         Object value = GeneratedMessageLite.invokeOrDie(getMethod, messageLite, new Object[0]);
/*     */ 
/*     */ 
/*     */         
/* 159 */         boolean hasValue = (hasMethod == null) ? (!isDefaultValue(value)) : ((Boolean)GeneratedMessageLite.invokeOrDie(hasMethod, messageLite, new Object[0])).booleanValue();
/*     */         
/* 161 */         if (hasValue) {
/* 162 */           printField(buffer, indent, camelCaseToSnakeCase(camelCase), value);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 168 */     if (messageLite instanceof GeneratedMessageLite.ExtendableMessage) {
/*     */       
/* 170 */       Iterator<Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object>> iter = ((GeneratedMessageLite.ExtendableMessage)messageLite).extensions.iterator();
/* 171 */       while (iter.hasNext()) {
/* 172 */         Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object> entry = iter.next();
/* 173 */         printField(buffer, indent, "[" + ((GeneratedMessageLite.ExtensionDescriptor)entry.getKey()).getNumber() + "]", entry.getValue());
/*     */       } 
/*     */     } 
/*     */     
/* 177 */     if (((GeneratedMessageLite)messageLite).unknownFields != null) {
/* 178 */       ((GeneratedMessageLite)messageLite).unknownFields.printWithIndent(buffer, indent);
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isDefaultValue(Object o) {
/* 183 */     if (o instanceof Boolean) {
/* 184 */       return !((Boolean)o).booleanValue();
/*     */     }
/* 186 */     if (o instanceof Integer) {
/* 187 */       return (((Integer)o).intValue() == 0);
/*     */     }
/* 189 */     if (o instanceof Float) {
/* 190 */       return (((Float)o).floatValue() == 0.0F);
/*     */     }
/* 192 */     if (o instanceof Double) {
/* 193 */       return (((Double)o).doubleValue() == 0.0D);
/*     */     }
/* 195 */     if (o instanceof String) {
/* 196 */       return o.equals("");
/*     */     }
/* 198 */     if (o instanceof ByteString) {
/* 199 */       return o.equals(ByteString.EMPTY);
/*     */     }
/* 201 */     if (o instanceof MessageLite) {
/* 202 */       return (o == ((MessageLite)o).getDefaultInstanceForType());
/*     */     }
/* 204 */     if (o instanceof java.lang.Enum) {
/* 205 */       return (((java.lang.Enum)o).ordinal() == 0);
/*     */     }
/*     */     
/* 208 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final void printField(StringBuilder buffer, int indent, String name, Object object) {
/* 222 */     if (object instanceof List) {
/* 223 */       List<?> list = (List)object;
/* 224 */       for (Object entry : list) {
/* 225 */         printField(buffer, indent, name, entry);
/*     */       }
/*     */       return;
/*     */     } 
/* 229 */     if (object instanceof Map) {
/* 230 */       Map<?, ?> map = (Map<?, ?>)object;
/* 231 */       for (Map.Entry<?, ?> entry : map.entrySet()) {
/* 232 */         printField(buffer, indent, name, entry);
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/* 237 */     buffer.append('\n'); int i;
/* 238 */     for (i = 0; i < indent; i++) {
/* 239 */       buffer.append(' ');
/*     */     }
/* 241 */     buffer.append(name);
/*     */     
/* 243 */     if (object instanceof String) {
/* 244 */       buffer.append(": \"").append(TextFormatEscaper.escapeText((String)object)).append('"');
/* 245 */     } else if (object instanceof ByteString) {
/* 246 */       buffer.append(": \"").append(TextFormatEscaper.escapeBytes((ByteString)object)).append('"');
/* 247 */     } else if (object instanceof GeneratedMessageLite) {
/* 248 */       buffer.append(" {");
/* 249 */       reflectivePrintWithIndent((GeneratedMessageLite)object, buffer, indent + 2);
/* 250 */       buffer.append("\n");
/* 251 */       for (i = 0; i < indent; i++) {
/* 252 */         buffer.append(' ');
/*     */       }
/* 254 */       buffer.append("}");
/* 255 */     } else if (object instanceof Map.Entry) {
/* 256 */       buffer.append(" {");
/* 257 */       Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
/* 258 */       printField(buffer, indent + 2, "key", entry.getKey());
/* 259 */       printField(buffer, indent + 2, "value", entry.getValue());
/* 260 */       buffer.append("\n");
/* 261 */       for (int j = 0; j < indent; j++) {
/* 262 */         buffer.append(' ');
/*     */       }
/* 264 */       buffer.append("}");
/*     */     } else {
/* 266 */       buffer.append(": ").append(object.toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final String camelCaseToSnakeCase(String camelCase) {
/* 271 */     StringBuilder builder = new StringBuilder();
/* 272 */     for (int i = 0; i < camelCase.length(); i++) {
/* 273 */       char ch = camelCase.charAt(i);
/* 274 */       if (Character.isUpperCase(ch)) {
/* 275 */         builder.append("_");
/*     */       }
/* 277 */       builder.append(Character.toLowerCase(ch));
/*     */     } 
/* 279 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MessageLiteToString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */