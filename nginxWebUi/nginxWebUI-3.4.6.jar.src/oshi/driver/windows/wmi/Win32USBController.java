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
/*    */ public final class Win32USBController
/*    */ {
/*    */   private static final String WIN32_USB_CONTROLLER = "Win32_USBController";
/*    */   
/*    */   public enum USBControllerProperty
/*    */   {
/* 44 */     PNPDEVICEID;
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
/*    */   public static WbemcliUtil.WmiResult<USBControllerProperty> queryUSBControllers() {
/* 56 */     WbemcliUtil.WmiQuery<USBControllerProperty> usbControllerQuery = new WbemcliUtil.WmiQuery("Win32_USBController", USBControllerProperty.class);
/*    */     
/* 58 */     return WmiQueryHandler.createInstance().queryWMI(usbControllerQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\Win32USBController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */