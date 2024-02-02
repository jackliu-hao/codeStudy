/*    */ package cn.hutool.core.date;
/*    */ 
/*    */ import java.time.ZoneId;
/*    */ import java.util.TimeZone;
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
/*    */ public class ZoneUtil
/*    */ {
/*    */   public static TimeZone toTimeZone(ZoneId zoneId) {
/* 21 */     if (null == zoneId) {
/* 22 */       return TimeZone.getDefault();
/*    */     }
/*    */     
/* 25 */     return TimeZone.getTimeZone(zoneId);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ZoneId toZoneId(TimeZone timeZone) {
/* 35 */     if (null == timeZone) {
/* 36 */       return ZoneId.systemDefault();
/*    */     }
/*    */     
/* 39 */     return timeZone.toZoneId();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\ZoneUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */