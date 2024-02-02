/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectStreamException;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class GeneratedMessageLite<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends GeneratedMessageLite.Builder<MessageType, BuilderType>>
/*      */   extends AbstractMessageLite<MessageType, BuilderType>
/*      */ {
/*   66 */   protected UnknownFieldSetLite unknownFields = UnknownFieldSetLite.getDefaultInstance();
/*      */ 
/*      */   
/*   69 */   protected int memoizedSerializedSize = -1;
/*      */ 
/*      */ 
/*      */   
/*      */   public final Parser<MessageType> getParserForType() {
/*   74 */     return (Parser<MessageType>)dynamicMethod(MethodToInvoke.GET_PARSER);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final MessageType getDefaultInstanceForType() {
/*   80 */     return (MessageType)dynamicMethod(MethodToInvoke.GET_DEFAULT_INSTANCE);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final BuilderType newBuilderForType() {
/*   86 */     return (BuilderType)dynamicMethod(MethodToInvoke.NEW_BUILDER);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  103 */     return MessageLiteToString.toString(this, super.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  109 */     if (this.memoizedHashCode != 0) {
/*  110 */       return this.memoizedHashCode;
/*      */     }
/*  112 */     this.memoizedHashCode = Protobuf.getInstance().<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this).hashCode(this);
/*  113 */     return this.memoizedHashCode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object other) {
/*  120 */     if (this == other) {
/*  121 */       return true;
/*      */     }
/*      */     
/*  124 */     if (!getDefaultInstanceForType().getClass().isInstance(other)) {
/*  125 */       return false;
/*      */     }
/*      */     
/*  128 */     return Protobuf.getInstance().<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this).equals(this, (GeneratedMessageLite<MessageType, BuilderType>)other);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void ensureUnknownFieldsInitialized() {
/*  137 */     if (this.unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
/*  138 */       this.unknownFields = UnknownFieldSetLite.newInstance();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean parseUnknownField(int tag, CodedInputStream input) throws IOException {
/*  149 */     if (WireFormat.getTagWireType(tag) == 4) {
/*  150 */       return false;
/*      */     }
/*      */     
/*  153 */     ensureUnknownFieldsInitialized();
/*  154 */     return this.unknownFields.mergeFieldFrom(tag, input);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeVarintField(int tag, int value) {
/*  159 */     ensureUnknownFieldsInitialized();
/*  160 */     this.unknownFields.mergeVarintField(tag, value);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeLengthDelimitedField(int fieldNumber, ByteString value) {
/*  165 */     ensureUnknownFieldsInitialized();
/*  166 */     this.unknownFields.mergeLengthDelimitedField(fieldNumber, value);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void makeImmutable() {
/*  171 */     Protobuf.getInstance().<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this).makeImmutable(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final <MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> BuilderType createBuilder() {
/*  178 */     return (BuilderType)dynamicMethod(MethodToInvoke.NEW_BUILDER);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final <MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> BuilderType createBuilder(MessageType prototype) {
/*  185 */     return createBuilder().mergeFrom(prototype);
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean isInitialized() {
/*  190 */     return isInitialized(this, Boolean.TRUE.booleanValue());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final BuilderType toBuilder() {
/*  196 */     Builder builder = (Builder)dynamicMethod(MethodToInvoke.NEW_BUILDER);
/*  197 */     builder.mergeFrom(this);
/*  198 */     return (BuilderType)builder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public enum MethodToInvoke
/*      */   {
/*  209 */     GET_MEMOIZED_IS_INITIALIZED,
/*  210 */     SET_MEMOIZED_IS_INITIALIZED,
/*      */ 
/*      */     
/*  213 */     BUILD_MESSAGE_INFO,
/*  214 */     NEW_MUTABLE_INSTANCE,
/*  215 */     NEW_BUILDER,
/*  216 */     GET_DEFAULT_INSTANCE,
/*  217 */     GET_PARSER;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object dynamicMethod(MethodToInvoke method, Object arg0) {
/*  247 */     return dynamicMethod(method, arg0, (Object)null);
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object dynamicMethod(MethodToInvoke method) {
/*  252 */     return dynamicMethod(method, (Object)null, (Object)null);
/*      */   }
/*      */ 
/*      */   
/*      */   int getMemoizedSerializedSize() {
/*  257 */     return this.memoizedSerializedSize;
/*      */   }
/*      */ 
/*      */   
/*      */   void setMemoizedSerializedSize(int size) {
/*  262 */     this.memoizedSerializedSize = size;
/*      */   }
/*      */   
/*      */   public void writeTo(CodedOutputStream output) throws IOException {
/*  266 */     Protobuf.getInstance()
/*  267 */       .<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this)
/*  268 */       .writeTo(this, CodedOutputStreamWriter.forCodedOutput(output));
/*      */   }
/*      */   
/*      */   public int getSerializedSize() {
/*  272 */     if (this.memoizedSerializedSize == -1) {
/*  273 */       this.memoizedSerializedSize = Protobuf.getInstance().<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this).getSerializedSize(this);
/*      */     }
/*  275 */     return this.memoizedSerializedSize;
/*      */   }
/*      */ 
/*      */   
/*      */   Object buildMessageInfo() throws Exception {
/*  280 */     return dynamicMethod(MethodToInvoke.BUILD_MESSAGE_INFO);
/*      */   }
/*      */   
/*  283 */   private static Map<Object, GeneratedMessageLite<?, ?>> defaultInstanceMap = new ConcurrentHashMap<>();
/*      */ 
/*      */ 
/*      */   
/*      */   static <T extends GeneratedMessageLite<?, ?>> T getDefaultInstance(Class<T> clazz) {
/*  288 */     GeneratedMessageLite<?, ?> generatedMessageLite = defaultInstanceMap.get(clazz);
/*  289 */     if (generatedMessageLite == null) {
/*      */ 
/*      */       
/*      */       try {
/*  293 */         Class.forName(clazz.getName(), true, clazz.getClassLoader());
/*  294 */       } catch (ClassNotFoundException e) {
/*  295 */         throw new IllegalStateException("Class initialization cannot fail.", e);
/*      */       } 
/*  297 */       generatedMessageLite = defaultInstanceMap.get(clazz);
/*      */     } 
/*  299 */     if (generatedMessageLite == null) {
/*      */ 
/*      */       
/*  302 */       generatedMessageLite = (GeneratedMessageLite<?, ?>)((GeneratedMessageLite)UnsafeUtil.<GeneratedMessageLite>allocateInstance(clazz)).getDefaultInstanceForType();
/*      */       
/*  304 */       if (generatedMessageLite == null) {
/*  305 */         throw new IllegalStateException();
/*      */       }
/*  307 */       defaultInstanceMap.put(clazz, generatedMessageLite);
/*      */     } 
/*  309 */     return (T)generatedMessageLite;
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<?, ?>> void registerDefaultInstance(Class<T> clazz, T defaultInstance) {
/*  314 */     defaultInstanceMap.put(clazz, (GeneratedMessageLite<?, ?>)defaultInstance);
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Object newMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
/*  319 */     return new RawMessageInfo(defaultInstance, info, objects);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void mergeUnknownFields(UnknownFieldSetLite unknownFields) {
/*  328 */     this.unknownFields = UnknownFieldSetLite.mutableCopyOf(this.unknownFields, unknownFields);
/*      */   }
/*      */ 
/*      */   
/*      */   public static abstract class Builder<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>>
/*      */     extends AbstractMessageLite.Builder<MessageType, BuilderType>
/*      */   {
/*      */     private final MessageType defaultInstance;
/*      */     
/*      */     protected MessageType instance;
/*      */     
/*      */     protected boolean isBuilt;
/*      */     
/*      */     protected Builder(MessageType defaultInstance) {
/*  342 */       this.defaultInstance = defaultInstance;
/*  343 */       this
/*  344 */         .instance = (MessageType)defaultInstance.dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE);
/*  345 */       this.isBuilt = false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final void copyOnWrite() {
/*  353 */       if (this.isBuilt) {
/*  354 */         copyOnWriteInternal();
/*  355 */         this.isBuilt = false;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     protected void copyOnWriteInternal() {
/*  361 */       GeneratedMessageLite generatedMessageLite = (GeneratedMessageLite)this.instance.dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE);
/*  362 */       mergeFromInstance((MessageType)generatedMessageLite, this.instance);
/*  363 */       this.instance = (MessageType)generatedMessageLite;
/*      */     }
/*      */ 
/*      */     
/*      */     public final boolean isInitialized() {
/*  368 */       return GeneratedMessageLite.isInitialized(this.instance, false);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final BuilderType clear() {
/*  374 */       this.instance = (MessageType)this.instance.dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE);
/*  375 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType clone() {
/*  380 */       BuilderType builder = (BuilderType)getDefaultInstanceForType().newBuilderForType();
/*  381 */       builder.mergeFrom(buildPartial());
/*  382 */       return builder;
/*      */     }
/*      */ 
/*      */     
/*      */     public MessageType buildPartial() {
/*  387 */       if (this.isBuilt) {
/*  388 */         return this.instance;
/*      */       }
/*      */       
/*  391 */       this.instance.makeImmutable();
/*      */       
/*  393 */       this.isBuilt = true;
/*  394 */       return this.instance;
/*      */     }
/*      */ 
/*      */     
/*      */     public final MessageType build() {
/*  399 */       MessageType result = buildPartial();
/*  400 */       if (!result.isInitialized()) {
/*  401 */         throw newUninitializedMessageException(result);
/*      */       }
/*  403 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     protected BuilderType internalMergeFrom(MessageType message) {
/*  408 */       return mergeFrom(message);
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType mergeFrom(MessageType message) {
/*  413 */       copyOnWrite();
/*  414 */       mergeFromInstance(this.instance, message);
/*  415 */       return (BuilderType)this;
/*      */     }
/*      */     
/*      */     private void mergeFromInstance(MessageType dest, MessageType src) {
/*  419 */       Protobuf.getInstance().<MessageType>schemaFor(dest).mergeFrom(dest, src);
/*      */     }
/*      */ 
/*      */     
/*      */     public MessageType getDefaultInstanceForType() {
/*  424 */       return this.defaultInstance;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType mergeFrom(byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  431 */       copyOnWrite();
/*      */       try {
/*  433 */         Protobuf.getInstance().<MessageType>schemaFor(this.instance).mergeFrom(this.instance, input, offset, offset + length, new ArrayDecoders.Registers(extensionRegistry));
/*      */       
/*      */       }
/*  436 */       catch (InvalidProtocolBufferException e) {
/*  437 */         throw e;
/*  438 */       } catch (IndexOutOfBoundsException e) {
/*  439 */         throw InvalidProtocolBufferException.truncatedMessage();
/*  440 */       } catch (IOException e) {
/*  441 */         throw new RuntimeException("Reading from byte array should not throw IOException.", e);
/*      */       } 
/*  443 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType mergeFrom(byte[] input, int offset, int length) throws InvalidProtocolBufferException {
/*  450 */       return mergeFrom(input, offset, length, ExtensionRegistryLite.getEmptyRegistry());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  458 */       copyOnWrite();
/*      */ 
/*      */       
/*      */       try {
/*  462 */         Protobuf.getInstance().<MessageType>schemaFor(this.instance).mergeFrom(this.instance, 
/*  463 */             CodedInputStreamReader.forCodedInput(input), extensionRegistry);
/*  464 */       } catch (RuntimeException e) {
/*  465 */         if (e.getCause() instanceof IOException) {
/*  466 */           throw (IOException)e.getCause();
/*      */         }
/*  468 */         throw e;
/*      */       } 
/*  470 */       return (BuilderType)this;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class ExtendableMessage<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>>
/*      */     extends GeneratedMessageLite<MessageType, BuilderType>
/*      */     implements ExtendableMessageOrBuilder<MessageType, BuilderType>
/*      */   {
/*  505 */     protected FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = FieldSet.emptySet();
/*      */ 
/*      */     
/*      */     protected final void mergeExtensionFields(MessageType other) {
/*  509 */       if (this.extensions.isImmutable()) {
/*  510 */         this.extensions = this.extensions.clone();
/*      */       }
/*  512 */       this.extensions.mergeFrom(((ExtendableMessage)other).extensions);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected <MessageType extends MessageLite> boolean parseUnknownField(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/*  528 */       int fieldNumber = WireFormat.getTagFieldNumber(tag);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  533 */       GeneratedMessageLite.GeneratedExtension<MessageType, ?> extension = extensionRegistry.findLiteExtensionByNumber(defaultInstance, fieldNumber);
/*      */       
/*  535 */       return parseExtension(input, extensionRegistry, extension, tag, fieldNumber);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean parseExtension(CodedInputStream input, ExtensionRegistryLite extensionRegistry, GeneratedMessageLite.GeneratedExtension<?, ?> extension, int tag, int fieldNumber) throws IOException {
/*  545 */       int wireType = WireFormat.getTagWireType(tag);
/*  546 */       boolean unknown = false;
/*  547 */       boolean packed = false;
/*  548 */       if (extension == null) {
/*  549 */         unknown = true;
/*  550 */       } else if (wireType == 
/*  551 */         FieldSet.getWireFormatForFieldType(extension.descriptor
/*  552 */           .getLiteType(), false)) {
/*  553 */         packed = false;
/*  554 */       } else if (extension.descriptor.isRepeated && extension.descriptor.type
/*  555 */         .isPackable() && wireType == 
/*      */         
/*  557 */         FieldSet.getWireFormatForFieldType(extension.descriptor
/*  558 */           .getLiteType(), true)) {
/*  559 */         packed = true;
/*      */       } else {
/*  561 */         unknown = true;
/*      */       } 
/*      */       
/*  564 */       if (unknown) {
/*  565 */         return parseUnknownField(tag, input);
/*      */       }
/*      */       
/*  568 */       ensureExtensionsAreMutable();
/*      */       
/*  570 */       if (packed) {
/*  571 */         int length = input.readRawVarint32();
/*  572 */         int limit = input.pushLimit(length);
/*  573 */         if (extension.descriptor.getLiteType() == WireFormat.FieldType.ENUM) {
/*  574 */           while (input.getBytesUntilLimit() > 0) {
/*  575 */             int rawValue = input.readEnum();
/*  576 */             Object value = extension.descriptor.getEnumType().findValueByNumber(rawValue);
/*  577 */             if (value == null)
/*      */             {
/*      */               
/*  580 */               return true;
/*      */             }
/*  582 */             this.extensions.addRepeatedField(extension.descriptor, extension
/*  583 */                 .singularToFieldSetType(value));
/*      */           } 
/*      */         } else {
/*  586 */           while (input.getBytesUntilLimit() > 0) {
/*      */             
/*  588 */             Object value = FieldSet.readPrimitiveField(input, extension.descriptor
/*  589 */                 .getLiteType(), false);
/*  590 */             this.extensions.addRepeatedField(extension.descriptor, value);
/*      */           } 
/*      */         } 
/*  593 */         input.popLimit(limit);
/*      */       } else {
/*      */         Object value; MessageLite.Builder subBuilder; int rawValue;
/*  596 */         switch (extension.descriptor.getLiteJavaType()) {
/*      */           
/*      */           case MESSAGE:
/*  599 */             subBuilder = null;
/*  600 */             if (!extension.descriptor.isRepeated()) {
/*  601 */               MessageLite existingValue = (MessageLite)this.extensions.getField(extension.descriptor);
/*  602 */               if (existingValue != null) {
/*  603 */                 subBuilder = existingValue.toBuilder();
/*      */               }
/*      */             } 
/*  606 */             if (subBuilder == null) {
/*  607 */               subBuilder = extension.getMessageDefaultInstance().newBuilderForType();
/*      */             }
/*  609 */             if (extension.descriptor.getLiteType() == WireFormat.FieldType.GROUP) {
/*  610 */               input.readGroup(extension.getNumber(), subBuilder, extensionRegistry);
/*      */             } else {
/*  612 */               input.readMessage(subBuilder, extensionRegistry);
/*      */             } 
/*  614 */             value = subBuilder.build();
/*      */             break;
/*      */           
/*      */           case ENUM:
/*  618 */             rawValue = input.readEnum();
/*  619 */             value = extension.descriptor.getEnumType().findValueByNumber(rawValue);
/*      */ 
/*      */             
/*  622 */             if (value == null) {
/*  623 */               mergeVarintField(fieldNumber, rawValue);
/*  624 */               return true;
/*      */             } 
/*      */             break;
/*      */           
/*      */           default:
/*  629 */             value = FieldSet.readPrimitiveField(input, extension.descriptor
/*  630 */                 .getLiteType(), false);
/*      */             break;
/*      */         } 
/*      */         
/*  634 */         if (extension.descriptor.isRepeated()) {
/*  635 */           this.extensions.addRepeatedField(extension.descriptor, extension
/*  636 */               .singularToFieldSetType(value));
/*      */         } else {
/*  638 */           this.extensions.setField(extension.descriptor, extension.singularToFieldSetType(value));
/*      */         } 
/*      */       } 
/*  641 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected <MessageType extends MessageLite> boolean parseUnknownFieldAsMessageSet(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/*  658 */       if (tag == WireFormat.MESSAGE_SET_ITEM_TAG) {
/*  659 */         mergeMessageSetExtensionFromCodedStream(defaultInstance, input, extensionRegistry);
/*  660 */         return true;
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  665 */       int wireType = WireFormat.getTagWireType(tag);
/*  666 */       if (wireType == 2) {
/*  667 */         return parseUnknownField(defaultInstance, input, extensionRegistry, tag);
/*      */       }
/*      */       
/*  670 */       return input.skipField(tag);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private <MessageType extends MessageLite> void mergeMessageSetExtensionFromCodedStream(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  702 */       int typeId = 0;
/*  703 */       ByteString rawBytes = null;
/*  704 */       GeneratedMessageLite.GeneratedExtension<?, ?> extension = null;
/*      */ 
/*      */ 
/*      */       
/*      */       while (true) {
/*  709 */         int tag = input.readTag();
/*  710 */         if (tag == 0) {
/*      */           break;
/*      */         }
/*      */         
/*  714 */         if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
/*  715 */           typeId = input.readUInt32();
/*  716 */           if (typeId != 0)
/*  717 */             extension = extensionRegistry.findLiteExtensionByNumber(defaultInstance, typeId); 
/*      */           continue;
/*      */         } 
/*  720 */         if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
/*  721 */           if (typeId != 0 && 
/*  722 */             extension != null) {
/*      */ 
/*      */             
/*  725 */             eagerlyMergeMessageSetExtension(input, extension, extensionRegistry, typeId);
/*  726 */             rawBytes = null;
/*      */             
/*      */             continue;
/*      */           } 
/*      */           
/*  731 */           rawBytes = input.readBytes();
/*      */           continue;
/*      */         } 
/*  734 */         if (!input.skipField(tag)) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */       
/*  739 */       input.checkLastTagWas(WireFormat.MESSAGE_SET_ITEM_END_TAG);
/*      */ 
/*      */       
/*  742 */       if (rawBytes != null && typeId != 0) {
/*  743 */         if (extension != null) {
/*  744 */           mergeMessageSetExtensionFromBytes(rawBytes, extensionRegistry, extension);
/*      */         }
/*  746 */         else if (rawBytes != null) {
/*  747 */           mergeLengthDelimitedField(typeId, rawBytes);
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void eagerlyMergeMessageSetExtension(CodedInputStream input, GeneratedMessageLite.GeneratedExtension<?, ?> extension, ExtensionRegistryLite extensionRegistry, int typeId) throws IOException {
/*  759 */       int fieldNumber = typeId;
/*  760 */       int tag = WireFormat.makeTag(typeId, 2);
/*  761 */       parseExtension(input, extensionRegistry, extension, tag, fieldNumber);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void mergeMessageSetExtensionFromBytes(ByteString rawBytes, ExtensionRegistryLite extensionRegistry, GeneratedMessageLite.GeneratedExtension<?, ?> extension) throws IOException {
/*  769 */       MessageLite.Builder subBuilder = null;
/*  770 */       MessageLite existingValue = (MessageLite)this.extensions.getField(extension.descriptor);
/*  771 */       if (existingValue != null) {
/*  772 */         subBuilder = existingValue.toBuilder();
/*      */       }
/*  774 */       if (subBuilder == null) {
/*  775 */         subBuilder = extension.getMessageDefaultInstance().newBuilderForType();
/*      */       }
/*  777 */       subBuilder.mergeFrom(rawBytes, extensionRegistry);
/*  778 */       MessageLite value = subBuilder.build();
/*      */       
/*  780 */       ensureExtensionsAreMutable()
/*  781 */         .setField(extension.descriptor, extension.singularToFieldSetType(value));
/*      */     }
/*      */     
/*      */     FieldSet<GeneratedMessageLite.ExtensionDescriptor> ensureExtensionsAreMutable() {
/*  785 */       if (this.extensions.isImmutable()) {
/*  786 */         this.extensions = this.extensions.clone();
/*      */       }
/*  788 */       return this.extensions;
/*      */     }
/*      */     
/*      */     private void verifyExtensionContainingType(GeneratedMessageLite.GeneratedExtension<MessageType, ?> extension) {
/*  792 */       if (extension.getContainingTypeDefaultInstance() != getDefaultInstanceForType())
/*      */       {
/*  794 */         throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings.");
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extension) {
/*  803 */       GeneratedMessageLite.GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
/*      */       
/*  805 */       verifyExtensionContainingType(extensionLite);
/*  806 */       return this.extensions.hasField(extensionLite.descriptor);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extension) {
/*  813 */       GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = (GeneratedMessageLite.GeneratedExtension)GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
/*      */       
/*  815 */       verifyExtensionContainingType(extensionLite);
/*  816 */       return this.extensions.getRepeatedFieldCount(extensionLite.descriptor);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extension) {
/*  823 */       GeneratedMessageLite.GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
/*      */       
/*  825 */       verifyExtensionContainingType(extensionLite);
/*  826 */       Object value = this.extensions.getField(extensionLite.descriptor);
/*  827 */       if (value == null) {
/*  828 */         return extensionLite.defaultValue;
/*      */       }
/*  830 */       return (Type)extensionLite.fromFieldSetType(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extension, int index) {
/*  839 */       GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = (GeneratedMessageLite.GeneratedExtension)GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
/*      */       
/*  841 */       verifyExtensionContainingType(extensionLite);
/*  842 */       return (Type)extensionLite
/*  843 */         .singularFromFieldSetType(this.extensions
/*  844 */           .getRepeatedField(extensionLite.descriptor, index));
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean extensionsAreInitialized() {
/*  849 */       return this.extensions.isInitialized();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected class ExtensionWriter
/*      */     {
/*  861 */       private final Iterator<Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object>> iter = GeneratedMessageLite.ExtendableMessage.this.extensions.iterator();
/*      */       private Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object> next;
/*      */       private final boolean messageSetWireFormat;
/*      */       
/*      */       private ExtensionWriter(boolean messageSetWireFormat) {
/*  866 */         if (this.iter.hasNext()) {
/*  867 */           this.next = this.iter.next();
/*      */         }
/*  869 */         this.messageSetWireFormat = messageSetWireFormat;
/*      */       }
/*      */       
/*      */       public void writeUntil(int end, CodedOutputStream output) throws IOException {
/*  873 */         while (this.next != null && ((GeneratedMessageLite.ExtensionDescriptor)this.next.getKey()).getNumber() < end) {
/*  874 */           GeneratedMessageLite.ExtensionDescriptor extension = this.next.getKey();
/*  875 */           if (this.messageSetWireFormat && extension
/*  876 */             .getLiteJavaType() == WireFormat.JavaType.MESSAGE && 
/*  877 */             !extension.isRepeated()) {
/*  878 */             output.writeMessageSetExtension(extension.getNumber(), (MessageLite)this.next.getValue());
/*      */           } else {
/*  880 */             FieldSet.writeField(extension, this.next.getValue(), output);
/*      */           } 
/*  882 */           if (this.iter.hasNext()) {
/*  883 */             this.next = this.iter.next(); continue;
/*      */           } 
/*  885 */           this.next = null;
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     protected ExtensionWriter newExtensionWriter() {
/*  892 */       return new ExtensionWriter(false);
/*      */     }
/*      */     
/*      */     protected ExtensionWriter newMessageSetExtensionWriter() {
/*  896 */       return new ExtensionWriter(true);
/*      */     }
/*      */ 
/*      */     
/*      */     protected int extensionsSerializedSize() {
/*  901 */       return this.extensions.getSerializedSize();
/*      */     }
/*      */     
/*      */     protected int extensionsSerializedSizeAsMessageSet() {
/*  905 */       return this.extensions.getMessageSetSerializedSize();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class ExtendableBuilder<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>>
/*      */     extends Builder<MessageType, BuilderType>
/*      */     implements ExtendableMessageOrBuilder<MessageType, BuilderType>
/*      */   {
/*      */     protected ExtendableBuilder(MessageType defaultInstance) {
/*  917 */       super(defaultInstance);
/*      */     }
/*      */ 
/*      */     
/*      */     void internalSetExtensionSet(FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) {
/*  922 */       copyOnWrite();
/*  923 */       ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions = extensions;
/*      */     }
/*      */ 
/*      */     
/*      */     protected void copyOnWriteInternal() {
/*  928 */       super.copyOnWriteInternal();
/*  929 */       ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions = ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions.clone();
/*      */     }
/*      */     
/*      */     private FieldSet<GeneratedMessageLite.ExtensionDescriptor> ensureExtensionsAreMutable() {
/*  933 */       FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions;
/*  934 */       if (extensions.isImmutable()) {
/*  935 */         extensions = extensions.clone();
/*  936 */         ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions = extensions;
/*      */       } 
/*  938 */       return extensions;
/*      */     }
/*      */ 
/*      */     
/*      */     public final MessageType buildPartial() {
/*  943 */       if (this.isBuilt) {
/*  944 */         return this.instance;
/*      */       }
/*      */       
/*  947 */       ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions.makeImmutable();
/*  948 */       return super.buildPartial();
/*      */     }
/*      */     
/*      */     private void verifyExtensionContainingType(GeneratedMessageLite.GeneratedExtension<MessageType, ?> extension) {
/*  952 */       if (extension.getContainingTypeDefaultInstance() != getDefaultInstanceForType())
/*      */       {
/*  954 */         throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings.");
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extension) {
/*  963 */       return ((GeneratedMessageLite.ExtendableMessage)this.instance).hasExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extension) {
/*  970 */       return ((GeneratedMessageLite.ExtendableMessage)this.instance).getExtensionCount(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extension) {
/*  977 */       return (Type)((GeneratedMessageLite.ExtendableMessage)this.instance).getExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extension, int index) {
/*  985 */       return (Type)((GeneratedMessageLite.ExtendableMessage)this.instance).getExtension(extension, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extension, Type value) {
/*  991 */       GeneratedMessageLite.GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
/*      */       
/*  993 */       verifyExtensionContainingType(extensionLite);
/*  994 */       copyOnWrite();
/*  995 */       ensureExtensionsAreMutable()
/*  996 */         .setField(extensionLite.descriptor, extensionLite.toFieldSetType(value));
/*  997 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extension, int index, Type value) {
/* 1003 */       GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = (GeneratedMessageLite.GeneratedExtension)GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
/*      */       
/* 1005 */       verifyExtensionContainingType(extensionLite);
/* 1006 */       copyOnWrite();
/* 1007 */       ensureExtensionsAreMutable()
/* 1008 */         .setRepeatedField(extensionLite.descriptor, index, extensionLite
/* 1009 */           .singularToFieldSetType(value));
/* 1010 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extension, Type value) {
/* 1016 */       GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = (GeneratedMessageLite.GeneratedExtension)GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
/*      */       
/* 1018 */       verifyExtensionContainingType(extensionLite);
/* 1019 */       copyOnWrite();
/* 1020 */       ensureExtensionsAreMutable()
/* 1021 */         .addRepeatedField(extensionLite.descriptor, extensionLite.singularToFieldSetType(value));
/* 1022 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public final BuilderType clearExtension(ExtensionLite<MessageType, ?> extension) {
/* 1027 */       GeneratedMessageLite.GeneratedExtension<MessageType, ?> extensionLite = GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
/*      */       
/* 1029 */       verifyExtensionContainingType(extensionLite);
/* 1030 */       copyOnWrite();
/* 1031 */       ensureExtensionsAreMutable().clearField(extensionLite.descriptor);
/* 1032 */       return (BuilderType)this;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newSingularGeneratedExtension(ContainingType containingTypeDefaultInstance, Type defaultValue, MessageLite messageDefaultInstance, Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, Class singularType) {
/* 1048 */     return new GeneratedExtension<>(containingTypeDefaultInstance, defaultValue, messageDefaultInstance, new ExtensionDescriptor(enumTypeMap, number, type, false, false), singularType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newRepeatedGeneratedExtension(ContainingType containingTypeDefaultInstance, MessageLite messageDefaultInstance, Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, boolean isPacked, Class singularType) {
/* 1068 */     List<?> list = Collections.emptyList();
/* 1069 */     return new GeneratedExtension<>(containingTypeDefaultInstance, (Type)list, messageDefaultInstance, new ExtensionDescriptor(enumTypeMap, number, type, true, isPacked), singularType);
/*      */   }
/*      */ 
/*      */   
/*      */   static final class ExtensionDescriptor
/*      */     implements FieldSet.FieldDescriptorLite<ExtensionDescriptor>
/*      */   {
/*      */     final Internal.EnumLiteMap<?> enumTypeMap;
/*      */     
/*      */     final int number;
/*      */     
/*      */     final WireFormat.FieldType type;
/*      */     final boolean isRepeated;
/*      */     final boolean isPacked;
/*      */     
/*      */     ExtensionDescriptor(Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, boolean isRepeated, boolean isPacked) {
/* 1085 */       this.enumTypeMap = enumTypeMap;
/* 1086 */       this.number = number;
/* 1087 */       this.type = type;
/* 1088 */       this.isRepeated = isRepeated;
/* 1089 */       this.isPacked = isPacked;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getNumber() {
/* 1100 */       return this.number;
/*      */     }
/*      */ 
/*      */     
/*      */     public WireFormat.FieldType getLiteType() {
/* 1105 */       return this.type;
/*      */     }
/*      */ 
/*      */     
/*      */     public WireFormat.JavaType getLiteJavaType() {
/* 1110 */       return this.type.getJavaType();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isRepeated() {
/* 1115 */       return this.isRepeated;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isPacked() {
/* 1120 */       return this.isPacked;
/*      */     }
/*      */ 
/*      */     
/*      */     public Internal.EnumLiteMap<?> getEnumType() {
/* 1125 */       return this.enumTypeMap;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public MessageLite.Builder internalMergeFrom(MessageLite.Builder to, MessageLite from) {
/* 1131 */       return ((GeneratedMessageLite.Builder<GeneratedMessageLite, MessageLite.Builder>)to).mergeFrom((GeneratedMessageLite)from);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int compareTo(ExtensionDescriptor other) {
/* 1137 */       return this.number - other.number;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static Method getMethodOrDie(Class clazz, String name, Class... params) {
/*      */     try {
/* 1147 */       return clazz.getMethod(name, params);
/* 1148 */     } catch (NoSuchMethodException e) {
/* 1149 */       throw new RuntimeException("Generated message class \"" + clazz
/* 1150 */           .getName() + "\" missing method \"" + name + "\".", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static Object invokeOrDie(Method method, Object object, Object... params) {
/*      */     try {
/* 1158 */       return method.invoke(object, params);
/* 1159 */     } catch (IllegalAccessException e) {
/* 1160 */       throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
/*      */     }
/* 1162 */     catch (InvocationTargetException e) {
/* 1163 */       Throwable cause = e.getCause();
/* 1164 */       if (cause instanceof RuntimeException)
/* 1165 */         throw (RuntimeException)cause; 
/* 1166 */       if (cause instanceof Error) {
/* 1167 */         throw (Error)cause;
/*      */       }
/* 1169 */       throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class GeneratedExtension<ContainingType extends MessageLite, Type>
/*      */     extends ExtensionLite<ContainingType, Type>
/*      */   {
/*      */     final ContainingType containingTypeDefaultInstance;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final Type defaultValue;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final MessageLite messageDefaultInstance;
/*      */ 
/*      */ 
/*      */     
/*      */     final GeneratedMessageLite.ExtensionDescriptor descriptor;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     GeneratedExtension(ContainingType containingTypeDefaultInstance, Type defaultValue, MessageLite messageDefaultInstance, GeneratedMessageLite.ExtensionDescriptor descriptor, Class singularType) {
/* 1200 */       if (containingTypeDefaultInstance == null) {
/* 1201 */         throw new IllegalArgumentException("Null containingTypeDefaultInstance");
/*      */       }
/* 1203 */       if (descriptor.getLiteType() == WireFormat.FieldType.MESSAGE && messageDefaultInstance == null)
/*      */       {
/* 1205 */         throw new IllegalArgumentException("Null messageDefaultInstance");
/*      */       }
/* 1207 */       this.containingTypeDefaultInstance = containingTypeDefaultInstance;
/* 1208 */       this.defaultValue = defaultValue;
/* 1209 */       this.messageDefaultInstance = messageDefaultInstance;
/* 1210 */       this.descriptor = descriptor;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ContainingType getContainingTypeDefaultInstance() {
/* 1220 */       return this.containingTypeDefaultInstance;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getNumber() {
/* 1226 */       return this.descriptor.getNumber();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public MessageLite getMessageDefaultInstance() {
/* 1235 */       return this.messageDefaultInstance;
/*      */     }
/*      */ 
/*      */     
/*      */     Object fromFieldSetType(Object value) {
/* 1240 */       if (this.descriptor.isRepeated()) {
/* 1241 */         if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
/* 1242 */           List<Object> result = new ArrayList();
/* 1243 */           for (Object element : value) {
/* 1244 */             result.add(singularFromFieldSetType(element));
/*      */           }
/* 1246 */           return result;
/*      */         } 
/* 1248 */         return value;
/*      */       } 
/*      */       
/* 1251 */       return singularFromFieldSetType(value);
/*      */     }
/*      */ 
/*      */     
/*      */     Object singularFromFieldSetType(Object value) {
/* 1256 */       if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
/* 1257 */         return this.descriptor.enumTypeMap.findValueByNumber(((Integer)value).intValue());
/*      */       }
/* 1259 */       return value;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     Object toFieldSetType(Object value) {
/* 1265 */       if (this.descriptor.isRepeated()) {
/* 1266 */         if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
/* 1267 */           List<Object> result = new ArrayList();
/* 1268 */           for (Object element : value) {
/* 1269 */             result.add(singularToFieldSetType(element));
/*      */           }
/* 1271 */           return result;
/*      */         } 
/* 1273 */         return value;
/*      */       } 
/*      */       
/* 1276 */       return singularToFieldSetType(value);
/*      */     }
/*      */ 
/*      */     
/*      */     Object singularToFieldSetType(Object value) {
/* 1281 */       if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
/* 1282 */         return Integer.valueOf(((Internal.EnumLite)value).getNumber());
/*      */       }
/* 1284 */       return value;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public WireFormat.FieldType getLiteType() {
/* 1290 */       return this.descriptor.getLiteType();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isRepeated() {
/* 1295 */       return this.descriptor.isRepeated;
/*      */     }
/*      */ 
/*      */     
/*      */     public Type getDefaultValue() {
/* 1300 */       return this.defaultValue;
/*      */     }
/*      */   }
/*      */   
/*      */   protected static final class SerializedForm implements Serializable {
/*      */     private static final long serialVersionUID = 0L;
/*      */     private final Class<?> messageClass;
/*      */     private final String messageClassName;
/*      */     private final byte[] asBytes;
/*      */     
/*      */     public static SerializedForm of(MessageLite message) {
/* 1311 */       return new SerializedForm(message);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     SerializedForm(MessageLite regularForm) {
/* 1328 */       this.messageClass = regularForm.getClass();
/* 1329 */       this.messageClassName = this.messageClass.getName();
/* 1330 */       this.asBytes = regularForm.toByteArray();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Object readResolve() throws ObjectStreamException {
/*      */       try {
/* 1342 */         Class<?> messageClass = resolveMessageClass();
/*      */         
/* 1344 */         Field defaultInstanceField = messageClass.getDeclaredField("DEFAULT_INSTANCE");
/* 1345 */         defaultInstanceField.setAccessible(true);
/* 1346 */         MessageLite defaultInstance = (MessageLite)defaultInstanceField.get(null);
/* 1347 */         return defaultInstance.newBuilderForType().mergeFrom(this.asBytes).buildPartial();
/* 1348 */       } catch (ClassNotFoundException e) {
/* 1349 */         throw new RuntimeException("Unable to find proto buffer class: " + this.messageClassName, e);
/* 1350 */       } catch (NoSuchFieldException e) {
/* 1351 */         return readResolveFallback();
/* 1352 */       } catch (SecurityException e) {
/* 1353 */         throw new RuntimeException("Unable to call DEFAULT_INSTANCE in " + this.messageClassName, e);
/* 1354 */       } catch (IllegalAccessException e) {
/* 1355 */         throw new RuntimeException("Unable to call parsePartialFrom", e);
/* 1356 */       } catch (InvalidProtocolBufferException e) {
/* 1357 */         throw new RuntimeException("Unable to understand proto buffer", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     private Object readResolveFallback() throws ObjectStreamException {
/*      */       try {
/* 1367 */         Class<?> messageClass = resolveMessageClass();
/*      */         
/* 1369 */         Field defaultInstanceField = messageClass.getDeclaredField("defaultInstance");
/* 1370 */         defaultInstanceField.setAccessible(true);
/* 1371 */         MessageLite defaultInstance = (MessageLite)defaultInstanceField.get(null);
/* 1372 */         return defaultInstance.newBuilderForType()
/* 1373 */           .mergeFrom(this.asBytes)
/* 1374 */           .buildPartial();
/* 1375 */       } catch (ClassNotFoundException e) {
/* 1376 */         throw new RuntimeException("Unable to find proto buffer class: " + this.messageClassName, e);
/* 1377 */       } catch (NoSuchFieldException e) {
/* 1378 */         throw new RuntimeException("Unable to find defaultInstance in " + this.messageClassName, e);
/* 1379 */       } catch (SecurityException e) {
/* 1380 */         throw new RuntimeException("Unable to call defaultInstance in " + this.messageClassName, e);
/* 1381 */       } catch (IllegalAccessException e) {
/* 1382 */         throw new RuntimeException("Unable to call parsePartialFrom", e);
/* 1383 */       } catch (InvalidProtocolBufferException e) {
/* 1384 */         throw new RuntimeException("Unable to understand proto buffer", e);
/*      */       } 
/*      */     }
/*      */     
/*      */     private Class<?> resolveMessageClass() throws ClassNotFoundException {
/* 1389 */       return (this.messageClass != null) ? this.messageClass : Class.forName(this.messageClassName);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static <MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>, T> GeneratedExtension<MessageType, T> checkIsLite(ExtensionLite<MessageType, T> extension) {
/* 1399 */     if (!extension.isLite()) {
/* 1400 */       throw new IllegalArgumentException("Expected a lite extension.");
/*      */     }
/*      */     
/* 1403 */     return (GeneratedExtension<MessageType, T>)extension;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static final <T extends GeneratedMessageLite<T, ?>> boolean isInitialized(T message, boolean shouldMemoize) {
/* 1414 */     byte memoizedIsInitialized = ((Byte)message.dynamicMethod(MethodToInvoke.GET_MEMOIZED_IS_INITIALIZED)).byteValue();
/* 1415 */     if (memoizedIsInitialized == 1) {
/* 1416 */       return true;
/*      */     }
/* 1418 */     if (memoizedIsInitialized == 0) {
/* 1419 */       return false;
/*      */     }
/* 1421 */     boolean isInitialized = Protobuf.getInstance().<T>schemaFor(message).isInitialized(message);
/* 1422 */     if (shouldMemoize) {
/* 1423 */       message.dynamicMethod(MethodToInvoke.SET_MEMOIZED_IS_INITIALIZED, isInitialized ? message : null);
/*      */     }
/*      */     
/* 1426 */     return isInitialized;
/*      */   }
/*      */   
/*      */   protected static Internal.IntList emptyIntList() {
/* 1430 */     return IntArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.IntList mutableCopy(Internal.IntList list) {
/* 1434 */     int size = list.size();
/* 1435 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Internal.LongList emptyLongList() {
/* 1440 */     return LongArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.LongList mutableCopy(Internal.LongList list) {
/* 1444 */     int size = list.size();
/* 1445 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Internal.FloatList emptyFloatList() {
/* 1450 */     return FloatArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
/* 1454 */     int size = list.size();
/* 1455 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Internal.DoubleList emptyDoubleList() {
/* 1460 */     return DoubleArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
/* 1464 */     int size = list.size();
/* 1465 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Internal.BooleanList emptyBooleanList() {
/* 1470 */     return BooleanArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
/* 1474 */     int size = list.size();
/* 1475 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <E> Internal.ProtobufList<E> emptyProtobufList() {
/* 1480 */     return ProtobufArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static <E> Internal.ProtobufList<E> mutableCopy(Internal.ProtobufList<E> list) {
/* 1484 */     int size = list.size();
/* 1485 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static class DefaultInstanceBasedParser<T extends GeneratedMessageLite<T, ?>>
/*      */     extends AbstractParser<T>
/*      */   {
/*      */     private final T defaultInstance;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DefaultInstanceBasedParser(T defaultInstance) {
/* 1500 */       this.defaultInstance = defaultInstance;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public T parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1506 */       return GeneratedMessageLite.parsePartialFrom(this.defaultInstance, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public T parsePartialFrom(byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1513 */       return GeneratedMessageLite.parsePartialFrom(this.defaultInstance, input, offset, length, extensionRegistry);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T instance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1527 */     GeneratedMessageLite generatedMessageLite = (GeneratedMessageLite)instance.dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
/*      */ 
/*      */     
/*      */     try {
/* 1531 */       Schema<T> schema = Protobuf.getInstance().schemaFor((T)generatedMessageLite);
/* 1532 */       schema.mergeFrom((T)generatedMessageLite, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
/* 1533 */       schema.makeImmutable((T)generatedMessageLite);
/* 1534 */     } catch (IOException e) {
/* 1535 */       if (e.getCause() instanceof InvalidProtocolBufferException) {
/* 1536 */         throw (InvalidProtocolBufferException)e.getCause();
/*      */       }
/* 1538 */       throw (new InvalidProtocolBufferException(e.getMessage())).setUnfinishedMessage(generatedMessageLite);
/* 1539 */     } catch (RuntimeException e) {
/* 1540 */       if (e.getCause() instanceof InvalidProtocolBufferException) {
/* 1541 */         throw (InvalidProtocolBufferException)e.getCause();
/*      */       }
/* 1543 */       throw e;
/*      */     } 
/* 1545 */     return (T)generatedMessageLite;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T instance, byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1553 */     GeneratedMessageLite generatedMessageLite = (GeneratedMessageLite)instance.dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
/*      */     try {
/* 1555 */       Schema<T> schema = Protobuf.getInstance().schemaFor((T)generatedMessageLite);
/* 1556 */       schema.mergeFrom((T)generatedMessageLite, input, offset, offset + length, new ArrayDecoders.Registers(extensionRegistry));
/*      */       
/* 1558 */       schema.makeImmutable((T)generatedMessageLite);
/* 1559 */       if (generatedMessageLite.memoizedHashCode != 0) {
/* 1560 */         throw new RuntimeException();
/*      */       }
/* 1562 */     } catch (IOException e) {
/* 1563 */       if (e.getCause() instanceof InvalidProtocolBufferException) {
/* 1564 */         throw (InvalidProtocolBufferException)e.getCause();
/*      */       }
/* 1566 */       throw (new InvalidProtocolBufferException(e.getMessage())).setUnfinishedMessage(generatedMessageLite);
/* 1567 */     } catch (IndexOutOfBoundsException e) {
/* 1568 */       throw InvalidProtocolBufferException.truncatedMessage().setUnfinishedMessage(generatedMessageLite);
/*      */     } 
/* 1570 */     return (T)generatedMessageLite;
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
/* 1575 */     return parsePartialFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static <T extends GeneratedMessageLite<T, ?>> T checkMessageInitialized(T message) throws InvalidProtocolBufferException {
/* 1586 */     if (message != null && !message.isInitialized()) {
/* 1587 */       throw message
/* 1588 */         .newUninitializedMessageException()
/* 1589 */         .asInvalidProtocolBufferException()
/* 1590 */         .setUnfinishedMessage(message);
/*      */     }
/* 1592 */     return message;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1599 */     return checkMessageInitialized(
/* 1600 */         parseFrom(defaultInstance, CodedInputStream.newInstance(data), extensionRegistry));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteBuffer data) throws InvalidProtocolBufferException {
/* 1606 */     return parseFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data) throws InvalidProtocolBufferException {
/* 1612 */     return checkMessageInitialized(
/* 1613 */         parseFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1620 */     return checkMessageInitialized(parsePartialFrom(defaultInstance, data, extensionRegistry));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*      */     try {
/* 1630 */       CodedInputStream input = data.newCodedInput();
/* 1631 */       T message = parsePartialFrom(defaultInstance, input, extensionRegistry);
/*      */       try {
/* 1633 */         input.checkLastTagWas(0);
/* 1634 */       } catch (InvalidProtocolBufferException e) {
/* 1635 */         throw e.setUnfinishedMessage(message);
/*      */       } 
/* 1637 */       return message;
/* 1638 */     } catch (InvalidProtocolBufferException e) {
/* 1639 */       throw e;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1648 */     return checkMessageInitialized(
/* 1649 */         parsePartialFrom(defaultInstance, data, 0, data.length, extensionRegistry));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data) throws InvalidProtocolBufferException {
/* 1655 */     return checkMessageInitialized(parsePartialFrom(defaultInstance, data, 0, data.length, 
/* 1656 */           ExtensionRegistryLite.getEmptyRegistry()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1663 */     return checkMessageInitialized(
/* 1664 */         parsePartialFrom(defaultInstance, data, 0, data.length, extensionRegistry));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
/* 1670 */     return checkMessageInitialized(
/* 1671 */         parsePartialFrom(defaultInstance, 
/*      */           
/* 1673 */           CodedInputStream.newInstance(input), 
/* 1674 */           ExtensionRegistryLite.getEmptyRegistry()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1681 */     return checkMessageInitialized(
/* 1682 */         parsePartialFrom(defaultInstance, CodedInputStream.newInstance(input), extensionRegistry));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
/* 1688 */     return parseFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1695 */     return checkMessageInitialized(parsePartialFrom(defaultInstance, input, extensionRegistry));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
/* 1701 */     return checkMessageInitialized(
/* 1702 */         parsePartialDelimitedFrom(defaultInstance, input, 
/* 1703 */           ExtensionRegistryLite.getEmptyRegistry()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1710 */     return checkMessageInitialized(
/* 1711 */         parsePartialDelimitedFrom(defaultInstance, input, extensionRegistry));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static <T extends GeneratedMessageLite<T, ?>> T parsePartialDelimitedFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*      */     int size;
/*      */     try {
/* 1719 */       int firstByte = input.read();
/* 1720 */       if (firstByte == -1) {
/* 1721 */         return null;
/*      */       }
/* 1723 */       size = CodedInputStream.readRawVarint32(firstByte, input);
/* 1724 */     } catch (IOException e) {
/* 1725 */       throw new InvalidProtocolBufferException(e.getMessage());
/*      */     } 
/* 1727 */     InputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
/* 1728 */     CodedInputStream codedInput = CodedInputStream.newInstance(limitedInput);
/* 1729 */     T message = parsePartialFrom(defaultInstance, codedInput, extensionRegistry);
/*      */     try {
/* 1731 */       codedInput.checkLastTagWas(0);
/* 1732 */     } catch (InvalidProtocolBufferException e) {
/* 1733 */       throw e.setUnfinishedMessage(message);
/*      */     } 
/* 1735 */     return message;
/*      */   }
/*      */   
/*      */   protected abstract Object dynamicMethod(MethodToInvoke paramMethodToInvoke, Object paramObject1, Object paramObject2);
/*      */   
/*      */   public static interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends MessageLiteOrBuilder {
/*      */     <Type> boolean hasExtension(ExtensionLite<MessageType, Type> param1ExtensionLite);
/*      */     
/*      */     <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> param1ExtensionLite);
/*      */     
/*      */     <Type> Type getExtension(ExtensionLite<MessageType, Type> param1ExtensionLite);
/*      */     
/*      */     <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> param1ExtensionLite, int param1Int);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\GeneratedMessageLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */