package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface DiskArbitration extends Library {
   DiskArbitration INSTANCE = (DiskArbitration)Native.load("DiskArbitration", DiskArbitration.class);

   DASessionRef DASessionCreate(CoreFoundation.CFAllocatorRef var1);

   DADiskRef DADiskCreateFromBSDName(CoreFoundation.CFAllocatorRef var1, DASessionRef var2, String var3);

   DADiskRef DADiskCreateFromIOMedia(CoreFoundation.CFAllocatorRef var1, DASessionRef var2, IOKit.IOObject var3);

   CoreFoundation.CFDictionaryRef DADiskCopyDescription(DADiskRef var1);

   String DADiskGetBSDName(DADiskRef var1);

   public static class DADiskRef extends CoreFoundation.CFTypeRef {
   }

   public static class DASessionRef extends CoreFoundation.CFTypeRef {
   }
}
