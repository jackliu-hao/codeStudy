/*     */ package oshi.software.os.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.perfmon.ProcessInformation;
/*     */ import oshi.driver.windows.wmi.Win32LogicalDisk;
/*     */ import oshi.software.common.AbstractFileSystem;
/*     */ import oshi.software.os.OSFileStore;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.windows.WmiUtil;
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
/*     */ public class WindowsFileSystem
/*     */   extends AbstractFileSystem
/*     */ {
/*     */   private static final int BUFSIZE = 255;
/*     */   private static final int SEM_FAILCRITICALERRORS = 1;
/*     */   private static final int FILE_CASE_SENSITIVE_SEARCH = 1;
/*     */   private static final int FILE_CASE_PRESERVED_NAMES = 2;
/*     */   private static final int FILE_FILE_COMPRESSION = 16;
/*     */   private static final int FILE_DAX_VOLUME = 536870912;
/*     */   private static final int FILE_NAMED_STREAMS = 262144;
/*     */   private static final int FILE_PERSISTENT_ACLS = 8;
/*     */   private static final int FILE_READ_ONLY_VOLUME = 524288;
/*     */   private static final int FILE_SEQUENTIAL_WRITE_ONCE = 1048576;
/*     */   private static final int FILE_SUPPORTS_ENCRYPTION = 131072;
/*     */   private static final int FILE_SUPPORTS_OBJECT_IDS = 65536;
/*     */   private static final int FILE_SUPPORTS_REPARSE_POINTS = 128;
/*     */   private static final int FILE_SUPPORTS_SPARSE_FILES = 64;
/*     */   private static final int FILE_SUPPORTS_TRANSACTIONS = 2097152;
/*     */   private static final int FILE_SUPPORTS_USN_JOURNAL = 33554432;
/*     */   private static final int FILE_UNICODE_ON_DISK = 4;
/*     */   private static final int FILE_VOLUME_IS_COMPRESSED = 32768;
/*     */   private static final int FILE_VOLUME_QUOTAS = 32;
/*  79 */   private static final Map<Integer, String> OPTIONS_MAP = new HashMap<>();
/*     */   static {
/*  81 */     OPTIONS_MAP.put(Integer.valueOf(2), "casepn");
/*  82 */     OPTIONS_MAP.put(Integer.valueOf(1), "casess");
/*  83 */     OPTIONS_MAP.put(Integer.valueOf(16), "fcomp");
/*  84 */     OPTIONS_MAP.put(Integer.valueOf(536870912), "dax");
/*  85 */     OPTIONS_MAP.put(Integer.valueOf(262144), "streams");
/*  86 */     OPTIONS_MAP.put(Integer.valueOf(8), "acls");
/*  87 */     OPTIONS_MAP.put(Integer.valueOf(1048576), "wronce");
/*  88 */     OPTIONS_MAP.put(Integer.valueOf(131072), "efs");
/*  89 */     OPTIONS_MAP.put(Integer.valueOf(65536), "oids");
/*  90 */     OPTIONS_MAP.put(Integer.valueOf(128), "reparse");
/*  91 */     OPTIONS_MAP.put(Integer.valueOf(64), "sparse");
/*  92 */     OPTIONS_MAP.put(Integer.valueOf(2097152), "trans");
/*  93 */     OPTIONS_MAP.put(Integer.valueOf(33554432), "journaled");
/*  94 */     OPTIONS_MAP.put(Integer.valueOf(4), "unicode");
/*  95 */     OPTIONS_MAP.put(Integer.valueOf(32768), "vcomp");
/*  96 */     OPTIONS_MAP.put(Integer.valueOf(32), "quota");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 104 */     if (System.getenv("ProgramFiles(x86)") == null) {
/* 105 */       MAX_WINDOWS_HANDLES = 16744448L;
/*     */     } else {
/* 107 */       MAX_WINDOWS_HANDLES = 16711680L;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long MAX_WINDOWS_HANDLES;
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowsFileSystem() {
/* 118 */     Kernel32.INSTANCE.SetErrorMode(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<OSFileStore> getFileStores(boolean localOnly) {
/* 127 */     ArrayList<OSFileStore> result = getLocalVolumes(null);
/*     */ 
/*     */     
/* 130 */     Map<String, OSFileStore> volumeMap = new HashMap<>();
/* 131 */     for (OSFileStore volume : result) {
/* 132 */       volumeMap.put(volume.getMount(), volume);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 137 */     for (OSFileStore wmiVolume : getWmiVolumes(null, localOnly)) {
/* 138 */       if (volumeMap.containsKey(wmiVolume.getMount())) {
/*     */ 
/*     */         
/* 141 */         OSFileStore volume = volumeMap.get(wmiVolume.getMount());
/* 142 */         result.remove(volume);
/* 143 */         result.add(new WindowsOSFileStore(wmiVolume.getName(), volume.getVolume(), 
/* 144 */               volume.getLabel().isEmpty() ? wmiVolume.getLabel() : volume.getLabel(), volume.getMount(), volume
/* 145 */               .getOptions(), volume.getUUID(), "", volume.getDescription(), volume.getType(), volume
/* 146 */               .getFreeSpace(), volume.getUsableSpace(), volume.getTotalSpace(), 0L, 0L)); continue;
/* 147 */       }  if (!localOnly)
/*     */       {
/* 149 */         result.add(wmiVolume);
/*     */       }
/*     */     } 
/* 152 */     return result;
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
/*     */   static ArrayList<OSFileStore> getLocalVolumes(String volumeToMatch) {
/* 180 */     ArrayList<OSFileStore> fs = new ArrayList<>();
/* 181 */     char[] aVolume = new char[255];
/*     */     
/* 183 */     WinNT.HANDLE hVol = Kernel32.INSTANCE.FindFirstVolume(aVolume, 255);
/* 184 */     if (hVol == WinBase.INVALID_HANDLE_VALUE) {
/* 185 */       return fs;
/*     */     }
/*     */     
/*     */     while (true) {
/* 189 */       char[] fstype = new char[16];
/* 190 */       char[] name = new char[255];
/* 191 */       char[] mount = new char[255];
/* 192 */       IntByReference pFlags = new IntByReference();
/*     */       
/* 194 */       WinNT.LARGE_INTEGER userFreeBytes = new WinNT.LARGE_INTEGER(0L);
/* 195 */       WinNT.LARGE_INTEGER totalBytes = new WinNT.LARGE_INTEGER(0L);
/* 196 */       WinNT.LARGE_INTEGER systemFreeBytes = new WinNT.LARGE_INTEGER(0L);
/*     */       
/* 198 */       String volume = (new String(aVolume)).trim();
/* 199 */       Kernel32.INSTANCE.GetVolumeInformation(volume, name, 255, null, null, pFlags, fstype, 16);
/* 200 */       int flags = pFlags.getValue();
/* 201 */       Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(volume, mount, 255, null);
/*     */       
/* 203 */       String strMount = (new String(mount)).trim();
/* 204 */       if (!strMount.isEmpty() && (volumeToMatch == null || volumeToMatch.equals(volume))) {
/* 205 */         String strName = (new String(name)).trim();
/* 206 */         String strFsType = (new String(fstype)).trim();
/*     */         
/* 208 */         StringBuilder options = new StringBuilder(((0x80000 & flags) == 0) ? "rw" : "ro");
/*     */         
/* 210 */         String moreOptions = OPTIONS_MAP.entrySet().stream().filter(e -> ((((Integer)e.getKey()).intValue() & flags) > 0)).map(Map.Entry::getValue).collect(Collectors.joining(","));
/* 211 */         if (!moreOptions.isEmpty()) {
/* 212 */           options.append(',').append(moreOptions);
/*     */         }
/* 214 */         Kernel32.INSTANCE.GetDiskFreeSpaceEx(volume, userFreeBytes, totalBytes, systemFreeBytes);
/*     */         
/* 216 */         String uuid = ParseUtil.parseUuidOrDefault(volume, "");
/*     */         
/* 218 */         fs.add(new WindowsOSFileStore(String.format("%s (%s)", new Object[] { strName, strMount }), volume, strName, strMount, options
/* 219 */               .toString(), uuid, "", getDriveType(strMount), strFsType, systemFreeBytes.getValue(), userFreeBytes
/* 220 */               .getValue(), totalBytes.getValue(), 0L, 0L));
/*     */       } 
/* 222 */       boolean retVal = Kernel32.INSTANCE.FindNextVolume(hVol, aVolume, 255);
/* 223 */       if (!retVal) {
/* 224 */         Kernel32.INSTANCE.FindVolumeClose(hVol);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 229 */         return fs;
/*     */       } 
/*     */     } 
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
/*     */   static List<OSFileStore> getWmiVolumes(String nameToMatch, boolean localOnly) {
/* 245 */     List<OSFileStore> fs = new ArrayList<>();
/* 246 */     WbemcliUtil.WmiResult<Win32LogicalDisk.LogicalDiskProperty> drives = Win32LogicalDisk.queryLogicalDisk(nameToMatch, localOnly);
/* 247 */     for (int i = 0; i < drives.getResultCount(); i++) {
/* 248 */       String volume; long free = WmiUtil.getUint64(drives, (Enum)Win32LogicalDisk.LogicalDiskProperty.FREESPACE, i);
/* 249 */       long total = WmiUtil.getUint64(drives, (Enum)Win32LogicalDisk.LogicalDiskProperty.SIZE, i);
/* 250 */       String description = WmiUtil.getString(drives, (Enum)Win32LogicalDisk.LogicalDiskProperty.DESCRIPTION, i);
/* 251 */       String name = WmiUtil.getString(drives, (Enum)Win32LogicalDisk.LogicalDiskProperty.NAME, i);
/* 252 */       String label = WmiUtil.getString(drives, (Enum)Win32LogicalDisk.LogicalDiskProperty.VOLUMENAME, i);
/* 253 */       String options = (WmiUtil.getUint16(drives, (Enum)Win32LogicalDisk.LogicalDiskProperty.ACCESS, i) == 1) ? "ro" : "rw";
/* 254 */       int type = WmiUtil.getUint32(drives, (Enum)Win32LogicalDisk.LogicalDiskProperty.DRIVETYPE, i);
/*     */       
/* 256 */       if (type != 4) {
/* 257 */         char[] chrVolume = new char[255];
/* 258 */         Kernel32.INSTANCE.GetVolumeNameForVolumeMountPoint(name + "\\", chrVolume, 255);
/* 259 */         volume = (new String(chrVolume)).trim();
/*     */       } else {
/* 261 */         volume = WmiUtil.getString(drives, (Enum)Win32LogicalDisk.LogicalDiskProperty.PROVIDERNAME, i);
/* 262 */         String[] split = volume.split("\\\\");
/* 263 */         if (split.length > 1 && split[split.length - 1].length() > 0) {
/* 264 */           description = split[split.length - 1];
/*     */         }
/*     */       } 
/* 267 */       fs.add(new WindowsOSFileStore(String.format("%s (%s)", new Object[] { description, name }), volume, label, name + "\\", options, "", "", 
/* 268 */             getDriveType(name), WmiUtil.getString(drives, (Enum)Win32LogicalDisk.LogicalDiskProperty.FILESYSTEM, i), free, free, total, 0L, 0L));
/*     */     } 
/*     */     
/* 271 */     return fs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getDriveType(String drive) {
/* 282 */     switch (Kernel32.INSTANCE.GetDriveType(drive)) {
/*     */       case 2:
/* 284 */         return "Removable drive";
/*     */       case 3:
/* 286 */         return "Fixed drive";
/*     */       case 4:
/* 288 */         return "Network drive";
/*     */       case 5:
/* 290 */         return "CD-ROM";
/*     */       case 6:
/* 292 */         return "RAM drive";
/*     */     } 
/* 294 */     return "Unknown drive type";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getOpenFileDescriptors() {
/* 300 */     Map<ProcessInformation.HandleCountProperty, List<Long>> valueListMap = (Map<ProcessInformation.HandleCountProperty, List<Long>>)ProcessInformation.queryHandles().getB();
/* 301 */     List<Long> valueList = valueListMap.get(ProcessInformation.HandleCountProperty.HANDLECOUNT);
/* 302 */     long descriptors = 0L;
/* 303 */     if (valueList != null) {
/* 304 */       for (int i = 0; i < valueList.size(); i++) {
/* 305 */         descriptors += ((Long)valueList.get(i)).longValue();
/*     */       }
/*     */     }
/* 308 */     return descriptors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxFileDescriptors() {
/* 313 */     return MAX_WINDOWS_HANDLES;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\windows\WindowsFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */