package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

public interface Wtsapi32 extends StdCallLibrary {
   Wtsapi32 INSTANCE = (Wtsapi32)Native.load("Wtsapi32", Wtsapi32.class, W32APIOptions.DEFAULT_OPTIONS);
   int NOTIFY_FOR_ALL_SESSIONS = 1;
   int NOTIFY_FOR_THIS_SESSION = 0;
   int WTS_CONSOLE_CONNECT = 1;
   int WTS_CONSOLE_DISCONNECT = 2;
   int WTS_REMOTE_CONNECT = 3;
   int WTS_REMOTE_DISCONNECT = 4;
   int WTS_SESSION_LOGON = 5;
   int WTS_SESSION_LOGOFF = 6;
   int WTS_SESSION_LOCK = 7;
   int WTS_SESSION_UNLOCK = 8;
   int WTS_SESSION_REMOTE_CONTROL = 9;
   WinNT.HANDLE WTS_CURRENT_SERVER_HANDLE = new WinNT.HANDLE((Pointer)null);
   int WTS_CURRENT_SESSION = -1;
   int WTS_ANY_SESSION = -2;
   int WTS_PROCESS_INFO_LEVEL_0 = 0;
   int WTS_PROCESS_INFO_LEVEL_1 = 1;
   int DOMAIN_LENGTH = 17;
   int USERNAME_LENGTH = 20;
   int WINSTATIONNAME_LENGTH = 32;

   boolean WTSEnumerateSessions(WinNT.HANDLE var1, int var2, int var3, PointerByReference var4, IntByReference var5);

   boolean WTSQuerySessionInformation(WinNT.HANDLE var1, int var2, int var3, PointerByReference var4, IntByReference var5);

   void WTSFreeMemory(Pointer var1);

   boolean WTSRegisterSessionNotification(WinDef.HWND var1, int var2);

   boolean WTSUnRegisterSessionNotification(WinDef.HWND var1);

   boolean WTSEnumerateProcessesEx(WinNT.HANDLE var1, IntByReference var2, int var3, PointerByReference var4, IntByReference var5);

   boolean WTSFreeMemoryEx(int var1, Pointer var2, int var3);

   @Structure.FieldOrder({"SessionId", "ProcessId", "pProcessName", "pUserSid", "NumberOfThreads", "HandleCount", "PagefileUsage", "PeakPagefileUsage", "WorkingSetSize", "PeakWorkingSetSize", "UserTime", "KernelTime"})
   public static class WTS_PROCESS_INFO_EX extends Structure {
      public int SessionId;
      public int ProcessId;
      public String pProcessName;
      public WinNT.PSID pUserSid;
      public int NumberOfThreads;
      public int HandleCount;
      public int PagefileUsage;
      public int PeakPagefileUsage;
      public int WorkingSetSize;
      public int PeakWorkingSetSize;
      public WinNT.LARGE_INTEGER UserTime;
      public WinNT.LARGE_INTEGER KernelTime;

      public WTS_PROCESS_INFO_EX() {
         super(W32APITypeMapper.DEFAULT);
      }

      public WTS_PROCESS_INFO_EX(Pointer p) {
         super(p, 0, W32APITypeMapper.DEFAULT);
         this.read();
      }
   }

   @Structure.FieldOrder({"State", "SessionId", "IncomingBytes", "OutgoingBytes", "IncomingFrames", "OutgoingFrames", "IncomingCompressedBytes", "OutgoingCompressedBytes", "WinStationName", "Domain", "UserName", "ConnectTime", "DisconnectTime", "LastInputTime", "LogonTime", "CurrentTime"})
   public static class WTSINFO extends Structure {
      private static final int CHAR_WIDTH = Boolean.getBoolean("w32.ascii") ? 1 : 2;
      public int State;
      public int SessionId;
      public int IncomingBytes;
      public int OutgoingBytes;
      public int IncomingFrames;
      public int OutgoingFrames;
      public int IncomingCompressedBytes;
      public int OutgoingCompressedBytes;
      public final byte[] WinStationName;
      public final byte[] Domain;
      public final byte[] UserName;
      public WinNT.LARGE_INTEGER ConnectTime;
      public WinNT.LARGE_INTEGER DisconnectTime;
      public WinNT.LARGE_INTEGER LastInputTime;
      public WinNT.LARGE_INTEGER LogonTime;
      public WinNT.LARGE_INTEGER CurrentTime;

      public WTSINFO() {
         this.WinStationName = new byte[32 * CHAR_WIDTH];
         this.Domain = new byte[17 * CHAR_WIDTH];
         this.UserName = new byte[21 * CHAR_WIDTH];
      }

      public WTSINFO(Pointer p) {
         super(p);
         this.WinStationName = new byte[32 * CHAR_WIDTH];
         this.Domain = new byte[17 * CHAR_WIDTH];
         this.UserName = new byte[21 * CHAR_WIDTH];
         this.read();
      }

      public String getWinStationName() {
         return this.getStringAtOffset(this.fieldOffset("WinStationName"));
      }

      public String getDomain() {
         return this.getStringAtOffset(this.fieldOffset("Domain"));
      }

      public String getUserName() {
         return this.getStringAtOffset(this.fieldOffset("UserName"));
      }

      private String getStringAtOffset(int offset) {
         return CHAR_WIDTH == 1 ? this.getPointer().getString((long)offset) : this.getPointer().getWideString((long)offset);
      }
   }

   @Structure.FieldOrder({"AddressFamily", "Address"})
   public static class WTS_CLIENT_ADDRESS extends Structure {
      public int AddressFamily;
      public byte[] Address = new byte[20];

      public WTS_CLIENT_ADDRESS() {
      }

      public WTS_CLIENT_ADDRESS(Pointer p) {
         super(p);
         this.read();
      }
   }

   @Structure.FieldOrder({"SessionId", "pWinStationName", "State"})
   public static class WTS_SESSION_INFO extends Structure {
      public int SessionId;
      public String pWinStationName;
      public int State;

      public WTS_SESSION_INFO() {
         super(W32APITypeMapper.DEFAULT);
      }

      public WTS_SESSION_INFO(Pointer p) {
         super(p, 0, W32APITypeMapper.DEFAULT);
         this.read();
      }
   }

   public interface WTS_INFO_CLASS {
      int WTSInitialProgram = 0;
      int WTSApplicationName = 1;
      int WTSWorkingDirectory = 2;
      int WTSOEMId = 3;
      int WTSSessionId = 4;
      int WTSUserName = 5;
      int WTSWinStationName = 6;
      int WTSDomainName = 7;
      int WTSConnectState = 8;
      int WTSClientBuildNumber = 9;
      int WTSClientName = 10;
      int WTSClientDirectory = 11;
      int WTSClientProductId = 12;
      int WTSClientHardwareId = 13;
      int WTSClientAddress = 14;
      int WTSClientDisplay = 15;
      int WTSClientProtocolType = 16;
      int WTSIdleTime = 17;
      int WTSLogonTime = 18;
      int WTSIncomingBytes = 19;
      int WTSOutgoingBytes = 20;
      int WTSIncomingFrames = 21;
      int WTSOutgoingFrames = 22;
      int WTSClientInfo = 23;
      int WTSSessionInfo = 24;
      int WTSSessionInfoEx = 25;
      int WTSConfigInfo = 26;
      int WTSValidationInfo = 27;
      int WTSSessionAddressV4 = 28;
      int WTSIsRemoteSession = 29;
   }

   public interface WTS_CONNECTSTATE_CLASS {
      int WTSActive = 0;
      int WTSConnected = 1;
      int WTSConnectQuery = 2;
      int WTSShadow = 3;
      int WTSDisconnected = 4;
      int WTSIdle = 5;
      int WTSListen = 6;
      int WTSReset = 7;
      int WTSDown = 8;
      int WTSInit = 9;
   }
}
