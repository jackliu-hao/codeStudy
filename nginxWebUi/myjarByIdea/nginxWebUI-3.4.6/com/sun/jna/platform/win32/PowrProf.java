package com.sun.jna.platform.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface PowrProf extends Library {
   PowrProf INSTANCE = (PowrProf)Native.load("PowrProf", PowrProf.class);

   int CallNtPowerInformation(int var1, Pointer var2, int var3, Pointer var4, int var5);

   public interface POWER_INFORMATION_LEVEL {
      int LastSleepTime = 15;
      int LastWakeTime = 14;
      int ProcessorInformation = 11;
      int SystemBatteryState = 5;
      int SystemExecutionState = 16;
      int SystemPowerCapabilities = 4;
      int SystemPowerInformation = 12;
      int SystemPowerPolicyAc = 0;
      int SystemPowerPolicyCurrent = 8;
      int SystemPowerPolicyDc = 1;
      int SystemReserveHiberFile = 10;
   }
}
