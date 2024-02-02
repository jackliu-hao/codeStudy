package com.google.protobuf;

import java.util.Map;

interface MapFieldSchema {
  Map<?, ?> forMutableMapData(Object paramObject);
  
  Map<?, ?> forMapData(Object paramObject);
  
  boolean isImmutable(Object paramObject);
  
  Object toImmutable(Object paramObject);
  
  Object newMapField(Object paramObject);
  
  MapEntryLite.Metadata<?, ?> forMapMetadata(Object paramObject);
  
  Object mergeFrom(Object paramObject1, Object paramObject2);
  
  int getSerializedSize(int paramInt, Object paramObject1, Object paramObject2);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MapFieldSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */