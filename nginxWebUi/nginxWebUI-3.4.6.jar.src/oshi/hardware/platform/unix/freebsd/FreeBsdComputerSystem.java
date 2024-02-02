/*     */ package oshi.hardware.platform.unix.freebsd;
/*     */ 
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.Baseboard;
/*     */ import oshi.hardware.Firmware;
/*     */ import oshi.hardware.common.AbstractComputerSystem;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.Util;
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
/*     */ final class FreeBsdComputerSystem
/*     */   extends AbstractComputerSystem
/*     */ {
/*  46 */   private final Supplier<Quartet<String, String, String, String>> manufModelSerialVers = Memoizer.memoize(FreeBsdComputerSystem::readDmiDecode);
/*     */ 
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  51 */     return (String)((Quartet)this.manufModelSerialVers.get()).getA();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getModel() {
/*  56 */     return (String)((Quartet)this.manufModelSerialVers.get()).getB();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSerialNumber() {
/*  61 */     return (String)((Quartet)this.manufModelSerialVers.get()).getC();
/*     */   }
/*     */ 
/*     */   
/*     */   public Firmware createFirmware() {
/*  66 */     return (Firmware)new FreeBsdFirmware();
/*     */   }
/*     */ 
/*     */   
/*     */   public Baseboard createBaseboard() {
/*  71 */     return (Baseboard)new FreeBsdBaseboard((String)((Quartet)this.manufModelSerialVers.get()).getA(), (String)((Quartet)this.manufModelSerialVers.get()).getB(), (String)((Quartet)this.manufModelSerialVers
/*  72 */         .get()).getC(), (String)((Quartet)this.manufModelSerialVers.get()).getD());
/*     */   }
/*     */   
/*     */   private static Quartet<String, String, String, String> readDmiDecode() {
/*  76 */     String manufacturer = null;
/*  77 */     String model = null;
/*  78 */     String serialNumber = null;
/*  79 */     String version = null;
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
/* 102 */     String manufacturerMarker = "Manufacturer:";
/* 103 */     String productNameMarker = "Product Name:";
/* 104 */     String serialNumMarker = "Serial Number:";
/* 105 */     String versionMarker = "Version:";
/*     */ 
/*     */     
/* 108 */     for (String checkLine : ExecutingCommand.runNative("dmidecode -t system")) {
/* 109 */       if (checkLine.contains("Manufacturer:")) {
/* 110 */         manufacturer = checkLine.split("Manufacturer:")[1].trim(); continue;
/* 111 */       }  if (checkLine.contains("Product Name:")) {
/* 112 */         model = checkLine.split("Product Name:")[1].trim(); continue;
/* 113 */       }  if (checkLine.contains("Serial Number:")) {
/* 114 */         serialNumber = checkLine.split("Serial Number:")[1].trim(); continue;
/* 115 */       }  if (checkLine.contains("Version:")) {
/* 116 */         version = checkLine.split("Version:")[1].trim();
/*     */       }
/*     */     } 
/*     */     
/* 120 */     if (Util.isBlank(serialNumber)) {
/* 121 */       serialNumber = querySystemSerialNumber();
/*     */     }
/* 123 */     return new Quartet(Util.isBlank(manufacturer) ? "unknown" : manufacturer, 
/* 124 */         Util.isBlank(model) ? "unknown" : model, 
/* 125 */         Util.isBlank(serialNumber) ? "unknown" : serialNumber, 
/* 126 */         Util.isBlank(version) ? "unknown" : version);
/*     */   }
/*     */   
/*     */   private static String querySystemSerialNumber() {
/* 130 */     String marker = "system.hardware.serial =";
/* 131 */     for (String checkLine : ExecutingCommand.runNative("lshal")) {
/* 132 */       if (checkLine.contains(marker)) {
/* 133 */         return ParseUtil.getSingleQuoteStringValue(checkLine);
/*     */       }
/*     */     } 
/* 136 */     return "unknown";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */