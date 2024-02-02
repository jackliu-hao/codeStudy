/*      */ package com.sun.jna.platform.win32;
/*      */ 
/*      */ import com.sun.jna.Callback;
/*      */ import com.sun.jna.Native;
/*      */ import com.sun.jna.Platform;
/*      */ import com.sun.jna.Pointer;
/*      */ import com.sun.jna.Structure;
/*      */ import com.sun.jna.Structure.FieldOrder;
/*      */ import com.sun.jna.Union;
/*      */ import com.sun.jna.ptr.ByteByReference;
/*      */ import com.sun.jna.win32.StdCallLibrary;
/*      */ import com.sun.jna.win32.W32APITypeMapper;
/*      */ import java.text.DateFormat;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public interface WinBase
/*      */   extends WinDef, BaseTSD
/*      */ {
/*   51 */   public static final WinNT.HANDLE INVALID_HANDLE_VALUE = new WinNT.HANDLE(
/*   52 */       Pointer.createConstant((Native.POINTER_SIZE == 8) ? -1L : 4294967295L)); public static final int WAIT_FAILED = -1; public static final int WAIT_OBJECT_0 = 0; public static final int WAIT_ABANDONED = 128; public static final int WAIT_ABANDONED_0 = 128; public static final int LOGON32_LOGON_INTERACTIVE = 2; public static final int LOGON32_LOGON_NETWORK = 3; public static final int LOGON32_LOGON_BATCH = 4; public static final int LOGON32_LOGON_SERVICE = 5; public static final int LOGON32_LOGON_UNLOCK = 7; public static final int LOGON32_LOGON_NETWORK_CLEARTEXT = 8; public static final int LOGON32_LOGON_NEW_CREDENTIALS = 9; public static final int LOGON32_PROVIDER_DEFAULT = 0; public static final int LOGON32_PROVIDER_WINNT35 = 1; public static final int LOGON32_PROVIDER_WINNT40 = 2; public static final int LOGON32_PROVIDER_WINNT50 = 3; public static final int HANDLE_FLAG_INHERIT = 1; public static final int HANDLE_FLAG_PROTECT_FROM_CLOSE = 2; public static final int STARTF_USESHOWWINDOW = 1; public static final int STARTF_USESIZE = 2; public static final int STARTF_USEPOSITION = 4; public static final int STARTF_USECOUNTCHARS = 8; public static final int STARTF_USEFILLATTRIBUTE = 16; public static final int STARTF_RUNFULLSCREEN = 32; public static final int STARTF_FORCEONFEEDBACK = 64; public static final int STARTF_FORCEOFFFEEDBACK = 128; public static final int STARTF_USESTDHANDLES = 256; public static final int DEBUG_PROCESS = 1; public static final int DEBUG_ONLY_THIS_PROCESS = 2; public static final int CREATE_SUSPENDED = 4; public static final int DETACHED_PROCESS = 8; public static final int CREATE_NEW_CONSOLE = 16; public static final int CREATE_NEW_PROCESS_GROUP = 512; public static final int CREATE_UNICODE_ENVIRONMENT = 1024; public static final int CREATE_SEPARATE_WOW_VDM = 2048; public static final int CREATE_SHARED_WOW_VDM = 4096; public static final int CREATE_FORCEDOS = 8192; public static final int INHERIT_PARENT_AFFINITY = 65536; public static final int CREATE_PROTECTED_PROCESS = 262144; public static final int EXTENDED_STARTUPINFO_PRESENT = 524288; public static final int CREATE_BREAKAWAY_FROM_JOB = 16777216; public static final int CREATE_PRESERVE_CODE_AUTHZ_LEVEL = 33554432; public static final int CREATE_DEFAULT_ERROR_MODE = 67108864; public static final int CREATE_NO_WINDOW = 134217728; public static final int FILE_ENCRYPTABLE = 0; public static final int FILE_IS_ENCRYPTED = 1; public static final int FILE_SYSTEM_ATTR = 2; public static final int FILE_ROOT_DIR = 3; public static final int FILE_SYSTEM_DIR = 4; public static final int FILE_UNKNOWN = 5; public static final int FILE_SYSTEM_NOT_SUPPORT = 6; public static final int FILE_USER_DISALLOWED = 7; public static final int FILE_READ_ONLY = 8; public static final int FILE_DIR_DISALOWED = 9; public static final int CREATE_FOR_IMPORT = 1; public static final int CREATE_FOR_DIR = 2; public static final int OVERWRITE_HIDDEN = 4; public static final int INVALID_FILE_SIZE = -1; public static final int INVALID_SET_FILE_POINTER = -1; public static final int INVALID_FILE_ATTRIBUTES = -1; public static final int STILL_ACTIVE = 259; public static final int FileBasicInfo = 0; public static final int FileStandardInfo = 1; public static final int FileNameInfo = 2; public static final int FileRenameInfo = 3; public static final int FileDispositionInfo = 4; public static final int FileAllocationInfo = 5; public static final int FileEndOfFileInfo = 6; public static final int FileStreamInfo = 7; public static final int FileCompressionInfo = 8; public static final int FileAttributeTagInfo = 9; public static final int FileIdBothDirectoryInfo = 10; public static final int FileIdBothDirectoryRestartInfo = 11; public static final int FileIoPriorityHintInfo = 12; public static final int FileRemoteProtocolInfo = 13; public static final int FileFullDirectoryInfo = 14; public static final int FileFullDirectoryRestartInfo = 15; public static final int FileStorageInfo = 16;
/*      */   public static final int FileAlignmentInfo = 17;
/*      */   public static final int FileIdInfo = 18;
/*      */   public static final int FileIdExtdDirectoryInfo = 19;
/*      */   public static final int FileIdExtdDirectoryRestartInfo = 20;
/*      */   public static final int FindExInfoStandard = 0;
/*      */   public static final int FindExInfoBasic = 1;
/*      */   public static final int FindExInfoMaxInfoLevel = 2;
/*      */   public static final int FindExSearchNameMatch = 0;
/*      */   public static final int FindExSearchLimitToDirectories = 1;
/*      */   public static final int FindExSearchLimitToDevices = 2;
/*      */   public static final int LMEM_FIXED = 0;
/*   64 */   public static final int MAX_COMPUTERNAME_LENGTH = Platform.isMac() ? 15 : 31;
/*      */   
/*      */   public static final int LMEM_MOVEABLE = 2;
/*      */   
/*      */   public static final int LMEM_NOCOMPACT = 16;
/*      */   
/*      */   public static final int LMEM_NODISCARD = 32;
/*      */   
/*      */   public static final int LMEM_ZEROINIT = 64;
/*      */   
/*      */   public static final int LMEM_MODIFY = 128;
/*      */   
/*      */   public static final int LMEM_DISCARDABLE = 3840;
/*      */   
/*      */   public static final int LMEM_VALID_FLAGS = 3954;
/*      */   
/*      */   public static final int LMEM_INVALID_HANDLE = 32768;
/*      */   
/*      */   public static final int LHND = 66;
/*      */   
/*      */   public static final int LPTR = 64;
/*      */   
/*      */   public static final int LMEM_DISCARDED = 16384;
/*      */   
/*      */   public static final int LMEM_LOCKCOUNT = 255;
/*      */   
/*      */   public static final int FORMAT_MESSAGE_ALLOCATE_BUFFER = 256;
/*      */   
/*      */   public static final int FORMAT_MESSAGE_IGNORE_INSERTS = 512;
/*      */   
/*      */   public static final int FORMAT_MESSAGE_FROM_STRING = 1024;
/*      */   
/*      */   public static final int FORMAT_MESSAGE_FROM_HMODULE = 2048;
/*      */   
/*      */   public static final int FORMAT_MESSAGE_FROM_SYSTEM = 4096;
/*      */   
/*      */   public static final int FORMAT_MESSAGE_ARGUMENT_ARRAY = 8192;
/*      */   
/*      */   public static final int DRIVE_UNKNOWN = 0;
/*      */   
/*      */   public static final int DRIVE_NO_ROOT_DIR = 1;
/*      */   
/*      */   public static final int DRIVE_REMOVABLE = 2;
/*      */   
/*      */   public static final int DRIVE_FIXED = 3;
/*      */   
/*      */   public static final int DRIVE_REMOTE = 4;
/*      */   
/*      */   public static final int DRIVE_CDROM = 5;
/*      */   
/*      */   public static final int DRIVE_RAMDISK = 6;
/*      */   
/*      */   public static final int INFINITE = -1;
/*      */   
/*      */   public static final int MOVEFILE_COPY_ALLOWED = 2;
/*      */   
/*      */   public static final int MOVEFILE_CREATE_HARDLINK = 16;
/*      */   
/*      */   public static final int MOVEFILE_DELAY_UNTIL_REBOOT = 4;
/*      */   
/*      */   public static final int MOVEFILE_FAIL_IF_NOT_TRACKABLE = 32;
/*      */   
/*      */   public static final int MOVEFILE_REPLACE_EXISTING = 1;
/*      */   
/*      */   public static final int MOVEFILE_WRITE_THROUGH = 8;
/*      */   
/*      */   public static final int PIPE_CLIENT_END = 0;
/*      */   
/*      */   public static final int PIPE_SERVER_END = 1;
/*      */   
/*      */   public static final int PIPE_ACCESS_DUPLEX = 3;
/*      */   
/*      */   public static final int PIPE_ACCESS_INBOUND = 1;
/*      */   
/*      */   public static final int PIPE_ACCESS_OUTBOUND = 2;
/*      */   
/*      */   public static final int PIPE_TYPE_BYTE = 0;
/*      */   
/*      */   public static final int PIPE_TYPE_MESSAGE = 4;
/*      */   
/*      */   public static final int PIPE_READMODE_BYTE = 0;
/*      */   
/*      */   public static final int PIPE_READMODE_MESSAGE = 2;
/*      */   
/*      */   public static final int PIPE_WAIT = 0;
/*      */   
/*      */   public static final int PIPE_NOWAIT = 1;
/*      */   
/*      */   public static final int PIPE_ACCEPT_REMOTE_CLIENTS = 0;
/*      */   
/*      */   public static final int PIPE_REJECT_REMOTE_CLIENTS = 8;
/*      */   
/*      */   public static final int PIPE_UNLIMITED_INSTANCES = 255;
/*      */   
/*      */   public static final int NMPWAIT_USE_DEFAULT_WAIT = 0;
/*      */   
/*      */   public static final int NMPWAIT_NOWAIT = 1;
/*      */   
/*      */   public static final int NMPWAIT_WAIT_FOREVER = -1;
/*      */   
/*      */   public static final int NOPARITY = 0;
/*      */   
/*      */   public static final int ODDPARITY = 1;
/*      */   
/*      */   public static final int EVENPARITY = 2;
/*      */   
/*      */   public static final int MARKPARITY = 3;
/*      */   
/*      */   public static final int SPACEPARITY = 4;
/*      */   
/*      */   public static final int ONESTOPBIT = 0;
/*      */   
/*      */   public static final int ONE5STOPBITS = 1;
/*      */   
/*      */   public static final int TWOSTOPBITS = 2;
/*      */   
/*      */   public static final int CBR_110 = 110;
/*      */   
/*      */   public static final int CBR_300 = 300;
/*      */   
/*      */   public static final int CBR_600 = 600;
/*      */   
/*      */   public static final int CBR_1200 = 1200;
/*      */   
/*      */   public static final int CBR_2400 = 2400;
/*      */   
/*      */   public static final int CBR_4800 = 4800;
/*      */   
/*      */   public static final int CBR_9600 = 9600;
/*      */   
/*      */   public static final int CBR_14400 = 14400;
/*      */   
/*      */   public static final int CBR_19200 = 19200;
/*      */   
/*      */   public static final int CBR_38400 = 38400;
/*      */   public static final int CBR_56000 = 56000;
/*      */   public static final int CBR_128000 = 128000;
/*      */   public static final int CBR_256000 = 256000;
/*      */   public static final int DTR_CONTROL_DISABLE = 0;
/*      */   public static final int DTR_CONTROL_ENABLE = 1;
/*      */   public static final int DTR_CONTROL_HANDSHAKE = 2;
/*      */   public static final int RTS_CONTROL_DISABLE = 0;
/*      */   public static final int RTS_CONTROL_ENABLE = 1;
/*      */   public static final int RTS_CONTROL_HANDSHAKE = 2;
/*      */   public static final int RTS_CONTROL_TOGGLE = 3;
/*      */   public static final int ES_AWAYMODE_REQUIRED = 64;
/*      */   public static final int ES_CONTINUOUS = -2147483648;
/*      */   public static final int ES_DISPLAY_REQUIRED = 2;
/*      */   public static final int ES_SYSTEM_REQUIRED = 1;
/*      */   public static final int ES_USER_PRESENT = 4;
/*      */   public static final int MUTEX_MODIFY_STATE = 1;
/*      */   public static final int MUTEX_ALL_ACCESS = 2031617;
/*      */   
/*      */   @FieldOrder({"CreationTime", "LastAccessTime", "LastWriteTime", "ChangeTime", "FileAttributes"})
/*      */   public static class FILE_BASIC_INFO
/*      */     extends Structure
/*      */   {
/*      */     public WinNT.LARGE_INTEGER CreationTime;
/*      */     public WinNT.LARGE_INTEGER LastAccessTime;
/*      */     public WinNT.LARGE_INTEGER LastWriteTime;
/*      */     public WinNT.LARGE_INTEGER ChangeTime;
/*      */     public int FileAttributes;
/*      */     
/*      */     public static class ByReference
/*      */       extends FILE_BASIC_INFO
/*      */       implements Structure.ByReference
/*      */     {
/*      */       public ByReference(Pointer memory) {
/*  232 */         super(memory);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ByReference() {}
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int sizeOf() {
/*  266 */       return Native.getNativeSize(FILE_BASIC_INFO.class, null);
/*      */     }
/*      */ 
/*      */     
/*      */     public FILE_BASIC_INFO() {}
/*      */ 
/*      */     
/*      */     public FILE_BASIC_INFO(Pointer memory) {
/*  274 */       super(memory);
/*  275 */       read();
/*      */       
/*  277 */       this.CreationTime = new WinNT.LARGE_INTEGER(this.CreationTime.getValue());
/*  278 */       this.LastAccessTime = new WinNT.LARGE_INTEGER(this.LastAccessTime.getValue());
/*  279 */       this.LastWriteTime = new WinNT.LARGE_INTEGER(this.LastWriteTime.getValue());
/*  280 */       this.ChangeTime = new WinNT.LARGE_INTEGER(this.ChangeTime.getValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FILE_BASIC_INFO(WinBase.FILETIME CreationTime, WinBase.FILETIME LastAccessTime, WinBase.FILETIME LastWriteTime, WinBase.FILETIME ChangeTime, int FileAttributes) {
/*  288 */       this.CreationTime = new WinNT.LARGE_INTEGER(CreationTime.toTime());
/*  289 */       this.LastAccessTime = new WinNT.LARGE_INTEGER(LastAccessTime.toTime());
/*  290 */       this.LastWriteTime = new WinNT.LARGE_INTEGER(LastWriteTime.toTime());
/*  291 */       this.ChangeTime = new WinNT.LARGE_INTEGER(ChangeTime.toTime());
/*  292 */       this.FileAttributes = FileAttributes;
/*  293 */       write();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FILE_BASIC_INFO(WinNT.LARGE_INTEGER CreationTime, WinNT.LARGE_INTEGER LastAccessTime, WinNT.LARGE_INTEGER LastWriteTime, WinNT.LARGE_INTEGER ChangeTime, int FileAttributes) {
/*  301 */       this.CreationTime = CreationTime;
/*  302 */       this.LastAccessTime = LastAccessTime;
/*  303 */       this.LastWriteTime = LastWriteTime;
/*  304 */       this.ChangeTime = ChangeTime;
/*  305 */       this.FileAttributes = FileAttributes;
/*  306 */       write();
/*      */     } }
/*      */   
/*      */   @FieldOrder({"AllocationSize", "EndOfFile", "NumberOfLinks", "DeletePending", "Directory"})
/*      */   public static class FILE_STANDARD_INFO extends Structure {
/*      */     public WinNT.LARGE_INTEGER AllocationSize;
/*      */     public WinNT.LARGE_INTEGER EndOfFile;
/*      */     public int NumberOfLinks;
/*      */     public boolean DeletePending;
/*      */     public boolean Directory;
/*      */     
/*      */     public static class ByReference extends FILE_STANDARD_INFO implements Structure.ByReference {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(Pointer memory) {
/*  321 */         super(memory);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int sizeOf() {
/*  352 */       return Native.getNativeSize(FILE_STANDARD_INFO.class, null);
/*      */     }
/*      */ 
/*      */     
/*      */     public FILE_STANDARD_INFO() {}
/*      */ 
/*      */     
/*      */     public FILE_STANDARD_INFO(Pointer memory) {
/*  360 */       super(memory);
/*  361 */       read();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FILE_STANDARD_INFO(WinNT.LARGE_INTEGER AllocationSize, WinNT.LARGE_INTEGER EndOfFile, int NumberOfLinks, boolean DeletePending, boolean Directory) {
/*  369 */       this.AllocationSize = AllocationSize;
/*  370 */       this.EndOfFile = EndOfFile;
/*  371 */       this.NumberOfLinks = NumberOfLinks;
/*  372 */       this.DeletePending = DeletePending;
/*  373 */       this.Directory = Directory;
/*  374 */       write();
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"DeleteFile"})
/*      */   public static class FILE_DISPOSITION_INFO
/*      */     extends Structure {
/*      */     public boolean DeleteFile;
/*      */     
/*      */     public static class ByReference
/*      */       extends FILE_DISPOSITION_INFO
/*      */       implements Structure.ByReference {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(Pointer memory) {
/*  389 */         super(memory);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int sizeOf() {
/*  401 */       return Native.getNativeSize(FILE_DISPOSITION_INFO.class, null);
/*      */     }
/*      */ 
/*      */     
/*      */     public FILE_DISPOSITION_INFO() {}
/*      */ 
/*      */     
/*      */     public FILE_DISPOSITION_INFO(Pointer memory) {
/*  409 */       super(memory);
/*  410 */       read();
/*      */     }
/*      */     
/*      */     public FILE_DISPOSITION_INFO(boolean DeleteFile) {
/*  414 */       this.DeleteFile = DeleteFile;
/*  415 */       write();
/*      */     } }
/*      */   
/*      */   @FieldOrder({"CompressedFileSize", "CompressionFormat", "CompressionUnitShift", "ChunkShift", "ClusterShift", "Reserved"})
/*      */   public static class FILE_COMPRESSION_INFO extends Structure {
/*      */     public WinNT.LARGE_INTEGER CompressedFileSize;
/*      */     public short CompressionFormat;
/*      */     public byte CompressionUnitShift;
/*      */     public byte ChunkShift;
/*      */     public byte ClusterShift;
/*      */     
/*      */     public static class ByReference extends FILE_COMPRESSION_INFO implements Structure.ByReference {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(Pointer memory) {
/*  430 */         super(memory);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  462 */     public byte[] Reserved = new byte[3];
/*      */ 
/*      */     
/*      */     public static int sizeOf() {
/*  466 */       return Native.getNativeSize(FILE_COMPRESSION_INFO.class, null);
/*      */     }
/*      */     
/*      */     public FILE_COMPRESSION_INFO() {
/*  470 */       super(W32APITypeMapper.DEFAULT);
/*      */     }
/*      */     
/*      */     public FILE_COMPRESSION_INFO(Pointer memory) {
/*  474 */       super(memory, 0, W32APITypeMapper.DEFAULT);
/*  475 */       read();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FILE_COMPRESSION_INFO(WinNT.LARGE_INTEGER CompressedFileSize, short CompressionFormat, byte CompressionUnitShift, byte ChunkShift, byte ClusterShift) {
/*  483 */       this.CompressedFileSize = CompressedFileSize;
/*  484 */       this.CompressionFormat = CompressionFormat;
/*  485 */       this.CompressionUnitShift = CompressionUnitShift;
/*  486 */       this.ChunkShift = ChunkShift;
/*  487 */       this.ClusterShift = ClusterShift;
/*  488 */       this.Reserved = new byte[3];
/*  489 */       write();
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"FileAttributes", "ReparseTag"})
/*      */   public static class FILE_ATTRIBUTE_TAG_INFO
/*      */     extends Structure {
/*      */     public int FileAttributes;
/*      */     public int ReparseTag;
/*      */     
/*      */     public static class ByReference
/*      */       extends FILE_ATTRIBUTE_TAG_INFO implements Structure.ByReference {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(Pointer memory) {
/*  504 */         super(memory);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int sizeOf() {
/*  520 */       return Native.getNativeSize(FILE_ATTRIBUTE_TAG_INFO.class, null);
/*      */     }
/*      */ 
/*      */     
/*      */     public FILE_ATTRIBUTE_TAG_INFO() {}
/*      */ 
/*      */     
/*      */     public FILE_ATTRIBUTE_TAG_INFO(Pointer memory) {
/*  528 */       super(memory);
/*  529 */       read();
/*      */     }
/*      */ 
/*      */     
/*      */     public FILE_ATTRIBUTE_TAG_INFO(int FileAttributes, int ReparseTag) {
/*  534 */       this.FileAttributes = FileAttributes;
/*  535 */       this.ReparseTag = ReparseTag;
/*  536 */       write();
/*      */     }
/*      */   }
/*      */   
/*      */   @FieldOrder({"VolumeSerialNumber", "FileId"})
/*      */   public static class FILE_ID_INFO
/*      */     extends Structure
/*      */   {
/*      */     public long VolumeSerialNumber;
/*      */     public FILE_ID_128 FileId;
/*      */     
/*      */     public static class ByReference
/*      */       extends FILE_ID_INFO
/*      */       implements Structure.ByReference {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(Pointer memory) {
/*  553 */         super(memory);
/*      */       }
/*      */     }
/*      */     
/*      */     @FieldOrder({"Identifier"})
/*      */     public static class FILE_ID_128 extends Structure {
/*  559 */       public WinDef.BYTE[] Identifier = new WinDef.BYTE[16];
/*      */ 
/*      */       
/*      */       public FILE_ID_128() {}
/*      */ 
/*      */       
/*      */       public FILE_ID_128(Pointer memory) {
/*  566 */         super(memory);
/*  567 */         read();
/*      */       }
/*      */       
/*      */       public FILE_ID_128(WinDef.BYTE[] Identifier) {
/*  571 */         this.Identifier = Identifier;
/*  572 */         write();
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static int sizeOf() {
/*  588 */       return Native.getNativeSize(FILE_ID_INFO.class, null);
/*      */     }
/*      */ 
/*      */     
/*      */     public FILE_ID_INFO() {}
/*      */ 
/*      */     
/*      */     public FILE_ID_INFO(Pointer memory) {
/*  596 */       super(memory);
/*  597 */       read();
/*      */     }
/*      */ 
/*      */     
/*      */     public FILE_ID_INFO(long VolumeSerialNumber, FILE_ID_128 FileId) {
/*  602 */       this.VolumeSerialNumber = VolumeSerialNumber;
/*  603 */       this.FileId = FileId;
/*  604 */       write();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwFileAttributes", "ftCreationTime", "ftLastAccessTime", "ftLastWriteTime", "nFileSizeHigh", "nFileSizeLow", "dwReserved0", "dwReserved1", "cFileName", "cAlternateFileName"})
/*      */   public static class WIN32_FIND_DATA
/*      */     extends Structure
/*      */   {
/*      */     public int dwFileAttributes;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinBase.FILETIME ftCreationTime;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinBase.FILETIME ftLastAccessTime;
/*      */ 
/*      */ 
/*      */     
/*      */     public WinBase.FILETIME ftLastWriteTime;
/*      */ 
/*      */ 
/*      */     
/*      */     public int nFileSizeHigh;
/*      */ 
/*      */ 
/*      */     
/*      */     public int nFileSizeLow;
/*      */ 
/*      */ 
/*      */     
/*      */     public int dwReserved0;
/*      */ 
/*      */ 
/*      */     
/*      */     public int dwReserved1;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends WIN32_FIND_DATA
/*      */       implements Structure.ByReference
/*      */     {
/*      */       public ByReference() {}
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ByReference(Pointer memory) {
/*  661 */         super(memory);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  738 */     public char[] cFileName = new char[260];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  746 */     public char[] cAlternateFileName = new char[14];
/*      */     
/*      */     public static int sizeOf() {
/*  749 */       return Native.getNativeSize(WIN32_FIND_DATA.class, null);
/*      */     }
/*      */     
/*      */     public WIN32_FIND_DATA() {
/*  753 */       super(W32APITypeMapper.DEFAULT);
/*      */     }
/*      */     
/*      */     public WIN32_FIND_DATA(Pointer memory) {
/*  757 */       super(memory, 0, W32APITypeMapper.DEFAULT);
/*  758 */       read();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WIN32_FIND_DATA(int dwFileAttributes, WinBase.FILETIME ftCreationTime, WinBase.FILETIME ftLastAccessTime, WinBase.FILETIME ftLastWriteTime, int nFileSizeHigh, int nFileSizeLow, int dwReserved0, int dwReserved1, char[] cFileName, char[] cAlternateFileName) {
/*  771 */       this.dwFileAttributes = dwFileAttributes;
/*  772 */       this.ftCreationTime = ftCreationTime;
/*  773 */       this.ftLastAccessTime = ftLastAccessTime;
/*  774 */       this.ftLastWriteTime = ftLastWriteTime;
/*  775 */       this.nFileSizeHigh = nFileSizeHigh;
/*  776 */       this.nFileSizeLow = nFileSizeLow;
/*  777 */       this.dwReserved0 = dwReserved0;
/*  778 */       this.cFileName = cFileName;
/*  779 */       this.cAlternateFileName = cAlternateFileName;
/*  780 */       write();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getFileName() {
/*  787 */       return Native.toString(this.cFileName);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getAlternateFileName() {
/*  794 */       return Native.toString(this.cAlternateFileName);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwLowDateTime", "dwHighDateTime"})
/*      */   public static class FILETIME
/*      */     extends Structure
/*      */   {
/*      */     public int dwLowDateTime;
/*      */     public int dwHighDateTime;
/*      */     private static final long EPOCH_DIFF = 11644473600000L;
/*      */     
/*      */     public static class ByReference
/*      */       extends FILETIME
/*      */       implements Structure.ByReference
/*      */     {
/*      */       public ByReference() {}
/*      */       
/*      */       public ByReference(Pointer memory) {
/*  814 */         super(memory);
/*      */       }
/*      */     }
/*      */     
/*      */     public FILETIME(Date date) {
/*  819 */       long rawValue = dateToFileTime(date);
/*  820 */       this.dwHighDateTime = (int)(rawValue >> 32L & 0xFFFFFFFFL);
/*  821 */       this.dwLowDateTime = (int)(rawValue & 0xFFFFFFFFL);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FILETIME(WinNT.LARGE_INTEGER ft) {
/*  829 */       this.dwHighDateTime = ft.getHigh().intValue();
/*  830 */       this.dwLowDateTime = ft.getLow().intValue();
/*      */     }
/*      */ 
/*      */     
/*      */     public FILETIME() {}
/*      */     
/*      */     public FILETIME(Pointer memory) {
/*  837 */       super(memory);
/*  838 */       read();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static Date filetimeToDate(int high, int low) {
/*  864 */       long filetime = high << 32L | low & 0xFFFFFFFFL;
/*  865 */       long ms_since_16010101 = filetime / 10000L;
/*  866 */       long ms_since_19700101 = ms_since_16010101 - 11644473600000L;
/*  867 */       return new Date(ms_since_19700101);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static long dateToFileTime(Date date) {
/*  879 */       long ms_since_19700101 = date.getTime();
/*  880 */       long ms_since_16010101 = ms_since_19700101 + 11644473600000L;
/*  881 */       return ms_since_16010101 * 1000L * 10L;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Date toDate() {
/*  889 */       return filetimeToDate(this.dwHighDateTime, this.dwLowDateTime);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long toTime() {
/*  899 */       return toDate().getTime();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORDLONG toDWordLong() {
/*  910 */       return new WinDef.DWORDLONG(this.dwHighDateTime << 32L | this.dwLowDateTime & 0xFFFFFFFFL);
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/*  915 */       return super.toString() + ": " + toDate().toString();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"wYear", "wMonth", "wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond", "wMilliseconds"})
/*      */   public static class SYSTEMTIME
/*      */     extends Structure
/*      */   {
/*      */     public short wYear;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public short wMonth;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public short wDayOfWeek;
/*      */ 
/*      */ 
/*      */     
/*      */     public short wDay;
/*      */ 
/*      */ 
/*      */     
/*      */     public short wHour;
/*      */ 
/*      */ 
/*      */     
/*      */     public short wMinute;
/*      */ 
/*      */ 
/*      */     
/*      */     public short wSecond;
/*      */ 
/*      */ 
/*      */     
/*      */     public short wMilliseconds;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public SYSTEMTIME() {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public SYSTEMTIME(Date date) {
/*  968 */       this(date.getTime());
/*      */     }
/*      */     
/*      */     public SYSTEMTIME(long timestamp) {
/*  972 */       Calendar cal = Calendar.getInstance();
/*  973 */       cal.setTimeInMillis(timestamp);
/*  974 */       fromCalendar(cal);
/*      */     }
/*      */     
/*      */     public SYSTEMTIME(Calendar cal) {
/*  978 */       fromCalendar(cal);
/*      */     }
/*      */     
/*      */     public void fromCalendar(Calendar cal) {
/*  982 */       this.wYear = (short)cal.get(1);
/*  983 */       this.wMonth = (short)(1 + cal.get(2) - 0);
/*  984 */       this.wDay = (short)cal.get(5);
/*  985 */       this.wHour = (short)cal.get(11);
/*  986 */       this.wMinute = (short)cal.get(12);
/*  987 */       this.wSecond = (short)cal.get(13);
/*  988 */       this.wMilliseconds = (short)cal.get(14);
/*  989 */       this.wDayOfWeek = (short)(cal.get(7) - 1);
/*      */     }
/*      */     
/*      */     public Calendar toCalendar() {
/*  993 */       Calendar cal = Calendar.getInstance();
/*  994 */       cal.set(1, this.wYear);
/*  995 */       cal.set(2, 0 + this.wMonth - 1);
/*  996 */       cal.set(5, this.wDay);
/*  997 */       cal.set(11, this.wHour);
/*  998 */       cal.set(12, this.wMinute);
/*  999 */       cal.set(13, this.wSecond);
/* 1000 */       cal.set(14, this.wMilliseconds);
/* 1001 */       return cal;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public String toString() {
/* 1007 */       if (this.wYear == 0 && this.wMonth == 0 && this.wDay == 0 && this.wHour == 0 && this.wMinute == 0 && this.wSecond == 0 && this.wMilliseconds == 0)
/*      */       {
/*      */         
/* 1010 */         return super.toString();
/*      */       }
/*      */       
/* 1013 */       DateFormat dtf = DateFormat.getDateTimeInstance();
/* 1014 */       Calendar cal = toCalendar();
/* 1015 */       return dtf.format(cal.getTime());
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"Bias", "StandardName", "StandardDate", "StandardBias", "DaylightName", "DaylightDate", "DaylightBias"})
/*      */   public static class TIME_ZONE_INFORMATION
/*      */     extends Structure
/*      */   {
/*      */     public WinDef.LONG Bias;
/*      */     
/*      */     public String StandardName;
/*      */     public WinBase.SYSTEMTIME StandardDate;
/*      */     public WinDef.LONG StandardBias;
/*      */     public String DaylightName;
/*      */     public WinBase.SYSTEMTIME DaylightDate;
/*      */     public WinDef.LONG DaylightBias;
/*      */     
/*      */     public TIME_ZONE_INFORMATION() {
/* 1034 */       super(W32APITypeMapper.DEFAULT);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"Internal", "InternalHigh", "Offset", "OffsetHigh", "hEvent"})
/*      */   public static class OVERLAPPED
/*      */     extends Structure
/*      */   {
/*      */     public BaseTSD.ULONG_PTR Internal;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BaseTSD.ULONG_PTR InternalHigh;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int Offset;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int OffsetHigh;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinNT.HANDLE hEvent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"processorArchitecture", "dwPageSize", "lpMinimumApplicationAddress", "lpMaximumApplicationAddress", "dwActiveProcessorMask", "dwNumberOfProcessors", "dwProcessorType", "dwAllocationGranularity", "wProcessorLevel", "wProcessorRevision"})
/*      */   public static class SYSTEM_INFO
/*      */     extends Structure
/*      */   {
/*      */     public UNION processorArchitecture;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwPageSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Pointer lpMinimumApplicationAddress;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Pointer lpMaximumApplicationAddress;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public BaseTSD.DWORD_PTR dwActiveProcessorMask;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwNumberOfProcessors;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwProcessorType;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwAllocationGranularity;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD wProcessorLevel;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD wProcessorRevision;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @FieldOrder({"wProcessorArchitecture", "wReserved"})
/*      */     public static class PI
/*      */       extends Structure
/*      */     {
/*      */       public WinDef.WORD wProcessorArchitecture;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public WinDef.WORD wReserved;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public static class ByReference
/*      */         extends PI
/*      */         implements Structure.ByReference {}
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class UNION
/*      */       extends Union
/*      */     {
/*      */       public WinDef.DWORD dwOemID;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public WinBase.SYSTEM_INFO.PI pi;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public static class ByReference
/*      */         extends UNION
/*      */         implements Structure.ByReference {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void read() {
/* 1185 */         setType("dwOemID");
/* 1186 */         super.read();
/*      */         
/* 1188 */         setType("pi");
/* 1189 */         super.read();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwLength", "dwMemoryLoad", "ullTotalPhys", "ullAvailPhys", "ullTotalPageFile", "ullAvailPageFile", "ullTotalVirtual", "ullAvailVirtual", "ullAvailExtendedVirtual"})
/*      */   public static class MEMORYSTATUSEX
/*      */     extends Structure
/*      */   {
/* 1292 */     public WinDef.DWORD dwLength = new WinDef.DWORD(size());
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwMemoryLoad;
/*      */ 
/*      */     
/*      */     public WinDef.DWORDLONG ullTotalPhys;
/*      */ 
/*      */     
/*      */     public WinDef.DWORDLONG ullAvailPhys;
/*      */ 
/*      */     
/*      */     public WinDef.DWORDLONG ullTotalPageFile;
/*      */ 
/*      */     
/*      */     public WinDef.DWORDLONG ullAvailPageFile;
/*      */ 
/*      */     
/*      */     public WinDef.DWORDLONG ullTotalVirtual;
/*      */     
/*      */     public WinDef.DWORDLONG ullAvailVirtual;
/*      */     
/*      */     public WinDef.DWORDLONG ullAvailExtendedVirtual;
/*      */   }
/*      */ 
/*      */   
/*      */   @FieldOrder({"dwLength", "lpSecurityDescriptor", "bInheritHandle"})
/*      */   public static class SECURITY_ATTRIBUTES
/*      */     extends Structure
/*      */   {
/* 1322 */     public WinDef.DWORD dwLength = new WinDef.DWORD(size());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Pointer lpSecurityDescriptor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean bInheritHandle;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"cb", "lpReserved", "lpDesktop", "lpTitle", "dwX", "dwY", "dwXSize", "dwYSize", "dwXCountChars", "dwYCountChars", "dwFillAttribute", "dwFlags", "wShowWindow", "cbReserved2", "lpReserved2", "hStdInput", "hStdOutput", "hStdError"})
/*      */   public static class STARTUPINFO
/*      */     extends Structure
/*      */   {
/*      */     public WinDef.DWORD cb;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String lpReserved;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String lpDesktop;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String lpTitle;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwX;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwXSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwYSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwXCountChars;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwYCountChars;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwFillAttribute;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int dwFlags;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD wShowWindow;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD cbReserved2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ByteByReference lpReserved2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinNT.HANDLE hStdInput;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinNT.HANDLE hStdOutput;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinNT.HANDLE hStdError;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public STARTUPINFO() {
/* 1495 */       super(W32APITypeMapper.DEFAULT);
/* 1496 */       this.cb = new WinDef.DWORD(size());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"hProcess", "hThread", "dwProcessId", "dwThreadId"})
/*      */   public static class PROCESS_INFORMATION
/*      */     extends Structure
/*      */   {
/*      */     public WinNT.HANDLE hProcess;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinNT.HANDLE hThread;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwProcessId;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD dwThreadId;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class ByReference
/*      */       extends PROCESS_INFORMATION
/*      */       implements Structure.ByReference
/*      */     {
/*      */       public ByReference() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ByReference(Pointer memory) {
/* 1543 */         super(memory);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public PROCESS_INFORMATION() {}
/*      */     
/*      */     public PROCESS_INFORMATION(Pointer memory) {
/* 1551 */       super(memory);
/* 1552 */       read();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface THREAD_START_ROUTINE
/*      */     extends StdCallLibrary.StdCallCallback
/*      */   {
/*      */     WinDef.DWORD apply(WinDef.LPVOID param1LPVOID);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"foreignLocation"})
/*      */   public static class FOREIGN_THREAD_START_ROUTINE
/*      */     extends Structure
/*      */   {
/*      */     public WinDef.LPVOID foreignLocation;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface COMPUTER_NAME_FORMAT
/*      */   {
/*      */     public static final int ComputerNameNetBIOS = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ComputerNameDnsHostname = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ComputerNameDnsDomain = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ComputerNameDnsFullyQualified = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ComputerNamePhysicalNetBIOS = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ComputerNamePhysicalDnsHostname = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ComputerNamePhysicalDnsDomain = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ComputerNamePhysicalDnsFullyQualified = 7;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final int ComputerNameMax = 8;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface FE_EXPORT_FUNC
/*      */     extends StdCallLibrary.StdCallCallback
/*      */   {
/*      */     WinDef.DWORD callback(Pointer param1Pointer1, Pointer param1Pointer2, WinDef.ULONG param1ULONG);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static interface FE_IMPORT_FUNC
/*      */     extends StdCallLibrary.StdCallCallback
/*      */   {
/*      */     WinDef.DWORD callback(Pointer param1Pointer1, Pointer param1Pointer2, WinDef.ULONGByReference param1ULONGByReference);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"ReadIntervalTimeout", "ReadTotalTimeoutMultiplier", "ReadTotalTimeoutConstant", "WriteTotalTimeoutMultiplier", "WriteTotalTimeoutConstant"})
/*      */   public static class COMMTIMEOUTS
/*      */     extends Structure
/*      */   {
/*      */     public WinDef.DWORD ReadIntervalTimeout;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD ReadTotalTimeoutMultiplier;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD ReadTotalTimeoutConstant;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD WriteTotalTimeoutMultiplier;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD WriteTotalTimeoutConstant;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FieldOrder({"DCBlength", "BaudRate", "controllBits", "wReserved", "XonLim", "XoffLim", "ByteSize", "Parity", "StopBits", "XonChar", "XoffChar", "ErrorChar", "EofChar", "EvtChar", "wReserved1"})
/*      */   public static class DCB
/*      */     extends Structure
/*      */   {
/*      */     public WinDef.DWORD DCBlength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.DWORD BaudRate;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DCBControllBits controllBits;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD wReserved;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD XonLim;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD XoffLim;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.BYTE ByteSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.BYTE Parity;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.BYTE StopBits;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public char XonChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public char XoffChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public char ErrorChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public char EofChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public char EvtChar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WinDef.WORD wReserved1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class DCBControllBits
/*      */       extends WinDef.DWORD
/*      */     {
/*      */       private static final long serialVersionUID = 8574966619718078579L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public String toString() {
/* 1851 */         StringBuilder stringBuilder = new StringBuilder();
/* 1852 */         stringBuilder.append('<');
/* 1853 */         stringBuilder.append("fBinary:1=");
/* 1854 */         stringBuilder.append(getfBinary() ? 49 : 48);
/* 1855 */         stringBuilder.append(", fParity:1=");
/* 1856 */         stringBuilder.append(getfParity() ? 49 : 48);
/* 1857 */         stringBuilder.append(", fOutxCtsFlow:1=");
/* 1858 */         stringBuilder.append(getfOutxCtsFlow() ? 49 : 48);
/* 1859 */         stringBuilder.append(", fOutxDsrFlow:1=");
/* 1860 */         stringBuilder.append(getfOutxDsrFlow() ? 49 : 48);
/* 1861 */         stringBuilder.append(", fDtrControl:2=");
/* 1862 */         stringBuilder.append(getfDtrControl());
/* 1863 */         stringBuilder.append(", fDsrSensitivity:1=");
/* 1864 */         stringBuilder.append(getfDsrSensitivity() ? 49 : 48);
/* 1865 */         stringBuilder.append(", fTXContinueOnXoff:1=");
/* 1866 */         stringBuilder.append(getfTXContinueOnXoff() ? 49 : 48);
/* 1867 */         stringBuilder.append(", fOutX:1=");
/* 1868 */         stringBuilder.append(getfOutX() ? 49 : 48);
/* 1869 */         stringBuilder.append(", fInX:1=");
/* 1870 */         stringBuilder.append(getfInX() ? 49 : 48);
/* 1871 */         stringBuilder.append(", fErrorChar:1=");
/* 1872 */         stringBuilder.append(getfErrorChar() ? 49 : 48);
/* 1873 */         stringBuilder.append(", fNull:1=");
/* 1874 */         stringBuilder.append(getfNull() ? 49 : 48);
/* 1875 */         stringBuilder.append(", fRtsControl:2=");
/* 1876 */         stringBuilder.append(getfRtsControl());
/* 1877 */         stringBuilder.append(", fAbortOnError:1=");
/* 1878 */         stringBuilder.append(getfAbortOnError() ? 49 : 48);
/* 1879 */         stringBuilder.append(", fDummy2:17=");
/* 1880 */         stringBuilder.append(getfDummy2());
/* 1881 */         stringBuilder.append('>');
/* 1882 */         return stringBuilder.toString();
/*      */       }
/*      */       
/*      */       public boolean getfAbortOnError() {
/* 1886 */         return ((intValue() & 0x4000) != 0);
/*      */       }
/*      */       
/*      */       public boolean getfBinary() {
/* 1890 */         return ((intValue() & 0x1) != 0);
/*      */       }
/*      */       
/*      */       public boolean getfDsrSensitivity() {
/* 1894 */         return ((intValue() & 0x40) != 0);
/*      */       }
/*      */       
/*      */       public int getfDtrControl() {
/* 1898 */         return intValue() >>> 4 & 0x3;
/*      */       }
/*      */       
/*      */       public boolean getfErrorChar() {
/* 1902 */         return ((intValue() & 0x400) != 0);
/*      */       }
/*      */       
/*      */       public boolean getfInX() {
/* 1906 */         return ((intValue() & 0x200) != 0);
/*      */       }
/*      */       
/*      */       public boolean getfNull() {
/* 1910 */         return ((intValue() & 0x800) != 0);
/*      */       }
/*      */       
/*      */       public boolean getfOutX() {
/* 1914 */         return ((intValue() & 0x100) != 0);
/*      */       }
/*      */       
/*      */       public boolean getfOutxCtsFlow() {
/* 1918 */         return ((intValue() & 0x4) != 0);
/*      */       }
/*      */       
/*      */       public boolean getfOutxDsrFlow() {
/* 1922 */         return ((intValue() & 0x8) != 0);
/*      */       }
/*      */       
/*      */       public boolean getfParity() {
/* 1926 */         return ((intValue() & 0x2) != 0);
/*      */       }
/*      */       
/*      */       public int getfRtsControl() {
/* 1930 */         return intValue() >>> 12 & 0x3;
/*      */       }
/*      */       
/*      */       public int getfDummy2() {
/* 1934 */         return intValue() >>> 15 & 0x1FFFF;
/*      */       }
/*      */       
/*      */       public boolean getfTXContinueOnXoff() {
/* 1938 */         return ((intValue() & 0x80) != 0);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfAbortOnError(boolean fAbortOnError) {
/* 1951 */         int tmp = leftShiftMask(fAbortOnError ? 1 : 0, (byte)14, 1, intValue());
/* 1952 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfBinary(boolean fBinary) {
/* 1963 */         int tmp = leftShiftMask(fBinary ? 1 : 0, (byte)0, 1, intValue());
/* 1964 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfDsrSensitivity(boolean fDsrSensitivity) {
/* 1976 */         int tmp = leftShiftMask(fDsrSensitivity ? 1 : 0, (byte)6, 1, intValue());
/* 1977 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfDtrControl(int fOutxDsrFlow) {
/* 1991 */         int tmp = leftShiftMask(fOutxDsrFlow, (byte)4, 3, intValue());
/* 1992 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfErrorChar(boolean fErrorChar) {
/* 2004 */         int tmp = leftShiftMask(fErrorChar ? 1 : 0, (byte)10, 1, intValue());
/* 2005 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfInX(boolean fInX) {
/* 2018 */         int tmp = leftShiftMask(fInX ? 1 : 0, (byte)9, 1, intValue());
/* 2019 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfNull(boolean fNull) {
/* 2028 */         int tmp = leftShiftMask(fNull ? 1 : 0, (byte)11, 1, intValue());
/* 2029 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfOutX(boolean fOutX) {
/* 2042 */         int tmp = leftShiftMask(fOutX ? 1 : 0, (byte)8, 1, intValue());
/* 2043 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfOutxCtsFlow(boolean fOutxCtsFlow) {
/* 2055 */         int tmp = leftShiftMask(fOutxCtsFlow ? 1 : 0, (byte)2, 1, intValue());
/* 2056 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfOutxDsrFlow(boolean fOutxDsrFlow) {
/* 2068 */         int tmp = leftShiftMask(fOutxDsrFlow ? 1 : 0, (byte)3, 1, intValue());
/* 2069 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfParity(boolean fParity) {
/* 2079 */         int tmp = leftShiftMask(fParity ? 1 : 0, (byte)1, 1, intValue());
/* 2080 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfRtsControl(int fRtsControl) {
/* 2095 */         int tmp = leftShiftMask(fRtsControl, (byte)12, 3, intValue());
/* 2096 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void setfTXContinueOnXoff(boolean fTXContinueOnXoff) {
/* 2110 */         int tmp = leftShiftMask(fTXContinueOnXoff ? 1 : 0, (byte)7, 1, intValue());
/* 2111 */         setValue(tmp);
/*      */       }
/*      */ 
/*      */       
/*      */       private static int leftShiftMask(int valuetoset, byte shift, int mask, int storage) {
/* 2116 */         int tmp = storage;
/* 2117 */         tmp &= mask << shift ^ 0xFFFFFFFF;
/* 2118 */         tmp |= (valuetoset & mask) << shift;
/* 2119 */         return tmp;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public DCB() {
/* 2238 */       this.DCBlength = new WinDef.DWORD(size());
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface EnumResTypeProc extends Callback {
/*      */     boolean invoke(WinDef.HMODULE param1HMODULE, Pointer param1Pointer1, Pointer param1Pointer2);
/*      */   }
/*      */   
/*      */   public static interface EnumResNameProc extends Callback {
/*      */     boolean invoke(WinDef.HMODULE param1HMODULE, Pointer param1Pointer1, Pointer param1Pointer2, Pointer param1Pointer3);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\WinBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */