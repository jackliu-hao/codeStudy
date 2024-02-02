/*     */ package oshi.software.os.mac;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.PointerType;
/*     */ import com.sun.jna.platform.mac.CoreFoundation;
/*     */ import com.sun.jna.platform.mac.DiskArbitration;
/*     */ import com.sun.jna.platform.mac.IOKit;
/*     */ import com.sun.jna.platform.mac.IOKitUtil;
/*     */ import com.sun.jna.platform.mac.SystemB;
/*     */ import java.io.File;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.stream.Collectors;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.common.AbstractFileSystem;
/*     */ import oshi.software.os.OSFileStore;
/*     */ import oshi.util.platform.mac.SysctlUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public class MacFileSystem
/*     */   extends AbstractFileSystem
/*     */ {
/*  67 */   private static final Logger LOG = LoggerFactory.getLogger(MacFileSystem.class);
/*     */ 
/*     */   
/*  70 */   private static final Pattern LOCAL_DISK = Pattern.compile("/dev/disk\\d");
/*     */   
/*     */   private static final int MNT_RDONLY = 1;
/*     */   
/*     */   private static final int MNT_SYNCHRONOUS = 2;
/*     */   
/*     */   private static final int MNT_NOEXEC = 4;
/*     */   private static final int MNT_NOSUID = 8;
/*     */   private static final int MNT_NODEV = 16;
/*     */   private static final int MNT_UNION = 32;
/*     */   private static final int MNT_ASYNC = 64;
/*     */   private static final int MNT_CPROTECT = 128;
/*     */   private static final int MNT_EXPORTED = 256;
/*     */   private static final int MNT_QUARANTINE = 1024;
/*     */   private static final int MNT_LOCAL = 4096;
/*     */   private static final int MNT_QUOTA = 8192;
/*     */   private static final int MNT_ROOTFS = 16384;
/*     */   private static final int MNT_DOVOLFS = 32768;
/*     */   private static final int MNT_DONTBROWSE = 1048576;
/*     */   private static final int MNT_IGNORE_OWNERSHIP = 2097152;
/*     */   private static final int MNT_AUTOMOUNTED = 4194304;
/*     */   private static final int MNT_JOURNALED = 8388608;
/*     */   private static final int MNT_NOUSERXATTR = 16777216;
/*     */   private static final int MNT_DEFWRITE = 33554432;
/*     */   private static final int MNT_MULTILABEL = 67108864;
/*     */   private static final int MNT_NOATIME = 268435456;
/*  96 */   private static final Map<Integer, String> OPTIONS_MAP = new HashMap<>();
/*     */   static {
/*  98 */     OPTIONS_MAP.put(Integer.valueOf(2), "synchronous");
/*  99 */     OPTIONS_MAP.put(Integer.valueOf(4), "noexec");
/* 100 */     OPTIONS_MAP.put(Integer.valueOf(8), "nosuid");
/* 101 */     OPTIONS_MAP.put(Integer.valueOf(16), "nodev");
/* 102 */     OPTIONS_MAP.put(Integer.valueOf(32), "union");
/* 103 */     OPTIONS_MAP.put(Integer.valueOf(64), "asynchronous");
/* 104 */     OPTIONS_MAP.put(Integer.valueOf(128), "content-protection");
/* 105 */     OPTIONS_MAP.put(Integer.valueOf(256), "exported");
/* 106 */     OPTIONS_MAP.put(Integer.valueOf(1024), "quarantined");
/* 107 */     OPTIONS_MAP.put(Integer.valueOf(4096), "local");
/* 108 */     OPTIONS_MAP.put(Integer.valueOf(8192), "quotas");
/* 109 */     OPTIONS_MAP.put(Integer.valueOf(16384), "rootfs");
/* 110 */     OPTIONS_MAP.put(Integer.valueOf(32768), "volfs");
/* 111 */     OPTIONS_MAP.put(Integer.valueOf(1048576), "nobrowse");
/* 112 */     OPTIONS_MAP.put(Integer.valueOf(2097152), "noowners");
/* 113 */     OPTIONS_MAP.put(Integer.valueOf(4194304), "automounted");
/* 114 */     OPTIONS_MAP.put(Integer.valueOf(8388608), "journaled");
/* 115 */     OPTIONS_MAP.put(Integer.valueOf(16777216), "nouserxattr");
/* 116 */     OPTIONS_MAP.put(Integer.valueOf(33554432), "defwrite");
/* 117 */     OPTIONS_MAP.put(Integer.valueOf(67108864), "multilabel");
/* 118 */     OPTIONS_MAP.put(Integer.valueOf(268435456), "noatime");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<OSFileStore> getFileStores(boolean localOnly) {
/* 124 */     return getFileStoreMatching(null, localOnly);
/*     */   }
/*     */ 
/*     */   
/*     */   static List<OSFileStore> getFileStoreMatching(String nameToMatch) {
/* 129 */     return getFileStoreMatching(nameToMatch, false);
/*     */   }
/*     */   
/*     */   private static List<OSFileStore> getFileStoreMatching(String nameToMatch, boolean localOnly) {
/* 133 */     List<OSFileStore> fsList = new ArrayList<>();
/*     */ 
/*     */ 
/*     */     
/* 137 */     int numfs = SystemB.INSTANCE.getfsstat64(null, 0, 0);
/* 138 */     if (numfs > 0) {
/*     */ 
/*     */ 
/*     */       
/* 142 */       DiskArbitration.DASessionRef session = DiskArbitration.INSTANCE.DASessionCreate(CoreFoundation.INSTANCE.CFAllocatorGetDefault());
/* 143 */       if (session == null) {
/* 144 */         LOG.error("Unable to open session to DiskArbitration framework.");
/*     */       } else {
/* 146 */         CoreFoundation.CFStringRef daVolumeNameKey = CoreFoundation.CFStringRef.createCFString("DAVolumeName");
/*     */ 
/*     */         
/* 149 */         SystemB.Statfs[] fs = new SystemB.Statfs[numfs];
/*     */         
/* 151 */         numfs = SystemB.INSTANCE.getfsstat64(fs, numfs * (new SystemB.Statfs()).size(), 16);
/* 152 */         for (int f = 0; f < numfs; f++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 158 */           String volume = (new String((fs[f]).f_mntfromname, StandardCharsets.UTF_8)).trim();
/*     */           
/* 160 */           int flags = (fs[f]).f_flags;
/* 161 */           if ((!localOnly || (flags & 0x1000) != 0) && !volume.equals("devfs") && 
/* 162 */             !volume.startsWith("map ")) {
/*     */ 
/*     */ 
/*     */             
/* 166 */             String type = (new String((fs[f]).f_fstypename, StandardCharsets.UTF_8)).trim();
/* 167 */             String description = "Volume";
/* 168 */             if (LOCAL_DISK.matcher(volume).matches()) {
/* 169 */               description = "Local Disk";
/* 170 */             } else if (volume.startsWith("localhost:") || volume.startsWith("//") || volume.startsWith("smb://") || NETWORK_FS_TYPES
/* 171 */               .contains(type)) {
/* 172 */               description = "Network Drive";
/*     */             } 
/* 174 */             String path = (new String((fs[f]).f_mntonname, StandardCharsets.UTF_8)).trim();
/* 175 */             String name = "";
/* 176 */             File file = new File(path);
/* 177 */             if (name.isEmpty()) {
/* 178 */               name = file.getName();
/*     */               
/* 180 */               if (name.isEmpty()) {
/* 181 */                 name = file.getPath();
/*     */               }
/*     */             } 
/* 184 */             if (nameToMatch == null || nameToMatch.equals(name))
/*     */             
/*     */             { 
/*     */               
/* 188 */               StringBuilder options = new StringBuilder(((0x1 & flags) == 0) ? "rw" : "ro");
/*     */               
/* 190 */               String moreOptions = OPTIONS_MAP.entrySet().stream().filter(e -> ((((Integer)e.getKey()).intValue() & flags) > 0)).map(Map.Entry::getValue).collect(Collectors.joining(","));
/* 191 */               if (!moreOptions.isEmpty()) {
/* 192 */                 options.append(',').append(moreOptions);
/*     */               }
/*     */               
/* 195 */               String uuid = "";
/*     */ 
/*     */               
/* 198 */               String bsdName = volume.replace("/dev/disk", "disk");
/* 199 */               if (bsdName.startsWith("disk")) {
/*     */ 
/*     */                 
/* 202 */                 DiskArbitration.DADiskRef disk = DiskArbitration.INSTANCE.DADiskCreateFromBSDName(CoreFoundation.INSTANCE
/* 203 */                     .CFAllocatorGetDefault(), session, volume);
/* 204 */                 if (disk != null) {
/* 205 */                   CoreFoundation.CFDictionaryRef diskInfo = DiskArbitration.INSTANCE.DADiskCopyDescription(disk);
/* 206 */                   if (diskInfo != null) {
/*     */                     
/* 208 */                     Pointer result = diskInfo.getValue((PointerType)daVolumeNameKey);
/* 209 */                     CoreFoundation.CFStringRef volumePtr = new CoreFoundation.CFStringRef(result);
/* 210 */                     name = volumePtr.stringValue();
/* 211 */                     if (name == null) {
/* 212 */                       name = "unknown";
/*     */                     }
/* 214 */                     diskInfo.release();
/*     */                   } 
/* 216 */                   disk.release();
/*     */                 } 
/*     */                 
/* 219 */                 CoreFoundation.CFMutableDictionaryRef matchingDict = IOKitUtil.getBSDNameMatchingDict(bsdName);
/* 220 */                 if (matchingDict != null) {
/*     */                   
/* 222 */                   IOKit.IOIterator fsIter = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
/* 223 */                   if (fsIter != null) {
/*     */ 
/*     */                     
/* 226 */                     IOKit.IORegistryEntry fsEntry = fsIter.next();
/* 227 */                     if (fsEntry != null && fsEntry.conformsTo("IOMedia")) {
/*     */                       
/* 229 */                       uuid = fsEntry.getStringProperty("UUID");
/* 230 */                       if (uuid != null) {
/* 231 */                         uuid = uuid.toLowerCase();
/*     */                       }
/* 233 */                       fsEntry.release();
/*     */                     } 
/* 235 */                     fsIter.release();
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */               
/* 240 */               fsList.add(new MacOSFileStore(name, volume, name, path, options.toString(), 
/* 241 */                     (uuid == null) ? "" : uuid, "", description, type, file.getFreeSpace(), file.getUsableSpace(), file
/* 242 */                     .getTotalSpace(), (fs[f]).f_ffree, (fs[f]).f_files)); } 
/*     */           } 
/* 244 */         }  daVolumeNameKey.release();
/*     */         
/* 246 */         session.release();
/*     */       } 
/*     */     } 
/* 249 */     return fsList;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOpenFileDescriptors() {
/* 254 */     return SysctlUtil.sysctl("kern.num_files", 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxFileDescriptors() {
/* 259 */     return SysctlUtil.sysctl("kern.maxfiles", 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\mac\MacFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */