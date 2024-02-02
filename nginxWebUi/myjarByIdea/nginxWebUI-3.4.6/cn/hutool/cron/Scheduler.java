package cn.hutool.cron;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.listener.TaskListener;
import cn.hutool.cron.listener.TaskListenerManager;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.InvokeTask;
import cn.hutool.cron.task.RunnableTask;
import cn.hutool.cron.task.Task;
import cn.hutool.log.StaticLog;
import cn.hutool.setting.Setting;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Lock lock = new ReentrantLock();
   protected CronConfig config = new CronConfig();
   private boolean started = false;
   protected boolean daemon;
   private CronTimer timer;
   protected TaskTable taskTable = new TaskTable();
   protected TaskLauncherManager taskLauncherManager;
   protected TaskExecutorManager taskExecutorManager;
   protected TaskListenerManager listenerManager = new TaskListenerManager();
   protected ExecutorService threadExecutor;

   public Scheduler setTimeZone(TimeZone timeZone) {
      this.config.setTimeZone(timeZone);
      return this;
   }

   public TimeZone getTimeZone() {
      return this.config.getTimeZone();
   }

   public Scheduler setDaemon(boolean on) throws CronException {
      this.lock.lock();

      try {
         this.checkStarted();
         this.daemon = on;
      } finally {
         this.lock.unlock();
      }

      return this;
   }

   public Scheduler setThreadExecutor(ExecutorService threadExecutor) throws CronException {
      this.lock.lock();

      try {
         this.checkStarted();
         this.threadExecutor = threadExecutor;
      } finally {
         this.lock.unlock();
      }

      return this;
   }

   public boolean isDaemon() {
      return this.daemon;
   }

   public boolean isMatchSecond() {
      return this.config.isMatchSecond();
   }

   public Scheduler setMatchSecond(boolean isMatchSecond) {
      this.config.setMatchSecond(isMatchSecond);
      return this;
   }

   public Scheduler addListener(TaskListener listener) {
      this.listenerManager.addListener(listener);
      return this;
   }

   public Scheduler removeListener(TaskListener listener) {
      this.listenerManager.removeListener(listener);
      return this;
   }

   public Scheduler schedule(Setting cronSetting) {
      if (MapUtil.isNotEmpty(cronSetting)) {
         Iterator var3 = cronSetting.getGroupedMap().entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<String, LinkedHashMap<String, String>> groupedEntry = (Map.Entry)var3.next();
            String group = (String)groupedEntry.getKey();
            Iterator var5 = ((LinkedHashMap)groupedEntry.getValue()).entrySet().iterator();

            while(var5.hasNext()) {
               Map.Entry<String, String> entry = (Map.Entry)var5.next();
               String jobClass = (String)entry.getKey();
               if (StrUtil.isNotBlank(group)) {
                  jobClass = group + '.' + jobClass;
               }

               String pattern = (String)entry.getValue();
               StaticLog.debug("Load job: {} {}", pattern, jobClass);

               try {
                  this.schedule(pattern, (Task)(new InvokeTask(jobClass)));
               } catch (Exception var10) {
                  throw new CronException(var10, "Schedule [{}] [{}] error!", new Object[]{pattern, jobClass});
               }
            }
         }
      }

      return this;
   }

   public String schedule(String pattern, Runnable task) {
      return this.schedule(pattern, (Task)(new RunnableTask(task)));
   }

   public String schedule(String pattern, Task task) {
      String id = IdUtil.fastUUID();
      this.schedule(id, pattern, task);
      return id;
   }

   public Scheduler schedule(String id, String pattern, Runnable task) {
      return this.schedule(id, (CronPattern)(new CronPattern(pattern)), (Task)(new RunnableTask(task)));
   }

   public Scheduler schedule(String id, String pattern, Task task) {
      return this.schedule(id, new CronPattern(pattern), task);
   }

   public Scheduler schedule(String id, CronPattern pattern, Task task) {
      this.taskTable.add(id, pattern, task);
      return this;
   }

   public Scheduler deschedule(String id) {
      this.descheduleWithStatus(id);
      return this;
   }

   public boolean descheduleWithStatus(String id) {
      return this.taskTable.remove(id);
   }

   public Scheduler updatePattern(String id, CronPattern pattern) {
      this.taskTable.updatePattern(id, pattern);
      return this;
   }

   public TaskTable getTaskTable() {
      return this.taskTable;
   }

   public CronPattern getPattern(String id) {
      return this.taskTable.getPattern(id);
   }

   public Task getTask(String id) {
      return this.taskTable.getTask(id);
   }

   public boolean isEmpty() {
      return this.taskTable.isEmpty();
   }

   public int size() {
      return this.taskTable.size();
   }

   public Scheduler clear() {
      this.taskTable = new TaskTable();
      return this;
   }

   public boolean isStarted() {
      return this.started;
   }

   public Scheduler start(boolean isDaemon) {
      this.daemon = isDaemon;
      return this.start();
   }

   public Scheduler start() {
      this.lock.lock();

      try {
         this.checkStarted();
         if (null == this.threadExecutor) {
            this.threadExecutor = ExecutorBuilder.create().useSynchronousQueue().setThreadFactory(ThreadFactoryBuilder.create().setNamePrefix("hutool-cron-").setDaemon(this.daemon).build()).build();
         }

         this.taskLauncherManager = new TaskLauncherManager(this);
         this.taskExecutorManager = new TaskExecutorManager(this);
         this.timer = new CronTimer(this);
         this.timer.setDaemon(this.daemon);
         this.timer.start();
         this.started = true;
      } finally {
         this.lock.unlock();
      }

      return this;
   }

   public Scheduler stop() {
      return this.stop(false);
   }

   public Scheduler stop(boolean clearTasks) {
      this.lock.lock();

      try {
         if (!this.started) {
            throw new IllegalStateException("Scheduler not started !");
         }

         this.timer.stopTimer();
         this.timer = null;
         this.threadExecutor.shutdown();
         this.threadExecutor = null;
         if (clearTasks) {
            this.clear();
         }

         this.started = false;
      } finally {
         this.lock.unlock();
      }

      return this;
   }

   private void checkStarted() throws CronException {
      if (this.started) {
         throw new CronException("Scheduler already started!");
      }
   }
}
