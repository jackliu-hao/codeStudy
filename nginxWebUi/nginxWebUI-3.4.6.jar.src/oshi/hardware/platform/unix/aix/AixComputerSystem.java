/*     */ package oshi.hardware.platform.unix.aix;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.Baseboard;
/*     */ import oshi.hardware.Firmware;
/*     */ import oshi.hardware.common.AbstractComputerSystem;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.Util;
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
/*     */ final class AixComputerSystem
/*     */   extends AbstractComputerSystem
/*     */ {
/*  46 */   private final Supplier<LsattrStrings> lsattrStrings = Memoizer.memoize(AixComputerSystem::readLsattr);
/*     */   private final Supplier<List<String>> lscfg;
/*     */   
/*     */   AixComputerSystem(Supplier<List<String>> lscfg) {
/*  50 */     this.lscfg = lscfg;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  55 */     return (this.lsattrStrings.get()).manufacturer;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getModel() {
/*  60 */     return (this.lsattrStrings.get()).model;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSerialNumber() {
/*  65 */     return (this.lsattrStrings.get()).serialNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   public Firmware createFirmware() {
/*  70 */     return (Firmware)new AixFirmware((this.lsattrStrings.get()).biosVendor, (this.lsattrStrings.get()).biosPlatformVersion, 
/*  71 */         (this.lsattrStrings.get()).biosVersion);
/*     */   }
/*     */ 
/*     */   
/*     */   public Baseboard createBaseboard() {
/*  76 */     return (Baseboard)new AixBaseboard(this.lscfg);
/*     */   }
/*     */   
/*     */   private static LsattrStrings readLsattr() {
/*  80 */     String fwVendor = "IBM";
/*  81 */     String fwVersion = null;
/*  82 */     String fwPlatformVersion = null;
/*     */     
/*  84 */     String manufacturer = fwVendor;
/*  85 */     String model = null;
/*  86 */     String serialNumber = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     String fwVersionMarker = "fwversion";
/*  97 */     String modelMarker = "modelname";
/*  98 */     String systemIdMarker = "systemid";
/*  99 */     String fwPlatformVersionMarker = "Platform Firmware level is";
/*     */     
/* 101 */     for (String checkLine : ExecutingCommand.runNative("lsattr -El sys0")) {
/* 102 */       if (checkLine.startsWith("fwversion")) {
/* 103 */         fwVersion = checkLine.split("fwversion")[1].trim();
/* 104 */         int comma = fwVersion.indexOf(',');
/* 105 */         if (comma > 0 && fwVersion.length() > comma) {
/* 106 */           fwVendor = fwVersion.substring(0, comma);
/* 107 */           fwVersion = fwVersion.substring(comma + 1);
/*     */         } 
/* 109 */         fwVersion = ParseUtil.whitespaces.split(fwVersion)[0]; continue;
/* 110 */       }  if (checkLine.startsWith("modelname")) {
/* 111 */         model = checkLine.split("modelname")[1].trim();
/* 112 */         int comma = model.indexOf(',');
/* 113 */         if (comma > 0 && model.length() > comma) {
/* 114 */           manufacturer = model.substring(0, comma);
/* 115 */           model = model.substring(comma + 1);
/*     */         } 
/* 117 */         model = ParseUtil.whitespaces.split(model)[0]; continue;
/* 118 */       }  if (checkLine.startsWith("systemid")) {
/* 119 */         serialNumber = checkLine.split("systemid")[1].trim();
/* 120 */         serialNumber = ParseUtil.whitespaces.split(serialNumber)[0];
/*     */       } 
/*     */     } 
/* 123 */     for (String checkLine : ExecutingCommand.runNative("lsmcode -c")) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 128 */       if (checkLine.startsWith("Platform Firmware level is")) {
/* 129 */         fwPlatformVersion = checkLine.split("Platform Firmware level is")[1].trim();
/*     */         break;
/*     */       } 
/*     */     } 
/* 133 */     return new LsattrStrings(fwVendor, fwPlatformVersion, fwVersion, manufacturer, model, serialNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class LsattrStrings
/*     */   {
/*     */     private final String biosVendor;
/*     */     private final String biosPlatformVersion;
/*     */     private final String biosVersion;
/*     */     private final String manufacturer;
/*     */     private final String model;
/*     */     private final String serialNumber;
/*     */     
/*     */     private LsattrStrings(String biosVendor, String biosPlatformVersion, String biosVersion, String manufacturer, String model, String serialNumber) {
/* 147 */       this.biosVendor = Util.isBlank(biosVendor) ? "unknown" : biosVendor;
/* 148 */       this.biosPlatformVersion = Util.isBlank(biosPlatformVersion) ? "unknown" : biosPlatformVersion;
/* 149 */       this.biosVersion = Util.isBlank(biosVersion) ? "unknown" : biosVersion;
/*     */       
/* 151 */       this.manufacturer = Util.isBlank(manufacturer) ? "unknown" : manufacturer;
/* 152 */       this.model = Util.isBlank(model) ? "unknown" : model;
/* 153 */       this.serialNumber = Util.isBlank(serialNumber) ? "unknown" : serialNumber;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */