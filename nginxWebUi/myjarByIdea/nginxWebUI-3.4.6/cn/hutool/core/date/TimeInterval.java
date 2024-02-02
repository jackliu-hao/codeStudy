package cn.hutool.core.date;

public class TimeInterval extends GroupTimeInterval {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_ID = "";

   public TimeInterval() {
      this(false);
   }

   public TimeInterval(boolean isNano) {
      super(isNano);
      this.start();
   }

   public long start() {
      return this.start("");
   }

   public long intervalRestart() {
      return this.intervalRestart("");
   }

   public TimeInterval restart() {
      this.start("");
      return this;
   }

   public long interval() {
      return this.interval("");
   }

   public String intervalPretty() {
      return this.intervalPretty("");
   }

   public long intervalMs() {
      return this.intervalMs("");
   }

   public long intervalSecond() {
      return this.intervalSecond("");
   }

   public long intervalMinute() {
      return this.intervalMinute("");
   }

   public long intervalHour() {
      return this.intervalHour("");
   }

   public long intervalDay() {
      return this.intervalDay("");
   }

   public long intervalWeek() {
      return this.intervalWeek("");
   }
}
