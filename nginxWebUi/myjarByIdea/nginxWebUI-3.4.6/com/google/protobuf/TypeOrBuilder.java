package com.google.protobuf;

import java.util.List;

public interface TypeOrBuilder extends MessageOrBuilder {
   String getName();

   ByteString getNameBytes();

   List<Field> getFieldsList();

   Field getFields(int var1);

   int getFieldsCount();

   List<? extends FieldOrBuilder> getFieldsOrBuilderList();

   FieldOrBuilder getFieldsOrBuilder(int var1);

   List<String> getOneofsList();

   int getOneofsCount();

   String getOneofs(int var1);

   ByteString getOneofsBytes(int var1);

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
