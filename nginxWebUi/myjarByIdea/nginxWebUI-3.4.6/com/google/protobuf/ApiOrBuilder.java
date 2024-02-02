package com.google.protobuf;

import java.util.List;

public interface ApiOrBuilder extends MessageOrBuilder {
   String getName();

   ByteString getNameBytes();

   List<Method> getMethodsList();

   Method getMethods(int var1);

   int getMethodsCount();

   List<? extends MethodOrBuilder> getMethodsOrBuilderList();

   MethodOrBuilder getMethodsOrBuilder(int var1);

   List<Option> getOptionsList();

   Option getOptions(int var1);

   int getOptionsCount();

   List<? extends OptionOrBuilder> getOptionsOrBuilderList();

   OptionOrBuilder getOptionsOrBuilder(int var1);

   String getVersion();

   ByteString getVersionBytes();

   boolean hasSourceContext();

   SourceContext getSourceContext();

   SourceContextOrBuilder getSourceContextOrBuilder();

   List<Mixin> getMixinsList();

   Mixin getMixins(int var1);

   int getMixinsCount();

   List<? extends MixinOrBuilder> getMixinsOrBuilderList();

   MixinOrBuilder getMixinsOrBuilder(int var1);

   int getSyntaxValue();

   Syntax getSyntax();
}
