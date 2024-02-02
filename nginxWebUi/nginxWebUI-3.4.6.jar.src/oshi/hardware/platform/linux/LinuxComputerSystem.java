/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.driver.linux.Devicetree;
/*     */ import oshi.driver.linux.Dmidecode;
/*     */ import oshi.driver.linux.Lshal;
/*     */ import oshi.driver.linux.Lshw;
/*     */ import oshi.driver.linux.Sysfs;
/*     */ import oshi.driver.linux.proc.CpuInfo;
/*     */ import oshi.hardware.Baseboard;
/*     */ import oshi.hardware.Firmware;
/*     */ import oshi.hardware.common.AbstractComputerSystem;
/*     */ import oshi.util.Memoizer;
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
/*     */ final class LinuxComputerSystem
/*     */   extends AbstractComputerSystem
/*     */ {
/*  48 */   private final Supplier<String> manufacturer = Memoizer.memoize(LinuxComputerSystem::queryManufacturer);
/*     */   
/*  50 */   private final Supplier<String> model = Memoizer.memoize(LinuxComputerSystem::queryModel);
/*     */   
/*  52 */   private final Supplier<String> serialNumber = Memoizer.memoize(LinuxComputerSystem::querySerialNumber);
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  56 */     return this.manufacturer.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getModel() {
/*  61 */     return this.model.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSerialNumber() {
/*  66 */     return this.serialNumber.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public Firmware createFirmware() {
/*  71 */     return (Firmware)new LinuxFirmware();
/*     */   }
/*     */ 
/*     */   
/*     */   public Baseboard createBaseboard() {
/*  76 */     return (Baseboard)new LinuxBaseboard();
/*     */   }
/*     */   
/*     */   private static String queryManufacturer() {
/*  80 */     String result = null;
/*  81 */     if ((result = Sysfs.querySystemVendor()) == null && (result = CpuInfo.queryCpuManufacturer()) == null) {
/*  82 */       return "unknown";
/*     */     }
/*  84 */     return result;
/*     */   }
/*     */   
/*     */   private static String queryModel() {
/*  88 */     String result = null;
/*  89 */     if ((result = Sysfs.queryProductModel()) == null && (result = Devicetree.queryModel()) == null && (
/*  90 */       result = Lshw.queryModel()) == null) {
/*  91 */       return "unknown";
/*     */     }
/*  93 */     return result;
/*     */   }
/*     */   
/*     */   private static String querySerialNumber() {
/*  97 */     String result = null;
/*  98 */     if ((result = Sysfs.queryProductSerial()) == null && (result = Dmidecode.querySerialNumber()) == null && (
/*  99 */       result = Lshal.querySerialNumber()) == null && (result = Lshw.querySerialNumber()) == null) {
/* 100 */       return "unknown";
/*     */     }
/* 102 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */