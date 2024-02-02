package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

public final class Struct extends GeneratedMessageV3 implements StructOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int FIELDS_FIELD_NUMBER = 1;
   private MapField<String, Value> fields_;
   private byte memoizedIsInitialized;
   private static final Struct DEFAULT_INSTANCE = new Struct();
   private static final Parser<Struct> PARSER = new AbstractParser<Struct>() {
      public Struct parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return new Struct(input, extensionRegistry);
      }
   };

   private Struct(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
      this.memoizedIsInitialized = -1;
   }

   private Struct() {
      this.memoizedIsInitialized = -1;
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      return new Struct();
   }

   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Struct(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.fields_ = MapField.newMapField(Struct.FieldsDefaultEntryHolder.defaultEntry);
                        mutable_bitField0_ |= true;
                     }

                     MapEntry<String, Value> fields__ = (MapEntry)input.readMessage(Struct.FieldsDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
                     this.fields_.getMutableMap().put(fields__.getKey(), fields__.getValue());
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
      return StructProto.internal_static_google_protobuf_Struct_descriptor;
   }

   protected MapField internalGetMapField(int number) {
      switch (number) {
         case 1:
            return this.internalGetFields();
         default:
            throw new RuntimeException("Invalid map field number: " + number);
      }
   }

   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Builder.class);
   }

   private MapField<String, Value> internalGetFields() {
      return this.fields_ == null ? MapField.emptyMapField(Struct.FieldsDefaultEntryHolder.defaultEntry) : this.fields_;
   }

   public int getFieldsCount() {
      return this.internalGetFields().getMap().size();
   }

   public boolean containsFields(String key) {
      if (key == null) {
         throw new NullPointerException();
      } else {
         return this.internalGetFields().getMap().containsKey(key);
      }
   }

   /** @deprecated */
   @Deprecated
   public Map<String, Value> getFields() {
      return this.getFieldsMap();
   }

   public Map<String, Value> getFieldsMap() {
      return this.internalGetFields().getMap();
   }

   public Value getFieldsOrDefault(String key, Value defaultValue) {
      if (key == null) {
         throw new NullPointerException();
      } else {
         Map<String, Value> map = this.internalGetFields().getMap();
         return map.containsKey(key) ? (Value)map.get(key) : defaultValue;
      }
   }

   public Value getFieldsOrThrow(String key) {
      if (key == null) {
         throw new NullPointerException();
      } else {
         Map<String, Value> map = this.internalGetFields().getMap();
         if (!map.containsKey(key)) {
            throw new IllegalArgumentException();
         } else {
            return (Value)map.get(key);
         }
      }
   }

   public final boolean isInitialized() {
      byte isInitialized = this.memoizedIsInitialized;
      if (isInitialized == 1) {
         return true;
      } else if (isInitialized == 0) {
         return false;
      } else {
         this.memoizedIsInitialized = 1;
         return true;
      }
   }

   public void writeTo(CodedOutputStream output) throws IOException {
      GeneratedMessageV3.serializeStringMapTo(output, this.internalGetFields(), Struct.FieldsDefaultEntryHolder.defaultEntry, 1);
      this.unknownFields.writeTo(output);
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         size = 0;

         MapEntry fields__;
         for(Iterator var2 = this.internalGetFields().getMap().entrySet().iterator(); var2.hasNext(); size += CodedOutputStream.computeMessageSize(1, fields__)) {
            Map.Entry<String, Value> entry = (Map.Entry)var2.next();
            fields__ = Struct.FieldsDefaultEntryHolder.defaultEntry.newBuilderForType().setKey(entry.getKey()).setValue(entry.getValue()).build();
         }

         size += this.unknownFields.getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Struct)) {
         return super.equals(obj);
      } else {
         Struct other = (Struct)obj;
         if (!this.internalGetFields().equals(other.internalGetFields())) {
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
         if (!this.internalGetFields().getMap().isEmpty()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.internalGetFields().hashCode();
         }

         hash = 29 * hash + this.unknownFields.hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Struct parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return (Struct)PARSER.parseFrom(data);
   }

   public static Struct parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Struct)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Struct parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return (Struct)PARSER.parseFrom(data);
   }

   public static Struct parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Struct)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Struct parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return (Struct)PARSER.parseFrom(data);
   }

   public static Struct parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return (Struct)PARSER.parseFrom(data, extensionRegistry);
   }

   public static Struct parseFrom(InputStream input) throws IOException {
      return (Struct)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Struct parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Struct)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Struct parseDelimitedFrom(InputStream input) throws IOException {
      return (Struct)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
   }

   public static Struct parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Struct)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Struct parseFrom(CodedInputStream input) throws IOException {
      return (Struct)GeneratedMessageV3.parseWithIOException(PARSER, input);
   }

   public static Struct parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return (Struct)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Builder newBuilderForType() {
      return newBuilder();
   }

   public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Builder newBuilder(Struct prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
   }

   protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
   }

   public static Struct getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Struct> parser() {
      return PARSER;
   }

   public Parser<Struct> getParserForType() {
      return PARSER;
   }

   public Struct getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   // $FF: synthetic method
   Struct(GeneratedMessageV3.Builder x0, Object x1) {
      this(x0);
   }

   // $FF: synthetic method
   Struct(CodedInputStream x0, ExtensionRegistryLite x1, Object x2) throws InvalidProtocolBufferException {
      this(x0, x1);
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements StructOrBuilder {
      private int bitField0_;
      private MapField<String, Value> fields_;

      public static final Descriptors.Descriptor getDescriptor() {
         return StructProto.internal_static_google_protobuf_Struct_descriptor;
      }

      protected MapField internalGetMapField(int number) {
         switch (number) {
            case 1:
               return this.internalGetFields();
            default:
               throw new RuntimeException("Invalid map field number: " + number);
         }
      }

      protected MapField internalGetMutableMapField(int number) {
         switch (number) {
            case 1:
               return this.internalGetMutableFields();
            default:
               throw new RuntimeException("Invalid map field number: " + number);
         }
      }

      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(GeneratedMessageV3.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessageV3.alwaysUseFieldBuilders) {
         }

      }

      public Builder clear() {
         super.clear();
         this.internalGetMutableFields().clear();
         return this;
      }

      public Descriptors.Descriptor getDescriptorForType() {
         return StructProto.internal_static_google_protobuf_Struct_descriptor;
      }

      public Struct getDefaultInstanceForType() {
         return Struct.getDefaultInstance();
      }

      public Struct build() {
         Struct result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Struct buildPartial() {
         Struct result = new Struct(this);
         int from_bitField0_ = this.bitField0_;
         result.fields_ = this.internalGetFields();
         result.fields_.makeImmutable();
         this.onBuilt();
         return result;
      }

      public Builder clone() {
         return (Builder)super.clone();
      }

      public Builder setField(Descriptors.FieldDescriptor field, Object value) {
         return (Builder)super.setField(field, value);
      }

      public Builder clearField(Descriptors.FieldDescriptor field) {
         return (Builder)super.clearField(field);
      }

      public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
         return (Builder)super.clearOneof(oneof);
      }

      public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
         return (Builder)super.setRepeatedField(field, index, value);
      }

      public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
         return (Builder)super.addRepeatedField(field, value);
      }

      public Builder mergeFrom(Message other) {
         if (other instanceof Struct) {
            return this.mergeFrom((Struct)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Builder mergeFrom(Struct other) {
         if (other == Struct.getDefaultInstance()) {
            return this;
         } else {
            this.internalGetMutableFields().mergeFrom(other.internalGetFields());
            this.mergeUnknownFields(other.unknownFields);
            this.onChanged();
            return this;
         }
      }

      public final boolean isInitialized() {
         return true;
      }

      public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         Struct parsedMessage = null;

         try {
            parsedMessage = (Struct)Struct.PARSER.parsePartialFrom(input, extensionRegistry);
         } catch (InvalidProtocolBufferException var8) {
            parsedMessage = (Struct)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (parsedMessage != null) {
               this.mergeFrom(parsedMessage);
            }

         }

         return this;
      }

      private MapField<String, Value> internalGetFields() {
         return this.fields_ == null ? MapField.emptyMapField(Struct.FieldsDefaultEntryHolder.defaultEntry) : this.fields_;
      }

      private MapField<String, Value> internalGetMutableFields() {
         this.onChanged();
         if (this.fields_ == null) {
            this.fields_ = MapField.newMapField(Struct.FieldsDefaultEntryHolder.defaultEntry);
         }

         if (!this.fields_.isMutable()) {
            this.fields_ = this.fields_.copy();
         }

         return this.fields_;
      }

      public int getFieldsCount() {
         return this.internalGetFields().getMap().size();
      }

      public boolean containsFields(String key) {
         if (key == null) {
            throw new NullPointerException();
         } else {
            return this.internalGetFields().getMap().containsKey(key);
         }
      }

      /** @deprecated */
      @Deprecated
      public Map<String, Value> getFields() {
         return this.getFieldsMap();
      }

      public Map<String, Value> getFieldsMap() {
         return this.internalGetFields().getMap();
      }

      public Value getFieldsOrDefault(String key, Value defaultValue) {
         if (key == null) {
            throw new NullPointerException();
         } else {
            Map<String, Value> map = this.internalGetFields().getMap();
            return map.containsKey(key) ? (Value)map.get(key) : defaultValue;
         }
      }

      public Value getFieldsOrThrow(String key) {
         if (key == null) {
            throw new NullPointerException();
         } else {
            Map<String, Value> map = this.internalGetFields().getMap();
            if (!map.containsKey(key)) {
               throw new IllegalArgumentException();
            } else {
               return (Value)map.get(key);
            }
         }
      }

      public Builder clearFields() {
         this.internalGetMutableFields().getMutableMap().clear();
         return this;
      }

      public Builder removeFields(String key) {
         if (key == null) {
            throw new NullPointerException();
         } else {
            this.internalGetMutableFields().getMutableMap().remove(key);
            return this;
         }
      }

      /** @deprecated */
      @Deprecated
      public Map<String, Value> getMutableFields() {
         return this.internalGetMutableFields().getMutableMap();
      }

      public Builder putFields(String key, Value value) {
         if (key == null) {
            throw new NullPointerException();
         } else if (value == null) {
            throw new NullPointerException();
         } else {
            this.internalGetMutableFields().getMutableMap().put(key, value);
            return this;
         }
      }

      public Builder putAllFields(Map<String, Value> values) {
         this.internalGetMutableFields().getMutableMap().putAll(values);
         return this;
      }

      public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
         return (Builder)super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
         return (Builder)super.mergeUnknownFields(unknownFields);
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }

      // $FF: synthetic method
      Builder(GeneratedMessageV3.BuilderParent x0, Object x1) {
         this(x0);
      }
   }

   private static final class FieldsDefaultEntryHolder {
      static final MapEntry<String, Value> defaultEntry;

      static {
         defaultEntry = MapEntry.newDefaultInstance(StructProto.internal_static_google_protobuf_Struct_FieldsEntry_descriptor, WireFormat.FieldType.STRING, "", WireFormat.FieldType.MESSAGE, Value.getDefaultInstance());
      }
   }
}
