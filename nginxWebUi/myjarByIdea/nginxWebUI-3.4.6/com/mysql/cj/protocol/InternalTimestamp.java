package com.mysql.cj.protocol;

public class InternalTimestamp extends InternalDate {
   private int hours = 0;
   private int minutes = 0;
   private int seconds = 0;
   private int nanos = 0;
   private int scale = 0;
   private int offset = 0;

   public InternalTimestamp() {
   }

   public InternalTimestamp(int year, int month, int day, int hours, int minutes, int seconds, int nanos, int scale) {
      this.year = year;
      this.month = month;
      this.day = day;
      this.hours = hours;
      this.minutes = minutes;
      this.seconds = seconds;
      this.nanos = nanos;
      this.scale = scale;
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

   public int getScale() {
      return this.scale;
   }

   public void setScale(int scale) {
      this.scale = scale;
   }

   public int getOffset() {
      return this.offset;
   }

   public void setOffset(int offset) {
      this.offset = offset;
   }

   public boolean isZero() {
      return super.isZero() && this.hours == 0 && this.minutes == 0 && this.seconds == 0 && this.nanos == 0;
   }
}
