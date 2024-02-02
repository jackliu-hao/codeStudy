/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.ByteString;
/*     */ import com.google.protobuf.CodedOutputStream;
/*     */ import com.google.protobuf.Descriptors;
/*     */ import com.google.protobuf.Message;
/*     */ import com.google.protobuf.MessageLite;
/*     */ import com.google.protobuf.Parser;
/*     */ import com.google.protobuf.UnknownFieldSet;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class XMessage
/*     */   implements Message, Message
/*     */ {
/*     */   private Message message;
/*  50 */   private List<Notice> notices = null;
/*     */   
/*     */   public XMessage(Message mess) {
/*  53 */     this.message = mess;
/*     */   }
/*     */   
/*     */   public Message getMessage() {
/*  57 */     return this.message;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getByteBuffer() {
/*  62 */     return this.message.toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPosition() {
/*  68 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSerializedSize() {
/*  73 */     return this.message.getSerializedSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] toByteArray() {
/*  78 */     return this.message.toByteArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteString toByteString() {
/*  83 */     return this.message.toByteString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeDelimitedTo(OutputStream arg0) throws IOException {
/*  88 */     this.message.writeDelimitedTo(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(CodedOutputStream arg0) throws IOException {
/*  93 */     this.message.writeTo(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream arg0) throws IOException {
/*  98 */     this.message.writeTo(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInitialized() {
/* 103 */     return this.message.isInitialized();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> findInitializationErrors() {
/* 108 */     return this.message.findInitializationErrors();
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
/* 113 */     return this.message.getAllFields();
/*     */   }
/*     */ 
/*     */   
/*     */   public Message getDefaultInstanceForType() {
/* 118 */     return this.message.getDefaultInstanceForType();
/*     */   }
/*     */ 
/*     */   
/*     */   public Descriptors.Descriptor getDescriptorForType() {
/* 123 */     return this.message.getDescriptorForType();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getField(Descriptors.FieldDescriptor arg0) {
/* 128 */     return this.message.getField(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getInitializationErrorString() {
/* 133 */     return this.message.getInitializationErrorString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor arg0) {
/* 138 */     return this.message.getOneofFieldDescriptor(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getRepeatedField(Descriptors.FieldDescriptor arg0, int arg1) {
/* 143 */     return this.message.getRepeatedField(arg0, arg1);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRepeatedFieldCount(Descriptors.FieldDescriptor arg0) {
/* 148 */     return this.message.getRepeatedFieldCount(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public UnknownFieldSet getUnknownFields() {
/* 153 */     return this.message.getUnknownFields();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasField(Descriptors.FieldDescriptor arg0) {
/* 158 */     return this.message.hasField(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasOneof(Descriptors.OneofDescriptor arg0) {
/* 163 */     return this.message.hasOneof(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public Parser<? extends Message> getParserForType() {
/* 168 */     return this.message.getParserForType();
/*     */   }
/*     */ 
/*     */   
/*     */   public Message.Builder newBuilderForType() {
/* 173 */     return this.message.newBuilderForType();
/*     */   }
/*     */ 
/*     */   
/*     */   public Message.Builder toBuilder() {
/* 178 */     return this.message.toBuilder();
/*     */   }
/*     */   
/*     */   public List<Notice> getNotices() {
/* 182 */     return this.notices;
/*     */   }
/*     */   
/*     */   public XMessage addNotices(List<Notice> n) {
/* 186 */     if (n != null) {
/* 187 */       if (this.notices == null) {
/* 188 */         this.notices = new ArrayList<>();
/*     */       }
/* 190 */       this.notices.addAll(n);
/*     */     } 
/* 192 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */