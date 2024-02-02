/*     */ package org.noear.solon.schedule;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.schedule.cron.CronExpressionPlus;
/*     */ import org.noear.solon.schedule.cron.CronUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JobManager
/*     */ {
/*  17 */   private static Map<String, JobEntity> jobEntityMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isStarted = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void add(String name, String cron, boolean concurrent, Runnable runnable) throws ParseException {
/*  28 */     CronExpressionPlus cronX = CronUtils.get(cron);
/*  29 */     addDo(name, new JobEntity(name, cronX, concurrent, runnable));
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
/*     */   public static void add(String name, String cron, String zone, boolean concurrent, Runnable runnable) throws ParseException {
/*  41 */     CronExpressionPlus cronX = CronUtils.get(cron);
/*     */     
/*  43 */     if (Utils.isNotEmpty(zone)) {
/*  44 */       cronX.setTimeZone(TimeZone.getTimeZone(zone));
/*     */     }
/*     */     
/*  47 */     addDo(name, new JobEntity(name, cronX, concurrent, runnable));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void add(String name, long fixedRate, boolean concurrent, Runnable runnable) {
/*  58 */     addDo(name, new JobEntity(name, fixedRate, 0L, concurrent, runnable));
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
/*     */   public static void add(String name, long fixedRate, long fixedDelay, boolean concurrent, Runnable runnable) {
/*  70 */     addDo(name, new JobEntity(name, fixedRate, fixedDelay, concurrent, runnable));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addDo(String name, JobEntity jobEntity) {
/*  80 */     jobEntityMap.putIfAbsent(name, jobEntity);
/*     */     
/*  82 */     if (isStarted)
/*     */     {
/*  84 */       jobEntity.start();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean contains(String name) {
/*  94 */     return jobEntityMap.containsKey(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void remove(String name) {
/* 103 */     JobEntity jobEntity = jobEntityMap.get(name);
/* 104 */     if (jobEntity != null) {
/* 105 */       jobEntity.cancel();
/* 106 */       jobEntityMap.remove(name);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Runnable getRunnable(String name) {
/* 114 */     JobEntity jobEntity = jobEntityMap.get(name);
/* 115 */     if (jobEntity != null) {
/* 116 */       return jobEntity.runnable;
/*     */     }
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void start() {
/* 126 */     for (JobEntity job : jobEntityMap.values()) {
/* 127 */       job.start();
/*     */     }
/* 129 */     isStarted = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void stop() {
/* 136 */     for (JobEntity job : jobEntityMap.values()) {
/* 137 */       job.cancel();
/*     */     }
/* 139 */     isStarted = false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\schedule\JobManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */