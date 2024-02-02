/*    */ package com.cym.ext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MonitorInfo
/*    */ {
/*    */   private String totalMemorySize;
/*    */   private String usedMemory;
/*    */   private String cpuRatio;
/*    */   private String memRatio;
/*    */   private Integer cpuCount;
/*    */   private Integer threadCount;
/*    */   
/*    */   public Integer getThreadCount() {
/* 39 */     return this.threadCount;
/*    */   }
/*    */   
/*    */   public void setThreadCount(Integer threadCount) {
/* 43 */     this.threadCount = threadCount;
/*    */   }
/*    */   
/*    */   public Integer getCpuCount() {
/* 47 */     return this.cpuCount;
/*    */   }
/*    */   
/*    */   public void setCpuCount(Integer cpuCount) {
/* 51 */     this.cpuCount = cpuCount;
/*    */   }
/*    */   
/*    */   public String getMemRatio() {
/* 55 */     return this.memRatio;
/*    */   }
/*    */   
/*    */   public void setMemRatio(String memRatio) {
/* 59 */     this.memRatio = memRatio;
/*    */   }
/*    */   
/*    */   public String getTotalMemorySize() {
/* 63 */     return this.totalMemorySize;
/*    */   }
/*    */   
/*    */   public void setTotalMemorySize(String totalMemorySize) {
/* 67 */     this.totalMemorySize = totalMemorySize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsedMemory() {
/* 79 */     return this.usedMemory;
/*    */   }
/*    */   
/*    */   public void setUsedMemory(String usedMemory) {
/* 83 */     this.usedMemory = usedMemory;
/*    */   }
/*    */   
/*    */   public String getCpuRatio() {
/* 87 */     return this.cpuRatio;
/*    */   }
/*    */   
/*    */   public void setCpuRatio(String cpuRatio) {
/* 91 */     this.cpuRatio = cpuRatio;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\ext\MonitorInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */