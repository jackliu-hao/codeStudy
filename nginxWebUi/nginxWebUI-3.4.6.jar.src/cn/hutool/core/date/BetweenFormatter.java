/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BetweenFormatter
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private long betweenMs;
/*     */   private Level level;
/*     */   private final int levelMaxCount;
/*     */   
/*     */   public BetweenFormatter(long betweenMs, Level level) {
/*  41 */     this(betweenMs, level, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BetweenFormatter(long betweenMs, Level level, int levelMaxCount) {
/*  52 */     this.betweenMs = betweenMs;
/*  53 */     this.level = level;
/*  54 */     this.levelMaxCount = levelMaxCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String format() {
/*  63 */     StringBuilder sb = new StringBuilder();
/*  64 */     if (this.betweenMs > 0L) {
/*  65 */       long day = this.betweenMs / DateUnit.DAY.getMillis();
/*  66 */       long hour = this.betweenMs / DateUnit.HOUR.getMillis() - day * 24L;
/*  67 */       long minute = this.betweenMs / DateUnit.MINUTE.getMillis() - day * 24L * 60L - hour * 60L;
/*     */       
/*  69 */       long BetweenOfSecond = ((day * 24L + hour) * 60L + minute) * 60L;
/*  70 */       long second = this.betweenMs / DateUnit.SECOND.getMillis() - BetweenOfSecond;
/*  71 */       long millisecond = this.betweenMs - (BetweenOfSecond + second) * 1000L;
/*     */       
/*  73 */       int level = this.level.ordinal();
/*  74 */       int levelCount = 0;
/*     */       
/*  76 */       if (isLevelCountValid(levelCount) && 0L != day && level >= Level.DAY.ordinal()) {
/*  77 */         sb.append(day).append(Level.DAY.name);
/*  78 */         levelCount++;
/*     */       } 
/*  80 */       if (isLevelCountValid(levelCount) && 0L != hour && level >= Level.HOUR.ordinal()) {
/*  81 */         sb.append(hour).append(Level.HOUR.name);
/*  82 */         levelCount++;
/*     */       } 
/*  84 */       if (isLevelCountValid(levelCount) && 0L != minute && level >= Level.MINUTE.ordinal()) {
/*  85 */         sb.append(minute).append(Level.MINUTE.name);
/*  86 */         levelCount++;
/*     */       } 
/*  88 */       if (isLevelCountValid(levelCount) && 0L != second && level >= Level.SECOND.ordinal()) {
/*  89 */         sb.append(second).append(Level.SECOND.name);
/*  90 */         levelCount++;
/*     */       } 
/*  92 */       if (isLevelCountValid(levelCount) && 0L != millisecond && level >= Level.MILLISECOND.ordinal()) {
/*  93 */         sb.append(millisecond).append(Level.MILLISECOND.name);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  98 */     if (StrUtil.isEmpty(sb)) {
/*  99 */       sb.append(0).append(this.level.name);
/*     */     }
/*     */     
/* 102 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getBetweenMs() {
/* 111 */     return this.betweenMs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBetweenMs(long betweenMs) {
/* 120 */     this.betweenMs = betweenMs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Level getLevel() {
/* 129 */     return this.level;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevel(Level level) {
/* 138 */     this.level = level;
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
/*     */   public enum Level
/*     */   {
/* 151 */     DAY("天"),
/*     */ 
/*     */ 
/*     */     
/* 155 */     HOUR("小时"),
/*     */ 
/*     */ 
/*     */     
/* 159 */     MINUTE("分"),
/*     */ 
/*     */ 
/*     */     
/* 163 */     SECOND("秒"),
/*     */ 
/*     */ 
/*     */     
/* 167 */     MILLISECOND("毫秒");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Level(String name) {
/* 180 */       this.name = name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 189 */       return this.name;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 195 */     return format();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isLevelCountValid(int levelCount) {
/* 206 */     return (this.levelMaxCount <= 0 || levelCount < this.levelMaxCount);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\BetweenFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */