package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Kernel32Util implements WinDef {
   public static final String VOLUME_GUID_PATH_PREFIX = "\\\\?\\Volume{";
   public static final String VOLUME_GUID_PATH_SUFFIX = "}\\";

   public static String getComputerName() {
      char[] buffer = new char[WinBase.MAX_COMPUTERNAME_LENGTH + 1];
      IntByReference lpnSize = new IntByReference(buffer.length);
      if (!Kernel32.INSTANCE.GetComputerName(buffer, lpnSize)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return Native.toString(buffer);
      }
   }

   public static void freeLocalMemory(Pointer ptr) {
      Pointer res = Kernel32.INSTANCE.LocalFree(ptr);
      if (res != null) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      }
   }

   public static void freeGlobalMemory(Pointer ptr) {
      Pointer res = Kernel32.INSTANCE.GlobalFree(ptr);
      if (res != null) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      }
   }

   public static void closeHandleRefs(WinNT.HANDLEByReference... refs) {
      Win32Exception err = null;
      WinNT.HANDLEByReference[] var2 = refs;
      int var3 = refs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WinNT.HANDLEByReference r = var2[var4];

         try {
            closeHandleRef(r);
         } catch (Win32Exception var7) {
            if (err == null) {
               err = var7;
            } else {
               err.addSuppressedReflected(var7);
            }
         }
      }

      if (err != null) {
         throw err;
      }
   }

   public static void closeHandleRef(WinNT.HANDLEByReference ref) {
      closeHandle(ref == null ? null : ref.getValue());
   }

   public static void closeHandles(WinNT.HANDLE... handles) {
      Win32Exception err = null;
      WinNT.HANDLE[] var2 = handles;
      int var3 = handles.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WinNT.HANDLE h = var2[var4];

         try {
            closeHandle(h);
         } catch (Win32Exception var7) {
            if (err == null) {
               err = var7;
            } else {
               err.addSuppressedReflected(var7);
            }
         }
      }

      if (err != null) {
         throw err;
      }
   }

   public static void closeHandle(WinNT.HANDLE h) {
      if (h != null) {
         if (!Kernel32.INSTANCE.CloseHandle(h)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }
      }
   }

   public static String formatMessage(int code) {
      PointerByReference buffer = new PointerByReference();
      int nLen = Kernel32.INSTANCE.FormatMessage(4864, (Pointer)null, code, 0, buffer, 0, (Pointer)null);
      if (nLen == 0) {
         throw new LastErrorException(Native.getLastError());
      } else {
         Pointer ptr = buffer.getValue();

         String var5;
         try {
            String s = ptr.getWideString(0L);
            var5 = s.trim();
         } finally {
            freeLocalMemory(ptr);
         }

         return var5;
      }
   }

   public static String formatMessage(WinNT.HRESULT code) {
      return formatMessage(code.intValue());
   }

   public static String formatMessageFromLastErrorCode(int code) {
      return formatMessage(W32Errors.HRESULT_FROM_WIN32(code));
   }

   public static String getLastErrorMessage() {
      return formatMessageFromLastErrorCode(Kernel32.INSTANCE.GetLastError());
   }

   public static String getTempPath() {
      WinDef.DWORD nBufferLength = new WinDef.DWORD(260L);
      char[] buffer = new char[nBufferLength.intValue()];
      if (Kernel32.INSTANCE.GetTempPath(nBufferLength, buffer).intValue() == 0) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return Native.toString(buffer);
      }
   }

   public static void deleteFile(String filename) {
      if (!Kernel32.INSTANCE.DeleteFile(filename)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      }
   }

   public static List<String> getLogicalDriveStrings() {
      WinDef.DWORD dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(new WinDef.DWORD(0L), (char[])null);
      if (dwSize.intValue() <= 0) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         char[] buf = new char[dwSize.intValue()];
         dwSize = Kernel32.INSTANCE.GetLogicalDriveStrings(dwSize, buf);
         int bufSize = dwSize.intValue();
         if (bufSize <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            return Native.toStringList(buf, 0, bufSize);
         }
      }
   }

   public static int getFileAttributes(String fileName) {
      int fileAttributes = Kernel32.INSTANCE.GetFileAttributes(fileName);
      if (fileAttributes == -1) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return fileAttributes;
      }
   }

   public static int getFileType(String fileName) throws FileNotFoundException {
      File f = new File(fileName);
      if (!f.exists()) {
         throw new FileNotFoundException(fileName);
      } else {
         WinNT.HANDLE hFile = null;
         Win32Exception err = null;

         int var6;
         try {
            hFile = Kernel32.INSTANCE.CreateFile(fileName, Integer.MIN_VALUE, 1, new WinBase.SECURITY_ATTRIBUTES(), 3, 128, (new WinNT.HANDLEByReference()).getValue());
            if (WinBase.INVALID_HANDLE_VALUE.equals(hFile)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            int type = Kernel32.INSTANCE.GetFileType(hFile);
            switch (type) {
               case 0:
                  int rc = Kernel32.INSTANCE.GetLastError();
                  switch (rc) {
                     case 0:
                        break;
                     default:
                        throw new Win32Exception(rc);
                  }
               default:
                  var6 = type;
            }
         } catch (Win32Exception var15) {
            err = var15;
            throw var15;
         } finally {
            try {
               closeHandle(hFile);
            } catch (Win32Exception var14) {
               if (err == null) {
                  err = var14;
               } else {
                  err.addSuppressedReflected(var14);
               }
            }

            if (err != null) {
               throw err;
            }

         }

         return var6;
      }
   }

   public static int getDriveType(String rootName) {
      return Kernel32.INSTANCE.GetDriveType(rootName);
   }

   public static String getEnvironmentVariable(String name) {
      int size = Kernel32.INSTANCE.GetEnvironmentVariable(name, (char[])null, 0);
      if (size == 0) {
         return null;
      } else if (size < 0) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         char[] buffer = new char[size];
         size = Kernel32.INSTANCE.GetEnvironmentVariable(name, buffer, buffer.length);
         if (size <= 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            return Native.toString(buffer);
         }
      }
   }

   public static Map<String, String> getEnvironmentVariables() {
      Pointer lpszEnvironmentBlock = Kernel32.INSTANCE.GetEnvironmentStrings();
      if (lpszEnvironmentBlock == null) {
         throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
      } else {
         Map var1;
         try {
            var1 = getEnvironmentVariables(lpszEnvironmentBlock, 0L);
         } finally {
            if (!Kernel32.INSTANCE.FreeEnvironmentStrings(lpszEnvironmentBlock)) {
               throw new LastErrorException(Kernel32.INSTANCE.GetLastError());
            }

         }

         return var1;
      }
   }

   public static Map<String, String> getEnvironmentVariables(Pointer lpszEnvironmentBlock, long offset) {
      if (lpszEnvironmentBlock == null) {
         return null;
      } else {
         Map<String, String> vars = new TreeMap();
         boolean asWideChars = isWideCharEnvironmentStringBlock(lpszEnvironmentBlock, offset);
         long stepFactor = asWideChars ? 2L : 1L;
         long curOffset = offset;

         while(true) {
            String nvp = readEnvironmentStringBlockEntry(lpszEnvironmentBlock, curOffset, asWideChars);
            int len = nvp.length();
            if (len == 0) {
               return vars;
            }

            int pos = nvp.indexOf(61);
            if (pos < 0) {
               throw new IllegalArgumentException("Missing variable value separator in " + nvp);
            }

            String name = nvp.substring(0, pos);
            String value = nvp.substring(pos + 1);
            vars.put(name, value);
            curOffset += (long)(len + 1) * stepFactor;
         }
      }
   }

   public static String readEnvironmentStringBlockEntry(Pointer lpszEnvironmentBlock, long offset, boolean asWideChars) {
      long endOffset = findEnvironmentStringBlockEntryEnd(lpszEnvironmentBlock, offset, asWideChars);
      int dataLen = (int)(endOffset - offset);
      if (dataLen == 0) {
         return "";
      } else {
         int charsLen = asWideChars ? dataLen / 2 : dataLen;
         char[] chars = new char[charsLen];
         long curOffset = offset;
         long stepSize = asWideChars ? 2L : 1L;
         ByteOrder byteOrder = ByteOrder.nativeOrder();

         for(int index = 0; index < chars.length; curOffset += stepSize) {
            byte b = lpszEnvironmentBlock.getByte(curOffset);
            if (asWideChars) {
               byte x = lpszEnvironmentBlock.getByte(curOffset + 1L);
               if (ByteOrder.LITTLE_ENDIAN.equals(byteOrder)) {
                  chars[index] = (char)(x << 8 & '\uff00' | b & 255);
               } else {
                  chars[index] = (char)(b << 8 & '\uff00' | x & 255);
               }
            } else {
               chars[index] = (char)(b & 255);
            }

            ++index;
         }

         return new String(chars);
      }
   }

   public static long findEnvironmentStringBlockEntryEnd(Pointer lpszEnvironmentBlock, long offset, boolean asWideChars) {
      long curOffset = offset;
      long stepSize = asWideChars ? 2L : 1L;

      while(true) {
         byte b = lpszEnvironmentBlock.getByte(curOffset);
         if (b == 0) {
            return curOffset;
         }

         curOffset += stepSize;
      }
   }

   public static boolean isWideCharEnvironmentStringBlock(Pointer lpszEnvironmentBlock, long offset) {
      byte b0 = lpszEnvironmentBlock.getByte(offset);
      byte b1 = lpszEnvironmentBlock.getByte(offset + 1L);
      ByteOrder byteOrder = ByteOrder.nativeOrder();
      return ByteOrder.LITTLE_ENDIAN.equals(byteOrder) ? isWideCharEnvironmentStringBlock(b1) : isWideCharEnvironmentStringBlock(b0);
   }

   private static boolean isWideCharEnvironmentStringBlock(byte charsetIndicator) {
      return charsetIndicator == 0;
   }

   public static final int getPrivateProfileInt(String appName, String keyName, int defaultValue, String fileName) {
      return Kernel32.INSTANCE.GetPrivateProfileInt(appName, keyName, defaultValue, fileName);
   }

   public static final String getPrivateProfileString(String lpAppName, String lpKeyName, String lpDefault, String lpFileName) {
      char[] buffer = new char[1024];
      Kernel32.INSTANCE.GetPrivateProfileString(lpAppName, lpKeyName, lpDefault, buffer, new WinDef.DWORD((long)buffer.length), lpFileName);
      return Native.toString(buffer);
   }

   public static final void writePrivateProfileString(String appName, String keyName, String string, String fileName) {
      if (!Kernel32.INSTANCE.WritePrivateProfileString(appName, keyName, string, fileName)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      }
   }

   public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] getLogicalProcessorInformation() {
      int sizePerStruct = (new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION()).size();
      WinDef.DWORDByReference bufferSize = new WinDef.DWORDByReference(new WinDef.DWORD((long)sizePerStruct));

      int err;
      do {
         Memory memory = new Memory((long)bufferSize.getValue().intValue());
         if (Kernel32.INSTANCE.GetLogicalProcessorInformation(memory, bufferSize)) {
            WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION firstInformation = new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION(memory);
            int returnedStructCount = bufferSize.getValue().intValue() / sizePerStruct;
            return (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[])((WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[])firstInformation.toArray(new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[returnedStructCount]));
         }

         err = Kernel32.INSTANCE.GetLastError();
      } while(err == 122);

      throw new Win32Exception(err);
   }

   public static final WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] getLogicalProcessorInformationEx(int relationshipType) {
      WinDef.DWORDByReference bufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(1L));

      int err;
      do {
         Memory memory = new Memory((long)bufferSize.getValue().intValue());
         if (Kernel32.INSTANCE.GetLogicalProcessorInformationEx(relationshipType, memory, bufferSize)) {
            List<WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX> procInfoList = new ArrayList();

            WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX information;
            for(int offset = 0; offset < bufferSize.getValue().intValue(); offset += information.size) {
               information = WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX.fromPointer(memory.share((long)offset));
               procInfoList.add(information);
            }

            return (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[])procInfoList.toArray(new WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[0]);
         }

         err = Kernel32.INSTANCE.GetLastError();
      } while(err == 122);

      throw new Win32Exception(err);
   }

   public static final String[] getPrivateProfileSection(String appName, String fileName) {
      char[] buffer = new char['耀'];
      if (Kernel32.INSTANCE.GetPrivateProfileSection(appName, buffer, new WinDef.DWORD((long)buffer.length), fileName).intValue() == 0) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return (new String(buffer)).split("\u0000");
      }
   }

   public static final String[] getPrivateProfileSectionNames(String fileName) {
      char[] buffer = new char[65536];
      if (Kernel32.INSTANCE.GetPrivateProfileSectionNames(buffer, new WinDef.DWORD((long)buffer.length), fileName).intValue() == 0) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return (new String(buffer)).split("\u0000");
      }
   }

   public static final void writePrivateProfileSection(String appName, String[] strings, String fileName) {
      StringBuilder buffer = new StringBuilder();
      String[] var4 = strings;
      int var5 = strings.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String string = var4[var6];
         buffer.append(string).append('\u0000');
      }

      buffer.append('\u0000');
      if (!Kernel32.INSTANCE.WritePrivateProfileSection(appName, buffer.toString(), fileName)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      }
   }

   public static final List<String> queryDosDevice(String lpszDeviceName, int maxTargetSize) {
      char[] lpTargetPath = new char[maxTargetSize];
      int dwSize = Kernel32.INSTANCE.QueryDosDevice(lpszDeviceName, lpTargetPath, lpTargetPath.length);
      if (dwSize == 0) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return Native.toStringList(lpTargetPath, 0, dwSize);
      }
   }

   public static final List<String> getVolumePathNamesForVolumeName(String lpszVolumeName) {
      char[] lpszVolumePathNames = new char[261];
      IntByReference lpcchReturnLength = new IntByReference();
      int hr;
      if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(lpszVolumeName, lpszVolumePathNames, lpszVolumePathNames.length, lpcchReturnLength)) {
         hr = Kernel32.INSTANCE.GetLastError();
         if (hr != 234) {
            throw new Win32Exception(hr);
         }

         int required = lpcchReturnLength.getValue();
         lpszVolumePathNames = new char[required];
         if (!Kernel32.INSTANCE.GetVolumePathNamesForVolumeName(lpszVolumeName, lpszVolumePathNames, lpszVolumePathNames.length, lpcchReturnLength)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }
      }

      hr = lpcchReturnLength.getValue();
      return Native.toStringList(lpszVolumePathNames, 0, hr);
   }

   public static final String extractVolumeGUID(String volumeGUIDPath) {
      if (volumeGUIDPath != null && volumeGUIDPath.length() > "\\\\?\\Volume{".length() + "}\\".length() && volumeGUIDPath.startsWith("\\\\?\\Volume{") && volumeGUIDPath.endsWith("}\\")) {
         return volumeGUIDPath.substring("\\\\?\\Volume{".length(), volumeGUIDPath.length() - "}\\".length());
      } else {
         throw new IllegalArgumentException("Bad volume GUID path format: " + volumeGUIDPath);
      }
   }

   public static final String QueryFullProcessImageName(int pid, int dwFlags) {
      WinNT.HANDLE hProcess = null;
      Win32Exception we = null;

      String var4;
      try {
         hProcess = Kernel32.INSTANCE.OpenProcess(1040, false, pid);
         if (hProcess == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

         var4 = QueryFullProcessImageName(hProcess, dwFlags);
      } catch (Win32Exception var13) {
         we = var13;
         throw var13;
      } finally {
         try {
            closeHandle(hProcess);
         } catch (Win32Exception var12) {
            if (we == null) {
               we = var12;
            } else {
               we.addSuppressed(var12);
            }
         }

         if (we != null) {
            throw we;
         }

      }

      return var4;
   }

   public static final String QueryFullProcessImageName(WinNT.HANDLE hProcess, int dwFlags) {
      int size = 260;
      IntByReference lpdwSize = new IntByReference();

      do {
         char[] lpExeName = new char[size];
         lpdwSize.setValue(size);
         if (Kernel32.INSTANCE.QueryFullProcessImageName(hProcess, dwFlags, lpExeName, lpdwSize)) {
            return new String(lpExeName, 0, lpdwSize.getValue());
         }

         size += 1024;
      } while(Kernel32.INSTANCE.GetLastError() == 122);

      throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
   }

   public static byte[] getResource(String path, String type, String name) {
      WinDef.HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, (WinNT.HANDLE)null, 2);
      if (target == null) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         Win32Exception err = null;
         Pointer start = null;
         int length = false;
         byte[] results = null;
         boolean var18 = false;

         label179: {
            Win32Exception we;
            label178: {
               try {
                  var18 = true;
                  Pointer t = null;

                  try {
                     t = new Pointer(Long.parseLong(type));
                  } catch (NumberFormatException var20) {
                     t = new Memory((long)(Native.WCHAR_SIZE * (type.length() + 1)));
                     ((Pointer)t).setWideString(0L, type);
                  }

                  Pointer n = null;

                  try {
                     n = new Pointer(Long.parseLong(name));
                  } catch (NumberFormatException var19) {
                     n = new Memory((long)(Native.WCHAR_SIZE * (name.length() + 1)));
                     ((Pointer)n).setWideString(0L, name);
                  }

                  WinDef.HRSRC hrsrc = Kernel32.INSTANCE.FindResource(target, (Pointer)n, (Pointer)t);
                  if (hrsrc == null) {
                     throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                  }

                  WinNT.HANDLE loaded = Kernel32.INSTANCE.LoadResource(target, hrsrc);
                  if (loaded == null) {
                     throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                  }

                  int length = Kernel32.INSTANCE.SizeofResource(target, hrsrc);
                  if (length == 0) {
                     throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                  }

                  start = Kernel32.INSTANCE.LockResource(loaded);
                  if (start == null) {
                     throw new IllegalStateException("LockResource returned null.");
                  }

                  results = start.getByteArray(0L, length);
                  var18 = false;
               } catch (Win32Exception var21) {
                  err = var21;
                  var18 = false;
                  break label178;
               } finally {
                  if (var18) {
                     if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                        Win32Exception we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        if (err != null) {
                           we.addSuppressedReflected(err);
                        }

                        throw we;
                     }

                  }
               }

               if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                  we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                  if (err != null) {
                     we.addSuppressedReflected(err);
                  }

                  throw we;
               }
               break label179;
            }

            if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
               we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
               if (err != null) {
                  we.addSuppressedReflected(err);
               }

               throw we;
            }
         }

         if (err != null) {
            throw err;
         } else {
            return results;
         }
      }
   }

   public static Map<String, List<String>> getResourceNames(String path) {
      WinDef.HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, (WinNT.HANDLE)null, 2);
      if (target == null) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         final List<String> types = new ArrayList();
         final Map<String, List<String>> result = new LinkedHashMap();
         WinBase.EnumResTypeProc ertp = new WinBase.EnumResTypeProc() {
            public boolean invoke(WinDef.HMODULE module, Pointer type, Pointer lParam) {
               if (Pointer.nativeValue(type) <= 65535L) {
                  types.add(Pointer.nativeValue(type) + "");
               } else {
                  types.add(type.getWideString(0L));
               }

               return true;
            }
         };
         WinBase.EnumResNameProc ernp = new WinBase.EnumResNameProc() {
            public boolean invoke(WinDef.HMODULE module, Pointer type, Pointer name, Pointer lParam) {
               String typeName = "";
               if (Pointer.nativeValue(type) <= 65535L) {
                  typeName = Pointer.nativeValue(type) + "";
               } else {
                  typeName = type.getWideString(0L);
               }

               if (Pointer.nativeValue(name) < 65535L) {
                  ((List)result.get(typeName)).add(Pointer.nativeValue(name) + "");
               } else {
                  ((List)result.get(typeName)).add(name.getWideString(0L));
               }

               return true;
            }
         };
         Win32Exception err = null;
         boolean var16 = false;

         label156: {
            Win32Exception we;
            label155: {
               try {
                  var16 = true;
                  if (!Kernel32.INSTANCE.EnumResourceTypes(target, ertp, (Pointer)null)) {
                     throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                  }

                  Iterator var7 = types.iterator();

                  boolean callResult;
                  do {
                     if (!var7.hasNext()) {
                        var16 = false;
                        break label155;
                     }

                     String typeName = (String)var7.next();
                     result.put(typeName, new ArrayList());
                     Pointer pointer = null;

                     try {
                        pointer = new Pointer(Long.parseLong(typeName));
                     } catch (NumberFormatException var17) {
                        pointer = new Memory((long)(Native.WCHAR_SIZE * (typeName.length() + 1)));
                        ((Pointer)pointer).setWideString(0L, typeName);
                     }

                     callResult = Kernel32.INSTANCE.EnumResourceNames(target, (Pointer)pointer, ernp, (Pointer)null);
                  } while(callResult);

                  throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
               } catch (Win32Exception var18) {
                  err = var18;
                  var16 = false;
               } finally {
                  if (var16) {
                     if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                        Win32Exception we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        if (err != null) {
                           we.addSuppressedReflected(err);
                        }

                        throw we;
                     }

                  }
               }

               if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
                  we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
                  if (err != null) {
                     we.addSuppressedReflected(err);
                  }

                  throw we;
               }
               break label156;
            }

            if (target != null && !Kernel32.INSTANCE.FreeLibrary(target)) {
               we = new Win32Exception(Kernel32.INSTANCE.GetLastError());
               if (err != null) {
                  we.addSuppressedReflected(err);
               }

               throw we;
            }
         }

         if (err != null) {
            throw err;
         } else {
            return result;
         }
      }
   }

   public static List<Tlhelp32.MODULEENTRY32W> getModules(int processID) {
      WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new WinDef.DWORD((long)processID));
      if (snapshot == null) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         Win32Exception we = null;

         ArrayList var7;
         try {
            Tlhelp32.MODULEENTRY32W first = new Tlhelp32.MODULEENTRY32W();
            if (!Kernel32.INSTANCE.Module32FirstW(snapshot, first)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            List<Tlhelp32.MODULEENTRY32W> modules = new ArrayList();
            modules.add(first);

            for(Tlhelp32.MODULEENTRY32W next = new Tlhelp32.MODULEENTRY32W(); Kernel32.INSTANCE.Module32NextW(snapshot, next); next = new Tlhelp32.MODULEENTRY32W()) {
               modules.add(next);
            }

            int lastError = Kernel32.INSTANCE.GetLastError();
            if (lastError != 0 && lastError != 18) {
               throw new Win32Exception(lastError);
            }

            var7 = modules;
         } catch (Win32Exception var16) {
            we = var16;
            throw var16;
         } finally {
            try {
               closeHandle(snapshot);
            } catch (Win32Exception var15) {
               if (we == null) {
                  we = var15;
               } else {
                  we.addSuppressedReflected(var15);
               }
            }

            if (we != null) {
               throw we;
            }

         }

         return var7;
      }
   }

   public static String expandEnvironmentStrings(String input) {
      if (input == null) {
         return "";
      } else {
         int resultChars = Kernel32.INSTANCE.ExpandEnvironmentStrings(input, (Pointer)null, 0);
         if (resultChars == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            Memory resultMemory;
            if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
               resultMemory = new Memory((long)(resultChars * Native.WCHAR_SIZE));
            } else {
               resultMemory = new Memory((long)(resultChars + 1));
            }

            resultChars = Kernel32.INSTANCE.ExpandEnvironmentStrings(input, resultMemory, resultChars);
            if (resultChars == 0) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               return W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? resultMemory.getWideString(0L) : resultMemory.getString(0L);
            }
         }
      }
   }
}
