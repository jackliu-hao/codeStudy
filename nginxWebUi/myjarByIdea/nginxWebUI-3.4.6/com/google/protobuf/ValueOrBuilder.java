package com.google.protobuf;

public interface ValueOrBuilder extends MessageOrBuilder {
   int getNullValueValue();

   NullValue getNullValue();

   double getNumberValue();

   String getStringValue();

   ByteString getStringValueBytes();

   boolean getBoolValue();

   boolean hasStructValue();

   Struct getStructValue();

   StructOrBuilder getStructValueOrBuilder();

   boolean hasListValue();

   ListValue getListValue();

   ListValueOrBuilder getListValueOrBuilder();

   Value.KindCase getKindCase();
}
