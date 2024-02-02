/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractParser<MessageType extends MessageLite>
/*     */   implements Parser<MessageType>
/*     */ {
/*     */   private UninitializedMessageException newUninitializedMessageException(MessageType message) {
/*  52 */     if (message instanceof AbstractMessageLite) {
/*  53 */       return ((AbstractMessageLite)message).newUninitializedMessageException();
/*     */     }
/*  55 */     return new UninitializedMessageException((MessageLite)message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MessageType checkMessageInitialized(MessageType message) throws InvalidProtocolBufferException {
/*  66 */     if (message != null && !message.isInitialized()) {
/*  67 */       throw newUninitializedMessageException(message)
/*  68 */         .asInvalidProtocolBufferException()
/*  69 */         .setUnfinishedMessage(message);
/*     */     }
/*  71 */     return message;
/*     */   }
/*     */ 
/*     */   
/*  75 */   private static final ExtensionRegistryLite EMPTY_REGISTRY = ExtensionRegistryLite.getEmptyRegistry();
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parsePartialFrom(CodedInputStream input) throws InvalidProtocolBufferException {
/*  80 */     return parsePartialFrom(input, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  86 */     return checkMessageInitialized(parsePartialFrom(input, extensionRegistry));
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(CodedInputStream input) throws InvalidProtocolBufferException {
/*  91 */     return parseFrom(input, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parsePartialFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*     */     try {
/*  99 */       CodedInputStream input = data.newCodedInput();
/* 100 */       MessageLite messageLite = (MessageLite)parsePartialFrom(input, extensionRegistry);
/*     */       try {
/* 102 */         input.checkLastTagWas(0);
/* 103 */       } catch (InvalidProtocolBufferException e) {
/* 104 */         throw e.setUnfinishedMessage(messageLite);
/*     */       } 
/* 106 */       return (MessageType)messageLite;
/* 107 */     } catch (InvalidProtocolBufferException e) {
/* 108 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageType parsePartialFrom(ByteString data) throws InvalidProtocolBufferException {
/* 114 */     return parsePartialFrom(data, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 120 */     return checkMessageInitialized(parsePartialFrom(data, extensionRegistry));
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 125 */     return parseFrom(data, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*     */     MessageLite messageLite;
/*     */     try {
/* 133 */       CodedInputStream input = CodedInputStream.newInstance(data);
/* 134 */       messageLite = (MessageLite)parsePartialFrom(input, extensionRegistry);
/*     */       try {
/* 136 */         input.checkLastTagWas(0);
/* 137 */       } catch (InvalidProtocolBufferException e) {
/* 138 */         throw e.setUnfinishedMessage(messageLite);
/*     */       } 
/* 140 */     } catch (InvalidProtocolBufferException e) {
/* 141 */       throw e;
/*     */     } 
/*     */     
/* 144 */     return checkMessageInitialized((MessageType)messageLite);
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 149 */     return parseFrom(data, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parsePartialFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*     */     try {
/* 157 */       CodedInputStream input = CodedInputStream.newInstance(data, off, len);
/* 158 */       MessageLite messageLite = (MessageLite)parsePartialFrom(input, extensionRegistry);
/*     */       try {
/* 160 */         input.checkLastTagWas(0);
/* 161 */       } catch (InvalidProtocolBufferException e) {
/* 162 */         throw e.setUnfinishedMessage(messageLite);
/*     */       } 
/* 164 */       return (MessageType)messageLite;
/* 165 */     } catch (InvalidProtocolBufferException e) {
/* 166 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parsePartialFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
/* 173 */     return parsePartialFrom(data, off, len, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parsePartialFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 179 */     return parsePartialFrom(data, 0, data.length, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageType parsePartialFrom(byte[] data) throws InvalidProtocolBufferException {
/* 184 */     return parsePartialFrom(data, 0, data.length, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 191 */     return checkMessageInitialized(parsePartialFrom(data, off, len, extensionRegistry));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
/* 197 */     return parseFrom(data, off, len, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 203 */     return parseFrom(data, 0, data.length, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 208 */     return parseFrom(data, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parsePartialFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 214 */     CodedInputStream codedInput = CodedInputStream.newInstance(input);
/* 215 */     MessageLite messageLite = (MessageLite)parsePartialFrom(codedInput, extensionRegistry);
/*     */     try {
/* 217 */       codedInput.checkLastTagWas(0);
/* 218 */     } catch (InvalidProtocolBufferException e) {
/* 219 */       throw e.setUnfinishedMessage(messageLite);
/*     */     } 
/* 221 */     return (MessageType)messageLite;
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageType parsePartialFrom(InputStream input) throws InvalidProtocolBufferException {
/* 226 */     return parsePartialFrom(input, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 232 */     return checkMessageInitialized(parsePartialFrom(input, extensionRegistry));
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageType parseFrom(InputStream input) throws InvalidProtocolBufferException {
/* 237 */     return parseFrom(input, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parsePartialDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*     */     int size;
/*     */     try {
/* 246 */       int firstByte = input.read();
/* 247 */       if (firstByte == -1) {
/* 248 */         return null;
/*     */       }
/* 250 */       size = CodedInputStream.readRawVarint32(firstByte, input);
/* 251 */     } catch (IOException e) {
/* 252 */       throw new InvalidProtocolBufferException(e);
/*     */     } 
/* 254 */     InputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
/* 255 */     return parsePartialFrom(limitedInput, extensionRegistry);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parsePartialDelimitedFrom(InputStream input) throws InvalidProtocolBufferException {
/* 261 */     return parsePartialDelimitedFrom(input, EMPTY_REGISTRY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageType parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 267 */     return checkMessageInitialized(parsePartialDelimitedFrom(input, extensionRegistry));
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageType parseDelimitedFrom(InputStream input) throws InvalidProtocolBufferException {
/* 272 */     return parseDelimitedFrom(input, EMPTY_REGISTRY);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\AbstractParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */