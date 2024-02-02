/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.AbstractMap;
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
/*     */ public class MapEntryLite<K, V>
/*     */ {
/*     */   private static final int KEY_FIELD_NUMBER = 1;
/*     */   private static final int VALUE_FIELD_NUMBER = 2;
/*     */   private final Metadata<K, V> metadata;
/*     */   private final K key;
/*     */   private final V value;
/*     */   
/*     */   static class Metadata<K, V>
/*     */   {
/*     */     public final WireFormat.FieldType keyType;
/*     */     public final K defaultKey;
/*     */     public final WireFormat.FieldType valueType;
/*     */     public final V defaultValue;
/*     */     
/*     */     public Metadata(WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
/*  58 */       this.keyType = keyType;
/*  59 */       this.defaultKey = defaultKey;
/*  60 */       this.valueType = valueType;
/*  61 */       this.defaultValue = defaultValue;
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
/*     */   private MapEntryLite(WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
/*  75 */     this.metadata = new Metadata<>(keyType, defaultKey, valueType, defaultValue);
/*  76 */     this.key = defaultKey;
/*  77 */     this.value = defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   private MapEntryLite(Metadata<K, V> metadata, K key, V value) {
/*  82 */     this.metadata = metadata;
/*  83 */     this.key = key;
/*  84 */     this.value = value;
/*     */   }
/*     */   
/*     */   public K getKey() {
/*  88 */     return this.key;
/*     */   }
/*     */   
/*     */   public V getValue() {
/*  92 */     return this.value;
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
/*     */   public static <K, V> MapEntryLite<K, V> newDefaultInstance(WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
/* 104 */     return new MapEntryLite<>(keyType, defaultKey, valueType, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   static <K, V> void writeTo(CodedOutputStream output, Metadata<K, V> metadata, K key, V value) throws IOException {
/* 109 */     FieldSet.writeElement(output, metadata.keyType, 1, key);
/* 110 */     FieldSet.writeElement(output, metadata.valueType, 2, value);
/*     */   }
/*     */   
/*     */   static <K, V> int computeSerializedSize(Metadata<K, V> metadata, K key, V value) {
/* 114 */     return FieldSet.computeElementSize(metadata.keyType, 1, key) + 
/* 115 */       FieldSet.computeElementSize(metadata.valueType, 2, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <T> T parseField(CodedInputStream input, ExtensionRegistryLite extensionRegistry, WireFormat.FieldType type, T value) throws IOException {
/*     */     MessageLite.Builder subBuilder;
/* 125 */     switch (type) {
/*     */       case MESSAGE:
/* 127 */         subBuilder = ((MessageLite)value).toBuilder();
/* 128 */         input.readMessage(subBuilder, extensionRegistry);
/* 129 */         return (T)subBuilder.buildPartial();
/*     */       case ENUM:
/* 131 */         return (T)Integer.valueOf(input.readEnum());
/*     */       case GROUP:
/* 133 */         throw new RuntimeException("Groups are not allowed in maps.");
/*     */     } 
/* 135 */     return (T)FieldSet.readPrimitiveField(input, type, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serializeTo(CodedOutputStream output, int fieldNumber, K key, V value) throws IOException {
/* 146 */     output.writeTag(fieldNumber, 2);
/* 147 */     output.writeUInt32NoTag(computeSerializedSize(this.metadata, key, value));
/* 148 */     writeTo(output, this.metadata, key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int computeMessageSize(int fieldNumber, K key, V value) {
/* 157 */     return CodedOutputStream.computeTagSize(fieldNumber) + 
/* 158 */       CodedOutputStream.computeLengthDelimitedFieldSize(
/* 159 */         computeSerializedSize(this.metadata, key, value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map.Entry<K, V> parseEntry(ByteString bytes, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 168 */     return parseEntry(bytes.newCodedInput(), this.metadata, extensionRegistry);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static <K, V> Map.Entry<K, V> parseEntry(CodedInputStream input, Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 174 */     K key = metadata.defaultKey;
/* 175 */     V value = metadata.defaultValue;
/*     */     while (true) {
/* 177 */       int tag = input.readTag();
/* 178 */       if (tag == 0) {
/*     */         break;
/*     */       }
/* 181 */       if (tag == WireFormat.makeTag(1, metadata.keyType.getWireType())) {
/* 182 */         key = parseField(input, extensionRegistry, metadata.keyType, key); continue;
/* 183 */       }  if (tag == WireFormat.makeTag(2, metadata.valueType.getWireType())) {
/* 184 */         value = parseField(input, extensionRegistry, metadata.valueType, value); continue;
/*     */       } 
/* 186 */       if (!input.skipField(tag)) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 191 */     return new AbstractMap.SimpleImmutableEntry<>(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parseInto(MapFieldLite<K, V> map, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 201 */     int length = input.readRawVarint32();
/* 202 */     int oldLimit = input.pushLimit(length);
/* 203 */     K key = this.metadata.defaultKey;
/* 204 */     V value = this.metadata.defaultValue;
/*     */     
/*     */     while (true) {
/* 207 */       int tag = input.readTag();
/* 208 */       if (tag == 0) {
/*     */         break;
/*     */       }
/* 211 */       if (tag == WireFormat.makeTag(1, this.metadata.keyType.getWireType())) {
/* 212 */         key = parseField(input, extensionRegistry, this.metadata.keyType, key); continue;
/* 213 */       }  if (tag == WireFormat.makeTag(2, this.metadata.valueType.getWireType())) {
/* 214 */         value = parseField(input, extensionRegistry, this.metadata.valueType, value); continue;
/*     */       } 
/* 216 */       if (!input.skipField(tag)) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 222 */     input.checkLastTagWas(0);
/* 223 */     input.popLimit(oldLimit);
/* 224 */     map.put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   Metadata<K, V> getMetadata() {
/* 229 */     return this.metadata;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MapEntryLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */