package com.google.protobuf;

import java.util.List;

public interface TypeOrBuilder extends MessageOrBuilder {
  String getName();
  
  ByteString getNameBytes();
  
  List<Field> getFieldsList();
  
  Field getFields(int paramInt);
  
  int getFieldsCount();
  
  List<? extends FieldOrBuilder> getFieldsOrBuilderList();
  
  FieldOrBuilder getFieldsOrBuilder(int paramInt);
  
  List<String> getOneofsList();
  
  int getOneofsCount();
  
  String getOneofs(int paramInt);
  
  ByteString getOneofsBytes(int paramInt);
  
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\TypeOrBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */