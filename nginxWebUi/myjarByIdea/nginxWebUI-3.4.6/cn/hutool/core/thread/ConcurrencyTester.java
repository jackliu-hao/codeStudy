package cn.hutool.core.thread;

import cn.hutool.core.date.TimeInterval;
import java.io.Closeable;
import java.io.IOException;

public class ConcurrencyTester implements Closeable {
   private final SyncFinisher sf;
   private final TimeInterval timeInterval;
   private long interval;

   public ConcurrencyTester(int threadSize) {
      this.sf = new SyncFinisher(threadSize);
      this.timeInterval = new TimeInterval();
   }

   public ConcurrencyTester test(Runnable runnable) {
      this.sf.clearWorker();
      this.timeInterval.start();
      this.sf.addRepeatWorker(runnable).setBeginAtSameTime(true).start();
      this.interval = this.timeInterval.interval();
      return this;
   }

   public ConcurrencyTester reset() {
      this.sf.clearWorker();
      this.timeInterval.restart();
      return this;
   }

   public long getInterval() {
      return this.interval;
   }

   public void close() throws IOException {
      this.sf.close();
   }
}
