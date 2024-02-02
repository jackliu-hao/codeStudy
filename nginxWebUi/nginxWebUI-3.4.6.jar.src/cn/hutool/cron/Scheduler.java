/*     */ package cn.hutool.cron;
/*     */ 
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.thread.ExecutorBuilder;
/*     */ import cn.hutool.core.thread.ThreadFactoryBuilder;
/*     */ import cn.hutool.core.util.IdUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.cron.listener.TaskListener;
/*     */ import cn.hutool.cron.listener.TaskListenerManager;
/*     */ import cn.hutool.cron.pattern.CronPattern;
/*     */ import cn.hutool.cron.task.InvokeTask;
/*     */ import cn.hutool.cron.task.RunnableTask;
/*     */ import cn.hutool.cron.task.Task;
/*     */ import cn.hutool.log.StaticLog;
/*     */ import cn.hutool.setting.Setting;
/*     */ import java.io.Serializable;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.ExecutorService;
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
/*     */ public class Scheduler
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  58 */   private final Lock lock = new ReentrantLock();
/*     */ 
/*     */   
/*  61 */   protected CronConfig config = new CronConfig();
/*     */ 
/*     */   
/*     */   private boolean started = false;
/*     */   
/*     */   protected boolean daemon;
/*     */   
/*     */   private CronTimer timer;
/*     */   
/*  70 */   protected TaskTable taskTable = new TaskTable();
/*     */   
/*     */   protected TaskLauncherManager taskLauncherManager;
/*     */   
/*     */   protected TaskExecutorManager taskExecutorManager;
/*     */   
/*  76 */   protected TaskListenerManager listenerManager = new TaskListenerManager();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ExecutorService threadExecutor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Scheduler setTimeZone(TimeZone timeZone) {
/*  88 */     this.config.setTimeZone(timeZone);
/*  89 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TimeZone getTimeZone() {
/*  98 */     return this.config.getTimeZone();
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
/*     */   public Scheduler setDaemon(boolean on) throws CronException {
/* 111 */     this.lock.lock();
/*     */     try {
/* 113 */       checkStarted();
/* 114 */       this.daemon = on;
/*     */     } finally {
/* 116 */       this.lock.unlock();
/*     */     } 
/* 118 */     return this;
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
/*     */   public Scheduler setThreadExecutor(ExecutorService threadExecutor) throws CronException {
/* 131 */     this.lock.lock();
/*     */     try {
/* 133 */       checkStarted();
/* 134 */       this.threadExecutor = threadExecutor;
/*     */     } finally {
/* 136 */       this.lock.unlock();
/*     */     } 
/* 138 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDaemon() {
/* 147 */     return this.daemon;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMatchSecond() {
/* 156 */     return this.config.isMatchSecond();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Scheduler setMatchSecond(boolean isMatchSecond) {
/* 166 */     this.config.setMatchSecond(isMatchSecond);
/* 167 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Scheduler addListener(TaskListener listener) {
/* 177 */     this.listenerManager.addListener(listener);
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Scheduler removeListener(TaskListener listener) {
/* 188 */     this.listenerManager.removeListener(listener);
/* 189 */     return this;
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
/*     */   public Scheduler schedule(Setting cronSetting) {
/* 202 */     if (MapUtil.isNotEmpty((Map)cronSetting))
/*     */     {
/* 204 */       for (Map.Entry<String, LinkedHashMap<String, String>> groupedEntry : (Iterable<Map.Entry<String, LinkedHashMap<String, String>>>)cronSetting.getGroupedMap().entrySet()) {
/* 205 */         String group = groupedEntry.getKey();
/* 206 */         for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)((LinkedHashMap)groupedEntry.getValue()).entrySet()) {
/* 207 */           String jobClass = entry.getKey();
/* 208 */           if (StrUtil.isNotBlank(group)) {
/* 209 */             jobClass = group + '.' + jobClass;
/*     */           }
/* 211 */           String pattern = entry.getValue();
/* 212 */           StaticLog.debug("Load job: {} {}", new Object[] { pattern, jobClass });
/*     */           try {
/* 214 */             schedule(pattern, (Task)new InvokeTask(jobClass));
/* 215 */           } catch (Exception e) {
/* 216 */             throw new CronException(e, "Schedule [{}] [{}] error!", new Object[] { pattern, jobClass });
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/* 221 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String schedule(String pattern, Runnable task) {
/* 232 */     return schedule(pattern, (Task)new RunnableTask(task));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String schedule(String pattern, Task task) {
/* 243 */     String id = IdUtil.fastUUID();
/* 244 */     schedule(id, pattern, task);
/* 245 */     return id;
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
/*     */   public Scheduler schedule(String id, String pattern, Runnable task) {
/* 257 */     return schedule(id, new CronPattern(pattern), (Task)new RunnableTask(task));
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
/*     */   public Scheduler schedule(String id, String pattern, Task task) {
/* 269 */     return schedule(id, new CronPattern(pattern), task);
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
/*     */   public Scheduler schedule(String id, CronPattern pattern, Task task) {
/* 281 */     this.taskTable.add(id, pattern, task);
/* 282 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Scheduler deschedule(String id) {
/* 292 */     descheduleWithStatus(id);
/* 293 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean descheduleWithStatus(String id) {
/* 304 */     return this.taskTable.remove(id);
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
/*     */   public Scheduler updatePattern(String id, CronPattern pattern) {
/* 316 */     this.taskTable.updatePattern(id, pattern);
/* 317 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskTable getTaskTable() {
/* 327 */     return this.taskTable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CronPattern getPattern(String id) {
/* 338 */     return this.taskTable.getPattern(id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Task getTask(String id) {
/* 349 */     return this.taskTable.getTask(id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 359 */     return this.taskTable.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 369 */     return this.taskTable.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Scheduler clear() {
/* 378 */     this.taskTable = new TaskTable();
/* 379 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStarted() {
/* 387 */     return this.started;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Scheduler start(boolean isDaemon) {
/* 397 */     this.daemon = isDaemon;
/* 398 */     return start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Scheduler start() {
/* 407 */     this.lock.lock();
/*     */     try {
/* 409 */       checkStarted();
/*     */       
/* 411 */       if (null == this.threadExecutor)
/*     */       {
/* 413 */         this
/*     */           
/* 415 */           .threadExecutor = ExecutorBuilder.create().useSynchronousQueue().setThreadFactory(ThreadFactoryBuilder.create().setNamePrefix("hutool-cron-").setDaemon(this.daemon).build()).build();
/*     */       }
/* 417 */       this.taskLauncherManager = new TaskLauncherManager(this);
/* 418 */       this.taskExecutorManager = new TaskExecutorManager(this);
/*     */ 
/*     */       
/* 421 */       this.timer = new CronTimer(this);
/* 422 */       this.timer.setDaemon(this.daemon);
/* 423 */       this.timer.start();
/* 424 */       this.started = true;
/*     */     } finally {
/* 426 */       this.lock.unlock();
/*     */     } 
/* 428 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Scheduler stop() {
/* 439 */     return stop(false);
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
/*     */   public Scheduler stop(boolean clearTasks) {
/* 451 */     this.lock.lock();
/*     */     try {
/* 453 */       if (false == this.started) {
/* 454 */         throw new IllegalStateException("Scheduler not started !");
/*     */       }
/*     */ 
/*     */       
/* 458 */       this.timer.stopTimer();
/* 459 */       this.timer = null;
/*     */ 
/*     */       
/* 462 */       this.threadExecutor.shutdown();
/* 463 */       this.threadExecutor = null;
/*     */ 
/*     */       
/* 466 */       if (clearTasks) {
/* 467 */         clear();
/*     */       }
/*     */ 
/*     */       
/* 471 */       this.started = false;
/*     */     } finally {
/* 473 */       this.lock.unlock();
/*     */     } 
/* 475 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkStarted() throws CronException {
/* 484 */     if (this.started)
/* 485 */       throw new CronException("Scheduler already started!"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\Scheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */