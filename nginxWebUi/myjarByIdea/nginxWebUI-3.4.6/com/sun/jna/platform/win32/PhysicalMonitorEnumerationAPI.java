package com.sun.jna.platform.win32;

import com.sun.jna.Structure;

public interface PhysicalMonitorEnumerationAPI {
   int PHYSICAL_MONITOR_DESCRIPTION_SIZE = 128;

   @Structure.FieldOrder({"hPhysicalMonitor", "szPhysicalMonitorDescription"})
   public static class PHYSICAL_MONITOR extends Structure {
      public WinNT.HANDLE hPhysicalMonitor;
      public char[] szPhysicalMonitorDescription = new char[128];
   }
}
