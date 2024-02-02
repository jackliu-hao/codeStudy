package com.google.protobuf;

import java.util.List;

public interface EnumOrBuilder extends MessageOrBuilder {
   String getName();

   ByteString getNameBytes();

   List<EnumValue> getEnumvalueList();

   EnumValue getEnumvalue(int var1);

   int getEnumvalueCount();

   List<? extends EnumValueOrBuilder> getEnumvalueOrBuilderList();

   EnumValueOrBuilder getEnumvalueOrBuilder(int var1);

   List<Option> getOptionsList();

   Option getOptions(int var1);

   int getOptionsCount();

   List<? extends OptionOrBuilder> getOptionsOrBuilderList();

   OptionOrBuilder getOptionsOrBuilder(int var1);

   boolean hasSourceContext();

   SourceContext getSourceContext();

   SourceContextOrBuilder getSourceContextOrBuilder();

   int getSyntaxValue();

   Syntax getSyntax();
}
