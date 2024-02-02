package com.google.protobuf;

public interface OptionOrBuilder extends MessageOrBuilder {
  String getName();
  
  ByteString getNameBytes();
  
  boolean hasValue();
  
  Any getValue();
  
  AnyOrBuilder getValueOrBuilder();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\OptionOrBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */