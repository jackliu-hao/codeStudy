package com.google.protobuf;

interface MessageInfoFactory {
   boolean isSupported(Class<?> var1);

   MessageInfo messageInfoFor(Class<?> var1);
}
