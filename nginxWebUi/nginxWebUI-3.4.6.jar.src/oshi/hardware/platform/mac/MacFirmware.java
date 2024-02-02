/*     */ package oshi.hardware.platform.mac;
/*     */ 
/*     */ import com.sun.jna.platform.mac.IOKit;
/*     */ import com.sun.jna.platform.mac.IOKitUtil;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.common.AbstractFirmware;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.Util;
/*     */ import oshi.util.tuples.Quintet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ final class MacFirmware
/*     */   extends AbstractFirmware
/*     */ {
/*  47 */   private final Supplier<Quintet<String, String, String, String, String>> manufNameDescVersRelease = Memoizer.memoize(MacFirmware::queryEfi);
/*     */ 
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  52 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getA();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  57 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getB();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription() {
/*  62 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getC();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVersion() {
/*  67 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getD();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getReleaseDate() {
/*  72 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getE();
/*     */   }
/*     */   
/*     */   private static Quintet<String, String, String, String, String> queryEfi() {
/*  76 */     String manufacturer = null;
/*  77 */     String name = null;
/*  78 */     String description = null;
/*  79 */     String version = null;
/*  80 */     String releaseDate = null;
/*     */     
/*  82 */     IOKit.IOService iOService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
/*  83 */     if (iOService != null) {
/*  84 */       IOKit.IOIterator iter = iOService.getChildIterator("IODeviceTree");
/*  85 */       if (iter != null) {
/*  86 */         IOKit.IORegistryEntry entry = iter.next();
/*  87 */         while (entry != null) {
/*  88 */           byte[] data; switch (entry.getName()) {
/*     */             case "rom":
/*  90 */               data = entry.getByteArrayProperty("vendor");
/*  91 */               if (data != null) {
/*  92 */                 manufacturer = (new String(data, StandardCharsets.UTF_8)).trim();
/*     */               }
/*  94 */               data = entry.getByteArrayProperty("version");
/*  95 */               if (data != null) {
/*  96 */                 version = (new String(data, StandardCharsets.UTF_8)).trim();
/*     */               }
/*  98 */               data = entry.getByteArrayProperty("release-date");
/*  99 */               if (data != null) {
/* 100 */                 releaseDate = (new String(data, StandardCharsets.UTF_8)).trim();
/*     */               }
/*     */               break;
/*     */             case "chosen":
/* 104 */               data = entry.getByteArrayProperty("booter-name");
/* 105 */               if (data != null) {
/* 106 */                 name = (new String(data, StandardCharsets.UTF_8)).trim();
/*     */               }
/*     */               break;
/*     */             case "efi":
/* 110 */               data = entry.getByteArrayProperty("firmware-abi");
/* 111 */               if (data != null) {
/* 112 */                 description = (new String(data, StandardCharsets.UTF_8)).trim();
/*     */               }
/*     */               break;
/*     */           } 
/*     */ 
/*     */           
/* 118 */           entry.release();
/* 119 */           entry = iter.next();
/*     */         } 
/* 121 */         iter.release();
/*     */       } 
/* 123 */       iOService.release();
/*     */     } 
/* 125 */     return new Quintet(Util.isBlank(manufacturer) ? "unknown" : manufacturer, 
/* 126 */         Util.isBlank(name) ? "unknown" : name, 
/* 127 */         Util.isBlank(description) ? "unknown" : description, 
/* 128 */         Util.isBlank(version) ? "unknown" : version, 
/* 129 */         Util.isBlank(releaseDate) ? "unknown" : releaseDate);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */