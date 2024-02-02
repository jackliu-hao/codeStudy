package ch.qos.logback.core.recovery;

public class RecoveryCoordinator {
   public static final long BACKOFF_COEFFICIENT_MIN = 20L;
   public static final long BACKOFF_MULTIPLIER = 4L;
   static long BACKOFF_COEFFICIENT_MAX = 327680L;
   private long backOffCoefficient = 20L;
   private static long UNSET = -1L;
   private long currentTime;
   private long next;

   public RecoveryCoordinator() {
      this.currentTime = UNSET;
      this.next = this.getCurrentTime() + this.getBackoffCoefficient();
   }

   public RecoveryCoordinator(long currentTime) {
      this.currentTime = UNSET;
      this.currentTime = currentTime;
      this.next = this.getCurrentTime() + this.getBackoffCoefficient();
   }

   public boolean isTooSoon() {
      long now = this.getCurrentTime();
      if (now > this.next) {
         this.next = now + this.getBackoffCoefficient();
         return false;
      } else {
         return true;
      }
   }

   void setCurrentTime(long forcedTime) {
      this.currentTime = forcedTime;
   }

   private long getCurrentTime() {
      return this.currentTime != UNSET ? this.currentTime : System.currentTimeMillis();
   }

   private long getBackoffCoefficient() {
      long currentCoeff = this.backOffCoefficient;
      if (this.backOffCoefficient < BACKOFF_COEFFICIENT_MAX) {
         this.backOffCoefficient *= 4L;
      }

      return currentCoeff;
   }
}
