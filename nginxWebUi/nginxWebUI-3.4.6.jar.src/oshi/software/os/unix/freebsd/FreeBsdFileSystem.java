/*     */ package oshi.software.os.unix.freebsd;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.common.AbstractFileSystem;
/*     */ import oshi.software.os.OSFileStore;
/*     */ import oshi.software.os.linux.LinuxOSFileStore;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
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
/*     */ @ThreadSafe
/*     */ public final class FreeBsdFileSystem
/*     */   extends AbstractFileSystem
/*     */ {
/*  51 */   private static final List<String> TMP_FS_PATHS = Arrays.asList(new String[] { "/system", "/tmp", "/dev/fd" });
/*     */ 
/*     */ 
/*     */   
/*     */   public List<OSFileStore> getFileStores(boolean localOnly) {
/*  56 */     Map<String, String> uuidMap = new HashMap<>();
/*     */     
/*  58 */     String device = "";
/*  59 */     for (String line : ExecutingCommand.runNative("geom part list")) {
/*  60 */       if (line.contains("Name: ")) {
/*  61 */         device = line.substring(line.lastIndexOf(' ') + 1);
/*     */       }
/*     */       
/*  64 */       if (device.isEmpty()) {
/*     */         continue;
/*     */       }
/*  67 */       line = line.trim();
/*  68 */       if (line.startsWith("rawuuid:")) {
/*  69 */         uuidMap.put(device, line.substring(line.lastIndexOf(' ') + 1));
/*  70 */         device = "";
/*     */       } 
/*     */     } 
/*     */     
/*  74 */     List<OSFileStore> fsList = new ArrayList<>();
/*     */ 
/*     */     
/*  77 */     Map<String, Long> inodeFreeMap = new HashMap<>();
/*  78 */     Map<String, Long> inodeTotalMap = new HashMap<>();
/*  79 */     for (String line : ExecutingCommand.runNative("df -i")) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  84 */       if (line.startsWith("/")) {
/*  85 */         String[] split = ParseUtil.whitespaces.split(line);
/*  86 */         if (split.length > 7) {
/*  87 */           inodeFreeMap.put(split[0], Long.valueOf(ParseUtil.parseLongOrDefault(split[6], 0L)));
/*     */           
/*  89 */           inodeTotalMap.put(split[0], 
/*  90 */               Long.valueOf(((Long)inodeFreeMap.get(split[0])).longValue() + ParseUtil.parseLongOrDefault(split[5], 0L)));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  96 */     for (String fs : ExecutingCommand.runNative("mount -p")) {
/*  97 */       String description, split[] = ParseUtil.whitespaces.split(fs);
/*  98 */       if (split.length < 5) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 106 */       String volume = split[0];
/* 107 */       String path = split[1];
/* 108 */       String type = split[2];
/* 109 */       String options = split[3];
/*     */ 
/*     */       
/* 112 */       if ((localOnly && NETWORK_FS_TYPES.contains(type)) || PSEUDO_FS_TYPES.contains(type) || path.equals("/dev") || 
/* 113 */         ParseUtil.filePathStartsWith(TMP_FS_PATHS, path) || (volume
/* 114 */         .startsWith("rpool") && !path.equals("/"))) {
/*     */         continue;
/*     */       }
/*     */       
/* 118 */       String name = path.substring(path.lastIndexOf('/') + 1);
/*     */       
/* 120 */       if (name.isEmpty()) {
/* 121 */         name = volume.substring(volume.lastIndexOf('/') + 1);
/*     */       }
/* 123 */       File f = new File(path);
/* 124 */       long totalSpace = f.getTotalSpace();
/* 125 */       long usableSpace = f.getUsableSpace();
/* 126 */       long freeSpace = f.getFreeSpace();
/*     */ 
/*     */       
/* 129 */       if (volume.startsWith("/dev") || path.equals("/")) {
/* 130 */         description = "Local Disk";
/* 131 */       } else if (volume.equals("tmpfs")) {
/* 132 */         description = "Ram Disk";
/* 133 */       } else if (NETWORK_FS_TYPES.contains(type)) {
/* 134 */         description = "Network Disk";
/*     */       } else {
/* 136 */         description = "Mount Point";
/*     */       } 
/*     */       
/* 139 */       String uuid = uuidMap.getOrDefault(name, "");
/*     */       
/* 141 */       fsList.add(new LinuxOSFileStore(name, volume, name, path, options, uuid, "", description, type, freeSpace, usableSpace, totalSpace, 
/* 142 */             inodeFreeMap.containsKey(path) ? ((Long)inodeFreeMap.get(path)).longValue() : 0L, 
/* 143 */             inodeTotalMap.containsKey(path) ? ((Long)inodeTotalMap.get(path)).longValue() : 0L));
/*     */     } 
/* 145 */     return fsList;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFileDescriptors() {
/* 150 */     return BsdSysctlUtil.sysctl("kern.openfiles", 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxFileDescriptors() {
/* 155 */     return BsdSysctlUtil.sysctl("kern.maxfiles", 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\freebsd\FreeBsdFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */