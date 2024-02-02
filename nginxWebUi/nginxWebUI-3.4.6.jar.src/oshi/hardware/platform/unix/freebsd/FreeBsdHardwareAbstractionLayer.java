/*     */ package oshi.hardware.platform.unix.freebsd;
/*     */ 
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ public final class FreeBsdHardwareAbstractionLayer
/*     */   extends AbstractHardwareAbstractionLayer
/*     */ {
/*     */   public ComputerSystem createComputerSystem() {
/*  50 */     return (ComputerSystem)new FreeBsdComputerSystem();
/*     */   }
/*     */ 
/*     */   
/*     */   public GlobalMemory createMemory() {
/*  55 */     return (GlobalMemory)new FreeBsdGlobalMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   public CentralProcessor createProcessor() {
/*  60 */     return (CentralProcessor)new FreeBsdCentralProcessor();
/*     */   }
/*     */ 
/*     */   
/*     */   public Sensors createSensors() {
/*  65 */     return (Sensors)new FreeBsdSensors();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PowerSource> getPowerSources() {
/*  70 */     return FreeBsdPowerSource.getPowerSources();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<HWDiskStore> getDiskStores() {
/*  75 */     return FreeBsdHWDiskStore.getDisks();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Display> getDisplays() {
/*  80 */     return FreeBsdDisplay.getDisplays();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
/*  85 */     return FreeBsdNetworkIF.getNetworks(includeLocalInterfaces);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<UsbDevice> getUsbDevices(boolean tree) {
/*  90 */     return FreeBsdUsbDevice.getUsbDevices(tree);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<SoundCard> getSoundCards() {
/*  95 */     return FreeBsdSoundCard.getSoundCards();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<GraphicsCard> getGraphicsCards() {
/* 100 */     return FreeBsdGraphicsCard.getGraphicsCards();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdHardwareAbstractionLayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */