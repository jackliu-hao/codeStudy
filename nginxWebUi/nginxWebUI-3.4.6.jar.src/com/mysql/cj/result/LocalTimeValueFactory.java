/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.WarningListener;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.DataReadException;
/*    */ import com.mysql.cj.protocol.InternalDate;
/*    */ import com.mysql.cj.protocol.InternalTime;
/*    */ import com.mysql.cj.protocol.InternalTimestamp;
/*    */ import java.time.LocalTime;
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
/*    */ public class LocalTimeValueFactory
/*    */   extends AbstractDateTimeValueFactory<LocalTime>
/*    */ {
/*    */   private WarningListener warningListener;
/*    */   
/*    */   public LocalTimeValueFactory(PropertySet pset) {
/* 49 */     super(pset);
/*    */   }
/*    */   
/*    */   public LocalTimeValueFactory(PropertySet pset, WarningListener warningListener) {
/* 53 */     this(pset);
/* 54 */     this.warningListener = warningListener;
/*    */   }
/*    */ 
/*    */   
/*    */   LocalTime localCreateFromDate(InternalDate idate) {
/* 59 */     return LocalTime.of(0, 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public LocalTime localCreateFromTime(InternalTime it) {
/* 64 */     if (it.getHours() < 0 || it.getHours() >= 24) {
/* 65 */       throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[] { it.toString() }));
/*    */     }
/* 67 */     return LocalTime.of(it.getHours(), it.getMinutes(), it.getSeconds(), it.getNanos());
/*    */   }
/*    */ 
/*    */   
/*    */   public LocalTime localCreateFromTimestamp(InternalTimestamp its) {
/* 72 */     if (this.warningListener != null) {
/* 73 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { getTargetTypeName() }));
/*    */     }
/*    */     
/* 76 */     return createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
/*    */   }
/*    */ 
/*    */   
/*    */   public LocalTime localCreateFromDatetime(InternalTimestamp its) {
/* 81 */     if (this.warningListener != null) {
/* 82 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { getTargetTypeName() }));
/*    */     }
/*    */     
/* 85 */     return createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale()));
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 89 */     return LocalTime.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\LocalTimeValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */