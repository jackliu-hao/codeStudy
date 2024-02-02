/*     */ package org.h2.api;
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
/*     */ public enum IntervalQualifier
/*     */ {
/*  16 */   YEAR,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  21 */   MONTH,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  26 */   DAY,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   HOUR,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  36 */   MINUTE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   SECOND,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   YEAR_TO_MONTH,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   DAY_TO_HOUR,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   DAY_TO_MINUTE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   DAY_TO_SECOND,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   HOUR_TO_MINUTE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   HOUR_TO_SECOND,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   MINUTE_TO_SECOND;
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
/*     */ 
/*     */   
/*     */   private final String string;
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
/*     */ 
/*     */   
/*     */   IntervalQualifier() {
/* 121 */     this.string = name().replace('_', ' ').intern();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isYearMonth() {
/* 130 */     return (this == YEAR || this == MONTH || this == YEAR_TO_MONTH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDayTime() {
/* 139 */     return !isYearMonth();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasYears() {
/* 148 */     return (this == YEAR || this == YEAR_TO_MONTH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasMonths() {
/* 157 */     return (this == MONTH || this == YEAR_TO_MONTH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasDays() {
/* 166 */     switch (this) {
/*     */       case DAY:
/*     */       case DAY_TO_HOUR:
/*     */       case DAY_TO_MINUTE:
/*     */       case DAY_TO_SECOND:
/* 171 */         return true;
/*     */     } 
/* 173 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasHours() {
/* 183 */     switch (this) {
/*     */       case DAY_TO_HOUR:
/*     */       case DAY_TO_MINUTE:
/*     */       case DAY_TO_SECOND:
/*     */       case HOUR:
/*     */       case HOUR_TO_MINUTE:
/*     */       case HOUR_TO_SECOND:
/* 190 */         return true;
/*     */     } 
/* 192 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasMinutes() {
/* 202 */     switch (this) {
/*     */       case DAY_TO_MINUTE:
/*     */       case DAY_TO_SECOND:
/*     */       case HOUR_TO_MINUTE:
/*     */       case HOUR_TO_SECOND:
/*     */       case MINUTE:
/*     */       case MINUTE_TO_SECOND:
/* 209 */         return true;
/*     */     } 
/* 211 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasSeconds() {
/* 221 */     switch (this) {
/*     */       case DAY_TO_SECOND:
/*     */       case HOUR_TO_SECOND:
/*     */       case MINUTE_TO_SECOND:
/*     */       case SECOND:
/* 226 */         return true;
/*     */     } 
/* 228 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasMultipleFields() {
/* 238 */     return (ordinal() > 5);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 243 */     return this.string;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTypeName(int paramInt1, int paramInt2) {
/* 254 */     return getTypeName(new StringBuilder(), paramInt1, paramInt2, false).toString();
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
/*     */   public StringBuilder getTypeName(StringBuilder paramStringBuilder, int paramInt1, int paramInt2, boolean paramBoolean) {
/* 267 */     if (!paramBoolean) {
/* 268 */       paramStringBuilder.append("INTERVAL ");
/*     */     }
/* 270 */     switch (this) {
/*     */       case DAY:
/*     */       case HOUR:
/*     */       case MINUTE:
/*     */       case YEAR:
/*     */       case MONTH:
/* 276 */         paramStringBuilder.append(this.string);
/* 277 */         if (paramInt1 > 0) {
/* 278 */           paramStringBuilder.append('(').append(paramInt1).append(')');
/*     */         }
/*     */         break;
/*     */       case SECOND:
/* 282 */         paramStringBuilder.append(this.string);
/* 283 */         if (paramInt1 > 0 || paramInt2 >= 0) {
/* 284 */           paramStringBuilder.append('(').append((paramInt1 > 0) ? paramInt1 : 2);
/* 285 */           if (paramInt2 >= 0) {
/* 286 */             paramStringBuilder.append(", ").append(paramInt2);
/*     */           }
/* 288 */           paramStringBuilder.append(')');
/*     */         } 
/*     */         break;
/*     */       case YEAR_TO_MONTH:
/* 292 */         paramStringBuilder.append("YEAR");
/* 293 */         if (paramInt1 > 0) {
/* 294 */           paramStringBuilder.append('(').append(paramInt1).append(')');
/*     */         }
/* 296 */         paramStringBuilder.append(" TO MONTH");
/*     */         break;
/*     */       case DAY_TO_HOUR:
/* 299 */         paramStringBuilder.append("DAY");
/* 300 */         if (paramInt1 > 0) {
/* 301 */           paramStringBuilder.append('(').append(paramInt1).append(')');
/*     */         }
/* 303 */         paramStringBuilder.append(" TO HOUR");
/*     */         break;
/*     */       case DAY_TO_MINUTE:
/* 306 */         paramStringBuilder.append("DAY");
/* 307 */         if (paramInt1 > 0) {
/* 308 */           paramStringBuilder.append('(').append(paramInt1).append(')');
/*     */         }
/* 310 */         paramStringBuilder.append(" TO MINUTE");
/*     */         break;
/*     */       case DAY_TO_SECOND:
/* 313 */         paramStringBuilder.append("DAY");
/* 314 */         if (paramInt1 > 0) {
/* 315 */           paramStringBuilder.append('(').append(paramInt1).append(')');
/*     */         }
/* 317 */         paramStringBuilder.append(" TO SECOND");
/* 318 */         if (paramInt2 >= 0) {
/* 319 */           paramStringBuilder.append('(').append(paramInt2).append(')');
/*     */         }
/*     */         break;
/*     */       case HOUR_TO_MINUTE:
/* 323 */         paramStringBuilder.append("HOUR");
/* 324 */         if (paramInt1 > 0) {
/* 325 */           paramStringBuilder.append('(').append(paramInt1).append(')');
/*     */         }
/* 327 */         paramStringBuilder.append(" TO MINUTE");
/*     */         break;
/*     */       case HOUR_TO_SECOND:
/* 330 */         paramStringBuilder.append("HOUR");
/* 331 */         if (paramInt1 > 0) {
/* 332 */           paramStringBuilder.append('(').append(paramInt1).append(')');
/*     */         }
/* 334 */         paramStringBuilder.append(" TO SECOND");
/* 335 */         if (paramInt2 >= 0) {
/* 336 */           paramStringBuilder.append('(').append(paramInt2).append(')');
/*     */         }
/*     */         break;
/*     */       case MINUTE_TO_SECOND:
/* 340 */         paramStringBuilder.append("MINUTE");
/* 341 */         if (paramInt1 > 0) {
/* 342 */           paramStringBuilder.append('(').append(paramInt1).append(')');
/*     */         }
/* 344 */         paramStringBuilder.append(" TO SECOND");
/* 345 */         if (paramInt2 >= 0)
/* 346 */           paramStringBuilder.append('(').append(paramInt2).append(')'); 
/*     */         break;
/*     */     } 
/* 349 */     return paramStringBuilder;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\api\IntervalQualifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */