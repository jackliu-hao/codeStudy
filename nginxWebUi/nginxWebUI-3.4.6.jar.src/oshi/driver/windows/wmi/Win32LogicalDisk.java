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
/*    */ public final class Win32LogicalDisk
/*    */ {
/*    */   private static final String WIN32_LOGICAL_DISK = "Win32_LogicalDisk";
/*    */   
/*    */   public enum LogicalDiskProperty
/*    */   {
/* 44 */     ACCESS, DESCRIPTION, DRIVETYPE, FILESYSTEM, FREESPACE, NAME, PROVIDERNAME, SIZE, VOLUMENAME;
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
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static WbemcliUtil.WmiResult<LogicalDiskProperty> queryLogicalDisk(String nameToMatch, boolean localOnly) {
/* 60 */     StringBuilder wmiClassName = new StringBuilder("Win32_LogicalDisk");
/* 61 */     boolean where = false;
/* 62 */     if (localOnly) {
/* 63 */       wmiClassName.append(" WHERE DriveType != 4");
/* 64 */       where = true;
/*    */     } 
/* 66 */     if (nameToMatch != null) {
/* 67 */       wmiClassName.append(where ? " AND" : " WHERE").append(" Name=\"").append(nameToMatch).append('"');
/*    */     }
/* 69 */     WbemcliUtil.WmiQuery<LogicalDiskProperty> logicalDiskQuery = new WbemcliUtil.WmiQuery(wmiClassName.toString(), LogicalDiskProperty.class);
/*    */     
/* 71 */     return WmiQueryHandler.createInstance().queryWMI(logicalDiskQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\Win32LogicalDisk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */