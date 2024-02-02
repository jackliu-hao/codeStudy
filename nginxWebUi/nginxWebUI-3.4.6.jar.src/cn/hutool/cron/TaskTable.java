/*     */ package cn.hutool.cron;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.cron.pattern.CronPattern;
/*     */ import cn.hutool.cron.task.CronTask;
/*     */ import cn.hutool.cron.task.Task;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
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
/*     */ public class TaskTable
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int DEFAULT_CAPACITY = 10;
/*     */   private final ReadWriteLock lock;
/*     */   private final List<String> ids;
/*     */   private final List<CronPattern> patterns;
/*     */   private final List<Task> tasks;
/*     */   private int size;
/*     */   
/*     */   public TaskTable() {
/*  39 */     this(10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskTable(int initialCapacity) {
/*  48 */     this.lock = new ReentrantReadWriteLock();
/*     */     
/*  50 */     this.ids = new ArrayList<>(initialCapacity);
/*  51 */     this.patterns = new ArrayList<>(initialCapacity);
/*  52 */     this.tasks = new ArrayList<>(initialCapacity);
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
/*     */   public TaskTable add(String id, CronPattern pattern, Task task) {
/*  64 */     Lock writeLock = this.lock.writeLock();
/*  65 */     writeLock.lock();
/*     */     try {
/*  67 */       if (this.ids.contains(id)) {
/*  68 */         throw new CronException("Id [{}] has been existed!", new Object[] { id });
/*     */       }
/*  70 */       this.ids.add(id);
/*  71 */       this.patterns.add(pattern);
/*  72 */       this.tasks.add(task);
/*  73 */       this.size++;
/*     */     } finally {
/*  75 */       writeLock.unlock();
/*     */     } 
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getIds() {
/*  87 */     Lock readLock = this.lock.readLock();
/*  88 */     readLock.lock();
/*     */     try {
/*  90 */       return Collections.unmodifiableList(this.ids);
/*     */     } finally {
/*  92 */       readLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CronPattern> getPatterns() {
/* 103 */     Lock readLock = this.lock.readLock();
/* 104 */     readLock.lock();
/*     */     try {
/* 106 */       return Collections.unmodifiableList(this.patterns);
/*     */     } finally {
/* 108 */       readLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Task> getTasks() {
/* 119 */     Lock readLock = this.lock.readLock();
/* 120 */     readLock.lock();
/*     */     try {
/* 122 */       return Collections.unmodifiableList(this.tasks);
/*     */     } finally {
/* 124 */       readLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean remove(String id) {
/* 135 */     Lock writeLock = this.lock.writeLock();
/* 136 */     writeLock.lock();
/*     */     try {
/* 138 */       int index = this.ids.indexOf(id);
/* 139 */       if (index < 0) {
/* 140 */         return false;
/*     */       }
/* 142 */       this.tasks.remove(index);
/* 143 */       this.patterns.remove(index);
/* 144 */       this.ids.remove(index);
/* 145 */       this.size--;
/*     */     } finally {
/* 147 */       writeLock.unlock();
/*     */     } 
/* 149 */     return true;
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
/*     */   public boolean updatePattern(String id, CronPattern pattern) {
/* 161 */     Lock writeLock = this.lock.writeLock();
/* 162 */     writeLock.lock();
/*     */     try {
/* 164 */       int index = this.ids.indexOf(id);
/* 165 */       if (index > -1) {
/* 166 */         this.patterns.set(index, pattern);
/* 167 */         return true;
/*     */       } 
/*     */     } finally {
/* 170 */       writeLock.unlock();
/*     */     } 
/* 172 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Task getTask(int index) {
/* 183 */     Lock readLock = this.lock.readLock();
/* 184 */     readLock.lock();
/*     */     try {
/* 186 */       return this.tasks.get(index);
/*     */     } finally {
/* 188 */       readLock.unlock();
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
/*     */   public Task getTask(String id) {
/* 200 */     int index = this.ids.indexOf(id);
/* 201 */     if (index > -1) {
/* 202 */       return getTask(index);
/*     */     }
/* 204 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CronPattern getPattern(int index) {
/* 215 */     Lock readLock = this.lock.readLock();
/* 216 */     readLock.lock();
/*     */     try {
/* 218 */       return this.patterns.get(index);
/*     */     } finally {
/* 220 */       readLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 231 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 241 */     return (this.size < 1);
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
/* 252 */     int index = this.ids.indexOf(id);
/* 253 */     if (index > -1) {
/* 254 */       return getPattern(index);
/*     */     }
/* 256 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void executeTaskIfMatch(Scheduler scheduler, long millis) {
/* 266 */     Lock readLock = this.lock.readLock();
/* 267 */     readLock.lock();
/*     */     try {
/* 269 */       executeTaskIfMatchInternal(scheduler, millis);
/*     */     } finally {
/* 271 */       readLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 277 */     StringBuilder builder = StrUtil.builder();
/* 278 */     for (int i = 0; i < this.size; i++) {
/* 279 */       builder.append(StrUtil.format("[{}] [{}] [{}]\n", new Object[] { this.ids
/* 280 */               .get(i), this.patterns.get(i), this.tasks.get(i) }));
/*     */     } 
/* 282 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void executeTaskIfMatchInternal(Scheduler scheduler, long millis) {
/* 293 */     for (int i = 0; i < this.size; i++) {
/* 294 */       if (((CronPattern)this.patterns.get(i)).match(scheduler.config.timezone, millis, scheduler.config.matchSecond))
/* 295 */         scheduler.taskExecutorManager.spawnExecutor(new CronTask(this.ids.get(i), this.patterns.get(i), this.tasks.get(i))); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\TaskTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */