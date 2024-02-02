/*     */ package cn.hutool.system.oshi;
/*     */ 
/*     */ import java.util.List;
/*     */ import oshi.SystemInfo;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.hardware.ComputerSystem;
/*     */ import oshi.hardware.GlobalMemory;
/*     */ import oshi.hardware.HWDiskStore;
/*     */ import oshi.hardware.HardwareAbstractionLayer;
/*     */ import oshi.hardware.NetworkIF;
/*     */ import oshi.hardware.Sensors;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OperatingSystem;
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
/*     */ 
/*     */ 
/*     */ public class OshiUtil
/*     */ {
/*  42 */   private static final SystemInfo systemInfo = new SystemInfo();
/*  43 */   private static final HardwareAbstractionLayer hardware = systemInfo.getHardware();
/*  44 */   private static final OperatingSystem os = systemInfo.getOperatingSystem();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static OperatingSystem getOs() {
/*  53 */     return os;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static OSProcess getCurrentProcess() {
/*  63 */     return os.getProcess(os.getProcessId());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HardwareAbstractionLayer getHardware() {
/*  72 */     return hardware;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ComputerSystem getSystem() {
/*  81 */     return hardware.getComputerSystem();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static GlobalMemory getMemory() {
/*  90 */     return hardware.getMemory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CentralProcessor getProcessor() {
/*  99 */     return hardware.getProcessor();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Sensors getSensors() {
/* 108 */     return hardware.getSensors();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<HWDiskStore> getDiskStores() {
/* 118 */     return hardware.getDiskStores();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<NetworkIF> getNetworkIFs() {
/* 128 */     return hardware.getNetworkIFs();
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
/*     */   public static CpuInfo getCpuInfo() {
/* 140 */     return getCpuInfo(1000L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CpuInfo getCpuInfo(long waitingTime) {
/* 150 */     return new CpuInfo(getProcessor(), waitingTime);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\oshi\OshiUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */