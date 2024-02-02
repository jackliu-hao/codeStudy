/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Logger;
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
/*     */ public class TypeRegistry
/*     */ {
/*     */   private final Map<String, Descriptors.Descriptor> types;
/*  47 */   private static final Logger logger = Logger.getLogger(TypeRegistry.class.getName());
/*     */   
/*     */   private static class EmptyTypeRegistryHolder {
/*  50 */     private static final TypeRegistry EMPTY = new TypeRegistry(
/*  51 */         Collections.emptyMap());
/*     */   }
/*     */   
/*     */   public static TypeRegistry getEmptyTypeRegistry() {
/*  55 */     return EmptyTypeRegistryHolder.EMPTY;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Builder newBuilder() {
/*  60 */     return new Builder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Descriptors.Descriptor find(String name) {
/*  67 */     return this.types.get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Descriptors.Descriptor getDescriptorForTypeUrl(String typeUrl) throws InvalidProtocolBufferException {
/*  76 */     return find(getTypeName(typeUrl));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   TypeRegistry(Map<String, Descriptors.Descriptor> types) {
/*  82 */     this.types = types;
/*     */   }
/*     */   
/*     */   private static String getTypeName(String typeUrl) throws InvalidProtocolBufferException {
/*  86 */     String[] parts = typeUrl.split("/");
/*  87 */     if (parts.length == 1) {
/*  88 */       throw new InvalidProtocolBufferException("Invalid type url found: " + typeUrl);
/*     */     }
/*  90 */     return parts[parts.length - 1];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */   {
/*     */     public Builder add(Descriptors.Descriptor messageType) {
/* 102 */       if (this.types == null) {
/* 103 */         throw new IllegalStateException("A TypeRegistry.Builder can only be used once.");
/*     */       }
/* 105 */       addFile(messageType.getFile());
/* 106 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder add(Iterable<Descriptors.Descriptor> messageTypes) {
/* 114 */       if (this.types == null) {
/* 115 */         throw new IllegalStateException("A TypeRegistry.Builder can only be used once.");
/*     */       }
/* 117 */       for (Descriptors.Descriptor type : messageTypes) {
/* 118 */         addFile(type.getFile());
/*     */       }
/* 120 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public TypeRegistry build() {
/* 125 */       TypeRegistry result = new TypeRegistry(this.types);
/*     */       
/* 127 */       this.types = null;
/* 128 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     private void addFile(Descriptors.FileDescriptor file) {
/* 133 */       if (!this.files.add(file.getFullName())) {
/*     */         return;
/*     */       }
/* 136 */       for (Descriptors.FileDescriptor dependency : file.getDependencies()) {
/* 137 */         addFile(dependency);
/*     */       }
/* 139 */       for (Descriptors.Descriptor message : file.getMessageTypes()) {
/* 140 */         addMessage(message);
/*     */       }
/*     */     }
/*     */     
/*     */     private void addMessage(Descriptors.Descriptor message) {
/* 145 */       for (Descriptors.Descriptor nestedType : message.getNestedTypes()) {
/* 146 */         addMessage(nestedType);
/*     */       }
/*     */       
/* 149 */       if (this.types.containsKey(message.getFullName())) {
/* 150 */         TypeRegistry.logger.warning("Type " + message.getFullName() + " is added multiple times.");
/*     */         
/*     */         return;
/*     */       } 
/* 154 */       this.types.put(message.getFullName(), message);
/*     */     }
/*     */     
/* 157 */     private final Set<String> files = new HashSet<>();
/* 158 */     private Map<String, Descriptors.Descriptor> types = new HashMap<>();
/*     */     
/*     */     private Builder() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\TypeRegistry.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */