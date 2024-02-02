/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.Structure.FieldOrder;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.win32.StdCallLibrary;
/*     */ import com.sun.jna.win32.W32APIOptions;
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
/*     */ public interface Winspool
/*     */   extends StdCallLibrary
/*     */ {
/*  49 */   public static final Winspool INSTANCE = (Winspool)Native.load("Winspool.drv", Winspool.class, W32APIOptions.DEFAULT_OPTIONS);
/*     */ 
/*     */   
/*     */   public static final int CCHDEVICENAME = 32;
/*     */ 
/*     */   
/*     */   public static final int PRINTER_STATUS_PAUSED = 1;
/*     */ 
/*     */   
/*     */   public static final int PRINTER_STATUS_ERROR = 2;
/*     */ 
/*     */   
/*     */   public static final int PRINTER_STATUS_PENDING_DELETION = 4;
/*     */ 
/*     */   
/*     */   public static final int PRINTER_STATUS_PAPER_JAM = 8;
/*     */ 
/*     */   
/*     */   public static final int PRINTER_STATUS_PAPER_OUT = 16;
/*     */ 
/*     */   
/*     */   public static final int PRINTER_STATUS_MANUAL_FEED = 32;
/*     */ 
/*     */   
/*     */   public static final int PRINTER_STATUS_PAPER_PROBLEM = 64;
/*     */ 
/*     */   
/*     */   public static final int PRINTER_STATUS_OFFLINE = 128;
/*     */   
/*     */   public static final int PRINTER_STATUS_IO_ACTIVE = 256;
/*     */   
/*     */   public static final int PRINTER_STATUS_BUSY = 512;
/*     */   
/*     */   public static final int PRINTER_STATUS_PRINTING = 1024;
/*     */   
/*     */   public static final int PRINTER_STATUS_OUTPUT_BIN_FULL = 2048;
/*     */   
/*     */   public static final int PRINTER_STATUS_NOT_AVAILABLE = 4096;
/*     */   
/*     */   public static final int PRINTER_STATUS_WAITING = 8192;
/*     */   
/*     */   public static final int PRINTER_STATUS_PROCESSING = 16384;
/*     */   
/*     */   public static final int PRINTER_STATUS_INITIALIZING = 32768;
/*     */   
/*     */   public static final int PRINTER_STATUS_WARMING_UP = 65536;
/*     */   
/*     */   public static final int PRINTER_STATUS_TONER_LOW = 131072;
/*     */   
/*     */   public static final int PRINTER_STATUS_NO_TONER = 262144;
/*     */   
/*     */   public static final int PRINTER_STATUS_PAGE_PUNT = 524288;
/*     */   
/*     */   public static final int PRINTER_STATUS_USER_INTERVENTION = 1048576;
/*     */   
/*     */   public static final int PRINTER_STATUS_OUT_OF_MEMORY = 2097152;
/*     */   
/*     */   public static final int PRINTER_STATUS_DOOR_OPEN = 4194304;
/*     */   
/*     */   public static final int PRINTER_STATUS_SERVER_UNKNOWN = 8388608;
/*     */   
/*     */   public static final int PRINTER_STATUS_POWER_SAVE = 16777216;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_QUEUED = 1;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_DIRECT = 2;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_DEFAULT = 4;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_SHARED = 8;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_NETWORK = 16;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_HIDDEN = 32;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_LOCAL = 64;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_ENABLE_DEVQ = 128;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_KEEPPRINTEDJOBS = 256;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_DO_COMPLETE_FIRST = 512;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_WORK_OFFLINE = 1024;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_ENABLE_BIDI = 2048;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_RAW_ONLY = 4096;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_PUBLISHED = 8192;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_FAX = 16384;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_TS = 32768;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_PUSHED_USER = 131072;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_PUSHED_MACHINE = 262144;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_MACHINE = 524288;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_FRIENDLY_NAME = 1048576;
/*     */   
/*     */   public static final int PRINTER_ATTRIBUTE_TS_GENERIC_DRIVER = 2097152;
/*     */   
/*     */   public static final int PRINTER_CHANGE_ADD_PRINTER = 1;
/*     */   
/*     */   public static final int PRINTER_CHANGE_SET_PRINTER = 2;
/*     */   
/*     */   public static final int PRINTER_CHANGE_DELETE_PRINTER = 4;
/*     */   
/*     */   public static final int PRINTER_CHANGE_FAILED_CONNECTION_PRINTER = 8;
/*     */   
/*     */   public static final int PRINTER_CHANGE_PRINTER = 255;
/*     */   
/*     */   public static final int PRINTER_CHANGE_ADD_JOB = 256;
/*     */   
/*     */   public static final int PRINTER_CHANGE_SET_JOB = 512;
/*     */   
/*     */   public static final int PRINTER_CHANGE_DELETE_JOB = 1024;
/*     */   
/*     */   public static final int PRINTER_CHANGE_WRITE_JOB = 2048;
/*     */   
/*     */   public static final int PRINTER_CHANGE_JOB = 65280;
/*     */   
/*     */   public static final int PRINTER_CHANGE_ADD_FORM = 65536;
/*     */   
/*     */   public static final int PRINTER_CHANGE_SET_FORM = 131072;
/*     */   
/*     */   public static final int PRINTER_CHANGE_DELETE_FORM = 262144;
/*     */   
/*     */   public static final int PRINTER_CHANGE_FORM = 458752;
/*     */   
/*     */   public static final int PRINTER_CHANGE_ADD_PORT = 1048576;
/*     */   
/*     */   public static final int PRINTER_CHANGE_CONFIGURE_PORT = 2097152;
/*     */   
/*     */   public static final int PRINTER_CHANGE_DELETE_PORT = 4194304;
/*     */   
/*     */   public static final int PRINTER_CHANGE_PORT = 7340032;
/*     */   
/*     */   public static final int PRINTER_CHANGE_ADD_PRINT_PROCESSOR = 16777216;
/*     */   
/*     */   public static final int PRINTER_CHANGE_DELETE_PRINT_PROCESSOR = 67108864;
/*     */   
/*     */   public static final int PRINTER_CHANGE_PRINT_PROCESSOR = 117440512;
/*     */   
/*     */   public static final int PRINTER_CHANGE_SERVER = 134217728;
/*     */   
/*     */   public static final int PRINTER_CHANGE_ADD_PRINTER_DRIVER = 268435456;
/*     */   
/*     */   public static final int PRINTER_CHANGE_SET_PRINTER_DRIVER = 536870912;
/*     */   
/*     */   public static final int PRINTER_CHANGE_DELETE_PRINTER_DRIVER = 1073741824;
/*     */   
/*     */   public static final int PRINTER_CHANGE_PRINTER_DRIVER = 1879048192;
/*     */   
/*     */   public static final int PRINTER_CHANGE_TIMEOUT = -2147483648;
/*     */   
/*     */   public static final int PRINTER_CHANGE_ALL_WIN7 = 2138570751;
/*     */   
/*     */   public static final int PRINTER_CHANGE_ALL = 2004353023;
/*     */   
/*     */   public static final int PRINTER_ENUM_DEFAULT = 1;
/*     */   
/*     */   public static final int PRINTER_ENUM_LOCAL = 2;
/*     */   
/*     */   public static final int PRINTER_ENUM_CONNECTIONS = 4;
/*     */   
/*     */   public static final int PRINTER_ENUM_FAVORITE = 4;
/*     */   
/*     */   public static final int PRINTER_ENUM_NAME = 8;
/*     */   
/*     */   public static final int PRINTER_ENUM_REMOTE = 16;
/*     */   
/*     */   public static final int PRINTER_ENUM_SHARED = 32;
/*     */   
/*     */   public static final int PRINTER_ENUM_NETWORK = 64;
/*     */   
/*     */   public static final int PRINTER_ENUM_EXPAND = 16384;
/*     */   
/*     */   public static final int PRINTER_ENUM_CONTAINER = 32768;
/*     */   
/*     */   public static final int PRINTER_ENUM_ICONMASK = 16711680;
/*     */   
/*     */   public static final int PRINTER_ENUM_ICON1 = 65536;
/*     */   
/*     */   public static final int PRINTER_ENUM_ICON2 = 131072;
/*     */   
/*     */   public static final int PRINTER_ENUM_ICON3 = 262144;
/*     */   
/*     */   public static final int PRINTER_ENUM_ICON4 = 524288;
/*     */   
/*     */   public static final int PRINTER_ENUM_ICON5 = 1048576;
/*     */   
/*     */   public static final int PRINTER_ENUM_ICON6 = 2097152;
/*     */   
/*     */   public static final int PRINTER_ENUM_ICON7 = 4194304;
/*     */   
/*     */   public static final int PRINTER_ENUM_ICON8 = 8388608;
/*     */   
/*     */   public static final int PRINTER_ENUM_HIDE = 16777216;
/*     */ 
/*     */   
/*     */   boolean EnumPrinters(int paramInt1, String paramString, int paramInt2, Pointer paramPointer, int paramInt3, IntByReference paramIntByReference1, IntByReference paramIntByReference2);
/*     */ 
/*     */   
/*     */   boolean GetPrinter(WinNT.HANDLE paramHANDLE, int paramInt1, Pointer paramPointer, int paramInt2, IntByReference paramIntByReference);
/*     */ 
/*     */   
/*     */   boolean OpenPrinter(String paramString, WinNT.HANDLEByReference paramHANDLEByReference, LPPRINTER_DEFAULTS paramLPPRINTER_DEFAULTS);
/*     */ 
/*     */   
/*     */   boolean ClosePrinter(WinNT.HANDLE paramHANDLE);
/*     */ 
/*     */   
/*     */   WinNT.HANDLE FindFirstPrinterChangeNotification(WinNT.HANDLE paramHANDLE, int paramInt1, int paramInt2, WinDef.LPVOID paramLPVOID);
/*     */ 
/*     */   
/*     */   boolean FindNextPrinterChangeNotification(WinNT.HANDLE paramHANDLE, WinDef.DWORDByReference paramDWORDByReference, WinDef.LPVOID paramLPVOID1, WinDef.LPVOID paramLPVOID2);
/*     */ 
/*     */   
/*     */   boolean FindClosePrinterChangeNotification(WinNT.HANDLE paramHANDLE);
/*     */ 
/*     */   
/*     */   boolean EnumJobs(WinNT.HANDLE paramHANDLE, int paramInt1, int paramInt2, int paramInt3, Pointer paramPointer, int paramInt4, IntByReference paramIntByReference1, IntByReference paramIntByReference2);
/*     */ 
/*     */   
/*     */   @FieldOrder({"Flags", "pDescription", "pName", "pComment"})
/*     */   public static class PRINTER_INFO_1
/*     */     extends Structure
/*     */   {
/*     */     public int Flags;
/*     */     
/*     */     public String pDescription;
/*     */     
/*     */     public String pName;
/*     */     
/*     */     public String pComment;
/*     */ 
/*     */     
/*     */     public PRINTER_INFO_1() {}
/*     */ 
/*     */     
/*     */     public PRINTER_INFO_1(int size) {
/* 294 */       super((Pointer)new Memory(size));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @FieldOrder({"pServerName", "pPrinterName", "pShareName", "pPortName", "pDriverName", "pComment", "pLocation", "pDevMode", "pSepFile", "pPrintProcessor", "pDatatype", "pParameters", "pSecurityDescriptor", "Attributes", "Priority", "DefaultPriority", "StartTime", "UntilTime", "Status", "cJobs", "AveragePPM"})
/*     */   public static class PRINTER_INFO_2
/*     */     extends Structure
/*     */   {
/*     */     public String pServerName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pPrinterName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pShareName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pPortName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pDriverName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pComment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pLocation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WinDef.INT_PTR pDevMode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pSepFile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pPrintProcessor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pDatatype;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WinDef.INT_PTR pSecurityDescriptor;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int Attributes;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int Priority;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int DefaultPriority;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int StartTime;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int UntilTime;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int Status;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int cJobs;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int AveragePPM;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PRINTER_INFO_2() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PRINTER_INFO_2(int size) {
/* 431 */       super((Pointer)new Memory(size));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasAttribute(int value) {
/* 439 */       return ((this.Attributes & value) == value);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @FieldOrder({"pPrinterName", "pServerName", "Attributes"})
/*     */   public static class PRINTER_INFO_4
/*     */     extends Structure
/*     */   {
/*     */     public String pPrinterName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String pServerName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WinDef.DWORD Attributes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PRINTER_INFO_4() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PRINTER_INFO_4(int size) {
/* 477 */       super((Pointer)new Memory(size));
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
/*     */   @FieldOrder({"pDatatype", "pDevMode", "DesiredAccess"})
/*     */   public static class LPPRINTER_DEFAULTS
/*     */     extends Structure
/*     */   {
/*     */     public String pDatatype;
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
/*     */     public Pointer pDevMode;
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
/*     */     public int DesiredAccess;
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
/*     */   @FieldOrder({"JobId", "pPrinterName", "pMachineName", "pUserName", "pDocument", "pDatatype", "pStatus", "Status", "Priority", "Position", "TotalPages", "PagesPrinted", "Submitted"})
/*     */   public static class JOB_INFO_1
/*     */     extends Structure
/*     */   {
/*     */     public int JobId;
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
/*     */     public String pPrinterName;
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
/*     */     public String pMachineName;
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
/*     */     public String pUserName;
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
/*     */     public String pDocument;
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
/*     */     public String pDatatype;
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
/*     */     public String pStatus;
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
/*     */     public int Status;
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
/*     */     public int Priority;
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
/*     */     public int Position;
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
/*     */     public int TotalPages;
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
/*     */     public int PagesPrinted;
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
/*     */     public WinBase.SYSTEMTIME Submitted;
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
/*     */     public JOB_INFO_1() {}
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
/*     */     public JOB_INFO_1(int size) {
/* 866 */       super((Pointer)new Memory(size));
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Winspool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */