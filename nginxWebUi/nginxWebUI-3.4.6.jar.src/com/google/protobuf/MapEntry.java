/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
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
/*     */ public final class MapEntry<K, V>
/*     */   extends AbstractMessage
/*     */ {
/*     */   private final K key;
/*     */   private final V value;
/*     */   private final Metadata<K, V> metadata;
/*     */   private volatile int cachedSerializedSize;
/*     */   
/*     */   private static final class Metadata<K, V>
/*     */     extends MapEntryLite.Metadata<K, V>
/*     */   {
/*     */     public final Descriptors.Descriptor descriptor;
/*     */     public final Parser<MapEntry<K, V>> parser;
/*     */     
/*     */     public Metadata(Descriptors.Descriptor descriptor, MapEntry<K, V> defaultInstance, WireFormat.FieldType keyType, WireFormat.FieldType valueType) {
/*  62 */       super(keyType, defaultInstance.key, valueType, defaultInstance.value);
/*  63 */       this.descriptor = descriptor;
/*  64 */       this.parser = new AbstractParser<MapEntry<K, V>>()
/*     */         {
/*     */ 
/*     */ 
/*     */           
/*     */           public MapEntry<K, V> parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*     */           {
/*  71 */             return new MapEntry<>(MapEntry.Metadata.this, input, extensionRegistry);
/*     */           }
/*     */         };
/*     */     }
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
/*     */   private MapEntry(Descriptors.Descriptor descriptor, WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue)
/*     */   {
/* 139 */     this.cachedSerializedSize = -1; this.key = defaultKey; this.value = defaultValue; this.metadata = new Metadata<>(descriptor, this, keyType, valueType); } private MapEntry(Metadata<K, V> metadata, K key, V value) { this.cachedSerializedSize = -1; this.key = key; this.value = value; this.metadata = metadata; } private MapEntry(Metadata<K, V> metadata, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this.cachedSerializedSize = -1; try { this.metadata = metadata; Map.Entry<K, V> entry = MapEntryLite.parseEntry(input, metadata, extensionRegistry); this.key = entry.getKey(); this.value = entry.getValue(); }
/*     */     catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); }
/*     */     catch (IOException e)
/*     */     { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); }
/* 143 */      } public int getSerializedSize() { if (this.cachedSerializedSize != -1) {
/* 144 */       return this.cachedSerializedSize;
/*     */     }
/*     */     
/* 147 */     int size = MapEntryLite.computeSerializedSize(this.metadata, this.key, this.value);
/* 148 */     this.cachedSerializedSize = size;
/* 149 */     return size; }
/*     */   public static <K, V> MapEntry<K, V> newDefaultInstance(Descriptors.Descriptor descriptor, WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) { return new MapEntry<>(descriptor, keyType, defaultKey, valueType, defaultValue); }
/*     */   public K getKey() { return this.key; } public V getValue() {
/*     */     return this.value;
/*     */   } public void writeTo(CodedOutputStream output) throws IOException {
/* 154 */     MapEntryLite.writeTo(output, this.metadata, this.key, this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInitialized() {
/* 159 */     return isInitialized(this.metadata, this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<MapEntry<K, V>> getParserForType() {
/* 164 */     return this.metadata.parser;
/*     */   }
/*     */ 
/*     */   
/*     */   public Builder<K, V> newBuilderForType() {
/* 169 */     return new Builder<>(this.metadata);
/*     */   }
/*     */ 
/*     */   
/*     */   public Builder<K, V> toBuilder() {
/* 174 */     return new Builder<>(this.metadata, this.key, this.value, true, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public MapEntry<K, V> getDefaultInstanceForType() {
/* 179 */     return new MapEntry(this.metadata, this.metadata.defaultKey, this.metadata.defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Descriptors.Descriptor getDescriptorForType() {
/* 184 */     return this.metadata.descriptor;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/* 189 */     TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
/* 190 */     for (Descriptors.FieldDescriptor field : this.metadata.descriptor.getFields()) {
/* 191 */       if (hasField(field)) {
/* 192 */         result.put(field, getField(field));
/*     */       }
/*     */     } 
/* 195 */     return Collections.unmodifiableMap(result);
/*     */   }
/*     */   
/*     */   private void checkFieldDescriptor(Descriptors.FieldDescriptor field) {
/* 199 */     if (field.getContainingType() != this.metadata.descriptor) {
/* 200 */       throw new RuntimeException("Wrong FieldDescriptor \"" + field
/*     */           
/* 202 */           .getFullName() + "\" used in message \"" + this.metadata.descriptor
/*     */           
/* 204 */           .getFullName());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasField(Descriptors.FieldDescriptor field) {
/* 210 */     checkFieldDescriptor(field);
/*     */ 
/*     */     
/* 213 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getField(Descriptors.FieldDescriptor field) {
/* 218 */     checkFieldDescriptor(field);
/* 219 */     Object result = (field.getNumber() == 1) ? getKey() : getValue();
/*     */     
/* 221 */     if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
/* 222 */       result = field.getEnumType().findValueByNumberCreatingIfUnknown(((Integer)result).intValue());
/*     */     }
/* 224 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/* 229 */     throw new RuntimeException("There is no repeated field in a map entry message.");
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/* 234 */     throw new RuntimeException("There is no repeated field in a map entry message.");
/*     */   }
/*     */ 
/*     */   
/*     */   public UnknownFieldSet getUnknownFields() {
/* 239 */     return UnknownFieldSet.getDefaultInstance();
/*     */   }
/*     */   
/*     */   public static class Builder<K, V>
/*     */     extends AbstractMessage.Builder<Builder<K, V>> {
/*     */     private final MapEntry.Metadata<K, V> metadata;
/*     */     private K key;
/*     */     private V value;
/*     */     private boolean hasKey;
/*     */     private boolean hasValue;
/*     */     
/*     */     private Builder(MapEntry.Metadata<K, V> metadata) {
/* 251 */       this(metadata, metadata.defaultKey, metadata.defaultValue, false, false);
/*     */     }
/*     */     
/*     */     private Builder(MapEntry.Metadata<K, V> metadata, K key, V value, boolean hasKey, boolean hasValue) {
/* 255 */       this.metadata = metadata;
/* 256 */       this.key = key;
/* 257 */       this.value = value;
/* 258 */       this.hasKey = hasKey;
/* 259 */       this.hasValue = hasValue;
/*     */     }
/*     */     
/*     */     public K getKey() {
/* 263 */       return this.key;
/*     */     }
/*     */     
/*     */     public V getValue() {
/* 267 */       return this.value;
/*     */     }
/*     */     
/*     */     public Builder<K, V> setKey(K key) {
/* 271 */       this.key = key;
/* 272 */       this.hasKey = true;
/* 273 */       return this;
/*     */     }
/*     */     
/*     */     public Builder<K, V> clearKey() {
/* 277 */       this.key = this.metadata.defaultKey;
/* 278 */       this.hasKey = false;
/* 279 */       return this;
/*     */     }
/*     */     
/*     */     public Builder<K, V> setValue(V value) {
/* 283 */       this.value = value;
/* 284 */       this.hasValue = true;
/* 285 */       return this;
/*     */     }
/*     */     
/*     */     public Builder<K, V> clearValue() {
/* 289 */       this.value = this.metadata.defaultValue;
/* 290 */       this.hasValue = false;
/* 291 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MapEntry<K, V> build() {
/* 296 */       MapEntry<K, V> result = buildPartial();
/* 297 */       if (!result.isInitialized()) {
/* 298 */         throw newUninitializedMessageException(result);
/*     */       }
/* 300 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public MapEntry<K, V> buildPartial() {
/* 305 */       return new MapEntry<>(this.metadata, this.key, this.value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Descriptors.Descriptor getDescriptorForType() {
/* 310 */       return this.metadata.descriptor;
/*     */     }
/*     */     
/*     */     private void checkFieldDescriptor(Descriptors.FieldDescriptor field) {
/* 314 */       if (field.getContainingType() != this.metadata.descriptor) {
/* 315 */         throw new RuntimeException("Wrong FieldDescriptor \"" + field
/*     */             
/* 317 */             .getFullName() + "\" used in message \"" + this.metadata.descriptor
/*     */             
/* 319 */             .getFullName());
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
/* 325 */       checkFieldDescriptor(field);
/*     */ 
/*     */ 
/*     */       
/* 329 */       if (field.getNumber() != 2 || field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
/* 330 */         throw new RuntimeException("\"" + field.getFullName() + "\" is not a message value field.");
/*     */       }
/* 332 */       return ((Message)this.value).newBuilderForType();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<K, V> setField(Descriptors.FieldDescriptor field, Object value) {
/* 338 */       checkFieldDescriptor(field);
/* 339 */       if (field.getNumber() == 1) {
/* 340 */         setKey((K)value);
/*     */       } else {
/* 342 */         if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
/* 343 */           value = Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber());
/* 344 */         } else if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && 
/* 345 */           value != null && !this.metadata.defaultValue.getClass().isInstance(value)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 351 */           value = ((Message)this.metadata.defaultValue).toBuilder().mergeFrom((Message)value).build();
/*     */         } 
/*     */         
/* 354 */         setValue((V)value);
/*     */       } 
/* 356 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder<K, V> clearField(Descriptors.FieldDescriptor field) {
/* 361 */       checkFieldDescriptor(field);
/* 362 */       if (field.getNumber() == 1) {
/* 363 */         clearKey();
/*     */       } else {
/* 365 */         clearValue();
/*     */       } 
/* 367 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder<K, V> setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 372 */       throw new RuntimeException("There is no repeated field in a map entry message.");
/*     */     }
/*     */ 
/*     */     
/*     */     public Builder<K, V> addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 377 */       throw new RuntimeException("There is no repeated field in a map entry message.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<K, V> setUnknownFields(UnknownFieldSet unknownFields) {
/* 383 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MapEntry<K, V> getDefaultInstanceForType() {
/* 388 */       return new MapEntry<>(this.metadata, this.metadata.defaultKey, this.metadata.defaultValue);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isInitialized() {
/* 393 */       return MapEntry.isInitialized(this.metadata, this.value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/* 398 */       TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
/* 399 */       for (Descriptors.FieldDescriptor field : this.metadata.descriptor.getFields()) {
/* 400 */         if (hasField(field)) {
/* 401 */           result.put(field, getField(field));
/*     */         }
/*     */       } 
/* 404 */       return Collections.unmodifiableMap(result);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasField(Descriptors.FieldDescriptor field) {
/* 409 */       checkFieldDescriptor(field);
/* 410 */       return (field.getNumber() == 1) ? this.hasKey : this.hasValue;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getField(Descriptors.FieldDescriptor field) {
/* 415 */       checkFieldDescriptor(field);
/* 416 */       Object result = (field.getNumber() == 1) ? getKey() : getValue();
/*     */       
/* 418 */       if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
/* 419 */         result = field.getEnumType().findValueByNumberCreatingIfUnknown(((Integer)result).intValue());
/*     */       }
/* 421 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
/* 426 */       throw new RuntimeException("There is no repeated field in a map entry message.");
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
/* 431 */       throw new RuntimeException("There is no repeated field in a map entry message.");
/*     */     }
/*     */ 
/*     */     
/*     */     public UnknownFieldSet getUnknownFields() {
/* 436 */       return UnknownFieldSet.getDefaultInstance();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder<K, V> clone() {
/* 442 */       return new Builder(this.metadata, this.key, this.value, this.hasKey, this.hasValue);
/*     */     }
/*     */   }
/*     */   
/*     */   private static <V> boolean isInitialized(Metadata metadata, V value) {
/* 447 */     if (metadata.valueType.getJavaType() == WireFormat.JavaType.MESSAGE) {
/* 448 */       return ((MessageLite)value).isInitialized();
/*     */     }
/* 450 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   final Metadata<K, V> getMetadata() {
/* 455 */     return this.metadata;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MapEntry.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */