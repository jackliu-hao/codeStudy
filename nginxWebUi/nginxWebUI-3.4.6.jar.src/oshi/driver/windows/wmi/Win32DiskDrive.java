/*    */ package oshi.driver.windows.wmi;
/*    */ 
/*    */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.platform.windows.WmiQueryHandler;
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
/*    */ @ThreadSafe
/*    */ public final class Win32DiskDrive
/*    */ {
/*    */   private static final String WIN32_DISK_DRIVE = "Win32_DiskDrive";
/*    */   
/*    */   public enum DiskDriveProperty
/*    */   {
/* 44 */     INDEX, MANUFACTURER, MODEL, NAME, SERIALNUMBER, SIZE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public enum DeviceIdProperty
/*    */   {
/* 51 */     PNPDEVICEID, SERIALNUMBER;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static WbemcliUtil.WmiResult<DiskDriveProperty> queryDiskDrive() {
/* 63 */     WbemcliUtil.WmiQuery<DiskDriveProperty> diskDriveQuery = new WbemcliUtil.WmiQuery("Win32_DiskDrive", DiskDriveProperty.class);
/* 64 */     return WmiQueryHandler.createInstance().queryWMI(diskDriveQuery);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static WbemcliUtil.WmiResult<DeviceIdProperty> queryDiskDriveId(String whereClause) {
/* 75 */     WbemcliUtil.WmiQuery<DeviceIdProperty> deviceIdQuery = new WbemcliUtil.WmiQuery("Win32_DiskDrive" + whereClause, DeviceIdProperty.class);
/*    */     
/* 77 */     return WmiQueryHandler.createInstance().queryWMI(deviceIdQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\Win32DiskDrive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */