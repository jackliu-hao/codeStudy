package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Kernel32 extends StdCallLibrary, WinNT, Wincon {
   Kernel32 INSTANCE = (Kernel32)Native.load("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);
   int LOAD_LIBRARY_AS_DATAFILE = 2;
   int MAX_PIPE_NAME_LENGTH = 256;

   boolean ReadFile(WinNT.HANDLE var1, byte[] var2, int var3, IntByReference var4, WinBase.OVERLAPPED var5);

   Pointer LocalFree(Pointer var1);

   Pointer GlobalFree(Pointer var1);

   WinDef.HMODULE GetModuleHandle(String var1);

   void GetSystemTime(WinBase.SYSTEMTIME var1);

   boolean SetSystemTime(WinBase.SYSTEMTIME var1);

   void GetLocalTime(WinBase.SYSTEMTIME var1);

   boolean SetLocalTime(WinBase.SYSTEMTIME var1);

   boolean GetSystemTimes(WinBase.FILETIME var1, WinBase.FILETIME var2, WinBase.FILETIME var3);

   int GetTickCount();

   long GetTickCount64();

   int GetCurrentThreadId();

   WinNT.HANDLE GetCurrentThread();

   int GetCurrentProcessId();

   WinNT.HANDLE GetCurrentProcess();

   int GetProcessId(WinNT.HANDLE var1);

   int GetProcessVersion(int var1);

   boolean GetProcessAffinityMask(WinNT.HANDLE var1, BaseTSD.ULONG_PTRByReference var2, BaseTSD.ULONG_PTRByReference var3);

   boolean SetProcessAffinityMask(WinNT.HANDLE var1, BaseTSD.ULONG_PTR var2);

   boolean GetExitCodeProcess(WinNT.HANDLE var1, IntByReference var2);

   boolean TerminateProcess(WinNT.HANDLE var1, int var2);

   int GetLastError();

   void SetLastError(int var1);

   int GetDriveType(String var1);

   int FormatMessage(int var1, Pointer var2, int var3, int var4, PointerByReference var5, int var6, Pointer var7);

   WinNT.HANDLE CreateFile(String var1, int var2, int var3, WinBase.SECURITY_ATTRIBUTES var4, int var5, int var6, WinNT.HANDLE var7);

   boolean CopyFile(String var1, String var2, boolean var3);

   boolean MoveFile(String var1, String var2);

   boolean MoveFileEx(String var1, String var2, WinDef.DWORD var3);

   boolean CreateDirectory(String var1, WinBase.SECURITY_ATTRIBUTES var2);

   WinNT.HANDLE CreateIoCompletionPort(WinNT.HANDLE var1, WinNT.HANDLE var2, Pointer var3, int var4);

   boolean GetQueuedCompletionStatus(WinNT.HANDLE var1, IntByReference var2, BaseTSD.ULONG_PTRByReference var3, PointerByReference var4, int var5);

   boolean PostQueuedCompletionStatus(WinNT.HANDLE var1, int var2, Pointer var3, WinBase.OVERLAPPED var4);

   int WaitForSingleObject(WinNT.HANDLE var1, int var2);

   int WaitForMultipleObjects(int var1, WinNT.HANDLE[] var2, boolean var3, int var4);

   boolean DuplicateHandle(WinNT.HANDLE var1, WinNT.HANDLE var2, WinNT.HANDLE var3, WinNT.HANDLEByReference var4, int var5, boolean var6, int var7);

   boolean CloseHandle(WinNT.HANDLE var1);

   boolean ReadDirectoryChangesW(WinNT.HANDLE var1, WinNT.FILE_NOTIFY_INFORMATION var2, int var3, boolean var4, int var5, IntByReference var6, WinBase.OVERLAPPED var7, WinNT.OVERLAPPED_COMPLETION_ROUTINE var8);

   int GetShortPathName(String var1, char[] var2, int var3);

   Pointer LocalAlloc(int var1, int var2);

   boolean WriteFile(WinNT.HANDLE var1, byte[] var2, int var3, IntByReference var4, WinBase.OVERLAPPED var5);

   boolean FlushFileBuffers(WinNT.HANDLE var1);

   WinNT.HANDLE CreateEvent(WinBase.SECURITY_ATTRIBUTES var1, boolean var2, boolean var3, String var4);

   WinNT.HANDLE OpenEvent(int var1, boolean var2, String var3);

   boolean SetEvent(WinNT.HANDLE var1);

   boolean ResetEvent(WinNT.HANDLE var1);

   boolean PulseEvent(WinNT.HANDLE var1);

   WinNT.HANDLE CreateFileMapping(WinNT.HANDLE var1, WinBase.SECURITY_ATTRIBUTES var2, int var3, int var4, int var5, String var6);

   Pointer MapViewOfFile(WinNT.HANDLE var1, int var2, int var3, int var4, int var5);

   boolean UnmapViewOfFile(Pointer var1);

   boolean GetComputerName(char[] var1, IntByReference var2);

   boolean GetComputerNameEx(int var1, char[] var2, IntByReference var3);

   WinNT.HANDLE OpenThread(int var1, boolean var2, int var3);

   boolean CreateProcess(String var1, String var2, WinBase.SECURITY_ATTRIBUTES var3, WinBase.SECURITY_ATTRIBUTES var4, boolean var5, WinDef.DWORD var6, Pointer var7, String var8, WinBase.STARTUPINFO var9, WinBase.PROCESS_INFORMATION var10);

   boolean CreateProcessW(String var1, char[] var2, WinBase.SECURITY_ATTRIBUTES var3, WinBase.SECURITY_ATTRIBUTES var4, boolean var5, WinDef.DWORD var6, Pointer var7, String var8, WinBase.STARTUPINFO var9, WinBase.PROCESS_INFORMATION var10);

   WinNT.HANDLE OpenProcess(int var1, boolean var2, int var3);

   boolean QueryFullProcessImageName(WinNT.HANDLE var1, int var2, char[] var3, IntByReference var4);

   WinDef.DWORD GetTempPath(WinDef.DWORD var1, char[] var2);

   WinDef.DWORD GetVersion();

   boolean GetVersionEx(WinNT.OSVERSIONINFO var1);

   boolean GetVersionEx(WinNT.OSVERSIONINFOEX var1);

   boolean VerifyVersionInfoW(WinNT.OSVERSIONINFOEX var1, int var2, long var3);

   long VerSetConditionMask(long var1, int var3, byte var4);

   void GetSystemInfo(WinBase.SYSTEM_INFO var1);

   void GetNativeSystemInfo(WinBase.SYSTEM_INFO var1);

   boolean IsWow64Process(WinNT.HANDLE var1, IntByReference var2);

   boolean GetLogicalProcessorInformation(Pointer var1, WinDef.DWORDByReference var2);

   boolean GetLogicalProcessorInformationEx(int var1, Pointer var2, WinDef.DWORDByReference var3);

   boolean GlobalMemoryStatusEx(WinBase.MEMORYSTATUSEX var1);

   boolean GetFileInformationByHandleEx(WinNT.HANDLE var1, int var2, Pointer var3, WinDef.DWORD var4);

   boolean SetFileInformationByHandle(WinNT.HANDLE var1, int var2, Pointer var3, WinDef.DWORD var4);

   boolean GetFileTime(WinNT.HANDLE var1, WinBase.FILETIME var2, WinBase.FILETIME var3, WinBase.FILETIME var4);

   int SetFileTime(WinNT.HANDLE var1, WinBase.FILETIME var2, WinBase.FILETIME var3, WinBase.FILETIME var4);

   boolean SetFileAttributes(String var1, WinDef.DWORD var2);

   WinDef.DWORD GetLogicalDriveStrings(WinDef.DWORD var1, char[] var2);

   boolean GetDiskFreeSpace(String var1, WinDef.DWORDByReference var2, WinDef.DWORDByReference var3, WinDef.DWORDByReference var4, WinDef.DWORDByReference var5);

   boolean GetDiskFreeSpaceEx(String var1, WinNT.LARGE_INTEGER var2, WinNT.LARGE_INTEGER var3, WinNT.LARGE_INTEGER var4);

   boolean DeleteFile(String var1);

   boolean CreatePipe(WinNT.HANDLEByReference var1, WinNT.HANDLEByReference var2, WinBase.SECURITY_ATTRIBUTES var3, int var4);

   boolean CallNamedPipe(String var1, byte[] var2, int var3, byte[] var4, int var5, IntByReference var6, int var7);

   boolean ConnectNamedPipe(WinNT.HANDLE var1, WinBase.OVERLAPPED var2);

   WinNT.HANDLE CreateNamedPipe(String var1, int var2, int var3, int var4, int var5, int var6, int var7, WinBase.SECURITY_ATTRIBUTES var8);

   boolean DisconnectNamedPipe(WinNT.HANDLE var1);

   boolean GetNamedPipeClientComputerName(WinNT.HANDLE var1, char[] var2, int var3);

   boolean GetNamedPipeClientProcessId(WinNT.HANDLE var1, WinDef.ULONGByReference var2);

   boolean GetNamedPipeClientSessionId(WinNT.HANDLE var1, WinDef.ULONGByReference var2);

   boolean GetNamedPipeHandleState(WinNT.HANDLE var1, IntByReference var2, IntByReference var3, IntByReference var4, IntByReference var5, char[] var6, int var7);

   boolean GetNamedPipeInfo(WinNT.HANDLE var1, IntByReference var2, IntByReference var3, IntByReference var4, IntByReference var5);

   boolean GetNamedPipeServerProcessId(WinNT.HANDLE var1, WinDef.ULONGByReference var2);

   boolean GetNamedPipeServerSessionId(WinNT.HANDLE var1, WinDef.ULONGByReference var2);

   boolean PeekNamedPipe(WinNT.HANDLE var1, byte[] var2, int var3, IntByReference var4, IntByReference var5, IntByReference var6);

   boolean SetNamedPipeHandleState(WinNT.HANDLE var1, IntByReference var2, IntByReference var3, IntByReference var4);

   boolean TransactNamedPipe(WinNT.HANDLE var1, byte[] var2, int var3, byte[] var4, int var5, IntByReference var6, WinBase.OVERLAPPED var7);

   boolean WaitNamedPipe(String var1, int var2);

   boolean SetHandleInformation(WinNT.HANDLE var1, int var2, int var3);

   int GetFileAttributes(String var1);

   int GetFileType(WinNT.HANDLE var1);

   boolean DeviceIoControl(WinNT.HANDLE var1, int var2, Pointer var3, int var4, Pointer var5, int var6, IntByReference var7, Pointer var8);

   WinNT.HANDLE CreateToolhelp32Snapshot(WinDef.DWORD var1, WinDef.DWORD var2);

   boolean Process32First(WinNT.HANDLE var1, Tlhelp32.PROCESSENTRY32 var2);

   boolean Process32Next(WinNT.HANDLE var1, Tlhelp32.PROCESSENTRY32 var2);

   boolean Thread32First(WinNT.HANDLE var1, Tlhelp32.THREADENTRY32 var2);

   boolean Thread32Next(WinNT.HANDLE var1, Tlhelp32.THREADENTRY32 var2);

   boolean SetEnvironmentVariable(String var1, String var2);

   int GetEnvironmentVariable(String var1, char[] var2, int var3);

   Pointer GetEnvironmentStrings();

   boolean FreeEnvironmentStrings(Pointer var1);

   WinDef.LCID GetSystemDefaultLCID();

   WinDef.LCID GetUserDefaultLCID();

   int GetPrivateProfileInt(String var1, String var2, int var3, String var4);

   WinDef.DWORD GetPrivateProfileString(String var1, String var2, String var3, char[] var4, WinDef.DWORD var5, String var6);

   boolean WritePrivateProfileString(String var1, String var2, String var3, String var4);

   WinDef.DWORD GetPrivateProfileSection(String var1, char[] var2, WinDef.DWORD var3, String var4);

   WinDef.DWORD GetPrivateProfileSectionNames(char[] var1, WinDef.DWORD var2, String var3);

   boolean WritePrivateProfileSection(String var1, String var2, String var3);

   boolean FileTimeToLocalFileTime(WinBase.FILETIME var1, WinBase.FILETIME var2);

   boolean SystemTimeToTzSpecificLocalTime(WinBase.TIME_ZONE_INFORMATION var1, WinBase.SYSTEMTIME var2, WinBase.SYSTEMTIME var3);

   boolean SystemTimeToFileTime(WinBase.SYSTEMTIME var1, WinBase.FILETIME var2);

   boolean FileTimeToSystemTime(WinBase.FILETIME var1, WinBase.SYSTEMTIME var2);

   /** @deprecated */
   @Deprecated
   WinNT.HANDLE CreateRemoteThread(WinNT.HANDLE var1, WinBase.SECURITY_ATTRIBUTES var2, int var3, WinBase.FOREIGN_THREAD_START_ROUTINE var4, Pointer var5, WinDef.DWORD var6, Pointer var7);

   WinNT.HANDLE CreateRemoteThread(WinNT.HANDLE var1, WinBase.SECURITY_ATTRIBUTES var2, int var3, Pointer var4, Pointer var5, int var6, WinDef.DWORDByReference var7);

   boolean WriteProcessMemory(WinNT.HANDLE var1, Pointer var2, Pointer var3, int var4, IntByReference var5);

   boolean ReadProcessMemory(WinNT.HANDLE var1, Pointer var2, Pointer var3, int var4, IntByReference var5);

   BaseTSD.SIZE_T VirtualQueryEx(WinNT.HANDLE var1, Pointer var2, WinNT.MEMORY_BASIC_INFORMATION var3, BaseTSD.SIZE_T var4);

   boolean DefineDosDevice(int var1, String var2, String var3);

   int QueryDosDevice(String var1, char[] var2, int var3);

   WinNT.HANDLE FindFirstFile(String var1, Pointer var2);

   WinNT.HANDLE FindFirstFileEx(String var1, int var2, Pointer var3, int var4, Pointer var5, WinDef.DWORD var6);

   boolean FindNextFile(WinNT.HANDLE var1, Pointer var2);

   boolean FindClose(WinNT.HANDLE var1);

   WinNT.HANDLE FindFirstVolumeMountPoint(String var1, char[] var2, int var3);

   boolean FindNextVolumeMountPoint(WinNT.HANDLE var1, char[] var2, int var3);

   boolean FindVolumeMountPointClose(WinNT.HANDLE var1);

   boolean GetVolumeNameForVolumeMountPoint(String var1, char[] var2, int var3);

   boolean SetVolumeLabel(String var1, String var2);

   boolean SetVolumeMountPoint(String var1, String var2);

   boolean DeleteVolumeMountPoint(String var1);

   boolean GetVolumeInformation(String var1, char[] var2, int var3, IntByReference var4, IntByReference var5, IntByReference var6, char[] var7, int var8);

   boolean GetVolumePathName(String var1, char[] var2, int var3);

   boolean GetVolumePathNamesForVolumeName(String var1, char[] var2, int var3, IntByReference var4);

   WinNT.HANDLE FindFirstVolume(char[] var1, int var2);

   boolean FindNextVolume(WinNT.HANDLE var1, char[] var2, int var3);

   boolean FindVolumeClose(WinNT.HANDLE var1);

   boolean GetCommState(WinNT.HANDLE var1, WinBase.DCB var2);

   boolean GetCommTimeouts(WinNT.HANDLE var1, WinBase.COMMTIMEOUTS var2);

   boolean SetCommState(WinNT.HANDLE var1, WinBase.DCB var2);

   boolean SetCommTimeouts(WinNT.HANDLE var1, WinBase.COMMTIMEOUTS var2);

   boolean ProcessIdToSessionId(int var1, IntByReference var2);

   WinDef.HMODULE LoadLibraryEx(String var1, WinNT.HANDLE var2, int var3);

   WinDef.HRSRC FindResource(WinDef.HMODULE var1, Pointer var2, Pointer var3);

   WinNT.HANDLE LoadResource(WinDef.HMODULE var1, WinDef.HRSRC var2);

   Pointer LockResource(WinNT.HANDLE var1);

   int SizeofResource(WinDef.HMODULE var1, WinNT.HANDLE var2);

   boolean FreeLibrary(WinDef.HMODULE var1);

   boolean EnumResourceTypes(WinDef.HMODULE var1, WinBase.EnumResTypeProc var2, Pointer var3);

   boolean EnumResourceNames(WinDef.HMODULE var1, Pointer var2, WinBase.EnumResNameProc var3, Pointer var4);

   boolean Module32FirstW(WinNT.HANDLE var1, Tlhelp32.MODULEENTRY32W var2);

   boolean Module32NextW(WinNT.HANDLE var1, Tlhelp32.MODULEENTRY32W var2);

   int SetErrorMode(int var1);

   Pointer GetProcAddress(WinDef.HMODULE var1, int var2) throws LastErrorException;

   int SetThreadExecutionState(int var1);

   int ExpandEnvironmentStrings(String var1, Pointer var2, int var3);

   boolean GetProcessTimes(WinNT.HANDLE var1, WinBase.FILETIME var2, WinBase.FILETIME var3, WinBase.FILETIME var4, WinBase.FILETIME var5);

   boolean GetProcessIoCounters(WinNT.HANDLE var1, WinNT.IO_COUNTERS var2);

   WinNT.HANDLE CreateMutex(WinBase.SECURITY_ATTRIBUTES var1, boolean var2, String var3);

   WinNT.HANDLE OpenMutex(int var1, boolean var2, String var3);

   boolean ReleaseMutex(WinNT.HANDLE var1);

   void ExitProcess(int var1);

   Pointer VirtualAllocEx(WinNT.HANDLE var1, Pointer var2, BaseTSD.SIZE_T var3, int var4, int var5);

   boolean GetExitCodeThread(WinNT.HANDLE var1, IntByReference var2);

   boolean VirtualFreeEx(WinNT.HANDLE var1, Pointer var2, BaseTSD.SIZE_T var3, int var4);
}
