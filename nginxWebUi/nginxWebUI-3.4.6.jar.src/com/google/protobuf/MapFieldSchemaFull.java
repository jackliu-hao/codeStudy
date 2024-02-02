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
/*     */ class MapFieldSchemaFull
/*     */   implements MapFieldSchema
/*     */ {
/*     */   public Map<?, ?> forMutableMapData(Object mapField) {
/*  39 */     return ((MapField<?, ?>)mapField).getMutableMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<?, ?> forMapData(Object mapField) {
/*  44 */     return ((MapField<?, ?>)mapField).getMap();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isImmutable(Object mapField) {
/*  49 */     return !((MapField)mapField).isMutable();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object toImmutable(Object mapField) {
/*  54 */     ((MapField)mapField).makeImmutable();
/*  55 */     return mapField;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object newMapField(Object mapDefaultEntry) {
/*  60 */     return MapField.newMapField((MapEntry<?, ?>)mapDefaultEntry);
/*     */   }
/*     */ 
/*     */   
/*     */   public MapEntryLite.Metadata<?, ?> forMapMetadata(Object mapDefaultEntry) {
/*  65 */     return ((MapEntry<?, ?>)mapDefaultEntry).getMetadata();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object mergeFrom(Object destMapField, Object srcMapField) {
/*  70 */     return mergeFromFull(destMapField, srcMapField);
/*     */   }
/*     */ 
/*     */   
/*     */   private static <K, V> Object mergeFromFull(Object destMapField, Object srcMapField) {
/*  75 */     MapField<K, V> mine = (MapField<K, V>)destMapField;
/*  76 */     MapField<K, V> other = (MapField<K, V>)srcMapField;
/*  77 */     if (!mine.isMutable()) {
/*  78 */       mine.copy();
/*     */     }
/*  80 */     mine.mergeFrom(other);
/*  81 */     return mine;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize(int number, Object mapField, Object mapDefaultEntry) {
/*  86 */     return getSerializedSizeFull(number, mapField, mapDefaultEntry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <K, V> int getSerializedSizeFull(int number, Object mapField, Object defaultEntryObject) {
/*  93 */     if (mapField == null) {
/*  94 */       return 0;
/*     */     }
/*     */     
/*  97 */     Map<K, V> map = ((MapField<K, V>)mapField).getMap();
/*  98 */     MapEntry<K, V> defaultEntry = (MapEntry<K, V>)defaultEntryObject;
/*  99 */     if (map.isEmpty()) {
/* 100 */       return 0;
/*     */     }
/* 102 */     int size = 0;
/* 103 */     for (Map.Entry<K, V> entry : map.entrySet()) {
/* 104 */       size += 
/* 105 */         CodedOutputStream.computeTagSize(number) + 
/* 106 */         CodedOutputStream.computeLengthDelimitedFieldSize(
/* 107 */           MapEntryLite.computeSerializedSize(defaultEntry
/* 108 */             .getMetadata(), entry.getKey(), entry.getValue()));
/*     */     }
/* 110 */     return size;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MapFieldSchemaFull.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */