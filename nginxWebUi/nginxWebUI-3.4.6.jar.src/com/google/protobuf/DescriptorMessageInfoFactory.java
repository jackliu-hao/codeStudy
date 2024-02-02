/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Stack;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DescriptorMessageInfoFactory
/*     */   implements MessageInfoFactory
/*     */ {
/*     */   private static final String GET_DEFAULT_INSTANCE_METHOD_NAME = "getDefaultInstance";
/*  63 */   private static final DescriptorMessageInfoFactory instance = new DescriptorMessageInfoFactory();
/*  64 */   private static final Set<String> specialFieldNames = new HashSet<>(
/*  65 */       Arrays.asList(new String[] { "cached_size", "serialized_size", "class" }));
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DescriptorMessageInfoFactory getInstance() {
/*  71 */     return instance;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSupported(Class<?> messageType) {
/*  76 */     return GeneratedMessageV3.class.isAssignableFrom(messageType);
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageInfo messageInfoFor(Class<?> messageType) {
/*  81 */     if (!GeneratedMessageV3.class.isAssignableFrom(messageType)) {
/*  82 */       throw new IllegalArgumentException("Unsupported message type: " + messageType.getName());
/*     */     }
/*     */     
/*  85 */     return convert(messageType, descriptorForType(messageType));
/*     */   }
/*     */   
/*     */   private static Message getDefaultInstance(Class<?> messageType) {
/*     */     try {
/*  90 */       Method method = messageType.getDeclaredMethod("getDefaultInstance", new Class[0]);
/*  91 */       return (Message)method.invoke(null, new Object[0]);
/*  92 */     } catch (Exception e) {
/*  93 */       throw new IllegalArgumentException("Unable to get default instance for message class " + messageType
/*  94 */           .getName(), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Descriptors.Descriptor descriptorForType(Class<?> messageType) {
/*  99 */     return getDefaultInstance(messageType).getDescriptorForType();
/*     */   }
/*     */   
/*     */   private static MessageInfo convert(Class<?> messageType, Descriptors.Descriptor messageDescriptor) {
/* 103 */     switch (messageDescriptor.getFile().getSyntax()) {
/*     */       case BOOL:
/* 105 */         return convertProto2(messageType, messageDescriptor);
/*     */       case BYTES:
/* 107 */         return convertProto3(messageType, messageDescriptor);
/*     */     } 
/* 109 */     throw new IllegalArgumentException("Unsupported syntax: " + messageDescriptor
/* 110 */         .getFile().getSyntax());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class IsInitializedCheckAnalyzer
/*     */   {
/* 130 */     private final Map<Descriptors.Descriptor, Boolean> resultCache = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 135 */     private int index = 0;
/* 136 */     private final Stack<Node> stack = new Stack<>();
/* 137 */     private final Map<Descriptors.Descriptor, Node> nodeCache = new HashMap<>();
/*     */     
/*     */     public boolean needsIsInitializedCheck(Descriptors.Descriptor descriptor) {
/* 140 */       Boolean cachedValue = this.resultCache.get(descriptor);
/* 141 */       if (cachedValue != null) {
/* 142 */         return cachedValue.booleanValue();
/*     */       }
/* 144 */       synchronized (this) {
/*     */ 
/*     */         
/* 147 */         cachedValue = this.resultCache.get(descriptor);
/* 148 */         if (cachedValue != null) {
/* 149 */           return cachedValue.booleanValue();
/*     */         }
/* 151 */         return (dfs(descriptor)).component.needsIsInitializedCheck;
/*     */       } 
/*     */     }
/*     */     
/*     */     private static class Node {
/*     */       final Descriptors.Descriptor descriptor;
/*     */       final int index;
/*     */       int lowLink;
/*     */       DescriptorMessageInfoFactory.IsInitializedCheckAnalyzer.StronglyConnectedComponent component;
/*     */       
/*     */       Node(Descriptors.Descriptor descriptor, int index) {
/* 162 */         this.descriptor = descriptor;
/* 163 */         this.index = index;
/* 164 */         this.lowLink = index;
/* 165 */         this.component = null;
/*     */       } }
/*     */     
/*     */     private static class StronglyConnectedComponent { private StronglyConnectedComponent() {}
/*     */       
/* 170 */       final List<Descriptors.Descriptor> messages = new ArrayList<>();
/*     */       boolean needsIsInitializedCheck = false; }
/*     */ 
/*     */     
/*     */     private Node dfs(Descriptors.Descriptor descriptor) {
/* 175 */       Node result = new Node(descriptor, this.index++);
/* 176 */       this.stack.push(result);
/* 177 */       this.nodeCache.put(descriptor, result);
/*     */ 
/*     */       
/* 180 */       for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
/* 181 */         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 182 */           Node child = this.nodeCache.get(field.getMessageType());
/* 183 */           if (child == null) {
/*     */             
/* 185 */             child = dfs(field.getMessageType());
/* 186 */             result.lowLink = Math.min(result.lowLink, child.lowLink); continue;
/*     */           } 
/* 188 */           if (child.component == null)
/*     */           {
/* 190 */             result.lowLink = Math.min(result.lowLink, child.lowLink);
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 196 */       if (result.index == result.lowLink) {
/*     */         Node node;
/* 198 */         StronglyConnectedComponent component = new StronglyConnectedComponent();
/*     */         do {
/* 200 */           node = this.stack.pop();
/* 201 */           node.component = component;
/* 202 */           component.messages.add(node.descriptor);
/* 203 */         } while (node != result);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 208 */         analyze(component);
/*     */       } 
/*     */       
/* 211 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     private void analyze(StronglyConnectedComponent component) {
/* 216 */       boolean needsIsInitializedCheck = false;
/*     */       
/* 218 */       label30: for (Descriptors.Descriptor descriptor : component.messages) {
/* 219 */         if (descriptor.isExtendable()) {
/* 220 */           needsIsInitializedCheck = true;
/*     */           
/*     */           break;
/*     */         } 
/* 224 */         for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
/* 225 */           if (field.isRequired()) {
/* 226 */             needsIsInitializedCheck = true;
/*     */             
/*     */             break label30;
/*     */           } 
/* 230 */           if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/*     */ 
/*     */             
/* 233 */             Node node = this.nodeCache.get(field.getMessageType());
/* 234 */             if (node.component != component && 
/* 235 */               node.component.needsIsInitializedCheck) {
/* 236 */               needsIsInitializedCheck = true;
/*     */               
/*     */               break label30;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 244 */       component.needsIsInitializedCheck = needsIsInitializedCheck;
/*     */       
/* 246 */       for (Descriptors.Descriptor descriptor : component.messages) {
/* 247 */         this.resultCache.put(descriptor, Boolean.valueOf(component.needsIsInitializedCheck));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 252 */   private static IsInitializedCheckAnalyzer isInitializedCheckAnalyzer = new IsInitializedCheckAnalyzer();
/*     */ 
/*     */   
/*     */   private static boolean needsIsInitializedCheck(Descriptors.Descriptor descriptor) {
/* 256 */     return isInitializedCheckAnalyzer.needsIsInitializedCheck(descriptor);
/*     */   }
/*     */ 
/*     */   
/*     */   private static StructuralMessageInfo convertProto2(Class<?> messageType, Descriptors.Descriptor messageDescriptor) {
/* 261 */     List<Descriptors.FieldDescriptor> fieldDescriptors = messageDescriptor.getFields();
/*     */     
/* 263 */     StructuralMessageInfo.Builder builder = StructuralMessageInfo.newBuilder(fieldDescriptors.size());
/* 264 */     builder.withDefaultInstance(getDefaultInstance(messageType));
/* 265 */     builder.withSyntax(ProtoSyntax.PROTO2);
/* 266 */     builder.withMessageSetWireFormat(messageDescriptor.getOptions().getMessageSetWireFormat());
/*     */     
/* 268 */     OneofState oneofState = new OneofState();
/* 269 */     int bitFieldIndex = 0;
/* 270 */     int presenceMask = 1;
/* 271 */     Field bitField = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 277 */     int i = 0; while (true) { if (i < fieldDescriptors.size())
/* 278 */       { final Descriptors.FieldDescriptor fd = fieldDescriptors.get(i);
/* 279 */         boolean enforceUtf8 = fd.getFile().getOptions().getJavaStringCheckUtf8();
/* 280 */         Internal.EnumVerifier enumVerifier = null;
/* 281 */         if (fd.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
/* 282 */           enumVerifier = new Internal.EnumVerifier()
/*     */             {
/*     */               public boolean isInRange(int number)
/*     */               {
/* 286 */                 return (fd.getEnumType().findValueByNumber(number) != null);
/*     */               }
/*     */             };
/*     */         }
/* 290 */         if (fd.getContainingOneof() != null)
/*     */         
/* 292 */         { builder.withField(buildOneofMember(messageType, fd, oneofState, enforceUtf8, enumVerifier)); }
/*     */         else
/* 294 */         { Field field = field(messageType, fd);
/* 295 */           int number = fd.getNumber();
/* 296 */           FieldType type = getFieldType(fd);
/*     */           
/* 298 */           if (fd.isMapField())
/*     */           
/*     */           { 
/*     */ 
/*     */ 
/*     */             
/* 304 */             final Descriptors.FieldDescriptor valueField = fd.getMessageType().findFieldByNumber(2);
/* 305 */             if (valueField.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
/* 306 */               enumVerifier = new Internal.EnumVerifier()
/*     */                 {
/*     */                   public boolean isInRange(int number)
/*     */                   {
/* 310 */                     return (valueField.getEnumType().findValueByNumber(number) != null);
/*     */                   }
/*     */                 };
/*     */             }
/* 314 */             builder.withField(
/* 315 */                 FieldInfo.forMapField(field, number, 
/*     */ 
/*     */                   
/* 318 */                   SchemaUtil.getMapDefaultEntry(messageType, fd.getName()), enumVerifier));
/*     */ 
/*     */             
/*     */              }
/*     */           
/* 323 */           else if (fd.isRepeated())
/*     */           
/* 325 */           { if (enumVerifier != null) {
/* 326 */               if (fd.isPacked()) {
/* 327 */                 builder.withField(
/* 328 */                     FieldInfo.forPackedFieldWithEnumVerifier(field, number, type, enumVerifier, 
/* 329 */                       cachedSizeField(messageType, fd)));
/*     */               } else {
/* 331 */                 builder.withField(FieldInfo.forFieldWithEnumVerifier(field, number, type, enumVerifier));
/*     */               } 
/* 333 */             } else if (fd.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 334 */               builder.withField(
/* 335 */                   FieldInfo.forRepeatedMessageField(field, number, type, 
/* 336 */                     getTypeForRepeatedMessageField(messageType, fd)));
/*     */             }
/* 338 */             else if (fd.isPacked()) {
/* 339 */               builder.withField(
/* 340 */                   FieldInfo.forPackedField(field, number, type, cachedSizeField(messageType, fd)));
/*     */             } else {
/* 342 */               builder.withField(FieldInfo.forField(field, number, type, enforceUtf8));
/*     */             }
/*     */              }
/*     */           
/*     */           else
/*     */           
/* 348 */           { if (bitField == null)
/*     */             {
/* 350 */               bitField = bitField(messageType, bitFieldIndex);
/*     */             }
/*     */ 
/*     */             
/* 354 */             if (fd.isRequired()) {
/* 355 */               builder.withField(
/* 356 */                   FieldInfo.forProto2RequiredField(field, number, type, bitField, presenceMask, enforceUtf8, enumVerifier));
/*     */             } else {
/*     */               
/* 359 */               builder.withField(
/* 360 */                   FieldInfo.forProto2OptionalField(field, number, type, bitField, presenceMask, enforceUtf8, enumVerifier));
/*     */             } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 367 */             presenceMask <<= 1; }  i++; }  } else { break; }  presenceMask <<= 1; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 375 */     List<Integer> fieldsToCheckIsInitialized = new ArrayList<>();
/* 376 */     for (int j = 0; j < fieldDescriptors.size(); j++) {
/* 377 */       final Descriptors.FieldDescriptor fd = fieldDescriptors.get(j);
/* 378 */       if (fd.isRequired() || (fd
/* 379 */         .getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE && 
/* 380 */         needsIsInitializedCheck(fd.getMessageType()))) {
/* 381 */         fieldsToCheckIsInitialized.add(Integer.valueOf(fd.getNumber()));
/*     */       }
/*     */     } 
/* 384 */     int[] numbers = new int[fieldsToCheckIsInitialized.size()];
/* 385 */     for (int k = 0; k < fieldsToCheckIsInitialized.size(); k++) {
/* 386 */       numbers[k] = ((Integer)fieldsToCheckIsInitialized.get(k)).intValue();
/*     */     }
/* 388 */     builder.withCheckInitialized(numbers);
/*     */     
/* 390 */     return builder.build();
/*     */   }
/*     */ 
/*     */   
/*     */   private static StructuralMessageInfo convertProto3(Class<?> messageType, Descriptors.Descriptor messageDescriptor) {
/* 395 */     List<Descriptors.FieldDescriptor> fieldDescriptors = messageDescriptor.getFields();
/*     */     
/* 397 */     StructuralMessageInfo.Builder builder = StructuralMessageInfo.newBuilder(fieldDescriptors.size());
/* 398 */     builder.withDefaultInstance(getDefaultInstance(messageType));
/* 399 */     builder.withSyntax(ProtoSyntax.PROTO3);
/*     */     
/* 401 */     OneofState oneofState = new OneofState();
/* 402 */     boolean enforceUtf8 = true;
/* 403 */     for (int i = 0; i < fieldDescriptors.size(); i++) {
/* 404 */       Descriptors.FieldDescriptor fd = fieldDescriptors.get(i);
/* 405 */       if (fd.getContainingOneof() != null) {
/*     */         
/* 407 */         builder.withField(buildOneofMember(messageType, fd, oneofState, enforceUtf8, null));
/*     */       
/*     */       }
/* 410 */       else if (fd.isMapField()) {
/* 411 */         builder.withField(
/* 412 */             FieldInfo.forMapField(
/* 413 */               field(messageType, fd), fd
/* 414 */               .getNumber(), 
/* 415 */               SchemaUtil.getMapDefaultEntry(messageType, fd.getName()), null));
/*     */ 
/*     */       
/*     */       }
/* 419 */       else if (fd.isRepeated() && fd.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 420 */         builder.withField(
/* 421 */             FieldInfo.forRepeatedMessageField(
/* 422 */               field(messageType, fd), fd
/* 423 */               .getNumber(), 
/* 424 */               getFieldType(fd), 
/* 425 */               getTypeForRepeatedMessageField(messageType, fd)));
/*     */       
/*     */       }
/* 428 */       else if (fd.isPacked()) {
/* 429 */         builder.withField(
/* 430 */             FieldInfo.forPackedField(
/* 431 */               field(messageType, fd), fd
/* 432 */               .getNumber(), 
/* 433 */               getFieldType(fd), 
/* 434 */               cachedSizeField(messageType, fd)));
/*     */       } else {
/* 436 */         builder.withField(
/* 437 */             FieldInfo.forField(field(messageType, fd), fd.getNumber(), getFieldType(fd), enforceUtf8));
/*     */       } 
/*     */     } 
/*     */     
/* 441 */     return builder.build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static FieldInfo buildOneofMember(Class<?> messageType, Descriptors.FieldDescriptor fd, OneofState oneofState, boolean enforceUtf8, Internal.EnumVerifier enumVerifier) {
/* 451 */     OneofInfo oneof = oneofState.getOneof(messageType, fd.getContainingOneof());
/* 452 */     FieldType type = getFieldType(fd);
/* 453 */     Class<?> oneofStoredType = getOneofStoredType(messageType, fd, type);
/* 454 */     return FieldInfo.forOneofMemberField(fd
/* 455 */         .getNumber(), type, oneof, oneofStoredType, enforceUtf8, enumVerifier);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Class<?> getOneofStoredType(Class<?> messageType, Descriptors.FieldDescriptor fd, FieldType type) {
/* 460 */     switch (type.getJavaType()) {
/*     */       case BOOL:
/* 462 */         return Boolean.class;
/*     */       case BYTES:
/* 464 */         return ByteString.class;
/*     */       case DOUBLE:
/* 466 */         return Double.class;
/*     */       case ENUM:
/* 468 */         return Float.class;
/*     */       case FIXED32:
/*     */       case FIXED64:
/* 471 */         return Integer.class;
/*     */       case FLOAT:
/* 473 */         return Long.class;
/*     */       case GROUP:
/* 475 */         return String.class;
/*     */       case INT32:
/* 477 */         return getOneofStoredTypeForMessage(messageType, fd);
/*     */     } 
/* 479 */     throw new IllegalArgumentException("Invalid type for oneof: " + type);
/*     */   }
/*     */ 
/*     */   
/*     */   private static FieldType getFieldType(Descriptors.FieldDescriptor fd) {
/* 484 */     switch (fd.getType()) {
/*     */       case BOOL:
/* 486 */         if (!fd.isRepeated()) {
/* 487 */           return FieldType.BOOL;
/*     */         }
/* 489 */         return fd.isPacked() ? FieldType.BOOL_LIST_PACKED : FieldType.BOOL_LIST;
/*     */       case BYTES:
/* 491 */         return fd.isRepeated() ? FieldType.BYTES_LIST : FieldType.BYTES;
/*     */       case DOUBLE:
/* 493 */         if (!fd.isRepeated()) {
/* 494 */           return FieldType.DOUBLE;
/*     */         }
/* 496 */         return fd.isPacked() ? FieldType.DOUBLE_LIST_PACKED : FieldType.DOUBLE_LIST;
/*     */       case ENUM:
/* 498 */         if (!fd.isRepeated()) {
/* 499 */           return FieldType.ENUM;
/*     */         }
/* 501 */         return fd.isPacked() ? FieldType.ENUM_LIST_PACKED : FieldType.ENUM_LIST;
/*     */       case FIXED32:
/* 503 */         if (!fd.isRepeated()) {
/* 504 */           return FieldType.FIXED32;
/*     */         }
/* 506 */         return fd.isPacked() ? FieldType.FIXED32_LIST_PACKED : FieldType.FIXED32_LIST;
/*     */       case FIXED64:
/* 508 */         if (!fd.isRepeated()) {
/* 509 */           return FieldType.FIXED64;
/*     */         }
/* 511 */         return fd.isPacked() ? FieldType.FIXED64_LIST_PACKED : FieldType.FIXED64_LIST;
/*     */       case FLOAT:
/* 513 */         if (!fd.isRepeated()) {
/* 514 */           return FieldType.FLOAT;
/*     */         }
/* 516 */         return fd.isPacked() ? FieldType.FLOAT_LIST_PACKED : FieldType.FLOAT_LIST;
/*     */       case GROUP:
/* 518 */         return fd.isRepeated() ? FieldType.GROUP_LIST : FieldType.GROUP;
/*     */       case INT32:
/* 520 */         if (!fd.isRepeated()) {
/* 521 */           return FieldType.INT32;
/*     */         }
/* 523 */         return fd.isPacked() ? FieldType.INT32_LIST_PACKED : FieldType.INT32_LIST;
/*     */       case INT64:
/* 525 */         if (!fd.isRepeated()) {
/* 526 */           return FieldType.INT64;
/*     */         }
/* 528 */         return fd.isPacked() ? FieldType.INT64_LIST_PACKED : FieldType.INT64_LIST;
/*     */       case MESSAGE:
/* 530 */         if (fd.isMapField()) {
/* 531 */           return FieldType.MAP;
/*     */         }
/* 533 */         return fd.isRepeated() ? FieldType.MESSAGE_LIST : FieldType.MESSAGE;
/*     */       case SFIXED32:
/* 535 */         if (!fd.isRepeated()) {
/* 536 */           return FieldType.SFIXED32;
/*     */         }
/* 538 */         return fd.isPacked() ? FieldType.SFIXED32_LIST_PACKED : FieldType.SFIXED32_LIST;
/*     */       case SFIXED64:
/* 540 */         if (!fd.isRepeated()) {
/* 541 */           return FieldType.SFIXED64;
/*     */         }
/* 543 */         return fd.isPacked() ? FieldType.SFIXED64_LIST_PACKED : FieldType.SFIXED64_LIST;
/*     */       case SINT32:
/* 545 */         if (!fd.isRepeated()) {
/* 546 */           return FieldType.SINT32;
/*     */         }
/* 548 */         return fd.isPacked() ? FieldType.SINT32_LIST_PACKED : FieldType.SINT32_LIST;
/*     */       case SINT64:
/* 550 */         if (!fd.isRepeated()) {
/* 551 */           return FieldType.SINT64;
/*     */         }
/* 553 */         return fd.isPacked() ? FieldType.SINT64_LIST_PACKED : FieldType.SINT64_LIST;
/*     */       case STRING:
/* 555 */         return fd.isRepeated() ? FieldType.STRING_LIST : FieldType.STRING;
/*     */       case UINT32:
/* 557 */         if (!fd.isRepeated()) {
/* 558 */           return FieldType.UINT32;
/*     */         }
/* 560 */         return fd.isPacked() ? FieldType.UINT32_LIST_PACKED : FieldType.UINT32_LIST;
/*     */       case UINT64:
/* 562 */         if (!fd.isRepeated()) {
/* 563 */           return FieldType.UINT64;
/*     */         }
/* 565 */         return fd.isPacked() ? FieldType.UINT64_LIST_PACKED : FieldType.UINT64_LIST;
/*     */     } 
/* 567 */     throw new IllegalArgumentException("Unsupported field type: " + fd.getType());
/*     */   }
/*     */ 
/*     */   
/*     */   private static Field bitField(Class<?> messageType, int index) {
/* 572 */     return field(messageType, "bitField" + index + "_");
/*     */   }
/*     */   
/*     */   private static Field field(Class<?> messageType, Descriptors.FieldDescriptor fd) {
/* 576 */     return field(messageType, getFieldName(fd));
/*     */   }
/*     */   
/*     */   private static Field cachedSizeField(Class<?> messageType, Descriptors.FieldDescriptor fd) {
/* 580 */     return field(messageType, getCachedSizeFieldName(fd));
/*     */   }
/*     */   
/*     */   private static Field field(Class<?> messageType, String fieldName) {
/*     */     try {
/* 585 */       return messageType.getDeclaredField(fieldName);
/* 586 */     } catch (Exception e) {
/* 587 */       throw new IllegalArgumentException("Unable to find field " + fieldName + " in message class " + messageType
/* 588 */           .getName());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static String getFieldName(Descriptors.FieldDescriptor fd) {
/* 595 */     String name = (fd.getType() == Descriptors.FieldDescriptor.Type.GROUP) ? fd.getMessageType().getName() : fd.getName();
/* 596 */     String suffix = specialFieldNames.contains(name) ? "__" : "_";
/* 597 */     return snakeCaseToCamelCase(name) + suffix;
/*     */   }
/*     */   
/*     */   private static String getCachedSizeFieldName(Descriptors.FieldDescriptor fd) {
/* 601 */     return snakeCaseToCamelCase(fd.getName()) + "MemoizedSerializedSize";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String snakeCaseToCamelCase(String snakeCase) {
/* 609 */     StringBuilder sb = new StringBuilder(snakeCase.length() + 1);
/* 610 */     boolean capNext = false;
/* 611 */     for (int ctr = 0; ctr < snakeCase.length(); ctr++) {
/* 612 */       char next = snakeCase.charAt(ctr);
/* 613 */       if (next == '_') {
/* 614 */         capNext = true;
/* 615 */       } else if (Character.isDigit(next)) {
/* 616 */         sb.append(next);
/* 617 */         capNext = true;
/* 618 */       } else if (capNext) {
/* 619 */         sb.append(Character.toUpperCase(next));
/* 620 */         capNext = false;
/* 621 */       } else if (ctr == 0) {
/* 622 */         sb.append(Character.toLowerCase(next));
/*     */       } else {
/* 624 */         sb.append(next);
/*     */       } 
/*     */     } 
/* 627 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Class<?> getOneofStoredTypeForMessage(Class<?> messageType, Descriptors.FieldDescriptor fd) {
/*     */     try {
/* 635 */       String name = (fd.getType() == Descriptors.FieldDescriptor.Type.GROUP) ? fd.getMessageType().getName() : fd.getName();
/* 636 */       Method getter = messageType.getDeclaredMethod(getterForField(name), new Class[0]);
/* 637 */       return getter.getReturnType();
/* 638 */     } catch (Exception e) {
/* 639 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static Class<?> getTypeForRepeatedMessageField(Class<?> messageType, Descriptors.FieldDescriptor fd) {
/*     */     try {
/* 646 */       String name = (fd.getType() == Descriptors.FieldDescriptor.Type.GROUP) ? fd.getMessageType().getName() : fd.getName();
/* 647 */       Method getter = messageType.getDeclaredMethod(getterForField(name), new Class[] { int.class });
/* 648 */       return getter.getReturnType();
/* 649 */     } catch (Exception e) {
/* 650 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static String getterForField(String snakeCase) {
/* 656 */     String camelCase = snakeCaseToCamelCase(snakeCase);
/* 657 */     StringBuilder builder = new StringBuilder("get");
/*     */     
/* 659 */     builder.append(Character.toUpperCase(camelCase.charAt(0)));
/* 660 */     builder.append(camelCase.substring(1, camelCase.length()));
/* 661 */     return builder.toString();
/*     */   }
/*     */   
/*     */   private static final class OneofState {
/* 665 */     private OneofInfo[] oneofs = new OneofInfo[2];
/*     */     
/*     */     OneofInfo getOneof(Class<?> messageType, Descriptors.OneofDescriptor desc) {
/* 668 */       int index = desc.getIndex();
/* 669 */       if (index >= this.oneofs.length)
/*     */       {
/* 671 */         this.oneofs = Arrays.<OneofInfo>copyOf(this.oneofs, index * 2);
/*     */       }
/* 673 */       OneofInfo info = this.oneofs[index];
/* 674 */       if (info == null) {
/* 675 */         info = newInfo(messageType, desc);
/* 676 */         this.oneofs[index] = info;
/*     */       } 
/* 678 */       return info;
/*     */     }
/*     */     
/*     */     private static OneofInfo newInfo(Class<?> messageType, Descriptors.OneofDescriptor desc) {
/* 682 */       String camelCase = DescriptorMessageInfoFactory.snakeCaseToCamelCase(desc.getName());
/* 683 */       String valueFieldName = camelCase + "_";
/* 684 */       String caseFieldName = camelCase + "Case_";
/*     */       
/* 686 */       return new OneofInfo(desc
/* 687 */           .getIndex(), DescriptorMessageInfoFactory.field(messageType, caseFieldName), DescriptorMessageInfoFactory.field(messageType, valueFieldName));
/*     */     }
/*     */     
/*     */     private OneofState() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\DescriptorMessageInfoFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */