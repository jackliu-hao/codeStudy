package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface MessageLite extends MessageLiteOrBuilder {
  void writeTo(CodedOutputStream paramCodedOutputStream) throws IOException;
  
  int getSerializedSize();
  
  Parser<? extends MessageLite> getParserForType();
  
  ByteString toByteString();
  
  byte[] toByteArray();
  
  void writeTo(OutputStream paramOutputStream) throws IOException;
  
  void writeDelimitedTo(OutputStream paramOutputStream) throws IOException;
  
  Builder newBuilderForType();
  
  Builder toBuilder();
  
  public static interface Builder extends MessageLiteOrBuilder, Cloneable {
    Builder clear();
    
    MessageLite build();
    
    MessageLite buildPartial();
    
    Builder clone();
    
    Builder mergeFrom(CodedInputStream param1CodedInputStream) throws IOException;
    
    Builder mergeFrom(CodedInputStream param1CodedInputStream, ExtensionRegistryLite param1ExtensionRegistryLite) throws IOException;
    
    Builder mergeFrom(ByteString param1ByteString) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(ByteString param1ByteString, ExtensionRegistryLite param1ExtensionRegistryLite) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(byte[] param1ArrayOfbyte) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(byte[] param1ArrayOfbyte, ExtensionRegistryLite param1ExtensionRegistryLite) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2, ExtensionRegistryLite param1ExtensionRegistryLite) throws InvalidProtocolBufferException;
    
    Builder mergeFrom(InputStream param1InputStream) throws IOException;
    
    Builder mergeFrom(InputStream param1InputStream, ExtensionRegistryLite param1ExtensionRegistryLite) throws IOException;
    
    Builder mergeFrom(MessageLite param1MessageLite);
    
    boolean mergeDelimitedFrom(InputStream param1InputStream) throws IOException;
    
    boolean mergeDelimitedFrom(InputStream param1InputStream, ExtensionRegistryLite param1ExtensionRegistryLite) throws IOException;
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MessageLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */