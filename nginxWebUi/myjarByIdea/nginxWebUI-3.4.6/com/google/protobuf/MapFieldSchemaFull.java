package com.google.protobuf;

import java.util.Iterator;
import java.util.Map;

class MapFieldSchemaFull implements MapFieldSchema {
   public Map<?, ?> forMutableMapData(Object mapField) {
      return ((MapField)mapField).getMutableMap();
   }

   public Map<?, ?> forMapData(Object mapField) {
      return ((MapField)mapField).getMap();
   }

   public boolean isImmutable(Object mapField) {
      return !((MapField)mapField).isMutable();
   }

   public Object toImmutable(Object mapField) {
      ((MapField)mapField).makeImmutable();
      return mapField;
   }

   public Object newMapField(Object mapDefaultEntry) {
      return MapField.newMapField((MapEntry)mapDefaultEntry);
   }

   public MapEntryLite.Metadata<?, ?> forMapMetadata(Object mapDefaultEntry) {
      return ((MapEntry)mapDefaultEntry).getMetadata();
   }

   public Object mergeFrom(Object destMapField, Object srcMapField) {
      return mergeFromFull(destMapField, srcMapField);
   }

   private static <K, V> Object mergeFromFull(Object destMapField, Object srcMapField) {
      MapField<K, V> mine = (MapField)destMapField;
      MapField<K, V> other = (MapField)srcMapField;
      if (!mine.isMutable()) {
         mine.copy();
      }

      mine.mergeFrom(other);
      return mine;
   }

   public int getSerializedSize(int number, Object mapField, Object mapDefaultEntry) {
      return getSerializedSizeFull(number, mapField, mapDefaultEntry);
   }

   private static <K, V> int getSerializedSizeFull(int number, Object mapField, Object defaultEntryObject) {
      if (mapField == null) {
         return 0;
      } else {
         Map<K, V> map = ((MapField)mapField).getMap();
         MapEntry<K, V> defaultEntry = (MapEntry)defaultEntryObject;
         if (map.isEmpty()) {
            return 0;
         } else {
            int size = 0;

            Map.Entry entry;
            for(Iterator var6 = map.entrySet().iterator(); var6.hasNext(); size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeLengthDelimitedFieldSize(MapEntryLite.computeSerializedSize(defaultEntry.getMetadata(), entry.getKey(), entry.getValue()))) {
               entry = (Map.Entry)var6.next();
            }

            return size;
         }
      }
   }
}
