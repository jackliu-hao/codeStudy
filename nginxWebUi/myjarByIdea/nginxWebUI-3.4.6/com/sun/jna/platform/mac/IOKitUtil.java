package com.sun.jna.platform.mac;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class IOKitUtil {
   private static final IOKit IO;
   private static final SystemB SYS;

   private IOKitUtil() {
   }

   public static int getMasterPort() {
      IntByReference port = new IntByReference();
      IO.IOMasterPort(0, port);
      return port.getValue();
   }

   public static IOKit.IORegistryEntry getRoot() {
      int masterPort = getMasterPort();
      IOKit.IORegistryEntry root = IO.IORegistryGetRootEntry(masterPort);
      SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
      return root;
   }

   public static IOKit.IOService getMatchingService(String serviceName) {
      CoreFoundation.CFMutableDictionaryRef dict = IO.IOServiceMatching(serviceName);
      return dict != null ? getMatchingService((CoreFoundation.CFDictionaryRef)dict) : null;
   }

   public static IOKit.IOService getMatchingService(CoreFoundation.CFDictionaryRef matchingDictionary) {
      int masterPort = getMasterPort();
      IOKit.IOService service = IO.IOServiceGetMatchingService(masterPort, matchingDictionary);
      SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
      return service;
   }

   public static IOKit.IOIterator getMatchingServices(String serviceName) {
      CoreFoundation.CFMutableDictionaryRef dict = IO.IOServiceMatching(serviceName);
      return dict != null ? getMatchingServices((CoreFoundation.CFDictionaryRef)dict) : null;
   }

   public static IOKit.IOIterator getMatchingServices(CoreFoundation.CFDictionaryRef matchingDictionary) {
      int masterPort = getMasterPort();
      PointerByReference serviceIterator = new PointerByReference();
      int result = IO.IOServiceGetMatchingServices(masterPort, matchingDictionary, serviceIterator);
      SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
      return result == 0 && serviceIterator.getValue() != null ? new IOKit.IOIterator(serviceIterator.getValue()) : null;
   }

   public static CoreFoundation.CFMutableDictionaryRef getBSDNameMatchingDict(String bsdName) {
      int masterPort = getMasterPort();
      CoreFoundation.CFMutableDictionaryRef result = IO.IOBSDNameMatching(masterPort, 0, bsdName);
      SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
      return result;
   }

   static {
      IO = IOKit.INSTANCE;
      SYS = SystemB.INSTANCE;
   }
}
