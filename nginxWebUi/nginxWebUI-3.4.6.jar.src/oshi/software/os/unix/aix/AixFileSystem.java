/*     */ package oshi.software.os.unix.aix;
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
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
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
/*     */ public class AixFileSystem
/*     */   extends AbstractFileSystem
/*     */ {
/*  48 */   private static final List<String> TMP_FS_PATHS = Arrays.asList(new String[] { "/proc" });
/*     */ 
/*     */   
/*     */   public List<OSFileStore> getFileStores(boolean localOnly) {
/*  52 */     return getFileStoreMatching((String)null, localOnly);
/*     */   }
/*     */ 
/*     */   
/*     */   static List<OSFileStore> getFileStoreMatching(String nameToMatch) {
/*  57 */     return getFileStoreMatching(nameToMatch, false);
/*     */   }
/*     */   
/*     */   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, boolean localOnly) {
/*  61 */     List<OSFileStore> fsList = new ArrayList<>();
/*     */ 
/*     */     
/*  64 */     Map<String, Long> inodeFreeMap = new HashMap<>();
/*  65 */     Map<String, Long> inodeTotalMap = new HashMap<>();
/*  66 */     String command = "df -i" + (localOnly ? " -l" : "");
/*  67 */     for (String line : ExecutingCommand.runNative(command)) {
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
/*  81 */       if (line.startsWith("/")) {
/*  82 */         String[] split = ParseUtil.whitespaces.split(line);
/*  83 */         if (split.length > 5) {
/*  84 */           inodeTotalMap.put(split[0], Long.valueOf(ParseUtil.parseLongOrDefault(split[1], 0L)));
/*  85 */           inodeFreeMap.put(split[0], Long.valueOf(ParseUtil.parseLongOrDefault(split[3], 0L)));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  91 */     for (String fs : ExecutingCommand.runNative("mount")) {
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
/* 107 */       String[] split = ParseUtil.whitespaces.split("x" + fs);
/* 108 */       if (split.length > 7) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 114 */         String description, volume = split[1];
/* 115 */         String path = split[2];
/* 116 */         String type = split[3];
/* 117 */         String options = split[4];
/*     */ 
/*     */         
/* 120 */         if ((localOnly && NETWORK_FS_TYPES.contains(type)) || PSEUDO_FS_TYPES.contains(type) || path
/* 121 */           .equals("/dev") || !path.startsWith("/") || (
/* 122 */           ParseUtil.filePathStartsWith(TMP_FS_PATHS, path) && !path.equals("/"))) {
/*     */           continue;
/*     */         }
/*     */         
/* 126 */         String name = path.substring(path.lastIndexOf('/') + 1);
/*     */         
/* 128 */         if (name.isEmpty()) {
/* 129 */           name = volume.substring(volume.lastIndexOf('/') + 1);
/*     */         }
/*     */         
/* 132 */         if (nameToMatch != null && !nameToMatch.equals(name)) {
/*     */           continue;
/*     */         }
/* 135 */         File f = new File(path);
/* 136 */         long totalSpace = f.getTotalSpace();
/* 137 */         long usableSpace = f.getUsableSpace();
/* 138 */         long freeSpace = f.getFreeSpace();
/*     */ 
/*     */         
/* 141 */         if (volume.startsWith("/dev") || path.equals("/")) {
/* 142 */           description = "Local Disk";
/* 143 */         } else if (volume.equals("tmpfs")) {
/* 144 */           description = "Ram Disk";
/* 145 */         } else if (NETWORK_FS_TYPES.contains(type)) {
/* 146 */           description = "Network Disk";
/*     */         } else {
/* 148 */           description = "Mount Point";
/*     */         } 
/*     */         
/* 151 */         fsList.add(new AixOSFileStore(name, volume, name, path, options, "", "", description, type, freeSpace, usableSpace, totalSpace, ((Long)inodeFreeMap
/* 152 */               .getOrDefault(volume, Long.valueOf(0L))).longValue(), ((Long)inodeTotalMap
/* 153 */               .getOrDefault(volume, Long.valueOf(0L))).longValue()));
/*     */       } 
/*     */     } 
/* 156 */     return fsList;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFileDescriptors() {
/* 161 */     boolean header = false;
/* 162 */     long openfiles = 0L;
/* 163 */     for (String f : ExecutingCommand.runNative("lsof -nl")) {
/* 164 */       if (!header) {
/* 165 */         header = f.startsWith("COMMAND"); continue;
/*     */       } 
/* 167 */       openfiles++;
/*     */     } 
/*     */     
/* 170 */     return openfiles;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxFileDescriptors() {
/* 175 */     return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("ulimit -n"), 0L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\aix\AixFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */