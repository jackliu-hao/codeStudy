/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.ptr.IntByReference;
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
/*     */ public abstract class WinspoolUtil
/*     */ {
/*     */   public static Winspool.PRINTER_INFO_1[] getPrinterInfo1() {
/*  41 */     IntByReference pcbNeeded = new IntByReference();
/*  42 */     IntByReference pcReturned = new IntByReference();
/*  43 */     Winspool.INSTANCE.EnumPrinters(2, null, 1, null, 0, pcbNeeded, pcReturned);
/*     */     
/*  45 */     if (pcbNeeded.getValue() <= 0) {
/*  46 */       return new Winspool.PRINTER_INFO_1[0];
/*     */     }
/*     */     
/*  49 */     Winspool.PRINTER_INFO_1 pPrinterEnum = new Winspool.PRINTER_INFO_1(pcbNeeded.getValue());
/*  50 */     if (!Winspool.INSTANCE.EnumPrinters(2, null, 1, pPrinterEnum
/*  51 */         .getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned))
/*     */     {
/*  53 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/*  56 */     pPrinterEnum.read();
/*     */     
/*  58 */     return (Winspool.PRINTER_INFO_1[])pPrinterEnum.toArray(pcReturned.getValue());
/*     */   }
/*     */   
/*     */   public static Winspool.PRINTER_INFO_2[] getPrinterInfo2() {
/*  62 */     return getPrinterInfo2(2);
/*     */   }
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
/*     */   public static Winspool.PRINTER_INFO_2[] getAllPrinterInfo2() {
/*  75 */     return getPrinterInfo2(6);
/*     */   }
/*     */   
/*     */   private static Winspool.PRINTER_INFO_2[] getPrinterInfo2(int flags) {
/*  79 */     IntByReference pcbNeeded = new IntByReference();
/*  80 */     IntByReference pcReturned = new IntByReference();
/*  81 */     Winspool.INSTANCE.EnumPrinters(flags, null, 2, null, 0, pcbNeeded, pcReturned);
/*  82 */     if (pcbNeeded.getValue() <= 0) {
/*  83 */       return new Winspool.PRINTER_INFO_2[0];
/*     */     }
/*     */     
/*  86 */     Winspool.PRINTER_INFO_2 pPrinterEnum = new Winspool.PRINTER_INFO_2(pcbNeeded.getValue());
/*  87 */     if (!Winspool.INSTANCE.EnumPrinters(flags, null, 2, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned))
/*     */     {
/*  89 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/*  92 */     pPrinterEnum.read();
/*  93 */     return (Winspool.PRINTER_INFO_2[])pPrinterEnum.toArray(pcReturned.getValue());
/*     */   }
/*     */   
/*     */   public static Winspool.PRINTER_INFO_2 getPrinterInfo2(String printerName) {
/*  97 */     IntByReference pcbNeeded = new IntByReference();
/*  98 */     IntByReference pcReturned = new IntByReference();
/*  99 */     WinNT.HANDLEByReference pHandle = new WinNT.HANDLEByReference();
/*     */     
/* 101 */     if (!Winspool.INSTANCE.OpenPrinter(printerName, pHandle, null)) {
/* 102 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/* 105 */     Win32Exception we = null;
/* 106 */     Winspool.PRINTER_INFO_2 pinfo2 = null;
/*     */     
/*     */     try {
/* 109 */       Winspool.INSTANCE.GetPrinter(pHandle.getValue(), 2, null, 0, pcbNeeded);
/* 110 */       if (pcbNeeded.getValue() <= 0) {
/* 111 */         return new Winspool.PRINTER_INFO_2();
/*     */       }
/*     */       
/* 114 */       pinfo2 = new Winspool.PRINTER_INFO_2(pcbNeeded.getValue());
/* 115 */       if (!Winspool.INSTANCE.GetPrinter(pHandle.getValue(), 2, pinfo2.getPointer(), pcbNeeded.getValue(), pcReturned)) {
/* 116 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */       }
/*     */       
/* 119 */       pinfo2.read();
/* 120 */     } catch (Win32Exception e) {
/* 121 */       we = e;
/*     */     } finally {
/* 123 */       if (!Winspool.INSTANCE.ClosePrinter(pHandle.getValue())) {
/* 124 */         Win32Exception ex = new Win32Exception(Kernel32.INSTANCE.GetLastError());
/* 125 */         if (we != null) {
/* 126 */           ex.addSuppressedReflected((Throwable)we);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 131 */     if (we != null) {
/* 132 */       throw we;
/*     */     }
/*     */     
/* 135 */     return pinfo2;
/*     */   }
/*     */   
/*     */   public static Winspool.PRINTER_INFO_4[] getPrinterInfo4() {
/* 139 */     IntByReference pcbNeeded = new IntByReference();
/* 140 */     IntByReference pcReturned = new IntByReference();
/* 141 */     Winspool.INSTANCE.EnumPrinters(2, null, 4, null, 0, pcbNeeded, pcReturned);
/*     */     
/* 143 */     if (pcbNeeded.getValue() <= 0) {
/* 144 */       return new Winspool.PRINTER_INFO_4[0];
/*     */     }
/*     */     
/* 147 */     Winspool.PRINTER_INFO_4 pPrinterEnum = new Winspool.PRINTER_INFO_4(pcbNeeded.getValue());
/* 148 */     if (!Winspool.INSTANCE.EnumPrinters(2, null, 4, pPrinterEnum
/* 149 */         .getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned))
/*     */     {
/* 151 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/* 154 */     pPrinterEnum.read();
/*     */     
/* 156 */     return (Winspool.PRINTER_INFO_4[])pPrinterEnum.toArray(pcReturned.getValue());
/*     */   }
/*     */   
/*     */   public static Winspool.JOB_INFO_1[] getJobInfo1(WinNT.HANDLEByReference phPrinter) {
/* 160 */     IntByReference pcbNeeded = new IntByReference();
/* 161 */     IntByReference pcReturned = new IntByReference();
/* 162 */     Winspool.INSTANCE.EnumJobs(phPrinter.getValue(), 0, 255, 1, null, 0, pcbNeeded, pcReturned);
/*     */     
/* 164 */     if (pcbNeeded.getValue() <= 0) {
/* 165 */       return new Winspool.JOB_INFO_1[0];
/*     */     }
/*     */     
/* 168 */     Winspool.JOB_INFO_1 pJobEnum = new Winspool.JOB_INFO_1(pcbNeeded.getValue());
/* 169 */     if (!Winspool.INSTANCE.EnumJobs(phPrinter.getValue(), 0, 255, 1, pJobEnum
/* 170 */         .getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned))
/*     */     {
/* 172 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */     }
/*     */     
/* 175 */     pJobEnum.read();
/*     */     
/* 177 */     return (Winspool.JOB_INFO_1[])pJobEnum.toArray(pcReturned.getValue());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\WinspoolUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */