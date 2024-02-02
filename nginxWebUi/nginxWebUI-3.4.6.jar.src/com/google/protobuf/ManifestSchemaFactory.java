/*     */ package com.google.protobuf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class ManifestSchemaFactory
/*     */   implements SchemaFactory
/*     */ {
/*     */   private final MessageInfoFactory messageInfoFactory;
/*     */   
/*     */   public ManifestSchemaFactory() {
/*  44 */     this(getDefaultMessageInfoFactory());
/*     */   }
/*     */   
/*     */   private ManifestSchemaFactory(MessageInfoFactory messageInfoFactory) {
/*  48 */     this.messageInfoFactory = Internal.<MessageInfoFactory>checkNotNull(messageInfoFactory, "messageInfoFactory");
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> Schema<T> createSchema(Class<T> messageType) {
/*  53 */     SchemaUtil.requireGeneratedMessage(messageType);
/*     */     
/*  55 */     MessageInfo messageInfo = this.messageInfoFactory.messageInfoFor(messageType);
/*     */ 
/*     */     
/*  58 */     if (messageInfo.isMessageSetWireFormat()) {
/*  59 */       if (GeneratedMessageLite.class.isAssignableFrom(messageType)) {
/*  60 */         return MessageSetSchema.newSchema(
/*  61 */             SchemaUtil.unknownFieldSetLiteSchema(), 
/*  62 */             ExtensionSchemas.lite(), messageInfo
/*  63 */             .getDefaultInstance());
/*     */       }
/*  65 */       return MessageSetSchema.newSchema(
/*  66 */           SchemaUtil.proto2UnknownFieldSetSchema(), 
/*  67 */           ExtensionSchemas.full(), messageInfo
/*  68 */           .getDefaultInstance());
/*     */     } 
/*     */     
/*  71 */     return newSchema(messageType, messageInfo);
/*     */   }
/*     */   
/*     */   private static <T> Schema<T> newSchema(Class<T> messageType, MessageInfo messageInfo) {
/*  75 */     if (GeneratedMessageLite.class.isAssignableFrom(messageType)) {
/*  76 */       return isProto2(messageInfo) ? 
/*  77 */         MessageSchema.<T>newSchema(messageType, messageInfo, 
/*     */ 
/*     */           
/*  80 */           NewInstanceSchemas.lite(), 
/*  81 */           ListFieldSchema.lite(), 
/*  82 */           SchemaUtil.unknownFieldSetLiteSchema(), 
/*  83 */           ExtensionSchemas.lite(), 
/*  84 */           MapFieldSchemas.lite()) : 
/*  85 */         MessageSchema.<T>newSchema(messageType, messageInfo, 
/*     */ 
/*     */           
/*  88 */           NewInstanceSchemas.lite(), 
/*  89 */           ListFieldSchema.lite(), 
/*  90 */           SchemaUtil.unknownFieldSetLiteSchema(), null, 
/*     */           
/*  92 */           MapFieldSchemas.lite());
/*     */     }
/*  94 */     return isProto2(messageInfo) ? 
/*  95 */       MessageSchema.<T>newSchema(messageType, messageInfo, 
/*     */ 
/*     */         
/*  98 */         NewInstanceSchemas.full(), 
/*  99 */         ListFieldSchema.full(), 
/* 100 */         SchemaUtil.proto2UnknownFieldSetSchema(), 
/* 101 */         ExtensionSchemas.full(), 
/* 102 */         MapFieldSchemas.full()) : 
/* 103 */       MessageSchema.<T>newSchema(messageType, messageInfo, 
/*     */ 
/*     */         
/* 106 */         NewInstanceSchemas.full(), 
/* 107 */         ListFieldSchema.full(), 
/* 108 */         SchemaUtil.proto3UnknownFieldSetSchema(), null, 
/*     */         
/* 110 */         MapFieldSchemas.full());
/*     */   }
/*     */   
/*     */   private static boolean isProto2(MessageInfo messageInfo) {
/* 114 */     return (messageInfo.getSyntax() == ProtoSyntax.PROTO2);
/*     */   }
/*     */   
/*     */   private static MessageInfoFactory getDefaultMessageInfoFactory() {
/* 118 */     return new CompositeMessageInfoFactory(new MessageInfoFactory[] {
/* 119 */           GeneratedMessageInfoFactory.getInstance(), getDescriptorMessageInfoFactory() });
/*     */   }
/*     */   
/*     */   private static class CompositeMessageInfoFactory implements MessageInfoFactory {
/*     */     private MessageInfoFactory[] factories;
/*     */     
/*     */     CompositeMessageInfoFactory(MessageInfoFactory... factories) {
/* 126 */       this.factories = factories;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSupported(Class<?> clazz) {
/* 131 */       for (MessageInfoFactory factory : this.factories) {
/* 132 */         if (factory.isSupported(clazz)) {
/* 133 */           return true;
/*     */         }
/*     */       } 
/* 136 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public MessageInfo messageInfoFor(Class<?> clazz) {
/* 141 */       for (MessageInfoFactory factory : this.factories) {
/* 142 */         if (factory.isSupported(clazz)) {
/* 143 */           return factory.messageInfoFor(clazz);
/*     */         }
/*     */       } 
/* 146 */       throw new UnsupportedOperationException("No factory is available for message type: " + clazz
/* 147 */           .getName());
/*     */     }
/*     */   }
/*     */   
/* 151 */   private static final MessageInfoFactory EMPTY_FACTORY = new MessageInfoFactory()
/*     */     {
/*     */       public boolean isSupported(Class<?> clazz)
/*     */       {
/* 155 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public MessageInfo messageInfoFor(Class<?> clazz) {
/* 160 */         throw new IllegalStateException("This should never be called.");
/*     */       }
/*     */     };
/*     */   
/*     */   private static MessageInfoFactory getDescriptorMessageInfoFactory() {
/*     */     try {
/* 166 */       Class<?> clazz = Class.forName("com.google.protobuf.DescriptorMessageInfoFactory");
/* 167 */       return (MessageInfoFactory)clazz.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
/* 168 */     } catch (Exception e) {
/* 169 */       return EMPTY_FACTORY;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ManifestSchemaFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */