/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.VersionHelpers;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.driver.windows.wmi.Win32VideoController;
/*     */ import oshi.hardware.GraphicsCard;
/*     */ import oshi.hardware.common.AbstractGraphicsCard;
/*     */ import oshi.util.ParseUtil;
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
/*     */ @Immutable
/*     */ final class WindowsGraphicsCard
/*     */   extends AbstractGraphicsCard
/*     */ {
/*  50 */   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   WindowsGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
/*  67 */     super(name, deviceId, vendor, versionInfo, vram);
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
/*     */   public static List<GraphicsCard> getGraphicsCards() {
/*  79 */     List<WindowsGraphicsCard> cardList = new ArrayList<>();
/*  80 */     if (IS_VISTA_OR_GREATER) {
/*  81 */       WbemcliUtil.WmiResult<Win32VideoController.VideoControllerProperty> cards = Win32VideoController.queryVideoController();
/*  82 */       for (int index = 0; index < cards.getResultCount(); index++) {
/*  83 */         String name = WmiUtil.getString(cards, (Enum)Win32VideoController.VideoControllerProperty.NAME, index);
/*  84 */         Pair<String, String> idPair = ParseUtil.parsePnPDeviceIdToVendorProductId(
/*  85 */             WmiUtil.getString(cards, (Enum)Win32VideoController.VideoControllerProperty.PNPDEVICEID, index));
/*  86 */         String deviceId = (idPair == null) ? "unknown" : (String)idPair.getB();
/*  87 */         String vendor = WmiUtil.getString(cards, (Enum)Win32VideoController.VideoControllerProperty.ADAPTERCOMPATIBILITY, index);
/*  88 */         if (idPair != null) {
/*  89 */           if (Util.isBlank(vendor)) {
/*  90 */             deviceId = (String)idPair.getA();
/*     */           } else {
/*  92 */             vendor = vendor + " (" + (String)idPair.getA() + ")";
/*     */           } 
/*     */         }
/*  95 */         String versionInfo = WmiUtil.getString(cards, (Enum)Win32VideoController.VideoControllerProperty.DRIVERVERSION, index);
/*  96 */         if (!Util.isBlank(versionInfo)) {
/*  97 */           versionInfo = "DriverVersion=" + versionInfo;
/*     */         } else {
/*  99 */           versionInfo = "unknown";
/*     */         } 
/* 101 */         long vram = WmiUtil.getUint32asLong(cards, (Enum)Win32VideoController.VideoControllerProperty.ADAPTERRAM, index);
/* 102 */         cardList.add(new WindowsGraphicsCard(Util.isBlank(name) ? "unknown" : name, deviceId, 
/* 103 */               Util.isBlank(vendor) ? "unknown" : vendor, versionInfo, vram));
/*     */       } 
/*     */     } 
/* 106 */     return (List)Collections.unmodifiableList(cardList);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */