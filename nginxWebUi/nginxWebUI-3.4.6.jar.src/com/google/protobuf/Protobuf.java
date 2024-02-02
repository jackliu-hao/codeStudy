/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ final class Protobuf
/*     */ {
/*  45 */   private static final Protobuf INSTANCE = new Protobuf();
/*     */ 
/*     */   
/*     */   private final SchemaFactory schemaFactory;
/*     */   
/*  50 */   private final ConcurrentMap<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   public static Protobuf getInstance() {
/*  55 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void writeTo(T message, Writer writer) throws IOException {
/*  60 */     schemaFor(message).writeTo(message, writer);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void mergeFrom(T message, Reader reader) throws IOException {
/*  65 */     mergeFrom(message, reader, ExtensionRegistryLite.getEmptyRegistry());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  71 */     schemaFor(message).mergeFrom(message, reader, extensionRegistry);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void makeImmutable(T message) {
/*  76 */     schemaFor(message).makeImmutable(message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> boolean isInitialized(T message) {
/*  84 */     return schemaFor(message).isInitialized(message);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> Schema<T> schemaFor(Class<T> messageType) {
/*  89 */     Internal.checkNotNull(messageType, "messageType");
/*     */     
/*  91 */     Schema<T> schema = (Schema<T>)this.schemaCache.get(messageType);
/*  92 */     if (schema == null) {
/*  93 */       schema = this.schemaFactory.createSchema(messageType);
/*     */       
/*  95 */       Schema<T> previous = (Schema)registerSchema(messageType, schema);
/*  96 */       if (previous != null)
/*     */       {
/*  98 */         schema = previous;
/*     */       }
/*     */     } 
/* 101 */     return schema;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Schema<T> schemaFor(T message) {
/* 107 */     return schemaFor((Class)message.getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Schema<?> registerSchema(Class<?> messageType, Schema<?> schema) {
/* 119 */     Internal.checkNotNull(messageType, "messageType");
/* 120 */     Internal.checkNotNull(schema, "schema");
/* 121 */     return this.schemaCache.putIfAbsent(messageType, schema);
/*     */   }
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
/*     */   public Schema<?> registerSchemaOverride(Class<?> messageType, Schema<?> schema) {
/* 134 */     Internal.checkNotNull(messageType, "messageType");
/* 135 */     Internal.checkNotNull(schema, "schema");
/* 136 */     return this.schemaCache.put(messageType, schema);
/*     */   }
/*     */   
/*     */   private Protobuf() {
/* 140 */     this.schemaFactory = new ManifestSchemaFactory();
/*     */   }
/*     */   
/*     */   int getTotalSchemaSize() {
/* 144 */     int result = 0;
/* 145 */     for (Schema<?> schema : this.schemaCache.values()) {
/* 146 */       if (schema instanceof MessageSchema) {
/* 147 */         result += ((MessageSchema)schema).getSchemaSize();
/*     */       }
/*     */     } 
/* 150 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Protobuf.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */