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
/*    */ public final class MSAcpiThermalZoneTemperature
/*    */ {
/*    */   public static final String WMI_NAMESPACE = "ROOT\\WMI";
/*    */   private static final String MS_ACPI_THERMAL_ZONE_TEMPERATURE = "MSAcpi_ThermalZoneTemperature";
/*    */   
/*    */   public enum TemperatureProperty
/*    */   {
/* 45 */     CURRENTTEMPERATURE;
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
/*    */   public static WbemcliUtil.WmiResult<TemperatureProperty> queryCurrentTemperature() {
/* 57 */     WbemcliUtil.WmiQuery<TemperatureProperty> curTempQuery = new WbemcliUtil.WmiQuery("ROOT\\WMI", "MSAcpi_ThermalZoneTemperature", TemperatureProperty.class);
/*    */     
/* 59 */     return WmiQueryHandler.createInstance().queryWMI(curTempQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\MSAcpiThermalZoneTemperature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */