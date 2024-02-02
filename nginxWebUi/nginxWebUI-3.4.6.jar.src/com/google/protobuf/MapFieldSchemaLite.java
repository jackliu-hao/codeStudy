/*     */ package com.google.protobuf;
/*     */ 
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
/*     */ class MapFieldSchemaLite
/*     */   implements MapFieldSchema
/*     */ {
/*     */   public Map<?, ?> forMutableMapData(Object mapField) {
/*  40 */     return (MapFieldLite)mapField;
/*     */   }
/*     */ 
/*     */   
/*     */   public MapEntryLite.Metadata<?, ?> forMapMetadata(Object mapDefaultEntry) {
/*  45 */     return ((MapEntryLite<?, ?>)mapDefaultEntry).getMetadata();
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<?, ?> forMapData(Object mapField) {
/*  50 */     return (MapFieldLite)mapField;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isImmutable(Object mapField) {
/*  55 */     return !((MapFieldLite)mapField).isMutable();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object toImmutable(Object mapField) {
/*  60 */     ((MapFieldLite)mapField).makeImmutable();
/*  61 */     return mapField;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object newMapField(Object unused) {
/*  66 */     return MapFieldLite.emptyMapField().mutableCopy();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object mergeFrom(Object destMapField, Object srcMapField) {
/*  71 */     return mergeFromLite(destMapField, srcMapField);
/*     */   }
/*     */ 
/*     */   
/*     */   private static <K, V> MapFieldLite<K, V> mergeFromLite(Object destMapField, Object srcMapField) {
/*  76 */     MapFieldLite<K, V> mine = (MapFieldLite<K, V>)destMapField;
/*  77 */     MapFieldLite<K, V> other = (MapFieldLite<K, V>)srcMapField;
/*  78 */     if (!other.isEmpty()) {
/*  79 */       if (!mine.isMutable()) {
/*  80 */         mine = mine.mutableCopy();
/*     */       }
/*  82 */       mine.mergeFrom(other);
/*     */     } 
/*  84 */     return mine;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize(int fieldNumber, Object mapField, Object mapDefaultEntry) {
/*  89 */     return getSerializedSizeLite(fieldNumber, mapField, mapDefaultEntry);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static <K, V> int getSerializedSizeLite(int fieldNumber, Object mapField, Object defaultEntry) {
/*  95 */     MapFieldLite<K, V> mapFieldLite = (MapFieldLite<K, V>)mapField;
/*  96 */     MapEntryLite<K, V> defaultEntryLite = (MapEntryLite<K, V>)defaultEntry;
/*     */     
/*  98 */     if (mapFieldLite.isEmpty()) {
/*  99 */       return 0;
/*     */     }
/* 101 */     int size = 0;
/* 102 */     for (Map.Entry<K, V> entry : mapFieldLite.entrySet()) {
/* 103 */       size += defaultEntryLite.computeMessageSize(fieldNumber, entry.getKey(), entry.getValue());
/*     */     }
/* 105 */     return size;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MapFieldSchemaLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */