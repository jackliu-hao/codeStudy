package com.sun.jna.platform.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Advapi32 extends StdCallLibrary {
   Advapi32 INSTANCE = (Advapi32)Native.load("Advapi32", Advapi32.class, W32APIOptions.DEFAULT_OPTIONS);
   int MAX_KEY_LENGTH = 255;
   int MAX_VALUE_NAME = 16383;
   int RRF_RT_ANY = 65535;
   int RRF_RT_DWORD = 24;
   int RRF_RT_QWORD = 72;
   int RRF_RT_REG_BINARY = 8;
   int RRF_RT_REG_DWORD = 16;
   int RRF_RT_REG_EXPAND_SZ = 4;
   int RRF_RT_REG_MULTI_SZ = 32;
   int RRF_RT_REG_NONE = 1;
   int RRF_RT_REG_QWORD = 64;
   int RRF_RT_REG_SZ = 2;
   int LOGON_WITH_PROFILE = 1;
   int LOGON_NETCREDENTIALS_ONLY = 2;

   boolean GetUserNameW(char[] var1, IntByReference var2);

   boolean LookupAccountName(String var1, String var2, WinNT.PSID var3, IntByReference var4, char[] var5, IntByReference var6, PointerByReference var7);

   boolean LookupAccountSid(String var1, WinNT.PSID var2, char[] var3, IntByReference var4, char[] var5, IntByReference var6, PointerByReference var7);

   boolean ConvertSidToStringSid(WinNT.PSID var1, PointerByReference var2);

   boolean ConvertStringSidToSid(String var1, WinNT.PSIDByReference var2);

   int GetLengthSid(WinNT.PSID var1);

   boolean IsValidSid(WinNT.PSID var1);

   boolean EqualSid(WinNT.PSID var1, WinNT.PSID var2);

   boolean IsWellKnownSid(WinNT.PSID var1, int var2);

   boolean CreateWellKnownSid(int var1, WinNT.PSID var2, WinNT.PSID var3, IntByReference var4);

   boolean InitializeSecurityDescriptor(WinNT.SECURITY_DESCRIPTOR var1, int var2);

   boolean GetSecurityDescriptorControl(WinNT.SECURITY_DESCRIPTOR var1, ShortByReference var2, IntByReference var3);

   boolean SetSecurityDescriptorControl(WinNT.SECURITY_DESCRIPTOR var1, short var2, short var3);

   boolean GetSecurityDescriptorOwner(WinNT.SECURITY_DESCRIPTOR var1, WinNT.PSIDByReference var2, WinDef.BOOLByReference var3);

   boolean SetSecurityDescriptorOwner(WinNT.SECURITY_DESCRIPTOR var1, WinNT.PSID var2, boolean var3);

   boolean GetSecurityDescriptorGroup(WinNT.SECURITY_DESCRIPTOR var1, WinNT.PSIDByReference var2, WinDef.BOOLByReference var3);

   boolean SetSecurityDescriptorGroup(WinNT.SECURITY_DESCRIPTOR var1, WinNT.PSID var2, boolean var3);

   boolean GetSecurityDescriptorDacl(WinNT.SECURITY_DESCRIPTOR var1, WinDef.BOOLByReference var2, WinNT.PACLByReference var3, WinDef.BOOLByReference var4);

   boolean SetSecurityDescriptorDacl(WinNT.SECURITY_DESCRIPTOR var1, boolean var2, WinNT.ACL var3, boolean var4);

   boolean InitializeAcl(WinNT.ACL var1, int var2, int var3);

   boolean AddAce(WinNT.ACL var1, int var2, int var3, Pointer var4, int var5);

   boolean AddAccessAllowedAce(WinNT.ACL var1, int var2, int var3, WinNT.PSID var4);

   boolean AddAccessAllowedAceEx(WinNT.ACL var1, int var2, int var3, int var4, WinNT.PSID var5);

   boolean GetAce(WinNT.ACL var1, int var2, PointerByReference var3);

   boolean LogonUser(String var1, String var2, String var3, int var4, int var5, WinNT.HANDLEByReference var6);

   boolean OpenThreadToken(WinNT.HANDLE var1, int var2, boolean var3, WinNT.HANDLEByReference var4);

   boolean SetThreadToken(WinNT.HANDLEByReference var1, WinNT.HANDLE var2);

   boolean OpenProcessToken(WinNT.HANDLE var1, int var2, WinNT.HANDLEByReference var3);

   boolean DuplicateToken(WinNT.HANDLE var1, int var2, WinNT.HANDLEByReference var3);

   boolean DuplicateTokenEx(WinNT.HANDLE var1, int var2, WinBase.SECURITY_ATTRIBUTES var3, int var4, int var5, WinNT.HANDLEByReference var6);

   boolean GetTokenInformation(WinNT.HANDLE var1, int var2, Structure var3, int var4, IntByReference var5);

   boolean ImpersonateLoggedOnUser(WinNT.HANDLE var1);

   boolean ImpersonateSelf(int var1);

   boolean RevertToSelf();

   int RegOpenKeyEx(WinReg.HKEY var1, String var2, int var3, int var4, WinReg.HKEYByReference var5);

   int RegConnectRegistry(String var1, WinReg.HKEY var2, WinReg.HKEYByReference var3);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, char[] var5, IntByReference var6);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, byte[] var5, IntByReference var6);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, IntByReference var5, IntByReference var6);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, LongByReference var5, IntByReference var6);

   int RegQueryValueEx(WinReg.HKEY var1, String var2, int var3, IntByReference var4, Pointer var5, IntByReference var6);

   int RegCloseKey(WinReg.HKEY var1);

   int RegDeleteValue(WinReg.HKEY var1, String var2);

   int RegSetValueEx(WinReg.HKEY var1, String var2, int var3, int var4, Pointer var5, int var6);

   int RegSetValueEx(WinReg.HKEY var1, String var2, int var3, int var4, char[] var5, int var6);

   int RegSetValueEx(WinReg.HKEY var1, String var2, int var3, int var4, byte[] var5, int var6);

   int RegCreateKeyEx(WinReg.HKEY var1, String var2, int var3, String var4, int var5, int var6, WinBase.SECURITY_ATTRIBUTES var7, WinReg.HKEYByReference var8, IntByReference var9);

   int RegDeleteKey(WinReg.HKEY var1, String var2);

   int RegEnumKeyEx(WinReg.HKEY var1, int var2, char[] var3, IntByReference var4, IntByReference var5, char[] var6, IntByReference var7, WinBase.FILETIME var8);

   int RegEnumValue(WinReg.HKEY var1, int var2, char[] var3, IntByReference var4, IntByReference var5, IntByReference var6, Pointer var7, IntByReference var8);

   int RegEnumValue(WinReg.HKEY var1, int var2, char[] var3, IntByReference var4, IntByReference var5, IntByReference var6, byte[] var7, IntByReference var8);

   int RegQueryInfoKey(WinReg.HKEY var1, char[] var2, IntByReference var3, IntByReference var4, IntByReference var5, IntByReference var6, IntByReference var7, IntByReference var8, IntByReference var9, IntByReference var10, IntByReference var11, WinBase.FILETIME var12);

   int RegGetValue(WinReg.HKEY var1, String var2, String var3, int var4, IntByReference var5, Pointer var6, IntByReference var7);

   int RegGetValue(WinReg.HKEY var1, String var2, String var3, int var4, IntByReference var5, byte[] var6, IntByReference var7);

   WinNT.HANDLE RegisterEventSource(String var1, String var2);

   boolean DeregisterEventSource(WinNT.HANDLE var1);

   WinNT.HANDLE OpenEventLog(String var1, String var2);

   boolean CloseEventLog(WinNT.HANDLE var1);

   boolean GetNumberOfEventLogRecords(WinNT.HANDLE var1, IntByReference var2);

   boolean ReportEvent(WinNT.HANDLE var1, int var2, int var3, int var4, WinNT.PSID var5, int var6, int var7, String[] var8, Pointer var9);

   boolean ClearEventLog(WinNT.HANDLE var1, String var2);

   boolean BackupEventLog(WinNT.HANDLE var1, String var2);

   WinNT.HANDLE OpenBackupEventLog(String var1, String var2);

   boolean ReadEventLog(WinNT.HANDLE var1, int var2, int var3, Pointer var4, int var5, IntByReference var6, IntByReference var7);

   boolean GetOldestEventLogRecord(WinNT.HANDLE var1, IntByReference var2);

   boolean ChangeServiceConfig2(Winsvc.SC_HANDLE var1, int var2, Winsvc.ChangeServiceConfig2Info var3);

   boolean QueryServiceConfig2(Winsvc.SC_HANDLE var1, int var2, Pointer var3, int var4, IntByReference var5);

   boolean QueryServiceStatusEx(Winsvc.SC_HANDLE var1, int var2, Winsvc.SERVICE_STATUS_PROCESS var3, int var4, IntByReference var5);

   boolean QueryServiceStatus(Winsvc.SC_HANDLE var1, Winsvc.SERVICE_STATUS var2);

   boolean ControlService(Winsvc.SC_HANDLE var1, int var2, Winsvc.SERVICE_STATUS var3);

   boolean StartService(Winsvc.SC_HANDLE var1, int var2, String[] var3);

   boolean CloseServiceHandle(Winsvc.SC_HANDLE var1);

   Winsvc.SC_HANDLE OpenService(Winsvc.SC_HANDLE var1, String var2, int var3);

   Winsvc.SC_HANDLE OpenSCManager(String var1, String var2, int var3);

   boolean EnumDependentServices(Winsvc.SC_HANDLE var1, int var2, Pointer var3, int var4, IntByReference var5, IntByReference var6);

   boolean EnumServicesStatusEx(Winsvc.SC_HANDLE var1, int var2, int var3, int var4, Pointer var5, int var6, IntByReference var7, IntByReference var8, IntByReference var9, String var10);

   boolean CreateProcessAsUser(WinNT.HANDLE var1, String var2, String var3, WinBase.SECURITY_ATTRIBUTES var4, WinBase.SECURITY_ATTRIBUTES var5, boolean var6, int var7, String var8, String var9, WinBase.STARTUPINFO var10, WinBase.PROCESS_INFORMATION var11);

   boolean AdjustTokenPrivileges(WinNT.HANDLE var1, boolean var2, WinNT.TOKEN_PRIVILEGES var3, int var4, WinNT.TOKEN_PRIVILEGES var5, IntByReference var6);

   boolean LookupPrivilegeName(String var1, WinNT.LUID var2, char[] var3, IntByReference var4);

   boolean LookupPrivilegeValue(String var1, String var2, WinNT.LUID var3);

   boolean GetFileSecurity(String var1, int var2, Pointer var3, int var4, IntByReference var5);

   boolean SetFileSecurity(String var1, int var2, Pointer var3);

   int GetSecurityInfo(WinNT.HANDLE var1, int var2, int var3, PointerByReference var4, PointerByReference var5, PointerByReference var6, PointerByReference var7, PointerByReference var8);

   int SetSecurityInfo(WinNT.HANDLE var1, int var2, int var3, Pointer var4, Pointer var5, Pointer var6, Pointer var7);

   int GetNamedSecurityInfo(String var1, int var2, int var3, PointerByReference var4, PointerByReference var5, PointerByReference var6, PointerByReference var7, PointerByReference var8);

   int SetNamedSecurityInfo(String var1, int var2, int var3, Pointer var4, Pointer var5, Pointer var6, Pointer var7);

   int GetSecurityDescriptorLength(Pointer var1);

   boolean IsValidSecurityDescriptor(Pointer var1);

   boolean MakeSelfRelativeSD(WinNT.SECURITY_DESCRIPTOR var1, WinNT.SECURITY_DESCRIPTOR_RELATIVE var2, IntByReference var3);

   boolean MakeAbsoluteSD(WinNT.SECURITY_DESCRIPTOR_RELATIVE var1, WinNT.SECURITY_DESCRIPTOR var2, IntByReference var3, WinNT.ACL var4, IntByReference var5, WinNT.ACL var6, IntByReference var7, WinNT.PSID var8, IntByReference var9, WinNT.PSID var10, IntByReference var11);

   boolean IsValidAcl(Pointer var1);

   void MapGenericMask(WinDef.DWORDByReference var1, WinNT.GENERIC_MAPPING var2);

   boolean AccessCheck(Pointer var1, WinNT.HANDLE var2, WinDef.DWORD var3, WinNT.GENERIC_MAPPING var4, WinNT.PRIVILEGE_SET var5, WinDef.DWORDByReference var6, WinDef.DWORDByReference var7, WinDef.BOOLByReference var8);

   boolean EncryptFile(String var1);

   boolean DecryptFile(String var1, WinDef.DWORD var2);

   boolean FileEncryptionStatus(String var1, WinDef.DWORDByReference var2);

   boolean EncryptionDisable(String var1, boolean var2);

   int OpenEncryptedFileRaw(String var1, WinDef.ULONG var2, PointerByReference var3);

   int ReadEncryptedFileRaw(WinBase.FE_EXPORT_FUNC var1, Pointer var2, Pointer var3);

   int WriteEncryptedFileRaw(WinBase.FE_IMPORT_FUNC var1, Pointer var2, Pointer var3);

   void CloseEncryptedFileRaw(Pointer var1);

   boolean CreateProcessWithLogonW(String var1, String var2, String var3, int var4, String var5, String var6, int var7, Pointer var8, String var9, WinBase.STARTUPINFO var10, WinBase.PROCESS_INFORMATION var11);

   boolean StartServiceCtrlDispatcher(Winsvc.SERVICE_TABLE_ENTRY[] var1);

   Winsvc.SERVICE_STATUS_HANDLE RegisterServiceCtrlHandler(String var1, Library.Handler var2);

   Winsvc.SERVICE_STATUS_HANDLE RegisterServiceCtrlHandlerEx(String var1, Winsvc.HandlerEx var2, Pointer var3);

   boolean SetServiceStatus(Winsvc.SERVICE_STATUS_HANDLE var1, Winsvc.SERVICE_STATUS var2);

   Winsvc.SC_HANDLE CreateService(Winsvc.SC_HANDLE var1, String var2, String var3, int var4, int var5, int var6, int var7, String var8, String var9, IntByReference var10, String var11, String var12, String var13);

   boolean DeleteService(Winsvc.SC_HANDLE var1);
}
