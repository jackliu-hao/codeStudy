/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ 
/*     */ 
/*     */ public class StopWatch
/*     */ {
/*     */   private final String id;
/*     */   private List<TaskInfo> taskList;
/*     */   private String currentTaskName;
/*     */   private long startTimeNanos;
/*     */   private TaskInfo lastTaskInfo;
/*     */   private int taskCount;
/*     */   private long totalTimeNanos;
/*     */   
/*     */   public static StopWatch create(String id) {
/*  53 */     return new StopWatch(id);
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
/*     */   public StopWatch() {
/*  89 */     this("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StopWatch(String id) {
/*  98 */     this(id, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StopWatch(String id, boolean keepTaskList) {
/* 108 */     this.id = id;
/* 109 */     if (keepTaskList) {
/* 110 */       this.taskList = new ArrayList<>();
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
/*     */   public String getId() {
/* 122 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKeepTaskList(boolean keepTaskList) {
/* 131 */     if (keepTaskList) {
/* 132 */       if (null == this.taskList) {
/* 133 */         this.taskList = new ArrayList<>();
/*     */       }
/*     */     } else {
/* 136 */       this.taskList = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() throws IllegalStateException {
/* 146 */     start("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start(String taskName) throws IllegalStateException {
/* 156 */     if (null != this.currentTaskName) {
/* 157 */       throw new IllegalStateException("Can't start StopWatch: it's already running");
/*     */     }
/* 159 */     this.currentTaskName = taskName;
/* 160 */     this.startTimeNanos = System.nanoTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() throws IllegalStateException {
/* 169 */     if (null == this.currentTaskName) {
/* 170 */       throw new IllegalStateException("Can't stop StopWatch: it's not running");
/*     */     }
/*     */     
/* 173 */     long lastTime = System.nanoTime() - this.startTimeNanos;
/* 174 */     this.totalTimeNanos += lastTime;
/* 175 */     this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
/* 176 */     if (null != this.taskList) {
/* 177 */       this.taskList.add(this.lastTaskInfo);
/*     */     }
/* 179 */     this.taskCount++;
/* 180 */     this.currentTaskName = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRunning() {
/* 190 */     return (this.currentTaskName != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String currentTaskName() {
/* 200 */     return this.currentTaskName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLastTaskTimeNanos() throws IllegalStateException {
/* 210 */     if (this.lastTaskInfo == null) {
/* 211 */       throw new IllegalStateException("No tasks run: can't get last task interval");
/*     */     }
/* 213 */     return this.lastTaskInfo.getTimeNanos();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLastTaskTimeMillis() throws IllegalStateException {
/* 223 */     if (this.lastTaskInfo == null) {
/* 224 */       throw new IllegalStateException("No tasks run: can't get last task interval");
/*     */     }
/* 226 */     return this.lastTaskInfo.getTimeMillis();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLastTaskName() throws IllegalStateException {
/* 236 */     if (this.lastTaskInfo == null) {
/* 237 */       throw new IllegalStateException("No tasks run: can't get last task name");
/*     */     }
/* 239 */     return this.lastTaskInfo.getTaskName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskInfo getLastTaskInfo() throws IllegalStateException {
/* 249 */     if (this.lastTaskInfo == null) {
/* 250 */       throw new IllegalStateException("No tasks run: can't get last task info");
/*     */     }
/* 252 */     return this.lastTaskInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotal(TimeUnit unit) {
/* 263 */     return unit.convert(this.totalTimeNanos, TimeUnit.NANOSECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalTimeNanos() {
/* 274 */     return this.totalTimeNanos;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTotalTimeMillis() {
/* 285 */     return getTotal(TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getTotalTimeSeconds() {
/* 296 */     return DateUtil.nanosToSeconds(this.totalTimeNanos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTaskCount() {
/* 305 */     return this.taskCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TaskInfo[] getTaskInfo() {
/* 314 */     if (null == this.taskList) {
/* 315 */       throw new UnsupportedOperationException("Task info is not being kept!");
/*     */     }
/* 317 */     return this.taskList.<TaskInfo>toArray(new TaskInfo[0]);
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
/*     */   public String shortSummary() {
/* 329 */     return shortSummary(null);
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
/*     */   public String shortSummary(TimeUnit unit) {
/* 342 */     if (null == unit) {
/* 343 */       unit = TimeUnit.NANOSECONDS;
/*     */     }
/* 345 */     return StrUtil.format("StopWatch '{}': running time = {} {}", new Object[] { this.id, 
/* 346 */           Long.valueOf(getTotal(unit)), DateUtil.getShotName(unit) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String prettyPrint() {
/* 355 */     return prettyPrint(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String prettyPrint(TimeUnit unit) {
/* 366 */     if (null == unit) {
/* 367 */       unit = TimeUnit.NANOSECONDS;
/*     */     }
/*     */     
/* 370 */     StringBuilder sb = new StringBuilder(shortSummary(unit));
/* 371 */     sb.append(FileUtil.getLineSeparator());
/* 372 */     if (null == this.taskList) {
/* 373 */       sb.append("No task info kept");
/*     */     } else {
/* 375 */       sb.append("---------------------------------------------").append(FileUtil.getLineSeparator());
/* 376 */       sb.append(DateUtil.getShotName(unit)).append("         %     Task name").append(FileUtil.getLineSeparator());
/* 377 */       sb.append("---------------------------------------------").append(FileUtil.getLineSeparator());
/*     */       
/* 379 */       NumberFormat nf = NumberFormat.getNumberInstance();
/* 380 */       nf.setMinimumIntegerDigits(9);
/* 381 */       nf.setGroupingUsed(false);
/*     */       
/* 383 */       NumberFormat pf = NumberFormat.getPercentInstance();
/* 384 */       pf.setMinimumIntegerDigits(2);
/* 385 */       pf.setGroupingUsed(false);
/*     */       
/* 387 */       for (TaskInfo task : getTaskInfo()) {
/* 388 */         sb.append(nf.format(task.getTime(unit))).append("  ");
/* 389 */         sb.append(pf.format(task.getTimeNanos() / getTotalTimeNanos())).append("   ");
/* 390 */         sb.append(task.getTaskName()).append(FileUtil.getLineSeparator());
/*     */       } 
/*     */     } 
/* 393 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 398 */     StringBuilder sb = new StringBuilder(shortSummary());
/* 399 */     if (null != this.taskList) {
/* 400 */       for (TaskInfo task : this.taskList) {
/* 401 */         sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeNanos()).append(" ns");
/* 402 */         long percent = Math.round(100.0D * task.getTimeNanos() / getTotalTimeNanos());
/* 403 */         sb.append(" = ").append(percent).append("%");
/*     */       } 
/*     */     } else {
/* 406 */       sb.append("; no task info kept");
/*     */     } 
/* 408 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class TaskInfo
/*     */   {
/*     */     private final String taskName;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final long timeNanos;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     TaskInfo(String taskName, long timeNanos) {
/* 428 */       this.taskName = taskName;
/* 429 */       this.timeNanos = timeNanos;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getTaskName() {
/* 438 */       return this.taskName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getTime(TimeUnit unit) {
/* 449 */       return unit.convert(this.timeNanos, TimeUnit.NANOSECONDS);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getTimeNanos() {
/* 460 */       return this.timeNanos;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getTimeMillis() {
/* 471 */       return getTime(TimeUnit.MILLISECONDS);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double getTimeSeconds() {
/* 482 */       return DateUtil.nanosToSeconds(this.timeNanos);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\StopWatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */