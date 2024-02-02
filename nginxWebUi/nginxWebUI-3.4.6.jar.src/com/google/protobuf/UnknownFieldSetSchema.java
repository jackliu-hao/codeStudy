/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ class UnknownFieldSetSchema
/*     */   extends UnknownFieldSchema<UnknownFieldSet, UnknownFieldSet.Builder>
/*     */ {
/*     */   private final boolean proto3;
/*     */   
/*     */   public UnknownFieldSetSchema(boolean proto3) {
/*  40 */     this.proto3 = proto3;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean shouldDiscardUnknownFields(Reader reader) {
/*  45 */     return reader.shouldDiscardUnknownFields();
/*     */   }
/*     */ 
/*     */   
/*     */   UnknownFieldSet.Builder newBuilder() {
/*  50 */     return UnknownFieldSet.newBuilder();
/*     */   }
/*     */ 
/*     */   
/*     */   void addVarint(UnknownFieldSet.Builder fields, int number, long value) {
/*  55 */     fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addVarint(value).build());
/*     */   }
/*     */ 
/*     */   
/*     */   void addFixed32(UnknownFieldSet.Builder fields, int number, int value) {
/*  60 */     fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addFixed32(value).build());
/*     */   }
/*     */ 
/*     */   
/*     */   void addFixed64(UnknownFieldSet.Builder fields, int number, long value) {
/*  65 */     fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addFixed64(value).build());
/*     */   }
/*     */ 
/*     */   
/*     */   void addLengthDelimited(UnknownFieldSet.Builder fields, int number, ByteString value) {
/*  70 */     fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addLengthDelimited(value).build());
/*     */   }
/*     */ 
/*     */   
/*     */   void addGroup(UnknownFieldSet.Builder fields, int number, UnknownFieldSet subFieldSet) {
/*  75 */     fields.mergeField(number, UnknownFieldSet.Field.newBuilder().addGroup(subFieldSet).build());
/*     */   }
/*     */ 
/*     */   
/*     */   UnknownFieldSet toImmutable(UnknownFieldSet.Builder fields) {
/*  80 */     return fields.build();
/*     */   }
/*     */ 
/*     */   
/*     */   void writeTo(UnknownFieldSet message, Writer writer) throws IOException {
/*  85 */     message.writeTo(writer);
/*     */   }
/*     */ 
/*     */   
/*     */   void writeAsMessageSetTo(UnknownFieldSet message, Writer writer) throws IOException {
/*  90 */     message.writeAsMessageSetTo(writer);
/*     */   }
/*     */ 
/*     */   
/*     */   UnknownFieldSet getFromMessage(Object message) {
/*  95 */     return ((GeneratedMessageV3)message).unknownFields;
/*     */   }
/*     */ 
/*     */   
/*     */   void setToMessage(Object message, UnknownFieldSet fields) {
/* 100 */     ((GeneratedMessageV3)message).unknownFields = fields;
/*     */   }
/*     */ 
/*     */   
/*     */   UnknownFieldSet.Builder getBuilderFromMessage(Object message) {
/* 105 */     return ((GeneratedMessageV3)message).unknownFields.toBuilder();
/*     */   }
/*     */ 
/*     */   
/*     */   void setBuilderToMessage(Object message, UnknownFieldSet.Builder builder) {
/* 110 */     ((GeneratedMessageV3)message).unknownFields = builder.build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void makeImmutable(Object message) {}
/*     */ 
/*     */ 
/*     */   
/*     */   UnknownFieldSet merge(UnknownFieldSet message, UnknownFieldSet other) {
/* 120 */     return message.toBuilder().mergeFrom(other).build();
/*     */   }
/*     */ 
/*     */   
/*     */   int getSerializedSize(UnknownFieldSet message) {
/* 125 */     return message.getSerializedSize();
/*     */   }
/*     */ 
/*     */   
/*     */   int getSerializedSizeAsMessageSet(UnknownFieldSet unknowns) {
/* 130 */     return unknowns.getSerializedSizeAsMessageSet();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\UnknownFieldSetSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */