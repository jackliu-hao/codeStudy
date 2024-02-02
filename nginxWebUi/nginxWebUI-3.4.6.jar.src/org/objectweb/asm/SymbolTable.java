/*      */ package org.objectweb.asm;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class SymbolTable
/*      */ {
/*      */   final ClassWriter classWriter;
/*      */   private final ClassReader sourceClassReader;
/*      */   private int majorVersion;
/*      */   private String className;
/*      */   private int entryCount;
/*      */   private Entry[] entries;
/*      */   private int constantPoolCount;
/*      */   private ByteVector constantPool;
/*      */   private int bootstrapMethodCount;
/*      */   private ByteVector bootstrapMethods;
/*      */   private int typeCount;
/*      */   private Entry[] typeTable;
/*      */   
/*      */   SymbolTable(ClassWriter classWriter) {
/*  122 */     this.classWriter = classWriter;
/*  123 */     this.sourceClassReader = null;
/*  124 */     this.entries = new Entry[256];
/*  125 */     this.constantPoolCount = 1;
/*  126 */     this.constantPool = new ByteVector();
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
/*      */   SymbolTable(ClassWriter classWriter, ClassReader classReader) {
/*  138 */     this.classWriter = classWriter;
/*  139 */     this.sourceClassReader = classReader;
/*      */ 
/*      */     
/*  142 */     byte[] inputBytes = classReader.classFileBuffer;
/*  143 */     int constantPoolOffset = classReader.getItem(1) - 1;
/*  144 */     int constantPoolLength = classReader.header - constantPoolOffset;
/*  145 */     this.constantPoolCount = classReader.getItemCount();
/*  146 */     this.constantPool = new ByteVector(constantPoolLength);
/*  147 */     this.constantPool.putByteArray(inputBytes, constantPoolOffset, constantPoolLength);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  152 */     this.entries = new Entry[this.constantPoolCount * 2];
/*  153 */     char[] charBuffer = new char[classReader.getMaxStringLength()];
/*  154 */     boolean hasBootstrapMethods = false;
/*  155 */     int itemIndex = 1;
/*  156 */     while (itemIndex < this.constantPoolCount) {
/*  157 */       int nameAndTypeItemOffset, memberRefItemOffset, itemOffset = classReader.getItem(itemIndex);
/*  158 */       int itemTag = inputBytes[itemOffset - 1];
/*      */       
/*  160 */       switch (itemTag) {
/*      */         
/*      */         case 9:
/*      */         case 10:
/*      */         case 11:
/*  165 */           nameAndTypeItemOffset = classReader.getItem(classReader.readUnsignedShort(itemOffset + 2));
/*  166 */           addConstantMemberReference(itemIndex, itemTag, classReader
/*      */ 
/*      */               
/*  169 */               .readClass(itemOffset, charBuffer), classReader
/*  170 */               .readUTF8(nameAndTypeItemOffset, charBuffer), classReader
/*  171 */               .readUTF8(nameAndTypeItemOffset + 2, charBuffer));
/*      */           break;
/*      */         case 3:
/*      */         case 4:
/*  175 */           addConstantIntegerOrFloat(itemIndex, itemTag, classReader.readInt(itemOffset));
/*      */           break;
/*      */         case 12:
/*  178 */           addConstantNameAndType(itemIndex, classReader
/*      */               
/*  180 */               .readUTF8(itemOffset, charBuffer), classReader
/*  181 */               .readUTF8(itemOffset + 2, charBuffer));
/*      */           break;
/*      */         case 5:
/*      */         case 6:
/*  185 */           addConstantLongOrDouble(itemIndex, itemTag, classReader.readLong(itemOffset));
/*      */           break;
/*      */         case 1:
/*  188 */           addConstantUtf8(itemIndex, classReader.readUtf(itemIndex, charBuffer));
/*      */           break;
/*      */         
/*      */         case 15:
/*  192 */           memberRefItemOffset = classReader.getItem(classReader.readUnsignedShort(itemOffset + 1));
/*      */           
/*  194 */           nameAndTypeItemOffset = classReader.getItem(classReader.readUnsignedShort(memberRefItemOffset + 2));
/*  195 */           addConstantMethodHandle(itemIndex, classReader
/*      */               
/*  197 */               .readByte(itemOffset), classReader
/*  198 */               .readClass(memberRefItemOffset, charBuffer), classReader
/*  199 */               .readUTF8(nameAndTypeItemOffset, charBuffer), classReader
/*  200 */               .readUTF8(nameAndTypeItemOffset + 2, charBuffer));
/*      */           break;
/*      */         case 17:
/*      */         case 18:
/*  204 */           hasBootstrapMethods = true;
/*      */           
/*  206 */           nameAndTypeItemOffset = classReader.getItem(classReader.readUnsignedShort(itemOffset + 2));
/*  207 */           addConstantDynamicOrInvokeDynamicReference(itemTag, itemIndex, classReader
/*      */ 
/*      */               
/*  210 */               .readUTF8(nameAndTypeItemOffset, charBuffer), classReader
/*  211 */               .readUTF8(nameAndTypeItemOffset + 2, charBuffer), classReader
/*  212 */               .readUnsignedShort(itemOffset));
/*      */           break;
/*      */         case 7:
/*      */         case 8:
/*      */         case 16:
/*      */         case 19:
/*      */         case 20:
/*  219 */           addConstantUtf8Reference(itemIndex, itemTag, classReader
/*  220 */               .readUTF8(itemOffset, charBuffer));
/*      */           break;
/*      */         default:
/*  223 */           throw new IllegalArgumentException();
/*      */       } 
/*  225 */       itemIndex += (
/*  226 */         itemTag == 5 || itemTag == 6) ? 2 : 1;
/*      */     } 
/*      */ 
/*      */     
/*  230 */     if (hasBootstrapMethods) {
/*  231 */       copyBootstrapMethods(classReader, charBuffer);
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
/*      */   private void copyBootstrapMethods(ClassReader classReader, char[] charBuffer) {
/*  245 */     byte[] inputBytes = classReader.classFileBuffer;
/*  246 */     int currentAttributeOffset = classReader.getFirstAttributeOffset();
/*  247 */     for (int i = classReader.readUnsignedShort(currentAttributeOffset - 2); i > 0; i--) {
/*  248 */       String attributeName = classReader.readUTF8(currentAttributeOffset, charBuffer);
/*  249 */       if ("BootstrapMethods".equals(attributeName)) {
/*  250 */         this.bootstrapMethodCount = classReader.readUnsignedShort(currentAttributeOffset + 6);
/*      */         break;
/*      */       } 
/*  253 */       currentAttributeOffset += 6 + classReader.readInt(currentAttributeOffset + 2);
/*      */     } 
/*  255 */     if (this.bootstrapMethodCount > 0) {
/*      */       
/*  257 */       int bootstrapMethodsOffset = currentAttributeOffset + 8;
/*  258 */       int bootstrapMethodsLength = classReader.readInt(currentAttributeOffset + 2) - 2;
/*  259 */       this.bootstrapMethods = new ByteVector(bootstrapMethodsLength);
/*  260 */       this.bootstrapMethods.putByteArray(inputBytes, bootstrapMethodsOffset, bootstrapMethodsLength);
/*      */ 
/*      */       
/*  263 */       int currentOffset = bootstrapMethodsOffset;
/*  264 */       for (int j = 0; j < this.bootstrapMethodCount; j++) {
/*  265 */         int offset = currentOffset - bootstrapMethodsOffset;
/*  266 */         int bootstrapMethodRef = classReader.readUnsignedShort(currentOffset);
/*  267 */         currentOffset += 2;
/*  268 */         int numBootstrapArguments = classReader.readUnsignedShort(currentOffset);
/*  269 */         currentOffset += 2;
/*  270 */         int hashCode = classReader.readConst(bootstrapMethodRef, charBuffer).hashCode();
/*  271 */         while (numBootstrapArguments-- > 0) {
/*  272 */           int bootstrapArgument = classReader.readUnsignedShort(currentOffset);
/*  273 */           currentOffset += 2;
/*  274 */           hashCode ^= classReader.readConst(bootstrapArgument, charBuffer).hashCode();
/*      */         } 
/*  276 */         add(new Entry(j, 64, offset, hashCode & Integer.MAX_VALUE));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   ClassReader getSource() {
/*  288 */     return this.sourceClassReader;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getMajorVersion() {
/*  297 */     return this.majorVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String getClassName() {
/*  306 */     return this.className;
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
/*      */   int setMajorVersionAndClassName(int majorVersion, String className) {
/*  318 */     this.majorVersion = majorVersion;
/*  319 */     this.className = className;
/*  320 */     return (addConstantClass(className)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getConstantPoolCount() {
/*  329 */     return this.constantPoolCount;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int getConstantPoolLength() {
/*  338 */     return this.constantPool.length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void putConstantPool(ByteVector output) {
/*  348 */     output.putShort(this.constantPoolCount).putByteArray(this.constantPool.data, 0, this.constantPool.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int computeBootstrapMethodsSize() {
/*  358 */     if (this.bootstrapMethods != null) {
/*  359 */       addConstantUtf8("BootstrapMethods");
/*  360 */       return 8 + this.bootstrapMethods.length;
/*      */     } 
/*  362 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void putBootstrapMethods(ByteVector output) {
/*  373 */     if (this.bootstrapMethods != null) {
/*  374 */       output
/*  375 */         .putShort(addConstantUtf8("BootstrapMethods"))
/*  376 */         .putInt(this.bootstrapMethods.length + 2)
/*  377 */         .putShort(this.bootstrapMethodCount)
/*  378 */         .putByteArray(this.bootstrapMethods.data, 0, this.bootstrapMethods.length);
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
/*      */   private Entry get(int hashCode) {
/*  394 */     return this.entries[hashCode % this.entries.length];
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
/*      */   private Entry put(Entry entry) {
/*  407 */     if (this.entryCount > this.entries.length * 3 / 4) {
/*  408 */       int currentCapacity = this.entries.length;
/*  409 */       int newCapacity = currentCapacity * 2 + 1;
/*  410 */       Entry[] newEntries = new Entry[newCapacity];
/*  411 */       for (int i = currentCapacity - 1; i >= 0; i--) {
/*  412 */         Entry currentEntry = this.entries[i];
/*  413 */         while (currentEntry != null) {
/*  414 */           int newCurrentEntryIndex = currentEntry.hashCode % newCapacity;
/*  415 */           Entry nextEntry = currentEntry.next;
/*  416 */           currentEntry.next = newEntries[newCurrentEntryIndex];
/*  417 */           newEntries[newCurrentEntryIndex] = currentEntry;
/*  418 */           currentEntry = nextEntry;
/*      */         } 
/*      */       } 
/*  421 */       this.entries = newEntries;
/*      */     } 
/*  423 */     this.entryCount++;
/*  424 */     int index = entry.hashCode % this.entries.length;
/*  425 */     entry.next = this.entries[index];
/*  426 */     this.entries[index] = entry; return entry;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void add(Entry entry) {
/*  437 */     this.entryCount++;
/*  438 */     int index = entry.hashCode % this.entries.length;
/*  439 */     entry.next = this.entries[index];
/*  440 */     this.entries[index] = entry;
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
/*      */   Symbol addConstant(Object value) {
/*  457 */     if (value instanceof Integer)
/*  458 */       return addConstantInteger(((Integer)value).intValue()); 
/*  459 */     if (value instanceof Byte)
/*  460 */       return addConstantInteger(((Byte)value).intValue()); 
/*  461 */     if (value instanceof Character)
/*  462 */       return addConstantInteger(((Character)value).charValue()); 
/*  463 */     if (value instanceof Short)
/*  464 */       return addConstantInteger(((Short)value).intValue()); 
/*  465 */     if (value instanceof Boolean)
/*  466 */       return addConstantInteger(((Boolean)value).booleanValue() ? 1 : 0); 
/*  467 */     if (value instanceof Float)
/*  468 */       return addConstantFloat(((Float)value).floatValue()); 
/*  469 */     if (value instanceof Long)
/*  470 */       return addConstantLong(((Long)value).longValue()); 
/*  471 */     if (value instanceof Double)
/*  472 */       return addConstantDouble(((Double)value).doubleValue()); 
/*  473 */     if (value instanceof String)
/*  474 */       return addConstantString((String)value); 
/*  475 */     if (value instanceof Type) {
/*  476 */       Type type = (Type)value;
/*  477 */       int typeSort = type.getSort();
/*  478 */       if (typeSort == 10)
/*  479 */         return addConstantClass(type.getInternalName()); 
/*  480 */       if (typeSort == 11) {
/*  481 */         return addConstantMethodType(type.getDescriptor());
/*      */       }
/*  483 */       return addConstantClass(type.getDescriptor());
/*      */     } 
/*  485 */     if (value instanceof Handle) {
/*  486 */       Handle handle = (Handle)value;
/*  487 */       return addConstantMethodHandle(handle
/*  488 */           .getTag(), handle
/*  489 */           .getOwner(), handle
/*  490 */           .getName(), handle
/*  491 */           .getDesc(), handle
/*  492 */           .isInterface());
/*  493 */     }  if (value instanceof ConstantDynamic) {
/*  494 */       ConstantDynamic constantDynamic = (ConstantDynamic)value;
/*  495 */       return addConstantDynamic(constantDynamic
/*  496 */           .getName(), constantDynamic
/*  497 */           .getDescriptor(), constantDynamic
/*  498 */           .getBootstrapMethod(), constantDynamic
/*  499 */           .getBootstrapMethodArgumentsUnsafe());
/*      */     } 
/*  501 */     throw new IllegalArgumentException("value " + value);
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
/*      */   Symbol addConstantClass(String value) {
/*  513 */     return addConstantUtf8Reference(7, value);
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
/*      */   Symbol addConstantFieldref(String owner, String name, String descriptor) {
/*  526 */     return addConstantMemberReference(9, owner, name, descriptor);
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
/*      */   Symbol addConstantMethodref(String owner, String name, String descriptor, boolean isInterface) {
/*  541 */     int tag = isInterface ? 11 : 10;
/*  542 */     return addConstantMemberReference(tag, owner, name, descriptor);
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
/*      */   private Entry addConstantMemberReference(int tag, String owner, String name, String descriptor) {
/*  559 */     int hashCode = hash(tag, owner, name, descriptor);
/*  560 */     Entry entry = get(hashCode);
/*  561 */     while (entry != null) {
/*  562 */       if (entry.tag == tag && entry.hashCode == hashCode && entry.owner
/*      */         
/*  564 */         .equals(owner) && entry.name
/*  565 */         .equals(name) && entry.value
/*  566 */         .equals(descriptor)) {
/*  567 */         return entry;
/*      */       }
/*  569 */       entry = entry.next;
/*      */     } 
/*  571 */     this.constantPool.put122(tag, 
/*  572 */         (addConstantClass(owner)).index, addConstantNameAndType(name, descriptor));
/*  573 */     return put(new Entry(this.constantPoolCount++, tag, owner, name, descriptor, 0L, hashCode));
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
/*      */   private void addConstantMemberReference(int index, int tag, String owner, String name, String descriptor) {
/*  593 */     add(new Entry(index, tag, owner, name, descriptor, 0L, hash(tag, owner, name, descriptor)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Symbol addConstantString(String value) {
/*  604 */     return addConstantUtf8Reference(8, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Symbol addConstantInteger(int value) {
/*  615 */     return addConstantIntegerOrFloat(3, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Symbol addConstantFloat(float value) {
/*  626 */     return addConstantIntegerOrFloat(4, Float.floatToRawIntBits(value));
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
/*      */   private Symbol addConstantIntegerOrFloat(int tag, int value) {
/*  638 */     int hashCode = hash(tag, value);
/*  639 */     Entry entry = get(hashCode);
/*  640 */     while (entry != null) {
/*  641 */       if (entry.tag == tag && entry.hashCode == hashCode && entry.data == value) {
/*  642 */         return entry;
/*      */       }
/*  644 */       entry = entry.next;
/*      */     } 
/*  646 */     this.constantPool.putByte(tag).putInt(value);
/*  647 */     return put(new Entry(this.constantPoolCount++, tag, value, hashCode));
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
/*      */   private void addConstantIntegerOrFloat(int index, int tag, int value) {
/*  659 */     add(new Entry(index, tag, value, hash(tag, value)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Symbol addConstantLong(long value) {
/*  670 */     return addConstantLongOrDouble(5, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Symbol addConstantDouble(double value) {
/*  681 */     return addConstantLongOrDouble(6, Double.doubleToRawLongBits(value));
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
/*      */   private Symbol addConstantLongOrDouble(int tag, long value) {
/*  693 */     int hashCode = hash(tag, value);
/*  694 */     Entry entry = get(hashCode);
/*  695 */     while (entry != null) {
/*  696 */       if (entry.tag == tag && entry.hashCode == hashCode && entry.data == value) {
/*  697 */         return entry;
/*      */       }
/*  699 */       entry = entry.next;
/*      */     } 
/*  701 */     int index = this.constantPoolCount;
/*  702 */     this.constantPool.putByte(tag).putLong(value);
/*  703 */     this.constantPoolCount += 2;
/*  704 */     return put(new Entry(index, tag, value, hashCode));
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
/*      */   private void addConstantLongOrDouble(int index, int tag, long value) {
/*  716 */     add(new Entry(index, tag, value, hash(tag, value)));
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
/*      */   int addConstantNameAndType(String name, String descriptor) {
/*  728 */     int tag = 12;
/*  729 */     int hashCode = hash(12, name, descriptor);
/*  730 */     Entry entry = get(hashCode);
/*  731 */     while (entry != null) {
/*  732 */       if (entry.tag == 12 && entry.hashCode == hashCode && entry.name
/*      */         
/*  734 */         .equals(name) && entry.value
/*  735 */         .equals(descriptor)) {
/*  736 */         return entry.index;
/*      */       }
/*  738 */       entry = entry.next;
/*      */     } 
/*  740 */     this.constantPool.put122(12, addConstantUtf8(name), addConstantUtf8(descriptor));
/*  741 */     return (put(new Entry(this.constantPoolCount++, 12, name, descriptor, hashCode))).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addConstantNameAndType(int index, String name, String descriptor) {
/*  752 */     int tag = 12;
/*  753 */     add(new Entry(index, 12, name, descriptor, hash(12, name, descriptor)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int addConstantUtf8(String value) {
/*  764 */     int hashCode = hash(1, value);
/*  765 */     Entry entry = get(hashCode);
/*  766 */     while (entry != null) {
/*  767 */       if (entry.tag == 1 && entry.hashCode == hashCode && entry.value
/*      */         
/*  769 */         .equals(value)) {
/*  770 */         return entry.index;
/*      */       }
/*  772 */       entry = entry.next;
/*      */     } 
/*  774 */     this.constantPool.putByte(1).putUTF8(value);
/*  775 */     return (put(new Entry(this.constantPoolCount++, 1, value, hashCode))).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addConstantUtf8(int index, String value) {
/*  785 */     add(new Entry(index, 1, value, hash(1, value)));
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
/*      */   Symbol addConstantMethodHandle(int referenceKind, String owner, String name, String descriptor, boolean isInterface) {
/*  808 */     int tag = 15;
/*      */ 
/*      */     
/*  811 */     int hashCode = hash(15, owner, name, descriptor, referenceKind);
/*  812 */     Entry entry = get(hashCode);
/*  813 */     while (entry != null) {
/*  814 */       if (entry.tag == 15 && entry.hashCode == hashCode && entry.data == referenceKind && entry.owner
/*      */ 
/*      */         
/*  817 */         .equals(owner) && entry.name
/*  818 */         .equals(name) && entry.value
/*  819 */         .equals(descriptor)) {
/*  820 */         return entry;
/*      */       }
/*  822 */       entry = entry.next;
/*      */     } 
/*  824 */     if (referenceKind <= 4) {
/*  825 */       this.constantPool.put112(15, referenceKind, (addConstantFieldref(owner, name, descriptor)).index);
/*      */     } else {
/*  827 */       this.constantPool.put112(15, referenceKind, 
/*  828 */           (addConstantMethodref(owner, name, descriptor, isInterface)).index);
/*      */     } 
/*  830 */     return put(new Entry(this.constantPoolCount++, 15, owner, name, descriptor, referenceKind, hashCode));
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
/*      */   private void addConstantMethodHandle(int index, int referenceKind, String owner, String name, String descriptor) {
/*  852 */     int tag = 15;
/*  853 */     int hashCode = hash(15, owner, name, descriptor, referenceKind);
/*  854 */     add(new Entry(index, 15, owner, name, descriptor, referenceKind, hashCode));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Symbol addConstantMethodType(String methodDescriptor) {
/*  865 */     return addConstantUtf8Reference(16, methodDescriptor);
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
/*      */   Symbol addConstantDynamic(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
/*  884 */     Symbol bootstrapMethod = addBootstrapMethod(bootstrapMethodHandle, bootstrapMethodArguments);
/*  885 */     return addConstantDynamicOrInvokeDynamicReference(17, name, descriptor, bootstrapMethod.index);
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
/*      */   Symbol addConstantInvokeDynamic(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
/*  905 */     Symbol bootstrapMethod = addBootstrapMethod(bootstrapMethodHandle, bootstrapMethodArguments);
/*  906 */     return addConstantDynamicOrInvokeDynamicReference(18, name, descriptor, bootstrapMethod.index);
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
/*      */   private Symbol addConstantDynamicOrInvokeDynamicReference(int tag, String name, String descriptor, int bootstrapMethodIndex) {
/*  924 */     int hashCode = hash(tag, name, descriptor, bootstrapMethodIndex);
/*  925 */     Entry entry = get(hashCode);
/*  926 */     while (entry != null) {
/*  927 */       if (entry.tag == tag && entry.hashCode == hashCode && entry.data == bootstrapMethodIndex && entry.name
/*      */ 
/*      */         
/*  930 */         .equals(name) && entry.value
/*  931 */         .equals(descriptor)) {
/*  932 */         return entry;
/*      */       }
/*  934 */       entry = entry.next;
/*      */     } 
/*  936 */     this.constantPool.put122(tag, bootstrapMethodIndex, addConstantNameAndType(name, descriptor));
/*  937 */     return put(new Entry(this.constantPoolCount++, tag, null, name, descriptor, bootstrapMethodIndex, hashCode));
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
/*      */   private void addConstantDynamicOrInvokeDynamicReference(int tag, int index, String name, String descriptor, int bootstrapMethodIndex) {
/*  960 */     int hashCode = hash(tag, name, descriptor, bootstrapMethodIndex);
/*  961 */     add(new Entry(index, tag, null, name, descriptor, bootstrapMethodIndex, hashCode));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Symbol addConstantModule(String moduleName) {
/*  972 */     return addConstantUtf8Reference(19, moduleName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Symbol addConstantPackage(String packageName) {
/*  983 */     return addConstantUtf8Reference(20, packageName);
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
/*      */   private Symbol addConstantUtf8Reference(int tag, String value) {
/*  999 */     int hashCode = hash(tag, value);
/* 1000 */     Entry entry = get(hashCode);
/* 1001 */     while (entry != null) {
/* 1002 */       if (entry.tag == tag && entry.hashCode == hashCode && entry.value.equals(value)) {
/* 1003 */         return entry;
/*      */       }
/* 1005 */       entry = entry.next;
/*      */     } 
/* 1007 */     this.constantPool.put12(tag, addConstantUtf8(value));
/* 1008 */     return put(new Entry(this.constantPoolCount++, tag, value, hashCode));
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
/*      */   private void addConstantUtf8Reference(int index, int tag, String value) {
/* 1023 */     add(new Entry(index, tag, value, hash(tag, value)));
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
/*      */   Symbol addBootstrapMethod(Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
/* 1040 */     ByteVector bootstrapMethodsAttribute = this.bootstrapMethods;
/* 1041 */     if (bootstrapMethodsAttribute == null) {
/* 1042 */       bootstrapMethodsAttribute = this.bootstrapMethods = new ByteVector();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1049 */     int numBootstrapArguments = bootstrapMethodArguments.length;
/* 1050 */     int[] bootstrapMethodArgumentIndexes = new int[numBootstrapArguments];
/* 1051 */     for (int i = 0; i < numBootstrapArguments; i++) {
/* 1052 */       bootstrapMethodArgumentIndexes[i] = (addConstant(bootstrapMethodArguments[i])).index;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1058 */     int bootstrapMethodOffset = bootstrapMethodsAttribute.length;
/* 1059 */     bootstrapMethodsAttribute.putShort(
/* 1060 */         (addConstantMethodHandle(bootstrapMethodHandle
/* 1061 */           .getTag(), bootstrapMethodHandle
/* 1062 */           .getOwner(), bootstrapMethodHandle
/* 1063 */           .getName(), bootstrapMethodHandle
/* 1064 */           .getDesc(), bootstrapMethodHandle
/* 1065 */           .isInterface())).index);
/*      */ 
/*      */     
/* 1068 */     bootstrapMethodsAttribute.putShort(numBootstrapArguments);
/* 1069 */     for (int j = 0; j < numBootstrapArguments; j++) {
/* 1070 */       bootstrapMethodsAttribute.putShort(bootstrapMethodArgumentIndexes[j]);
/*      */     }
/*      */ 
/*      */     
/* 1074 */     int bootstrapMethodlength = bootstrapMethodsAttribute.length - bootstrapMethodOffset;
/* 1075 */     int hashCode = bootstrapMethodHandle.hashCode();
/* 1076 */     for (Object bootstrapMethodArgument : bootstrapMethodArguments) {
/* 1077 */       hashCode ^= bootstrapMethodArgument.hashCode();
/*      */     }
/* 1079 */     hashCode &= Integer.MAX_VALUE;
/*      */ 
/*      */     
/* 1082 */     return addBootstrapMethod(bootstrapMethodOffset, bootstrapMethodlength, hashCode);
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
/*      */   private Symbol addBootstrapMethod(int offset, int length, int hashCode) {
/* 1096 */     byte[] bootstrapMethodsData = this.bootstrapMethods.data;
/* 1097 */     Entry entry = get(hashCode);
/* 1098 */     while (entry != null) {
/* 1099 */       if (entry.tag == 64 && entry.hashCode == hashCode) {
/* 1100 */         int otherOffset = (int)entry.data;
/* 1101 */         boolean isSameBootstrapMethod = true;
/* 1102 */         for (int i = 0; i < length; i++) {
/* 1103 */           if (bootstrapMethodsData[offset + i] != bootstrapMethodsData[otherOffset + i]) {
/* 1104 */             isSameBootstrapMethod = false;
/*      */             break;
/*      */           } 
/*      */         } 
/* 1108 */         if (isSameBootstrapMethod) {
/* 1109 */           this.bootstrapMethods.length = offset;
/* 1110 */           return entry;
/*      */         } 
/*      */       } 
/* 1113 */       entry = entry.next;
/*      */     } 
/* 1115 */     return put(new Entry(this.bootstrapMethodCount++, 64, offset, hashCode));
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
/*      */   Symbol getType(int typeIndex) {
/* 1129 */     return this.typeTable[typeIndex];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int addType(String value) {
/* 1140 */     int hashCode = hash(128, value);
/* 1141 */     Entry entry = get(hashCode);
/* 1142 */     while (entry != null) {
/* 1143 */       if (entry.tag == 128 && entry.hashCode == hashCode && entry.value.equals(value)) {
/* 1144 */         return entry.index;
/*      */       }
/* 1146 */       entry = entry.next;
/*      */     } 
/* 1148 */     return addTypeInternal(new Entry(this.typeCount, 128, value, hashCode));
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
/*      */   int addUninitializedType(String value, int bytecodeOffset) {
/* 1161 */     int hashCode = hash(129, value, bytecodeOffset);
/* 1162 */     Entry entry = get(hashCode);
/* 1163 */     while (entry != null) {
/* 1164 */       if (entry.tag == 129 && entry.hashCode == hashCode && entry.data == bytecodeOffset && entry.value
/*      */ 
/*      */         
/* 1167 */         .equals(value)) {
/* 1168 */         return entry.index;
/*      */       }
/* 1170 */       entry = entry.next;
/*      */     } 
/* 1172 */     return addTypeInternal(new Entry(this.typeCount, 129, value, bytecodeOffset, hashCode));
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
/*      */   int addMergedType(int typeTableIndex1, int typeTableIndex2) {
/* 1191 */     long data = (typeTableIndex1 < typeTableIndex2) ? (typeTableIndex1 | typeTableIndex2 << 32L) : (typeTableIndex2 | typeTableIndex1 << 32L);
/* 1192 */     int hashCode = hash(130, typeTableIndex1 + typeTableIndex2);
/* 1193 */     Entry entry = get(hashCode);
/* 1194 */     while (entry != null) {
/* 1195 */       if (entry.tag == 130 && entry.hashCode == hashCode && entry.data == data) {
/* 1196 */         return entry.info;
/*      */       }
/* 1198 */       entry = entry.next;
/*      */     } 
/* 1200 */     String type1 = (this.typeTable[typeTableIndex1]).value;
/* 1201 */     String type2 = (this.typeTable[typeTableIndex2]).value;
/* 1202 */     int commonSuperTypeIndex = addType(this.classWriter.getCommonSuperClass(type1, type2));
/* 1203 */     (put(new Entry(this.typeCount, 130, data, hashCode))).info = commonSuperTypeIndex;
/* 1204 */     return commonSuperTypeIndex;
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
/*      */   private int addTypeInternal(Entry entry) {
/* 1216 */     if (this.typeTable == null) {
/* 1217 */       this.typeTable = new Entry[16];
/*      */     }
/* 1219 */     if (this.typeCount == this.typeTable.length) {
/* 1220 */       Entry[] newTypeTable = new Entry[2 * this.typeTable.length];
/* 1221 */       System.arraycopy(this.typeTable, 0, newTypeTable, 0, this.typeTable.length);
/* 1222 */       this.typeTable = newTypeTable;
/*      */     } 
/* 1224 */     this.typeTable[this.typeCount++] = entry;
/* 1225 */     return (put(entry)).index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int hash(int tag, int value) {
/* 1233 */     return Integer.MAX_VALUE & tag + value;
/*      */   }
/*      */   
/*      */   private static int hash(int tag, long value) {
/* 1237 */     return Integer.MAX_VALUE & tag + (int)value + (int)(value >>> 32L);
/*      */   }
/*      */   
/*      */   private static int hash(int tag, String value) {
/* 1241 */     return Integer.MAX_VALUE & tag + value.hashCode();
/*      */   }
/*      */   
/*      */   private static int hash(int tag, String value1, int value2) {
/* 1245 */     return Integer.MAX_VALUE & tag + value1.hashCode() + value2;
/*      */   }
/*      */   
/*      */   private static int hash(int tag, String value1, String value2) {
/* 1249 */     return Integer.MAX_VALUE & tag + value1.hashCode() * value2.hashCode();
/*      */   }
/*      */ 
/*      */   
/*      */   private static int hash(int tag, String value1, String value2, int value3) {
/* 1254 */     return Integer.MAX_VALUE & tag + value1.hashCode() * value2.hashCode() * (value3 + 1);
/*      */   }
/*      */ 
/*      */   
/*      */   private static int hash(int tag, String value1, String value2, String value3) {
/* 1259 */     return Integer.MAX_VALUE & tag + value1.hashCode() * value2.hashCode() * value3.hashCode();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int hash(int tag, String value1, String value2, String value3, int value4) {
/* 1268 */     return Integer.MAX_VALUE & tag + value1.hashCode() * value2.hashCode() * value3.hashCode() * value4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class Entry
/*      */     extends Symbol
/*      */   {
/*      */     final int hashCode;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Entry next;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Entry(int index, int tag, String owner, String name, String value, long data, int hashCode) {
/* 1297 */       super(index, tag, owner, name, value, data);
/* 1298 */       this.hashCode = hashCode;
/*      */     }
/*      */     
/*      */     Entry(int index, int tag, String value, int hashCode) {
/* 1302 */       super(index, tag, null, null, value, 0L);
/* 1303 */       this.hashCode = hashCode;
/*      */     }
/*      */     
/*      */     Entry(int index, int tag, String value, long data, int hashCode) {
/* 1307 */       super(index, tag, null, null, value, data);
/* 1308 */       this.hashCode = hashCode;
/*      */     }
/*      */ 
/*      */     
/*      */     Entry(int index, int tag, String name, String value, int hashCode) {
/* 1313 */       super(index, tag, null, name, value, 0L);
/* 1314 */       this.hashCode = hashCode;
/*      */     }
/*      */     
/*      */     Entry(int index, int tag, long data, int hashCode) {
/* 1318 */       super(index, tag, null, null, null, data);
/* 1319 */       this.hashCode = hashCode;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\SymbolTable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */