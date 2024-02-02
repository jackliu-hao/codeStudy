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
/*    */ public final class Win32LogicalDiskToPartition
/*    */ {
/*    */   private static final String WIN32_LOGICAL_DISK_TO_PARTITION = "Win32_LogicalDiskToPartition";
/*    */   
/*    */   public enum DiskToPartitionProperty
/*    */   {
/* 44 */     ANTECEDENT, DEPENDENT, ENDINGADDRESS, STARTINGADDRESS;
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
/*    */   public static WbemcliUtil.WmiResult<DiskToPartitionProperty> queryDiskToPartition() {
/* 56 */     WbemcliUtil.WmiQuery<DiskToPartitionProperty> diskToPartitionQuery = new WbemcliUtil.WmiQuery("Win32_LogicalDiskToPartition", DiskToPartitionProperty.class);
/*    */     
/* 58 */     return WmiQueryHandler.createInstance().queryWMI(diskToPartitionQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\Win32LogicalDiskToPartition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */