/*     */ package oshi;
/*     */ 
/*     */ import com.sun.jna.Platform;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.hardware.HardwareAbstractionLayer;
/*     */ import oshi.hardware.platform.linux.LinuxHardwareAbstractionLayer;
/*     */ import oshi.hardware.platform.mac.MacHardwareAbstractionLayer;
/*     */ import oshi.hardware.platform.unix.aix.AixHardwareAbstractionLayer;
/*     */ import oshi.hardware.platform.unix.freebsd.FreeBsdHardwareAbstractionLayer;
/*     */ import oshi.hardware.platform.unix.solaris.SolarisHardwareAbstractionLayer;
/*     */ import oshi.hardware.platform.windows.WindowsHardwareAbstractionLayer;
/*     */ import oshi.software.os.OperatingSystem;
/*     */ import oshi.software.os.linux.LinuxOperatingSystem;
/*     */ import oshi.software.os.mac.MacOperatingSystem;
/*     */ import oshi.software.os.unix.aix.AixOperatingSystem;
/*     */ import oshi.software.os.unix.freebsd.FreeBsdOperatingSystem;
/*     */ import oshi.software.os.unix.solaris.SolarisOperatingSystem;
/*     */ import oshi.software.os.windows.WindowsOperatingSystem;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SystemInfo
/*     */ {
/*     */   private static final PlatformEnum currentPlatformEnum;
/*     */   
/*     */   static {
/*  61 */     if (Platform.isWindows()) {
/*  62 */       currentPlatformEnum = PlatformEnum.WINDOWS;
/*  63 */     } else if (Platform.isLinux()) {
/*  64 */       currentPlatformEnum = PlatformEnum.LINUX;
/*  65 */     } else if (Platform.isMac()) {
/*  66 */       currentPlatformEnum = PlatformEnum.MACOSX;
/*  67 */     } else if (Platform.isSolaris()) {
/*  68 */       currentPlatformEnum = PlatformEnum.SOLARIS;
/*  69 */     } else if (Platform.isFreeBSD()) {
/*  70 */       currentPlatformEnum = PlatformEnum.FREEBSD;
/*  71 */     } else if (Platform.isAIX()) {
/*  72 */       currentPlatformEnum = PlatformEnum.AIX;
/*     */     } else {
/*  74 */       currentPlatformEnum = PlatformEnum.UNKNOWN;
/*     */     } 
/*     */   }
/*     */   
/*  78 */   private final Supplier<OperatingSystem> os = Memoizer.memoize(this::createOperatingSystem);
/*     */   
/*  80 */   private final Supplier<HardwareAbstractionLayer> hardware = Memoizer.memoize(this::createHardware);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PlatformEnum getCurrentPlatformEnum() {
/*  90 */     return currentPlatformEnum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OperatingSystem getOperatingSystem() {
/* 100 */     return this.os.get();
/*     */   }
/*     */   
/*     */   private OperatingSystem createOperatingSystem() {
/* 104 */     switch (currentPlatformEnum) {
/*     */       
/*     */       case WINDOWS:
/* 107 */         return (OperatingSystem)new WindowsOperatingSystem();
/*     */       case LINUX:
/* 109 */         return (OperatingSystem)new LinuxOperatingSystem();
/*     */       case MACOSX:
/* 111 */         return (OperatingSystem)new MacOperatingSystem();
/*     */       case SOLARIS:
/* 113 */         return (OperatingSystem)new SolarisOperatingSystem();
/*     */       case FREEBSD:
/* 115 */         return (OperatingSystem)new FreeBsdOperatingSystem();
/*     */       case AIX:
/* 117 */         return (OperatingSystem)new AixOperatingSystem();
/*     */     } 
/* 119 */     throw new UnsupportedOperationException("Operating system not supported: " + Platform.getOSType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HardwareAbstractionLayer getHardware() {
/* 130 */     return this.hardware.get();
/*     */   }
/*     */   
/*     */   private HardwareAbstractionLayer createHardware() {
/* 134 */     switch (currentPlatformEnum) {
/*     */       
/*     */       case WINDOWS:
/* 137 */         return (HardwareAbstractionLayer)new WindowsHardwareAbstractionLayer();
/*     */       case LINUX:
/* 139 */         return (HardwareAbstractionLayer)new LinuxHardwareAbstractionLayer();
/*     */       case MACOSX:
/* 141 */         return (HardwareAbstractionLayer)new MacHardwareAbstractionLayer();
/*     */       case SOLARIS:
/* 143 */         return (HardwareAbstractionLayer)new SolarisHardwareAbstractionLayer();
/*     */       case FREEBSD:
/* 145 */         return (HardwareAbstractionLayer)new FreeBsdHardwareAbstractionLayer();
/*     */       case AIX:
/* 147 */         return (HardwareAbstractionLayer)new AixHardwareAbstractionLayer();
/*     */     } 
/* 149 */     throw new UnsupportedOperationException("Operating system not supported: " + Platform.getOSType());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\SystemInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */