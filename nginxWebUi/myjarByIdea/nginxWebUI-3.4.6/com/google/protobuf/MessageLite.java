package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MessageLite extends MessageLiteOrBuilder {
   void writeTo(CodedOutputStream var1) throws IOException;

   int getSerializedSize();

   Parser<? extends MessageLite> getParserForType();

   ByteString toByteString();

   byte[] toByteArray();

   void writeTo(OutputStream var1) throws IOException;

   void writeDelimitedTo(OutputStream var1) throws IOException;

   Builder newBuilderForType();

   Builder toBuilder();

   public interface Builder extends MessageLiteOrBuilder, Cloneable {
      Builder clear();

      MessageLite build();

      MessageLite buildPartial();

      Builder clone();

      Builder mergeFrom(CodedInputStream var1) throws IOException;

      Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException;

      Builder mergeFrom(ByteString var1) throws InvalidProtocolBufferException;

      Builder mergeFrom(ByteString var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException;

      Builder mergeFrom(byte[] var1) throws InvalidProtocolBufferException;

      Builder mergeFrom(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException;

      Builder mergeFrom(byte[] var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException;

      Builder mergeFrom(byte[] var1, int var2, int var3, ExtensionRegistryLite var4) throws InvalidProtocolBufferException;

      Builder mergeFrom(InputStream var1) throws IOException;

      Builder mergeFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException;

      Builder mergeFrom(MessageLite var1);

      boolean mergeDelimitedFrom(InputStream var1) throws IOException;

      boolean mergeDelimitedFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException;
   }
}
