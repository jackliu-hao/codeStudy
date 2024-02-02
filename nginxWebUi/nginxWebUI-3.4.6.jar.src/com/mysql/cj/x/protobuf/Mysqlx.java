/*      */ package com.mysql.cj.x.protobuf;
/*      */ 
/*      */ import com.google.protobuf.AbstractMessage;
/*      */ import com.google.protobuf.AbstractMessageLite;
/*      */ import com.google.protobuf.AbstractParser;
/*      */ import com.google.protobuf.ByteString;
/*      */ import com.google.protobuf.CodedInputStream;
/*      */ import com.google.protobuf.CodedOutputStream;
/*      */ import com.google.protobuf.DescriptorProtos;
/*      */ import com.google.protobuf.Descriptors;
/*      */ import com.google.protobuf.ExtensionLite;
/*      */ import com.google.protobuf.ExtensionRegistry;
/*      */ import com.google.protobuf.ExtensionRegistryLite;
/*      */ import com.google.protobuf.GeneratedMessage;
/*      */ import com.google.protobuf.GeneratedMessageV3;
/*      */ import com.google.protobuf.Internal;
/*      */ import com.google.protobuf.InvalidProtocolBufferException;
/*      */ import com.google.protobuf.Message;
/*      */ import com.google.protobuf.MessageLite;
/*      */ import com.google.protobuf.MessageOrBuilder;
/*      */ import com.google.protobuf.Parser;
/*      */ import com.google.protobuf.ProtocolMessageEnum;
/*      */ import com.google.protobuf.UnknownFieldSet;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Mysqlx
/*      */ {
/*      */   public static final int CLIENT_MESSAGE_ID_FIELD_NUMBER = 100001;
/*      */   
/*      */   public static void registerAllExtensions(ExtensionRegistryLite registry) {
/*   39 */     registry.add((ExtensionLite)clientMessageId);
/*   40 */     registry.add((ExtensionLite)serverMessageId);
/*      */   }
/*      */ 
/*      */   
/*      */   public static void registerAllExtensions(ExtensionRegistry registry) {
/*   45 */     registerAllExtensions((ExtensionRegistryLite)registry);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class ClientMessages
/*      */     extends GeneratedMessageV3
/*      */     implements ClientMessagesOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ClientMessages(GeneratedMessageV3.Builder<?> builder) {
/*   71 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  432 */       this.memoizedIsInitialized = -1; } private ClientMessages() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new ClientMessages(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private ClientMessages(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return Mysqlx.internal_static_Mysqlx_ClientMessages_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return Mysqlx.internal_static_Mysqlx_ClientMessages_fieldAccessorTable.ensureFieldAccessorsInitialized(ClientMessages.class, Builder.class); } public enum Type implements ProtocolMessageEnum {
/*      */       CON_CAPABILITIES_GET(1), CON_CAPABILITIES_SET(2), CON_CLOSE(3), SESS_AUTHENTICATE_START(4), SESS_AUTHENTICATE_CONTINUE(5), SESS_RESET(6), SESS_CLOSE(7), SQL_STMT_EXECUTE(12), CRUD_FIND(17), CRUD_INSERT(18), CRUD_UPDATE(19), CRUD_DELETE(20), EXPECT_OPEN(24), EXPECT_CLOSE(25), CRUD_CREATE_VIEW(30), CRUD_MODIFY_VIEW(31), CRUD_DROP_VIEW(32), PREPARE_PREPARE(40), PREPARE_EXECUTE(41), PREPARE_DEALLOCATE(42), CURSOR_OPEN(43), CURSOR_CLOSE(44), CURSOR_FETCH(45), COMPRESSION(46); public static final int CON_CAPABILITIES_GET_VALUE = 1; public static final int CON_CAPABILITIES_SET_VALUE = 2; public static final int CON_CLOSE_VALUE = 3; public static final int SESS_AUTHENTICATE_START_VALUE = 4; public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 5; public static final int SESS_RESET_VALUE = 6; public static final int SESS_CLOSE_VALUE = 7; public static final int SQL_STMT_EXECUTE_VALUE = 12; public static final int CRUD_FIND_VALUE = 17; public static final int CRUD_INSERT_VALUE = 18; public static final int CRUD_UPDATE_VALUE = 19; public static final int CRUD_DELETE_VALUE = 20; public static final int EXPECT_OPEN_VALUE = 24; public static final int EXPECT_CLOSE_VALUE = 25; public static final int CRUD_CREATE_VIEW_VALUE = 30; public static final int CRUD_MODIFY_VIEW_VALUE = 31; public static final int CRUD_DROP_VIEW_VALUE = 32; public static final int PREPARE_PREPARE_VALUE = 40; public static final int PREPARE_EXECUTE_VALUE = 41; public static final int PREPARE_DEALLOCATE_VALUE = 42; public static final int CURSOR_OPEN_VALUE = 43; public static final int CURSOR_CLOSE_VALUE = 44; public static final int CURSOR_FETCH_VALUE = 45; public static final int COMPRESSION_VALUE = 46; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public Mysqlx.ClientMessages.Type findValueByNumber(int number) { return Mysqlx.ClientMessages.Type.forNumber(number); } }
/*      */       ; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 1: return CON_CAPABILITIES_GET;case 2: return CON_CAPABILITIES_SET;case 3: return CON_CLOSE;case 4: return SESS_AUTHENTICATE_START;case 5: return SESS_AUTHENTICATE_CONTINUE;case 6: return SESS_RESET;case 7: return SESS_CLOSE;case 12: return SQL_STMT_EXECUTE;case 17: return CRUD_FIND;case 18: return CRUD_INSERT;case 19: return CRUD_UPDATE;case 20: return CRUD_DELETE;case 24: return EXPECT_OPEN;case 25: return EXPECT_CLOSE;case 30: return CRUD_CREATE_VIEW;case 31: return CRUD_MODIFY_VIEW;case 32: return CRUD_DROP_VIEW;case 40: return PREPARE_PREPARE;case 41: return PREPARE_EXECUTE;case 42: return PREPARE_DEALLOCATE;case 43: return CURSOR_OPEN;case 44: return CURSOR_CLOSE;case 45: return CURSOR_FETCH;case 46: return COMPRESSION; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return Mysqlx.ClientMessages.getDescriptor().getEnumTypes().get(0); } Type(int value) { this.value = value; } }
/*  435 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/*  436 */       if (isInitialized == 1) return true; 
/*  437 */       if (isInitialized == 0) return false;
/*      */       
/*  439 */       this.memoizedIsInitialized = 1;
/*  440 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/*  446 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/*  451 */       int size = this.memoizedSize;
/*  452 */       if (size != -1) return size;
/*      */       
/*  454 */       size = 0;
/*  455 */       size += this.unknownFields.getSerializedSize();
/*  456 */       this.memoizedSize = size;
/*  457 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*  462 */       if (obj == this) {
/*  463 */         return true;
/*      */       }
/*  465 */       if (!(obj instanceof ClientMessages)) {
/*  466 */         return super.equals(obj);
/*      */       }
/*  468 */       ClientMessages other = (ClientMessages)obj;
/*      */       
/*  470 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/*  471 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  476 */       if (this.memoizedHashCode != 0) {
/*  477 */         return this.memoizedHashCode;
/*      */       }
/*  479 */       int hash = 41;
/*  480 */       hash = 19 * hash + getDescriptor().hashCode();
/*  481 */       hash = 29 * hash + this.unknownFields.hashCode();
/*  482 */       this.memoizedHashCode = hash;
/*  483 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ClientMessages parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/*  489 */       return (ClientMessages)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ClientMessages parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  495 */       return (ClientMessages)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static ClientMessages parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  500 */       return (ClientMessages)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ClientMessages parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  506 */       return (ClientMessages)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ClientMessages parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  510 */       return (ClientMessages)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ClientMessages parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  516 */       return (ClientMessages)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ClientMessages parseFrom(InputStream input) throws IOException {
/*  520 */       return 
/*  521 */         (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ClientMessages parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  527 */       return 
/*  528 */         (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ClientMessages parseDelimitedFrom(InputStream input) throws IOException {
/*  532 */       return 
/*  533 */         (ClientMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ClientMessages parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  539 */       return 
/*  540 */         (ClientMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static ClientMessages parseFrom(CodedInputStream input) throws IOException {
/*  545 */       return 
/*  546 */         (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ClientMessages parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  552 */       return 
/*  553 */         (ClientMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/*  557 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/*  559 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(ClientMessages prototype) {
/*  562 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/*  566 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/*  567 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/*  573 */       Builder builder = new Builder(parent);
/*  574 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements Mysqlx.ClientMessagesOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/*  594 */         return Mysqlx.internal_static_Mysqlx_ClientMessages_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/*  600 */         return Mysqlx.internal_static_Mysqlx_ClientMessages_fieldAccessorTable
/*  601 */           .ensureFieldAccessorsInitialized(Mysqlx.ClientMessages.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/*  607 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/*  612 */         super(parent);
/*  613 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/*  617 */         if (Mysqlx.ClientMessages.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/*  622 */         super.clear();
/*  623 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/*  629 */         return Mysqlx.internal_static_Mysqlx_ClientMessages_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public Mysqlx.ClientMessages getDefaultInstanceForType() {
/*  634 */         return Mysqlx.ClientMessages.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public Mysqlx.ClientMessages build() {
/*  639 */         Mysqlx.ClientMessages result = buildPartial();
/*  640 */         if (!result.isInitialized()) {
/*  641 */           throw newUninitializedMessageException(result);
/*      */         }
/*  643 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Mysqlx.ClientMessages buildPartial() {
/*  648 */         Mysqlx.ClientMessages result = new Mysqlx.ClientMessages(this);
/*  649 */         onBuilt();
/*  650 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/*  655 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/*  661 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/*  666 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/*  671 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/*  677 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/*  683 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/*  687 */         if (other instanceof Mysqlx.ClientMessages) {
/*  688 */           return mergeFrom((Mysqlx.ClientMessages)other);
/*      */         }
/*  690 */         super.mergeFrom(other);
/*  691 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(Mysqlx.ClientMessages other) {
/*  696 */         if (other == Mysqlx.ClientMessages.getDefaultInstance()) return this; 
/*  697 */         mergeUnknownFields(other.unknownFields);
/*  698 */         onChanged();
/*  699 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/*  704 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  712 */         Mysqlx.ClientMessages parsedMessage = null;
/*      */         try {
/*  714 */           parsedMessage = (Mysqlx.ClientMessages)Mysqlx.ClientMessages.PARSER.parsePartialFrom(input, extensionRegistry);
/*  715 */         } catch (InvalidProtocolBufferException e) {
/*  716 */           parsedMessage = (Mysqlx.ClientMessages)e.getUnfinishedMessage();
/*  717 */           throw e.unwrapIOException();
/*      */         } finally {
/*  719 */           if (parsedMessage != null) {
/*  720 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/*  723 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/*  728 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/*  734 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  744 */     private static final ClientMessages DEFAULT_INSTANCE = new ClientMessages();
/*      */ 
/*      */     
/*      */     public static ClientMessages getDefaultInstance() {
/*  748 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/*  752 */     public static final Parser<ClientMessages> PARSER = (Parser<ClientMessages>)new AbstractParser<ClientMessages>()
/*      */       {
/*      */ 
/*      */         
/*      */         public Mysqlx.ClientMessages parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/*  758 */           return new Mysqlx.ClientMessages(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<ClientMessages> parser() {
/*  763 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<ClientMessages> getParserForType() {
/*  768 */       return PARSER;
/*      */     }
/*      */     
/*      */     public ClientMessages getDefaultInstanceForType()
/*      */     {
/*  773 */       return DEFAULT_INSTANCE;
/*      */     } } public enum Type implements ProtocolMessageEnum { CON_CAPABILITIES_GET(1), CON_CAPABILITIES_SET(2), CON_CLOSE(3), SESS_AUTHENTICATE_START(4), SESS_AUTHENTICATE_CONTINUE(5), SESS_RESET(6), SESS_CLOSE(7), SQL_STMT_EXECUTE(12), CRUD_FIND(17), CRUD_INSERT(18), CRUD_UPDATE(19), CRUD_DELETE(20), EXPECT_OPEN(24), EXPECT_CLOSE(25), CRUD_CREATE_VIEW(30), CRUD_MODIFY_VIEW(31), CRUD_DROP_VIEW(32), PREPARE_PREPARE(40), PREPARE_EXECUTE(41), PREPARE_DEALLOCATE(42), CURSOR_OPEN(43), CURSOR_CLOSE(44), CURSOR_FETCH(45), COMPRESSION(46); public static final int CON_CAPABILITIES_GET_VALUE = 1; public static final int CON_CAPABILITIES_SET_VALUE = 2; public static final int CON_CLOSE_VALUE = 3; public static final int SESS_AUTHENTICATE_START_VALUE = 4; public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 5; public static final int SESS_RESET_VALUE = 6; public static final int SESS_CLOSE_VALUE = 7; public static final int SQL_STMT_EXECUTE_VALUE = 12; public static final int CRUD_FIND_VALUE = 17; public static final int CRUD_INSERT_VALUE = 18; public static final int CRUD_UPDATE_VALUE = 19; public static final int CRUD_DELETE_VALUE = 20; public static final int EXPECT_OPEN_VALUE = 24; public static final int EXPECT_CLOSE_VALUE = 25; public static final int CRUD_CREATE_VIEW_VALUE = 30; public static final int CRUD_MODIFY_VIEW_VALUE = 31; public static final int CRUD_DROP_VIEW_VALUE = 32; public static final int PREPARE_PREPARE_VALUE = 40; public static final int PREPARE_EXECUTE_VALUE = 41; public static final int PREPARE_DEALLOCATE_VALUE = 42; public static final int CURSOR_OPEN_VALUE = 43; public static final int CURSOR_CLOSE_VALUE = 44; public static final int CURSOR_FETCH_VALUE = 45; public static final int COMPRESSION_VALUE = 46; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public Mysqlx.ClientMessages.Type findValueByNumber(int number) { return Mysqlx.ClientMessages.Type.forNumber(number); } }
/*      */     ; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 1: return CON_CAPABILITIES_GET;case 2: return CON_CAPABILITIES_SET;case 3: return CON_CLOSE;case 4: return SESS_AUTHENTICATE_START;case 5: return SESS_AUTHENTICATE_CONTINUE;case 6: return SESS_RESET;
/*      */         case 7: return SESS_CLOSE;
/*      */         case 12: return SQL_STMT_EXECUTE;
/*      */         case 17: return CRUD_FIND;
/*      */         case 18: return CRUD_INSERT;
/*      */         case 19: return CRUD_UPDATE;
/*      */         case 20: return CRUD_DELETE;
/*      */         case 24: return EXPECT_OPEN;
/*      */         case 25: return EXPECT_CLOSE;
/*      */         case 30: return CRUD_CREATE_VIEW;
/*      */         case 31: return CRUD_MODIFY_VIEW;
/*      */         case 32: return CRUD_DROP_VIEW;
/*      */         case 40: return PREPARE_PREPARE;
/*      */         case 41: return PREPARE_EXECUTE;
/*      */         case 42: return PREPARE_DEALLOCATE;
/*      */         case 43: return CURSOR_OPEN;
/*      */         case 44: return CURSOR_CLOSE;
/*      */         case 45: return CURSOR_FETCH;
/*      */         case 46: return COMPRESSION; }  return null; }
/*      */     public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; }
/*      */     static {  }
/*      */     public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); }
/*      */     public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); }
/*      */     public static final Descriptors.EnumDescriptor getDescriptor() { return Mysqlx.ClientMessages.getDescriptor().getEnumTypes().get(0); }
/*      */     Type(int value) { this.value = value; } }
/*      */   public static final class ServerMessages extends GeneratedMessageV3 implements ServerMessagesOrBuilder { private static final long serialVersionUID = 0L; private byte memoizedIsInitialized;
/*  801 */     private ServerMessages(GeneratedMessageV3.Builder<?> builder) { super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1080 */       this.memoizedIsInitialized = -1; } private ServerMessages() { this.memoizedIsInitialized = -1; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new ServerMessages(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private ServerMessages(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int tag = input.readTag(); switch (tag) { case 0: done = true; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return Mysqlx.internal_static_Mysqlx_ServerMessages_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return Mysqlx.internal_static_Mysqlx_ServerMessages_fieldAccessorTable.ensureFieldAccessorsInitialized(ServerMessages.class, Builder.class); } public enum Type implements ProtocolMessageEnum {
/*      */       OK(0), ERROR(1), CONN_CAPABILITIES(2), SESS_AUTHENTICATE_CONTINUE(3), SESS_AUTHENTICATE_OK(4), NOTICE(11), RESULTSET_COLUMN_META_DATA(12), RESULTSET_ROW(13), RESULTSET_FETCH_DONE(14), RESULTSET_FETCH_SUSPENDED(15), RESULTSET_FETCH_DONE_MORE_RESULTSETS(16), SQL_STMT_EXECUTE_OK(17), RESULTSET_FETCH_DONE_MORE_OUT_PARAMS(18), COMPRESSION(19); public static final int OK_VALUE = 0; public static final int ERROR_VALUE = 1; public static final int CONN_CAPABILITIES_VALUE = 2; public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 3; public static final int SESS_AUTHENTICATE_OK_VALUE = 4; public static final int NOTICE_VALUE = 11; public static final int RESULTSET_COLUMN_META_DATA_VALUE = 12; public static final int RESULTSET_ROW_VALUE = 13; public static final int RESULTSET_FETCH_DONE_VALUE = 14; public static final int RESULTSET_FETCH_SUSPENDED_VALUE = 15; public static final int RESULTSET_FETCH_DONE_MORE_RESULTSETS_VALUE = 16; public static final int SQL_STMT_EXECUTE_OK_VALUE = 17; public static final int RESULTSET_FETCH_DONE_MORE_OUT_PARAMS_VALUE = 18; public static final int COMPRESSION_VALUE = 19; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public Mysqlx.ServerMessages.Type findValueByNumber(int number) { return Mysqlx.ServerMessages.Type.forNumber(number); } }
/*      */       ; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 0: return OK;case 1: return ERROR;case 2: return CONN_CAPABILITIES;case 3: return SESS_AUTHENTICATE_CONTINUE;case 4: return SESS_AUTHENTICATE_OK;case 11: return NOTICE;case 12: return RESULTSET_COLUMN_META_DATA;case 13: return RESULTSET_ROW;case 14: return RESULTSET_FETCH_DONE;case 15: return RESULTSET_FETCH_SUSPENDED;case 16: return RESULTSET_FETCH_DONE_MORE_RESULTSETS;case 17: return SQL_STMT_EXECUTE_OK;case 18: return RESULTSET_FETCH_DONE_MORE_OUT_PARAMS;case 19: return COMPRESSION; }  return null; } public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return Mysqlx.ServerMessages.getDescriptor().getEnumTypes().get(0); } Type(int value) { this.value = value; } }
/* 1083 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1084 */       if (isInitialized == 1) return true; 
/* 1085 */       if (isInitialized == 0) return false;
/*      */       
/* 1087 */       this.memoizedIsInitialized = 1;
/* 1088 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 1094 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1099 */       int size = this.memoizedSize;
/* 1100 */       if (size != -1) return size;
/*      */       
/* 1102 */       size = 0;
/* 1103 */       size += this.unknownFields.getSerializedSize();
/* 1104 */       this.memoizedSize = size;
/* 1105 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1110 */       if (obj == this) {
/* 1111 */         return true;
/*      */       }
/* 1113 */       if (!(obj instanceof ServerMessages)) {
/* 1114 */         return super.equals(obj);
/*      */       }
/* 1116 */       ServerMessages other = (ServerMessages)obj;
/*      */       
/* 1118 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1119 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1124 */       if (this.memoizedHashCode != 0) {
/* 1125 */         return this.memoizedHashCode;
/*      */       }
/* 1127 */       int hash = 41;
/* 1128 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1129 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1130 */       this.memoizedHashCode = hash;
/* 1131 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerMessages parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1137 */       return (ServerMessages)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerMessages parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1143 */       return (ServerMessages)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static ServerMessages parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1148 */       return (ServerMessages)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerMessages parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1154 */       return (ServerMessages)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ServerMessages parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1158 */       return (ServerMessages)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerMessages parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1164 */       return (ServerMessages)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ServerMessages parseFrom(InputStream input) throws IOException {
/* 1168 */       return 
/* 1169 */         (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerMessages parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1175 */       return 
/* 1176 */         (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static ServerMessages parseDelimitedFrom(InputStream input) throws IOException {
/* 1180 */       return 
/* 1181 */         (ServerMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerMessages parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1187 */       return 
/* 1188 */         (ServerMessages)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static ServerMessages parseFrom(CodedInputStream input) throws IOException {
/* 1193 */       return 
/* 1194 */         (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static ServerMessages parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1200 */       return 
/* 1201 */         (ServerMessages)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1205 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1207 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(ServerMessages prototype) {
/* 1210 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1214 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1215 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1221 */       Builder builder = new Builder(parent);
/* 1222 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements Mysqlx.ServerMessagesOrBuilder
/*      */     {
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1242 */         return Mysqlx.internal_static_Mysqlx_ServerMessages_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1248 */         return Mysqlx.internal_static_Mysqlx_ServerMessages_fieldAccessorTable
/* 1249 */           .ensureFieldAccessorsInitialized(Mysqlx.ServerMessages.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder() {
/* 1255 */         maybeForceBuilderInitialization();
/*      */       }
/*      */ 
/*      */       
/*      */       private Builder(GeneratedMessageV3.BuilderParent parent) {
/* 1260 */         super(parent);
/* 1261 */         maybeForceBuilderInitialization();
/*      */       }
/*      */       
/*      */       private void maybeForceBuilderInitialization() {
/* 1265 */         if (Mysqlx.ServerMessages.alwaysUseFieldBuilders);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/* 1270 */         super.clear();
/* 1271 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Descriptors.Descriptor getDescriptorForType() {
/* 1277 */         return Mysqlx.internal_static_Mysqlx_ServerMessages_descriptor;
/*      */       }
/*      */ 
/*      */       
/*      */       public Mysqlx.ServerMessages getDefaultInstanceForType() {
/* 1282 */         return Mysqlx.ServerMessages.getDefaultInstance();
/*      */       }
/*      */ 
/*      */       
/*      */       public Mysqlx.ServerMessages build() {
/* 1287 */         Mysqlx.ServerMessages result = buildPartial();
/* 1288 */         if (!result.isInitialized()) {
/* 1289 */           throw newUninitializedMessageException(result);
/*      */         }
/* 1291 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Mysqlx.ServerMessages buildPartial() {
/* 1296 */         Mysqlx.ServerMessages result = new Mysqlx.ServerMessages(this);
/* 1297 */         onBuilt();
/* 1298 */         return result;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clone() {
/* 1303 */         return (Builder)super.clone();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) {
/* 1309 */         return (Builder)super.setField(field, value);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) {
/* 1314 */         return (Builder)super.clearField(field);
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
/* 1319 */         return (Builder)super.clearOneof(oneof);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
/* 1325 */         return (Builder)super.setRepeatedField(field, index, value);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
/* 1331 */         return (Builder)super.addRepeatedField(field, value);
/*      */       }
/*      */       
/*      */       public Builder mergeFrom(Message other) {
/* 1335 */         if (other instanceof Mysqlx.ServerMessages) {
/* 1336 */           return mergeFrom((Mysqlx.ServerMessages)other);
/*      */         }
/* 1338 */         super.mergeFrom(other);
/* 1339 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(Mysqlx.ServerMessages other) {
/* 1344 */         if (other == Mysqlx.ServerMessages.getDefaultInstance()) return this; 
/* 1345 */         mergeUnknownFields(other.unknownFields);
/* 1346 */         onChanged();
/* 1347 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final boolean isInitialized() {
/* 1352 */         return true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1360 */         Mysqlx.ServerMessages parsedMessage = null;
/*      */         try {
/* 1362 */           parsedMessage = (Mysqlx.ServerMessages)Mysqlx.ServerMessages.PARSER.parsePartialFrom(input, extensionRegistry);
/* 1363 */         } catch (InvalidProtocolBufferException e) {
/* 1364 */           parsedMessage = (Mysqlx.ServerMessages)e.getUnfinishedMessage();
/* 1365 */           throw e.unwrapIOException();
/*      */         } finally {
/* 1367 */           if (parsedMessage != null) {
/* 1368 */             mergeFrom(parsedMessage);
/*      */           }
/*      */         } 
/* 1371 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1376 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1382 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1392 */     private static final ServerMessages DEFAULT_INSTANCE = new ServerMessages();
/*      */ 
/*      */     
/*      */     public static ServerMessages getDefaultInstance() {
/* 1396 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 1400 */     public static final Parser<ServerMessages> PARSER = (Parser<ServerMessages>)new AbstractParser<ServerMessages>()
/*      */       {
/*      */ 
/*      */         
/*      */         public Mysqlx.ServerMessages parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 1406 */           return new Mysqlx.ServerMessages(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<ServerMessages> parser() {
/* 1411 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<ServerMessages> getParserForType() {
/* 1416 */       return PARSER;
/*      */     }
/*      */     
/*      */     public ServerMessages getDefaultInstanceForType()
/*      */     {
/* 1421 */       return DEFAULT_INSTANCE;
/*      */     } } public enum Type implements ProtocolMessageEnum { OK(0), ERROR(1), CONN_CAPABILITIES(2), SESS_AUTHENTICATE_CONTINUE(3), SESS_AUTHENTICATE_OK(4), NOTICE(11), RESULTSET_COLUMN_META_DATA(12), RESULTSET_ROW(13), RESULTSET_FETCH_DONE(14), RESULTSET_FETCH_SUSPENDED(15), RESULTSET_FETCH_DONE_MORE_RESULTSETS(16), SQL_STMT_EXECUTE_OK(17), RESULTSET_FETCH_DONE_MORE_OUT_PARAMS(18), COMPRESSION(19); public static final int OK_VALUE = 0; public static final int ERROR_VALUE = 1; public static final int CONN_CAPABILITIES_VALUE = 2; public static final int SESS_AUTHENTICATE_CONTINUE_VALUE = 3; public static final int SESS_AUTHENTICATE_OK_VALUE = 4; public static final int NOTICE_VALUE = 11; public static final int RESULTSET_COLUMN_META_DATA_VALUE = 12; public static final int RESULTSET_ROW_VALUE = 13; public static final int RESULTSET_FETCH_DONE_VALUE = 14; public static final int RESULTSET_FETCH_SUSPENDED_VALUE = 15; public static final int RESULTSET_FETCH_DONE_MORE_RESULTSETS_VALUE = 16; public static final int SQL_STMT_EXECUTE_OK_VALUE = 17; public static final int RESULTSET_FETCH_DONE_MORE_OUT_PARAMS_VALUE = 18; public static final int COMPRESSION_VALUE = 19; private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { public Mysqlx.ServerMessages.Type findValueByNumber(int number) { return Mysqlx.ServerMessages.Type.forNumber(number); } }
/*      */     ; private static final Type[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Type forNumber(int value) { switch (value) { case 0: return OK;
/*      */         case 1:
/*      */           return ERROR;
/*      */         case 2:
/*      */           return CONN_CAPABILITIES;
/*      */         case 3:
/*      */           return SESS_AUTHENTICATE_CONTINUE;
/*      */         case 4:
/*      */           return SESS_AUTHENTICATE_OK;
/*      */         case 11:
/*      */           return NOTICE;
/*      */         case 12:
/*      */           return RESULTSET_COLUMN_META_DATA;
/*      */         case 13:
/*      */           return RESULTSET_ROW;
/*      */         case 14:
/*      */           return RESULTSET_FETCH_DONE;
/*      */         case 15:
/*      */           return RESULTSET_FETCH_SUSPENDED;
/*      */         case 16:
/*      */           return RESULTSET_FETCH_DONE_MORE_RESULTSETS;
/*      */         case 17:
/*      */           return SQL_STMT_EXECUTE_OK;
/*      */         case 18:
/*      */           return RESULTSET_FETCH_DONE_MORE_OUT_PARAMS;
/*      */         case 19:
/*      */           return COMPRESSION; }  return null; }
/*      */     public static Internal.EnumLiteMap<Type> internalGetValueMap() { return internalValueMap; }
/*      */     static {  }
/*      */     public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); }
/*      */     public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); }
/*      */     public static final Descriptors.EnumDescriptor getDescriptor() { return Mysqlx.ServerMessages.getDescriptor().getEnumTypes().get(0); }
/*      */     Type(int value) { this.value = value; } }
/*      */   public static final class Ok extends GeneratedMessageV3 implements OkOrBuilder { private static final long serialVersionUID = 0L; private int bitField0_; public static final int MSG_FIELD_NUMBER = 1; private volatile Object msg_; private byte memoizedIsInitialized;
/* 1457 */     private Ok(GeneratedMessageV3.Builder<?> builder) { super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1578 */       this.memoizedIsInitialized = -1; } private Ok() { this.memoizedIsInitialized = -1; this.msg_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Ok(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Ok(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { ByteString bs; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 10: bs = input.readBytes(); this.bitField0_ |= 0x1; this.msg_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return Mysqlx.internal_static_Mysqlx_Ok_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return Mysqlx.internal_static_Mysqlx_Ok_fieldAccessorTable.ensureFieldAccessorsInitialized(Ok.class, Builder.class); } public boolean hasMsg() { return ((this.bitField0_ & 0x1) != 0); }
/*      */     public String getMsg() { Object ref = this.msg_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.msg_ = s;  return s; }
/*      */     public ByteString getMsgBytes() { Object ref = this.msg_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.msg_ = b; return b; }  return (ByteString)ref; }
/* 1581 */     public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 1582 */       if (isInitialized == 1) return true; 
/* 1583 */       if (isInitialized == 0) return false;
/*      */       
/* 1585 */       this.memoizedIsInitialized = 1;
/* 1586 */       return true; }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(CodedOutputStream output) throws IOException {
/* 1592 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1593 */         GeneratedMessageV3.writeString(output, 1, this.msg_);
/*      */       }
/* 1595 */       this.unknownFields.writeTo(output);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 1600 */       int size = this.memoizedSize;
/* 1601 */       if (size != -1) return size;
/*      */       
/* 1603 */       size = 0;
/* 1604 */       if ((this.bitField0_ & 0x1) != 0) {
/* 1605 */         size += GeneratedMessageV3.computeStringSize(1, this.msg_);
/*      */       }
/* 1607 */       size += this.unknownFields.getSerializedSize();
/* 1608 */       this.memoizedSize = size;
/* 1609 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 1614 */       if (obj == this) {
/* 1615 */         return true;
/*      */       }
/* 1617 */       if (!(obj instanceof Ok)) {
/* 1618 */         return super.equals(obj);
/*      */       }
/* 1620 */       Ok other = (Ok)obj;
/*      */       
/* 1622 */       if (hasMsg() != other.hasMsg()) return false; 
/* 1623 */       if (hasMsg() && 
/*      */         
/* 1625 */         !getMsg().equals(other.getMsg())) return false;
/*      */       
/* 1627 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 1628 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 1633 */       if (this.memoizedHashCode != 0) {
/* 1634 */         return this.memoizedHashCode;
/*      */       }
/* 1636 */       int hash = 41;
/* 1637 */       hash = 19 * hash + getDescriptor().hashCode();
/* 1638 */       if (hasMsg()) {
/* 1639 */         hash = 37 * hash + 1;
/* 1640 */         hash = 53 * hash + getMsg().hashCode();
/*      */       } 
/* 1642 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 1643 */       this.memoizedHashCode = hash;
/* 1644 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Ok parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 1650 */       return (Ok)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Ok parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1656 */       return (Ok)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Ok parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 1661 */       return (Ok)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Ok parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1667 */       return (Ok)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Ok parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 1671 */       return (Ok)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Ok parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1677 */       return (Ok)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Ok parseFrom(InputStream input) throws IOException {
/* 1681 */       return 
/* 1682 */         (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Ok parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1688 */       return 
/* 1689 */         (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Ok parseDelimitedFrom(InputStream input) throws IOException {
/* 1693 */       return 
/* 1694 */         (Ok)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Ok parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1700 */       return 
/* 1701 */         (Ok)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Ok parseFrom(CodedInputStream input) throws IOException {
/* 1706 */       return 
/* 1707 */         (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Ok parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 1713 */       return 
/* 1714 */         (Ok)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 1718 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 1720 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Ok prototype) {
/* 1723 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 1727 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 1728 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 1734 */       Builder builder = new Builder(parent);
/* 1735 */       return builder;
/*      */     }
/*      */     
/*      */     public static final class Builder
/*      */       extends GeneratedMessageV3.Builder<Builder>
/*      */       implements Mysqlx.OkOrBuilder
/*      */     {
/*      */       private int bitField0_;
/*      */       private Object msg_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 1746 */         return Mysqlx.internal_static_Mysqlx_Ok_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 1752 */         return Mysqlx.internal_static_Mysqlx_Ok_fieldAccessorTable
/* 1753 */           .ensureFieldAccessorsInitialized(Mysqlx.Ok.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder()
/*      */       {
/* 1893 */         this.msg_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.msg_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (Mysqlx.Ok.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.msg_ = ""; this.bitField0_ &= 0xFFFFFFFE; return this; } public Descriptors.Descriptor getDescriptorForType() { return Mysqlx.internal_static_Mysqlx_Ok_descriptor; } public Mysqlx.Ok getDefaultInstanceForType() { return Mysqlx.Ok.getDefaultInstance(); } public Mysqlx.Ok build() { Mysqlx.Ok result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public Mysqlx.Ok buildPartial() { Mysqlx.Ok result = new Mysqlx.Ok(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.msg_ = this.msg_; result.bitField0_ = to_bitField0_; onBuilt(); return result; } public Builder clone() { return (Builder)super.clone(); } public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); } public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); } public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); } public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof Mysqlx.Ok) return mergeFrom((Mysqlx.Ok)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(Mysqlx.Ok other) { if (other == Mysqlx.Ok.getDefaultInstance()) return this;  if (other.hasMsg()) { this.bitField0_ |= 0x1; this.msg_ = other.msg_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { return true; }
/*      */       public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { Mysqlx.Ok parsedMessage = null; try { parsedMessage = (Mysqlx.Ok)Mysqlx.Ok.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (Mysqlx.Ok)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; }
/* 1899 */       public boolean hasMsg() { return ((this.bitField0_ & 0x1) != 0); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public String getMsg() {
/* 1906 */         Object ref = this.msg_;
/* 1907 */         if (!(ref instanceof String)) {
/* 1908 */           ByteString bs = (ByteString)ref;
/*      */           
/* 1910 */           String s = bs.toStringUtf8();
/* 1911 */           if (bs.isValidUtf8()) {
/* 1912 */             this.msg_ = s;
/*      */           }
/* 1914 */           return s;
/*      */         } 
/* 1916 */         return (String)ref;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ByteString getMsgBytes() {
/* 1925 */         Object ref = this.msg_;
/* 1926 */         if (ref instanceof String) {
/*      */           
/* 1928 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*      */           
/* 1930 */           this.msg_ = b;
/* 1931 */           return b;
/*      */         } 
/* 1933 */         return (ByteString)ref;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setMsg(String value) {
/* 1943 */         if (value == null) {
/* 1944 */           throw new NullPointerException();
/*      */         }
/* 1946 */         this.bitField0_ |= 0x1;
/* 1947 */         this.msg_ = value;
/* 1948 */         onChanged();
/* 1949 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearMsg() {
/* 1956 */         this.bitField0_ &= 0xFFFFFFFE;
/* 1957 */         this.msg_ = Mysqlx.Ok.getDefaultInstance().getMsg();
/* 1958 */         onChanged();
/* 1959 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setMsgBytes(ByteString value) {
/* 1968 */         if (value == null) {
/* 1969 */           throw new NullPointerException();
/*      */         }
/* 1971 */         this.bitField0_ |= 0x1;
/* 1972 */         this.msg_ = value;
/* 1973 */         onChanged();
/* 1974 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 1979 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 1985 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1995 */     private static final Ok DEFAULT_INSTANCE = new Ok();
/*      */ 
/*      */     
/*      */     public static Ok getDefaultInstance() {
/* 1999 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 2003 */     public static final Parser<Ok> PARSER = (Parser<Ok>)new AbstractParser<Ok>()
/*      */       {
/*      */ 
/*      */         
/*      */         public Mysqlx.Ok parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 2009 */           return new Mysqlx.Ok(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Ok> parser() {
/* 2014 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Ok> getParserForType() {
/* 2019 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Ok getDefaultInstanceForType() {
/* 2024 */       return DEFAULT_INSTANCE;
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Error
/*      */     extends GeneratedMessageV3
/*      */     implements ErrorOrBuilder
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int bitField0_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int SEVERITY_FIELD_NUMBER = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int severity_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int CODE_FIELD_NUMBER = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int code_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int SQL_STATE_FIELD_NUMBER = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile Object sqlState_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int MSG_FIELD_NUMBER = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private volatile Object msg_;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private byte memoizedIsInitialized;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Error(GeneratedMessageV3.Builder<?> builder)
/*      */     {
/* 2139 */       super(builder);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2502 */       this.memoizedIsInitialized = -1; } private Error() { this.memoizedIsInitialized = -1; this.severity_ = 0; this.sqlState_ = ""; this.msg_ = ""; } protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) { return new Error(); } public final UnknownFieldSet getUnknownFields() { return this.unknownFields; } private Error(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException { this(); if (extensionRegistry == null) throw new NullPointerException();  int mutable_bitField0_ = 0; UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(); try { boolean done = false; while (!done) { int rawValue; ByteString bs; Severity value; int tag = input.readTag(); switch (tag) { case 0: done = true; continue;case 8: rawValue = input.readEnum(); value = Severity.valueOf(rawValue); if (value == null) { unknownFields.mergeVarintField(1, rawValue); continue; }  this.bitField0_ |= 0x1; this.severity_ = rawValue; continue;case 16: this.bitField0_ |= 0x2; this.code_ = input.readUInt32(); continue;case 26: bs = input.readBytes(); this.bitField0_ |= 0x8; this.msg_ = bs; continue;case 34: bs = input.readBytes(); this.bitField0_ |= 0x4; this.sqlState_ = bs; continue; }  if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;  }  } catch (InvalidProtocolBufferException e) { throw e.setUnfinishedMessage(this); } catch (IOException e) { throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this); } finally { this.unknownFields = unknownFields.build(); makeExtensionsImmutable(); }  } public static final Descriptors.Descriptor getDescriptor() { return Mysqlx.internal_static_Mysqlx_Error_descriptor; } protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return Mysqlx.internal_static_Mysqlx_Error_fieldAccessorTable.ensureFieldAccessorsInitialized(Error.class, Builder.class); } public enum Severity implements ProtocolMessageEnum {
/*      */       ERROR(0), FATAL(1); public static final int ERROR_VALUE = 0; public static final int FATAL_VALUE = 1; private static final Internal.EnumLiteMap<Severity> internalValueMap = new Internal.EnumLiteMap<Severity>() { public Mysqlx.Error.Severity findValueByNumber(int number) { return Mysqlx.Error.Severity.forNumber(number); } }
/*      */       ; private static final Severity[] VALUES = values(); private final int value; public final int getNumber() { return this.value; } public static Severity forNumber(int value) { switch (value) { case 0: return ERROR;case 1: return FATAL; }  return null; } public static Internal.EnumLiteMap<Severity> internalGetValueMap() { return internalValueMap; } static {  } public final Descriptors.EnumValueDescriptor getValueDescriptor() { return getDescriptor().getValues().get(ordinal()); } public final Descriptors.EnumDescriptor getDescriptorForType() { return getDescriptor(); } public static final Descriptors.EnumDescriptor getDescriptor() { return Mysqlx.Error.getDescriptor().getEnumTypes().get(0); } Severity(int value) { this.value = value; }
/* 2505 */     } public boolean hasSeverity() { return ((this.bitField0_ & 0x1) != 0); } public Severity getSeverity() { Severity result = Severity.valueOf(this.severity_); return (result == null) ? Severity.ERROR : result; } public final boolean isInitialized() { byte isInitialized = this.memoizedIsInitialized;
/* 2506 */       if (isInitialized == 1) return true; 
/* 2507 */       if (isInitialized == 0) return false;
/*      */       
/* 2509 */       if (!hasCode()) {
/* 2510 */         this.memoizedIsInitialized = 0;
/* 2511 */         return false;
/*      */       } 
/* 2513 */       if (!hasSqlState()) {
/* 2514 */         this.memoizedIsInitialized = 0;
/* 2515 */         return false;
/*      */       } 
/* 2517 */       if (!hasMsg()) {
/* 2518 */         this.memoizedIsInitialized = 0;
/* 2519 */         return false;
/*      */       } 
/* 2521 */       this.memoizedIsInitialized = 1;
/* 2522 */       return true; }
/*      */     public boolean hasCode() { return ((this.bitField0_ & 0x2) != 0); }
/*      */     public int getCode() { return this.code_; }
/*      */     public boolean hasSqlState() { return ((this.bitField0_ & 0x4) != 0); }
/*      */     public String getSqlState() { Object ref = this.sqlState_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.sqlState_ = s;  return s; }
/*      */     public ByteString getSqlStateBytes() { Object ref = this.sqlState_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.sqlState_ = b; return b; }  return (ByteString)ref; }
/* 2528 */     public boolean hasMsg() { return ((this.bitField0_ & 0x8) != 0); } public String getMsg() { Object ref = this.msg_; if (ref instanceof String) return (String)ref;  ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.msg_ = s;  return s; } public ByteString getMsgBytes() { Object ref = this.msg_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.msg_ = b; return b; }  return (ByteString)ref; } public void writeTo(CodedOutputStream output) throws IOException { if ((this.bitField0_ & 0x1) != 0) {
/* 2529 */         output.writeEnum(1, this.severity_);
/*      */       }
/* 2531 */       if ((this.bitField0_ & 0x2) != 0) {
/* 2532 */         output.writeUInt32(2, this.code_);
/*      */       }
/* 2534 */       if ((this.bitField0_ & 0x8) != 0) {
/* 2535 */         GeneratedMessageV3.writeString(output, 3, this.msg_);
/*      */       }
/* 2537 */       if ((this.bitField0_ & 0x4) != 0) {
/* 2538 */         GeneratedMessageV3.writeString(output, 4, this.sqlState_);
/*      */       }
/* 2540 */       this.unknownFields.writeTo(output); }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSize() {
/* 2545 */       int size = this.memoizedSize;
/* 2546 */       if (size != -1) return size;
/*      */       
/* 2548 */       size = 0;
/* 2549 */       if ((this.bitField0_ & 0x1) != 0) {
/* 2550 */         size += 
/* 2551 */           CodedOutputStream.computeEnumSize(1, this.severity_);
/*      */       }
/* 2553 */       if ((this.bitField0_ & 0x2) != 0) {
/* 2554 */         size += 
/* 2555 */           CodedOutputStream.computeUInt32Size(2, this.code_);
/*      */       }
/* 2557 */       if ((this.bitField0_ & 0x8) != 0) {
/* 2558 */         size += GeneratedMessageV3.computeStringSize(3, this.msg_);
/*      */       }
/* 2560 */       if ((this.bitField0_ & 0x4) != 0) {
/* 2561 */         size += GeneratedMessageV3.computeStringSize(4, this.sqlState_);
/*      */       }
/* 2563 */       size += this.unknownFields.getSerializedSize();
/* 2564 */       this.memoizedSize = size;
/* 2565 */       return size;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/* 2570 */       if (obj == this) {
/* 2571 */         return true;
/*      */       }
/* 2573 */       if (!(obj instanceof Error)) {
/* 2574 */         return super.equals(obj);
/*      */       }
/* 2576 */       Error other = (Error)obj;
/*      */       
/* 2578 */       if (hasSeverity() != other.hasSeverity()) return false; 
/* 2579 */       if (hasSeverity() && 
/* 2580 */         this.severity_ != other.severity_) return false;
/*      */       
/* 2582 */       if (hasCode() != other.hasCode()) return false; 
/* 2583 */       if (hasCode() && 
/* 2584 */         getCode() != other
/* 2585 */         .getCode()) return false;
/*      */       
/* 2587 */       if (hasSqlState() != other.hasSqlState()) return false; 
/* 2588 */       if (hasSqlState() && 
/*      */         
/* 2590 */         !getSqlState().equals(other.getSqlState())) return false;
/*      */       
/* 2592 */       if (hasMsg() != other.hasMsg()) return false; 
/* 2593 */       if (hasMsg() && 
/*      */         
/* 2595 */         !getMsg().equals(other.getMsg())) return false;
/*      */       
/* 2597 */       if (!this.unknownFields.equals(other.unknownFields)) return false; 
/* 2598 */       return true;
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/* 2603 */       if (this.memoizedHashCode != 0) {
/* 2604 */         return this.memoizedHashCode;
/*      */       }
/* 2606 */       int hash = 41;
/* 2607 */       hash = 19 * hash + getDescriptor().hashCode();
/* 2608 */       if (hasSeverity()) {
/* 2609 */         hash = 37 * hash + 1;
/* 2610 */         hash = 53 * hash + this.severity_;
/*      */       } 
/* 2612 */       if (hasCode()) {
/* 2613 */         hash = 37 * hash + 2;
/* 2614 */         hash = 53 * hash + getCode();
/*      */       } 
/* 2616 */       if (hasSqlState()) {
/* 2617 */         hash = 37 * hash + 4;
/* 2618 */         hash = 53 * hash + getSqlState().hashCode();
/*      */       } 
/* 2620 */       if (hasMsg()) {
/* 2621 */         hash = 37 * hash + 3;
/* 2622 */         hash = 53 * hash + getMsg().hashCode();
/*      */       } 
/* 2624 */       hash = 29 * hash + this.unknownFields.hashCode();
/* 2625 */       this.memoizedHashCode = hash;
/* 2626 */       return hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Error parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
/* 2632 */       return (Error)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Error parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2638 */       return (Error)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Error parseFrom(ByteString data) throws InvalidProtocolBufferException {
/* 2643 */       return (Error)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Error parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2649 */       return (Error)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Error parseFrom(byte[] data) throws InvalidProtocolBufferException {
/* 2653 */       return (Error)PARSER.parseFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Error parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 2659 */       return (Error)PARSER.parseFrom(data, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Error parseFrom(InputStream input) throws IOException {
/* 2663 */       return 
/* 2664 */         (Error)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Error parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2670 */       return 
/* 2671 */         (Error)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public static Error parseDelimitedFrom(InputStream input) throws IOException {
/* 2675 */       return 
/* 2676 */         (Error)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Error parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2682 */       return 
/* 2683 */         (Error)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Error parseFrom(CodedInputStream input) throws IOException {
/* 2688 */       return 
/* 2689 */         (Error)GeneratedMessageV3.parseWithIOException(PARSER, input);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public static Error parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/* 2695 */       return 
/* 2696 */         (Error)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
/*      */     }
/*      */     
/*      */     public Builder newBuilderForType() {
/* 2700 */       return newBuilder();
/*      */     } public static Builder newBuilder() {
/* 2702 */       return DEFAULT_INSTANCE.toBuilder();
/*      */     }
/*      */     public static Builder newBuilder(Error prototype) {
/* 2705 */       return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
/*      */     }
/*      */     
/*      */     public Builder toBuilder() {
/* 2709 */       return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
/* 2710 */         .mergeFrom(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
/* 2716 */       Builder builder = new Builder(parent);
/* 2717 */       return builder;
/*      */     }
/*      */     
/*      */     public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements Mysqlx.ErrorOrBuilder {
/*      */       private int bitField0_;
/*      */       private int severity_;
/*      */       private int code_;
/*      */       private Object sqlState_;
/*      */       private Object msg_;
/*      */       
/*      */       public static final Descriptors.Descriptor getDescriptor() {
/* 2728 */         return Mysqlx.internal_static_Mysqlx_Error_descriptor;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
/* 2734 */         return Mysqlx.internal_static_Mysqlx_Error_fieldAccessorTable
/* 2735 */           .ensureFieldAccessorsInitialized(Mysqlx.Error.class, Builder.class);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private Builder()
/*      */       {
/* 2913 */         this.severity_ = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3024 */         this.sqlState_ = "";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3132 */         this.msg_ = ""; maybeForceBuilderInitialization(); } private Builder(GeneratedMessageV3.BuilderParent parent) { super(parent); this.severity_ = 0; this.sqlState_ = ""; this.msg_ = ""; maybeForceBuilderInitialization(); } private void maybeForceBuilderInitialization() { if (Mysqlx.Error.alwaysUseFieldBuilders); } public Builder clear() { super.clear(); this.severity_ = 0; this.bitField0_ &= 0xFFFFFFFE; this.code_ = 0; this.bitField0_ &= 0xFFFFFFFD; this.sqlState_ = ""; this.bitField0_ &= 0xFFFFFFFB; this.msg_ = ""; this.bitField0_ &= 0xFFFFFFF7; return this; } public Descriptors.Descriptor getDescriptorForType() { return Mysqlx.internal_static_Mysqlx_Error_descriptor; } public Mysqlx.Error getDefaultInstanceForType() { return Mysqlx.Error.getDefaultInstance(); } public Mysqlx.Error build() { Mysqlx.Error result = buildPartial(); if (!result.isInitialized()) throw newUninitializedMessageException(result);  return result; } public Mysqlx.Error buildPartial() { Mysqlx.Error result = new Mysqlx.Error(this); int from_bitField0_ = this.bitField0_; int to_bitField0_ = 0; if ((from_bitField0_ & 0x1) != 0) to_bitField0_ |= 0x1;  result.severity_ = this.severity_; if ((from_bitField0_ & 0x2) != 0) { result.code_ = this.code_; to_bitField0_ |= 0x2; }  if ((from_bitField0_ & 0x4) != 0) to_bitField0_ |= 0x4;  result.sqlState_ = this.sqlState_; if ((from_bitField0_ & 0x8) != 0) to_bitField0_ |= 0x8;  result.msg_ = this.msg_; result.bitField0_ = to_bitField0_; onBuilt(); return result; }
/*      */       public Builder clone() { return (Builder)super.clone(); }
/*      */       public Builder setField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.setField(field, value); }
/*      */       public Builder clearField(Descriptors.FieldDescriptor field) { return (Builder)super.clearField(field); }
/*      */       public Builder clearOneof(Descriptors.OneofDescriptor oneof) { return (Builder)super.clearOneof(oneof); }
/*      */       public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) { return (Builder)super.setRepeatedField(field, index, value); }
/*      */       public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) { return (Builder)super.addRepeatedField(field, value); }
/*      */       public Builder mergeFrom(Message other) { if (other instanceof Mysqlx.Error) return mergeFrom((Mysqlx.Error)other);  super.mergeFrom(other); return this; }
/*      */       public Builder mergeFrom(Mysqlx.Error other) { if (other == Mysqlx.Error.getDefaultInstance()) return this;  if (other.hasSeverity()) setSeverity(other.getSeverity());  if (other.hasCode()) setCode(other.getCode());  if (other.hasSqlState()) { this.bitField0_ |= 0x4; this.sqlState_ = other.sqlState_; onChanged(); }  if (other.hasMsg()) { this.bitField0_ |= 0x8; this.msg_ = other.msg_; onChanged(); }  mergeUnknownFields(other.unknownFields); onChanged(); return this; }
/*      */       public final boolean isInitialized() { if (!hasCode()) return false;  if (!hasSqlState()) return false;  if (!hasMsg()) return false;  return true; }
/* 3142 */       public boolean hasMsg() { return ((this.bitField0_ & 0x8) != 0); } public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException { Mysqlx.Error parsedMessage = null; try { parsedMessage = (Mysqlx.Error)Mysqlx.Error.PARSER.parsePartialFrom(input, extensionRegistry); } catch (InvalidProtocolBufferException e) { parsedMessage = (Mysqlx.Error)e.getUnfinishedMessage(); throw e.unwrapIOException(); } finally { if (parsedMessage != null) mergeFrom(parsedMessage);  }  return this; } public boolean hasSeverity() { return ((this.bitField0_ & 0x1) != 0); } public Mysqlx.Error.Severity getSeverity() { Mysqlx.Error.Severity result = Mysqlx.Error.Severity.valueOf(this.severity_); return (result == null) ? Mysqlx.Error.Severity.ERROR : result; } public Builder setSeverity(Mysqlx.Error.Severity value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x1; this.severity_ = value.getNumber(); onChanged(); return this; } public Builder clearSeverity() { this.bitField0_ &= 0xFFFFFFFE; this.severity_ = 0; onChanged(); return this; }
/*      */       public boolean hasCode() { return ((this.bitField0_ & 0x2) != 0); }
/*      */       public int getCode() { return this.code_; }
/*      */       public Builder setCode(int value) { this.bitField0_ |= 0x2; this.code_ = value; onChanged(); return this; }
/*      */       public Builder clearCode() { this.bitField0_ &= 0xFFFFFFFD; this.code_ = 0; onChanged(); return this; }
/*      */       public boolean hasSqlState() { return ((this.bitField0_ & 0x4) != 0); }
/*      */       public String getSqlState() { Object ref = this.sqlState_; if (!(ref instanceof String)) { ByteString bs = (ByteString)ref; String s = bs.toStringUtf8(); if (bs.isValidUtf8()) this.sqlState_ = s;  return s; }  return (String)ref; }
/*      */       public ByteString getSqlStateBytes() { Object ref = this.sqlState_; if (ref instanceof String) { ByteString b = ByteString.copyFromUtf8((String)ref); this.sqlState_ = b; return b; }  return (ByteString)ref; }
/*      */       public Builder setSqlState(String value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.sqlState_ = value; onChanged(); return this; }
/*      */       public Builder clearSqlState() { this.bitField0_ &= 0xFFFFFFFB; this.sqlState_ = Mysqlx.Error.getDefaultInstance().getSqlState(); onChanged(); return this; }
/*      */       public Builder setSqlStateBytes(ByteString value) { if (value == null) throw new NullPointerException();  this.bitField0_ |= 0x4; this.sqlState_ = value; onChanged(); return this; }
/* 3153 */       public String getMsg() { Object ref = this.msg_;
/* 3154 */         if (!(ref instanceof String)) {
/* 3155 */           ByteString bs = (ByteString)ref;
/*      */           
/* 3157 */           String s = bs.toStringUtf8();
/* 3158 */           if (bs.isValidUtf8()) {
/* 3159 */             this.msg_ = s;
/*      */           }
/* 3161 */           return s;
/*      */         } 
/* 3163 */         return (String)ref; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ByteString getMsgBytes() {
/* 3176 */         Object ref = this.msg_;
/* 3177 */         if (ref instanceof String) {
/*      */           
/* 3179 */           ByteString b = ByteString.copyFromUtf8((String)ref);
/*      */           
/* 3181 */           this.msg_ = b;
/* 3182 */           return b;
/*      */         } 
/* 3184 */         return (ByteString)ref;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setMsg(String value) {
/* 3198 */         if (value == null) {
/* 3199 */           throw new NullPointerException();
/*      */         }
/* 3201 */         this.bitField0_ |= 0x8;
/* 3202 */         this.msg_ = value;
/* 3203 */         onChanged();
/* 3204 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder clearMsg() {
/* 3215 */         this.bitField0_ &= 0xFFFFFFF7;
/* 3216 */         this.msg_ = Mysqlx.Error.getDefaultInstance().getMsg();
/* 3217 */         onChanged();
/* 3218 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder setMsgBytes(ByteString value) {
/* 3231 */         if (value == null) {
/* 3232 */           throw new NullPointerException();
/*      */         }
/* 3234 */         this.bitField0_ |= 0x8;
/* 3235 */         this.msg_ = value;
/* 3236 */         onChanged();
/* 3237 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
/* 3242 */         return (Builder)super.setUnknownFields(unknownFields);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
/* 3248 */         return (Builder)super.mergeUnknownFields(unknownFields);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3258 */     private static final Error DEFAULT_INSTANCE = new Error();
/*      */ 
/*      */     
/*      */     public static Error getDefaultInstance() {
/* 3262 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */     
/*      */     @Deprecated
/* 3266 */     public static final Parser<Error> PARSER = (Parser<Error>)new AbstractParser<Error>()
/*      */       {
/*      */ 
/*      */         
/*      */         public Mysqlx.Error parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException
/*      */         {
/* 3272 */           return new Mysqlx.Error(input, extensionRegistry);
/*      */         }
/*      */       };
/*      */     
/*      */     public static Parser<Error> parser() {
/* 3277 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Parser<Error> getParserForType() {
/* 3282 */       return PARSER;
/*      */     }
/*      */ 
/*      */     
/*      */     public Error getDefaultInstanceForType() {
/* 3287 */       return DEFAULT_INSTANCE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 3300 */   public static final GeneratedMessage.GeneratedExtension<DescriptorProtos.MessageOptions, ClientMessages.Type> clientMessageId = GeneratedMessage.newFileScopedGeneratedExtension(ClientMessages.Type.class, null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SERVER_MESSAGE_ID_FIELD_NUMBER = 100002;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 3311 */   public static final GeneratedMessage.GeneratedExtension<DescriptorProtos.MessageOptions, ServerMessages.Type> serverMessageId = GeneratedMessage.newFileScopedGeneratedExtension(ServerMessages.Type.class, null);
/*      */ 
/*      */   
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_ClientMessages_descriptor;
/*      */ 
/*      */   
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_ClientMessages_fieldAccessorTable;
/*      */ 
/*      */   
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_ServerMessages_descriptor;
/*      */ 
/*      */   
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_ServerMessages_fieldAccessorTable;
/*      */   
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Ok_descriptor;
/*      */   
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Ok_fieldAccessorTable;
/*      */   
/*      */   private static final Descriptors.Descriptor internal_static_Mysqlx_Error_descriptor;
/*      */   
/*      */   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Error_fieldAccessorTable;
/*      */   
/*      */   private static Descriptors.FileDescriptor descriptor;
/*      */ 
/*      */   
/*      */   public static Descriptors.FileDescriptor getDescriptor() {
/* 3337 */     return descriptor;
/*      */   }
/*      */ 
/*      */   
/*      */   static {
/* 3342 */     String[] descriptorData = { "\n\fmysqlx.proto\022\006Mysqlx\032 google/protobuf/descriptor.proto\"ü\003\n\016ClientMessages\"é\003\n\004Type\022\030\n\024CON_CAPABILITIES_GET\020\001\022\030\n\024CON_CAPABILITIES_SET\020\002\022\r\n\tCON_CLOSE\020\003\022\033\n\027SESS_AUTHENTICATE_START\020\004\022\036\n\032SESS_AUTHENTICATE_CONTINUE\020\005\022\016\n\nSESS_RESET\020\006\022\016\n\nSESS_CLOSE\020\007\022\024\n\020SQL_STMT_EXECUTE\020\f\022\r\n\tCRUD_FIND\020\021\022\017\n\013CRUD_INSERT\020\022\022\017\n\013CRUD_UPDATE\020\023\022\017\n\013CRUD_DELETE\020\024\022\017\n\013EXPECT_OPEN\020\030\022\020\n\fEXPECT_CLOSE\020\031\022\024\n\020CRUD_CREATE_VIEW\020\036\022\024\n\020CRUD_MODIFY_VIEW\020\037\022\022\n\016CRUD_DROP_VIEW\020 \022\023\n\017PREPARE_PREPARE\020(\022\023\n\017PREPARE_EXECUTE\020)\022\026\n\022PREPARE_DEALLOCATE\020*\022\017\n\013CURSOR_OPEN\020+\022\020\n\fCURSOR_CLOSE\020,\022\020\n\fCURSOR_FETCH\020-\022\017\n\013COMPRESSION\020.\"ó\002\n\016ServerMessages\"à\002\n\004Type\022\006\n\002OK\020\000\022\t\n\005ERROR\020\001\022\025\n\021CONN_CAPABILITIES\020\002\022\036\n\032SESS_AUTHENTICATE_CONTINUE\020\003\022\030\n\024SESS_AUTHENTICATE_OK\020\004\022\n\n\006NOTICE\020\013\022\036\n\032RESULTSET_COLUMN_META_DATA\020\f\022\021\n\rRESULTSET_ROW\020\r\022\030\n\024RESULTSET_FETCH_DONE\020\016\022\035\n\031RESULTSET_FETCH_SUSPENDED\020\017\022(\n$RESULTSET_FETCH_DONE_MORE_RESULTSETS\020\020\022\027\n\023SQL_STMT_EXECUTE_OK\020\021\022(\n$RESULTSET_FETCH_DONE_MORE_OUT_PARAMS\020\022\022\017\n\013COMPRESSION\020\023\"\027\n\002Ok\022\013\n\003msg\030\001 \001(\t:\004ê0\000\"\001\n\005Error\022/\n\bseverity\030\001 \001(\0162\026.Mysqlx.Error.Severity:\005ERROR\022\f\n\004code\030\002 \002(\r\022\021\n\tsql_state\030\004 \002(\t\022\013\n\003msg\030\003 \002(\t\" \n\bSeverity\022\t\n\005ERROR\020\000\022\t\n\005FATAL\020\001:\004ê0\001:Y\n\021client_message_id\022\037.google.protobuf.MessageOptions\030¡\006 \001(\0162\033.Mysqlx.ClientMessages.Type:Y\n\021server_message_id\022\037.google.protobuf.MessageOptions\030¢\006 \001(\0162\033.Mysqlx.ServerMessages.TypeB\031\n\027com.mysql.cj.x.protobuf" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3378 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*      */           
/* 3380 */           DescriptorProtos.getDescriptor()
/*      */         });
/*      */     
/* 3383 */     internal_static_Mysqlx_ClientMessages_descriptor = getDescriptor().getMessageTypes().get(0);
/* 3384 */     internal_static_Mysqlx_ClientMessages_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_ClientMessages_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3389 */     internal_static_Mysqlx_ServerMessages_descriptor = getDescriptor().getMessageTypes().get(1);
/* 3390 */     internal_static_Mysqlx_ServerMessages_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_ServerMessages_descriptor, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3395 */     internal_static_Mysqlx_Ok_descriptor = getDescriptor().getMessageTypes().get(2);
/* 3396 */     internal_static_Mysqlx_Ok_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Ok_descriptor, new String[] { "Msg" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3401 */     internal_static_Mysqlx_Error_descriptor = getDescriptor().getMessageTypes().get(3);
/* 3402 */     internal_static_Mysqlx_Error_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Error_descriptor, new String[] { "Severity", "Code", "SqlState", "Msg" });
/*      */ 
/*      */ 
/*      */     
/* 3406 */     clientMessageId.internalInit(descriptor.getExtensions().get(0));
/* 3407 */     serverMessageId.internalInit(descriptor.getExtensions().get(1));
/*      */     
/* 3409 */     ExtensionRegistry registry = ExtensionRegistry.newInstance();
/* 3410 */     registry.add(serverMessageId);
/*      */     
/* 3412 */     Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
/* 3413 */     DescriptorProtos.getDescriptor();
/*      */   }
/*      */   
/*      */   public static interface ErrorOrBuilder extends MessageOrBuilder {
/*      */     boolean hasSeverity();
/*      */     
/*      */     Mysqlx.Error.Severity getSeverity();
/*      */     
/*      */     boolean hasCode();
/*      */     
/*      */     int getCode();
/*      */     
/*      */     boolean hasSqlState();
/*      */     
/*      */     String getSqlState();
/*      */     
/*      */     ByteString getSqlStateBytes();
/*      */     
/*      */     boolean hasMsg();
/*      */     
/*      */     String getMsg();
/*      */     
/*      */     ByteString getMsgBytes();
/*      */   }
/*      */   
/*      */   public static interface OkOrBuilder extends MessageOrBuilder {
/*      */     boolean hasMsg();
/*      */     
/*      */     String getMsg();
/*      */     
/*      */     ByteString getMsgBytes();
/*      */   }
/*      */   
/*      */   public static interface ServerMessagesOrBuilder extends MessageOrBuilder {}
/*      */   
/*      */   public static interface ClientMessagesOrBuilder extends MessageOrBuilder {}
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\x\protobuf\Mysqlx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */