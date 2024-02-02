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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum DateField
/*     */ {
/*  19 */   ERA(0),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  25 */   YEAR(1),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   MONTH(2),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  37 */   WEEK_OF_YEAR(3),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   WEEK_OF_MONTH(4),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   DAY_OF_MONTH(5),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   DAY_OF_YEAR(6),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   DAY_OF_WEEK(7),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   DAY_OF_WEEK_IN_MONTH(8),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   AM_PM(9),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   HOUR(10),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   HOUR_OF_DAY(11),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   MINUTE(12),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   SECOND(13),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   MILLISECOND(14);
/*     */   
/*     */   private final int value;
/*     */ 
/*     */   
/*     */   DateField(int value) {
/* 109 */     this.value = value;
/*     */   }
/*     */   
/*     */   public int getValue() {
/* 113 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DateField of(int calendarPartIntValue) {
/* 123 */     switch (calendarPartIntValue) {
/*     */       case 0:
/* 125 */         return ERA;
/*     */       case 1:
/* 127 */         return YEAR;
/*     */       case 2:
/* 129 */         return MONTH;
/*     */       case 3:
/* 131 */         return WEEK_OF_YEAR;
/*     */       case 4:
/* 133 */         return WEEK_OF_MONTH;
/*     */       case 5:
/* 135 */         return DAY_OF_MONTH;
/*     */       case 6:
/* 137 */         return DAY_OF_YEAR;
/*     */       case 7:
/* 139 */         return DAY_OF_WEEK;
/*     */       case 8:
/* 141 */         return DAY_OF_WEEK_IN_MONTH;
/*     */       case 9:
/* 143 */         return AM_PM;
/*     */       case 10:
/* 145 */         return HOUR;
/*     */       case 11:
/* 147 */         return HOUR_OF_DAY;
/*     */       case 12:
/* 149 */         return MINUTE;
/*     */       case 13:
/* 151 */         return SECOND;
/*     */       case 14:
/* 153 */         return MILLISECOND;
/*     */     } 
/* 155 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\DateField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */