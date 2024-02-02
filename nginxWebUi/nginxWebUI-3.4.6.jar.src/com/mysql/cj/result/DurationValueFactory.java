/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.protocol.InternalDate;
/*    */ import com.mysql.cj.protocol.InternalTime;
/*    */ import com.mysql.cj.protocol.InternalTimestamp;
/*    */ import java.time.Duration;
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
/*    */ 
/*    */ public class DurationValueFactory
/*    */   extends AbstractDateTimeValueFactory<Duration>
/*    */ {
/*    */   public DurationValueFactory(PropertySet pset) {
/* 45 */     super(pset);
/*    */   }
/*    */ 
/*    */   
/*    */   Duration localCreateFromDate(InternalDate idate) {
/* 50 */     return unsupported("DATE");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Duration localCreateFromTime(InternalTime it) {
/* 56 */     String ptn = ((it.getHours() < 0) ? "-PT" : "PT") + ((it.getHours() < 0) ? -it.getHours() : it.getHours()) + "H" + it.getMinutes() + "M" + it.getSeconds() + "." + it.getNanos() + "S";
/* 57 */     return Duration.parse(ptn);
/*    */   }
/*    */ 
/*    */   
/*    */   public Duration localCreateFromTimestamp(InternalTimestamp its) {
/* 62 */     return unsupported("TIMESTAMP");
/*    */   }
/*    */ 
/*    */   
/*    */   public Duration localCreateFromDatetime(InternalTimestamp its) {
/* 67 */     return unsupported("DATETIME");
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 71 */     return Duration.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\DurationValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */