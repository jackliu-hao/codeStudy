/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.DataReadException;
/*    */ import com.mysql.cj.protocol.InternalDate;
/*    */ import com.mysql.cj.protocol.InternalTime;
/*    */ import com.mysql.cj.protocol.InternalTimestamp;
/*    */ import java.time.LocalDateTime;
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
/*    */ public class LocalDateTimeValueFactory
/*    */   extends AbstractDateTimeValueFactory<LocalDateTime>
/*    */ {
/*    */   public LocalDateTimeValueFactory(PropertySet pset) {
/* 47 */     super(pset);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LocalDateTime localCreateFromDate(InternalDate idate) {
/* 57 */     return createFromTimestamp(new InternalTimestamp(idate.getYear(), idate.getMonth(), idate.getDay(), 0, 0, 0, 0, 0));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LocalDateTime localCreateFromTime(InternalTime it) {
/* 67 */     if (it.getHours() < 0 || it.getHours() >= 24) {
/* 68 */       throw new DataReadException(Messages.getString("ResultSet.InvalidTimeValue", new Object[] { it.toString() }));
/*    */     }
/* 70 */     return createFromTimestamp(new InternalTimestamp(1970, 1, 1, it.getHours(), it.getMinutes(), it.getSeconds(), it.getNanos(), it.getScale()));
/*    */   }
/*    */ 
/*    */   
/*    */   public LocalDateTime localCreateFromTimestamp(InternalTimestamp its) {
/* 75 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/* 76 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*    */     }
/* 78 */     return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos());
/*    */   }
/*    */ 
/*    */   
/*    */   public LocalDateTime localCreateFromDatetime(InternalTimestamp its) {
/* 83 */     if (its.getYear() == 0 && its.getMonth() == 0 && its.getDay() == 0) {
/* 84 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*    */     }
/* 86 */     return LocalDateTime.of(its.getYear(), its.getMonth(), its.getDay(), its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos());
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 90 */     return LocalDateTime.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\LocalDateTimeValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */