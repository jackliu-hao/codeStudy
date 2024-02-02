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
/*    */ public final class OhmSensor
/*    */ {
/*    */   private static final String SENSOR = "Sensor";
/*    */   
/*    */   public enum ValueProperty
/*    */   {
/* 45 */     VALUE;
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
/*    */   public static WbemcliUtil.WmiResult<ValueProperty> querySensorValue(String identifier, String sensorType) {
/* 61 */     StringBuilder sb = new StringBuilder("Sensor");
/* 62 */     sb.append(" WHERE Parent = \"").append(identifier);
/* 63 */     sb.append("\" AND SensorType=\"").append(sensorType).append('"');
/* 64 */     WbemcliUtil.WmiQuery<ValueProperty> ohmSensorQuery = new WbemcliUtil.WmiQuery("ROOT\\OpenHardwareMonitor", sb.toString(), ValueProperty.class);
/*    */     
/* 66 */     return WmiQueryHandler.createInstance().queryWMI(ohmSensorQuery);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\OhmSensor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */