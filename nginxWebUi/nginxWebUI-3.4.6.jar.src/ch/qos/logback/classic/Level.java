/*     */ package ch.qos.logback.classic;
/*     */ 
/*     */ import java.io.Serializable;
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
/*     */ 
/*     */ public final class Level
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -814092767334282137L;
/*     */   public static final int OFF_INT = 2147483647;
/*     */   public static final int ERROR_INT = 40000;
/*     */   public static final int WARN_INT = 30000;
/*     */   public static final int INFO_INT = 20000;
/*     */   public static final int DEBUG_INT = 10000;
/*     */   public static final int TRACE_INT = 5000;
/*     */   public static final int ALL_INT = -2147483648;
/*  37 */   public static final Integer OFF_INTEGER = Integer.valueOf(2147483647);
/*  38 */   public static final Integer ERROR_INTEGER = Integer.valueOf(40000);
/*  39 */   public static final Integer WARN_INTEGER = Integer.valueOf(30000);
/*  40 */   public static final Integer INFO_INTEGER = Integer.valueOf(20000);
/*  41 */   public static final Integer DEBUG_INTEGER = Integer.valueOf(10000);
/*  42 */   public static final Integer TRACE_INTEGER = Integer.valueOf(5000);
/*  43 */   public static final Integer ALL_INTEGER = Integer.valueOf(-2147483648);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   public static final Level OFF = new Level(2147483647, "OFF");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   public static final Level ERROR = new Level(40000, "ERROR");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   public static final Level WARN = new Level(30000, "WARN");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   public static final Level INFO = new Level(20000, "INFO");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   public static final Level DEBUG = new Level(10000, "DEBUG");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   public static final Level TRACE = new Level(5000, "TRACE");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   public static final Level ALL = new Level(-2147483648, "ALL");
/*     */ 
/*     */   
/*     */   public final int levelInt;
/*     */   
/*     */   public final String levelStr;
/*     */ 
/*     */   
/*     */   private Level(int levelInt, String levelStr) {
/*  91 */     this.levelInt = levelInt;
/*  92 */     this.levelStr = levelStr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  99 */     return this.levelStr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int toInt() {
/* 106 */     return this.levelInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer toInteger() {
/* 115 */     switch (this.levelInt) {
/*     */       case -2147483648:
/* 117 */         return ALL_INTEGER;
/*     */       case 5000:
/* 119 */         return TRACE_INTEGER;
/*     */       case 10000:
/* 121 */         return DEBUG_INTEGER;
/*     */       case 20000:
/* 123 */         return INFO_INTEGER;
/*     */       case 30000:
/* 125 */         return WARN_INTEGER;
/*     */       case 40000:
/* 127 */         return ERROR_INTEGER;
/*     */       case 2147483647:
/* 129 */         return OFF_INTEGER;
/*     */     } 
/* 131 */     throw new IllegalStateException("Level " + this.levelStr + ", " + this.levelInt + " is unknown.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isGreaterOrEqual(Level r) {
/* 140 */     return (this.levelInt >= r.levelInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Level toLevel(String sArg) {
/* 148 */     return toLevel(sArg, DEBUG);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Level valueOf(String sArg) {
/* 158 */     return toLevel(sArg, DEBUG);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Level toLevel(int val) {
/* 166 */     return toLevel(val, DEBUG);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Level toLevel(int val, Level defaultLevel) {
/* 174 */     switch (val) {
/*     */       case -2147483648:
/* 176 */         return ALL;
/*     */       case 5000:
/* 178 */         return TRACE;
/*     */       case 10000:
/* 180 */         return DEBUG;
/*     */       case 20000:
/* 182 */         return INFO;
/*     */       case 30000:
/* 184 */         return WARN;
/*     */       case 40000:
/* 186 */         return ERROR;
/*     */       case 2147483647:
/* 188 */         return OFF;
/*     */     } 
/* 190 */     return defaultLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Level toLevel(String sArg, Level defaultLevel) {
/* 199 */     if (sArg == null) {
/* 200 */       return defaultLevel;
/*     */     }
/*     */     
/* 203 */     if (sArg.equalsIgnoreCase("ALL")) {
/* 204 */       return ALL;
/*     */     }
/* 206 */     if (sArg.equalsIgnoreCase("TRACE")) {
/* 207 */       return TRACE;
/*     */     }
/* 209 */     if (sArg.equalsIgnoreCase("DEBUG")) {
/* 210 */       return DEBUG;
/*     */     }
/* 212 */     if (sArg.equalsIgnoreCase("INFO")) {
/* 213 */       return INFO;
/*     */     }
/* 215 */     if (sArg.equalsIgnoreCase("WARN")) {
/* 216 */       return WARN;
/*     */     }
/* 218 */     if (sArg.equalsIgnoreCase("ERROR")) {
/* 219 */       return ERROR;
/*     */     }
/* 221 */     if (sArg.equalsIgnoreCase("OFF")) {
/* 222 */       return OFF;
/*     */     }
/* 224 */     return defaultLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object readResolve() {
/* 234 */     return toLevel(this.levelInt);
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
/*     */   public static Level fromLocationAwareLoggerInteger(int levelInt) {
/*     */     Level level;
/* 247 */     switch (levelInt) {
/*     */       case 0:
/* 249 */         level = TRACE;
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
/* 266 */         return level;case 10: level = DEBUG; return level;case 20: level = INFO; return level;case 30: level = WARN; return level;case 40: level = ERROR; return level;
/*     */     } 
/*     */     throw new IllegalArgumentException(levelInt + " not a valid level value");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int toLocationAwareLoggerInteger(Level level) {
/* 278 */     if (level == null)
/* 279 */       throw new IllegalArgumentException("null level parameter is not admitted"); 
/* 280 */     switch (level.toInt()) {
/*     */       case 5000:
/* 282 */         return 0;
/*     */       case 10000:
/* 284 */         return 10;
/*     */       case 20000:
/* 286 */         return 20;
/*     */       case 30000:
/* 288 */         return 30;
/*     */       case 40000:
/* 290 */         return 40;
/*     */     } 
/* 292 */     throw new IllegalArgumentException(level + " not a valid level value");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\Level.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */