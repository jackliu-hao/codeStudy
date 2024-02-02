/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class UnknownFieldSet
/*      */   implements MessageLite
/*      */ {
/*      */   private UnknownFieldSet() {
/*   61 */     this.fields = null;
/*   62 */     this.fieldsDescending = null;
/*      */   }
/*      */ 
/*      */   
/*      */   public static Builder newBuilder() {
/*   67 */     return Builder.create();
/*      */   }
/*      */ 
/*      */   
/*      */   public static Builder newBuilder(UnknownFieldSet copyFrom) {
/*   72 */     return newBuilder().mergeFrom(copyFrom);
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnknownFieldSet getDefaultInstance() {
/*   77 */     return defaultInstance;
/*      */   }
/*      */ 
/*      */   
/*      */   public UnknownFieldSet getDefaultInstanceForType() {
/*   82 */     return defaultInstance;
/*      */   }
/*      */   
/*   85 */   private static final UnknownFieldSet defaultInstance = new UnknownFieldSet(
/*      */       
/*   87 */       Collections.emptyMap(), Collections.emptyMap());
/*      */   
/*      */   private final Map<Integer, Field> fields;
/*      */   private final Map<Integer, Field> fieldsDescending;
/*      */   
/*      */   UnknownFieldSet(Map<Integer, Field> fields, Map<Integer, Field> fieldsDescending) {
/*   93 */     this.fields = fields;
/*   94 */     this.fieldsDescending = fieldsDescending;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object other) {
/*  105 */     if (this == other) {
/*  106 */       return true;
/*      */     }
/*  108 */     return (other instanceof UnknownFieldSet && this.fields.equals(((UnknownFieldSet)other).fields));
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  113 */     return this.fields.hashCode();
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<Integer, Field> asMap() {
/*  118 */     return this.fields;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasField(int number) {
/*  123 */     return this.fields.containsKey(Integer.valueOf(number));
/*      */   }
/*      */ 
/*      */   
/*      */   public Field getField(int number) {
/*  128 */     Field result = this.fields.get(Integer.valueOf(number));
/*  129 */     return (result == null) ? Field.getDefaultInstance() : result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(CodedOutputStream output) throws IOException {
/*  135 */     for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
/*  136 */       Field field = entry.getValue();
/*  137 */       field.writeTo(((Integer)entry.getKey()).intValue(), output);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  147 */     return TextFormat.printer().printToString(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteString toByteString() {
/*      */     try {
/*  157 */       ByteString.CodedBuilder out = ByteString.newCodedBuilder(getSerializedSize());
/*  158 */       writeTo(out.getCodedOutput());
/*  159 */       return out.build();
/*  160 */     } catch (IOException e) {
/*  161 */       throw new RuntimeException("Serializing to a ByteString threw an IOException (should never happen).", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] toByteArray() {
/*      */     try {
/*  173 */       byte[] result = new byte[getSerializedSize()];
/*  174 */       CodedOutputStream output = CodedOutputStream.newInstance(result);
/*  175 */       writeTo(output);
/*  176 */       output.checkNoSpaceLeft();
/*  177 */       return result;
/*  178 */     } catch (IOException e) {
/*  179 */       throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(OutputStream output) throws IOException {
/*  190 */     CodedOutputStream codedOutput = CodedOutputStream.newInstance(output);
/*  191 */     writeTo(codedOutput);
/*  192 */     codedOutput.flush();
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeDelimitedTo(OutputStream output) throws IOException {
/*  197 */     CodedOutputStream codedOutput = CodedOutputStream.newInstance(output);
/*  198 */     codedOutput.writeRawVarint32(getSerializedSize());
/*  199 */     writeTo(codedOutput);
/*  200 */     codedOutput.flush();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  206 */     int result = 0;
/*  207 */     for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
/*  208 */       result += ((Field)entry.getValue()).getSerializedSize(((Integer)entry.getKey()).intValue());
/*      */     }
/*  210 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeAsMessageSetTo(CodedOutputStream output) throws IOException {
/*  215 */     for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
/*  216 */       ((Field)entry.getValue()).writeAsMessageSetExtensionTo(((Integer)entry.getKey()).intValue(), output);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   void writeTo(Writer writer) throws IOException {
/*  222 */     if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
/*      */       
/*  224 */       for (Map.Entry<Integer, Field> entry : this.fieldsDescending.entrySet()) {
/*  225 */         ((Field)entry.getValue()).writeTo(((Integer)entry.getKey()).intValue(), writer);
/*      */       }
/*      */     } else {
/*      */       
/*  229 */       for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
/*  230 */         ((Field)entry.getValue()).writeTo(((Integer)entry.getKey()).intValue(), writer);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   void writeAsMessageSetTo(Writer writer) throws IOException {
/*  237 */     if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
/*      */       
/*  239 */       for (Map.Entry<Integer, Field> entry : this.fieldsDescending.entrySet()) {
/*  240 */         ((Field)entry.getValue()).writeAsMessageSetExtensionTo(((Integer)entry.getKey()).intValue(), writer);
/*      */       }
/*      */     } else {
/*      */       
/*  244 */       for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
/*  245 */         ((Field)entry.getValue()).writeAsMessageSetExtensionTo(((Integer)entry.getKey()).intValue(), writer);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSerializedSizeAsMessageSet() {
/*  252 */     int result = 0;
/*  253 */     for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
/*  254 */       result += ((Field)entry.getValue()).getSerializedSizeAsMessageSetExtension(((Integer)entry.getKey()).intValue());
/*      */     }
/*  256 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isInitialized() {
/*  263 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnknownFieldSet parseFrom(CodedInputStream input) throws IOException {
/*  268 */     return newBuilder().mergeFrom(input).build();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static UnknownFieldSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
/*  274 */     return newBuilder().mergeFrom(data).build();
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnknownFieldSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
/*  279 */     return newBuilder().mergeFrom(data).build();
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnknownFieldSet parseFrom(InputStream input) throws IOException {
/*  284 */     return newBuilder().mergeFrom(input).build();
/*      */   }
/*      */ 
/*      */   
/*      */   public Builder newBuilderForType() {
/*  289 */     return newBuilder();
/*      */   }
/*      */ 
/*      */   
/*      */   public Builder toBuilder() {
/*  294 */     return newBuilder().mergeFrom(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Builder
/*      */     implements MessageLite.Builder
/*      */   {
/*      */     private Map<Integer, UnknownFieldSet.Field> fields;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private int lastFieldNumber;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private UnknownFieldSet.Field.Builder lastField;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static Builder create() {
/*  321 */       Builder builder = new Builder();
/*  322 */       builder.reinitialize();
/*  323 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private UnknownFieldSet.Field.Builder getFieldBuilder(int number) {
/*  330 */       if (this.lastField != null) {
/*  331 */         if (number == this.lastFieldNumber) {
/*  332 */           return this.lastField;
/*      */         }
/*      */         
/*  335 */         addField(this.lastFieldNumber, this.lastField.build());
/*      */       } 
/*  337 */       if (number == 0) {
/*  338 */         return null;
/*      */       }
/*  340 */       UnknownFieldSet.Field existing = this.fields.get(Integer.valueOf(number));
/*  341 */       this.lastFieldNumber = number;
/*  342 */       this.lastField = UnknownFieldSet.Field.newBuilder();
/*  343 */       if (existing != null) {
/*  344 */         this.lastField.mergeFrom(existing);
/*      */       }
/*  346 */       return this.lastField;
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
/*      */     public UnknownFieldSet build() {
/*      */       UnknownFieldSet result;
/*  359 */       getFieldBuilder(0);
/*      */       
/*  361 */       if (this.fields.isEmpty()) {
/*  362 */         result = UnknownFieldSet.getDefaultInstance();
/*      */       } else {
/*  364 */         Map<Integer, UnknownFieldSet.Field> descendingFields = null;
/*      */         
/*  366 */         descendingFields = Collections.unmodifiableMap(((TreeMap<? extends Integer, ? extends UnknownFieldSet.Field>)this.fields).descendingMap());
/*  367 */         result = new UnknownFieldSet(Collections.unmodifiableMap(this.fields), descendingFields);
/*      */       } 
/*  369 */       this.fields = null;
/*  370 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public UnknownFieldSet buildPartial() {
/*  376 */       return build();
/*      */     }
/*      */ 
/*      */     
/*      */     public Builder clone() {
/*  381 */       getFieldBuilder(0);
/*  382 */       Map<Integer, UnknownFieldSet.Field> descendingFields = null;
/*      */       
/*  384 */       descendingFields = Collections.unmodifiableMap(((TreeMap<? extends Integer, ? extends UnknownFieldSet.Field>)this.fields).descendingMap());
/*  385 */       return UnknownFieldSet.newBuilder().mergeFrom(new UnknownFieldSet(this.fields, descendingFields));
/*      */     }
/*      */ 
/*      */     
/*      */     public UnknownFieldSet getDefaultInstanceForType() {
/*  390 */       return UnknownFieldSet.getDefaultInstance();
/*      */     }
/*      */     
/*      */     private void reinitialize() {
/*  394 */       this.fields = Collections.emptyMap();
/*  395 */       this.lastFieldNumber = 0;
/*  396 */       this.lastField = null;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder clear() {
/*  402 */       reinitialize();
/*  403 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public Builder clearField(int number) {
/*  408 */       if (number == 0) {
/*  409 */         throw new IllegalArgumentException("Zero is not a valid field number.");
/*      */       }
/*  411 */       if (this.lastField != null && this.lastFieldNumber == number) {
/*      */         
/*  413 */         this.lastField = null;
/*  414 */         this.lastFieldNumber = 0;
/*      */       } 
/*  416 */       if (this.fields.containsKey(Integer.valueOf(number))) {
/*  417 */         this.fields.remove(Integer.valueOf(number));
/*      */       }
/*  419 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(UnknownFieldSet other) {
/*  427 */       if (other != UnknownFieldSet.getDefaultInstance()) {
/*  428 */         for (Map.Entry<Integer, UnknownFieldSet.Field> entry : (Iterable<Map.Entry<Integer, UnknownFieldSet.Field>>)other.fields.entrySet()) {
/*  429 */           mergeField(((Integer)entry.getKey()).intValue(), entry.getValue());
/*      */         }
/*      */       }
/*  432 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeField(int number, UnknownFieldSet.Field field) {
/*  440 */       if (number == 0) {
/*  441 */         throw new IllegalArgumentException("Zero is not a valid field number.");
/*      */       }
/*  443 */       if (hasField(number)) {
/*  444 */         getFieldBuilder(number).mergeFrom(field);
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  449 */         addField(number, field);
/*      */       } 
/*  451 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeVarintField(int number, int value) {
/*  459 */       if (number == 0) {
/*  460 */         throw new IllegalArgumentException("Zero is not a valid field number.");
/*      */       }
/*  462 */       getFieldBuilder(number).addVarint(value);
/*  463 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeLengthDelimitedField(int number, ByteString value) {
/*  472 */       if (number == 0) {
/*  473 */         throw new IllegalArgumentException("Zero is not a valid field number.");
/*      */       }
/*  475 */       getFieldBuilder(number).addLengthDelimited(value);
/*  476 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasField(int number) {
/*  481 */       if (number == 0) {
/*  482 */         throw new IllegalArgumentException("Zero is not a valid field number.");
/*      */       }
/*  484 */       return (number == this.lastFieldNumber || this.fields.containsKey(Integer.valueOf(number)));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder addField(int number, UnknownFieldSet.Field field) {
/*  492 */       if (number == 0) {
/*  493 */         throw new IllegalArgumentException("Zero is not a valid field number.");
/*      */       }
/*  495 */       if (this.lastField != null && this.lastFieldNumber == number) {
/*      */         
/*  497 */         this.lastField = null;
/*  498 */         this.lastFieldNumber = 0;
/*      */       } 
/*  500 */       if (this.fields.isEmpty()) {
/*  501 */         this.fields = new TreeMap<>();
/*      */       }
/*  503 */       this.fields.put(Integer.valueOf(number), field);
/*  504 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<Integer, UnknownFieldSet.Field> asMap() {
/*  512 */       getFieldBuilder(0);
/*  513 */       return Collections.unmodifiableMap(this.fields);
/*      */     }
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(CodedInputStream input) throws IOException {
/*      */       int tag;
/*      */       do {
/*  520 */         tag = input.readTag();
/*  521 */       } while (tag != 0 && mergeFieldFrom(tag, input));
/*      */ 
/*      */ 
/*      */       
/*  525 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean mergeFieldFrom(int tag, CodedInputStream input) throws IOException {
/*      */       Builder subBuilder;
/*  535 */       int number = WireFormat.getTagFieldNumber(tag);
/*  536 */       switch (WireFormat.getTagWireType(tag)) {
/*      */         case 0:
/*  538 */           getFieldBuilder(number).addVarint(input.readInt64());
/*  539 */           return true;
/*      */         case 1:
/*  541 */           getFieldBuilder(number).addFixed64(input.readFixed64());
/*  542 */           return true;
/*      */         case 2:
/*  544 */           getFieldBuilder(number).addLengthDelimited(input.readBytes());
/*  545 */           return true;
/*      */         case 3:
/*  547 */           subBuilder = UnknownFieldSet.newBuilder();
/*  548 */           input.readGroup(number, subBuilder, ExtensionRegistry.getEmptyRegistry());
/*  549 */           getFieldBuilder(number).addGroup(subBuilder.build());
/*  550 */           return true;
/*      */         case 4:
/*  552 */           return false;
/*      */         case 5:
/*  554 */           getFieldBuilder(number).addFixed32(input.readFixed32());
/*  555 */           return true;
/*      */       } 
/*  557 */       throw InvalidProtocolBufferException.invalidWireType();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(ByteString data) throws InvalidProtocolBufferException {
/*      */       try {
/*  568 */         CodedInputStream input = data.newCodedInput();
/*  569 */         mergeFrom(input);
/*  570 */         input.checkLastTagWas(0);
/*  571 */         return this;
/*  572 */       } catch (InvalidProtocolBufferException e) {
/*  573 */         throw e;
/*  574 */       } catch (IOException e) {
/*  575 */         throw new RuntimeException("Reading from a ByteString threw an IOException (should never happen).", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(byte[] data) throws InvalidProtocolBufferException {
/*      */       try {
/*  587 */         CodedInputStream input = CodedInputStream.newInstance(data);
/*  588 */         mergeFrom(input);
/*  589 */         input.checkLastTagWas(0);
/*  590 */         return this;
/*  591 */       } catch (InvalidProtocolBufferException e) {
/*  592 */         throw e;
/*  593 */       } catch (IOException e) {
/*  594 */         throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(InputStream input) throws IOException {
/*  605 */       CodedInputStream codedInput = CodedInputStream.newInstance(input);
/*  606 */       mergeFrom(codedInput);
/*  607 */       codedInput.checkLastTagWas(0);
/*  608 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean mergeDelimitedFrom(InputStream input) throws IOException {
/*  613 */       int firstByte = input.read();
/*  614 */       if (firstByte == -1) {
/*  615 */         return false;
/*      */       }
/*  617 */       int size = CodedInputStream.readRawVarint32(firstByte, input);
/*  618 */       InputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
/*  619 */       mergeFrom(limitedInput);
/*  620 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  627 */       return mergeDelimitedFrom(input);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  634 */       return mergeFrom(input);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  641 */       return mergeFrom(data);
/*      */     }
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
/*      */       try {
/*  647 */         CodedInputStream input = CodedInputStream.newInstance(data, off, len);
/*  648 */         mergeFrom(input);
/*  649 */         input.checkLastTagWas(0);
/*  650 */         return this;
/*  651 */       } catch (InvalidProtocolBufferException e) {
/*  652 */         throw e;
/*  653 */       } catch (IOException e) {
/*  654 */         throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  663 */       return mergeFrom(data);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/*  670 */       return mergeFrom(data, off, len);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
/*  677 */       return mergeFrom(input);
/*      */     }
/*      */ 
/*      */     
/*      */     public Builder mergeFrom(MessageLite m) {
/*  682 */       if (m instanceof UnknownFieldSet) {
/*  683 */         return mergeFrom((UnknownFieldSet)m);
/*      */       }
/*  685 */       throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isInitialized() {
/*  693 */       return true;
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
/*      */   public static final class Field
/*      */   {
/*      */     private Field() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static Builder newBuilder() {
/*  717 */       return Builder.create();
/*      */     }
/*      */ 
/*      */     
/*      */     public static Builder newBuilder(Field copyFrom) {
/*  722 */       return newBuilder().mergeFrom(copyFrom);
/*      */     }
/*      */ 
/*      */     
/*      */     public static Field getDefaultInstance() {
/*  727 */       return fieldDefaultInstance;
/*      */     }
/*      */     
/*  730 */     private static final Field fieldDefaultInstance = newBuilder().build(); private List<Long> varint;
/*      */     private List<Integer> fixed32;
/*      */     
/*      */     public List<Long> getVarintList() {
/*  734 */       return this.varint;
/*      */     }
/*      */     private List<Long> fixed64; private List<ByteString> lengthDelimited; private List<UnknownFieldSet> group;
/*      */     
/*      */     public List<Integer> getFixed32List() {
/*  739 */       return this.fixed32;
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Long> getFixed64List() {
/*  744 */       return this.fixed64;
/*      */     }
/*      */ 
/*      */     
/*      */     public List<ByteString> getLengthDelimitedList() {
/*  749 */       return this.lengthDelimited;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public List<UnknownFieldSet> getGroupList() {
/*  757 */       return this.group;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object other) {
/*  762 */       if (this == other) {
/*  763 */         return true;
/*      */       }
/*  765 */       if (!(other instanceof Field)) {
/*  766 */         return false;
/*      */       }
/*  768 */       return Arrays.equals(getIdentityArray(), ((Field)other).getIdentityArray());
/*      */     }
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  773 */       return Arrays.hashCode(getIdentityArray());
/*      */     }
/*      */ 
/*      */     
/*      */     private Object[] getIdentityArray() {
/*  778 */       return new Object[] { this.varint, this.fixed32, this.fixed64, this.lengthDelimited, this.group };
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ByteString toByteString(int fieldNumber) {
/*      */       try {
/*  789 */         ByteString.CodedBuilder out = ByteString.newCodedBuilder(getSerializedSize(fieldNumber));
/*  790 */         writeTo(fieldNumber, out.getCodedOutput());
/*  791 */         return out.build();
/*  792 */       } catch (IOException e) {
/*  793 */         throw new RuntimeException("Serializing to a ByteString should never fail with an IOException", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(int fieldNumber, CodedOutputStream output) throws IOException {
/*  800 */       for (null = this.varint.iterator(); null.hasNext(); ) { long value = ((Long)null.next()).longValue();
/*  801 */         output.writeUInt64(fieldNumber, value); }
/*      */       
/*  803 */       for (null = (Iterator)this.fixed32.iterator(); null.hasNext(); ) { int value = ((Integer)null.next()).intValue();
/*  804 */         output.writeFixed32(fieldNumber, value); }
/*      */       
/*  806 */       for (null = this.fixed64.iterator(); null.hasNext(); ) { long value = ((Long)null.next()).longValue();
/*  807 */         output.writeFixed64(fieldNumber, value); }
/*      */       
/*  809 */       for (ByteString value : this.lengthDelimited) {
/*  810 */         output.writeBytes(fieldNumber, value);
/*      */       }
/*  812 */       for (UnknownFieldSet value : this.group) {
/*  813 */         output.writeGroup(fieldNumber, value);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSerializedSize(int fieldNumber) {
/*  819 */       int result = 0;
/*  820 */       for (null = this.varint.iterator(); null.hasNext(); ) { long value = ((Long)null.next()).longValue();
/*  821 */         result += CodedOutputStream.computeUInt64Size(fieldNumber, value); }
/*      */       
/*  823 */       for (null = (Iterator)this.fixed32.iterator(); null.hasNext(); ) { int value = ((Integer)null.next()).intValue();
/*  824 */         result += CodedOutputStream.computeFixed32Size(fieldNumber, value); }
/*      */       
/*  826 */       for (null = this.fixed64.iterator(); null.hasNext(); ) { long value = ((Long)null.next()).longValue();
/*  827 */         result += CodedOutputStream.computeFixed64Size(fieldNumber, value); }
/*      */       
/*  829 */       for (ByteString value : this.lengthDelimited) {
/*  830 */         result += CodedOutputStream.computeBytesSize(fieldNumber, value);
/*      */       }
/*  832 */       for (UnknownFieldSet value : this.group) {
/*  833 */         result += CodedOutputStream.computeGroupSize(fieldNumber, value);
/*      */       }
/*  835 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeAsMessageSetExtensionTo(int fieldNumber, CodedOutputStream output) throws IOException {
/*  844 */       for (ByteString value : this.lengthDelimited) {
/*  845 */         output.writeRawMessageSetExtension(fieldNumber, value);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     void writeTo(int fieldNumber, Writer writer) throws IOException {
/*  851 */       writer.writeInt64List(fieldNumber, this.varint, false);
/*  852 */       writer.writeFixed32List(fieldNumber, this.fixed32, false);
/*  853 */       writer.writeFixed64List(fieldNumber, this.fixed64, false);
/*  854 */       writer.writeBytesList(fieldNumber, this.lengthDelimited);
/*      */       
/*  856 */       if (writer.fieldOrder() == Writer.FieldOrder.ASCENDING) {
/*  857 */         for (int i = 0; i < this.group.size(); i++) {
/*  858 */           writer.writeStartGroup(fieldNumber);
/*  859 */           ((UnknownFieldSet)this.group.get(i)).writeTo(writer);
/*  860 */           writer.writeEndGroup(fieldNumber);
/*      */         } 
/*      */       } else {
/*  863 */         for (int i = this.group.size() - 1; i >= 0; i--) {
/*  864 */           writer.writeEndGroup(fieldNumber);
/*  865 */           ((UnknownFieldSet)this.group.get(i)).writeTo(writer);
/*  866 */           writer.writeStartGroup(fieldNumber);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void writeAsMessageSetExtensionTo(int fieldNumber, Writer writer) throws IOException {
/*  877 */       if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
/*      */         
/*  879 */         ListIterator<ByteString> iter = this.lengthDelimited.listIterator(this.lengthDelimited.size());
/*  880 */         while (iter.hasPrevious()) {
/*  881 */           writer.writeMessageSetItem(fieldNumber, iter.previous());
/*      */         }
/*      */       } else {
/*      */         
/*  885 */         for (ByteString value : this.lengthDelimited) {
/*  886 */           writer.writeMessageSetItem(fieldNumber, value);
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSerializedSizeAsMessageSetExtension(int fieldNumber) {
/*  896 */       int result = 0;
/*  897 */       for (ByteString value : this.lengthDelimited) {
/*  898 */         result += CodedOutputStream.computeRawMessageSetExtensionSize(fieldNumber, value);
/*      */       }
/*  900 */       return result;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final class Builder
/*      */     {
/*      */       private UnknownFieldSet.Field result;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       private static Builder create() {
/*  919 */         Builder builder = new Builder();
/*  920 */         builder.result = new UnknownFieldSet.Field();
/*  921 */         return builder;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public UnknownFieldSet.Field build() {
/*  932 */         if (this.result.varint == null) {
/*  933 */           this.result.varint = Collections.emptyList();
/*      */         } else {
/*  935 */           this.result.varint = Collections.unmodifiableList(this.result.varint);
/*      */         } 
/*  937 */         if (this.result.fixed32 == null) {
/*  938 */           this.result.fixed32 = Collections.emptyList();
/*      */         } else {
/*  940 */           this.result.fixed32 = Collections.unmodifiableList(this.result.fixed32);
/*      */         } 
/*  942 */         if (this.result.fixed64 == null) {
/*  943 */           this.result.fixed64 = Collections.emptyList();
/*      */         } else {
/*  945 */           this.result.fixed64 = Collections.unmodifiableList(this.result.fixed64);
/*      */         } 
/*  947 */         if (this.result.lengthDelimited == null) {
/*  948 */           this.result.lengthDelimited = Collections.emptyList();
/*      */         } else {
/*  950 */           this.result.lengthDelimited = Collections.unmodifiableList(this.result.lengthDelimited);
/*      */         } 
/*  952 */         if (this.result.group == null) {
/*  953 */           this.result.group = Collections.emptyList();
/*      */         } else {
/*  955 */           this.result.group = Collections.unmodifiableList(this.result.group);
/*      */         } 
/*      */         
/*  958 */         UnknownFieldSet.Field returnMe = this.result;
/*  959 */         this.result = null;
/*  960 */         return returnMe;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder clear() {
/*  965 */         this.result = new UnknownFieldSet.Field();
/*  966 */         return this;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Builder mergeFrom(UnknownFieldSet.Field other) {
/*  974 */         if (!other.varint.isEmpty()) {
/*  975 */           if (this.result.varint == null) {
/*  976 */             this.result.varint = new ArrayList();
/*      */           }
/*  978 */           this.result.varint.addAll(other.varint);
/*      */         } 
/*  980 */         if (!other.fixed32.isEmpty()) {
/*  981 */           if (this.result.fixed32 == null) {
/*  982 */             this.result.fixed32 = new ArrayList();
/*      */           }
/*  984 */           this.result.fixed32.addAll(other.fixed32);
/*      */         } 
/*  986 */         if (!other.fixed64.isEmpty()) {
/*  987 */           if (this.result.fixed64 == null) {
/*  988 */             this.result.fixed64 = new ArrayList();
/*      */           }
/*  990 */           this.result.fixed64.addAll(other.fixed64);
/*      */         } 
/*  992 */         if (!other.lengthDelimited.isEmpty()) {
/*  993 */           if (this.result.lengthDelimited == null) {
/*  994 */             this.result.lengthDelimited = new ArrayList();
/*      */           }
/*  996 */           this.result.lengthDelimited.addAll(other.lengthDelimited);
/*      */         } 
/*  998 */         if (!other.group.isEmpty()) {
/*  999 */           if (this.result.group == null) {
/* 1000 */             this.result.group = new ArrayList();
/*      */           }
/* 1002 */           this.result.group.addAll(other.group);
/*      */         } 
/* 1004 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder addVarint(long value) {
/* 1009 */         if (this.result.varint == null) {
/* 1010 */           this.result.varint = new ArrayList();
/*      */         }
/* 1012 */         this.result.varint.add(Long.valueOf(value));
/* 1013 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder addFixed32(int value) {
/* 1018 */         if (this.result.fixed32 == null) {
/* 1019 */           this.result.fixed32 = new ArrayList();
/*      */         }
/* 1021 */         this.result.fixed32.add(Integer.valueOf(value));
/* 1022 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder addFixed64(long value) {
/* 1027 */         if (this.result.fixed64 == null) {
/* 1028 */           this.result.fixed64 = new ArrayList();
/*      */         }
/* 1030 */         this.result.fixed64.add(Long.valueOf(value));
/* 1031 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder addLengthDelimited(ByteString value) {
/* 1036 */         if (this.result.lengthDelimited == null) {
/* 1037 */           this.result.lengthDelimited = new ArrayList();
/*      */         }
/* 1039 */         this.result.lengthDelimited.add(value);
/* 1040 */         return this;
/*      */       }
/*      */ 
/*      */       
/*      */       public Builder addGroup(UnknownFieldSet value) {
/* 1045 */         if (this.result.group == null) {
/* 1046 */           this.result.group = new ArrayList();
/*      */         }
/* 1048 */         this.result.group.add(value);
/* 1049 */         return this;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class Parser
/*      */     extends AbstractParser<UnknownFieldSet>
/*      */   {
/*      */     public UnknownFieldSet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
/* 1060 */       UnknownFieldSet.Builder builder = UnknownFieldSet.newBuilder();
/*      */       try {
/* 1062 */         builder.mergeFrom(input);
/* 1063 */       } catch (InvalidProtocolBufferException e) {
/* 1064 */         throw e.setUnfinishedMessage(builder.buildPartial());
/* 1065 */       } catch (IOException e) {
/* 1066 */         throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(builder.buildPartial());
/*      */       } 
/* 1068 */       return builder.buildPartial();
/*      */     }
/*      */   }
/*      */   
/* 1072 */   private static final Parser PARSER = new Parser();
/*      */ 
/*      */   
/*      */   public final Parser getParserForType() {
/* 1076 */     return PARSER;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\UnknownFieldSet.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */