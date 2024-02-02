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
/*     */ abstract class UnknownFieldSchema<T, B>
/*     */ {
/*     */   abstract boolean shouldDiscardUnknownFields(Reader paramReader);
/*     */   
/*     */   abstract void addVarint(B paramB, int paramInt, long paramLong);
/*     */   
/*     */   abstract void addFixed32(B paramB, int paramInt1, int paramInt2);
/*     */   
/*     */   abstract void addFixed64(B paramB, int paramInt, long paramLong);
/*     */   
/*     */   abstract void addLengthDelimited(B paramB, int paramInt, ByteString paramByteString);
/*     */   
/*     */   abstract void addGroup(B paramB, int paramInt, T paramT);
/*     */   
/*     */   abstract B newBuilder();
/*     */   
/*     */   abstract T toImmutable(B paramB);
/*     */   
/*     */   abstract void setToMessage(Object paramObject, T paramT);
/*     */   
/*     */   abstract T getFromMessage(Object paramObject);
/*     */   
/*     */   abstract B getBuilderFromMessage(Object paramObject);
/*     */   
/*     */   abstract void setBuilderToMessage(Object paramObject, B paramB);
/*     */   
/*     */   abstract void makeImmutable(Object paramObject);
/*     */   
/*     */   final boolean mergeOneFieldFrom(B unknownFields, Reader reader) throws IOException {
/*     */     B subFields;
/*  82 */     int endGroupTag, tag = reader.getTag();
/*  83 */     int fieldNumber = WireFormat.getTagFieldNumber(tag);
/*  84 */     switch (WireFormat.getTagWireType(tag)) {
/*     */       case 0:
/*  86 */         addVarint(unknownFields, fieldNumber, reader.readInt64());
/*  87 */         return true;
/*     */       case 5:
/*  89 */         addFixed32(unknownFields, fieldNumber, reader.readFixed32());
/*  90 */         return true;
/*     */       case 1:
/*  92 */         addFixed64(unknownFields, fieldNumber, reader.readFixed64());
/*  93 */         return true;
/*     */       case 2:
/*  95 */         addLengthDelimited(unknownFields, fieldNumber, reader.readBytes());
/*  96 */         return true;
/*     */       case 3:
/*  98 */         subFields = newBuilder();
/*  99 */         endGroupTag = WireFormat.makeTag(fieldNumber, 4);
/* 100 */         mergeFrom(subFields, reader);
/* 101 */         if (endGroupTag != reader.getTag()) {
/* 102 */           throw InvalidProtocolBufferException.invalidEndTag();
/*     */         }
/* 104 */         addGroup(unknownFields, fieldNumber, toImmutable(subFields));
/* 105 */         return true;
/*     */       case 4:
/* 107 */         return false;
/*     */     } 
/* 109 */     throw InvalidProtocolBufferException.invalidWireType();
/*     */   }
/*     */   
/*     */   final void mergeFrom(B unknownFields, Reader reader) throws IOException {
/*     */     do {
/*     */     
/* 115 */     } while (reader.getFieldNumber() != Integer.MAX_VALUE && 
/* 116 */       mergeOneFieldFrom(unknownFields, reader));
/*     */   }
/*     */   
/*     */   abstract void writeTo(T paramT, Writer paramWriter) throws IOException;
/*     */   
/*     */   abstract void writeAsMessageSetTo(T paramT, Writer paramWriter) throws IOException;
/*     */   
/*     */   abstract T merge(T paramT1, T paramT2);
/*     */   
/*     */   abstract int getSerializedSizeAsMessageSet(T paramT);
/*     */   
/*     */   abstract int getSerializedSize(T paramT);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\UnknownFieldSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */