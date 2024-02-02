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
/*    */ public final class Win32ComputerSystem
/*    */ {
/*    */   private static final String WIN32_COMPUTER_SYSTEM = "Win32_ComputerSystem";
/*    */   
/*    */   public enum ComputerSystemProperty
/*    */   {
/* 44 */     MANUFACTURER, MODEL;
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
/*    */   public static WbemcliUtil.WmiResult<ComputerSystemProperty> queryComputerSystem() {
/* 56 */     WbemcliUtil.WmiQuery<ComputerSystemProperty> computerSystemQuery = new WbemcliUtil.WmiQuery("Win32_ComputerSystem", ComputerSystemProperty.class);
/*    */     
/* 58 */     return WmiQueryHandler.createInstance().queryWMI(computerSystemQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\Win32ComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */