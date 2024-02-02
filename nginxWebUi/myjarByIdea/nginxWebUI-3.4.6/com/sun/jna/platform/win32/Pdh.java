package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Pdh extends StdCallLibrary {
   Pdh INSTANCE = (Pdh)Native.load("Pdh", Pdh.class, W32APIOptions.DEFAULT_OPTIONS);
   int PDH_MAX_COUNTER_NAME = 1024;
   int PDH_MAX_INSTANCE_NAME = 1024;
   int PDH_MAX_COUNTER_PATH = 2048;
   int PDH_MAX_DATASOURCE_PATH = 1024;
   int PDH_MORE_DATA = -2147481646;
   int PDH_INSUFFICIENT_BUFFER = -1073738814;
   int PDH_INVALID_ARGUMENT = -1073738819;
   int PDH_MEMORY_ALLOCATION_FAILURE = -1073738821;
   int PDH_CSTATUS_NO_MACHINE = -2147481648;
   int PDH_CSTATUS_NO_OBJECT = -1073738824;
   int PDH_CVERSION_WIN40 = 1024;
   int PDH_CVERSION_WIN50 = 1280;
   int PDH_VERSION = 1283;
   int PDH_PATH_WBEM_RESULT = 1;
   int PDH_PATH_WBEM_INPUT = 2;
   int PDH_FMT_RAW = 16;
   int PDH_FMT_ANSI = 32;
   int PDH_FMT_UNICODE = 64;
   int PDH_FMT_LONG = 256;
   int PDH_FMT_DOUBLE = 512;
   int PDH_FMT_LARGE = 1024;
   int PDH_FMT_NOSCALE = 4096;
   int PDH_FMT_1000 = 8192;
   int PDH_FMT_NODATA = 16384;
   int PDH_FMT_NOCAP100 = 32768;
   int PERF_DETAIL_COSTLY = 65536;
   int PERF_DETAIL_STANDARD = 65535;

   int PdhConnectMachine(String var1);

   int PdhGetDllVersion(WinDef.DWORDByReference var1);

   int PdhOpenQuery(String var1, BaseTSD.DWORD_PTR var2, WinNT.HANDLEByReference var3);

   int PdhCloseQuery(WinNT.HANDLE var1);

   int PdhMakeCounterPath(PDH_COUNTER_PATH_ELEMENTS var1, char[] var2, WinDef.DWORDByReference var3, int var4);

   int PdhAddCounter(WinNT.HANDLE var1, String var2, BaseTSD.DWORD_PTR var3, WinNT.HANDLEByReference var4);

   int PdhAddEnglishCounter(WinNT.HANDLE var1, String var2, BaseTSD.DWORD_PTR var3, WinNT.HANDLEByReference var4);

   int PdhRemoveCounter(WinNT.HANDLE var1);

   int PdhGetRawCounterValue(WinNT.HANDLE var1, WinDef.DWORDByReference var2, PDH_RAW_COUNTER var3);

   int PdhValidatePath(String var1);

   int PdhCollectQueryData(WinNT.HANDLE var1);

   int PdhCollectQueryDataEx(WinNT.HANDLE var1, int var2, WinNT.HANDLE var3);

   int PdhCollectQueryDataWithTime(WinNT.HANDLE var1, WinDef.LONGLONGByReference var2);

   int PdhSetQueryTimeRange(WinNT.HANDLE var1, PDH_TIME_INFO var2);

   int PdhEnumObjectItems(String var1, String var2, String var3, Pointer var4, WinDef.DWORDByReference var5, Pointer var6, WinDef.DWORDByReference var7, int var8, int var9);

   int PdhLookupPerfIndexByName(String var1, String var2, WinDef.DWORDByReference var3);

   int PdhLookupPerfNameByIndex(String var1, int var2, Pointer var3, WinDef.DWORDByReference var4);

   @Structure.FieldOrder({"StartTime", "EndTime", "SampleCount"})
   public static class PDH_TIME_INFO extends Structure {
      public long StartTime;
      public long EndTime;
      public int SampleCount;
   }

   @Structure.FieldOrder({"CStatus", "TimeStamp", "FirstValue", "SecondValue", "MultiCount"})
   public static class PDH_RAW_COUNTER extends Structure {
      public int CStatus;
      public WinBase.FILETIME TimeStamp = new WinBase.FILETIME();
      public long FirstValue;
      public long SecondValue;
      public int MultiCount;
   }

   @Structure.FieldOrder({"szMachineName", "szObjectName", "szInstanceName", "szParentInstance", "dwInstanceIndex", "szCounterName"})
   public static class PDH_COUNTER_PATH_ELEMENTS extends Structure {
      public String szMachineName;
      public String szObjectName;
      public String szInstanceName;
      public String szParentInstance;
      public int dwInstanceIndex;
      public String szCounterName;
   }
}
