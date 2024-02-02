/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.driver.linux.Sysfs;
/*     */ import oshi.driver.linux.proc.CpuInfo;
/*     */ import oshi.hardware.common.AbstractBaseboard;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.tuples.Quartet;
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
/*     */ final class LinuxBaseboard
/*     */   extends AbstractBaseboard
/*     */ {
/*  43 */   private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
/*  44 */   private final Supplier<String> model = Memoizer.memoize(this::queryModel);
/*  45 */   private final Supplier<String> version = Memoizer.memoize(this::queryVersion);
/*  46 */   private final Supplier<String> serialNumber = Memoizer.memoize(this::querySerialNumber);
/*  47 */   private final Supplier<Quartet<String, String, String, String>> manufacturerModelVersionSerial = Memoizer.memoize(CpuInfo::queryBoardInfo);
/*     */ 
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  52 */     return this.manufacturer.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getModel() {
/*  57 */     return this.model.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVersion() {
/*  62 */     return this.version.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSerialNumber() {
/*  67 */     return this.serialNumber.get();
/*     */   }
/*     */   
/*     */   private String queryManufacturer() {
/*  71 */     String result = null;
/*  72 */     if ((result = Sysfs.queryBoardVendor()) == null && (
/*  73 */       result = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getA()) == null) {
/*  74 */       return "unknown";
/*     */     }
/*  76 */     return result;
/*     */   }
/*     */   
/*     */   private String queryModel() {
/*  80 */     String result = null;
/*  81 */     if ((result = Sysfs.queryBoardModel()) == null && (
/*  82 */       result = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getB()) == null) {
/*  83 */       return "unknown";
/*     */     }
/*  85 */     return result;
/*     */   }
/*     */   
/*     */   private String queryVersion() {
/*  89 */     String result = null;
/*  90 */     if ((result = Sysfs.queryBoardVersion()) == null && (
/*  91 */       result = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getC()) == null) {
/*  92 */       return "unknown";
/*     */     }
/*  94 */     return result;
/*     */   }
/*     */   
/*     */   private String querySerialNumber() {
/*  98 */     String result = null;
/*  99 */     if ((result = Sysfs.queryBoardSerial()) == null && (
/* 100 */       result = (String)((Quartet)this.manufacturerModelVersionSerial.get()).getD()) == null) {
/* 101 */       return "unknown";
/*     */     }
/* 103 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */