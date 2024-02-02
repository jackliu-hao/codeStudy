package cn.hutool.cron.timingwheel;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class TimerTaskList implements Delayed {
   private final AtomicLong expire = new AtomicLong(-1L);
   private final TimerTask root = new TimerTask((Runnable)null, -1L);

   public TimerTaskList() {
      this.root.prev = this.root;
      this.root.next = this.root;
   }

   public boolean setExpiration(long expire) {
      return this.expire.getAndSet(expire) != expire;
   }

   public long getExpire() {
      return this.expire.get();
   }

   public void addTask(TimerTask timerTask) {
      synchronized(this) {
         if (timerTask.timerTaskList == null) {
            timerTask.timerTaskList = this;
            TimerTask tail = this.root.prev;
            timerTask.next = this.root;
            timerTask.prev = tail;
            tail.next = timerTask;
            this.root.prev = timerTask;
         }

      }
   }

   public void removeTask(TimerTask timerTask) {
      synchronized(this) {
         if (this.equals(timerTask.timerTaskList)) {
            timerTask.next.prev = timerTask.prev;
            timerTask.prev.next = timerTask.next;
            timerTask.timerTaskList = null;
            timerTask.next = null;
            timerTask.prev = null;
         }

      }
   }

   public synchronized void flush(Consumer<TimerTask> flush) {
      for(TimerTask timerTask = this.root.next; !timerTask.equals(this.root); timerTask = this.root.next) {
         this.removeTask(timerTask);
         flush.accept(timerTask);
      }

      this.expire.set(-1L);
   }

   public long getDelay(TimeUnit unit) {
      return Math.max(0L, unit.convert(this.expire.get() - System.currentTimeMillis(), TimeUnit.MILLISECONDS));
   }

   public int compareTo(Delayed o) {
      return o instanceof TimerTaskList ? Long.compare(this.expire.get(), ((TimerTaskList)o).expire.get()) : 0;
   }
}
