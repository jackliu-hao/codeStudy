/*    */ package cn.hutool.core.date;
/*    */ 
/*    */ import cn.hutool.core.lang.Range;
/*    */ import java.util.Date;
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
/*    */ public class DateRange
/*    */   extends Range<DateTime>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DateRange(Date start, Date end, DateField unit) {
/* 24 */     this(start, end, unit, 1);
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
/*    */   public DateRange(Date start, Date end, DateField unit, int step) {
/* 36 */     this(start, end, unit, step, true, true);
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
/*    */   public DateRange(Date start, Date end, DateField unit, int step, boolean isIncludeStart, boolean isIncludeEnd) {
/* 50 */     super(DateUtil.date(start), DateUtil.date(end), (current, end1, index) -> { DateTime dt = DateUtil.date(start).offsetNew(unit, (index + 1) * step); return dt.isAfter(end1) ? null : dt; }isIncludeStart, isIncludeEnd);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\DateRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */