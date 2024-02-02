package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxExpr {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Expr_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Expr_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Identifier_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_FunctionCall_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Operator_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Operator_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Object_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Object_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable;
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Array_descriptor;
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Array_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxExpr() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      String[] descriptorData = new String[]{"\n\u0011mysqlx_expr.proto\u0012\u000bMysqlx.Expr\u001a\u0016mysqlx_datatypes.proto\"Ä\u0003\n\u0004Expr\u0012$\n\u0004type\u0018\u0001 \u0002(\u000e2\u0016.Mysqlx.Expr.Expr.Type\u00121\n\nidentifier\u0018\u0002 \u0001(\u000b2\u001d.Mysqlx.Expr.ColumnIdentifier\u0012\u0010\n\bvariable\u0018\u0003 \u0001(\t\u0012)\n\u0007literal\u0018\u0004 \u0001(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u00120\n\rfunction_call\u0018\u0005 \u0001(\u000b2\u0019.Mysqlx.Expr.FunctionCall\u0012'\n\boperator\u0018\u0006 \u0001(\u000b2\u0015.Mysqlx.Expr.Operator\u0012\u0010\n\bposition\u0018\u0007 \u0001(\r\u0012#\n\u0006object\u0018\b \u0001(\u000b2\u0013.Mysqlx.Expr.Object\u0012!\n\u0005array\u0018\t \u0001(\u000b2\u0012.Mysqlx.Expr.Array\"q\n\u0004Type\u0012\t\n\u0005IDENT\u0010\u0001\u0012\u000b\n\u0007LITERAL\u0010\u0002\u0012\f\n\bVARIABLE\u0010\u0003\u0012\r\n\tFUNC_CALL\u0010\u0004\u0012\f\n\bOPERATOR\u0010\u0005\u0012\u000f\n\u000bPLACEHOLDER\u0010\u0006\u0012\n\n\u0006OBJECT\u0010\u0007\u0012\t\n\u0005ARRAY\u0010\b\"/\n\nIdentifier\u0012\f\n\u0004name\u0018\u0001 \u0002(\t\u0012\u0013\n\u000bschema_name\u0018\u0002 \u0001(\t\"Ë\u0001\n\u0010DocumentPathItem\u00120\n\u0004type\u0018\u0001 \u0002(\u000e2\".Mysqlx.Expr.DocumentPathItem.Type\u0012\r\n\u0005value\u0018\u0002 \u0001(\t\u0012\r\n\u0005index\u0018\u0003 \u0001(\r\"g\n\u0004Type\u0012\n\n\u0006MEMBER\u0010\u0001\u0012\u0013\n\u000fMEMBER_ASTERISK\u0010\u0002\u0012\u000f\n\u000bARRAY_INDEX\u0010\u0003\u0012\u0018\n\u0014ARRAY_INDEX_ASTERISK\u0010\u0004\u0012\u0013\n\u000fDOUBLE_ASTERISK\u0010\u0005\"\u007f\n\u0010ColumnIdentifier\u00124\n\rdocument_path\u0018\u0001 \u0003(\u000b2\u001d.Mysqlx.Expr.DocumentPathItem\u0012\f\n\u0004name\u0018\u0002 \u0001(\t\u0012\u0012\n\ntable_name\u0018\u0003 \u0001(\t\u0012\u0013\n\u000bschema_name\u0018\u0004 \u0001(\t\"W\n\fFunctionCall\u0012%\n\u0004name\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Expr.Identifier\u0012 \n\u0005param\u0018\u0002 \u0003(\u000b2\u0011.Mysqlx.Expr.Expr\":\n\bOperator\u0012\f\n\u0004name\u0018\u0001 \u0002(\t\u0012 \n\u0005param\u0018\u0002 \u0003(\u000b2\u0011.Mysqlx.Expr.Expr\"t\n\u0006Object\u0012,\n\u0003fld\u0018\u0001 \u0003(\u000b2\u001f.Mysqlx.Expr.Object.ObjectField\u001a<\n\u000bObjectField\u0012\u000b\n\u0003key\u0018\u0001 \u0002(\t\u0012 \n\u0005value\u0018\u0002 \u0002(\u000b2\u0011.Mysqlx.Expr.Expr\")\n\u0005Array\u0012 \n\u0005value\u0018\u0001 \u0003(\u000b2\u0011.Mysqlx.Expr.ExprB\u0019\n\u0017com.mysql.cj.x.protobuf"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{MysqlxDatatypes.getDescriptor()});
      internal_static_Mysqlx_Expr_Expr_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_Mysqlx_Expr_Expr_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Expr_descriptor, new String[]{"Type", "Identifier", "Variable", "Literal", "FunctionCall", "Operator", "Position", "Object", "Array"});
      internal_static_Mysqlx_Expr_Identifier_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Identifier_descriptor, new String[]{"Name", "SchemaName"});
      internal_static_Mysqlx_Expr_DocumentPathItem_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(2);
      internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_DocumentPathItem_descriptor, new String[]{"Type", "Value", "Index"});
      internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(3);
      internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor, new String[]{"DocumentPath", "Name", "TableName", "SchemaName"});
      internal_static_Mysqlx_Expr_FunctionCall_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(4);
      internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_FunctionCall_descriptor, new String[]{"Name", "Param"});
      internal_static_Mysqlx_Expr_Operator_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(5);
      internal_static_Mysqlx_Expr_Operator_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Operator_descriptor, new String[]{"Name", "Param"});
      internal_static_Mysqlx_Expr_Object_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(6);
      internal_static_Mysqlx_Expr_Object_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Object_descriptor, new String[]{"Fld"});
      internal_static_Mysqlx_Expr_Object_ObjectField_descriptor = (Descriptors.Descriptor)internal_static_Mysqlx_Expr_Object_descriptor.getNestedTypes().get(0);
      internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Object_ObjectField_descriptor, new String[]{"Key", "Value"});
      internal_static_Mysqlx_Expr_Array_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(7);
      internal_static_Mysqlx_Expr_Array_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Expr_Array_descriptor, new String[]{"Value"});
      MysqlxDatatypes.getDescriptor();
   }

   public static final class Array extends GeneratedMessageV3 implements ArrayOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int VALUE_FIELD_NUMBER = 1;
      private List<Expr> value_;
      private byte memoizedIsInitialized;
      private static final Array DEFAULT_INSTANCE = new Array();
      /** @deprecated */
      @Deprecated
      public static final Parser<Array> PARSER = new AbstractParser<Array>() {
         public Array parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Array(input, extensionRegistry);
         }
      };

      private Array(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Array() {
         this.memoizedIsInitialized = -1;
         this.value_ = Collections.emptyList();
      }

      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Array();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Array(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        if (!(mutable_bitField0_ & true)) {
                           this.value_ = new ArrayList();
                           mutable_bitField0_ |= true;
                        }

                        this.value_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var11) {
               throw var11.setUnfinishedMessage(this);
            } catch (IOException var12) {
               throw (new InvalidProtocolBufferException(var12)).setUnfinishedMessage(this);
            } finally {
               if (mutable_bitField0_ & true) {
                  this.value_ = Collections.unmodifiableList(this.value_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Array_fieldAccessorTable.ensureFieldAccessorsInitialized(Array.class, Builder.class);
      }

      public List<Expr> getValueList() {
         return this.value_;
      }

      public List<? extends ExprOrBuilder> getValueOrBuilderList() {
         return this.value_;
      }

      public int getValueCount() {
         return this.value_.size();
      }

      public Expr getValue(int index) {
         return (Expr)this.value_.get(index);
      }

      public ExprOrBuilder getValueOrBuilder(int index) {
         return (ExprOrBuilder)this.value_.get(index);
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for(int i = 0; i < this.getValueCount(); ++i) {
               if (!this.getValue(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         for(int i = 0; i < this.value_.size(); ++i) {
            output.writeMessage(1, (MessageLite)this.value_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;

            for(int i = 0; i < this.value_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(1, (MessageLite)this.value_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Array)) {
            return super.equals(obj);
         } else {
            Array other = (Array)obj;
            if (!this.getValueList().equals(other.getValueList())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.getValueCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getValueList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Array parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Array)PARSER.parseFrom(data);
      }

      public static Array parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Array)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Array parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Array)PARSER.parseFrom(data);
      }

      public static Array parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Array)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Array parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Array)PARSER.parseFrom(data);
      }

      public static Array parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Array)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Array parseFrom(InputStream input) throws IOException {
         return (Array)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Array parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Array)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Array parseDelimitedFrom(InputStream input) throws IOException {
         return (Array)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Array parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Array)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Array parseFrom(CodedInputStream input) throws IOException {
         return (Array)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Array parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Array)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Array prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Array getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Array> parser() {
         return PARSER;
      }

      public Parser<Array> getParserForType() {
         return PARSER;
      }

      public Array getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Array(GeneratedMessageV3.Builder x0, java.lang.Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Array(CodedInputStream x0, ExtensionRegistryLite x1, java.lang.Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ArrayOrBuilder {
         private int bitField0_;
         private List<Expr> value_;
         private RepeatedFieldBuilderV3<Expr, Expr.Builder, ExprOrBuilder> valueBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Array_fieldAccessorTable.ensureFieldAccessorsInitialized(Array.class, Builder.class);
         }

         private Builder() {
            this.value_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.value_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Array.alwaysUseFieldBuilders) {
               this.getValueFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.valueBuilder_ == null) {
               this.value_ = Collections.emptyList();
               this.bitField0_ &= -2;
            } else {
               this.valueBuilder_.clear();
            }

            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
         }

         public Array getDefaultInstanceForType() {
            return MysqlxExpr.Array.getDefaultInstance();
         }

         public Array build() {
            Array result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Array buildPartial() {
            Array result = new Array(this);
            int from_bitField0_ = this.bitField0_;
            if (this.valueBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0) {
                  this.value_ = Collections.unmodifiableList(this.value_);
                  this.bitField0_ &= -2;
               }

               result.value_ = this.value_;
            } else {
               result.value_ = this.valueBuilder_.build();
            }

            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Array) {
               return this.mergeFrom((Array)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Array other) {
            if (other == MysqlxExpr.Array.getDefaultInstance()) {
               return this;
            } else {
               if (this.valueBuilder_ == null) {
                  if (!other.value_.isEmpty()) {
                     if (this.value_.isEmpty()) {
                        this.value_ = other.value_;
                        this.bitField0_ &= -2;
                     } else {
                        this.ensureValueIsMutable();
                        this.value_.addAll(other.value_);
                     }

                     this.onChanged();
                  }
               } else if (!other.value_.isEmpty()) {
                  if (this.valueBuilder_.isEmpty()) {
                     this.valueBuilder_.dispose();
                     this.valueBuilder_ = null;
                     this.value_ = other.value_;
                     this.bitField0_ &= -2;
                     this.valueBuilder_ = MysqlxExpr.Array.alwaysUseFieldBuilders ? this.getValueFieldBuilder() : null;
                  } else {
                     this.valueBuilder_.addAllMessages(other.value_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            for(int i = 0; i < this.getValueCount(); ++i) {
               if (!this.getValue(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Array parsedMessage = null;

            try {
               parsedMessage = (Array)MysqlxExpr.Array.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Array)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         private void ensureValueIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.value_ = new ArrayList(this.value_);
               this.bitField0_ |= 1;
            }

         }

         public List<Expr> getValueList() {
            return this.valueBuilder_ == null ? Collections.unmodifiableList(this.value_) : this.valueBuilder_.getMessageList();
         }

         public int getValueCount() {
            return this.valueBuilder_ == null ? this.value_.size() : this.valueBuilder_.getCount();
         }

         public Expr getValue(int index) {
            return this.valueBuilder_ == null ? (Expr)this.value_.get(index) : (Expr)this.valueBuilder_.getMessage(index);
         }

         public Builder setValue(int index, Expr value) {
            if (this.valueBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureValueIsMutable();
               this.value_.set(index, value);
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setValue(int index, Expr.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addValue(Expr value) {
            if (this.valueBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureValueIsMutable();
               this.value_.add(value);
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addValue(int index, Expr value) {
            if (this.valueBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureValueIsMutable();
               this.value_.add(index, value);
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addValue(Expr.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addValue(int index, Expr.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllValue(Iterable<? extends Expr> values) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.value_);
               this.onChanged();
            } else {
               this.valueBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearValue() {
            if (this.valueBuilder_ == null) {
               this.value_ = Collections.emptyList();
               this.bitField0_ &= -2;
               this.onChanged();
            } else {
               this.valueBuilder_.clear();
            }

            return this;
         }

         public Builder removeValue(int index) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.remove(index);
               this.onChanged();
            } else {
               this.valueBuilder_.remove(index);
            }

            return this;
         }

         public Expr.Builder getValueBuilder(int index) {
            return (Expr.Builder)this.getValueFieldBuilder().getBuilder(index);
         }

         public ExprOrBuilder getValueOrBuilder(int index) {
            return this.valueBuilder_ == null ? (ExprOrBuilder)this.value_.get(index) : (ExprOrBuilder)this.valueBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends ExprOrBuilder> getValueOrBuilderList() {
            return this.valueBuilder_ != null ? this.valueBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.value_);
         }

         public Expr.Builder addValueBuilder() {
            return (Expr.Builder)this.getValueFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
         }

         public Expr.Builder addValueBuilder(int index) {
            return (Expr.Builder)this.getValueFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
         }

         public List<Expr.Builder> getValueBuilderList() {
            return this.getValueFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Expr, Expr.Builder, ExprOrBuilder> getValueFieldBuilder() {
            if (this.valueBuilder_ == null) {
               this.valueBuilder_ = new RepeatedFieldBuilderV3(this.value_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
               this.value_ = null;
            }

            return this.valueBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(java.lang.Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, java.lang.Object x1) {
            this(x0);
         }
      }
   }

   public interface ArrayOrBuilder extends MessageOrBuilder {
      List<Expr> getValueList();

      Expr getValue(int var1);

      int getValueCount();

      List<? extends ExprOrBuilder> getValueOrBuilderList();

      ExprOrBuilder getValueOrBuilder(int var1);
   }

   public static final class Object extends GeneratedMessageV3 implements ObjectOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int FLD_FIELD_NUMBER = 1;
      private List<ObjectField> fld_;
      private byte memoizedIsInitialized;
      private static final Object DEFAULT_INSTANCE = new Object();
      /** @deprecated */
      @Deprecated
      public static final Parser<Object> PARSER = new AbstractParser<Object>() {
         public Object parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Object(input, extensionRegistry);
         }
      };

      private Object(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Object() {
         this.memoizedIsInitialized = -1;
         this.fld_ = Collections.emptyList();
      }

      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Object();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Object(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        if (!(mutable_bitField0_ & true)) {
                           this.fld_ = new ArrayList();
                           mutable_bitField0_ |= true;
                        }

                        this.fld_.add(input.readMessage(MysqlxExpr.Object.ObjectField.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var11) {
               throw var11.setUnfinishedMessage(this);
            } catch (IOException var12) {
               throw (new InvalidProtocolBufferException(var12)).setUnfinishedMessage(this);
            } finally {
               if (mutable_bitField0_ & true) {
                  this.fld_ = Collections.unmodifiableList(this.fld_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Object_fieldAccessorTable.ensureFieldAccessorsInitialized(Object.class, Builder.class);
      }

      public List<ObjectField> getFldList() {
         return this.fld_;
      }

      public List<? extends ObjectFieldOrBuilder> getFldOrBuilderList() {
         return this.fld_;
      }

      public int getFldCount() {
         return this.fld_.size();
      }

      public ObjectField getFld(int index) {
         return (ObjectField)this.fld_.get(index);
      }

      public ObjectFieldOrBuilder getFldOrBuilder(int index) {
         return (ObjectFieldOrBuilder)this.fld_.get(index);
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for(int i = 0; i < this.getFldCount(); ++i) {
               if (!this.getFld(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         for(int i = 0; i < this.fld_.size(); ++i) {
            output.writeMessage(1, (MessageLite)this.fld_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;

            for(int i = 0; i < this.fld_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(1, (MessageLite)this.fld_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Object)) {
            return super.equals(obj);
         } else {
            Object other = (Object)obj;
            if (!this.getFldList().equals(other.getFldList())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.getFldCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getFldList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Object parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Object)PARSER.parseFrom(data);
      }

      public static Object parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Object)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Object parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Object)PARSER.parseFrom(data);
      }

      public static Object parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Object)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Object parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Object)PARSER.parseFrom(data);
      }

      public static Object parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Object)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Object parseFrom(InputStream input) throws IOException {
         return (Object)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Object parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Object)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Object parseDelimitedFrom(InputStream input) throws IOException {
         return (Object)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Object parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Object)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Object parseFrom(CodedInputStream input) throws IOException {
         return (Object)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Object parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Object)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Object prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Object getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Object> parser() {
         return PARSER;
      }

      public Parser<Object> getParserForType() {
         return PARSER;
      }

      public Object getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Object(GeneratedMessageV3.Builder x0, java.lang.Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Object(CodedInputStream x0, ExtensionRegistryLite x1, java.lang.Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ObjectOrBuilder {
         private int bitField0_;
         private List<ObjectField> fld_;
         private RepeatedFieldBuilderV3<ObjectField, ObjectField.Builder, ObjectFieldOrBuilder> fldBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_fieldAccessorTable.ensureFieldAccessorsInitialized(Object.class, Builder.class);
         }

         private Builder() {
            this.fld_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.fld_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Object.alwaysUseFieldBuilders) {
               this.getFldFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.fldBuilder_ == null) {
               this.fld_ = Collections.emptyList();
               this.bitField0_ &= -2;
            } else {
               this.fldBuilder_.clear();
            }

            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
         }

         public Object getDefaultInstanceForType() {
            return MysqlxExpr.Object.getDefaultInstance();
         }

         public Object build() {
            Object result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Object buildPartial() {
            Object result = new Object(this);
            int from_bitField0_ = this.bitField0_;
            if (this.fldBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0) {
                  this.fld_ = Collections.unmodifiableList(this.fld_);
                  this.bitField0_ &= -2;
               }

               result.fld_ = this.fld_;
            } else {
               result.fld_ = this.fldBuilder_.build();
            }

            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Object) {
               return this.mergeFrom((Object)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Object other) {
            if (other == MysqlxExpr.Object.getDefaultInstance()) {
               return this;
            } else {
               if (this.fldBuilder_ == null) {
                  if (!other.fld_.isEmpty()) {
                     if (this.fld_.isEmpty()) {
                        this.fld_ = other.fld_;
                        this.bitField0_ &= -2;
                     } else {
                        this.ensureFldIsMutable();
                        this.fld_.addAll(other.fld_);
                     }

                     this.onChanged();
                  }
               } else if (!other.fld_.isEmpty()) {
                  if (this.fldBuilder_.isEmpty()) {
                     this.fldBuilder_.dispose();
                     this.fldBuilder_ = null;
                     this.fld_ = other.fld_;
                     this.bitField0_ &= -2;
                     this.fldBuilder_ = MysqlxExpr.Object.alwaysUseFieldBuilders ? this.getFldFieldBuilder() : null;
                  } else {
                     this.fldBuilder_.addAllMessages(other.fld_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            for(int i = 0; i < this.getFldCount(); ++i) {
               if (!this.getFld(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Object parsedMessage = null;

            try {
               parsedMessage = (Object)MysqlxExpr.Object.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Object)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         private void ensureFldIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.fld_ = new ArrayList(this.fld_);
               this.bitField0_ |= 1;
            }

         }

         public List<ObjectField> getFldList() {
            return this.fldBuilder_ == null ? Collections.unmodifiableList(this.fld_) : this.fldBuilder_.getMessageList();
         }

         public int getFldCount() {
            return this.fldBuilder_ == null ? this.fld_.size() : this.fldBuilder_.getCount();
         }

         public ObjectField getFld(int index) {
            return this.fldBuilder_ == null ? (ObjectField)this.fld_.get(index) : (ObjectField)this.fldBuilder_.getMessage(index);
         }

         public Builder setFld(int index, ObjectField value) {
            if (this.fldBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureFldIsMutable();
               this.fld_.set(index, value);
               this.onChanged();
            } else {
               this.fldBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setFld(int index, ObjectField.Builder builderForValue) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               this.fld_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.fldBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addFld(ObjectField value) {
            if (this.fldBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureFldIsMutable();
               this.fld_.add(value);
               this.onChanged();
            } else {
               this.fldBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addFld(int index, ObjectField value) {
            if (this.fldBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureFldIsMutable();
               this.fld_.add(index, value);
               this.onChanged();
            } else {
               this.fldBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addFld(ObjectField.Builder builderForValue) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               this.fld_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.fldBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addFld(int index, ObjectField.Builder builderForValue) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               this.fld_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.fldBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllFld(Iterable<? extends ObjectField> values) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.fld_);
               this.onChanged();
            } else {
               this.fldBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearFld() {
            if (this.fldBuilder_ == null) {
               this.fld_ = Collections.emptyList();
               this.bitField0_ &= -2;
               this.onChanged();
            } else {
               this.fldBuilder_.clear();
            }

            return this;
         }

         public Builder removeFld(int index) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               this.fld_.remove(index);
               this.onChanged();
            } else {
               this.fldBuilder_.remove(index);
            }

            return this;
         }

         public ObjectField.Builder getFldBuilder(int index) {
            return (ObjectField.Builder)this.getFldFieldBuilder().getBuilder(index);
         }

         public ObjectFieldOrBuilder getFldOrBuilder(int index) {
            return this.fldBuilder_ == null ? (ObjectFieldOrBuilder)this.fld_.get(index) : (ObjectFieldOrBuilder)this.fldBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends ObjectFieldOrBuilder> getFldOrBuilderList() {
            return this.fldBuilder_ != null ? this.fldBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.fld_);
         }

         public ObjectField.Builder addFldBuilder() {
            return (ObjectField.Builder)this.getFldFieldBuilder().addBuilder(MysqlxExpr.Object.ObjectField.getDefaultInstance());
         }

         public ObjectField.Builder addFldBuilder(int index) {
            return (ObjectField.Builder)this.getFldFieldBuilder().addBuilder(index, MysqlxExpr.Object.ObjectField.getDefaultInstance());
         }

         public List<ObjectField.Builder> getFldBuilderList() {
            return this.getFldFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<ObjectField, ObjectField.Builder, ObjectFieldOrBuilder> getFldFieldBuilder() {
            if (this.fldBuilder_ == null) {
               this.fldBuilder_ = new RepeatedFieldBuilderV3(this.fld_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
               this.fld_ = null;
            }

            return this.fldBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(java.lang.Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, java.lang.Object x1) {
            this(x0);
         }
      }

      public static final class ObjectField extends GeneratedMessageV3 implements ObjectFieldOrBuilder {
         private static final long serialVersionUID = 0L;
         private int bitField0_;
         public static final int KEY_FIELD_NUMBER = 1;
         private volatile java.lang.Object key_;
         public static final int VALUE_FIELD_NUMBER = 2;
         private Expr value_;
         private byte memoizedIsInitialized;
         private static final ObjectField DEFAULT_INSTANCE = new ObjectField();
         /** @deprecated */
         @Deprecated
         public static final Parser<ObjectField> PARSER = new AbstractParser<ObjectField>() {
            public ObjectField parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               return new ObjectField(input, extensionRegistry);
            }
         };

         private ObjectField(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
         }

         private ObjectField() {
            this.memoizedIsInitialized = -1;
            this.key_ = "";
         }

         protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new ObjectField();
         }

         public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
         }

         private ObjectField(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
               throw new NullPointerException();
            } else {
               int mutable_bitField0_ = false;
               UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

               try {
                  boolean done = false;

                  while(!done) {
                     int tag = input.readTag();
                     switch (tag) {
                        case 0:
                           done = true;
                           break;
                        case 10:
                           ByteString bs = input.readBytes();
                           this.bitField0_ |= 1;
                           this.key_ = bs;
                           break;
                        case 18:
                           Expr.Builder subBuilder = null;
                           if ((this.bitField0_ & 2) != 0) {
                              subBuilder = this.value_.toBuilder();
                           }

                           this.value_ = (Expr)input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                           if (subBuilder != null) {
                              subBuilder.mergeFrom(this.value_);
                              this.value_ = subBuilder.buildPartial();
                           }

                           this.bitField0_ |= 2;
                           break;
                        default:
                           if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                              done = true;
                           }
                     }
                  }
               } catch (InvalidProtocolBufferException var12) {
                  throw var12.setUnfinishedMessage(this);
               } catch (IOException var13) {
                  throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
               } finally {
                  this.unknownFields = unknownFields.build();
                  this.makeExtensionsImmutable();
               }

            }
         }

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable.ensureFieldAccessorsInitialized(ObjectField.class, Builder.class);
         }

         public boolean hasKey() {
            return (this.bitField0_ & 1) != 0;
         }

         public String getKey() {
            java.lang.Object ref = this.key_;
            if (ref instanceof String) {
               return (String)ref;
            } else {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.key_ = s;
               }

               return s;
            }
         }

         public ByteString getKeyBytes() {
            java.lang.Object ref = this.key_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.key_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public boolean hasValue() {
            return (this.bitField0_ & 2) != 0;
         }

         public Expr getValue() {
            return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
         }

         public ExprOrBuilder getValueOrBuilder() {
            return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
         }

         public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
               return true;
            } else if (isInitialized == 0) {
               return false;
            } else if (!this.hasKey()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (!this.hasValue()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (!this.getValue().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               this.memoizedIsInitialized = 1;
               return true;
            }
         }

         public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
               GeneratedMessageV3.writeString(output, 1, this.key_);
            }

            if ((this.bitField0_ & 2) != 0) {
               output.writeMessage(2, this.getValue());
            }

            this.unknownFields.writeTo(output);
         }

         public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
               return size;
            } else {
               size = 0;
               if ((this.bitField0_ & 1) != 0) {
                  size += GeneratedMessageV3.computeStringSize(1, this.key_);
               }

               if ((this.bitField0_ & 2) != 0) {
                  size += CodedOutputStream.computeMessageSize(2, this.getValue());
               }

               size += this.unknownFields.getSerializedSize();
               this.memoizedSize = size;
               return size;
            }
         }

         public boolean equals(java.lang.Object obj) {
            if (obj == this) {
               return true;
            } else if (!(obj instanceof ObjectField)) {
               return super.equals(obj);
            } else {
               ObjectField other = (ObjectField)obj;
               if (this.hasKey() != other.hasKey()) {
                  return false;
               } else if (this.hasKey() && !this.getKey().equals(other.getKey())) {
                  return false;
               } else if (this.hasValue() != other.hasValue()) {
                  return false;
               } else if (this.hasValue() && !this.getValue().equals(other.getValue())) {
                  return false;
               } else {
                  return this.unknownFields.equals(other.unknownFields);
               }
            }
         }

         public int hashCode() {
            if (this.memoizedHashCode != 0) {
               return this.memoizedHashCode;
            } else {
               int hash = 41;
               hash = 19 * hash + getDescriptor().hashCode();
               if (this.hasKey()) {
                  hash = 37 * hash + 1;
                  hash = 53 * hash + this.getKey().hashCode();
               }

               if (this.hasValue()) {
                  hash = 37 * hash + 2;
                  hash = 53 * hash + this.getValue().hashCode();
               }

               hash = 29 * hash + this.unknownFields.hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static ObjectField parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (ObjectField)PARSER.parseFrom(data);
         }

         public static ObjectField parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (ObjectField)PARSER.parseFrom(data, extensionRegistry);
         }

         public static ObjectField parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (ObjectField)PARSER.parseFrom(data);
         }

         public static ObjectField parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (ObjectField)PARSER.parseFrom(data, extensionRegistry);
         }

         public static ObjectField parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (ObjectField)PARSER.parseFrom(data);
         }

         public static ObjectField parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (ObjectField)PARSER.parseFrom(data, extensionRegistry);
         }

         public static ObjectField parseFrom(InputStream input) throws IOException {
            return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static ObjectField parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static ObjectField parseDelimitedFrom(InputStream input) throws IOException {
            return (ObjectField)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
         }

         public static ObjectField parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ObjectField)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static ObjectField parseFrom(CodedInputStream input) throws IOException {
            return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static ObjectField parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (ObjectField)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public Builder newBuilderForType() {
            return newBuilder();
         }

         public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static Builder newBuilder(ObjectField prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
         }

         protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
         }

         public static ObjectField getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<ObjectField> parser() {
            return PARSER;
         }

         public Parser<ObjectField> getParserForType() {
            return PARSER;
         }

         public ObjectField getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         // $FF: synthetic method
         ObjectField(GeneratedMessageV3.Builder x0, java.lang.Object x1) {
            this(x0);
         }

         // $FF: synthetic method
         ObjectField(CodedInputStream x0, ExtensionRegistryLite x1, java.lang.Object x2) throws InvalidProtocolBufferException {
            this(x0, x1);
         }

         public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ObjectFieldOrBuilder {
            private int bitField0_;
            private java.lang.Object key_;
            private Expr value_;
            private SingleFieldBuilderV3<Expr, Expr.Builder, ExprOrBuilder> valueBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
               return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
            }

            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable.ensureFieldAccessorsInitialized(ObjectField.class, Builder.class);
            }

            private Builder() {
               this.key_ = "";
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
               super(parent);
               this.key_ = "";
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (MysqlxExpr.Object.ObjectField.alwaysUseFieldBuilders) {
                  this.getValueFieldBuilder();
               }

            }

            public Builder clear() {
               super.clear();
               this.key_ = "";
               this.bitField0_ &= -2;
               if (this.valueBuilder_ == null) {
                  this.value_ = null;
               } else {
                  this.valueBuilder_.clear();
               }

               this.bitField0_ &= -3;
               return this;
            }

            public Descriptors.Descriptor getDescriptorForType() {
               return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
            }

            public ObjectField getDefaultInstanceForType() {
               return MysqlxExpr.Object.ObjectField.getDefaultInstance();
            }

            public ObjectField build() {
               ObjectField result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public ObjectField buildPartial() {
               ObjectField result = new ObjectField(this);
               int from_bitField0_ = this.bitField0_;
               int to_bitField0_ = 0;
               if ((from_bitField0_ & 1) != 0) {
                  to_bitField0_ |= 1;
               }

               result.key_ = this.key_;
               if ((from_bitField0_ & 2) != 0) {
                  if (this.valueBuilder_ == null) {
                     result.value_ = this.value_;
                  } else {
                     result.value_ = (Expr)this.valueBuilder_.build();
                  }

                  to_bitField0_ |= 2;
               }

               result.bitField0_ = to_bitField0_;
               this.onBuilt();
               return result;
            }

            public Builder clone() {
               return (Builder)super.clone();
            }

            public Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
               return (Builder)super.setField(field, value);
            }

            public Builder clearField(Descriptors.FieldDescriptor field) {
               return (Builder)super.clearField(field);
            }

            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
               return (Builder)super.clearOneof(oneof);
            }

            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
               return (Builder)super.setRepeatedField(field, index, value);
            }

            public Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
               return (Builder)super.addRepeatedField(field, value);
            }

            public Builder mergeFrom(Message other) {
               if (other instanceof ObjectField) {
                  return this.mergeFrom((ObjectField)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public Builder mergeFrom(ObjectField other) {
               if (other == MysqlxExpr.Object.ObjectField.getDefaultInstance()) {
                  return this;
               } else {
                  if (other.hasKey()) {
                     this.bitField0_ |= 1;
                     this.key_ = other.key_;
                     this.onChanged();
                  }

                  if (other.hasValue()) {
                     this.mergeValue(other.getValue());
                  }

                  this.mergeUnknownFields(other.unknownFields);
                  this.onChanged();
                  return this;
               }
            }

            public final boolean isInitialized() {
               if (!this.hasKey()) {
                  return false;
               } else if (!this.hasValue()) {
                  return false;
               } else {
                  return this.getValue().isInitialized();
               }
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               ObjectField parsedMessage = null;

               try {
                  parsedMessage = (ObjectField)MysqlxExpr.Object.ObjectField.PARSER.parsePartialFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var8) {
                  parsedMessage = (ObjectField)var8.getUnfinishedMessage();
                  throw var8.unwrapIOException();
               } finally {
                  if (parsedMessage != null) {
                     this.mergeFrom(parsedMessage);
                  }

               }

               return this;
            }

            public boolean hasKey() {
               return (this.bitField0_ & 1) != 0;
            }

            public String getKey() {
               java.lang.Object ref = this.key_;
               if (!(ref instanceof String)) {
                  ByteString bs = (ByteString)ref;
                  String s = bs.toStringUtf8();
                  if (bs.isValidUtf8()) {
                     this.key_ = s;
                  }

                  return s;
               } else {
                  return (String)ref;
               }
            }

            public ByteString getKeyBytes() {
               java.lang.Object ref = this.key_;
               if (ref instanceof String) {
                  ByteString b = ByteString.copyFromUtf8((String)ref);
                  this.key_ = b;
                  return b;
               } else {
                  return (ByteString)ref;
               }
            }

            public Builder setKey(String value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 1;
                  this.key_ = value;
                  this.onChanged();
                  return this;
               }
            }

            public Builder clearKey() {
               this.bitField0_ &= -2;
               this.key_ = MysqlxExpr.Object.ObjectField.getDefaultInstance().getKey();
               this.onChanged();
               return this;
            }

            public Builder setKeyBytes(ByteString value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 1;
                  this.key_ = value;
                  this.onChanged();
                  return this;
               }
            }

            public boolean hasValue() {
               return (this.bitField0_ & 2) != 0;
            }

            public Expr getValue() {
               if (this.valueBuilder_ == null) {
                  return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
               } else {
                  return (Expr)this.valueBuilder_.getMessage();
               }
            }

            public Builder setValue(Expr value) {
               if (this.valueBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.value_ = value;
                  this.onChanged();
               } else {
                  this.valueBuilder_.setMessage(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public Builder setValue(Expr.Builder builderForValue) {
               if (this.valueBuilder_ == null) {
                  this.value_ = builderForValue.build();
                  this.onChanged();
               } else {
                  this.valueBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 2;
               return this;
            }

            public Builder mergeValue(Expr value) {
               if (this.valueBuilder_ == null) {
                  if ((this.bitField0_ & 2) != 0 && this.value_ != null && this.value_ != MysqlxExpr.Expr.getDefaultInstance()) {
                     this.value_ = MysqlxExpr.Expr.newBuilder(this.value_).mergeFrom(value).buildPartial();
                  } else {
                     this.value_ = value;
                  }

                  this.onChanged();
               } else {
                  this.valueBuilder_.mergeFrom(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public Builder clearValue() {
               if (this.valueBuilder_ == null) {
                  this.value_ = null;
                  this.onChanged();
               } else {
                  this.valueBuilder_.clear();
               }

               this.bitField0_ &= -3;
               return this;
            }

            public Expr.Builder getValueBuilder() {
               this.bitField0_ |= 2;
               this.onChanged();
               return (Expr.Builder)this.getValueFieldBuilder().getBuilder();
            }

            public ExprOrBuilder getValueOrBuilder() {
               if (this.valueBuilder_ != null) {
                  return (ExprOrBuilder)this.valueBuilder_.getMessageOrBuilder();
               } else {
                  return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
               }
            }

            private SingleFieldBuilderV3<Expr, Expr.Builder, ExprOrBuilder> getValueFieldBuilder() {
               if (this.valueBuilder_ == null) {
                  this.valueBuilder_ = new SingleFieldBuilderV3(this.getValue(), this.getParentForChildren(), this.isClean());
                  this.value_ = null;
               }

               return this.valueBuilder_;
            }

            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
               return (Builder)super.setUnknownFields(unknownFields);
            }

            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
               return (Builder)super.mergeUnknownFields(unknownFields);
            }

            // $FF: synthetic method
            Builder(java.lang.Object x0) {
               this();
            }

            // $FF: synthetic method
            Builder(GeneratedMessageV3.BuilderParent x0, java.lang.Object x1) {
               this(x0);
            }
         }
      }

      public interface ObjectFieldOrBuilder extends MessageOrBuilder {
         boolean hasKey();

         String getKey();

         ByteString getKeyBytes();

         boolean hasValue();

         Expr getValue();

         ExprOrBuilder getValueOrBuilder();
      }
   }

   public interface ObjectOrBuilder extends MessageOrBuilder {
      List<Object.ObjectField> getFldList();

      Object.ObjectField getFld(int var1);

      int getFldCount();

      List<? extends Object.ObjectFieldOrBuilder> getFldOrBuilderList();

      Object.ObjectFieldOrBuilder getFldOrBuilder(int var1);
   }

   public static final class Operator extends GeneratedMessageV3 implements OperatorOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile java.lang.Object name_;
      public static final int PARAM_FIELD_NUMBER = 2;
      private List<Expr> param_;
      private byte memoizedIsInitialized;
      private static final Operator DEFAULT_INSTANCE = new Operator();
      /** @deprecated */
      @Deprecated
      public static final Parser<Operator> PARSER = new AbstractParser<Operator>() {
         public Operator parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Operator(input, extensionRegistry);
         }
      };

      private Operator(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Operator() {
         this.memoizedIsInitialized = -1;
         this.name_ = "";
         this.param_ = Collections.emptyList();
      }

      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Operator();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Operator(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 2) == 0) {
                           this.param_ = new ArrayList();
                           mutable_bitField0_ |= 2;
                        }

                        this.param_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 2) != 0) {
                  this.param_ = Collections.unmodifiableList(this.param_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_fieldAccessorTable.ensureFieldAccessorsInitialized(Operator.class, Builder.class);
      }

      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      public String getName() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      public ByteString getNameBytes() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public List<Expr> getParamList() {
         return this.param_;
      }

      public List<? extends ExprOrBuilder> getParamOrBuilderList() {
         return this.param_;
      }

      public int getParamCount() {
         return this.param_.size();
      }

      public Expr getParam(int index) {
         return (Expr)this.param_.get(index);
      }

      public ExprOrBuilder getParamOrBuilder(int index) {
         return (ExprOrBuilder)this.param_.get(index);
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for(int i = 0; i < this.getParamCount(); ++i) {
               if (!this.getParam(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
         }

         for(int i = 0; i < this.param_.size(); ++i) {
            output.writeMessage(2, (MessageLite)this.param_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
            }

            for(int i = 0; i < this.param_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(2, (MessageLite)this.param_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Operator)) {
            return super.equals(obj);
         } else {
            Operator other = (Operator)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (!this.getParamList().equals(other.getParamList())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.getParamCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getParamList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Operator parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Operator)PARSER.parseFrom(data);
      }

      public static Operator parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Operator)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Operator parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Operator)PARSER.parseFrom(data);
      }

      public static Operator parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Operator)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Operator parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Operator)PARSER.parseFrom(data);
      }

      public static Operator parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Operator)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Operator parseFrom(InputStream input) throws IOException {
         return (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Operator parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Operator parseDelimitedFrom(InputStream input) throws IOException {
         return (Operator)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Operator parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Operator)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Operator parseFrom(CodedInputStream input) throws IOException {
         return (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Operator parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Operator)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Operator prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Operator getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Operator> parser() {
         return PARSER;
      }

      public Parser<Operator> getParserForType() {
         return PARSER;
      }

      public Operator getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Operator(GeneratedMessageV3.Builder x0, java.lang.Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Operator(CodedInputStream x0, ExtensionRegistryLite x1, java.lang.Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements OperatorOrBuilder {
         private int bitField0_;
         private java.lang.Object name_;
         private List<Expr> param_;
         private RepeatedFieldBuilderV3<Expr, Expr.Builder, ExprOrBuilder> paramBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_fieldAccessorTable.ensureFieldAccessorsInitialized(Operator.class, Builder.class);
         }

         private Builder() {
            this.name_ = "";
            this.param_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.name_ = "";
            this.param_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Operator.alwaysUseFieldBuilders) {
               this.getParamFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            this.name_ = "";
            this.bitField0_ &= -2;
            if (this.paramBuilder_ == null) {
               this.param_ = Collections.emptyList();
               this.bitField0_ &= -3;
            } else {
               this.paramBuilder_.clear();
            }

            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
         }

         public Operator getDefaultInstanceForType() {
            return MysqlxExpr.Operator.getDefaultInstance();
         }

         public Operator build() {
            Operator result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Operator buildPartial() {
            Operator result = new Operator(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if (this.paramBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0) {
                  this.param_ = Collections.unmodifiableList(this.param_);
                  this.bitField0_ &= -3;
               }

               result.param_ = this.param_;
            } else {
               result.param_ = this.paramBuilder_.build();
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Operator) {
               return this.mergeFrom((Operator)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Operator other) {
            if (other == MysqlxExpr.Operator.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (this.paramBuilder_ == null) {
                  if (!other.param_.isEmpty()) {
                     if (this.param_.isEmpty()) {
                        this.param_ = other.param_;
                        this.bitField0_ &= -3;
                     } else {
                        this.ensureParamIsMutable();
                        this.param_.addAll(other.param_);
                     }

                     this.onChanged();
                  }
               } else if (!other.param_.isEmpty()) {
                  if (this.paramBuilder_.isEmpty()) {
                     this.paramBuilder_.dispose();
                     this.paramBuilder_ = null;
                     this.param_ = other.param_;
                     this.bitField0_ &= -3;
                     this.paramBuilder_ = MysqlxExpr.Operator.alwaysUseFieldBuilders ? this.getParamFieldBuilder() : null;
                  } else {
                     this.paramBuilder_.addAllMessages(other.param_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasName()) {
               return false;
            } else {
               for(int i = 0; i < this.getParamCount(); ++i) {
                  if (!this.getParam(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Operator parsedMessage = null;

            try {
               parsedMessage = (Operator)MysqlxExpr.Operator.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Operator)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         public String getName() {
            java.lang.Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getNameBytes() {
            java.lang.Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxExpr.Operator.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         private void ensureParamIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
               this.param_ = new ArrayList(this.param_);
               this.bitField0_ |= 2;
            }

         }

         public List<Expr> getParamList() {
            return this.paramBuilder_ == null ? Collections.unmodifiableList(this.param_) : this.paramBuilder_.getMessageList();
         }

         public int getParamCount() {
            return this.paramBuilder_ == null ? this.param_.size() : this.paramBuilder_.getCount();
         }

         public Expr getParam(int index) {
            return this.paramBuilder_ == null ? (Expr)this.param_.get(index) : (Expr)this.paramBuilder_.getMessage(index);
         }

         public Builder setParam(int index, Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.set(index, value);
               this.onChanged();
            } else {
               this.paramBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setParam(int index, Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addParam(Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.add(value);
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addParam(int index, Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.add(index, value);
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addParam(Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addParam(int index, Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllParam(Iterable<? extends Expr> values) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.param_);
               this.onChanged();
            } else {
               this.paramBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearParam() {
            if (this.paramBuilder_ == null) {
               this.param_ = Collections.emptyList();
               this.bitField0_ &= -3;
               this.onChanged();
            } else {
               this.paramBuilder_.clear();
            }

            return this;
         }

         public Builder removeParam(int index) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.remove(index);
               this.onChanged();
            } else {
               this.paramBuilder_.remove(index);
            }

            return this;
         }

         public Expr.Builder getParamBuilder(int index) {
            return (Expr.Builder)this.getParamFieldBuilder().getBuilder(index);
         }

         public ExprOrBuilder getParamOrBuilder(int index) {
            return this.paramBuilder_ == null ? (ExprOrBuilder)this.param_.get(index) : (ExprOrBuilder)this.paramBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends ExprOrBuilder> getParamOrBuilderList() {
            return this.paramBuilder_ != null ? this.paramBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.param_);
         }

         public Expr.Builder addParamBuilder() {
            return (Expr.Builder)this.getParamFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
         }

         public Expr.Builder addParamBuilder(int index) {
            return (Expr.Builder)this.getParamFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
         }

         public List<Expr.Builder> getParamBuilderList() {
            return this.getParamFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Expr, Expr.Builder, ExprOrBuilder> getParamFieldBuilder() {
            if (this.paramBuilder_ == null) {
               this.paramBuilder_ = new RepeatedFieldBuilderV3(this.param_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
               this.param_ = null;
            }

            return this.paramBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(java.lang.Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, java.lang.Object x1) {
            this(x0);
         }
      }
   }

   public interface OperatorOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      List<Expr> getParamList();

      Expr getParam(int var1);

      int getParamCount();

      List<? extends ExprOrBuilder> getParamOrBuilderList();

      ExprOrBuilder getParamOrBuilder(int var1);
   }

   public static final class FunctionCall extends GeneratedMessageV3 implements FunctionCallOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private Identifier name_;
      public static final int PARAM_FIELD_NUMBER = 2;
      private List<Expr> param_;
      private byte memoizedIsInitialized;
      private static final FunctionCall DEFAULT_INSTANCE = new FunctionCall();
      /** @deprecated */
      @Deprecated
      public static final Parser<FunctionCall> PARSER = new AbstractParser<FunctionCall>() {
         public FunctionCall parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new FunctionCall(input, extensionRegistry);
         }
      };

      private FunctionCall(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private FunctionCall() {
         this.memoizedIsInitialized = -1;
         this.param_ = Collections.emptyList();
      }

      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new FunctionCall();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FunctionCall(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        Identifier.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.name_.toBuilder();
                        }

                        this.name_ = (Identifier)input.readMessage(MysqlxExpr.Identifier.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.name_);
                           this.name_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 2) == 0) {
                           this.param_ = new ArrayList();
                           mutable_bitField0_ |= 2;
                        }

                        this.param_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 2) != 0) {
                  this.param_ = Collections.unmodifiableList(this.param_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable.ensureFieldAccessorsInitialized(FunctionCall.class, Builder.class);
      }

      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      public Identifier getName() {
         return this.name_ == null ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
      }

      public IdentifierOrBuilder getNameOrBuilder() {
         return this.name_ == null ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
      }

      public List<Expr> getParamList() {
         return this.param_;
      }

      public List<? extends ExprOrBuilder> getParamOrBuilderList() {
         return this.param_;
      }

      public int getParamCount() {
         return this.param_.size();
      }

      public Expr getParam(int index) {
         return (Expr)this.param_.get(index);
      }

      public ExprOrBuilder getParamOrBuilder(int index) {
         return (ExprOrBuilder)this.param_.get(index);
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getName().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for(int i = 0; i < this.getParamCount(); ++i) {
               if (!this.getParam(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getName());
         }

         for(int i = 0; i < this.param_.size(); ++i) {
            output.writeMessage(2, (MessageLite)this.param_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getName());
            }

            for(int i = 0; i < this.param_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(2, (MessageLite)this.param_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof FunctionCall)) {
            return super.equals(obj);
         } else {
            FunctionCall other = (FunctionCall)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (!this.getParamList().equals(other.getParamList())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.getParamCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getParamList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static FunctionCall parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (FunctionCall)PARSER.parseFrom(data);
      }

      public static FunctionCall parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FunctionCall)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FunctionCall parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (FunctionCall)PARSER.parseFrom(data);
      }

      public static FunctionCall parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FunctionCall)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FunctionCall parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (FunctionCall)PARSER.parseFrom(data);
      }

      public static FunctionCall parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (FunctionCall)PARSER.parseFrom(data, extensionRegistry);
      }

      public static FunctionCall parseFrom(InputStream input) throws IOException {
         return (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FunctionCall parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static FunctionCall parseDelimitedFrom(InputStream input) throws IOException {
         return (FunctionCall)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static FunctionCall parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FunctionCall)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static FunctionCall parseFrom(CodedInputStream input) throws IOException {
         return (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static FunctionCall parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (FunctionCall)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(FunctionCall prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static FunctionCall getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<FunctionCall> parser() {
         return PARSER;
      }

      public Parser<FunctionCall> getParserForType() {
         return PARSER;
      }

      public FunctionCall getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      FunctionCall(GeneratedMessageV3.Builder x0, java.lang.Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      FunctionCall(CodedInputStream x0, ExtensionRegistryLite x1, java.lang.Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FunctionCallOrBuilder {
         private int bitField0_;
         private Identifier name_;
         private SingleFieldBuilderV3<Identifier, Identifier.Builder, IdentifierOrBuilder> nameBuilder_;
         private List<Expr> param_;
         private RepeatedFieldBuilderV3<Expr, Expr.Builder, ExprOrBuilder> paramBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable.ensureFieldAccessorsInitialized(FunctionCall.class, Builder.class);
         }

         private Builder() {
            this.param_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.param_ = Collections.emptyList();
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.FunctionCall.alwaysUseFieldBuilders) {
               this.getNameFieldBuilder();
               this.getParamFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.nameBuilder_ == null) {
               this.name_ = null;
            } else {
               this.nameBuilder_.clear();
            }

            this.bitField0_ &= -2;
            if (this.paramBuilder_ == null) {
               this.param_ = Collections.emptyList();
               this.bitField0_ &= -3;
            } else {
               this.paramBuilder_.clear();
            }

            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
         }

         public FunctionCall getDefaultInstanceForType() {
            return MysqlxExpr.FunctionCall.getDefaultInstance();
         }

         public FunctionCall build() {
            FunctionCall result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public FunctionCall buildPartial() {
            FunctionCall result = new FunctionCall(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.nameBuilder_ == null) {
                  result.name_ = this.name_;
               } else {
                  result.name_ = (Identifier)this.nameBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if (this.paramBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0) {
                  this.param_ = Collections.unmodifiableList(this.param_);
                  this.bitField0_ &= -3;
               }

               result.param_ = this.param_;
            } else {
               result.param_ = this.paramBuilder_.build();
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof FunctionCall) {
               return this.mergeFrom((FunctionCall)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(FunctionCall other) {
            if (other == MysqlxExpr.FunctionCall.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.mergeName(other.getName());
               }

               if (this.paramBuilder_ == null) {
                  if (!other.param_.isEmpty()) {
                     if (this.param_.isEmpty()) {
                        this.param_ = other.param_;
                        this.bitField0_ &= -3;
                     } else {
                        this.ensureParamIsMutable();
                        this.param_.addAll(other.param_);
                     }

                     this.onChanged();
                  }
               } else if (!other.param_.isEmpty()) {
                  if (this.paramBuilder_.isEmpty()) {
                     this.paramBuilder_.dispose();
                     this.paramBuilder_ = null;
                     this.param_ = other.param_;
                     this.bitField0_ &= -3;
                     this.paramBuilder_ = MysqlxExpr.FunctionCall.alwaysUseFieldBuilders ? this.getParamFieldBuilder() : null;
                  } else {
                     this.paramBuilder_.addAllMessages(other.param_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasName()) {
               return false;
            } else if (!this.getName().isInitialized()) {
               return false;
            } else {
               for(int i = 0; i < this.getParamCount(); ++i) {
                  if (!this.getParam(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            FunctionCall parsedMessage = null;

            try {
               parsedMessage = (FunctionCall)MysqlxExpr.FunctionCall.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (FunctionCall)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         public Identifier getName() {
            if (this.nameBuilder_ == null) {
               return this.name_ == null ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
            } else {
               return (Identifier)this.nameBuilder_.getMessage();
            }
         }

         public Builder setName(Identifier value) {
            if (this.nameBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.name_ = value;
               this.onChanged();
            } else {
               this.nameBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder setName(Identifier.Builder builderForValue) {
            if (this.nameBuilder_ == null) {
               this.name_ = builderForValue.build();
               this.onChanged();
            } else {
               this.nameBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder mergeName(Identifier value) {
            if (this.nameBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.name_ != null && this.name_ != MysqlxExpr.Identifier.getDefaultInstance()) {
                  this.name_ = MysqlxExpr.Identifier.newBuilder(this.name_).mergeFrom(value).buildPartial();
               } else {
                  this.name_ = value;
               }

               this.onChanged();
            } else {
               this.nameBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public Builder clearName() {
            if (this.nameBuilder_ == null) {
               this.name_ = null;
               this.onChanged();
            } else {
               this.nameBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public Identifier.Builder getNameBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return (Identifier.Builder)this.getNameFieldBuilder().getBuilder();
         }

         public IdentifierOrBuilder getNameOrBuilder() {
            if (this.nameBuilder_ != null) {
               return (IdentifierOrBuilder)this.nameBuilder_.getMessageOrBuilder();
            } else {
               return this.name_ == null ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
            }
         }

         private SingleFieldBuilderV3<Identifier, Identifier.Builder, IdentifierOrBuilder> getNameFieldBuilder() {
            if (this.nameBuilder_ == null) {
               this.nameBuilder_ = new SingleFieldBuilderV3(this.getName(), this.getParentForChildren(), this.isClean());
               this.name_ = null;
            }

            return this.nameBuilder_;
         }

         private void ensureParamIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
               this.param_ = new ArrayList(this.param_);
               this.bitField0_ |= 2;
            }

         }

         public List<Expr> getParamList() {
            return this.paramBuilder_ == null ? Collections.unmodifiableList(this.param_) : this.paramBuilder_.getMessageList();
         }

         public int getParamCount() {
            return this.paramBuilder_ == null ? this.param_.size() : this.paramBuilder_.getCount();
         }

         public Expr getParam(int index) {
            return this.paramBuilder_ == null ? (Expr)this.param_.get(index) : (Expr)this.paramBuilder_.getMessage(index);
         }

         public Builder setParam(int index, Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.set(index, value);
               this.onChanged();
            } else {
               this.paramBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setParam(int index, Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addParam(Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.add(value);
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addParam(int index, Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.add(index, value);
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addParam(Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addParam(int index, Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllParam(Iterable<? extends Expr> values) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.param_);
               this.onChanged();
            } else {
               this.paramBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearParam() {
            if (this.paramBuilder_ == null) {
               this.param_ = Collections.emptyList();
               this.bitField0_ &= -3;
               this.onChanged();
            } else {
               this.paramBuilder_.clear();
            }

            return this;
         }

         public Builder removeParam(int index) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.remove(index);
               this.onChanged();
            } else {
               this.paramBuilder_.remove(index);
            }

            return this;
         }

         public Expr.Builder getParamBuilder(int index) {
            return (Expr.Builder)this.getParamFieldBuilder().getBuilder(index);
         }

         public ExprOrBuilder getParamOrBuilder(int index) {
            return this.paramBuilder_ == null ? (ExprOrBuilder)this.param_.get(index) : (ExprOrBuilder)this.paramBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends ExprOrBuilder> getParamOrBuilderList() {
            return this.paramBuilder_ != null ? this.paramBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.param_);
         }

         public Expr.Builder addParamBuilder() {
            return (Expr.Builder)this.getParamFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
         }

         public Expr.Builder addParamBuilder(int index) {
            return (Expr.Builder)this.getParamFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
         }

         public List<Expr.Builder> getParamBuilderList() {
            return this.getParamFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<Expr, Expr.Builder, ExprOrBuilder> getParamFieldBuilder() {
            if (this.paramBuilder_ == null) {
               this.paramBuilder_ = new RepeatedFieldBuilderV3(this.param_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
               this.param_ = null;
            }

            return this.paramBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(java.lang.Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, java.lang.Object x1) {
            this(x0);
         }
      }
   }

   public interface FunctionCallOrBuilder extends MessageOrBuilder {
      boolean hasName();

      Identifier getName();

      IdentifierOrBuilder getNameOrBuilder();

      List<Expr> getParamList();

      Expr getParam(int var1);

      int getParamCount();

      List<? extends ExprOrBuilder> getParamOrBuilderList();

      ExprOrBuilder getParamOrBuilder(int var1);
   }

   public static final class ColumnIdentifier extends GeneratedMessageV3 implements ColumnIdentifierOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int DOCUMENT_PATH_FIELD_NUMBER = 1;
      private List<DocumentPathItem> documentPath_;
      public static final int NAME_FIELD_NUMBER = 2;
      private volatile java.lang.Object name_;
      public static final int TABLE_NAME_FIELD_NUMBER = 3;
      private volatile java.lang.Object tableName_;
      public static final int SCHEMA_NAME_FIELD_NUMBER = 4;
      private volatile java.lang.Object schemaName_;
      private byte memoizedIsInitialized;
      private static final ColumnIdentifier DEFAULT_INSTANCE = new ColumnIdentifier();
      /** @deprecated */
      @Deprecated
      public static final Parser<ColumnIdentifier> PARSER = new AbstractParser<ColumnIdentifier>() {
         public ColumnIdentifier parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new ColumnIdentifier(input, extensionRegistry);
         }
      };

      private ColumnIdentifier(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private ColumnIdentifier() {
         this.memoizedIsInitialized = -1;
         this.documentPath_ = Collections.emptyList();
         this.name_ = "";
         this.tableName_ = "";
         this.schemaName_ = "";
      }

      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new ColumnIdentifier();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private ColumnIdentifier(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  ByteString bs;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        if (!(mutable_bitField0_ & true)) {
                           this.documentPath_ = new ArrayList();
                           mutable_bitField0_ |= true;
                        }

                        this.documentPath_.add(input.readMessage(MysqlxExpr.DocumentPathItem.PARSER, extensionRegistry));
                        break;
                     case 18:
                        bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     case 26:
                        bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.tableName_ = bs;
                        break;
                     case 34:
                        bs = input.readBytes();
                        this.bitField0_ |= 4;
                        this.schemaName_ = bs;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
            } finally {
               if (mutable_bitField0_ & true) {
                  this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable.ensureFieldAccessorsInitialized(ColumnIdentifier.class, Builder.class);
      }

      public List<DocumentPathItem> getDocumentPathList() {
         return this.documentPath_;
      }

      public List<? extends DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
         return this.documentPath_;
      }

      public int getDocumentPathCount() {
         return this.documentPath_.size();
      }

      public DocumentPathItem getDocumentPath(int index) {
         return (DocumentPathItem)this.documentPath_.get(index);
      }

      public DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
         return (DocumentPathItemOrBuilder)this.documentPath_.get(index);
      }

      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      public String getName() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      public ByteString getNameBytes() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasTableName() {
         return (this.bitField0_ & 2) != 0;
      }

      public String getTableName() {
         java.lang.Object ref = this.tableName_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.tableName_ = s;
            }

            return s;
         }
      }

      public ByteString getTableNameBytes() {
         java.lang.Object ref = this.tableName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.tableName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasSchemaName() {
         return (this.bitField0_ & 4) != 0;
      }

      public String getSchemaName() {
         java.lang.Object ref = this.schemaName_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.schemaName_ = s;
            }

            return s;
         }
      }

      public ByteString getSchemaNameBytes() {
         java.lang.Object ref = this.schemaName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.schemaName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for(int i = 0; i < this.getDocumentPathCount(); ++i) {
               if (!this.getDocumentPath(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         for(int i = 0; i < this.documentPath_.size(); ++i) {
            output.writeMessage(1, (MessageLite)this.documentPath_.get(i));
         }

         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.name_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 3, this.tableName_);
         }

         if ((this.bitField0_ & 4) != 0) {
            GeneratedMessageV3.writeString(output, 4, this.schemaName_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;

            for(int i = 0; i < this.documentPath_.size(); ++i) {
               size += CodedOutputStream.computeMessageSize(1, (MessageLite)this.documentPath_.get(i));
            }

            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(3, this.tableName_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += GeneratedMessageV3.computeStringSize(4, this.schemaName_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof ColumnIdentifier)) {
            return super.equals(obj);
         } else {
            ColumnIdentifier other = (ColumnIdentifier)obj;
            if (!this.getDocumentPathList().equals(other.getDocumentPathList())) {
               return false;
            } else if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasTableName() != other.hasTableName()) {
               return false;
            } else if (this.hasTableName() && !this.getTableName().equals(other.getTableName())) {
               return false;
            } else if (this.hasSchemaName() != other.hasSchemaName()) {
               return false;
            } else if (this.hasSchemaName() && !this.getSchemaName().equals(other.getSchemaName())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.getDocumentPathCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getDocumentPathList().hashCode();
            }

            if (this.hasName()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasTableName()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getTableName().hashCode();
            }

            if (this.hasSchemaName()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getSchemaName().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static ColumnIdentifier parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (ColumnIdentifier)PARSER.parseFrom(data);
      }

      public static ColumnIdentifier parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ColumnIdentifier)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ColumnIdentifier parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (ColumnIdentifier)PARSER.parseFrom(data);
      }

      public static ColumnIdentifier parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ColumnIdentifier)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ColumnIdentifier parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (ColumnIdentifier)PARSER.parseFrom(data);
      }

      public static ColumnIdentifier parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (ColumnIdentifier)PARSER.parseFrom(data, extensionRegistry);
      }

      public static ColumnIdentifier parseFrom(InputStream input) throws IOException {
         return (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ColumnIdentifier parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static ColumnIdentifier parseDelimitedFrom(InputStream input) throws IOException {
         return (ColumnIdentifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static ColumnIdentifier parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ColumnIdentifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static ColumnIdentifier parseFrom(CodedInputStream input) throws IOException {
         return (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static ColumnIdentifier parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (ColumnIdentifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(ColumnIdentifier prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static ColumnIdentifier getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<ColumnIdentifier> parser() {
         return PARSER;
      }

      public Parser<ColumnIdentifier> getParserForType() {
         return PARSER;
      }

      public ColumnIdentifier getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      ColumnIdentifier(GeneratedMessageV3.Builder x0, java.lang.Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      ColumnIdentifier(CodedInputStream x0, ExtensionRegistryLite x1, java.lang.Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ColumnIdentifierOrBuilder {
         private int bitField0_;
         private List<DocumentPathItem> documentPath_;
         private RepeatedFieldBuilderV3<DocumentPathItem, DocumentPathItem.Builder, DocumentPathItemOrBuilder> documentPathBuilder_;
         private java.lang.Object name_;
         private java.lang.Object tableName_;
         private java.lang.Object schemaName_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable.ensureFieldAccessorsInitialized(ColumnIdentifier.class, Builder.class);
         }

         private Builder() {
            this.documentPath_ = Collections.emptyList();
            this.name_ = "";
            this.tableName_ = "";
            this.schemaName_ = "";
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.documentPath_ = Collections.emptyList();
            this.name_ = "";
            this.tableName_ = "";
            this.schemaName_ = "";
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.ColumnIdentifier.alwaysUseFieldBuilders) {
               this.getDocumentPathFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            if (this.documentPathBuilder_ == null) {
               this.documentPath_ = Collections.emptyList();
               this.bitField0_ &= -2;
            } else {
               this.documentPathBuilder_.clear();
            }

            this.name_ = "";
            this.bitField0_ &= -3;
            this.tableName_ = "";
            this.bitField0_ &= -5;
            this.schemaName_ = "";
            this.bitField0_ &= -9;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
         }

         public ColumnIdentifier getDefaultInstanceForType() {
            return MysqlxExpr.ColumnIdentifier.getDefaultInstance();
         }

         public ColumnIdentifier build() {
            ColumnIdentifier result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public ColumnIdentifier buildPartial() {
            ColumnIdentifier result = new ColumnIdentifier(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if (this.documentPathBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0) {
                  this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
                  this.bitField0_ &= -2;
               }

               result.documentPath_ = this.documentPath_;
            } else {
               result.documentPath_ = this.documentPathBuilder_.build();
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 2;
            }

            result.tableName_ = this.tableName_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 4;
            }

            result.schemaName_ = this.schemaName_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof ColumnIdentifier) {
               return this.mergeFrom((ColumnIdentifier)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(ColumnIdentifier other) {
            if (other == MysqlxExpr.ColumnIdentifier.getDefaultInstance()) {
               return this;
            } else {
               if (this.documentPathBuilder_ == null) {
                  if (!other.documentPath_.isEmpty()) {
                     if (this.documentPath_.isEmpty()) {
                        this.documentPath_ = other.documentPath_;
                        this.bitField0_ &= -2;
                     } else {
                        this.ensureDocumentPathIsMutable();
                        this.documentPath_.addAll(other.documentPath_);
                     }

                     this.onChanged();
                  }
               } else if (!other.documentPath_.isEmpty()) {
                  if (this.documentPathBuilder_.isEmpty()) {
                     this.documentPathBuilder_.dispose();
                     this.documentPathBuilder_ = null;
                     this.documentPath_ = other.documentPath_;
                     this.bitField0_ &= -2;
                     this.documentPathBuilder_ = MysqlxExpr.ColumnIdentifier.alwaysUseFieldBuilders ? this.getDocumentPathFieldBuilder() : null;
                  } else {
                     this.documentPathBuilder_.addAllMessages(other.documentPath_);
                  }
               }

               if (other.hasName()) {
                  this.bitField0_ |= 2;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (other.hasTableName()) {
                  this.bitField0_ |= 4;
                  this.tableName_ = other.tableName_;
                  this.onChanged();
               }

               if (other.hasSchemaName()) {
                  this.bitField0_ |= 8;
                  this.schemaName_ = other.schemaName_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            for(int i = 0; i < this.getDocumentPathCount(); ++i) {
               if (!this.getDocumentPath(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            ColumnIdentifier parsedMessage = null;

            try {
               parsedMessage = (ColumnIdentifier)MysqlxExpr.ColumnIdentifier.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (ColumnIdentifier)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         private void ensureDocumentPathIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.documentPath_ = new ArrayList(this.documentPath_);
               this.bitField0_ |= 1;
            }

         }

         public List<DocumentPathItem> getDocumentPathList() {
            return this.documentPathBuilder_ == null ? Collections.unmodifiableList(this.documentPath_) : this.documentPathBuilder_.getMessageList();
         }

         public int getDocumentPathCount() {
            return this.documentPathBuilder_ == null ? this.documentPath_.size() : this.documentPathBuilder_.getCount();
         }

         public DocumentPathItem getDocumentPath(int index) {
            return this.documentPathBuilder_ == null ? (DocumentPathItem)this.documentPath_.get(index) : (DocumentPathItem)this.documentPathBuilder_.getMessage(index);
         }

         public Builder setDocumentPath(int index, DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.set(index, value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.setMessage(index, value);
            }

            return this;
         }

         public Builder setDocumentPath(int index, DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addDocumentPath(DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(value);
            }

            return this;
         }

         public Builder addDocumentPath(int index, DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(index, value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(index, value);
            }

            return this;
         }

         public Builder addDocumentPath(DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public Builder addDocumentPath(int index, DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public Builder addAllDocumentPath(Iterable<? extends DocumentPathItem> values) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.documentPath_);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addAllMessages(values);
            }

            return this;
         }

         public Builder clearDocumentPath() {
            if (this.documentPathBuilder_ == null) {
               this.documentPath_ = Collections.emptyList();
               this.bitField0_ &= -2;
               this.onChanged();
            } else {
               this.documentPathBuilder_.clear();
            }

            return this;
         }

         public Builder removeDocumentPath(int index) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.remove(index);
               this.onChanged();
            } else {
               this.documentPathBuilder_.remove(index);
            }

            return this;
         }

         public DocumentPathItem.Builder getDocumentPathBuilder(int index) {
            return (DocumentPathItem.Builder)this.getDocumentPathFieldBuilder().getBuilder(index);
         }

         public DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
            return this.documentPathBuilder_ == null ? (DocumentPathItemOrBuilder)this.documentPath_.get(index) : (DocumentPathItemOrBuilder)this.documentPathBuilder_.getMessageOrBuilder(index);
         }

         public List<? extends DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
            return this.documentPathBuilder_ != null ? this.documentPathBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.documentPath_);
         }

         public DocumentPathItem.Builder addDocumentPathBuilder() {
            return (DocumentPathItem.Builder)this.getDocumentPathFieldBuilder().addBuilder(MysqlxExpr.DocumentPathItem.getDefaultInstance());
         }

         public DocumentPathItem.Builder addDocumentPathBuilder(int index) {
            return (DocumentPathItem.Builder)this.getDocumentPathFieldBuilder().addBuilder(index, MysqlxExpr.DocumentPathItem.getDefaultInstance());
         }

         public List<DocumentPathItem.Builder> getDocumentPathBuilderList() {
            return this.getDocumentPathFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<DocumentPathItem, DocumentPathItem.Builder, DocumentPathItemOrBuilder> getDocumentPathFieldBuilder() {
            if (this.documentPathBuilder_ == null) {
               this.documentPathBuilder_ = new RepeatedFieldBuilderV3(this.documentPath_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
               this.documentPath_ = null;
            }

            return this.documentPathBuilder_;
         }

         public boolean hasName() {
            return (this.bitField0_ & 2) != 0;
         }

         public String getName() {
            java.lang.Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getNameBytes() {
            java.lang.Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearName() {
            this.bitField0_ &= -3;
            this.name_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasTableName() {
            return (this.bitField0_ & 4) != 0;
         }

         public String getTableName() {
            java.lang.Object ref = this.tableName_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.tableName_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getTableNameBytes() {
            java.lang.Object ref = this.tableName_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.tableName_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setTableName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.tableName_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearTableName() {
            this.bitField0_ &= -5;
            this.tableName_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getTableName();
            this.onChanged();
            return this;
         }

         public Builder setTableNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.tableName_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasSchemaName() {
            return (this.bitField0_ & 8) != 0;
         }

         public String getSchemaName() {
            java.lang.Object ref = this.schemaName_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.schemaName_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getSchemaNameBytes() {
            java.lang.Object ref = this.schemaName_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.schemaName_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setSchemaName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.schemaName_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearSchemaName() {
            this.bitField0_ &= -9;
            this.schemaName_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getSchemaName();
            this.onChanged();
            return this;
         }

         public Builder setSchemaNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.schemaName_ = value;
               this.onChanged();
               return this;
            }
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(java.lang.Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, java.lang.Object x1) {
            this(x0);
         }
      }
   }

   public interface ColumnIdentifierOrBuilder extends MessageOrBuilder {
      List<DocumentPathItem> getDocumentPathList();

      DocumentPathItem getDocumentPath(int var1);

      int getDocumentPathCount();

      List<? extends DocumentPathItemOrBuilder> getDocumentPathOrBuilderList();

      DocumentPathItemOrBuilder getDocumentPathOrBuilder(int var1);

      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasTableName();

      String getTableName();

      ByteString getTableNameBytes();

      boolean hasSchemaName();

      String getSchemaName();

      ByteString getSchemaNameBytes();
   }

   public static final class DocumentPathItem extends GeneratedMessageV3 implements DocumentPathItemOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int VALUE_FIELD_NUMBER = 2;
      private volatile java.lang.Object value_;
      public static final int INDEX_FIELD_NUMBER = 3;
      private int index_;
      private byte memoizedIsInitialized;
      private static final DocumentPathItem DEFAULT_INSTANCE = new DocumentPathItem();
      /** @deprecated */
      @Deprecated
      public static final Parser<DocumentPathItem> PARSER = new AbstractParser<DocumentPathItem>() {
         public DocumentPathItem parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new DocumentPathItem(input, extensionRegistry);
         }
      };

      private DocumentPathItem(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private DocumentPathItem() {
         this.memoizedIsInitialized = -1;
         this.type_ = 1;
         this.value_ = "";
      }

      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new DocumentPathItem();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private DocumentPathItem(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 8:
                        int rawValue = input.readEnum();
                        Type value = MysqlxExpr.DocumentPathItem.Type.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.type_ = rawValue;
                        }
                        break;
                     case 18:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.value_ = bs;
                        break;
                     case 24:
                        this.bitField0_ |= 4;
                        this.index_ = input.readUInt32();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable.ensureFieldAccessorsInitialized(DocumentPathItem.class, Builder.class);
      }

      public boolean hasType() {
         return (this.bitField0_ & 1) != 0;
      }

      public Type getType() {
         Type result = MysqlxExpr.DocumentPathItem.Type.valueOf(this.type_);
         return result == null ? MysqlxExpr.DocumentPathItem.Type.MEMBER : result;
      }

      public boolean hasValue() {
         return (this.bitField0_ & 2) != 0;
      }

      public String getValue() {
         java.lang.Object ref = this.value_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.value_ = s;
            }

            return s;
         }
      }

      public ByteString getValueBytes() {
         java.lang.Object ref = this.value_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.value_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasIndex() {
         return (this.bitField0_ & 4) != 0;
      }

      public int getIndex() {
         return this.index_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasType()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeEnum(1, this.type_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.value_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeUInt32(3, this.index_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeEnumSize(1, this.type_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.value_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeUInt32Size(3, this.index_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof DocumentPathItem)) {
            return super.equals(obj);
         } else {
            DocumentPathItem other = (DocumentPathItem)obj;
            if (this.hasType() != other.hasType()) {
               return false;
            } else if (this.hasType() && this.type_ != other.type_) {
               return false;
            } else if (this.hasValue() != other.hasValue()) {
               return false;
            } else if (this.hasValue() && !this.getValue().equals(other.getValue())) {
               return false;
            } else if (this.hasIndex() != other.hasIndex()) {
               return false;
            } else if (this.hasIndex() && this.getIndex() != other.getIndex()) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasType()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.type_;
            }

            if (this.hasValue()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getValue().hashCode();
            }

            if (this.hasIndex()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getIndex();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static DocumentPathItem parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (DocumentPathItem)PARSER.parseFrom(data);
      }

      public static DocumentPathItem parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (DocumentPathItem)PARSER.parseFrom(data, extensionRegistry);
      }

      public static DocumentPathItem parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (DocumentPathItem)PARSER.parseFrom(data);
      }

      public static DocumentPathItem parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (DocumentPathItem)PARSER.parseFrom(data, extensionRegistry);
      }

      public static DocumentPathItem parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (DocumentPathItem)PARSER.parseFrom(data);
      }

      public static DocumentPathItem parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (DocumentPathItem)PARSER.parseFrom(data, extensionRegistry);
      }

      public static DocumentPathItem parseFrom(InputStream input) throws IOException {
         return (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static DocumentPathItem parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static DocumentPathItem parseDelimitedFrom(InputStream input) throws IOException {
         return (DocumentPathItem)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static DocumentPathItem parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (DocumentPathItem)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static DocumentPathItem parseFrom(CodedInputStream input) throws IOException {
         return (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static DocumentPathItem parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (DocumentPathItem)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(DocumentPathItem prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static DocumentPathItem getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<DocumentPathItem> parser() {
         return PARSER;
      }

      public Parser<DocumentPathItem> getParserForType() {
         return PARSER;
      }

      public DocumentPathItem getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      DocumentPathItem(GeneratedMessageV3.Builder x0, java.lang.Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      DocumentPathItem(CodedInputStream x0, ExtensionRegistryLite x1, java.lang.Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements DocumentPathItemOrBuilder {
         private int bitField0_;
         private int type_;
         private java.lang.Object value_;
         private int index_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable.ensureFieldAccessorsInitialized(DocumentPathItem.class, Builder.class);
         }

         private Builder() {
            this.type_ = 1;
            this.value_ = "";
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.type_ = 1;
            this.value_ = "";
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.DocumentPathItem.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.type_ = 1;
            this.bitField0_ &= -2;
            this.value_ = "";
            this.bitField0_ &= -3;
            this.index_ = 0;
            this.bitField0_ &= -5;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
         }

         public DocumentPathItem getDefaultInstanceForType() {
            return MysqlxExpr.DocumentPathItem.getDefaultInstance();
         }

         public DocumentPathItem build() {
            DocumentPathItem result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public DocumentPathItem buildPartial() {
            DocumentPathItem result = new DocumentPathItem(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.type_ = this.type_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.value_ = this.value_;
            if ((from_bitField0_ & 4) != 0) {
               result.index_ = this.index_;
               to_bitField0_ |= 4;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof DocumentPathItem) {
               return this.mergeFrom((DocumentPathItem)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(DocumentPathItem other) {
            if (other == MysqlxExpr.DocumentPathItem.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasType()) {
                  this.setType(other.getType());
               }

               if (other.hasValue()) {
                  this.bitField0_ |= 2;
                  this.value_ = other.value_;
                  this.onChanged();
               }

               if (other.hasIndex()) {
                  this.setIndex(other.getIndex());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasType();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            DocumentPathItem parsedMessage = null;

            try {
               parsedMessage = (DocumentPathItem)MysqlxExpr.DocumentPathItem.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (DocumentPathItem)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
         }

         public Type getType() {
            Type result = MysqlxExpr.DocumentPathItem.Type.valueOf(this.type_);
            return result == null ? MysqlxExpr.DocumentPathItem.Type.MEMBER : result;
         }

         public Builder setType(Type value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.type_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearType() {
            this.bitField0_ &= -2;
            this.type_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasValue() {
            return (this.bitField0_ & 2) != 0;
         }

         public String getValue() {
            java.lang.Object ref = this.value_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.value_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getValueBytes() {
            java.lang.Object ref = this.value_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.value_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setValue(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.value_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearValue() {
            this.bitField0_ &= -3;
            this.value_ = MysqlxExpr.DocumentPathItem.getDefaultInstance().getValue();
            this.onChanged();
            return this;
         }

         public Builder setValueBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.value_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasIndex() {
            return (this.bitField0_ & 4) != 0;
         }

         public int getIndex() {
            return this.index_;
         }

         public Builder setIndex(int value) {
            this.bitField0_ |= 4;
            this.index_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearIndex() {
            this.bitField0_ &= -5;
            this.index_ = 0;
            this.onChanged();
            return this;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(java.lang.Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, java.lang.Object x1) {
            this(x0);
         }
      }

      public static enum Type implements ProtocolMessageEnum {
         MEMBER(1),
         MEMBER_ASTERISK(2),
         ARRAY_INDEX(3),
         ARRAY_INDEX_ASTERISK(4),
         DOUBLE_ASTERISK(5);

         public static final int MEMBER_VALUE = 1;
         public static final int MEMBER_ASTERISK_VALUE = 2;
         public static final int ARRAY_INDEX_VALUE = 3;
         public static final int ARRAY_INDEX_ASTERISK_VALUE = 4;
         public static final int DOUBLE_ASTERISK_VALUE = 5;
         private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
            public Type findValueByNumber(int number) {
               return MysqlxExpr.DocumentPathItem.Type.forNumber(number);
            }
         };
         private static final Type[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static Type valueOf(int value) {
            return forNumber(value);
         }

         public static Type forNumber(int value) {
            switch (value) {
               case 1:
                  return MEMBER;
               case 2:
                  return MEMBER_ASTERISK;
               case 3:
                  return ARRAY_INDEX;
               case 4:
                  return ARRAY_INDEX_ASTERISK;
               case 5:
                  return DOUBLE_ASTERISK;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<Type> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)MysqlxExpr.DocumentPathItem.getDescriptor().getEnumTypes().get(0);
         }

         public static Type valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Type(int value) {
            this.value = value;
         }
      }
   }

   public interface DocumentPathItemOrBuilder extends MessageOrBuilder {
      boolean hasType();

      DocumentPathItem.Type getType();

      boolean hasValue();

      String getValue();

      ByteString getValueBytes();

      boolean hasIndex();

      int getIndex();
   }

   public static final class Identifier extends GeneratedMessageV3 implements IdentifierOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile java.lang.Object name_;
      public static final int SCHEMA_NAME_FIELD_NUMBER = 2;
      private volatile java.lang.Object schemaName_;
      private byte memoizedIsInitialized;
      private static final Identifier DEFAULT_INSTANCE = new Identifier();
      /** @deprecated */
      @Deprecated
      public static final Parser<Identifier> PARSER = new AbstractParser<Identifier>() {
         public Identifier parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Identifier(input, extensionRegistry);
         }
      };

      private Identifier(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Identifier() {
         this.memoizedIsInitialized = -1;
         this.name_ = "";
         this.schemaName_ = "";
      }

      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Identifier();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Identifier(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  ByteString bs;
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     case 18:
                        bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.schemaName_ = bs;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw (new InvalidProtocolBufferException(var13)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable.ensureFieldAccessorsInitialized(Identifier.class, Builder.class);
      }

      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      public String getName() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      public ByteString getNameBytes() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasSchemaName() {
         return (this.bitField0_ & 2) != 0;
      }

      public String getSchemaName() {
         java.lang.Object ref = this.schemaName_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.schemaName_ = s;
            }

            return s;
         }
      }

      public ByteString getSchemaNameBytes() {
         java.lang.Object ref = this.schemaName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.schemaName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.schemaName_);
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.schemaName_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Identifier)) {
            return super.equals(obj);
         } else {
            Identifier other = (Identifier)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasSchemaName() != other.hasSchemaName()) {
               return false;
            } else if (this.hasSchemaName() && !this.getSchemaName().equals(other.getSchemaName())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasSchemaName()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getSchemaName().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Identifier parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Identifier)PARSER.parseFrom(data);
      }

      public static Identifier parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Identifier)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Identifier parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Identifier)PARSER.parseFrom(data);
      }

      public static Identifier parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Identifier)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Identifier parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Identifier)PARSER.parseFrom(data);
      }

      public static Identifier parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Identifier)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Identifier parseFrom(InputStream input) throws IOException {
         return (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Identifier parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Identifier parseDelimitedFrom(InputStream input) throws IOException {
         return (Identifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Identifier parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Identifier)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Identifier parseFrom(CodedInputStream input) throws IOException {
         return (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Identifier parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Identifier)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Identifier prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Identifier getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Identifier> parser() {
         return PARSER;
      }

      public Parser<Identifier> getParserForType() {
         return PARSER;
      }

      public Identifier getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Identifier(GeneratedMessageV3.Builder x0, java.lang.Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Identifier(CodedInputStream x0, ExtensionRegistryLite x1, java.lang.Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements IdentifierOrBuilder {
         private int bitField0_;
         private java.lang.Object name_;
         private java.lang.Object schemaName_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable.ensureFieldAccessorsInitialized(Identifier.class, Builder.class);
         }

         private Builder() {
            this.name_ = "";
            this.schemaName_ = "";
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.name_ = "";
            this.schemaName_ = "";
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Identifier.alwaysUseFieldBuilders) {
            }

         }

         public Builder clear() {
            super.clear();
            this.name_ = "";
            this.bitField0_ &= -2;
            this.schemaName_ = "";
            this.bitField0_ &= -3;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
         }

         public Identifier getDefaultInstanceForType() {
            return MysqlxExpr.Identifier.getDefaultInstance();
         }

         public Identifier build() {
            Identifier result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Identifier buildPartial() {
            Identifier result = new Identifier(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.schemaName_ = this.schemaName_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Identifier) {
               return this.mergeFrom((Identifier)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Identifier other) {
            if (other == MysqlxExpr.Identifier.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (other.hasSchemaName()) {
                  this.bitField0_ |= 2;
                  this.schemaName_ = other.schemaName_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            return this.hasName();
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Identifier parsedMessage = null;

            try {
               parsedMessage = (Identifier)MysqlxExpr.Identifier.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Identifier)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         public String getName() {
            java.lang.Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getNameBytes() {
            java.lang.Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxExpr.Identifier.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasSchemaName() {
            return (this.bitField0_ & 2) != 0;
         }

         public String getSchemaName() {
            java.lang.Object ref = this.schemaName_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.schemaName_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getSchemaNameBytes() {
            java.lang.Object ref = this.schemaName_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.schemaName_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setSchemaName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.schemaName_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearSchemaName() {
            this.bitField0_ &= -3;
            this.schemaName_ = MysqlxExpr.Identifier.getDefaultInstance().getSchemaName();
            this.onChanged();
            return this;
         }

         public Builder setSchemaNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.schemaName_ = value;
               this.onChanged();
               return this;
            }
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(java.lang.Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, java.lang.Object x1) {
            this(x0);
         }
      }
   }

   public interface IdentifierOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasSchemaName();

      String getSchemaName();

      ByteString getSchemaNameBytes();
   }

   public static final class Expr extends GeneratedMessageV3 implements ExprOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int IDENTIFIER_FIELD_NUMBER = 2;
      private ColumnIdentifier identifier_;
      public static final int VARIABLE_FIELD_NUMBER = 3;
      private volatile java.lang.Object variable_;
      public static final int LITERAL_FIELD_NUMBER = 4;
      private MysqlxDatatypes.Scalar literal_;
      public static final int FUNCTION_CALL_FIELD_NUMBER = 5;
      private FunctionCall functionCall_;
      public static final int OPERATOR_FIELD_NUMBER = 6;
      private Operator operator_;
      public static final int POSITION_FIELD_NUMBER = 7;
      private int position_;
      public static final int OBJECT_FIELD_NUMBER = 8;
      private Object object_;
      public static final int ARRAY_FIELD_NUMBER = 9;
      private Array array_;
      private byte memoizedIsInitialized;
      private static final Expr DEFAULT_INSTANCE = new Expr();
      /** @deprecated */
      @Deprecated
      public static final Parser<Expr> PARSER = new AbstractParser<Expr>() {
         public Expr parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Expr(input, extensionRegistry);
         }
      };

      private Expr(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
         this.memoizedIsInitialized = -1;
      }

      private Expr() {
         this.memoizedIsInitialized = -1;
         this.type_ = 1;
         this.variable_ = "";
      }

      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new Expr();
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Expr(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while(!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 8:
                        int rawValue = input.readEnum();
                        Type value = MysqlxExpr.Expr.Type.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.type_ = rawValue;
                        }
                        break;
                     case 18:
                        ColumnIdentifier.Builder subBuilder = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilder = this.identifier_.toBuilder();
                        }

                        this.identifier_ = (ColumnIdentifier)input.readMessage(MysqlxExpr.ColumnIdentifier.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.identifier_);
                           this.identifier_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 2;
                        break;
                     case 26:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 4;
                        this.variable_ = bs;
                        break;
                     case 34:
                        MysqlxDatatypes.Scalar.Builder subBuilder = null;
                        if ((this.bitField0_ & 8) != 0) {
                           subBuilder = this.literal_.toBuilder();
                        }

                        this.literal_ = (MysqlxDatatypes.Scalar)input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.literal_);
                           this.literal_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 8;
                        break;
                     case 42:
                        FunctionCall.Builder subBuilder = null;
                        if ((this.bitField0_ & 16) != 0) {
                           subBuilder = this.functionCall_.toBuilder();
                        }

                        this.functionCall_ = (FunctionCall)input.readMessage(MysqlxExpr.FunctionCall.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.functionCall_);
                           this.functionCall_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 16;
                        break;
                     case 50:
                        Operator.Builder subBuilder = null;
                        if ((this.bitField0_ & 32) != 0) {
                           subBuilder = this.operator_.toBuilder();
                        }

                        this.operator_ = (Operator)input.readMessage(MysqlxExpr.Operator.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.operator_);
                           this.operator_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 32;
                        break;
                     case 56:
                        this.bitField0_ |= 64;
                        this.position_ = input.readUInt32();
                        break;
                     case 66:
                        Object.Builder subBuilder = null;
                        if ((this.bitField0_ & 128) != 0) {
                           subBuilder = this.object_.toBuilder();
                        }

                        this.object_ = (Object)input.readMessage(MysqlxExpr.Object.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.object_);
                           this.object_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 128;
                        break;
                     case 74:
                        Array.Builder subBuilder = null;
                        if ((this.bitField0_ & 256) != 0) {
                           subBuilder = this.array_.toBuilder();
                        }

                        this.array_ = (Array)input.readMessage(MysqlxExpr.Array.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.array_);
                           this.array_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 256;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw (new InvalidProtocolBufferException(var14)).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }

         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_fieldAccessorTable.ensureFieldAccessorsInitialized(Expr.class, Builder.class);
      }

      public boolean hasType() {
         return (this.bitField0_ & 1) != 0;
      }

      public Type getType() {
         Type result = MysqlxExpr.Expr.Type.valueOf(this.type_);
         return result == null ? MysqlxExpr.Expr.Type.IDENT : result;
      }

      public boolean hasIdentifier() {
         return (this.bitField0_ & 2) != 0;
      }

      public ColumnIdentifier getIdentifier() {
         return this.identifier_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
      }

      public ColumnIdentifierOrBuilder getIdentifierOrBuilder() {
         return this.identifier_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
      }

      public boolean hasVariable() {
         return (this.bitField0_ & 4) != 0;
      }

      public String getVariable() {
         java.lang.Object ref = this.variable_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.variable_ = s;
            }

            return s;
         }
      }

      public ByteString getVariableBytes() {
         java.lang.Object ref = this.variable_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.variable_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public boolean hasLiteral() {
         return (this.bitField0_ & 8) != 0;
      }

      public MysqlxDatatypes.Scalar getLiteral() {
         return this.literal_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
      }

      public MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder() {
         return this.literal_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
      }

      public boolean hasFunctionCall() {
         return (this.bitField0_ & 16) != 0;
      }

      public FunctionCall getFunctionCall() {
         return this.functionCall_ == null ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
      }

      public FunctionCallOrBuilder getFunctionCallOrBuilder() {
         return this.functionCall_ == null ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
      }

      public boolean hasOperator() {
         return (this.bitField0_ & 32) != 0;
      }

      public Operator getOperator() {
         return this.operator_ == null ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
      }

      public OperatorOrBuilder getOperatorOrBuilder() {
         return this.operator_ == null ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
      }

      public boolean hasPosition() {
         return (this.bitField0_ & 64) != 0;
      }

      public int getPosition() {
         return this.position_;
      }

      public boolean hasObject() {
         return (this.bitField0_ & 128) != 0;
      }

      public Object getObject() {
         return this.object_ == null ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
      }

      public ObjectOrBuilder getObjectOrBuilder() {
         return this.object_ == null ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
      }

      public boolean hasArray() {
         return (this.bitField0_ & 256) != 0;
      }

      public Array getArray() {
         return this.array_ == null ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
      }

      public ArrayOrBuilder getArrayOrBuilder() {
         return this.array_ == null ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
      }

      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasType()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasIdentifier() && !this.getIdentifier().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasLiteral() && !this.getLiteral().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasFunctionCall() && !this.getFunctionCall().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasOperator() && !this.getOperator().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasObject() && !this.getObject().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasArray() && !this.getArray().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeEnum(1, this.type_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(2, this.getIdentifier());
         }

         if ((this.bitField0_ & 4) != 0) {
            GeneratedMessageV3.writeString(output, 3, this.variable_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeMessage(4, this.getLiteral());
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeMessage(5, this.getFunctionCall());
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeMessage(6, this.getOperator());
         }

         if ((this.bitField0_ & 64) != 0) {
            output.writeUInt32(7, this.position_);
         }

         if ((this.bitField0_ & 128) != 0) {
            output.writeMessage(8, this.getObject());
         }

         if ((this.bitField0_ & 256) != 0) {
            output.writeMessage(9, this.getArray());
         }

         this.unknownFields.writeTo(output);
      }

      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeEnumSize(1, this.type_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeMessageSize(2, this.getIdentifier());
            }

            if ((this.bitField0_ & 4) != 0) {
               size += GeneratedMessageV3.computeStringSize(3, this.variable_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeMessageSize(4, this.getLiteral());
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeMessageSize(5, this.getFunctionCall());
            }

            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeMessageSize(6, this.getOperator());
            }

            if ((this.bitField0_ & 64) != 0) {
               size += CodedOutputStream.computeUInt32Size(7, this.position_);
            }

            if ((this.bitField0_ & 128) != 0) {
               size += CodedOutputStream.computeMessageSize(8, this.getObject());
            }

            if ((this.bitField0_ & 256) != 0) {
               size += CodedOutputStream.computeMessageSize(9, this.getArray());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof Expr)) {
            return super.equals(obj);
         } else {
            Expr other = (Expr)obj;
            if (this.hasType() != other.hasType()) {
               return false;
            } else if (this.hasType() && this.type_ != other.type_) {
               return false;
            } else if (this.hasIdentifier() != other.hasIdentifier()) {
               return false;
            } else if (this.hasIdentifier() && !this.getIdentifier().equals(other.getIdentifier())) {
               return false;
            } else if (this.hasVariable() != other.hasVariable()) {
               return false;
            } else if (this.hasVariable() && !this.getVariable().equals(other.getVariable())) {
               return false;
            } else if (this.hasLiteral() != other.hasLiteral()) {
               return false;
            } else if (this.hasLiteral() && !this.getLiteral().equals(other.getLiteral())) {
               return false;
            } else if (this.hasFunctionCall() != other.hasFunctionCall()) {
               return false;
            } else if (this.hasFunctionCall() && !this.getFunctionCall().equals(other.getFunctionCall())) {
               return false;
            } else if (this.hasOperator() != other.hasOperator()) {
               return false;
            } else if (this.hasOperator() && !this.getOperator().equals(other.getOperator())) {
               return false;
            } else if (this.hasPosition() != other.hasPosition()) {
               return false;
            } else if (this.hasPosition() && this.getPosition() != other.getPosition()) {
               return false;
            } else if (this.hasObject() != other.hasObject()) {
               return false;
            } else if (this.hasObject() && !this.getObject().equals(other.getObject())) {
               return false;
            } else if (this.hasArray() != other.hasArray()) {
               return false;
            } else if (this.hasArray() && !this.getArray().equals(other.getArray())) {
               return false;
            } else {
               return this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasType()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.type_;
            }

            if (this.hasIdentifier()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getIdentifier().hashCode();
            }

            if (this.hasVariable()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getVariable().hashCode();
            }

            if (this.hasLiteral()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getLiteral().hashCode();
            }

            if (this.hasFunctionCall()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getFunctionCall().hashCode();
            }

            if (this.hasOperator()) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getOperator().hashCode();
            }

            if (this.hasPosition()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getPosition();
            }

            if (this.hasObject()) {
               hash = 37 * hash + 8;
               hash = 53 * hash + this.getObject().hashCode();
            }

            if (this.hasArray()) {
               hash = 37 * hash + 9;
               hash = 53 * hash + this.getArray().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static Expr parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return (Expr)PARSER.parseFrom(data);
      }

      public static Expr parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Expr)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Expr parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return (Expr)PARSER.parseFrom(data);
      }

      public static Expr parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Expr)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Expr parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return (Expr)PARSER.parseFrom(data);
      }

      public static Expr parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return (Expr)PARSER.parseFrom(data, extensionRegistry);
      }

      public static Expr parseFrom(InputStream input) throws IOException {
         return (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Expr parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static Expr parseDelimitedFrom(InputStream input) throws IOException {
         return (Expr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static Expr parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Expr)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static Expr parseFrom(CodedInputStream input) throws IOException {
         return (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static Expr parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return (Expr)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public Builder newBuilderForType() {
         return newBuilder();
      }

      public static Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static Builder newBuilder(Expr prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
      }

      protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         Builder builder = new Builder(parent);
         return builder;
      }

      public static Expr getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<Expr> parser() {
         return PARSER;
      }

      public Parser<Expr> getParserForType() {
         return PARSER;
      }

      public Expr getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      // $FF: synthetic method
      Expr(GeneratedMessageV3.Builder x0, java.lang.Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      Expr(CodedInputStream x0, ExtensionRegistryLite x1, java.lang.Object x2) throws InvalidProtocolBufferException {
         this(x0, x1);
      }

      public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ExprOrBuilder {
         private int bitField0_;
         private int type_;
         private ColumnIdentifier identifier_;
         private SingleFieldBuilderV3<ColumnIdentifier, ColumnIdentifier.Builder, ColumnIdentifierOrBuilder> identifierBuilder_;
         private java.lang.Object variable_;
         private MysqlxDatatypes.Scalar literal_;
         private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> literalBuilder_;
         private FunctionCall functionCall_;
         private SingleFieldBuilderV3<FunctionCall, FunctionCall.Builder, FunctionCallOrBuilder> functionCallBuilder_;
         private Operator operator_;
         private SingleFieldBuilderV3<Operator, Operator.Builder, OperatorOrBuilder> operatorBuilder_;
         private int position_;
         private Object object_;
         private SingleFieldBuilderV3<Object, Object.Builder, ObjectOrBuilder> objectBuilder_;
         private Array array_;
         private SingleFieldBuilderV3<Array, Array.Builder, ArrayOrBuilder> arrayBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
         }

         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_fieldAccessorTable.ensureFieldAccessorsInitialized(Expr.class, Builder.class);
         }

         private Builder() {
            this.type_ = 1;
            this.variable_ = "";
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.type_ = 1;
            this.variable_ = "";
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Expr.alwaysUseFieldBuilders) {
               this.getIdentifierFieldBuilder();
               this.getLiteralFieldBuilder();
               this.getFunctionCallFieldBuilder();
               this.getOperatorFieldBuilder();
               this.getObjectFieldBuilder();
               this.getArrayFieldBuilder();
            }

         }

         public Builder clear() {
            super.clear();
            this.type_ = 1;
            this.bitField0_ &= -2;
            if (this.identifierBuilder_ == null) {
               this.identifier_ = null;
            } else {
               this.identifierBuilder_.clear();
            }

            this.bitField0_ &= -3;
            this.variable_ = "";
            this.bitField0_ &= -5;
            if (this.literalBuilder_ == null) {
               this.literal_ = null;
            } else {
               this.literalBuilder_.clear();
            }

            this.bitField0_ &= -9;
            if (this.functionCallBuilder_ == null) {
               this.functionCall_ = null;
            } else {
               this.functionCallBuilder_.clear();
            }

            this.bitField0_ &= -17;
            if (this.operatorBuilder_ == null) {
               this.operator_ = null;
            } else {
               this.operatorBuilder_.clear();
            }

            this.bitField0_ &= -33;
            this.position_ = 0;
            this.bitField0_ &= -65;
            if (this.objectBuilder_ == null) {
               this.object_ = null;
            } else {
               this.objectBuilder_.clear();
            }

            this.bitField0_ &= -129;
            if (this.arrayBuilder_ == null) {
               this.array_ = null;
            } else {
               this.arrayBuilder_.clear();
            }

            this.bitField0_ &= -257;
            return this;
         }

         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
         }

         public Expr getDefaultInstanceForType() {
            return MysqlxExpr.Expr.getDefaultInstance();
         }

         public Expr build() {
            Expr result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public Expr buildPartial() {
            Expr result = new Expr(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.type_ = this.type_;
            if ((from_bitField0_ & 2) != 0) {
               if (this.identifierBuilder_ == null) {
                  result.identifier_ = this.identifier_;
               } else {
                  result.identifier_ = (ColumnIdentifier)this.identifierBuilder_.build();
               }

               to_bitField0_ |= 2;
            }

            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.variable_ = this.variable_;
            if ((from_bitField0_ & 8) != 0) {
               if (this.literalBuilder_ == null) {
                  result.literal_ = this.literal_;
               } else {
                  result.literal_ = (MysqlxDatatypes.Scalar)this.literalBuilder_.build();
               }

               to_bitField0_ |= 8;
            }

            if ((from_bitField0_ & 16) != 0) {
               if (this.functionCallBuilder_ == null) {
                  result.functionCall_ = this.functionCall_;
               } else {
                  result.functionCall_ = (FunctionCall)this.functionCallBuilder_.build();
               }

               to_bitField0_ |= 16;
            }

            if ((from_bitField0_ & 32) != 0) {
               if (this.operatorBuilder_ == null) {
                  result.operator_ = this.operator_;
               } else {
                  result.operator_ = (Operator)this.operatorBuilder_.build();
               }

               to_bitField0_ |= 32;
            }

            if ((from_bitField0_ & 64) != 0) {
               result.position_ = this.position_;
               to_bitField0_ |= 64;
            }

            if ((from_bitField0_ & 128) != 0) {
               if (this.objectBuilder_ == null) {
                  result.object_ = this.object_;
               } else {
                  result.object_ = (Object)this.objectBuilder_.build();
               }

               to_bitField0_ |= 128;
            }

            if ((from_bitField0_ & 256) != 0) {
               if (this.arrayBuilder_ == null) {
                  result.array_ = this.array_;
               } else {
                  result.array_ = (Array)this.arrayBuilder_.build();
               }

               to_bitField0_ |= 256;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public Builder clone() {
            return (Builder)super.clone();
         }

         public Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.setField(field, value);
         }

         public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
         }

         public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
         }

         public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
         }

         public Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (Builder)super.addRepeatedField(field, value);
         }

         public Builder mergeFrom(Message other) {
            if (other instanceof Expr) {
               return this.mergeFrom((Expr)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public Builder mergeFrom(Expr other) {
            if (other == MysqlxExpr.Expr.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasType()) {
                  this.setType(other.getType());
               }

               if (other.hasIdentifier()) {
                  this.mergeIdentifier(other.getIdentifier());
               }

               if (other.hasVariable()) {
                  this.bitField0_ |= 4;
                  this.variable_ = other.variable_;
                  this.onChanged();
               }

               if (other.hasLiteral()) {
                  this.mergeLiteral(other.getLiteral());
               }

               if (other.hasFunctionCall()) {
                  this.mergeFunctionCall(other.getFunctionCall());
               }

               if (other.hasOperator()) {
                  this.mergeOperator(other.getOperator());
               }

               if (other.hasPosition()) {
                  this.setPosition(other.getPosition());
               }

               if (other.hasObject()) {
                  this.mergeObject(other.getObject());
               }

               if (other.hasArray()) {
                  this.mergeArray(other.getArray());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         public final boolean isInitialized() {
            if (!this.hasType()) {
               return false;
            } else if (this.hasIdentifier() && !this.getIdentifier().isInitialized()) {
               return false;
            } else if (this.hasLiteral() && !this.getLiteral().isInitialized()) {
               return false;
            } else if (this.hasFunctionCall() && !this.getFunctionCall().isInitialized()) {
               return false;
            } else if (this.hasOperator() && !this.getOperator().isInitialized()) {
               return false;
            } else if (this.hasObject() && !this.getObject().isInitialized()) {
               return false;
            } else {
               return !this.hasArray() || this.getArray().isInitialized();
            }
         }

         public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Expr parsedMessage = null;

            try {
               parsedMessage = (Expr)MysqlxExpr.Expr.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (Expr)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }

            }

            return this;
         }

         public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
         }

         public Type getType() {
            Type result = MysqlxExpr.Expr.Type.valueOf(this.type_);
            return result == null ? MysqlxExpr.Expr.Type.IDENT : result;
         }

         public Builder setType(Type value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.type_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public Builder clearType() {
            this.bitField0_ &= -2;
            this.type_ = 1;
            this.onChanged();
            return this;
         }

         public boolean hasIdentifier() {
            return (this.bitField0_ & 2) != 0;
         }

         public ColumnIdentifier getIdentifier() {
            if (this.identifierBuilder_ == null) {
               return this.identifier_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
            } else {
               return (ColumnIdentifier)this.identifierBuilder_.getMessage();
            }
         }

         public Builder setIdentifier(ColumnIdentifier value) {
            if (this.identifierBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.identifier_ = value;
               this.onChanged();
            } else {
               this.identifierBuilder_.setMessage(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder setIdentifier(ColumnIdentifier.Builder builderForValue) {
            if (this.identifierBuilder_ == null) {
               this.identifier_ = builderForValue.build();
               this.onChanged();
            } else {
               this.identifierBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder mergeIdentifier(ColumnIdentifier value) {
            if (this.identifierBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.identifier_ != null && this.identifier_ != MysqlxExpr.ColumnIdentifier.getDefaultInstance()) {
                  this.identifier_ = MysqlxExpr.ColumnIdentifier.newBuilder(this.identifier_).mergeFrom(value).buildPartial();
               } else {
                  this.identifier_ = value;
               }

               this.onChanged();
            } else {
               this.identifierBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public Builder clearIdentifier() {
            if (this.identifierBuilder_ == null) {
               this.identifier_ = null;
               this.onChanged();
            } else {
               this.identifierBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public ColumnIdentifier.Builder getIdentifierBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return (ColumnIdentifier.Builder)this.getIdentifierFieldBuilder().getBuilder();
         }

         public ColumnIdentifierOrBuilder getIdentifierOrBuilder() {
            if (this.identifierBuilder_ != null) {
               return (ColumnIdentifierOrBuilder)this.identifierBuilder_.getMessageOrBuilder();
            } else {
               return this.identifier_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
            }
         }

         private SingleFieldBuilderV3<ColumnIdentifier, ColumnIdentifier.Builder, ColumnIdentifierOrBuilder> getIdentifierFieldBuilder() {
            if (this.identifierBuilder_ == null) {
               this.identifierBuilder_ = new SingleFieldBuilderV3(this.getIdentifier(), this.getParentForChildren(), this.isClean());
               this.identifier_ = null;
            }

            return this.identifierBuilder_;
         }

         public boolean hasVariable() {
            return (this.bitField0_ & 4) != 0;
         }

         public String getVariable() {
            java.lang.Object ref = this.variable_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.variable_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         public ByteString getVariableBytes() {
            java.lang.Object ref = this.variable_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.variable_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public Builder setVariable(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.variable_ = value;
               this.onChanged();
               return this;
            }
         }

         public Builder clearVariable() {
            this.bitField0_ &= -5;
            this.variable_ = MysqlxExpr.Expr.getDefaultInstance().getVariable();
            this.onChanged();
            return this;
         }

         public Builder setVariableBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.variable_ = value;
               this.onChanged();
               return this;
            }
         }

         public boolean hasLiteral() {
            return (this.bitField0_ & 8) != 0;
         }

         public MysqlxDatatypes.Scalar getLiteral() {
            if (this.literalBuilder_ == null) {
               return this.literal_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
            } else {
               return (MysqlxDatatypes.Scalar)this.literalBuilder_.getMessage();
            }
         }

         public Builder setLiteral(MysqlxDatatypes.Scalar value) {
            if (this.literalBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.literal_ = value;
               this.onChanged();
            } else {
               this.literalBuilder_.setMessage(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public Builder setLiteral(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.literalBuilder_ == null) {
               this.literal_ = builderForValue.build();
               this.onChanged();
            } else {
               this.literalBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 8;
            return this;
         }

         public Builder mergeLiteral(MysqlxDatatypes.Scalar value) {
            if (this.literalBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0 && this.literal_ != null && this.literal_ != MysqlxDatatypes.Scalar.getDefaultInstance()) {
                  this.literal_ = MysqlxDatatypes.Scalar.newBuilder(this.literal_).mergeFrom(value).buildPartial();
               } else {
                  this.literal_ = value;
               }

               this.onChanged();
            } else {
               this.literalBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public Builder clearLiteral() {
            if (this.literalBuilder_ == null) {
               this.literal_ = null;
               this.onChanged();
            } else {
               this.literalBuilder_.clear();
            }

            this.bitField0_ &= -9;
            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getLiteralBuilder() {
            this.bitField0_ |= 8;
            this.onChanged();
            return (MysqlxDatatypes.Scalar.Builder)this.getLiteralFieldBuilder().getBuilder();
         }

         public MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder() {
            if (this.literalBuilder_ != null) {
               return (MysqlxDatatypes.ScalarOrBuilder)this.literalBuilder_.getMessageOrBuilder();
            } else {
               return this.literal_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
            }
         }

         private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getLiteralFieldBuilder() {
            if (this.literalBuilder_ == null) {
               this.literalBuilder_ = new SingleFieldBuilderV3(this.getLiteral(), this.getParentForChildren(), this.isClean());
               this.literal_ = null;
            }

            return this.literalBuilder_;
         }

         public boolean hasFunctionCall() {
            return (this.bitField0_ & 16) != 0;
         }

         public FunctionCall getFunctionCall() {
            if (this.functionCallBuilder_ == null) {
               return this.functionCall_ == null ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
            } else {
               return (FunctionCall)this.functionCallBuilder_.getMessage();
            }
         }

         public Builder setFunctionCall(FunctionCall value) {
            if (this.functionCallBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.functionCall_ = value;
               this.onChanged();
            } else {
               this.functionCallBuilder_.setMessage(value);
            }

            this.bitField0_ |= 16;
            return this;
         }

         public Builder setFunctionCall(FunctionCall.Builder builderForValue) {
            if (this.functionCallBuilder_ == null) {
               this.functionCall_ = builderForValue.build();
               this.onChanged();
            } else {
               this.functionCallBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 16;
            return this;
         }

         public Builder mergeFunctionCall(FunctionCall value) {
            if (this.functionCallBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0 && this.functionCall_ != null && this.functionCall_ != MysqlxExpr.FunctionCall.getDefaultInstance()) {
                  this.functionCall_ = MysqlxExpr.FunctionCall.newBuilder(this.functionCall_).mergeFrom(value).buildPartial();
               } else {
                  this.functionCall_ = value;
               }

               this.onChanged();
            } else {
               this.functionCallBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 16;
            return this;
         }

         public Builder clearFunctionCall() {
            if (this.functionCallBuilder_ == null) {
               this.functionCall_ = null;
               this.onChanged();
            } else {
               this.functionCallBuilder_.clear();
            }

            this.bitField0_ &= -17;
            return this;
         }

         public FunctionCall.Builder getFunctionCallBuilder() {
            this.bitField0_ |= 16;
            this.onChanged();
            return (FunctionCall.Builder)this.getFunctionCallFieldBuilder().getBuilder();
         }

         public FunctionCallOrBuilder getFunctionCallOrBuilder() {
            if (this.functionCallBuilder_ != null) {
               return (FunctionCallOrBuilder)this.functionCallBuilder_.getMessageOrBuilder();
            } else {
               return this.functionCall_ == null ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
            }
         }

         private SingleFieldBuilderV3<FunctionCall, FunctionCall.Builder, FunctionCallOrBuilder> getFunctionCallFieldBuilder() {
            if (this.functionCallBuilder_ == null) {
               this.functionCallBuilder_ = new SingleFieldBuilderV3(this.getFunctionCall(), this.getParentForChildren(), this.isClean());
               this.functionCall_ = null;
            }

            return this.functionCallBuilder_;
         }

         public boolean hasOperator() {
            return (this.bitField0_ & 32) != 0;
         }

         public Operator getOperator() {
            if (this.operatorBuilder_ == null) {
               return this.operator_ == null ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
            } else {
               return (Operator)this.operatorBuilder_.getMessage();
            }
         }

         public Builder setOperator(Operator value) {
            if (this.operatorBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.operator_ = value;
               this.onChanged();
            } else {
               this.operatorBuilder_.setMessage(value);
            }

            this.bitField0_ |= 32;
            return this;
         }

         public Builder setOperator(Operator.Builder builderForValue) {
            if (this.operatorBuilder_ == null) {
               this.operator_ = builderForValue.build();
               this.onChanged();
            } else {
               this.operatorBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 32;
            return this;
         }

         public Builder mergeOperator(Operator value) {
            if (this.operatorBuilder_ == null) {
               if ((this.bitField0_ & 32) != 0 && this.operator_ != null && this.operator_ != MysqlxExpr.Operator.getDefaultInstance()) {
                  this.operator_ = MysqlxExpr.Operator.newBuilder(this.operator_).mergeFrom(value).buildPartial();
               } else {
                  this.operator_ = value;
               }

               this.onChanged();
            } else {
               this.operatorBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 32;
            return this;
         }

         public Builder clearOperator() {
            if (this.operatorBuilder_ == null) {
               this.operator_ = null;
               this.onChanged();
            } else {
               this.operatorBuilder_.clear();
            }

            this.bitField0_ &= -33;
            return this;
         }

         public Operator.Builder getOperatorBuilder() {
            this.bitField0_ |= 32;
            this.onChanged();
            return (Operator.Builder)this.getOperatorFieldBuilder().getBuilder();
         }

         public OperatorOrBuilder getOperatorOrBuilder() {
            if (this.operatorBuilder_ != null) {
               return (OperatorOrBuilder)this.operatorBuilder_.getMessageOrBuilder();
            } else {
               return this.operator_ == null ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
            }
         }

         private SingleFieldBuilderV3<Operator, Operator.Builder, OperatorOrBuilder> getOperatorFieldBuilder() {
            if (this.operatorBuilder_ == null) {
               this.operatorBuilder_ = new SingleFieldBuilderV3(this.getOperator(), this.getParentForChildren(), this.isClean());
               this.operator_ = null;
            }

            return this.operatorBuilder_;
         }

         public boolean hasPosition() {
            return (this.bitField0_ & 64) != 0;
         }

         public int getPosition() {
            return this.position_;
         }

         public Builder setPosition(int value) {
            this.bitField0_ |= 64;
            this.position_ = value;
            this.onChanged();
            return this;
         }

         public Builder clearPosition() {
            this.bitField0_ &= -65;
            this.position_ = 0;
            this.onChanged();
            return this;
         }

         public boolean hasObject() {
            return (this.bitField0_ & 128) != 0;
         }

         public Object getObject() {
            if (this.objectBuilder_ == null) {
               return this.object_ == null ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
            } else {
               return (Object)this.objectBuilder_.getMessage();
            }
         }

         public Builder setObject(Object value) {
            if (this.objectBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.object_ = value;
               this.onChanged();
            } else {
               this.objectBuilder_.setMessage(value);
            }

            this.bitField0_ |= 128;
            return this;
         }

         public Builder setObject(Object.Builder builderForValue) {
            if (this.objectBuilder_ == null) {
               this.object_ = builderForValue.build();
               this.onChanged();
            } else {
               this.objectBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 128;
            return this;
         }

         public Builder mergeObject(Object value) {
            if (this.objectBuilder_ == null) {
               if ((this.bitField0_ & 128) != 0 && this.object_ != null && this.object_ != MysqlxExpr.Object.getDefaultInstance()) {
                  this.object_ = MysqlxExpr.Object.newBuilder(this.object_).mergeFrom(value).buildPartial();
               } else {
                  this.object_ = value;
               }

               this.onChanged();
            } else {
               this.objectBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 128;
            return this;
         }

         public Builder clearObject() {
            if (this.objectBuilder_ == null) {
               this.object_ = null;
               this.onChanged();
            } else {
               this.objectBuilder_.clear();
            }

            this.bitField0_ &= -129;
            return this;
         }

         public Object.Builder getObjectBuilder() {
            this.bitField0_ |= 128;
            this.onChanged();
            return (Object.Builder)this.getObjectFieldBuilder().getBuilder();
         }

         public ObjectOrBuilder getObjectOrBuilder() {
            if (this.objectBuilder_ != null) {
               return (ObjectOrBuilder)this.objectBuilder_.getMessageOrBuilder();
            } else {
               return this.object_ == null ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
            }
         }

         private SingleFieldBuilderV3<Object, Object.Builder, ObjectOrBuilder> getObjectFieldBuilder() {
            if (this.objectBuilder_ == null) {
               this.objectBuilder_ = new SingleFieldBuilderV3(this.getObject(), this.getParentForChildren(), this.isClean());
               this.object_ = null;
            }

            return this.objectBuilder_;
         }

         public boolean hasArray() {
            return (this.bitField0_ & 256) != 0;
         }

         public Array getArray() {
            if (this.arrayBuilder_ == null) {
               return this.array_ == null ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
            } else {
               return (Array)this.arrayBuilder_.getMessage();
            }
         }

         public Builder setArray(Array value) {
            if (this.arrayBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.array_ = value;
               this.onChanged();
            } else {
               this.arrayBuilder_.setMessage(value);
            }

            this.bitField0_ |= 256;
            return this;
         }

         public Builder setArray(Array.Builder builderForValue) {
            if (this.arrayBuilder_ == null) {
               this.array_ = builderForValue.build();
               this.onChanged();
            } else {
               this.arrayBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 256;
            return this;
         }

         public Builder mergeArray(Array value) {
            if (this.arrayBuilder_ == null) {
               if ((this.bitField0_ & 256) != 0 && this.array_ != null && this.array_ != MysqlxExpr.Array.getDefaultInstance()) {
                  this.array_ = MysqlxExpr.Array.newBuilder(this.array_).mergeFrom(value).buildPartial();
               } else {
                  this.array_ = value;
               }

               this.onChanged();
            } else {
               this.arrayBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 256;
            return this;
         }

         public Builder clearArray() {
            if (this.arrayBuilder_ == null) {
               this.array_ = null;
               this.onChanged();
            } else {
               this.arrayBuilder_.clear();
            }

            this.bitField0_ &= -257;
            return this;
         }

         public Array.Builder getArrayBuilder() {
            this.bitField0_ |= 256;
            this.onChanged();
            return (Array.Builder)this.getArrayFieldBuilder().getBuilder();
         }

         public ArrayOrBuilder getArrayOrBuilder() {
            if (this.arrayBuilder_ != null) {
               return (ArrayOrBuilder)this.arrayBuilder_.getMessageOrBuilder();
            } else {
               return this.array_ == null ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
            }
         }

         private SingleFieldBuilderV3<Array, Array.Builder, ArrayOrBuilder> getArrayFieldBuilder() {
            if (this.arrayBuilder_ == null) {
               this.arrayBuilder_ = new SingleFieldBuilderV3(this.getArray(), this.getParentForChildren(), this.isClean());
               this.array_ = null;
            }

            return this.arrayBuilder_;
         }

         public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
         }

         public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
         }

         // $FF: synthetic method
         Builder(java.lang.Object x0) {
            this();
         }

         // $FF: synthetic method
         Builder(GeneratedMessageV3.BuilderParent x0, java.lang.Object x1) {
            this(x0);
         }
      }

      public static enum Type implements ProtocolMessageEnum {
         IDENT(1),
         LITERAL(2),
         VARIABLE(3),
         FUNC_CALL(4),
         OPERATOR(5),
         PLACEHOLDER(6),
         OBJECT(7),
         ARRAY(8);

         public static final int IDENT_VALUE = 1;
         public static final int LITERAL_VALUE = 2;
         public static final int VARIABLE_VALUE = 3;
         public static final int FUNC_CALL_VALUE = 4;
         public static final int OPERATOR_VALUE = 5;
         public static final int PLACEHOLDER_VALUE = 6;
         public static final int OBJECT_VALUE = 7;
         public static final int ARRAY_VALUE = 8;
         private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() {
            public Type findValueByNumber(int number) {
               return MysqlxExpr.Expr.Type.forNumber(number);
            }
         };
         private static final Type[] VALUES = values();
         private final int value;

         public final int getNumber() {
            return this.value;
         }

         /** @deprecated */
         @Deprecated
         public static Type valueOf(int value) {
            return forNumber(value);
         }

         public static Type forNumber(int value) {
            switch (value) {
               case 1:
                  return IDENT;
               case 2:
                  return LITERAL;
               case 3:
                  return VARIABLE;
               case 4:
                  return FUNC_CALL;
               case 5:
                  return OPERATOR;
               case 6:
                  return PLACEHOLDER;
               case 7:
                  return OBJECT;
               case 8:
                  return ARRAY;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<Type> internalGetValueMap() {
            return internalValueMap;
         }

         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return (Descriptors.EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
         }

         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static final Descriptors.EnumDescriptor getDescriptor() {
            return (Descriptors.EnumDescriptor)MysqlxExpr.Expr.getDescriptor().getEnumTypes().get(0);
         }

         public static Type valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Type(int value) {
            this.value = value;
         }
      }
   }

   public interface ExprOrBuilder extends MessageOrBuilder {
      boolean hasType();

      Expr.Type getType();

      boolean hasIdentifier();

      ColumnIdentifier getIdentifier();

      ColumnIdentifierOrBuilder getIdentifierOrBuilder();

      boolean hasVariable();

      String getVariable();

      ByteString getVariableBytes();

      boolean hasLiteral();

      MysqlxDatatypes.Scalar getLiteral();

      MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder();

      boolean hasFunctionCall();

      FunctionCall getFunctionCall();

      FunctionCallOrBuilder getFunctionCallOrBuilder();

      boolean hasOperator();

      Operator getOperator();

      OperatorOrBuilder getOperatorOrBuilder();

      boolean hasPosition();

      int getPosition();

      boolean hasObject();

      Object getObject();

      ObjectOrBuilder getObjectOrBuilder();

      boolean hasArray();

      Array getArray();

      ArrayOrBuilder getArrayOrBuilder();
   }
}
