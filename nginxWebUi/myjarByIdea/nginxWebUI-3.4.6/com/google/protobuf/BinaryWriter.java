package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

abstract class BinaryWriter extends ByteOutput implements Writer {
   public static final int DEFAULT_CHUNK_SIZE = 4096;
   private final BufferAllocator alloc;
   private final int chunkSize;
   final ArrayDeque<AllocatedBuffer> buffers;
   int totalDoneBytes;
   private static final int MAP_KEY_NUMBER = 1;
   private static final int MAP_VALUE_NUMBER = 2;

   public static BinaryWriter newHeapInstance(BufferAllocator alloc) {
      return newHeapInstance(alloc, 4096);
   }

   public static BinaryWriter newHeapInstance(BufferAllocator alloc, int chunkSize) {
      return isUnsafeHeapSupported() ? newUnsafeHeapInstance(alloc, chunkSize) : newSafeHeapInstance(alloc, chunkSize);
   }

   public static BinaryWriter newDirectInstance(BufferAllocator alloc) {
      return newDirectInstance(alloc, 4096);
   }

   public static BinaryWriter newDirectInstance(BufferAllocator alloc, int chunkSize) {
      return isUnsafeDirectSupported() ? newUnsafeDirectInstance(alloc, chunkSize) : newSafeDirectInstance(alloc, chunkSize);
   }

   static boolean isUnsafeHeapSupported() {
      return BinaryWriter.UnsafeHeapWriter.isSupported();
   }

   static boolean isUnsafeDirectSupported() {
      return BinaryWriter.UnsafeDirectWriter.isSupported();
   }

   static BinaryWriter newSafeHeapInstance(BufferAllocator alloc, int chunkSize) {
      return new SafeHeapWriter(alloc, chunkSize);
   }

   static BinaryWriter newUnsafeHeapInstance(BufferAllocator alloc, int chunkSize) {
      if (!isUnsafeHeapSupported()) {
         throw new UnsupportedOperationException("Unsafe operations not supported");
      } else {
         return new UnsafeHeapWriter(alloc, chunkSize);
      }
   }

   static BinaryWriter newSafeDirectInstance(BufferAllocator alloc, int chunkSize) {
      return new SafeDirectWriter(alloc, chunkSize);
   }

   static BinaryWriter newUnsafeDirectInstance(BufferAllocator alloc, int chunkSize) {
      if (!isUnsafeDirectSupported()) {
         throw new UnsupportedOperationException("Unsafe operations not supported");
      } else {
         return new UnsafeDirectWriter(alloc, chunkSize);
      }
   }

   private BinaryWriter(BufferAllocator alloc, int chunkSize) {
      this.buffers = new ArrayDeque(4);
      if (chunkSize <= 0) {
         throw new IllegalArgumentException("chunkSize must be > 0");
      } else {
         this.alloc = (BufferAllocator)Internal.checkNotNull(alloc, "alloc");
         this.chunkSize = chunkSize;
      }
   }

   public final Writer.FieldOrder fieldOrder() {
      return Writer.FieldOrder.DESCENDING;
   }

   public final Queue<AllocatedBuffer> complete() {
      this.finishCurrentBuffer();
      return this.buffers;
   }

   public final void writeSFixed32(int fieldNumber, int value) throws IOException {
      this.writeFixed32(fieldNumber, value);
   }

   public final void writeInt64(int fieldNumber, long value) throws IOException {
      this.writeUInt64(fieldNumber, value);
   }

   public final void writeSFixed64(int fieldNumber, long value) throws IOException {
      this.writeFixed64(fieldNumber, value);
   }

   public final void writeFloat(int fieldNumber, float value) throws IOException {
      this.writeFixed32(fieldNumber, Float.floatToRawIntBits(value));
   }

   public final void writeDouble(int fieldNumber, double value) throws IOException {
      this.writeFixed64(fieldNumber, Double.doubleToRawLongBits(value));
   }

   public final void writeEnum(int fieldNumber, int value) throws IOException {
      this.writeInt32(fieldNumber, value);
   }

   public final void writeInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      if (list instanceof IntArrayList) {
         this.writeInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
      } else {
         this.writeInt32List_Internal(fieldNumber, list, packed);
      }

   }

   private final void writeInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 10);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeInt32((Integer)list.get(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeInt32(fieldNumber, (Integer)list.get(prevBytes));
         }
      }

   }

   private final void writeInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 10);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeInt32(list.getInt(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeInt32(fieldNumber, list.getInt(prevBytes));
         }
      }

   }

   public final void writeFixed32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      if (list instanceof IntArrayList) {
         this.writeFixed32List_Internal(fieldNumber, (IntArrayList)list, packed);
      } else {
         this.writeFixed32List_Internal(fieldNumber, list, packed);
      }

   }

   private final void writeFixed32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 4);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeFixed32((Integer)list.get(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeFixed32(fieldNumber, (Integer)list.get(prevBytes));
         }
      }

   }

   private final void writeFixed32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 4);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeFixed32(list.getInt(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeFixed32(fieldNumber, list.getInt(prevBytes));
         }
      }

   }

   public final void writeInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
      this.writeUInt64List(fieldNumber, list, packed);
   }

   public final void writeUInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
      if (list instanceof LongArrayList) {
         this.writeUInt64List_Internal(fieldNumber, (LongArrayList)list, packed);
      } else {
         this.writeUInt64List_Internal(fieldNumber, list, packed);
      }

   }

   private final void writeUInt64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 10);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeVarint64((Long)list.get(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeUInt64(fieldNumber, (Long)list.get(prevBytes));
         }
      }

   }

   private final void writeUInt64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 10);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeVarint64(list.getLong(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeUInt64(fieldNumber, list.getLong(prevBytes));
         }
      }

   }

   public final void writeFixed64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
      if (list instanceof LongArrayList) {
         this.writeFixed64List_Internal(fieldNumber, (LongArrayList)list, packed);
      } else {
         this.writeFixed64List_Internal(fieldNumber, list, packed);
      }

   }

   private final void writeFixed64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 8);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeFixed64((Long)list.get(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeFixed64(fieldNumber, (Long)list.get(prevBytes));
         }
      }

   }

   private final void writeFixed64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 8);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeFixed64(list.getLong(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeFixed64(fieldNumber, list.getLong(prevBytes));
         }
      }

   }

   public final void writeFloatList(int fieldNumber, List<Float> list, boolean packed) throws IOException {
      if (list instanceof FloatArrayList) {
         this.writeFloatList_Internal(fieldNumber, (FloatArrayList)list, packed);
      } else {
         this.writeFloatList_Internal(fieldNumber, list, packed);
      }

   }

   private final void writeFloatList_Internal(int fieldNumber, List<Float> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 4);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeFixed32(Float.floatToRawIntBits((Float)list.get(length)));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeFloat(fieldNumber, (Float)list.get(prevBytes));
         }
      }

   }

   private final void writeFloatList_Internal(int fieldNumber, FloatArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 4);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeFixed32(Float.floatToRawIntBits(list.getFloat(length)));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeFloat(fieldNumber, list.getFloat(prevBytes));
         }
      }

   }

   public final void writeDoubleList(int fieldNumber, List<Double> list, boolean packed) throws IOException {
      if (list instanceof DoubleArrayList) {
         this.writeDoubleList_Internal(fieldNumber, (DoubleArrayList)list, packed);
      } else {
         this.writeDoubleList_Internal(fieldNumber, list, packed);
      }

   }

   private final void writeDoubleList_Internal(int fieldNumber, List<Double> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 8);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeFixed64(Double.doubleToRawLongBits((Double)list.get(length)));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeDouble(fieldNumber, (Double)list.get(prevBytes));
         }
      }

   }

   private final void writeDoubleList_Internal(int fieldNumber, DoubleArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 8);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeFixed64(Double.doubleToRawLongBits(list.getDouble(length)));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeDouble(fieldNumber, list.getDouble(prevBytes));
         }
      }

   }

   public final void writeEnumList(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      this.writeInt32List(fieldNumber, list, packed);
   }

   public final void writeBoolList(int fieldNumber, List<Boolean> list, boolean packed) throws IOException {
      if (list instanceof BooleanArrayList) {
         this.writeBoolList_Internal(fieldNumber, (BooleanArrayList)list, packed);
      } else {
         this.writeBoolList_Internal(fieldNumber, list, packed);
      }

   }

   private final void writeBoolList_Internal(int fieldNumber, List<Boolean> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size());
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeBool((Boolean)list.get(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeBool(fieldNumber, (Boolean)list.get(prevBytes));
         }
      }

   }

   private final void writeBoolList_Internal(int fieldNumber, BooleanArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size());
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeBool(list.getBoolean(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeBool(fieldNumber, list.getBoolean(prevBytes));
         }
      }

   }

   public final void writeStringList(int fieldNumber, List<String> list) throws IOException {
      if (list instanceof LazyStringList) {
         LazyStringList lazyList = (LazyStringList)list;

         for(int i = list.size() - 1; i >= 0; --i) {
            this.writeLazyString(fieldNumber, lazyList.getRaw(i));
         }
      } else {
         for(int i = list.size() - 1; i >= 0; --i) {
            this.writeString(fieldNumber, (String)list.get(i));
         }
      }

   }

   private void writeLazyString(int fieldNumber, Object value) throws IOException {
      if (value instanceof String) {
         this.writeString(fieldNumber, (String)value);
      } else {
         this.writeBytes(fieldNumber, (ByteString)value);
      }

   }

   public final void writeBytesList(int fieldNumber, List<ByteString> list) throws IOException {
      for(int i = list.size() - 1; i >= 0; --i) {
         this.writeBytes(fieldNumber, (ByteString)list.get(i));
      }

   }

   public final void writeUInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      if (list instanceof IntArrayList) {
         this.writeUInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
      } else {
         this.writeUInt32List_Internal(fieldNumber, list, packed);
      }

   }

   private final void writeUInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 5);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeVarint32((Integer)list.get(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeUInt32(fieldNumber, (Integer)list.get(prevBytes));
         }
      }

   }

   private final void writeUInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 5);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeVarint32(list.getInt(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeUInt32(fieldNumber, list.getInt(prevBytes));
         }
      }

   }

   public final void writeSFixed32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      this.writeFixed32List(fieldNumber, list, packed);
   }

   public final void writeSFixed64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
      this.writeFixed64List(fieldNumber, list, packed);
   }

   public final void writeSInt32List(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      if (list instanceof IntArrayList) {
         this.writeSInt32List_Internal(fieldNumber, (IntArrayList)list, packed);
      } else {
         this.writeSInt32List_Internal(fieldNumber, list, packed);
      }

   }

   private final void writeSInt32List_Internal(int fieldNumber, List<Integer> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 5);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeSInt32((Integer)list.get(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeSInt32(fieldNumber, (Integer)list.get(prevBytes));
         }
      }

   }

   private final void writeSInt32List_Internal(int fieldNumber, IntArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 5);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeSInt32(list.getInt(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeSInt32(fieldNumber, list.getInt(prevBytes));
         }
      }

   }

   public final void writeSInt64List(int fieldNumber, List<Long> list, boolean packed) throws IOException {
      if (list instanceof LongArrayList) {
         this.writeSInt64List_Internal(fieldNumber, (LongArrayList)list, packed);
      } else {
         this.writeSInt64List_Internal(fieldNumber, list, packed);
      }

   }

   public <K, V> void writeMap(int fieldNumber, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map) throws IOException {
      Iterator var4 = map.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry<K, V> entry = (Map.Entry)var4.next();
         int prevBytes = this.getTotalBytesWritten();
         writeMapEntryField(this, 2, metadata.valueType, entry.getValue());
         writeMapEntryField(this, 1, metadata.keyType, entry.getKey());
         int length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

   }

   static final void writeMapEntryField(Writer writer, int fieldNumber, WireFormat.FieldType fieldType, Object object) throws IOException {
      switch (fieldType) {
         case BOOL:
            writer.writeBool(fieldNumber, (Boolean)object);
            break;
         case FIXED32:
            writer.writeFixed32(fieldNumber, (Integer)object);
            break;
         case FIXED64:
            writer.writeFixed64(fieldNumber, (Long)object);
            break;
         case INT32:
            writer.writeInt32(fieldNumber, (Integer)object);
            break;
         case INT64:
            writer.writeInt64(fieldNumber, (Long)object);
            break;
         case SFIXED32:
            writer.writeSFixed32(fieldNumber, (Integer)object);
            break;
         case SFIXED64:
            writer.writeSFixed64(fieldNumber, (Long)object);
            break;
         case SINT32:
            writer.writeSInt32(fieldNumber, (Integer)object);
            break;
         case SINT64:
            writer.writeSInt64(fieldNumber, (Long)object);
            break;
         case STRING:
            writer.writeString(fieldNumber, (String)object);
            break;
         case UINT32:
            writer.writeUInt32(fieldNumber, (Integer)object);
            break;
         case UINT64:
            writer.writeUInt64(fieldNumber, (Long)object);
            break;
         case FLOAT:
            writer.writeFloat(fieldNumber, (Float)object);
            break;
         case DOUBLE:
            writer.writeDouble(fieldNumber, (Double)object);
            break;
         case MESSAGE:
            writer.writeMessage(fieldNumber, object);
            break;
         case BYTES:
            writer.writeBytes(fieldNumber, (ByteString)object);
            break;
         case ENUM:
            if (object instanceof Internal.EnumLite) {
               writer.writeEnum(fieldNumber, ((Internal.EnumLite)object).getNumber());
            } else {
               if (!(object instanceof Integer)) {
                  throw new IllegalArgumentException("Unexpected type for enum in map.");
               }

               writer.writeEnum(fieldNumber, (Integer)object);
            }
            break;
         default:
            throw new IllegalArgumentException("Unsupported map value type for: " + fieldType);
      }

   }

   private final void writeSInt64List_Internal(int fieldNumber, List<Long> list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 10);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeSInt64((Long)list.get(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeSInt64(fieldNumber, (Long)list.get(prevBytes));
         }
      }

   }

   private final void writeSInt64List_Internal(int fieldNumber, LongArrayList list, boolean packed) throws IOException {
      int prevBytes;
      if (packed) {
         this.requireSpace(10 + list.size() * 10);
         prevBytes = this.getTotalBytesWritten();

         int length;
         for(length = list.size() - 1; length >= 0; --length) {
            this.writeSInt64(list.getLong(length));
         }

         length = this.getTotalBytesWritten() - prevBytes;
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      } else {
         for(prevBytes = list.size() - 1; prevBytes >= 0; --prevBytes) {
            this.writeSInt64(fieldNumber, list.getLong(prevBytes));
         }
      }

   }

   public final void writeMessageList(int fieldNumber, List<?> list) throws IOException {
      for(int i = list.size() - 1; i >= 0; --i) {
         this.writeMessage(fieldNumber, list.get(i));
      }

   }

   public final void writeMessageList(int fieldNumber, List<?> list, Schema schema) throws IOException {
      for(int i = list.size() - 1; i >= 0; --i) {
         this.writeMessage(fieldNumber, list.get(i), schema);
      }

   }

   public final void writeGroupList(int fieldNumber, List<?> list) throws IOException {
      for(int i = list.size() - 1; i >= 0; --i) {
         this.writeGroup(fieldNumber, list.get(i));
      }

   }

   public final void writeGroupList(int fieldNumber, List<?> list, Schema schema) throws IOException {
      for(int i = list.size() - 1; i >= 0; --i) {
         this.writeGroup(fieldNumber, list.get(i), schema);
      }

   }

   public final void writeMessageSetItem(int fieldNumber, Object value) throws IOException {
      this.writeTag(1, 4);
      if (value instanceof ByteString) {
         this.writeBytes(3, (ByteString)value);
      } else {
         this.writeMessage(3, value);
      }

      this.writeUInt32(2, fieldNumber);
      this.writeTag(1, 3);
   }

   final AllocatedBuffer newHeapBuffer() {
      return this.alloc.allocateHeapBuffer(this.chunkSize);
   }

   final AllocatedBuffer newHeapBuffer(int capacity) {
      return this.alloc.allocateHeapBuffer(Math.max(capacity, this.chunkSize));
   }

   final AllocatedBuffer newDirectBuffer() {
      return this.alloc.allocateDirectBuffer(this.chunkSize);
   }

   final AllocatedBuffer newDirectBuffer(int capacity) {
      return this.alloc.allocateDirectBuffer(Math.max(capacity, this.chunkSize));
   }

   public abstract int getTotalBytesWritten();

   abstract void requireSpace(int var1);

   abstract void finishCurrentBuffer();

   abstract void writeTag(int var1, int var2);

   abstract void writeVarint32(int var1);

   abstract void writeInt32(int var1);

   abstract void writeSInt32(int var1);

   abstract void writeFixed32(int var1);

   abstract void writeVarint64(long var1);

   abstract void writeSInt64(long var1);

   abstract void writeFixed64(long var1);

   abstract void writeBool(boolean var1);

   abstract void writeString(String var1);

   private static byte computeUInt64SizeNoTag(long value) {
      if ((value & -128L) == 0L) {
         return 1;
      } else if (value < 0L) {
         return 10;
      } else {
         byte n = 2;
         if ((value & -34359738368L) != 0L) {
            n = (byte)(n + 4);
            value >>>= 28;
         }

         if ((value & -2097152L) != 0L) {
            n = (byte)(n + 2);
            value >>>= 14;
         }

         if ((value & -16384L) != 0L) {
            ++n;
         }

         return n;
      }
   }

   // $FF: synthetic method
   BinaryWriter(BufferAllocator x0, int x1, Object x2) {
      this(x0, x1);
   }

   // $FF: synthetic method
   static byte access$200(long x0) {
      return computeUInt64SizeNoTag(x0);
   }

   private static final class UnsafeDirectWriter extends BinaryWriter {
      private ByteBuffer buffer;
      private long bufferOffset;
      private long limitMinusOne;
      private long pos;

      UnsafeDirectWriter(BufferAllocator alloc, int chunkSize) {
         super(alloc, chunkSize, null);
         this.nextBuffer();
      }

      private static boolean isSupported() {
         return UnsafeUtil.hasUnsafeByteBufferOperations();
      }

      private void nextBuffer() {
         this.nextBuffer(this.newDirectBuffer());
      }

      private void nextBuffer(int capacity) {
         this.nextBuffer(this.newDirectBuffer(capacity));
      }

      private void nextBuffer(AllocatedBuffer allocatedBuffer) {
         if (!allocatedBuffer.hasNioBuffer()) {
            throw new RuntimeException("Allocated buffer does not have NIO buffer");
         } else {
            ByteBuffer nioBuffer = allocatedBuffer.nioBuffer();
            if (!nioBuffer.isDirect()) {
               throw new RuntimeException("Allocator returned non-direct buffer");
            } else {
               this.finishCurrentBuffer();
               this.buffers.addFirst(allocatedBuffer);
               this.buffer = nioBuffer;
               this.buffer.limit(this.buffer.capacity());
               this.buffer.position(0);
               this.bufferOffset = UnsafeUtil.addressOffset(this.buffer);
               this.limitMinusOne = this.bufferOffset + (long)(this.buffer.limit() - 1);
               this.pos = this.limitMinusOne;
            }
         }
      }

      public int getTotalBytesWritten() {
         return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
      }

      private int bytesWrittenToCurrentBuffer() {
         return (int)(this.limitMinusOne - this.pos);
      }

      private int spaceLeft() {
         return this.bufferPos() + 1;
      }

      void finishCurrentBuffer() {
         if (this.buffer != null) {
            this.totalDoneBytes += this.bytesWrittenToCurrentBuffer();
            this.buffer.position(this.bufferPos() + 1);
            this.buffer = null;
            this.pos = 0L;
            this.limitMinusOne = 0L;
         }

      }

      private int bufferPos() {
         return (int)(this.pos - this.bufferOffset);
      }

      public void writeUInt32(int fieldNumber, int value) {
         this.requireSpace(10);
         this.writeVarint32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeInt32(int fieldNumber, int value) {
         this.requireSpace(15);
         this.writeInt32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeSInt32(int fieldNumber, int value) {
         this.requireSpace(10);
         this.writeSInt32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeFixed32(int fieldNumber, int value) {
         this.requireSpace(9);
         this.writeFixed32(value);
         this.writeTag(fieldNumber, 5);
      }

      public void writeUInt64(int fieldNumber, long value) {
         this.requireSpace(15);
         this.writeVarint64(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeSInt64(int fieldNumber, long value) {
         this.requireSpace(15);
         this.writeSInt64(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeFixed64(int fieldNumber, long value) {
         this.requireSpace(13);
         this.writeFixed64(value);
         this.writeTag(fieldNumber, 1);
      }

      public void writeBool(int fieldNumber, boolean value) {
         this.requireSpace(6);
         this.write((byte)(value ? 1 : 0));
         this.writeTag(fieldNumber, 0);
      }

      public void writeString(int fieldNumber, String value) {
         int prevBytes = this.getTotalBytesWritten();
         this.writeString(value);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeBytes(int fieldNumber, ByteString value) {
         try {
            value.writeToReverse(this);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }

         this.requireSpace(10);
         this.writeVarint32(value.size());
         this.writeTag(fieldNumber, 2);
      }

      public void writeMessage(int fieldNumber, Object value) throws IOException {
         int prevBytes = this.getTotalBytesWritten();
         Protobuf.getInstance().writeTo(value, this);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
         int prevBytes = this.getTotalBytesWritten();
         schema.writeTo(value, this);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeGroup(int fieldNumber, Object value) throws IOException {
         this.writeTag(fieldNumber, 4);
         Protobuf.getInstance().writeTo(value, this);
         this.writeTag(fieldNumber, 3);
      }

      public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
         this.writeTag(fieldNumber, 4);
         schema.writeTo(value, this);
         this.writeTag(fieldNumber, 3);
      }

      public void writeStartGroup(int fieldNumber) {
         this.writeTag(fieldNumber, 3);
      }

      public void writeEndGroup(int fieldNumber) {
         this.writeTag(fieldNumber, 4);
      }

      void writeInt32(int value) {
         if (value >= 0) {
            this.writeVarint32(value);
         } else {
            this.writeVarint64((long)value);
         }

      }

      void writeSInt32(int value) {
         this.writeVarint32(CodedOutputStream.encodeZigZag32(value));
      }

      void writeSInt64(long value) {
         this.writeVarint64(CodedOutputStream.encodeZigZag64(value));
      }

      void writeBool(boolean value) {
         this.write((byte)(value ? 1 : 0));
      }

      void writeTag(int fieldNumber, int wireType) {
         this.writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
      }

      void writeVarint32(int value) {
         if ((value & -128) == 0) {
            this.writeVarint32OneByte(value);
         } else if ((value & -16384) == 0) {
            this.writeVarint32TwoBytes(value);
         } else if ((value & -2097152) == 0) {
            this.writeVarint32ThreeBytes(value);
         } else if ((value & -268435456) == 0) {
            this.writeVarint32FourBytes(value);
         } else {
            this.writeVarint32FiveBytes(value);
         }

      }

      private void writeVarint32OneByte(int value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)value);
      }

      private void writeVarint32TwoBytes(int value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 7));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value & 127 | 128));
      }

      private void writeVarint32ThreeBytes(int value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 14));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 7 & 127 | 128));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value & 127 | 128));
      }

      private void writeVarint32FourBytes(int value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 21));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 14 & 127 | 128));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 7 & 127 | 128));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value & 127 | 128));
      }

      private void writeVarint32FiveBytes(int value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 28));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 21 & 127 | 128));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 14 & 127 | 128));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >>> 7 & 127 | 128));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value & 127 | 128));
      }

      void writeVarint64(long var1) {
         // $FF: Couldn't be decompiled
      }

      private void writeVarint64OneByte(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)value));
      }

      private void writeVarint64TwoBytes(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 7)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)value & 127 | 128));
      }

      private void writeVarint64ThreeBytes(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)value >>> 14));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64FourBytes(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 21)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64FiveBytes(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 28)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64SixBytes(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 35)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64SevenBytes(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 42)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64EightBytes(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 49)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64NineBytes(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 56)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 49 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64TenBytes(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 63)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 56 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 49 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      void writeFixed32(int value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >> 24 & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >> 16 & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value >> 8 & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)(value & 255));
      }

      void writeFixed64(long value) {
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >> 56) & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >> 48) & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >> 40) & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >> 32) & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >> 24) & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >> 16) & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)(value >> 8) & 255));
         UnsafeUtil.putByte((long)(this.pos--), (byte)((int)value & 255));
      }

      void writeString(String in) {
         this.requireSpace(in.length());

         int i;
         char c;
         for(i = in.length() - 1; i >= 0 && (c = in.charAt(i)) < 128; --i) {
            UnsafeUtil.putByte((long)(this.pos--), (byte)c);
         }

         if (i != -1) {
            for(; i >= 0; --i) {
               c = in.charAt(i);
               if (c < 128 && this.pos >= this.bufferOffset) {
                  UnsafeUtil.putByte((long)(this.pos--), (byte)c);
               } else if (c < 2048 && this.pos > this.bufferOffset) {
                  UnsafeUtil.putByte((long)(this.pos--), (byte)(128 | 63 & c));
                  UnsafeUtil.putByte((long)(this.pos--), (byte)(960 | c >>> 6));
               } else if ((c < '\ud800' || '\udfff' < c) && this.pos > this.bufferOffset + 1L) {
                  UnsafeUtil.putByte((long)(this.pos--), (byte)(128 | 63 & c));
                  UnsafeUtil.putByte((long)(this.pos--), (byte)(128 | 63 & c >>> 6));
                  UnsafeUtil.putByte((long)(this.pos--), (byte)(480 | c >>> 12));
               } else if (this.pos > this.bufferOffset + 2L) {
                  char high;
                  if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
                     throw new Utf8.UnpairedSurrogateException(i - 1, i);
                  }

                  --i;
                  int codePoint = Character.toCodePoint(high, c);
                  UnsafeUtil.putByte((long)(this.pos--), (byte)(128 | 63 & codePoint));
                  UnsafeUtil.putByte((long)(this.pos--), (byte)(128 | 63 & codePoint >>> 6));
                  UnsafeUtil.putByte((long)(this.pos--), (byte)(128 | 63 & codePoint >>> 12));
                  UnsafeUtil.putByte((long)(this.pos--), (byte)(240 | codePoint >>> 18));
               } else {
                  this.requireSpace(i);
                  ++i;
               }
            }

         }
      }

      public void write(byte value) {
         UnsafeUtil.putByte((long)(this.pos--), value);
      }

      public void write(byte[] value, int offset, int length) {
         if (this.spaceLeft() < length) {
            this.nextBuffer(length);
         }

         this.pos -= (long)length;
         this.buffer.position(this.bufferPos() + 1);
         this.buffer.put(value, offset, length);
      }

      public void writeLazy(byte[] value, int offset, int length) {
         if (this.spaceLeft() < length) {
            this.totalDoneBytes += length;
            this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
            this.nextBuffer();
         } else {
            this.pos -= (long)length;
            this.buffer.position(this.bufferPos() + 1);
            this.buffer.put(value, offset, length);
         }
      }

      public void write(ByteBuffer value) {
         int length = value.remaining();
         if (this.spaceLeft() < length) {
            this.nextBuffer(length);
         }

         this.pos -= (long)length;
         this.buffer.position(this.bufferPos() + 1);
         this.buffer.put(value);
      }

      public void writeLazy(ByteBuffer value) {
         int length = value.remaining();
         if (this.spaceLeft() < length) {
            this.totalDoneBytes += length;
            this.buffers.addFirst(AllocatedBuffer.wrap(value));
            this.nextBuffer();
         } else {
            this.pos -= (long)length;
            this.buffer.position(this.bufferPos() + 1);
            this.buffer.put(value);
         }
      }

      void requireSpace(int size) {
         if (this.spaceLeft() < size) {
            this.nextBuffer(size);
         }

      }
   }

   private static final class SafeDirectWriter extends BinaryWriter {
      private ByteBuffer buffer;
      private int limitMinusOne;
      private int pos;

      SafeDirectWriter(BufferAllocator alloc, int chunkSize) {
         super(alloc, chunkSize, null);
         this.nextBuffer();
      }

      private void nextBuffer() {
         this.nextBuffer(this.newDirectBuffer());
      }

      private void nextBuffer(int capacity) {
         this.nextBuffer(this.newDirectBuffer(capacity));
      }

      private void nextBuffer(AllocatedBuffer allocatedBuffer) {
         if (!allocatedBuffer.hasNioBuffer()) {
            throw new RuntimeException("Allocated buffer does not have NIO buffer");
         } else {
            ByteBuffer nioBuffer = allocatedBuffer.nioBuffer();
            if (!nioBuffer.isDirect()) {
               throw new RuntimeException("Allocator returned non-direct buffer");
            } else {
               this.finishCurrentBuffer();
               this.buffers.addFirst(allocatedBuffer);
               this.buffer = nioBuffer;
               this.buffer.limit(this.buffer.capacity());
               this.buffer.position(0);
               this.buffer.order(ByteOrder.LITTLE_ENDIAN);
               this.limitMinusOne = this.buffer.limit() - 1;
               this.pos = this.limitMinusOne;
            }
         }
      }

      public int getTotalBytesWritten() {
         return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
      }

      private int bytesWrittenToCurrentBuffer() {
         return this.limitMinusOne - this.pos;
      }

      private int spaceLeft() {
         return this.pos + 1;
      }

      void finishCurrentBuffer() {
         if (this.buffer != null) {
            this.totalDoneBytes += this.bytesWrittenToCurrentBuffer();
            this.buffer.position(this.pos + 1);
            this.buffer = null;
            this.pos = 0;
            this.limitMinusOne = 0;
         }

      }

      public void writeUInt32(int fieldNumber, int value) {
         this.requireSpace(10);
         this.writeVarint32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeInt32(int fieldNumber, int value) {
         this.requireSpace(15);
         this.writeInt32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeSInt32(int fieldNumber, int value) {
         this.requireSpace(10);
         this.writeSInt32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeFixed32(int fieldNumber, int value) {
         this.requireSpace(9);
         this.writeFixed32(value);
         this.writeTag(fieldNumber, 5);
      }

      public void writeUInt64(int fieldNumber, long value) {
         this.requireSpace(15);
         this.writeVarint64(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeSInt64(int fieldNumber, long value) {
         this.requireSpace(15);
         this.writeSInt64(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeFixed64(int fieldNumber, long value) {
         this.requireSpace(13);
         this.writeFixed64(value);
         this.writeTag(fieldNumber, 1);
      }

      public void writeBool(int fieldNumber, boolean value) {
         this.requireSpace(6);
         this.write((byte)(value ? 1 : 0));
         this.writeTag(fieldNumber, 0);
      }

      public void writeString(int fieldNumber, String value) {
         int prevBytes = this.getTotalBytesWritten();
         this.writeString(value);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeBytes(int fieldNumber, ByteString value) {
         try {
            value.writeToReverse(this);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }

         this.requireSpace(10);
         this.writeVarint32(value.size());
         this.writeTag(fieldNumber, 2);
      }

      public void writeMessage(int fieldNumber, Object value) throws IOException {
         int prevBytes = this.getTotalBytesWritten();
         Protobuf.getInstance().writeTo(value, this);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
         int prevBytes = this.getTotalBytesWritten();
         schema.writeTo(value, this);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeGroup(int fieldNumber, Object value) throws IOException {
         this.writeTag(fieldNumber, 4);
         Protobuf.getInstance().writeTo(value, this);
         this.writeTag(fieldNumber, 3);
      }

      public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
         this.writeTag(fieldNumber, 4);
         schema.writeTo(value, this);
         this.writeTag(fieldNumber, 3);
      }

      public void writeStartGroup(int fieldNumber) {
         this.writeTag(fieldNumber, 3);
      }

      public void writeEndGroup(int fieldNumber) {
         this.writeTag(fieldNumber, 4);
      }

      void writeInt32(int value) {
         if (value >= 0) {
            this.writeVarint32(value);
         } else {
            this.writeVarint64((long)value);
         }

      }

      void writeSInt32(int value) {
         this.writeVarint32(CodedOutputStream.encodeZigZag32(value));
      }

      void writeSInt64(long value) {
         this.writeVarint64(CodedOutputStream.encodeZigZag64(value));
      }

      void writeBool(boolean value) {
         this.write((byte)(value ? 1 : 0));
      }

      void writeTag(int fieldNumber, int wireType) {
         this.writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
      }

      void writeVarint32(int value) {
         if ((value & -128) == 0) {
            this.writeVarint32OneByte(value);
         } else if ((value & -16384) == 0) {
            this.writeVarint32TwoBytes(value);
         } else if ((value & -2097152) == 0) {
            this.writeVarint32ThreeBytes(value);
         } else if ((value & -268435456) == 0) {
            this.writeVarint32FourBytes(value);
         } else {
            this.writeVarint32FiveBytes(value);
         }

      }

      private void writeVarint32OneByte(int value) {
         this.buffer.put(this.pos--, (byte)value);
      }

      private void writeVarint32TwoBytes(int value) {
         this.pos -= 2;
         this.buffer.putShort(this.pos + 1, (short)((value & 16256) << 1 | value & 127 | 128));
      }

      private void writeVarint32ThreeBytes(int value) {
         this.pos -= 3;
         this.buffer.putInt(this.pos, (value & 2080768) << 10 | (value & 16256 | 16384) << 9 | (value & 127 | 128) << 8);
      }

      private void writeVarint32FourBytes(int value) {
         this.pos -= 4;
         this.buffer.putInt(this.pos + 1, (value & 266338304) << 3 | (value & 2080768 | 2097152) << 2 | (value & 16256 | 16384) << 1 | value & 127 | 128);
      }

      private void writeVarint32FiveBytes(int value) {
         this.buffer.put(this.pos--, (byte)(value >>> 28));
         this.pos -= 4;
         this.buffer.putInt(this.pos + 1, (value >>> 21 & 127 | 128) << 24 | (value >>> 14 & 127 | 128) << 16 | (value >>> 7 & 127 | 128) << 8 | value & 127 | 128);
      }

      void writeVarint64(long var1) {
         // $FF: Couldn't be decompiled
      }

      private void writeVarint64OneByte(long value) {
         this.writeVarint32OneByte((int)value);
      }

      private void writeVarint64TwoBytes(long value) {
         this.writeVarint32TwoBytes((int)value);
      }

      private void writeVarint64ThreeBytes(long value) {
         this.writeVarint32ThreeBytes((int)value);
      }

      private void writeVarint64FourBytes(long value) {
         this.writeVarint32FourBytes((int)value);
      }

      private void writeVarint64FiveBytes(long value) {
         this.pos -= 5;
         this.buffer.putLong(this.pos - 2, (value & 34091302912L) << 28 | (value & 266338304L | 268435456L) << 27 | (value & 2080768L | 2097152L) << 26 | (value & 16256L | 16384L) << 25 | (value & 127L | 128L) << 24);
      }

      private void writeVarint64SixBytes(long value) {
         this.pos -= 6;
         this.buffer.putLong(this.pos - 1, (value & 4363686772736L) << 21 | (value & 34091302912L | 34359738368L) << 20 | (value & 266338304L | 268435456L) << 19 | (value & 2080768L | 2097152L) << 18 | (value & 16256L | 16384L) << 17 | (value & 127L | 128L) << 16);
      }

      private void writeVarint64SevenBytes(long value) {
         this.pos -= 7;
         this.buffer.putLong(this.pos, (value & 558551906910208L) << 14 | (value & 4363686772736L | 4398046511104L) << 13 | (value & 34091302912L | 34359738368L) << 12 | (value & 266338304L | 268435456L) << 11 | (value & 2080768L | 2097152L) << 10 | (value & 16256L | 16384L) << 9 | (value & 127L | 128L) << 8);
      }

      private void writeVarint64EightBytes(long value) {
         this.pos -= 8;
         this.buffer.putLong(this.pos + 1, (value & 71494644084506624L) << 7 | (value & 558551906910208L | 562949953421312L) << 6 | (value & 4363686772736L | 4398046511104L) << 5 | (value & 34091302912L | 34359738368L) << 4 | (value & 266338304L | 268435456L) << 3 | (value & 2080768L | 2097152L) << 2 | (value & 16256L | 16384L) << 1 | value & 127L | 128L);
      }

      private void writeVarint64EightBytesWithSign(long value) {
         this.pos -= 8;
         this.buffer.putLong(this.pos + 1, (value & 71494644084506624L | 72057594037927936L) << 7 | (value & 558551906910208L | 562949953421312L) << 6 | (value & 4363686772736L | 4398046511104L) << 5 | (value & 34091302912L | 34359738368L) << 4 | (value & 266338304L | 268435456L) << 3 | (value & 2080768L | 2097152L) << 2 | (value & 16256L | 16384L) << 1 | value & 127L | 128L);
      }

      private void writeVarint64NineBytes(long value) {
         this.buffer.put(this.pos--, (byte)((int)(value >>> 56)));
         this.writeVarint64EightBytesWithSign(value & 72057594037927935L);
      }

      private void writeVarint64TenBytes(long value) {
         this.buffer.put(this.pos--, (byte)((int)(value >>> 63)));
         this.buffer.put(this.pos--, (byte)((int)(value >>> 56 & 127L | 128L)));
         this.writeVarint64EightBytesWithSign(value & 72057594037927935L);
      }

      void writeFixed32(int value) {
         this.pos -= 4;
         this.buffer.putInt(this.pos + 1, value);
      }

      void writeFixed64(long value) {
         this.pos -= 8;
         this.buffer.putLong(this.pos + 1, value);
      }

      void writeString(String in) {
         this.requireSpace(in.length());
         int i = in.length() - 1;

         char c;
         for(this.pos -= i; i >= 0 && (c = in.charAt(i)) < 128; --i) {
            this.buffer.put(this.pos + i, (byte)c);
         }

         if (i == -1) {
            --this.pos;
         } else {
            for(this.pos += i; i >= 0; --i) {
               c = in.charAt(i);
               if (c < 128 && this.pos >= 0) {
                  this.buffer.put(this.pos--, (byte)c);
               } else if (c < 2048 && this.pos > 0) {
                  this.buffer.put(this.pos--, (byte)(128 | 63 & c));
                  this.buffer.put(this.pos--, (byte)(960 | c >>> 6));
               } else if ((c < '\ud800' || '\udfff' < c) && this.pos > 1) {
                  this.buffer.put(this.pos--, (byte)(128 | 63 & c));
                  this.buffer.put(this.pos--, (byte)(128 | 63 & c >>> 6));
                  this.buffer.put(this.pos--, (byte)(480 | c >>> 12));
               } else if (this.pos > 2) {
                  char high = false;
                  char high;
                  if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
                     throw new Utf8.UnpairedSurrogateException(i - 1, i);
                  }

                  --i;
                  int codePoint = Character.toCodePoint(high, c);
                  this.buffer.put(this.pos--, (byte)(128 | 63 & codePoint));
                  this.buffer.put(this.pos--, (byte)(128 | 63 & codePoint >>> 6));
                  this.buffer.put(this.pos--, (byte)(128 | 63 & codePoint >>> 12));
                  this.buffer.put(this.pos--, (byte)(240 | codePoint >>> 18));
               } else {
                  this.requireSpace(i);
                  ++i;
               }
            }

         }
      }

      public void write(byte value) {
         this.buffer.put(this.pos--, value);
      }

      public void write(byte[] value, int offset, int length) {
         if (this.spaceLeft() < length) {
            this.nextBuffer(length);
         }

         this.pos -= length;
         this.buffer.position(this.pos + 1);
         this.buffer.put(value, offset, length);
      }

      public void writeLazy(byte[] value, int offset, int length) {
         if (this.spaceLeft() < length) {
            this.totalDoneBytes += length;
            this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
            this.nextBuffer();
         } else {
            this.pos -= length;
            this.buffer.position(this.pos + 1);
            this.buffer.put(value, offset, length);
         }
      }

      public void write(ByteBuffer value) {
         int length = value.remaining();
         if (this.spaceLeft() < length) {
            this.nextBuffer(length);
         }

         this.pos -= length;
         this.buffer.position(this.pos + 1);
         this.buffer.put(value);
      }

      public void writeLazy(ByteBuffer value) {
         int length = value.remaining();
         if (this.spaceLeft() < length) {
            this.totalDoneBytes += length;
            this.buffers.addFirst(AllocatedBuffer.wrap(value));
            this.nextBuffer();
         } else {
            this.pos -= length;
            this.buffer.position(this.pos + 1);
            this.buffer.put(value);
         }
      }

      void requireSpace(int size) {
         if (this.spaceLeft() < size) {
            this.nextBuffer(size);
         }

      }
   }

   private static final class UnsafeHeapWriter extends BinaryWriter {
      private AllocatedBuffer allocatedBuffer;
      private byte[] buffer;
      private long offset;
      private long limit;
      private long offsetMinusOne;
      private long limitMinusOne;
      private long pos;

      UnsafeHeapWriter(BufferAllocator alloc, int chunkSize) {
         super(alloc, chunkSize, null);
         this.nextBuffer();
      }

      static boolean isSupported() {
         return UnsafeUtil.hasUnsafeArrayOperations();
      }

      void finishCurrentBuffer() {
         if (this.allocatedBuffer != null) {
            this.totalDoneBytes += this.bytesWrittenToCurrentBuffer();
            this.allocatedBuffer.position(this.arrayPos() - this.allocatedBuffer.arrayOffset() + 1);
            this.allocatedBuffer = null;
            this.pos = 0L;
            this.limitMinusOne = 0L;
         }

      }

      private int arrayPos() {
         return (int)this.pos;
      }

      private void nextBuffer() {
         this.nextBuffer(this.newHeapBuffer());
      }

      private void nextBuffer(int capacity) {
         this.nextBuffer(this.newHeapBuffer(capacity));
      }

      private void nextBuffer(AllocatedBuffer allocatedBuffer) {
         if (!allocatedBuffer.hasArray()) {
            throw new RuntimeException("Allocator returned non-heap buffer");
         } else {
            this.finishCurrentBuffer();
            this.buffers.addFirst(allocatedBuffer);
            this.allocatedBuffer = allocatedBuffer;
            this.buffer = allocatedBuffer.array();
            int arrayOffset = allocatedBuffer.arrayOffset();
            this.limit = (long)(arrayOffset + allocatedBuffer.limit());
            this.offset = (long)(arrayOffset + allocatedBuffer.position());
            this.offsetMinusOne = this.offset - 1L;
            this.limitMinusOne = this.limit - 1L;
            this.pos = this.limitMinusOne;
         }
      }

      public int getTotalBytesWritten() {
         return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
      }

      int bytesWrittenToCurrentBuffer() {
         return (int)(this.limitMinusOne - this.pos);
      }

      int spaceLeft() {
         return (int)(this.pos - this.offsetMinusOne);
      }

      public void writeUInt32(int fieldNumber, int value) {
         this.requireSpace(10);
         this.writeVarint32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeInt32(int fieldNumber, int value) {
         this.requireSpace(15);
         this.writeInt32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeSInt32(int fieldNumber, int value) {
         this.requireSpace(10);
         this.writeSInt32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeFixed32(int fieldNumber, int value) {
         this.requireSpace(9);
         this.writeFixed32(value);
         this.writeTag(fieldNumber, 5);
      }

      public void writeUInt64(int fieldNumber, long value) {
         this.requireSpace(15);
         this.writeVarint64(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeSInt64(int fieldNumber, long value) {
         this.requireSpace(15);
         this.writeSInt64(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeFixed64(int fieldNumber, long value) {
         this.requireSpace(13);
         this.writeFixed64(value);
         this.writeTag(fieldNumber, 1);
      }

      public void writeBool(int fieldNumber, boolean value) {
         this.requireSpace(6);
         this.write((byte)(value ? 1 : 0));
         this.writeTag(fieldNumber, 0);
      }

      public void writeString(int fieldNumber, String value) {
         int prevBytes = this.getTotalBytesWritten();
         this.writeString(value);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeBytes(int fieldNumber, ByteString value) {
         try {
            value.writeToReverse(this);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }

         this.requireSpace(10);
         this.writeVarint32(value.size());
         this.writeTag(fieldNumber, 2);
      }

      public void writeMessage(int fieldNumber, Object value) throws IOException {
         int prevBytes = this.getTotalBytesWritten();
         Protobuf.getInstance().writeTo(value, this);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
         int prevBytes = this.getTotalBytesWritten();
         schema.writeTo(value, this);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeGroup(int fieldNumber, Object value) throws IOException {
         this.writeTag(fieldNumber, 4);
         Protobuf.getInstance().writeTo(value, this);
         this.writeTag(fieldNumber, 3);
      }

      public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
         this.writeTag(fieldNumber, 4);
         schema.writeTo(value, this);
         this.writeTag(fieldNumber, 3);
      }

      public void writeStartGroup(int fieldNumber) {
         this.writeTag(fieldNumber, 3);
      }

      public void writeEndGroup(int fieldNumber) {
         this.writeTag(fieldNumber, 4);
      }

      void writeInt32(int value) {
         if (value >= 0) {
            this.writeVarint32(value);
         } else {
            this.writeVarint64((long)value);
         }

      }

      void writeSInt32(int value) {
         this.writeVarint32(CodedOutputStream.encodeZigZag32(value));
      }

      void writeSInt64(long value) {
         this.writeVarint64(CodedOutputStream.encodeZigZag64(value));
      }

      void writeBool(boolean value) {
         this.write((byte)(value ? 1 : 0));
      }

      void writeTag(int fieldNumber, int wireType) {
         this.writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
      }

      void writeVarint32(int value) {
         if ((value & -128) == 0) {
            this.writeVarint32OneByte(value);
         } else if ((value & -16384) == 0) {
            this.writeVarint32TwoBytes(value);
         } else if ((value & -2097152) == 0) {
            this.writeVarint32ThreeBytes(value);
         } else if ((value & -268435456) == 0) {
            this.writeVarint32FourBytes(value);
         } else {
            this.writeVarint32FiveBytes(value);
         }

      }

      private void writeVarint32OneByte(int value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)value);
      }

      private void writeVarint32TwoBytes(int value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 7));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value & 127 | 128));
      }

      private void writeVarint32ThreeBytes(int value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 14));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 7 & 127 | 128));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value & 127 | 128));
      }

      private void writeVarint32FourBytes(int value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 21));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 14 & 127 | 128));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 7 & 127 | 128));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value & 127 | 128));
      }

      private void writeVarint32FiveBytes(int value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 28));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 21 & 127 | 128));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 14 & 127 | 128));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >>> 7 & 127 | 128));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value & 127 | 128));
      }

      void writeVarint64(long var1) {
         // $FF: Couldn't be decompiled
      }

      private void writeVarint64OneByte(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)value));
      }

      private void writeVarint64TwoBytes(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 7)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)value & 127 | 128));
      }

      private void writeVarint64ThreeBytes(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)value >>> 14));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64FourBytes(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 21)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64FiveBytes(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 28)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64SixBytes(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 35)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64SevenBytes(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 42)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64EightBytes(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 49)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64NineBytes(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 56)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 49 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      private void writeVarint64TenBytes(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 63)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 56 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 49 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value & 127L | 128L)));
      }

      void writeFixed32(int value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >> 24 & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >> 16 & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value >> 8 & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(value & 255));
      }

      void writeFixed64(long value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >> 56) & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >> 48) & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >> 40) & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >> 32) & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >> 24) & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >> 16) & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)(value >> 8) & 255));
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)((int)value & 255));
      }

      void writeString(String in) {
         this.requireSpace(in.length());

         int i;
         char c;
         for(i = in.length() - 1; i >= 0 && (c = in.charAt(i)) < 128; --i) {
            UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)c);
         }

         if (i != -1) {
            for(; i >= 0; --i) {
               c = in.charAt(i);
               if (c < 128 && this.pos > this.offsetMinusOne) {
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)c);
               } else if (c < 2048 && this.pos > this.offset) {
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(128 | 63 & c));
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(960 | c >>> 6));
               } else if ((c < '\ud800' || '\udfff' < c) && this.pos > this.offset + 1L) {
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(128 | 63 & c));
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(128 | 63 & c >>> 6));
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(480 | c >>> 12));
               } else if (this.pos > this.offset + 2L) {
                  char high;
                  if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
                     throw new Utf8.UnpairedSurrogateException(i - 1, i);
                  }

                  --i;
                  int codePoint = Character.toCodePoint(high, c);
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(128 | 63 & codePoint));
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(128 | 63 & codePoint >>> 6));
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(128 | 63 & codePoint >>> 12));
                  UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), (byte)(240 | codePoint >>> 18));
               } else {
                  this.requireSpace(i);
                  ++i;
               }
            }

         }
      }

      public void write(byte value) {
         UnsafeUtil.putByte((byte[])this.buffer, (long)(this.pos--), value);
      }

      public void write(byte[] value, int offset, int length) {
         if (offset >= 0 && offset + length <= value.length) {
            this.requireSpace(length);
            this.pos -= (long)length;
            System.arraycopy(value, offset, this.buffer, this.arrayPos() + 1, length);
         } else {
            throw new ArrayIndexOutOfBoundsException(String.format("value.length=%d, offset=%d, length=%d", value.length, offset, length));
         }
      }

      public void writeLazy(byte[] value, int offset, int length) {
         if (offset >= 0 && offset + length <= value.length) {
            if (this.spaceLeft() < length) {
               this.totalDoneBytes += length;
               this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
               this.nextBuffer();
            } else {
               this.pos -= (long)length;
               System.arraycopy(value, offset, this.buffer, this.arrayPos() + 1, length);
            }
         } else {
            throw new ArrayIndexOutOfBoundsException(String.format("value.length=%d, offset=%d, length=%d", value.length, offset, length));
         }
      }

      public void write(ByteBuffer value) {
         int length = value.remaining();
         this.requireSpace(length);
         this.pos -= (long)length;
         value.get(this.buffer, this.arrayPos() + 1, length);
      }

      public void writeLazy(ByteBuffer value) {
         int length = value.remaining();
         if (this.spaceLeft() < length) {
            this.totalDoneBytes += length;
            this.buffers.addFirst(AllocatedBuffer.wrap(value));
            this.nextBuffer();
         }

         this.pos -= (long)length;
         value.get(this.buffer, this.arrayPos() + 1, length);
      }

      void requireSpace(int size) {
         if (this.spaceLeft() < size) {
            this.nextBuffer(size);
         }

      }
   }

   private static final class SafeHeapWriter extends BinaryWriter {
      private AllocatedBuffer allocatedBuffer;
      private byte[] buffer;
      private int offset;
      private int limit;
      private int offsetMinusOne;
      private int limitMinusOne;
      private int pos;

      SafeHeapWriter(BufferAllocator alloc, int chunkSize) {
         super(alloc, chunkSize, null);
         this.nextBuffer();
      }

      void finishCurrentBuffer() {
         if (this.allocatedBuffer != null) {
            this.totalDoneBytes += this.bytesWrittenToCurrentBuffer();
            this.allocatedBuffer.position(this.pos - this.allocatedBuffer.arrayOffset() + 1);
            this.allocatedBuffer = null;
            this.pos = 0;
            this.limitMinusOne = 0;
         }

      }

      private void nextBuffer() {
         this.nextBuffer(this.newHeapBuffer());
      }

      private void nextBuffer(int capacity) {
         this.nextBuffer(this.newHeapBuffer(capacity));
      }

      private void nextBuffer(AllocatedBuffer allocatedBuffer) {
         if (!allocatedBuffer.hasArray()) {
            throw new RuntimeException("Allocator returned non-heap buffer");
         } else {
            this.finishCurrentBuffer();
            this.buffers.addFirst(allocatedBuffer);
            this.allocatedBuffer = allocatedBuffer;
            this.buffer = allocatedBuffer.array();
            int arrayOffset = allocatedBuffer.arrayOffset();
            this.limit = arrayOffset + allocatedBuffer.limit();
            this.offset = arrayOffset + allocatedBuffer.position();
            this.offsetMinusOne = this.offset - 1;
            this.limitMinusOne = this.limit - 1;
            this.pos = this.limitMinusOne;
         }
      }

      public int getTotalBytesWritten() {
         return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
      }

      int bytesWrittenToCurrentBuffer() {
         return this.limitMinusOne - this.pos;
      }

      int spaceLeft() {
         return this.pos - this.offsetMinusOne;
      }

      public void writeUInt32(int fieldNumber, int value) throws IOException {
         this.requireSpace(10);
         this.writeVarint32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeInt32(int fieldNumber, int value) throws IOException {
         this.requireSpace(15);
         this.writeInt32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeSInt32(int fieldNumber, int value) throws IOException {
         this.requireSpace(10);
         this.writeSInt32(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeFixed32(int fieldNumber, int value) throws IOException {
         this.requireSpace(9);
         this.writeFixed32(value);
         this.writeTag(fieldNumber, 5);
      }

      public void writeUInt64(int fieldNumber, long value) throws IOException {
         this.requireSpace(15);
         this.writeVarint64(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeSInt64(int fieldNumber, long value) throws IOException {
         this.requireSpace(15);
         this.writeSInt64(value);
         this.writeTag(fieldNumber, 0);
      }

      public void writeFixed64(int fieldNumber, long value) throws IOException {
         this.requireSpace(13);
         this.writeFixed64(value);
         this.writeTag(fieldNumber, 1);
      }

      public void writeBool(int fieldNumber, boolean value) throws IOException {
         this.requireSpace(6);
         this.write((byte)(value ? 1 : 0));
         this.writeTag(fieldNumber, 0);
      }

      public void writeString(int fieldNumber, String value) throws IOException {
         int prevBytes = this.getTotalBytesWritten();
         this.writeString(value);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeBytes(int fieldNumber, ByteString value) throws IOException {
         try {
            value.writeToReverse(this);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }

         this.requireSpace(10);
         this.writeVarint32(value.size());
         this.writeTag(fieldNumber, 2);
      }

      public void writeMessage(int fieldNumber, Object value) throws IOException {
         int prevBytes = this.getTotalBytesWritten();
         Protobuf.getInstance().writeTo(value, this);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeMessage(int fieldNumber, Object value, Schema schema) throws IOException {
         int prevBytes = this.getTotalBytesWritten();
         schema.writeTo(value, this);
         int length = this.getTotalBytesWritten() - prevBytes;
         this.requireSpace(10);
         this.writeVarint32(length);
         this.writeTag(fieldNumber, 2);
      }

      public void writeGroup(int fieldNumber, Object value) throws IOException {
         this.writeTag(fieldNumber, 4);
         Protobuf.getInstance().writeTo(value, this);
         this.writeTag(fieldNumber, 3);
      }

      public void writeGroup(int fieldNumber, Object value, Schema schema) throws IOException {
         this.writeTag(fieldNumber, 4);
         schema.writeTo(value, this);
         this.writeTag(fieldNumber, 3);
      }

      public void writeStartGroup(int fieldNumber) {
         this.writeTag(fieldNumber, 3);
      }

      public void writeEndGroup(int fieldNumber) {
         this.writeTag(fieldNumber, 4);
      }

      void writeInt32(int value) {
         if (value >= 0) {
            this.writeVarint32(value);
         } else {
            this.writeVarint64((long)value);
         }

      }

      void writeSInt32(int value) {
         this.writeVarint32(CodedOutputStream.encodeZigZag32(value));
      }

      void writeSInt64(long value) {
         this.writeVarint64(CodedOutputStream.encodeZigZag64(value));
      }

      void writeBool(boolean value) {
         this.write((byte)(value ? 1 : 0));
      }

      void writeTag(int fieldNumber, int wireType) {
         this.writeVarint32(WireFormat.makeTag(fieldNumber, wireType));
      }

      void writeVarint32(int value) {
         if ((value & -128) == 0) {
            this.writeVarint32OneByte(value);
         } else if ((value & -16384) == 0) {
            this.writeVarint32TwoBytes(value);
         } else if ((value & -2097152) == 0) {
            this.writeVarint32ThreeBytes(value);
         } else if ((value & -268435456) == 0) {
            this.writeVarint32FourBytes(value);
         } else {
            this.writeVarint32FiveBytes(value);
         }

      }

      private void writeVarint32OneByte(int value) {
         this.buffer[this.pos--] = (byte)value;
      }

      private void writeVarint32TwoBytes(int value) {
         this.buffer[this.pos--] = (byte)(value >>> 7);
         this.buffer[this.pos--] = (byte)(value & 127 | 128);
      }

      private void writeVarint32ThreeBytes(int value) {
         this.buffer[this.pos--] = (byte)(value >>> 14);
         this.buffer[this.pos--] = (byte)(value >>> 7 & 127 | 128);
         this.buffer[this.pos--] = (byte)(value & 127 | 128);
      }

      private void writeVarint32FourBytes(int value) {
         this.buffer[this.pos--] = (byte)(value >>> 21);
         this.buffer[this.pos--] = (byte)(value >>> 14 & 127 | 128);
         this.buffer[this.pos--] = (byte)(value >>> 7 & 127 | 128);
         this.buffer[this.pos--] = (byte)(value & 127 | 128);
      }

      private void writeVarint32FiveBytes(int value) {
         this.buffer[this.pos--] = (byte)(value >>> 28);
         this.buffer[this.pos--] = (byte)(value >>> 21 & 127 | 128);
         this.buffer[this.pos--] = (byte)(value >>> 14 & 127 | 128);
         this.buffer[this.pos--] = (byte)(value >>> 7 & 127 | 128);
         this.buffer[this.pos--] = (byte)(value & 127 | 128);
      }

      void writeVarint64(long var1) {
         // $FF: Couldn't be decompiled
      }

      private void writeVarint64OneByte(long value) {
         this.buffer[this.pos--] = (byte)((int)value);
      }

      private void writeVarint64TwoBytes(long value) {
         this.buffer[this.pos--] = (byte)((int)(value >>> 7));
         this.buffer[this.pos--] = (byte)((int)value & 127 | 128);
      }

      private void writeVarint64ThreeBytes(long value) {
         this.buffer[this.pos--] = (byte)((int)value >>> 14);
         this.buffer[this.pos--] = (byte)((int)(value >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value & 127L | 128L));
      }

      private void writeVarint64FourBytes(long value) {
         this.buffer[this.pos--] = (byte)((int)(value >>> 21));
         this.buffer[this.pos--] = (byte)((int)(value >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value & 127L | 128L));
      }

      private void writeVarint64FiveBytes(long value) {
         this.buffer[this.pos--] = (byte)((int)(value >>> 28));
         this.buffer[this.pos--] = (byte)((int)(value >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value & 127L | 128L));
      }

      private void writeVarint64SixBytes(long value) {
         this.buffer[this.pos--] = (byte)((int)(value >>> 35));
         this.buffer[this.pos--] = (byte)((int)(value >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value & 127L | 128L));
      }

      private void writeVarint64SevenBytes(long value) {
         this.buffer[this.pos--] = (byte)((int)(value >>> 42));
         this.buffer[this.pos--] = (byte)((int)(value >>> 35 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value & 127L | 128L));
      }

      private void writeVarint64EightBytes(long value) {
         this.buffer[this.pos--] = (byte)((int)(value >>> 49));
         this.buffer[this.pos--] = (byte)((int)(value >>> 42 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 35 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value & 127L | 128L));
      }

      private void writeVarint64NineBytes(long value) {
         this.buffer[this.pos--] = (byte)((int)(value >>> 56));
         this.buffer[this.pos--] = (byte)((int)(value >>> 49 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 42 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 35 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value & 127L | 128L));
      }

      private void writeVarint64TenBytes(long value) {
         this.buffer[this.pos--] = (byte)((int)(value >>> 63));
         this.buffer[this.pos--] = (byte)((int)(value >>> 56 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 49 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 42 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 35 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(value & 127L | 128L));
      }

      void writeFixed32(int value) {
         this.buffer[this.pos--] = (byte)(value >> 24 & 255);
         this.buffer[this.pos--] = (byte)(value >> 16 & 255);
         this.buffer[this.pos--] = (byte)(value >> 8 & 255);
         this.buffer[this.pos--] = (byte)(value & 255);
      }

      void writeFixed64(long value) {
         this.buffer[this.pos--] = (byte)((int)(value >> 56) & 255);
         this.buffer[this.pos--] = (byte)((int)(value >> 48) & 255);
         this.buffer[this.pos--] = (byte)((int)(value >> 40) & 255);
         this.buffer[this.pos--] = (byte)((int)(value >> 32) & 255);
         this.buffer[this.pos--] = (byte)((int)(value >> 24) & 255);
         this.buffer[this.pos--] = (byte)((int)(value >> 16) & 255);
         this.buffer[this.pos--] = (byte)((int)(value >> 8) & 255);
         this.buffer[this.pos--] = (byte)((int)value & 255);
      }

      void writeString(String in) {
         this.requireSpace(in.length());
         int i = in.length() - 1;

         char c;
         for(this.pos -= i; i >= 0 && (c = in.charAt(i)) < 128; --i) {
            this.buffer[this.pos + i] = (byte)c;
         }

         if (i == -1) {
            --this.pos;
         } else {
            for(this.pos += i; i >= 0; --i) {
               c = in.charAt(i);
               if (c < 128 && this.pos > this.offsetMinusOne) {
                  this.buffer[this.pos--] = (byte)c;
               } else if (c < 2048 && this.pos > this.offset) {
                  this.buffer[this.pos--] = (byte)(128 | 63 & c);
                  this.buffer[this.pos--] = (byte)(960 | c >>> 6);
               } else if ((c < '\ud800' || '\udfff' < c) && this.pos > this.offset + 1) {
                  this.buffer[this.pos--] = (byte)(128 | 63 & c);
                  this.buffer[this.pos--] = (byte)(128 | 63 & c >>> 6);
                  this.buffer[this.pos--] = (byte)(480 | c >>> 12);
               } else if (this.pos > this.offset + 2) {
                  char high = false;
                  char high;
                  if (i == 0 || !Character.isSurrogatePair(high = in.charAt(i - 1), c)) {
                     throw new Utf8.UnpairedSurrogateException(i - 1, i);
                  }

                  --i;
                  int codePoint = Character.toCodePoint(high, c);
                  this.buffer[this.pos--] = (byte)(128 | 63 & codePoint);
                  this.buffer[this.pos--] = (byte)(128 | 63 & codePoint >>> 6);
                  this.buffer[this.pos--] = (byte)(128 | 63 & codePoint >>> 12);
                  this.buffer[this.pos--] = (byte)(240 | codePoint >>> 18);
               } else {
                  this.requireSpace(i);
                  ++i;
               }
            }

         }
      }

      public void write(byte value) {
         this.buffer[this.pos--] = value;
      }

      public void write(byte[] value, int offset, int length) {
         if (this.spaceLeft() < length) {
            this.nextBuffer(length);
         }

         this.pos -= length;
         System.arraycopy(value, offset, this.buffer, this.pos + 1, length);
      }

      public void writeLazy(byte[] value, int offset, int length) {
         if (this.spaceLeft() < length) {
            this.totalDoneBytes += length;
            this.buffers.addFirst(AllocatedBuffer.wrap(value, offset, length));
            this.nextBuffer();
         } else {
            this.pos -= length;
            System.arraycopy(value, offset, this.buffer, this.pos + 1, length);
         }
      }

      public void write(ByteBuffer value) {
         int length = value.remaining();
         if (this.spaceLeft() < length) {
            this.nextBuffer(length);
         }

         this.pos -= length;
         value.get(this.buffer, this.pos + 1, length);
      }

      public void writeLazy(ByteBuffer value) {
         int length = value.remaining();
         if (this.spaceLeft() < length) {
            this.totalDoneBytes += length;
            this.buffers.addFirst(AllocatedBuffer.wrap(value));
            this.nextBuffer();
         }

         this.pos -= length;
         value.get(this.buffer, this.pos + 1, length);
      }

      void requireSpace(int size) {
         if (this.spaceLeft() < size) {
            this.nextBuffer(size);
         }

      }
   }
}
