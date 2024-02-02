package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

abstract class BinaryReader implements Reader {
   private static final int FIXED32_MULTIPLE_MASK = 3;
   private static final int FIXED64_MULTIPLE_MASK = 7;

   public static BinaryReader newInstance(ByteBuffer buffer, boolean bufferIsImmutable) {
      if (buffer.hasArray()) {
         return new SafeHeapReader(buffer, bufferIsImmutable);
      } else {
         throw new IllegalArgumentException("Direct buffers not yet supported");
      }
   }

   private BinaryReader() {
   }

   public abstract int getTotalBytesRead();

   public boolean shouldDiscardUnknownFields() {
      return false;
   }

   // $FF: synthetic method
   BinaryReader(Object x0) {
      this();
   }

   private static final class SafeHeapReader extends BinaryReader {
      private final boolean bufferIsImmutable;
      private final byte[] buffer;
      private int pos;
      private final int initialPos;
      private int limit;
      private int tag;
      private int endGroupTag;

      public SafeHeapReader(ByteBuffer bytebuf, boolean bufferIsImmutable) {
         super(null);
         this.bufferIsImmutable = bufferIsImmutable;
         this.buffer = bytebuf.array();
         this.initialPos = this.pos = bytebuf.arrayOffset() + bytebuf.position();
         this.limit = bytebuf.arrayOffset() + bytebuf.limit();
      }

      private boolean isAtEnd() {
         return this.pos == this.limit;
      }

      public int getTotalBytesRead() {
         return this.pos - this.initialPos;
      }

      public int getFieldNumber() throws IOException {
         if (this.isAtEnd()) {
            return Integer.MAX_VALUE;
         } else {
            this.tag = this.readVarint32();
            return this.tag == this.endGroupTag ? Integer.MAX_VALUE : WireFormat.getTagFieldNumber(this.tag);
         }
      }

      public int getTag() {
         return this.tag;
      }

      public boolean skipField() throws IOException {
         // $FF: Couldn't be decompiled
      }

      public double readDouble() throws IOException {
         this.requireWireType(1);
         return Double.longBitsToDouble(this.readLittleEndian64());
      }

      public float readFloat() throws IOException {
         this.requireWireType(5);
         return Float.intBitsToFloat(this.readLittleEndian32());
      }

      public long readUInt64() throws IOException {
         this.requireWireType(0);
         return this.readVarint64();
      }

      public long readInt64() throws IOException {
         this.requireWireType(0);
         return this.readVarint64();
      }

      public int readInt32() throws IOException {
         this.requireWireType(0);
         return this.readVarint32();
      }

      public long readFixed64() throws IOException {
         this.requireWireType(1);
         return this.readLittleEndian64();
      }

      public int readFixed32() throws IOException {
         this.requireWireType(5);
         return this.readLittleEndian32();
      }

      public boolean readBool() throws IOException {
         this.requireWireType(0);
         return this.readVarint32() != 0;
      }

      public String readString() throws IOException {
         return this.readStringInternal(false);
      }

      public String readStringRequireUtf8() throws IOException {
         return this.readStringInternal(true);
      }

      public String readStringInternal(boolean requireUtf8) throws IOException {
         this.requireWireType(2);
         int size = this.readVarint32();
         if (size == 0) {
            return "";
         } else {
            this.requireBytes(size);
            if (requireUtf8 && !Utf8.isValidUtf8(this.buffer, this.pos, this.pos + size)) {
               throw InvalidProtocolBufferException.invalidUtf8();
            } else {
               String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
               this.pos += size;
               return result;
            }
         }
      }

      public <T> T readMessage(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
         this.requireWireType(2);
         return this.readMessage(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
      }

      public <T> T readMessageBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
         this.requireWireType(2);
         return this.readMessage(schema, extensionRegistry);
      }

      private <T> T readMessage(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
         int size = this.readVarint32();
         this.requireBytes(size);
         int prevLimit = this.limit;
         int newLimit = this.pos + size;
         this.limit = newLimit;

         Object var7;
         try {
            T message = schema.newInstance();
            schema.mergeFrom(message, this, extensionRegistry);
            schema.makeImmutable(message);
            if (this.pos != newLimit) {
               throw InvalidProtocolBufferException.parseFailure();
            }

            var7 = message;
         } finally {
            this.limit = prevLimit;
         }

         return var7;
      }

      public <T> T readGroup(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
         this.requireWireType(3);
         return this.readGroup(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
      }

      public <T> T readGroupBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
         this.requireWireType(3);
         return this.readGroup(schema, extensionRegistry);
      }

      private <T> T readGroup(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
         int prevEndGroupTag = this.endGroupTag;
         this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);

         Object var5;
         try {
            T message = schema.newInstance();
            schema.mergeFrom(message, this, extensionRegistry);
            schema.makeImmutable(message);
            if (this.tag != this.endGroupTag) {
               throw InvalidProtocolBufferException.parseFailure();
            }

            var5 = message;
         } finally {
            this.endGroupTag = prevEndGroupTag;
         }

         return var5;
      }

      public ByteString readBytes() throws IOException {
         this.requireWireType(2);
         int size = this.readVarint32();
         if (size == 0) {
            return ByteString.EMPTY;
         } else {
            this.requireBytes(size);
            ByteString bytes = this.bufferIsImmutable ? ByteString.wrap(this.buffer, this.pos, size) : ByteString.copyFrom(this.buffer, this.pos, size);
            this.pos += size;
            return bytes;
         }
      }

      public int readUInt32() throws IOException {
         this.requireWireType(0);
         return this.readVarint32();
      }

      public int readEnum() throws IOException {
         this.requireWireType(0);
         return this.readVarint32();
      }

      public int readSFixed32() throws IOException {
         this.requireWireType(5);
         return this.readLittleEndian32();
      }

      public long readSFixed64() throws IOException {
         this.requireWireType(1);
         return this.readLittleEndian64();
      }

      public int readSInt32() throws IOException {
         this.requireWireType(0);
         return CodedInputStream.decodeZigZag32(this.readVarint32());
      }

      public long readSInt64() throws IOException {
         this.requireWireType(0);
         return CodedInputStream.decodeZigZag64(this.readVarint64());
      }

      public void readDoubleList(List<Double> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readFloatList(List<Float> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readUInt64List(List<Long> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readInt64List(List<Long> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readInt32List(List<Integer> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readFixed64List(List<Long> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readFixed32List(List<Integer> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readBoolList(List<Boolean> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readStringList(List<String> target) throws IOException {
         this.readStringListInternal(target, false);
      }

      public void readStringListRequireUtf8(List<String> target) throws IOException {
         this.readStringListInternal(target, true);
      }

      public void readStringListInternal(List<String> target, boolean requireUtf8) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != 2) {
            throw InvalidProtocolBufferException.invalidWireType();
         } else {
            int prevPos;
            if (target instanceof LazyStringList && !requireUtf8) {
               LazyStringList lazyList = (LazyStringList)target;

               int nextTag;
               do {
                  lazyList.add(this.readBytes());
                  if (this.isAtEnd()) {
                     return;
                  }

                  prevPos = this.pos;
                  nextTag = this.readVarint32();
               } while(nextTag == this.tag);

               this.pos = prevPos;
            } else {
               int prevPos;
               do {
                  target.add(this.readStringInternal(requireUtf8));
                  if (this.isAtEnd()) {
                     return;
                  }

                  prevPos = this.pos;
                  prevPos = this.readVarint32();
               } while(prevPos == this.tag);

               this.pos = prevPos;
            }
         }
      }

      public <T> void readMessageList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
         Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
         this.readMessageList(target, schema, extensionRegistry);
      }

      public <T> void readMessageList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != 2) {
            throw InvalidProtocolBufferException.invalidWireType();
         } else {
            int listTag = this.tag;

            int prevPos;
            int nextTag;
            do {
               target.add(this.readMessage(schema, extensionRegistry));
               if (this.isAtEnd()) {
                  return;
               }

               prevPos = this.pos;
               nextTag = this.readVarint32();
            } while(nextTag == listTag);

            this.pos = prevPos;
         }
      }

      public <T> void readGroupList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
         Schema<T> schema = Protobuf.getInstance().schemaFor(targetType);
         this.readGroupList(target, schema, extensionRegistry);
      }

      public <T> void readGroupList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != 3) {
            throw InvalidProtocolBufferException.invalidWireType();
         } else {
            int listTag = this.tag;

            int prevPos;
            int nextTag;
            do {
               target.add(this.readGroup(schema, extensionRegistry));
               if (this.isAtEnd()) {
                  return;
               }

               prevPos = this.pos;
               nextTag = this.readVarint32();
            } while(nextTag == listTag);

            this.pos = prevPos;
         }
      }

      public void readBytesList(List<ByteString> target) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != 2) {
            throw InvalidProtocolBufferException.invalidWireType();
         } else {
            int prevPos;
            int nextTag;
            do {
               target.add(this.readBytes());
               if (this.isAtEnd()) {
                  return;
               }

               prevPos = this.pos;
               nextTag = this.readVarint32();
            } while(nextTag == this.tag);

            this.pos = prevPos;
         }
      }

      public void readUInt32List(List<Integer> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readEnumList(List<Integer> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readSFixed32List(List<Integer> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readSFixed64List(List<Long> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readSInt32List(List<Integer> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public void readSInt64List(List<Long> var1) throws IOException {
         // $FF: Couldn't be decompiled
      }

      public <K, V> void readMap(Map<K, V> target, MapEntryLite.Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
         this.requireWireType(2);
         int size = this.readVarint32();
         this.requireBytes(size);
         int prevLimit = this.limit;
         int newLimit = this.pos + size;
         this.limit = newLimit;

         try {
            K key = metadata.defaultKey;
            V value = metadata.defaultValue;

            while(true) {
               int number = this.getFieldNumber();
               if (number == Integer.MAX_VALUE) {
                  target.put(key, value);
                  return;
               }

               try {
                  switch (number) {
                     case 1:
                        key = this.readField(metadata.keyType, (Class)null, (ExtensionRegistryLite)null);
                        break;
                     case 2:
                        value = this.readField(metadata.valueType, metadata.defaultValue.getClass(), extensionRegistry);
                        break;
                     default:
                        if (!this.skipField()) {
                           throw new InvalidProtocolBufferException("Unable to parse map entry.");
                        }
                  }
               } catch (InvalidProtocolBufferException.InvalidWireTypeException var14) {
                  if (!this.skipField()) {
                     throw new InvalidProtocolBufferException("Unable to parse map entry.");
                  }
               }
            }
         } finally {
            this.limit = prevLimit;
         }
      }

      private Object readField(WireFormat.FieldType fieldType, Class<?> messageType, ExtensionRegistryLite extensionRegistry) throws IOException {
         switch (fieldType) {
            case BOOL:
               return this.readBool();
            case BYTES:
               return this.readBytes();
            case DOUBLE:
               return this.readDouble();
            case ENUM:
               return this.readEnum();
            case FIXED32:
               return this.readFixed32();
            case FIXED64:
               return this.readFixed64();
            case FLOAT:
               return this.readFloat();
            case INT32:
               return this.readInt32();
            case INT64:
               return this.readInt64();
            case MESSAGE:
               return this.readMessage(messageType, extensionRegistry);
            case SFIXED32:
               return this.readSFixed32();
            case SFIXED64:
               return this.readSFixed64();
            case SINT32:
               return this.readSInt32();
            case SINT64:
               return this.readSInt64();
            case STRING:
               return this.readStringRequireUtf8();
            case UINT32:
               return this.readUInt32();
            case UINT64:
               return this.readUInt64();
            default:
               throw new RuntimeException("unsupported field type.");
         }
      }

      private int readVarint32() throws IOException {
         int i = this.pos;
         if (this.limit == this.pos) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            int x;
            if ((x = this.buffer[i++]) >= 0) {
               this.pos = i;
               return x;
            } else if (this.limit - i < 9) {
               return (int)this.readVarint64SlowPath();
            } else {
               if ((x ^= this.buffer[i++] << 7) < 0) {
                  x ^= -128;
               } else if ((x ^= this.buffer[i++] << 14) >= 0) {
                  x ^= 16256;
               } else if ((x ^= this.buffer[i++] << 21) < 0) {
                  x ^= -2080896;
               } else {
                  int y = this.buffer[i++];
                  x ^= y << 28;
                  x ^= 266354560;
                  if (y < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0) {
                     throw InvalidProtocolBufferException.malformedVarint();
                  }
               }

               this.pos = i;
               return x;
            }
         }
      }

      public long readVarint64() throws IOException {
         int i = this.pos;
         if (this.limit == i) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            byte[] buffer = this.buffer;
            int y;
            if ((y = buffer[i++]) >= 0) {
               this.pos = i;
               return (long)y;
            } else if (this.limit - i < 9) {
               return this.readVarint64SlowPath();
            } else {
               long x;
               if ((y ^= buffer[i++] << 7) < 0) {
                  x = (long)(y ^ -128);
               } else if ((y ^= buffer[i++] << 14) >= 0) {
                  x = (long)(y ^ 16256);
               } else if ((y ^= buffer[i++] << 21) < 0) {
                  x = (long)(y ^ -2080896);
               } else if ((x = (long)y ^ (long)buffer[i++] << 28) >= 0L) {
                  x ^= 266354560L;
               } else if ((x ^= (long)buffer[i++] << 35) < 0L) {
                  x ^= -34093383808L;
               } else if ((x ^= (long)buffer[i++] << 42) >= 0L) {
                  x ^= 4363953127296L;
               } else if ((x ^= (long)buffer[i++] << 49) < 0L) {
                  x ^= -558586000294016L;
               } else {
                  x ^= (long)buffer[i++] << 56;
                  x ^= 71499008037633920L;
                  if (x < 0L && (long)buffer[i++] < 0L) {
                     throw InvalidProtocolBufferException.malformedVarint();
                  }
               }

               this.pos = i;
               return x;
            }
         }
      }

      private long readVarint64SlowPath() throws IOException {
         long result = 0L;

         for(int shift = 0; shift < 64; shift += 7) {
            byte b = this.readByte();
            result |= (long)(b & 127) << shift;
            if ((b & 128) == 0) {
               return result;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private byte readByte() throws IOException {
         if (this.pos == this.limit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return this.buffer[this.pos++];
         }
      }

      private int readLittleEndian32() throws IOException {
         this.requireBytes(4);
         return this.readLittleEndian32_NoCheck();
      }

      private long readLittleEndian64() throws IOException {
         this.requireBytes(8);
         return this.readLittleEndian64_NoCheck();
      }

      private int readLittleEndian32_NoCheck() {
         int p = this.pos;
         byte[] buffer = this.buffer;
         this.pos = p + 4;
         return buffer[p] & 255 | (buffer[p + 1] & 255) << 8 | (buffer[p + 2] & 255) << 16 | (buffer[p + 3] & 255) << 24;
      }

      private long readLittleEndian64_NoCheck() {
         int p = this.pos;
         byte[] buffer = this.buffer;
         this.pos = p + 8;
         return (long)buffer[p] & 255L | ((long)buffer[p + 1] & 255L) << 8 | ((long)buffer[p + 2] & 255L) << 16 | ((long)buffer[p + 3] & 255L) << 24 | ((long)buffer[p + 4] & 255L) << 32 | ((long)buffer[p + 5] & 255L) << 40 | ((long)buffer[p + 6] & 255L) << 48 | ((long)buffer[p + 7] & 255L) << 56;
      }

      private void skipVarint() throws IOException {
         if (this.limit - this.pos >= 10) {
            byte[] buffer = this.buffer;
            int p = this.pos;

            for(int i = 0; i < 10; ++i) {
               if (buffer[p++] >= 0) {
                  this.pos = p;
                  return;
               }
            }
         }

         this.skipVarintSlowPath();
      }

      private void skipVarintSlowPath() throws IOException {
         for(int i = 0; i < 10; ++i) {
            if (this.readByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipBytes(int size) throws IOException {
         this.requireBytes(size);
         this.pos += size;
      }

      private void skipGroup() throws IOException {
         int prevEndGroupTag = this.endGroupTag;
         this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);

         while(this.getFieldNumber() != Integer.MAX_VALUE && this.skipField()) {
         }

         if (this.tag != this.endGroupTag) {
            throw InvalidProtocolBufferException.parseFailure();
         } else {
            this.endGroupTag = prevEndGroupTag;
         }
      }

      private void requireBytes(int size) throws IOException {
         if (size < 0 || size > this.limit - this.pos) {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void requireWireType(int requiredWireType) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != requiredWireType) {
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      private void verifyPackedFixed64Length(int bytes) throws IOException {
         this.requireBytes(bytes);
         if ((bytes & 7) != 0) {
            throw InvalidProtocolBufferException.parseFailure();
         }
      }

      private void verifyPackedFixed32Length(int bytes) throws IOException {
         this.requireBytes(bytes);
         if ((bytes & 3) != 0) {
            throw InvalidProtocolBufferException.parseFailure();
         }
      }

      private void requirePosition(int expectedPosition) throws IOException {
         if (this.pos != expectedPosition) {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }
   }
}
