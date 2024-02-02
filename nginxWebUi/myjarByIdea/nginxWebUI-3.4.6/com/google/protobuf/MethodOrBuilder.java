package com.google.protobuf;

import java.util.List;

public interface MethodOrBuilder extends MessageOrBuilder {
   String getName();

   ByteString getNameBytes();

   String getRequestTypeUrl();

   ByteString getRequestTypeUrlBytes();

   boolean getRequestStreaming();

   String getResponseTypeUrl();

   ByteString getResponseTypeUrlBytes();

   boolean getResponseStreaming();

   List<Option> getOptionsList();

   Option getOptions(int var1);

   int getOptionsCount();

   List<? extends OptionOrBuilder> getOptionsOrBuilderList();

   OptionOrBuilder getOptionsOrBuilder(int var1);

   int getSyntaxValue();

   Syntax getSyntax();
}
