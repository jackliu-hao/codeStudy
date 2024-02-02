/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectStreamException;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.TreeMap;
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
/*      */ public abstract class GeneratedMessageV3
/*      */   extends AbstractMessage
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   protected static boolean alwaysUseFieldBuilders = false;
/*      */   protected UnknownFieldSet unknownFields;
/*      */   
/*      */   protected GeneratedMessageV3() {
/*   94 */     this.unknownFields = UnknownFieldSet.getDefaultInstance();
/*      */   }
/*      */   
/*      */   protected GeneratedMessageV3(Builder<?> builder) {
/*   98 */     this.unknownFields = builder.getUnknownFields();
/*      */   }
/*      */ 
/*      */   
/*      */   public Parser<? extends GeneratedMessageV3> getParserForType() {
/*  103 */     throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void enableAlwaysUseFieldBuildersForTesting() {
/*  111 */     setAlwaysUseFieldBuildersForTesting(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void setAlwaysUseFieldBuildersForTesting(boolean useBuilders) {
/*  121 */     alwaysUseFieldBuilders = useBuilders;
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
/*      */   public Descriptors.Descriptor getDescriptorForType() {
/*  133 */     return (internalGetFieldAccessorTable()).descriptor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeFromAndMakeImmutableInternal(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  140 */     Schema<GeneratedMessageV3> schema = Protobuf.getInstance().schemaFor(this);
/*      */     try {
/*  142 */       schema.mergeFrom(this, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
/*  143 */     } catch (InvalidProtocolBufferException e) {
/*  144 */       throw e.setUnfinishedMessage(this);
/*  145 */     } catch (IOException e) {
/*  146 */       throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this);
/*      */     } 
/*  148 */     schema.makeImmutable(this);
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
/*      */   private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable(boolean getBytesForString) {
/*  160 */     TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
/*      */     
/*  162 */     Descriptors.Descriptor descriptor = (internalGetFieldAccessorTable()).descriptor;
/*  163 */     List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
/*      */     
/*  165 */     for (int i = 0; i < fields.size(); i++) {
/*  166 */       Descriptors.FieldDescriptor field = fields.get(i);
/*  167 */       Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  173 */       if (oneofDescriptor != null) {
/*      */         
/*  175 */         i += oneofDescriptor.getFieldCount() - 1;
/*  176 */         if (!hasOneof(oneofDescriptor)) {
/*      */           continue;
/*      */         }
/*      */ 
/*      */         
/*  181 */         field = getOneofFieldDescriptor(oneofDescriptor);
/*      */       } else {
/*      */         
/*  184 */         if (field.isRepeated()) {
/*  185 */           List<?> value = (List)getField(field);
/*  186 */           if (!value.isEmpty()) {
/*  187 */             result.put(field, value);
/*      */           }
/*      */           continue;
/*      */         } 
/*  191 */         if (!hasField(field)) {
/*      */           continue;
/*      */         }
/*      */       } 
/*      */       
/*  196 */       if (getBytesForString && field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
/*  197 */         result.put(field, getFieldRaw(field));
/*      */       } else {
/*  199 */         result.put(field, getField(field));
/*      */       }  continue;
/*      */     } 
/*  202 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isInitialized() {
/*  207 */     for (Descriptors.FieldDescriptor field : getDescriptorForType().getFields()) {
/*      */       
/*  209 */       if (field.isRequired() && 
/*  210 */         !hasField(field)) {
/*  211 */         return false;
/*      */       }
/*      */ 
/*      */       
/*  215 */       if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/*  216 */         if (field.isRepeated()) {
/*      */           
/*  218 */           List<Message> messageList = (List<Message>)getField(field);
/*  219 */           for (Message element : messageList) {
/*  220 */             if (!element.isInitialized())
/*  221 */               return false; 
/*      */           } 
/*      */           continue;
/*      */         } 
/*  225 */         if (hasField(field) && !((Message)getField(field)).isInitialized()) {
/*  226 */           return false;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  232 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/*  237 */     return Collections.unmodifiableMap(
/*  238 */         getAllFieldsMutable(false));
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
/*      */   Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
/*  252 */     return Collections.unmodifiableMap(
/*  253 */         getAllFieldsMutable(true));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/*  258 */     return internalGetFieldAccessorTable().getOneof(oneof).has(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/*  263 */     return internalGetFieldAccessorTable().getOneof(oneof).get(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasField(Descriptors.FieldDescriptor field) {
/*  268 */     return internalGetFieldAccessorTable().getField(field).has(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getField(Descriptors.FieldDescriptor field) {
/*  273 */     return internalGetFieldAccessorTable().getField(field).get(this);
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
/*      */   Object getFieldRaw(Descriptors.FieldDescriptor field) {
/*  285 */     return internalGetFieldAccessorTable().getField(field).getRaw(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/*  290 */     return internalGetFieldAccessorTable().getField(field)
/*  291 */       .getRepeatedCount(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/*  296 */     return internalGetFieldAccessorTable().getField(field)
/*  297 */       .getRepeated(this, index);
/*      */   }
/*      */ 
/*      */   
/*      */   public UnknownFieldSet getUnknownFields() {
/*  302 */     throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
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
/*      */   protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/*  317 */     if (input.shouldDiscardUnknownFields()) {
/*  318 */       return input.skipField(tag);
/*      */     }
/*  320 */     return unknownFields.mergeFieldFrom(tag, input);
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
/*      */   protected boolean parseUnknownFieldProto3(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/*  333 */     return parseUnknownField(input, unknownFields, extensionRegistry, tag);
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input) throws IOException {
/*      */     try {
/*  339 */       return parser.parseFrom(input);
/*  340 */     } catch (InvalidProtocolBufferException e) {
/*  341 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
/*      */     try {
/*  348 */       return parser.parseFrom(input, extensions);
/*  349 */     } catch (InvalidProtocolBufferException e) {
/*  350 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input) throws IOException {
/*      */     try {
/*  357 */       return parser.parseFrom(input);
/*  358 */     } catch (InvalidProtocolBufferException e) {
/*  359 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input, ExtensionRegistryLite extensions) throws IOException {
/*      */     try {
/*  366 */       return parser.parseFrom(input, extensions);
/*  367 */     } catch (InvalidProtocolBufferException e) {
/*  368 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input) throws IOException {
/*      */     try {
/*  375 */       return parser.parseDelimitedFrom(input);
/*  376 */     } catch (InvalidProtocolBufferException e) {
/*  377 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
/*      */     try {
/*  384 */       return parser.parseDelimitedFrom(input, extensions);
/*  385 */     } catch (InvalidProtocolBufferException e) {
/*  386 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */   
/*      */   protected static boolean canUseUnsafe() {
/*  391 */     return (UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations());
/*      */   }
/*      */   
/*      */   protected static Internal.IntList emptyIntList() {
/*  395 */     return IntArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.IntList newIntList() {
/*  399 */     return new IntArrayList();
/*      */   }
/*      */   
/*      */   protected static Internal.IntList mutableCopy(Internal.IntList list) {
/*  403 */     int size = list.size();
/*  404 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Internal.LongList emptyLongList() {
/*  409 */     return LongArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.LongList newLongList() {
/*  413 */     return new LongArrayList();
/*      */   }
/*      */   
/*      */   protected static Internal.LongList mutableCopy(Internal.LongList list) {
/*  417 */     int size = list.size();
/*  418 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Internal.FloatList emptyFloatList() {
/*  423 */     return FloatArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.FloatList newFloatList() {
/*  427 */     return new FloatArrayList();
/*      */   }
/*      */   
/*      */   protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
/*  431 */     int size = list.size();
/*  432 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Internal.DoubleList emptyDoubleList() {
/*  437 */     return DoubleArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.DoubleList newDoubleList() {
/*  441 */     return new DoubleArrayList();
/*      */   }
/*      */   
/*      */   protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
/*  445 */     int size = list.size();
/*  446 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static Internal.BooleanList emptyBooleanList() {
/*  451 */     return BooleanArrayList.emptyList();
/*      */   }
/*      */   
/*      */   protected static Internal.BooleanList newBooleanList() {
/*  455 */     return new BooleanArrayList();
/*      */   }
/*      */   
/*      */   protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
/*  459 */     int size = list.size();
/*  460 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(CodedOutputStream output) throws IOException {
/*  467 */     MessageReflection.writeMessageTo(this, getAllFieldsRaw(), output, false);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  472 */     int size = this.memoizedSize;
/*  473 */     if (size != -1) {
/*  474 */       return size;
/*      */     }
/*      */     
/*  477 */     this.memoizedSize = MessageReflection.getSerializedSize(this, 
/*  478 */         getAllFieldsRaw());
/*  479 */     return this.memoizedSize;
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
/*      */   protected static final class UnusedPrivateParameter
/*      */   {
/*  493 */     static final UnusedPrivateParameter INSTANCE = new UnusedPrivateParameter();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object newInstance(UnusedPrivateParameter unused) {
/*  504 */     throw new UnsupportedOperationException("This method must be overridden by the subclass.");
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
/*      */   protected void makeExtensionsImmutable() {}
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
/*      */   protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent parent) {
/*  529 */     return newBuilderForType(new BuilderParent()
/*      */         {
/*      */           public void markDirty() {
/*  532 */             parent.markDirty();
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class Builder<BuilderType extends Builder<BuilderType>>
/*      */     extends AbstractMessage.Builder<BuilderType>
/*      */   {
/*      */     private GeneratedMessageV3.BuilderParent builderParent;
/*      */ 
/*      */     
/*      */     private BuilderParentImpl meAsParent;
/*      */ 
/*      */     
/*      */     private boolean isClean;
/*      */ 
/*      */     
/*  551 */     private UnknownFieldSet unknownFields = UnknownFieldSet.getDefaultInstance();
/*      */     
/*      */     protected Builder() {
/*  554 */       this((GeneratedMessageV3.BuilderParent)null);
/*      */     }
/*      */     
/*      */     protected Builder(GeneratedMessageV3.BuilderParent builderParent) {
/*  558 */       this.builderParent = builderParent;
/*      */     }
/*      */ 
/*      */     
/*      */     void dispose() {
/*  563 */       this.builderParent = null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void onBuilt() {
/*  570 */       if (this.builderParent != null) {
/*  571 */         markClean();
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void markClean() {
/*  581 */       this.isClean = true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean isClean() {
/*  590 */       return this.isClean;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType clone() {
/*  596 */       Builder builder = (Builder)getDefaultInstanceForType().newBuilderForType();
/*  597 */       builder.mergeFrom(buildPartial());
/*  598 */       return (BuilderType)builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType clear() {
/*  607 */       this.unknownFields = UnknownFieldSet.getDefaultInstance();
/*  608 */       onChanged();
/*  609 */       return (BuilderType)this;
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
/*      */     public Descriptors.Descriptor getDescriptorForType() {
/*  621 */       return (internalGetFieldAccessorTable()).descriptor;
/*      */     }
/*      */ 
/*      */     
/*      */     public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/*  626 */       return Collections.unmodifiableMap(getAllFieldsMutable());
/*      */     }
/*      */ 
/*      */     
/*      */     private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable() {
/*  631 */       TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
/*      */       
/*  633 */       Descriptors.Descriptor descriptor = (internalGetFieldAccessorTable()).descriptor;
/*  634 */       List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
/*      */       
/*  636 */       for (int i = 0; i < fields.size(); i++) {
/*  637 */         Descriptors.FieldDescriptor field = fields.get(i);
/*  638 */         Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  644 */         if (oneofDescriptor != null) {
/*      */           
/*  646 */           i += oneofDescriptor.getFieldCount() - 1;
/*  647 */           if (!hasOneof(oneofDescriptor)) {
/*      */             continue;
/*      */           }
/*      */ 
/*      */           
/*  652 */           field = getOneofFieldDescriptor(oneofDescriptor);
/*      */         } else {
/*      */           
/*  655 */           if (field.isRepeated()) {
/*  656 */             List<?> value = (List)getField(field);
/*  657 */             if (!value.isEmpty()) {
/*  658 */               result.put(field, value);
/*      */             }
/*      */             continue;
/*      */           } 
/*  662 */           if (!hasField(field)) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */         
/*  667 */         result.put(field, getField(field)); continue;
/*      */       } 
/*  669 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
/*  674 */       return internalGetFieldAccessorTable().getField(field).newBuilder();
/*      */     }
/*      */ 
/*      */     
/*      */     public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
/*  679 */       return internalGetFieldAccessorTable().getField(field).getBuilder(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
/*  684 */       return internalGetFieldAccessorTable().getField(field).getRepeatedBuilder(this, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/*  690 */       return internalGetFieldAccessorTable().getOneof(oneof).has(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/*  695 */       return internalGetFieldAccessorTable().getOneof(oneof).get(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasField(Descriptors.FieldDescriptor field) {
/*  700 */       return internalGetFieldAccessorTable().getField(field).has(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getField(Descriptors.FieldDescriptor field) {
/*  705 */       Object object = internalGetFieldAccessorTable().getField(field).get(this);
/*  706 */       if (field.isRepeated())
/*      */       {
/*      */         
/*  709 */         return Collections.unmodifiableList((List)object);
/*      */       }
/*  711 */       return object;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
/*  717 */       internalGetFieldAccessorTable().getField(field).set(this, value);
/*  718 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType clearField(Descriptors.FieldDescriptor field) {
/*  723 */       internalGetFieldAccessorTable().getField(field).clear(this);
/*  724 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType clearOneof(Descriptors.OneofDescriptor oneof) {
/*  729 */       internalGetFieldAccessorTable().getOneof(oneof).clear(this);
/*  730 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/*  735 */       return internalGetFieldAccessorTable().getField(field)
/*  736 */         .getRepeatedCount(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/*  741 */       return internalGetFieldAccessorTable().getField(field)
/*  742 */         .getRepeated(this, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/*  748 */       internalGetFieldAccessorTable().getField(field)
/*  749 */         .setRepeated(this, index, value);
/*  750 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/*  755 */       internalGetFieldAccessorTable().getField(field).addRepeated(this, value);
/*  756 */       return (BuilderType)this;
/*      */     }
/*      */     
/*      */     private BuilderType setUnknownFieldsInternal(UnknownFieldSet unknownFields) {
/*  760 */       this.unknownFields = unknownFields;
/*  761 */       onChanged();
/*  762 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType setUnknownFields(UnknownFieldSet unknownFields) {
/*  767 */       return setUnknownFieldsInternal(unknownFields);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected BuilderType setUnknownFieldsProto3(UnknownFieldSet unknownFields) {
/*  775 */       return setUnknownFieldsInternal(unknownFields);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  781 */       return setUnknownFields(
/*  782 */           UnknownFieldSet.newBuilder(this.unknownFields)
/*  783 */           .mergeFrom(unknownFields)
/*  784 */           .build());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isInitialized() {
/*  790 */       for (Descriptors.FieldDescriptor field : getDescriptorForType().getFields()) {
/*      */         
/*  792 */         if (field.isRequired() && 
/*  793 */           !hasField(field)) {
/*  794 */           return false;
/*      */         }
/*      */ 
/*      */         
/*  798 */         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/*  799 */           if (field.isRepeated()) {
/*      */             
/*  801 */             List<Message> messageList = (List<Message>)getField(field);
/*  802 */             for (Message element : messageList) {
/*  803 */               if (!element.isInitialized())
/*  804 */                 return false; 
/*      */             } 
/*      */             continue;
/*      */           } 
/*  808 */           if (hasField(field) && 
/*  809 */             !((Message)getField(field)).isInitialized()) {
/*  810 */             return false;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/*  815 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public final UnknownFieldSet getUnknownFields() {
/*  820 */       return this.unknownFields;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private class BuilderParentImpl
/*      */       implements GeneratedMessageV3.BuilderParent
/*      */     {
/*      */       private BuilderParentImpl() {}
/*      */ 
/*      */       
/*      */       public void markDirty() {
/*  832 */         GeneratedMessageV3.Builder.this.onChanged();
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected GeneratedMessageV3.BuilderParent getParentForChildren() {
/*  841 */       if (this.meAsParent == null) {
/*  842 */         this.meAsParent = new BuilderParentImpl();
/*      */       }
/*  844 */       return this.meAsParent;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final void onChanged() {
/*  852 */       if (this.isClean && this.builderParent != null) {
/*  853 */         this.builderParent.markDirty();
/*      */ 
/*      */         
/*  856 */         this.isClean = false;
/*      */       } 
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
/*      */     protected MapField internalGetMapField(int fieldNumber) {
/*  875 */       throw new RuntimeException("No map fields found in " + 
/*  876 */           getClass().getName());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected MapField internalGetMutableMapField(int fieldNumber) {
/*  884 */       throw new RuntimeException("No map fields found in " + 
/*  885 */           getClass().getName());
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
/*      */     protected abstract GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable();
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
/*      */   public static abstract class ExtendableMessage<MessageType extends ExtendableMessage>
/*      */     extends GeneratedMessageV3
/*      */     implements ExtendableMessageOrBuilder<MessageType>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
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
/*      */     private final FieldSet<Descriptors.FieldDescriptor> extensions;
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
/*      */     protected ExtendableMessage() {
/*  988 */       this.extensions = FieldSet.newFieldSet();
/*      */     }
/*      */ 
/*      */     
/*      */     protected ExtendableMessage(GeneratedMessageV3.ExtendableBuilder<MessageType, ?> builder) {
/*  993 */       super(builder);
/*  994 */       this.extensions = builder.buildExtensions();
/*      */     }
/*      */ 
/*      */     
/*      */     private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
/*  999 */       if (extension.getDescriptor().getContainingType() != 
/* 1000 */         getDescriptorForType())
/*      */       {
/* 1002 */         throw new IllegalArgumentException("Extension is for type \"" + extension
/*      */             
/* 1004 */             .getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + 
/*      */             
/* 1006 */             getDescriptorForType().getFullName() + "\".");
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
/* 1014 */       Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
/*      */       
/* 1016 */       verifyExtensionContainingType(extension);
/* 1017 */       return this.extensions.hasField(extension.getDescriptor());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
/* 1025 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1027 */       verifyExtensionContainingType(extension);
/* 1028 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1029 */       return this.extensions.getRepeatedFieldCount(descriptor);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
/* 1036 */       Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
/*      */       
/* 1038 */       verifyExtensionContainingType(extension);
/* 1039 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1040 */       Object value = this.extensions.getField(descriptor);
/* 1041 */       if (value == null) {
/* 1042 */         if (descriptor.isRepeated())
/* 1043 */           return (Type)Collections.emptyList(); 
/* 1044 */         if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
/*      */         {
/* 1046 */           return (Type)extension.getMessageDefaultInstance();
/*      */         }
/* 1048 */         return (Type)extension.fromReflectionType(descriptor
/* 1049 */             .getDefaultValue());
/*      */       } 
/*      */       
/* 1052 */       return (Type)extension.fromReflectionType(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
/* 1061 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1063 */       verifyExtensionContainingType(extension);
/* 1064 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1065 */       return (Type)extension.singularFromReflectionType(this.extensions
/* 1066 */           .getRepeatedField(descriptor, index));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
/* 1072 */       return hasExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
/* 1078 */       return hasExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
/* 1084 */       return getExtensionCount(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
/* 1090 */       return getExtensionCount(extension);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
/* 1095 */       return getExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
/* 1101 */       return getExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
/* 1107 */       return getExtension(extension, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
/* 1113 */       return getExtension(extension, index);
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean extensionsAreInitialized() {
/* 1118 */       return this.extensions.isInitialized();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isInitialized() {
/* 1123 */       return (super.isInitialized() && extensionsAreInitialized());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/* 1132 */       return MessageReflection.mergeFieldFrom(input, 
/* 1133 */           input.shouldDiscardUnknownFields() ? null : unknownFields, extensionRegistry, 
/* 1134 */           getDescriptorForType(), new MessageReflection.ExtensionAdapter(this.extensions), tag);
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
/*      */     protected boolean parseUnknownFieldProto3(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/* 1147 */       return parseUnknownField(input, unknownFields, extensionRegistry, tag);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void makeExtensionsImmutable() {
/* 1156 */       this.extensions.makeImmutable();
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
/*      */     protected class ExtensionWriter
/*      */     {
/* 1169 */       private final Iterator<Map.Entry<Descriptors.FieldDescriptor, Object>> iter = GeneratedMessageV3.ExtendableMessage.this
/* 1170 */         .extensions.iterator();
/*      */       private Map.Entry<Descriptors.FieldDescriptor, Object> next;
/*      */       private final boolean messageSetWireFormat;
/*      */       
/*      */       private ExtensionWriter(boolean messageSetWireFormat) {
/* 1175 */         if (this.iter.hasNext()) {
/* 1176 */           this.next = this.iter.next();
/*      */         }
/* 1178 */         this.messageSetWireFormat = messageSetWireFormat;
/*      */       }
/*      */ 
/*      */       
/*      */       public void writeUntil(int end, CodedOutputStream output) throws IOException {
/* 1183 */         while (this.next != null && ((Descriptors.FieldDescriptor)this.next.getKey()).getNumber() < end) {
/* 1184 */           Descriptors.FieldDescriptor descriptor = this.next.getKey();
/* 1185 */           if (this.messageSetWireFormat && descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && 
/*      */             
/* 1187 */             !descriptor.isRepeated()) {
/* 1188 */             if (this.next instanceof LazyField.LazyEntry) {
/* 1189 */               output.writeRawMessageSetExtension(descriptor.getNumber(), ((LazyField.LazyEntry)this.next)
/* 1190 */                   .getField().toByteString());
/*      */             } else {
/* 1192 */               output.writeMessageSetExtension(descriptor.getNumber(), (Message)this.next
/* 1193 */                   .getValue());
/*      */ 
/*      */             
/*      */             }
/*      */ 
/*      */           
/*      */           }
/*      */           else {
/*      */ 
/*      */             
/* 1203 */             FieldSet.writeField(descriptor, this.next.getValue(), output);
/*      */           } 
/* 1205 */           if (this.iter.hasNext()) {
/* 1206 */             this.next = this.iter.next(); continue;
/*      */           } 
/* 1208 */           this.next = null;
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     protected ExtensionWriter newExtensionWriter() {
/* 1215 */       return new ExtensionWriter(false);
/*      */     }
/*      */     protected ExtensionWriter newMessageSetExtensionWriter() {
/* 1218 */       return new ExtensionWriter(true);
/*      */     }
/*      */ 
/*      */     
/*      */     protected int extensionsSerializedSize() {
/* 1223 */       return this.extensions.getSerializedSize();
/*      */     }
/*      */     protected int extensionsSerializedSizeAsMessageSet() {
/* 1226 */       return this.extensions.getMessageSetSerializedSize();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Map<Descriptors.FieldDescriptor, Object> getExtensionFields() {
/* 1233 */       return this.extensions.getAllFields();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/* 1239 */       Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable(false);
/* 1240 */       result.putAll(getExtensionFields());
/* 1241 */       return Collections.unmodifiableMap(result);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
/* 1247 */       Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable(false);
/* 1248 */       result.putAll(getExtensionFields());
/* 1249 */       return Collections.unmodifiableMap(result);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasField(Descriptors.FieldDescriptor field) {
/* 1254 */       if (field.isExtension()) {
/* 1255 */         verifyContainingType(field);
/* 1256 */         return this.extensions.hasField(field);
/*      */       } 
/* 1258 */       return super.hasField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getField(Descriptors.FieldDescriptor field) {
/* 1264 */       if (field.isExtension()) {
/* 1265 */         verifyContainingType(field);
/* 1266 */         Object value = this.extensions.getField(field);
/* 1267 */         if (value == null) {
/* 1268 */           if (field.isRepeated())
/* 1269 */             return Collections.emptyList(); 
/* 1270 */           if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
/*      */           {
/*      */             
/* 1273 */             return DynamicMessage.getDefaultInstance(field.getMessageType());
/*      */           }
/* 1275 */           return field.getDefaultValue();
/*      */         } 
/*      */         
/* 1278 */         return value;
/*      */       } 
/*      */       
/* 1281 */       return super.getField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/* 1287 */       if (field.isExtension()) {
/* 1288 */         verifyContainingType(field);
/* 1289 */         return this.extensions.getRepeatedFieldCount(field);
/*      */       } 
/* 1291 */       return super.getRepeatedFieldCount(field);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/* 1298 */       if (field.isExtension()) {
/* 1299 */         verifyContainingType(field);
/* 1300 */         return this.extensions.getRepeatedField(field, index);
/*      */       } 
/* 1302 */       return super.getRepeatedField(field, index);
/*      */     }
/*      */ 
/*      */     
/*      */     private void verifyContainingType(Descriptors.FieldDescriptor field) {
/* 1307 */       if (field.getContainingType() != getDescriptorForType()) {
/* 1308 */         throw new IllegalArgumentException("FieldDescriptor does not match message type.");
/*      */       }
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
/*      */   public static abstract class ExtendableBuilder<MessageType extends ExtendableMessage, BuilderType extends ExtendableBuilder<MessageType, BuilderType>>
/*      */     extends Builder<BuilderType>
/*      */     implements ExtendableMessageOrBuilder<MessageType>
/*      */   {
/*      */     private FieldSet.Builder<Descriptors.FieldDescriptor> extensions;
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
/*      */     protected ExtendableBuilder() {}
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
/*      */     protected ExtendableBuilder(GeneratedMessageV3.BuilderParent parent) {
/* 1364 */       super(parent);
/*      */     }
/*      */ 
/*      */     
/*      */     void internalSetExtensionSet(FieldSet<Descriptors.FieldDescriptor> extensions) {
/* 1369 */       this.extensions = FieldSet.Builder.fromFieldSet(extensions);
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType clear() {
/* 1374 */       this.extensions = null;
/* 1375 */       return super.clear();
/*      */     }
/*      */     
/*      */     private void ensureExtensionsIsMutable() {
/* 1379 */       if (this.extensions == null) {
/* 1380 */         this.extensions = FieldSet.newBuilder();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
/* 1386 */       if (extension.getDescriptor().getContainingType() != 
/* 1387 */         getDescriptorForType())
/*      */       {
/* 1389 */         throw new IllegalArgumentException("Extension is for type \"" + extension
/*      */             
/* 1391 */             .getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + 
/*      */             
/* 1393 */             getDescriptorForType().getFullName() + "\".");
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
/* 1400 */       Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
/*      */       
/* 1402 */       verifyExtensionContainingType(extension);
/* 1403 */       return (this.extensions == null) ? false : this.extensions.hasField(extension.getDescriptor());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
/* 1410 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1412 */       verifyExtensionContainingType(extension);
/* 1413 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1414 */       return (this.extensions == null) ? 0 : this.extensions.getRepeatedFieldCount(descriptor);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
/* 1420 */       Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
/*      */       
/* 1422 */       verifyExtensionContainingType(extension);
/* 1423 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1424 */       Object value = (this.extensions == null) ? null : this.extensions.getField(descriptor);
/* 1425 */       if (value == null) {
/* 1426 */         if (descriptor.isRepeated())
/* 1427 */           return (Type)Collections.emptyList(); 
/* 1428 */         if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
/*      */         {
/* 1430 */           return (Type)extension.getMessageDefaultInstance();
/*      */         }
/* 1432 */         return (Type)extension.fromReflectionType(descriptor
/* 1433 */             .getDefaultValue());
/*      */       } 
/*      */       
/* 1436 */       return (Type)extension.fromReflectionType(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
/* 1444 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1446 */       verifyExtensionContainingType(extension);
/* 1447 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1448 */       if (this.extensions == null) {
/* 1449 */         throw new IndexOutOfBoundsException();
/*      */       }
/* 1451 */       return (Type)extension
/* 1452 */         .singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extensionLite, Type value) {
/* 1459 */       Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
/*      */       
/* 1461 */       verifyExtensionContainingType(extension);
/* 1462 */       ensureExtensionsIsMutable();
/* 1463 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1464 */       this.extensions.setField(descriptor, extension.toReflectionType(value));
/* 1465 */       onChanged();
/* 1466 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index, Type value) {
/* 1473 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1475 */       verifyExtensionContainingType(extension);
/* 1476 */       ensureExtensionsIsMutable();
/* 1477 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1478 */       this.extensions.setRepeatedField(descriptor, index, extension
/*      */           
/* 1480 */           .singularToReflectionType(value));
/* 1481 */       onChanged();
/* 1482 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extensionLite, Type value) {
/* 1489 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1491 */       verifyExtensionContainingType(extension);
/* 1492 */       ensureExtensionsIsMutable();
/* 1493 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1494 */       this.extensions.addRepeatedField(descriptor, extension
/* 1495 */           .singularToReflectionType(value));
/* 1496 */       onChanged();
/* 1497 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public final BuilderType clearExtension(ExtensionLite<MessageType, ?> extensionLite) {
/* 1502 */       Extension<MessageType, ?> extension = GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1504 */       verifyExtensionContainingType(extension);
/* 1505 */       ensureExtensionsIsMutable();
/* 1506 */       this.extensions.clearField(extension.getDescriptor());
/* 1507 */       onChanged();
/* 1508 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
/* 1514 */       return hasExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
/* 1520 */       return hasExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
/* 1526 */       return getExtensionCount(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
/* 1532 */       return getExtensionCount(extension);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
/* 1537 */       return getExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
/* 1543 */       return getExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
/* 1549 */       return getExtension(extension, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
/* 1555 */       return getExtension(extension, index);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(Extension<MessageType, Type> extension, Type value) {
/* 1560 */       return setExtension(extension, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension, Type value) {
/* 1565 */       return setExtension(extension, value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(Extension<MessageType, List<Type>> extension, int index, Type value) {
/* 1571 */       return setExtension(extension, index, value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index, Type value) {
/* 1577 */       return setExtension(extension, index, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType addExtension(Extension<MessageType, List<Type>> extension, Type value) {
/* 1582 */       return addExtension(extension, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public <Type> BuilderType addExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, Type value) {
/* 1587 */       return addExtension(extension, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType clearExtension(Extension<MessageType, ?> extension) {
/* 1592 */       return clearExtension(extension);
/*      */     }
/*      */ 
/*      */     
/*      */     public <Type> BuilderType clearExtension(GeneratedMessage.GeneratedExtension<MessageType, ?> extension) {
/* 1597 */       return clearExtension(extension);
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean extensionsAreInitialized() {
/* 1602 */       return (this.extensions == null) ? true : this.extensions.isInitialized();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
/* 1610 */       return (this.extensions == null) ? 
/* 1611 */         FieldSet.<Descriptors.FieldDescriptor>emptySet() : this.extensions
/* 1612 */         .build();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isInitialized() {
/* 1617 */       return (super.isInitialized() && extensionsAreInitialized());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/* 1625 */       Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable();
/* 1626 */       if (this.extensions != null) {
/* 1627 */         result.putAll(this.extensions.getAllFields());
/*      */       }
/* 1629 */       return Collections.unmodifiableMap(result);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getField(Descriptors.FieldDescriptor field) {
/* 1634 */       if (field.isExtension()) {
/* 1635 */         verifyContainingType(field);
/* 1636 */         Object value = (this.extensions == null) ? null : this.extensions.getField(field);
/* 1637 */         if (value == null) {
/* 1638 */           if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
/*      */           {
/*      */             
/* 1641 */             return DynamicMessage.getDefaultInstance(field.getMessageType());
/*      */           }
/* 1643 */           return field.getDefaultValue();
/*      */         } 
/*      */         
/* 1646 */         return value;
/*      */       } 
/*      */       
/* 1649 */       return super.getField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
/* 1655 */       if (field.isExtension()) {
/* 1656 */         verifyContainingType(field);
/* 1657 */         if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 1658 */           throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
/*      */         }
/*      */         
/* 1661 */         ensureExtensionsIsMutable();
/* 1662 */         Object value = this.extensions.getFieldAllowBuilders(field);
/* 1663 */         if (value == null) {
/* 1664 */           Message.Builder builder = DynamicMessage.newBuilder(field.getMessageType());
/* 1665 */           this.extensions.setField(field, builder);
/* 1666 */           onChanged();
/* 1667 */           return builder;
/*      */         } 
/* 1669 */         if (value instanceof Message.Builder)
/* 1670 */           return (Message.Builder)value; 
/* 1671 */         if (value instanceof Message) {
/* 1672 */           Message.Builder builder = ((Message)value).toBuilder();
/* 1673 */           this.extensions.setField(field, builder);
/* 1674 */           onChanged();
/* 1675 */           return builder;
/*      */         } 
/* 1677 */         throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1682 */       return super.getFieldBuilder(field);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/* 1688 */       if (field.isExtension()) {
/* 1689 */         verifyContainingType(field);
/* 1690 */         return (this.extensions == null) ? 0 : this.extensions.getRepeatedFieldCount(field);
/*      */       } 
/* 1692 */       return super.getRepeatedFieldCount(field);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/* 1699 */       if (field.isExtension()) {
/* 1700 */         verifyContainingType(field);
/* 1701 */         if (this.extensions == null) {
/* 1702 */           throw new IndexOutOfBoundsException();
/*      */         }
/* 1704 */         return this.extensions.getRepeatedField(field, index);
/*      */       } 
/* 1706 */       return super.getRepeatedField(field, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
/* 1712 */       if (field.isExtension()) {
/* 1713 */         verifyContainingType(field);
/* 1714 */         ensureExtensionsIsMutable();
/* 1715 */         if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 1716 */           throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
/*      */         }
/*      */         
/* 1719 */         Object value = this.extensions.getRepeatedFieldAllowBuilders(field, index);
/* 1720 */         if (value instanceof Message.Builder)
/* 1721 */           return (Message.Builder)value; 
/* 1722 */         if (value instanceof Message) {
/* 1723 */           Message.Builder builder = ((Message)value).toBuilder();
/* 1724 */           this.extensions.setRepeatedField(field, index, builder);
/* 1725 */           onChanged();
/* 1726 */           return builder;
/*      */         } 
/* 1728 */         throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
/*      */       } 
/*      */ 
/*      */       
/* 1732 */       return super.getRepeatedFieldBuilder(field, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean hasField(Descriptors.FieldDescriptor field) {
/* 1738 */       if (field.isExtension()) {
/* 1739 */         verifyContainingType(field);
/* 1740 */         return (this.extensions == null) ? false : this.extensions.hasField(field);
/*      */       } 
/* 1742 */       return super.hasField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
/* 1749 */       if (field.isExtension()) {
/* 1750 */         verifyContainingType(field);
/* 1751 */         ensureExtensionsIsMutable();
/* 1752 */         this.extensions.setField(field, value);
/* 1753 */         onChanged();
/* 1754 */         return (BuilderType)this;
/*      */       } 
/* 1756 */       return super.setField(field, value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType clearField(Descriptors.FieldDescriptor field) {
/* 1762 */       if (field.isExtension()) {
/* 1763 */         verifyContainingType(field);
/* 1764 */         ensureExtensionsIsMutable();
/* 1765 */         this.extensions.clearField(field);
/* 1766 */         onChanged();
/* 1767 */         return (BuilderType)this;
/*      */       } 
/* 1769 */       return super.clearField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 1776 */       if (field.isExtension()) {
/* 1777 */         verifyContainingType(field);
/* 1778 */         ensureExtensionsIsMutable();
/* 1779 */         this.extensions.setRepeatedField(field, index, value);
/* 1780 */         onChanged();
/* 1781 */         return (BuilderType)this;
/*      */       } 
/* 1783 */       return super.setRepeatedField(field, index, value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 1790 */       if (field.isExtension()) {
/* 1791 */         verifyContainingType(field);
/* 1792 */         ensureExtensionsIsMutable();
/* 1793 */         this.extensions.addRepeatedField(field, value);
/* 1794 */         onChanged();
/* 1795 */         return (BuilderType)this;
/*      */       } 
/* 1797 */       return super.addRepeatedField(field, value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
/* 1803 */       if (field.isExtension()) {
/* 1804 */         return DynamicMessage.newBuilder(field.getMessageType());
/*      */       }
/* 1806 */       return super.newBuilderForField(field);
/*      */     }
/*      */ 
/*      */     
/*      */     protected final void mergeExtensionFields(GeneratedMessageV3.ExtendableMessage other) {
/* 1811 */       if (other.extensions != null) {
/* 1812 */         ensureExtensionsIsMutable();
/* 1813 */         this.extensions.mergeFrom(other.extensions);
/* 1814 */         onChanged();
/*      */       } 
/*      */     }
/*      */     
/*      */     private void verifyContainingType(Descriptors.FieldDescriptor field) {
/* 1819 */       if (field.getContainingType() != getDescriptorForType()) {
/* 1820 */         throw new IllegalArgumentException("FieldDescriptor does not match message type.");
/*      */       }
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
/*      */   private static Method getMethodOrDie(Class clazz, String name, Class... params) {
/*      */     try {
/* 1844 */       return clazz.getMethod(name, params);
/* 1845 */     } catch (NoSuchMethodException e) {
/* 1846 */       throw new RuntimeException("Generated message class \"" + clazz
/* 1847 */           .getName() + "\" missing method \"" + name + "\".", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Object invokeOrDie(Method method, Object object, Object... params) {
/*      */     try {
/* 1856 */       return method.invoke(object, params);
/* 1857 */     } catch (IllegalAccessException e) {
/* 1858 */       throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
/*      */     
/*      */     }
/* 1861 */     catch (InvocationTargetException e) {
/* 1862 */       Throwable cause = e.getCause();
/* 1863 */       if (cause instanceof RuntimeException)
/* 1864 */         throw (RuntimeException)cause; 
/* 1865 */       if (cause instanceof Error) {
/* 1866 */         throw (Error)cause;
/*      */       }
/* 1868 */       throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
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
/*      */   protected MapField internalGetMapField(int fieldNumber) {
/* 1887 */     throw new RuntimeException("No map fields found in " + 
/* 1888 */         getClass().getName());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class FieldAccessorTable
/*      */   {
/*      */     private final Descriptors.Descriptor descriptor;
/*      */ 
/*      */     
/*      */     private final FieldAccessor[] fields;
/*      */ 
/*      */     
/*      */     private String[] camelCaseNames;
/*      */ 
/*      */     
/*      */     private final OneofAccessor[] oneofs;
/*      */ 
/*      */     
/*      */     private volatile boolean initialized;
/*      */ 
/*      */ 
/*      */     
/*      */     public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
/* 1912 */       this(descriptor, camelCaseNames);
/* 1913 */       ensureFieldAccessorsInitialized(messageClass, builderClass);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames) {
/* 1923 */       this.descriptor = descriptor;
/* 1924 */       this.camelCaseNames = camelCaseNames;
/* 1925 */       this.fields = new FieldAccessor[descriptor.getFields().size()];
/* 1926 */       this.oneofs = new OneofAccessor[descriptor.getOneofs().size()];
/* 1927 */       this.initialized = false;
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
/*      */     public FieldAccessorTable ensureFieldAccessorsInitialized(Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
/* 1940 */       if (this.initialized) return this; 
/* 1941 */       synchronized (this) {
/* 1942 */         if (this.initialized) return this; 
/* 1943 */         int fieldsSize = this.fields.length;
/* 1944 */         for (int i = 0; i < fieldsSize; i++) {
/* 1945 */           Descriptors.FieldDescriptor field = this.descriptor.getFields().get(i);
/* 1946 */           String containingOneofCamelCaseName = null;
/* 1947 */           if (field.getContainingOneof() != null)
/*      */           {
/* 1949 */             containingOneofCamelCaseName = this.camelCaseNames[fieldsSize + field.getContainingOneof().getIndex()];
/*      */           }
/* 1951 */           if (field.isRepeated()) {
/* 1952 */             if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 1953 */               if (field.isMapField()) {
/* 1954 */                 this.fields[i] = new MapFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
/*      */               } else {
/*      */                 
/* 1957 */                 this.fields[i] = new RepeatedMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
/*      */               }
/*      */             
/* 1960 */             } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
/* 1961 */               this.fields[i] = new RepeatedEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
/*      */             } else {
/*      */               
/* 1964 */               this.fields[i] = new RepeatedFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
/*      */             }
/*      */           
/*      */           }
/* 1968 */           else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 1969 */             this.fields[i] = new SingularMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
/*      */           
/*      */           }
/* 1972 */           else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
/* 1973 */             this.fields[i] = new SingularEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
/*      */           
/*      */           }
/* 1976 */           else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
/* 1977 */             this.fields[i] = new SingularStringFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
/*      */           }
/*      */           else {
/*      */             
/* 1981 */             this.fields[i] = new SingularFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1988 */         int oneofsSize = this.oneofs.length;
/* 1989 */         for (int j = 0; j < oneofsSize; j++) {
/* 1990 */           this.oneofs[j] = new OneofAccessor(this.descriptor, this.camelCaseNames[j + fieldsSize], messageClass, builderClass);
/*      */         }
/*      */ 
/*      */         
/* 1994 */         this.initialized = true;
/* 1995 */         this.camelCaseNames = null;
/* 1996 */         return this;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private FieldAccessor getField(Descriptors.FieldDescriptor field) {
/* 2008 */       if (field.getContainingType() != this.descriptor) {
/* 2009 */         throw new IllegalArgumentException("FieldDescriptor does not match message type.");
/*      */       }
/* 2011 */       if (field.isExtension())
/*      */       {
/*      */         
/* 2014 */         throw new IllegalArgumentException("This type does not have extensions.");
/*      */       }
/*      */       
/* 2017 */       return this.fields[field.getIndex()];
/*      */     }
/*      */ 
/*      */     
/*      */     private OneofAccessor getOneof(Descriptors.OneofDescriptor oneof) {
/* 2022 */       if (oneof.getContainingType() != this.descriptor) {
/* 2023 */         throw new IllegalArgumentException("OneofDescriptor does not match message type.");
/*      */       }
/*      */       
/* 2026 */       return this.oneofs[oneof.getIndex()];
/*      */     }
/*      */     private static interface FieldAccessor {
/*      */       Object get(GeneratedMessageV3 param2GeneratedMessageV3);
/*      */       Object get(GeneratedMessageV3.Builder param2Builder);
/*      */       Object getRaw(GeneratedMessageV3 param2GeneratedMessageV3);
/*      */       Object getRaw(GeneratedMessageV3.Builder param2Builder);
/*      */       void set(GeneratedMessageV3.Builder param2Builder, Object param2Object);
/*      */       Object getRepeated(GeneratedMessageV3 param2GeneratedMessageV3, int param2Int);
/*      */       Object getRepeated(GeneratedMessageV3.Builder param2Builder, int param2Int);
/*      */       Object getRepeatedRaw(GeneratedMessageV3 param2GeneratedMessageV3, int param2Int);
/*      */       Object getRepeatedRaw(GeneratedMessageV3.Builder param2Builder, int param2Int);
/*      */       void setRepeated(GeneratedMessageV3.Builder param2Builder, int param2Int, Object param2Object);
/*      */       void addRepeated(GeneratedMessageV3.Builder param2Builder, Object param2Object);
/*      */       
/*      */       boolean has(GeneratedMessageV3 param2GeneratedMessageV3);
/*      */       
/*      */       boolean has(GeneratedMessageV3.Builder param2Builder);
/*      */       
/*      */       int getRepeatedCount(GeneratedMessageV3 param2GeneratedMessageV3);
/*      */       
/*      */       int getRepeatedCount(GeneratedMessageV3.Builder param2Builder);
/*      */       
/*      */       void clear(GeneratedMessageV3.Builder param2Builder);
/*      */       
/*      */       Message.Builder newBuilder();
/*      */       
/*      */       Message.Builder getBuilder(GeneratedMessageV3.Builder param2Builder);
/*      */       
/*      */       Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder param2Builder, int param2Int); }
/*      */     
/*      */     private static class OneofAccessor { private final Descriptors.Descriptor descriptor;
/*      */       private final Method caseMethod;
/*      */       private final Method caseMethodBuilder;
/*      */       private final Method clearMethod;
/*      */       
/*      */       OneofAccessor(Descriptors.Descriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
/* 2063 */         this.descriptor = descriptor;
/* 2064 */         this
/* 2065 */           .caseMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]);
/* 2066 */         this
/* 2067 */           .caseMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]);
/* 2068 */         this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean has(GeneratedMessageV3 message) {
/* 2077 */         if (((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() == 0) {
/* 2078 */           return false;
/*      */         }
/* 2080 */         return true;
/*      */       }
/*      */       
/*      */       public boolean has(GeneratedMessageV3.Builder builder) {
/* 2084 */         if (((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() == 0) {
/* 2085 */           return false;
/*      */         }
/* 2087 */         return true;
/*      */       }
/*      */       
/*      */       public Descriptors.FieldDescriptor get(GeneratedMessageV3 message) {
/* 2091 */         int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
/* 2092 */         if (fieldNumber > 0) {
/* 2093 */           return this.descriptor.findFieldByNumber(fieldNumber);
/*      */         }
/* 2095 */         return null;
/*      */       }
/*      */       
/*      */       public Descriptors.FieldDescriptor get(GeneratedMessageV3.Builder builder) {
/* 2099 */         int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
/* 2100 */         if (fieldNumber > 0) {
/* 2101 */           return this.descriptor.findFieldByNumber(fieldNumber);
/*      */         }
/* 2103 */         return null;
/*      */       }
/*      */       
/*      */       public void clear(GeneratedMessageV3.Builder builder) {
/* 2107 */         GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
/*      */       } }
/*      */ 
/*      */     
/*      */     private static boolean supportFieldPresence(Descriptors.FileDescriptor file) {
/* 2112 */       return (file.getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private static class SingularFieldAccessor
/*      */       implements FieldAccessor
/*      */     {
/*      */       protected final Class<?> type;
/*      */ 
/*      */       
/*      */       protected final Descriptors.FieldDescriptor field;
/*      */ 
/*      */       
/*      */       protected final boolean isOneofField;
/*      */       
/*      */       protected final boolean hasHasMethod;
/*      */       
/*      */       protected final MethodInvoker invoker;
/*      */ 
/*      */       
/*      */       private static final class ReflectionInvoker
/*      */         implements MethodInvoker
/*      */       {
/*      */         protected final Method getMethod;
/*      */         
/*      */         protected final Method getMethodBuilder;
/*      */         
/*      */         protected final Method setMethod;
/*      */         
/*      */         protected final Method hasMethod;
/*      */         
/*      */         protected final Method hasMethodBuilder;
/*      */         
/*      */         protected final Method clearMethod;
/*      */         
/*      */         protected final Method caseMethod;
/*      */         
/*      */         protected final Method caseMethodBuilder;
/*      */ 
/*      */         
/*      */         ReflectionInvoker(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName, boolean isOneofField, boolean hasHasMethod) {
/* 2154 */           this.getMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[0]);
/* 2155 */           this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[0]);
/* 2156 */           Class<?> type = this.getMethod.getReturnType();
/* 2157 */           this.setMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { type });
/* 2158 */           this.hasMethod = hasHasMethod ? GeneratedMessageV3.getMethodOrDie(messageClass, "has" + camelCaseName, new Class[0]) : null;
/* 2159 */           this
/* 2160 */             .hasMethodBuilder = hasHasMethod ? GeneratedMessageV3.getMethodOrDie(builderClass, "has" + camelCaseName, new Class[0]) : null;
/* 2161 */           this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
/* 2162 */           this
/*      */             
/* 2164 */             .caseMethod = isOneofField ? GeneratedMessageV3.getMethodOrDie(messageClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
/*      */           
/* 2166 */           this
/*      */             
/* 2168 */             .caseMethodBuilder = isOneofField ? GeneratedMessageV3.getMethodOrDie(builderClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*      */         public Object get(GeneratedMessageV3 message) {
/* 2174 */           return GeneratedMessageV3.invokeOrDie(this.getMethod, message, new Object[0]);
/*      */         }
/*      */ 
/*      */         
/*      */         public Object get(GeneratedMessageV3.Builder<?> builder) {
/* 2179 */           return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
/*      */         }
/*      */ 
/*      */         
/*      */         public int getOneofFieldNumber(GeneratedMessageV3 message) {
/* 2184 */           return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
/*      */         }
/*      */ 
/*      */         
/*      */         public int getOneofFieldNumber(GeneratedMessageV3.Builder<?> builder) {
/* 2189 */           return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
/*      */         }
/*      */ 
/*      */         
/*      */         public void set(GeneratedMessageV3.Builder<?> builder, Object value) {
/* 2194 */           GeneratedMessageV3.invokeOrDie(this.setMethod, builder, new Object[] { value });
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean has(GeneratedMessageV3 message) {
/* 2199 */           return ((Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethod, message, new Object[0])).booleanValue();
/*      */         }
/*      */ 
/*      */         
/*      */         public boolean has(GeneratedMessageV3.Builder<?> builder) {
/* 2204 */           return ((Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethodBuilder, builder, new Object[0])).booleanValue();
/*      */         }
/*      */ 
/*      */         
/*      */         public void clear(GeneratedMessageV3.Builder<?> builder) {
/* 2209 */           GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
/*      */         }
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       SingularFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName) {
/* 2219 */         this.isOneofField = (descriptor.getContainingOneof() != null);
/* 2220 */         this
/* 2221 */           .hasHasMethod = (GeneratedMessageV3.FieldAccessorTable.supportFieldPresence(descriptor.getFile()) || (!this.isOneofField && descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE));
/* 2222 */         ReflectionInvoker reflectionInvoker = new ReflectionInvoker(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName, this.isOneofField, this.hasHasMethod);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2231 */         this.field = descriptor;
/* 2232 */         this.type = reflectionInvoker.getMethod.getReturnType();
/* 2233 */         this.invoker = getMethodInvoker(reflectionInvoker);
/*      */       }
/*      */       
/*      */       static MethodInvoker getMethodInvoker(ReflectionInvoker accessor) {
/* 2237 */         return accessor;
/*      */       }
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
/*      */       public Object get(GeneratedMessageV3 message) {
/* 2251 */         return this.invoker.get(message);
/*      */       }
/*      */       
/*      */       public Object get(GeneratedMessageV3.Builder<?> builder) {
/* 2255 */         return this.invoker.get(builder);
/*      */       }
/*      */       
/*      */       public Object getRaw(GeneratedMessageV3 message) {
/* 2259 */         return get(message);
/*      */       }
/*      */       
/*      */       public Object getRaw(GeneratedMessageV3.Builder builder) {
/* 2263 */         return get(builder);
/*      */       }
/*      */       
/*      */       public void set(GeneratedMessageV3.Builder<?> builder, Object value) {
/* 2267 */         this.invoker.set(builder, value);
/*      */       }
/*      */       
/*      */       public Object getRepeated(GeneratedMessageV3 message, int index) {
/* 2271 */         throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
/*      */       }
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
/* 2275 */         throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessageV3.Builder builder, int index) {
/* 2280 */         throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
/*      */       }
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessageV3.Builder builder, int index) {
/* 2284 */         throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public void setRepeated(GeneratedMessageV3.Builder builder, int index, Object value) {
/* 2289 */         throw new UnsupportedOperationException("setRepeatedField() called on a singular field.");
/*      */       }
/*      */       
/*      */       public void addRepeated(GeneratedMessageV3.Builder builder, Object value) {
/* 2293 */         throw new UnsupportedOperationException("addRepeatedField() called on a singular field.");
/*      */       }
/*      */       
/*      */       public boolean has(GeneratedMessageV3 message) {
/* 2297 */         if (!this.hasHasMethod) {
/* 2298 */           if (this.isOneofField) {
/* 2299 */             return (this.invoker.getOneofFieldNumber(message) == this.field.getNumber());
/*      */           }
/* 2301 */           return !get(message).equals(this.field.getDefaultValue());
/*      */         } 
/* 2303 */         return this.invoker.has(message);
/*      */       }
/*      */       
/*      */       public boolean has(GeneratedMessageV3.Builder<?> builder) {
/* 2307 */         if (!this.hasHasMethod) {
/* 2308 */           if (this.isOneofField) {
/* 2309 */             return (this.invoker.getOneofFieldNumber(builder) == this.field.getNumber());
/*      */           }
/* 2311 */           return !get(builder).equals(this.field.getDefaultValue());
/*      */         } 
/* 2313 */         return this.invoker.has(builder);
/*      */       }
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessageV3 message) {
/* 2317 */         throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessageV3.Builder builder) {
/* 2322 */         throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public void clear(GeneratedMessageV3.Builder<?> builder) {
/* 2327 */         this.invoker.clear(builder);
/*      */       }
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 2331 */         throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder getBuilder(GeneratedMessageV3.Builder builder) {
/* 2336 */         throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
/*      */       }
/*      */       
/*      */       public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder builder, int index) {
/* 2340 */         throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
/*      */       }
/*      */       
/*      */       private static interface MethodInvoker
/*      */       {
/*      */         Object get(GeneratedMessageV3 param3GeneratedMessageV3);
/*      */         
/*      */         Object get(GeneratedMessageV3.Builder<?> param3Builder);
/*      */         
/*      */         int getOneofFieldNumber(GeneratedMessageV3 param3GeneratedMessageV3);
/*      */         
/*      */         int getOneofFieldNumber(GeneratedMessageV3.Builder<?> param3Builder);
/*      */         
/*      */         void set(GeneratedMessageV3.Builder<?> param3Builder, Object param3Object);
/*      */         
/*      */         boolean has(GeneratedMessageV3 param3GeneratedMessageV3);
/*      */         
/*      */         boolean has(GeneratedMessageV3.Builder<?> param3Builder);
/*      */         
/*      */         void clear(GeneratedMessageV3.Builder<?> param3Builder);
/*      */       }
/*      */     }
/*      */     
/*      */     private static class RepeatedFieldAccessor
/*      */       implements FieldAccessor
/*      */     {
/*      */       protected final Class type;
/*      */       protected final MethodInvoker invoker;
/*      */       
/*      */       private static final class ReflectionInvoker
/*      */         implements MethodInvoker
/*      */       {
/*      */         protected final Method getMethod;
/*      */         protected final Method getMethodBuilder;
/*      */         protected final Method getRepeatedMethod;
/*      */         protected final Method getRepeatedMethodBuilder;
/*      */         protected final Method setRepeatedMethod;
/*      */         protected final Method addRepeatedMethod;
/*      */         protected final Method getCountMethod;
/*      */         protected final Method getCountMethodBuilder;
/*      */         protected final Method clearMethod;
/*      */         
/*      */         ReflectionInvoker(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
/* 2383 */           this.getMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "List", new Class[0]);
/* 2384 */           this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "List", new Class[0]);
/* 2385 */           this.getRepeatedMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[] { int.class });
/* 2386 */           this
/* 2387 */             .getRepeatedMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[] { int.class });
/* 2388 */           Class<?> type = this.getRepeatedMethod.getReturnType();
/* 2389 */           this
/* 2390 */             .setRepeatedMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { int.class, type });
/* 2391 */           this.addRepeatedMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "add" + camelCaseName, new Class[] { type });
/* 2392 */           this.getCountMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Count", new Class[0]);
/* 2393 */           this.getCountMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Count", new Class[0]);
/* 2394 */           this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
/*      */         }
/*      */ 
/*      */         
/*      */         public Object get(GeneratedMessageV3 message) {
/* 2399 */           return GeneratedMessageV3.invokeOrDie(this.getMethod, message, new Object[0]);
/*      */         }
/*      */ 
/*      */         
/*      */         public Object get(GeneratedMessageV3.Builder<?> builder) {
/* 2404 */           return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*      */         public Object getRepeated(GeneratedMessageV3 message, int index) {
/* 2410 */           return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethod, message, new Object[] { Integer.valueOf(index) });
/*      */         }
/*      */ 
/*      */         
/*      */         public Object getRepeated(GeneratedMessageV3.Builder<?> builder, int index) {
/* 2415 */           return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethodBuilder, builder, new Object[] { Integer.valueOf(index) });
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*      */         public void setRepeated(GeneratedMessageV3.Builder<?> builder, int index, Object value) {
/* 2421 */           GeneratedMessageV3.invokeOrDie(this.setRepeatedMethod, builder, new Object[] { Integer.valueOf(index), value });
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*      */         public void addRepeated(GeneratedMessageV3.Builder<?> builder, Object value) {
/* 2427 */           GeneratedMessageV3.invokeOrDie(this.addRepeatedMethod, builder, new Object[] { value });
/*      */         }
/*      */ 
/*      */         
/*      */         public int getRepeatedCount(GeneratedMessageV3 message) {
/* 2432 */           return ((Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethod, message, new Object[0])).intValue();
/*      */         }
/*      */ 
/*      */         
/*      */         public int getRepeatedCount(GeneratedMessageV3.Builder<?> builder) {
/* 2437 */           return ((Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethodBuilder, builder, new Object[0])).intValue();
/*      */         }
/*      */ 
/*      */         
/*      */         public void clear(GeneratedMessageV3.Builder<?> builder) {
/* 2442 */           GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
/*      */         }
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       RepeatedFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
/* 2453 */         ReflectionInvoker reflectionInvoker = new ReflectionInvoker(descriptor, camelCaseName, messageClass, builderClass);
/*      */         
/* 2455 */         this.type = reflectionInvoker.getRepeatedMethod.getReturnType();
/* 2456 */         this.invoker = getMethodInvoker(reflectionInvoker);
/*      */       }
/*      */       
/*      */       static MethodInvoker getMethodInvoker(ReflectionInvoker accessor) {
/* 2460 */         return accessor;
/*      */       }
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessageV3 message) {
/* 2465 */         return this.invoker.get(message);
/*      */       }
/*      */       
/*      */       public Object get(GeneratedMessageV3.Builder<?> builder) {
/* 2469 */         return this.invoker.get(builder);
/*      */       }
/*      */       
/*      */       public Object getRaw(GeneratedMessageV3 message) {
/* 2473 */         return get(message);
/*      */       }
/*      */       
/*      */       public Object getRaw(GeneratedMessageV3.Builder builder) {
/* 2477 */         return get(builder);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessageV3.Builder builder, Object value) {
/* 2485 */         clear(builder);
/* 2486 */         for (Object element : value) {
/* 2487 */           addRepeated(builder, element);
/*      */         }
/*      */       }
/*      */       
/*      */       public Object getRepeated(GeneratedMessageV3 message, int index) {
/* 2492 */         return this.invoker.getRepeated(message, index);
/*      */       }
/*      */       
/*      */       public Object getRepeated(GeneratedMessageV3.Builder<?> builder, int index) {
/* 2496 */         return this.invoker.getRepeated(builder, index);
/*      */       }
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
/* 2500 */         return getRepeated(message, index);
/*      */       }
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessageV3.Builder builder, int index) {
/* 2504 */         return getRepeated(builder, index);
/*      */       }
/*      */       
/*      */       public void setRepeated(GeneratedMessageV3.Builder<?> builder, int index, Object value) {
/* 2508 */         this.invoker.setRepeated(builder, index, value);
/*      */       }
/*      */       
/*      */       public void addRepeated(GeneratedMessageV3.Builder<?> builder, Object value) {
/* 2512 */         this.invoker.addRepeated(builder, value);
/*      */       }
/*      */       
/*      */       public boolean has(GeneratedMessageV3 message) {
/* 2516 */         throw new UnsupportedOperationException("hasField() called on a repeated field.");
/*      */       }
/*      */       
/*      */       public boolean has(GeneratedMessageV3.Builder builder) {
/* 2520 */         throw new UnsupportedOperationException("hasField() called on a repeated field.");
/*      */       }
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessageV3 message) {
/* 2524 */         return this.invoker.getRepeatedCount(message);
/*      */       }
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessageV3.Builder<?> builder) {
/* 2528 */         return this.invoker.getRepeatedCount(builder);
/*      */       }
/*      */       
/*      */       public void clear(GeneratedMessageV3.Builder<?> builder) {
/* 2532 */         this.invoker.clear(builder);
/*      */       }
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 2536 */         throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder getBuilder(GeneratedMessageV3.Builder builder) {
/* 2541 */         throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
/*      */       }
/*      */       
/*      */       public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder builder, int index) {
/* 2545 */         throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
/*      */       } static interface MethodInvoker { Object get(GeneratedMessageV3 param3GeneratedMessageV3); Object get(GeneratedMessageV3.Builder<?> param3Builder); Object getRepeated(GeneratedMessageV3 param3GeneratedMessageV3, int param3Int);
/*      */         Object getRepeated(GeneratedMessageV3.Builder<?> param3Builder, int param3Int);
/*      */         void setRepeated(GeneratedMessageV3.Builder<?> param3Builder, int param3Int, Object param3Object);
/*      */         void addRepeated(GeneratedMessageV3.Builder<?> param3Builder, Object param3Object);
/*      */         int getRepeatedCount(GeneratedMessageV3 param3GeneratedMessageV3);
/*      */         int getRepeatedCount(GeneratedMessageV3.Builder<?> param3Builder);
/*      */         void clear(GeneratedMessageV3.Builder<?> param3Builder); } }
/*      */     private static class MapFieldAccessor implements FieldAccessor { private final Descriptors.FieldDescriptor field; private final Message mapEntryMessageDefaultInstance;
/*      */       MapFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
/* 2555 */         this.field = descriptor;
/*      */         
/* 2557 */         Method getDefaultInstanceMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "getDefaultInstance", new Class[0]);
/* 2558 */         MapField<?, ?> defaultMapField = getMapField(
/* 2559 */             (GeneratedMessageV3)GeneratedMessageV3.invokeOrDie(getDefaultInstanceMethod, null, new Object[0]));
/* 2560 */         this
/* 2561 */           .mapEntryMessageDefaultInstance = defaultMapField.getMapEntryMessageDefaultInstance();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private MapField<?, ?> getMapField(GeneratedMessageV3 message) {
/* 2568 */         return message.internalGetMapField(this.field.getNumber());
/*      */       }
/*      */       
/*      */       private MapField<?, ?> getMapField(GeneratedMessageV3.Builder builder) {
/* 2572 */         return builder.internalGetMapField(this.field.getNumber());
/*      */       }
/*      */ 
/*      */       
/*      */       private MapField<?, ?> getMutableMapField(GeneratedMessageV3.Builder builder) {
/* 2577 */         return builder.internalGetMutableMapField(this.field
/* 2578 */             .getNumber());
/*      */       }
/*      */       
/*      */       private Message coerceType(Message value) {
/* 2582 */         if (value == null) {
/* 2583 */           return null;
/*      */         }
/* 2585 */         if (this.mapEntryMessageDefaultInstance.getClass().isInstance(value)) {
/* 2586 */           return value;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2592 */         return this.mapEntryMessageDefaultInstance.toBuilder().mergeFrom(value).build();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessageV3 message) {
/* 2598 */         List<Object> result = new ArrayList();
/* 2599 */         for (int i = 0; i < getRepeatedCount(message); i++) {
/* 2600 */           result.add(getRepeated(message, i));
/*      */         }
/* 2602 */         return Collections.unmodifiableList(result);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessageV3.Builder builder) {
/* 2608 */         List<Object> result = new ArrayList();
/* 2609 */         for (int i = 0; i < getRepeatedCount(builder); i++) {
/* 2610 */           result.add(getRepeated(builder, i));
/*      */         }
/* 2612 */         return Collections.unmodifiableList(result);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRaw(GeneratedMessageV3 message) {
/* 2617 */         return get(message);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRaw(GeneratedMessageV3.Builder builder) {
/* 2622 */         return get(builder);
/*      */       }
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessageV3.Builder builder, Object value) {
/* 2627 */         clear(builder);
/* 2628 */         for (Object entry : value) {
/* 2629 */           addRepeated(builder, entry);
/*      */         }
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessageV3 message, int index) {
/* 2635 */         return getMapField(message).getList().get(index);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessageV3.Builder builder, int index) {
/* 2640 */         return getMapField(builder).getList().get(index);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
/* 2645 */         return getRepeated(message, index);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessageV3.Builder builder, int index) {
/* 2650 */         return getRepeated(builder, index);
/*      */       }
/*      */ 
/*      */       
/*      */       public void setRepeated(GeneratedMessageV3.Builder builder, int index, Object value) {
/* 2655 */         getMutableMapField(builder).getMutableList().set(index, coerceType((Message)value));
/*      */       }
/*      */ 
/*      */       
/*      */       public void addRepeated(GeneratedMessageV3.Builder builder, Object value) {
/* 2660 */         getMutableMapField(builder).getMutableList().add(coerceType((Message)value));
/*      */       }
/*      */ 
/*      */       
/*      */       public boolean has(GeneratedMessageV3 message) {
/* 2665 */         throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean has(GeneratedMessageV3.Builder builder) {
/* 2671 */         throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessageV3 message) {
/* 2677 */         return getMapField(message).getList().size();
/*      */       }
/*      */ 
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessageV3.Builder builder) {
/* 2682 */         return getMapField(builder).getList().size();
/*      */       }
/*      */ 
/*      */       
/*      */       public void clear(GeneratedMessageV3.Builder builder) {
/* 2687 */         getMutableMapField(builder).getMutableList().clear();
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 2692 */         return this.mapEntryMessageDefaultInstance.newBuilderForType();
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder getBuilder(GeneratedMessageV3.Builder builder) {
/* 2697 */         throw new UnsupportedOperationException("Nested builder not supported for map fields.");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder builder, int index) {
/* 2703 */         throw new UnsupportedOperationException("Nested builder not supported for map fields.");
/*      */       } }
/*      */ 
/*      */     
/*      */     private static final class SingularEnumFieldAccessor extends SingularFieldAccessor {
/*      */       private Descriptors.EnumDescriptor enumDescriptor;
/*      */       private Method valueOfMethod;
/*      */       private Method getValueDescriptorMethod;
/*      */       private boolean supportUnknownEnumValue;
/*      */       private Method getValueMethod;
/*      */       private Method getValueMethodBuilder;
/*      */       private Method setValueMethod;
/*      */       
/*      */       SingularEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName) {
/* 2717 */         super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
/*      */         
/* 2719 */         this.enumDescriptor = descriptor.getEnumType();
/*      */         
/* 2721 */         this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
/* 2722 */         this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
/*      */         
/* 2724 */         this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
/* 2725 */         if (this.supportUnknownEnumValue) {
/* 2726 */           this
/* 2727 */             .getValueMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[0]);
/* 2728 */           this
/* 2729 */             .getValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[0]);
/* 2730 */           this
/* 2731 */             .setValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[] { int.class });
/*      */         } 
/*      */       }
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
/*      */       public Object get(GeneratedMessageV3 message) {
/* 2747 */         if (this.supportUnknownEnumValue) {
/* 2748 */           int value = ((Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethod, message, new Object[0])).intValue();
/* 2749 */           return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
/*      */         } 
/* 2751 */         return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(message), new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessageV3.Builder builder) {
/* 2756 */         if (this.supportUnknownEnumValue) {
/* 2757 */           int value = ((Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethodBuilder, builder, new Object[0])).intValue();
/* 2758 */           return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
/*      */         } 
/* 2760 */         return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(builder), new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessageV3.Builder builder, Object value) {
/* 2765 */         if (this.supportUnknownEnumValue) {
/* 2766 */           GeneratedMessageV3.invokeOrDie(this.setValueMethod, builder, new Object[] {
/* 2767 */                 Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
/*      */           return;
/*      */         } 
/* 2770 */         super.set(builder, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
/*      */       } }
/*      */     private static final class RepeatedEnumFieldAccessor extends RepeatedFieldAccessor { private Descriptors.EnumDescriptor enumDescriptor; private final Method valueOfMethod; private final Method getValueDescriptorMethod;
/*      */       private boolean supportUnknownEnumValue;
/*      */       private Method getRepeatedValueMethod;
/*      */       private Method getRepeatedValueMethodBuilder;
/*      */       private Method setRepeatedValueMethod;
/*      */       private Method addRepeatedValueMethod;
/*      */       
/*      */       RepeatedEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
/* 2780 */         super(descriptor, camelCaseName, messageClass, builderClass);
/*      */         
/* 2782 */         this.enumDescriptor = descriptor.getEnumType();
/*      */         
/* 2784 */         this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
/* 2785 */         this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
/*      */         
/* 2787 */         this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
/* 2788 */         if (this.supportUnknownEnumValue) {
/* 2789 */           this
/* 2790 */             .getRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[] { int.class });
/* 2791 */           this
/* 2792 */             .getRepeatedValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[] { int.class });
/* 2793 */           this
/* 2794 */             .setRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[] { int.class, int.class });
/* 2795 */           this
/* 2796 */             .addRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "add" + camelCaseName + "Value", new Class[] { int.class });
/*      */         } 
/*      */       }
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
/*      */       public Object get(GeneratedMessageV3 message) {
/* 2813 */         List<Object> newList = new ArrayList();
/* 2814 */         int size = getRepeatedCount(message);
/* 2815 */         for (int i = 0; i < size; i++) {
/* 2816 */           newList.add(getRepeated(message, i));
/*      */         }
/* 2818 */         return Collections.unmodifiableList(newList);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessageV3.Builder builder) {
/* 2824 */         List<Object> newList = new ArrayList();
/* 2825 */         int size = getRepeatedCount(builder);
/* 2826 */         for (int i = 0; i < size; i++) {
/* 2827 */           newList.add(getRepeated(builder, i));
/*      */         }
/* 2829 */         return Collections.unmodifiableList(newList);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessageV3 message, int index) {
/* 2834 */         if (this.supportUnknownEnumValue) {
/* 2835 */           int value = ((Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethod, message, new Object[] { Integer.valueOf(index) })).intValue();
/* 2836 */           return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
/*      */         } 
/* 2838 */         return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(message, index), new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessageV3.Builder builder, int index) {
/* 2843 */         if (this.supportUnknownEnumValue) {
/* 2844 */           int value = ((Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethodBuilder, builder, new Object[] { Integer.valueOf(index) })).intValue();
/* 2845 */           return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
/*      */         } 
/* 2847 */         return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(builder, index), new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public void setRepeated(GeneratedMessageV3.Builder builder, int index, Object value) {
/* 2852 */         if (this.supportUnknownEnumValue) {
/* 2853 */           GeneratedMessageV3.invokeOrDie(this.setRepeatedValueMethod, builder, new Object[] { Integer.valueOf(index), 
/* 2854 */                 Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
/*      */           return;
/*      */         } 
/* 2857 */         super.setRepeated(builder, index, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
/*      */       }
/*      */       
/*      */       public void addRepeated(GeneratedMessageV3.Builder builder, Object value) {
/* 2861 */         if (this.supportUnknownEnumValue) {
/* 2862 */           GeneratedMessageV3.invokeOrDie(this.addRepeatedValueMethod, builder, new Object[] {
/* 2863 */                 Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
/*      */           return;
/*      */         } 
/* 2866 */         super.addRepeated(builder, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
/*      */       } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static final class SingularStringFieldAccessor
/*      */       extends SingularFieldAccessor
/*      */     {
/*      */       private final Method getBytesMethod;
/*      */ 
/*      */ 
/*      */       
/*      */       private final Method getBytesMethodBuilder;
/*      */ 
/*      */ 
/*      */       
/*      */       private final Method setBytesMethodBuilder;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       SingularStringFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName) {
/* 2890 */         super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
/*      */         
/* 2892 */         this.getBytesMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Bytes", new Class[0]);
/*      */         
/* 2894 */         this.getBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Bytes", new Class[0]);
/*      */         
/* 2896 */         this.setBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Bytes", new Class[] { ByteString.class });
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Object getRaw(GeneratedMessageV3 message) {
/* 2906 */         return GeneratedMessageV3.invokeOrDie(this.getBytesMethod, message, new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRaw(GeneratedMessageV3.Builder builder) {
/* 2911 */         return GeneratedMessageV3.invokeOrDie(this.getBytesMethodBuilder, builder, new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessageV3.Builder builder, Object value) {
/* 2916 */         if (value instanceof ByteString) {
/* 2917 */           GeneratedMessageV3.invokeOrDie(this.setBytesMethodBuilder, builder, new Object[] { value });
/*      */         } else {
/* 2919 */           super.set(builder, value);
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private static final class SingularMessageFieldAccessor
/*      */       extends SingularFieldAccessor
/*      */     {
/*      */       private final Method newBuilderMethod;
/*      */       
/*      */       private final Method getBuilderMethodBuilder;
/*      */       
/*      */       SingularMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName) {
/* 2933 */         super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
/*      */ 
/*      */         
/* 2936 */         this.newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder", new Class[0]);
/* 2937 */         this
/* 2938 */           .getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[0]);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private Object coerceType(Object value) {
/* 2945 */         if (this.type.isInstance(value)) {
/* 2946 */           return value;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2952 */         return ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]))
/* 2953 */           .mergeFrom((Message)value)
/* 2954 */           .buildPartial();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessageV3.Builder builder, Object value) {
/* 2960 */         super.set(builder, coerceType(value));
/*      */       }
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 2964 */         return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
/*      */       }
/*      */       
/*      */       public Message.Builder getBuilder(GeneratedMessageV3.Builder builder) {
/* 2968 */         return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[0]);
/*      */       }
/*      */     }
/*      */     
/*      */     private static final class RepeatedMessageFieldAccessor
/*      */       extends RepeatedFieldAccessor {
/*      */       private final Method newBuilderMethod;
/*      */       private final Method getBuilderMethodBuilder;
/*      */       
/*      */       RepeatedMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
/* 2978 */         super(descriptor, camelCaseName, messageClass, builderClass);
/*      */         
/* 2980 */         this.newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder", new Class[0]);
/* 2981 */         this.getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[] { int.class });
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private Object coerceType(Object value) {
/* 2989 */         if (this.type.isInstance(value)) {
/* 2990 */           return value;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2996 */         return ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]))
/* 2997 */           .mergeFrom((Message)value)
/* 2998 */           .build();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public void setRepeated(GeneratedMessageV3.Builder builder, int index, Object value) {
/* 3004 */         super.setRepeated(builder, index, coerceType(value));
/*      */       }
/*      */       
/*      */       public void addRepeated(GeneratedMessageV3.Builder builder, Object value) {
/* 3008 */         super.addRepeated(builder, coerceType(value));
/*      */       }
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 3012 */         return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
/*      */       }
/*      */       
/*      */       public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder builder, int index)
/*      */       {
/* 3017 */         return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[] {
/* 3018 */               Integer.valueOf(index) });
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object writeReplace() throws ObjectStreamException {
/* 3030 */     return new GeneratedMessageLite.SerializedForm(this);
/*      */   }
/*      */   private static class OneofAccessor {
/*      */     private final Descriptors.Descriptor descriptor;
/*      */     private final Method caseMethod;
/*      */     private final Method caseMethodBuilder;
/*      */     private final Method clearMethod;
/*      */     OneofAccessor(Descriptors.Descriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) { this.descriptor = descriptor; this.caseMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]); this.caseMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]); this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]); } public boolean has(GeneratedMessageV3 message) { if (((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() == 0) return false;  return true; } public boolean has(GeneratedMessageV3.Builder builder) { if (((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() == 0) return false;  return true; } public Descriptors.FieldDescriptor get(GeneratedMessageV3 message) { int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber(); if (fieldNumber > 0) return this.descriptor.findFieldByNumber(fieldNumber);  return null; } public Descriptors.FieldDescriptor get(GeneratedMessageV3.Builder builder) { int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber(); if (fieldNumber > 0)
/* 3038 */         return this.descriptor.findFieldByNumber(fieldNumber);  return null; } public void clear(GeneratedMessageV3.Builder builder) { GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]); } } private static <MessageType extends ExtendableMessage<MessageType>, T> Extension<MessageType, T> checkNotLite(ExtensionLite<MessageType, T> extension) { if (extension.isLite()) {
/* 3039 */       throw new IllegalArgumentException("Expected non-lite extension.");
/*      */     }
/*      */     
/* 3042 */     return (Extension<MessageType, T>)extension; }
/*      */ 
/*      */   
/*      */   protected static int computeStringSize(int fieldNumber, Object value) {
/* 3046 */     if (value instanceof String) {
/* 3047 */       return CodedOutputStream.computeStringSize(fieldNumber, (String)value);
/*      */     }
/* 3049 */     return CodedOutputStream.computeBytesSize(fieldNumber, (ByteString)value);
/*      */   }
/*      */ 
/*      */   
/*      */   protected static int computeStringSizeNoTag(Object value) {
/* 3054 */     if (value instanceof String) {
/* 3055 */       return CodedOutputStream.computeStringSizeNoTag((String)value);
/*      */     }
/* 3057 */     return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void writeString(CodedOutputStream output, int fieldNumber, Object value) throws IOException {
/* 3063 */     if (value instanceof String) {
/* 3064 */       output.writeString(fieldNumber, (String)value);
/*      */     } else {
/* 3066 */       output.writeBytes(fieldNumber, (ByteString)value);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static void writeStringNoTag(CodedOutputStream output, Object value) throws IOException {
/* 3072 */     if (value instanceof String) {
/* 3073 */       output.writeStringNoTag((String)value);
/*      */     } else {
/* 3075 */       output.writeBytesNoTag((ByteString)value);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <V> void serializeIntegerMapTo(CodedOutputStream out, MapField<Integer, V> field, MapEntry<Integer, V> defaultEntry, int fieldNumber) throws IOException {
/* 3084 */     Map<Integer, V> m = field.getMap();
/* 3085 */     if (!out.isSerializationDeterministic()) {
/* 3086 */       serializeMapTo(out, m, defaultEntry, fieldNumber);
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 3091 */     int[] keys = new int[m.size()];
/* 3092 */     int index = 0;
/* 3093 */     for (Iterator<Integer> iterator = m.keySet().iterator(); iterator.hasNext(); ) { int k = ((Integer)iterator.next()).intValue();
/* 3094 */       keys[index++] = k; }
/*      */     
/* 3096 */     Arrays.sort(keys);
/* 3097 */     for (int key : keys) {
/* 3098 */       out.writeMessage(fieldNumber, defaultEntry
/* 3099 */           .newBuilderForType()
/* 3100 */           .setKey(Integer.valueOf(key))
/* 3101 */           .setValue(m.get(Integer.valueOf(key)))
/* 3102 */           .build());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <V> void serializeLongMapTo(CodedOutputStream out, MapField<Long, V> field, MapEntry<Long, V> defaultEntry, int fieldNumber) throws IOException {
/* 3112 */     Map<Long, V> m = field.getMap();
/* 3113 */     if (!out.isSerializationDeterministic()) {
/* 3114 */       serializeMapTo(out, m, defaultEntry, fieldNumber);
/*      */       
/*      */       return;
/*      */     } 
/* 3118 */     long[] keys = new long[m.size()];
/* 3119 */     int index = 0;
/* 3120 */     for (Iterator<Long> iterator = m.keySet().iterator(); iterator.hasNext(); ) { long k = ((Long)iterator.next()).longValue();
/* 3121 */       keys[index++] = k; }
/*      */     
/* 3123 */     Arrays.sort(keys);
/* 3124 */     for (long key : keys) {
/* 3125 */       out.writeMessage(fieldNumber, defaultEntry
/* 3126 */           .newBuilderForType()
/* 3127 */           .setKey(Long.valueOf(key))
/* 3128 */           .setValue(m.get(Long.valueOf(key)))
/* 3129 */           .build());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <V> void serializeStringMapTo(CodedOutputStream out, MapField<String, V> field, MapEntry<String, V> defaultEntry, int fieldNumber) throws IOException {
/* 3139 */     Map<String, V> m = field.getMap();
/* 3140 */     if (!out.isSerializationDeterministic()) {
/* 3141 */       serializeMapTo(out, m, defaultEntry, fieldNumber);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 3147 */     String[] keys = new String[m.size()];
/* 3148 */     keys = (String[])m.keySet().toArray((Object[])keys);
/* 3149 */     Arrays.sort((Object[])keys);
/* 3150 */     for (String key : keys) {
/* 3151 */       out.writeMessage(fieldNumber, defaultEntry
/* 3152 */           .newBuilderForType()
/* 3153 */           .setKey(key)
/* 3154 */           .setValue(m.get(key))
/* 3155 */           .build());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static <V> void serializeBooleanMapTo(CodedOutputStream out, MapField<Boolean, V> field, MapEntry<Boolean, V> defaultEntry, int fieldNumber) throws IOException {
/* 3165 */     Map<Boolean, V> m = field.getMap();
/* 3166 */     if (!out.isSerializationDeterministic()) {
/* 3167 */       serializeMapTo(out, m, defaultEntry, fieldNumber);
/*      */       return;
/*      */     } 
/* 3170 */     maybeSerializeBooleanEntryTo(out, m, defaultEntry, fieldNumber, false);
/* 3171 */     maybeSerializeBooleanEntryTo(out, m, defaultEntry, fieldNumber, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static <V> void maybeSerializeBooleanEntryTo(CodedOutputStream out, Map<Boolean, V> m, MapEntry<Boolean, V> defaultEntry, int fieldNumber, boolean key) throws IOException {
/* 3181 */     if (m.containsKey(Boolean.valueOf(key))) {
/* 3182 */       out.writeMessage(fieldNumber, defaultEntry
/* 3183 */           .newBuilderForType()
/* 3184 */           .setKey(Boolean.valueOf(key))
/* 3185 */           .setValue(m.get(Boolean.valueOf(key)))
/* 3186 */           .build());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static <K, V> void serializeMapTo(CodedOutputStream out, Map<K, V> m, MapEntry<K, V> defaultEntry, int fieldNumber) throws IOException {
/* 3197 */     for (Map.Entry<K, V> entry : m.entrySet())
/* 3198 */       out.writeMessage(fieldNumber, defaultEntry
/* 3199 */           .newBuilderForType()
/* 3200 */           .setKey(entry.getKey())
/* 3201 */           .setValue(entry.getValue())
/* 3202 */           .build()); 
/*      */   }
/*      */   
/*      */   protected abstract FieldAccessorTable internalGetFieldAccessorTable();
/*      */   
/*      */   protected abstract Message.Builder newBuilderForType(BuilderParent paramBuilderParent);
/*      */   
/*      */   static interface ExtensionDescriptorRetriever {
/*      */     Descriptors.FieldDescriptor getDescriptor();
/*      */   }
/*      */   
/*      */   public static interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage> extends MessageOrBuilder {
/*      */     Message getDefaultInstanceForType();
/*      */     
/*      */     <Type> boolean hasExtension(ExtensionLite<MessageType, Type> param1ExtensionLite);
/*      */     
/*      */     <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> param1ExtensionLite);
/*      */     
/*      */     <Type> Type getExtension(ExtensionLite<MessageType, Type> param1ExtensionLite);
/*      */     
/*      */     <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> param1ExtensionLite, int param1Int);
/*      */     
/*      */     <Type> boolean hasExtension(Extension<MessageType, Type> param1Extension);
/*      */     
/*      */     <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> param1GeneratedExtension);
/*      */     
/*      */     <Type> int getExtensionCount(Extension<MessageType, List<Type>> param1Extension);
/*      */     
/*      */     <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> param1GeneratedExtension);
/*      */     
/*      */     <Type> Type getExtension(Extension<MessageType, Type> param1Extension);
/*      */     
/*      */     <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> param1GeneratedExtension);
/*      */     
/*      */     <Type> Type getExtension(Extension<MessageType, List<Type>> param1Extension, int param1Int);
/*      */     
/*      */     <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> param1GeneratedExtension, int param1Int);
/*      */   }
/*      */   
/*      */   protected static interface BuilderParent extends AbstractMessage.BuilderParent {}
/*      */   
/*      */   private static interface FieldAccessor {
/*      */     Object get(GeneratedMessageV3 param1GeneratedMessageV3);
/*      */     
/*      */     Object get(GeneratedMessageV3.Builder param1Builder);
/*      */     
/*      */     Object getRaw(GeneratedMessageV3 param1GeneratedMessageV3);
/*      */     
/*      */     Object getRaw(GeneratedMessageV3.Builder param1Builder);
/*      */     
/*      */     void set(GeneratedMessageV3.Builder param1Builder, Object param1Object);
/*      */     
/*      */     Object getRepeated(GeneratedMessageV3 param1GeneratedMessageV3, int param1Int);
/*      */     
/*      */     Object getRepeated(GeneratedMessageV3.Builder param1Builder, int param1Int);
/*      */     
/*      */     Object getRepeatedRaw(GeneratedMessageV3 param1GeneratedMessageV3, int param1Int);
/*      */     
/*      */     Object getRepeatedRaw(GeneratedMessageV3.Builder param1Builder, int param1Int);
/*      */     
/*      */     void setRepeated(GeneratedMessageV3.Builder param1Builder, int param1Int, Object param1Object);
/*      */     
/*      */     void addRepeated(GeneratedMessageV3.Builder param1Builder, Object param1Object);
/*      */     
/*      */     boolean has(GeneratedMessageV3 param1GeneratedMessageV3);
/*      */     
/*      */     boolean has(GeneratedMessageV3.Builder param1Builder);
/*      */     
/*      */     int getRepeatedCount(GeneratedMessageV3 param1GeneratedMessageV3);
/*      */     
/*      */     int getRepeatedCount(GeneratedMessageV3.Builder param1Builder);
/*      */     
/*      */     void clear(GeneratedMessageV3.Builder param1Builder);
/*      */     
/*      */     Message.Builder newBuilder();
/*      */     
/*      */     Message.Builder getBuilder(GeneratedMessageV3.Builder param1Builder);
/*      */     
/*      */     Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder param1Builder, int param1Int);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\GeneratedMessageV3.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */