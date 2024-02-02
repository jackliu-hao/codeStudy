package com.sun.jna.platform.unix;

public interface Reboot {
   int RB_AUTOBOOT = 19088743;
   int RB_HALT_SYSTEM = -839974621;
   int RB_ENABLE_CAD = -1985229329;
   int RB_DISABLE_CAD = 0;
   int RB_POWER_OFF = 1126301404;
   int RB_SW_SUSPEND = -805241630;
   int RB_KEXEC = 1163412803;

   int reboot(int var1);
}
