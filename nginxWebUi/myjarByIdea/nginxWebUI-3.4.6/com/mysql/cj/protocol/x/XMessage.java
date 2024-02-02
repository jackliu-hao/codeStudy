package com.mysql.cj.protocol.x;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import com.mysql.cj.protocol.Message;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XMessage implements Message, com.google.protobuf.Message {
   private com.google.protobuf.Message message;
   private List<Notice> notices = null;

   public XMessage(com.google.protobuf.Message mess) {
      this.message = mess;
   }

   public com.google.protobuf.Message getMessage() {
      return this.message;
   }

   public byte[] getByteBuffer() {
      return this.message.toByteArray();
   }

   public int getPosition() {
      return 0;
   }

   public int getSerializedSize() {
      return this.message.getSerializedSize();
   }

   public byte[] toByteArray() {
      return this.message.toByteArray();
   }

   public ByteString toByteString() {
      return this.message.toByteString();
   }

   public void writeDelimitedTo(OutputStream arg0) throws IOException {
      this.message.writeDelimitedTo(arg0);
   }

   public void writeTo(CodedOutputStream arg0) throws IOException {
      this.message.writeTo(arg0);
   }

   public void writeTo(OutputStream arg0) throws IOException {
      this.message.writeTo(arg0);
   }

   public boolean isInitialized() {
      return this.message.isInitialized();
   }

   public List<String> findInitializationErrors() {
      return this.message.findInitializationErrors();
   }

   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      return this.message.getAllFields();
   }

   public com.google.protobuf.Message getDefaultInstanceForType() {
      return this.message.getDefaultInstanceForType();
   }

   public Descriptors.Descriptor getDescriptorForType() {
      return this.message.getDescriptorForType();
   }

   public Object getField(Descriptors.FieldDescriptor arg0) {
      return this.message.getField(arg0);
   }

   public String getInitializationErrorString() {
      return this.message.getInitializationErrorString();
   }

   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor arg0) {
      return this.message.getOneofFieldDescriptor(arg0);
   }

   public Object getRepeatedField(Descriptors.FieldDescriptor arg0, int arg1) {
      return this.message.getRepeatedField(arg0, arg1);
   }

   public int getRepeatedFieldCount(Descriptors.FieldDescriptor arg0) {
      return this.message.getRepeatedFieldCount(arg0);
   }

   public UnknownFieldSet getUnknownFields() {
      return this.message.getUnknownFields();
   }

   public boolean hasField(Descriptors.FieldDescriptor arg0) {
      return this.message.hasField(arg0);
   }

   public boolean hasOneof(Descriptors.OneofDescriptor arg0) {
      return this.message.hasOneof(arg0);
   }

   public Parser<? extends com.google.protobuf.Message> getParserForType() {
      return this.message.getParserForType();
   }

   public com.google.protobuf.Message.Builder newBuilderForType() {
      return this.message.newBuilderForType();
   }

   public com.google.protobuf.Message.Builder toBuilder() {
      return this.message.toBuilder();
   }

   public List<Notice> getNotices() {
      return this.notices;
   }

   public XMessage addNotices(List<Notice> n) {
      if (n != null) {
         if (this.notices == null) {
            this.notices = new ArrayList();
         }

         this.notices.addAll(n);
      }

      return this;
   }
}
