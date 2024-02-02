package com.cym.sqlhelper.utils;

public class SnowFlake {
   private static final long START_STAMP = 1480166465631L;
   private static final long SEQUENCE_BIT = 12L;
   private static final long MACHINE_BIT = 5L;
   private static final long DATA_CENTER_BIT = 5L;
   private static final long MAX_DATA_CENTER_NUM = 31L;
   private static final long MAX_MACHINE_NUM = 31L;
   private static final long MAX_SEQUENCE = 4095L;
   private static final long MACHINE_LEFT = 12L;
   private static final long DATA_CENTER_LEFT = 17L;
   private static final long TIMESTAMP_LEFT = 22L;
   private long dataCenterId;
   private long machineId;
   private long sequence = 0L;
   private long lastStamp = -1L;

   public SnowFlake(long dataCenterId, long machineId) {
      if (dataCenterId <= 31L && dataCenterId >= 0L) {
         if (machineId <= 31L && machineId >= 0L) {
            this.dataCenterId = dataCenterId;
            this.machineId = machineId;
         } else {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
         }
      } else {
         throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
      }
   }

   public synchronized String nextId() {
      long currStamp = this.getNewStamp();
      if (currStamp < this.lastStamp) {
         throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
      } else {
         if (currStamp == this.lastStamp) {
            this.sequence = this.sequence + 1L & 4095L;
            if (this.sequence == 0L) {
               currStamp = this.getNextMill();
            }
         } else {
            this.sequence = 0L;
         }

         this.lastStamp = currStamp;
         long id = currStamp - 1480166465631L << 22 | this.dataCenterId << 17 | this.machineId << 12 | this.sequence;
         return String.valueOf(id);
      }
   }

   private long getNextMill() {
      long mill;
      for(mill = this.getNewStamp(); mill <= this.lastStamp; mill = this.getNewStamp()) {
      }

      return mill;
   }

   private long getNewStamp() {
      return System.currentTimeMillis();
   }
}
