package com.sun.jna.platform.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

public interface IPHlpAPI extends Library {
   IPHlpAPI INSTANCE = (IPHlpAPI)Native.load("IPHlpAPI", IPHlpAPI.class, W32APIOptions.DEFAULT_OPTIONS);
   int IF_MAX_STRING_SIZE = 256;
   int IF_MAX_PHYS_ADDRESS_LENGTH = 32;
   int MAX_INTERFACE_NAME_LEN = 256;
   int MAXLEN_IFDESCR = 256;
   int MAXLEN_PHYSADDR = 8;
   int MAX_HOSTNAME_LEN = 128;
   int MAX_DOMAIN_NAME_LEN = 128;
   int MAX_SCOPE_ID_LEN = 256;
   int AF_UNSPEC = 0;
   int AF_INET = 2;
   int AF_IPX = 6;
   int AF_NETBIOS = 17;
   int AF_INET6 = 23;
   int AF_IRDA = 26;
   int AF_BTH = 32;

   int GetIfEntry(MIB_IFROW var1);

   int GetIfEntry2(MIB_IF_ROW2 var1);

   int GetNetworkParams(Pointer var1, IntByReference var2);

   int GetTcpStatistics(MIB_TCPSTATS var1);

   int GetTcpStatisticsEx(MIB_TCPSTATS var1, int var2);

   int GetUdpStatistics(MIB_UDPSTATS var1);

   int GetUdpStatisticsEx(MIB_UDPSTATS var1, int var2);

   @Structure.FieldOrder({"dwInDatagrams", "dwNoPorts", "dwInErrors", "dwOutDatagrams", "dwNumAddrs"})
   public static class MIB_UDPSTATS extends Structure {
      public int dwInDatagrams;
      public int dwNoPorts;
      public int dwInErrors;
      public int dwOutDatagrams;
      public int dwNumAddrs;
   }

   @Structure.FieldOrder({"dwRtoAlgorithm", "dwRtoMin", "dwRtoMax", "dwMaxConn", "dwActiveOpens", "dwPassiveOpens", "dwAttemptFails", "dwEstabResets", "dwCurrEstab", "dwInSegs", "dwOutSegs", "dwRetransSegs", "dwInErrs", "dwOutRsts", "dwNumConns"})
   public static class MIB_TCPSTATS extends Structure {
      public int dwRtoAlgorithm;
      public int dwRtoMin;
      public int dwRtoMax;
      public int dwMaxConn;
      public int dwActiveOpens;
      public int dwPassiveOpens;
      public int dwAttemptFails;
      public int dwEstabResets;
      public int dwCurrEstab;
      public int dwInSegs;
      public int dwOutSegs;
      public int dwRetransSegs;
      public int dwInErrs;
      public int dwOutRsts;
      public int dwNumConns;
   }

   @Structure.FieldOrder({"HostName", "DomainName", "CurrentDnsServer", "DnsServerList", "NodeType", "ScopeId", "EnableRouting", "EnableProxy", "EnableDns"})
   public static class FIXED_INFO extends Structure {
      public byte[] HostName = new byte[132];
      public byte[] DomainName = new byte[132];
      public IP_ADDR_STRING.ByReference CurrentDnsServer;
      public IP_ADDR_STRING DnsServerList;
      public int NodeType;
      public byte[] ScopeId = new byte[260];
      public int EnableRouting;
      public int EnableProxy;
      public int EnableDns;

      public FIXED_INFO(Pointer p) {
         super(p);
         this.read();
      }

      public FIXED_INFO() {
      }
   }

   @Structure.FieldOrder({"Next", "IpAddress", "IpMask", "Context"})
   public static class IP_ADDR_STRING extends Structure {
      public ByReference Next;
      public IP_ADDRESS_STRING IpAddress;
      public IP_ADDRESS_STRING IpMask;
      public int Context;

      public static class ByReference extends IP_ADDR_STRING implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"String"})
   public static class IP_ADDRESS_STRING extends Structure {
      public byte[] String = new byte[16];
   }

   @Structure.FieldOrder({"InterfaceLuid", "InterfaceIndex", "InterfaceGuid", "Alias", "Description", "PhysicalAddressLength", "PhysicalAddress", "PermanentPhysicalAddress", "Mtu", "Type", "TunnelType", "MediaType", "PhysicalMediumType", "AccessType", "DirectionType", "InterfaceAndOperStatusFlags", "OperStatus", "AdminStatus", "MediaConnectState", "NetworkGuid", "ConnectionType", "TransmitLinkSpeed", "ReceiveLinkSpeed", "InOctets", "InUcastPkts", "InNUcastPkts", "InDiscards", "InErrors", "InUnknownProtos", "InUcastOctets", "InMulticastOctets", "InBroadcastOctets", "OutOctets", "OutUcastPkts", "OutNUcastPkts", "OutDiscards", "OutErrors", "OutUcastOctets", "OutMulticastOctets", "OutBroadcastOctets", "OutQLen"})
   public static class MIB_IF_ROW2 extends Structure {
      public long InterfaceLuid;
      public int InterfaceIndex;
      public Guid.GUID InterfaceGuid;
      public char[] Alias = new char[257];
      public char[] Description = new char[257];
      public int PhysicalAddressLength;
      public byte[] PhysicalAddress = new byte[32];
      public byte[] PermanentPhysicalAddress = new byte[32];
      public int Mtu;
      public int Type;
      public int TunnelType;
      public int MediaType;
      public int PhysicalMediumType;
      public int AccessType;
      public int DirectionType;
      public byte InterfaceAndOperStatusFlags;
      public int OperStatus;
      public int AdminStatus;
      public int MediaConnectState;
      public Guid.GUID NetworkGuid;
      public int ConnectionType;
      public long TransmitLinkSpeed;
      public long ReceiveLinkSpeed;
      public long InOctets;
      public long InUcastPkts;
      public long InNUcastPkts;
      public long InDiscards;
      public long InErrors;
      public long InUnknownProtos;
      public long InUcastOctets;
      public long InMulticastOctets;
      public long InBroadcastOctets;
      public long OutOctets;
      public long OutUcastPkts;
      public long OutNUcastPkts;
      public long OutDiscards;
      public long OutErrors;
      public long OutUcastOctets;
      public long OutMulticastOctets;
      public long OutBroadcastOctets;
      public long OutQLen;
   }

   @Structure.FieldOrder({"wszName", "dwIndex", "dwType", "dwMtu", "dwSpeed", "dwPhysAddrLen", "bPhysAddr", "dwAdminStatus", "dwOperStatus", "dwLastChange", "dwInOctets", "dwInUcastPkts", "dwInNUcastPkts", "dwInDiscards", "dwInErrors", "dwInUnknownProtos", "dwOutOctets", "dwOutUcastPkts", "dwOutNUcastPkts", "dwOutDiscards", "dwOutErrors", "dwOutQLen", "dwDescrLen", "bDescr"})
   public static class MIB_IFROW extends Structure {
      public char[] wszName = new char[256];
      public int dwIndex;
      public int dwType;
      public int dwMtu;
      public int dwSpeed;
      public int dwPhysAddrLen;
      public byte[] bPhysAddr = new byte[8];
      public int dwAdminStatus;
      public int dwOperStatus;
      public int dwLastChange;
      public int dwInOctets;
      public int dwInUcastPkts;
      public int dwInNUcastPkts;
      public int dwInDiscards;
      public int dwInErrors;
      public int dwInUnknownProtos;
      public int dwOutOctets;
      public int dwOutUcastPkts;
      public int dwOutNUcastPkts;
      public int dwOutDiscards;
      public int dwOutErrors;
      public int dwOutQLen;
      public int dwDescrLen;
      public byte[] bDescr = new byte[256];
   }
}
