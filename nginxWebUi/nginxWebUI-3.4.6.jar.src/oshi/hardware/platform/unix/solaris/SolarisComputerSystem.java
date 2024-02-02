/*     */ package oshi.hardware.platform.unix.solaris;
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
/*     */ final class SolarisComputerSystem
/*     */   extends AbstractComputerSystem
/*     */ {
/*  45 */   private final Supplier<SmbiosStrings> smbiosStrings = Memoizer.memoize(SolarisComputerSystem::readSmbios);
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  49 */     return (this.smbiosStrings.get()).manufacturer;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getModel() {
/*  54 */     return (this.smbiosStrings.get()).model;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSerialNumber() {
/*  59 */     return (this.smbiosStrings.get()).serialNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   public Firmware createFirmware() {
/*  64 */     return (Firmware)new SolarisFirmware((this.smbiosStrings.get()).biosVendor, (this.smbiosStrings.get()).biosVersion, 
/*  65 */         (this.smbiosStrings.get()).biosDate);
/*     */   }
/*     */ 
/*     */   
/*     */   public Baseboard createBaseboard() {
/*  70 */     return (Baseboard)new SolarisBaseboard((this.smbiosStrings.get()).boardManufacturer, (this.smbiosStrings.get()).boardModel, 
/*  71 */         (this.smbiosStrings.get()).boardSerialNumber, (this.smbiosStrings.get()).boardVersion);
/*     */   }
/*     */   
/*     */   private static SmbiosStrings readSmbios() {
/*  75 */     String biosVendor = null;
/*  76 */     String biosVersion = null;
/*  77 */     String biosDate = null;
/*     */     
/*  79 */     String manufacturer = null;
/*  80 */     String model = null;
/*  81 */     String serialNumber = null;
/*     */     
/*  83 */     String boardManufacturer = null;
/*  84 */     String boardModel = null;
/*  85 */     String boardVersion = null;
/*  86 */     String boardSerialNumber = null;
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
/* 124 */     String vendorMarker = "Vendor:";
/* 125 */     String biosDateMarker = "Release Date:";
/* 126 */     String biosVersionMarker = "VersionString:";
/*     */     
/* 128 */     String manufacturerMarker = "Manufacturer:";
/* 129 */     String productMarker = "Product:";
/* 130 */     String serialNumMarker = "Serial Number:";
/* 131 */     String versionMarker = "Version:";
/*     */     
/* 133 */     int smbTypeId = -1;
/*     */     
/* 135 */     for (String checkLine : ExecutingCommand.runNative("smbios")) {
/*     */       
/* 137 */       if (checkLine.contains("SMB_TYPE_") && (smbTypeId = getSmbType(checkLine)) == Integer.MAX_VALUE) {
/*     */         break;
/*     */       }
/*     */ 
/*     */       
/* 142 */       switch (smbTypeId) {
/*     */         case 0:
/* 144 */           if (checkLine.contains("Vendor:")) {
/* 145 */             biosVendor = checkLine.split("Vendor:")[1].trim(); continue;
/* 146 */           }  if (checkLine.contains("VersionString:")) {
/* 147 */             biosVersion = checkLine.split("VersionString:")[1].trim(); continue;
/* 148 */           }  if (checkLine.contains("Release Date:")) {
/* 149 */             biosDate = checkLine.split("Release Date:")[1].trim();
/*     */           }
/*     */         
/*     */         case 1:
/* 153 */           if (checkLine.contains("Manufacturer:")) {
/* 154 */             manufacturer = checkLine.split("Manufacturer:")[1].trim(); continue;
/* 155 */           }  if (checkLine.contains("Product:")) {
/* 156 */             model = checkLine.split("Product:")[1].trim(); continue;
/* 157 */           }  if (checkLine.contains("Serial Number:")) {
/* 158 */             serialNumber = checkLine.split("Serial Number:")[1].trim();
/*     */           }
/*     */         
/*     */         case 2:
/* 162 */           if (checkLine.contains("Manufacturer:")) {
/* 163 */             boardManufacturer = checkLine.split("Manufacturer:")[1].trim(); continue;
/* 164 */           }  if (checkLine.contains("Product:")) {
/* 165 */             boardModel = checkLine.split("Product:")[1].trim(); continue;
/* 166 */           }  if (checkLine.contains("Version:")) {
/* 167 */             boardVersion = checkLine.split("Version:")[1].trim(); continue;
/* 168 */           }  if (checkLine.contains("Serial Number:")) {
/* 169 */             boardSerialNumber = checkLine.split("Serial Number:")[1].trim();
/*     */           }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 177 */     if (Util.isBlank(serialNumber)) {
/* 178 */       serialNumber = readSerialNumber();
/*     */     }
/* 180 */     return new SmbiosStrings(biosVendor, biosVersion, biosDate, manufacturer, model, serialNumber, boardManufacturer, boardModel, boardVersion, boardSerialNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int getSmbType(String checkLine) {
/* 185 */     if (checkLine.contains("SMB_TYPE_BIOS"))
/* 186 */       return 0; 
/* 187 */     if (checkLine.contains("SMB_TYPE_SYSTEM"))
/* 188 */       return 1; 
/* 189 */     if (checkLine.contains("SMB_TYPE_BASEBOARD")) {
/* 190 */       return 2;
/*     */     }
/*     */ 
/*     */     
/* 194 */     return Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String readSerialNumber() {
/* 200 */     String serialNumber = ExecutingCommand.getFirstAnswer("sneep");
/*     */     
/* 202 */     if (serialNumber.isEmpty()) {
/* 203 */       String marker = "chassis-sn:";
/* 204 */       for (String checkLine : ExecutingCommand.runNative("prtconf -pv")) {
/* 205 */         if (checkLine.contains(marker)) {
/* 206 */           serialNumber = ParseUtil.getSingleQuoteStringValue(checkLine);
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 211 */     return serialNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class SmbiosStrings
/*     */   {
/*     */     private final String biosVendor;
/*     */     
/*     */     private final String biosVersion;
/*     */     
/*     */     private final String biosDate;
/*     */     private final String manufacturer;
/*     */     private final String model;
/*     */     private final String serialNumber;
/*     */     private final String boardManufacturer;
/*     */     private final String boardModel;
/*     */     private final String boardVersion;
/*     */     private final String boardSerialNumber;
/*     */     
/*     */     private SmbiosStrings(String biosVendor, String biosVersion, String biosDate, String manufacturer, String model, String serialNumber, String boardManufacturer, String boardModel, String boardVersion, String boardSerialNumber) {
/* 231 */       this.biosVendor = Util.isBlank(biosVendor) ? "unknown" : biosVendor;
/* 232 */       this.biosVersion = Util.isBlank(biosVersion) ? "unknown" : biosVersion;
/* 233 */       this.biosDate = Util.isBlank(biosDate) ? "unknown" : biosDate;
/*     */       
/* 235 */       this.manufacturer = Util.isBlank(manufacturer) ? "unknown" : manufacturer;
/* 236 */       this.model = Util.isBlank(model) ? "unknown" : model;
/* 237 */       this.serialNumber = Util.isBlank(serialNumber) ? "unknown" : serialNumber;
/*     */       
/* 239 */       this.boardManufacturer = Util.isBlank(boardManufacturer) ? "unknown" : boardManufacturer;
/* 240 */       this.boardModel = Util.isBlank(boardModel) ? "unknown" : boardModel;
/* 241 */       this.boardVersion = Util.isBlank(boardVersion) ? "unknown" : boardVersion;
/* 242 */       this.boardSerialNumber = Util.isBlank(boardSerialNumber) ? "unknown" : boardSerialNumber;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */