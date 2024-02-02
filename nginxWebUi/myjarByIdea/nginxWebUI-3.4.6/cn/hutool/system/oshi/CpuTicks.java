package cn.hutool.system.oshi;

import oshi.hardware.CentralProcessor;
import oshi.util.Util;

public class CpuTicks {
   long idle;
   long nice;
   long irq;
   long softIrq;
   long steal;
   long cSys;
   long user;
   long ioWait;

   public CpuTicks(CentralProcessor processor, long waitingTime) {
      long[] prevTicks = processor.getSystemCpuLoadTicks();
      Util.sleep(waitingTime);
      long[] ticks = processor.getSystemCpuLoadTicks();
      this.idle = tick(prevTicks, ticks, CentralProcessor.TickType.IDLE);
      this.nice = tick(prevTicks, ticks, CentralProcessor.TickType.NICE);
      this.irq = tick(prevTicks, ticks, CentralProcessor.TickType.IRQ);
      this.softIrq = tick(prevTicks, ticks, CentralProcessor.TickType.SOFTIRQ);
      this.steal = tick(prevTicks, ticks, CentralProcessor.TickType.STEAL);
      this.cSys = tick(prevTicks, ticks, CentralProcessor.TickType.SYSTEM);
      this.user = tick(prevTicks, ticks, CentralProcessor.TickType.USER);
      this.ioWait = tick(prevTicks, ticks, CentralProcessor.TickType.IOWAIT);
   }

   public long getIdle() {
      return this.idle;
   }

   public void setIdle(long idle) {
      this.idle = idle;
   }

   public long getNice() {
      return this.nice;
   }

   public void setNice(long nice) {
      this.nice = nice;
   }

   public long getIrq() {
      return this.irq;
   }

   public void setIrq(long irq) {
      this.irq = irq;
   }

   public long getSoftIrq() {
      return this.softIrq;
   }

   public void setSoftIrq(long softIrq) {
      this.softIrq = softIrq;
   }

   public long getSteal() {
      return this.steal;
   }

   public void setSteal(long steal) {
      this.steal = steal;
   }

   public long getcSys() {
      return this.cSys;
   }

   public void setcSys(long cSys) {
      this.cSys = cSys;
   }

   public long getUser() {
      return this.user;
   }

   public void setUser(long user) {
      this.user = user;
   }

   public long getIoWait() {
      return this.ioWait;
   }

   public void setIoWait(long ioWait) {
      this.ioWait = ioWait;
   }

   public long totalCpu() {
      return Math.max(this.user + this.nice + this.cSys + this.idle + this.ioWait + this.irq + this.softIrq + this.steal, 0L);
   }

   public String toString() {
      return "CpuTicks{idle=" + this.idle + ", nice=" + this.nice + ", irq=" + this.irq + ", softIrq=" + this.softIrq + ", steal=" + this.steal + ", cSys=" + this.cSys + ", user=" + this.user + ", ioWait=" + this.ioWait + '}';
   }

   private static long tick(long[] prevTicks, long[] ticks, CentralProcessor.TickType tickType) {
      return ticks[tickType.getIndex()] - prevTicks[tickType.getIndex()];
   }
}
