package com.google.protobuf;

public abstract class Extension<ContainingType extends MessageLite, Type> extends ExtensionLite<ContainingType, Type> {
   public abstract Message getMessageDefaultInstance();

   public abstract Descriptors.FieldDescriptor getDescriptor();

   final boolean isLite() {
      return false;
   }

   protected abstract ExtensionType getExtensionType();

   public MessageType getMessageType() {
      return Extension.MessageType.PROTO2;
   }

   protected abstract Object fromReflectionType(Object var1);

   protected abstract Object singularFromReflectionType(Object var1);

   protected abstract Object toReflectionType(Object var1);

   protected abstract Object singularToReflectionType(Object var1);

   public static enum MessageType {
      PROTO1,
      PROTO2;
   }

   protected static enum ExtensionType {
      IMMUTABLE,
      MUTABLE,
      PROTO1;
   }
}
