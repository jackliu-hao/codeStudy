/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimeInterval
/*     */   extends GroupTimeInterval
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String DEFAULT_ID = "";
/*     */   
/*     */   public TimeInterval() {
/*  19 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TimeInterval(boolean isNano) {
/*  28 */     super(isNano);
/*  29 */     start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long start() {
/*  36 */     return start("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalRestart() {
/*  43 */     return intervalRestart("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TimeInterval restart() {
/*  54 */     start("");
/*  55 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long interval() {
/*  67 */     return interval("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String intervalPretty() {
/*  77 */     return intervalPretty("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalMs() {
/*  86 */     return intervalMs("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalSecond() {
/*  95 */     return intervalSecond("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalMinute() {
/* 104 */     return intervalMinute("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalHour() {
/* 113 */     return intervalHour("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalDay() {
/* 122 */     return intervalDay("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalWeek() {
/* 131 */     return intervalWeek("");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\TimeInterval.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */