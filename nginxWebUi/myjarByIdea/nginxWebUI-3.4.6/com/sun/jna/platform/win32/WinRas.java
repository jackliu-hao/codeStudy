package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;

public interface WinRas {
   int ERROR_BUFFER_TOO_SMALL = 603;
   int ERROR_CANNOT_FIND_PHONEBOOK_ENTRY = 623;
   int MAX_PATH = 260;
   int UNLEN = 256;
   int PWLEN = 256;
   int DNLEN = 15;
   int RAS_MaxEntryName = 256;
   int RAS_MaxPhoneNumber = 128;
   int RAS_MaxCallbackNumber = 128;
   int RAS_MaxDeviceType = 16;
   int RAS_MaxDeviceName = 128;
   int RAS_MaxDnsSuffix = 256;
   int RAS_MaxAreaCode = 10;
   int RAS_MaxX25Address = 200;
   int RAS_MaxIpAddress = 15;
   int RAS_MaxFacilities = 200;
   int RAS_MaxUserData = 200;
   int RAS_MaxPadType = 32;
   int RASCS_Connected = 8192;
   int RASCS_Disconnected = 8193;
   int RASCM_UserName = 1;
   int RASCM_Password = 2;
   int RASCM_Domain = 4;
   int RASTUNNELENDPOINT_IPv4 = 1;
   int RASTUNNELENDPOINT_IPv6 = 2;
   String RASDT_Modem = "modem";

   public interface RasDialFunc2 extends StdCallLibrary.StdCallCallback {
      int dialNotification(int var1, int var2, WinNT.HANDLE var3, int var4, int var5, int var6, int var7);
   }

   @Structure.FieldOrder({"dwSize", "dwfOptions", "dwCountryID", "dwCountryCode", "szAreaCode", "szLocalPhoneNumber", "dwAlternateOffset", "ipaddr", "ipaddrDns", "ipaddrDnsAlt", "ipaddrWins", "ipaddrWinsAlt", "dwFrameSize", "dwfNetProtocols", "dwFramingProtocol", "szScript", "szAutodialDll", "szAutodialFunc", "szDeviceType", "szDeviceName", "szX25PadType", "szX25Address", "szX25Facilities", "szX25UserData", "dwChannels", "dwReserved1", "dwReserved2", "dwSubEntries", "dwDialMode", "dwDialExtraPercent", "dwDialExtraSampleSeconds", "dwHangUpExtraPercent", "dwHangUpExtraSampleSeconds", "dwIdleDisconnectSeconds", "dwType", "dwEncryptionType", "dwCustomAuthKey", "guidId", "szCustomDialDll", "dwVpnStrategy", "dwfOptions2", "dwfOptions3", "szDnsSuffix", "dwTcpWindowSize", "szPrerequisitePbk", "szPrerequisiteEntry", "dwRedialCount", "dwRedialPause", "ipv6addrDns", "ipv6addrDnsAlt", "dwIPv4InterfaceMetric", "dwIPv6InterfaceMetric", "ipv6addr", "dwIPv6PrefixLength", "dwNetworkOutageTime"})
   public static class RASENTRY extends Structure {
      public int dwSize;
      public int dwfOptions;
      public int dwCountryID;
      public int dwCountryCode;
      public char[] szAreaCode = new char[11];
      public char[] szLocalPhoneNumber = new char[129];
      public int dwAlternateOffset;
      public RASIPADDR ipaddr;
      public RASIPADDR ipaddrDns;
      public RASIPADDR ipaddrDnsAlt;
      public RASIPADDR ipaddrWins;
      public RASIPADDR ipaddrWinsAlt;
      public int dwFrameSize;
      public int dwfNetProtocols;
      public int dwFramingProtocol;
      public char[] szScript = new char[260];
      public char[] szAutodialDll = new char[260];
      public char[] szAutodialFunc = new char[260];
      public char[] szDeviceType = new char[17];
      public char[] szDeviceName = new char[129];
      public char[] szX25PadType = new char[33];
      public char[] szX25Address = new char[201];
      public char[] szX25Facilities = new char[201];
      public char[] szX25UserData = new char[201];
      public int dwChannels;
      public int dwReserved1;
      public int dwReserved2;
      public int dwSubEntries;
      public int dwDialMode;
      public int dwDialExtraPercent;
      public int dwDialExtraSampleSeconds;
      public int dwHangUpExtraPercent;
      public int dwHangUpExtraSampleSeconds;
      public int dwIdleDisconnectSeconds;
      public int dwType;
      public int dwEncryptionType;
      public int dwCustomAuthKey;
      public Guid.GUID guidId;
      public char[] szCustomDialDll = new char[260];
      public int dwVpnStrategy;
      public int dwfOptions2;
      public int dwfOptions3;
      public char[] szDnsSuffix = new char[256];
      public int dwTcpWindowSize;
      public char[] szPrerequisitePbk = new char[260];
      public char[] szPrerequisiteEntry = new char[257];
      public int dwRedialCount;
      public int dwRedialPause;
      public RASIPV6ADDR ipv6addrDns;
      public RASIPV6ADDR ipv6addrDnsAlt;
      public int dwIPv4InterfaceMetric;
      public int dwIPv6InterfaceMetric;
      public RASIPV6ADDR ipv6addr;
      public int dwIPv6PrefixLength;
      public int dwNetworkOutageTime;

      public RASENTRY() {
         this.dwSize = this.size();
      }

      public RASENTRY(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends RASENTRY implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"addr"})
   public static class RASIPADDR extends Structure {
      public byte[] addr = new byte[4];

      public RASIPADDR() {
      }

      public RASIPADDR(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dwSize", "dwMask", "szUserName", "szPassword", "szDomain"})
   public static class RASCREDENTIALS extends Structure {
      public int dwSize;
      public int dwMask;
      public char[] szUserName = new char[257];
      public char[] szPassword = new char[257];
      public char[] szDomain = new char[16];

      public RASCREDENTIALS() {
         this.dwSize = this.size();
      }

      public RASCREDENTIALS(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends RASCREDENTIALS implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwSize", "rasconnstate", "dwError", "szDeviceType", "szDeviceName", "szPhoneNumber", "localEndPoint", "remoteEndPoint", "rasconnsubstate"})
   public static class RASCONNSTATUS extends Structure {
      public int dwSize;
      public int rasconnstate;
      public int dwError;
      public char[] szDeviceType = new char[17];
      public char[] szDeviceName = new char[129];
      public char[] szPhoneNumber = new char[129];
      public RASTUNNELENDPOINT localEndPoint;
      public RASTUNNELENDPOINT remoteEndPoint;
      public int rasconnsubstate;

      public RASCONNSTATUS() {
         this.dwSize = this.size();
      }

      public RASCONNSTATUS(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dwType", "u"})
   public static class RASTUNNELENDPOINT extends Structure {
      public int dwType;
      public UNION u;

      public RASTUNNELENDPOINT() {
      }

      public RASTUNNELENDPOINT(Pointer memory) {
         super(memory);
         this.read();
      }

      public void read() {
         super.read();
         switch (this.dwType) {
            case 1:
               this.u.setType(RASIPV4ADDR.class);
               break;
            case 2:
               this.u.setType(RASIPV6ADDR.class);
               break;
            default:
               this.u.setType(RASIPV4ADDR.class);
         }

         this.u.read();
      }

      public static class UNION extends Union {
         public RASIPV4ADDR ipv4;
         public RASIPV6ADDR ipv6;

         public static class ByReference extends UNION implements Structure.ByReference {
         }
      }
   }

   @Structure.FieldOrder({"dwSize", "dwError", "szIpAddress", "szServerIpAddress", "dwOptions", "dwServerOptions"})
   public static class RASPPPIP extends Structure {
      public int dwSize;
      public int dwError;
      public char[] szIpAddress = new char[16];
      public char[] szServerIpAddress = new char[16];
      public int dwOptions;
      public int dwServerOptions;

      public RASPPPIP() {
         this.dwSize = this.size();
      }

      public RASPPPIP(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends RASPPPIP implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"addr"})
   public static class RASIPV6ADDR extends Structure {
      public byte[] addr = new byte[16];

      public RASIPV6ADDR() {
      }

      public RASIPV6ADDR(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"addr"})
   public static class RASIPV4ADDR extends Structure {
      public byte[] addr = new byte[8];

      public RASIPV4ADDR() {
      }

      public RASIPV4ADDR(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dwSize", "dwBytesXmited", "dwBytesRcved", "dwFramesXmited", "dwFramesRcved", "dwCrcErr", "dwTimeoutErr", "dwAlignmentErr", "dwHardwareOverrunErr", "dwFramingErr", "dwBufferOverrunErr", "dwCompressionRatioIn", "dwCompressionRatioOut", "dwBps", "dwConnectDuration"})
   public static class RAS_STATS extends Structure {
      public int dwSize;
      public int dwBytesXmited;
      public int dwBytesRcved;
      public int dwFramesXmited;
      public int dwFramesRcved;
      public int dwCrcErr;
      public int dwTimeoutErr;
      public int dwAlignmentErr;
      public int dwHardwareOverrunErr;
      public int dwFramingErr;
      public int dwBufferOverrunErr;
      public int dwCompressionRatioIn;
      public int dwCompressionRatioOut;
      public int dwBps;
      public int dwConnectDuration;

      public RAS_STATS() {
         this.dwSize = this.size();
      }

      public RAS_STATS(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"dwSize", "hrasconn", "szEntryName", "szDeviceType", "szDeviceName", "szPhonebook", "dwSubEntry", "guidEntry", "dwFlags", "luid", "guidCorrelationId"})
   public static class RASCONN extends Structure {
      public int dwSize;
      public WinNT.HANDLE hrasconn;
      public char[] szEntryName = new char[257];
      public char[] szDeviceType = new char[17];
      public char[] szDeviceName = new char[129];
      public char[] szPhonebook = new char[260];
      public int dwSubEntry;
      public Guid.GUID guidEntry;
      public int dwFlags;
      public WinNT.LUID luid;
      public Guid.GUID guidCorrelationId;

      public RASCONN() {
         this.dwSize = this.size();
      }

      public RASCONN(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends RASCONN implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwSize", "szEntryName", "szPhoneNumber", "szCallbackNumber", "szUserName", "szPassword", "szDomain"})
   public static class RASDIALPARAMS extends Structure {
      public int dwSize;
      public char[] szEntryName = new char[257];
      public char[] szPhoneNumber = new char[129];
      public char[] szCallbackNumber = new char[129];
      public char[] szUserName = new char[257];
      public char[] szPassword = new char[257];
      public char[] szDomain = new char[16];

      public RASDIALPARAMS() {
         this.dwSize = this.size();
      }

      public RASDIALPARAMS(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends RASDIALPARAMS implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwSize", "dwfOptions", "hwndParent", "reserved", "reserved1", "RasEapInfo", "fSkipPppAuth", "RasDevSpecificInfo"})
   public static class RASDIALEXTENSIONS extends Structure {
      public int dwSize;
      public int dwfOptions;
      public WinDef.HWND hwndParent;
      public BaseTSD.ULONG_PTR reserved;
      public BaseTSD.ULONG_PTR reserved1;
      public RASEAPINFO RasEapInfo;
      public WinDef.BOOL fSkipPppAuth;
      public RASDEVSPECIFICINFO RasDevSpecificInfo;

      public RASDIALEXTENSIONS() {
         this.dwSize = this.size();
      }

      public RASDIALEXTENSIONS(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends RASDIALEXTENSIONS implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"dwSize", "pbDevSpecificInfo"})
   public static class RASDEVSPECIFICINFO extends Structure {
      public int dwSize;
      public Pointer pbDevSpecificInfo;

      public RASDEVSPECIFICINFO() {
      }

      public RASDEVSPECIFICINFO(Pointer memory) {
         super(memory);
         this.read();
      }

      public RASDEVSPECIFICINFO(byte[] data) {
         this.pbDevSpecificInfo = new Memory((long)data.length);
         this.pbDevSpecificInfo.write(0L, (byte[])data, 0, data.length);
         this.dwSize = data.length;
         this.allocateMemory();
      }

      public RASDEVSPECIFICINFO(String s) {
         this(Native.toByteArray(s));
      }

      public byte[] getData() {
         return this.pbDevSpecificInfo == null ? null : this.pbDevSpecificInfo.getByteArray(0L, this.dwSize);
      }
   }

   @Structure.FieldOrder({"dwSizeofEapInfo", "pbEapInfo"})
   public static class RASEAPINFO extends Structure {
      public int dwSizeofEapInfo;
      public Pointer pbEapInfo;

      public RASEAPINFO() {
      }

      public RASEAPINFO(Pointer memory) {
         super(memory);
         this.read();
      }

      public RASEAPINFO(byte[] data) {
         this.pbEapInfo = new Memory((long)data.length);
         this.pbEapInfo.write(0L, (byte[])data, 0, data.length);
         this.dwSizeofEapInfo = data.length;
         this.allocateMemory();
      }

      public RASEAPINFO(String s) {
         this(Native.toByteArray(s));
      }

      public byte[] getData() {
         return this.pbEapInfo == null ? null : this.pbEapInfo.getByteArray(0L, this.dwSizeofEapInfo);
      }
   }
}
