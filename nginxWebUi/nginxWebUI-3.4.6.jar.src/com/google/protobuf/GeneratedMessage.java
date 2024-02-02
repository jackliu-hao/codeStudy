/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectStreamException;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
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
/*      */ public abstract class GeneratedMessage
/*      */   extends AbstractMessage
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   protected static boolean alwaysUseFieldBuilders = false;
/*      */   protected UnknownFieldSet unknownFields;
/*      */   
/*      */   protected GeneratedMessage() {
/*   76 */     this.unknownFields = UnknownFieldSet.getDefaultInstance();
/*      */   }
/*      */   
/*      */   protected GeneratedMessage(Builder<?> builder) {
/*   80 */     this.unknownFields = builder.getUnknownFields();
/*      */   }
/*      */ 
/*      */   
/*      */   public Parser<? extends GeneratedMessage> getParserForType() {
/*   85 */     throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void enableAlwaysUseFieldBuildersForTesting() {
/*   96 */     alwaysUseFieldBuilders = true;
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
/*  108 */     return (internalGetFieldAccessorTable()).descriptor;
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
/*  120 */     TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
/*      */     
/*  122 */     Descriptors.Descriptor descriptor = (internalGetFieldAccessorTable()).descriptor;
/*  123 */     List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
/*      */     
/*  125 */     for (int i = 0; i < fields.size(); i++) {
/*  126 */       Descriptors.FieldDescriptor field = fields.get(i);
/*  127 */       Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  133 */       if (oneofDescriptor != null) {
/*      */         
/*  135 */         i += oneofDescriptor.getFieldCount() - 1;
/*  136 */         if (!hasOneof(oneofDescriptor)) {
/*      */           continue;
/*      */         }
/*      */ 
/*      */         
/*  141 */         field = getOneofFieldDescriptor(oneofDescriptor);
/*      */       } else {
/*      */         
/*  144 */         if (field.isRepeated()) {
/*  145 */           List<?> value = (List)getField(field);
/*  146 */           if (!value.isEmpty()) {
/*  147 */             result.put(field, value);
/*      */           }
/*      */           continue;
/*      */         } 
/*  151 */         if (!hasField(field)) {
/*      */           continue;
/*      */         }
/*      */       } 
/*      */       
/*  156 */       if (getBytesForString && field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
/*  157 */         result.put(field, getFieldRaw(field));
/*      */       } else {
/*  159 */         result.put(field, getField(field));
/*      */       }  continue;
/*      */     } 
/*  162 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isInitialized() {
/*  167 */     for (Descriptors.FieldDescriptor field : getDescriptorForType().getFields()) {
/*      */       
/*  169 */       if (field.isRequired() && 
/*  170 */         !hasField(field)) {
/*  171 */         return false;
/*      */       }
/*      */ 
/*      */       
/*  175 */       if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/*  176 */         if (field.isRepeated()) {
/*      */           
/*  178 */           List<Message> messageList = (List<Message>)getField(field);
/*  179 */           for (Message element : messageList) {
/*  180 */             if (!element.isInitialized())
/*  181 */               return false; 
/*      */           } 
/*      */           continue;
/*      */         } 
/*  185 */         if (hasField(field) && !((Message)getField(field)).isInitialized()) {
/*  186 */           return false;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  192 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/*  197 */     return Collections.unmodifiableMap(
/*  198 */         getAllFieldsMutable(false));
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
/*  212 */     return Collections.unmodifiableMap(
/*  213 */         getAllFieldsMutable(true));
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/*  218 */     return internalGetFieldAccessorTable().getOneof(oneof).has(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/*  223 */     return internalGetFieldAccessorTable().getOneof(oneof).get(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasField(Descriptors.FieldDescriptor field) {
/*  228 */     return internalGetFieldAccessorTable().getField(field).has(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getField(Descriptors.FieldDescriptor field) {
/*  233 */     return internalGetFieldAccessorTable().getField(field).get(this);
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
/*  245 */     return internalGetFieldAccessorTable().getField(field).getRaw(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/*  250 */     return internalGetFieldAccessorTable().getField(field)
/*  251 */       .getRepeatedCount(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/*  256 */     return internalGetFieldAccessorTable().getField(field)
/*  257 */       .getRepeated(this, index);
/*      */   }
/*      */ 
/*      */   
/*      */   public UnknownFieldSet getUnknownFields() {
/*  262 */     throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
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
/*      */   protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/*  275 */     return unknownFields.mergeFieldFrom(tag, input);
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input) throws IOException {
/*      */     try {
/*  281 */       return parser.parseFrom(input);
/*  282 */     } catch (InvalidProtocolBufferException e) {
/*  283 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
/*      */     try {
/*  290 */       return parser.parseFrom(input, extensions);
/*  291 */     } catch (InvalidProtocolBufferException e) {
/*  292 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input) throws IOException {
/*      */     try {
/*  299 */       return parser.parseFrom(input);
/*  300 */     } catch (InvalidProtocolBufferException e) {
/*  301 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input, ExtensionRegistryLite extensions) throws IOException {
/*      */     try {
/*  308 */       return parser.parseFrom(input, extensions);
/*  309 */     } catch (InvalidProtocolBufferException e) {
/*  310 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input) throws IOException {
/*      */     try {
/*  317 */       return parser.parseDelimitedFrom(input);
/*  318 */     } catch (InvalidProtocolBufferException e) {
/*  319 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
/*      */     try {
/*  326 */       return parser.parseDelimitedFrom(input, extensions);
/*  327 */     } catch (InvalidProtocolBufferException e) {
/*  328 */       throw e.unwrapIOException();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeTo(CodedOutputStream output) throws IOException {
/*  334 */     MessageReflection.writeMessageTo(this, getAllFieldsRaw(), output, false);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  339 */     int size = this.memoizedSize;
/*  340 */     if (size != -1) {
/*  341 */       return size;
/*      */     }
/*      */     
/*  344 */     this.memoizedSize = MessageReflection.getSerializedSize(this, 
/*  345 */         getAllFieldsRaw());
/*  346 */     return this.memoizedSize;
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
/*      */   
/*      */   protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent parent) {
/*  373 */     return newBuilderForType(new BuilderParent()
/*      */         {
/*      */           public void markDirty() {
/*  376 */             parent.markDirty();
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class Builder<BuilderType extends Builder<BuilderType>>
/*      */     extends AbstractMessage.Builder<BuilderType>
/*      */   {
/*      */     private GeneratedMessage.BuilderParent builderParent;
/*      */ 
/*      */     
/*      */     private BuilderParentImpl meAsParent;
/*      */ 
/*      */     
/*      */     private boolean isClean;
/*      */ 
/*      */     
/*  395 */     private UnknownFieldSet unknownFields = UnknownFieldSet.getDefaultInstance();
/*      */     
/*      */     protected Builder() {
/*  398 */       this((GeneratedMessage.BuilderParent)null);
/*      */     }
/*      */     
/*      */     protected Builder(GeneratedMessage.BuilderParent builderParent) {
/*  402 */       this.builderParent = builderParent;
/*      */     }
/*      */ 
/*      */     
/*      */     void dispose() {
/*  407 */       this.builderParent = null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void onBuilt() {
/*  414 */       if (this.builderParent != null) {
/*  415 */         markClean();
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void markClean() {
/*  425 */       this.isClean = true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean isClean() {
/*  434 */       return this.isClean;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType clone() {
/*  440 */       Builder builder = (Builder)getDefaultInstanceForType().newBuilderForType();
/*  441 */       builder.mergeFrom(buildPartial());
/*  442 */       return (BuilderType)builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType clear() {
/*  451 */       this.unknownFields = UnknownFieldSet.getDefaultInstance();
/*  452 */       onChanged();
/*  453 */       return (BuilderType)this;
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
/*  465 */       return (internalGetFieldAccessorTable()).descriptor;
/*      */     }
/*      */ 
/*      */     
/*      */     public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/*  470 */       return Collections.unmodifiableMap(getAllFieldsMutable());
/*      */     }
/*      */ 
/*      */     
/*      */     private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable() {
/*  475 */       TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
/*      */       
/*  477 */       Descriptors.Descriptor descriptor = (internalGetFieldAccessorTable()).descriptor;
/*  478 */       List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
/*      */       
/*  480 */       for (int i = 0; i < fields.size(); i++) {
/*  481 */         Descriptors.FieldDescriptor field = fields.get(i);
/*  482 */         Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  488 */         if (oneofDescriptor != null) {
/*      */           
/*  490 */           i += oneofDescriptor.getFieldCount() - 1;
/*  491 */           if (!hasOneof(oneofDescriptor)) {
/*      */             continue;
/*      */           }
/*      */ 
/*      */           
/*  496 */           field = getOneofFieldDescriptor(oneofDescriptor);
/*      */         } else {
/*      */           
/*  499 */           if (field.isRepeated()) {
/*  500 */             List<?> value = (List)getField(field);
/*  501 */             if (!value.isEmpty()) {
/*  502 */               result.put(field, value);
/*      */             }
/*      */             continue;
/*      */           } 
/*  506 */           if (!hasField(field)) {
/*      */             continue;
/*      */           }
/*      */         } 
/*      */         
/*  511 */         result.put(field, getField(field)); continue;
/*      */       } 
/*  513 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
/*  518 */       return internalGetFieldAccessorTable().getField(field).newBuilder();
/*      */     }
/*      */ 
/*      */     
/*      */     public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
/*  523 */       return internalGetFieldAccessorTable().getField(field).getBuilder(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
/*  528 */       return internalGetFieldAccessorTable().getField(field).getRepeatedBuilder(this, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/*  534 */       return internalGetFieldAccessorTable().getOneof(oneof).has(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/*  539 */       return internalGetFieldAccessorTable().getOneof(oneof).get(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasField(Descriptors.FieldDescriptor field) {
/*  544 */       return internalGetFieldAccessorTable().getField(field).has(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getField(Descriptors.FieldDescriptor field) {
/*  549 */       Object object = internalGetFieldAccessorTable().getField(field).get(this);
/*  550 */       if (field.isRepeated())
/*      */       {
/*      */         
/*  553 */         return Collections.unmodifiableList((List)object);
/*      */       }
/*  555 */       return object;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
/*  561 */       internalGetFieldAccessorTable().getField(field).set(this, value);
/*  562 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType clearField(Descriptors.FieldDescriptor field) {
/*  567 */       internalGetFieldAccessorTable().getField(field).clear(this);
/*  568 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType clearOneof(Descriptors.OneofDescriptor oneof) {
/*  573 */       internalGetFieldAccessorTable().getOneof(oneof).clear(this);
/*  574 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/*  579 */       return internalGetFieldAccessorTable().getField(field)
/*  580 */         .getRepeatedCount(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/*  585 */       return internalGetFieldAccessorTable().getField(field)
/*  586 */         .getRepeated(this, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/*  592 */       internalGetFieldAccessorTable().getField(field)
/*  593 */         .setRepeated(this, index, value);
/*  594 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/*  599 */       internalGetFieldAccessorTable().getField(field).addRepeated(this, value);
/*  600 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType setUnknownFields(UnknownFieldSet unknownFields) {
/*  605 */       this.unknownFields = unknownFields;
/*  606 */       onChanged();
/*  607 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  613 */       this
/*      */ 
/*      */         
/*  616 */         .unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build();
/*  617 */       onChanged();
/*  618 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isInitialized() {
/*  623 */       for (Descriptors.FieldDescriptor field : getDescriptorForType().getFields()) {
/*      */         
/*  625 */         if (field.isRequired() && 
/*  626 */           !hasField(field)) {
/*  627 */           return false;
/*      */         }
/*      */ 
/*      */         
/*  631 */         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/*  632 */           if (field.isRepeated()) {
/*      */             
/*  634 */             List<Message> messageList = (List<Message>)getField(field);
/*  635 */             for (Message element : messageList) {
/*  636 */               if (!element.isInitialized())
/*  637 */                 return false; 
/*      */             } 
/*      */             continue;
/*      */           } 
/*  641 */           if (hasField(field) && 
/*  642 */             !((Message)getField(field)).isInitialized()) {
/*  643 */             return false;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/*  648 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public final UnknownFieldSet getUnknownFields() {
/*  653 */       return this.unknownFields;
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
/*      */     protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/*  665 */       return unknownFields.mergeFieldFrom(tag, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private class BuilderParentImpl
/*      */       implements GeneratedMessage.BuilderParent
/*      */     {
/*      */       private BuilderParentImpl() {}
/*      */ 
/*      */       
/*      */       public void markDirty() {
/*  677 */         GeneratedMessage.Builder.this.onChanged();
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected GeneratedMessage.BuilderParent getParentForChildren() {
/*  686 */       if (this.meAsParent == null) {
/*  687 */         this.meAsParent = new BuilderParentImpl();
/*      */       }
/*  689 */       return this.meAsParent;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final void onChanged() {
/*  697 */       if (this.isClean && this.builderParent != null) {
/*  698 */         this.builderParent.markDirty();
/*      */ 
/*      */         
/*  701 */         this.isClean = false;
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
/*  720 */       throw new RuntimeException("No map fields found in " + 
/*  721 */           getClass().getName());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected MapField internalGetMutableMapField(int fieldNumber) {
/*  729 */       throw new RuntimeException("No map fields found in " + 
/*  730 */           getClass().getName());
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
/*      */     protected abstract GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable();
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
/*      */     extends GeneratedMessage
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
/*  833 */       this.extensions = FieldSet.newFieldSet();
/*      */     }
/*      */ 
/*      */     
/*      */     protected ExtendableMessage(GeneratedMessage.ExtendableBuilder<MessageType, ?> builder) {
/*  838 */       super(builder);
/*  839 */       this.extensions = builder.buildExtensions();
/*      */     }
/*      */ 
/*      */     
/*      */     private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
/*  844 */       if (extension.getDescriptor().getContainingType() != 
/*  845 */         getDescriptorForType())
/*      */       {
/*  847 */         throw new IllegalArgumentException("Extension is for type \"" + extension
/*      */             
/*  849 */             .getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + 
/*      */             
/*  851 */             getDescriptorForType().getFullName() + "\".");
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
/*  859 */       Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
/*      */       
/*  861 */       verifyExtensionContainingType(extension);
/*  862 */       return this.extensions.hasField(extension.getDescriptor());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
/*  870 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/*  872 */       verifyExtensionContainingType(extension);
/*  873 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/*  874 */       return this.extensions.getRepeatedFieldCount(descriptor);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
/*  881 */       Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
/*      */       
/*  883 */       verifyExtensionContainingType(extension);
/*  884 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/*  885 */       Object value = this.extensions.getField(descriptor);
/*  886 */       if (value == null) {
/*  887 */         if (descriptor.isRepeated())
/*  888 */           return (Type)Collections.emptyList(); 
/*  889 */         if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
/*      */         {
/*  891 */           return (Type)extension.getMessageDefaultInstance();
/*      */         }
/*  893 */         return (Type)extension.fromReflectionType(descriptor
/*  894 */             .getDefaultValue());
/*      */       } 
/*      */       
/*  897 */       return (Type)extension.fromReflectionType(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
/*  906 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/*  908 */       verifyExtensionContainingType(extension);
/*  909 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/*  910 */       return (Type)extension.singularFromReflectionType(this.extensions
/*  911 */           .getRepeatedField(descriptor, index));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
/*  917 */       return hasExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
/*  923 */       return hasExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
/*  929 */       return getExtensionCount(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
/*  935 */       return getExtensionCount(extension);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
/*  940 */       return getExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
/*  946 */       return getExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
/*  952 */       return getExtension(extension, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
/*  958 */       return getExtension(extension, index);
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean extensionsAreInitialized() {
/*  963 */       return this.extensions.isInitialized();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isInitialized() {
/*  968 */       return (super.isInitialized() && extensionsAreInitialized());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/*  977 */       return MessageReflection.mergeFieldFrom(input, unknownFields, extensionRegistry, 
/*  978 */           getDescriptorForType(), new MessageReflection.ExtensionAdapter(this.extensions), tag);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void makeExtensionsImmutable() {
/*  988 */       this.extensions.makeImmutable();
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
/* 1001 */       private final Iterator<Map.Entry<Descriptors.FieldDescriptor, Object>> iter = GeneratedMessage.ExtendableMessage.this
/* 1002 */         .extensions.iterator();
/*      */       private Map.Entry<Descriptors.FieldDescriptor, Object> next;
/*      */       private final boolean messageSetWireFormat;
/*      */       
/*      */       private ExtensionWriter(boolean messageSetWireFormat) {
/* 1007 */         if (this.iter.hasNext()) {
/* 1008 */           this.next = this.iter.next();
/*      */         }
/* 1010 */         this.messageSetWireFormat = messageSetWireFormat;
/*      */       }
/*      */ 
/*      */       
/*      */       public void writeUntil(int end, CodedOutputStream output) throws IOException {
/* 1015 */         while (this.next != null && ((Descriptors.FieldDescriptor)this.next.getKey()).getNumber() < end) {
/* 1016 */           Descriptors.FieldDescriptor descriptor = this.next.getKey();
/* 1017 */           if (this.messageSetWireFormat && descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && 
/*      */             
/* 1019 */             !descriptor.isRepeated()) {
/* 1020 */             if (this.next instanceof LazyField.LazyEntry) {
/* 1021 */               output.writeRawMessageSetExtension(descriptor.getNumber(), ((LazyField.LazyEntry)this.next)
/* 1022 */                   .getField().toByteString());
/*      */             } else {
/* 1024 */               output.writeMessageSetExtension(descriptor.getNumber(), (Message)this.next
/* 1025 */                   .getValue());
/*      */ 
/*      */             
/*      */             }
/*      */ 
/*      */           
/*      */           }
/*      */           else {
/*      */ 
/*      */             
/* 1035 */             FieldSet.writeField(descriptor, this.next.getValue(), output);
/*      */           } 
/* 1037 */           if (this.iter.hasNext()) {
/* 1038 */             this.next = this.iter.next(); continue;
/*      */           } 
/* 1040 */           this.next = null;
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     protected ExtensionWriter newExtensionWriter() {
/* 1047 */       return new ExtensionWriter(false);
/*      */     }
/*      */     protected ExtensionWriter newMessageSetExtensionWriter() {
/* 1050 */       return new ExtensionWriter(true);
/*      */     }
/*      */ 
/*      */     
/*      */     protected int extensionsSerializedSize() {
/* 1055 */       return this.extensions.getSerializedSize();
/*      */     }
/*      */     protected int extensionsSerializedSizeAsMessageSet() {
/* 1058 */       return this.extensions.getMessageSetSerializedSize();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Map<Descriptors.FieldDescriptor, Object> getExtensionFields() {
/* 1065 */       return this.extensions.getAllFields();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/* 1071 */       Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable(false);
/* 1072 */       result.putAll(getExtensionFields());
/* 1073 */       return Collections.unmodifiableMap(result);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
/* 1079 */       Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable(false);
/* 1080 */       result.putAll(getExtensionFields());
/* 1081 */       return Collections.unmodifiableMap(result);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasField(Descriptors.FieldDescriptor field) {
/* 1086 */       if (field.isExtension()) {
/* 1087 */         verifyContainingType(field);
/* 1088 */         return this.extensions.hasField(field);
/*      */       } 
/* 1090 */       return super.hasField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getField(Descriptors.FieldDescriptor field) {
/* 1096 */       if (field.isExtension()) {
/* 1097 */         verifyContainingType(field);
/* 1098 */         Object value = this.extensions.getField(field);
/* 1099 */         if (value == null) {
/* 1100 */           if (field.isRepeated())
/* 1101 */             return Collections.emptyList(); 
/* 1102 */           if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
/*      */           {
/*      */             
/* 1105 */             return DynamicMessage.getDefaultInstance(field.getMessageType());
/*      */           }
/* 1107 */           return field.getDefaultValue();
/*      */         } 
/*      */         
/* 1110 */         return value;
/*      */       } 
/*      */       
/* 1113 */       return super.getField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/* 1119 */       if (field.isExtension()) {
/* 1120 */         verifyContainingType(field);
/* 1121 */         return this.extensions.getRepeatedFieldCount(field);
/*      */       } 
/* 1123 */       return super.getRepeatedFieldCount(field);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/* 1130 */       if (field.isExtension()) {
/* 1131 */         verifyContainingType(field);
/* 1132 */         return this.extensions.getRepeatedField(field, index);
/*      */       } 
/* 1134 */       return super.getRepeatedField(field, index);
/*      */     }
/*      */ 
/*      */     
/*      */     private void verifyContainingType(Descriptors.FieldDescriptor field) {
/* 1139 */       if (field.getContainingType() != getDescriptorForType()) {
/* 1140 */         throw new IllegalArgumentException("FieldDescriptor does not match message type.");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/* 1190 */     private FieldSet<Descriptors.FieldDescriptor> extensions = FieldSet.emptySet();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected ExtendableBuilder(GeneratedMessage.BuilderParent parent) {
/* 1196 */       super(parent);
/*      */     }
/*      */ 
/*      */     
/*      */     void internalSetExtensionSet(FieldSet<Descriptors.FieldDescriptor> extensions) {
/* 1201 */       this.extensions = extensions;
/*      */     }
/*      */ 
/*      */     
/*      */     public BuilderType clear() {
/* 1206 */       this.extensions = FieldSet.emptySet();
/* 1207 */       return super.clear();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType clone() {
/* 1215 */       return super.clone();
/*      */     }
/*      */     
/*      */     private void ensureExtensionsIsMutable() {
/* 1219 */       if (this.extensions.isImmutable()) {
/* 1220 */         this.extensions = this.extensions.clone();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
/* 1226 */       if (extension.getDescriptor().getContainingType() != 
/* 1227 */         getDescriptorForType())
/*      */       {
/* 1229 */         throw new IllegalArgumentException("Extension is for type \"" + extension
/*      */             
/* 1231 */             .getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + 
/*      */             
/* 1233 */             getDescriptorForType().getFullName() + "\".");
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
/* 1240 */       Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
/*      */       
/* 1242 */       verifyExtensionContainingType(extension);
/* 1243 */       return this.extensions.hasField(extension.getDescriptor());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
/* 1250 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1252 */       verifyExtensionContainingType(extension);
/* 1253 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1254 */       return this.extensions.getRepeatedFieldCount(descriptor);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
/* 1260 */       Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
/*      */       
/* 1262 */       verifyExtensionContainingType(extension);
/* 1263 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1264 */       Object value = this.extensions.getField(descriptor);
/* 1265 */       if (value == null) {
/* 1266 */         if (descriptor.isRepeated())
/* 1267 */           return (Type)Collections.emptyList(); 
/* 1268 */         if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
/*      */         {
/* 1270 */           return (Type)extension.getMessageDefaultInstance();
/*      */         }
/* 1272 */         return (Type)extension.fromReflectionType(descriptor
/* 1273 */             .getDefaultValue());
/*      */       } 
/*      */       
/* 1276 */       return (Type)extension.fromReflectionType(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
/* 1284 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1286 */       verifyExtensionContainingType(extension);
/* 1287 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1288 */       return (Type)extension.singularFromReflectionType(this.extensions
/* 1289 */           .getRepeatedField(descriptor, index));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extensionLite, Type value) {
/* 1296 */       Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
/*      */       
/* 1298 */       verifyExtensionContainingType(extension);
/* 1299 */       ensureExtensionsIsMutable();
/* 1300 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1301 */       this.extensions.setField(descriptor, extension.toReflectionType(value));
/* 1302 */       onChanged();
/* 1303 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index, Type value) {
/* 1310 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1312 */       verifyExtensionContainingType(extension);
/* 1313 */       ensureExtensionsIsMutable();
/* 1314 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1315 */       this.extensions.setRepeatedField(descriptor, index, extension
/*      */           
/* 1317 */           .singularToReflectionType(value));
/* 1318 */       onChanged();
/* 1319 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extensionLite, Type value) {
/* 1326 */       Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1328 */       verifyExtensionContainingType(extension);
/* 1329 */       ensureExtensionsIsMutable();
/* 1330 */       Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
/* 1331 */       this.extensions.addRepeatedField(descriptor, extension
/* 1332 */           .singularToReflectionType(value));
/* 1333 */       onChanged();
/* 1334 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType clearExtension(ExtensionLite<MessageType, ?> extensionLite) {
/* 1340 */       Extension<MessageType, ?> extension = GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
/*      */       
/* 1342 */       verifyExtensionContainingType(extension);
/* 1343 */       ensureExtensionsIsMutable();
/* 1344 */       this.extensions.clearField(extension.getDescriptor());
/* 1345 */       onChanged();
/* 1346 */       return (BuilderType)this;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
/* 1352 */       return hasExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
/* 1358 */       return hasExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
/* 1364 */       return getExtensionCount(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
/* 1370 */       return getExtensionCount(extension);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
/* 1375 */       return getExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
/* 1381 */       return getExtension(extension);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
/* 1387 */       return getExtension(extension, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
/* 1393 */       return getExtension(extension, index);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(Extension<MessageType, Type> extension, Type value) {
/* 1398 */       return setExtension(extension, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension, Type value) {
/* 1403 */       return setExtension(extension, value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType setExtension(Extension<MessageType, List<Type>> extension, int index, Type value) {
/* 1409 */       return setExtension(extension, index, value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index, Type value) {
/* 1415 */       return setExtension(extension, index, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType addExtension(Extension<MessageType, List<Type>> extension, Type value) {
/* 1420 */       return addExtension(extension, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public <Type> BuilderType addExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, Type value) {
/* 1425 */       return addExtension(extension, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final <Type> BuilderType clearExtension(Extension<MessageType, ?> extension) {
/* 1430 */       return clearExtension(extension);
/*      */     }
/*      */ 
/*      */     
/*      */     public <Type> BuilderType clearExtension(GeneratedMessage.GeneratedExtension<MessageType, ?> extension) {
/* 1435 */       return clearExtension(extension);
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean extensionsAreInitialized() {
/* 1440 */       return this.extensions.isInitialized();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
/* 1448 */       this.extensions.makeImmutable();
/* 1449 */       return this.extensions;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isInitialized() {
/* 1454 */       return (super.isInitialized() && extensionsAreInitialized());
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
/*      */     protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
/* 1467 */       return MessageReflection.mergeFieldFrom(input, unknownFields, extensionRegistry, 
/* 1468 */           getDescriptorForType(), new MessageReflection.BuilderAdapter(this), tag);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/* 1477 */       Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable();
/* 1478 */       result.putAll(this.extensions.getAllFields());
/* 1479 */       return Collections.unmodifiableMap(result);
/*      */     }
/*      */ 
/*      */     
/*      */     public Object getField(Descriptors.FieldDescriptor field) {
/* 1484 */       if (field.isExtension()) {
/* 1485 */         verifyContainingType(field);
/* 1486 */         Object value = this.extensions.getField(field);
/* 1487 */         if (value == null) {
/* 1488 */           if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
/*      */           {
/*      */             
/* 1491 */             return DynamicMessage.getDefaultInstance(field.getMessageType());
/*      */           }
/* 1493 */           return field.getDefaultValue();
/*      */         } 
/*      */         
/* 1496 */         return value;
/*      */       } 
/*      */       
/* 1499 */       return super.getField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/* 1505 */       if (field.isExtension()) {
/* 1506 */         verifyContainingType(field);
/* 1507 */         return this.extensions.getRepeatedFieldCount(field);
/*      */       } 
/* 1509 */       return super.getRepeatedFieldCount(field);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/* 1516 */       if (field.isExtension()) {
/* 1517 */         verifyContainingType(field);
/* 1518 */         return this.extensions.getRepeatedField(field, index);
/*      */       } 
/* 1520 */       return super.getRepeatedField(field, index);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean hasField(Descriptors.FieldDescriptor field) {
/* 1526 */       if (field.isExtension()) {
/* 1527 */         verifyContainingType(field);
/* 1528 */         return this.extensions.hasField(field);
/*      */       } 
/* 1530 */       return super.hasField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
/* 1537 */       if (field.isExtension()) {
/* 1538 */         verifyContainingType(field);
/* 1539 */         ensureExtensionsIsMutable();
/* 1540 */         this.extensions.setField(field, value);
/* 1541 */         onChanged();
/* 1542 */         return (BuilderType)this;
/*      */       } 
/* 1544 */       return super.setField(field, value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType clearField(Descriptors.FieldDescriptor field) {
/* 1550 */       if (field.isExtension()) {
/* 1551 */         verifyContainingType(field);
/* 1552 */         ensureExtensionsIsMutable();
/* 1553 */         this.extensions.clearField(field);
/* 1554 */         onChanged();
/* 1555 */         return (BuilderType)this;
/*      */       } 
/* 1557 */       return super.clearField(field);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 1564 */       if (field.isExtension()) {
/* 1565 */         verifyContainingType(field);
/* 1566 */         ensureExtensionsIsMutable();
/* 1567 */         this.extensions.setRepeatedField(field, index, value);
/* 1568 */         onChanged();
/* 1569 */         return (BuilderType)this;
/*      */       } 
/* 1571 */       return super.setRepeatedField(field, index, value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 1578 */       if (field.isExtension()) {
/* 1579 */         verifyContainingType(field);
/* 1580 */         ensureExtensionsIsMutable();
/* 1581 */         this.extensions.addRepeatedField(field, value);
/* 1582 */         onChanged();
/* 1583 */         return (BuilderType)this;
/*      */       } 
/* 1585 */       return super.addRepeatedField(field, value);
/*      */     }
/*      */ 
/*      */     
/*      */     protected final void mergeExtensionFields(GeneratedMessage.ExtendableMessage other) {
/* 1590 */       ensureExtensionsIsMutable();
/* 1591 */       this.extensions.mergeFrom(other.extensions);
/* 1592 */       onChanged();
/*      */     }
/*      */     
/*      */     private void verifyContainingType(Descriptors.FieldDescriptor field) {
/* 1596 */       if (field.getContainingType() != getDescriptorForType()) {
/* 1597 */         throw new IllegalArgumentException("FieldDescriptor does not match message type.");
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
/*      */     protected ExtendableBuilder() {}
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
/*      */   public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newMessageScopedGeneratedExtension(final Message scope, final int descriptorIndex, Class singularType, Message defaultInstance) {
/* 1623 */     return new GeneratedExtension<>(new CachedDescriptorRetriever()
/*      */         {
/*      */           public Descriptors.FieldDescriptor loadDescriptor()
/*      */           {
/* 1627 */             return scope.getDescriptorForType().getExtensions().get(descriptorIndex);
/*      */           }
/*      */         },  singularType, defaultInstance, Extension.ExtensionType.IMMUTABLE);
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
/*      */   public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newFileScopedGeneratedExtension(Class singularType, Message defaultInstance) {
/* 1643 */     return new GeneratedExtension<>(null, singularType, defaultInstance, Extension.ExtensionType.IMMUTABLE);
/*      */   }
/*      */ 
/*      */   
/*      */   private static abstract class CachedDescriptorRetriever
/*      */     implements ExtensionDescriptorRetriever
/*      */   {
/*      */     private volatile Descriptors.FieldDescriptor descriptor;
/*      */ 
/*      */     
/*      */     private CachedDescriptorRetriever() {}
/*      */ 
/*      */     
/*      */     public Descriptors.FieldDescriptor getDescriptor() {
/* 1657 */       if (this.descriptor == null) {
/* 1658 */         synchronized (this) {
/* 1659 */           if (this.descriptor == null) {
/* 1660 */             this.descriptor = loadDescriptor();
/*      */           }
/*      */         } 
/*      */       }
/* 1664 */       return this.descriptor;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected abstract Descriptors.FieldDescriptor loadDescriptor();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newMessageScopedGeneratedExtension(final Message scope, final String name, Class singularType, Message defaultInstance) {
/* 1683 */     return new GeneratedExtension<>(new CachedDescriptorRetriever()
/*      */         {
/*      */           protected Descriptors.FieldDescriptor loadDescriptor()
/*      */           {
/* 1687 */             return scope.getDescriptorForType().findFieldByName(name);
/*      */           }
/*      */         },  singularType, defaultInstance, Extension.ExtensionType.MUTABLE);
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
/*      */   public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newFileScopedGeneratedExtension(final Class singularType, Message defaultInstance, final String descriptorOuterClass, final String extensionName) {
/* 1710 */     return new GeneratedExtension<>(new CachedDescriptorRetriever()
/*      */         {
/*      */           protected Descriptors.FieldDescriptor loadDescriptor()
/*      */           {
/*      */             try {
/* 1715 */               Class<?> clazz = singularType.getClassLoader().loadClass(descriptorOuterClass);
/* 1716 */               Descriptors.FileDescriptor file = (Descriptors.FileDescriptor)clazz.getField("descriptor").get(null);
/* 1717 */               return file.findExtensionByName(extensionName);
/* 1718 */             } catch (Exception e) {
/* 1719 */               throw new RuntimeException("Cannot load descriptors: " + descriptorOuterClass + " is not a valid descriptor class name", e);
/*      */             } 
/*      */           }
/*      */         }singularType, defaultInstance, Extension.ExtensionType.MUTABLE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class GeneratedExtension<ContainingType extends Message, Type>
/*      */     extends Extension<ContainingType, Type>
/*      */   {
/*      */     private GeneratedMessage.ExtensionDescriptorRetriever descriptorRetriever;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final Class singularType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final Message messageDefaultInstance;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final Method enumValueOf;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final Method enumGetValueDescriptor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final Extension.ExtensionType extensionType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     GeneratedExtension(GeneratedMessage.ExtensionDescriptorRetriever descriptorRetriever, Class<?> singularType, Message messageDefaultInstance, Extension.ExtensionType extensionType) {
/* 1779 */       if (Message.class.isAssignableFrom(singularType) && 
/* 1780 */         !singularType.isInstance(messageDefaultInstance)) {
/* 1781 */         throw new IllegalArgumentException("Bad messageDefaultInstance for " + singularType
/* 1782 */             .getName());
/*      */       }
/* 1784 */       this.descriptorRetriever = descriptorRetriever;
/* 1785 */       this.singularType = singularType;
/* 1786 */       this.messageDefaultInstance = messageDefaultInstance;
/*      */       
/* 1788 */       if (ProtocolMessageEnum.class.isAssignableFrom(singularType)) {
/* 1789 */         this.enumValueOf = GeneratedMessage.getMethodOrDie(singularType, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
/*      */         
/* 1791 */         this
/* 1792 */           .enumGetValueDescriptor = GeneratedMessage.getMethodOrDie(singularType, "getValueDescriptor", new Class[0]);
/*      */       } else {
/* 1794 */         this.enumValueOf = null;
/* 1795 */         this.enumGetValueDescriptor = null;
/*      */       } 
/* 1797 */       this.extensionType = extensionType;
/*      */     }
/*      */ 
/*      */     
/*      */     public void internalInit(final Descriptors.FieldDescriptor descriptor) {
/* 1802 */       if (this.descriptorRetriever != null) {
/* 1803 */         throw new IllegalStateException("Already initialized.");
/*      */       }
/* 1805 */       this.descriptorRetriever = new GeneratedMessage.ExtensionDescriptorRetriever()
/*      */         {
/*      */           public Descriptors.FieldDescriptor getDescriptor()
/*      */           {
/* 1809 */             return descriptor;
/*      */           }
/*      */         };
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
/*      */     public Descriptors.FieldDescriptor getDescriptor() {
/* 1823 */       if (this.descriptorRetriever == null) {
/* 1824 */         throw new IllegalStateException("getDescriptor() called before internalInit()");
/*      */       }
/*      */       
/* 1827 */       return this.descriptorRetriever.getDescriptor();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Message getMessageDefaultInstance() {
/* 1836 */       return this.messageDefaultInstance;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Extension.ExtensionType getExtensionType() {
/* 1841 */       return this.extensionType;
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
/*      */     protected Object fromReflectionType(Object value) {
/* 1853 */       Descriptors.FieldDescriptor descriptor = getDescriptor();
/* 1854 */       if (descriptor.isRepeated()) {
/* 1855 */         if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE || descriptor
/* 1856 */           .getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
/*      */           
/* 1858 */           List<Object> result = new ArrayList();
/* 1859 */           for (Object element : value) {
/* 1860 */             result.add(singularFromReflectionType(element));
/*      */           }
/* 1862 */           return result;
/*      */         } 
/* 1864 */         return value;
/*      */       } 
/*      */       
/* 1867 */       return singularFromReflectionType(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Object singularFromReflectionType(Object value) {
/* 1877 */       Descriptors.FieldDescriptor descriptor = getDescriptor();
/* 1878 */       switch (descriptor.getJavaType()) {
/*      */         case MESSAGE:
/* 1880 */           if (this.singularType.isInstance(value)) {
/* 1881 */             return value;
/*      */           }
/* 1883 */           return this.messageDefaultInstance.newBuilderForType()
/* 1884 */             .mergeFrom((Message)value).build();
/*      */         
/*      */         case ENUM:
/* 1887 */           return GeneratedMessage.invokeOrDie(this.enumValueOf, null, new Object[] { value });
/*      */       } 
/* 1889 */       return value;
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
/*      */     protected Object toReflectionType(Object value) {
/* 1902 */       Descriptors.FieldDescriptor descriptor = getDescriptor();
/* 1903 */       if (descriptor.isRepeated()) {
/* 1904 */         if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
/*      */           
/* 1906 */           List<Object> result = new ArrayList();
/* 1907 */           for (Object element : value) {
/* 1908 */             result.add(singularToReflectionType(element));
/*      */           }
/* 1910 */           return result;
/*      */         } 
/* 1912 */         return value;
/*      */       } 
/*      */       
/* 1915 */       return singularToReflectionType(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Object singularToReflectionType(Object value) {
/* 1925 */       Descriptors.FieldDescriptor descriptor = getDescriptor();
/* 1926 */       switch (descriptor.getJavaType()) {
/*      */         case ENUM:
/* 1928 */           return GeneratedMessage.invokeOrDie(this.enumGetValueDescriptor, value, new Object[0]);
/*      */       } 
/* 1930 */       return value;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getNumber() {
/* 1936 */       return getDescriptor().getNumber();
/*      */     }
/*      */ 
/*      */     
/*      */     public WireFormat.FieldType getLiteType() {
/* 1941 */       return getDescriptor().getLiteType();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isRepeated() {
/* 1946 */       return getDescriptor().isRepeated();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Type getDefaultValue() {
/* 1952 */       if (isRepeated()) {
/* 1953 */         return (Type)Collections.emptyList();
/*      */       }
/* 1955 */       if (getDescriptor().getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 1956 */         return (Type)this.messageDefaultInstance;
/*      */       }
/* 1958 */       return (Type)singularFromReflectionType(
/* 1959 */           getDescriptor().getDefaultValue());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Method getMethodOrDie(Class clazz, String name, Class... params) {
/*      */     try {
/* 1970 */       return clazz.getMethod(name, params);
/* 1971 */     } catch (NoSuchMethodException e) {
/* 1972 */       throw new RuntimeException("Generated message class \"" + clazz
/* 1973 */           .getName() + "\" missing method \"" + name + "\".", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Object invokeOrDie(Method method, Object object, Object... params) {
/*      */     try {
/* 1982 */       return method.invoke(object, params);
/* 1983 */     } catch (IllegalAccessException e) {
/* 1984 */       throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
/*      */     
/*      */     }
/* 1987 */     catch (InvocationTargetException e) {
/* 1988 */       Throwable cause = e.getCause();
/* 1989 */       if (cause instanceof RuntimeException)
/* 1990 */         throw (RuntimeException)cause; 
/* 1991 */       if (cause instanceof Error) {
/* 1992 */         throw (Error)cause;
/*      */       }
/* 1994 */       throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
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
/*      */   protected MapField internalGetMapField(int fieldNumber) {
/* 2015 */     throw new RuntimeException("No map fields found in " + 
/* 2016 */         getClass().getName());
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
/*      */     public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
/* 2040 */       this(descriptor, camelCaseNames);
/* 2041 */       ensureFieldAccessorsInitialized(messageClass, builderClass);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames) {
/* 2051 */       this.descriptor = descriptor;
/* 2052 */       this.camelCaseNames = camelCaseNames;
/* 2053 */       this.fields = new FieldAccessor[descriptor.getFields().size()];
/* 2054 */       this.oneofs = new OneofAccessor[descriptor.getOneofs().size()];
/* 2055 */       this.initialized = false;
/*      */     }
/*      */     
/*      */     private boolean isMapFieldEnabled(Descriptors.FieldDescriptor field) {
/* 2059 */       boolean result = true;
/* 2060 */       return result;
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
/*      */     public FieldAccessorTable ensureFieldAccessorsInitialized(Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
/* 2073 */       if (this.initialized) return this; 
/* 2074 */       synchronized (this) {
/* 2075 */         if (this.initialized) return this; 
/* 2076 */         int fieldsSize = this.fields.length;
/* 2077 */         for (int i = 0; i < fieldsSize; i++) {
/* 2078 */           Descriptors.FieldDescriptor field = this.descriptor.getFields().get(i);
/* 2079 */           String containingOneofCamelCaseName = null;
/* 2080 */           if (field.getContainingOneof() != null)
/*      */           {
/* 2082 */             containingOneofCamelCaseName = this.camelCaseNames[fieldsSize + field.getContainingOneof().getIndex()];
/*      */           }
/* 2084 */           if (field.isRepeated()) {
/* 2085 */             if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 2086 */               if (field.isMapField() && isMapFieldEnabled(field)) {
/* 2087 */                 this.fields[i] = new MapFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
/*      */               } else {
/*      */                 
/* 2090 */                 this.fields[i] = new RepeatedMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
/*      */               }
/*      */             
/* 2093 */             } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
/* 2094 */               this.fields[i] = new RepeatedEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
/*      */             } else {
/*      */               
/* 2097 */               this.fields[i] = new RepeatedFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
/*      */             }
/*      */           
/*      */           }
/* 2101 */           else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 2102 */             this.fields[i] = new SingularMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
/*      */           
/*      */           }
/* 2105 */           else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
/* 2106 */             this.fields[i] = new SingularEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
/*      */           
/*      */           }
/* 2109 */           else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
/* 2110 */             this.fields[i] = new SingularStringFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
/*      */           }
/*      */           else {
/*      */             
/* 2114 */             this.fields[i] = new SingularFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2121 */         int oneofsSize = this.oneofs.length;
/* 2122 */         for (int j = 0; j < oneofsSize; j++) {
/* 2123 */           this.oneofs[j] = new OneofAccessor(this.descriptor, this.camelCaseNames[j + fieldsSize], messageClass, builderClass);
/*      */         }
/*      */ 
/*      */         
/* 2127 */         this.initialized = true;
/* 2128 */         this.camelCaseNames = null;
/* 2129 */         return this;
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
/* 2141 */       if (field.getContainingType() != this.descriptor) {
/* 2142 */         throw new IllegalArgumentException("FieldDescriptor does not match message type.");
/*      */       }
/* 2144 */       if (field.isExtension())
/*      */       {
/*      */         
/* 2147 */         throw new IllegalArgumentException("This type does not have extensions.");
/*      */       }
/*      */       
/* 2150 */       return this.fields[field.getIndex()];
/*      */     }
/*      */ 
/*      */     
/*      */     private OneofAccessor getOneof(Descriptors.OneofDescriptor oneof) {
/* 2155 */       if (oneof.getContainingType() != this.descriptor) {
/* 2156 */         throw new IllegalArgumentException("OneofDescriptor does not match message type.");
/*      */       }
/*      */       
/* 2159 */       return this.oneofs[oneof.getIndex()];
/*      */     }
/*      */     private static interface FieldAccessor {
/*      */       Object get(GeneratedMessage param2GeneratedMessage);
/*      */       Object get(GeneratedMessage.Builder param2Builder);
/*      */       Object getRaw(GeneratedMessage param2GeneratedMessage);
/*      */       Object getRaw(GeneratedMessage.Builder param2Builder);
/*      */       void set(GeneratedMessage.Builder param2Builder, Object param2Object);
/*      */       Object getRepeated(GeneratedMessage param2GeneratedMessage, int param2Int);
/*      */       Object getRepeated(GeneratedMessage.Builder param2Builder, int param2Int);
/*      */       Object getRepeatedRaw(GeneratedMessage param2GeneratedMessage, int param2Int);
/*      */       Object getRepeatedRaw(GeneratedMessage.Builder param2Builder, int param2Int);
/*      */       void setRepeated(GeneratedMessage.Builder param2Builder, int param2Int, Object param2Object);
/*      */       void addRepeated(GeneratedMessage.Builder param2Builder, Object param2Object);
/*      */       
/*      */       boolean has(GeneratedMessage param2GeneratedMessage);
/*      */       
/*      */       boolean has(GeneratedMessage.Builder param2Builder);
/*      */       
/*      */       int getRepeatedCount(GeneratedMessage param2GeneratedMessage);
/*      */       
/*      */       int getRepeatedCount(GeneratedMessage.Builder param2Builder);
/*      */       
/*      */       void clear(GeneratedMessage.Builder param2Builder);
/*      */       
/*      */       Message.Builder newBuilder();
/*      */       
/*      */       Message.Builder getBuilder(GeneratedMessage.Builder param2Builder);
/*      */       
/*      */       Message.Builder getRepeatedBuilder(GeneratedMessage.Builder param2Builder, int param2Int); }
/*      */     
/*      */     private static class OneofAccessor { private final Descriptors.Descriptor descriptor;
/*      */       private final Method caseMethod;
/*      */       private final Method caseMethodBuilder;
/*      */       private final Method clearMethod;
/*      */       
/*      */       OneofAccessor(Descriptors.Descriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
/* 2196 */         this.descriptor = descriptor;
/* 2197 */         this
/* 2198 */           .caseMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]);
/* 2199 */         this
/* 2200 */           .caseMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]);
/* 2201 */         this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean has(GeneratedMessage message) {
/* 2210 */         if (((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() == 0) {
/* 2211 */           return false;
/*      */         }
/* 2213 */         return true;
/*      */       }
/*      */       
/*      */       public boolean has(GeneratedMessage.Builder builder) {
/* 2217 */         if (((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() == 0) {
/* 2218 */           return false;
/*      */         }
/* 2220 */         return true;
/*      */       }
/*      */       
/*      */       public Descriptors.FieldDescriptor get(GeneratedMessage message) {
/* 2224 */         int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
/* 2225 */         if (fieldNumber > 0) {
/* 2226 */           return this.descriptor.findFieldByNumber(fieldNumber);
/*      */         }
/* 2228 */         return null;
/*      */       }
/*      */       
/*      */       public Descriptors.FieldDescriptor get(GeneratedMessage.Builder builder) {
/* 2232 */         int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
/* 2233 */         if (fieldNumber > 0) {
/* 2234 */           return this.descriptor.findFieldByNumber(fieldNumber);
/*      */         }
/* 2236 */         return null;
/*      */       }
/*      */       
/*      */       public void clear(GeneratedMessage.Builder builder) {
/* 2240 */         GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
/*      */       } }
/*      */ 
/*      */     
/*      */     private static boolean supportFieldPresence(Descriptors.FileDescriptor file) {
/* 2245 */       return (file.getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2);
/*      */     }
/*      */     private static class SingularFieldAccessor implements FieldAccessor { protected final Class<?> type; protected final Method getMethod; protected final Method getMethodBuilder; protected final Method setMethod; protected final Method hasMethod; protected final Method hasMethodBuilder;
/*      */       protected final Method clearMethod;
/*      */       protected final Method caseMethod;
/*      */       protected final Method caseMethodBuilder;
/*      */       protected final Descriptors.FieldDescriptor field;
/*      */       protected final boolean isOneofField;
/*      */       protected final boolean hasHasMethod;
/*      */       
/*      */       SingularFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass, String containingOneofCamelCaseName) {
/* 2256 */         this.field = descriptor;
/* 2257 */         this.isOneofField = (descriptor.getContainingOneof() != null);
/* 2258 */         this
/* 2259 */           .hasHasMethod = (GeneratedMessage.FieldAccessorTable.supportFieldPresence(descriptor.getFile()) || (!this.isOneofField && descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE));
/* 2260 */         this.getMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[0]);
/* 2261 */         this.getMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[0]);
/* 2262 */         this.type = this.getMethod.getReturnType();
/* 2263 */         this.setMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { this.type });
/* 2264 */         this
/* 2265 */           .hasMethod = this.hasHasMethod ? GeneratedMessage.getMethodOrDie(messageClass, "has" + camelCaseName, new Class[0]) : null;
/* 2266 */         this
/* 2267 */           .hasMethodBuilder = this.hasHasMethod ? GeneratedMessage.getMethodOrDie(builderClass, "has" + camelCaseName, new Class[0]) : null;
/* 2268 */         this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
/* 2269 */         this.caseMethod = this.isOneofField ? GeneratedMessage.getMethodOrDie(messageClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
/*      */         
/* 2271 */         this.caseMethodBuilder = this.isOneofField ? GeneratedMessage.getMethodOrDie(builderClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private int getOneofFieldNumber(GeneratedMessage message) {
/* 2292 */         return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
/*      */       }
/*      */       
/*      */       private int getOneofFieldNumber(GeneratedMessage.Builder builder) {
/* 2296 */         return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
/*      */       }
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessage message) {
/* 2301 */         return GeneratedMessage.invokeOrDie(this.getMethod, message, new Object[0]);
/*      */       }
/*      */       
/*      */       public Object get(GeneratedMessage.Builder builder) {
/* 2305 */         return GeneratedMessage.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
/*      */       }
/*      */       
/*      */       public Object getRaw(GeneratedMessage message) {
/* 2309 */         return get(message);
/*      */       }
/*      */       
/*      */       public Object getRaw(GeneratedMessage.Builder builder) {
/* 2313 */         return get(builder);
/*      */       }
/*      */       
/*      */       public void set(GeneratedMessage.Builder builder, Object value) {
/* 2317 */         GeneratedMessage.invokeOrDie(this.setMethod, builder, new Object[] { value });
/*      */       }
/*      */       
/*      */       public Object getRepeated(GeneratedMessage message, int index) {
/* 2321 */         throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessage message, int index) {
/* 2326 */         throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessage.Builder builder, int index) {
/* 2331 */         throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessage.Builder builder, int index) {
/* 2336 */         throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
/* 2341 */         throw new UnsupportedOperationException("setRepeatedField() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public void addRepeated(GeneratedMessage.Builder builder, Object value) {
/* 2346 */         throw new UnsupportedOperationException("addRepeatedField() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public boolean has(GeneratedMessage message) {
/* 2351 */         if (!this.hasHasMethod) {
/* 2352 */           if (this.isOneofField) {
/* 2353 */             return (getOneofFieldNumber(message) == this.field.getNumber());
/*      */           }
/* 2355 */           return !get(message).equals(this.field.getDefaultValue());
/*      */         } 
/* 2357 */         return ((Boolean)GeneratedMessage.invokeOrDie(this.hasMethod, message, new Object[0])).booleanValue();
/*      */       }
/*      */       
/*      */       public boolean has(GeneratedMessage.Builder builder) {
/* 2361 */         if (!this.hasHasMethod) {
/* 2362 */           if (this.isOneofField) {
/* 2363 */             return (getOneofFieldNumber(builder) == this.field.getNumber());
/*      */           }
/* 2365 */           return !get(builder).equals(this.field.getDefaultValue());
/*      */         } 
/* 2367 */         return ((Boolean)GeneratedMessage.invokeOrDie(this.hasMethodBuilder, builder, new Object[0])).booleanValue();
/*      */       }
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessage message) {
/* 2371 */         throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessage.Builder builder) {
/* 2376 */         throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public void clear(GeneratedMessage.Builder builder) {
/* 2381 */         GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
/*      */       }
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 2385 */         throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder getBuilder(GeneratedMessage.Builder builder) {
/* 2390 */         throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder builder, int index) {
/* 2395 */         throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
/*      */       } }
/*      */ 
/*      */ 
/*      */     
/*      */     private static class RepeatedFieldAccessor
/*      */       implements FieldAccessor
/*      */     {
/*      */       protected final Class type;
/*      */       
/*      */       protected final Method getMethod;
/*      */       protected final Method getMethodBuilder;
/*      */       protected final Method getRepeatedMethod;
/*      */       protected final Method getRepeatedMethodBuilder;
/*      */       protected final Method setRepeatedMethod;
/*      */       protected final Method addRepeatedMethod;
/*      */       protected final Method getCountMethod;
/*      */       protected final Method getCountMethodBuilder;
/*      */       protected final Method clearMethod;
/*      */       
/*      */       RepeatedFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
/* 2416 */         this.getMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "List", new Class[0]);
/*      */         
/* 2418 */         this.getMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "List", new Class[0]);
/*      */         
/* 2420 */         this
/* 2421 */           .getRepeatedMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[] { int.class });
/* 2422 */         this
/* 2423 */           .getRepeatedMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[] { int.class });
/* 2424 */         this.type = this.getRepeatedMethod.getReturnType();
/* 2425 */         this
/* 2426 */           .setRepeatedMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { int.class, this.type });
/*      */         
/* 2428 */         this
/* 2429 */           .addRepeatedMethod = GeneratedMessage.getMethodOrDie(builderClass, "add" + camelCaseName, new Class[] { this.type });
/* 2430 */         this
/* 2431 */           .getCountMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Count", new Class[0]);
/* 2432 */         this
/* 2433 */           .getCountMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Count", new Class[0]);
/*      */         
/* 2435 */         this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessage message) {
/* 2440 */         return GeneratedMessage.invokeOrDie(this.getMethod, message, new Object[0]);
/*      */       }
/*      */       
/*      */       public Object get(GeneratedMessage.Builder builder) {
/* 2444 */         return GeneratedMessage.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
/*      */       }
/*      */       
/*      */       public Object getRaw(GeneratedMessage message) {
/* 2448 */         return get(message);
/*      */       }
/*      */       
/*      */       public Object getRaw(GeneratedMessage.Builder builder) {
/* 2452 */         return get(builder);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessage.Builder builder, Object value) {
/* 2460 */         clear(builder);
/* 2461 */         for (Object element : value) {
/* 2462 */           addRepeated(builder, element);
/*      */         }
/*      */       }
/*      */       
/*      */       public Object getRepeated(GeneratedMessage message, int index) {
/* 2467 */         return GeneratedMessage.invokeOrDie(this.getRepeatedMethod, message, new Object[] { Integer.valueOf(index) });
/*      */       }
/*      */       
/*      */       public Object getRepeated(GeneratedMessage.Builder builder, int index) {
/* 2471 */         return GeneratedMessage.invokeOrDie(this.getRepeatedMethodBuilder, builder, new Object[] { Integer.valueOf(index) });
/*      */       }
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessage message, int index) {
/* 2475 */         return getRepeated(message, index);
/*      */       }
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessage.Builder builder, int index) {
/* 2479 */         return getRepeated(builder, index);
/*      */       }
/*      */       
/*      */       public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
/* 2483 */         GeneratedMessage.invokeOrDie(this.setRepeatedMethod, builder, new Object[] { Integer.valueOf(index), value });
/*      */       }
/*      */       
/*      */       public void addRepeated(GeneratedMessage.Builder builder, Object value) {
/* 2487 */         GeneratedMessage.invokeOrDie(this.addRepeatedMethod, builder, new Object[] { value });
/*      */       }
/*      */       
/*      */       public boolean has(GeneratedMessage message) {
/* 2491 */         throw new UnsupportedOperationException("hasField() called on a repeated field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public boolean has(GeneratedMessage.Builder builder) {
/* 2496 */         throw new UnsupportedOperationException("hasField() called on a repeated field.");
/*      */       }
/*      */ 
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessage message) {
/* 2501 */         return ((Integer)GeneratedMessage.invokeOrDie(this.getCountMethod, message, new Object[0])).intValue();
/*      */       }
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessage.Builder builder) {
/* 2505 */         return ((Integer)GeneratedMessage.invokeOrDie(this.getCountMethodBuilder, builder, new Object[0])).intValue();
/*      */       }
/*      */       
/*      */       public void clear(GeneratedMessage.Builder builder) {
/* 2509 */         GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
/*      */       }
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 2513 */         throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder getBuilder(GeneratedMessage.Builder builder) {
/* 2518 */         throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder builder, int index) {
/* 2523 */         throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
/*      */       }
/*      */     }
/*      */     
/*      */     private static class MapFieldAccessor
/*      */       implements FieldAccessor {
/*      */       private final Descriptors.FieldDescriptor field;
/*      */       private final Message mapEntryMessageDefaultInstance;
/*      */       
/*      */       MapFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
/* 2533 */         this.field = descriptor;
/*      */         
/* 2535 */         Method getDefaultInstanceMethod = GeneratedMessage.getMethodOrDie(messageClass, "getDefaultInstance", new Class[0]);
/* 2536 */         MapField<?, ?> defaultMapField = getMapField(
/* 2537 */             (GeneratedMessage)GeneratedMessage.invokeOrDie(getDefaultInstanceMethod, null, new Object[0]));
/* 2538 */         this
/* 2539 */           .mapEntryMessageDefaultInstance = defaultMapField.getMapEntryMessageDefaultInstance();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private MapField<?, ?> getMapField(GeneratedMessage message) {
/* 2546 */         return message.internalGetMapField(this.field.getNumber());
/*      */       }
/*      */       
/*      */       private MapField<?, ?> getMapField(GeneratedMessage.Builder builder) {
/* 2550 */         return builder.internalGetMapField(this.field.getNumber());
/*      */       }
/*      */ 
/*      */       
/*      */       private MapField<?, ?> getMutableMapField(GeneratedMessage.Builder builder) {
/* 2555 */         return builder.internalGetMutableMapField(this.field
/* 2556 */             .getNumber());
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessage message) {
/* 2562 */         List<Object> result = new ArrayList();
/* 2563 */         for (int i = 0; i < getRepeatedCount(message); i++) {
/* 2564 */           result.add(getRepeated(message, i));
/*      */         }
/* 2566 */         return Collections.unmodifiableList(result);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessage.Builder builder) {
/* 2572 */         List<Object> result = new ArrayList();
/* 2573 */         for (int i = 0; i < getRepeatedCount(builder); i++) {
/* 2574 */           result.add(getRepeated(builder, i));
/*      */         }
/* 2576 */         return Collections.unmodifiableList(result);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRaw(GeneratedMessage message) {
/* 2581 */         return get(message);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRaw(GeneratedMessage.Builder builder) {
/* 2586 */         return get(builder);
/*      */       }
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessage.Builder builder, Object value) {
/* 2591 */         clear(builder);
/* 2592 */         for (Object entry : value) {
/* 2593 */           addRepeated(builder, entry);
/*      */         }
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessage message, int index) {
/* 2599 */         return getMapField(message).getList().get(index);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessage.Builder builder, int index) {
/* 2604 */         return getMapField(builder).getList().get(index);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessage message, int index) {
/* 2609 */         return getRepeated(message, index);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeatedRaw(GeneratedMessage.Builder builder, int index) {
/* 2614 */         return getRepeated(builder, index);
/*      */       }
/*      */ 
/*      */       
/*      */       public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
/* 2619 */         getMutableMapField(builder).getMutableList().set(index, (Message)value);
/*      */       }
/*      */ 
/*      */       
/*      */       public void addRepeated(GeneratedMessage.Builder builder, Object value) {
/* 2624 */         getMutableMapField(builder).getMutableList().add((Message)value);
/*      */       }
/*      */ 
/*      */       
/*      */       public boolean has(GeneratedMessage message) {
/* 2629 */         throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public boolean has(GeneratedMessage.Builder builder) {
/* 2635 */         throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessage message) {
/* 2641 */         return getMapField(message).getList().size();
/*      */       }
/*      */ 
/*      */       
/*      */       public int getRepeatedCount(GeneratedMessage.Builder builder) {
/* 2646 */         return getMapField(builder).getList().size();
/*      */       }
/*      */ 
/*      */       
/*      */       public void clear(GeneratedMessage.Builder builder) {
/* 2651 */         getMutableMapField(builder).getMutableList().clear();
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 2656 */         return this.mapEntryMessageDefaultInstance.newBuilderForType();
/*      */       }
/*      */ 
/*      */       
/*      */       public Message.Builder getBuilder(GeneratedMessage.Builder builder) {
/* 2661 */         throw new UnsupportedOperationException("Nested builder not supported for map fields.");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder builder, int index) {
/* 2667 */         throw new UnsupportedOperationException("Nested builder not supported for map fields.");
/*      */       }
/*      */     }
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
/*      */       SingularEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass, String containingOneofCamelCaseName) {
/* 2681 */         super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
/*      */         
/* 2683 */         this.enumDescriptor = descriptor.getEnumType();
/*      */         
/* 2685 */         this.valueOfMethod = GeneratedMessage.getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
/*      */         
/* 2687 */         this
/* 2688 */           .getValueDescriptorMethod = GeneratedMessage.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
/*      */         
/* 2690 */         this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
/* 2691 */         if (this.supportUnknownEnumValue) {
/* 2692 */           this
/* 2693 */             .getValueMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[0]);
/* 2694 */           this
/* 2695 */             .getValueMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[0]);
/* 2696 */           this
/* 2697 */             .setValueMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[] { int.class });
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
/*      */       public Object get(GeneratedMessage message) {
/* 2713 */         if (this.supportUnknownEnumValue) {
/* 2714 */           int value = ((Integer)GeneratedMessage.invokeOrDie(this.getValueMethod, message, new Object[0])).intValue();
/* 2715 */           return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
/*      */         } 
/* 2717 */         return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.get(message), new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessage.Builder builder) {
/* 2722 */         if (this.supportUnknownEnumValue) {
/* 2723 */           int value = ((Integer)GeneratedMessage.invokeOrDie(this.getValueMethodBuilder, builder, new Object[0])).intValue();
/* 2724 */           return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
/*      */         } 
/* 2726 */         return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.get(builder), new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessage.Builder builder, Object value) {
/* 2731 */         if (this.supportUnknownEnumValue) {
/* 2732 */           GeneratedMessage.invokeOrDie(this.setValueMethod, builder, new Object[] {
/* 2733 */                 Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
/*      */           return;
/*      */         } 
/* 2736 */         super.set(builder, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
/*      */       } }
/*      */     private static final class RepeatedEnumFieldAccessor extends RepeatedFieldAccessor { private Descriptors.EnumDescriptor enumDescriptor; private final Method valueOfMethod; private final Method getValueDescriptorMethod;
/*      */       private boolean supportUnknownEnumValue;
/*      */       private Method getRepeatedValueMethod;
/*      */       private Method getRepeatedValueMethodBuilder;
/*      */       private Method setRepeatedValueMethod;
/*      */       private Method addRepeatedValueMethod;
/*      */       
/*      */       RepeatedEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
/* 2746 */         super(descriptor, camelCaseName, messageClass, builderClass);
/*      */         
/* 2748 */         this.enumDescriptor = descriptor.getEnumType();
/*      */         
/* 2750 */         this.valueOfMethod = GeneratedMessage.getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
/*      */         
/* 2752 */         this
/* 2753 */           .getValueDescriptorMethod = GeneratedMessage.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
/*      */         
/* 2755 */         this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
/* 2756 */         if (this.supportUnknownEnumValue) {
/* 2757 */           this
/* 2758 */             .getRepeatedValueMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[] { int.class });
/* 2759 */           this
/* 2760 */             .getRepeatedValueMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[] { int.class });
/* 2761 */           this
/* 2762 */             .setRepeatedValueMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[] { int.class, int.class });
/* 2763 */           this
/* 2764 */             .addRepeatedValueMethod = GeneratedMessage.getMethodOrDie(builderClass, "add" + camelCaseName + "Value", new Class[] { int.class });
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
/*      */       public Object get(GeneratedMessage message) {
/* 2781 */         List<Object> newList = new ArrayList();
/* 2782 */         int size = getRepeatedCount(message);
/* 2783 */         for (int i = 0; i < size; i++) {
/* 2784 */           newList.add(getRepeated(message, i));
/*      */         }
/* 2786 */         return Collections.unmodifiableList(newList);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object get(GeneratedMessage.Builder builder) {
/* 2792 */         List<Object> newList = new ArrayList();
/* 2793 */         int size = getRepeatedCount(builder);
/* 2794 */         for (int i = 0; i < size; i++) {
/* 2795 */           newList.add(getRepeated(builder, i));
/*      */         }
/* 2797 */         return Collections.unmodifiableList(newList);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessage message, int index) {
/* 2803 */         if (this.supportUnknownEnumValue) {
/* 2804 */           int value = ((Integer)GeneratedMessage.invokeOrDie(this.getRepeatedValueMethod, message, new Object[] { Integer.valueOf(index) })).intValue();
/* 2805 */           return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
/*      */         } 
/* 2807 */         return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super
/* 2808 */             .getRepeated(message, index), new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRepeated(GeneratedMessage.Builder builder, int index) {
/* 2813 */         if (this.supportUnknownEnumValue) {
/* 2814 */           int value = ((Integer)GeneratedMessage.invokeOrDie(this.getRepeatedValueMethodBuilder, builder, new Object[] { Integer.valueOf(index) })).intValue();
/* 2815 */           return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
/*      */         } 
/* 2817 */         return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super
/* 2818 */             .getRepeated(builder, index), new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
/* 2823 */         if (this.supportUnknownEnumValue) {
/* 2824 */           GeneratedMessage.invokeOrDie(this.setRepeatedValueMethod, builder, new Object[] { Integer.valueOf(index), 
/* 2825 */                 Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
/*      */           return;
/*      */         } 
/* 2828 */         super.setRepeated(builder, index, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
/*      */       }
/*      */ 
/*      */       
/*      */       public void addRepeated(GeneratedMessage.Builder builder, Object value) {
/* 2833 */         if (this.supportUnknownEnumValue) {
/* 2834 */           GeneratedMessage.invokeOrDie(this.addRepeatedValueMethod, builder, new Object[] {
/* 2835 */                 Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
/*      */           return;
/*      */         } 
/* 2838 */         super.addRepeated(builder, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
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
/*      */       SingularStringFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass, String containingOneofCamelCaseName) {
/* 2862 */         super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
/*      */         
/* 2864 */         this.getBytesMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Bytes", new Class[0]);
/*      */         
/* 2866 */         this.getBytesMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Bytes", new Class[0]);
/*      */         
/* 2868 */         this.setBytesMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName + "Bytes", new Class[] { ByteString.class });
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Object getRaw(GeneratedMessage message) {
/* 2878 */         return GeneratedMessage.invokeOrDie(this.getBytesMethod, message, new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public Object getRaw(GeneratedMessage.Builder builder) {
/* 2883 */         return GeneratedMessage.invokeOrDie(this.getBytesMethodBuilder, builder, new Object[0]);
/*      */       }
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessage.Builder builder, Object value) {
/* 2888 */         if (value instanceof ByteString) {
/* 2889 */           GeneratedMessage.invokeOrDie(this.setBytesMethodBuilder, builder, new Object[] { value });
/*      */         } else {
/* 2891 */           super.set(builder, value);
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
/*      */       SingularMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass, String containingOneofCamelCaseName) {
/* 2905 */         super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
/*      */ 
/*      */         
/* 2908 */         this.newBuilderMethod = GeneratedMessage.getMethodOrDie(this.type, "newBuilder", new Class[0]);
/* 2909 */         this
/* 2910 */           .getBuilderMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[0]);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private Object coerceType(Object value) {
/* 2917 */         if (this.type.isInstance(value)) {
/* 2918 */           return value;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2924 */         return ((Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]))
/* 2925 */           .mergeFrom((Message)value).buildPartial();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public void set(GeneratedMessage.Builder builder, Object value) {
/* 2931 */         super.set(builder, coerceType(value));
/*      */       }
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 2935 */         return (Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
/*      */       }
/*      */       
/*      */       public Message.Builder getBuilder(GeneratedMessage.Builder builder) {
/* 2939 */         return (Message.Builder)GeneratedMessage.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[0]);
/*      */       }
/*      */     }
/*      */     
/*      */     private static final class RepeatedMessageFieldAccessor
/*      */       extends RepeatedFieldAccessor {
/*      */       private final Method newBuilderMethod;
/*      */       private final Method getBuilderMethodBuilder;
/*      */       
/*      */       RepeatedMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
/* 2949 */         super(descriptor, camelCaseName, messageClass, builderClass);
/*      */         
/* 2951 */         this.newBuilderMethod = GeneratedMessage.getMethodOrDie(this.type, "newBuilder", new Class[0]);
/* 2952 */         this.getBuilderMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[] { int.class });
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private Object coerceType(Object value) {
/* 2960 */         if (this.type.isInstance(value)) {
/* 2961 */           return value;
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2967 */         return ((Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]))
/* 2968 */           .mergeFrom((Message)value).build();
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
/* 2975 */         super.setRepeated(builder, index, coerceType(value));
/*      */       }
/*      */       
/*      */       public void addRepeated(GeneratedMessage.Builder builder, Object value) {
/* 2979 */         super.addRepeated(builder, coerceType(value));
/*      */       }
/*      */       
/*      */       public Message.Builder newBuilder() {
/* 2983 */         return (Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
/*      */       }
/*      */       
/*      */       public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder builder, int index)
/*      */       {
/* 2988 */         return (Message.Builder)GeneratedMessage.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[] {
/* 2989 */               Integer.valueOf(index) });
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
/* 3001 */     return new GeneratedMessageLite.SerializedForm(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static <MessageType extends ExtendableMessage<MessageType>, T> Extension<MessageType, T> checkNotLite(ExtensionLite<MessageType, T> extension) {
/* 3011 */     if (extension.isLite()) {
/* 3012 */       throw new IllegalArgumentException("Expected non-lite extension.");
/*      */     }
/*      */     
/* 3015 */     return (Extension<MessageType, T>)extension;
/*      */   }
/*      */   private static class OneofAccessor {
/*      */     private final Descriptors.Descriptor descriptor;
/* 3019 */     private final Method caseMethod; private final Method caseMethodBuilder; private final Method clearMethod; OneofAccessor(Descriptors.Descriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) { this.descriptor = descriptor; this.caseMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]); this.caseMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]); this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]); } public boolean has(GeneratedMessage message) { if (((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() == 0) return false;  return true; } public boolean has(GeneratedMessage.Builder builder) { if (((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() == 0) return false;  return true; } public Descriptors.FieldDescriptor get(GeneratedMessage message) { int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber(); if (fieldNumber > 0) return this.descriptor.findFieldByNumber(fieldNumber);  return null; } public Descriptors.FieldDescriptor get(GeneratedMessage.Builder builder) { int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber(); if (fieldNumber > 0) return this.descriptor.findFieldByNumber(fieldNumber);  return null; } public void clear(GeneratedMessage.Builder builder) { GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]); } } protected static int computeStringSize(int fieldNumber, Object value) { if (value instanceof String) {
/* 3020 */       return CodedOutputStream.computeStringSize(fieldNumber, (String)value);
/*      */     }
/* 3022 */     return CodedOutputStream.computeBytesSize(fieldNumber, (ByteString)value); }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static int computeStringSizeNoTag(Object value) {
/* 3027 */     if (value instanceof String) {
/* 3028 */       return CodedOutputStream.computeStringSizeNoTag((String)value);
/*      */     }
/* 3030 */     return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void writeString(CodedOutputStream output, int fieldNumber, Object value) throws IOException {
/* 3036 */     if (value instanceof String) {
/* 3037 */       output.writeString(fieldNumber, (String)value);
/*      */     } else {
/* 3039 */       output.writeBytes(fieldNumber, (ByteString)value);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected static void writeStringNoTag(CodedOutputStream output, Object value) throws IOException {
/* 3045 */     if (value instanceof String) {
/* 3046 */       output.writeStringNoTag((String)value);
/*      */     } else {
/* 3048 */       output.writeBytesNoTag((ByteString)value);
/*      */     } 
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
/*      */     Object get(GeneratedMessage param1GeneratedMessage);
/*      */     
/*      */     Object get(GeneratedMessage.Builder param1Builder);
/*      */     
/*      */     Object getRaw(GeneratedMessage param1GeneratedMessage);
/*      */     
/*      */     Object getRaw(GeneratedMessage.Builder param1Builder);
/*      */     
/*      */     void set(GeneratedMessage.Builder param1Builder, Object param1Object);
/*      */     
/*      */     Object getRepeated(GeneratedMessage param1GeneratedMessage, int param1Int);
/*      */     
/*      */     Object getRepeated(GeneratedMessage.Builder param1Builder, int param1Int);
/*      */     
/*      */     Object getRepeatedRaw(GeneratedMessage param1GeneratedMessage, int param1Int);
/*      */     
/*      */     Object getRepeatedRaw(GeneratedMessage.Builder param1Builder, int param1Int);
/*      */     
/*      */     void setRepeated(GeneratedMessage.Builder param1Builder, int param1Int, Object param1Object);
/*      */     
/*      */     void addRepeated(GeneratedMessage.Builder param1Builder, Object param1Object);
/*      */     
/*      */     boolean has(GeneratedMessage param1GeneratedMessage);
/*      */     
/*      */     boolean has(GeneratedMessage.Builder param1Builder);
/*      */     
/*      */     int getRepeatedCount(GeneratedMessage param1GeneratedMessage);
/*      */     
/*      */     int getRepeatedCount(GeneratedMessage.Builder param1Builder);
/*      */     
/*      */     void clear(GeneratedMessage.Builder param1Builder);
/*      */     
/*      */     Message.Builder newBuilder();
/*      */     
/*      */     Message.Builder getBuilder(GeneratedMessage.Builder param1Builder);
/*      */     
/*      */     Message.Builder getRepeatedBuilder(GeneratedMessage.Builder param1Builder, int param1Int);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\GeneratedMessage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */