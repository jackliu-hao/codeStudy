package com.google.protobuf;

import java.util.List;

public interface EnumOrBuilder extends MessageOrBuilder {
  String getName();
  
  ByteString getNameBytes();
  
  List<EnumValue> getEnumvalueList();
  
  EnumValue getEnumvalue(int paramInt);
  
  int getEnumvalueCount();
  
  List<? extends EnumValueOrBuilder> getEnumvalueOrBuilderList();
  
  EnumValueOrBuilder getEnumvalueOrBuilder(int paramInt);
  
  List<Option> getOptionsList();
  
  Option getOptions(int paramInt);
  
  int getOptionsCount();
  
  List<? extends OptionOrBuilder> getOptionsOrBuilderList();
  
  OptionOrBuilder getOptionsOrBuilder(int paramInt);
  
  boolean hasSourceContext();
  
  SourceContext getSourceContext();
  
  SourceContextOrBuilder getSourceContextOrBuilder();
  
  int getSyntaxValue();
  
  Syntax getSyntax();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\EnumOrBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */