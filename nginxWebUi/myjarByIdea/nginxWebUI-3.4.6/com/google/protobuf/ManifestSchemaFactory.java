package com.google.protobuf;

final class ManifestSchemaFactory implements SchemaFactory {
   private final MessageInfoFactory messageInfoFactory;
   private static final MessageInfoFactory EMPTY_FACTORY = new MessageInfoFactory() {
      public boolean isSupported(Class<?> clazz) {
         return false;
      }

      public MessageInfo messageInfoFor(Class<?> clazz) {
         throw new IllegalStateException("This should never be called.");
      }
   };

   public ManifestSchemaFactory() {
      this(getDefaultMessageInfoFactory());
   }

   private ManifestSchemaFactory(MessageInfoFactory messageInfoFactory) {
      this.messageInfoFactory = (MessageInfoFactory)Internal.checkNotNull(messageInfoFactory, "messageInfoFactory");
   }

   public <T> Schema<T> createSchema(Class<T> messageType) {
      SchemaUtil.requireGeneratedMessage(messageType);
      MessageInfo messageInfo = this.messageInfoFactory.messageInfoFor(messageType);
      if (messageInfo.isMessageSetWireFormat()) {
         return GeneratedMessageLite.class.isAssignableFrom(messageType) ? MessageSetSchema.newSchema(SchemaUtil.unknownFieldSetLiteSchema(), ExtensionSchemas.lite(), messageInfo.getDefaultInstance()) : MessageSetSchema.newSchema(SchemaUtil.proto2UnknownFieldSetSchema(), ExtensionSchemas.full(), messageInfo.getDefaultInstance());
      } else {
         return newSchema(messageType, messageInfo);
      }
   }

   private static <T> Schema<T> newSchema(Class<T> messageType, MessageInfo messageInfo) {
      if (GeneratedMessageLite.class.isAssignableFrom(messageType)) {
         return isProto2(messageInfo) ? MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.lite(), ListFieldSchema.lite(), SchemaUtil.unknownFieldSetLiteSchema(), ExtensionSchemas.lite(), MapFieldSchemas.lite()) : MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.lite(), ListFieldSchema.lite(), SchemaUtil.unknownFieldSetLiteSchema(), (ExtensionSchema)null, MapFieldSchemas.lite());
      } else {
         return isProto2(messageInfo) ? MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.full(), ListFieldSchema.full(), SchemaUtil.proto2UnknownFieldSetSchema(), ExtensionSchemas.full(), MapFieldSchemas.full()) : MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.full(), ListFieldSchema.full(), SchemaUtil.proto3UnknownFieldSetSchema(), (ExtensionSchema)null, MapFieldSchemas.full());
      }
   }

   private static boolean isProto2(MessageInfo messageInfo) {
      return messageInfo.getSyntax() == ProtoSyntax.PROTO2;
   }

   private static MessageInfoFactory getDefaultMessageInfoFactory() {
      return new CompositeMessageInfoFactory(new MessageInfoFactory[]{GeneratedMessageInfoFactory.getInstance(), getDescriptorMessageInfoFactory()});
   }

   private static MessageInfoFactory getDescriptorMessageInfoFactory() {
      try {
         Class<?> clazz = Class.forName("com.google.protobuf.DescriptorMessageInfoFactory");
         return (MessageInfoFactory)clazz.getDeclaredMethod("getInstance").invoke((Object)null);
      } catch (Exception var1) {
         return EMPTY_FACTORY;
      }
   }

   private static class CompositeMessageInfoFactory implements MessageInfoFactory {
      private MessageInfoFactory[] factories;

      CompositeMessageInfoFactory(MessageInfoFactory... factories) {
         this.factories = factories;
      }

      public boolean isSupported(Class<?> clazz) {
         MessageInfoFactory[] var2 = this.factories;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            MessageInfoFactory factory = var2[var4];
            if (factory.isSupported(clazz)) {
               return true;
            }
         }

         return false;
      }

      public MessageInfo messageInfoFor(Class<?> clazz) {
         MessageInfoFactory[] var2 = this.factories;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            MessageInfoFactory factory = var2[var4];
            if (factory.isSupported(clazz)) {
               return factory.messageInfoFor(clazz);
            }
         }

         throw new UnsupportedOperationException("No factory is available for message type: " + clazz.getName());
      }
   }
}
