package com.google.protobuf;

final class NewInstanceSchemaFull implements NewInstanceSchema {
   public Object newInstance(Object defaultInstance) {
      return ((GeneratedMessageV3)defaultInstance).newInstance(GeneratedMessageV3.UnusedPrivateParameter.INSTANCE);
   }
}
