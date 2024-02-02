/*     */ package oshi.software.os.linux;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.platform.linux.LibC;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.common.AbstractFileSystem;
/*     */ import oshi.software.os.OSFileStore;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
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
/*     */ public class LinuxFileSystem
/*     */   extends AbstractFileSystem
/*     */ {
/*  59 */   private static final Logger LOG = LoggerFactory.getLogger(LinuxFileSystem.class);
/*     */ 
/*     */   
/*     */   private static final String UNICODE_SPACE = "\\040";
/*     */   
/*  64 */   private static final List<String> TMP_FS_PATHS = Arrays.asList(new String[] { "/run", "/sys", "/proc", ProcPath.PROC });
/*     */ 
/*     */ 
/*     */   
/*     */   public List<OSFileStore> getFileStores(boolean localOnly) {
/*  69 */     Map<String, String> uuidMap = new HashMap<>();
/*  70 */     File uuidDir = new File("/dev/disk/by-uuid");
/*  71 */     if (uuidDir.listFiles() != null) {
/*  72 */       for (File uuid : uuidDir.listFiles()) {
/*     */         
/*     */         try {
/*  75 */           uuidMap.put(uuid.getCanonicalPath(), uuid.getName().toLowerCase());
/*  76 */         } catch (IOException e) {
/*  77 */           LOG.error("Couldn't get canonical path for {}. {}", uuid.getName(), e.getMessage());
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  83 */     return getFileStoreMatching((String)null, uuidMap, localOnly);
/*     */   }
/*     */ 
/*     */   
/*     */   static List<OSFileStore> getFileStoreMatching(String nameToMatch, Map<String, String> uuidMap) {
/*  88 */     return getFileStoreMatching(nameToMatch, uuidMap, false);
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, Map<String, String> uuidMap, boolean localOnly) {
/*  93 */     List<OSFileStore> fsList = new ArrayList<>();
/*     */ 
/*     */     
/*  96 */     List<String> mounts = FileUtil.readFile(ProcPath.MOUNTS);
/*  97 */     for (String mount : mounts) {
/*  98 */       String description, split[] = mount.split(" ");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 106 */       if (split.length < 6) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 111 */       String path = split[1].replace("\\040", " ");
/* 112 */       String type = split[2];
/* 113 */       if ((localOnly && NETWORK_FS_TYPES.contains(type)) || PSEUDO_FS_TYPES
/* 114 */         .contains(type) || path
/* 115 */         .equals("/dev") || 
/* 116 */         ParseUtil.filePathStartsWith(TMP_FS_PATHS, path) || path
/* 117 */         .endsWith("/shm")) {
/*     */         continue;
/*     */       }
/*     */       
/* 121 */       String options = split[3];
/*     */       
/* 123 */       String name = split[0].replace("\\040", " ");
/* 124 */       if (path.equals("/")) {
/* 125 */         name = "/";
/*     */       }
/*     */ 
/*     */       
/* 129 */       if (nameToMatch != null && !nameToMatch.equals(name)) {
/*     */         continue;
/*     */       }
/*     */       
/* 133 */       String volume = split[0].replace("\\040", " ");
/* 134 */       String uuid = (uuidMap != null) ? uuidMap.getOrDefault(split[0], "") : "";
/*     */ 
/*     */       
/* 137 */       if (volume.startsWith("/dev")) {
/* 138 */         description = "Local Disk";
/* 139 */       } else if (volume.equals("tmpfs")) {
/* 140 */         description = "Ram Disk";
/* 141 */       } else if (NETWORK_FS_TYPES.contains(type)) {
/* 142 */         description = "Network Disk";
/*     */       } else {
/* 144 */         description = "Mount Point";
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 149 */       String logicalVolume = "";
/* 150 */       String volumeMapperDirectory = "/dev/mapper/";
/* 151 */       Path link = Paths.get(volume, new String[0]);
/* 152 */       if (link.toFile().exists() && Files.isSymbolicLink(link)) {
/*     */         try {
/* 154 */           Path slink = Files.readSymbolicLink(link);
/* 155 */           Path full = Paths.get(volumeMapperDirectory + slink.toString(), new String[0]);
/* 156 */           if (full.toFile().exists()) {
/* 157 */             logicalVolume = full.normalize().toString();
/*     */           }
/* 159 */         } catch (IOException e) {
/* 160 */           LOG.warn("Couldn't access symbolic path  {}. {}", link, e.getMessage());
/*     */         } 
/*     */       }
/*     */       
/* 164 */       long totalInodes = 0L;
/* 165 */       long freeInodes = 0L;
/* 166 */       long totalSpace = 0L;
/* 167 */       long usableSpace = 0L;
/* 168 */       long freeSpace = 0L;
/*     */       
/*     */       try {
/* 171 */         LibC.Statvfs vfsStat = new LibC.Statvfs();
/* 172 */         if (0 == LibC.INSTANCE.statvfs(path, vfsStat)) {
/* 173 */           totalInodes = vfsStat.f_files.longValue();
/* 174 */           freeInodes = vfsStat.f_ffree.longValue();
/*     */           
/* 176 */           totalSpace = vfsStat.f_blocks.longValue() * vfsStat.f_frsize.longValue();
/* 177 */           usableSpace = vfsStat.f_bavail.longValue() * vfsStat.f_frsize.longValue();
/* 178 */           freeSpace = vfsStat.f_bfree.longValue() * vfsStat.f_frsize.longValue();
/*     */         } else {
/* 180 */           File tmpFile = new File(path);
/* 181 */           totalSpace = tmpFile.getTotalSpace();
/* 182 */           usableSpace = tmpFile.getUsableSpace();
/* 183 */           freeSpace = tmpFile.getFreeSpace();
/* 184 */           LOG.warn("Failed to get information to use statvfs. path: {}, Error code: {}", path, 
/* 185 */               Integer.valueOf(Native.getLastError()));
/*     */         } 
/* 187 */       } catch (UnsatisfiedLinkError|NoClassDefFoundError e) {
/* 188 */         LOG.error("Failed to get file counts from statvfs. {}", e.getMessage());
/*     */       } 
/*     */       
/* 191 */       fsList.add(new LinuxOSFileStore(name, volume, name, path, options, uuid, logicalVolume, description, type, freeSpace, usableSpace, totalSpace, freeInodes, totalInodes));
/*     */     } 
/*     */     
/* 194 */     return fsList;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFileDescriptors() {
/* 199 */     return getFileDescriptors(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxFileDescriptors() {
/* 204 */     return getFileDescriptors(2);
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
/*     */   private static long getFileDescriptors(int index) {
/* 219 */     String filename = ProcPath.SYS_FS_FILE_NR;
/* 220 */     if (index < 0 || index > 2) {
/* 221 */       throw new IllegalArgumentException("Index must be between 0 and 2.");
/*     */     }
/* 223 */     List<String> osDescriptors = FileUtil.readFile(filename);
/* 224 */     if (!osDescriptors.isEmpty()) {
/* 225 */       String[] splittedLine = ((String)osDescriptors.get(0)).split("\\D+");
/* 226 */       return ParseUtil.parseLongOrDefault(splittedLine[index], 0L);
/*     */     } 
/* 228 */     return 0L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\linux\LinuxFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */