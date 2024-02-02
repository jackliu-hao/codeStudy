/*      */ package com.sun.jna.platform.win32;
/*      */ 
/*      */ import com.sun.jna.Memory;
/*      */ import com.sun.jna.Pointer;
/*      */ import com.sun.jna.StringArray;
/*      */ import com.sun.jna.Structure;
/*      */ import com.sun.jna.Structure.FieldOrder;
/*      */ import com.sun.jna.Union;
/*      */ import com.sun.jna.win32.W32APITypeMapper;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public interface Winevt
/*      */ {
/*      */   public static final int EVT_VARIANT_TYPE_ARRAY = 128;
/*      */   public static final int EVT_VARIANT_TYPE_MASK = 127;
/*      */   public static final int EVT_READ_ACCESS = 1;
/*      */   public static final int EVT_WRITE_ACCESS = 2;
/*      */   public static final int EVT_ALL_ACCESS = 7;
/*      */   public static final int EVT_CLEAR_ACCESS = 4;
/*      */   
/*      */   public enum EVT_VARIANT_TYPE
/*      */   {
/*   50 */     EvtVarTypeNull(""),
/*      */ 
/*      */     
/*   53 */     EvtVarTypeString("String"),
/*      */ 
/*      */     
/*   56 */     EvtVarTypeAnsiString("AnsiString"),
/*      */ 
/*      */     
/*   59 */     EvtVarTypeSByte("SByte"),
/*      */ 
/*      */     
/*   62 */     EvtVarTypeByte("Byte"),
/*      */ 
/*      */     
/*   65 */     EvtVarTypeInt16("Int16"),
/*      */ 
/*      */     
/*   68 */     EvtVarTypeUInt16("UInt16"),
/*      */ 
/*      */     
/*   71 */     EvtVarTypeInt32("Int32"),
/*      */ 
/*      */     
/*   74 */     EvtVarTypeUInt32("UInt32"),
/*      */ 
/*      */     
/*   77 */     EvtVarTypeInt64("Int64"),
/*      */ 
/*      */     
/*   80 */     EvtVarTypeUInt64("UInt64"),
/*      */ 
/*      */     
/*   83 */     EvtVarTypeSingle("Single"),
/*      */ 
/*      */     
/*   86 */     EvtVarTypeDouble("Double"),
/*      */ 
/*      */     
/*   89 */     EvtVarTypeBoolean("Boolean"),
/*      */ 
/*      */     
/*   92 */     EvtVarTypeBinary("Binary"),
/*      */ 
/*      */     
/*   95 */     EvtVarTypeGuid("Guid"),
/*      */ 
/*      */     
/*   98 */     EvtVarTypeSizeT("SizeT"),
/*      */ 
/*      */     
/*  101 */     EvtVarTypeFileTime("FileTime"),
/*      */ 
/*      */     
/*  104 */     EvtVarTypeSysTime("SysTime"),
/*      */ 
/*      */     
/*  107 */     EvtVarTypeSid("Sid"),
/*      */ 
/*      */     
/*  110 */     EvtVarTypeHexInt32("Int32"),
/*      */ 
/*      */     
/*  113 */     EvtVarTypeHexInt64("Int64"),
/*      */ 
/*      */     
/*  116 */     EvtVarTypeEvtHandle("EvtHandle"),
/*      */ 
/*      */     
/*  119 */     EvtVarTypeEvtXml("Xml");
/*      */     
/*      */     private final String field;
/*      */     
/*      */     EVT_VARIANT_TYPE(String field) {
/*  124 */       this.field = field;
/*      */     }
/*      */     
/*      */     public String getField() {
/*  128 */       return this.field.isEmpty() ? "" : (this.field + "Val");
/*      */     }
/*      */     
/*      */     public String getArrField() {
/*  132 */       return this.field.isEmpty() ? "" : (this.field + "Arr");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"field1", "Count", "Type"})
/*      */   public static class EVT_VARIANT
/*      */     extends Structure
/*      */   {
/*      */     public field1_union field1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int Count;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int Type;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Object holder;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class field1_union
/*      */       extends Union
/*      */     {
/*      */       public byte byteValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public short shortValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public int intValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public long longValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public float floatValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public double doubleVal;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Pointer pointerValue;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public EVT_VARIANT() {
/*  220 */       super(W32APITypeMapper.DEFAULT);
/*      */     }
/*      */     
/*      */     public EVT_VARIANT(Pointer peer) {
/*  224 */       super(peer, 0, W32APITypeMapper.DEFAULT);
/*      */     }
/*      */     
/*      */     public void use(Pointer m) {
/*  228 */       useMemory(m, 0);
/*      */     }
/*      */     
/*      */     public static class ByReference extends EVT_VARIANT implements Structure.ByReference {
/*      */       public ByReference(Pointer p) {
/*  233 */         super(p);
/*      */       }
/*      */       
/*      */       public ByReference() {}
/*      */     }
/*      */     
/*      */     public static class ByValue
/*      */       extends EVT_VARIANT
/*      */       implements Structure.ByValue {
/*      */       public ByValue(Pointer p) {
/*  243 */         super(p);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public ByValue() {}
/*      */     }
/*      */ 
/*      */     
/*      */     private int getBaseType() {
/*  253 */       return this.Type & 0x7F;
/*      */     }
/*      */     
/*      */     public boolean isArray() {
/*  257 */       return ((this.Type & 0x80) == 128);
/*      */     }
/*      */     
/*      */     public Winevt.EVT_VARIANT_TYPE getVariantType() {
/*  261 */       return Winevt.EVT_VARIANT_TYPE.values()[getBaseType()];
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setValue(Winevt.EVT_VARIANT_TYPE type, Object value) {
/*  272 */       allocateMemory();
/*  273 */       if (type == null) {
/*  274 */         throw new IllegalArgumentException("setValue must not be called with type set to NULL");
/*      */       }
/*  276 */       this.holder = null;
/*  277 */       if (value == null || type == Winevt.EVT_VARIANT_TYPE.EvtVarTypeNull) {
/*  278 */         this.Type = Winevt.EVT_VARIANT_TYPE.EvtVarTypeNull.ordinal();
/*  279 */         this.Count = 0;
/*  280 */         this.field1.writeField("pointerValue", Pointer.NULL);
/*      */       } else {
/*  282 */         switch (type) {
/*      */           case EvtVarTypeAnsiString:
/*  284 */             if (value.getClass().isArray() && value.getClass().getComponentType() == String.class) {
/*  285 */               this.Type = type.ordinal() | 0x80;
/*  286 */               StringArray sa = new StringArray((String[])value, false);
/*  287 */               this.holder = sa;
/*  288 */               this.Count = ((String[])value).length;
/*  289 */               this.field1.writeField("pointerValue", sa); break;
/*  290 */             }  if (value.getClass() == String.class) {
/*  291 */               this.Type = type.ordinal();
/*  292 */               Memory mem = new Memory((((String)value).length() + 1));
/*  293 */               mem.setString(0L, (String)value);
/*  294 */               this.holder = mem;
/*  295 */               this.Count = 0;
/*  296 */               this.field1.writeField("pointerValue", mem); break;
/*      */             } 
/*  298 */             throw new IllegalArgumentException(type.name() + " must be set from String/String[]");
/*      */ 
/*      */           
/*      */           case EvtVarTypeBoolean:
/*  302 */             if (value.getClass().isArray() && value.getClass().getComponentType() == WinDef.BOOL.class) {
/*  303 */               this.Type = type.ordinal() | 0x80;
/*  304 */               Memory mem = new Memory((((WinDef.BOOL[])value).length * 4));
/*  305 */               for (int i = 0; i < ((WinDef.BOOL[])value).length; i++) {
/*  306 */                 mem.setInt((i * 4), ((WinDef.BOOL[])value)[i].intValue());
/*      */               }
/*  308 */               this.holder = mem;
/*  309 */               this.Count = 0;
/*  310 */               this.field1.writeField("pointerValue", mem); break;
/*  311 */             }  if (value.getClass() == WinDef.BOOL.class) {
/*  312 */               this.Type = type.ordinal();
/*  313 */               this.Count = 0;
/*  314 */               this.field1.writeField("intValue", Integer.valueOf(((WinDef.BOOL)value).intValue())); break;
/*      */             } 
/*  316 */             throw new IllegalArgumentException(type.name() + " must be set from BOOL/BOOL[]");
/*      */ 
/*      */           
/*      */           case EvtVarTypeString:
/*      */           case EvtVarTypeEvtXml:
/*  321 */             if (value.getClass().isArray() && value.getClass().getComponentType() == String.class) {
/*  322 */               this.Type = type.ordinal() | 0x80;
/*  323 */               StringArray sa = new StringArray((String[])value, true);
/*  324 */               this.holder = sa;
/*  325 */               this.Count = ((String[])value).length;
/*  326 */               this.field1.writeField("pointerValue", sa); break;
/*  327 */             }  if (value.getClass() == String.class) {
/*  328 */               this.Type = type.ordinal();
/*  329 */               Memory mem = new Memory(((((String)value).length() + 1) * 2));
/*  330 */               mem.setWideString(0L, (String)value);
/*  331 */               this.holder = mem;
/*  332 */               this.Count = 0;
/*  333 */               this.field1.writeField("pointerValue", mem); break;
/*      */             } 
/*  335 */             throw new IllegalArgumentException(type.name() + " must be set from String/String[]");
/*      */ 
/*      */           
/*      */           case EvtVarTypeSByte:
/*      */           case EvtVarTypeByte:
/*  340 */             if (value.getClass().isArray() && value.getClass().getComponentType() == byte.class) {
/*  341 */               this.Type = type.ordinal() | 0x80;
/*  342 */               Memory mem = new Memory((((byte[])value).length * 1));
/*  343 */               mem.write(0L, (byte[])value, 0, ((byte[])value).length);
/*  344 */               this.holder = mem;
/*  345 */               this.Count = 0;
/*  346 */               this.field1.writeField("pointerValue", mem); break;
/*  347 */             }  if (value.getClass() == byte.class) {
/*  348 */               this.Type = type.ordinal();
/*  349 */               this.Count = 0;
/*  350 */               this.field1.writeField("byteValue", value); break;
/*      */             } 
/*  352 */             throw new IllegalArgumentException(type.name() + " must be set from byte/byte[]");
/*      */ 
/*      */           
/*      */           case EvtVarTypeInt16:
/*      */           case EvtVarTypeUInt16:
/*  357 */             if (value.getClass().isArray() && value.getClass().getComponentType() == short.class) {
/*  358 */               this.Type = type.ordinal() | 0x80;
/*  359 */               Memory mem = new Memory((((short[])value).length * 2));
/*  360 */               mem.write(0L, (short[])value, 0, ((short[])value).length);
/*  361 */               this.holder = mem;
/*  362 */               this.Count = 0;
/*  363 */               this.field1.writeField("pointerValue", mem); break;
/*  364 */             }  if (value.getClass() == short.class) {
/*  365 */               this.Type = type.ordinal();
/*  366 */               this.Count = 0;
/*  367 */               this.field1.writeField("shortValue", value); break;
/*      */             } 
/*  369 */             throw new IllegalArgumentException(type.name() + " must be set from short/short[]");
/*      */ 
/*      */           
/*      */           case EvtVarTypeHexInt32:
/*      */           case EvtVarTypeInt32:
/*      */           case EvtVarTypeUInt32:
/*  375 */             if (value.getClass().isArray() && value.getClass().getComponentType() == int.class) {
/*  376 */               this.Type = type.ordinal() | 0x80;
/*  377 */               Memory mem = new Memory((((int[])value).length * 4));
/*  378 */               mem.write(0L, (int[])value, 0, ((int[])value).length);
/*  379 */               this.holder = mem;
/*  380 */               this.Count = 0;
/*  381 */               this.field1.writeField("pointerValue", mem); break;
/*  382 */             }  if (value.getClass() == int.class) {
/*  383 */               this.Type = type.ordinal();
/*  384 */               this.Count = 0;
/*  385 */               this.field1.writeField("intValue", value); break;
/*      */             } 
/*  387 */             throw new IllegalArgumentException(type.name() + " must be set from int/int[]");
/*      */ 
/*      */           
/*      */           case EvtVarTypeHexInt64:
/*      */           case EvtVarTypeInt64:
/*      */           case EvtVarTypeUInt64:
/*  393 */             if (value.getClass().isArray() && value.getClass().getComponentType() == long.class) {
/*  394 */               this.Type = type.ordinal() | 0x80;
/*  395 */               Memory mem = new Memory((((long[])value).length * 4));
/*  396 */               mem.write(0L, (long[])value, 0, ((long[])value).length);
/*  397 */               this.holder = mem;
/*  398 */               this.Count = 0;
/*  399 */               this.field1.writeField("pointerValue", mem); break;
/*  400 */             }  if (value.getClass() == long.class) {
/*  401 */               this.Type = type.ordinal();
/*  402 */               this.Count = 0;
/*  403 */               this.field1.writeField("longValue", value); break;
/*      */             } 
/*  405 */             throw new IllegalArgumentException(type.name() + " must be set from long/long[]");
/*      */ 
/*      */           
/*      */           case EvtVarTypeSingle:
/*  409 */             if (value.getClass().isArray() && value.getClass().getComponentType() == float.class) {
/*  410 */               this.Type = type.ordinal() | 0x80;
/*  411 */               Memory mem = new Memory((((float[])value).length * 4));
/*  412 */               mem.write(0L, (float[])value, 0, ((float[])value).length);
/*  413 */               this.holder = mem;
/*  414 */               this.Count = 0;
/*  415 */               this.field1.writeField("pointerValue", mem); break;
/*  416 */             }  if (value.getClass() == float.class) {
/*  417 */               this.Type = type.ordinal();
/*  418 */               this.Count = 0;
/*  419 */               this.field1.writeField("floatValue", value); break;
/*      */             } 
/*  421 */             throw new IllegalArgumentException(type.name() + " must be set from float/float[]");
/*      */ 
/*      */           
/*      */           case EvtVarTypeDouble:
/*  425 */             if (value.getClass().isArray() && value.getClass().getComponentType() == double.class) {
/*  426 */               this.Type = type.ordinal() | 0x80;
/*  427 */               Memory mem = new Memory((((double[])value).length * 4));
/*  428 */               mem.write(0L, (double[])value, 0, ((double[])value).length);
/*  429 */               this.holder = mem;
/*  430 */               this.Count = 0;
/*  431 */               this.field1.writeField("pointerValue", mem); break;
/*  432 */             }  if (value.getClass() == double.class) {
/*  433 */               this.Type = type.ordinal();
/*  434 */               this.Count = 0;
/*  435 */               this.field1.writeField("doubleVal", value); break;
/*      */             } 
/*  437 */             throw new IllegalArgumentException(type.name() + " must be set from double/double[]");
/*      */ 
/*      */           
/*      */           case EvtVarTypeBinary:
/*  441 */             if (value.getClass().isArray() && value.getClass().getComponentType() == byte.class) {
/*  442 */               this.Type = type.ordinal();
/*  443 */               Memory mem = new Memory((((byte[])value).length * 1));
/*  444 */               mem.write(0L, (byte[])value, 0, ((byte[])value).length);
/*  445 */               this.holder = mem;
/*  446 */               this.Count = 0;
/*  447 */               this.field1.writeField("pointerValue", mem); break;
/*      */             } 
/*  449 */             throw new IllegalArgumentException(type.name() + " must be set from byte[]");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           default:
/*  459 */             throw new IllegalStateException(String.format("NOT IMPLEMENTED: getValue(%s) (Array: %b, Count: %d)", new Object[] { type, Boolean.valueOf(isArray()), Integer.valueOf(this.Count) }));
/*      */         } 
/*      */       } 
/*  462 */       write();
/*      */     }
/*      */     public Object getValue() {
/*      */       WinBase.FILETIME fILETIME;
/*      */       WinBase.SYSTEMTIME sYSTEMTIME;
/*      */       Guid.GUID gUID;
/*      */       WinNT.PSID result;
/*  469 */       Winevt.EVT_VARIANT_TYPE type = getVariantType();
/*  470 */       switch (type) {
/*      */         case EvtVarTypeAnsiString:
/*  472 */           return isArray() ? this.field1.getPointer().getPointer(0L).getStringArray(0L, this.Count) : this.field1.getPointer().getPointer(0L).getString(0L);
/*      */         case EvtVarTypeBoolean:
/*  474 */           if (isArray()) {
/*  475 */             int[] rawValue = this.field1.getPointer().getPointer(0L).getIntArray(0L, this.Count);
/*  476 */             WinDef.BOOL[] arrayOfBOOL = new WinDef.BOOL[rawValue.length];
/*  477 */             for (int i = 0; i < arrayOfBOOL.length; i++) {
/*  478 */               arrayOfBOOL[i] = new WinDef.BOOL(rawValue[i]);
/*      */             }
/*  480 */             return arrayOfBOOL;
/*      */           } 
/*  482 */           return new WinDef.BOOL(this.field1.getPointer().getInt(0L));
/*      */         
/*      */         case EvtVarTypeString:
/*      */         case EvtVarTypeEvtXml:
/*  486 */           return isArray() ? this.field1.getPointer().getPointer(0L).getWideStringArray(0L, this.Count) : this.field1.getPointer().getPointer(0L).getWideString(0L);
/*      */         case EvtVarTypeFileTime:
/*  488 */           if (isArray()) {
/*  489 */             WinBase.FILETIME resultFirst = (WinBase.FILETIME)Structure.newInstance(WinBase.FILETIME.class, this.field1.getPointer().getPointer(0L));
/*  490 */             resultFirst.read();
/*  491 */             return resultFirst.toArray(this.Count);
/*      */           } 
/*  493 */           fILETIME = new WinBase.FILETIME(this.field1.getPointer());
/*  494 */           fILETIME.read();
/*  495 */           return fILETIME;
/*      */         
/*      */         case EvtVarTypeSysTime:
/*  498 */           if (isArray()) {
/*  499 */             WinBase.SYSTEMTIME resultFirst = (WinBase.SYSTEMTIME)Structure.newInstance(WinBase.SYSTEMTIME.class, this.field1.getPointer().getPointer(0L));
/*  500 */             resultFirst.read();
/*  501 */             return resultFirst.toArray(this.Count);
/*      */           } 
/*  503 */           sYSTEMTIME = (WinBase.SYSTEMTIME)Structure.newInstance(WinBase.SYSTEMTIME.class, this.field1.getPointer().getPointer(0L));
/*  504 */           sYSTEMTIME.read();
/*  505 */           return sYSTEMTIME;
/*      */         
/*      */         case EvtVarTypeSByte:
/*      */         case EvtVarTypeByte:
/*  509 */           return isArray() ? this.field1.getPointer().getPointer(0L).getByteArray(0L, this.Count) : Byte.valueOf(this.field1.getPointer().getByte(0L));
/*      */         case EvtVarTypeInt16:
/*      */         case EvtVarTypeUInt16:
/*  512 */           return isArray() ? this.field1.getPointer().getPointer(0L).getShortArray(0L, this.Count) : Short.valueOf(this.field1.getPointer().getShort(0L));
/*      */         case EvtVarTypeHexInt32:
/*      */         case EvtVarTypeInt32:
/*      */         case EvtVarTypeUInt32:
/*  516 */           return isArray() ? this.field1.getPointer().getPointer(0L).getIntArray(0L, this.Count) : Integer.valueOf(this.field1.getPointer().getInt(0L));
/*      */         case EvtVarTypeHexInt64:
/*      */         case EvtVarTypeInt64:
/*      */         case EvtVarTypeUInt64:
/*  520 */           return isArray() ? this.field1.getPointer().getPointer(0L).getLongArray(0L, this.Count) : Long.valueOf(this.field1.getPointer().getLong(0L));
/*      */         case EvtVarTypeSingle:
/*  522 */           return isArray() ? this.field1.getPointer().getPointer(0L).getFloatArray(0L, this.Count) : Float.valueOf(this.field1.getPointer().getFloat(0L));
/*      */         case EvtVarTypeDouble:
/*  524 */           return isArray() ? this.field1.getPointer().getPointer(0L).getDoubleArray(0L, this.Count) : Double.valueOf(this.field1.getPointer().getDouble(0L));
/*      */         case EvtVarTypeBinary:
/*  526 */           assert !isArray();
/*  527 */           return this.field1.getPointer().getPointer(0L).getByteArray(0L, this.Count);
/*      */         case EvtVarTypeNull:
/*  529 */           return null;
/*      */         case EvtVarTypeGuid:
/*  531 */           if (isArray()) {
/*  532 */             Guid.GUID resultFirst = (Guid.GUID)Structure.newInstance(Guid.GUID.class, this.field1.getPointer().getPointer(0L));
/*  533 */             resultFirst.read();
/*  534 */             return resultFirst.toArray(this.Count);
/*      */           } 
/*  536 */           gUID = (Guid.GUID)Structure.newInstance(Guid.GUID.class, this.field1.getPointer().getPointer(0L));
/*  537 */           gUID.read();
/*  538 */           return gUID;
/*      */         
/*      */         case EvtVarTypeSid:
/*  541 */           if (isArray()) {
/*  542 */             WinNT.PSID resultFirst = (WinNT.PSID)Structure.newInstance(WinNT.PSID.class, this.field1.getPointer().getPointer(0L));
/*  543 */             resultFirst.read();
/*  544 */             return resultFirst.toArray(this.Count);
/*      */           } 
/*  546 */           result = (WinNT.PSID)Structure.newInstance(WinNT.PSID.class, this.field1.getPointer().getPointer(0L));
/*  547 */           result.read();
/*  548 */           return result;
/*      */         
/*      */         case EvtVarTypeSizeT:
/*  551 */           if (isArray()) {
/*  552 */             long[] rawValue = this.field1.getPointer().getPointer(0L).getLongArray(0L, this.Count);
/*  553 */             BaseTSD.SIZE_T[] arrayOfSIZE_T = new BaseTSD.SIZE_T[rawValue.length];
/*  554 */             for (int i = 0; i < arrayOfSIZE_T.length; i++) {
/*  555 */               arrayOfSIZE_T[i] = new BaseTSD.SIZE_T(rawValue[i]);
/*      */             }
/*  557 */             return arrayOfSIZE_T;
/*      */           } 
/*  559 */           return new BaseTSD.SIZE_T(this.field1.getPointer().getLong(0L));
/*      */         
/*      */         case EvtVarTypeEvtHandle:
/*  562 */           if (isArray()) {
/*  563 */             Pointer[] rawValue = this.field1.getPointer().getPointer(0L).getPointerArray(0L, this.Count);
/*  564 */             WinNT.HANDLE[] arrayOfHANDLE = new WinNT.HANDLE[rawValue.length];
/*  565 */             for (int i = 0; i < arrayOfHANDLE.length; i++) {
/*  566 */               arrayOfHANDLE[i] = new WinNT.HANDLE(rawValue[i]);
/*      */             }
/*  568 */             return arrayOfHANDLE;
/*      */           } 
/*  570 */           return new WinNT.HANDLE(this.field1.getPointer().getPointer(0L));
/*      */       } 
/*      */       
/*  573 */       throw new IllegalStateException(String.format("NOT IMPLEMENTED: getValue(%s) (Array: %b, Count: %d)", new Object[] { type, Boolean.valueOf(isArray()), Integer.valueOf(this.Count) }));
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
/*      */   @FieldOrder({"Server", "User", "Domain", "Password", "Flags"})
/*      */   public static class EVT_RPC_LOGIN
/*      */     extends Structure
/*      */   {
/*      */     public String Server;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String User;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String Domain;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String Password;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int Flags;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public EVT_RPC_LOGIN() {
/*  629 */       super(W32APITypeMapper.UNICODE);
/*      */     }
/*      */     
/*      */     public EVT_RPC_LOGIN(String Server, String User, String Domain, String Password, int Flags) {
/*  633 */       super(W32APITypeMapper.UNICODE);
/*  634 */       this.Server = Server;
/*  635 */       this.User = User;
/*  636 */       this.Domain = Domain;
/*  637 */       this.Password = Password;
/*  638 */       this.Flags = Flags;
/*      */     }
/*      */     
/*      */     public EVT_RPC_LOGIN(Pointer peer) {
/*  642 */       super(peer, 0, W32APITypeMapper.UNICODE);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends EVT_RPC_LOGIN
/*      */       implements Structure.ByReference {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class ByValue
/*      */       extends EVT_RPC_LOGIN
/*      */       implements Structure.ByValue {}
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class EVT_HANDLE
/*      */     extends WinNT.HANDLE
/*      */   {
/*      */     public EVT_HANDLE() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public EVT_HANDLE(Pointer p) {
/* 1701 */       super(p);
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface EVT_EVENT_PROPERTY_ID {
/*      */     public static final int EvtEventQueryIDs = 0;
/*      */     public static final int EvtEventPath = 1;
/*      */     public static final int EvtEventPropertyIdEND = 2;
/*      */   }
/*      */   
/*      */   public static interface EVT_QUERY_PROPERTY_ID {
/*      */     public static final int EvtQueryNames = 0;
/*      */     public static final int EvtQueryStatuses = 1;
/*      */     public static final int EvtQueryPropertyIdEND = 2;
/*      */   }
/*      */   
/*      */   public static interface EVT_EVENT_METADATA_PROPERTY_ID {
/*      */     public static final int EventMetadataEventID = 0;
/*      */     public static final int EventMetadataEventVersion = 1;
/*      */     public static final int EventMetadataEventChannel = 2;
/*      */     public static final int EventMetadataEventLevel = 3;
/*      */     public static final int EventMetadataEventOpcode = 4;
/*      */     public static final int EventMetadataEventTask = 5;
/*      */     public static final int EventMetadataEventKeyword = 6;
/*      */     public static final int EventMetadataEventMessageID = 7;
/*      */     public static final int EventMetadataEventTemplate = 8;
/*      */     public static final int EvtEventMetadataPropertyIdEND = 9;
/*      */   }
/*      */   
/*      */   public static interface EVT_PUBLISHER_METADATA_PROPERTY_ID {
/*      */     public static final int EvtPublisherMetadataPublisherGuid = 0;
/*      */     public static final int EvtPublisherMetadataResourceFilePath = 1;
/*      */     public static final int EvtPublisherMetadataParameterFilePath = 2;
/*      */     public static final int EvtPublisherMetadataMessageFilePath = 3;
/*      */     public static final int EvtPublisherMetadataHelpLink = 4;
/*      */     public static final int EvtPublisherMetadataPublisherMessageID = 5;
/*      */     public static final int EvtPublisherMetadataChannelReferences = 6;
/*      */     public static final int EvtPublisherMetadataChannelReferencePath = 7;
/*      */     public static final int EvtPublisherMetadataChannelReferenceIndex = 8;
/*      */     public static final int EvtPublisherMetadataChannelReferenceID = 9;
/*      */     public static final int EvtPublisherMetadataChannelReferenceFlags = 10;
/*      */     public static final int EvtPublisherMetadataChannelReferenceMessageID = 11;
/*      */     public static final int EvtPublisherMetadataLevels = 12;
/*      */     public static final int EvtPublisherMetadataLevelName = 13;
/*      */     public static final int EvtPublisherMetadataLevelValue = 14;
/*      */     public static final int EvtPublisherMetadataLevelMessageID = 15;
/*      */     public static final int EvtPublisherMetadataTasks = 16;
/*      */     public static final int EvtPublisherMetadataTaskName = 17;
/*      */     public static final int EvtPublisherMetadataTaskEventGuid = 18;
/*      */     public static final int EvtPublisherMetadataTaskValue = 19;
/*      */     public static final int EvtPublisherMetadataTaskMessageID = 20;
/*      */     public static final int EvtPublisherMetadataOpcodes = 21;
/*      */     public static final int EvtPublisherMetadataOpcodeName = 22;
/*      */     public static final int EvtPublisherMetadataOpcodeValue = 23;
/*      */     public static final int EvtPublisherMetadataOpcodeMessageID = 24;
/*      */     public static final int EvtPublisherMetadataKeywords = 25;
/*      */     public static final int EvtPublisherMetadataKeywordName = 26;
/*      */     public static final int EvtPublisherMetadataKeywordValue = 27;
/*      */     public static final int EvtPublisherMetadataKeywordMessageID = 28;
/*      */     public static final int EvtPublisherMetadataPropertyIdEND = 29;
/*      */   }
/*      */   
/*      */   public static interface EVT_CHANNEL_REFERENCE_FLAGS {
/*      */     public static final int EvtChannelReferenceImported = 1;
/*      */   }
/*      */   
/*      */   public static interface EVT_CHANNEL_SID_TYPE {
/*      */     public static final int EvtChannelSidTypeNone = 0;
/*      */     public static final int EvtChannelSidTypePublishing = 1;
/*      */   }
/*      */   
/*      */   public static interface EVT_CHANNEL_CLOCK_TYPE {
/*      */     public static final int EvtChannelClockTypeSystemTime = 0;
/*      */     public static final int EvtChannelClockTypeQPC = 1;
/*      */   }
/*      */   
/*      */   public static interface EVT_CHANNEL_ISOLATION_TYPE {
/*      */     public static final int EvtChannelIsolationTypeApplication = 0;
/*      */     public static final int EvtChannelIsolationTypeSystem = 1;
/*      */     public static final int EvtChannelIsolationTypeCustom = 2;
/*      */   }
/*      */   
/*      */   public static interface EVT_CHANNEL_TYPE {
/*      */     public static final int EvtChannelTypeAdmin = 0;
/*      */     public static final int EvtChannelTypeOperational = 1;
/*      */     public static final int EvtChannelTypeAnalytic = 2;
/*      */     public static final int EvtChannelTypeDebug = 3;
/*      */   }
/*      */   
/*      */   public static interface EVT_CHANNEL_CONFIG_PROPERTY_ID {
/*      */     public static final int EvtChannelConfigEnabled = 0;
/*      */     public static final int EvtChannelConfigIsolation = 1;
/*      */     public static final int EvtChannelConfigType = 2;
/*      */     public static final int EvtChannelConfigOwningPublisher = 3;
/*      */     public static final int EvtChannelConfigClassicEventlog = 4;
/*      */     public static final int EvtChannelConfigAccess = 5;
/*      */     public static final int EvtChannelLoggingConfigRetention = 6;
/*      */     public static final int EvtChannelLoggingConfigAutoBackup = 7;
/*      */     public static final int EvtChannelLoggingConfigMaxSize = 8;
/*      */     public static final int EvtChannelLoggingConfigLogFilePath = 9;
/*      */     public static final int EvtChannelPublishingConfigLevel = 10;
/*      */     public static final int EvtChannelPublishingConfigKeywords = 11;
/*      */     public static final int EvtChannelPublishingConfigControlGuid = 12;
/*      */     public static final int EvtChannelPublishingConfigBufferSize = 13;
/*      */     public static final int EvtChannelPublishingConfigMinBuffers = 14;
/*      */     public static final int EvtChannelPublishingConfigMaxBuffers = 15;
/*      */     public static final int EvtChannelPublishingConfigLatency = 16;
/*      */     public static final int EvtChannelPublishingConfigClockType = 17;
/*      */     public static final int EvtChannelPublishingConfigSidType = 18;
/*      */     public static final int EvtChannelPublisherList = 19;
/*      */     public static final int EvtChannelPublishingConfigFileMax = 20;
/*      */     public static final int EvtChannelConfigPropertyIdEND = 21;
/*      */   }
/*      */   
/*      */   public static interface EVT_EXPORTLOG_FLAGS {
/*      */     public static final int EvtExportLogChannelPath = 1;
/*      */     public static final int EvtExportLogFilePath = 2;
/*      */     public static final int EvtExportLogTolerateQueryErrors = 4096;
/*      */     public static final int EvtExportLogOverwrite = 8192;
/*      */   }
/*      */   
/*      */   public static interface EVT_LOG_PROPERTY_ID {
/*      */     public static final int EvtLogCreationTime = 0;
/*      */     public static final int EvtLogLastAccessTime = 1;
/*      */     public static final int EvtLogLastWriteTime = 2;
/*      */     public static final int EvtLogFileSize = 3;
/*      */     public static final int EvtLogAttributes = 4;
/*      */     public static final int EvtLogNumberOfLogRecords = 5;
/*      */     public static final int EvtLogOldestRecordNumber = 6;
/*      */     public static final int EvtLogFull = 7;
/*      */   }
/*      */   
/*      */   public static interface EVT_OPEN_LOG_FLAGS {
/*      */     public static final int EvtOpenChannelPath = 1;
/*      */     public static final int EvtOpenFilePath = 2;
/*      */   }
/*      */   
/*      */   public static interface EVT_FORMAT_MESSAGE_FLAGS {
/*      */     public static final int EvtFormatMessageEvent = 1;
/*      */     public static final int EvtFormatMessageLevel = 2;
/*      */     public static final int EvtFormatMessageTask = 3;
/*      */     public static final int EvtFormatMessageOpcode = 4;
/*      */     public static final int EvtFormatMessageKeyword = 5;
/*      */     public static final int EvtFormatMessageChannel = 6;
/*      */     public static final int EvtFormatMessageProvider = 7;
/*      */     public static final int EvtFormatMessageId = 8;
/*      */     public static final int EvtFormatMessageXml = 9;
/*      */   }
/*      */   
/*      */   public static interface EVT_RENDER_FLAGS {
/*      */     public static final int EvtRenderEventValues = 0;
/*      */     public static final int EvtRenderEventXml = 1;
/*      */     public static final int EvtRenderBookmark = 2;
/*      */   }
/*      */   
/*      */   public static interface EVT_RENDER_CONTEXT_FLAGS {
/*      */     public static final int EvtRenderContextValues = 0;
/*      */     public static final int EvtRenderContextSystem = 1;
/*      */     public static final int EvtRenderContextUser = 2;
/*      */   }
/*      */   
/*      */   public static interface EVT_SYSTEM_PROPERTY_ID {
/*      */     public static final int EvtSystemProviderName = 0;
/*      */     public static final int EvtSystemProviderGuid = 1;
/*      */     public static final int EvtSystemEventID = 2;
/*      */     public static final int EvtSystemQualifiers = 3;
/*      */     public static final int EvtSystemLevel = 4;
/*      */     public static final int EvtSystemTask = 5;
/*      */     public static final int EvtSystemOpcode = 6;
/*      */     public static final int EvtSystemKeywords = 7;
/*      */     public static final int EvtSystemTimeCreated = 8;
/*      */     public static final int EvtSystemEventRecordId = 9;
/*      */     public static final int EvtSystemActivityID = 10;
/*      */     public static final int EvtSystemRelatedActivityID = 11;
/*      */     public static final int EvtSystemProcessID = 12;
/*      */     public static final int EvtSystemThreadID = 13;
/*      */     public static final int EvtSystemChannel = 14;
/*      */     public static final int EvtSystemComputer = 15;
/*      */     public static final int EvtSystemUserID = 16;
/*      */     public static final int EvtSystemVersion = 17;
/*      */     public static final int EvtSystemPropertyIdEND = 18;
/*      */   }
/*      */   
/*      */   public static interface EVT_SUBSCRIBE_NOTIFY_ACTION {
/*      */     public static final int EvtSubscribeActionError = 0;
/*      */     public static final int EvtSubscribeActionDeliver = 1;
/*      */   }
/*      */   
/*      */   public static interface EVT_SUBSCRIBE_FLAGS {
/*      */     public static final int EvtSubscribeToFutureEvents = 1;
/*      */     public static final int EvtSubscribeStartAtOldestRecord = 2;
/*      */     public static final int EvtSubscribeStartAfterBookmark = 3;
/*      */     public static final int EvtSubscribeOriginMask = 3;
/*      */     public static final int EvtSubscribeTolerateQueryErrors = 4096;
/*      */     public static final int EvtSubscribeStrict = 65536;
/*      */   }
/*      */   
/*      */   public static interface EVT_SEEK_FLAGS {
/*      */     public static final int EvtSeekRelativeToFirst = 1;
/*      */     public static final int EvtSeekRelativeToLast = 2;
/*      */     public static final int EvtSeekRelativeToCurrent = 3;
/*      */     public static final int EvtSeekRelativeToBookmark = 4;
/*      */     public static final int EvtSeekOriginMask = 7;
/*      */     public static final int EvtSeekStrict = 65536;
/*      */   }
/*      */   
/*      */   public static interface EVT_QUERY_FLAGS {
/*      */     public static final int EvtQueryChannelPath = 1;
/*      */     public static final int EvtQueryFilePath = 2;
/*      */     public static final int EvtQueryForwardDirection = 256;
/*      */     public static final int EvtQueryReverseDirection = 512;
/*      */     public static final int EvtQueryTolerateQueryErrors = 4096;
/*      */   }
/*      */   
/*      */   public static interface EVT_RPC_LOGIN_FLAGS {
/*      */     public static final int EvtRpcLoginAuthDefault = 0;
/*      */     public static final int EvtRpcLoginAuthNegotiate = 1;
/*      */     public static final int EvtRpcLoginAuthKerberos = 2;
/*      */     public static final int EvtRpcLoginAuthNTLM = 3;
/*      */   }
/*      */   
/*      */   public static interface EVT_LOGIN_CLASS {
/*      */     public static final int EvtRpcLogin = 1;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Winevt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */