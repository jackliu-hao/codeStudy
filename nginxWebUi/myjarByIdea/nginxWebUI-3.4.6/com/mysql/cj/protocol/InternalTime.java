package com.mysql.cj.protocol;

import com.mysql.cj.util.TimeUtil;

public class InternalTime {
   private boolean negative = false;
   private int hours = 0;
   private int minutes = 0;
   private int seconds = 0;
   private int nanos = 0;
   private int scale = 0;

   public InternalTime() {
   }

   public InternalTime(int hours, int minutes, int seconds, int nanos, int scale) {
      this.hours = hours;
      this.minutes = minutes;
      this.seconds = seconds;
      this.nanos = nanos;
      this.scale = scale;
   }

   public boolean isNegative() {
      return this.negative;
   }

   public void setNegative(boolean negative) {
      this.negative = negative;
   }

   public int getHours() {
      return this.hours;
   }

   public void setHours(int hours) {
      this.hours = hours;
   }

   public int getMinutes() {
      return this.minutes;
   }

   public void setMinutes(int minutes) {
      this.minutes = minutes;
   }

   public int getSeconds() {
      return this.seconds;
   }

   public void setSeconds(int seconds) {
      this.seconds = seconds;
   }

   public int getNanos() {
      return this.nanos;
   }

   public void setNanos(int nanos) {
      this.nanos = nanos;
   }

   public boolean isZero() {
      return this.hours == 0 && this.minutes == 0 && this.seconds == 0 && this.nanos == 0;
   }

   public int getScale() {
      return this.scale;
   }

   public void setScale(int scale) {
      this.scale = scale;
   }

   public String toString() {
      return this.nanos > 0 ? String.format("%02d:%02d:%02d.%s", this.hours, this.minutes, this.seconds, TimeUtil.formatNanos(this.nanos, this.scale, false)) : String.format("%02d:%02d:%02d", this.hours, this.minutes, this.seconds);
   }
}
