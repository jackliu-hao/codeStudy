/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.driver.windows.wmi.Win32Bios;
/*     */ import oshi.driver.windows.wmi.Win32ComputerSystem;
/*     */ import oshi.driver.windows.wmi.Win32ComputerSystemProduct;
/*     */ import oshi.hardware.Baseboard;
/*     */ import oshi.hardware.Firmware;
/*     */ import oshi.hardware.common.AbstractComputerSystem;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.Util;
/*     */ import oshi.util.platform.windows.WmiUtil;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ @Immutable
/*     */ final class WindowsComputerSystem
/*     */   extends AbstractComputerSystem
/*     */ {
/*  53 */   private final Supplier<Pair<String, String>> manufacturerModel = Memoizer.memoize(WindowsComputerSystem::queryManufacturerModel);
/*     */   
/*  55 */   private final Supplier<String> serialNumber = Memoizer.memoize(WindowsComputerSystem::querySystemSerialNumber);
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  59 */     return (String)((Pair)this.manufacturerModel.get()).getA();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getModel() {
/*  64 */     return (String)((Pair)this.manufacturerModel.get()).getB();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSerialNumber() {
/*  69 */     return this.serialNumber.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public Firmware createFirmware() {
/*  74 */     return (Firmware)new WindowsFirmware();
/*     */   }
/*     */ 
/*     */   
/*     */   public Baseboard createBaseboard() {
/*  79 */     return (Baseboard)new WindowsBaseboard();
/*     */   }
/*     */   
/*     */   private static Pair<String, String> queryManufacturerModel() {
/*  83 */     String manufacturer = null;
/*  84 */     String model = null;
/*  85 */     WbemcliUtil.WmiResult<Win32ComputerSystem.ComputerSystemProperty> win32ComputerSystem = Win32ComputerSystem.queryComputerSystem();
/*  86 */     if (win32ComputerSystem.getResultCount() > 0) {
/*  87 */       manufacturer = WmiUtil.getString(win32ComputerSystem, (Enum)Win32ComputerSystem.ComputerSystemProperty.MANUFACTURER, 0);
/*  88 */       model = WmiUtil.getString(win32ComputerSystem, (Enum)Win32ComputerSystem.ComputerSystemProperty.MODEL, 0);
/*     */     } 
/*  90 */     return new Pair(Util.isBlank(manufacturer) ? "unknown" : manufacturer, 
/*  91 */         Util.isBlank(model) ? "unknown" : model);
/*     */   }
/*     */   
/*     */   private static String querySystemSerialNumber() {
/*     */     String result;
/*  96 */     if (((result = querySerialFromBios()) != null || (result = querySerialFromCsProduct()) != null) && 
/*  97 */       !Util.isBlank(result)) {
/*  98 */       return result;
/*     */     }
/* 100 */     return "unknown";
/*     */   }
/*     */   
/*     */   private static String querySerialFromBios() {
/* 104 */     WbemcliUtil.WmiResult<Win32Bios.BiosSerialProperty> serialNum = Win32Bios.querySerialNumber();
/* 105 */     if (serialNum.getResultCount() > 0) {
/* 106 */       return WmiUtil.getString(serialNum, (Enum)Win32Bios.BiosSerialProperty.SERIALNUMBER, 0);
/*     */     }
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String querySerialFromCsProduct() {
/* 113 */     WbemcliUtil.WmiResult<Win32ComputerSystemProduct.ComputerSystemProductProperty> identifyingNumber = Win32ComputerSystemProduct.queryIdentifyingNumber();
/* 114 */     if (identifyingNumber.getResultCount() > 0) {
/* 115 */       return WmiUtil.getString(identifyingNumber, (Enum)Win32ComputerSystemProduct.ComputerSystemProductProperty.IDENTIFYINGNUMBER, 0);
/*     */     }
/* 117 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */