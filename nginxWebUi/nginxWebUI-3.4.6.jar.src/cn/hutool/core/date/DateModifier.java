/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.util.Calendar;
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
/*     */ 
/*     */ 
/*     */ public class DateModifier
/*     */ {
/*  24 */   private static final int[] IGNORE_FIELDS = new int[] { 11, 9, 8, 6, 4, 3 };
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
/*     */   
/*     */   public static Calendar modify(Calendar calendar, int dateField, ModifyType modifyType) {
/*  42 */     return modify(calendar, dateField, modifyType, false);
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
/*     */   public static Calendar modify(Calendar calendar, int dateField, ModifyType modifyType, boolean truncateMillisecond) {
/*  64 */     if (9 == dateField) {
/*  65 */       int min, max, href, value; boolean isAM = DateUtil.isAM(calendar);
/*  66 */       switch (modifyType) {
/*     */         case TRUNCATE:
/*  68 */           calendar.set(11, isAM ? 0 : 12);
/*     */           break;
/*     */         case CEILING:
/*  71 */           calendar.set(11, isAM ? 11 : 23);
/*     */           break;
/*     */         case ROUND:
/*  74 */           min = isAM ? 0 : 12;
/*  75 */           max = isAM ? 11 : 23;
/*  76 */           href = (max - min) / 2 + 1;
/*  77 */           value = calendar.get(11);
/*  78 */           calendar.set(11, (value < href) ? min : max);
/*     */           break;
/*     */       } 
/*     */       
/*  82 */       return modify(calendar, dateField + 1, modifyType);
/*     */     } 
/*     */     
/*  85 */     int endField = truncateMillisecond ? 13 : 14;
/*     */     
/*  87 */     for (int i = dateField + 1; i <= endField; i++) {
/*  88 */       if (!ArrayUtil.contains(IGNORE_FIELDS, i))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  94 */         if ((3 == dateField) ? (
/*  95 */           5 == i) : (
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 100 */           7 == i))
/*     */         {
/*     */ 
/*     */ 
/*     */           
/* 105 */           modifyField(calendar, i, modifyType); } 
/*     */       }
/*     */     } 
/* 108 */     if (truncateMillisecond) {
/* 109 */       calendar.set(14, 0);
/*     */     }
/*     */     
/* 112 */     return calendar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void modifyField(Calendar calendar, int field, ModifyType modifyType) {
/*     */     int min;
/*     */     int max;
/*     */     int href;
/*     */     int value;
/* 125 */     if (10 == field)
/*     */     {
/* 127 */       field = 11;
/*     */     }
/*     */     
/* 130 */     switch (modifyType) {
/*     */       case TRUNCATE:
/* 132 */         calendar.set(field, DateUtil.getBeginValue(calendar, field));
/*     */         break;
/*     */       case CEILING:
/* 135 */         calendar.set(field, DateUtil.getEndValue(calendar, field));
/*     */         break;
/*     */       case ROUND:
/* 138 */         min = DateUtil.getBeginValue(calendar, field);
/* 139 */         max = DateUtil.getEndValue(calendar, field);
/*     */         
/* 141 */         if (7 == field) {
/*     */           
/* 143 */           href = (min + 3) % 7;
/*     */         } else {
/* 145 */           href = (max - min) / 2 + 1;
/*     */         } 
/* 147 */         value = calendar.get(field);
/* 148 */         calendar.set(field, (value < href) ? min : max);
/*     */         break;
/*     */     } 
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
/*     */   
/*     */   public enum ModifyType
/*     */   {
/* 164 */     TRUNCATE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 169 */     ROUND,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 174 */     CEILING;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\DateModifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */