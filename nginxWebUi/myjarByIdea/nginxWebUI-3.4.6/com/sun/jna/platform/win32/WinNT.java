package com.sun.jna.platform.win32;

import com.sun.jna.FromNativeContext;
import com.sun.jna.IntegerType;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;

public interface WinNT extends WinError, WinDef, WinBase, BaseTSD {
   int MINCHAR = 128;
   int MAXCHAR = 127;
   int MINSHORT = 32768;
   int MAXSHORT = 32767;
   int MINLONG = Integer.MIN_VALUE;
   int MAXLONG = Integer.MAX_VALUE;
   int MAXBYTE = 255;
   int MAXWORD = 65535;
   int MAXDWORD = -1;
   int DELETE = 65536;
   int READ_CONTROL = 131072;
   int WRITE_DAC = 262144;
   int WRITE_OWNER = 524288;
   int SYNCHRONIZE = 1048576;
   int STANDARD_RIGHTS_REQUIRED = 983040;
   int STANDARD_RIGHTS_READ = 131072;
   int STANDARD_RIGHTS_WRITE = 131072;
   int STANDARD_RIGHTS_EXECUTE = 131072;
   int STANDARD_RIGHTS_ALL = 2031616;
   int SPECIFIC_RIGHTS_ALL = 65535;
   int MUTANT_QUERY_STATE = 1;
   int MUTANT_ALL_ACCESS = 2031617;
   int TOKEN_ASSIGN_PRIMARY = 1;
   int TOKEN_DUPLICATE = 2;
   int TOKEN_IMPERSONATE = 4;
   int TOKEN_QUERY = 8;
   int TOKEN_QUERY_SOURCE = 16;
   int TOKEN_ADJUST_PRIVILEGES = 32;
   int TOKEN_ADJUST_GROUPS = 64;
   int TOKEN_ADJUST_DEFAULT = 128;
   int TOKEN_ADJUST_SESSIONID = 256;
   int TOKEN_ALL_ACCESS_P = 983295;
   int TOKEN_ALL_ACCESS = 983551;
   int TOKEN_READ = 131080;
   int TOKEN_WRITE = 131296;
   int TOKEN_EXECUTE = 131072;
   int THREAD_TERMINATE = 1;
   int THREAD_SUSPEND_RESUME = 2;
   int THREAD_GET_CONTEXT = 8;
   int THREAD_SET_CONTEXT = 16;
   int THREAD_QUERY_INFORMATION = 64;
   int THREAD_SET_INFORMATION = 32;
   int THREAD_SET_THREAD_TOKEN = 128;
   int THREAD_IMPERSONATE = 256;
   int THREAD_DIRECT_IMPERSONATION = 512;
   int THREAD_SET_LIMITED_INFORMATION = 1024;
   int THREAD_QUERY_LIMITED_INFORMATION = 2048;
   int THREAD_ALL_ACCESS = 2032639;
   int LTP_PC_SMT = 1;
   int FILE_READ_DATA = 1;
   int FILE_LIST_DIRECTORY = 1;
   int FILE_WRITE_DATA = 2;
   int FILE_ADD_FILE = 2;
   int FILE_APPEND_DATA = 4;
   int FILE_ADD_SUBDIRECTORY = 4;
   int FILE_CREATE_PIPE_INSTANCE = 4;
   int FILE_READ_EA = 8;
   int FILE_WRITE_EA = 16;
   int FILE_EXECUTE = 32;
   int FILE_TRAVERSE = 32;
   int FILE_DELETE_CHILD = 64;
   int FILE_READ_ATTRIBUTES = 128;
   int FILE_WRITE_ATTRIBUTES = 256;
   int FILE_ALL_ACCESS = 2032127;
   int FILE_GENERIC_READ = 1179785;
   int FILE_GENERIC_WRITE = 1179926;
   int FILE_GENERIC_EXECUTE = 1179808;
   int CREATE_NEW = 1;
   int CREATE_ALWAYS = 2;
   int OPEN_EXISTING = 3;
   int OPEN_ALWAYS = 4;
   int TRUNCATE_EXISTING = 5;
   int FILE_FLAG_WRITE_THROUGH = Integer.MIN_VALUE;
   int FILE_FLAG_OVERLAPPED = 1073741824;
   int FILE_FLAG_NO_BUFFERING = 536870912;
   int FILE_FLAG_RANDOM_ACCESS = 268435456;
   int FILE_FLAG_SEQUENTIAL_SCAN = 134217728;
   int FILE_FLAG_DELETE_ON_CLOSE = 67108864;
   int FILE_FLAG_BACKUP_SEMANTICS = 33554432;
   int FILE_FLAG_POSIX_SEMANTICS = 16777216;
   int FILE_FLAG_OPEN_REPARSE_POINT = 2097152;
   int FILE_FLAG_OPEN_NO_RECALL = 1048576;
   int GENERIC_READ = Integer.MIN_VALUE;
   int GENERIC_WRITE = 1073741824;
   int GENERIC_EXECUTE = 536870912;
   int GENERIC_ALL = 268435456;
   int ACCESS_SYSTEM_SECURITY = 16777216;
   int PAGE_GUARD = 256;
   int PAGE_NOACCESS = 1;
   int PAGE_READONLY = 2;
   int PAGE_READWRITE = 4;
   int PAGE_WRITECOPY = 8;
   int PAGE_EXECUTE = 16;
   int PAGE_EXECUTE_READ = 32;
   int PAGE_EXECUTE_READWRITE = 64;
   int SECTION_QUERY = 1;
   int SECTION_MAP_WRITE = 2;
   int SECTION_MAP_READ = 4;
   int SECTION_MAP_EXECUTE = 8;
   int SECTION_EXTEND_SIZE = 16;
   int FILE_SHARE_READ = 1;
   int FILE_SHARE_WRITE = 2;
   int FILE_SHARE_DELETE = 4;
   int FILE_TYPE_CHAR = 2;
   int FILE_TYPE_DISK = 1;
   int FILE_TYPE_PIPE = 3;
   int FILE_TYPE_REMOTE = 32768;
   int FILE_TYPE_UNKNOWN = 0;
   int FILE_ATTRIBUTE_READONLY = 1;
   int FILE_ATTRIBUTE_HIDDEN = 2;
   int FILE_ATTRIBUTE_SYSTEM = 4;
   int FILE_ATTRIBUTE_DIRECTORY = 16;
   int FILE_ATTRIBUTE_ARCHIVE = 32;
   int FILE_ATTRIBUTE_DEVICE = 64;
   int FILE_ATTRIBUTE_NORMAL = 128;
   int FILE_ATTRIBUTE_TEMPORARY = 256;
   int FILE_ATTRIBUTE_SPARSE_FILE = 512;
   int FILE_ATTRIBUTE_REPARSE_POINT = 1024;
   int FILE_ATTRIBUTE_COMPRESSED = 2048;
   int FILE_ATTRIBUTE_OFFLINE = 4096;
   int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 8192;
   int FILE_ATTRIBUTE_ENCRYPTED = 16384;
   int FILE_ATTRIBUTE_VIRTUAL = 65536;
   int FILE_NOTIFY_CHANGE_FILE_NAME = 1;
   int FILE_NOTIFY_CHANGE_DIR_NAME = 2;
   int FILE_NOTIFY_CHANGE_NAME = 3;
   int FILE_NOTIFY_CHANGE_ATTRIBUTES = 4;
   int FILE_NOTIFY_CHANGE_SIZE = 8;
   int FILE_NOTIFY_CHANGE_LAST_WRITE = 16;
   int FILE_NOTIFY_CHANGE_LAST_ACCESS = 32;
   int FILE_NOTIFY_CHANGE_CREATION = 64;
   int FILE_NOTIFY_CHANGE_SECURITY = 256;
   int FILE_ACTION_ADDED = 1;
   int FILE_ACTION_REMOVED = 2;
   int FILE_ACTION_MODIFIED = 3;
   int FILE_ACTION_RENAMED_OLD_NAME = 4;
   int FILE_ACTION_RENAMED_NEW_NAME = 5;
   int FILE_CASE_SENSITIVE_SEARCH = 1;
   int FILE_CASE_PRESERVED_NAMES = 2;
   int FILE_UNICODE_ON_DISK = 4;
   int FILE_PERSISTENT_ACLS = 8;
   int FILE_FILE_COMPRESSION = 16;
   int FILE_VOLUME_QUOTAS = 32;
   int FILE_SUPPORTS_SPARSE_FILES = 64;
   int FILE_SUPPORTS_REPARSE_POINTS = 128;
   int FILE_SUPPORTS_REMOTE_STORAGE = 256;
   int FILE_VOLUME_IS_COMPRESSED = 32768;
   int FILE_SUPPORTS_OBJECT_IDS = 65536;
   int FILE_SUPPORTS_ENCRYPTION = 131072;
   int FILE_NAMED_STREAMS = 262144;
   int FILE_READ_ONLY_VOLUME = 524288;
   int FILE_SEQUENTIAL_WRITE_ONCE = 1048576;
   int FILE_SUPPORTS_TRANSACTIONS = 2097152;
   int FILE_SUPPORTS_HARD_LINKS = 4194304;
   int FILE_SUPPORTS_EXTENDED_ATTRIBUTES = 8388608;
   int FILE_SUPPORTS_OPEN_BY_FILE_ID = 16777216;
   int FILE_SUPPORTS_USN_JOURNAL = 33554432;
   int IO_REPARSE_TAG_MOUNT_POINT = -1610612733;
   int IO_REPARSE_TAG_HSM = -1073741820;
   int IO_REPARSE_TAG_HSM2 = -2147483642;
   int IO_REPARSE_TAG_SIS = -2147483641;
   int IO_REPARSE_TAG_WIM = -2147483640;
   int IO_REPARSE_TAG_CSV = -2147483639;
   int IO_REPARSE_TAG_DFS = -2147483638;
   int IO_REPARSE_TAG_SYMLINK = -1610612724;
   int IO_REPARSE_TAG_DFSR = -2147483630;
   int DDD_RAW_TARGET_PATH = 1;
   int DDD_REMOVE_DEFINITION = 2;
   int DDD_EXACT_MATCH_ON_REMOVE = 4;
   int DDD_NO_BROADCAST_SYSTEM = 8;
   int COMPRESSION_FORMAT_NONE = 0;
   int COMPRESSION_FORMAT_DEFAULT = 1;
   int COMPRESSION_FORMAT_LZNT1 = 2;
   int COMPRESSION_FORMAT_XPRESS = 3;
   int COMPRESSION_FORMAT_XPRESS_HUFF = 4;
   int COMPRESSION_ENGINE_STANDARD = 0;
   int COMPRESSION_ENGINE_MAXIMUM = 256;
   int COMPRESSION_ENGINE_HIBER = 512;
   int KEY_QUERY_VALUE = 1;
   int KEY_SET_VALUE = 2;
   int KEY_CREATE_SUB_KEY = 4;
   int KEY_ENUMERATE_SUB_KEYS = 8;
   int KEY_NOTIFY = 16;
   int KEY_CREATE_LINK = 32;
   int KEY_WOW64_32KEY = 512;
   int KEY_WOW64_64KEY = 256;
   int KEY_WOW64_RES = 768;
   int KEY_READ = 131097;
   int KEY_WRITE = 131078;
   int KEY_EXECUTE = 131097;
   int KEY_ALL_ACCESS = 983103;
   int REG_OPTION_RESERVED = 0;
   int REG_OPTION_NON_VOLATILE = 0;
   int REG_OPTION_VOLATILE = 1;
   int REG_OPTION_CREATE_LINK = 2;
   int REG_OPTION_BACKUP_RESTORE = 4;
   int REG_OPTION_OPEN_LINK = 8;
   int REG_LEGAL_OPTION = 15;
   int REG_CREATED_NEW_KEY = 1;
   int REG_OPENED_EXISTING_KEY = 2;
   int REG_STANDARD_FORMAT = 1;
   int REG_LATEST_FORMAT = 2;
   int REG_NO_COMPRESSION = 4;
   int REG_WHOLE_HIVE_VOLATILE = 1;
   int REG_REFRESH_HIVE = 2;
   int REG_NO_LAZY_FLUSH = 4;
   int REG_FORCE_RESTORE = 8;
   int REG_APP_HIVE = 16;
   int REG_PROCESS_PRIVATE = 32;
   int REG_START_JOURNAL = 64;
   int REG_HIVE_EXACT_FILE_GROWTH = 128;
   int REG_HIVE_NO_RM = 256;
   int REG_HIVE_SINGLE_LOG = 512;
   int REG_FORCE_UNLOAD = 1;
   int REG_NOTIFY_CHANGE_NAME = 1;
   int REG_NOTIFY_CHANGE_ATTRIBUTES = 2;
   int REG_NOTIFY_CHANGE_LAST_SET = 4;
   int REG_NOTIFY_CHANGE_SECURITY = 8;
   int REG_LEGAL_CHANGE_FILTER = 15;
   int REG_NONE = 0;
   int REG_SZ = 1;
   int REG_EXPAND_SZ = 2;
   int REG_BINARY = 3;
   int REG_DWORD = 4;
   int REG_DWORD_LITTLE_ENDIAN = 4;
   int REG_DWORD_BIG_ENDIAN = 5;
   int REG_LINK = 6;
   int REG_MULTI_SZ = 7;
   int REG_RESOURCE_LIST = 8;
   int REG_FULL_RESOURCE_DESCRIPTOR = 9;
   int REG_RESOURCE_REQUIREMENTS_LIST = 10;
   int REG_QWORD = 11;
   int REG_QWORD_LITTLE_ENDIAN = 11;
   int SID_REVISION = 1;
   int SID_MAX_SUB_AUTHORITIES = 15;
   int SID_RECOMMENDED_SUB_AUTHORITIES = 1;
   int SECURITY_MAX_SID_SIZE = 68;
   int VER_EQUAL = 1;
   int VER_GREATER = 2;
   int VER_GREATER_EQUAL = 3;
   int VER_LESS = 4;
   int VER_LESS_EQUAL = 5;
   int VER_AND = 6;
   int VER_OR = 7;
   int VER_CONDITION_MASK = 7;
   int VER_NUM_BITS_PER_CONDITION_MASK = 3;
   int VER_MINORVERSION = 1;
   int VER_MAJORVERSION = 2;
   int VER_BUILDNUMBER = 4;
   int VER_PLATFORMID = 8;
   int VER_SERVICEPACKMINOR = 16;
   int VER_SERVICEPACKMAJOR = 32;
   int VER_SUITENAME = 64;
   int VER_PRODUCT_TYPE = 128;
   int VER_NT_WORKSTATION = 1;
   int VER_NT_DOMAIN_CONTROLLER = 2;
   int VER_NT_SERVER = 3;
   int VER_PLATFORM_WIN32s = 0;
   int VER_PLATFORM_WIN32_WINDOWS = 1;
   int VER_PLATFORM_WIN32_NT = 2;
   short WIN32_WINNT_NT4 = 1024;
   short WIN32_WINNT_WIN2K = 1280;
   short WIN32_WINNT_WINXP = 1281;
   short WIN32_WINNT_WS03 = 1282;
   short WIN32_WINNT_WIN6 = 1536;
   short WIN32_WINNT_VISTA = 1536;
   short WIN32_WINNT_WS08 = 1536;
   short WIN32_WINNT_LONGHORN = 1536;
   short WIN32_WINNT_WIN7 = 1537;
   short WIN32_WINNT_WIN8 = 1538;
   short WIN32_WINNT_WINBLUE = 1539;
   short WIN32_WINNT_WINTHRESHOLD = 2560;
   short WIN32_WINNT_WIN10 = 2560;
   int EVENTLOG_SEQUENTIAL_READ = 1;
   int EVENTLOG_SEEK_READ = 2;
   int EVENTLOG_FORWARDS_READ = 4;
   int EVENTLOG_BACKWARDS_READ = 8;
   int EVENTLOG_SUCCESS = 0;
   int EVENTLOG_ERROR_TYPE = 1;
   int EVENTLOG_WARNING_TYPE = 2;
   int EVENTLOG_INFORMATION_TYPE = 4;
   int EVENTLOG_AUDIT_SUCCESS = 8;
   int EVENTLOG_AUDIT_FAILURE = 16;
   int SERVICE_KERNEL_DRIVER = 1;
   int SERVICE_FILE_SYSTEM_DRIVER = 2;
   int SERVICE_ADAPTER = 4;
   int SERVICE_RECOGNIZER_DRIVER = 8;
   int SERVICE_DRIVER = 11;
   int SERVICE_WIN32_OWN_PROCESS = 16;
   int SERVICE_WIN32_SHARE_PROCESS = 32;
   int SERVICE_WIN32 = 48;
   int SERVICE_INTERACTIVE_PROCESS = 256;
   int SERVICE_TYPE_ALL = 319;
   int SERVICE_BOOT_START = 0;
   int SERVICE_SYSTEM_START = 1;
   int SERVICE_AUTO_START = 2;
   int SERVICE_DEMAND_START = 3;
   int SERVICE_DISABLED = 4;
   int SERVICE_ERROR_IGNORE = 0;
   int SERVICE_ERROR_NORMAL = 1;
   int SERVICE_ERROR_SEVERE = 2;
   int SERVICE_ERROR_CRITICAL = 3;
   int STATUS_PENDING = 259;
   String SE_CREATE_TOKEN_NAME = "SeCreateTokenPrivilege";
   String SE_ASSIGNPRIMARYTOKEN_NAME = "SeAssignPrimaryTokenPrivilege";
   String SE_LOCK_MEMORY_NAME = "SeLockMemoryPrivilege";
   String SE_INCREASE_QUOTA_NAME = "SeIncreaseQuotaPrivilege";
   String SE_UNSOLICITED_INPUT_NAME = "SeUnsolicitedInputPrivilege";
   String SE_MACHINE_ACCOUNT_NAME = "SeMachineAccountPrivilege";
   String SE_TCB_NAME = "SeTcbPrivilege";
   String SE_SECURITY_NAME = "SeSecurityPrivilege";
   String SE_TAKE_OWNERSHIP_NAME = "SeTakeOwnershipPrivilege";
   String SE_LOAD_DRIVER_NAME = "SeLoadDriverPrivilege";
   String SE_SYSTEM_PROFILE_NAME = "SeSystemProfilePrivilege";
   String SE_SYSTEMTIME_NAME = "SeSystemtimePrivilege";
   String SE_PROF_SINGLE_PROCESS_NAME = "SeProfileSingleProcessPrivilege";
   String SE_INC_BASE_PRIORITY_NAME = "SeIncreaseBasePriorityPrivilege";
   String SE_CREATE_PAGEFILE_NAME = "SeCreatePagefilePrivilege";
   String SE_CREATE_PERMANENT_NAME = "SeCreatePermanentPrivilege";
   String SE_BACKUP_NAME = "SeBackupPrivilege";
   String SE_RESTORE_NAME = "SeRestorePrivilege";
   String SE_SHUTDOWN_NAME = "SeShutdownPrivilege";
   String SE_DEBUG_NAME = "SeDebugPrivilege";
   String SE_AUDIT_NAME = "SeAuditPrivilege";
   String SE_SYSTEM_ENVIRONMENT_NAME = "SeSystemEnvironmentPrivilege";
   String SE_CHANGE_NOTIFY_NAME = "SeChangeNotifyPrivilege";
   String SE_REMOTE_SHUTDOWN_NAME = "SeRemoteShutdownPrivilege";
   String SE_UNDOCK_NAME = "SeUndockPrivilege";
   String SE_SYNC_AGENT_NAME = "SeSyncAgentPrivilege";
   String SE_ENABLE_DELEGATION_NAME = "SeEnableDelegationPrivilege";
   String SE_MANAGE_VOLUME_NAME = "SeManageVolumePrivilege";
   String SE_IMPERSONATE_NAME = "SeImpersonatePrivilege";
   String SE_CREATE_GLOBAL_NAME = "SeCreateGlobalPrivilege";
   int SE_PRIVILEGE_ENABLED_BY_DEFAULT = 1;
   int SE_PRIVILEGE_ENABLED = 2;
   int SE_PRIVILEGE_REMOVED = 4;
   int SE_PRIVILEGE_USED_FOR_ACCESS = Integer.MIN_VALUE;
   int PROCESS_CREATE_PROCESS = 128;
   int PROCESS_CREATE_THREAD = 2;
   int PROCESS_DUP_HANDLE = 64;
   int PROCESS_ALL_ACCESS = 2039803;
   int PROCESS_QUERY_INFORMATION = 1024;
   int PROCESS_QUERY_LIMITED_INFORMATION = 4096;
   int PROCESS_SET_INFORMATION = 512;
   int PROCESS_SET_QUOTA = 256;
   int PROCESS_SUSPEND_RESUME = 2048;
   int PROCESS_TERMINATE = 1;
   int PROCESS_NAME_NATIVE = 1;
   int PROCESS_VM_OPERATION = 8;
   int PROCESS_VM_READ = 16;
   int PROCESS_VM_WRITE = 32;
   int PROCESS_SYNCHRONIZE = 1048576;
   int OWNER_SECURITY_INFORMATION = 1;
   int GROUP_SECURITY_INFORMATION = 2;
   int DACL_SECURITY_INFORMATION = 4;
   int SACL_SECURITY_INFORMATION = 8;
   int LABEL_SECURITY_INFORMATION = 16;
   int PROTECTED_DACL_SECURITY_INFORMATION = Integer.MIN_VALUE;
   int PROTECTED_SACL_SECURITY_INFORMATION = 1073741824;
   int UNPROTECTED_DACL_SECURITY_INFORMATION = 536870912;
   int UNPROTECTED_SACL_SECURITY_INFORMATION = 268435456;
   int SE_OWNER_DEFAULTED = 1;
   int SE_GROUP_DEFAULTED = 2;
   int SE_DACL_PRESENT = 4;
   int SE_DACL_DEFAULTED = 8;
   int SE_SACL_PRESENT = 16;
   int SE_SACL_DEFAULTED = 32;
   int SE_DACL_AUTO_INHERIT_REQ = 256;
   int SE_SACL_AUTO_INHERIT_REQ = 512;
   int SE_DACL_AUTO_INHERITED = 1024;
   int SE_SACL_AUTO_INHERITED = 2048;
   int SE_DACL_PROTECTED = 4096;
   int SE_SACL_PROTECTED = 8192;
   int SE_RM_CONTROL_VALID = 16384;
   int SE_SELF_RELATIVE = 32768;
   int SECURITY_DESCRIPTOR_REVISION = 1;
   int ACL_REVISION = 2;
   int ACL_REVISION_DS = 4;
   int ACL_REVISION1 = 1;
   int ACL_REVISION2 = 2;
   int ACL_REVISION3 = 3;
   int ACL_REVISION4 = 4;
   int MIN_ACL_REVISION = 2;
   int MAX_ACL_REVISION = 4;
   byte ACCESS_ALLOWED_ACE_TYPE = 0;
   byte ACCESS_DENIED_ACE_TYPE = 1;
   byte SYSTEM_AUDIT_ACE_TYPE = 2;
   byte SYSTEM_ALARM_ACE_TYPE = 3;
   byte ACCESS_ALLOWED_COMPOUND_ACE_TYPE = 4;
   byte ACCESS_ALLOWED_OBJECT_ACE_TYPE = 5;
   byte ACCESS_DENIED_OBJECT_ACE_TYPE = 6;
   byte SYSTEM_AUDIT_OBJECT_ACE_TYPE = 7;
   byte SYSTEM_ALARM_OBJECT_ACE_TYPE = 8;
   byte ACCESS_ALLOWED_CALLBACK_ACE_TYPE = 9;
   byte ACCESS_DENIED_CALLBACK_ACE_TYPE = 10;
   byte ACCESS_ALLOWED_CALLBACK_OBJECT_ACE_TYPE = 11;
   byte ACCESS_DENIED_CALLBACK_OBJECT_ACE_TYPE = 12;
   byte SYSTEM_AUDIT_CALLBACK_ACE_TYPE = 13;
   byte SYSTEM_ALARM_CALLBACK_ACE_TYPE = 14;
   byte SYSTEM_AUDIT_CALLBACK_OBJECT_ACE_TYPE = 15;
   byte SYSTEM_ALARM_CALLBACK_OBJECT_ACE_TYPE = 16;
   byte SYSTEM_MANDATORY_LABEL_ACE_TYPE = 17;
   byte OBJECT_INHERIT_ACE = 1;
   byte CONTAINER_INHERIT_ACE = 2;
   byte NO_PROPAGATE_INHERIT_ACE = 4;
   byte INHERIT_ONLY_ACE = 8;
   byte INHERITED_ACE = 16;
   byte VALID_INHERIT_FLAGS = 31;
   byte CACHE_FULLY_ASSOCIATIVE = -1;
   int NUM_DISCHARGE_POLICIES = 4;
   int MEM_COMMIT = 4096;
   int MEM_FREE = 65536;
   int MEM_RESERVE = 8192;
   int MEM_IMAGE = 16777216;
   int MEM_MAPPED = 262144;
   int MEM_PRIVATE = 131072;
   int MEM_RESET = 524288;
   int MEM_RESET_UNDO = 16777216;
   int MEM_LARGE_PAGES = 536870912;
   int MEM_PHYSICAL = 4194304;
   int MEM_TOP_DOWN = 1048576;
   int MEM_COALESCE_PLACEHOLDERS = 1;
   int MEM_PRESERVE_PLACEHOLDER = 2;
   int MEM_DECOMMIT = 16384;
   int MEM_RELEASE = 32768;
   byte SECURITY_DYNAMIC_TRACKING = 1;
   byte SECURITY_STATIC_TRACKING = 0;
   byte BOOLEAN_TRUE = 1;
   byte BOOLEAN_FALSE = 0;
   int LANG_NEUTRAL = 0;
   int LANG_INVARIANT = 127;
   int LANG_AFRIKAANS = 54;
   int LANG_ALBANIAN = 28;
   int LANG_ARABIC = 1;
   int LANG_ARMENIAN = 43;
   int LANG_ASSAMESE = 77;
   int LANG_AZERI = 44;
   int LANG_BASQUE = 45;
   int LANG_BELARUSIAN = 35;
   int LANG_BENGALI = 69;
   int LANG_BULGARIAN = 2;
   int LANG_CATALAN = 3;
   int LANG_CHINESE = 4;
   int LANG_CROATIAN = 26;
   int LANG_CZECH = 5;
   int LANG_DANISH = 6;
   int LANG_DIVEHI = 101;
   int LANG_DUTCH = 19;
   int LANG_ENGLISH = 9;
   int LANG_ESTONIAN = 37;
   int LANG_FAEROESE = 56;
   int LANG_FARSI = 41;
   int LANG_FINNISH = 11;
   int LANG_FRENCH = 12;
   int LANG_GALICIAN = 86;
   int LANG_GEORGIAN = 55;
   int LANG_GERMAN = 7;
   int LANG_GREEK = 8;
   int LANG_GUJARATI = 71;
   int LANG_HEBREW = 13;
   int LANG_HINDI = 57;
   int LANG_HUNGARIAN = 14;
   int LANG_ICELANDIC = 15;
   int LANG_INDONESIAN = 33;
   int LANG_ITALIAN = 16;
   int LANG_JAPANESE = 17;
   int LANG_KANNADA = 75;
   int LANG_KASHMIRI = 96;
   int LANG_KAZAK = 63;
   int LANG_KONKANI = 87;
   int LANG_KOREAN = 18;
   int LANG_KYRGYZ = 64;
   int LANG_LATVIAN = 38;
   int LANG_LITHUANIAN = 39;
   int LANG_MACEDONIAN = 47;
   int LANG_MALAY = 62;
   int LANG_MALAYALAM = 76;
   int LANG_MANIPURI = 88;
   int LANG_MARATHI = 78;
   int LANG_MONGOLIAN = 80;
   int LANG_NEPALI = 97;
   int LANG_NORWEGIAN = 20;
   int LANG_ORIYA = 72;
   int LANG_POLISH = 21;
   int LANG_PORTUGUESE = 22;
   int LANG_PUNJABI = 70;
   int LANG_ROMANIAN = 24;
   int LANG_RUSSIAN = 25;
   int LANG_SANSKRIT = 79;
   int LANG_SERBIAN = 26;
   int LANG_SINDHI = 89;
   int LANG_SLOVAK = 27;
   int LANG_SLOVENIAN = 36;
   int LANG_SPANISH = 10;
   int LANG_SWAHILI = 65;
   int LANG_SWEDISH = 29;
   int LANG_SYRIAC = 90;
   int LANG_TAMIL = 73;
   int LANG_TATAR = 68;
   int LANG_TELUGU = 74;
   int LANG_THAI = 30;
   int LANG_TURKISH = 31;
   int LANG_UKRAINIAN = 34;
   int LANG_URDU = 32;
   int LANG_UZBEK = 67;
   int LANG_VIETNAMESE = 42;
   int SUBLANG_NEUTRAL = 0;
   int SUBLANG_DEFAULT = 1;
   int SUBLANG_SYS_DEFAULT = 2;
   int SUBLANG_ARABIC_SAUDI_ARABIA = 1;
   int SUBLANG_ARABIC_IRAQ = 2;
   int SUBLANG_ARABIC_EGYPT = 3;
   int SUBLANG_ARABIC_LIBYA = 4;
   int SUBLANG_ARABIC_ALGERIA = 5;
   int SUBLANG_ARABIC_MOROCCO = 6;
   int SUBLANG_ARABIC_TUNISIA = 7;
   int SUBLANG_ARABIC_OMAN = 8;
   int SUBLANG_ARABIC_YEMEN = 9;
   int SUBLANG_ARABIC_SYRIA = 10;
   int SUBLANG_ARABIC_JORDAN = 11;
   int SUBLANG_ARABIC_LEBANON = 12;
   int SUBLANG_ARABIC_KUWAIT = 13;
   int SUBLANG_ARABIC_UAE = 14;
   int SUBLANG_ARABIC_BAHRAIN = 15;
   int SUBLANG_ARABIC_QATAR = 16;
   int SUBLANG_AZERI_LATIN = 1;
   int SUBLANG_AZERI_CYRILLIC = 2;
   int SUBLANG_CHINESE_TRADITIONAL = 1;
   int SUBLANG_CHINESE_SIMPLIFIED = 2;
   int SUBLANG_CHINESE_HONGKONG = 3;
   int SUBLANG_CHINESE_SINGAPORE = 4;
   int SUBLANG_CHINESE_MACAU = 5;
   int SUBLANG_DUTCH = 1;
   int SUBLANG_DUTCH_BELGIAN = 2;
   int SUBLANG_ENGLISH_US = 1;
   int SUBLANG_ENGLISH_UK = 2;
   int SUBLANG_ENGLISH_AUS = 3;
   int SUBLANG_ENGLISH_CAN = 4;
   int SUBLANG_ENGLISH_NZ = 5;
   int SUBLANG_ENGLISH_EIRE = 6;
   int SUBLANG_ENGLISH_SOUTH_AFRICA = 7;
   int SUBLANG_ENGLISH_JAMAICA = 8;
   int SUBLANG_ENGLISH_CARIBBEAN = 9;
   int SUBLANG_ENGLISH_BELIZE = 10;
   int SUBLANG_ENGLISH_TRINIDAD = 11;
   int SUBLANG_ENGLISH_ZIMBABWE = 12;
   int SUBLANG_ENGLISH_PHILIPPINES = 13;
   int SUBLANG_FRENCH = 1;
   int SUBLANG_FRENCH_BELGIAN = 2;
   int SUBLANG_FRENCH_CANADIAN = 3;
   int SUBLANG_FRENCH_SWISS = 4;
   int SUBLANG_FRENCH_LUXEMBOURG = 5;
   int SUBLANG_FRENCH_MONACO = 6;
   int SUBLANG_GERMAN = 1;
   int SUBLANG_GERMAN_SWISS = 2;
   int SUBLANG_GERMAN_AUSTRIAN = 3;
   int SUBLANG_GERMAN_LUXEMBOURG = 4;
   int SUBLANG_GERMAN_LIECHTENSTEIN = 5;
   int SUBLANG_ITALIAN = 1;
   int SUBLANG_ITALIAN_SWISS = 2;
   int SUBLANG_KASHMIRI_SASIA = 2;
   int SUBLANG_KASHMIRI_INDIA = 2;
   int SUBLANG_KOREAN = 1;
   int SUBLANG_LITHUANIAN = 1;
   int SUBLANG_MALAY_MALAYSIA = 1;
   int SUBLANG_MALAY_BRUNEI_DARUSSALAM = 2;
   int SUBLANG_NEPALI_INDIA = 2;
   int SUBLANG_NORWEGIAN_BOKMAL = 1;
   int SUBLANG_NORWEGIAN_NYNORSK = 2;
   int SUBLANG_PORTUGUESE = 2;
   int SUBLANG_PORTUGUESE_BRAZILIAN = 1;
   int SUBLANG_SERBIAN_LATIN = 2;
   int SUBLANG_SERBIAN_CYRILLIC = 3;
   int SUBLANG_SPANISH = 1;
   int SUBLANG_SPANISH_MEXICAN = 2;
   int SUBLANG_SPANISH_MODERN = 3;
   int SUBLANG_SPANISH_GUATEMALA = 4;
   int SUBLANG_SPANISH_COSTA_RICA = 5;
   int SUBLANG_SPANISH_PANAMA = 6;
   int SUBLANG_SPANISH_DOMINICAN_REPUBLIC = 7;
   int SUBLANG_SPANISH_VENEZUELA = 8;
   int SUBLANG_SPANISH_COLOMBIA = 9;
   int SUBLANG_SPANISH_PERU = 10;
   int SUBLANG_SPANISH_ARGENTINA = 11;
   int SUBLANG_SPANISH_ECUADOR = 12;
   int SUBLANG_SPANISH_CHILE = 13;
   int SUBLANG_SPANISH_URUGUAY = 14;
   int SUBLANG_SPANISH_PARAGUAY = 15;
   int SUBLANG_SPANISH_BOLIVIA = 16;
   int SUBLANG_SPANISH_EL_SALVADOR = 17;
   int SUBLANG_SPANISH_HONDURAS = 18;
   int SUBLANG_SPANISH_NICARAGUA = 19;
   int SUBLANG_SPANISH_PUERTO_RICO = 20;
   int SUBLANG_SWEDISH = 1;
   int SUBLANG_SWEDISH_FINLAND = 2;
   int SUBLANG_URDU_PAKISTAN = 1;
   int SUBLANG_URDU_INDIA = 2;
   int SUBLANG_UZBEK_LATIN = 1;
   int SUBLANG_UZBEK_CYRILLIC = 2;
   int SORT_DEFAULT = 0;
   int SORT_JAPANESE_XJIS = 0;
   int SORT_JAPANESE_UNICODE = 1;
   int SORT_CHINESE_BIG5 = 0;
   int SORT_CHINESE_PRCP = 0;
   int SORT_CHINESE_UNICODE = 1;
   int SORT_CHINESE_PRC = 2;
   int SORT_CHINESE_BOPOMOFO = 3;
   int SORT_KOREAN_KSC = 0;
   int SORT_KOREAN_UNICODE = 1;
   int SORT_GERMAN_PHONE_BOOK = 1;
   int SORT_HUNGARIAN_DEFAULT = 0;
   int SORT_HUNGARIAN_TECHNICAL = 1;
   int SORT_GEORGIAN_TRADITIONAL = 0;
   int SORT_GEORGIAN_MODERN = 1;
   int NLS_VALID_LOCALE_MASK = 1048575;
   int LANG_SYSTEM_DEFAULT = WinNT.LocaleMacros.MAKELANGID(0, 2);
   int LANG_USER_DEFAULT = WinNT.LocaleMacros.MAKELANGID(0, 1);
   WinDef.LCID LOCALE_SYSTEM_DEFAULT = WinNT.LocaleMacros.MAKELCID(LANG_SYSTEM_DEFAULT, 0);
   WinDef.LCID LOCALE_USER_DEFAULT = WinNT.LocaleMacros.MAKELCID(LANG_USER_DEFAULT, 0);
   WinDef.LCID LOCALE_NEUTRAL = WinNT.LocaleMacros.MAKELCID(WinNT.LocaleMacros.MAKELANGID(0, 0), 0);
   WinDef.LCID LOCALE_INVARIANT = WinNT.LocaleMacros.MAKELCID(WinNT.LocaleMacros.MAKELANGID(127, 0), 0);
   int EVENT_MODIFY_STATE = 2;
   int EVENT_ALL_ACCESS = 2031619;

   @Structure.FieldOrder({"ReadOperationCount", "WriteOperationCount", "OtherOperationCount", "ReadTransferCount", "WriteTransferCount", "OtherTransferCount"})
   public static class IO_COUNTERS extends Structure {
      public long ReadOperationCount;
      public long WriteOperationCount;
      public long OtherOperationCount;
      public long ReadTransferCount;
      public long WriteTransferCount;
      public long OtherTransferCount;

      public IO_COUNTERS() {
      }

      public IO_COUNTERS(Pointer memory) {
         super(memory);
      }
   }

   public static final class LocaleMacros {
      private static final int _MAKELCID(int lgid, int srtid) {
         return srtid << 16 | lgid;
      }

      public static final WinDef.LCID MAKELCID(int lgid, int srtid) {
         return new WinDef.LCID((long)_MAKELCID(lgid, srtid));
      }

      public static final WinDef.LCID MAKESORTLCID(int lgid, int srtid, int ver) {
         return new WinDef.LCID((long)(_MAKELCID(lgid, srtid) | ver << 20));
      }

      public static final int LANGIDFROMLCID(WinDef.LCID lcid) {
         return lcid.intValue() & '\uffff';
      }

      public static final int SORTIDFROMLCID(WinDef.LCID lcid) {
         return lcid.intValue() >>> 16 & 15;
      }

      public static final int SORTVERSIONFROMLCID(WinDef.LCID lcid) {
         return lcid.intValue() >>> 20 & 15;
      }

      public static final int MAKELANGID(int p, int s) {
         return s << 10 | p & '\uffff';
      }

      public static final int PRIMARYLANGID(int lgid) {
         return lgid & 1023;
      }

      public static final int SUBLANGID(int lgid) {
         return (lgid & '\uffff') >>> 10;
      }
   }

   @Structure.FieldOrder({"Length", "ImpersonationLevel", "ContextTrackingMode", "EffectiveOnly"})
   public static class SECURITY_QUALITY_OF_SERVICE extends Structure {
      public int Length;
      public int ImpersonationLevel;
      public byte ContextTrackingMode;
      public byte EffectiveOnly;

      public void write() {
         this.Length = this.size();
         super.write();
      }
   }

   @Structure.FieldOrder({"baseAddress", "allocationBase", "allocationProtect", "regionSize", "state", "protect", "type"})
   public static class MEMORY_BASIC_INFORMATION extends Structure {
      public Pointer baseAddress;
      public Pointer allocationBase;
      public WinDef.DWORD allocationProtect;
      public BaseTSD.SIZE_T regionSize;
      public WinDef.DWORD state;
      public WinDef.DWORD protect;
      public WinDef.DWORD type;
   }

   @Structure.FieldOrder({"PowerButtonPresent", "SleepButtonPresent", "LidPresent", "SystemS1", "SystemS2", "SystemS3", "SystemS4", "SystemS5", "HiberFilePresent", "FullWake", "VideoDimPresent", "ApmPresent", "UpsPresent", "ThermalControl", "ProcessorThrottle", "ProcessorMinThrottle", "ProcessorMaxThrottle", "FastSystemS4", "Hiberboot", "WakeAlarmPresent", "AoAc", "DiskSpinDown", "HiberFileType", "AoAcConnectivitySupported", "spare3", "SystemBatteriesPresent", "BatteriesAreShortTerm", "BatteryScale", "AcOnLineWake", "SoftLidWake", "RtcWake", "MinDeviceWakeState", "DefaultLowLatencyWake"})
   public static class SYSTEM_POWER_CAPABILITIES extends Structure {
      public byte PowerButtonPresent;
      public byte SleepButtonPresent;
      public byte LidPresent;
      public byte SystemS1;
      public byte SystemS2;
      public byte SystemS3;
      public byte SystemS4;
      public byte SystemS5;
      public byte HiberFilePresent;
      public byte FullWake;
      public byte VideoDimPresent;
      public byte ApmPresent;
      public byte UpsPresent;
      public byte ThermalControl;
      public byte ProcessorThrottle;
      public byte ProcessorMinThrottle;
      public byte ProcessorMaxThrottle;
      public byte FastSystemS4;
      public byte Hiberboot;
      public byte WakeAlarmPresent;
      public byte AoAc;
      public byte DiskSpinDown;
      public byte HiberFileType;
      public byte AoAcConnectivitySupported;
      public byte[] spare3 = new byte[6];
      public byte SystemBatteriesPresent;
      public byte BatteriesAreShortTerm;
      public BATTERY_REPORTING_SCALE[] BatteryScale = new BATTERY_REPORTING_SCALE[3];
      public int AcOnLineWake;
      public int SoftLidWake;
      public int RtcWake;
      public int MinDeviceWakeState;
      public int DefaultLowLatencyWake;

      public SYSTEM_POWER_CAPABILITIES(Pointer p) {
         super(p);
         this.read();
      }

      public SYSTEM_POWER_CAPABILITIES() {
      }
   }

   @Structure.FieldOrder({"Revision", "PowerButton", "SleepButton", "LidClose", "LidOpenWake", "Reserved", "Idle", "IdleTimeout", "IdleSensitivity", "DynamicThrottle", "Spare2", "MinSleep", "MaxSleep", "ReducedLatencySleep", "WinLogonFlags", "Spare3", "DozeS4Timeout", "BroadcastCapacityResolution", "DischargePolicy", "VideoTimeout", "VideoDimDisplay", "VideoReserved", "SpindownTimeout", "OptimizeForPower", "FanThrottleTolerance", "ForcedThrottle", "MinThrottle", "OverThrottled"})
   public static class SYSTEM_POWER_POLICY extends Structure {
      public int Revision;
      public POWER_ACTION_POLICY PowerButton;
      public POWER_ACTION_POLICY SleepButton;
      public POWER_ACTION_POLICY LidClose;
      public int LidOpenWake;
      public int Reserved;
      public POWER_ACTION_POLICY Idle;
      public int IdleTimeout;
      public byte IdleSensitivity;
      public byte DynamicThrottle;
      public byte[] Spare2 = new byte[2];
      public int MinSleep;
      public int MaxSleep;
      public int ReducedLatencySleep;
      public int WinLogonFlags;
      public int Spare3;
      public int DozeS4Timeout;
      public int BroadcastCapacityResolution;
      public SYSTEM_POWER_LEVEL[] DischargePolicy = new SYSTEM_POWER_LEVEL[4];
      public int VideoTimeout;
      public byte VideoDimDisplay;
      public int[] VideoReserved = new int[3];
      public int SpindownTimeout;
      public byte OptimizeForPower;
      public byte FanThrottleTolerance;
      public byte ForcedThrottle;
      public byte MinThrottle;
      public POWER_ACTION_POLICY OverThrottled;

      public SYSTEM_POWER_POLICY(Pointer p) {
         super(p);
         this.read();
      }

      public SYSTEM_POWER_POLICY() {
      }
   }

   @Structure.FieldOrder({"Enable", "Spare", "BatteryLevel", "PowerPolicy", "MinSystemState"})
   public static class SYSTEM_POWER_LEVEL extends Structure {
      public byte Enable;
      public byte[] Spare = new byte[3];
      public int BatteryLevel;
      public POWER_ACTION_POLICY PowerPolicy;
      public int MinSystemState;
   }

   @Structure.FieldOrder({"Action", "Flags", "EventCode"})
   public static class POWER_ACTION_POLICY extends Structure {
      public int Action;
      public int Flags;
      public int EventCode;
   }

   @Structure.FieldOrder({"MaxIdlenessAllowed", "Idleness", "TimeRemaining", "CoolingMode"})
   public static class SYSTEM_POWER_INFORMATION extends Structure {
      public int MaxIdlenessAllowed;
      public int Idleness;
      public int TimeRemaining;
      public byte CoolingMode;

      public SYSTEM_POWER_INFORMATION(Pointer p) {
         super(p);
         this.read();
      }

      public SYSTEM_POWER_INFORMATION() {
      }
   }

   @Structure.FieldOrder({"Number", "MaxMhz", "CurrentMhz", "MhzLimit", "MaxIdleState", "CurrentIdleState"})
   public static class PROCESSOR_POWER_INFORMATION extends Structure {
      public int Number;
      public int MaxMhz;
      public int CurrentMhz;
      public int MhzLimit;
      public int MaxIdleState;
      public int CurrentIdleState;

      public PROCESSOR_POWER_INFORMATION(Pointer p) {
         super(p);
         this.read();
      }

      public PROCESSOR_POWER_INFORMATION() {
      }
   }

   @Structure.FieldOrder({"Granularity", "Capacity"})
   public static class BATTERY_REPORTING_SCALE extends Structure {
      public int Granularity;
      public int Capacity;
   }

   @Structure.FieldOrder({"AcOnLine", "BatteryPresent", "Charging", "Discharging", "Spare1", "Tag", "MaxCapacity", "RemainingCapacity", "Rate", "EstimatedTime", "DefaultAlert1", "DefaultAlert2"})
   public static class SYSTEM_BATTERY_STATE extends Structure {
      public byte AcOnLine;
      public byte BatteryPresent;
      public byte Charging;
      public byte Discharging;
      public byte[] Spare1 = new byte[3];
      public byte Tag;
      public int MaxCapacity;
      public int RemainingCapacity;
      public int Rate;
      public int EstimatedTime;
      public int DefaultAlert1;
      public int DefaultAlert2;

      public SYSTEM_BATTERY_STATE(Pointer p) {
         super(p);
         this.read();
      }

      public SYSTEM_BATTERY_STATE() {
      }
   }

   public interface SYSTEM_POWER_STATE {
      int PowerSystemUnspecified = 0;
      int PowerSystemWorking = 1;
      int PowerSystemSleeping1 = 2;
      int PowerSystemSleeping2 = 3;
      int PowerSystemSleeping3 = 4;
      int PowerSystemHibernate = 5;
      int PowerSystemShutdown = 6;
      int PowerSystemMaximum = 7;
   }

   public interface POWER_ACTION {
      int PowerActionNone = 0;
      int PowerActionReserved = 1;
      int PowerActionSleep = 2;
      int PowerActionHibernate = 3;
      int PowerActionShutdown = 4;
      int PowerActionShutdownReset = 5;
      int PowerActionShutdownOff = 6;
      int PowerActionWarmEject = 7;
      int PowerActionDisplayOff = 8;
   }

   public abstract static class PROCESSOR_CACHE_TYPE {
      public static int CacheUnified = 0;
      public static int CacheInstruction = 1;
      public static int CacheData = 2;
      public static int CacheTrace = 3;
   }

   @Structure.FieldOrder({"level", "associativity", "lineSize", "size", "type"})
   public static class CACHE_DESCRIPTOR extends Structure {
      public WinDef.BYTE level;
      public WinDef.BYTE associativity;
      public WinDef.WORD lineSize;
      public WinDef.DWORD size;
      public int type;
   }

   public interface LOGICAL_PROCESSOR_RELATIONSHIP {
      int RelationProcessorCore = 0;
      int RelationNumaNode = 1;
      int RelationCache = 2;
      int RelationProcessorPackage = 3;
      int RelationGroup = 4;
      int RelationAll = 65535;
   }

   @Structure.FieldOrder({"maximumProcessorCount", "activeProcessorCount", "reserved", "activeProcessorMask"})
   public static class PROCESSOR_GROUP_INFO extends Structure {
      public byte maximumProcessorCount;
      public byte activeProcessorCount;
      public byte[] reserved = new byte[38];
      public BaseTSD.ULONG_PTR activeProcessorMask;

      public PROCESSOR_GROUP_INFO(Pointer memory) {
         super(memory);
      }

      public PROCESSOR_GROUP_INFO() {
      }
   }

   @Structure.FieldOrder({"mask", "group", "reserved"})
   public static class GROUP_AFFINITY extends Structure {
      public BaseTSD.ULONG_PTR mask;
      public short group;
      public short[] reserved = new short[3];

      public GROUP_AFFINITY(Pointer memory) {
         super(memory);
      }

      public GROUP_AFFINITY() {
      }
   }

   @Structure.FieldOrder({"maximumGroupCount", "activeGroupCount", "reserved", "groupInfo"})
   public static class GROUP_RELATIONSHIP extends SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX {
      public short maximumGroupCount;
      public short activeGroupCount;
      public byte[] reserved = new byte[20];
      public PROCESSOR_GROUP_INFO[] groupInfo = new PROCESSOR_GROUP_INFO[1];

      public GROUP_RELATIONSHIP() {
      }

      public GROUP_RELATIONSHIP(Pointer memory) {
         super(memory);
      }

      public void read() {
         this.readField("activeGroupCount");
         this.groupInfo = new PROCESSOR_GROUP_INFO[this.activeGroupCount];
         super.read();
      }
   }

   @Structure.FieldOrder({"level", "associativity", "lineSize", "cacheSize", "type", "reserved", "groupMask"})
   public static class CACHE_RELATIONSHIP extends SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX {
      public byte level;
      public byte associativity;
      public short lineSize;
      public int cacheSize;
      public int type;
      public byte[] reserved = new byte[20];
      public GROUP_AFFINITY groupMask;

      public CACHE_RELATIONSHIP() {
      }

      public CACHE_RELATIONSHIP(Pointer memory) {
         super(memory);
      }
   }

   @Structure.FieldOrder({"nodeNumber", "reserved", "groupMask"})
   public static class NUMA_NODE_RELATIONSHIP extends SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX {
      public int nodeNumber;
      public byte[] reserved = new byte[20];
      public GROUP_AFFINITY groupMask;

      public NUMA_NODE_RELATIONSHIP() {
      }

      public NUMA_NODE_RELATIONSHIP(Pointer memory) {
         super(memory);
      }
   }

   @Structure.FieldOrder({"flags", "efficiencyClass", "reserved", "groupCount", "groupMask"})
   public static class PROCESSOR_RELATIONSHIP extends SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX {
      public byte flags;
      public byte efficiencyClass;
      public byte[] reserved = new byte[20];
      public short groupCount;
      public GROUP_AFFINITY[] groupMask = new GROUP_AFFINITY[1];

      public PROCESSOR_RELATIONSHIP() {
      }

      public PROCESSOR_RELATIONSHIP(Pointer memory) {
         super(memory);
      }

      public void read() {
         this.readField("groupCount");
         this.groupMask = new GROUP_AFFINITY[this.groupCount];
         super.read();
      }
   }

   @Structure.FieldOrder({"relationship", "size"})
   public abstract static class SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX extends Structure {
      public int relationship;
      public int size;

      public SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX() {
      }

      protected SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX(Pointer memory) {
         super(memory);
      }

      public static SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX fromPointer(Pointer memory) {
         int relationship = memory.getInt(0L);
         Object result;
         switch (relationship) {
            case 0:
            case 3:
               result = new PROCESSOR_RELATIONSHIP(memory);
               break;
            case 1:
               result = new NUMA_NODE_RELATIONSHIP(memory);
               break;
            case 2:
               result = new CACHE_RELATIONSHIP(memory);
               break;
            case 4:
               result = new GROUP_RELATIONSHIP(memory);
               break;
            default:
               throw new IllegalStateException("Unmapped relationship: " + relationship);
         }

         ((SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX)result).read();
         return (SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX)result;
      }
   }

   @Structure.FieldOrder({"processorMask", "relationship", "payload"})
   public static class SYSTEM_LOGICAL_PROCESSOR_INFORMATION extends Structure {
      public BaseTSD.ULONG_PTR processorMask;
      public int relationship;
      public AnonymousUnionPayload payload;

      public SYSTEM_LOGICAL_PROCESSOR_INFORMATION() {
      }

      public SYSTEM_LOGICAL_PROCESSOR_INFORMATION(Pointer memory) {
         super(memory);
         this.read();
      }

      @Structure.FieldOrder({"nodeNumber"})
      public static class AnonymousStructNumaNode extends Structure {
         public WinDef.DWORD nodeNumber;
      }

      @Structure.FieldOrder({"flags"})
      public static class AnonymousStructProcessorCore extends Structure {
         public WinDef.BYTE flags;
      }

      public static class AnonymousUnionPayload extends Union {
         public AnonymousStructProcessorCore processorCore;
         public AnonymousStructNumaNode numaNode;
         public CACHE_DESCRIPTOR cache;
         public WinDef.ULONGLONG[] reserved = new WinDef.ULONGLONG[2];
      }
   }

   @Structure.FieldOrder({"genericRead", "genericWrite", "genericExecute", "genericAll"})
   public static class GENERIC_MAPPING extends Structure {
      public WinDef.DWORD genericRead;
      public WinDef.DWORD genericWrite;
      public WinDef.DWORD genericExecute;
      public WinDef.DWORD genericAll;

      public static class ByReference extends GENERIC_MAPPING implements Structure.ByReference {
      }
   }

   public interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallLibrary.StdCallCallback {
      void callback(int var1, int var2, WinBase.OVERLAPPED var3);
   }

   public static class ACCESS_DENIED_ACE extends ACCESS_ACEStructure {
      public ACCESS_DENIED_ACE() {
      }

      public ACCESS_DENIED_ACE(Pointer p) {
         super(p);
      }

      public ACCESS_DENIED_ACE(int Mask, byte AceFlags, PSID psid) {
         super(Mask, (byte)1, AceFlags, psid);
      }
   }

   public static class ACCESS_ALLOWED_ACE extends ACCESS_ACEStructure {
      public ACCESS_ALLOWED_ACE() {
      }

      public ACCESS_ALLOWED_ACE(Pointer p) {
         super(p);
      }

      public ACCESS_ALLOWED_ACE(int Mask, byte AceFlags, PSID psid) {
         super(Mask, (byte)0, AceFlags, psid);
      }
   }

   @Structure.FieldOrder({"Mask", "SidStart"})
   public abstract static class ACCESS_ACEStructure extends ACE_HEADER {
      public int Mask;
      public byte[] SidStart = new byte[4];
      PSID psid;

      public ACCESS_ACEStructure() {
      }

      public ACCESS_ACEStructure(int Mask, byte AceType, byte AceFlags, PSID psid) {
         this.calculateSize(true);
         this.AceType = AceType;
         this.AceFlags = AceFlags;
         this.AceSize = (short)(super.fieldOffset("SidStart") + psid.getBytes().length);
         this.psid = psid;
         this.Mask = Mask;
         this.SidStart = psid.getPointer().getByteArray(0L, this.SidStart.length);
         this.allocateMemory(this.AceSize);
         this.write();
      }

      public ACCESS_ACEStructure(Pointer p) {
         super(p);
         this.read();
      }

      public String getSidString() {
         return Advapi32Util.convertSidToStringSid(this.psid);
      }

      public PSID getSID() {
         return this.psid;
      }

      public void write() {
         super.write();
         int offsetOfSID = super.fieldOffset("SidStart");
         int sizeOfSID = super.AceSize - super.fieldOffset("SidStart");
         if (this.psid != null) {
            byte[] psidWrite = this.psid.getBytes();

            assert psidWrite.length <= sizeOfSID;

            this.getPointer().write((long)offsetOfSID, (byte[])psidWrite, 0, sizeOfSID);
         }

      }

      public void read() {
         if (this.SidStart == null) {
            this.SidStart = new byte[4];
         }

         super.read();
         int offsetOfSID = super.fieldOffset("SidStart");
         int sizeOfSID = super.AceSize - super.fieldOffset("SidStart");
         if (sizeOfSID > 0) {
            this.psid = new PSID(this.getPointer().getByteArray((long)offsetOfSID, sizeOfSID));
         } else {
            this.psid = new PSID();
         }

      }
   }

   @Structure.FieldOrder({"AceType", "AceFlags", "AceSize"})
   public static class ACE_HEADER extends Structure {
      public byte AceType;
      public byte AceFlags;
      public short AceSize;

      public ACE_HEADER() {
      }

      public ACE_HEADER(Pointer p) {
         super(p);
         this.read();
      }

      public ACE_HEADER(byte AceType, byte AceFlags, short AceSize) {
         this.AceType = AceType;
         this.AceFlags = AceFlags;
         this.AceSize = AceSize;
         this.write();
      }
   }

   @Structure.FieldOrder({"Revision", "Sbz1", "Control", "Owner", "Group", "Sacl", "Dacl"})
   public static class SECURITY_DESCRIPTOR_RELATIVE extends Structure {
      public byte Revision;
      public byte Sbz1;
      public short Control;
      public int Owner;
      public int Group;
      public int Sacl;
      public int Dacl;
      private PSID OWNER;
      private PSID GROUP;
      private ACL SACL;
      private ACL DACL;

      public SECURITY_DESCRIPTOR_RELATIVE() {
      }

      public SECURITY_DESCRIPTOR_RELATIVE(byte[] data) {
         super((Pointer)(new Memory((long)data.length)));
         this.getPointer().write(0L, (byte[])data, 0, data.length);
         this.setMembers();
      }

      public SECURITY_DESCRIPTOR_RELATIVE(int length) {
         super((Pointer)(new Memory((long)length)));
      }

      public SECURITY_DESCRIPTOR_RELATIVE(Pointer p) {
         super(p);
         this.setMembers();
      }

      public PSID getOwner() {
         return this.OWNER;
      }

      public PSID getGroup() {
         return this.GROUP;
      }

      public ACL getDiscretionaryACL() {
         return this.DACL;
      }

      public ACL getSystemACL() {
         return this.SACL;
      }

      private final void setMembers() {
         this.read();
         if (this.Dacl != 0) {
            this.DACL = new ACL(this.getPointer().share((long)this.Dacl));
         }

         if (this.Sacl != 0) {
            this.SACL = new ACL(this.getPointer().share((long)this.Sacl));
         }

         if (this.Group != 0) {
            this.GROUP = new PSID(this.getPointer().share((long)this.Group));
         }

         if (this.Owner != 0) {
            this.OWNER = new PSID(this.getPointer().share((long)this.Owner));
         }

      }

      public static class ByReference extends SECURITY_DESCRIPTOR_RELATIVE implements Structure.ByReference {
      }
   }

   public static class PACLByReference extends com.sun.jna.ptr.ByReference {
      public PACLByReference() {
         this((ACL)null);
      }

      public PACLByReference(ACL h) {
         super(Native.POINTER_SIZE);
         this.setValue(h);
      }

      public void setValue(ACL h) {
         this.getPointer().setPointer(0L, h != null ? h.getPointer() : null);
      }

      public ACL getValue() {
         Pointer p = this.getPointer().getPointer(0L);
         return p == null ? null : new ACL(p);
      }
   }

   @Structure.FieldOrder({"AclRevision", "Sbz1", "AclSize", "AceCount", "Sbz2"})
   public static class ACL extends Structure {
      public static int MAX_ACL_SIZE = 65536;
      public byte AclRevision;
      public byte Sbz1;
      public short AclSize;
      public short AceCount;
      public short Sbz2;

      public ACL() {
      }

      public ACL(int size) {
         this.useMemory(new Memory((long)size));
      }

      public ACL(Pointer pointer) {
         super(pointer);
         this.read();
      }

      public ACE_HEADER[] getACEs() {
         ACE_HEADER[] ACEs = new ACE_HEADER[this.AceCount];
         Pointer pointer = this.getPointer();
         int offset = this.size();

         for(int i = 0; i < this.AceCount; ++i) {
            Pointer share = pointer.share((long)offset);
            byte aceType = share.getByte(0L);
            switch (aceType) {
               case 0:
                  ACEs[i] = new ACCESS_ALLOWED_ACE(share);
                  break;
               case 1:
                  ACEs[i] = new ACCESS_DENIED_ACE(share);
                  break;
               default:
                  ACEs[i] = new ACE_HEADER(share);
            }

            offset += ACEs[i].AceSize;
         }

         return ACEs;
      }
   }

   @Structure.FieldOrder({"data"})
   public static class SECURITY_DESCRIPTOR extends Structure {
      public byte[] data;

      public SECURITY_DESCRIPTOR() {
      }

      public SECURITY_DESCRIPTOR(byte[] data) {
         this.data = data;
         this.useMemory(new Memory((long)data.length));
      }

      public SECURITY_DESCRIPTOR(int size) {
         this.useMemory(new Memory((long)size));
         this.data = new byte[size];
      }

      public SECURITY_DESCRIPTOR(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends SECURITY_DESCRIPTOR implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"Length", "Reserved", "RecordNumber", "TimeGenerated", "TimeWritten", "EventID", "EventType", "NumStrings", "EventCategory", "ReservedFlags", "ClosingRecordNumber", "StringOffset", "UserSidLength", "UserSidOffset", "DataLength", "DataOffset"})
   public static class EVENTLOGRECORD extends Structure {
      public WinDef.DWORD Length;
      public WinDef.DWORD Reserved;
      public WinDef.DWORD RecordNumber;
      public WinDef.DWORD TimeGenerated;
      public WinDef.DWORD TimeWritten;
      public WinDef.DWORD EventID;
      public WinDef.WORD EventType;
      public WinDef.WORD NumStrings;
      public WinDef.WORD EventCategory;
      public WinDef.WORD ReservedFlags;
      public WinDef.DWORD ClosingRecordNumber;
      public WinDef.DWORD StringOffset;
      public WinDef.DWORD UserSidLength;
      public WinDef.DWORD UserSidOffset;
      public WinDef.DWORD DataLength;
      public WinDef.DWORD DataOffset;

      public EVENTLOGRECORD() {
      }

      public EVENTLOGRECORD(Pointer p) {
         super(p);
         this.read();
      }
   }

   @Structure.FieldOrder({"dwOSVersionInfoSize", "dwMajorVersion", "dwMinorVersion", "dwBuildNumber", "dwPlatformId", "szCSDVersion", "wServicePackMajor", "wServicePackMinor", "wSuiteMask", "wProductType", "wReserved"})
   public static class OSVERSIONINFOEX extends Structure {
      public WinDef.DWORD dwOSVersionInfoSize;
      public WinDef.DWORD dwMajorVersion;
      public WinDef.DWORD dwMinorVersion;
      public WinDef.DWORD dwBuildNumber;
      public WinDef.DWORD dwPlatformId;
      public char[] szCSDVersion;
      public WinDef.WORD wServicePackMajor;
      public WinDef.WORD wServicePackMinor;
      public WinDef.WORD wSuiteMask;
      public byte wProductType;
      public byte wReserved;

      public OSVERSIONINFOEX() {
         this.szCSDVersion = new char[128];
         this.dwOSVersionInfoSize = new WinDef.DWORD((long)this.size());
      }

      public OSVERSIONINFOEX(Pointer memory) {
         super(memory);
         this.read();
      }

      public int getMajor() {
         return this.dwMajorVersion.intValue();
      }

      public int getMinor() {
         return this.dwMinorVersion.intValue();
      }

      public int getBuildNumber() {
         return this.dwBuildNumber.intValue();
      }

      public int getPlatformId() {
         return this.dwPlatformId.intValue();
      }

      public String getServicePack() {
         return Native.toString(this.szCSDVersion);
      }

      public int getSuiteMask() {
         return this.wSuiteMask.intValue();
      }

      public byte getProductType() {
         return this.wProductType;
      }
   }

   @Structure.FieldOrder({"dwOSVersionInfoSize", "dwMajorVersion", "dwMinorVersion", "dwBuildNumber", "dwPlatformId", "szCSDVersion"})
   public static class OSVERSIONINFO extends Structure {
      public WinDef.DWORD dwOSVersionInfoSize;
      public WinDef.DWORD dwMajorVersion;
      public WinDef.DWORD dwMinorVersion;
      public WinDef.DWORD dwBuildNumber;
      public WinDef.DWORD dwPlatformId;
      public char[] szCSDVersion;

      public OSVERSIONINFO() {
         this.szCSDVersion = new char[128];
         this.dwOSVersionInfoSize = new WinDef.DWORD((long)this.size());
      }

      public OSVERSIONINFO(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   public abstract static class WELL_KNOWN_SID_TYPE {
      public static final int WinNullSid = 0;
      public static final int WinWorldSid = 1;
      public static final int WinLocalSid = 2;
      public static final int WinCreatorOwnerSid = 3;
      public static final int WinCreatorGroupSid = 4;
      public static final int WinCreatorOwnerServerSid = 5;
      public static final int WinCreatorGroupServerSid = 6;
      public static final int WinNtAuthoritySid = 7;
      public static final int WinDialupSid = 8;
      public static final int WinNetworkSid = 9;
      public static final int WinBatchSid = 10;
      public static final int WinInteractiveSid = 11;
      public static final int WinServiceSid = 12;
      public static final int WinAnonymousSid = 13;
      public static final int WinProxySid = 14;
      public static final int WinEnterpriseControllersSid = 15;
      public static final int WinSelfSid = 16;
      public static final int WinAuthenticatedUserSid = 17;
      public static final int WinRestrictedCodeSid = 18;
      public static final int WinTerminalServerSid = 19;
      public static final int WinRemoteLogonIdSid = 20;
      public static final int WinLogonIdsSid = 21;
      public static final int WinLocalSystemSid = 22;
      public static final int WinLocalServiceSid = 23;
      public static final int WinNetworkServiceSid = 24;
      public static final int WinBuiltinDomainSid = 25;
      public static final int WinBuiltinAdministratorsSid = 26;
      public static final int WinBuiltinUsersSid = 27;
      public static final int WinBuiltinGuestsSid = 28;
      public static final int WinBuiltinPowerUsersSid = 29;
      public static final int WinBuiltinAccountOperatorsSid = 30;
      public static final int WinBuiltinSystemOperatorsSid = 31;
      public static final int WinBuiltinPrintOperatorsSid = 32;
      public static final int WinBuiltinBackupOperatorsSid = 33;
      public static final int WinBuiltinReplicatorSid = 34;
      public static final int WinBuiltinPreWindows2000CompatibleAccessSid = 35;
      public static final int WinBuiltinRemoteDesktopUsersSid = 36;
      public static final int WinBuiltinNetworkConfigurationOperatorsSid = 37;
      public static final int WinAccountAdministratorSid = 38;
      public static final int WinAccountGuestSid = 39;
      public static final int WinAccountKrbtgtSid = 40;
      public static final int WinAccountDomainAdminsSid = 41;
      public static final int WinAccountDomainUsersSid = 42;
      public static final int WinAccountDomainGuestsSid = 43;
      public static final int WinAccountComputersSid = 44;
      public static final int WinAccountControllersSid = 45;
      public static final int WinAccountCertAdminsSid = 46;
      public static final int WinAccountSchemaAdminsSid = 47;
      public static final int WinAccountEnterpriseAdminsSid = 48;
      public static final int WinAccountPolicyAdminsSid = 49;
      public static final int WinAccountRasAndIasServersSid = 50;
      public static final int WinNTLMAuthenticationSid = 51;
      public static final int WinDigestAuthenticationSid = 52;
      public static final int WinSChannelAuthenticationSid = 53;
      public static final int WinThisOrganizationSid = 54;
      public static final int WinOtherOrganizationSid = 55;
      public static final int WinBuiltinIncomingForestTrustBuildersSid = 56;
      public static final int WinBuiltinPerfMonitoringUsersSid = 57;
      public static final int WinBuiltinPerfLoggingUsersSid = 58;
      public static final int WinBuiltinAuthorizationAccessSid = 59;
      public static final int WinBuiltinTerminalServerLicenseServersSid = 60;
      public static final int WinBuiltinDCOMUsersSid = 61;
      public static final int WinBuiltinIUsersSid = 62;
      public static final int WinIUserSid = 63;
      public static final int WinBuiltinCryptoOperatorsSid = 64;
      public static final int WinUntrustedLabelSid = 65;
      public static final int WinLowLabelSid = 66;
      public static final int WinMediumLabelSid = 67;
      public static final int WinHighLabelSid = 68;
      public static final int WinSystemLabelSid = 69;
      public static final int WinWriteRestrictedCodeSid = 70;
      public static final int WinCreatorOwnerRightsSid = 71;
      public static final int WinCacheablePrincipalsGroupSid = 72;
      public static final int WinNonCacheablePrincipalsGroupSid = 73;
      public static final int WinEnterpriseReadonlyControllersSid = 74;
      public static final int WinAccountReadonlyControllersSid = 75;
      public static final int WinBuiltinEventLogReadersGroup = 76;
   }

   public static class HRESULT extends NativeLong {
      public HRESULT() {
      }

      public HRESULT(int value) {
         super((long)value);
      }
   }

   public static class HANDLEByReference extends com.sun.jna.ptr.ByReference {
      public HANDLEByReference() {
         this((HANDLE)null);
      }

      public HANDLEByReference(HANDLE h) {
         super(Native.POINTER_SIZE);
         this.setValue(h);
      }

      public void setValue(HANDLE h) {
         this.getPointer().setPointer(0L, h != null ? h.getPointer() : null);
      }

      public HANDLE getValue() {
         Pointer p = this.getPointer().getPointer(0L);
         if (p == null) {
            return null;
         } else if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p)) {
            return WinBase.INVALID_HANDLE_VALUE;
         } else {
            HANDLE h = new HANDLE();
            h.setPointer(p);
            return h;
         }
      }
   }

   public static class HANDLE extends PointerType {
      private boolean immutable;

      public HANDLE() {
      }

      public HANDLE(Pointer p) {
         this.setPointer(p);
         this.immutable = true;
      }

      public Object fromNative(Object nativeValue, FromNativeContext context) {
         Object o = super.fromNative(nativeValue, context);
         return WinBase.INVALID_HANDLE_VALUE.equals(o) ? WinBase.INVALID_HANDLE_VALUE : o;
      }

      public void setPointer(Pointer p) {
         if (this.immutable) {
            throw new UnsupportedOperationException("immutable reference");
         } else {
            super.setPointer(p);
         }
      }

      public String toString() {
         return String.valueOf(this.getPointer());
      }
   }

   @Structure.FieldOrder({"u"})
   public static class LARGE_INTEGER extends Structure implements Comparable<LARGE_INTEGER> {
      public UNION u;

      public LARGE_INTEGER() {
      }

      public LARGE_INTEGER(long value) {
         this.u = new UNION(value);
      }

      public WinDef.DWORD getLow() {
         return this.u.lh.LowPart;
      }

      public WinDef.DWORD getHigh() {
         return this.u.lh.HighPart;
      }

      public long getValue() {
         return this.u.value;
      }

      public int compareTo(LARGE_INTEGER other) {
         return compare(this, other);
      }

      public String toString() {
         return this.u == null ? "null" : Long.toString(this.getValue());
      }

      public static int compare(LARGE_INTEGER v1, LARGE_INTEGER v2) {
         if (v1 == v2) {
            return 0;
         } else if (v1 == null) {
            return 1;
         } else {
            return v2 == null ? -1 : IntegerType.compare(v1.getValue(), v2.getValue());
         }
      }

      public static int compare(LARGE_INTEGER v1, long v2) {
         return v1 == null ? 1 : IntegerType.compare(v1.getValue(), v2);
      }

      public static class UNION extends Union {
         public LowHigh lh;
         public long value;

         public UNION() {
         }

         public UNION(long value) {
            this.value = value;
            this.lh = new LowHigh(value);
         }

         public void read() {
            this.readField("lh");
            this.readField("value");
         }

         public long longValue() {
            return this.value;
         }

         public String toString() {
            return Long.toString(this.longValue());
         }
      }

      @Structure.FieldOrder({"LowPart", "HighPart"})
      public static class LowHigh extends Structure {
         public WinDef.DWORD LowPart;
         public WinDef.DWORD HighPart;

         public LowHigh() {
         }

         public LowHigh(long value) {
            this(new WinDef.DWORD(value & 4294967295L), new WinDef.DWORD(value >> 32 & 4294967295L));
         }

         public LowHigh(WinDef.DWORD low, WinDef.DWORD high) {
            this.LowPart = low;
            this.HighPart = high;
         }

         public long longValue() {
            long loValue = this.LowPart.longValue();
            long hiValue = this.HighPart.longValue();
            return hiValue << 32 & -4294967296L | loValue & 4294967295L;
         }

         public String toString() {
            return this.LowPart != null && this.HighPart != null ? Long.toString(this.longValue()) : "null";
         }
      }

      public static class ByReference extends LARGE_INTEGER implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"LowPart", "HighPart"})
   public static class LUID extends Structure {
      public int LowPart;
      public int HighPart;
   }

   @Structure.FieldOrder({"NextEntryOffset", "Action", "FileNameLength", "FileName"})
   public static class FILE_NOTIFY_INFORMATION extends Structure {
      public int NextEntryOffset;
      public int Action;
      public int FileNameLength;
      public char[] FileName = new char[1];

      private FILE_NOTIFY_INFORMATION() {
      }

      public FILE_NOTIFY_INFORMATION(int size) {
         if (size < this.size()) {
            throw new IllegalArgumentException("Size must greater than " + this.size() + ", requested " + size);
         } else {
            this.allocateMemory(size);
         }
      }

      public String getFilename() {
         return new String(this.FileName, 0, this.FileNameLength / 2);
      }

      public void read() {
         this.FileName = new char[0];
         super.read();
         this.FileName = this.getPointer().getCharArray(12L, this.FileNameLength / 2);
      }

      public FILE_NOTIFY_INFORMATION next() {
         if (this.NextEntryOffset == 0) {
            return null;
         } else {
            FILE_NOTIFY_INFORMATION next = new FILE_NOTIFY_INFORMATION();
            next.useMemory(this.getPointer(), this.NextEntryOffset);
            next.read();
            return next;
         }
      }
   }

   public abstract static class SID_NAME_USE {
      public static final int SidTypeUser = 1;
      public static final int SidTypeGroup = 2;
      public static final int SidTypeDomain = 3;
      public static final int SidTypeAlias = 4;
      public static final int SidTypeWellKnownGroup = 5;
      public static final int SidTypeDeletedAccount = 6;
      public static final int SidTypeInvalid = 7;
      public static final int SidTypeUnknown = 8;
      public static final int SidTypeComputer = 9;
      public static final int SidTypeLabel = 10;
   }

   @Structure.FieldOrder({"PrivilegeCount", "Privileges"})
   public static class TOKEN_PRIVILEGES extends Structure {
      public WinDef.DWORD PrivilegeCount;
      public LUID_AND_ATTRIBUTES[] Privileges;

      public TOKEN_PRIVILEGES() {
         this(0);
      }

      public TOKEN_PRIVILEGES(int nbOfPrivileges) {
         this.PrivilegeCount = new WinDef.DWORD((long)nbOfPrivileges);
         this.Privileges = new LUID_AND_ATTRIBUTES[nbOfPrivileges];
      }

      public TOKEN_PRIVILEGES(Pointer p) {
         super(p);
         int count = p.getInt(0L);
         this.PrivilegeCount = new WinDef.DWORD((long)count);
         this.Privileges = new LUID_AND_ATTRIBUTES[count];
         this.read();
      }
   }

   @Structure.FieldOrder({"PrivilegeCount", "Control", "Privileges"})
   public static class PRIVILEGE_SET extends Structure {
      public WinDef.DWORD PrivilegeCount;
      public WinDef.DWORD Control;
      public LUID_AND_ATTRIBUTES[] Privileges;

      public PRIVILEGE_SET() {
         this(0);
      }

      public PRIVILEGE_SET(int nbOfPrivileges) {
         this.PrivilegeCount = new WinDef.DWORD((long)nbOfPrivileges);
         if (nbOfPrivileges > 0) {
            this.Privileges = new LUID_AND_ATTRIBUTES[nbOfPrivileges];
         }

      }

      public PRIVILEGE_SET(Pointer p) {
         super(p);
         int count = p.getInt(0L);
         this.PrivilegeCount = new WinDef.DWORD((long)count);
         if (count > 0) {
            this.Privileges = new LUID_AND_ATTRIBUTES[count];
         }

         this.read();
      }
   }

   @Structure.FieldOrder({"GroupCount", "Group0"})
   public static class TOKEN_GROUPS extends Structure {
      public int GroupCount;
      public SID_AND_ATTRIBUTES Group0;

      public TOKEN_GROUPS() {
      }

      public TOKEN_GROUPS(Pointer memory) {
         super(memory);
         this.read();
      }

      public TOKEN_GROUPS(int size) {
         super((Pointer)(new Memory((long)size)));
      }

      public SID_AND_ATTRIBUTES[] getGroups() {
         return (SID_AND_ATTRIBUTES[])((SID_AND_ATTRIBUTES[])this.Group0.toArray(this.GroupCount));
      }
   }

   @Structure.FieldOrder({"PrimaryGroup"})
   public static class TOKEN_PRIMARY_GROUP extends Structure {
      public PSID.ByReference PrimaryGroup;

      public TOKEN_PRIMARY_GROUP() {
      }

      public TOKEN_PRIMARY_GROUP(Pointer memory) {
         super(memory);
         this.read();
      }

      public TOKEN_PRIMARY_GROUP(int size) {
         super((Pointer)(new Memory((long)size)));
      }
   }

   @Structure.FieldOrder({"User"})
   public static class TOKEN_USER extends Structure {
      public SID_AND_ATTRIBUTES User;

      public TOKEN_USER() {
      }

      public TOKEN_USER(Pointer memory) {
         super(memory);
         this.read();
      }

      public TOKEN_USER(int size) {
         super((Pointer)(new Memory((long)size)));
      }
   }

   public static class PSIDByReference extends com.sun.jna.ptr.ByReference {
      public PSIDByReference() {
         this((PSID)null);
      }

      public PSIDByReference(PSID h) {
         super(Native.POINTER_SIZE);
         this.setValue(h);
      }

      public void setValue(PSID h) {
         this.getPointer().setPointer(0L, h != null ? h.getPointer() : null);
      }

      public PSID getValue() {
         Pointer p = this.getPointer().getPointer(0L);
         return p == null ? null : new PSID(p);
      }
   }

   @Structure.FieldOrder({"sid"})
   public static class PSID extends Structure {
      public Pointer sid;

      public PSID() {
      }

      public PSID(byte[] data) {
         super((Pointer)(new Memory((long)data.length)));
         this.getPointer().write(0L, (byte[])data, 0, data.length);
         this.read();
      }

      public PSID(int size) {
         super((Pointer)(new Memory((long)size)));
      }

      public PSID(Pointer memory) {
         super(memory);
         this.read();
      }

      public byte[] getBytes() {
         int len = Advapi32.INSTANCE.GetLengthSid(this);
         return this.getPointer().getByteArray(0L, len);
      }

      public String getSidString() {
         return Advapi32Util.convertSidToStringSid(this);
      }

      public static class ByReference extends PSID implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"Owner"})
   public static class TOKEN_OWNER extends Structure {
      public PSID.ByReference Owner;

      public TOKEN_OWNER() {
      }

      public TOKEN_OWNER(int size) {
         super((Pointer)(new Memory((long)size)));
      }

      public TOKEN_OWNER(Pointer memory) {
         super(memory);
         this.read();
      }
   }

   @Structure.FieldOrder({"Sid", "Attributes"})
   public static class SID_AND_ATTRIBUTES extends Structure {
      public PSID.ByReference Sid;
      public int Attributes;

      public SID_AND_ATTRIBUTES() {
      }

      public SID_AND_ATTRIBUTES(Pointer memory) {
         super(memory);
      }
   }

   @Structure.FieldOrder({"Luid", "Attributes"})
   public static class LUID_AND_ATTRIBUTES extends Structure {
      public LUID Luid;
      public WinDef.DWORD Attributes;

      public LUID_AND_ATTRIBUTES() {
      }

      public LUID_AND_ATTRIBUTES(LUID luid, WinDef.DWORD attributes) {
         this.Luid = luid;
         this.Attributes = attributes;
      }
   }

   public abstract static class TOKEN_TYPE {
      public static final int TokenPrimary = 1;
      public static final int TokenImpersonation = 2;
   }

   public abstract static class TOKEN_INFORMATION_CLASS {
      public static final int TokenUser = 1;
      public static final int TokenGroups = 2;
      public static final int TokenPrivileges = 3;
      public static final int TokenOwner = 4;
      public static final int TokenPrimaryGroup = 5;
      public static final int TokenDefaultDacl = 6;
      public static final int TokenSource = 7;
      public static final int TokenType = 8;
      public static final int TokenImpersonationLevel = 9;
      public static final int TokenStatistics = 10;
      public static final int TokenRestrictedSids = 11;
      public static final int TokenSessionId = 12;
      public static final int TokenGroupsAndPrivileges = 13;
      public static final int TokenSessionReference = 14;
      public static final int TokenSandBoxInert = 15;
      public static final int TokenAuditPolicy = 16;
      public static final int TokenOrigin = 17;
      public static final int TokenElevationType = 18;
      public static final int TokenLinkedToken = 19;
      public static final int TokenElevation = 20;
      public static final int TokenHasRestrictions = 21;
      public static final int TokenAccessInformation = 22;
      public static final int TokenVirtualizationAllowed = 23;
      public static final int TokenVirtualizationEnabled = 24;
      public static final int TokenIntegrityLevel = 25;
      public static final int TokenUIAccess = 26;
      public static final int TokenMandatoryPolicy = 27;
      public static final int TokenLogonSid = 28;
   }

   public abstract static class SECURITY_IMPERSONATION_LEVEL {
      public static final int SecurityAnonymous = 0;
      public static final int SecurityIdentification = 1;
      public static final int SecurityImpersonation = 2;
      public static final int SecurityDelegation = 3;
   }
}
