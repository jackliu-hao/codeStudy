/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class GroupTimeInterval
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final boolean isNano;
/*     */   protected final Map<String, Long> groupMap;
/*     */   
/*     */   public GroupTimeInterval(boolean isNano) {
/*  28 */     this.isNano = isNano;
/*  29 */     this.groupMap = new ConcurrentHashMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GroupTimeInterval clear() {
/*  38 */     this.groupMap.clear();
/*  39 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long start(String id) {
/*  49 */     long time = getTime();
/*  50 */     this.groupMap.put(id, Long.valueOf(time));
/*  51 */     return time;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalRestart(String id) {
/*  62 */     long now = getTime();
/*  63 */     return now - ((Long)ObjectUtil.defaultIfNull(this.groupMap.put(id, Long.valueOf(now)), Long.valueOf(now))).longValue();
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
/*     */   public long interval(String id) {
/*  77 */     Long lastTime = this.groupMap.get(id);
/*  78 */     if (null == lastTime) {
/*  79 */       return 0L;
/*     */     }
/*  81 */     return getTime() - lastTime.longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long interval(String id, DateUnit dateUnit) {
/*  92 */     long intervalMs = this.isNano ? (interval(id) / 1000000L) : interval(id);
/*  93 */     if (DateUnit.MS == dateUnit) {
/*  94 */       return intervalMs;
/*     */     }
/*  96 */     return intervalMs / dateUnit.getMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalMs(String id) {
/* 106 */     return interval(id, DateUnit.MS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalSecond(String id) {
/* 116 */     return interval(id, DateUnit.SECOND);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalMinute(String id) {
/* 126 */     return interval(id, DateUnit.MINUTE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalHour(String id) {
/* 136 */     return interval(id, DateUnit.HOUR);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalDay(String id) {
/* 146 */     return interval(id, DateUnit.DAY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intervalWeek(String id) {
/* 156 */     return interval(id, DateUnit.WEEK);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String intervalPretty(String id) {
/* 166 */     return DateUtil.formatBetween(intervalMs(id));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long getTime() {
/* 175 */     return this.isNano ? System.nanoTime() : System.currentTimeMillis();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\GroupTimeInterval.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */