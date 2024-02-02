package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class GeneratedMessageV3 extends AbstractMessage implements Serializable {
   private static final long serialVersionUID = 1L;
   protected static boolean alwaysUseFieldBuilders = false;
   protected UnknownFieldSet unknownFields;

   protected GeneratedMessageV3() {
      this.unknownFields = UnknownFieldSet.getDefaultInstance();
   }

   protected GeneratedMessageV3(Builder<?> builder) {
      this.unknownFields = builder.getUnknownFields();
   }

   public Parser<? extends GeneratedMessageV3> getParserForType() {
      throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
   }

   static void enableAlwaysUseFieldBuildersForTesting() {
      setAlwaysUseFieldBuildersForTesting(true);
   }

   static void setAlwaysUseFieldBuildersForTesting(boolean useBuilders) {
      alwaysUseFieldBuilders = useBuilders;
   }

   protected abstract FieldAccessorTable internalGetFieldAccessorTable();

   public Descriptors.Descriptor getDescriptorForType() {
      return this.internalGetFieldAccessorTable().descriptor;
   }

   protected void mergeFromAndMakeImmutableInternal(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      Schema<GeneratedMessageV3> schema = Protobuf.getInstance().schemaFor((Object)this);

      try {
         schema.mergeFrom(this, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
      } catch (InvalidProtocolBufferException var5) {
         throw var5.setUnfinishedMessage(this);
      } catch (IOException var6) {
         throw (new InvalidProtocolBufferException(var6)).setUnfinishedMessage(this);
      }

      schema.makeImmutable(this);
   }

   private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable(boolean getBytesForString) {
      TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap();
      Descriptors.Descriptor descriptor = this.internalGetFieldAccessorTable().descriptor;
      List<Descriptors.FieldDescriptor> fields = descriptor.getFields();

      for(int i = 0; i < fields.size(); ++i) {
         Descriptors.FieldDescriptor field = (Descriptors.FieldDescriptor)fields.get(i);
         Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
         if (oneofDescriptor != null) {
            i += oneofDescriptor.getFieldCount() - 1;
            if (!this.hasOneof(oneofDescriptor)) {
               continue;
            }

            field = this.getOneofFieldDescriptor(oneofDescriptor);
         } else {
            if (field.isRepeated()) {
               List<?> value = (List)this.getField(field);
               if (!value.isEmpty()) {
                  result.put(field, value);
               }
               continue;
            }

            if (!this.hasField(field)) {
               continue;
            }
         }

         if (getBytesForString && field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
            result.put(field, this.getFieldRaw(field));
         } else {
            result.put(field, this.getField(field));
         }
      }

      return result;
   }

   public boolean isInitialized() {
      Iterator var1 = this.getDescriptorForType().getFields().iterator();

      while(true) {
         while(true) {
            Descriptors.FieldDescriptor field;
            do {
               if (!var1.hasNext()) {
                  return true;
               }

               field = (Descriptors.FieldDescriptor)var1.next();
               if (field.isRequired() && !this.hasField(field)) {
                  return false;
               }
            } while(field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE);

            if (field.isRepeated()) {
               List<Message> messageList = (List)this.getField(field);
               Iterator var4 = messageList.iterator();

               while(var4.hasNext()) {
                  Message element = (Message)var4.next();
                  if (!element.isInitialized()) {
                     return false;
                  }
               }
            } else if (this.hasField(field) && !((Message)this.getField(field)).isInitialized()) {
               return false;
            }
         }
      }
   }

   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      return Collections.unmodifiableMap(this.getAllFieldsMutable(false));
   }

   Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
      return Collections.unmodifiableMap(this.getAllFieldsMutable(true));
   }

   public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      return this.internalGetFieldAccessorTable().getOneof(oneof).has(this);
   }

   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      return this.internalGetFieldAccessorTable().getOneof(oneof).get(this);
   }

   public boolean hasField(Descriptors.FieldDescriptor field) {
      return this.internalGetFieldAccessorTable().getField(field).has(this);
   }

   public Object getField(Descriptors.FieldDescriptor field) {
      return this.internalGetFieldAccessorTable().getField(field).get(this);
   }

   Object getFieldRaw(Descriptors.FieldDescriptor field) {
      return this.internalGetFieldAccessorTable().getField(field).getRaw(this);
   }

   public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
      return this.internalGetFieldAccessorTable().getField(field).getRepeatedCount(this);
   }

   public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
      return this.internalGetFieldAccessorTable().getField(field).getRepeated(this, index);
   }

   public UnknownFieldSet getUnknownFields() {
      throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
   }

   protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      return input.shouldDiscardUnknownFields() ? input.skipField(tag) : unknownFields.mergeFieldFrom(tag, input);
   }

   protected boolean parseUnknownFieldProto3(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      return this.parseUnknownField(input, unknownFields, extensionRegistry, tag);
   }

   protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input) throws IOException {
      try {
         return (Message)parser.parseFrom(input);
      } catch (InvalidProtocolBufferException var3) {
         throw var3.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
      try {
         return (Message)parser.parseFrom(input, extensions);
      } catch (InvalidProtocolBufferException var4) {
         throw var4.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input) throws IOException {
      try {
         return (Message)parser.parseFrom(input);
      } catch (InvalidProtocolBufferException var3) {
         throw var3.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input, ExtensionRegistryLite extensions) throws IOException {
      try {
         return (Message)parser.parseFrom(input, extensions);
      } catch (InvalidProtocolBufferException var4) {
         throw var4.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input) throws IOException {
      try {
         return (Message)parser.parseDelimitedFrom(input);
      } catch (InvalidProtocolBufferException var3) {
         throw var3.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
      try {
         return (Message)parser.parseDelimitedFrom(input, extensions);
      } catch (InvalidProtocolBufferException var4) {
         throw var4.unwrapIOException();
      }
   }

   protected static boolean canUseUnsafe() {
      return UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations();
   }

   protected static Internal.IntList emptyIntList() {
      return IntArrayList.emptyList();
   }

   protected static Internal.IntList newIntList() {
      return new IntArrayList();
   }

   protected static Internal.IntList mutableCopy(Internal.IntList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
   }

   protected static Internal.LongList emptyLongList() {
      return LongArrayList.emptyList();
   }

   protected static Internal.LongList newLongList() {
      return new LongArrayList();
   }

   protected static Internal.LongList mutableCopy(Internal.LongList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
   }

   protected static Internal.FloatList emptyFloatList() {
      return FloatArrayList.emptyList();
   }

   protected static Internal.FloatList newFloatList() {
      return new FloatArrayList();
   }

   protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
   }

   protected static Internal.DoubleList emptyDoubleList() {
      return DoubleArrayList.emptyList();
   }

   protected static Internal.DoubleList newDoubleList() {
      return new DoubleArrayList();
   }

   protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
   }

   protected static Internal.BooleanList emptyBooleanList() {
      return BooleanArrayList.emptyList();
   }

   protected static Internal.BooleanList newBooleanList() {
      return new BooleanArrayList();
   }

   protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
      int size = list.size();
      return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
   }

   public void writeTo(CodedOutputStream output) throws IOException {
      MessageReflection.writeMessageTo(this, this.getAllFieldsRaw(), output, false);
   }

   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         this.memoizedSize = MessageReflection.getSerializedSize(this, this.getAllFieldsRaw());
         return this.memoizedSize;
      }
   }

   protected Object newInstance(UnusedPrivateParameter unused) {
      throw new UnsupportedOperationException("This method must be overridden by the subclass.");
   }

   protected void makeExtensionsImmutable() {
   }

   protected abstract Message.Builder newBuilderForType(BuilderParent var1);

   protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent parent) {
      return this.newBuilderForType(new BuilderParent() {
         public void markDirty() {
            parent.markDirty();
         }
      });
   }

   private static java.lang.reflect.Method getMethodOrDie(Class clazz, String name, Class... params) {
      try {
         return clazz.getMethod(name, params);
      } catch (NoSuchMethodException var4) {
         throw new RuntimeException("Generated message class \"" + clazz.getName() + "\" missing method \"" + name + "\".", var4);
      }
   }

   private static Object invokeOrDie(java.lang.reflect.Method method, Object object, Object... params) {
      try {
         return method.invoke(object, params);
      } catch (IllegalAccessException var5) {
         throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", var5);
      } catch (InvocationTargetException var6) {
         Throwable cause = var6.getCause();
         if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
         } else if (cause instanceof Error) {
            throw (Error)cause;
         } else {
            throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
         }
      }
   }

   protected MapField internalGetMapField(int fieldNumber) {
      throw new RuntimeException("No map fields found in " + this.getClass().getName());
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new GeneratedMessageLite.SerializedForm(this);
   }

   private static <MessageType extends ExtendableMessage<MessageType>, T> Extension<MessageType, T> checkNotLite(ExtensionLite<MessageType, T> extension) {
      if (extension.isLite()) {
         throw new IllegalArgumentException("Expected non-lite extension.");
      } else {
         return (Extension)extension;
      }
   }

   protected static int computeStringSize(int fieldNumber, Object value) {
      return value instanceof String ? CodedOutputStream.computeStringSize(fieldNumber, (String)value) : CodedOutputStream.computeBytesSize(fieldNumber, (ByteString)value);
   }

   protected static int computeStringSizeNoTag(Object value) {
      return value instanceof String ? CodedOutputStream.computeStringSizeNoTag((String)value) : CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
   }

   protected static void writeString(CodedOutputStream output, int fieldNumber, Object value) throws IOException {
      if (value instanceof String) {
         output.writeString(fieldNumber, (String)value);
      } else {
         output.writeBytes(fieldNumber, (ByteString)value);
      }

   }

   protected static void writeStringNoTag(CodedOutputStream output, Object value) throws IOException {
      if (value instanceof String) {
         output.writeStringNoTag((String)value);
      } else {
         output.writeBytesNoTag((ByteString)value);
      }

   }

   protected static <V> void serializeIntegerMapTo(CodedOutputStream out, MapField<Integer, V> field, MapEntry<Integer, V> defaultEntry, int fieldNumber) throws IOException {
      Map<Integer, V> m = field.getMap();
      if (!out.isSerializationDeterministic()) {
         serializeMapTo(out, m, defaultEntry, fieldNumber);
      } else {
         int[] keys = new int[m.size()];
         int index = 0;

         int k;
         for(Iterator var7 = m.keySet().iterator(); var7.hasNext(); keys[index++] = k) {
            k = (Integer)var7.next();
         }

         Arrays.sort(keys);
         int[] var11 = keys;
         k = keys.length;

         for(int var9 = 0; var9 < k; ++var9) {
            int key = var11[var9];
            out.writeMessage(fieldNumber, defaultEntry.newBuilderForType().setKey(key).setValue(m.get(key)).build());
         }

      }
   }

   protected static <V> void serializeLongMapTo(CodedOutputStream out, MapField<Long, V> field, MapEntry<Long, V> defaultEntry, int fieldNumber) throws IOException {
      Map<Long, V> m = field.getMap();
      if (!out.isSerializationDeterministic()) {
         serializeMapTo(out, m, defaultEntry, fieldNumber);
      } else {
         long[] keys = new long[m.size()];
         int index = 0;

         long k;
         for(Iterator var7 = m.keySet().iterator(); var7.hasNext(); keys[index++] = k) {
            k = (Long)var7.next();
         }

         Arrays.sort(keys);
         long[] var12 = keys;
         int var13 = keys.length;

         for(int var9 = 0; var9 < var13; ++var9) {
            long key = var12[var9];
            out.writeMessage(fieldNumber, defaultEntry.newBuilderForType().setKey(key).setValue(m.get(key)).build());
         }

      }
   }

   protected static <V> void serializeStringMapTo(CodedOutputStream out, MapField<String, V> field, MapEntry<String, V> defaultEntry, int fieldNumber) throws IOException {
      Map<String, V> m = field.getMap();
      if (!out.isSerializationDeterministic()) {
         serializeMapTo(out, m, defaultEntry, fieldNumber);
      } else {
         String[] keys = new String[m.size()];
         keys = (String[])m.keySet().toArray(keys);
         Arrays.sort(keys);
         String[] var6 = keys;
         int var7 = keys.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String key = var6[var8];
            out.writeMessage(fieldNumber, defaultEntry.newBuilderForType().setKey(key).setValue(m.get(key)).build());
         }

      }
   }

   protected static <V> void serializeBooleanMapTo(CodedOutputStream out, MapField<Boolean, V> field, MapEntry<Boolean, V> defaultEntry, int fieldNumber) throws IOException {
      Map<Boolean, V> m = field.getMap();
      if (!out.isSerializationDeterministic()) {
         serializeMapTo(out, m, defaultEntry, fieldNumber);
      } else {
         maybeSerializeBooleanEntryTo(out, m, defaultEntry, fieldNumber, false);
         maybeSerializeBooleanEntryTo(out, m, defaultEntry, fieldNumber, true);
      }
   }

   private static <V> void maybeSerializeBooleanEntryTo(CodedOutputStream out, Map<Boolean, V> m, MapEntry<Boolean, V> defaultEntry, int fieldNumber, boolean key) throws IOException {
      if (m.containsKey(key)) {
         out.writeMessage(fieldNumber, defaultEntry.newBuilderForType().setKey(key).setValue(m.get(key)).build());
      }

   }

   private static <K, V> void serializeMapTo(CodedOutputStream out, Map<K, V> m, MapEntry<K, V> defaultEntry, int fieldNumber) throws IOException {
      Iterator var4 = m.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry<K, V> entry = (Map.Entry)var4.next();
         out.writeMessage(fieldNumber, defaultEntry.newBuilderForType().setKey(entry.getKey()).setValue(entry.getValue()).build());
      }

   }

   public static final class FieldAccessorTable {
      private final Descriptors.Descriptor descriptor;
      private final FieldAccessor[] fields;
      private String[] camelCaseNames;
      private final OneofAccessor[] oneofs;
      private volatile boolean initialized;

      public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
         this(descriptor, camelCaseNames);
         this.ensureFieldAccessorsInitialized(messageClass, builderClass);
      }

      public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames) {
         this.descriptor = descriptor;
         this.camelCaseNames = camelCaseNames;
         this.fields = new FieldAccessor[descriptor.getFields().size()];
         this.oneofs = new OneofAccessor[descriptor.getOneofs().size()];
         this.initialized = false;
      }

      public FieldAccessorTable ensureFieldAccessorsInitialized(Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
         if (this.initialized) {
            return this;
         } else {
            synchronized(this) {
               if (this.initialized) {
                  return this;
               } else {
                  int fieldsSize = this.fields.length;

                  int i;
                  for(i = 0; i < fieldsSize; ++i) {
                     Descriptors.FieldDescriptor field = (Descriptors.FieldDescriptor)this.descriptor.getFields().get(i);
                     String containingOneofCamelCaseName = null;
                     if (field.getContainingOneof() != null) {
                        containingOneofCamelCaseName = this.camelCaseNames[fieldsSize + field.getContainingOneof().getIndex()];
                     }

                     if (field.isRepeated()) {
                        if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                           if (field.isMapField()) {
                              this.fields[i] = new MapFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                           } else {
                              this.fields[i] = new RepeatedMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                           }
                        } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                           this.fields[i] = new RepeatedEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                        } else {
                           this.fields[i] = new RepeatedFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                        }
                     } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        this.fields[i] = new SingularMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
                     } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                        this.fields[i] = new SingularEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
                     } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
                        this.fields[i] = new SingularStringFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
                     } else {
                        this.fields[i] = new SingularFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
                     }
                  }

                  i = this.oneofs.length;

                  for(int i = 0; i < i; ++i) {
                     this.oneofs[i] = new OneofAccessor(this.descriptor, this.camelCaseNames[i + fieldsSize], messageClass, builderClass);
                  }

                  this.initialized = true;
                  this.camelCaseNames = null;
                  return this;
               }
            }
         }
      }

      private FieldAccessor getField(Descriptors.FieldDescriptor field) {
         if (field.getContainingType() != this.descriptor) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         } else if (field.isExtension()) {
            throw new IllegalArgumentException("This type does not have extensions.");
         } else {
            return this.fields[field.getIndex()];
         }
      }

      private OneofAccessor getOneof(Descriptors.OneofDescriptor oneof) {
         if (oneof.getContainingType() != this.descriptor) {
            throw new IllegalArgumentException("OneofDescriptor does not match message type.");
         } else {
            return this.oneofs[oneof.getIndex()];
         }
      }

      private static boolean supportFieldPresence(Descriptors.FileDescriptor file) {
         return file.getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2;
      }

      private static final class RepeatedMessageFieldAccessor extends RepeatedFieldAccessor {
         private final java.lang.reflect.Method newBuilderMethod;
         private final java.lang.reflect.Method getBuilderMethodBuilder;

         RepeatedMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
            super(descriptor, camelCaseName, messageClass, builderClass);
            this.newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder");
            this.getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", Integer.TYPE);
         }

         private Object coerceType(Object value) {
            return this.type.isInstance(value) ? value : ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, (Object)null)).mergeFrom((Message)value).build();
         }

         public void setRepeated(Builder builder, int index, Object value) {
            super.setRepeated(builder, index, this.coerceType(value));
         }

         public void addRepeated(Builder builder, Object value) {
            super.addRepeated(builder, this.coerceType(value));
         }

         public Message.Builder newBuilder() {
            return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, (Object)null);
         }

         public Message.Builder getRepeatedBuilder(Builder builder, int index) {
            return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, builder, index);
         }
      }

      private static final class SingularMessageFieldAccessor extends SingularFieldAccessor {
         private final java.lang.reflect.Method newBuilderMethod;
         private final java.lang.reflect.Method getBuilderMethodBuilder;

         SingularMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
            super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
            this.newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder");
            this.getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder");
         }

         private Object coerceType(Object value) {
            return this.type.isInstance(value) ? value : ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, (Object)null)).mergeFrom((Message)value).buildPartial();
         }

         public void set(Builder builder, Object value) {
            super.set(builder, this.coerceType(value));
         }

         public Message.Builder newBuilder() {
            return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, (Object)null);
         }

         public Message.Builder getBuilder(Builder builder) {
            return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, builder);
         }
      }

      private static final class SingularStringFieldAccessor extends SingularFieldAccessor {
         private final java.lang.reflect.Method getBytesMethod;
         private final java.lang.reflect.Method getBytesMethodBuilder;
         private final java.lang.reflect.Method setBytesMethodBuilder;

         SingularStringFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
            super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
            this.getBytesMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Bytes");
            this.getBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Bytes");
            this.setBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Bytes", ByteString.class);
         }

         public Object getRaw(GeneratedMessageV3 message) {
            return GeneratedMessageV3.invokeOrDie(this.getBytesMethod, message);
         }

         public Object getRaw(Builder builder) {
            return GeneratedMessageV3.invokeOrDie(this.getBytesMethodBuilder, builder);
         }

         public void set(Builder builder, Object value) {
            if (value instanceof ByteString) {
               GeneratedMessageV3.invokeOrDie(this.setBytesMethodBuilder, builder, value);
            } else {
               super.set(builder, value);
            }

         }
      }

      private static final class RepeatedEnumFieldAccessor extends RepeatedFieldAccessor {
         private Descriptors.EnumDescriptor enumDescriptor;
         private final java.lang.reflect.Method valueOfMethod;
         private final java.lang.reflect.Method getValueDescriptorMethod;
         private boolean supportUnknownEnumValue;
         private java.lang.reflect.Method getRepeatedValueMethod;
         private java.lang.reflect.Method getRepeatedValueMethodBuilder;
         private java.lang.reflect.Method setRepeatedValueMethod;
         private java.lang.reflect.Method addRepeatedValueMethod;

         RepeatedEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
            super(descriptor, camelCaseName, messageClass, builderClass);
            this.enumDescriptor = descriptor.getEnumType();
            this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", Descriptors.EnumValueDescriptor.class);
            this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor");
            this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
            if (this.supportUnknownEnumValue) {
               this.getRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", Integer.TYPE);
               this.getRepeatedValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", Integer.TYPE);
               this.setRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", Integer.TYPE, Integer.TYPE);
               this.addRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "add" + camelCaseName + "Value", Integer.TYPE);
            }

         }

         public Object get(GeneratedMessageV3 message) {
            List newList = new ArrayList();
            int size = this.getRepeatedCount(message);

            for(int i = 0; i < size; ++i) {
               newList.add(this.getRepeated(message, i));
            }

            return Collections.unmodifiableList(newList);
         }

         public Object get(Builder builder) {
            List newList = new ArrayList();
            int size = this.getRepeatedCount(builder);

            for(int i = 0; i < size; ++i) {
               newList.add(this.getRepeated(builder, i));
            }

            return Collections.unmodifiableList(newList);
         }

         public Object getRepeated(GeneratedMessageV3 message, int index) {
            if (this.supportUnknownEnumValue) {
               int value = (Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethod, message, index);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
            } else {
               return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(message, index));
            }
         }

         public Object getRepeated(Builder builder, int index) {
            if (this.supportUnknownEnumValue) {
               int value = (Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethodBuilder, builder, index);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
            } else {
               return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(builder, index));
            }
         }

         public void setRepeated(Builder builder, int index, Object value) {
            if (this.supportUnknownEnumValue) {
               GeneratedMessageV3.invokeOrDie(this.setRepeatedValueMethod, builder, index, ((Descriptors.EnumValueDescriptor)value).getNumber());
            } else {
               super.setRepeated(builder, index, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, (Object)null, value));
            }
         }

         public void addRepeated(Builder builder, Object value) {
            if (this.supportUnknownEnumValue) {
               GeneratedMessageV3.invokeOrDie(this.addRepeatedValueMethod, builder, ((Descriptors.EnumValueDescriptor)value).getNumber());
            } else {
               super.addRepeated(builder, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, (Object)null, value));
            }
         }
      }

      private static final class SingularEnumFieldAccessor extends SingularFieldAccessor {
         private Descriptors.EnumDescriptor enumDescriptor;
         private java.lang.reflect.Method valueOfMethod;
         private java.lang.reflect.Method getValueDescriptorMethod;
         private boolean supportUnknownEnumValue;
         private java.lang.reflect.Method getValueMethod;
         private java.lang.reflect.Method getValueMethodBuilder;
         private java.lang.reflect.Method setValueMethod;

         SingularEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
            super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
            this.enumDescriptor = descriptor.getEnumType();
            this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", Descriptors.EnumValueDescriptor.class);
            this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor");
            this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
            if (this.supportUnknownEnumValue) {
               this.getValueMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Value");
               this.getValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Value");
               this.setValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", Integer.TYPE);
            }

         }

         public Object get(GeneratedMessageV3 message) {
            if (this.supportUnknownEnumValue) {
               int value = (Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethod, message);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
            } else {
               return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(message));
            }
         }

         public Object get(Builder builder) {
            if (this.supportUnknownEnumValue) {
               int value = (Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethodBuilder, builder);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
            } else {
               return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(builder));
            }
         }

         public void set(Builder builder, Object value) {
            if (this.supportUnknownEnumValue) {
               GeneratedMessageV3.invokeOrDie(this.setValueMethod, builder, ((Descriptors.EnumValueDescriptor)value).getNumber());
            } else {
               super.set(builder, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, (Object)null, value));
            }
         }
      }

      private static class MapFieldAccessor implements FieldAccessor {
         private final Descriptors.FieldDescriptor field;
         private final Message mapEntryMessageDefaultInstance;

         MapFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
            this.field = descriptor;
            java.lang.reflect.Method getDefaultInstanceMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "getDefaultInstance");
            MapField defaultMapField = this.getMapField((GeneratedMessageV3)GeneratedMessageV3.invokeOrDie(getDefaultInstanceMethod, (Object)null));
            this.mapEntryMessageDefaultInstance = defaultMapField.getMapEntryMessageDefaultInstance();
         }

         private MapField<?, ?> getMapField(GeneratedMessageV3 message) {
            return message.internalGetMapField(this.field.getNumber());
         }

         private MapField<?, ?> getMapField(Builder builder) {
            return builder.internalGetMapField(this.field.getNumber());
         }

         private MapField<?, ?> getMutableMapField(Builder builder) {
            return builder.internalGetMutableMapField(this.field.getNumber());
         }

         private Message coerceType(Message value) {
            if (value == null) {
               return null;
            } else {
               return this.mapEntryMessageDefaultInstance.getClass().isInstance(value) ? value : this.mapEntryMessageDefaultInstance.toBuilder().mergeFrom(value).build();
            }
         }

         public Object get(GeneratedMessageV3 message) {
            List result = new ArrayList();

            for(int i = 0; i < this.getRepeatedCount(message); ++i) {
               result.add(this.getRepeated(message, i));
            }

            return Collections.unmodifiableList(result);
         }

         public Object get(Builder builder) {
            List result = new ArrayList();

            for(int i = 0; i < this.getRepeatedCount(builder); ++i) {
               result.add(this.getRepeated(builder, i));
            }

            return Collections.unmodifiableList(result);
         }

         public Object getRaw(GeneratedMessageV3 message) {
            return this.get(message);
         }

         public Object getRaw(Builder builder) {
            return this.get(builder);
         }

         public void set(Builder builder, Object value) {
            this.clear(builder);
            Iterator var3 = ((List)value).iterator();

            while(var3.hasNext()) {
               Object entry = var3.next();
               this.addRepeated(builder, entry);
            }

         }

         public Object getRepeated(GeneratedMessageV3 message, int index) {
            return this.getMapField(message).getList().get(index);
         }

         public Object getRepeated(Builder builder, int index) {
            return this.getMapField(builder).getList().get(index);
         }

         public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
            return this.getRepeated(message, index);
         }

         public Object getRepeatedRaw(Builder builder, int index) {
            return this.getRepeated(builder, index);
         }

         public void setRepeated(Builder builder, int index, Object value) {
            this.getMutableMapField(builder).getMutableList().set(index, this.coerceType((Message)value));
         }

         public void addRepeated(Builder builder, Object value) {
            this.getMutableMapField(builder).getMutableList().add(this.coerceType((Message)value));
         }

         public boolean has(GeneratedMessageV3 message) {
            throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
         }

         public boolean has(Builder builder) {
            throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
         }

         public int getRepeatedCount(GeneratedMessageV3 message) {
            return this.getMapField(message).getList().size();
         }

         public int getRepeatedCount(Builder builder) {
            return this.getMapField(builder).getList().size();
         }

         public void clear(Builder builder) {
            this.getMutableMapField(builder).getMutableList().clear();
         }

         public Message.Builder newBuilder() {
            return this.mapEntryMessageDefaultInstance.newBuilderForType();
         }

         public Message.Builder getBuilder(Builder builder) {
            throw new UnsupportedOperationException("Nested builder not supported for map fields.");
         }

         public Message.Builder getRepeatedBuilder(Builder builder, int index) {
            throw new UnsupportedOperationException("Nested builder not supported for map fields.");
         }
      }

      private static class RepeatedFieldAccessor implements FieldAccessor {
         protected final Class type;
         protected final MethodInvoker invoker;

         RepeatedFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
            ReflectionInvoker reflectionInvoker = new ReflectionInvoker(descriptor, camelCaseName, messageClass, builderClass);
            this.type = reflectionInvoker.getRepeatedMethod.getReturnType();
            this.invoker = getMethodInvoker(reflectionInvoker);
         }

         static MethodInvoker getMethodInvoker(ReflectionInvoker accessor) {
            return accessor;
         }

         public Object get(GeneratedMessageV3 message) {
            return this.invoker.get(message);
         }

         public Object get(Builder builder) {
            return this.invoker.get(builder);
         }

         public Object getRaw(GeneratedMessageV3 message) {
            return this.get(message);
         }

         public Object getRaw(Builder builder) {
            return this.get(builder);
         }

         public void set(Builder builder, Object value) {
            this.clear(builder);
            Iterator var3 = ((List)value).iterator();

            while(var3.hasNext()) {
               Object element = var3.next();
               this.addRepeated(builder, element);
            }

         }

         public Object getRepeated(GeneratedMessageV3 message, int index) {
            return this.invoker.getRepeated(message, index);
         }

         public Object getRepeated(Builder builder, int index) {
            return this.invoker.getRepeated(builder, index);
         }

         public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
            return this.getRepeated(message, index);
         }

         public Object getRepeatedRaw(Builder builder, int index) {
            return this.getRepeated(builder, index);
         }

         public void setRepeated(Builder builder, int index, Object value) {
            this.invoker.setRepeated(builder, index, value);
         }

         public void addRepeated(Builder builder, Object value) {
            this.invoker.addRepeated(builder, value);
         }

         public boolean has(GeneratedMessageV3 message) {
            throw new UnsupportedOperationException("hasField() called on a repeated field.");
         }

         public boolean has(Builder builder) {
            throw new UnsupportedOperationException("hasField() called on a repeated field.");
         }

         public int getRepeatedCount(GeneratedMessageV3 message) {
            return this.invoker.getRepeatedCount(message);
         }

         public int getRepeatedCount(Builder builder) {
            return this.invoker.getRepeatedCount(builder);
         }

         public void clear(Builder builder) {
            this.invoker.clear(builder);
         }

         public Message.Builder newBuilder() {
            throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
         }

         public Message.Builder getBuilder(Builder builder) {
            throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
         }

         public Message.Builder getRepeatedBuilder(Builder builder, int index) {
            throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
         }

         private static final class ReflectionInvoker implements MethodInvoker {
            protected final java.lang.reflect.Method getMethod;
            protected final java.lang.reflect.Method getMethodBuilder;
            protected final java.lang.reflect.Method getRepeatedMethod;
            protected final java.lang.reflect.Method getRepeatedMethodBuilder;
            protected final java.lang.reflect.Method setRepeatedMethod;
            protected final java.lang.reflect.Method addRepeatedMethod;
            protected final java.lang.reflect.Method getCountMethod;
            protected final java.lang.reflect.Method getCountMethodBuilder;
            protected final java.lang.reflect.Method clearMethod;

            ReflectionInvoker(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
               this.getMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "List");
               this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "List");
               this.getRepeatedMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName, Integer.TYPE);
               this.getRepeatedMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName, Integer.TYPE);
               Class<?> type = this.getRepeatedMethod.getReturnType();
               this.setRepeatedMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName, Integer.TYPE, type);
               this.addRepeatedMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "add" + camelCaseName, type);
               this.getCountMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Count");
               this.getCountMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Count");
               this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName);
            }

            public Object get(GeneratedMessageV3 message) {
               return GeneratedMessageV3.invokeOrDie(this.getMethod, message);
            }

            public Object get(Builder<?> builder) {
               return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, builder);
            }

            public Object getRepeated(GeneratedMessageV3 message, int index) {
               return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethod, message, index);
            }

            public Object getRepeated(Builder<?> builder, int index) {
               return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethodBuilder, builder, index);
            }

            public void setRepeated(Builder<?> builder, int index, Object value) {
               GeneratedMessageV3.invokeOrDie(this.setRepeatedMethod, builder, index, value);
            }

            public void addRepeated(Builder<?> builder, Object value) {
               GeneratedMessageV3.invokeOrDie(this.addRepeatedMethod, builder, value);
            }

            public int getRepeatedCount(GeneratedMessageV3 message) {
               return (Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethod, message);
            }

            public int getRepeatedCount(Builder<?> builder) {
               return (Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethodBuilder, builder);
            }

            public void clear(Builder<?> builder) {
               GeneratedMessageV3.invokeOrDie(this.clearMethod, builder);
            }
         }

         interface MethodInvoker {
            Object get(GeneratedMessageV3 var1);

            Object get(Builder<?> var1);

            Object getRepeated(GeneratedMessageV3 var1, int var2);

            Object getRepeated(Builder<?> var1, int var2);

            void setRepeated(Builder<?> var1, int var2, Object var3);

            void addRepeated(Builder<?> var1, Object var2);

            int getRepeatedCount(GeneratedMessageV3 var1);

            int getRepeatedCount(Builder<?> var1);

            void clear(Builder<?> var1);
         }
      }

      private static class SingularFieldAccessor implements FieldAccessor {
         protected final Class<?> type;
         protected final Descriptors.FieldDescriptor field;
         protected final boolean isOneofField;
         protected final boolean hasHasMethod;
         protected final MethodInvoker invoker;

         SingularFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
            this.isOneofField = descriptor.getContainingOneof() != null;
            this.hasHasMethod = GeneratedMessageV3.FieldAccessorTable.supportFieldPresence(descriptor.getFile()) || !this.isOneofField && descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE;
            ReflectionInvoker reflectionInvoker = new ReflectionInvoker(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName, this.isOneofField, this.hasHasMethod);
            this.field = descriptor;
            this.type = reflectionInvoker.getMethod.getReturnType();
            this.invoker = getMethodInvoker(reflectionInvoker);
         }

         static MethodInvoker getMethodInvoker(ReflectionInvoker accessor) {
            return accessor;
         }

         public Object get(GeneratedMessageV3 message) {
            return this.invoker.get(message);
         }

         public Object get(Builder builder) {
            return this.invoker.get(builder);
         }

         public Object getRaw(GeneratedMessageV3 message) {
            return this.get(message);
         }

         public Object getRaw(Builder builder) {
            return this.get(builder);
         }

         public void set(Builder builder, Object value) {
            this.invoker.set(builder, value);
         }

         public Object getRepeated(GeneratedMessageV3 message, int index) {
            throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
         }

         public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
            throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
         }

         public Object getRepeated(Builder builder, int index) {
            throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
         }

         public Object getRepeatedRaw(Builder builder, int index) {
            throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
         }

         public void setRepeated(Builder builder, int index, Object value) {
            throw new UnsupportedOperationException("setRepeatedField() called on a singular field.");
         }

         public void addRepeated(Builder builder, Object value) {
            throw new UnsupportedOperationException("addRepeatedField() called on a singular field.");
         }

         public boolean has(GeneratedMessageV3 message) {
            if (!this.hasHasMethod) {
               if (this.isOneofField) {
                  return this.invoker.getOneofFieldNumber(message) == this.field.getNumber();
               } else {
                  return !this.get(message).equals(this.field.getDefaultValue());
               }
            } else {
               return this.invoker.has(message);
            }
         }

         public boolean has(Builder builder) {
            if (!this.hasHasMethod) {
               if (this.isOneofField) {
                  return this.invoker.getOneofFieldNumber(builder) == this.field.getNumber();
               } else {
                  return !this.get(builder).equals(this.field.getDefaultValue());
               }
            } else {
               return this.invoker.has(builder);
            }
         }

         public int getRepeatedCount(GeneratedMessageV3 message) {
            throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
         }

         public int getRepeatedCount(Builder builder) {
            throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
         }

         public void clear(Builder builder) {
            this.invoker.clear(builder);
         }

         public Message.Builder newBuilder() {
            throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
         }

         public Message.Builder getBuilder(Builder builder) {
            throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
         }

         public Message.Builder getRepeatedBuilder(Builder builder, int index) {
            throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
         }

         private static final class ReflectionInvoker implements MethodInvoker {
            protected final java.lang.reflect.Method getMethod;
            protected final java.lang.reflect.Method getMethodBuilder;
            protected final java.lang.reflect.Method setMethod;
            protected final java.lang.reflect.Method hasMethod;
            protected final java.lang.reflect.Method hasMethodBuilder;
            protected final java.lang.reflect.Method clearMethod;
            protected final java.lang.reflect.Method caseMethod;
            protected final java.lang.reflect.Method caseMethodBuilder;

            ReflectionInvoker(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName, boolean isOneofField, boolean hasHasMethod) {
               this.getMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName);
               this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName);
               Class<?> type = this.getMethod.getReturnType();
               this.setMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName, type);
               this.hasMethod = hasHasMethod ? GeneratedMessageV3.getMethodOrDie(messageClass, "has" + camelCaseName) : null;
               this.hasMethodBuilder = hasHasMethod ? GeneratedMessageV3.getMethodOrDie(builderClass, "has" + camelCaseName) : null;
               this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName);
               this.caseMethod = isOneofField ? GeneratedMessageV3.getMethodOrDie(messageClass, "get" + containingOneofCamelCaseName + "Case") : null;
               this.caseMethodBuilder = isOneofField ? GeneratedMessageV3.getMethodOrDie(builderClass, "get" + containingOneofCamelCaseName + "Case") : null;
            }

            public Object get(GeneratedMessageV3 message) {
               return GeneratedMessageV3.invokeOrDie(this.getMethod, message);
            }

            public Object get(Builder<?> builder) {
               return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, builder);
            }

            public int getOneofFieldNumber(GeneratedMessageV3 message) {
               return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message)).getNumber();
            }

            public int getOneofFieldNumber(Builder<?> builder) {
               return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder)).getNumber();
            }

            public void set(Builder<?> builder, Object value) {
               GeneratedMessageV3.invokeOrDie(this.setMethod, builder, value);
            }

            public boolean has(GeneratedMessageV3 message) {
               return (Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethod, message);
            }

            public boolean has(Builder<?> builder) {
               return (Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethodBuilder, builder);
            }

            public void clear(Builder<?> builder) {
               GeneratedMessageV3.invokeOrDie(this.clearMethod, builder);
            }
         }

         private interface MethodInvoker {
            Object get(GeneratedMessageV3 var1);

            Object get(Builder<?> var1);

            int getOneofFieldNumber(GeneratedMessageV3 var1);

            int getOneofFieldNumber(Builder<?> var1);

            void set(Builder<?> var1, Object var2);

            boolean has(GeneratedMessageV3 var1);

            boolean has(Builder<?> var1);

            void clear(Builder<?> var1);
         }
      }

      private static class OneofAccessor {
         private final Descriptors.Descriptor descriptor;
         private final java.lang.reflect.Method caseMethod;
         private final java.lang.reflect.Method caseMethodBuilder;
         private final java.lang.reflect.Method clearMethod;

         OneofAccessor(Descriptors.Descriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
            this.descriptor = descriptor;
            this.caseMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Case");
            this.caseMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Case");
            this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName);
         }

         public boolean has(GeneratedMessageV3 message) {
            return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message)).getNumber() != 0;
         }

         public boolean has(Builder builder) {
            return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder)).getNumber() != 0;
         }

         public Descriptors.FieldDescriptor get(GeneratedMessageV3 message) {
            int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message)).getNumber();
            return fieldNumber > 0 ? this.descriptor.findFieldByNumber(fieldNumber) : null;
         }

         public Descriptors.FieldDescriptor get(Builder builder) {
            int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder)).getNumber();
            return fieldNumber > 0 ? this.descriptor.findFieldByNumber(fieldNumber) : null;
         }

         public void clear(Builder builder) {
            GeneratedMessageV3.invokeOrDie(this.clearMethod, builder);
         }
      }

      private interface FieldAccessor {
         Object get(GeneratedMessageV3 var1);

         Object get(Builder var1);

         Object getRaw(GeneratedMessageV3 var1);

         Object getRaw(Builder var1);

         void set(Builder var1, Object var2);

         Object getRepeated(GeneratedMessageV3 var1, int var2);

         Object getRepeated(Builder var1, int var2);

         Object getRepeatedRaw(GeneratedMessageV3 var1, int var2);

         Object getRepeatedRaw(Builder var1, int var2);

         void setRepeated(Builder var1, int var2, Object var3);

         void addRepeated(Builder var1, Object var2);

         boolean has(GeneratedMessageV3 var1);

         boolean has(Builder var1);

         int getRepeatedCount(GeneratedMessageV3 var1);

         int getRepeatedCount(Builder var1);

         void clear(Builder var1);

         Message.Builder newBuilder();

         Message.Builder getBuilder(Builder var1);

         Message.Builder getRepeatedBuilder(Builder var1, int var2);
      }
   }

   interface ExtensionDescriptorRetriever {
      Descriptors.FieldDescriptor getDescriptor();
   }

   public abstract static class ExtendableBuilder<MessageType extends ExtendableMessage, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends Builder<BuilderType> implements ExtendableMessageOrBuilder<MessageType> {
      private FieldSet.Builder<Descriptors.FieldDescriptor> extensions;

      protected ExtendableBuilder() {
      }

      protected ExtendableBuilder(BuilderParent parent) {
         super(parent);
      }

      void internalSetExtensionSet(FieldSet<Descriptors.FieldDescriptor> extensions) {
         this.extensions = FieldSet.Builder.fromFieldSet(extensions);
      }

      public BuilderType clear() {
         this.extensions = null;
         return (ExtendableBuilder)super.clear();
      }

      private void ensureExtensionsIsMutable() {
         if (this.extensions == null) {
            this.extensions = FieldSet.newBuilder();
         }

      }

      private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
         if (extension.getDescriptor().getContainingType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException("Extension is for type \"" + extension.getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + this.getDescriptorForType().getFullName() + "\".");
         }
      }

      public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
         Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         return this.extensions == null ? false : this.extensions.hasField(extension.getDescriptor());
      }

      public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
         Extension<MessageType, List<Type>> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
         return this.extensions == null ? 0 : this.extensions.getRepeatedFieldCount(descriptor);
      }

      public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
         Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
         Object value = this.extensions == null ? null : this.extensions.getField(descriptor);
         if (value == null) {
            if (descriptor.isRepeated()) {
               return Collections.emptyList();
            } else {
               return descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE ? extension.getMessageDefaultInstance() : extension.fromReflectionType(descriptor.getDefaultValue());
            }
         } else {
            return extension.fromReflectionType(value);
         }
      }

      public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
         Extension<MessageType, List<Type>> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
         if (this.extensions == null) {
            throw new IndexOutOfBoundsException();
         } else {
            return extension.singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
         }
      }

      public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extensionLite, Type value) {
         Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         this.ensureExtensionsIsMutable();
         Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
         this.extensions.setField(descriptor, extension.toReflectionType(value));
         this.onChanged();
         return this;
      }

      public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index, Type value) {
         Extension<MessageType, List<Type>> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         this.ensureExtensionsIsMutable();
         Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
         this.extensions.setRepeatedField(descriptor, index, extension.singularToReflectionType(value));
         this.onChanged();
         return this;
      }

      public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extensionLite, Type value) {
         Extension<MessageType, List<Type>> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         this.ensureExtensionsIsMutable();
         Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
         this.extensions.addRepeatedField(descriptor, extension.singularToReflectionType(value));
         this.onChanged();
         return this;
      }

      public final BuilderType clearExtension(ExtensionLite<MessageType, ?> extensionLite) {
         Extension<MessageType, ?> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         this.ensureExtensionsIsMutable();
         this.extensions.clearField(extension.getDescriptor());
         this.onChanged();
         return this;
      }

      public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
         return this.hasExtension((ExtensionLite)extension);
      }

      public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
         return this.hasExtension((ExtensionLite)extension);
      }

      public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
         return this.getExtensionCount((ExtensionLite)extension);
      }

      public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
         return this.getExtensionCount((ExtensionLite)extension);
      }

      public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
         return this.getExtension((ExtensionLite)extension);
      }

      public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
         return this.getExtension((ExtensionLite)extension);
      }

      public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
         return this.getExtension((ExtensionLite)extension, index);
      }

      public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
         return this.getExtension((ExtensionLite)extension, index);
      }

      public final <Type> BuilderType setExtension(Extension<MessageType, Type> extension, Type value) {
         return this.setExtension((ExtensionLite)extension, value);
      }

      public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension, Type value) {
         return this.setExtension((ExtensionLite)extension, value);
      }

      public final <Type> BuilderType setExtension(Extension<MessageType, List<Type>> extension, int index, Type value) {
         return this.setExtension((ExtensionLite)extension, index, value);
      }

      public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index, Type value) {
         return this.setExtension((ExtensionLite)extension, index, value);
      }

      public final <Type> BuilderType addExtension(Extension<MessageType, List<Type>> extension, Type value) {
         return this.addExtension((ExtensionLite)extension, value);
      }

      public <Type> BuilderType addExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, Type value) {
         return this.addExtension((ExtensionLite)extension, value);
      }

      public final <Type> BuilderType clearExtension(Extension<MessageType, ?> extension) {
         return this.clearExtension((ExtensionLite)extension);
      }

      public <Type> BuilderType clearExtension(GeneratedMessage.GeneratedExtension<MessageType, ?> extension) {
         return this.clearExtension((ExtensionLite)extension);
      }

      protected boolean extensionsAreInitialized() {
         return this.extensions == null ? true : this.extensions.isInitialized();
      }

      private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
         return this.extensions == null ? FieldSet.emptySet() : this.extensions.build();
      }

      public boolean isInitialized() {
         return super.isInitialized() && this.extensionsAreInitialized();
      }

      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         Map<Descriptors.FieldDescriptor, Object> result = super.getAllFieldsMutable();
         if (this.extensions != null) {
            result.putAll(this.extensions.getAllFields());
         }

         return Collections.unmodifiableMap(result);
      }

      public Object getField(Descriptors.FieldDescriptor field) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            Object value = this.extensions == null ? null : this.extensions.getField(field);
            if (value == null) {
               return field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE ? DynamicMessage.getDefaultInstance(field.getMessageType()) : field.getDefaultValue();
            } else {
               return value;
            }
         } else {
            return super.getField(field);
         }
      }

      public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
            } else {
               this.ensureExtensionsIsMutable();
               Object value = this.extensions.getFieldAllowBuilders(field);
               if (value == null) {
                  Message.Builder builder = DynamicMessage.newBuilder(field.getMessageType());
                  this.extensions.setField(field, builder);
                  this.onChanged();
                  return builder;
               } else if (value instanceof Message.Builder) {
                  return (Message.Builder)value;
               } else if (value instanceof Message) {
                  Message.Builder builder = ((Message)value).toBuilder();
                  this.extensions.setField(field, builder);
                  this.onChanged();
                  return builder;
               } else {
                  throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
               }
            }
         } else {
            return super.getFieldBuilder(field);
         }
      }

      public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            return this.extensions == null ? 0 : this.extensions.getRepeatedFieldCount(field);
         } else {
            return super.getRepeatedFieldCount(field);
         }
      }

      public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            if (this.extensions == null) {
               throw new IndexOutOfBoundsException();
            } else {
               return this.extensions.getRepeatedField(field, index);
            }
         } else {
            return super.getRepeatedField(field, index);
         }
      }

      public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            this.ensureExtensionsIsMutable();
            if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
            } else {
               Object value = this.extensions.getRepeatedFieldAllowBuilders(field, index);
               if (value instanceof Message.Builder) {
                  return (Message.Builder)value;
               } else if (value instanceof Message) {
                  Message.Builder builder = ((Message)value).toBuilder();
                  this.extensions.setRepeatedField(field, index, builder);
                  this.onChanged();
                  return builder;
               } else {
                  throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
               }
            }
         } else {
            return super.getRepeatedFieldBuilder(field, index);
         }
      }

      public boolean hasField(Descriptors.FieldDescriptor field) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            return this.extensions == null ? false : this.extensions.hasField(field);
         } else {
            return super.hasField(field);
         }
      }

      public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            this.ensureExtensionsIsMutable();
            this.extensions.setField(field, value);
            this.onChanged();
            return this;
         } else {
            return (ExtendableBuilder)super.setField(field, value);
         }
      }

      public BuilderType clearField(Descriptors.FieldDescriptor field) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            this.ensureExtensionsIsMutable();
            this.extensions.clearField(field);
            this.onChanged();
            return this;
         } else {
            return (ExtendableBuilder)super.clearField(field);
         }
      }

      public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            this.ensureExtensionsIsMutable();
            this.extensions.setRepeatedField(field, index, value);
            this.onChanged();
            return this;
         } else {
            return (ExtendableBuilder)super.setRepeatedField(field, index, value);
         }
      }

      public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            this.ensureExtensionsIsMutable();
            this.extensions.addRepeatedField(field, value);
            this.onChanged();
            return this;
         } else {
            return (ExtendableBuilder)super.addRepeatedField(field, value);
         }
      }

      public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
         return (Message.Builder)(field.isExtension() ? DynamicMessage.newBuilder(field.getMessageType()) : super.newBuilderForField(field));
      }

      protected final void mergeExtensionFields(ExtendableMessage other) {
         if (other.extensions != null) {
            this.ensureExtensionsIsMutable();
            this.extensions.mergeFrom(other.extensions);
            this.onChanged();
         }

      }

      private void verifyContainingType(Descriptors.FieldDescriptor field) {
         if (field.getContainingType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         }
      }
   }

   public abstract static class ExtendableMessage<MessageType extends ExtendableMessage> extends GeneratedMessageV3 implements ExtendableMessageOrBuilder<MessageType> {
      private static final long serialVersionUID = 1L;
      private final FieldSet<Descriptors.FieldDescriptor> extensions;

      protected ExtendableMessage() {
         this.extensions = FieldSet.newFieldSet();
      }

      protected ExtendableMessage(ExtendableBuilder<MessageType, ?> builder) {
         super(builder);
         this.extensions = builder.buildExtensions();
      }

      private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
         if (extension.getDescriptor().getContainingType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException("Extension is for type \"" + extension.getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + this.getDescriptorForType().getFullName() + "\".");
         }
      }

      public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
         Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         return this.extensions.hasField(extension.getDescriptor());
      }

      public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
         Extension<MessageType, List<Type>> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
         return this.extensions.getRepeatedFieldCount(descriptor);
      }

      public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
         Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
         Object value = this.extensions.getField(descriptor);
         if (value == null) {
            if (descriptor.isRepeated()) {
               return Collections.emptyList();
            } else {
               return descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE ? extension.getMessageDefaultInstance() : extension.fromReflectionType(descriptor.getDefaultValue());
            }
         } else {
            return extension.fromReflectionType(value);
         }
      }

      public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
         Extension<MessageType, List<Type>> extension = GeneratedMessageV3.checkNotLite(extensionLite);
         this.verifyExtensionContainingType(extension);
         Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
         return extension.singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
      }

      public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
         return this.hasExtension((ExtensionLite)extension);
      }

      public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
         return this.hasExtension((ExtensionLite)extension);
      }

      public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
         return this.getExtensionCount((ExtensionLite)extension);
      }

      public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
         return this.getExtensionCount((ExtensionLite)extension);
      }

      public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
         return this.getExtension((ExtensionLite)extension);
      }

      public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
         return this.getExtension((ExtensionLite)extension);
      }

      public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
         return this.getExtension((ExtensionLite)extension, index);
      }

      public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
         return this.getExtension((ExtensionLite)extension, index);
      }

      protected boolean extensionsAreInitialized() {
         return this.extensions.isInitialized();
      }

      public boolean isInitialized() {
         return super.isInitialized() && this.extensionsAreInitialized();
      }

      protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
         return MessageReflection.mergeFieldFrom(input, input.shouldDiscardUnknownFields() ? null : unknownFields, extensionRegistry, this.getDescriptorForType(), new MessageReflection.ExtensionAdapter(this.extensions), tag);
      }

      protected boolean parseUnknownFieldProto3(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
         return this.parseUnknownField(input, unknownFields, extensionRegistry, tag);
      }

      protected void makeExtensionsImmutable() {
         this.extensions.makeImmutable();
      }

      protected ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter() {
         return new ExtensionWriter(false);
      }

      protected ExtendableMessage<MessageType>.ExtensionWriter newMessageSetExtensionWriter() {
         return new ExtensionWriter(true);
      }

      protected int extensionsSerializedSize() {
         return this.extensions.getSerializedSize();
      }

      protected int extensionsSerializedSizeAsMessageSet() {
         return this.extensions.getMessageSetSerializedSize();
      }

      protected Map<Descriptors.FieldDescriptor, Object> getExtensionFields() {
         return this.extensions.getAllFields();
      }

      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         Map<Descriptors.FieldDescriptor, Object> result = super.getAllFieldsMutable(false);
         result.putAll(this.getExtensionFields());
         return Collections.unmodifiableMap(result);
      }

      public Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
         Map<Descriptors.FieldDescriptor, Object> result = super.getAllFieldsMutable(false);
         result.putAll(this.getExtensionFields());
         return Collections.unmodifiableMap(result);
      }

      public boolean hasField(Descriptors.FieldDescriptor field) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            return this.extensions.hasField(field);
         } else {
            return super.hasField(field);
         }
      }

      public Object getField(Descriptors.FieldDescriptor field) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            Object value = this.extensions.getField(field);
            if (value == null) {
               if (field.isRepeated()) {
                  return Collections.emptyList();
               } else {
                  return field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE ? DynamicMessage.getDefaultInstance(field.getMessageType()) : field.getDefaultValue();
               }
            } else {
               return value;
            }
         } else {
            return super.getField(field);
         }
      }

      public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            return this.extensions.getRepeatedFieldCount(field);
         } else {
            return super.getRepeatedFieldCount(field);
         }
      }

      public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
         if (field.isExtension()) {
            this.verifyContainingType(field);
            return this.extensions.getRepeatedField(field, index);
         } else {
            return super.getRepeatedField(field, index);
         }
      }

      private void verifyContainingType(Descriptors.FieldDescriptor field) {
         if (field.getContainingType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         }
      }

      protected class ExtensionWriter {
         private final Iterator<Map.Entry<Descriptors.FieldDescriptor, Object>> iter;
         private Map.Entry<Descriptors.FieldDescriptor, Object> next;
         private final boolean messageSetWireFormat;

         private ExtensionWriter(boolean messageSetWireFormat) {
            this.iter = ExtendableMessage.this.extensions.iterator();
            if (this.iter.hasNext()) {
               this.next = (Map.Entry)this.iter.next();
            }

            this.messageSetWireFormat = messageSetWireFormat;
         }

         public void writeUntil(int end, CodedOutputStream output) throws IOException {
            while(this.next != null && ((Descriptors.FieldDescriptor)this.next.getKey()).getNumber() < end) {
               Descriptors.FieldDescriptor descriptor = (Descriptors.FieldDescriptor)this.next.getKey();
               if (this.messageSetWireFormat && descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated()) {
                  if (this.next instanceof LazyField.LazyEntry) {
                     output.writeRawMessageSetExtension(descriptor.getNumber(), ((LazyField.LazyEntry)this.next).getField().toByteString());
                  } else {
                     output.writeMessageSetExtension(descriptor.getNumber(), (Message)this.next.getValue());
                  }
               } else {
                  FieldSet.writeField(descriptor, this.next.getValue(), output);
               }

               if (this.iter.hasNext()) {
                  this.next = (Map.Entry)this.iter.next();
               } else {
                  this.next = null;
               }
            }

         }

         // $FF: synthetic method
         ExtensionWriter(boolean x1, Object x2) {
            this(x1);
         }
      }
   }

   public interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage> extends MessageOrBuilder {
      Message getDefaultInstanceForType();

      <Type> boolean hasExtension(ExtensionLite<MessageType, Type> var1);

      <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> var1);

      <Type> Type getExtension(ExtensionLite<MessageType, Type> var1);

      <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> var1, int var2);

      <Type> boolean hasExtension(Extension<MessageType, Type> var1);

      <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1);

      <Type> int getExtensionCount(Extension<MessageType, List<Type>> var1);

      <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1);

      <Type> Type getExtension(Extension<MessageType, Type> var1);

      <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1);

      <Type> Type getExtension(Extension<MessageType, List<Type>> var1, int var2);

      <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1, int var2);
   }

   public abstract static class Builder<BuilderType extends Builder<BuilderType>> extends AbstractMessage.Builder<BuilderType> {
      private BuilderParent builderParent;
      private Builder<BuilderType>.BuilderParentImpl meAsParent;
      private boolean isClean;
      private UnknownFieldSet unknownFields;

      protected Builder() {
         this((BuilderParent)null);
      }

      protected Builder(BuilderParent builderParent) {
         this.unknownFields = UnknownFieldSet.getDefaultInstance();
         this.builderParent = builderParent;
      }

      void dispose() {
         this.builderParent = null;
      }

      protected void onBuilt() {
         if (this.builderParent != null) {
            this.markClean();
         }

      }

      protected void markClean() {
         this.isClean = true;
      }

      protected boolean isClean() {
         return this.isClean;
      }

      public BuilderType clone() {
         BuilderType builder = (Builder)this.getDefaultInstanceForType().newBuilderForType();
         builder.mergeFrom(this.buildPartial());
         return builder;
      }

      public BuilderType clear() {
         this.unknownFields = UnknownFieldSet.getDefaultInstance();
         this.onChanged();
         return this;
      }

      protected abstract FieldAccessorTable internalGetFieldAccessorTable();

      public Descriptors.Descriptor getDescriptorForType() {
         return this.internalGetFieldAccessorTable().descriptor;
      }

      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         return Collections.unmodifiableMap(this.getAllFieldsMutable());
      }

      private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable() {
         TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap();
         Descriptors.Descriptor descriptor = this.internalGetFieldAccessorTable().descriptor;
         List<Descriptors.FieldDescriptor> fields = descriptor.getFields();

         for(int i = 0; i < fields.size(); ++i) {
            Descriptors.FieldDescriptor field = (Descriptors.FieldDescriptor)fields.get(i);
            Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
            if (oneofDescriptor != null) {
               i += oneofDescriptor.getFieldCount() - 1;
               if (!this.hasOneof(oneofDescriptor)) {
                  continue;
               }

               field = this.getOneofFieldDescriptor(oneofDescriptor);
            } else {
               if (field.isRepeated()) {
                  List<?> value = (List)this.getField(field);
                  if (!value.isEmpty()) {
                     result.put(field, value);
                  }
                  continue;
               }

               if (!this.hasField(field)) {
                  continue;
               }
            }

            result.put(field, this.getField(field));
         }

         return result;
      }

      public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
         return this.internalGetFieldAccessorTable().getField(field).newBuilder();
      }

      public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
         return this.internalGetFieldAccessorTable().getField(field).getBuilder(this);
      }

      public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
         return this.internalGetFieldAccessorTable().getField(field).getRepeatedBuilder(this, index);
      }

      public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
         return this.internalGetFieldAccessorTable().getOneof(oneof).has(this);
      }

      public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
         return this.internalGetFieldAccessorTable().getOneof(oneof).get(this);
      }

      public boolean hasField(Descriptors.FieldDescriptor field) {
         return this.internalGetFieldAccessorTable().getField(field).has(this);
      }

      public Object getField(Descriptors.FieldDescriptor field) {
         Object object = this.internalGetFieldAccessorTable().getField(field).get(this);
         return field.isRepeated() ? Collections.unmodifiableList((List)object) : object;
      }

      public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
         this.internalGetFieldAccessorTable().getField(field).set(this, value);
         return this;
      }

      public BuilderType clearField(Descriptors.FieldDescriptor field) {
         this.internalGetFieldAccessorTable().getField(field).clear(this);
         return this;
      }

      public BuilderType clearOneof(Descriptors.OneofDescriptor oneof) {
         this.internalGetFieldAccessorTable().getOneof(oneof).clear(this);
         return this;
      }

      public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
         return this.internalGetFieldAccessorTable().getField(field).getRepeatedCount(this);
      }

      public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
         return this.internalGetFieldAccessorTable().getField(field).getRepeated(this, index);
      }

      public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
         this.internalGetFieldAccessorTable().getField(field).setRepeated(this, index, value);
         return this;
      }

      public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
         this.internalGetFieldAccessorTable().getField(field).addRepeated(this, value);
         return this;
      }

      private BuilderType setUnknownFieldsInternal(UnknownFieldSet unknownFields) {
         this.unknownFields = unknownFields;
         this.onChanged();
         return this;
      }

      public BuilderType setUnknownFields(UnknownFieldSet unknownFields) {
         return this.setUnknownFieldsInternal(unknownFields);
      }

      protected BuilderType setUnknownFieldsProto3(UnknownFieldSet unknownFields) {
         return this.setUnknownFieldsInternal(unknownFields);
      }

      public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
         return this.setUnknownFields(UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build());
      }

      public boolean isInitialized() {
         Iterator var1 = this.getDescriptorForType().getFields().iterator();

         while(true) {
            while(true) {
               Descriptors.FieldDescriptor field;
               do {
                  if (!var1.hasNext()) {
                     return true;
                  }

                  field = (Descriptors.FieldDescriptor)var1.next();
                  if (field.isRequired() && !this.hasField(field)) {
                     return false;
                  }
               } while(field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE);

               if (field.isRepeated()) {
                  List<Message> messageList = (List)this.getField(field);
                  Iterator var4 = messageList.iterator();

                  while(var4.hasNext()) {
                     Message element = (Message)var4.next();
                     if (!element.isInitialized()) {
                        return false;
                     }
                  }
               } else if (this.hasField(field) && !((Message)this.getField(field)).isInitialized()) {
                  return false;
               }
            }
         }
      }

      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      protected BuilderParent getParentForChildren() {
         if (this.meAsParent == null) {
            this.meAsParent = new BuilderParentImpl();
         }

         return this.meAsParent;
      }

      protected final void onChanged() {
         if (this.isClean && this.builderParent != null) {
            this.builderParent.markDirty();
            this.isClean = false;
         }

      }

      protected MapField internalGetMapField(int fieldNumber) {
         throw new RuntimeException("No map fields found in " + this.getClass().getName());
      }

      protected MapField internalGetMutableMapField(int fieldNumber) {
         throw new RuntimeException("No map fields found in " + this.getClass().getName());
      }

      private class BuilderParentImpl implements BuilderParent {
         private BuilderParentImpl() {
         }

         public void markDirty() {
            Builder.this.onChanged();
         }

         // $FF: synthetic method
         BuilderParentImpl(Object x1) {
            this();
         }
      }
   }

   protected interface BuilderParent extends AbstractMessage.BuilderParent {
   }

   protected static final class UnusedPrivateParameter {
      static final UnusedPrivateParameter INSTANCE = new UnusedPrivateParameter();

      private UnusedPrivateParameter() {
      }
   }
}
