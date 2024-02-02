package net.jodah.expiringmap;

import java.util.concurrent.TimeUnit;

public final class ExpiringValue<V> {
   private static final long UNSET_DURATION = -1L;
   private final V value;
   private final ExpirationPolicy expirationPolicy;
   private final long duration;
   private final TimeUnit timeUnit;

   public ExpiringValue(V value) {
      this(value, -1L, (TimeUnit)null, (ExpirationPolicy)null);
   }

   public ExpiringValue(V value, ExpirationPolicy expirationPolicy) {
      this(value, -1L, (TimeUnit)null, expirationPolicy);
   }

   public ExpiringValue(V value, long duration, TimeUnit timeUnit) {
      this(value, duration, timeUnit, (ExpirationPolicy)null);
      if (timeUnit == null) {
         throw new NullPointerException();
      }
   }

   public ExpiringValue(V value, ExpirationPolicy expirationPolicy, long duration, TimeUnit timeUnit) {
      this(value, duration, timeUnit, expirationPolicy);
      if (timeUnit == null) {
         throw new NullPointerException();
      }
   }

   private ExpiringValue(V value, long duration, TimeUnit timeUnit, ExpirationPolicy expirationPolicy) {
      this.value = value;
      this.expirationPolicy = expirationPolicy;
      this.duration = duration;
      this.timeUnit = timeUnit;
   }

   public V getValue() {
      return this.value;
   }

   public ExpirationPolicy getExpirationPolicy() {
      return this.expirationPolicy;
   }

   public long getDuration() {
      return this.duration;
   }

   public TimeUnit getTimeUnit() {
      return this.timeUnit;
   }

   public int hashCode() {
      return this.value != null ? this.value.hashCode() : 0;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         boolean var10000;
         label28: {
            ExpiringValue<?> that = (ExpiringValue)o;
            if (this.value != null) {
               if (!this.value.equals(that.value)) {
                  break label28;
               }
            } else if (that.value != null) {
               break label28;
            }

            if (this.expirationPolicy == that.expirationPolicy && this.duration == that.duration && this.timeUnit == that.timeUnit) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      } else {
         return false;
      }
   }

   public String toString() {
      return "ExpiringValue{value=" + this.value + ", expirationPolicy=" + this.expirationPolicy + ", duration=" + this.duration + ", timeUnit=" + this.timeUnit + '}';
   }
}
