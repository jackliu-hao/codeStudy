/*     */ package cn.hutool.system.oshi;
/*     */ 
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import java.text.DecimalFormat;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CpuInfo
/*     */ {
/*  15 */   private static final DecimalFormat LOAD_FORMAT = new DecimalFormat("#.00");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Integer cpuNum;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double toTal;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double sys;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double user;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double wait;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double free;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String cpuModel;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CpuTicks ticks;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpuInfo() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpuInfo(CentralProcessor processor, long waitingTime) {
/*  70 */     init(processor, waitingTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CpuInfo(Integer cpuNum, double toTal, double sys, double user, double wait, double free, String cpuModel) {
/*  85 */     this.cpuNum = cpuNum;
/*  86 */     this.toTal = toTal;
/*  87 */     this.sys = sys;
/*  88 */     this.user = user;
/*  89 */     this.wait = wait;
/*  90 */     this.free = free;
/*  91 */     this.cpuModel = cpuModel;
/*     */   }
/*     */   
/*     */   public Integer getCpuNum() {
/*  95 */     return this.cpuNum;
/*     */   }
/*     */   
/*     */   public void setCpuNum(Integer cpuNum) {
/*  99 */     this.cpuNum = cpuNum;
/*     */   }
/*     */   
/*     */   public double getToTal() {
/* 103 */     return this.toTal;
/*     */   }
/*     */   
/*     */   public void setToTal(double toTal) {
/* 107 */     this.toTal = toTal;
/*     */   }
/*     */   
/*     */   public double getSys() {
/* 111 */     return this.sys;
/*     */   }
/*     */   
/*     */   public void setSys(double sys) {
/* 115 */     this.sys = sys;
/*     */   }
/*     */   
/*     */   public double getUser() {
/* 119 */     return this.user;
/*     */   }
/*     */   
/*     */   public void setUser(double user) {
/* 123 */     this.user = user;
/*     */   }
/*     */   
/*     */   public double getWait() {
/* 127 */     return this.wait;
/*     */   }
/*     */   
/*     */   public void setWait(double wait) {
/* 131 */     this.wait = wait;
/*     */   }
/*     */   
/*     */   public double getFree() {
/* 135 */     return this.free;
/*     */   }
/*     */   
/*     */   public void setFree(double free) {
/* 139 */     this.free = free;
/*     */   }
/*     */   
/*     */   public String getCpuModel() {
/* 143 */     return this.cpuModel;
/*     */   }
/*     */   
/*     */   public void setCpuModel(String cpuModel) {
/* 147 */     this.cpuModel = cpuModel;
/*     */   }
/*     */   
/*     */   public CpuTicks getTicks() {
/* 151 */     return this.ticks;
/*     */   }
/*     */   
/*     */   public void setTicks(CpuTicks ticks) {
/* 155 */     this.ticks = ticks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getUsed() {
/* 164 */     return NumberUtil.sub(100.0F, this.free);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 169 */     return "CpuInfo{CPU核心数=" + this.cpuNum + ", CPU总的使用率=" + this.toTal + ", CPU系统使用率=" + this.sys + ", CPU用户使用率=" + this.user + ", CPU当前等待率=" + this.wait + ", CPU当前空闲率=" + this.free + ", CPU利用率=" + 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 176 */       getUsed() + ", CPU型号信息='" + this.cpuModel + '\'' + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(CentralProcessor processor, long waitingTime) {
/* 189 */     CpuTicks ticks = new CpuTicks(processor, waitingTime);
/* 190 */     this.ticks = ticks;
/*     */     
/* 192 */     this.cpuNum = Integer.valueOf(processor.getLogicalProcessorCount());
/* 193 */     this.cpuModel = processor.toString();
/*     */     
/* 195 */     long totalCpu = ticks.totalCpu();
/* 196 */     this.toTal = totalCpu;
/* 197 */     this.sys = formatDouble(ticks.cSys, totalCpu);
/* 198 */     this.user = formatDouble(ticks.user, totalCpu);
/* 199 */     this.wait = formatDouble(ticks.ioWait, totalCpu);
/* 200 */     this.free = formatDouble(ticks.idle, totalCpu);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static double formatDouble(long tick, long totalCpu) {
/* 212 */     if (0L == totalCpu) {
/* 213 */       return 0.0D;
/*     */     }
/* 215 */     return Double.parseDouble(LOAD_FORMAT.format((tick <= 0L) ? 0.0D : (100.0D * tick / totalCpu)));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\oshi\CpuInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */