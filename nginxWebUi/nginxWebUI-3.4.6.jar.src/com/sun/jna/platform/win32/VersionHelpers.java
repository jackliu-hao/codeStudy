/*     */ package com.sun.jna.platform.win32;
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
/*     */ public class VersionHelpers
/*     */ {
/*     */   public static boolean IsWindowsVersionOrGreater(int wMajorVersion, int wMinorVersion, int wServicePackMajor) {
/*  59 */     WinNT.OSVERSIONINFOEX osvi = new WinNT.OSVERSIONINFOEX();
/*  60 */     osvi.dwOSVersionInfoSize = new WinDef.DWORD(osvi.size());
/*  61 */     osvi.dwMajorVersion = new WinDef.DWORD(wMajorVersion);
/*  62 */     osvi.dwMinorVersion = new WinDef.DWORD(wMinorVersion);
/*  63 */     osvi.wServicePackMajor = new WinDef.WORD(wServicePackMajor);
/*     */     
/*  65 */     long dwlConditionMask = 0L;
/*  66 */     dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(dwlConditionMask, 2, (byte)3);
/*     */     
/*  68 */     dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(dwlConditionMask, 1, (byte)3);
/*     */     
/*  70 */     dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(dwlConditionMask, 32, (byte)3);
/*     */ 
/*     */     
/*  73 */     return Kernel32.INSTANCE.VerifyVersionInfoW(osvi, 35, dwlConditionMask);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindowsXPOrGreater() {
/*  91 */     return IsWindowsVersionOrGreater(5, 1, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindowsXPSP1OrGreater() {
/* 100 */     return IsWindowsVersionOrGreater(5, 1, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindowsXPSP2OrGreater() {
/* 109 */     return IsWindowsVersionOrGreater(5, 1, 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindowsXPSP3OrGreater() {
/* 118 */     return IsWindowsVersionOrGreater(5, 1, 3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindowsVistaOrGreater() {
/* 127 */     return IsWindowsVersionOrGreater(6, 0, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindowsVistaSP1OrGreater() {
/* 136 */     return IsWindowsVersionOrGreater(6, 0, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindowsVistaSP2OrGreater() {
/* 145 */     return IsWindowsVersionOrGreater(6, 0, 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindows7OrGreater() {
/* 154 */     return IsWindowsVersionOrGreater(6, 1, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindows7SP1OrGreater() {
/* 162 */     return IsWindowsVersionOrGreater(6, 1, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean IsWindows8OrGreater() {
/* 170 */     return IsWindowsVersionOrGreater(6, 2, 0);
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
/*     */   public static boolean IsWindows8Point1OrGreater() {
/* 182 */     return IsWindowsVersionOrGreater(6, 3, 0);
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
/*     */   public static boolean IsWindows10OrGreater() {
/* 194 */     return IsWindowsVersionOrGreater(10, 0, 0);
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
/*     */   
/*     */   public static boolean IsWindowsServer() {
/* 208 */     WinNT.OSVERSIONINFOEX osvi = new WinNT.OSVERSIONINFOEX();
/* 209 */     osvi.dwOSVersionInfoSize = new WinDef.DWORD(osvi.size());
/* 210 */     osvi.wProductType = 1;
/*     */     
/* 212 */     long dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(0L, 128, (byte)1);
/*     */ 
/*     */     
/* 215 */     return !Kernel32.INSTANCE.VerifyVersionInfoW(osvi, 128, dwlConditionMask);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\VersionHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */