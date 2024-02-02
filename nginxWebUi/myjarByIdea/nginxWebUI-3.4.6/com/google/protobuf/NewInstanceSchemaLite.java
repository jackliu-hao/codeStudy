package com.google.protobuf;

final class NewInstanceSchemaLite implements NewInstanceSchema {
   public Object newInstance(Object defaultInstance) {
      return ((GeneratedMessageLite)defaultInstance).dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE);
   }
}
