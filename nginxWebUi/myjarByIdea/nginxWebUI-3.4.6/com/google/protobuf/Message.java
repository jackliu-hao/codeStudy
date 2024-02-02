package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;

public interface Message extends MessageLite, MessageOrBuilder {
   Parser<? extends Message> getParserForType();

   boolean equals(Object var1);

   int hashCode();

   String toString();

   Builder newBuilderForType();

   Builder toBuilder();

   public interface Builder extends MessageLite.Builder, MessageOrBuilder {
      Builder clear();

      Builder mergeFrom(Message var1);

      Message build();

      Message buildPartial();

      Builder clone();

      Builder mergeFrom(CodedInputStream var1) throws IOException;

      Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException;

      Descriptors.Descriptor getDescriptorForType();

      Builder newBuilderForField(Descriptors.FieldDescriptor var1);

      Builder getFieldBuilder(Descriptors.FieldDescriptor var1);

      Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor var1, int var2);

      Builder setField(Descriptors.FieldDescriptor var1, Object var2);

      Builder clearField(Descriptors.FieldDescriptor var1);

      Builder clearOneof(Descriptors.OneofDescriptor var1);

      Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3);

      Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2);

      Builder setUnknownFields(UnknownFieldSet var1);

      Builder mergeUnknownFields(UnknownFieldSet var1);

      Builder mergeFrom(ByteString var1) throws InvalidProtocolBufferException;

      Builder mergeFrom(ByteString var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException;

      Builder mergeFrom(byte[] var1) throws InvalidProtocolBufferException;

      Builder mergeFrom(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException;

      Builder mergeFrom(byte[] var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException;

      Builder mergeFrom(byte[] var1, int var2, int var3, ExtensionRegistryLite var4) throws InvalidProtocolBufferException;

      Builder mergeFrom(InputStream var1) throws IOException;

      Builder mergeFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException;

      boolean mergeDelimitedFrom(InputStream var1) throws IOException;

      boolean mergeDelimitedFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException;
   }
}
