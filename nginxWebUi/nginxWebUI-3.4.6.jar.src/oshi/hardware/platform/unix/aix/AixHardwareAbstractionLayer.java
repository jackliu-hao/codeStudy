/*     */ package oshi.hardware.platform.unix.aix;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.aix.Lscfg;
/*     */ import oshi.driver.unix.aix.perfstat.PerfstatDisk;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.hardware.ComputerSystem;
/*     */ import oshi.hardware.Display;
/*     */ import oshi.hardware.GlobalMemory;
/*     */ import oshi.hardware.GraphicsCard;
/*     */ import oshi.hardware.HWDiskStore;
/*     */ import oshi.hardware.NetworkIF;
/*     */ import oshi.hardware.PowerSource;
/*     */ import oshi.hardware.Sensors;
/*     */ import oshi.hardware.SoundCard;
/*     */ import oshi.hardware.UsbDevice;
/*     */ import oshi.hardware.common.AbstractHardwareAbstractionLayer;
/*     */ import oshi.jna.platform.unix.aix.Perfstat;
/*     */ import oshi.util.Memoizer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class AixHardwareAbstractionLayer
/*     */   extends AbstractHardwareAbstractionLayer
/*     */ {
/*  56 */   private final Supplier<List<String>> lscfg = Memoizer.memoize(Lscfg::queryAllDevices, Memoizer.defaultExpiration());
/*     */   
/*  58 */   private final Supplier<Perfstat.perfstat_disk_t[]> diskStats = Memoizer.memoize(PerfstatDisk::queryDiskStats, Memoizer.defaultExpiration());
/*     */ 
/*     */   
/*     */   public ComputerSystem createComputerSystem() {
/*  62 */     return (ComputerSystem)new AixComputerSystem(this.lscfg);
/*     */   }
/*     */ 
/*     */   
/*     */   public GlobalMemory createMemory() {
/*  67 */     return (GlobalMemory)new AixGlobalMemory(this.lscfg);
/*     */   }
/*     */ 
/*     */   
/*     */   public CentralProcessor createProcessor() {
/*  72 */     return (CentralProcessor)new AixCentralProcessor();
/*     */   }
/*     */ 
/*     */   
/*     */   public Sensors createSensors() {
/*  77 */     return (Sensors)new AixSensors(this.lscfg);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PowerSource> getPowerSources() {
/*  82 */     return AixPowerSource.getPowerSources();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HWDiskStore> getDiskStores() {
/*  87 */     return AixHWDiskStore.getDisks(this.diskStats);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Display> getDisplays() {
/*  92 */     return AixDisplay.getDisplays();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
/*  97 */     return AixNetworkIF.getNetworks(includeLocalInterfaces);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<UsbDevice> getUsbDevices(boolean tree) {
/* 102 */     return AixUsbDevice.getUsbDevices(tree, this.lscfg);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<SoundCard> getSoundCards() {
/* 107 */     return AixSoundCard.getSoundCards(this.lscfg);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<GraphicsCard> getGraphicsCards() {
/* 112 */     return AixGraphicsCard.getGraphicsCards(this.lscfg);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixHardwareAbstractionLayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */