package com.sun.jna.platform.win32;

import com.sun.jna.ptr.IntByReference;
import java.util.Arrays;

public abstract class PsapiUtil {
   public static int[] enumProcesses() {
      int size = 0;
      int[] lpidProcess = null;
      IntByReference lpcbNeeded = new IntByReference();

      int[] lpidProcess;
      do {
         size += 1024;
         lpidProcess = new int[size];
         if (!Psapi.INSTANCE.EnumProcesses(lpidProcess, size * 4, lpcbNeeded)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }
      } while(size == lpcbNeeded.getValue() / 4);

      return Arrays.copyOf(lpidProcess, lpcbNeeded.getValue() / 4);
   }
}
