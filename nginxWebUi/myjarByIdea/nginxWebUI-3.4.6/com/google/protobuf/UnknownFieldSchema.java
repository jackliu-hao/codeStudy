package com.google.protobuf;

import java.io.IOException;

abstract class UnknownFieldSchema<T, B> {
   abstract boolean shouldDiscardUnknownFields(Reader var1);

   abstract void addVarint(B var1, int var2, long var3);

   abstract void addFixed32(B var1, int var2, int var3);

   abstract void addFixed64(B var1, int var2, long var3);

   abstract void addLengthDelimited(B var1, int var2, ByteString var3);

   abstract void addGroup(B var1, int var2, T var3);

   abstract B newBuilder();

   abstract T toImmutable(B var1);

   abstract void setToMessage(Object var1, T var2);

   abstract T getFromMessage(Object var1);

   abstract B getBuilderFromMessage(Object var1);

   abstract void setBuilderToMessage(Object var1, B var2);

   abstract void makeImmutable(Object var1);

   final boolean mergeOneFieldFrom(B var1, Reader var2) throws IOException {
      // $FF: Couldn't be decompiled
   }

   final void mergeFrom(B unknownFields, Reader reader) throws IOException {
      while(reader.getFieldNumber() != Integer.MAX_VALUE && this.mergeOneFieldFrom(unknownFields, reader)) {
      }

   }

   abstract void writeTo(T var1, Writer var2) throws IOException;

   abstract void writeAsMessageSetTo(T var1, Writer var2) throws IOException;

   abstract T merge(T var1, T var2);

   abstract int getSerializedSizeAsMessageSet(T var1);

   abstract int getSerializedSize(T var1);
}
