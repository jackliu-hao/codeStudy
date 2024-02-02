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
/*    */ public final class Win32ComputerSystemProduct
/*    */ {
/*    */   private static final String WIN32_COMPUTER_SYSTEM_PRODUCT = "Win32_ComputerSystemProduct";
/*    */   
/*    */   public enum ComputerSystemProductProperty
/*    */   {
/* 44 */     IDENTIFYINGNUMBER;
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
/*    */   public static WbemcliUtil.WmiResult<ComputerSystemProductProperty> queryIdentifyingNumber() {
/* 56 */     WbemcliUtil.WmiQuery<ComputerSystemProductProperty> identifyingNumberQuery = new WbemcliUtil.WmiQuery("Win32_ComputerSystemProduct", ComputerSystemProductProperty.class);
/*    */     
/* 58 */     return WmiQueryHandler.createInstance().queryWMI(identifyingNumberQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\Win32ComputerSystemProduct.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */