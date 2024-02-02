/*     */ package oshi.software.os.unix.solaris;
/*     */ 
/*     */ import com.sun.jna.platform.unix.solaris.LibKstat;
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
/*     */ import oshi.util.platform.unix.solaris.KstatUtil;
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
/*     */ @ThreadSafe
/*     */ public class SolarisFileSystem
/*     */   extends AbstractFileSystem
/*     */ {
/*  53 */   private static final List<String> TMP_FS_PATHS = Arrays.asList(new String[] { "/system", "/tmp", "/dev/fd" });
/*     */ 
/*     */   
/*     */   public List<OSFileStore> getFileStores(boolean localOnly) {
/*  57 */     return getFileStoreMatching((String)null, localOnly);
/*     */   }
/*     */ 
/*     */   
/*     */   static List<OSFileStore> getFileStoreMatching(String nameToMatch) {
/*  62 */     return getFileStoreMatching(nameToMatch, false);
/*     */   }
/*     */   
/*     */   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, boolean localOnly) {
/*  66 */     List<OSFileStore> fsList = new ArrayList<>();
/*     */ 
/*     */     
/*  69 */     Map<String, Long> inodeFreeMap = new HashMap<>();
/*  70 */     Map<String, Long> inodeTotalMap = new HashMap<>();
/*  71 */     String key = null;
/*  72 */     String total = null;
/*  73 */     String free = null;
/*  74 */     String command = "df -g" + (localOnly ? " -l" : "");
/*  75 */     for (String line : ExecutingCommand.runNative(command)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  82 */       if (line.startsWith("/")) {
/*  83 */         key = ParseUtil.whitespaces.split(line)[0];
/*  84 */         total = null; continue;
/*  85 */       }  if (line.contains("available") && line.contains("total files")) {
/*  86 */         total = ParseUtil.getTextBetweenStrings(line, "available", "total files").trim(); continue;
/*  87 */       }  if (line.contains("free files")) {
/*  88 */         free = ParseUtil.getTextBetweenStrings(line, "", "free files").trim();
/*  89 */         if (key != null && total != null) {
/*  90 */           inodeFreeMap.put(key, Long.valueOf(ParseUtil.parseLongOrDefault(free, 0L)));
/*  91 */           inodeTotalMap.put(key, Long.valueOf(ParseUtil.parseLongOrDefault(total, 0L)));
/*  92 */           key = null;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  98 */     for (String fs : ExecutingCommand.runNative("cat /etc/mnttab")) {
/*  99 */       String description, split[] = ParseUtil.whitespaces.split(fs);
/* 100 */       if (split.length < 5) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 108 */       String volume = split[0];
/* 109 */       String path = split[1];
/* 110 */       String type = split[2];
/* 111 */       String options = split[3];
/*     */ 
/*     */       
/* 114 */       if ((localOnly && NETWORK_FS_TYPES.contains(type)) || PSEUDO_FS_TYPES.contains(type) || path.equals("/dev") || 
/* 115 */         ParseUtil.filePathStartsWith(TMP_FS_PATHS, path) || (volume
/* 116 */         .startsWith("rpool") && !path.equals("/"))) {
/*     */         continue;
/*     */       }
/*     */       
/* 120 */       String name = path.substring(path.lastIndexOf('/') + 1);
/*     */       
/* 122 */       if (name.isEmpty()) {
/* 123 */         name = volume.substring(volume.lastIndexOf('/') + 1);
/*     */       }
/*     */       
/* 126 */       if (nameToMatch != null && !nameToMatch.equals(name)) {
/*     */         continue;
/*     */       }
/* 129 */       File f = new File(path);
/* 130 */       long totalSpace = f.getTotalSpace();
/* 131 */       long usableSpace = f.getUsableSpace();
/* 132 */       long freeSpace = f.getFreeSpace();
/*     */ 
/*     */       
/* 135 */       if (volume.startsWith("/dev") || path.equals("/")) {
/* 136 */         description = "Local Disk";
/* 137 */       } else if (volume.equals("tmpfs")) {
/* 138 */         description = "Ram Disk";
/* 139 */       } else if (NETWORK_FS_TYPES.contains(type)) {
/* 140 */         description = "Network Disk";
/*     */       } else {
/* 142 */         description = "Mount Point";
/*     */       } 
/*     */       
/* 145 */       fsList.add(new SolarisOSFileStore(name, volume, name, path, options, "", "", description, type, freeSpace, usableSpace, totalSpace, 
/* 146 */             inodeFreeMap.containsKey(path) ? ((Long)inodeFreeMap.get(path)).longValue() : 0L, 
/* 147 */             inodeTotalMap.containsKey(path) ? ((Long)inodeTotalMap.get(path)).longValue() : 0L));
/*     */     } 
/* 149 */     return fsList;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFileDescriptors() {
/* 154 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 155 */     try { LibKstat.Kstat ksp = kc.lookup(null, -1, "file_cache");
/*     */       
/* 157 */       if (ksp != null && kc.read(ksp))
/* 158 */       { long l = KstatUtil.dataLookupLong(ksp, "buf_inuse");
/*     */         
/* 160 */         if (kc != null) kc.close();  return l; }  if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 161 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxFileDescriptors() {
/* 166 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 167 */     try { LibKstat.Kstat ksp = kc.lookup(null, -1, "file_cache");
/*     */       
/* 169 */       if (ksp != null && kc.read(ksp))
/* 170 */       { long l = KstatUtil.dataLookupLong(ksp, "buf_max");
/*     */         
/* 172 */         if (kc != null) kc.close();  return l; }  if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 173 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return 0L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\solaris\SolarisFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */