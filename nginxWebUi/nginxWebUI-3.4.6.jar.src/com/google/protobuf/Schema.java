package com.google.protobuf;

import java.io.IOException;

interface Schema<T> {
  void writeTo(T paramT, Writer paramWriter) throws IOException;
  
  void mergeFrom(T paramT, Reader paramReader, ExtensionRegistryLite paramExtensionRegistryLite) throws IOException;
  
  void mergeFrom(T paramT, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ArrayDecoders.Registers paramRegisters) throws IOException;
  
  void makeImmutable(T paramT);
  
  boolean isInitialized(T paramT);
  
  T newInstance();
  
  boolean equals(T paramT1, T paramT2);
  
  int hashCode(T paramT);
  
  void mergeFrom(T paramT1, T paramT2);
  
  int getSerializedSize(T paramT);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Schema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */