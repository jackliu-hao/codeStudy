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
/*    */ public final class Win32PnPEntity
/*    */ {
/*    */   private static final String WIN32_PNP_ENTITY = "Win32_PnPEntity";
/*    */   
/*    */   public enum PnPEntityProperty
/*    */   {
/* 44 */     NAME, MANUFACTURER, PNPDEVICEID;
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
/*    */   public static WbemcliUtil.WmiResult<PnPEntityProperty> queryDeviceId(String whereClause) {
/* 58 */     WbemcliUtil.WmiQuery<PnPEntityProperty> pnpEntityQuery = new WbemcliUtil.WmiQuery("Win32_PnPEntity" + whereClause, PnPEntityProperty.class);
/*    */     
/* 60 */     return WmiQueryHandler.createInstance().queryWMI(pnpEntityQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\Win32PnPEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */