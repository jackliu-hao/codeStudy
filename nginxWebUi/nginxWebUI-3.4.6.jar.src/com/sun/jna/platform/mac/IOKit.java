/*     */ package com.sun.jna.platform.mac;
/*     */ 
/*     */ import com.sun.jna.Library;
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.PointerType;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.LongByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
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
/*     */ public interface IOKit
/*     */   extends Library
/*     */ {
/*     */   public static final int kIORegistryIterateRecursively = 1;
/*     */   public static final int kIORegistryIterateParents = 2;
/*     */   public static final int kIOReturnNoDevice = -536870208;
/*     */   public static final double kIOPSTimeRemainingUnlimited = -2.0D;
/*     */   public static final double kIOPSTimeRemainingUnknown = -1.0D;
/*     */   
/*     */   int IOMasterPort(int paramInt, IntByReference paramIntByReference);
/*     */   
/*     */   CoreFoundation.CFMutableDictionaryRef IOServiceMatching(String paramString);
/*     */   
/*     */   CoreFoundation.CFMutableDictionaryRef IOServiceNameMatching(String paramString);
/*     */   
/*     */   CoreFoundation.CFMutableDictionaryRef IOBSDNameMatching(int paramInt1, int paramInt2, String paramString);
/*     */   
/*     */   IOService IOServiceGetMatchingService(int paramInt, CoreFoundation.CFDictionaryRef paramCFDictionaryRef);
/*     */   
/*     */   int IOServiceGetMatchingServices(int paramInt, CoreFoundation.CFDictionaryRef paramCFDictionaryRef, PointerByReference paramPointerByReference);
/*     */   
/*     */   IORegistryEntry IOIteratorNext(IOIterator paramIOIterator);
/*     */   
/*     */   CoreFoundation.CFTypeRef IORegistryEntryCreateCFProperty(IORegistryEntry paramIORegistryEntry, CoreFoundation.CFStringRef paramCFStringRef, CoreFoundation.CFAllocatorRef paramCFAllocatorRef, int paramInt);
/*     */   
/*     */   int IORegistryEntryCreateCFProperties(IORegistryEntry paramIORegistryEntry, PointerByReference paramPointerByReference, CoreFoundation.CFAllocatorRef paramCFAllocatorRef, int paramInt);
/*     */   
/*     */   CoreFoundation.CFTypeRef IORegistryEntrySearchCFProperty(IORegistryEntry paramIORegistryEntry, String paramString, CoreFoundation.CFStringRef paramCFStringRef, CoreFoundation.CFAllocatorRef paramCFAllocatorRef, int paramInt);
/*     */   
/*  51 */   public static final IOKit INSTANCE = (IOKit)Native.load("IOKit", IOKit.class);
/*     */ 
/*     */   
/*     */   int IORegistryEntryGetRegistryEntryID(IORegistryEntry paramIORegistryEntry, LongByReference paramLongByReference);
/*     */   
/*     */   int IORegistryEntryGetName(IORegistryEntry paramIORegistryEntry, Pointer paramPointer);
/*     */   
/*     */   int IORegistryEntryGetChildIterator(IORegistryEntry paramIORegistryEntry, String paramString, PointerByReference paramPointerByReference);
/*     */   
/*     */   int IORegistryEntryGetChildEntry(IORegistryEntry paramIORegistryEntry, String paramString, PointerByReference paramPointerByReference);
/*     */   
/*     */   int IORegistryEntryGetParentEntry(IORegistryEntry paramIORegistryEntry, String paramString, PointerByReference paramPointerByReference);
/*     */   
/*     */   IORegistryEntry IORegistryGetRootEntry(int paramInt);
/*     */   
/*     */   boolean IOObjectConformsTo(IOObject paramIOObject, String paramString);
/*     */   
/*     */   int IOObjectRelease(IOObject paramIOObject);
/*     */   
/*     */   int IOServiceOpen(IOService paramIOService, int paramInt1, int paramInt2, PointerByReference paramPointerByReference);
/*     */   
/*     */   int IOServiceGetBusyState(IOService paramIOService, IntByReference paramIntByReference);
/*     */   
/*     */   int IOServiceClose(IOConnect paramIOConnect);
/*     */   
/*     */   CoreFoundation.CFTypeRef IOPSCopyPowerSourcesInfo();
/*     */   
/*     */   CoreFoundation.CFArrayRef IOPSCopyPowerSourcesList(CoreFoundation.CFTypeRef paramCFTypeRef);
/*     */   
/*     */   CoreFoundation.CFDictionaryRef IOPSGetPowerSourceDescription(CoreFoundation.CFTypeRef paramCFTypeRef1, CoreFoundation.CFTypeRef paramCFTypeRef2);
/*     */   
/*     */   double IOPSGetTimeRemainingEstimate();
/*     */   
/*     */   public static class IOObject
/*     */     extends PointerType
/*     */   {
/*     */     public IOObject(Pointer p) {
/*  88 */       super(p);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public IOObject() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean conformsTo(String className) {
/* 100 */       return IOKit.INSTANCE.IOObjectConformsTo(this, className);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int release() {
/* 109 */       return IOKit.INSTANCE.IOObjectRelease(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IOIterator
/*     */     extends IOObject
/*     */   {
/*     */     public IOIterator() {}
/*     */ 
/*     */     
/*     */     public IOIterator(Pointer p) {
/* 122 */       super(p);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public IOKit.IORegistryEntry next() {
/* 133 */       return IOKit.INSTANCE.IOIteratorNext(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IORegistryEntry
/*     */     extends IOObject
/*     */   {
/*     */     public IORegistryEntry() {}
/*     */ 
/*     */     
/*     */     public IORegistryEntry(Pointer p) {
/* 146 */       super(p);
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
/*     */     public long getRegistryEntryID() {
/* 158 */       LongByReference id = new LongByReference();
/* 159 */       int kr = IOKit.INSTANCE.IORegistryEntryGetRegistryEntryID(this, id);
/* 160 */       if (kr != 0) {
/* 161 */         throw new IOReturnException(kr);
/*     */       }
/* 163 */       return id.getValue();
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
/*     */     public String getName() {
/* 175 */       Memory name = new Memory(128L);
/* 176 */       int kr = IOKit.INSTANCE.IORegistryEntryGetName(this, (Pointer)name);
/* 177 */       if (kr != 0) {
/* 178 */         throw new IOReturnException(kr);
/*     */       }
/* 180 */       return name.getString(0L);
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
/*     */     public IOKit.IOIterator getChildIterator(String plane) {
/* 197 */       PointerByReference iter = new PointerByReference();
/* 198 */       int kr = IOKit.INSTANCE.IORegistryEntryGetChildIterator(this, plane, iter);
/* 199 */       if (kr != 0) {
/* 200 */         throw new IOReturnException(kr);
/*     */       }
/* 202 */       return new IOKit.IOIterator(iter.getValue());
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
/*     */     public IORegistryEntry getChildEntry(String plane) {
/* 216 */       PointerByReference child = new PointerByReference();
/* 217 */       int kr = IOKit.INSTANCE.IORegistryEntryGetChildEntry(this, plane, child);
/* 218 */       if (kr == -536870208)
/* 219 */         return null; 
/* 220 */       if (kr != 0) {
/* 221 */         throw new IOReturnException(kr);
/*     */       }
/* 223 */       return new IORegistryEntry(child.getValue());
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
/*     */     public IORegistryEntry getParentEntry(String plane) {
/* 237 */       PointerByReference parent = new PointerByReference();
/* 238 */       int kr = IOKit.INSTANCE.IORegistryEntryGetParentEntry(this, plane, parent);
/* 239 */       if (kr == -536870208)
/* 240 */         return null; 
/* 241 */       if (kr != 0) {
/* 242 */         throw new IOReturnException(kr);
/*     */       }
/* 244 */       return new IORegistryEntry(parent.getValue());
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
/*     */     public CoreFoundation.CFTypeRef createCFProperty(CoreFoundation.CFStringRef key) {
/* 258 */       return IOKit.INSTANCE.IORegistryEntryCreateCFProperty(this, key, CoreFoundation.INSTANCE.CFAllocatorGetDefault(), 0);
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
/*     */     public CoreFoundation.CFMutableDictionaryRef createCFProperties() {
/* 275 */       PointerByReference properties = new PointerByReference();
/* 276 */       int kr = IOKit.INSTANCE.IORegistryEntryCreateCFProperties(this, properties, CoreFoundation.INSTANCE
/* 277 */           .CFAllocatorGetDefault(), 0);
/* 278 */       if (kr != 0) {
/* 279 */         throw new IOReturnException(kr);
/*     */       }
/* 281 */       return new CoreFoundation.CFMutableDictionaryRef(properties.getValue());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     CoreFoundation.CFTypeRef searchCFProperty(String plane, CoreFoundation.CFStringRef key, int options) {
/* 304 */       return IOKit.INSTANCE.IORegistryEntrySearchCFProperty(this, plane, key, CoreFoundation.INSTANCE
/* 305 */           .CFAllocatorGetDefault(), options);
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
/*     */     public String getStringProperty(String key) {
/* 317 */       String value = null;
/* 318 */       CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
/* 319 */       CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
/* 320 */       keyAsCFString.release();
/* 321 */       if (valueAsCFType != null) {
/* 322 */         CoreFoundation.CFStringRef valueAsCFString = new CoreFoundation.CFStringRef(valueAsCFType.getPointer());
/* 323 */         value = valueAsCFString.stringValue();
/* 324 */         valueAsCFType.release();
/*     */       } 
/* 326 */       return value;
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
/*     */     public Long getLongProperty(String key) {
/* 343 */       Long value = null;
/* 344 */       CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
/* 345 */       CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
/* 346 */       keyAsCFString.release();
/* 347 */       if (valueAsCFType != null) {
/* 348 */         CoreFoundation.CFNumberRef valueAsCFNumber = new CoreFoundation.CFNumberRef(valueAsCFType.getPointer());
/* 349 */         value = Long.valueOf(valueAsCFNumber.longValue());
/* 350 */         valueAsCFType.release();
/*     */       } 
/* 352 */       return value;
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
/*     */     public Integer getIntegerProperty(String key) {
/* 369 */       Integer value = null;
/* 370 */       CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
/* 371 */       CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
/* 372 */       keyAsCFString.release();
/* 373 */       if (valueAsCFType != null) {
/* 374 */         CoreFoundation.CFNumberRef valueAsCFNumber = new CoreFoundation.CFNumberRef(valueAsCFType.getPointer());
/* 375 */         value = Integer.valueOf(valueAsCFNumber.intValue());
/* 376 */         valueAsCFType.release();
/*     */       } 
/* 378 */       return value;
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
/*     */     public Double getDoubleProperty(String key) {
/* 395 */       Double value = null;
/* 396 */       CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
/* 397 */       CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
/* 398 */       keyAsCFString.release();
/* 399 */       if (valueAsCFType != null) {
/* 400 */         CoreFoundation.CFNumberRef valueAsCFNumber = new CoreFoundation.CFNumberRef(valueAsCFType.getPointer());
/* 401 */         value = Double.valueOf(valueAsCFNumber.doubleValue());
/* 402 */         valueAsCFType.release();
/*     */       } 
/* 404 */       return value;
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
/*     */     public Boolean getBooleanProperty(String key) {
/* 416 */       Boolean value = null;
/* 417 */       CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
/* 418 */       CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
/* 419 */       keyAsCFString.release();
/* 420 */       if (valueAsCFType != null) {
/* 421 */         CoreFoundation.CFBooleanRef valueAsCFBoolean = new CoreFoundation.CFBooleanRef(valueAsCFType.getPointer());
/* 422 */         value = Boolean.valueOf(valueAsCFBoolean.booleanValue());
/* 423 */         valueAsCFType.release();
/*     */       } 
/* 425 */       return value;
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
/*     */     public byte[] getByteArrayProperty(String key) {
/* 437 */       byte[] value = null;
/* 438 */       CoreFoundation.CFStringRef keyAsCFString = CoreFoundation.CFStringRef.createCFString(key);
/* 439 */       CoreFoundation.CFTypeRef valueAsCFType = createCFProperty(keyAsCFString);
/* 440 */       keyAsCFString.release();
/* 441 */       if (valueAsCFType != null) {
/* 442 */         CoreFoundation.CFDataRef valueAsCFData = new CoreFoundation.CFDataRef(valueAsCFType.getPointer());
/* 443 */         int length = valueAsCFData.getLength();
/* 444 */         Pointer p = valueAsCFData.getBytePtr();
/* 445 */         value = p.getByteArray(0L, length);
/* 446 */         valueAsCFType.release();
/*     */       } 
/* 448 */       return value;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IOService
/*     */     extends IORegistryEntry
/*     */   {
/*     */     public IOService() {}
/*     */ 
/*     */     
/*     */     public IOService(Pointer p) {
/* 461 */       super(p);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IOConnect
/*     */     extends IOService
/*     */   {
/*     */     public IOConnect() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public IOConnect(Pointer p) {
/* 476 */       super(p);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\mac\IOKit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */