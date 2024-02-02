/*      */ package com.google.protobuf;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class FieldSet<T extends FieldSet.FieldDescriptorLite<T>>
/*      */ {
/*      */   private static final int DEFAULT_FIELD_MAP_ARRAY_SIZE = 16;
/*      */   private final SmallSortedMap<T, Object> fields;
/*      */   private boolean isImmutable;
/*      */   private boolean hasLazyField;
/*      */   
/*      */   private FieldSet() {
/*   82 */     this.fields = SmallSortedMap.newFieldMap(16);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private FieldSet(boolean dummy) {
/*   88 */     this(SmallSortedMap.newFieldMap(0));
/*   89 */     makeImmutable();
/*      */   }
/*      */   
/*      */   private FieldSet(SmallSortedMap<T, Object> fields) {
/*   93 */     this.fields = fields;
/*   94 */     makeImmutable();
/*      */   }
/*      */ 
/*      */   
/*      */   public static <T extends FieldDescriptorLite<T>> FieldSet<T> newFieldSet() {
/*   99 */     return new FieldSet<>();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends FieldDescriptorLite<T>> FieldSet<T> emptySet() {
/*  105 */     return DEFAULT_INSTANCE;
/*      */   }
/*      */ 
/*      */   
/*      */   public static <T extends FieldDescriptorLite<T>> Builder<T> newBuilder() {
/*  110 */     return new Builder<>();
/*      */   }
/*      */ 
/*      */   
/*  114 */   private static final FieldSet DEFAULT_INSTANCE = new FieldSet(true);
/*      */ 
/*      */   
/*      */   boolean isEmpty() {
/*  118 */     return this.fields.isEmpty();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void makeImmutable() {
/*  124 */     if (this.isImmutable) {
/*      */       return;
/*      */     }
/*  127 */     this.fields.makeImmutable();
/*  128 */     this.isImmutable = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isImmutable() {
/*  138 */     return this.isImmutable;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object o) {
/*  143 */     if (this == o) {
/*  144 */       return true;
/*      */     }
/*      */     
/*  147 */     if (!(o instanceof FieldSet)) {
/*  148 */       return false;
/*      */     }
/*      */     
/*  151 */     FieldSet<?> other = (FieldSet)o;
/*  152 */     return this.fields.equals(other.fields);
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  157 */     return this.fields.hashCode();
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
/*      */   public FieldSet<T> clone() {
/*  170 */     FieldSet<T> clone = newFieldSet();
/*  171 */     for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
/*  172 */       Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
/*  173 */       clone.setField(entry.getKey(), entry.getValue());
/*      */     } 
/*  175 */     for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
/*  176 */       clone.setField(entry.getKey(), entry.getValue());
/*      */     }
/*  178 */     clone.hasLazyField = this.hasLazyField;
/*  179 */     return clone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clear() {
/*  187 */     this.fields.clear();
/*  188 */     this.hasLazyField = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public Map<T, Object> getAllFields() {
/*  193 */     if (this.hasLazyField) {
/*  194 */       SmallSortedMap<T, Object> result = cloneAllFieldsMap(this.fields, false);
/*  195 */       if (this.fields.isImmutable()) {
/*  196 */         result.makeImmutable();
/*      */       }
/*  198 */       return result;
/*      */     } 
/*  200 */     return this.fields.isImmutable() ? this.fields : Collections.<T, Object>unmodifiableMap(this.fields);
/*      */   }
/*      */ 
/*      */   
/*      */   private static <T extends FieldDescriptorLite<T>> SmallSortedMap<T, Object> cloneAllFieldsMap(SmallSortedMap<T, Object> fields, boolean copyList) {
/*  205 */     SmallSortedMap<T, Object> result = SmallSortedMap.newFieldMap(16);
/*  206 */     for (int i = 0; i < fields.getNumArrayEntries(); i++) {
/*  207 */       cloneFieldEntry(result, fields.getArrayEntryAt(i), copyList);
/*      */     }
/*  209 */     for (Map.Entry<T, Object> entry : fields.getOverflowEntries()) {
/*  210 */       cloneFieldEntry(result, entry, copyList);
/*      */     }
/*  212 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   private static <T extends FieldDescriptorLite<T>> void cloneFieldEntry(Map<T, Object> map, Map.Entry<T, Object> entry, boolean copyList) {
/*  217 */     FieldDescriptorLite fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
/*  218 */     Object value = entry.getValue();
/*  219 */     if (value instanceof LazyField) {
/*  220 */       map.put((T)fieldDescriptorLite, ((LazyField)value).getValue());
/*  221 */     } else if (copyList && value instanceof List) {
/*  222 */       map.put((T)fieldDescriptorLite, new ArrayList((List)value));
/*      */     } else {
/*  224 */       map.put((T)fieldDescriptorLite, value);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Iterator<Map.Entry<T, Object>> iterator() {
/*  233 */     if (this.hasLazyField) {
/*  234 */       return new LazyField.LazyIterator<>(this.fields.entrySet().iterator());
/*      */     }
/*  236 */     return this.fields.entrySet().iterator();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Iterator<Map.Entry<T, Object>> descendingIterator() {
/*  245 */     if (this.hasLazyField) {
/*  246 */       return new LazyField.LazyIterator<>(this.fields.descendingEntrySet().iterator());
/*      */     }
/*  248 */     return this.fields.descendingEntrySet().iterator();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasField(T descriptor) {
/*  253 */     if (descriptor.isRepeated()) {
/*  254 */       throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
/*      */     }
/*      */     
/*  257 */     return (this.fields.get(descriptor) != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getField(T descriptor) {
/*  266 */     Object o = this.fields.get(descriptor);
/*  267 */     if (o instanceof LazyField) {
/*  268 */       return ((LazyField)o).getValue();
/*      */     }
/*  270 */     return o;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setField(T descriptor, Object value) {
/*  278 */     if (descriptor.isRepeated()) {
/*  279 */       if (!(value instanceof List)) {
/*  280 */         throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  286 */       List newList = new ArrayList();
/*  287 */       newList.addAll((List)value);
/*  288 */       for (Object element : newList) {
/*  289 */         verifyType(descriptor.getLiteType(), element);
/*      */       }
/*  291 */       value = newList;
/*      */     } else {
/*  293 */       verifyType(descriptor.getLiteType(), value);
/*      */     } 
/*      */     
/*  296 */     if (value instanceof LazyField) {
/*  297 */       this.hasLazyField = true;
/*      */     }
/*  299 */     this.fields.put(descriptor, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public void clearField(T descriptor) {
/*  304 */     this.fields.remove(descriptor);
/*  305 */     if (this.fields.isEmpty()) {
/*  306 */       this.hasLazyField = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public int getRepeatedFieldCount(T descriptor) {
/*  312 */     if (!descriptor.isRepeated()) {
/*  313 */       throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
/*      */     }
/*      */ 
/*      */     
/*  317 */     Object value = getField(descriptor);
/*  318 */     if (value == null) {
/*  319 */       return 0;
/*      */     }
/*  321 */     return ((List)value).size();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getRepeatedField(T descriptor, int index) {
/*  327 */     if (!descriptor.isRepeated()) {
/*  328 */       throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
/*      */     }
/*      */ 
/*      */     
/*  332 */     Object value = getField(descriptor);
/*      */     
/*  334 */     if (value == null) {
/*  335 */       throw new IndexOutOfBoundsException();
/*      */     }
/*  337 */     return ((List)value).get(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRepeatedField(T descriptor, int index, Object value) {
/*  347 */     if (!descriptor.isRepeated()) {
/*  348 */       throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
/*      */     }
/*      */ 
/*      */     
/*  352 */     Object list = getField(descriptor);
/*  353 */     if (list == null) {
/*  354 */       throw new IndexOutOfBoundsException();
/*      */     }
/*      */     
/*  357 */     verifyType(descriptor.getLiteType(), value);
/*  358 */     ((List<Object>)list).set(index, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addRepeatedField(T descriptor, Object value) {
/*      */     List<Object> list;
/*  367 */     if (!descriptor.isRepeated()) {
/*  368 */       throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
/*      */     }
/*      */ 
/*      */     
/*  372 */     verifyType(descriptor.getLiteType(), value);
/*      */     
/*  374 */     Object existingValue = getField(descriptor);
/*      */     
/*  376 */     if (existingValue == null) {
/*  377 */       list = new ArrayList();
/*  378 */       this.fields.put(descriptor, list);
/*      */     } else {
/*  380 */       list = (List<Object>)existingValue;
/*      */     } 
/*      */     
/*  383 */     list.add(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void verifyType(WireFormat.FieldType type, Object value) {
/*  394 */     if (!isValidType(type, value))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  402 */       throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean isValidType(WireFormat.FieldType type, Object value) {
/*  408 */     Internal.checkNotNull(value);
/*  409 */     switch (type.getJavaType()) {
/*      */       case DOUBLE:
/*  411 */         return value instanceof Integer;
/*      */       case FLOAT:
/*  413 */         return value instanceof Long;
/*      */       case INT64:
/*  415 */         return value instanceof Float;
/*      */       case UINT64:
/*  417 */         return value instanceof Double;
/*      */       case INT32:
/*  419 */         return value instanceof Boolean;
/*      */       case FIXED64:
/*  421 */         return value instanceof String;
/*      */       case FIXED32:
/*  423 */         return (value instanceof ByteString || value instanceof byte[]);
/*      */       
/*      */       case BOOL:
/*  426 */         return (value instanceof Integer || value instanceof Internal.EnumLite);
/*      */       
/*      */       case GROUP:
/*  429 */         return (value instanceof MessageLite || value instanceof LazyField);
/*      */     } 
/*  431 */     return false;
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
/*      */   public boolean isInitialized() {
/*  443 */     for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
/*  444 */       if (!isInitialized(this.fields.getArrayEntryAt(i))) {
/*  445 */         return false;
/*      */       }
/*      */     } 
/*  448 */     for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
/*  449 */       if (!isInitialized(entry)) {
/*  450 */         return false;
/*      */       }
/*      */     } 
/*  453 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static <T extends FieldDescriptorLite<T>> boolean isInitialized(Map.Entry<T, Object> entry) {
/*  459 */     FieldDescriptorLite fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
/*  460 */     if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
/*  461 */       if (fieldDescriptorLite.isRepeated()) {
/*  462 */         for (MessageLite element : entry.getValue()) {
/*  463 */           if (!element.isInitialized()) {
/*  464 */             return false;
/*      */           }
/*      */         } 
/*      */       } else {
/*  468 */         Object value = entry.getValue();
/*  469 */         if (value instanceof MessageLite) {
/*  470 */           if (!((MessageLite)value).isInitialized())
/*  471 */             return false; 
/*      */         } else {
/*  473 */           if (value instanceof LazyField) {
/*  474 */             return true;
/*      */           }
/*  476 */           throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  481 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int getWireFormatForFieldType(WireFormat.FieldType type, boolean isPacked) {
/*  490 */     if (isPacked) {
/*  491 */       return 2;
/*      */     }
/*  493 */     return type.getWireType();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void mergeFrom(FieldSet<T> other) {
/*  499 */     for (int i = 0; i < other.fields.getNumArrayEntries(); i++) {
/*  500 */       mergeFromField(other.fields.getArrayEntryAt(i));
/*      */     }
/*  502 */     for (Map.Entry<T, Object> entry : other.fields.getOverflowEntries()) {
/*  503 */       mergeFromField(entry);
/*      */     }
/*      */   }
/*      */   
/*      */   private static Object cloneIfMutable(Object value) {
/*  508 */     if (value instanceof byte[]) {
/*  509 */       byte[] bytes = (byte[])value;
/*  510 */       byte[] copy = new byte[bytes.length];
/*  511 */       System.arraycopy(bytes, 0, copy, 0, bytes.length);
/*  512 */       return copy;
/*      */     } 
/*  514 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void mergeFromField(Map.Entry<T, Object> entry) {
/*  520 */     FieldDescriptorLite fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
/*  521 */     Object otherValue = entry.getValue();
/*  522 */     if (otherValue instanceof LazyField) {
/*  523 */       otherValue = ((LazyField)otherValue).getValue();
/*      */     }
/*      */     
/*  526 */     if (fieldDescriptorLite.isRepeated()) {
/*  527 */       Object value = getField((T)fieldDescriptorLite);
/*  528 */       if (value == null) {
/*  529 */         value = new ArrayList();
/*      */       }
/*  531 */       for (Object element : otherValue) {
/*  532 */         ((List<Object>)value).add(cloneIfMutable(element));
/*      */       }
/*  534 */       this.fields.put((T)fieldDescriptorLite, value);
/*  535 */     } else if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
/*  536 */       Object value = getField((T)fieldDescriptorLite);
/*  537 */       if (value == null) {
/*  538 */         this.fields.put((T)fieldDescriptorLite, cloneIfMutable(otherValue));
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/*  544 */         value = fieldDescriptorLite.internalMergeFrom(((MessageLite)value).toBuilder(), (MessageLite)otherValue).build();
/*  545 */         this.fields.put((T)fieldDescriptorLite, value);
/*      */       } 
/*      */     } else {
/*  548 */       this.fields.put((T)fieldDescriptorLite, cloneIfMutable(otherValue));
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
/*      */ 
/*      */   
/*      */   public static Object readPrimitiveField(CodedInputStream input, WireFormat.FieldType type, boolean checkUtf8) throws IOException {
/*  568 */     if (checkUtf8) {
/*  569 */       return WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.STRICT);
/*      */     }
/*  571 */     return WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.LOOSE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeTo(CodedOutputStream output) throws IOException {
/*  578 */     for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
/*  579 */       Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
/*  580 */       writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output);
/*      */     } 
/*  582 */     for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
/*  583 */       writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeMessageSetTo(CodedOutputStream output) throws IOException {
/*  589 */     for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
/*  590 */       writeMessageSetTo(this.fields.getArrayEntryAt(i), output);
/*      */     }
/*  592 */     for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
/*  593 */       writeMessageSetTo(entry, output);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeMessageSetTo(Map.Entry<T, Object> entry, CodedOutputStream output) throws IOException {
/*  599 */     FieldDescriptorLite<?> fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
/*  600 */     if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE && 
/*  601 */       !fieldDescriptorLite.isRepeated() && 
/*  602 */       !fieldDescriptorLite.isPacked()) {
/*  603 */       Object value = entry.getValue();
/*  604 */       if (value instanceof LazyField) {
/*  605 */         value = ((LazyField)value).getValue();
/*      */       }
/*  607 */       output.writeMessageSetExtension(((FieldDescriptorLite)entry.getKey()).getNumber(), (MessageLite)value);
/*      */     } else {
/*  609 */       writeField(fieldDescriptorLite, entry.getValue(), output);
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
/*      */ 
/*      */ 
/*      */   
/*      */   static void writeElement(CodedOutputStream output, WireFormat.FieldType type, int number, Object value) throws IOException {
/*  630 */     if (type == WireFormat.FieldType.GROUP) {
/*  631 */       output.writeGroup(number, (MessageLite)value);
/*      */     } else {
/*  633 */       output.writeTag(number, getWireFormatForFieldType(type, false));
/*  634 */       writeElementNoTag(output, type, value);
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
/*      */   static void writeElementNoTag(CodedOutputStream output, WireFormat.FieldType type, Object value) throws IOException {
/*  649 */     switch (type) {
/*      */       case DOUBLE:
/*  651 */         output.writeDoubleNoTag(((Double)value).doubleValue());
/*      */         break;
/*      */       case FLOAT:
/*  654 */         output.writeFloatNoTag(((Float)value).floatValue());
/*      */         break;
/*      */       case INT64:
/*  657 */         output.writeInt64NoTag(((Long)value).longValue());
/*      */         break;
/*      */       case UINT64:
/*  660 */         output.writeUInt64NoTag(((Long)value).longValue());
/*      */         break;
/*      */       case INT32:
/*  663 */         output.writeInt32NoTag(((Integer)value).intValue());
/*      */         break;
/*      */       case FIXED64:
/*  666 */         output.writeFixed64NoTag(((Long)value).longValue());
/*      */         break;
/*      */       case FIXED32:
/*  669 */         output.writeFixed32NoTag(((Integer)value).intValue());
/*      */         break;
/*      */       case BOOL:
/*  672 */         output.writeBoolNoTag(((Boolean)value).booleanValue());
/*      */         break;
/*      */       case GROUP:
/*  675 */         output.writeGroupNoTag((MessageLite)value);
/*      */         break;
/*      */       case MESSAGE:
/*  678 */         output.writeMessageNoTag((MessageLite)value);
/*      */         break;
/*      */       case STRING:
/*  681 */         if (value instanceof ByteString) {
/*  682 */           output.writeBytesNoTag((ByteString)value); break;
/*      */         } 
/*  684 */         output.writeStringNoTag((String)value);
/*      */         break;
/*      */       
/*      */       case BYTES:
/*  688 */         if (value instanceof ByteString) {
/*  689 */           output.writeBytesNoTag((ByteString)value); break;
/*      */         } 
/*  691 */         output.writeByteArrayNoTag((byte[])value);
/*      */         break;
/*      */       
/*      */       case UINT32:
/*  695 */         output.writeUInt32NoTag(((Integer)value).intValue());
/*      */         break;
/*      */       case SFIXED32:
/*  698 */         output.writeSFixed32NoTag(((Integer)value).intValue());
/*      */         break;
/*      */       case SFIXED64:
/*  701 */         output.writeSFixed64NoTag(((Long)value).longValue());
/*      */         break;
/*      */       case SINT32:
/*  704 */         output.writeSInt32NoTag(((Integer)value).intValue());
/*      */         break;
/*      */       case SINT64:
/*  707 */         output.writeSInt64NoTag(((Long)value).longValue());
/*      */         break;
/*      */       
/*      */       case ENUM:
/*  711 */         if (value instanceof Internal.EnumLite) {
/*  712 */           output.writeEnumNoTag(((Internal.EnumLite)value).getNumber()); break;
/*      */         } 
/*  714 */         output.writeEnumNoTag(((Integer)value).intValue());
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void writeField(FieldDescriptorLite<?> descriptor, Object value, CodedOutputStream output) throws IOException {
/*  724 */     WireFormat.FieldType type = descriptor.getLiteType();
/*  725 */     int number = descriptor.getNumber();
/*  726 */     if (descriptor.isRepeated()) {
/*  727 */       List<?> valueList = (List)value;
/*  728 */       if (descriptor.isPacked()) {
/*  729 */         output.writeTag(number, 2);
/*      */         
/*  731 */         int dataSize = 0;
/*  732 */         for (Object element : valueList) {
/*  733 */           dataSize += computeElementSizeNoTag(type, element);
/*      */         }
/*  735 */         output.writeRawVarint32(dataSize);
/*      */         
/*  737 */         for (Object element : valueList) {
/*  738 */           writeElementNoTag(output, type, element);
/*      */         }
/*      */       } else {
/*  741 */         for (Object element : valueList) {
/*  742 */           writeElement(output, type, number, element);
/*      */         }
/*      */       }
/*      */     
/*  746 */     } else if (value instanceof LazyField) {
/*  747 */       writeElement(output, type, number, ((LazyField)value).getValue());
/*      */     } else {
/*  749 */       writeElement(output, type, number, value);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSerializedSize() {
/*  759 */     int size = 0;
/*  760 */     for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
/*  761 */       Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
/*  762 */       size += computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue());
/*      */     } 
/*  764 */     for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
/*  765 */       size += computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue());
/*      */     }
/*  767 */     return size;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMessageSetSerializedSize() {
/*  772 */     int size = 0;
/*  773 */     for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
/*  774 */       size += getMessageSetSerializedSize(this.fields.getArrayEntryAt(i));
/*      */     }
/*  776 */     for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
/*  777 */       size += getMessageSetSerializedSize(entry);
/*      */     }
/*  779 */     return size;
/*      */   }
/*      */   
/*      */   private int getMessageSetSerializedSize(Map.Entry<T, Object> entry) {
/*  783 */     FieldDescriptorLite<?> fieldDescriptorLite = (FieldDescriptorLite)entry.getKey();
/*  784 */     Object value = entry.getValue();
/*  785 */     if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE && 
/*  786 */       !fieldDescriptorLite.isRepeated() && 
/*  787 */       !fieldDescriptorLite.isPacked()) {
/*  788 */       if (value instanceof LazyField) {
/*  789 */         return CodedOutputStream.computeLazyFieldMessageSetExtensionSize(((FieldDescriptorLite)entry
/*  790 */             .getKey()).getNumber(), (LazyField)value);
/*      */       }
/*  792 */       return CodedOutputStream.computeMessageSetExtensionSize(((FieldDescriptorLite)entry
/*  793 */           .getKey()).getNumber(), (MessageLite)value);
/*      */     } 
/*      */     
/*  796 */     return computeFieldSize(fieldDescriptorLite, value);
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
/*      */   static int computeElementSize(WireFormat.FieldType type, int number, Object value) {
/*  811 */     int tagSize = CodedOutputStream.computeTagSize(number);
/*  812 */     if (type == WireFormat.FieldType.GROUP)
/*      */     {
/*      */       
/*  815 */       tagSize *= 2;
/*      */     }
/*  817 */     return tagSize + computeElementSizeNoTag(type, value);
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
/*      */   static int computeElementSizeNoTag(WireFormat.FieldType type, Object value) {
/*  829 */     switch (type) {
/*      */ 
/*      */       
/*      */       case DOUBLE:
/*  833 */         return CodedOutputStream.computeDoubleSizeNoTag(((Double)value).doubleValue());
/*      */       case FLOAT:
/*  835 */         return CodedOutputStream.computeFloatSizeNoTag(((Float)value).floatValue());
/*      */       case INT64:
/*  837 */         return CodedOutputStream.computeInt64SizeNoTag(((Long)value).longValue());
/*      */       case UINT64:
/*  839 */         return CodedOutputStream.computeUInt64SizeNoTag(((Long)value).longValue());
/*      */       case INT32:
/*  841 */         return CodedOutputStream.computeInt32SizeNoTag(((Integer)value).intValue());
/*      */       case FIXED64:
/*  843 */         return CodedOutputStream.computeFixed64SizeNoTag(((Long)value).longValue());
/*      */       case FIXED32:
/*  845 */         return CodedOutputStream.computeFixed32SizeNoTag(((Integer)value).intValue());
/*      */       case BOOL:
/*  847 */         return CodedOutputStream.computeBoolSizeNoTag(((Boolean)value).booleanValue());
/*      */       case GROUP:
/*  849 */         return CodedOutputStream.computeGroupSizeNoTag((MessageLite)value);
/*      */       case BYTES:
/*  851 */         if (value instanceof ByteString) {
/*  852 */           return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
/*      */         }
/*  854 */         return CodedOutputStream.computeByteArraySizeNoTag((byte[])value);
/*      */       
/*      */       case STRING:
/*  857 */         if (value instanceof ByteString) {
/*  858 */           return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
/*      */         }
/*  860 */         return CodedOutputStream.computeStringSizeNoTag((String)value);
/*      */       
/*      */       case UINT32:
/*  863 */         return CodedOutputStream.computeUInt32SizeNoTag(((Integer)value).intValue());
/*      */       case SFIXED32:
/*  865 */         return CodedOutputStream.computeSFixed32SizeNoTag(((Integer)value).intValue());
/*      */       case SFIXED64:
/*  867 */         return CodedOutputStream.computeSFixed64SizeNoTag(((Long)value).longValue());
/*      */       case SINT32:
/*  869 */         return CodedOutputStream.computeSInt32SizeNoTag(((Integer)value).intValue());
/*      */       case SINT64:
/*  871 */         return CodedOutputStream.computeSInt64SizeNoTag(((Long)value).longValue());
/*      */       
/*      */       case MESSAGE:
/*  874 */         if (value instanceof LazyField) {
/*  875 */           return CodedOutputStream.computeLazyFieldSizeNoTag((LazyField)value);
/*      */         }
/*  877 */         return CodedOutputStream.computeMessageSizeNoTag((MessageLite)value);
/*      */ 
/*      */       
/*      */       case ENUM:
/*  881 */         if (value instanceof Internal.EnumLite) {
/*  882 */           return CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite)value).getNumber());
/*      */         }
/*  884 */         return CodedOutputStream.computeEnumSizeNoTag(((Integer)value).intValue());
/*      */     } 
/*      */ 
/*      */     
/*  888 */     throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
/*      */   }
/*      */ 
/*      */   
/*      */   public static int computeFieldSize(FieldDescriptorLite<?> descriptor, Object value) {
/*  893 */     WireFormat.FieldType type = descriptor.getLiteType();
/*  894 */     int number = descriptor.getNumber();
/*  895 */     if (descriptor.isRepeated()) {
/*  896 */       if (descriptor.isPacked()) {
/*  897 */         int dataSize = 0;
/*  898 */         for (Object element : value) {
/*  899 */           dataSize += computeElementSizeNoTag(type, element);
/*      */         }
/*  901 */         return dataSize + 
/*  902 */           CodedOutputStream.computeTagSize(number) + 
/*  903 */           CodedOutputStream.computeRawVarint32Size(dataSize);
/*      */       } 
/*  905 */       int size = 0;
/*  906 */       for (Object element : value) {
/*  907 */         size += computeElementSize(type, number, element);
/*      */       }
/*  909 */       return size;
/*      */     } 
/*      */     
/*  912 */     return computeElementSize(type, number, value);
/*      */   }
/*      */ 
/*      */   
/*      */   static final class Builder<T extends FieldDescriptorLite<T>>
/*      */   {
/*      */     private SmallSortedMap<T, Object> fields;
/*      */     
/*      */     private boolean hasLazyField;
/*      */     
/*      */     private boolean isMutable;
/*      */     
/*      */     private boolean hasNestedBuilders;
/*      */ 
/*      */     
/*      */     private Builder() {
/*  928 */       this(SmallSortedMap.newFieldMap(16));
/*      */     }
/*      */     
/*      */     private Builder(SmallSortedMap<T, Object> fields) {
/*  932 */       this.fields = fields;
/*  933 */       this.isMutable = true;
/*      */     }
/*      */ 
/*      */     
/*      */     public FieldSet<T> build() {
/*  938 */       if (this.fields.isEmpty()) {
/*  939 */         return FieldSet.emptySet();
/*      */       }
/*  941 */       this.isMutable = false;
/*  942 */       SmallSortedMap<T, Object> fieldsForBuild = this.fields;
/*  943 */       if (this.hasNestedBuilders) {
/*      */         
/*  945 */         fieldsForBuild = FieldSet.cloneAllFieldsMap(this.fields, false);
/*  946 */         replaceBuilders(fieldsForBuild);
/*      */       } 
/*  948 */       FieldSet<T> fieldSet = new FieldSet<>(fieldsForBuild);
/*  949 */       fieldSet.hasLazyField = this.hasLazyField;
/*  950 */       return fieldSet;
/*      */     }
/*      */ 
/*      */     
/*      */     private static <T extends FieldSet.FieldDescriptorLite<T>> void replaceBuilders(SmallSortedMap<T, Object> fieldMap) {
/*  955 */       for (int i = 0; i < fieldMap.getNumArrayEntries(); i++) {
/*  956 */         replaceBuilders(fieldMap.getArrayEntryAt(i));
/*      */       }
/*  958 */       for (Map.Entry<T, Object> entry : fieldMap.getOverflowEntries()) {
/*  959 */         replaceBuilders(entry);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private static <T extends FieldSet.FieldDescriptorLite<T>> void replaceBuilders(Map.Entry<T, Object> entry) {
/*  965 */       entry.setValue(replaceBuilders((FieldSet.FieldDescriptorLite)entry.getKey(), entry.getValue()));
/*      */     }
/*      */ 
/*      */     
/*      */     private static <T extends FieldSet.FieldDescriptorLite<T>> Object replaceBuilders(T descriptor, Object value) {
/*  970 */       if (value == null) {
/*  971 */         return value;
/*      */       }
/*  973 */       if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
/*  974 */         if (descriptor.isRepeated()) {
/*  975 */           if (!(value instanceof List)) {
/*  976 */             throw new IllegalStateException("Repeated field should contains a List but actually contains type: " + value
/*      */                 
/*  978 */                 .getClass());
/*      */           }
/*      */           
/*  981 */           List<Object> list = (List<Object>)value;
/*  982 */           for (int i = 0; i < list.size(); i++) {
/*  983 */             Object oldElement = list.get(i);
/*  984 */             Object newElement = replaceBuilder(oldElement);
/*  985 */             if (newElement != oldElement) {
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  990 */               if (list == value) {
/*  991 */                 list = new ArrayList(list);
/*      */               }
/*  993 */               list.set(i, newElement);
/*      */             } 
/*      */           } 
/*  996 */           return list;
/*      */         } 
/*  998 */         return replaceBuilder(value);
/*      */       } 
/*      */       
/* 1001 */       return value;
/*      */     }
/*      */     
/*      */     private static Object replaceBuilder(Object value) {
/* 1005 */       return (value instanceof MessageLite.Builder) ? ((MessageLite.Builder)value).build() : value;
/*      */     }
/*      */ 
/*      */     
/*      */     public static <T extends FieldSet.FieldDescriptorLite<T>> Builder<T> fromFieldSet(FieldSet<T> fieldSet) {
/* 1010 */       Builder<T> builder = new Builder<>(FieldSet.cloneAllFieldsMap(fieldSet.fields, true));
/* 1011 */       builder.hasLazyField = fieldSet.hasLazyField;
/* 1012 */       return builder;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<T, Object> getAllFields() {
/* 1019 */       if (this.hasLazyField) {
/* 1020 */         SmallSortedMap<T, Object> result = FieldSet.cloneAllFieldsMap(this.fields, false);
/* 1021 */         if (this.fields.isImmutable()) {
/* 1022 */           result.makeImmutable();
/*      */         } else {
/* 1024 */           replaceBuilders(result);
/*      */         } 
/* 1026 */         return result;
/*      */       } 
/* 1028 */       return this.fields.isImmutable() ? this.fields : Collections.<T, Object>unmodifiableMap(this.fields);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean hasField(T descriptor) {
/* 1033 */       if (descriptor.isRepeated()) {
/* 1034 */         throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
/*      */       }
/*      */       
/* 1037 */       return (this.fields.get(descriptor) != null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getField(T descriptor) {
/* 1046 */       Object value = getFieldAllowBuilders(descriptor);
/* 1047 */       return replaceBuilders(descriptor, value);
/*      */     }
/*      */ 
/*      */     
/*      */     Object getFieldAllowBuilders(T descriptor) {
/* 1052 */       Object o = this.fields.get(descriptor);
/* 1053 */       if (o instanceof LazyField) {
/* 1054 */         return ((LazyField)o).getValue();
/*      */       }
/* 1056 */       return o;
/*      */     }
/*      */     
/*      */     private void ensureIsMutable() {
/* 1060 */       if (!this.isMutable) {
/* 1061 */         this.fields = FieldSet.cloneAllFieldsMap(this.fields, true);
/* 1062 */         this.isMutable = true;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setField(T descriptor, Object value) {
/* 1072 */       ensureIsMutable();
/* 1073 */       if (descriptor.isRepeated()) {
/* 1074 */         if (!(value instanceof List)) {
/* 1075 */           throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1081 */         List newList = new ArrayList();
/* 1082 */         newList.addAll((List)value);
/* 1083 */         for (Object element : newList) {
/* 1084 */           verifyType(descriptor.getLiteType(), element);
/* 1085 */           this.hasNestedBuilders = (this.hasNestedBuilders || element instanceof MessageLite.Builder);
/*      */         } 
/* 1087 */         value = newList;
/*      */       } else {
/* 1089 */         verifyType(descriptor.getLiteType(), value);
/*      */       } 
/*      */       
/* 1092 */       if (value instanceof LazyField) {
/* 1093 */         this.hasLazyField = true;
/*      */       }
/* 1095 */       this.hasNestedBuilders = (this.hasNestedBuilders || value instanceof MessageLite.Builder);
/*      */       
/* 1097 */       this.fields.put(descriptor, value);
/*      */     }
/*      */ 
/*      */     
/*      */     public void clearField(T descriptor) {
/* 1102 */       ensureIsMutable();
/* 1103 */       this.fields.remove(descriptor);
/* 1104 */       if (this.fields.isEmpty()) {
/* 1105 */         this.hasLazyField = false;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getRepeatedFieldCount(T descriptor) {
/* 1113 */       if (!descriptor.isRepeated()) {
/* 1114 */         throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
/*      */       }
/*      */ 
/*      */       
/* 1118 */       Object value = getField(descriptor);
/* 1119 */       if (value == null) {
/* 1120 */         return 0;
/*      */       }
/* 1122 */       return ((List)value).size();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getRepeatedField(T descriptor, int index) {
/* 1130 */       if (this.hasNestedBuilders) {
/* 1131 */         ensureIsMutable();
/*      */       }
/* 1133 */       Object value = getRepeatedFieldAllowBuilders(descriptor, index);
/* 1134 */       return replaceBuilder(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Object getRepeatedFieldAllowBuilders(T descriptor, int index) {
/* 1142 */       if (!descriptor.isRepeated()) {
/* 1143 */         throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
/*      */       }
/*      */ 
/*      */       
/* 1147 */       Object value = getFieldAllowBuilders(descriptor);
/*      */       
/* 1149 */       if (value == null) {
/* 1150 */         throw new IndexOutOfBoundsException();
/*      */       }
/* 1152 */       return ((List)value).get(index);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setRepeatedField(T descriptor, int index, Object value) {
/* 1162 */       ensureIsMutable();
/* 1163 */       if (!descriptor.isRepeated()) {
/* 1164 */         throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
/*      */       }
/*      */ 
/*      */       
/* 1168 */       this.hasNestedBuilders = (this.hasNestedBuilders || value instanceof MessageLite.Builder);
/*      */       
/* 1170 */       Object list = getField(descriptor);
/* 1171 */       if (list == null) {
/* 1172 */         throw new IndexOutOfBoundsException();
/*      */       }
/*      */       
/* 1175 */       verifyType(descriptor.getLiteType(), value);
/* 1176 */       ((List<Object>)list).set(index, value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void addRepeatedField(T descriptor, Object value) {
/*      */       List<Object> list;
/* 1185 */       ensureIsMutable();
/* 1186 */       if (!descriptor.isRepeated()) {
/* 1187 */         throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
/*      */       }
/*      */ 
/*      */       
/* 1191 */       this.hasNestedBuilders = (this.hasNestedBuilders || value instanceof MessageLite.Builder);
/*      */       
/* 1193 */       verifyType(descriptor.getLiteType(), value);
/*      */       
/* 1195 */       Object existingValue = getField(descriptor);
/*      */       
/* 1197 */       if (existingValue == null) {
/* 1198 */         list = new ArrayList();
/* 1199 */         this.fields.put(descriptor, list);
/*      */       } else {
/* 1201 */         list = (List<Object>)existingValue;
/*      */       } 
/*      */       
/* 1204 */       list.add(value);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private static void verifyType(WireFormat.FieldType type, Object value) {
/* 1215 */       if (!FieldSet.isValidType(type, value)) {
/*      */         
/* 1217 */         if (type.getJavaType() == WireFormat.JavaType.MESSAGE && value instanceof MessageLite.Builder) {
/*      */           return;
/*      */         }
/*      */         
/* 1221 */         throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isInitialized() {
/* 1232 */       for (int i = 0; i < this.fields.getNumArrayEntries(); i++) {
/* 1233 */         if (!FieldSet.isInitialized(this.fields.getArrayEntryAt(i))) {
/* 1234 */           return false;
/*      */         }
/*      */       } 
/* 1237 */       for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
/* 1238 */         if (!FieldSet.isInitialized(entry)) {
/* 1239 */           return false;
/*      */         }
/*      */       } 
/* 1242 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void mergeFrom(FieldSet<T> other) {
/* 1249 */       ensureIsMutable();
/* 1250 */       for (int i = 0; i < other.fields.getNumArrayEntries(); i++) {
/* 1251 */         mergeFromField(other.fields.getArrayEntryAt(i));
/*      */       }
/* 1253 */       for (Map.Entry<T, Object> entry : (Iterable<Map.Entry<T, Object>>)other.fields.getOverflowEntries()) {
/* 1254 */         mergeFromField(entry);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private void mergeFromField(Map.Entry<T, Object> entry) {
/* 1260 */       FieldSet.FieldDescriptorLite fieldDescriptorLite = (FieldSet.FieldDescriptorLite)entry.getKey();
/* 1261 */       Object otherValue = entry.getValue();
/* 1262 */       if (otherValue instanceof LazyField) {
/* 1263 */         otherValue = ((LazyField)otherValue).getValue();
/*      */       }
/*      */       
/* 1266 */       if (fieldDescriptorLite.isRepeated()) {
/* 1267 */         Object value = getField((T)fieldDescriptorLite);
/* 1268 */         if (value == null) {
/* 1269 */           value = new ArrayList();
/*      */         }
/* 1271 */         for (Object element : otherValue) {
/* 1272 */           ((List<Object>)value).add(FieldSet.cloneIfMutable(element));
/*      */         }
/* 1274 */         this.fields.put((T)fieldDescriptorLite, value);
/* 1275 */       } else if (fieldDescriptorLite.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
/* 1276 */         Object value = getField((T)fieldDescriptorLite);
/* 1277 */         if (value == null) {
/* 1278 */           this.fields.put((T)fieldDescriptorLite, FieldSet.cloneIfMutable(otherValue));
/*      */         
/*      */         }
/* 1281 */         else if (value instanceof MessageLite.Builder) {
/* 1282 */           fieldDescriptorLite.internalMergeFrom((MessageLite.Builder)value, (MessageLite)otherValue);
/*      */         
/*      */         }
/*      */         else {
/*      */           
/* 1287 */           value = fieldDescriptorLite.internalMergeFrom(((MessageLite)value).toBuilder(), (MessageLite)otherValue).build();
/* 1288 */           this.fields.put((T)fieldDescriptorLite, value);
/*      */         } 
/*      */       } else {
/*      */         
/* 1292 */         this.fields.put((T)fieldDescriptorLite, FieldSet.cloneIfMutable(otherValue));
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface FieldDescriptorLite<T extends FieldDescriptorLite<T>> extends Comparable<T> {
/*      */     int getNumber();
/*      */     
/*      */     WireFormat.FieldType getLiteType();
/*      */     
/*      */     WireFormat.JavaType getLiteJavaType();
/*      */     
/*      */     boolean isRepeated();
/*      */     
/*      */     boolean isPacked();
/*      */     
/*      */     Internal.EnumLiteMap<?> getEnumType();
/*      */     
/*      */     MessageLite.Builder internalMergeFrom(MessageLite.Builder param1Builder, MessageLite param1MessageLite);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\FieldSet.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */