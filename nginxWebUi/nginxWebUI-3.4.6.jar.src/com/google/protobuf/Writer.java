/*    */ package com.google.protobuf;interface Writer { FieldOrder fieldOrder(); void writeSFixed32(int paramInt1, int paramInt2) throws IOException; void writeInt64(int paramInt, long paramLong) throws IOException; void writeSFixed64(int paramInt, long paramLong) throws IOException; void writeFloat(int paramInt, float paramFloat) throws IOException; void writeDouble(int paramInt, double paramDouble) throws IOException; void writeEnum(int paramInt1, int paramInt2) throws IOException; void writeUInt64(int paramInt, long paramLong) throws IOException; void writeInt32(int paramInt1, int paramInt2) throws IOException;
/*    */   void writeFixed64(int paramInt, long paramLong) throws IOException;
/*    */   void writeFixed32(int paramInt1, int paramInt2) throws IOException;
/*    */   void writeBool(int paramInt, boolean paramBoolean) throws IOException;
/*    */   void writeString(int paramInt, String paramString) throws IOException;
/*    */   void writeBytes(int paramInt, ByteString paramByteString) throws IOException;
/*    */   void writeUInt32(int paramInt1, int paramInt2) throws IOException;
/*    */   void writeSInt32(int paramInt1, int paramInt2) throws IOException;
/*    */   void writeSInt64(int paramInt, long paramLong) throws IOException;
/*    */   void writeMessage(int paramInt, Object paramObject) throws IOException;
/*    */   void writeMessage(int paramInt, Object paramObject, Schema paramSchema) throws IOException;
/*    */   @Deprecated
/*    */   void writeGroup(int paramInt, Object paramObject) throws IOException;
/*    */   @Deprecated
/*    */   void writeGroup(int paramInt, Object paramObject, Schema paramSchema) throws IOException;
/*    */   @Deprecated
/*    */   void writeStartGroup(int paramInt) throws IOException;
/*    */   @Deprecated
/*    */   void writeEndGroup(int paramInt) throws IOException;
/*    */   void writeInt32List(int paramInt, List<Integer> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeFixed32List(int paramInt, List<Integer> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeInt64List(int paramInt, List<Long> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeUInt64List(int paramInt, List<Long> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeFixed64List(int paramInt, List<Long> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeFloatList(int paramInt, List<Float> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeDoubleList(int paramInt, List<Double> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeEnumList(int paramInt, List<Integer> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeBoolList(int paramInt, List<Boolean> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeStringList(int paramInt, List<String> paramList) throws IOException;
/*    */   void writeBytesList(int paramInt, List<ByteString> paramList) throws IOException;
/*    */   void writeUInt32List(int paramInt, List<Integer> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeSFixed32List(int paramInt, List<Integer> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeSFixed64List(int paramInt, List<Long> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeSInt32List(int paramInt, List<Integer> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeSInt64List(int paramInt, List<Long> paramList, boolean paramBoolean) throws IOException;
/*    */   void writeMessageList(int paramInt, List<?> paramList) throws IOException;
/*    */   void writeMessageList(int paramInt, List<?> paramList, Schema paramSchema) throws IOException;
/*    */   @Deprecated
/*    */   void writeGroupList(int paramInt, List<?> paramList) throws IOException;
/*    */   @Deprecated
/*    */   void writeGroupList(int paramInt, List<?> paramList, Schema paramSchema) throws IOException;
/*    */   void writeMessageSetItem(int paramInt, Object paramObject) throws IOException;
/*    */   <K, V> void writeMap(int paramInt, MapEntryLite.Metadata<K, V> paramMetadata, Map<K, V> paramMap) throws IOException;
/* 44 */   public enum FieldOrder { ASCENDING,
/*    */ 
/*    */     
/* 47 */     DESCENDING; }
/*    */    }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Writer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */