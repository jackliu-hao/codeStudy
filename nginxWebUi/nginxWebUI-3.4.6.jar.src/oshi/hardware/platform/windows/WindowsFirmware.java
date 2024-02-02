/*    */ package oshi.hardware.platform.windows;
/*    */ 
/*    */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.driver.windows.wmi.Win32Bios;
/*    */ import oshi.hardware.common.AbstractFirmware;
/*    */ import oshi.util.Memoizer;
/*    */ import oshi.util.Util;
/*    */ import oshi.util.platform.windows.WmiUtil;
/*    */ import oshi.util.tuples.Quintet;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Immutable
/*    */ final class WindowsFirmware
/*    */   extends AbstractFirmware
/*    */ {
/* 47 */   private final Supplier<Quintet<String, String, String, String, String>> manufNameDescVersRelease = Memoizer.memoize(WindowsFirmware::queryManufNameDescVersRelease);
/*    */ 
/*    */ 
/*    */   
/*    */   public String getManufacturer() {
/* 52 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getA();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 57 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getB();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDescription() {
/* 62 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getC();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 67 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getD();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getReleaseDate() {
/* 72 */     return (String)((Quintet)this.manufNameDescVersRelease.get()).getE();
/*    */   }
/*    */   
/*    */   private static Quintet<String, String, String, String, String> queryManufNameDescVersRelease() {
/* 76 */     String manufacturer = null;
/* 77 */     String name = null;
/* 78 */     String description = null;
/* 79 */     String version = null;
/* 80 */     String releaseDate = null;
/* 81 */     WbemcliUtil.WmiResult<Win32Bios.BiosProperty> win32BIOS = Win32Bios.queryBiosInfo();
/* 82 */     if (win32BIOS.getResultCount() > 0) {
/* 83 */       manufacturer = WmiUtil.getString(win32BIOS, (Enum)Win32Bios.BiosProperty.MANUFACTURER, 0);
/* 84 */       name = WmiUtil.getString(win32BIOS, (Enum)Win32Bios.BiosProperty.NAME, 0);
/* 85 */       description = WmiUtil.getString(win32BIOS, (Enum)Win32Bios.BiosProperty.DESCRIPTION, 0);
/* 86 */       version = WmiUtil.getString(win32BIOS, (Enum)Win32Bios.BiosProperty.VERSION, 0);
/* 87 */       releaseDate = WmiUtil.getDateString(win32BIOS, (Enum)Win32Bios.BiosProperty.RELEASEDATE, 0);
/*    */     } 
/* 89 */     return new Quintet(Util.isBlank(manufacturer) ? "unknown" : manufacturer, 
/* 90 */         Util.isBlank(name) ? "unknown" : name, 
/* 91 */         Util.isBlank(description) ? "unknown" : description, 
/* 92 */         Util.isBlank(version) ? "unknown" : version, 
/* 93 */         Util.isBlank(releaseDate) ? "unknown" : releaseDate);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */