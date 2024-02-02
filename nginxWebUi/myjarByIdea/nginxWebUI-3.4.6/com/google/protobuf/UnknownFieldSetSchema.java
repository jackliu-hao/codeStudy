package com.google.protobuf;

import java.io.IOException;

class UnknownFieldSetSchema extends UnknownFieldSchema<UnknownFieldSet, UnknownFieldSet.Builder> {
   private final boolean proto3;

   public UnknownFieldSetSchema(boolean proto3) {
      this.proto3 = proto3;
   }

   boolean shouldDiscardUnknownFields(Reader reader) {
      return reader.shouldDiscardUnknownFields();
   }

   UnknownFieldSet.Builder newBuilder() {
      return UnknownFieldSet.newBuilder();
   }

   void addVarint(UnknownFieldSet.Builder fields, int number, long value) {
      fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addVarint(value).build());
   }

   void addFixed32(UnknownFieldSet.Builder fields, int number, int value) {
      fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addFixed32(value).build());
   }

   void addFixed64(UnknownFieldSet.Builder fields, int number, long value) {
      fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addFixed64(value).build());
   }

   void addLengthDelimited(UnknownFieldSet.Builder fields, int number, ByteString value) {
      fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addLengthDelimited(value).build());
   }

   void addGroup(UnknownFieldSet.Builder fields, int number, UnknownFieldSet subFieldSet) {
      fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addGroup(subFieldSet).build());
   }

   UnknownFieldSet toImmutable(UnknownFieldSet.Builder fields) {
      return fields.build();
   }

   void writeTo(UnknownFieldSet message, Writer writer) throws IOException {
      message.writeTo(writer);
   }

   void writeAsMessageSetTo(UnknownFieldSet message, Writer writer) throws IOException {
      message.writeAsMessageSetTo(writer);
   }

   UnknownFieldSet getFromMessage(Object message) {
      return ((GeneratedMessageV3)message).unknownFields;
   }

   void setToMessage(Object message, UnknownFieldSet fields) {
      ((GeneratedMessageV3)message).unknownFields = fields;
   }

   UnknownFieldSet.Builder getBuilderFromMessage(Object message) {
      return ((GeneratedMessageV3)message).unknownFields.toBuilder();
   }

   void setBuilderToMessage(Object message, UnknownFieldSet.Builder builder) {
      ((GeneratedMessageV3)message).unknownFields = builder.build();
   }

   void makeImmutable(Object message) {
   }

   UnknownFieldSet merge(UnknownFieldSet message, UnknownFieldSet other) {
      return message.toBuilder().mergeFrom(other).build();
   }

   int getSerializedSize(UnknownFieldSet message) {
      return message.getSerializedSize();
   }

   int getSerializedSizeAsMessageSet(UnknownFieldSet unknowns) {
      return unknowns.getSerializedSizeAsMessageSet();
   }
}
