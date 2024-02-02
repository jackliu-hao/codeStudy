package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;

public interface Message extends MessageLite, MessageOrBuilder {
  Parser<? extends Message> getParserForType();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
  
  Builder newBuilderForType();
  
  Builder toBuilder();
  
  public static interface Builder extends MessageLite.Builder, MessageOrBuilder {
    Builder clear();
    
    Builder mergeFrom(Message param1Message);
    
    Message build();
    
    Message buildPartial();
    
    Builder clone();
    
    Builder mergeFrom(CodedInputStream param1CodedInputStream) throws IOException;
    
    Builder mergeFrom(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite) throws IOException;
    
    Descriptors.Descriptor getDescriptorForType();
    
    Builder newBuilderForField(Descriptors.FieldDescriptor param1FieldDescriptor);
    
    Builder getFieldBuilder(Descriptors.FieldDescriptor param1FieldDescriptor);
    
    Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor param1FieldDescriptor, int param1Int);
    
    Builder setField(Descriptors.FieldDescriptor param1FieldDescriptor, Object param1Object);
    
    Builder clearField(Descriptors.FieldDescriptor param1FieldDescriptor);
    
    Builder clearOneof(Descriptors.OneofDescriptor param1OneofDescriptor);
    
    Builder setRepeatedField(Descriptors.FieldDescriptor param1FieldDescriptor, int param1Int, Object param1Object);
    
    Builder addRepeatedField(Descriptors.FieldDescriptor param1FieldDescriptor, Object param1Object);
    
    Builder setUnknownFields(UnknownFieldSet param1UnknownFieldSet);
    
    Builder mergeUnknownFields(UnknownFieldSet param1UnknownFieldSet);
    
    Builder mergeFrom(ByteString param1ByteString) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(ByteString param1ByteString, ExtensionRegistryLite param1ExtensionRegistryLite) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(byte[] param1ArrayOfbyte) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(byte[] param1ArrayOfbyte, ExtensionRegistryLite param1ExtensionRegistryLite) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2, ExtensionRegistryLite param1ExtensionRegistryLite) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(InputStream param1InputStream) throws IOException;
    
    Builder mergeFrom(InputStream param1InputStream, ExtensionRegistryLite param1ExtensionRegistryLite) throws IOException;
    
    boolean mergeDelimitedFrom(InputStream param1InputStream) throws IOException;
    
    boolean mergeDelimitedFrom(InputStream param1InputStream, ExtensionRegistryLite param1ExtensionRegistryLite) throws IOException;
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Message.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */