/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public abstract class AbstractMessage
/*     */   extends AbstractMessageLite
/*     */   implements Message
/*     */ {
/*     */   public boolean isInitialized() {
/*  58 */     return MessageReflection.isInitialized(this);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Message.Builder newBuilderForType(BuilderParent parent) {
/*  83 */     throw new UnsupportedOperationException("Nested builder is not supported for this type.");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> findInitializationErrors() {
/*  89 */     return MessageReflection.findMissingFields(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getInitializationErrorString() {
/*  94 */     return MessageReflection.delimitWithCommas(findInitializationErrors());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/* 100 */     throw new UnsupportedOperationException("hasOneof() is not implemented.");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/* 106 */     throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 111 */     return TextFormat.printer().printToString(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(CodedOutputStream output) throws IOException {
/* 116 */     MessageReflection.writeMessageTo(this, getAllFields(), output, false);
/*     */   }
/*     */   
/* 119 */   protected int memoizedSize = -1;
/*     */ 
/*     */   
/*     */   int getMemoizedSerializedSize() {
/* 123 */     return this.memoizedSize;
/*     */   }
/*     */ 
/*     */   
/*     */   void setMemoizedSerializedSize(int size) {
/* 128 */     this.memoizedSize = size;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/* 133 */     int size = this.memoizedSize;
/* 134 */     if (size != -1) {
/* 135 */       return size;
/*     */     }
/*     */     
/* 138 */     this.memoizedSize = MessageReflection.getSerializedSize(this, getAllFields());
/* 139 */     return this.memoizedSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 144 */     if (other == this) {
/* 145 */       return true;
/*     */     }
/* 147 */     if (!(other instanceof Message)) {
/* 148 */       return false;
/*     */     }
/* 150 */     Message otherMessage = (Message)other;
/* 151 */     if (getDescriptorForType() != otherMessage.getDescriptorForType()) {
/* 152 */       return false;
/*     */     }
/* 154 */     return (compareFields(getAllFields(), otherMessage.getAllFields()) && 
/* 155 */       getUnknownFields().equals(otherMessage.getUnknownFields()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 160 */     int hash = this.memoizedHashCode;
/* 161 */     if (hash == 0) {
/* 162 */       hash = 41;
/* 163 */       hash = 19 * hash + getDescriptorForType().hashCode();
/* 164 */       hash = hashFields(hash, getAllFields());
/* 165 */       hash = 29 * hash + getUnknownFields().hashCode();
/* 166 */       this.memoizedHashCode = hash;
/*     */     } 
/* 168 */     return hash;
/*     */   }
/*     */   
/*     */   private static ByteString toByteString(Object value) {
/* 172 */     if (value instanceof byte[]) {
/* 173 */       return ByteString.copyFrom((byte[])value);
/*     */     }
/* 175 */     return (ByteString)value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean compareBytes(Object a, Object b) {
/* 184 */     if (a instanceof byte[] && b instanceof byte[]) {
/* 185 */       return Arrays.equals((byte[])a, (byte[])b);
/*     */     }
/* 187 */     return toByteString(a).equals(toByteString(b));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map convertMapEntryListToMap(List list) {
/* 193 */     if (list.isEmpty()) {
/* 194 */       return Collections.emptyMap();
/*     */     }
/* 196 */     Map<Object, Object> result = new HashMap<>();
/* 197 */     Iterator<Message> iterator = list.iterator();
/* 198 */     Message entry = iterator.next();
/* 199 */     Descriptors.Descriptor descriptor = entry.getDescriptorForType();
/* 200 */     Descriptors.FieldDescriptor key = descriptor.findFieldByName("key");
/* 201 */     Descriptors.FieldDescriptor value = descriptor.findFieldByName("value");
/* 202 */     Object fieldValue = entry.getField(value);
/* 203 */     if (fieldValue instanceof Descriptors.EnumValueDescriptor) {
/* 204 */       fieldValue = Integer.valueOf(((Descriptors.EnumValueDescriptor)fieldValue).getNumber());
/*     */     }
/* 206 */     result.put(entry.getField(key), fieldValue);
/* 207 */     while (iterator.hasNext()) {
/* 208 */       entry = iterator.next();
/* 209 */       fieldValue = entry.getField(value);
/* 210 */       if (fieldValue instanceof Descriptors.EnumValueDescriptor) {
/* 211 */         fieldValue = Integer.valueOf(((Descriptors.EnumValueDescriptor)fieldValue).getNumber());
/*     */       }
/* 213 */       result.put(entry.getField(key), fieldValue);
/*     */     } 
/* 215 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean compareMapField(Object a, Object b) {
/* 221 */     Map<?, ?> ma = convertMapEntryListToMap((List)a);
/* 222 */     Map<?, ?> mb = convertMapEntryListToMap((List)b);
/* 223 */     return MapFieldLite.equals(ma, mb);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean compareFields(Map<Descriptors.FieldDescriptor, Object> a, Map<Descriptors.FieldDescriptor, Object> b) {
/* 234 */     if (a.size() != b.size()) {
/* 235 */       return false;
/*     */     }
/* 237 */     for (Descriptors.FieldDescriptor descriptor : a.keySet()) {
/* 238 */       if (!b.containsKey(descriptor)) {
/* 239 */         return false;
/*     */       }
/* 241 */       Object value1 = a.get(descriptor);
/* 242 */       Object value2 = b.get(descriptor);
/* 243 */       if (descriptor.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
/* 244 */         if (descriptor.isRepeated()) {
/* 245 */           List list1 = (List)value1;
/* 246 */           List list2 = (List)value2;
/* 247 */           if (list1.size() != list2.size()) {
/* 248 */             return false;
/*     */           }
/* 250 */           for (int i = 0; i < list1.size(); i++) {
/* 251 */             if (!compareBytes(list1.get(i), list2.get(i))) {
/* 252 */               return false;
/*     */             }
/*     */           } 
/*     */           continue;
/*     */         } 
/* 257 */         if (!compareBytes(value1, value2))
/* 258 */           return false; 
/*     */         continue;
/*     */       } 
/* 261 */       if (descriptor.isMapField()) {
/* 262 */         if (!compareMapField(value1, value2)) {
/* 263 */           return false;
/*     */         }
/*     */         continue;
/*     */       } 
/* 267 */       if (!value1.equals(value2)) {
/* 268 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 272 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static int hashMapField(Object value) {
/* 278 */     return MapFieldLite.calculateHashCodeForMap(convertMapEntryListToMap((List)value));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected static int hashFields(int hash, Map<Descriptors.FieldDescriptor, Object> map) {
/* 284 */     for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : map.entrySet()) {
/* 285 */       Descriptors.FieldDescriptor field = entry.getKey();
/* 286 */       Object value = entry.getValue();
/* 287 */       hash = 37 * hash + field.getNumber();
/* 288 */       if (field.isMapField()) {
/* 289 */         hash = 53 * hash + hashMapField(value); continue;
/* 290 */       }  if (field.getType() != Descriptors.FieldDescriptor.Type.ENUM) {
/* 291 */         hash = 53 * hash + value.hashCode(); continue;
/* 292 */       }  if (field.isRepeated()) {
/* 293 */         List<? extends Internal.EnumLite> list = (List<? extends Internal.EnumLite>)value;
/* 294 */         hash = 53 * hash + Internal.hashEnumList(list); continue;
/*     */       } 
/* 296 */       hash = 53 * hash + Internal.hashEnum((Internal.EnumLite)value);
/*     */     } 
/*     */     
/* 299 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   UninitializedMessageException newUninitializedMessageException() {
/* 308 */     return Builder.newUninitializedMessageException(this);
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
/*     */   public static abstract class Builder<BuilderType extends Builder<BuilderType>>
/*     */     extends AbstractMessageLite.Builder
/*     */     implements Message.Builder
/*     */   {
/*     */     public BuilderType clone() {
/* 325 */       throw new UnsupportedOperationException("clone() should be implemented in subclasses.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
/* 331 */       throw new UnsupportedOperationException("hasOneof() is not implemented.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
/* 337 */       throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType clearOneof(Descriptors.OneofDescriptor oneof) {
/* 343 */       throw new UnsupportedOperationException("clearOneof() is not implemented.");
/*     */     }
/*     */ 
/*     */     
/*     */     public BuilderType clear() {
/* 348 */       for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : getAllFields().entrySet()) {
/* 349 */         clearField(entry.getKey());
/*     */       }
/* 351 */       return (BuilderType)this;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> findInitializationErrors() {
/* 356 */       return MessageReflection.findMissingFields(this);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getInitializationErrorString() {
/* 361 */       return MessageReflection.delimitWithCommas(findInitializationErrors());
/*     */     }
/*     */ 
/*     */     
/*     */     protected BuilderType internalMergeFrom(AbstractMessageLite other) {
/* 366 */       return mergeFrom((Message)other);
/*     */     }
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(Message other) {
/* 371 */       return mergeFrom(other, other.getAllFields());
/*     */     }
/*     */     
/*     */     BuilderType mergeFrom(Message other, Map<Descriptors.FieldDescriptor, Object> allFields) {
/* 375 */       if (other.getDescriptorForType() != getDescriptorForType()) {
/* 376 */         throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
/*     */       }
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
/* 389 */       for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : allFields.entrySet()) {
/* 390 */         Descriptors.FieldDescriptor field = entry.getKey();
/* 391 */         if (field.isRepeated()) {
/* 392 */           for (Object element : entry.getValue())
/* 393 */             addRepeatedField(field, element);  continue;
/*     */         } 
/* 395 */         if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 396 */           Message existingValue = (Message)getField(field);
/* 397 */           if (existingValue == existingValue.getDefaultInstanceForType()) {
/* 398 */             setField(field, entry.getValue()); continue;
/*     */           } 
/* 400 */           setField(field, existingValue
/*     */ 
/*     */               
/* 403 */               .newBuilderForType()
/* 404 */               .mergeFrom(existingValue)
/* 405 */               .mergeFrom((Message)entry.getValue())
/* 406 */               .build());
/*     */           continue;
/*     */         } 
/* 409 */         setField(field, entry.getValue());
/*     */       } 
/*     */ 
/*     */       
/* 413 */       mergeUnknownFields(other.getUnknownFields());
/*     */       
/* 415 */       return (BuilderType)this;
/*     */     }
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(CodedInputStream input) throws IOException {
/* 420 */       return mergeFrom(input, ExtensionRegistry.getEmptyRegistry());
/*     */     }
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*     */       int tag;
/*     */       MessageReflection.BuilderAdapter builderAdapter;
/* 427 */       boolean discardUnknown = input.shouldDiscardUnknownFields();
/*     */       
/* 429 */       UnknownFieldSet.Builder unknownFields = discardUnknown ? null : UnknownFieldSet.newBuilder(getUnknownFields());
/*     */       do {
/* 431 */         tag = input.readTag();
/* 432 */         if (tag == 0) {
/*     */           break;
/*     */         }
/*     */         
/* 436 */         builderAdapter = new MessageReflection.BuilderAdapter(this);
/*     */       }
/* 438 */       while (MessageReflection.mergeFieldFrom(input, unknownFields, extensionRegistry, 
/* 439 */           getDescriptorForType(), builderAdapter, tag));
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 444 */       if (unknownFields != null) {
/* 445 */         setUnknownFields(unknownFields.build());
/*     */       }
/* 447 */       return (BuilderType)this;
/*     */     }
/*     */ 
/*     */     
/*     */     public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 452 */       setUnknownFields(
/* 453 */           UnknownFieldSet.newBuilder(getUnknownFields()).mergeFrom(unknownFields).build());
/* 454 */       return (BuilderType)this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
/* 459 */       throw new UnsupportedOperationException("getFieldBuilder() called on an unsupported message type.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
/* 465 */       throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on an unsupported message type.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 471 */       return TextFormat.printer().printToString(this);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected static UninitializedMessageException newUninitializedMessageException(Message message) {
/* 477 */       return new UninitializedMessageException(MessageReflection.findMissingFields(message));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void markClean() {
/* 489 */       throw new IllegalStateException("Should be overridden by subclasses.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void dispose() {
/* 500 */       throw new IllegalStateException("Should be overridden by subclasses.");
/*     */     }
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
/*     */     public BuilderType mergeFrom(ByteString data) throws InvalidProtocolBufferException {
/* 524 */       return (BuilderType)super.mergeFrom(data);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 531 */       return (BuilderType)super.mergeFrom(data, extensionRegistry);
/*     */     }
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(byte[] data) throws InvalidProtocolBufferException {
/* 536 */       return (BuilderType)super.mergeFrom(data);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
/* 542 */       return (BuilderType)super.mergeFrom(data, off, len);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 548 */       return (BuilderType)super.mergeFrom(data, extensionRegistry);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 558 */       return (BuilderType)super.mergeFrom(data, off, len, extensionRegistry);
/*     */     }
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(InputStream input) throws IOException {
/* 563 */       return (BuilderType)super.mergeFrom(input);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public BuilderType mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 569 */       return (BuilderType)super.mergeFrom(input, extensionRegistry);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean mergeDelimitedFrom(InputStream input) throws IOException {
/* 574 */       return super.mergeDelimitedFrom(input);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 580 */       return super.mergeDelimitedFrom(input, extensionRegistry);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected static int hashLong(long n) {
/* 590 */     return (int)(n ^ n >>> 32L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected static int hashBoolean(boolean b) {
/* 599 */     return b ? 1231 : 1237;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected static int hashEnum(Internal.EnumLite e) {
/* 608 */     return e.getNumber();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected static int hashEnumList(List<? extends Internal.EnumLite> list) {
/* 617 */     int hash = 1;
/* 618 */     for (Internal.EnumLite e : list) {
/* 619 */       hash = 31 * hash + hashEnum(e);
/*     */     }
/* 621 */     return hash;
/*     */   }
/*     */   
/*     */   protected static interface BuilderParent {
/*     */     void markDirty();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\AbstractMessage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */