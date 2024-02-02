package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

public interface IOKit extends Library {
   IOKit INSTANCE = (IOKit)Native.load("IOKit", IOKit.class);
   int kIORegistryIterateRecursively = 1;
   int kIORegistryIterateParents = 2;
   int kIOReturnNoDevice = -536870208;
   double kIOPSTimeRemainingUnlimited = -2.0;
   double kIOPSTimeRemainingUnknown = -1.0;

   int IOMasterPort(int var1, IntByReference var2);

   CoreFoundation.CFMutableDictionaryRef IOServiceMatching(String var1);

   CoreFoundation.CFMutableDictionaryRef IOServiceNameMatching(String var1);

   CoreFoundation.CFMutableDictionaryRef IOBSDNameMatching(int var1, int var2, String var3);

   IOService IOServiceGetMatchingService(int var1, CoreFoundation.CFDictionaryRef var2);

   int IOServiceGetMatchingServices(int var1, CoreFoundation.CFDictionaryRef var2, PointerByReference var3);

   IORegistryEntry IOIteratorNext(IOIterator var1);

   CoreFoundation.CFTypeRef IORegistryEntryCreateCFProperty(IORegistryEntry var1, CoreFoundation.CFStringRef var2, CoreFoundation.CFAllocatorRef var3, int var4);

   int IORegistryEntryCreateCFProperties(IORegistryEntry var1, PointerByReference var2, CoreFoundation.CFAllocatorRef var3, int var4);

   CoreFoundation.CFTypeRef IORegistryEntrySearchCFProperty(IORegistryEntry var1, String var2, CoreFoundation.CFStringRef var3, CoreFoundation.CFAllocatorRef var4, int var5);

   int IORegistryEntryGetRegistryEntryID(IORegistryEntry var1, LongByReference var2);

   int IORegistryEntryGetName(IORegistryEntry var1, Pointer var2);

   int IORegistryEntryGetChildIterator(IORegistryEntry var1, String var2, PointerByReference var3);

   int IORegistryEntryGetChildEntry(IORegistryEntry var1, String var2, PointerByReference var3);

   int IORegistryEntryGetParentEntry(IORegistryEntry var1, String var2, PointerByReference var3);

   IORegistryEntry IORegistryGetRootEntry(int var1);

   boolean IOObjectConformsTo(IOObject var1, String var2);

   int IOObjectRelease(IOObject var1);

   int IOServiceOpen(IOService var1, int var2, int var3, PointerByReference var4);

   int IOServiceGetBusyState(IOService var1, IntByReference var2);

   int IOServiceClose(IOConnect var1);

   CoreFoundation.CFTypeRef IOPSCopyPowerSourcesInfo();

   CoreFoundation.CFArrayRef IOPSCopyPowerSourcesList(CoreFoundation.CFTypeRef var1);

   CoreFoundation.CFDictionaryRef IOPSGetPowerSourceDescription(CoreFoundation.CFTypeRef var1, CoreFoundation.CFTypeRef var2);

   double IOPSGetTimeRemainingEstimate();

   public static class IOConnect extends IOService {
      public IOConnect() {
      }

      public IOConnect(Pointer p) {
         super(p);
      }
   }

   public static class IOService extends IORegistryEntry {
      public IOService() {
      }

      public IOService(Pointer p) {
         super(p);
      }
   }

   public static class IORegistryEntry extends IOObject {
      public IORegistryEntry() {
      }

      public IORegistryEntry(Pointer p) {
         super(p);
      }

      public long getRegistryEntryID() {
         LongByReference id = new LongByReference();
         int kr = IOKit.INSTANCE.IORegistryEntryGetRegistryEntryID(this, id);
         if (kr != 0) {
            throw new IOReturnException(kr);
         } else {
            return id.getValue();
         }
      }

      public String getName() {
         Memory name = new Memory(128L);
         int kr = IOKit.INSTANCE.IORegistryEntryGetName(this, name);
         if (kr != 0) {
            throw new IOReturnException(kr);
         } else {
            return name.getString(0L);
         }
      }

      public IOIterator getChildIterator(String plane) {
         PointerByReference iter = new PointerByReference();
         int kr = IOKit.INSTANCE.IORegistryEntryGetChildIterator(this, plane, iter);
         if (kr != 0) {
            throw new IOReturnException(kr);
         } else {
            return new IOIterator(iter.getValue());
         }
      }

      public IORegistryEntry getChildEntry(String plane) {
         PointerByReference child = new PointerByReference();
         int kr = IOKit.INSTANCE.IORegistryEntryGetChildEntry(this, plane, child);
         if (kr == -536870208) {
            return null;
         } else if (kr != 0) {
            throw new IOReturnException(kr);
         } else {
            return new IORegistryEntry(child.getValue());
         }
      }

      public IORegistryEntry getParentEntry(String plane) {
         PointerByReference parent = new PointerByReference();
         int kr = IOKit.INSTANCE.IORegistryEntryGetParentEntry(this, plane, parent);
         if (kr == -536870208) {
            return null;
         } else if (kr != 0) {
            throw new IOReturnException(kr);
         } else {
            return new IORegistryEntry(parent.getValue());
         }
      }

      public CoreFoundation.CFTypeRef createCFProperty(CoreFoundation.CFStringRef key) {
         return IOKit.INSTANCE.IORegistryEntryCreateCFProperty(this, key, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), 0);
      }

      public CoreFoundation.CFMutableDictionaryRef createCFProperties() {
         PointerByReference properties = new PointerByReference();
         int kr = IOKit.INSTANCE.IORegistryEntryCreateCFProperties(this, properties, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), 0);
         if (kr != 0) {
            throw new IOReturnException(kr);
         } else {
            return new CoreFoundation.CFMutableDictionaryRef(properties.getValue());
         }
      }

      CoreFoundation.CFTypeRef searchCFProperty(String plane, CoreFoundation.CFStringRef key, int options) {
         return IOKit.INSTANCE.IORegistryEntrySearchCFProperty(this, plane, key, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), options);
      }

      public String getStringProperty(String key) {
         String value = null;
         CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
         CoreFoundation.CFTypeRef valueAsCFType = this.createCFProperty(keyAsCFString);
         keyAsCFString.release();
         if (valueAsCFType != null) {
            CoreFoundation.CFStringRef valueAsCFString = new CoreFoundation.CFStringRef(valueAsCFType.getPointer());
            value = valueAsCFString.stringValue();
            valueAsCFType.release();
         }

         return value;
      }

      public Long getLongProperty(String key) {
         Long value = null;
         CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
         CoreFoundation.CFTypeRef valueAsCFType = this.createCFProperty(keyAsCFString);
         keyAsCFString.release();
         if (valueAsCFType != null) {
            CoreFoundation.CFNumberRef valueAsCFNumber = new CoreFoundation.CFNumberRef(valueAsCFType.getPointer());
            value = valueAsCFNumber.longValue();
            valueAsCFType.release();
         }

         return value;
      }

      public Integer getIntegerProperty(String key) {
         Integer value = null;
         CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
         CoreFoundation.CFTypeRef valueAsCFType = this.createCFProperty(keyAsCFString);
         keyAsCFString.release();
         if (valueAsCFType != null) {
            CoreFoundation.CFNumberRef valueAsCFNumber = new CoreFoundation.CFNumberRef(valueAsCFType.getPointer());
            value = valueAsCFNumber.intValue();
            valueAsCFType.release();
         }

         return value;
      }

      public Double getDoubleProperty(String key) {
         Double value = null;
         CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
         CoreFoundation.CFTypeRef valueAsCFType = this.createCFProperty(keyAsCFString);
         keyAsCFString.release();
         if (valueAsCFType != null) {
            CoreFoundation.CFNumberRef valueAsCFNumber = new CoreFoundation.CFNumberRef(valueAsCFType.getPointer());
            value = valueAsCFNumber.doubleValue();
            valueAsCFType.release();
         }

         return value;
      }

      public Boolean getBooleanProperty(String key) {
         Boolean value = null;
         CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
         CoreFoundation.CFTypeRef valueAsCFType = this.createCFProperty(keyAsCFString);
         keyAsCFString.release();
         if (valueAsCFType != null) {
            CoreFoundation.CFBooleanRef valueAsCFBoolean = new CoreFoundation.CFBooleanRef(valueAsCFType.getPointer());
            value = valueAsCFBoolean.booleanValue();
            valueAsCFType.release();
         }

         return value;
      }

      public byte[] getByteArrayProperty(String key) {
         byte[] value = null;
         CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
         CoreFoundation.CFTypeRef valueAsCFType = this.createCFProperty(keyAsCFString);
         keyAsCFString.release();
         if (valueAsCFType != null) {
            CoreFoundation.CFDataRef valueAsCFData = new CoreFoundation.CFDataRef(valueAsCFType.getPointer());
            int length = valueAsCFData.getLength();
            Pointer p = valueAsCFData.getBytePtr();
            value = p.getByteArray(0L, length);
            valueAsCFType.release();
         }

         return value;
      }
   }

   public static class IOIterator extends IOObject {
      public IOIterator() {
      }

      public IOIterator(Pointer p) {
         super(p);
      }

      public IORegistryEntry next() {
         return IOKit.INSTANCE.IOIteratorNext(this);
      }
   }

   public static class IOObject extends PointerType {
      public IOObject() {
      }

      public IOObject(Pointer p) {
         super(p);
      }

      public boolean conformsTo(String className) {
         return IOKit.INSTANCE.IOObjectConformsTo(this, className);
      }

      public int release() {
         return IOKit.INSTANCE.IOObjectRelease(this);
      }
   }
}
