/*     */ package oshi.hardware.platform.unix.freebsd;
/*     */ 
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.common.AbstractFirmware;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.Util;
/*     */ import oshi.util.tuples.Triplet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ final class FreeBsdFirmware
/*     */   extends AbstractFirmware
/*     */ {
/*  44 */   private final Supplier<Triplet<String, String, String>> manufVersRelease = Memoizer.memoize(FreeBsdFirmware::readDmiDecode);
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  48 */     return (String)((Triplet)this.manufVersRelease.get()).getA();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVersion() {
/*  53 */     return (String)((Triplet)this.manufVersRelease.get()).getB();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getReleaseDate() {
/*  58 */     return (String)((Triplet)this.manufVersRelease.get()).getC();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Triplet<String, String, String> readDmiDecode() {
/*  66 */     String manufacturer = null;
/*  67 */     String version = null;
/*  68 */     String releaseDate = "";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     String manufacturerMarker = "Vendor:";
/*  85 */     String versionMarker = "Version:";
/*  86 */     String releaseDateMarker = "Release Date:";
/*     */ 
/*     */     
/*  89 */     for (String checkLine : ExecutingCommand.runNative("dmidecode -t bios")) {
/*  90 */       if (checkLine.contains("Vendor:")) {
/*  91 */         manufacturer = checkLine.split("Vendor:")[1].trim(); continue;
/*  92 */       }  if (checkLine.contains("Version:")) {
/*  93 */         version = checkLine.split("Version:")[1].trim(); continue;
/*  94 */       }  if (checkLine.contains("Release Date:")) {
/*  95 */         releaseDate = checkLine.split("Release Date:")[1].trim();
/*     */       }
/*     */     } 
/*  98 */     releaseDate = ParseUtil.parseMmDdYyyyToYyyyMmDD(releaseDate);
/*  99 */     return new Triplet(Util.isBlank(manufacturer) ? "unknown" : manufacturer, 
/* 100 */         Util.isBlank(version) ? "unknown" : version, 
/* 101 */         Util.isBlank(releaseDate) ? "unknown" : releaseDate);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */