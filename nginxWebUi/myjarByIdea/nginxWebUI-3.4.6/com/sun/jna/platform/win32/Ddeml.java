package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

public interface Ddeml extends StdCallLibrary {
   Ddeml INSTANCE = (Ddeml)Native.load("user32", Ddeml.class, W32APIOptions.DEFAULT_OPTIONS);
   int XST_NULL = 0;
   int XST_INCOMPLETE = 1;
   int XST_CONNECTED = 2;
   int XST_INIT1 = 3;
   int XST_INIT2 = 4;
   int XST_REQSENT = 5;
   int XST_DATARCVD = 6;
   int XST_POKESENT = 7;
   int XST_POKEACKRCVD = 8;
   int XST_EXECSENT = 9;
   int XST_EXECACKRCVD = 10;
   int XST_ADVSENT = 11;
   int XST_UNADVSENT = 12;
   int XST_ADVACKRCVD = 13;
   int XST_UNADVACKRCVD = 14;
   int XST_ADVDATASENT = 15;
   int XST_ADVDATAACKRCVD = 16;
   int CADV_LATEACK = 65535;
   int ST_CONNECTED = 1;
   int ST_ADVISE = 2;
   int ST_ISLOCAL = 4;
   int ST_BLOCKED = 8;
   int ST_CLIENT = 16;
   int ST_TERMINATED = 32;
   int ST_INLIST = 64;
   int ST_BLOCKNEXT = 128;
   int ST_ISSELF = 256;
   int DDE_FACK = 32768;
   int DDE_FBUSY = 16384;
   int DDE_FDEFERUPD = 16384;
   int DDE_FACKREQ = 32768;
   int DDE_FRELEASE = 8192;
   int DDE_FREQUESTED = 4096;
   int DDE_FAPPSTATUS = 255;
   int DDE_FNOTPROCESSED = 0;
   int DDE_FACKRESERVED = -49408;
   int DDE_FADVRESERVED = -49153;
   int DDE_FDATRESERVED = -45057;
   int DDE_FPOKRESERVED = -8193;
   int MSGF_DDEMGR = 32769;
   int CP_WINANSI = 1004;
   int CP_WINUNICODE = 1200;
   int CP_WINNEUTRAL = 1200;
   int XTYPF_NOBLOCK = 2;
   int XTYPF_NODATA = 4;
   int XTYPF_ACKREQ = 8;
   int XCLASS_MASK = 64512;
   int XCLASS_BOOL = 4096;
   int XCLASS_DATA = 8192;
   int XCLASS_FLAGS = 16384;
   int XCLASS_NOTIFICATION = 32768;
   int XTYP_ERROR = 32770;
   int XTYP_ADVDATA = 16400;
   int XTYP_ADVREQ = 8226;
   int XTYP_ADVSTART = 4144;
   int XTYP_ADVSTOP = 32832;
   int XTYP_EXECUTE = 16464;
   int XTYP_CONNECT = 4194;
   int XTYP_CONNECT_CONFIRM = 32882;
   int XTYP_XACT_COMPLETE = 32896;
   int XTYP_POKE = 16528;
   int XTYP_REGISTER = 32930;
   int XTYP_REQUEST = 8368;
   int XTYP_DISCONNECT = 32962;
   int XTYP_UNREGISTER = 32978;
   int XTYP_WILDCONNECT = 8418;
   int XTYP_MONITOR = 33010;
   int XTYP_MASK = 240;
   int XTYP_SHIFT = 4;
   int TIMEOUT_ASYNC = -1;
   int QID_SYNC = -1;
   String SZDDESYS_TOPIC = "System";
   String SZDDESYS_ITEM_TOPICS = "Topics";
   String SZDDESYS_ITEM_SYSITEMS = "SysItems";
   String SZDDESYS_ITEM_RTNMSG = "ReturnMessage";
   String SZDDESYS_ITEM_STATUS = "Status";
   String SZDDESYS_ITEM_FORMATS = "Formats";
   String SZDDESYS_ITEM_HELP = "Help";
   String SZDDE_ITEM_ITEMLIST = "TopicItemList";
   int DMLERR_NO_ERROR = 0;
   int DMLERR_FIRST = 16384;
   int DMLERR_ADVACKTIMEOUT = 16384;
   int DMLERR_BUSY = 16385;
   int DMLERR_DATAACKTIMEOUT = 16386;
   int DMLERR_DLL_NOT_INITIALIZED = 16387;
   int DMLERR_DLL_USAGE = 16388;
   int DMLERR_EXECACKTIMEOUT = 16389;
   int DMLERR_INVALIDPARAMETER = 16390;
   int DMLERR_LOW_MEMORY = 16391;
   int DMLERR_MEMORY_ERROR = 16392;
   int DMLERR_NOTPROCESSED = 16393;
   int DMLERR_NO_CONV_ESTABLISHED = 16394;
   int DMLERR_POKEACKTIMEOUT = 16395;
   int DMLERR_POSTMSG_FAILED = 16396;
   int DMLERR_REENTRANCY = 16397;
   int DMLERR_SERVER_DIED = 16398;
   int DMLERR_SYS_ERROR = 16399;
   int DMLERR_UNADVACKTIMEOUT = 16400;
   int DMLERR_UNFOUND_QUEUE_ID = 16401;
   int DMLERR_LAST = 16401;
   int HDATA_APPOWNED = 1;
   int CBF_FAIL_SELFCONNECTIONS = 4096;
   int CBF_FAIL_CONNECTIONS = 8192;
   int CBF_FAIL_ADVISES = 16384;
   int CBF_FAIL_EXECUTES = 32768;
   int CBF_FAIL_POKES = 65536;
   int CBF_FAIL_REQUESTS = 131072;
   int CBF_FAIL_ALLSVRXACTIONS = 258048;
   int CBF_SKIP_CONNECT_CONFIRMS = 262144;
   int CBF_SKIP_REGISTRATIONS = 524288;
   int CBF_SKIP_UNREGISTRATIONS = 1048576;
   int CBF_SKIP_DISCONNECTS = 2097152;
   int CBF_SKIP_ALLNOTIFICATIONS = 3932160;
   int APPCMD_CLIENTONLY = 16;
   int APPCMD_FILTERINITS = 32;
   int APPCMD_MASK = 4080;
   int APPCLASS_STANDARD = 0;
   int APPCLASS_MONITOR = 1;
   int APPCLASS_MASK = 15;
   int MF_HSZ_INFO = 16777216;
   int MF_SENDMSGS = 33554432;
   int MF_POSTMSGS = 67108864;
   int MF_CALLBACKS = 134217728;
   int MF_ERRORS = 268435456;
   int MF_LINKS = 536870912;
   int MF_CONV = 1073741824;
   int MF_MASK = -16777216;
   int EC_ENABLEALL = 0;
   int EC_ENABLEONE = 128;
   int EC_DISABLE = 8;
   int EC_QUERYWAITING = 2;
   int DNS_REGISTER = 1;
   int DNS_UNREGISTER = 2;
   int DNS_FILTERON = 4;
   int DNS_FILTEROFF = 8;

   int DdeInitialize(WinDef.DWORDByReference var1, DdeCallback var2, int var3, int var4);

   boolean DdeUninitialize(int var1);

   HCONVLIST DdeConnectList(int var1, HSZ var2, HSZ var3, HCONVLIST var4, CONVCONTEXT var5);

   HCONV DdeQueryNextServer(HCONVLIST var1, HCONV var2);

   boolean DdeDisconnectList(HCONVLIST var1);

   HCONV DdeConnect(int var1, HSZ var2, HSZ var3, CONVCONTEXT var4);

   boolean DdeDisconnect(HCONV var1);

   HCONV DdeReconnect(HCONV var1);

   int DdeQueryConvInfo(HCONV var1, int var2, CONVINFO var3);

   boolean DdeSetUserHandle(HCONV var1, int var2, BaseTSD.DWORD_PTR var3);

   boolean DdeAbandonTransaction(int var1, HCONV var2, int var3);

   boolean DdePostAdvise(int var1, HSZ var2, HSZ var3);

   boolean DdeEnableCallback(int var1, HCONV var2, int var3);

   boolean DdeImpersonateClient(HCONV var1);

   HDDEDATA DdeNameService(int var1, HSZ var2, HSZ var3, int var4);

   HDDEDATA DdeClientTransaction(Pointer var1, int var2, HCONV var3, HSZ var4, int var5, int var6, int var7, WinDef.DWORDByReference var8);

   HDDEDATA DdeCreateDataHandle(int var1, Pointer var2, int var3, int var4, HSZ var5, int var6, int var7);

   HDDEDATA DdeAddData(HDDEDATA var1, Pointer var2, int var3, int var4);

   int DdeGetData(HDDEDATA var1, Pointer var2, int var3, int var4);

   Pointer DdeAccessData(HDDEDATA var1, WinDef.DWORDByReference var2);

   boolean DdeUnaccessData(HDDEDATA var1);

   boolean DdeFreeDataHandle(HDDEDATA var1);

   int DdeGetLastError(int var1);

   HSZ DdeCreateStringHandle(int var1, String var2, int var3);

   int DdeQueryString(int var1, HSZ var2, Pointer var3, int var4, int var5);

   boolean DdeFreeStringHandle(int var1, HSZ var2);

   boolean DdeKeepStringHandle(int var1, HSZ var2);

   public interface DdeCallback extends StdCallLibrary.StdCallCallback {
      WinDef.PVOID ddeCallback(int var1, int var2, HCONV var3, HSZ var4, HSZ var5, HDDEDATA var6, BaseTSD.ULONG_PTR var7, BaseTSD.ULONG_PTR var8);
   }

   @Structure.FieldOrder({"uiLo", "uiHi", "cbData", "Data"})
   public static class DDEML_MSG_HOOK_DATA extends Structure {
      public WinDef.UINT_PTR uiLo;
      public WinDef.UINT_PTR uiHi;
      public int cbData;
      public byte[] Data = new byte[32];
   }

   @Structure.FieldOrder({"cb", "hwndTo", "dwTime", "hTask", "wMsg", "wParam", "lParam", "dmhd"})
   public static class MONMSGSTRUCT extends Structure {
      public int cb;
      public WinDef.HWND hwndTo;
      public int dwTime;
      public WinNT.HANDLE hTask;
      public int wMsg;
      public WinDef.WPARAM wParam;
      public WinDef.LPARAM lParam;
      public DDEML_MSG_HOOK_DATA dmhd;
   }

   @Structure.FieldOrder({"cb", "dwTime", "hTask", "fEstablished", "fNoData", "hszSvc", "hszTopic", "hszItem", "wFmt", "fServer", "hConvServer", "hConvClient"})
   public static class MONLINKSTRUCT extends Structure {
      public int cb;
      public int dwTime;
      public WinNT.HANDLE hTask;
      public WinDef.BOOL fEstablished;
      public WinDef.BOOL fNoData;
      public HSZ hszSvc;
      public HSZ hszTopic;
      public HSZ hszItem;
      public int wFmt;
      public WinDef.BOOL fServer;
      public HCONV hConvServer;
      public HCONV hConvClient;
   }

   @Structure.FieldOrder({"cb", "fsAction", "dwTime", "hsz", "hTask", "str"})
   public static class MONHSZSTRUCT extends Structure {
      public int cb;
      public int fsAction;
      public int dwTime;
      public HSZ hsz;
      public WinNT.HANDLE hTask;
      public byte[] str = new byte[1];

      public void write() {
         this.cb = this.calculateSize(true);
         super.write();
      }

      public void read() {
         this.readField("cb");
         this.allocateMemory(this.cb);
         super.read();
      }

      public String getStr() {
         int offset = this.fieldOffset("str");
         return W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? this.getPointer().getWideString((long)offset) : this.getPointer().getString((long)offset);
      }
   }

   @Structure.FieldOrder({"cb", "wLastError", "dwTime", "hTask"})
   public static class MONERRSTRUCT extends Structure {
      public int cb;
      public int wLastError;
      public int dwTime;
      public WinNT.HANDLE hTask;
   }

   @Structure.FieldOrder({"cb", "fConnect", "dwTime", "hTask", "hszSvc", "hszTopic", "hConvClient", "hConvServer"})
   public static class MONCONVSTRUCT extends Structure {
      public WinDef.UINT cb;
      public WinDef.BOOL fConnect;
      public WinDef.DWORD dwTime;
      public WinNT.HANDLE hTask;
      public HSZ hszSvc;
      public HSZ hszTopic;
      public HCONV hConvClient;
      public HCONV hConvServer;
   }

   @Structure.FieldOrder({"cb", "dwTime", "hTask", "dwRet", "wType", "wFmt", "hConv", "hsz1", "hsz2", "hData", "dwData1", "dwData2", "cc", "cbData", "Data"})
   public static class MONCBSTRUCT extends Structure {
      public int cb;
      public int dwTime;
      public WinNT.HANDLE hTask;
      public WinDef.DWORD dwRet;
      public int wType;
      public int wFmt;
      public HCONV hConv;
      public HSZ hsz1;
      public HSZ hsz2;
      public HDDEDATA hData;
      public BaseTSD.ULONG_PTR dwData1;
      public BaseTSD.ULONG_PTR dwData2;
      public CONVCONTEXT cc;
      public int cbData;
      public byte[] Data = new byte[32];
   }

   @Structure.FieldOrder({"cb", "hUser", "hConvPartner", "hszSvcPartner", "hszServiceReq", "hszTopic", "hszItem", "wFmt", "wType", "wStatus", "wConvst", "wLastError", "hConvList", "ConvCtxt", "hwnd", "hwndPartner"})
   public static class CONVINFO extends Structure {
      public int cb;
      public BaseTSD.DWORD_PTR hUser;
      public HCONV hConvPartner;
      public HSZ hszSvcPartner;
      public HSZ hszServiceReq;
      public HSZ hszTopic;
      public HSZ hszItem;
      public int wFmt;
      public int wType;
      public int wStatus;
      public int wConvst;
      public int wLastError;
      public HCONVLIST hConvList;
      public CONVCONTEXT ConvCtxt;
      public WinDef.HWND hwnd;
      public WinDef.HWND hwndPartner;

      public void write() {
         this.cb = this.size();
         super.write();
      }
   }

   @Structure.FieldOrder({"cb", "wFlags", "wCountryID", "iCodePage", "dwLangID", "dwSecurity", "qos"})
   public static class CONVCONTEXT extends Structure {
      public int cb;
      public int wFlags;
      public int wCountryID;
      public int iCodePage;
      public int dwLangID;
      public int dwSecurity;
      public WinNT.SECURITY_QUALITY_OF_SERVICE qos;

      public CONVCONTEXT() {
      }

      public CONVCONTEXT(Pointer p) {
         super(p);
      }

      public void write() {
         this.cb = this.size();
         super.write();
      }
   }

   @Structure.FieldOrder({"service", "topic"})
   public static class HSZPAIR extends Structure {
      public HSZ service;
      public HSZ topic;

      public HSZPAIR() {
      }

      public HSZPAIR(HSZ service, HSZ topic) {
         this.service = service;
         this.topic = topic;
      }
   }

   public static class HDDEDATA extends WinDef.PVOID {
   }

   public static class HSZ extends PointerType {
   }

   public static class HCONV extends PointerType {
   }

   public static class HCONVLIST extends PointerType {
   }
}
