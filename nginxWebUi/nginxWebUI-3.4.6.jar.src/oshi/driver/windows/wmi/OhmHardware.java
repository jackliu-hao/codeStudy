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
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class OhmHardware
/*    */ {
/*    */   private static final String HARDWARE = "Hardware";
/*    */   
/*    */   public enum IdentifierProperty
/*    */   {
/* 45 */     IDENTIFIER;
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
/*    */   public static WbemcliUtil.WmiResult<IdentifierProperty> queryHwIdentifier(String typeToQuery, String typeName) {
/* 61 */     StringBuilder sb = new StringBuilder("Hardware");
/* 62 */     sb.append(" WHERE ").append(typeToQuery).append("Type=\"").append(typeName).append('"');
/* 63 */     WbemcliUtil.WmiQuery<IdentifierProperty> cpuIdentifierQuery = new WbemcliUtil.WmiQuery("ROOT\\OpenHardwareMonitor", sb.toString(), IdentifierProperty.class);
/*    */     
/* 65 */     return WmiQueryHandler.createInstance().queryWMI(cpuIdentifierQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\OhmHardware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */