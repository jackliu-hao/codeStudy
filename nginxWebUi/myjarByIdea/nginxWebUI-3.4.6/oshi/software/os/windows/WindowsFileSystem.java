package oshi.software.os.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.ProcessInformation;
import oshi.driver.windows.wmi.Win32LogicalDisk;
import oshi.software.common.AbstractFileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
public class WindowsFileSystem extends AbstractFileSystem {
   private static final int BUFSIZE = 255;
   private static final int SEM_FAILCRITICALERRORS = 1;
   private static final int FILE_CASE_SENSITIVE_SEARCH = 1;
   private static final int FILE_CASE_PRESERVED_NAMES = 2;
   private static final int FILE_FILE_COMPRESSION = 16;
   private static final int FILE_DAX_VOLUME = 536870912;
   private static final int FILE_NAMED_STREAMS = 262144;
   private static final int FILE_PERSISTENT_ACLS = 8;
   private static final int FILE_READ_ONLY_VOLUME = 524288;
   private static final int FILE_SEQUENTIAL_WRITE_ONCE = 1048576;
   private static final int FILE_SUPPORTS_ENCRYPTION = 131072;
   private static final int FILE_SUPPORTS_OBJECT_IDS = 65536;
   private static final int FILE_SUPPORTS_REPARSE_POINTS = 128;
   private static final int FILE_SUPPORTS_SPARSE_FILES = 64;
   private static final int FILE_SUPPORTS_TRANSACTIONS = 2097152;
   private static final int FILE_SUPPORTS_USN_JOURNAL = 33554432;
   private static final int FILE_UNICODE_ON_DISK = 4;
   private static final int FILE_VOLUME_IS_COMPRESSED = 32768;
   private static final int FILE_VOLUME_QUOTAS = 32;
   private static final Map<Integer, String> OPTIONS_MAP = new HashMap();
   private static final long MAX_WINDOWS_HANDLES;

   public WindowsFileSystem() {
      Kernel32.INSTANCE.SetErrorMode(1);
   }

   public List<OSFileStore> getFileStores(boolean localOnly) {
      ArrayList<OSFileStore> result = getLocalVolumes((String)null);
      Map<String, OSFileStore> volumeMap = new HashMap();
      Iterator var4 = result.iterator();

      OSFileStore wmiVolume;
      while(var4.hasNext()) {
         wmiVolume = (OSFileStore)var4.next();
         volumeMap.put(wmiVolume.getMount(), wmiVolume);
      }

      var4 = getWmiVolumes((String)null, localOnly).iterator();

      while(var4.hasNext()) {
         wmiVolume = (OSFileStore)var4.next();
         if (volumeMap.containsKey(wmiVolume.getMount())) {
            OSFileStore volume = (OSFileStore)volumeMap.get(wmiVolume.getMount());
            result.remove(volume);
            result.add(new WindowsOSFileStore(wmiVolume.getName(), volume.getVolume(), volume.getLabel().isEmpty() ? wmiVolume.getLabel() : volume.getLabel(), volume.getMount(), volume.getOptions(), volume.getUUID(), "", volume.getDescription(), volume.getType(), volume.getFreeSpace(), volume.getUsableSpace(), volume.getTotalSpace(), 0L, 0L));
         } else if (!localOnly) {
            result.add(wmiVolume);
         }
      }

      return result;
   }

   static ArrayList<OSFileStore> getLocalVolumes(String volumeToMatch) {
      ArrayList<OSFileStore> fs = new ArrayList();
      char[] aVolume = new char[255];
      WinNT.HANDLE hVol = Kernel32.INSTANCE.FindFirstVolume(aVolume, 255);
      if (hVol == WinBase.INVALID_HANDLE_VALUE) {
         return fs;
      } else {
         boolean retVal;
         do {
            char[] fstype = new char[16];
            char[] name = new char[255];
            char[] mount = new char[255];
            IntByReference pFlags = new IntByReference();
            WinNT.LARGE_INTEGER userFreeBytes = new WinNT.LARGE_INTEGER(0L);
            WinNT.LARGE_INTEGER totalBytes = new WinNT.LARGE_INTEGER(0L);
            WinNT.LARGE_INTEGER systemFreeBytes = new WinNT.LARGE_INTEGER(0L);
            String volume = (new String(aVolume)).trim();
            Kernel32.INSTANCE.GetVolumeInformation(volume, name, 255, (IntByReference)null, (IntByReference)null, pFlags, fstype, 16);
            int flags = pFlags.getValue();
            Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(volume, mount, 255, (IntByReference)null);
            String strMount = (new String(mount)).trim();
            if (!strMount.isEmpty() && (volumeToMatch == null || volumeToMatch.equals(volume))) {
               String strName = (new String(name)).trim();
               String strFsType = (new String(fstype)).trim();
               StringBuilder options = new StringBuilder((524288 & flags) == 0 ? "rw" : "ro");
               String moreOptions = (String)OPTIONS_MAP.entrySet().stream().filter((e) -> {
                  return ((Integer)e.getKey() & flags) > 0;
               }).map(Map.Entry::getValue).collect(Collectors.joining(","));
               if (!moreOptions.isEmpty()) {
                  options.append(',').append(moreOptions);
               }

               Kernel32.INSTANCE.GetDiskFreeSpaceEx(volume, userFreeBytes, totalBytes, systemFreeBytes);
               String uuid = ParseUtil.parseUuidOrDefault(volume, "");
               fs.add(new WindowsOSFileStore(String.format("%s (%s)", strName, strMount), volume, strName, strMount, options.toString(), uuid, "", getDriveType(strMount), strFsType, systemFreeBytes.getValue(), userFreeBytes.getValue(), totalBytes.getValue(), 0L, 0L));
            }

            retVal = Kernel32.INSTANCE.FindNextVolume(hVol, aVolume, 255);
         } while(retVal);

         Kernel32.INSTANCE.FindVolumeClose(hVol);
         return fs;
      }
   }

   static List<OSFileStore> getWmiVolumes(String nameToMatch, boolean localOnly) {
      List<OSFileStore> fs = new ArrayList();
      WbemcliUtil.WmiResult<Win32LogicalDisk.LogicalDiskProperty> drives = Win32LogicalDisk.queryLogicalDisk(nameToMatch, localOnly);

      for(int i = 0; i < drives.getResultCount(); ++i) {
         long free = WmiUtil.getUint64(drives, Win32LogicalDisk.LogicalDiskProperty.FREESPACE, i);
         long total = WmiUtil.getUint64(drives, Win32LogicalDisk.LogicalDiskProperty.SIZE, i);
         String description = WmiUtil.getString(drives, Win32LogicalDisk.LogicalDiskProperty.DESCRIPTION, i);
         String name = WmiUtil.getString(drives, Win32LogicalDisk.LogicalDiskProperty.NAME, i);
         String label = WmiUtil.getString(drives, Win32LogicalDisk.LogicalDiskProperty.VOLUMENAME, i);
         String options = WmiUtil.getUint16(drives, Win32LogicalDisk.LogicalDiskProperty.ACCESS, i) == 1 ? "ro" : "rw";
         int type = WmiUtil.getUint32(drives, Win32LogicalDisk.LogicalDiskProperty.DRIVETYPE, i);
         String volume;
         if (type != 4) {
            char[] chrVolume = new char[255];
            Kernel32.INSTANCE.GetVolumeNameForVolumeMountPoint(name + "\\", chrVolume, 255);
            volume = (new String(chrVolume)).trim();
         } else {
            volume = WmiUtil.getString(drives, Win32LogicalDisk.LogicalDiskProperty.PROVIDERNAME, i);
            String[] split = volume.split("\\\\");
            if (split.length > 1 && split[split.length - 1].length() > 0) {
               description = split[split.length - 1];
            }
         }

         fs.add(new WindowsOSFileStore(String.format("%s (%s)", description, name), volume, label, name + "\\", options, "", "", getDriveType(name), WmiUtil.getString(drives, Win32LogicalDisk.LogicalDiskProperty.FILESYSTEM, i), free, free, total, 0L, 0L));
      }

      return fs;
   }

   private static String getDriveType(String drive) {
      switch (Kernel32.INSTANCE.GetDriveType(drive)) {
         case 2:
            return "Removable drive";
         case 3:
            return "Fixed drive";
         case 4:
            return "Network drive";
         case 5:
            return "CD-ROM";
         case 6:
            return "RAM drive";
         default:
            return "Unknown drive type";
      }
   }

   public long getOpenFileDescriptors() {
      Map<ProcessInformation.HandleCountProperty, List<Long>> valueListMap = (Map)ProcessInformation.queryHandles().getB();
      List<Long> valueList = (List)valueListMap.get(ProcessInformation.HandleCountProperty.HANDLECOUNT);
      long descriptors = 0L;
      if (valueList != null) {
         for(int i = 0; i < valueList.size(); ++i) {
            descriptors += (Long)valueList.get(i);
         }
      }

      return descriptors;
   }

   public long getMaxFileDescriptors() {
      return MAX_WINDOWS_HANDLES;
   }

   static {
      OPTIONS_MAP.put(2, "casepn");
      OPTIONS_MAP.put(1, "casess");
      OPTIONS_MAP.put(16, "fcomp");
      OPTIONS_MAP.put(536870912, "dax");
      OPTIONS_MAP.put(262144, "streams");
      OPTIONS_MAP.put(8, "acls");
      OPTIONS_MAP.put(1048576, "wronce");
      OPTIONS_MAP.put(131072, "efs");
      OPTIONS_MAP.put(65536, "oids");
      OPTIONS_MAP.put(128, "reparse");
      OPTIONS_MAP.put(64, "sparse");
      OPTIONS_MAP.put(2097152, "trans");
      OPTIONS_MAP.put(33554432, "journaled");
      OPTIONS_MAP.put(4, "unicode");
      OPTIONS_MAP.put(32768, "vcomp");
      OPTIONS_MAP.put(32, "quota");
      if (System.getenv("ProgramFiles(x86)") == null) {
         MAX_WINDOWS_HANDLES = 16744448L;
      } else {
         MAX_WINDOWS_HANDLES = 16711680L;
      }

   }
}
