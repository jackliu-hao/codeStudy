package com.google.protobuf;

import java.io.IOException;
import java.util.Map;

abstract class ExtensionSchema<T extends FieldSet.FieldDescriptorLite<T>> {
   abstract boolean hasExtensions(MessageLite var1);

   abstract FieldSet<T> getExtensions(Object var1);

   abstract void setExtensions(Object var1, FieldSet<T> var2);

   abstract FieldSet<T> getMutableExtensions(Object var1);

   abstract void makeImmutable(Object var1);

   abstract <UT, UB> UB parseExtension(Reader var1, Object var2, ExtensionRegistryLite var3, FieldSet<T> var4, UB var5, UnknownFieldSchema<UT, UB> var6) throws IOException;

   abstract int extensionNumber(Map.Entry<?, ?> var1);

   abstract void serializeExtension(Writer var1, Map.Entry<?, ?> var2) throws IOException;

   abstract Object findExtensionByNumber(ExtensionRegistryLite var1, MessageLite var2, int var3);

   abstract void parseLengthPrefixedMessageSetItem(Reader var1, Object var2, ExtensionRegistryLite var3, FieldSet<T> var4) throws IOException;

   abstract void parseMessageSetItem(ByteString var1, Object var2, ExtensionRegistryLite var3, FieldSet<T> var4) throws IOException;
}
