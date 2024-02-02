package com.google.protobuf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

interface Reader {
  public static final int READ_DONE = 2147483647;
  
  public static final int TAG_UNKNOWN = 0;
  
  boolean shouldDiscardUnknownFields();
  
  int getFieldNumber() throws IOException;
  
  int getTag();
  
  boolean skipField() throws IOException;
  
  double readDouble() throws IOException;
  
  float readFloat() throws IOException;
  
  long readUInt64() throws IOException;
  
  long readInt64() throws IOException;
  
  int readInt32() throws IOException;
  
  long readFixed64() throws IOException;
  
  int readFixed32() throws IOException;
  
  boolean readBool() throws IOException;
  
  String readString() throws IOException;
  
  String readStringRequireUtf8() throws IOException;
  
  <T> T readMessageBySchemaWithCheck(Schema<T> paramSchema, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  <T> T readMessage(Class<T> paramClass, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  @Deprecated
  <T> T readGroup(Class<T> paramClass, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  @Deprecated
  <T> T readGroupBySchemaWithCheck(Schema<T> paramSchema, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  ByteString readBytes() throws IOException;
  
  int readUInt32() throws IOException;
  
  int readEnum() throws IOException;
  
  int readSFixed32() throws IOException;
  
  long readSFixed64() throws IOException;
  
  int readSInt32() throws IOException;
  
  long readSInt64() throws IOException;
  
  void readDoubleList(List<Double> paramList) throws IOException;
  
  void readFloatList(List<Float> paramList) throws IOException;
  
  void readUInt64List(List<Long> paramList) throws IOException;
  
  void readInt64List(List<Long> paramList) throws IOException;
  
  void readInt32List(List<Integer> paramList) throws IOException;
  
  void readFixed64List(List<Long> paramList) throws IOException;
  
  void readFixed32List(List<Integer> paramList) throws IOException;
  
  void readBoolList(List<Boolean> paramList) throws IOException;
  
  void readStringList(List<String> paramList) throws IOException;
  
  void readStringListRequireUtf8(List<String> paramList) throws IOException;
  
  <T> void readMessageList(List<T> paramList, Schema<T> paramSchema, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  <T> void readMessageList(List<T> paramList, Class<T> paramClass, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  @Deprecated
  <T> void readGroupList(List<T> paramList, Class<T> paramClass, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  @Deprecated
  <T> void readGroupList(List<T> paramList, Schema<T> paramSchema, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  void readBytesList(List<ByteString> paramList) throws IOException;
  
  void readUInt32List(List<Integer> paramList) throws IOException;
  
  void readEnumList(List<Integer> paramList) throws IOException;
  
  void readSFixed32List(List<Integer> paramList) throws IOException;
  
  void readSFixed64List(List<Long> paramList) throws IOException;
  
  void readSInt32List(List<Integer> paramList) throws IOException;
  
  void readSInt64List(List<Long> paramList) throws IOException;
  
  <K, V> void readMap(Map<K, V> paramMap, MapEntryLite.Metadata<K, V> paramMetadata, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Reader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */