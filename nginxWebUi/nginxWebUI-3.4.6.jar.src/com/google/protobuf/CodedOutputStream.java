/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.BufferOverflowException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class CodedOutputStream
/*      */   extends ByteOutput
/*      */ {
/*   60 */   private static final Logger logger = Logger.getLogger(CodedOutputStream.class.getName());
/*   61 */   private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = UnsafeUtil.hasUnsafeArrayOperations();
/*      */ 
/*      */   
/*      */   CodedOutputStreamWriter wrapper;
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static final int LITTLE_ENDIAN_32_SIZE = 4;
/*      */ 
/*      */   
/*      */   public static final int DEFAULT_BUFFER_SIZE = 4096;
/*      */ 
/*      */   
/*      */   private boolean serializationDeterministic;
/*      */ 
/*      */ 
/*      */   
/*      */   static int computePreferredBufferSize(int dataLength) {
/*   79 */     if (dataLength > 4096) {
/*   80 */       return 4096;
/*      */     }
/*   82 */     return dataLength;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static CodedOutputStream newInstance(OutputStream output) {
/*   93 */     return newInstance(output, 4096);
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
/*      */   public static CodedOutputStream newInstance(OutputStream output, int bufferSize) {
/*  105 */     return new OutputStreamEncoder(output, bufferSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static CodedOutputStream newInstance(byte[] flatArray) {
/*  115 */     return newInstance(flatArray, 0, flatArray.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static CodedOutputStream newInstance(byte[] flatArray, int offset, int length) {
/*  126 */     return new ArrayEncoder(flatArray, offset, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public static CodedOutputStream newInstance(ByteBuffer buffer) {
/*  131 */     if (buffer.hasArray()) {
/*  132 */       return new HeapNioEncoder(buffer);
/*      */     }
/*  134 */     if (buffer.isDirect() && !buffer.isReadOnly()) {
/*  135 */       return UnsafeDirectNioEncoder.isSupported() ? 
/*  136 */         newUnsafeInstance(buffer) : 
/*  137 */         newSafeInstance(buffer);
/*      */     }
/*  139 */     throw new IllegalArgumentException("ByteBuffer is read-only");
/*      */   }
/*      */ 
/*      */   
/*      */   static CodedOutputStream newUnsafeInstance(ByteBuffer buffer) {
/*  144 */     return new UnsafeDirectNioEncoder(buffer);
/*      */   }
/*      */ 
/*      */   
/*      */   static CodedOutputStream newSafeInstance(ByteBuffer buffer) {
/*  149 */     return new SafeDirectNioEncoder(buffer);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void useDeterministicSerialization() {
/*  182 */     this.serializationDeterministic = true;
/*      */   }
/*      */   
/*      */   boolean isSerializationDeterministic() {
/*  186 */     return this.serializationDeterministic;
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
/*      */   
/*      */   @Deprecated
/*      */   public static CodedOutputStream newInstance(ByteBuffer byteBuffer, int unused) {
/*  201 */     return newInstance(byteBuffer);
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
/*      */ 
/*      */   
/*      */   static CodedOutputStream newInstance(ByteOutput byteOutput, int bufferSize) {
/*  216 */     if (bufferSize < 0) {
/*  217 */       throw new IllegalArgumentException("bufferSize must be positive");
/*      */     }
/*      */     
/*  220 */     return new ByteOutputEncoder(byteOutput, bufferSize);
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
/*      */   private CodedOutputStream() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSInt32(int fieldNumber, int value) throws IOException {
/*  242 */     writeUInt32(fieldNumber, encodeZigZag32(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSFixed32(int fieldNumber, int value) throws IOException {
/*  251 */     writeFixed32(fieldNumber, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeInt64(int fieldNumber, long value) throws IOException {
/*  256 */     writeUInt64(fieldNumber, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSInt64(int fieldNumber, long value) throws IOException {
/*  265 */     writeUInt64(fieldNumber, encodeZigZag64(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSFixed64(int fieldNumber, long value) throws IOException {
/*  274 */     writeFixed64(fieldNumber, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeFloat(int fieldNumber, float value) throws IOException {
/*  279 */     writeFixed32(fieldNumber, Float.floatToRawIntBits(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeDouble(int fieldNumber, double value) throws IOException {
/*  284 */     writeFixed64(fieldNumber, Double.doubleToRawLongBits(value));
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
/*      */   public final void writeEnum(int fieldNumber, int value) throws IOException {
/*  296 */     writeInt32(fieldNumber, value);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeRawByte(byte value) throws IOException {
/*  329 */     write(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeRawByte(int value) throws IOException {
/*  334 */     write((byte)value);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeRawBytes(byte[] value) throws IOException {
/*  339 */     write(value, 0, value.length);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeRawBytes(byte[] value, int offset, int length) throws IOException {
/*  344 */     write(value, offset, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeRawBytes(ByteString value) throws IOException {
/*  349 */     value.writeTo(this);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSInt32NoTag(int value) throws IOException {
/*  400 */     writeUInt32NoTag(encodeZigZag32(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSFixed32NoTag(int value) throws IOException {
/*  409 */     writeFixed32NoTag(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeInt64NoTag(long value) throws IOException {
/*  414 */     writeUInt64NoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSInt64NoTag(long value) throws IOException {
/*  423 */     writeUInt64NoTag(encodeZigZag64(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeSFixed64NoTag(long value) throws IOException {
/*  432 */     writeFixed64NoTag(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeFloatNoTag(float value) throws IOException {
/*  437 */     writeFixed32NoTag(Float.floatToRawIntBits(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeDoubleNoTag(double value) throws IOException {
/*  442 */     writeFixed64NoTag(Double.doubleToRawLongBits(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public final void writeBoolNoTag(boolean value) throws IOException {
/*  447 */     write((byte)(value ? 1 : 0));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void writeEnumNoTag(int value) throws IOException {
/*  455 */     writeInt32NoTag(value);
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
/*      */   
/*      */   public final void writeByteArrayNoTag(byte[] value) throws IOException {
/*  469 */     writeByteArrayNoTag(value, 0, value.length);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeInt32Size(int fieldNumber, int value) {
/*  509 */     return computeTagSize(fieldNumber) + computeInt32SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeUInt32Size(int fieldNumber, int value) {
/*  517 */     return computeTagSize(fieldNumber) + computeUInt32SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeSInt32Size(int fieldNumber, int value) {
/*  525 */     return computeTagSize(fieldNumber) + computeSInt32SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeFixed32Size(int fieldNumber, int value) {
/*  533 */     return computeTagSize(fieldNumber) + computeFixed32SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeSFixed32Size(int fieldNumber, int value) {
/*  541 */     return computeTagSize(fieldNumber) + computeSFixed32SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeInt64Size(int fieldNumber, long value) {
/*  549 */     return computeTagSize(fieldNumber) + computeInt64SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeUInt64Size(int fieldNumber, long value) {
/*  557 */     return computeTagSize(fieldNumber) + computeUInt64SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeSInt64Size(int fieldNumber, long value) {
/*  565 */     return computeTagSize(fieldNumber) + computeSInt64SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeFixed64Size(int fieldNumber, long value) {
/*  573 */     return computeTagSize(fieldNumber) + computeFixed64SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeSFixed64Size(int fieldNumber, long value) {
/*  581 */     return computeTagSize(fieldNumber) + computeSFixed64SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeFloatSize(int fieldNumber, float value) {
/*  589 */     return computeTagSize(fieldNumber) + computeFloatSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeDoubleSize(int fieldNumber, double value) {
/*  597 */     return computeTagSize(fieldNumber) + computeDoubleSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeBoolSize(int fieldNumber, boolean value) {
/*  604 */     return computeTagSize(fieldNumber) + computeBoolSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeEnumSize(int fieldNumber, int value) {
/*  613 */     return computeTagSize(fieldNumber) + computeEnumSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeStringSize(int fieldNumber, String value) {
/*  621 */     return computeTagSize(fieldNumber) + computeStringSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeBytesSize(int fieldNumber, ByteString value) {
/*  629 */     return computeTagSize(fieldNumber) + computeBytesSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeByteArraySize(int fieldNumber, byte[] value) {
/*  637 */     return computeTagSize(fieldNumber) + computeByteArraySizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeByteBufferSize(int fieldNumber, ByteBuffer value) {
/*  645 */     return computeTagSize(fieldNumber) + computeByteBufferSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeLazyFieldSize(int fieldNumber, LazyFieldLite value) {
/*  653 */     return computeTagSize(fieldNumber) + computeLazyFieldSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeMessageSize(int fieldNumber, MessageLite value) {
/*  661 */     return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int computeMessageSize(int fieldNumber, MessageLite value, Schema schema) {
/*  670 */     return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value, schema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeMessageSetExtensionSize(int fieldNumber, MessageLite value) {
/*  678 */     return computeTagSize(1) * 2 + 
/*  679 */       computeUInt32Size(2, fieldNumber) + 
/*  680 */       computeMessageSize(3, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeRawMessageSetExtensionSize(int fieldNumber, ByteString value) {
/*  689 */     return computeTagSize(1) * 2 + 
/*  690 */       computeUInt32Size(2, fieldNumber) + 
/*  691 */       computeBytesSize(3, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeLazyFieldMessageSetExtensionSize(int fieldNumber, LazyFieldLite value) {
/*  701 */     return computeTagSize(1) * 2 + 
/*  702 */       computeUInt32Size(2, fieldNumber) + 
/*  703 */       computeLazyFieldSize(3, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeTagSize(int fieldNumber) {
/*  710 */     return computeUInt32SizeNoTag(WireFormat.makeTag(fieldNumber, 0));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeInt32SizeNoTag(int value) {
/*  718 */     if (value >= 0) {
/*  719 */       return computeUInt32SizeNoTag(value);
/*      */     }
/*      */     
/*  722 */     return 10;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeUInt32SizeNoTag(int value) {
/*  728 */     if ((value & 0xFFFFFF80) == 0) {
/*  729 */       return 1;
/*      */     }
/*  731 */     if ((value & 0xFFFFC000) == 0) {
/*  732 */       return 2;
/*      */     }
/*  734 */     if ((value & 0xFFE00000) == 0) {
/*  735 */       return 3;
/*      */     }
/*  737 */     if ((value & 0xF0000000) == 0) {
/*  738 */       return 4;
/*      */     }
/*  740 */     return 5;
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeSInt32SizeNoTag(int value) {
/*  745 */     return computeUInt32SizeNoTag(encodeZigZag32(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeFixed32SizeNoTag(int unused) {
/*  750 */     return 4;
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeSFixed32SizeNoTag(int unused) {
/*  755 */     return 4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeInt64SizeNoTag(long value) {
/*  763 */     return computeUInt64SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeUInt64SizeNoTag(long value) {
/*  772 */     if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
/*  773 */       return 1;
/*      */     }
/*  775 */     if (value < 0L) {
/*  776 */       return 10;
/*      */     }
/*      */     
/*  779 */     int n = 2;
/*  780 */     if ((value & 0xFFFFFFF800000000L) != 0L) {
/*  781 */       n += 4;
/*  782 */       value >>>= 28L;
/*      */     } 
/*  784 */     if ((value & 0xFFFFFFFFFFE00000L) != 0L) {
/*  785 */       n += 2;
/*  786 */       value >>>= 14L;
/*      */     } 
/*  788 */     if ((value & 0xFFFFFFFFFFFFC000L) != 0L) {
/*  789 */       n++;
/*      */     }
/*  791 */     return n;
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeSInt64SizeNoTag(long value) {
/*  796 */     return computeUInt64SizeNoTag(encodeZigZag64(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeFixed64SizeNoTag(long unused) {
/*  801 */     return 8;
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeSFixed64SizeNoTag(long unused) {
/*  806 */     return 8;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeFloatSizeNoTag(float unused) {
/*  814 */     return 4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeDoubleSizeNoTag(double unused) {
/*  822 */     return 8;
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeBoolSizeNoTag(boolean unused) {
/*  827 */     return 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeEnumSizeNoTag(int value) {
/*  835 */     return computeInt32SizeNoTag(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeStringSizeNoTag(String value) {
/*      */     int length;
/*      */     try {
/*  842 */       length = Utf8.encodedLength(value);
/*  843 */     } catch (UnpairedSurrogateException e) {
/*      */       
/*  845 */       byte[] bytes = value.getBytes(Internal.UTF_8);
/*  846 */       length = bytes.length;
/*      */     } 
/*      */     
/*  849 */     return computeLengthDelimitedFieldSize(length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int computeLazyFieldSizeNoTag(LazyFieldLite value) {
/*  857 */     return computeLengthDelimitedFieldSize(value.getSerializedSize());
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeBytesSizeNoTag(ByteString value) {
/*  862 */     return computeLengthDelimitedFieldSize(value.size());
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeByteArraySizeNoTag(byte[] value) {
/*  867 */     return computeLengthDelimitedFieldSize(value.length);
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeByteBufferSizeNoTag(ByteBuffer value) {
/*  872 */     return computeLengthDelimitedFieldSize(value.capacity());
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeMessageSizeNoTag(MessageLite value) {
/*  877 */     return computeLengthDelimitedFieldSize(value.getSerializedSize());
/*      */   }
/*      */ 
/*      */   
/*      */   static int computeMessageSizeNoTag(MessageLite value, Schema schema) {
/*  882 */     return computeLengthDelimitedFieldSize(((AbstractMessageLite)value).getSerializedSize(schema));
/*      */   }
/*      */   
/*      */   static int computeLengthDelimitedFieldSize(int fieldLength) {
/*  886 */     return computeUInt32SizeNoTag(fieldLength) + fieldLength;
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
/*      */   
/*      */   public static int encodeZigZag32(int n) {
/*  900 */     return n << 1 ^ n >> 31;
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
/*      */   
/*      */   public static long encodeZigZag64(long n) {
/*  914 */     return n << 1L ^ n >> 63L;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void checkNoSpaceLeft() {
/*  938 */     if (spaceLeft() != 0) {
/*  939 */       throw new IllegalStateException("Did not write as much data as expected.");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class OutOfSpaceException
/*      */     extends IOException
/*      */   {
/*      */     private static final long serialVersionUID = -6947486886997889499L;
/*      */     
/*      */     private static final String MESSAGE = "CodedOutputStream was writing to a flat byte array and ran out of space.";
/*      */ 
/*      */     
/*      */     OutOfSpaceException() {
/*  954 */       super("CodedOutputStream was writing to a flat byte array and ran out of space.");
/*      */     }
/*      */     
/*      */     OutOfSpaceException(String explanationMessage) {
/*  958 */       super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + explanationMessage);
/*      */     }
/*      */     
/*      */     OutOfSpaceException(Throwable cause) {
/*  962 */       super("CodedOutputStream was writing to a flat byte array and ran out of space.", cause);
/*      */     }
/*      */     
/*      */     OutOfSpaceException(String explanationMessage, Throwable cause) {
/*  966 */       super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + explanationMessage, cause);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void inefficientWriteStringNoTag(String value, Utf8.UnpairedSurrogateException cause) throws IOException {
/*  984 */     logger.log(Level.WARNING, "Converting ill-formed UTF-16. Your Protocol Buffer will not round trip correctly!", cause);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  993 */     byte[] bytes = value.getBytes(Internal.UTF_8);
/*      */     try {
/*  995 */       writeUInt32NoTag(bytes.length);
/*  996 */       writeLazy(bytes, 0, bytes.length);
/*  997 */     } catch (IndexOutOfBoundsException e) {
/*  998 */       throw new OutOfSpaceException(e);
/*  999 */     } catch (OutOfSpaceException e) {
/* 1000 */       throw e;
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
/*      */   @Deprecated
/*      */   public final void writeGroup(int fieldNumber, MessageLite value) throws IOException {
/* 1013 */     writeTag(fieldNumber, 3);
/* 1014 */     writeGroupNoTag(value);
/* 1015 */     writeTag(fieldNumber, 4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   final void writeGroup(int fieldNumber, MessageLite value, Schema schema) throws IOException {
/* 1026 */     writeTag(fieldNumber, 3);
/* 1027 */     writeGroupNoTag(value, schema);
/* 1028 */     writeTag(fieldNumber, 4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public final void writeGroupNoTag(MessageLite value) throws IOException {
/* 1038 */     value.writeTo(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   final void writeGroupNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
/* 1048 */     schema.writeTo(value, this.wrapper);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static int computeGroupSize(int fieldNumber, MessageLite value) {
/* 1059 */     return computeTagSize(fieldNumber) * 2 + computeGroupSizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   static int computeGroupSize(int fieldNumber, MessageLite value, Schema schema) {
/* 1070 */     return computeTagSize(fieldNumber) * 2 + computeGroupSizeNoTag(value, schema);
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static int computeGroupSizeNoTag(MessageLite value) {
/* 1076 */     return value.getSerializedSize();
/*      */   }
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   static int computeGroupSizeNoTag(MessageLite value, Schema schema) {
/* 1082 */     return ((AbstractMessageLite)value).getSerializedSize(schema);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public final void writeRawVarint32(int value) throws IOException {
/* 1093 */     writeUInt32NoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public final void writeRawVarint64(long value) throws IOException {
/* 1103 */     writeUInt64NoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static int computeRawVarint32Size(int value) {
/* 1114 */     return computeUInt32SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static int computeRawVarint64Size(long value) {
/* 1124 */     return computeUInt64SizeNoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public final void writeRawLittleEndian32(int value) throws IOException {
/* 1134 */     writeFixed32NoTag(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public final void writeRawLittleEndian64(long value) throws IOException {
/* 1144 */     writeFixed64NoTag(value);
/*      */   } public abstract void writeTag(int paramInt1, int paramInt2) throws IOException; public abstract void writeInt32(int paramInt1, int paramInt2) throws IOException; public abstract void writeUInt32(int paramInt1, int paramInt2) throws IOException; public abstract void writeFixed32(int paramInt1, int paramInt2) throws IOException; public abstract void writeUInt64(int paramInt, long paramLong) throws IOException; public abstract void writeFixed64(int paramInt, long paramLong) throws IOException; public abstract void writeBool(int paramInt, boolean paramBoolean) throws IOException; public abstract void writeString(int paramInt, String paramString) throws IOException; public abstract void writeBytes(int paramInt, ByteString paramByteString) throws IOException; public abstract void writeByteArray(int paramInt, byte[] paramArrayOfbyte) throws IOException; public abstract void writeByteArray(int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) throws IOException; public abstract void writeByteBuffer(int paramInt, ByteBuffer paramByteBuffer) throws IOException; public abstract void writeRawBytes(ByteBuffer paramByteBuffer) throws IOException; public abstract void writeMessage(int paramInt, MessageLite paramMessageLite) throws IOException; abstract void writeMessage(int paramInt, MessageLite paramMessageLite, Schema paramSchema) throws IOException; public abstract void writeMessageSetExtension(int paramInt, MessageLite paramMessageLite) throws IOException; public abstract void writeRawMessageSetExtension(int paramInt, ByteString paramByteString) throws IOException; public abstract void writeInt32NoTag(int paramInt) throws IOException; public abstract void writeUInt32NoTag(int paramInt) throws IOException; public abstract void writeFixed32NoTag(int paramInt) throws IOException; public abstract void writeUInt64NoTag(long paramLong) throws IOException; public abstract void writeFixed64NoTag(long paramLong) throws IOException; public abstract void writeStringNoTag(String paramString) throws IOException; public abstract void writeBytesNoTag(ByteString paramByteString) throws IOException; public abstract void writeMessageNoTag(MessageLite paramMessageLite) throws IOException; abstract void writeMessageNoTag(MessageLite paramMessageLite, Schema paramSchema) throws IOException;
/*      */   public abstract void write(byte paramByte) throws IOException;
/*      */   public abstract void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
/*      */   public abstract void writeLazy(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
/*      */   public abstract void write(ByteBuffer paramByteBuffer) throws IOException;
/*      */   public abstract void writeLazy(ByteBuffer paramByteBuffer) throws IOException;
/*      */   public abstract void flush() throws IOException;
/*      */   public abstract int spaceLeft();
/*      */   public abstract int getTotalBytesWritten();
/*      */   abstract void writeByteArrayNoTag(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
/*      */   private static class ArrayEncoder extends CodedOutputStream { private final byte[] buffer; private final int offset;
/*      */     ArrayEncoder(byte[] buffer, int offset, int length) {
/* 1157 */       if (buffer == null) {
/* 1158 */         throw new NullPointerException("buffer");
/*      */       }
/* 1160 */       if ((offset | length | buffer.length - offset + length) < 0)
/* 1161 */         throw new IllegalArgumentException(
/* 1162 */             String.format("Array range is invalid. Buffer.length=%d, offset=%d, length=%d", new Object[] {
/*      */                 
/* 1164 */                 Integer.valueOf(buffer.length), Integer.valueOf(offset), Integer.valueOf(length)
/*      */               })); 
/* 1166 */       this.buffer = buffer;
/* 1167 */       this.offset = offset;
/* 1168 */       this.position = offset;
/* 1169 */       this.limit = offset + length;
/*      */     }
/*      */     private final int limit; private int position;
/*      */     
/*      */     public final void writeTag(int fieldNumber, int wireType) throws IOException {
/* 1174 */       writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeInt32(int fieldNumber, int value) throws IOException {
/* 1179 */       writeTag(fieldNumber, 0);
/* 1180 */       writeInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeUInt32(int fieldNumber, int value) throws IOException {
/* 1185 */       writeTag(fieldNumber, 0);
/* 1186 */       writeUInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeFixed32(int fieldNumber, int value) throws IOException {
/* 1191 */       writeTag(fieldNumber, 5);
/* 1192 */       writeFixed32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeUInt64(int fieldNumber, long value) throws IOException {
/* 1197 */       writeTag(fieldNumber, 0);
/* 1198 */       writeUInt64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeFixed64(int fieldNumber, long value) throws IOException {
/* 1203 */       writeTag(fieldNumber, 1);
/* 1204 */       writeFixed64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeBool(int fieldNumber, boolean value) throws IOException {
/* 1209 */       writeTag(fieldNumber, 0);
/* 1210 */       write((byte)(value ? 1 : 0));
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeString(int fieldNumber, String value) throws IOException {
/* 1215 */       writeTag(fieldNumber, 2);
/* 1216 */       writeStringNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeBytes(int fieldNumber, ByteString value) throws IOException {
/* 1221 */       writeTag(fieldNumber, 2);
/* 1222 */       writeBytesNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeByteArray(int fieldNumber, byte[] value) throws IOException {
/* 1227 */       writeByteArray(fieldNumber, value, 0, value.length);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
/* 1234 */       writeTag(fieldNumber, 2);
/* 1235 */       writeByteArrayNoTag(value, offset, length);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
/* 1241 */       writeTag(fieldNumber, 2);
/* 1242 */       writeUInt32NoTag(value.capacity());
/* 1243 */       writeRawBytes(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeBytesNoTag(ByteString value) throws IOException {
/* 1248 */       writeUInt32NoTag(value.size());
/* 1249 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
/* 1255 */       writeUInt32NoTag(length);
/* 1256 */       write(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeRawBytes(ByteBuffer value) throws IOException {
/* 1261 */       if (value.hasArray()) {
/* 1262 */         write(value.array(), value.arrayOffset(), value.capacity());
/*      */       } else {
/* 1264 */         ByteBuffer duplicated = value.duplicate();
/* 1265 */         duplicated.clear();
/* 1266 */         write(duplicated);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final void writeMessage(int fieldNumber, MessageLite value) throws IOException {
/* 1273 */       writeTag(fieldNumber, 2);
/* 1274 */       writeMessageNoTag(value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     final void writeMessage(int fieldNumber, MessageLite value, Schema<MessageLite> schema) throws IOException {
/* 1280 */       writeTag(fieldNumber, 2);
/* 1281 */       writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
/* 1282 */       schema.writeTo(value, this.wrapper);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
/* 1288 */       writeTag(1, 3);
/* 1289 */       writeUInt32(2, fieldNumber);
/* 1290 */       writeMessage(3, value);
/* 1291 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public final void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
/* 1297 */       writeTag(1, 3);
/* 1298 */       writeUInt32(2, fieldNumber);
/* 1299 */       writeBytes(3, value);
/* 1300 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeMessageNoTag(MessageLite value) throws IOException {
/* 1305 */       writeUInt32NoTag(value.getSerializedSize());
/* 1306 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */     
/*      */     final void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
/* 1311 */       writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
/* 1312 */       schema.writeTo(value, this.wrapper);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void write(byte value) throws IOException {
/*      */       try {
/* 1318 */         this.buffer[this.position++] = value;
/* 1319 */       } catch (IndexOutOfBoundsException e) {
/* 1320 */         throw new CodedOutputStream.OutOfSpaceException(
/* 1321 */             String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeInt32NoTag(int value) throws IOException {
/* 1327 */       if (value >= 0) {
/* 1328 */         writeUInt32NoTag(value);
/*      */       } else {
/*      */         
/* 1331 */         writeUInt64NoTag(value);
/*      */       } 
/*      */     }
/*      */     
/*      */     public final void writeUInt32NoTag(int value) throws IOException
/*      */     {
/* 1337 */       if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS)
/* 1338 */       { if (!Android.isOnAndroidDevice() && 
/* 1339 */           spaceLeft() >= 5) {
/* 1340 */           if ((value & 0xFFFFFF80) == 0) {
/* 1341 */             UnsafeUtil.putByte(this.buffer, this.position++, (byte)value);
/*      */             return;
/*      */           } 
/* 1344 */           UnsafeUtil.putByte(this.buffer, this.position++, (byte)(value | 0x80));
/* 1345 */           value >>>= 7;
/* 1346 */           if ((value & 0xFFFFFF80) == 0) {
/* 1347 */             UnsafeUtil.putByte(this.buffer, this.position++, (byte)value);
/*      */             return;
/*      */           } 
/* 1350 */           UnsafeUtil.putByte(this.buffer, this.position++, (byte)(value | 0x80));
/* 1351 */           value >>>= 7;
/* 1352 */           if ((value & 0xFFFFFF80) == 0) {
/* 1353 */             UnsafeUtil.putByte(this.buffer, this.position++, (byte)value);
/*      */             return;
/*      */           } 
/* 1356 */           UnsafeUtil.putByte(this.buffer, this.position++, (byte)(value | 0x80));
/* 1357 */           value >>>= 7;
/* 1358 */           if ((value & 0xFFFFFF80) == 0) {
/* 1359 */             UnsafeUtil.putByte(this.buffer, this.position++, (byte)value);
/*      */             return;
/*      */           } 
/* 1362 */           UnsafeUtil.putByte(this.buffer, this.position++, (byte)(value | 0x80));
/* 1363 */           value >>>= 7;
/* 1364 */           UnsafeUtil.putByte(this.buffer, this.position++, (byte)value); return;
/*      */         }  }
/*      */       else { try {
/*      */           while (true) {
/* 1368 */             if ((value & 0xFFFFFF80) == 0) {
/* 1369 */               this.buffer[this.position++] = (byte)value;
/*      */               return;
/*      */             } 
/* 1372 */             this.buffer[this.position++] = (byte)(value & 0x7F | 0x80);
/* 1373 */             value >>>= 7;
/*      */           }
/*      */         
/* 1376 */         } catch (IndexOutOfBoundsException e) {
/* 1377 */           throw new CodedOutputStream.OutOfSpaceException(
/* 1378 */               String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
/*      */         }  }
/*      */        while (true) {
/*      */         if ((value & 0xFFFFFF80) == 0) {
/*      */           this.buffer[this.position++] = (byte)value; return;
/*      */         }  this.buffer[this.position++] = (byte)(value & 0x7F | 0x80);
/*      */         value >>>= 7;
/*      */       }  } public final void writeFixed32NoTag(int value) throws IOException { try {
/* 1386 */         this.buffer[this.position++] = (byte)(value & 0xFF);
/* 1387 */         this.buffer[this.position++] = (byte)(value >> 8 & 0xFF);
/* 1388 */         this.buffer[this.position++] = (byte)(value >> 16 & 0xFF);
/* 1389 */         this.buffer[this.position++] = (byte)(value >> 24 & 0xFF);
/* 1390 */       } catch (IndexOutOfBoundsException e) {
/* 1391 */         throw new CodedOutputStream.OutOfSpaceException(
/* 1392 */             String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
/*      */       }  }
/*      */ 
/*      */     
/*      */     public final void writeUInt64NoTag(long value) throws IOException
/*      */     {
/* 1398 */       if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) { if (spaceLeft() >= 10) {
/*      */           while (true) {
/* 1400 */             if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
/* 1401 */               UnsafeUtil.putByte(this.buffer, this.position++, (byte)(int)value);
/*      */               return;
/*      */             } 
/* 1404 */             UnsafeUtil.putByte(this.buffer, this.position++, (byte)((int)value & 0x7F | 0x80));
/* 1405 */             value >>>= 7L;
/*      */           } 
/*      */         } }
/*      */       else
/*      */       { try {
/*      */           while (true) {
/* 1411 */             if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
/* 1412 */               this.buffer[this.position++] = (byte)(int)value;
/*      */               return;
/*      */             } 
/* 1415 */             this.buffer[this.position++] = (byte)((int)value & 0x7F | 0x80);
/* 1416 */             value >>>= 7L;
/*      */           }
/*      */         
/* 1419 */         } catch (IndexOutOfBoundsException e) {
/* 1420 */           throw new CodedOutputStream.OutOfSpaceException(
/* 1421 */               String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
/*      */         }  }
/*      */        while (true) {
/*      */         if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
/*      */           this.buffer[this.position++] = (byte)(int)value; return;
/*      */         }  this.buffer[this.position++] = (byte)((int)value & 0x7F | 0x80);
/*      */         value >>>= 7L;
/*      */       }  } public final void writeFixed64NoTag(long value) throws IOException { try {
/* 1429 */         this.buffer[this.position++] = (byte)((int)value & 0xFF);
/* 1430 */         this.buffer[this.position++] = (byte)((int)(value >> 8L) & 0xFF);
/* 1431 */         this.buffer[this.position++] = (byte)((int)(value >> 16L) & 0xFF);
/* 1432 */         this.buffer[this.position++] = (byte)((int)(value >> 24L) & 0xFF);
/* 1433 */         this.buffer[this.position++] = (byte)((int)(value >> 32L) & 0xFF);
/* 1434 */         this.buffer[this.position++] = (byte)((int)(value >> 40L) & 0xFF);
/* 1435 */         this.buffer[this.position++] = (byte)((int)(value >> 48L) & 0xFF);
/* 1436 */         this.buffer[this.position++] = (byte)((int)(value >> 56L) & 0xFF);
/* 1437 */       } catch (IndexOutOfBoundsException e) {
/* 1438 */         throw new CodedOutputStream.OutOfSpaceException(
/* 1439 */             String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(1) }), e);
/*      */       }  }
/*      */ 
/*      */ 
/*      */     
/*      */     public final void write(byte[] value, int offset, int length) throws IOException {
/*      */       try {
/* 1446 */         System.arraycopy(value, offset, this.buffer, this.position, length);
/* 1447 */         this.position += length;
/* 1448 */       } catch (IndexOutOfBoundsException e) {
/* 1449 */         throw new CodedOutputStream.OutOfSpaceException(
/* 1450 */             String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(length) }), e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeLazy(byte[] value, int offset, int length) throws IOException {
/* 1456 */       write(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void write(ByteBuffer value) throws IOException {
/* 1461 */       int length = value.remaining();
/*      */       try {
/* 1463 */         value.get(this.buffer, this.position, length);
/* 1464 */         this.position += length;
/* 1465 */       } catch (IndexOutOfBoundsException e) {
/* 1466 */         throw new CodedOutputStream.OutOfSpaceException(
/* 1467 */             String.format("Pos: %d, limit: %d, len: %d", new Object[] { Integer.valueOf(this.position), Integer.valueOf(this.limit), Integer.valueOf(length) }), e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeLazy(ByteBuffer value) throws IOException {
/* 1473 */       write(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public final void writeStringNoTag(String value) throws IOException {
/* 1478 */       int oldPosition = this.position;
/*      */ 
/*      */       
/*      */       try {
/* 1482 */         int maxLength = value.length() * 3;
/* 1483 */         int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
/* 1484 */         int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
/* 1485 */         if (minLengthVarIntSize == maxLengthVarIntSize) {
/* 1486 */           this.position = oldPosition + minLengthVarIntSize;
/* 1487 */           int newPosition = Utf8.encode(value, this.buffer, this.position, spaceLeft());
/*      */ 
/*      */           
/* 1490 */           this.position = oldPosition;
/* 1491 */           int length = newPosition - oldPosition - minLengthVarIntSize;
/* 1492 */           writeUInt32NoTag(length);
/* 1493 */           this.position = newPosition;
/*      */         } else {
/* 1495 */           int length = Utf8.encodedLength(value);
/* 1496 */           writeUInt32NoTag(length);
/* 1497 */           this.position = Utf8.encode(value, this.buffer, this.position, spaceLeft());
/*      */         } 
/* 1499 */       } catch (UnpairedSurrogateException e) {
/*      */         
/* 1501 */         this.position = oldPosition;
/*      */ 
/*      */         
/* 1504 */         inefficientWriteStringNoTag(value, e);
/* 1505 */       } catch (IndexOutOfBoundsException e) {
/* 1506 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void flush() {}
/*      */ 
/*      */ 
/*      */     
/*      */     public final int spaceLeft() {
/* 1517 */       return this.limit - this.position;
/*      */     }
/*      */ 
/*      */     
/*      */     public final int getTotalBytesWritten() {
/* 1522 */       return this.position - this.offset;
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class HeapNioEncoder
/*      */     extends ArrayEncoder
/*      */   {
/*      */     private final ByteBuffer byteBuffer;
/*      */     
/*      */     private int initialPosition;
/*      */     
/*      */     HeapNioEncoder(ByteBuffer byteBuffer) {
/* 1535 */       super(byteBuffer
/* 1536 */           .array(), byteBuffer
/* 1537 */           .arrayOffset() + byteBuffer.position(), byteBuffer
/* 1538 */           .remaining());
/* 1539 */       this.byteBuffer = byteBuffer;
/* 1540 */       this.initialPosition = byteBuffer.position();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void flush() {
/* 1546 */       this.byteBuffer.position(this.initialPosition + getTotalBytesWritten());
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class SafeDirectNioEncoder
/*      */     extends CodedOutputStream
/*      */   {
/*      */     private final ByteBuffer originalBuffer;
/*      */     
/*      */     private final ByteBuffer buffer;
/*      */     private final int initialPosition;
/*      */     
/*      */     SafeDirectNioEncoder(ByteBuffer buffer) {
/* 1560 */       this.originalBuffer = buffer;
/* 1561 */       this.buffer = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
/* 1562 */       this.initialPosition = buffer.position();
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeTag(int fieldNumber, int wireType) throws IOException {
/* 1567 */       writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32(int fieldNumber, int value) throws IOException {
/* 1572 */       writeTag(fieldNumber, 0);
/* 1573 */       writeInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32(int fieldNumber, int value) throws IOException {
/* 1578 */       writeTag(fieldNumber, 0);
/* 1579 */       writeUInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32(int fieldNumber, int value) throws IOException {
/* 1584 */       writeTag(fieldNumber, 5);
/* 1585 */       writeFixed32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64(int fieldNumber, long value) throws IOException {
/* 1590 */       writeTag(fieldNumber, 0);
/* 1591 */       writeUInt64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64(int fieldNumber, long value) throws IOException {
/* 1596 */       writeTag(fieldNumber, 1);
/* 1597 */       writeFixed64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBool(int fieldNumber, boolean value) throws IOException {
/* 1602 */       writeTag(fieldNumber, 0);
/* 1603 */       write((byte)(value ? 1 : 0));
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeString(int fieldNumber, String value) throws IOException {
/* 1608 */       writeTag(fieldNumber, 2);
/* 1609 */       writeStringNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytes(int fieldNumber, ByteString value) throws IOException {
/* 1614 */       writeTag(fieldNumber, 2);
/* 1615 */       writeBytesNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
/* 1620 */       writeByteArray(fieldNumber, value, 0, value.length);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
/* 1627 */       writeTag(fieldNumber, 2);
/* 1628 */       writeByteArrayNoTag(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
/* 1633 */       writeTag(fieldNumber, 2);
/* 1634 */       writeUInt32NoTag(value.capacity());
/* 1635 */       writeRawBytes(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
/* 1640 */       writeTag(fieldNumber, 2);
/* 1641 */       writeMessageNoTag(value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
/* 1647 */       writeTag(fieldNumber, 2);
/* 1648 */       writeMessageNoTag(value, schema);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
/* 1654 */       writeTag(1, 3);
/* 1655 */       writeUInt32(2, fieldNumber);
/* 1656 */       writeMessage(3, value);
/* 1657 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
/* 1663 */       writeTag(1, 3);
/* 1664 */       writeUInt32(2, fieldNumber);
/* 1665 */       writeBytes(3, value);
/* 1666 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessageNoTag(MessageLite value) throws IOException {
/* 1671 */       writeUInt32NoTag(value.getSerializedSize());
/* 1672 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
/* 1677 */       writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
/* 1678 */       schema.writeTo(value, this.wrapper);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte value) throws IOException {
/*      */       try {
/* 1684 */         this.buffer.put(value);
/* 1685 */       } catch (BufferOverflowException e) {
/* 1686 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytesNoTag(ByteString value) throws IOException {
/* 1692 */       writeUInt32NoTag(value.size());
/* 1693 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
/* 1698 */       writeUInt32NoTag(length);
/* 1699 */       write(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeRawBytes(ByteBuffer value) throws IOException {
/* 1704 */       if (value.hasArray()) {
/* 1705 */         write(value.array(), value.arrayOffset(), value.capacity());
/*      */       } else {
/* 1707 */         ByteBuffer duplicated = value.duplicate();
/* 1708 */         duplicated.clear();
/* 1709 */         write(duplicated);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32NoTag(int value) throws IOException {
/* 1715 */       if (value >= 0) {
/* 1716 */         writeUInt32NoTag(value);
/*      */       } else {
/*      */         
/* 1719 */         writeUInt64NoTag(value);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32NoTag(int value) throws IOException {
/*      */       try {
/*      */         while (true) {
/* 1727 */           if ((value & 0xFFFFFF80) == 0) {
/* 1728 */             this.buffer.put((byte)value);
/*      */             return;
/*      */           } 
/* 1731 */           this.buffer.put((byte)(value & 0x7F | 0x80));
/* 1732 */           value >>>= 7;
/*      */         }
/*      */       
/* 1735 */       } catch (BufferOverflowException e) {
/* 1736 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32NoTag(int value) throws IOException {
/*      */       try {
/* 1743 */         this.buffer.putInt(value);
/* 1744 */       } catch (BufferOverflowException e) {
/* 1745 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64NoTag(long value) throws IOException {
/*      */       try {
/*      */         while (true) {
/* 1753 */           if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
/* 1754 */             this.buffer.put((byte)(int)value);
/*      */             return;
/*      */           } 
/* 1757 */           this.buffer.put((byte)((int)value & 0x7F | 0x80));
/* 1758 */           value >>>= 7L;
/*      */         }
/*      */       
/* 1761 */       } catch (BufferOverflowException e) {
/* 1762 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64NoTag(long value) throws IOException {
/*      */       try {
/* 1769 */         this.buffer.putLong(value);
/* 1770 */       } catch (BufferOverflowException e) {
/* 1771 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte[] value, int offset, int length) throws IOException {
/*      */       try {
/* 1778 */         this.buffer.put(value, offset, length);
/* 1779 */       } catch (IndexOutOfBoundsException e) {
/* 1780 */         throw new CodedOutputStream.OutOfSpaceException(e);
/* 1781 */       } catch (BufferOverflowException e) {
/* 1782 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(byte[] value, int offset, int length) throws IOException {
/* 1788 */       write(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(ByteBuffer value) throws IOException {
/*      */       try {
/* 1794 */         this.buffer.put(value);
/* 1795 */       } catch (BufferOverflowException e) {
/* 1796 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(ByteBuffer value) throws IOException {
/* 1802 */       write(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeStringNoTag(String value) throws IOException {
/* 1807 */       int startPos = this.buffer.position();
/*      */ 
/*      */       
/*      */       try {
/* 1811 */         int maxEncodedSize = value.length() * 3;
/* 1812 */         int maxLengthVarIntSize = computeUInt32SizeNoTag(maxEncodedSize);
/* 1813 */         int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
/* 1814 */         if (minLengthVarIntSize == maxLengthVarIntSize) {
/*      */ 
/*      */           
/* 1817 */           int startOfBytes = this.buffer.position() + minLengthVarIntSize;
/* 1818 */           this.buffer.position(startOfBytes);
/*      */ 
/*      */           
/* 1821 */           encode(value);
/*      */ 
/*      */           
/* 1824 */           int endOfBytes = this.buffer.position();
/* 1825 */           this.buffer.position(startPos);
/* 1826 */           writeUInt32NoTag(endOfBytes - startOfBytes);
/*      */ 
/*      */           
/* 1829 */           this.buffer.position(endOfBytes);
/*      */         } else {
/* 1831 */           int length = Utf8.encodedLength(value);
/* 1832 */           writeUInt32NoTag(length);
/* 1833 */           encode(value);
/*      */         } 
/* 1835 */       } catch (UnpairedSurrogateException e) {
/*      */         
/* 1837 */         this.buffer.position(startPos);
/*      */ 
/*      */         
/* 1840 */         inefficientWriteStringNoTag(value, e);
/* 1841 */       } catch (IllegalArgumentException e) {
/*      */         
/* 1843 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void flush() {
/* 1850 */       this.originalBuffer.position(this.buffer.position());
/*      */     }
/*      */ 
/*      */     
/*      */     public int spaceLeft() {
/* 1855 */       return this.buffer.remaining();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesWritten() {
/* 1860 */       return this.buffer.position() - this.initialPosition;
/*      */     }
/*      */     
/*      */     private void encode(String value) throws IOException {
/*      */       try {
/* 1865 */         Utf8.encodeUtf8(value, this.buffer);
/* 1866 */       } catch (IndexOutOfBoundsException e) {
/* 1867 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class UnsafeDirectNioEncoder
/*      */     extends CodedOutputStream
/*      */   {
/*      */     private final ByteBuffer originalBuffer;
/*      */     
/*      */     private final ByteBuffer buffer;
/*      */     private final long address;
/*      */     private final long initialPosition;
/*      */     private final long limit;
/*      */     private final long oneVarintLimit;
/*      */     private long position;
/*      */     
/*      */     UnsafeDirectNioEncoder(ByteBuffer buffer) {
/* 1886 */       this.originalBuffer = buffer;
/* 1887 */       this.buffer = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
/* 1888 */       this.address = UnsafeUtil.addressOffset(buffer);
/* 1889 */       this.initialPosition = this.address + buffer.position();
/* 1890 */       this.limit = this.address + buffer.limit();
/* 1891 */       this.oneVarintLimit = this.limit - 10L;
/* 1892 */       this.position = this.initialPosition;
/*      */     }
/*      */     
/*      */     static boolean isSupported() {
/* 1896 */       return UnsafeUtil.hasUnsafeByteBufferOperations();
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeTag(int fieldNumber, int wireType) throws IOException {
/* 1901 */       writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32(int fieldNumber, int value) throws IOException {
/* 1906 */       writeTag(fieldNumber, 0);
/* 1907 */       writeInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32(int fieldNumber, int value) throws IOException {
/* 1912 */       writeTag(fieldNumber, 0);
/* 1913 */       writeUInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32(int fieldNumber, int value) throws IOException {
/* 1918 */       writeTag(fieldNumber, 5);
/* 1919 */       writeFixed32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64(int fieldNumber, long value) throws IOException {
/* 1924 */       writeTag(fieldNumber, 0);
/* 1925 */       writeUInt64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64(int fieldNumber, long value) throws IOException {
/* 1930 */       writeTag(fieldNumber, 1);
/* 1931 */       writeFixed64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBool(int fieldNumber, boolean value) throws IOException {
/* 1936 */       writeTag(fieldNumber, 0);
/* 1937 */       write((byte)(value ? 1 : 0));
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeString(int fieldNumber, String value) throws IOException {
/* 1942 */       writeTag(fieldNumber, 2);
/* 1943 */       writeStringNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytes(int fieldNumber, ByteString value) throws IOException {
/* 1948 */       writeTag(fieldNumber, 2);
/* 1949 */       writeBytesNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
/* 1954 */       writeByteArray(fieldNumber, value, 0, value.length);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
/* 1960 */       writeTag(fieldNumber, 2);
/* 1961 */       writeByteArrayNoTag(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
/* 1966 */       writeTag(fieldNumber, 2);
/* 1967 */       writeUInt32NoTag(value.capacity());
/* 1968 */       writeRawBytes(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
/* 1973 */       writeTag(fieldNumber, 2);
/* 1974 */       writeMessageNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
/* 1979 */       writeTag(fieldNumber, 2);
/* 1980 */       writeMessageNoTag(value, schema);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
/* 1985 */       writeTag(1, 3);
/* 1986 */       writeUInt32(2, fieldNumber);
/* 1987 */       writeMessage(3, value);
/* 1988 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
/* 1993 */       writeTag(1, 3);
/* 1994 */       writeUInt32(2, fieldNumber);
/* 1995 */       writeBytes(3, value);
/* 1996 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessageNoTag(MessageLite value) throws IOException {
/* 2001 */       writeUInt32NoTag(value.getSerializedSize());
/* 2002 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
/* 2007 */       writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
/* 2008 */       schema.writeTo(value, this.wrapper);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte value) throws IOException {
/* 2013 */       if (this.position >= this.limit) {
/* 2014 */         throw new CodedOutputStream.OutOfSpaceException(
/* 2015 */             String.format("Pos: %d, limit: %d, len: %d", new Object[] { Long.valueOf(this.position), Long.valueOf(this.limit), Integer.valueOf(1) }));
/*      */       }
/* 2017 */       UnsafeUtil.putByte(this.position++, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytesNoTag(ByteString value) throws IOException {
/* 2022 */       writeUInt32NoTag(value.size());
/* 2023 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
/* 2028 */       writeUInt32NoTag(length);
/* 2029 */       write(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeRawBytes(ByteBuffer value) throws IOException {
/* 2034 */       if (value.hasArray()) {
/* 2035 */         write(value.array(), value.arrayOffset(), value.capacity());
/*      */       } else {
/* 2037 */         ByteBuffer duplicated = value.duplicate();
/* 2038 */         duplicated.clear();
/* 2039 */         write(duplicated);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32NoTag(int value) throws IOException {
/* 2045 */       if (value >= 0) {
/* 2046 */         writeUInt32NoTag(value);
/*      */       } else {
/*      */         
/* 2049 */         writeUInt64NoTag(value);
/*      */       } 
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
/*      */     
/*      */     public void writeUInt32NoTag(int value) throws IOException {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield position : J
/*      */       //   4: aload_0
/*      */       //   5: getfield oneVarintLimit : J
/*      */       //   8: lcmp
/*      */       //   9: ifgt -> 67
/*      */       //   12: iload_1
/*      */       //   13: bipush #-128
/*      */       //   15: iand
/*      */       //   16: ifne -> 36
/*      */       //   19: aload_0
/*      */       //   20: dup
/*      */       //   21: getfield position : J
/*      */       //   24: dup2_x1
/*      */       //   25: lconst_1
/*      */       //   26: ladd
/*      */       //   27: putfield position : J
/*      */       //   30: iload_1
/*      */       //   31: i2b
/*      */       //   32: invokestatic putByte : (JB)V
/*      */       //   35: return
/*      */       //   36: aload_0
/*      */       //   37: dup
/*      */       //   38: getfield position : J
/*      */       //   41: dup2_x1
/*      */       //   42: lconst_1
/*      */       //   43: ladd
/*      */       //   44: putfield position : J
/*      */       //   47: iload_1
/*      */       //   48: bipush #127
/*      */       //   50: iand
/*      */       //   51: sipush #128
/*      */       //   54: ior
/*      */       //   55: i2b
/*      */       //   56: invokestatic putByte : (JB)V
/*      */       //   59: iload_1
/*      */       //   60: bipush #7
/*      */       //   62: iushr
/*      */       //   63: istore_1
/*      */       //   64: goto -> 12
/*      */       //   67: aload_0
/*      */       //   68: getfield position : J
/*      */       //   71: aload_0
/*      */       //   72: getfield limit : J
/*      */       //   75: lcmp
/*      */       //   76: ifge -> 134
/*      */       //   79: iload_1
/*      */       //   80: bipush #-128
/*      */       //   82: iand
/*      */       //   83: ifne -> 103
/*      */       //   86: aload_0
/*      */       //   87: dup
/*      */       //   88: getfield position : J
/*      */       //   91: dup2_x1
/*      */       //   92: lconst_1
/*      */       //   93: ladd
/*      */       //   94: putfield position : J
/*      */       //   97: iload_1
/*      */       //   98: i2b
/*      */       //   99: invokestatic putByte : (JB)V
/*      */       //   102: return
/*      */       //   103: aload_0
/*      */       //   104: dup
/*      */       //   105: getfield position : J
/*      */       //   108: dup2_x1
/*      */       //   109: lconst_1
/*      */       //   110: ladd
/*      */       //   111: putfield position : J
/*      */       //   114: iload_1
/*      */       //   115: bipush #127
/*      */       //   117: iand
/*      */       //   118: sipush #128
/*      */       //   121: ior
/*      */       //   122: i2b
/*      */       //   123: invokestatic putByte : (JB)V
/*      */       //   126: iload_1
/*      */       //   127: bipush #7
/*      */       //   129: iushr
/*      */       //   130: istore_1
/*      */       //   131: goto -> 67
/*      */       //   134: new com/google/protobuf/CodedOutputStream$OutOfSpaceException
/*      */       //   137: dup
/*      */       //   138: ldc 'Pos: %d, limit: %d, len: %d'
/*      */       //   140: iconst_3
/*      */       //   141: anewarray java/lang/Object
/*      */       //   144: dup
/*      */       //   145: iconst_0
/*      */       //   146: aload_0
/*      */       //   147: getfield position : J
/*      */       //   150: invokestatic valueOf : (J)Ljava/lang/Long;
/*      */       //   153: aastore
/*      */       //   154: dup
/*      */       //   155: iconst_1
/*      */       //   156: aload_0
/*      */       //   157: getfield limit : J
/*      */       //   160: invokestatic valueOf : (J)Ljava/lang/Long;
/*      */       //   163: aastore
/*      */       //   164: dup
/*      */       //   165: iconst_2
/*      */       //   166: iconst_1
/*      */       //   167: invokestatic valueOf : (I)Ljava/lang/Integer;
/*      */       //   170: aastore
/*      */       //   171: invokestatic format : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */       //   174: invokespecial <init> : (Ljava/lang/String;)V
/*      */       //   177: athrow
/*      */       // Line number table:
/*      */       //   Java source line number -> byte code offset
/*      */       //   #2055	-> 0
/*      */       //   #2058	-> 12
/*      */       //   #2059	-> 19
/*      */       //   #2060	-> 35
/*      */       //   #2062	-> 36
/*      */       //   #2063	-> 59
/*      */       //   #2067	-> 67
/*      */       //   #2068	-> 79
/*      */       //   #2069	-> 86
/*      */       //   #2070	-> 102
/*      */       //   #2072	-> 103
/*      */       //   #2073	-> 126
/*      */       //   #2076	-> 134
/*      */       //   #2077	-> 150
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	descriptor
/*      */       //   0	178	0	this	Lcom/google/protobuf/CodedOutputStream$UnsafeDirectNioEncoder;
/*      */       //   0	178	1	value	I
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
/*      */     public void writeFixed32NoTag(int value) throws IOException {
/* 2083 */       this.buffer.putInt(bufferPos(this.position), value);
/* 2084 */       this.position += 4L;
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
/*      */     
/*      */     public void writeUInt64NoTag(long value) throws IOException {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield position : J
/*      */       //   4: aload_0
/*      */       //   5: getfield oneVarintLimit : J
/*      */       //   8: lcmp
/*      */       //   9: ifgt -> 72
/*      */       //   12: lload_1
/*      */       //   13: ldc2_w -128
/*      */       //   16: land
/*      */       //   17: lconst_0
/*      */       //   18: lcmp
/*      */       //   19: ifne -> 40
/*      */       //   22: aload_0
/*      */       //   23: dup
/*      */       //   24: getfield position : J
/*      */       //   27: dup2_x1
/*      */       //   28: lconst_1
/*      */       //   29: ladd
/*      */       //   30: putfield position : J
/*      */       //   33: lload_1
/*      */       //   34: l2i
/*      */       //   35: i2b
/*      */       //   36: invokestatic putByte : (JB)V
/*      */       //   39: return
/*      */       //   40: aload_0
/*      */       //   41: dup
/*      */       //   42: getfield position : J
/*      */       //   45: dup2_x1
/*      */       //   46: lconst_1
/*      */       //   47: ladd
/*      */       //   48: putfield position : J
/*      */       //   51: lload_1
/*      */       //   52: l2i
/*      */       //   53: bipush #127
/*      */       //   55: iand
/*      */       //   56: sipush #128
/*      */       //   59: ior
/*      */       //   60: i2b
/*      */       //   61: invokestatic putByte : (JB)V
/*      */       //   64: lload_1
/*      */       //   65: bipush #7
/*      */       //   67: lushr
/*      */       //   68: lstore_1
/*      */       //   69: goto -> 12
/*      */       //   72: aload_0
/*      */       //   73: getfield position : J
/*      */       //   76: aload_0
/*      */       //   77: getfield limit : J
/*      */       //   80: lcmp
/*      */       //   81: ifge -> 144
/*      */       //   84: lload_1
/*      */       //   85: ldc2_w -128
/*      */       //   88: land
/*      */       //   89: lconst_0
/*      */       //   90: lcmp
/*      */       //   91: ifne -> 112
/*      */       //   94: aload_0
/*      */       //   95: dup
/*      */       //   96: getfield position : J
/*      */       //   99: dup2_x1
/*      */       //   100: lconst_1
/*      */       //   101: ladd
/*      */       //   102: putfield position : J
/*      */       //   105: lload_1
/*      */       //   106: l2i
/*      */       //   107: i2b
/*      */       //   108: invokestatic putByte : (JB)V
/*      */       //   111: return
/*      */       //   112: aload_0
/*      */       //   113: dup
/*      */       //   114: getfield position : J
/*      */       //   117: dup2_x1
/*      */       //   118: lconst_1
/*      */       //   119: ladd
/*      */       //   120: putfield position : J
/*      */       //   123: lload_1
/*      */       //   124: l2i
/*      */       //   125: bipush #127
/*      */       //   127: iand
/*      */       //   128: sipush #128
/*      */       //   131: ior
/*      */       //   132: i2b
/*      */       //   133: invokestatic putByte : (JB)V
/*      */       //   136: lload_1
/*      */       //   137: bipush #7
/*      */       //   139: lushr
/*      */       //   140: lstore_1
/*      */       //   141: goto -> 72
/*      */       //   144: new com/google/protobuf/CodedOutputStream$OutOfSpaceException
/*      */       //   147: dup
/*      */       //   148: ldc 'Pos: %d, limit: %d, len: %d'
/*      */       //   150: iconst_3
/*      */       //   151: anewarray java/lang/Object
/*      */       //   154: dup
/*      */       //   155: iconst_0
/*      */       //   156: aload_0
/*      */       //   157: getfield position : J
/*      */       //   160: invokestatic valueOf : (J)Ljava/lang/Long;
/*      */       //   163: aastore
/*      */       //   164: dup
/*      */       //   165: iconst_1
/*      */       //   166: aload_0
/*      */       //   167: getfield limit : J
/*      */       //   170: invokestatic valueOf : (J)Ljava/lang/Long;
/*      */       //   173: aastore
/*      */       //   174: dup
/*      */       //   175: iconst_2
/*      */       //   176: iconst_1
/*      */       //   177: invokestatic valueOf : (I)Ljava/lang/Integer;
/*      */       //   180: aastore
/*      */       //   181: invokestatic format : (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */       //   184: invokespecial <init> : (Ljava/lang/String;)V
/*      */       //   187: athrow
/*      */       // Line number table:
/*      */       //   Java source line number -> byte code offset
/*      */       //   #2089	-> 0
/*      */       //   #2092	-> 12
/*      */       //   #2093	-> 22
/*      */       //   #2094	-> 39
/*      */       //   #2096	-> 40
/*      */       //   #2097	-> 64
/*      */       //   #2101	-> 72
/*      */       //   #2102	-> 84
/*      */       //   #2103	-> 94
/*      */       //   #2104	-> 111
/*      */       //   #2106	-> 112
/*      */       //   #2107	-> 136
/*      */       //   #2110	-> 144
/*      */       //   #2111	-> 160
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	descriptor
/*      */       //   0	188	0	this	Lcom/google/protobuf/CodedOutputStream$UnsafeDirectNioEncoder;
/*      */       //   0	188	1	value	J
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
/*      */     public void writeFixed64NoTag(long value) throws IOException {
/* 2117 */       this.buffer.putLong(bufferPos(this.position), value);
/* 2118 */       this.position += 8L;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte[] value, int offset, int length) throws IOException {
/* 2123 */       if (value == null || offset < 0 || length < 0 || value.length - length < offset || this.limit - length < this.position) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2128 */         if (value == null) {
/* 2129 */           throw new NullPointerException("value");
/*      */         }
/* 2131 */         throw new CodedOutputStream.OutOfSpaceException(
/* 2132 */             String.format("Pos: %d, limit: %d, len: %d", new Object[] { Long.valueOf(this.position), Long.valueOf(this.limit), Integer.valueOf(length) }));
/*      */       } 
/*      */       
/* 2135 */       UnsafeUtil.copyMemory(value, offset, this.position, length);
/* 2136 */       this.position += length;
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(byte[] value, int offset, int length) throws IOException {
/* 2141 */       write(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(ByteBuffer value) throws IOException {
/*      */       try {
/* 2147 */         int length = value.remaining();
/* 2148 */         repositionBuffer(this.position);
/* 2149 */         this.buffer.put(value);
/* 2150 */         this.position += length;
/* 2151 */       } catch (BufferOverflowException e) {
/* 2152 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(ByteBuffer value) throws IOException {
/* 2158 */       write(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeStringNoTag(String value) throws IOException {
/* 2163 */       long prevPos = this.position;
/*      */ 
/*      */       
/*      */       try {
/* 2167 */         int maxEncodedSize = value.length() * 3;
/* 2168 */         int maxLengthVarIntSize = computeUInt32SizeNoTag(maxEncodedSize);
/* 2169 */         int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
/* 2170 */         if (minLengthVarIntSize == maxLengthVarIntSize) {
/*      */ 
/*      */           
/* 2173 */           int stringStart = bufferPos(this.position) + minLengthVarIntSize;
/* 2174 */           this.buffer.position(stringStart);
/*      */ 
/*      */           
/* 2177 */           Utf8.encodeUtf8(value, this.buffer);
/*      */ 
/*      */           
/* 2180 */           int length = this.buffer.position() - stringStart;
/* 2181 */           writeUInt32NoTag(length);
/* 2182 */           this.position += length;
/*      */         } else {
/*      */           
/* 2185 */           int length = Utf8.encodedLength(value);
/* 2186 */           writeUInt32NoTag(length);
/*      */ 
/*      */           
/* 2189 */           repositionBuffer(this.position);
/* 2190 */           Utf8.encodeUtf8(value, this.buffer);
/* 2191 */           this.position += length;
/*      */         } 
/* 2193 */       } catch (UnpairedSurrogateException e) {
/*      */         
/* 2195 */         this.position = prevPos;
/* 2196 */         repositionBuffer(this.position);
/*      */ 
/*      */         
/* 2199 */         inefficientWriteStringNoTag(value, e);
/* 2200 */       } catch (IllegalArgumentException e) {
/*      */         
/* 2202 */         throw new CodedOutputStream.OutOfSpaceException(e);
/* 2203 */       } catch (IndexOutOfBoundsException e) {
/* 2204 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void flush() {
/* 2211 */       this.originalBuffer.position(bufferPos(this.position));
/*      */     }
/*      */ 
/*      */     
/*      */     public int spaceLeft() {
/* 2216 */       return (int)(this.limit - this.position);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTotalBytesWritten() {
/* 2221 */       return (int)(this.position - this.initialPosition);
/*      */     }
/*      */     
/*      */     private void repositionBuffer(long pos) {
/* 2225 */       this.buffer.position(bufferPos(pos));
/*      */     }
/*      */     
/*      */     private int bufferPos(long pos) {
/* 2229 */       return (int)(pos - this.address);
/*      */     }
/*      */   }
/*      */   
/*      */   private static abstract class AbstractBufferedEncoder
/*      */     extends CodedOutputStream {
/*      */     final byte[] buffer;
/*      */     final int limit;
/*      */     int position;
/*      */     int totalBytesWritten;
/*      */     
/*      */     AbstractBufferedEncoder(int bufferSize) {
/* 2241 */       if (bufferSize < 0) {
/* 2242 */         throw new IllegalArgumentException("bufferSize must be >= 0");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2248 */       this.buffer = new byte[Math.max(bufferSize, 20)];
/* 2249 */       this.limit = this.buffer.length;
/*      */     }
/*      */ 
/*      */     
/*      */     public final int spaceLeft() {
/* 2254 */       throw new UnsupportedOperationException("spaceLeft() can only be called on CodedOutputStreams that are writing to a flat array or ByteBuffer.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public final int getTotalBytesWritten() {
/* 2261 */       return this.totalBytesWritten;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void buffer(byte value) {
/* 2269 */       this.buffer[this.position++] = value;
/* 2270 */       this.totalBytesWritten++;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void bufferTag(int fieldNumber, int wireType) {
/* 2278 */       bufferUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void bufferInt32NoTag(int value) {
/* 2286 */       if (value >= 0) {
/* 2287 */         bufferUInt32NoTag(value);
/*      */       } else {
/*      */         
/* 2290 */         bufferUInt64NoTag(value);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void bufferUInt32NoTag(int value) {
/* 2299 */       if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
/* 2300 */         long originalPos = this.position;
/*      */         while (true) {
/* 2302 */           if ((value & 0xFFFFFF80) == 0) {
/* 2303 */             UnsafeUtil.putByte(this.buffer, this.position++, (byte)value);
/*      */             break;
/*      */           } 
/* 2306 */           UnsafeUtil.putByte(this.buffer, this.position++, (byte)(value & 0x7F | 0x80));
/* 2307 */           value >>>= 7;
/*      */         } 
/*      */         
/* 2310 */         int delta = (int)(this.position - originalPos);
/* 2311 */         this.totalBytesWritten += delta;
/*      */       } else {
/*      */         while (true) {
/* 2314 */           if ((value & 0xFFFFFF80) == 0) {
/* 2315 */             this.buffer[this.position++] = (byte)value;
/* 2316 */             this.totalBytesWritten++;
/*      */             return;
/*      */           } 
/* 2319 */           this.buffer[this.position++] = (byte)(value & 0x7F | 0x80);
/* 2320 */           this.totalBytesWritten++;
/* 2321 */           value >>>= 7;
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void bufferUInt64NoTag(long value) {
/* 2332 */       if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
/* 2333 */         long originalPos = this.position;
/*      */         while (true) {
/* 2335 */           if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
/* 2336 */             UnsafeUtil.putByte(this.buffer, this.position++, (byte)(int)value);
/*      */             break;
/*      */           } 
/* 2339 */           UnsafeUtil.putByte(this.buffer, this.position++, (byte)((int)value & 0x7F | 0x80));
/* 2340 */           value >>>= 7L;
/*      */         } 
/*      */         
/* 2343 */         int delta = (int)(this.position - originalPos);
/* 2344 */         this.totalBytesWritten += delta;
/*      */       } else {
/*      */         while (true) {
/* 2347 */           if ((value & 0xFFFFFFFFFFFFFF80L) == 0L) {
/* 2348 */             this.buffer[this.position++] = (byte)(int)value;
/* 2349 */             this.totalBytesWritten++;
/*      */             return;
/*      */           } 
/* 2352 */           this.buffer[this.position++] = (byte)((int)value & 0x7F | 0x80);
/* 2353 */           this.totalBytesWritten++;
/* 2354 */           value >>>= 7L;
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void bufferFixed32NoTag(int value) {
/* 2365 */       this.buffer[this.position++] = (byte)(value & 0xFF);
/* 2366 */       this.buffer[this.position++] = (byte)(value >> 8 & 0xFF);
/* 2367 */       this.buffer[this.position++] = (byte)(value >> 16 & 0xFF);
/* 2368 */       this.buffer[this.position++] = (byte)(value >> 24 & 0xFF);
/* 2369 */       this.totalBytesWritten += 4;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final void bufferFixed64NoTag(long value) {
/* 2377 */       this.buffer[this.position++] = (byte)(int)(value & 0xFFL);
/* 2378 */       this.buffer[this.position++] = (byte)(int)(value >> 8L & 0xFFL);
/* 2379 */       this.buffer[this.position++] = (byte)(int)(value >> 16L & 0xFFL);
/* 2380 */       this.buffer[this.position++] = (byte)(int)(value >> 24L & 0xFFL);
/* 2381 */       this.buffer[this.position++] = (byte)((int)(value >> 32L) & 0xFF);
/* 2382 */       this.buffer[this.position++] = (byte)((int)(value >> 40L) & 0xFF);
/* 2383 */       this.buffer[this.position++] = (byte)((int)(value >> 48L) & 0xFF);
/* 2384 */       this.buffer[this.position++] = (byte)((int)(value >> 56L) & 0xFF);
/* 2385 */       this.totalBytesWritten += 8;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class ByteOutputEncoder
/*      */     extends AbstractBufferedEncoder
/*      */   {
/*      */     private final ByteOutput out;
/*      */ 
/*      */     
/*      */     ByteOutputEncoder(ByteOutput out, int bufferSize) {
/* 2398 */       super(bufferSize);
/* 2399 */       if (out == null) {
/* 2400 */         throw new NullPointerException("out");
/*      */       }
/* 2402 */       this.out = out;
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeTag(int fieldNumber, int wireType) throws IOException {
/* 2407 */       writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32(int fieldNumber, int value) throws IOException {
/* 2412 */       flushIfNotAvailable(20);
/* 2413 */       bufferTag(fieldNumber, 0);
/* 2414 */       bufferInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32(int fieldNumber, int value) throws IOException {
/* 2419 */       flushIfNotAvailable(20);
/* 2420 */       bufferTag(fieldNumber, 0);
/* 2421 */       bufferUInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32(int fieldNumber, int value) throws IOException {
/* 2426 */       flushIfNotAvailable(14);
/* 2427 */       bufferTag(fieldNumber, 5);
/* 2428 */       bufferFixed32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64(int fieldNumber, long value) throws IOException {
/* 2433 */       flushIfNotAvailable(20);
/* 2434 */       bufferTag(fieldNumber, 0);
/* 2435 */       bufferUInt64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64(int fieldNumber, long value) throws IOException {
/* 2440 */       flushIfNotAvailable(18);
/* 2441 */       bufferTag(fieldNumber, 1);
/* 2442 */       bufferFixed64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBool(int fieldNumber, boolean value) throws IOException {
/* 2447 */       flushIfNotAvailable(11);
/* 2448 */       bufferTag(fieldNumber, 0);
/* 2449 */       buffer((byte)(value ? 1 : 0));
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeString(int fieldNumber, String value) throws IOException {
/* 2454 */       writeTag(fieldNumber, 2);
/* 2455 */       writeStringNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytes(int fieldNumber, ByteString value) throws IOException {
/* 2460 */       writeTag(fieldNumber, 2);
/* 2461 */       writeBytesNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
/* 2466 */       writeByteArray(fieldNumber, value, 0, value.length);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
/* 2473 */       writeTag(fieldNumber, 2);
/* 2474 */       writeByteArrayNoTag(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
/* 2479 */       writeTag(fieldNumber, 2);
/* 2480 */       writeUInt32NoTag(value.capacity());
/* 2481 */       writeRawBytes(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytesNoTag(ByteString value) throws IOException {
/* 2486 */       writeUInt32NoTag(value.size());
/* 2487 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
/* 2492 */       writeUInt32NoTag(length);
/* 2493 */       write(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeRawBytes(ByteBuffer value) throws IOException {
/* 2498 */       if (value.hasArray()) {
/* 2499 */         write(value.array(), value.arrayOffset(), value.capacity());
/*      */       } else {
/* 2501 */         ByteBuffer duplicated = value.duplicate();
/* 2502 */         duplicated.clear();
/* 2503 */         write(duplicated);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
/* 2509 */       writeTag(fieldNumber, 2);
/* 2510 */       writeMessageNoTag(value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
/* 2516 */       writeTag(fieldNumber, 2);
/* 2517 */       writeMessageNoTag(value, schema);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
/* 2523 */       writeTag(1, 3);
/* 2524 */       writeUInt32(2, fieldNumber);
/* 2525 */       writeMessage(3, value);
/* 2526 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
/* 2532 */       writeTag(1, 3);
/* 2533 */       writeUInt32(2, fieldNumber);
/* 2534 */       writeBytes(3, value);
/* 2535 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessageNoTag(MessageLite value) throws IOException {
/* 2540 */       writeUInt32NoTag(value.getSerializedSize());
/* 2541 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
/* 2546 */       writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
/* 2547 */       schema.writeTo(value, this.wrapper);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte value) throws IOException {
/* 2552 */       if (this.position == this.limit) {
/* 2553 */         doFlush();
/*      */       }
/*      */       
/* 2556 */       buffer(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32NoTag(int value) throws IOException {
/* 2561 */       if (value >= 0) {
/* 2562 */         writeUInt32NoTag(value);
/*      */       } else {
/*      */         
/* 2565 */         writeUInt64NoTag(value);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32NoTag(int value) throws IOException {
/* 2571 */       flushIfNotAvailable(5);
/* 2572 */       bufferUInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32NoTag(int value) throws IOException {
/* 2577 */       flushIfNotAvailable(4);
/* 2578 */       bufferFixed32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64NoTag(long value) throws IOException {
/* 2583 */       flushIfNotAvailable(10);
/* 2584 */       bufferUInt64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64NoTag(long value) throws IOException {
/* 2589 */       flushIfNotAvailable(8);
/* 2590 */       bufferFixed64NoTag(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeStringNoTag(String value) throws IOException {
/* 2597 */       int maxLength = value.length() * 3;
/* 2598 */       int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
/*      */ 
/*      */ 
/*      */       
/* 2602 */       if (maxLengthVarIntSize + maxLength > this.limit) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2607 */         byte[] encodedBytes = new byte[maxLength];
/* 2608 */         int actualLength = Utf8.encode(value, encodedBytes, 0, maxLength);
/* 2609 */         writeUInt32NoTag(actualLength);
/* 2610 */         writeLazy(encodedBytes, 0, actualLength);
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/* 2615 */       if (maxLengthVarIntSize + maxLength > this.limit - this.position)
/*      */       {
/* 2617 */         doFlush();
/*      */       }
/*      */       
/* 2620 */       int oldPosition = this.position;
/*      */ 
/*      */       
/*      */       try {
/* 2624 */         int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
/*      */         
/* 2626 */         if (minLengthVarIntSize == maxLengthVarIntSize) {
/* 2627 */           this.position = oldPosition + minLengthVarIntSize;
/* 2628 */           int newPosition = Utf8.encode(value, this.buffer, this.position, this.limit - this.position);
/*      */ 
/*      */           
/* 2631 */           this.position = oldPosition;
/* 2632 */           int length = newPosition - oldPosition - minLengthVarIntSize;
/* 2633 */           bufferUInt32NoTag(length);
/* 2634 */           this.position = newPosition;
/* 2635 */           this.totalBytesWritten += length;
/*      */         } else {
/* 2637 */           int length = Utf8.encodedLength(value);
/* 2638 */           bufferUInt32NoTag(length);
/* 2639 */           this.position = Utf8.encode(value, this.buffer, this.position, length);
/* 2640 */           this.totalBytesWritten += length;
/*      */         } 
/* 2642 */       } catch (UnpairedSurrogateException e) {
/*      */         
/* 2644 */         this.totalBytesWritten -= this.position - oldPosition;
/* 2645 */         this.position = oldPosition;
/*      */ 
/*      */         
/* 2648 */         inefficientWriteStringNoTag(value, e);
/* 2649 */       } catch (IndexOutOfBoundsException e) {
/* 2650 */         throw new CodedOutputStream.OutOfSpaceException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void flush() throws IOException {
/* 2656 */       if (this.position > 0)
/*      */       {
/* 2658 */         doFlush();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte[] value, int offset, int length) throws IOException {
/* 2664 */       flush();
/* 2665 */       this.out.write(value, offset, length);
/* 2666 */       this.totalBytesWritten += length;
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(byte[] value, int offset, int length) throws IOException {
/* 2671 */       flush();
/* 2672 */       this.out.writeLazy(value, offset, length);
/* 2673 */       this.totalBytesWritten += length;
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(ByteBuffer value) throws IOException {
/* 2678 */       flush();
/* 2679 */       int length = value.remaining();
/* 2680 */       this.out.write(value);
/* 2681 */       this.totalBytesWritten += length;
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(ByteBuffer value) throws IOException {
/* 2686 */       flush();
/* 2687 */       int length = value.remaining();
/* 2688 */       this.out.writeLazy(value);
/* 2689 */       this.totalBytesWritten += length;
/*      */     }
/*      */     
/*      */     private void flushIfNotAvailable(int requiredSize) throws IOException {
/* 2693 */       if (this.limit - this.position < requiredSize) {
/* 2694 */         doFlush();
/*      */       }
/*      */     }
/*      */     
/*      */     private void doFlush() throws IOException {
/* 2699 */       this.out.write(this.buffer, 0, this.position);
/* 2700 */       this.position = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class OutputStreamEncoder
/*      */     extends AbstractBufferedEncoder
/*      */   {
/*      */     private final OutputStream out;
/*      */ 
/*      */     
/*      */     OutputStreamEncoder(OutputStream out, int bufferSize) {
/* 2712 */       super(bufferSize);
/* 2713 */       if (out == null) {
/* 2714 */         throw new NullPointerException("out");
/*      */       }
/* 2716 */       this.out = out;
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeTag(int fieldNumber, int wireType) throws IOException {
/* 2721 */       writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32(int fieldNumber, int value) throws IOException {
/* 2726 */       flushIfNotAvailable(20);
/* 2727 */       bufferTag(fieldNumber, 0);
/* 2728 */       bufferInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32(int fieldNumber, int value) throws IOException {
/* 2733 */       flushIfNotAvailable(20);
/* 2734 */       bufferTag(fieldNumber, 0);
/* 2735 */       bufferUInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32(int fieldNumber, int value) throws IOException {
/* 2740 */       flushIfNotAvailable(14);
/* 2741 */       bufferTag(fieldNumber, 5);
/* 2742 */       bufferFixed32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64(int fieldNumber, long value) throws IOException {
/* 2747 */       flushIfNotAvailable(20);
/* 2748 */       bufferTag(fieldNumber, 0);
/* 2749 */       bufferUInt64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64(int fieldNumber, long value) throws IOException {
/* 2754 */       flushIfNotAvailable(18);
/* 2755 */       bufferTag(fieldNumber, 1);
/* 2756 */       bufferFixed64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBool(int fieldNumber, boolean value) throws IOException {
/* 2761 */       flushIfNotAvailable(11);
/* 2762 */       bufferTag(fieldNumber, 0);
/* 2763 */       buffer((byte)(value ? 1 : 0));
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeString(int fieldNumber, String value) throws IOException {
/* 2768 */       writeTag(fieldNumber, 2);
/* 2769 */       writeStringNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytes(int fieldNumber, ByteString value) throws IOException {
/* 2774 */       writeTag(fieldNumber, 2);
/* 2775 */       writeBytesNoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
/* 2780 */       writeByteArray(fieldNumber, value, 0, value.length);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
/* 2787 */       writeTag(fieldNumber, 2);
/* 2788 */       writeByteArrayNoTag(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
/* 2793 */       writeTag(fieldNumber, 2);
/* 2794 */       writeUInt32NoTag(value.capacity());
/* 2795 */       writeRawBytes(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeBytesNoTag(ByteString value) throws IOException {
/* 2800 */       writeUInt32NoTag(value.size());
/* 2801 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
/* 2806 */       writeUInt32NoTag(length);
/* 2807 */       write(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeRawBytes(ByteBuffer value) throws IOException {
/* 2812 */       if (value.hasArray()) {
/* 2813 */         write(value.array(), value.arrayOffset(), value.capacity());
/*      */       } else {
/* 2815 */         ByteBuffer duplicated = value.duplicate();
/* 2816 */         duplicated.clear();
/* 2817 */         write(duplicated);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
/* 2823 */       writeTag(fieldNumber, 2);
/* 2824 */       writeMessageNoTag(value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     void writeMessage(int fieldNumber, MessageLite value, Schema schema) throws IOException {
/* 2830 */       writeTag(fieldNumber, 2);
/* 2831 */       writeMessageNoTag(value, schema);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
/* 2837 */       writeTag(1, 3);
/* 2838 */       writeUInt32(2, fieldNumber);
/* 2839 */       writeMessage(3, value);
/* 2840 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
/* 2846 */       writeTag(1, 3);
/* 2847 */       writeUInt32(2, fieldNumber);
/* 2848 */       writeBytes(3, value);
/* 2849 */       writeTag(1, 4);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeMessageNoTag(MessageLite value) throws IOException {
/* 2854 */       writeUInt32NoTag(value.getSerializedSize());
/* 2855 */       value.writeTo(this);
/*      */     }
/*      */ 
/*      */     
/*      */     void writeMessageNoTag(MessageLite value, Schema<MessageLite> schema) throws IOException {
/* 2860 */       writeUInt32NoTag(((AbstractMessageLite)value).getSerializedSize(schema));
/* 2861 */       schema.writeTo(value, this.wrapper);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte value) throws IOException {
/* 2866 */       if (this.position == this.limit) {
/* 2867 */         doFlush();
/*      */       }
/*      */       
/* 2870 */       buffer(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeInt32NoTag(int value) throws IOException {
/* 2875 */       if (value >= 0) {
/* 2876 */         writeUInt32NoTag(value);
/*      */       } else {
/*      */         
/* 2879 */         writeUInt64NoTag(value);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt32NoTag(int value) throws IOException {
/* 2885 */       flushIfNotAvailable(5);
/* 2886 */       bufferUInt32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed32NoTag(int value) throws IOException {
/* 2891 */       flushIfNotAvailable(4);
/* 2892 */       bufferFixed32NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeUInt64NoTag(long value) throws IOException {
/* 2897 */       flushIfNotAvailable(10);
/* 2898 */       bufferUInt64NoTag(value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeFixed64NoTag(long value) throws IOException {
/* 2903 */       flushIfNotAvailable(8);
/* 2904 */       bufferFixed64NoTag(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeStringNoTag(String value) throws IOException {
/*      */       try {
/* 2912 */         int maxLength = value.length() * 3;
/* 2913 */         int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
/*      */ 
/*      */ 
/*      */         
/* 2917 */         if (maxLengthVarIntSize + maxLength > this.limit) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2922 */           byte[] encodedBytes = new byte[maxLength];
/* 2923 */           int actualLength = Utf8.encode(value, encodedBytes, 0, maxLength);
/* 2924 */           writeUInt32NoTag(actualLength);
/* 2925 */           writeLazy(encodedBytes, 0, actualLength);
/*      */           
/*      */           return;
/*      */         } 
/*      */         
/* 2930 */         if (maxLengthVarIntSize + maxLength > this.limit - this.position)
/*      */         {
/* 2932 */           doFlush();
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 2937 */         int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
/* 2938 */         int oldPosition = this.position;
/*      */         try {
/*      */           int length;
/* 2941 */           if (minLengthVarIntSize == maxLengthVarIntSize) {
/* 2942 */             this.position = oldPosition + minLengthVarIntSize;
/* 2943 */             int newPosition = Utf8.encode(value, this.buffer, this.position, this.limit - this.position);
/*      */ 
/*      */             
/* 2946 */             this.position = oldPosition;
/* 2947 */             length = newPosition - oldPosition - minLengthVarIntSize;
/* 2948 */             bufferUInt32NoTag(length);
/* 2949 */             this.position = newPosition;
/*      */           } else {
/* 2951 */             length = Utf8.encodedLength(value);
/* 2952 */             bufferUInt32NoTag(length);
/* 2953 */             this.position = Utf8.encode(value, this.buffer, this.position, length);
/*      */           } 
/* 2955 */           this.totalBytesWritten += length;
/* 2956 */         } catch (UnpairedSurrogateException e) {
/*      */ 
/*      */           
/* 2959 */           this.totalBytesWritten -= this.position - oldPosition;
/* 2960 */           this.position = oldPosition;
/* 2961 */           throw e;
/* 2962 */         } catch (ArrayIndexOutOfBoundsException e) {
/* 2963 */           throw new CodedOutputStream.OutOfSpaceException(e);
/*      */         } 
/* 2965 */       } catch (UnpairedSurrogateException e) {
/* 2966 */         inefficientWriteStringNoTag(value, e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void flush() throws IOException {
/* 2972 */       if (this.position > 0)
/*      */       {
/* 2974 */         doFlush();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(byte[] value, int offset, int length) throws IOException {
/* 2980 */       if (this.limit - this.position >= length) {
/*      */         
/* 2982 */         System.arraycopy(value, offset, this.buffer, this.position, length);
/* 2983 */         this.position += length;
/* 2984 */         this.totalBytesWritten += length;
/*      */       }
/*      */       else {
/*      */         
/* 2988 */         int bytesWritten = this.limit - this.position;
/* 2989 */         System.arraycopy(value, offset, this.buffer, this.position, bytesWritten);
/* 2990 */         offset += bytesWritten;
/* 2991 */         length -= bytesWritten;
/* 2992 */         this.position = this.limit;
/* 2993 */         this.totalBytesWritten += bytesWritten;
/* 2994 */         doFlush();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2999 */         if (length <= this.limit) {
/*      */           
/* 3001 */           System.arraycopy(value, offset, this.buffer, 0, length);
/* 3002 */           this.position = length;
/*      */         } else {
/*      */           
/* 3005 */           this.out.write(value, offset, length);
/*      */         } 
/* 3007 */         this.totalBytesWritten += length;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(byte[] value, int offset, int length) throws IOException {
/* 3013 */       write(value, offset, length);
/*      */     }
/*      */ 
/*      */     
/*      */     public void write(ByteBuffer value) throws IOException {
/* 3018 */       int length = value.remaining();
/* 3019 */       if (this.limit - this.position >= length) {
/*      */         
/* 3021 */         value.get(this.buffer, this.position, length);
/* 3022 */         this.position += length;
/* 3023 */         this.totalBytesWritten += length;
/*      */       }
/*      */       else {
/*      */         
/* 3027 */         int bytesWritten = this.limit - this.position;
/* 3028 */         value.get(this.buffer, this.position, bytesWritten);
/* 3029 */         length -= bytesWritten;
/* 3030 */         this.position = this.limit;
/* 3031 */         this.totalBytesWritten += bytesWritten;
/* 3032 */         doFlush();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 3037 */         while (length > this.limit) {
/*      */           
/* 3039 */           value.get(this.buffer, 0, this.limit);
/* 3040 */           this.out.write(this.buffer, 0, this.limit);
/* 3041 */           length -= this.limit;
/* 3042 */           this.totalBytesWritten += this.limit;
/*      */         } 
/* 3044 */         value.get(this.buffer, 0, length);
/* 3045 */         this.position = length;
/* 3046 */         this.totalBytesWritten += length;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void writeLazy(ByteBuffer value) throws IOException {
/* 3052 */       write(value);
/*      */     }
/*      */     
/*      */     private void flushIfNotAvailable(int requiredSize) throws IOException {
/* 3056 */       if (this.limit - this.position < requiredSize) {
/* 3057 */         doFlush();
/*      */       }
/*      */     }
/*      */     
/*      */     private void doFlush() throws IOException {
/* 3062 */       this.out.write(this.buffer, 0, this.position);
/* 3063 */       this.position = 0;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\CodedOutputStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */