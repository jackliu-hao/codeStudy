package com.cym.ext;

public class MonitorInfo {
   private String totalMemorySize;
   private String usedMemory;
   private String cpuRatio;
   private String memRatio;
   private Integer cpuCount;
   private Integer threadCount;

   public Integer getThreadCount() {
      return this.threadCount;
   }

   public void setThreadCount(Integer threadCount) {
      this.threadCount = threadCount;
   }

   public Integer getCpuCount() {
      return this.cpuCount;
   }

   public void setCpuCount(Integer cpuCount) {
      this.cpuCount = cpuCount;
   }

   public String getMemRatio() {
      return this.memRatio;
   }

   public void setMemRatio(String memRatio) {
      this.memRatio = memRatio;
   }

   public String getTotalMemorySize() {
      return this.totalMemorySize;
   }

   public void setTotalMemorySize(String totalMemorySize) {
      this.totalMemorySize = totalMemorySize;
   }

   public String getUsedMemory() {
      return this.usedMemory;
   }

   public void setUsedMemory(String usedMemory) {
      this.usedMemory = usedMemory;
   }

   public String getCpuRatio() {
      return this.cpuRatio;
   }

   public void setCpuRatio(String cpuRatio) {
      this.cpuRatio = cpuRatio;
   }
}
