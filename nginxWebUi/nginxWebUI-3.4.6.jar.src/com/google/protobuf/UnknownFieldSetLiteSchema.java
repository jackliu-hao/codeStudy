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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class UnknownFieldSetLiteSchema
/*     */   extends UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite>
/*     */ {
/*     */   boolean shouldDiscardUnknownFields(Reader reader) {
/*  43 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   UnknownFieldSetLite newBuilder() {
/*  48 */     return UnknownFieldSetLite.newInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   void addVarint(UnknownFieldSetLite fields, int number, long value) {
/*  53 */     fields.storeField(WireFormat.makeTag(number, 0), Long.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   void addFixed32(UnknownFieldSetLite fields, int number, int value) {
/*  58 */     fields.storeField(WireFormat.makeTag(number, 5), Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   void addFixed64(UnknownFieldSetLite fields, int number, long value) {
/*  63 */     fields.storeField(WireFormat.makeTag(number, 1), Long.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   void addLengthDelimited(UnknownFieldSetLite fields, int number, ByteString value) {
/*  68 */     fields.storeField(WireFormat.makeTag(number, 2), value);
/*     */   }
/*     */ 
/*     */   
/*     */   void addGroup(UnknownFieldSetLite fields, int number, UnknownFieldSetLite subFieldSet) {
/*  73 */     fields.storeField(WireFormat.makeTag(number, 3), subFieldSet);
/*     */   }
/*     */ 
/*     */   
/*     */   UnknownFieldSetLite toImmutable(UnknownFieldSetLite fields) {
/*  78 */     fields.makeImmutable();
/*  79 */     return fields;
/*     */   }
/*     */ 
/*     */   
/*     */   void setToMessage(Object message, UnknownFieldSetLite fields) {
/*  84 */     ((GeneratedMessageLite)message).unknownFields = fields;
/*     */   }
/*     */ 
/*     */   
/*     */   UnknownFieldSetLite getFromMessage(Object message) {
/*  89 */     return ((GeneratedMessageLite)message).unknownFields;
/*     */   }
/*     */ 
/*     */   
/*     */   UnknownFieldSetLite getBuilderFromMessage(Object message) {
/*  94 */     UnknownFieldSetLite unknownFields = getFromMessage(message);
/*     */ 
/*     */     
/*  97 */     if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
/*  98 */       unknownFields = UnknownFieldSetLite.newInstance();
/*  99 */       setToMessage(message, unknownFields);
/*     */     } 
/* 101 */     return unknownFields;
/*     */   }
/*     */ 
/*     */   
/*     */   void setBuilderToMessage(Object message, UnknownFieldSetLite fields) {
/* 106 */     setToMessage(message, fields);
/*     */   }
/*     */ 
/*     */   
/*     */   void makeImmutable(Object message) {
/* 111 */     getFromMessage(message).makeImmutable();
/*     */   }
/*     */ 
/*     */   
/*     */   void writeTo(UnknownFieldSetLite fields, Writer writer) throws IOException {
/* 116 */     fields.writeTo(writer);
/*     */   }
/*     */ 
/*     */   
/*     */   void writeAsMessageSetTo(UnknownFieldSetLite fields, Writer writer) throws IOException {
/* 121 */     fields.writeAsMessageSetTo(writer);
/*     */   }
/*     */ 
/*     */   
/*     */   UnknownFieldSetLite merge(UnknownFieldSetLite message, UnknownFieldSetLite other) {
/* 126 */     return other.equals(UnknownFieldSetLite.getDefaultInstance()) ? message : 
/*     */       
/* 128 */       UnknownFieldSetLite.mutableCopyOf(message, other);
/*     */   }
/*     */ 
/*     */   
/*     */   int getSerializedSize(UnknownFieldSetLite unknowns) {
/* 133 */     return unknowns.getSerializedSize();
/*     */   }
/*     */ 
/*     */   
/*     */   int getSerializedSizeAsMessageSet(UnknownFieldSetLite unknowns) {
/* 138 */     return unknowns.getSerializedSizeAsMessageSet();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\UnknownFieldSetLiteSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */