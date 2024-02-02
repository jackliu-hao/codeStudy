package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public abstract class WinspoolUtil {
   public static Winspool.PRINTER_INFO_1[] getPrinterInfo1() {
      IntByReference pcbNeeded = new IntByReference();
      IntByReference pcReturned = new IntByReference();
      Winspool.INSTANCE.EnumPrinters(2, (String)null, 1, (Pointer)null, 0, pcbNeeded, pcReturned);
      if (pcbNeeded.getValue() <= 0) {
         return new Winspool.PRINTER_INFO_1[0];
      } else {
         Winspool.PRINTER_INFO_1 pPrinterEnum = new Winspool.PRINTER_INFO_1(pcbNeeded.getValue());
         if (!Winspool.INSTANCE.EnumPrinters(2, (String)null, 1, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            pPrinterEnum.read();
            return (Winspool.PRINTER_INFO_1[])((Winspool.PRINTER_INFO_1[])pPrinterEnum.toArray(pcReturned.getValue()));
         }
      }
   }

   public static Winspool.PRINTER_INFO_2[] getPrinterInfo2() {
      return getPrinterInfo2(2);
   }

   public static Winspool.PRINTER_INFO_2[] getAllPrinterInfo2() {
      return getPrinterInfo2(6);
   }

   private static Winspool.PRINTER_INFO_2[] getPrinterInfo2(int flags) {
      IntByReference pcbNeeded = new IntByReference();
      IntByReference pcReturned = new IntByReference();
      Winspool.INSTANCE.EnumPrinters(flags, (String)null, 2, (Pointer)null, 0, pcbNeeded, pcReturned);
      if (pcbNeeded.getValue() <= 0) {
         return new Winspool.PRINTER_INFO_2[0];
      } else {
         Winspool.PRINTER_INFO_2 pPrinterEnum = new Winspool.PRINTER_INFO_2(pcbNeeded.getValue());
         if (!Winspool.INSTANCE.EnumPrinters(flags, (String)null, 2, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            pPrinterEnum.read();
            return (Winspool.PRINTER_INFO_2[])((Winspool.PRINTER_INFO_2[])pPrinterEnum.toArray(pcReturned.getValue()));
         }
      }
   }

   public static Winspool.PRINTER_INFO_2 getPrinterInfo2(String printerName) {
      IntByReference pcbNeeded = new IntByReference();
      IntByReference pcReturned = new IntByReference();
      WinNT.HANDLEByReference pHandle = new WinNT.HANDLEByReference();
      if (!Winspool.INSTANCE.OpenPrinter(printerName, pHandle, (Winspool.LPPRINTER_DEFAULTS)null)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         Win32Exception we = null;
         Winspool.PRINTER_INFO_2 pinfo2 = null;
         boolean var12 = false;

         Winspool.PRINTER_INFO_2 var6;
         label140: {
            label133: {
               Win32Exception ex;
               label132: {
                  try {
                     var12 = true;
                     Winspool.INSTANCE.GetPrinter(pHandle.getValue(), 2, (Pointer)null, 0, pcbNeeded);
                     if (pcbNeeded.getValue() <= 0) {
                        var6 = new Winspool.PRINTER_INFO_2();
                        var12 = false;
                        break label140;
                     }

                     pinfo2 = new Winspool.PRINTER_INFO_2(pcbNeeded.getValue());
                     if (!Winspool.INSTANCE.GetPrinter(pHandle.getValue(), 2, pinfo2.getPointer(), pcbNeeded.getValue(), pcReturned)) {
                        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                     }

                     pinfo2.read();
                     var12 = false;
                     break label132;
                  } catch (Win32Exception var13) {
                     we = var13;
                     var12 = false;
                  } finally {
                     if (var12) {
                        if (!Winspool.INSTANCE.ClosePrinter(pHandle.getValue())) {
                           Win32Exception ex = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                           if (we != null) {
                              ex.addSuppressedReflected(we);
                           }
                        }

                     }
                  }

                  if (!Winspool.INSTANCE.ClosePrinter(pHandle.getValue())) {
                     ex = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                     if (we != null) {
                        ex.addSuppressedReflected(we);
                     }
                  }
                  break label133;
               }

               if (!Winspool.INSTANCE.ClosePrinter(pHandle.getValue())) {
                  ex = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                  if (we != null) {
                     ex.addSuppressedReflected(we);
                  }
               }
            }

            if (we != null) {
               throw we;
            }

            return pinfo2;
         }

         if (!Winspool.INSTANCE.ClosePrinter(pHandle.getValue())) {
            Win32Exception ex = new Win32Exception(Kernel32.INSTANCE.GetLastError());
            if (we != null) {
               ex.addSuppressedReflected(we);
            }
         }

         return var6;
      }
   }

   public static Winspool.PRINTER_INFO_4[] getPrinterInfo4() {
      IntByReference pcbNeeded = new IntByReference();
      IntByReference pcReturned = new IntByReference();
      Winspool.INSTANCE.EnumPrinters(2, (String)null, 4, (Pointer)null, 0, pcbNeeded, pcReturned);
      if (pcbNeeded.getValue() <= 0) {
         return new Winspool.PRINTER_INFO_4[0];
      } else {
         Winspool.PRINTER_INFO_4 pPrinterEnum = new Winspool.PRINTER_INFO_4(pcbNeeded.getValue());
         if (!Winspool.INSTANCE.EnumPrinters(2, (String)null, 4, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            pPrinterEnum.read();
            return (Winspool.PRINTER_INFO_4[])((Winspool.PRINTER_INFO_4[])pPrinterEnum.toArray(pcReturned.getValue()));
         }
      }
   }

   public static Winspool.JOB_INFO_1[] getJobInfo1(WinNT.HANDLEByReference phPrinter) {
      IntByReference pcbNeeded = new IntByReference();
      IntByReference pcReturned = new IntByReference();
      Winspool.INSTANCE.EnumJobs(phPrinter.getValue(), 0, 255, 1, (Pointer)null, 0, pcbNeeded, pcReturned);
      if (pcbNeeded.getValue() <= 0) {
         return new Winspool.JOB_INFO_1[0];
      } else {
         Winspool.JOB_INFO_1 pJobEnum = new Winspool.JOB_INFO_1(pcbNeeded.getValue());
         if (!Winspool.INSTANCE.EnumJobs(phPrinter.getValue(), 0, 255, 1, pJobEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            pJobEnum.read();
            return (Winspool.JOB_INFO_1[])((Winspool.JOB_INFO_1[])pJobEnum.toArray(pcReturned.getValue()));
         }
      }
   }
}
