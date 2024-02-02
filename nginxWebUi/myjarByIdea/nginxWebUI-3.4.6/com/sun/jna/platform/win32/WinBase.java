package com.sun.jna.platform.win32;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public interface WinBase extends WinDef, BaseTSD {
   WinNT.HANDLE INVALID_HANDLE_VALUE = new WinNT.HANDLE(Pointer.createConstant(Native.POINTER_SIZE == 8 ? -1L : 4294967295L));
   int WAIT_FAILED = -1;
   int WAIT_OBJECT_0 = 0;
   int WAIT_ABANDONED = 128;
   int WAIT_ABANDONED_0 = 128;
   int MAX_COMPUTERNAME_LENGTH = Platform.isMac() ? 15 : 31;
   int LOGON32_LOGON_INTERACTIVE = 2;
   int LOGON32_LOGON_NETWORK = 3;
   int LOGON32_LOGON_BATCH = 4;
   int LOGON32_LOGON_SERVICE = 5;
   int LOGON32_LOGON_UNLOCK = 7;
   int LOGON32_LOGON_NETWORK_CLEARTEXT = 8;
   int LOGON32_LOGON_NEW_CREDENTIALS = 9;
   int LOGON32_PROVIDER_DEFAULT = 0;
   int LOGON32_PROVIDER_WINNT35 = 1;
   int LOGON32_PROVIDER_WINNT40 = 2;
   int LOGON32_PROVIDER_WINNT50 = 3;
   int HANDLE_FLAG_INHERIT = 1;
   int HANDLE_FLAG_PROTECT_FROM_CLOSE = 2;
   int STARTF_USESHOWWINDOW = 1;
   int STARTF_USESIZE = 2;
   int STARTF_USEPOSITION = 4;
   int STARTF_USECOUNTCHARS = 8;
   int STARTF_USEFILLATTRIBUTE = 16;
   int STARTF_RUNFULLSCREEN = 32;
   int STARTF_FORCEONFEEDBACK = 64;
   int STARTF_FORCEOFFFEEDBACK = 128;
   int STARTF_USESTDHANDLES = 256;
   int DEBUG_PROCESS = 1;
   int DEBUG_ONLY_THIS_PROCESS = 2;
   int CREATE_SUSPENDED = 4;
   int DETACHED_PROCESS = 8;
   int CREATE_NEW_CONSOLE = 16;
   int CREATE_NEW_PROCESS_GROUP = 512;
   int CREATE_UNICODE_ENVIRONMENT = 1024;
   int CREATE_SEPARATE_WOW_VDM = 2048;
   int CREATE_SHARED_WOW_VDM = 4096;
   int CREATE_FORCEDOS = 8192;
   int INHERIT_PARENT_AFFINITY = 65536;
   int CREATE_PROTECTED_PROCESS = 262144;
   int EXTENDED_STARTUPINFO_PRESENT = 524288;
   int CREATE_BREAKAWAY_FROM_JOB = 16777216;
   int CREATE_PRESERVE_CODE_AUTHZ_LEVEL = 33554432;
   int CREATE_DEFAULT_ERROR_MODE = 67108864;
   int CREATE_NO_WINDOW = 134217728;
   int FILE_ENCRYPTABLE = 0;
   int FILE_IS_ENCRYPTED = 1;
   int FILE_SYSTEM_ATTR = 2;
   int FILE_ROOT_DIR = 3;
   int FILE_SYSTEM_DIR = 4;
   int FILE_UNKNOWN = 5;
   int FILE_SYSTEM_NOT_SUPPORT = 6;
   int FILE_USER_DISALLOWED = 7;
   int FILE_READ_ONLY = 8;
   int FILE_DIR_DISALOWED = 9;
   int CREATE_FOR_IMPORT = 1;
   int CREATE_FOR_DIR = 2;
   int OVERWRITE_HIDDEN = 4;
   int INVALID_FILE_SIZE = -1;
   int INVALID_SET_FILE_POINTER = -1;
   int INVALID_FILE_ATTRIBUTES = -1;
   int STILL_ACTIVE = 259;
   int FileBasicInfo = 0;
   int FileStandardInfo = 1;
   int FileNameInfo = 2;
   int FileRenameInfo = 3;
   int FileDispositionInfo = 4;
   int FileAllocationInfo = 5;
   int FileEndOfFileInfo = 6;
   int FileStreamInfo = 7;
   int FileCompressionInfo = 8;
   int FileAttributeTagInfo = 9;
   int FileIdBothDirectoryInfo = 10;
   int FileIdBothDirectoryRestartInfo = 11;
   int FileIoPriorityHintInfo = 12;
   int FileRemoteProtocolInfo = 13;
   int FileFullDirectoryInfo = 14;
   int FileFullDirectoryRestartInfo = 15;
   int FileStorageInfo = 16;
   int FileAlignmentInfo = 17;
   int FileIdInfo = 18;
   int FileIdExtdDirectoryInfo = 19;
   int FileIdExtdDirectoryRestartInfo = 20;
   int FindExInfoStandard = 0;
   int FindExInfoBasic = 1;
   int FindExInfoMaxInfoLevel = 2;
   int FindExSearchNameMatch = 0;
   int FindExSearchLimitToDirectories = 1;
   int FindExSearchLimitToDevices = 2;
   int LMEM_FIXED = 0;
   int LMEM_MOVEABLE = 2;
   int LMEM_NOCOMPACT = 16;
   int LMEM_NODISCARD = 32;
   int LMEM_ZEROINIT = 64;
   int LMEM_MODIFY = 128;
   int LMEM_DISCARDABLE = 3840;
   int LMEM_VALID_FLAGS = 3954;
   int LMEM_INVALID_HANDLE = 32768;
   int LHND = 66;
   int LPTR = 64;
   int LMEM_DISCARDED = 16384;
   int LMEM_LOCKCOUNT = 255;
   int FORMAT_MESSAGE_ALLOCATE_BUFFER = 256;
   int FORMAT_MESSAGE_IGNORE_INSERTS = 512;
   int FORMAT_MESSAGE_FROM_STRING = 1024;
   int FORMAT_MESSAGE_FROM_HMODULE = 2048;
   int FORMAT_MESSAGE_FROM_SYSTEM = 4096;
   int FORMAT_MESSAGE_ARGUMENT_ARRAY = 8192;
   int DRIVE_UNKNOWN = 0;
   int DRIVE_NO_ROOT_DIR = 1;
   int DRIVE_REMOVABLE = 2;
   int DRIVE_FIXED = 3;
   int DRIVE_REMOTE = 4;
   int DRIVE_CDROM = 5;
   int DRIVE_RAMDISK = 6;
   int INFINITE = -1;
   int MOVEFILE_COPY_ALLOWED = 2;
   int MOVEFILE_CREATE_HARDLINK = 16;
   int MOVEFILE_DELAY_UNTIL_REBOOT = 4;
   int MOVEFILE_FAIL_IF_NOT_TRACKABLE = 32;
   int MOVEFILE_REPLACE_EXISTING = 1;
   int MOVEFILE_WRITE_THROUGH = 8;
   int PIPE_CLIENT_END = 0;
   int PIPE_SERVER_END = 1;
   int PIPE_ACCESS_DUPLEX = 3;
   int PIPE_ACCESS_INBOUND = 1;
   int PIPE_ACCESS_OUTBOUND = 2;
   int PIPE_TYPE_BYTE = 0;
   int PIPE_TYPE_MESSAGE = 4;
   int PIPE_READMODE_BYTE = 0;
   int PIPE_READMODE_MESSAGE = 2;
   int PIPE_WAIT = 0;
   int PIPE_NOWAIT = 1;
   int PIPE_ACCEPT_REMOTE_CLIENTS = 0;
   int PIPE_REJECT_REMOTE_CLIENTS = 8;
   int PIPE_UNLIMITED_INSTANCES = 255;
   int NMPWAIT_USE_DEFAULT_WAIT = 0;
   int NMPWAIT_NOWAIT = 1;
   int NMPWAIT_WAIT_FOREVER = -1;
   int NOPARITY = 0;
   int ODDPARITY = 1;
   int EVENPARITY = 2;
   int MARKPARITY = 3;
   int SPACEPARITY = 4;
   int ONESTOPBIT = 0;
   int ONE5STOPBITS = 1;
   int TWOSTOPBITS = 2;
   int CBR_110 = 110;
   int CBR_300 = 300;
   int CBR_600 = 600;
   int CBR_1200 = 1200;
   int CBR_2400 = 2400;
   int CBR_4800 = 4800;
   int CBR_9600 = 9600;
   int CBR_14400 = 14400;
   int CBR_19200 = 19200;
   int CBR_38400 = 38400;
   int CBR_56000 = 56000;
   int CBR_128000 = 128000;
   int CBR_256000 = 256000;
   int DTR_CONTROL_DISABLE = 0;
   int DTR_CONTROL_ENABLE = 1;
   int DTR_CONTROL_HANDSHAKE = 2;
   int RTS_CONTROL_DISABLE = 0;
   int RTS_CONTROL_ENABLE = 1;
   int RTS_CONTROL_HANDSHAKE = 2;
   int RTS_CONTROL_TOGGLE = 3;
   int ES_AWAYMODE_REQUIRED = 64;
   int ES_CONTINUOUS = Integer.MIN_VALUE;
   int ES_DISPLAY_REQUIRED = 2;
   int ES_SYSTEM_REQUIRED = 1;
   int ES_USER_PRESENT = 4;
   int MUTEX_MODIFY_STATE = 1;
   int MUTEX_ALL_ACCESS = 2031617;

   public interface EnumResNameProc extends Callback {
      boolean invoke(WinDef.HMODULE var1, Pointer var2, Pointer var3, Pointer var4);
   }

   public interface EnumResTypeProc extends Callback {
      boolean invoke(WinDef.HMODULE var1, Pointer var2, Pointer var3);
   }

   @Structure.FieldOrder({"DCBlength", "BaudRate", "controllBits", "wReserved", "XonLim", "XoffLim", "ByteSize", "Parity", "StopBits", "XonChar", "XoffChar", "ErrorChar", "EofChar", "EvtChar", "wReserved1"})
   public static class DCB extends Structure {
      public WinDef.DWORD DCBlength = new WinDef.DWORD((long)this.size());
      public WinDef.DWORD BaudRate;
      public DCBControllBits controllBits;
      public WinDef.WORD wReserved;
      public WinDef.WORD XonLim;
      public WinDef.WORD XoffLim;
      public WinDef.BYTE ByteSize;
      public WinDef.BYTE Parity;
      public WinDef.BYTE StopBits;
      public char XonChar;
      public char XoffChar;
      public char ErrorChar;
      public char EofChar;
      public char EvtChar;
      public WinDef.WORD wReserved1;

      public static class DCBControllBits extends WinDef.DWORD {
         private static final long serialVersionUID = 8574966619718078579L;

         public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('<');
            stringBuilder.append("fBinary:1=");
            stringBuilder.append((char)(this.getfBinary() ? '1' : '0'));
            stringBuilder.append(", fParity:1=");
            stringBuilder.append((char)(this.getfParity() ? '1' : '0'));
            stringBuilder.append(", fOutxCtsFlow:1=");
            stringBuilder.append((char)(this.getfOutxCtsFlow() ? '1' : '0'));
            stringBuilder.append(", fOutxDsrFlow:1=");
            stringBuilder.append((char)(this.getfOutxDsrFlow() ? '1' : '0'));
            stringBuilder.append(", fDtrControl:2=");
            stringBuilder.append(this.getfDtrControl());
            stringBuilder.append(", fDsrSensitivity:1=");
            stringBuilder.append((char)(this.getfDsrSensitivity() ? '1' : '0'));
            stringBuilder.append(", fTXContinueOnXoff:1=");
            stringBuilder.append((char)(this.getfTXContinueOnXoff() ? '1' : '0'));
            stringBuilder.append(", fOutX:1=");
            stringBuilder.append((char)(this.getfOutX() ? '1' : '0'));
            stringBuilder.append(", fInX:1=");
            stringBuilder.append((char)(this.getfInX() ? '1' : '0'));
            stringBuilder.append(", fErrorChar:1=");
            stringBuilder.append((char)(this.getfErrorChar() ? '1' : '0'));
            stringBuilder.append(", fNull:1=");
            stringBuilder.append((char)(this.getfNull() ? '1' : '0'));
            stringBuilder.append(", fRtsControl:2=");
            stringBuilder.append(this.getfRtsControl());
            stringBuilder.append(", fAbortOnError:1=");
            stringBuilder.append((char)(this.getfAbortOnError() ? '1' : '0'));
            stringBuilder.append(", fDummy2:17=");
            stringBuilder.append(this.getfDummy2());
            stringBuilder.append('>');
            return stringBuilder.toString();
         }

         public boolean getfAbortOnError() {
            return (this.intValue() & 16384) != 0;
         }

         public boolean getfBinary() {
            return (this.intValue() & 1) != 0;
         }

         public boolean getfDsrSensitivity() {
            return (this.intValue() & 64) != 0;
         }

         public int getfDtrControl() {
            return this.intValue() >>> 4 & 3;
         }

         public boolean getfErrorChar() {
            return (this.intValue() & 1024) != 0;
         }

         public boolean getfInX() {
            return (this.intValue() & 512) != 0;
         }

         public boolean getfNull() {
            return (this.intValue() & 2048) != 0;
         }

         public boolean getfOutX() {
            return (this.intValue() & 256) != 0;
         }

         public boolean getfOutxCtsFlow() {
            return (this.intValue() & 4) != 0;
         }

         public boolean getfOutxDsrFlow() {
            return (this.intValue() & 8) != 0;
         }

         public boolean getfParity() {
            return (this.intValue() & 2) != 0;
         }

         public int getfRtsControl() {
            return this.intValue() >>> 12 & 3;
         }

         public int getfDummy2() {
            return this.intValue() >>> 15 & 131071;
         }

         public boolean getfTXContinueOnXoff() {
            return (this.intValue() & 128) != 0;
         }

         public void setfAbortOnError(boolean fAbortOnError) {
            int tmp = leftShiftMask(fAbortOnError ? 1 : 0, (byte)14, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfBinary(boolean fBinary) {
            int tmp = leftShiftMask(fBinary ? 1 : 0, (byte)0, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfDsrSensitivity(boolean fDsrSensitivity) {
            int tmp = leftShiftMask(fDsrSensitivity ? 1 : 0, (byte)6, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfDtrControl(int fOutxDsrFlow) {
            int tmp = leftShiftMask(fOutxDsrFlow, (byte)4, 3, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfErrorChar(boolean fErrorChar) {
            int tmp = leftShiftMask(fErrorChar ? 1 : 0, (byte)10, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfInX(boolean fInX) {
            int tmp = leftShiftMask(fInX ? 1 : 0, (byte)9, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfNull(boolean fNull) {
            int tmp = leftShiftMask(fNull ? 1 : 0, (byte)11, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfOutX(boolean fOutX) {
            int tmp = leftShiftMask(fOutX ? 1 : 0, (byte)8, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfOutxCtsFlow(boolean fOutxCtsFlow) {
            int tmp = leftShiftMask(fOutxCtsFlow ? 1 : 0, (byte)2, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfOutxDsrFlow(boolean fOutxDsrFlow) {
            int tmp = leftShiftMask(fOutxDsrFlow ? 1 : 0, (byte)3, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfParity(boolean fParity) {
            int tmp = leftShiftMask(fParity ? 1 : 0, (byte)1, 1, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfRtsControl(int fRtsControl) {
            int tmp = leftShiftMask(fRtsControl, (byte)12, 3, this.intValue());
            this.setValue((long)tmp);
         }

         public void setfTXContinueOnXoff(boolean fTXContinueOnXoff) {
            int tmp = leftShiftMask(fTXContinueOnXoff ? 1 : 0, (byte)7, 1, this.intValue());
            this.setValue((long)tmp);
         }

         private static int leftShiftMask(int valuetoset, byte shift, int mask, int storage) {
            int tmp = storage & ~(mask << shift);
            tmp |= (valuetoset & mask) << shift;
            return tmp;
         }
      }
   }

   @Structure.FieldOrder({"ReadIntervalTimeout", "ReadTotalTimeoutMultiplier", "ReadTotalTimeoutConstant", "WriteTotalTimeoutMultiplier", "WriteTotalTimeoutConstant"})
   public static class COMMTIMEOUTS extends Structure {
      public WinDef.DWORD ReadIntervalTimeout;
      public WinDef.DWORD ReadTotalTimeoutMultiplier;
      public WinDef.DWORD ReadTotalTimeoutConstant;
      public WinDef.DWORD WriteTotalTimeoutMultiplier;
      public WinDef.DWORD WriteTotalTimeoutConstant;
   }

   public interface FE_IMPORT_FUNC extends StdCallLibrary.StdCallCallback {
      WinDef.DWORD callback(Pointer var1, Pointer var2, WinDef.ULONGByReference var3);
   }

   public interface FE_EXPORT_FUNC extends StdCallLibrary.StdCallCallback {
      WinDef.DWORD callback(Pointer var1, Pointer var2, WinDef.ULONG var3);
   }

   public interface COMPUTER_NAME_FORMAT {
      int ComputerNameNetBIOS = 0;
      int ComputerNameDnsHostname = 1;
      int ComputerNameDnsDomain = 2;
      int ComputerNameDnsFullyQualified = 3;
      int ComputerNamePhysicalNetBIOS = 4;
      int ComputerNamePhysicalDnsHostname = 5;
      int ComputerNamePhysicalDnsDomain = 6;
      int ComputerNamePhysicalDnsFullyQualified = 7;
      int ComputerNameMax = 8;
   }

   @Structure.FieldOrder({"foreignLocation"})
   public static class FOREIGN_THREAD_START_ROUTINE extends Structure {
      public WinDef.LPVOID foreignLocation;
   }

   public interface THREAD_START_ROUTINE extends StdCallLibrary.StdCallCallback {
      WinDef.DWORD apply(WinDef.LPVOID var1);
   }

   @Structure.FieldOrder({"hProcess", "hThread", "dwProcessId", "dwThreadId"})
   public static class PROCESS_INFORMATION extends Structure {
      public WinNT.HANDLE hProcess;
      public WinNT.HANDLE hThread;
      public WinDef.DWORD dwProcessId;
      public WinDef.DWORD dwThreadId;

      public PROCESS_INFORMATION() {
      }

      public PROCESS_INFORMATION(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends PROCESS_INFORMATION implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"cb", "lpReserved", "lpDesktop", "lpTitle", "dwX", "dwY", "dwXSize", "dwYSize", "dwXCountChars", "dwYCountChars", "dwFillAttribute", "dwFlags", "wShowWindow", "cbReserved2", "lpReserved2", "hStdInput", "hStdOutput", "hStdError"})
   public static class STARTUPINFO extends Structure {
      public WinDef.DWORD cb = new WinDef.DWORD((long)this.size());
      public String lpReserved;
      public String lpDesktop;
      public String lpTitle;
      public WinDef.DWORD dwX;
      public WinDef.DWORD dwY;
      public WinDef.DWORD dwXSize;
      public WinDef.DWORD dwYSize;
      public WinDef.DWORD dwXCountChars;
      public WinDef.DWORD dwYCountChars;
      public WinDef.DWORD dwFillAttribute;
      public int dwFlags;
      public WinDef.WORD wShowWindow;
      public WinDef.WORD cbReserved2;
      public ByteByReference lpReserved2;
      public WinNT.HANDLE hStdInput;
      public WinNT.HANDLE hStdOutput;
      public WinNT.HANDLE hStdError;

      public STARTUPINFO() {
         super(W32APITypeMapper.DEFAULT);
      }
   }

   @Structure.FieldOrder({"dwLength", "lpSecurityDescriptor", "bInheritHandle"})
   public static class SECURITY_ATTRIBUTES extends Structure {
      public WinDef.DWORD dwLength = new WinDef.DWORD((long)this.size());
      public Pointer lpSecurityDescriptor;
      public boolean bInheritHandle;
   }

   @Structure.FieldOrder({"dwLength", "dwMemoryLoad", "ullTotalPhys", "ullAvailPhys", "ullTotalPageFile", "ullAvailPageFile", "ullTotalVirtual", "ullAvailVirtual", "ullAvailExtendedVirtual"})
   public static class MEMORYSTATUSEX extends Structure {
      public WinDef.DWORD dwLength = new WinDef.DWORD((long)this.size());
      public WinDef.DWORD dwMemoryLoad;
      public WinDef.DWORDLONG ullTotalPhys;
      public WinDef.DWORDLONG ullAvailPhys;
      public WinDef.DWORDLONG ullTotalPageFile;
      public WinDef.DWORDLONG ullAvailPageFile;
      public WinDef.DWORDLONG ullTotalVirtual;
      public WinDef.DWORDLONG ullAvailVirtual;
      public WinDef.DWORDLONG ullAvailExtendedVirtual;
   }

   @Structure.FieldOrder({"processorArchitecture", "dwPageSize", "lpMinimumApplicationAddress", "lpMaximumApplicationAddress", "dwActiveProcessorMask", "dwNumberOfProcessors", "dwProcessorType", "dwAllocationGranularity", "wProcessorLevel", "wProcessorRevision"})
   public static class SYSTEM_INFO extends Structure {
      public UNION processorArchitecture;
      public WinDef.DWORD dwPageSize;
      public Pointer lpMinimumApplicationAddress;
      public Pointer lpMaximumApplicationAddress;
      public BaseTSD.DWORD_PTR dwActiveProcessorMask;
      public WinDef.DWORD dwNumberOfProcessors;
      public WinDef.DWORD dwProcessorType;
      public WinDef.DWORD dwAllocationGranularity;
      public WinDef.WORD wProcessorLevel;
      public WinDef.WORD wProcessorRevision;

      public static class UNION extends Union {
         public WinDef.DWORD dwOemID;
         public PI pi;

         public void read() {
            this.setType("dwOemID");
            super.read();
            this.setType("pi");
            super.read();
         }

         public static class ByReference extends UNION implements Structure.ByReference {
         }
      }

      @Structure.FieldOrder({"wProcessorArchitecture", "wReserved"})
      public static class PI extends Structure {
         public WinDef.WORD wProcessorArchitecture;
         public WinDef.WORD wReserved;

         public static class ByReference extends PI implements Structure.ByReference {
         }
      }
   }

   @Structure.FieldOrder({"Internal", "InternalHigh", "Offset", "OffsetHigh", "hEvent"})
   public static class OVERLAPPED extends Structure {
      public BaseTSD.ULONG_PTR Internal;
      public BaseTSD.ULONG_PTR InternalHigh;
      public int Offset;
      public int OffsetHigh;
      public WinNT.HANDLE hEvent;
   }

   @Structure.FieldOrder({"Bias", "StandardName", "StandardDate", "StandardBias", "DaylightName", "DaylightDate", "DaylightBias"})
   public static class TIME_ZONE_INFORMATION extends Structure {
      public WinDef.LONG Bias;
      public String StandardName;
      public SYSTEMTIME StandardDate;
      public WinDef.LONG StandardBias;
      public String DaylightName;
      public SYSTEMTIME DaylightDate;
      public WinDef.LONG DaylightBias;

      public TIME_ZONE_INFORMATION() {
         super(W32APITypeMapper.DEFAULT);
      }
   }

   @Structure.FieldOrder({"wYear", "wMonth", "wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond", "wMilliseconds"})
   public static class SYSTEMTIME extends Structure {
      public short wYear;
      public short wMonth;
      public short wDayOfWeek;
      public short wDay;
      public short wHour;
      public short wMinute;
      public short wSecond;
      public short wMilliseconds;

      public SYSTEMTIME() {
      }

      public SYSTEMTIME(Date date) {
         this(date.getTime());
      }

      public SYSTEMTIME(long timestamp) {
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(timestamp);
         this.fromCalendar(cal);
      }

      public SYSTEMTIME(Calendar cal) {
         this.fromCalendar(cal);
      }

      public void fromCalendar(Calendar cal) {
         this.wYear = (short)cal.get(1);
         this.wMonth = (short)(1 + cal.get(2) - 0);
         this.wDay = (short)cal.get(5);
         this.wHour = (short)cal.get(11);
         this.wMinute = (short)cal.get(12);
         this.wSecond = (short)cal.get(13);
         this.wMilliseconds = (short)cal.get(14);
         this.wDayOfWeek = (short)(cal.get(7) - 1);
      }

      public Calendar toCalendar() {
         Calendar cal = Calendar.getInstance();
         cal.set(1, this.wYear);
         cal.set(2, 0 + (this.wMonth - 1));
         cal.set(5, this.wDay);
         cal.set(11, this.wHour);
         cal.set(12, this.wMinute);
         cal.set(13, this.wSecond);
         cal.set(14, this.wMilliseconds);
         return cal;
      }

      public String toString() {
         if (this.wYear == 0 && this.wMonth == 0 && this.wDay == 0 && this.wHour == 0 && this.wMinute == 0 && this.wSecond == 0 && this.wMilliseconds == 0) {
            return super.toString();
         } else {
            DateFormat dtf = DateFormat.getDateTimeInstance();
            Calendar cal = this.toCalendar();
            return dtf.format(cal.getTime());
         }
      }
   }

   @Structure.FieldOrder({"dwLowDateTime", "dwHighDateTime"})
   public static class FILETIME extends Structure {
      public int dwLowDateTime;
      public int dwHighDateTime;
      private static final long EPOCH_DIFF = 11644473600000L;

      public FILETIME(Date date) {
         long rawValue = dateToFileTime(date);
         this.dwHighDateTime = (int)(rawValue >> 32 & 4294967295L);
         this.dwLowDateTime = (int)(rawValue & 4294967295L);
      }

      public FILETIME(WinNT.LARGE_INTEGER ft) {
         this.dwHighDateTime = ft.getHigh().intValue();
         this.dwLowDateTime = ft.getLow().intValue();
      }

      public FILETIME() {
      }

      public FILETIME(Pointer memory) {
         super(memory);
         this.read();
      }

      public static Date filetimeToDate(int high, int low) {
         long filetime = (long)high << 32 | (long)low & 4294967295L;
         long ms_since_16010101 = filetime / 10000L;
         long ms_since_19700101 = ms_since_16010101 - 11644473600000L;
         return new Date(ms_since_19700101);
      }

      public static long dateToFileTime(Date date) {
         long ms_since_19700101 = date.getTime();
         long ms_since_16010101 = ms_since_19700101 + 11644473600000L;
         return ms_since_16010101 * 1000L * 10L;
      }

      public Date toDate() {
         return filetimeToDate(this.dwHighDateTime, this.dwLowDateTime);
      }

      public long toTime() {
         return this.toDate().getTime();
      }

      public WinDef.DWORDLONG toDWordLong() {
         return new WinDef.DWORDLONG((long)this.dwHighDateTime << 32 | (long)this.dwLowDateTime & 4294967295L);
      }

      public String toString() {
         return super.toString() + ": " + this.toDate().toString();
      }

      public static class ByReference extends FILETIME implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"dwFileAttributes", "ftCreationTime", "ftLastAccessTime", "ftLastWriteTime", "nFileSizeHigh", "nFileSizeLow", "dwReserved0", "dwReserved1", "cFileName", "cAlternateFileName"})
   public static class WIN32_FIND_DATA extends Structure {
      public int dwFileAttributes;
      public FILETIME ftCreationTime;
      public FILETIME ftLastAccessTime;
      public FILETIME ftLastWriteTime;
      public int nFileSizeHigh;
      public int nFileSizeLow;
      public int dwReserved0;
      public int dwReserved1;
      public char[] cFileName = new char[260];
      public char[] cAlternateFileName = new char[14];

      public static int sizeOf() {
         return Native.getNativeSize(WIN32_FIND_DATA.class, (Object)null);
      }

      public WIN32_FIND_DATA() {
         super(W32APITypeMapper.DEFAULT);
      }

      public WIN32_FIND_DATA(Pointer memory) {
         super(memory, 0, W32APITypeMapper.DEFAULT);
         this.read();
      }

      public WIN32_FIND_DATA(int dwFileAttributes, FILETIME ftCreationTime, FILETIME ftLastAccessTime, FILETIME ftLastWriteTime, int nFileSizeHigh, int nFileSizeLow, int dwReserved0, int dwReserved1, char[] cFileName, char[] cAlternateFileName) {
         this.dwFileAttributes = dwFileAttributes;
         this.ftCreationTime = ftCreationTime;
         this.ftLastAccessTime = ftLastAccessTime;
         this.ftLastWriteTime = ftLastWriteTime;
         this.nFileSizeHigh = nFileSizeHigh;
         this.nFileSizeLow = nFileSizeLow;
         this.dwReserved0 = dwReserved0;
         this.cFileName = cFileName;
         this.cAlternateFileName = cAlternateFileName;
         this.write();
      }

      public String getFileName() {
         return Native.toString(this.cFileName);
      }

      public String getAlternateFileName() {
         return Native.toString(this.cAlternateFileName);
      }

      public static class ByReference extends WIN32_FIND_DATA implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"VolumeSerialNumber", "FileId"})
   public static class FILE_ID_INFO extends Structure {
      public long VolumeSerialNumber;
      public FILE_ID_128 FileId;

      public static int sizeOf() {
         return Native.getNativeSize(FILE_ID_INFO.class, (Object)null);
      }

      public FILE_ID_INFO() {
      }

      public FILE_ID_INFO(Pointer memory) {
         super(memory);
         this.read();
      }

      public FILE_ID_INFO(long VolumeSerialNumber, FILE_ID_128 FileId) {
         this.VolumeSerialNumber = VolumeSerialNumber;
         this.FileId = FileId;
         this.write();
      }

      @Structure.FieldOrder({"Identifier"})
      public static class FILE_ID_128 extends Structure {
         public WinDef.BYTE[] Identifier = new WinDef.BYTE[16];

         public FILE_ID_128() {
         }

         public FILE_ID_128(Pointer memory) {
            super(memory);
            this.read();
         }

         public FILE_ID_128(WinDef.BYTE[] Identifier) {
            this.Identifier = Identifier;
            this.write();
         }
      }

      public static class ByReference extends FILE_ID_INFO implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"FileAttributes", "ReparseTag"})
   public static class FILE_ATTRIBUTE_TAG_INFO extends Structure {
      public int FileAttributes;
      public int ReparseTag;

      public static int sizeOf() {
         return Native.getNativeSize(FILE_ATTRIBUTE_TAG_INFO.class, (Object)null);
      }

      public FILE_ATTRIBUTE_TAG_INFO() {
      }

      public FILE_ATTRIBUTE_TAG_INFO(Pointer memory) {
         super(memory);
         this.read();
      }

      public FILE_ATTRIBUTE_TAG_INFO(int FileAttributes, int ReparseTag) {
         this.FileAttributes = FileAttributes;
         this.ReparseTag = ReparseTag;
         this.write();
      }

      public static class ByReference extends FILE_ATTRIBUTE_TAG_INFO implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"CompressedFileSize", "CompressionFormat", "CompressionUnitShift", "ChunkShift", "ClusterShift", "Reserved"})
   public static class FILE_COMPRESSION_INFO extends Structure {
      public WinNT.LARGE_INTEGER CompressedFileSize;
      public short CompressionFormat;
      public byte CompressionUnitShift;
      public byte ChunkShift;
      public byte ClusterShift;
      public byte[] Reserved = new byte[3];

      public static int sizeOf() {
         return Native.getNativeSize(FILE_COMPRESSION_INFO.class, (Object)null);
      }

      public FILE_COMPRESSION_INFO() {
         super(W32APITypeMapper.DEFAULT);
      }

      public FILE_COMPRESSION_INFO(Pointer memory) {
         super(memory, 0, W32APITypeMapper.DEFAULT);
         this.read();
      }

      public FILE_COMPRESSION_INFO(WinNT.LARGE_INTEGER CompressedFileSize, short CompressionFormat, byte CompressionUnitShift, byte ChunkShift, byte ClusterShift) {
         this.CompressedFileSize = CompressedFileSize;
         this.CompressionFormat = CompressionFormat;
         this.CompressionUnitShift = CompressionUnitShift;
         this.ChunkShift = ChunkShift;
         this.ClusterShift = ClusterShift;
         this.Reserved = new byte[3];
         this.write();
      }

      public static class ByReference extends FILE_COMPRESSION_INFO implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"DeleteFile"})
   public static class FILE_DISPOSITION_INFO extends Structure {
      public boolean DeleteFile;

      public static int sizeOf() {
         return Native.getNativeSize(FILE_DISPOSITION_INFO.class, (Object)null);
      }

      public FILE_DISPOSITION_INFO() {
      }

      public FILE_DISPOSITION_INFO(Pointer memory) {
         super(memory);
         this.read();
      }

      public FILE_DISPOSITION_INFO(boolean DeleteFile) {
         this.DeleteFile = DeleteFile;
         this.write();
      }

      public static class ByReference extends FILE_DISPOSITION_INFO implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"AllocationSize", "EndOfFile", "NumberOfLinks", "DeletePending", "Directory"})
   public static class FILE_STANDARD_INFO extends Structure {
      public WinNT.LARGE_INTEGER AllocationSize;
      public WinNT.LARGE_INTEGER EndOfFile;
      public int NumberOfLinks;
      public boolean DeletePending;
      public boolean Directory;

      public static int sizeOf() {
         return Native.getNativeSize(FILE_STANDARD_INFO.class, (Object)null);
      }

      public FILE_STANDARD_INFO() {
      }

      public FILE_STANDARD_INFO(Pointer memory) {
         super(memory);
         this.read();
      }

      public FILE_STANDARD_INFO(WinNT.LARGE_INTEGER AllocationSize, WinNT.LARGE_INTEGER EndOfFile, int NumberOfLinks, boolean DeletePending, boolean Directory) {
         this.AllocationSize = AllocationSize;
         this.EndOfFile = EndOfFile;
         this.NumberOfLinks = NumberOfLinks;
         this.DeletePending = DeletePending;
         this.Directory = Directory;
         this.write();
      }

      public static class ByReference extends FILE_STANDARD_INFO implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"CreationTime", "LastAccessTime", "LastWriteTime", "ChangeTime", "FileAttributes"})
   public static class FILE_BASIC_INFO extends Structure {
      public WinNT.LARGE_INTEGER CreationTime;
      public WinNT.LARGE_INTEGER LastAccessTime;
      public WinNT.LARGE_INTEGER LastWriteTime;
      public WinNT.LARGE_INTEGER ChangeTime;
      public int FileAttributes;

      public static int sizeOf() {
         return Native.getNativeSize(FILE_BASIC_INFO.class, (Object)null);
      }

      public FILE_BASIC_INFO() {
      }

      public FILE_BASIC_INFO(Pointer memory) {
         super(memory);
         this.read();
         this.CreationTime = new WinNT.LARGE_INTEGER(this.CreationTime.getValue());
         this.LastAccessTime = new WinNT.LARGE_INTEGER(this.LastAccessTime.getValue());
         this.LastWriteTime = new WinNT.LARGE_INTEGER(this.LastWriteTime.getValue());
         this.ChangeTime = new WinNT.LARGE_INTEGER(this.ChangeTime.getValue());
      }

      public FILE_BASIC_INFO(FILETIME CreationTime, FILETIME LastAccessTime, FILETIME LastWriteTime, FILETIME ChangeTime, int FileAttributes) {
         this.CreationTime = new WinNT.LARGE_INTEGER(CreationTime.toTime());
         this.LastAccessTime = new WinNT.LARGE_INTEGER(LastAccessTime.toTime());
         this.LastWriteTime = new WinNT.LARGE_INTEGER(LastWriteTime.toTime());
         this.ChangeTime = new WinNT.LARGE_INTEGER(ChangeTime.toTime());
         this.FileAttributes = FileAttributes;
         this.write();
      }

      public FILE_BASIC_INFO(WinNT.LARGE_INTEGER CreationTime, WinNT.LARGE_INTEGER LastAccessTime, WinNT.LARGE_INTEGER LastWriteTime, WinNT.LARGE_INTEGER ChangeTime, int FileAttributes) {
         this.CreationTime = CreationTime;
         this.LastAccessTime = LastAccessTime;
         this.LastWriteTime = LastWriteTime;
         this.ChangeTime = ChangeTime;
         this.FileAttributes = FileAttributes;
         this.write();
      }

      public static class ByReference extends FILE_BASIC_INFO implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }
}
