/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.WarningListener;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.DataReadException;
/*    */ import com.mysql.cj.protocol.InternalDate;
/*    */ import com.mysql.cj.protocol.InternalTime;
/*    */ import com.mysql.cj.protocol.InternalTimestamp;
/*    */ import java.time.LocalDate;
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
/*    */ public class LocalDateValueFactory
/*    */   extends AbstractDateTimeValueFactory<LocalDate>
/*    */ {
/*    */   private WarningListener warningListener;
/*    */   
/*    */   public LocalDateValueFactory(PropertySet pset) {
/* 49 */     super(pset);
/*    */   }
/*    */   
/*    */   public LocalDateValueFactory(PropertySet pset, WarningListener warningListener) {
/* 53 */     this(pset);
/* 54 */     this.warningListener = warningListener;
/*    */   }
/*    */ 
/*    */   
/*    */   public LocalDate localCreateFromDate(InternalDate idate) {
/* 59 */     if (idate.getYear() == 0 && idate.getMonth() == 0 && idate.getDay() == 0) {
/* 60 */       throw new DataReadException(Messages.getString("ResultSet.InvalidZeroDate"));
/*    */     }
/* 62 */     return LocalDate.of(idate.getYear(), idate.getMonth(), idate.getDay());
/*    */   }
/*    */ 
/*    */   
/*    */   public LocalDate localCreateFromTimestamp(InternalTimestamp its) {
/* 67 */     if (this.warningListener != null) {
/* 68 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { getTargetTypeName() }));
/*    */     }
/*    */     
/* 71 */     return createFromDate((InternalDate)its);
/*    */   }
/*    */ 
/*    */   
/*    */   public LocalDate localCreateFromDatetime(InternalTimestamp its) {
/* 76 */     if (this.warningListener != null) {
/* 77 */       this.warningListener.warningEncountered(Messages.getString("ResultSet.PrecisionLostWarning", new Object[] { getTargetTypeName() }));
/*    */     }
/*    */     
/* 80 */     return createFromDate((InternalDate)its);
/*    */   }
/*    */ 
/*    */   
/*    */   LocalDate localCreateFromTime(InternalTime it) {
/* 85 */     return LocalDate.of(1970, 1, 1);
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 89 */     return LocalDate.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\LocalDateValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */