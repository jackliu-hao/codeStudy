package cn.hutool.core.date;

import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupTimeInterval implements Serializable {
   private static final long serialVersionUID = 1L;
   private final boolean isNano;
   protected final Map<String, Long> groupMap;

   public GroupTimeInterval(boolean isNano) {
      this.isNano = isNano;
      this.groupMap = new ConcurrentHashMap();
   }

   public GroupTimeInterval clear() {
      this.groupMap.clear();
      return this;
   }

   public long start(String id) {
      long time = this.getTime();
      this.groupMap.put(id, time);
      return time;
   }

   public long intervalRestart(String id) {
      long now = this.getTime();
      return now - (Long)ObjectUtil.defaultIfNull(this.groupMap.put(id, now), (Object)now);
   }

   public long interval(String id) {
      Long lastTime = (Long)this.groupMap.get(id);
      return null == lastTime ? 0L : this.getTime() - lastTime;
   }

   public long interval(String id, DateUnit dateUnit) {
      long intervalMs = this.isNano ? this.interval(id) / 1000000L : this.interval(id);
      return DateUnit.MS == dateUnit ? intervalMs : intervalMs / dateUnit.getMillis();
   }

   public long intervalMs(String id) {
      return this.interval(id, DateUnit.MS);
   }

   public long intervalSecond(String id) {
      return this.interval(id, DateUnit.SECOND);
   }

   public long intervalMinute(String id) {
      return this.interval(id, DateUnit.MINUTE);
   }

   public long intervalHour(String id) {
      return this.interval(id, DateUnit.HOUR);
   }

   public long intervalDay(String id) {
      return this.interval(id, DateUnit.DAY);
   }

   public long intervalWeek(String id) {
      return this.interval(id, DateUnit.WEEK);
   }

   public String intervalPretty(String id) {
      return DateUtil.formatBetween(this.intervalMs(id));
   }

   private long getTime() {
      return this.isNano ? System.nanoTime() : System.currentTimeMillis();
   }
}
