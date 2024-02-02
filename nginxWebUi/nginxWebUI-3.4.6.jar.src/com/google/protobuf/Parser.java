package com.google.protobuf;

import java.io.InputStream;
import java.nio.ByteBuffer;

public interface Parser<MessageType> {
  MessageType parseFrom(CodedInputStream paramCodedInputStream) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(CodedInputStream paramCodedInputStream, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(CodedInputStream paramCodedInputStream) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(CodedInputStream paramCodedInputStream, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(ByteBuffer paramByteBuffer) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(ByteBuffer paramByteBuffer, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(ByteString paramByteString) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(ByteString paramByteString, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(ByteString paramByteString) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(ByteString paramByteString, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(byte[] paramArrayOfbyte) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(byte[] paramArrayOfbyte, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(byte[] paramArrayOfbyte) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(byte[] paramArrayOfbyte, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(InputStream paramInputStream) throws InvalidProtocolBufferException;
  
  MessageType parseFrom(InputStream paramInputStream, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(InputStream paramInputStream) throws InvalidProtocolBufferException;
  
  MessageType parsePartialFrom(InputStream paramInputStream, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parseDelimitedFrom(InputStream paramInputStream) throws InvalidProtocolBufferException;
  
  MessageType parseDelimitedFrom(InputStream paramInputStream, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
  
  MessageType parsePartialDelimitedFrom(InputStream paramInputStream) throws InvalidProtocolBufferException;
  
  MessageType parsePartialDelimitedFrom(InputStream paramInputStream, ExtensionRegistryLite paramExtensionRegistryLite) throws InvalidProtocolBufferException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Parser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */