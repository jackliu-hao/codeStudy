/*     */ package com.sun.jna.platform.mac;
/*     */ 
/*     */ import com.sun.jna.Library;
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.NativeLong;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.PointerType;
/*     */ import com.sun.jna.ptr.ByReference;
/*     */ import com.sun.jna.ptr.ByteByReference;
/*     */ import com.sun.jna.ptr.DoubleByReference;
/*     */ import com.sun.jna.ptr.FloatByReference;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.LongByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import com.sun.jna.ptr.ShortByReference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface CoreFoundation
/*     */   extends Library
/*     */ {
/*  61 */   public static final CoreFoundation INSTANCE = (CoreFoundation)Native.load("CoreFoundation", CoreFoundation.class);
/*     */   
/*     */   public static final int kCFNotFound = -1;
/*     */   
/*     */   public static final int kCFStringEncodingASCII = 1536;
/*     */   
/*     */   public static final int kCFStringEncodingUTF8 = 134217984;
/*  68 */   public static final CFTypeID ARRAY_TYPE_ID = INSTANCE.CFArrayGetTypeID();
/*  69 */   public static final CFTypeID BOOLEAN_TYPE_ID = INSTANCE.CFBooleanGetTypeID();
/*  70 */   public static final CFTypeID DATA_TYPE_ID = INSTANCE.CFDataGetTypeID();
/*  71 */   public static final CFTypeID DATE_TYPE_ID = INSTANCE.CFDateGetTypeID();
/*  72 */   public static final CFTypeID DICTIONARY_TYPE_ID = INSTANCE.CFDictionaryGetTypeID();
/*  73 */   public static final CFTypeID NUMBER_TYPE_ID = INSTANCE.CFNumberGetTypeID(); CFStringRef CFStringCreateWithCharacters(CFAllocatorRef paramCFAllocatorRef, char[] paramArrayOfchar, CFIndex paramCFIndex); CFNumberRef CFNumberCreate(CFAllocatorRef paramCFAllocatorRef, CFIndex paramCFIndex, ByReference paramByReference); CFArrayRef CFArrayCreate(CFAllocatorRef paramCFAllocatorRef, Pointer paramPointer1, CFIndex paramCFIndex, Pointer paramPointer2); CFDataRef CFDataCreate(CFAllocatorRef paramCFAllocatorRef, Pointer paramPointer, CFIndex paramCFIndex); CFMutableDictionaryRef CFDictionaryCreateMutable(CFAllocatorRef paramCFAllocatorRef, CFIndex paramCFIndex, Pointer paramPointer1, Pointer paramPointer2); CFStringRef CFCopyDescription(CFTypeRef paramCFTypeRef); void CFRelease(CFTypeRef paramCFTypeRef); CFTypeRef CFRetain(CFTypeRef paramCFTypeRef); CFIndex CFGetRetainCount(CFTypeRef paramCFTypeRef); Pointer CFDictionaryGetValue(CFDictionaryRef paramCFDictionaryRef, PointerType paramPointerType); byte CFDictionaryGetValueIfPresent(CFDictionaryRef paramCFDictionaryRef, PointerType paramPointerType, PointerByReference paramPointerByReference); void CFDictionarySetValue(CFMutableDictionaryRef paramCFMutableDictionaryRef, PointerType paramPointerType1, PointerType paramPointerType2); byte CFStringGetCString(CFStringRef paramCFStringRef, Pointer paramPointer, CFIndex paramCFIndex, int paramInt); byte CFBooleanGetValue(CFBooleanRef paramCFBooleanRef); CFIndex CFArrayGetCount(CFArrayRef paramCFArrayRef); Pointer CFArrayGetValueAtIndex(CFArrayRef paramCFArrayRef, CFIndex paramCFIndex);
/*  74 */   public static final CFTypeID STRING_TYPE_ID = INSTANCE.CFStringGetTypeID(); CFIndex CFNumberGetType(CFNumberRef paramCFNumberRef); byte CFNumberGetValue(CFNumberRef paramCFNumberRef, CFIndex paramCFIndex, ByReference paramByReference); CFIndex CFStringGetLength(CFStringRef paramCFStringRef);
/*     */   CFIndex CFStringGetMaximumSizeForEncoding(CFIndex paramCFIndex, int paramInt);
/*     */   CFAllocatorRef CFAllocatorGetDefault();
/*     */   CFIndex CFDataGetLength(CFDataRef paramCFDataRef);
/*     */   Pointer CFDataGetBytePtr(CFDataRef paramCFDataRef);
/*     */   CFTypeID CFGetTypeID(CFTypeRef paramCFTypeRef);
/*     */   CFTypeID CFArrayGetTypeID();
/*     */   CFTypeID CFBooleanGetTypeID();
/*     */   CFTypeID CFDateGetTypeID();
/*     */   CFTypeID CFDataGetTypeID();
/*     */   CFTypeID CFDictionaryGetTypeID();
/*     */   CFTypeID CFNumberGetTypeID();
/*     */   CFTypeID CFStringGetTypeID();
/*     */   public static class CFTypeRef extends PointerType { public CFTypeRef(Pointer p) {
/*  88 */       super(p);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public CFTypeRef() {}
/*     */ 
/*     */     
/*     */     public CoreFoundation.CFTypeID getTypeID() {
/*  97 */       if (getPointer() == null) {
/*  98 */         return new CoreFoundation.CFTypeID(0L);
/*     */       }
/* 100 */       return CoreFoundation.INSTANCE.CFGetTypeID(this);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isTypeID(CoreFoundation.CFTypeID typeID) {
/* 111 */       return getTypeID().equals(typeID);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void retain() {
/* 118 */       CoreFoundation.INSTANCE.CFRetain(this);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void release() {
/* 125 */       CoreFoundation.INSTANCE.CFRelease(this);
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFAllocatorRef
/*     */     extends CFTypeRef {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFNumberRef
/*     */     extends CFTypeRef
/*     */   {
/*     */     public CFNumberRef() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public CFNumberRef(Pointer p) {
/* 146 */       super(p);
/* 147 */       if (!isTypeID(CoreFoundation.NUMBER_TYPE_ID)) {
/* 148 */         throw new ClassCastException("Unable to cast to CFNumber. Type ID: " + getTypeID());
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long longValue() {
/* 164 */       LongByReference lbr = new LongByReference();
/* 165 */       CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberLongLongType.typeIndex(), (ByReference)lbr);
/* 166 */       return lbr.getValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int intValue() {
/* 181 */       IntByReference ibr = new IntByReference();
/* 182 */       CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberIntType.typeIndex(), (ByReference)ibr);
/* 183 */       return ibr.getValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public short shortValue() {
/* 198 */       ShortByReference sbr = new ShortByReference();
/* 199 */       CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberShortType.typeIndex(), (ByReference)sbr);
/* 200 */       return sbr.getValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public byte byteValue() {
/* 215 */       ByteByReference bbr = new ByteByReference();
/* 216 */       CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberCharType.typeIndex(), (ByReference)bbr);
/* 217 */       return bbr.getValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double doubleValue() {
/* 232 */       DoubleByReference dbr = new DoubleByReference();
/* 233 */       CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberDoubleType.typeIndex(), (ByReference)dbr);
/* 234 */       return dbr.getValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public float floatValue() {
/* 249 */       FloatByReference fbr = new FloatByReference();
/* 250 */       CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberFloatType.typeIndex(), (ByReference)fbr);
/* 251 */       return fbr.getValue();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum CFNumberType
/*     */   {
/* 261 */     unusedZero, kCFNumberSInt8Type, kCFNumberSInt16Type, kCFNumberSInt32Type, kCFNumberSInt64Type,
/* 262 */     kCFNumberFloat32Type, kCFNumberFloat64Type, kCFNumberCharType, kCFNumberShortType, kCFNumberIntType,
/* 263 */     kCFNumberLongType, kCFNumberLongLongType, kCFNumberFloatType, kCFNumberDoubleType, kCFNumberCFIndexType,
/* 264 */     kCFNumberNSIntegerType, kCFNumberCGFloatType, kCFNumberMaxType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CoreFoundation.CFIndex typeIndex() {
/* 272 */       return new CoreFoundation.CFIndex(ordinal());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFBooleanRef
/*     */     extends CFTypeRef
/*     */   {
/*     */     public CFBooleanRef() {}
/*     */ 
/*     */     
/*     */     public CFBooleanRef(Pointer p) {
/* 285 */       super(p);
/* 286 */       if (!isTypeID(CoreFoundation.BOOLEAN_TYPE_ID)) {
/* 287 */         throw new ClassCastException("Unable to cast to CFBoolean. Type ID: " + getTypeID());
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean booleanValue() {
/* 297 */       return (0 != CoreFoundation.INSTANCE.CFBooleanGetValue(this));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFArrayRef
/*     */     extends CFTypeRef
/*     */   {
/*     */     public CFArrayRef() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CFArrayRef(Pointer p) {
/* 314 */       super(p);
/* 315 */       if (!isTypeID(CoreFoundation.ARRAY_TYPE_ID)) {
/* 316 */         throw new ClassCastException("Unable to cast to CFArray. Type ID: " + getTypeID());
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getCount() {
/* 326 */       return CoreFoundation.INSTANCE.CFArrayGetCount(this).intValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Pointer getValueAtIndex(int idx) {
/* 337 */       return CoreFoundation.INSTANCE.CFArrayGetValueAtIndex(this, new CoreFoundation.CFIndex(idx));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFDataRef
/*     */     extends CFTypeRef
/*     */   {
/*     */     public CFDataRef() {}
/*     */ 
/*     */     
/*     */     public CFDataRef(Pointer p) {
/* 350 */       super(p);
/* 351 */       if (!isTypeID(CoreFoundation.DATA_TYPE_ID)) {
/* 352 */         throw new ClassCastException("Unable to cast to CFData. Type ID: " + getTypeID());
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getLength() {
/* 363 */       return CoreFoundation.INSTANCE.CFDataGetLength(this).intValue();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Pointer getBytePtr() {
/* 372 */       return CoreFoundation.INSTANCE.CFDataGetBytePtr(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFDictionaryRef
/*     */     extends CFTypeRef
/*     */   {
/*     */     public CFDictionaryRef() {}
/*     */ 
/*     */     
/*     */     public CFDictionaryRef(Pointer p) {
/* 385 */       super(p);
/* 386 */       if (!isTypeID(CoreFoundation.DICTIONARY_TYPE_ID)) {
/* 387 */         throw new ClassCastException("Unable to cast to CFDictionary. Type ID: " + getTypeID());
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Pointer getValue(PointerType key) {
/* 401 */       return CoreFoundation.INSTANCE.CFDictionaryGetValue(this, key);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean getValueIfPresent(PointerType key, PointerByReference value) {
/* 417 */       return (CoreFoundation.INSTANCE.CFDictionaryGetValueIfPresent(this, key, value) > 0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFMutableDictionaryRef
/*     */     extends CFDictionaryRef
/*     */   {
/*     */     public CFMutableDictionaryRef() {}
/*     */ 
/*     */     
/*     */     public CFMutableDictionaryRef(Pointer p) {
/* 430 */       super(p);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setValue(PointerType key, PointerType value) {
/* 443 */       CoreFoundation.INSTANCE.CFDictionarySetValue(this, key, value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFStringRef
/*     */     extends CFTypeRef
/*     */   {
/*     */     public CFStringRef() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public CFStringRef(Pointer p) {
/* 458 */       super(p);
/* 459 */       if (!isTypeID(CoreFoundation.STRING_TYPE_ID)) {
/* 460 */         throw new ClassCastException("Unable to cast to CFString. Type ID: " + getTypeID());
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static CFStringRef createCFString(String s) {
/* 478 */       char[] chars = s.toCharArray();
/* 479 */       return CoreFoundation.INSTANCE.CFStringCreateWithCharacters(null, chars, new CoreFoundation.CFIndex(chars.length));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String stringValue() {
/* 490 */       CoreFoundation.CFIndex length = CoreFoundation.INSTANCE.CFStringGetLength(this);
/* 491 */       CoreFoundation.CFIndex maxSize = CoreFoundation.INSTANCE.CFStringGetMaximumSizeForEncoding(length, 134217984);
/* 492 */       if (maxSize.intValue() == -1) {
/* 493 */         return null;
/*     */       }
/* 495 */       Memory buf = new Memory(maxSize.longValue());
/* 496 */       if (0 != CoreFoundation.INSTANCE.CFStringGetCString(this, (Pointer)buf, maxSize, 134217984)) {
/* 497 */         return buf.getString(0L, "UTF8");
/*     */       }
/* 499 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFIndex
/*     */     extends NativeLong
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     
/*     */     public CFIndex() {}
/*     */ 
/*     */     
/*     */     public CFIndex(long value) {
/* 515 */       super(value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CFTypeID
/*     */     extends NativeLong
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CFTypeID() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CFTypeID(long value) {
/* 536 */       super(value);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 541 */       if (equals(CoreFoundation.ARRAY_TYPE_ID))
/* 542 */         return "CFArray"; 
/* 543 */       if (equals(CoreFoundation.BOOLEAN_TYPE_ID))
/* 544 */         return "CFBoolean"; 
/* 545 */       if (equals(CoreFoundation.DATA_TYPE_ID))
/* 546 */         return "CFData"; 
/* 547 */       if (equals(CoreFoundation.DATE_TYPE_ID))
/* 548 */         return "CFDate"; 
/* 549 */       if (equals(CoreFoundation.DICTIONARY_TYPE_ID))
/* 550 */         return "CFDictionary"; 
/* 551 */       if (equals(CoreFoundation.NUMBER_TYPE_ID))
/* 552 */         return "CFNumber"; 
/* 553 */       if (equals(CoreFoundation.STRING_TYPE_ID)) {
/* 554 */         return "CFString";
/*     */       }
/* 556 */       return super.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\mac\CoreFoundation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */