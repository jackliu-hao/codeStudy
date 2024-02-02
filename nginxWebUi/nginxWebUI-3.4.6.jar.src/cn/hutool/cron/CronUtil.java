/*     */ package cn.hutool.cron;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.cron.pattern.CronPattern;
/*     */ import cn.hutool.cron.task.Task;
/*     */ import cn.hutool.setting.Setting;
/*     */ import cn.hutool.setting.SettingRuntimeException;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
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
/*     */ public class CronUtil
/*     */ {
/*     */   public static final String CRONTAB_CONFIG_PATH = "config/cron.setting";
/*     */   public static final String CRONTAB_CONFIG_PATH2 = "cron.setting";
/*  28 */   private static final Lock lock = new ReentrantLock();
/*  29 */   private static final Scheduler scheduler = new Scheduler();
/*     */ 
/*     */ 
/*     */   
/*     */   private static Setting crontabSetting;
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setCronSetting(Setting cronSetting) {
/*  38 */     crontabSetting = cronSetting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setCronSetting(String cronSettingPath) {
/*     */     try {
/*  48 */       crontabSetting = new Setting(cronSettingPath, Setting.DEFAULT_CHARSET, false);
/*  49 */     } catch (SettingRuntimeException|cn.hutool.core.io.resource.NoResourceException settingRuntimeException) {}
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
/*     */   public static void setMatchSecond(boolean isMatchSecond) {
/*  61 */     scheduler.setMatchSecond(isMatchSecond);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String schedule(String schedulingPattern, Task task) {
/*  72 */     return scheduler.schedule(schedulingPattern, task);
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
/*     */   public static String schedule(String id, String schedulingPattern, Task task) {
/*  85 */     scheduler.schedule(id, schedulingPattern, task);
/*  86 */     return id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String schedule(String schedulingPattern, Runnable task) {
/*  97 */     return scheduler.schedule(schedulingPattern, task);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void schedule(Setting cronSetting) {
/* 106 */     scheduler.schedule(cronSetting);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean remove(String schedulerId) {
/* 116 */     return scheduler.descheduleWithStatus(schedulerId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void updatePattern(String id, CronPattern pattern) {
/* 127 */     scheduler.updatePattern(id, pattern);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Scheduler getScheduler() {
/* 134 */     return scheduler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void start() {
/* 143 */     start(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void start(boolean isDaemon) {
/* 152 */     if (scheduler.isStarted()) {
/* 153 */       throw new UtilException("Scheduler has been started, please stop it first!");
/*     */     }
/*     */     
/* 156 */     lock.lock();
/*     */     try {
/* 158 */       if (null == crontabSetting)
/*     */       {
/* 160 */         setCronSetting("config/cron.setting");
/*     */       }
/*     */       
/* 163 */       if (null == crontabSetting) {
/* 164 */         setCronSetting("cron.setting");
/*     */       }
/*     */     } finally {
/* 167 */       lock.unlock();
/*     */     } 
/*     */     
/* 170 */     schedule(crontabSetting);
/* 171 */     scheduler.start(isDaemon);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void restart() {
/* 179 */     lock.lock();
/*     */     try {
/* 181 */       if (null != crontabSetting)
/*     */       {
/* 183 */         crontabSetting.load();
/*     */       }
/* 185 */       if (scheduler.isStarted())
/*     */       {
/* 187 */         stop();
/*     */       }
/*     */     } finally {
/* 190 */       lock.unlock();
/*     */     } 
/*     */ 
/*     */     
/* 194 */     schedule(crontabSetting);
/*     */     
/* 196 */     scheduler.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void stop() {
/* 203 */     scheduler.stop(true);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\CronUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */