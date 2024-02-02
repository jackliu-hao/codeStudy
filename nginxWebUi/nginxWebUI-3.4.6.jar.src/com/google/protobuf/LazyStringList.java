package com.google.protobuf;

import java.util.Collection;
import java.util.List;

public interface LazyStringList extends ProtocolStringList {
  ByteString getByteString(int paramInt);
  
  Object getRaw(int paramInt);
  
  byte[] getByteArray(int paramInt);
  
  void add(ByteString paramByteString);
  
  void add(byte[] paramArrayOfbyte);
  
  void set(int paramInt, ByteString paramByteString);
  
  void set(int paramInt, byte[] paramArrayOfbyte);
  
  boolean addAllByteString(Collection<? extends ByteString> paramCollection);
  
  boolean addAllByteArray(Collection<byte[]> paramCollection);
  
  List<?> getUnderlyingElements();
  
  void mergeFrom(LazyStringList paramLazyStringList);
  
  List<byte[]> asByteArrayList();
  
  LazyStringList getUnmodifiableView();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\LazyStringList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */