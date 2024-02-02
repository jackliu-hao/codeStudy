package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

public interface CoreFoundation extends Library {
   CoreFoundation INSTANCE = (CoreFoundation)Native.load("CoreFoundation", CoreFoundation.class);
   int kCFNotFound = -1;
   int kCFStringEncodingASCII = 1536;
   int kCFStringEncodingUTF8 = 134217984;
   CFTypeID ARRAY_TYPE_ID = INSTANCE.CFArrayGetTypeID();
   CFTypeID BOOLEAN_TYPE_ID = INSTANCE.CFBooleanGetTypeID();
   CFTypeID DATA_TYPE_ID = INSTANCE.CFDataGetTypeID();
   CFTypeID DATE_TYPE_ID = INSTANCE.CFDateGetTypeID();
   CFTypeID DICTIONARY_TYPE_ID = INSTANCE.CFDictionaryGetTypeID();
   CFTypeID NUMBER_TYPE_ID = INSTANCE.CFNumberGetTypeID();
   CFTypeID STRING_TYPE_ID = INSTANCE.CFStringGetTypeID();

   CFStringRef CFStringCreateWithCharacters(CFAllocatorRef var1, char[] var2, CFIndex var3);

   CFNumberRef CFNumberCreate(CFAllocatorRef var1, CFIndex var2, ByReference var3);

   CFArrayRef CFArrayCreate(CFAllocatorRef var1, Pointer var2, CFIndex var3, Pointer var4);

   CFDataRef CFDataCreate(CFAllocatorRef var1, Pointer var2, CFIndex var3);

   CFMutableDictionaryRef CFDictionaryCreateMutable(CFAllocatorRef var1, CFIndex var2, Pointer var3, Pointer var4);

   CFStringRef CFCopyDescription(CFTypeRef var1);

   void CFRelease(CFTypeRef var1);

   CFTypeRef CFRetain(CFTypeRef var1);

   CFIndex CFGetRetainCount(CFTypeRef var1);

   Pointer CFDictionaryGetValue(CFDictionaryRef var1, PointerType var2);

   byte CFDictionaryGetValueIfPresent(CFDictionaryRef var1, PointerType var2, PointerByReference var3);

   void CFDictionarySetValue(CFMutableDictionaryRef var1, PointerType var2, PointerType var3);

   byte CFStringGetCString(CFStringRef var1, Pointer var2, CFIndex var3, int var4);

   byte CFBooleanGetValue(CFBooleanRef var1);

   CFIndex CFArrayGetCount(CFArrayRef var1);

   Pointer CFArrayGetValueAtIndex(CFArrayRef var1, CFIndex var2);

   CFIndex CFNumberGetType(CFNumberRef var1);

   byte CFNumberGetValue(CFNumberRef var1, CFIndex var2, ByReference var3);

   CFIndex CFStringGetLength(CFStringRef var1);

   CFIndex CFStringGetMaximumSizeForEncoding(CFIndex var1, int var2);

   CFAllocatorRef CFAllocatorGetDefault();

   CFIndex CFDataGetLength(CFDataRef var1);

   Pointer CFDataGetBytePtr(CFDataRef var1);

   CFTypeID CFGetTypeID(CFTypeRef var1);

   CFTypeID CFArrayGetTypeID();

   CFTypeID CFBooleanGetTypeID();

   CFTypeID CFDateGetTypeID();

   CFTypeID CFDataGetTypeID();

   CFTypeID CFDictionaryGetTypeID();

   CFTypeID CFNumberGetTypeID();

   CFTypeID CFStringGetTypeID();

   public static class CFTypeID extends NativeLong {
      private static final long serialVersionUID = 1L;

      public CFTypeID() {
      }

      public CFTypeID(long value) {
         super(value);
      }

      public String toString() {
         if (this.equals(CoreFoundation.ARRAY_TYPE_ID)) {
            return "CFArray";
         } else if (this.equals(CoreFoundation.BOOLEAN_TYPE_ID)) {
            return "CFBoolean";
         } else if (this.equals(CoreFoundation.DATA_TYPE_ID)) {
            return "CFData";
         } else if (this.equals(CoreFoundation.DATE_TYPE_ID)) {
            return "CFDate";
         } else if (this.equals(CoreFoundation.DICTIONARY_TYPE_ID)) {
            return "CFDictionary";
         } else if (this.equals(CoreFoundation.NUMBER_TYPE_ID)) {
            return "CFNumber";
         } else {
            return this.equals(CoreFoundation.STRING_TYPE_ID) ? "CFString" : super.toString();
         }
      }
   }

   public static class CFIndex extends NativeLong {
      private static final long serialVersionUID = 1L;

      public CFIndex() {
      }

      public CFIndex(long value) {
         super(value);
      }
   }

   public static class CFStringRef extends CFTypeRef {
      public CFStringRef() {
      }

      public CFStringRef(Pointer p) {
         super(p);
         if (!this.isTypeID(CoreFoundation.STRING_TYPE_ID)) {
            throw new ClassCastException("Unable to cast to CFString. Type ID: " + this.getTypeID());
         }
      }

      public static CFStringRef createCFString(String s) {
         char[] chars = s.toCharArray();
         return CoreFoundation.INSTANCE.CFStringCreateWithCharacters((CFAllocatorRef)null, chars, new CFIndex((long)chars.length));
      }

      public String stringValue() {
         CFIndex length = CoreFoundation.INSTANCE.CFStringGetLength(this);
         CFIndex maxSize = CoreFoundation.INSTANCE.CFStringGetMaximumSizeForEncoding(length, 134217984);
         if (maxSize.intValue() == -1) {
            return null;
         } else {
            Memory buf = new Memory(maxSize.longValue());
            return 0 != CoreFoundation.INSTANCE.CFStringGetCString(this, buf, maxSize, 134217984) ? buf.getString(0L, "UTF8") : null;
         }
      }
   }

   public static class CFMutableDictionaryRef extends CFDictionaryRef {
      public CFMutableDictionaryRef() {
      }

      public CFMutableDictionaryRef(Pointer p) {
         super(p);
      }

      public void setValue(PointerType key, PointerType value) {
         CoreFoundation.INSTANCE.CFDictionarySetValue(this, key, value);
      }
   }

   public static class CFDictionaryRef extends CFTypeRef {
      public CFDictionaryRef() {
      }

      public CFDictionaryRef(Pointer p) {
         super(p);
         if (!this.isTypeID(CoreFoundation.DICTIONARY_TYPE_ID)) {
            throw new ClassCastException("Unable to cast to CFDictionary. Type ID: " + this.getTypeID());
         }
      }

      public Pointer getValue(PointerType key) {
         return CoreFoundation.INSTANCE.CFDictionaryGetValue(this, key);
      }

      public boolean getValueIfPresent(PointerType key, PointerByReference value) {
         return CoreFoundation.INSTANCE.CFDictionaryGetValueIfPresent(this, key, value) > 0;
      }
   }

   public static class CFDataRef extends CFTypeRef {
      public CFDataRef() {
      }

      public CFDataRef(Pointer p) {
         super(p);
         if (!this.isTypeID(CoreFoundation.DATA_TYPE_ID)) {
            throw new ClassCastException("Unable to cast to CFData. Type ID: " + this.getTypeID());
         }
      }

      public int getLength() {
         return CoreFoundation.INSTANCE.CFDataGetLength(this).intValue();
      }

      public Pointer getBytePtr() {
         return CoreFoundation.INSTANCE.CFDataGetBytePtr(this);
      }
   }

   public static class CFArrayRef extends CFTypeRef {
      public CFArrayRef() {
      }

      public CFArrayRef(Pointer p) {
         super(p);
         if (!this.isTypeID(CoreFoundation.ARRAY_TYPE_ID)) {
            throw new ClassCastException("Unable to cast to CFArray. Type ID: " + this.getTypeID());
         }
      }

      public int getCount() {
         return CoreFoundation.INSTANCE.CFArrayGetCount(this).intValue();
      }

      public Pointer getValueAtIndex(int idx) {
         return CoreFoundation.INSTANCE.CFArrayGetValueAtIndex(this, new CFIndex((long)idx));
      }
   }

   public static class CFBooleanRef extends CFTypeRef {
      public CFBooleanRef() {
      }

      public CFBooleanRef(Pointer p) {
         super(p);
         if (!this.isTypeID(CoreFoundation.BOOLEAN_TYPE_ID)) {
            throw new ClassCastException("Unable to cast to CFBoolean. Type ID: " + this.getTypeID());
         }
      }

      public boolean booleanValue() {
         return 0 != CoreFoundation.INSTANCE.CFBooleanGetValue(this);
      }
   }

   public static enum CFNumberType {
      unusedZero,
      kCFNumberSInt8Type,
      kCFNumberSInt16Type,
      kCFNumberSInt32Type,
      kCFNumberSInt64Type,
      kCFNumberFloat32Type,
      kCFNumberFloat64Type,
      kCFNumberCharType,
      kCFNumberShortType,
      kCFNumberIntType,
      kCFNumberLongType,
      kCFNumberLongLongType,
      kCFNumberFloatType,
      kCFNumberDoubleType,
      kCFNumberCFIndexType,
      kCFNumberNSIntegerType,
      kCFNumberCGFloatType,
      kCFNumberMaxType;

      public CFIndex typeIndex() {
         return new CFIndex((long)this.ordinal());
      }
   }

   public static class CFNumberRef extends CFTypeRef {
      public CFNumberRef() {
      }

      public CFNumberRef(Pointer p) {
         super(p);
         if (!this.isTypeID(CoreFoundation.NUMBER_TYPE_ID)) {
            throw new ClassCastException("Unable to cast to CFNumber. Type ID: " + this.getTypeID());
         }
      }

      public long longValue() {
         LongByReference lbr = new LongByReference();
         CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberLongLongType.typeIndex(), lbr);
         return lbr.getValue();
      }

      public int intValue() {
         IntByReference ibr = new IntByReference();
         CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberIntType.typeIndex(), ibr);
         return ibr.getValue();
      }

      public short shortValue() {
         ShortByReference sbr = new ShortByReference();
         CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberShortType.typeIndex(), sbr);
         return sbr.getValue();
      }

      public byte byteValue() {
         ByteByReference bbr = new ByteByReference();
         CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberCharType.typeIndex(), bbr);
         return bbr.getValue();
      }

      public double doubleValue() {
         DoubleByReference dbr = new DoubleByReference();
         CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberDoubleType.typeIndex(), dbr);
         return dbr.getValue();
      }

      public float floatValue() {
         FloatByReference fbr = new FloatByReference();
         CoreFoundation.INSTANCE.CFNumberGetValue(this, CoreFoundation.CFNumberType.kCFNumberFloatType.typeIndex(), fbr);
         return fbr.getValue();
      }
   }

   public static class CFAllocatorRef extends CFTypeRef {
   }

   public static class CFTypeRef extends PointerType {
      public CFTypeRef() {
      }

      public CFTypeRef(Pointer p) {
         super(p);
      }

      public CFTypeID getTypeID() {
         return this.getPointer() == null ? new CFTypeID(0L) : CoreFoundation.INSTANCE.CFGetTypeID(this);
      }

      public boolean isTypeID(CFTypeID typeID) {
         return this.getTypeID().equals(typeID);
      }

      public void retain() {
         CoreFoundation.INSTANCE.CFRetain(this);
      }

      public void release() {
         CoreFoundation.INSTANCE.CFRelease(this);
      }
   }
}
