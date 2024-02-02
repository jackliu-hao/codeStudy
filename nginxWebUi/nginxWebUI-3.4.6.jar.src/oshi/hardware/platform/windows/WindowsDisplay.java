/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.Advapi32;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.SetupApi;
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.platform.win32.WinReg;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.Display;
/*     */ import oshi.hardware.common.AbstractDisplay;
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
/*     */ @Immutable
/*     */ final class WindowsDisplay
/*     */   extends AbstractDisplay
/*     */ {
/*  54 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsDisplay.class);
/*     */   
/*  56 */   private static final SetupApi SU = SetupApi.INSTANCE;
/*  57 */   private static final Advapi32 ADV = Advapi32.INSTANCE;
/*     */   
/*  59 */   private static final Guid.GUID GUID_DEVINTERFACE_MONITOR = new Guid.GUID("E6F07B5F-EE97-4a90-B076-33F57BF4EAA7");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   WindowsDisplay(byte[] edid) {
/*  68 */     super(edid);
/*  69 */     LOG.debug("Initialized WindowsDisplay");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<Display> getDisplays() {
/*  78 */     List<Display> displays = new ArrayList<>();
/*     */     
/*  80 */     WinNT.HANDLE hDevInfo = SU.SetupDiGetClassDevs(GUID_DEVINTERFACE_MONITOR, null, null, 18);
/*     */     
/*  82 */     if (!hDevInfo.equals(WinBase.INVALID_HANDLE_VALUE)) {
/*  83 */       SetupApi.SP_DEVICE_INTERFACE_DATA deviceInterfaceData = new SetupApi.SP_DEVICE_INTERFACE_DATA();
/*  84 */       deviceInterfaceData.cbSize = deviceInterfaceData.size();
/*     */ 
/*     */       
/*  87 */       SetupApi.SP_DEVINFO_DATA info = new SetupApi.SP_DEVINFO_DATA();
/*     */       
/*  89 */       for (int memberIndex = 0; SU.SetupDiEnumDeviceInfo(hDevInfo, memberIndex, info); memberIndex++) {
/*  90 */         WinReg.HKEY key = SU.SetupDiOpenDevRegKey(hDevInfo, info, 1, 0, 1, 1);
/*     */ 
/*     */         
/*  93 */         byte[] edid = new byte[1];
/*     */         
/*  95 */         IntByReference pType = new IntByReference();
/*  96 */         IntByReference lpcbData = new IntByReference();
/*     */         
/*  98 */         if (ADV.RegQueryValueEx(key, "EDID", 0, pType, edid, lpcbData) == 234) {
/*  99 */           edid = new byte[lpcbData.getValue()];
/* 100 */           if (ADV.RegQueryValueEx(key, "EDID", 0, pType, edid, lpcbData) == 0) {
/* 101 */             WindowsDisplay windowsDisplay = new WindowsDisplay(edid);
/* 102 */             displays.add(windowsDisplay);
/*     */           } 
/*     */         } 
/* 105 */         Advapi32.INSTANCE.RegCloseKey(key);
/*     */       } 
/* 107 */       SU.SetupDiDestroyDeviceInfoList(hDevInfo);
/*     */     } 
/* 109 */     return Collections.unmodifiableList(displays);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */