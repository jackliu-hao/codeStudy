/*    */ package oshi.hardware.platform.windows;
/*    */ 
/*    */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.driver.windows.wmi.Win32BaseBoard;
/*    */ import oshi.hardware.common.AbstractBaseboard;
/*    */ import oshi.util.Memoizer;
/*    */ import oshi.util.Util;
/*    */ import oshi.util.platform.windows.WmiUtil;
/*    */ import oshi.util.tuples.Quartet;
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
/*    */ final class WindowsBaseboard
/*    */   extends AbstractBaseboard
/*    */ {
/* 47 */   private final Supplier<Quartet<String, String, String, String>> manufModelVersSerial = Memoizer.memoize(WindowsBaseboard::queryManufModelVersSerial);
/*    */ 
/*    */ 
/*    */   
/*    */   public String getManufacturer() {
/* 52 */     return (String)((Quartet)this.manufModelVersSerial.get()).getA();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getModel() {
/* 57 */     return (String)((Quartet)this.manufModelVersSerial.get()).getB();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 62 */     return (String)((Quartet)this.manufModelVersSerial.get()).getC();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSerialNumber() {
/* 67 */     return (String)((Quartet)this.manufModelVersSerial.get()).getD();
/*    */   }
/*    */   
/*    */   private static Quartet<String, String, String, String> queryManufModelVersSerial() {
/* 71 */     String manufacturer = null;
/* 72 */     String model = null;
/* 73 */     String version = null;
/* 74 */     String serialNumber = null;
/* 75 */     WbemcliUtil.WmiResult<Win32BaseBoard.BaseBoardProperty> win32BaseBoard = Win32BaseBoard.queryBaseboardInfo();
/* 76 */     if (win32BaseBoard.getResultCount() > 0) {
/* 77 */       manufacturer = WmiUtil.getString(win32BaseBoard, (Enum)Win32BaseBoard.BaseBoardProperty.MANUFACTURER, 0);
/* 78 */       model = WmiUtil.getString(win32BaseBoard, (Enum)Win32BaseBoard.BaseBoardProperty.MODEL, 0);
/* 79 */       version = WmiUtil.getString(win32BaseBoard, (Enum)Win32BaseBoard.BaseBoardProperty.VERSION, 0);
/* 80 */       serialNumber = WmiUtil.getString(win32BaseBoard, (Enum)Win32BaseBoard.BaseBoardProperty.SERIALNUMBER, 0);
/*    */     } 
/* 82 */     return new Quartet(Util.isBlank(manufacturer) ? "unknown" : manufacturer, 
/* 83 */         Util.isBlank(model) ? "unknown" : model, Util.isBlank(version) ? "unknown" : version, 
/* 84 */         Util.isBlank(serialNumber) ? "unknown" : serialNumber);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */