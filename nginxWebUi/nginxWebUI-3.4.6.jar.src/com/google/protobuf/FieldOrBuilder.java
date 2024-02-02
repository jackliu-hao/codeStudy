package com.google.protobuf;

import java.util.List;

public interface FieldOrBuilder extends MessageOrBuilder {
  int getKindValue();
  
  Field.Kind getKind();
  
  int getCardinalityValue();
  
  Field.Cardinality getCardinality();
  
  int getNumber();
  
  String getName();
  
  ByteString getNameBytes();
  
  String getTypeUrl();
  
  ByteString getTypeUrlBytes();
  
  int getOneofIndex();
  
  boolean getPacked();
  
  List<Option> getOptionsList();
  
  Option getOptions(int paramInt);
  
  int getOptionsCount();
  
  List<? extends OptionOrBuilder> getOptionsOrBuilderList();
  
  OptionOrBuilder getOptionsOrBuilder(int paramInt);
  
  String getJsonName();
  
  ByteString getJsonNameBytes();
  
  String getDefaultValue();
  
  ByteString getDefaultValueBytes();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\FieldOrBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */