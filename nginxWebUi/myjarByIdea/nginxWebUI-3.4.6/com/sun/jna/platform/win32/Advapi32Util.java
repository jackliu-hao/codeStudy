package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Advapi32Util {
   public static String getUserName() {
      char[] buffer = new char[128];
      IntByReference len = new IntByReference(buffer.length);
      boolean result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
      if (!result) {
         switch (Kernel32.INSTANCE.GetLastError()) {
            case 122:
               buffer = new char[len.getValue()];
               result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
               break;
            default:
               throw new Win32Exception(Native.getLastError());
         }
      }

      if (!result) {
         throw new Win32Exception(Native.getLastError());
      } else {
         return Native.toString(buffer);
      }
   }

   public static Account getAccountByName(String accountName) {
      return getAccountByName((String)null, accountName);
   }

   public static Account getAccountByName(String systemName, String accountName) {
      IntByReference pSid = new IntByReference(0);
      IntByReference cchDomainName = new IntByReference(0);
      PointerByReference peUse = new PointerByReference();
      if (Advapi32.INSTANCE.LookupAccountName(systemName, accountName, (WinNT.PSID)null, pSid, (char[])null, cchDomainName, peUse)) {
         throw new RuntimeException("LookupAccountNameW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
      } else {
         int rc = Kernel32.INSTANCE.GetLastError();
         if (pSid.getValue() != 0 && rc == 122) {
            Memory sidMemory = new Memory((long)pSid.getValue());
            WinNT.PSID result = new WinNT.PSID(sidMemory);
            char[] referencedDomainName = new char[cchDomainName.getValue() + 1];
            if (!Advapi32.INSTANCE.LookupAccountName(systemName, accountName, result, pSid, referencedDomainName, cchDomainName, peUse)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               Account account = new Account();
               account.accountType = peUse.getPointer().getInt(0L);
               String[] accountNamePartsBs = accountName.split("\\\\", 2);
               String[] accountNamePartsAt = accountName.split("@", 2);
               if (accountNamePartsBs.length == 2) {
                  account.name = accountNamePartsBs[1];
               } else if (accountNamePartsAt.length == 2) {
                  account.name = accountNamePartsAt[0];
               } else {
                  account.name = accountName;
               }

               if (cchDomainName.getValue() > 0) {
                  account.domain = Native.toString(referencedDomainName);
                  account.fqn = account.domain + "\\" + account.name;
               } else {
                  account.fqn = account.name;
               }

               account.sid = result.getBytes();
               account.sidString = convertSidToStringSid(new WinNT.PSID(account.sid));
               return account;
            }
         } else {
            throw new Win32Exception(rc);
         }
      }
   }

   public static Account getAccountBySid(WinNT.PSID sid) {
      return getAccountBySid((String)null, (WinNT.PSID)sid);
   }

   public static Account getAccountBySid(String systemName, WinNT.PSID sid) {
      IntByReference cchName = new IntByReference();
      IntByReference cchDomainName = new IntByReference();
      PointerByReference peUse = new PointerByReference();
      if (Advapi32.INSTANCE.LookupAccountSid(systemName, sid, (char[])null, cchName, (char[])null, cchDomainName, peUse)) {
         throw new RuntimeException("LookupAccountSidW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
      } else {
         int rc = Kernel32.INSTANCE.GetLastError();
         if (cchName.getValue() != 0 && rc == 122) {
            char[] domainName = new char[cchDomainName.getValue()];
            char[] name = new char[cchName.getValue()];
            if (!Advapi32.INSTANCE.LookupAccountSid(systemName, sid, name, cchName, domainName, cchDomainName, peUse)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               Account account = new Account();
               account.accountType = peUse.getPointer().getInt(0L);
               account.name = Native.toString(name);
               if (cchDomainName.getValue() > 0) {
                  account.domain = Native.toString(domainName);
                  account.fqn = account.domain + "\\" + account.name;
               } else {
                  account.fqn = account.name;
               }

               account.sid = sid.getBytes();
               account.sidString = convertSidToStringSid(sid);
               return account;
            }
         } else {
            throw new Win32Exception(rc);
         }
      }
   }

   public static String convertSidToStringSid(WinNT.PSID sid) {
      PointerByReference stringSid = new PointerByReference();
      if (!Advapi32.INSTANCE.ConvertSidToStringSid(sid, stringSid)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         Pointer ptr = stringSid.getValue();

         String var3;
         try {
            var3 = ptr.getWideString(0L);
         } finally {
            Kernel32Util.freeLocalMemory(ptr);
         }

         return var3;
      }
   }

   public static byte[] convertStringSidToSid(String sidString) {
      WinNT.PSIDByReference pSID = new WinNT.PSIDByReference();
      if (!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         WinNT.PSID value = pSID.getValue();

         byte[] var3;
         try {
            var3 = value.getBytes();
         } finally {
            Kernel32Util.freeLocalMemory(value.getPointer());
         }

         return var3;
      }
   }

   public static boolean isWellKnownSid(String sidString, int wellKnownSidType) {
      WinNT.PSIDByReference pSID = new WinNT.PSIDByReference();
      if (!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         WinNT.PSID value = pSID.getValue();

         boolean var4;
         try {
            var4 = Advapi32.INSTANCE.IsWellKnownSid(value, wellKnownSidType);
         } finally {
            Kernel32Util.freeLocalMemory(value.getPointer());
         }

         return var4;
      }
   }

   public static boolean isWellKnownSid(byte[] sidBytes, int wellKnownSidType) {
      WinNT.PSID pSID = new WinNT.PSID(sidBytes);
      return Advapi32.INSTANCE.IsWellKnownSid(pSID, wellKnownSidType);
   }

   public static int alignOnDWORD(int cbAcl) {
      return cbAcl + 3 & -4;
   }

   public static int getAceSize(int sidLength) {
      return Native.getNativeSize(WinNT.ACCESS_ALLOWED_ACE.class, (Object)null) + sidLength - 4;
   }

   public static Account getAccountBySid(String sidString) {
      return getAccountBySid((String)null, (String)sidString);
   }

   public static Account getAccountBySid(String systemName, String sidString) {
      return getAccountBySid(systemName, new WinNT.PSID(convertStringSidToSid(sidString)));
   }

   public static Account[] getTokenGroups(WinNT.HANDLE hToken) {
      IntByReference tokenInformationLength = new IntByReference();
      if (Advapi32.INSTANCE.GetTokenInformation(hToken, 2, (Structure)null, 0, tokenInformationLength)) {
         throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
      } else {
         int rc = Kernel32.INSTANCE.GetLastError();
         if (rc != 122) {
            throw new Win32Exception(rc);
         } else {
            WinNT.TOKEN_GROUPS groups = new WinNT.TOKEN_GROUPS(tokenInformationLength.getValue());
            if (!Advapi32.INSTANCE.GetTokenInformation(hToken, 2, groups, tokenInformationLength.getValue(), tokenInformationLength)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               ArrayList<Account> userGroups = new ArrayList();
               WinNT.SID_AND_ATTRIBUTES[] var5 = groups.getGroups();
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  WinNT.SID_AND_ATTRIBUTES sidAndAttribute = var5[var7];

                  Account group;
                  try {
                     group = getAccountBySid((WinNT.PSID)sidAndAttribute.Sid);
                  } catch (Exception var11) {
                     group = new Account();
                     group.sid = sidAndAttribute.Sid.getBytes();
                     group.sidString = convertSidToStringSid(sidAndAttribute.Sid);
                     group.name = group.sidString;
                     group.fqn = group.sidString;
                     group.accountType = 2;
                  }

                  userGroups.add(group);
               }

               return (Account[])userGroups.toArray(new Account[0]);
            }
         }
      }
   }

   public static Account getTokenPrimaryGroup(WinNT.HANDLE hToken) {
      IntByReference tokenInformationLength = new IntByReference();
      if (Advapi32.INSTANCE.GetTokenInformation(hToken, 5, (Structure)null, 0, tokenInformationLength)) {
         throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
      } else {
         int rc = Kernel32.INSTANCE.GetLastError();
         if (rc != 122) {
            throw new Win32Exception(rc);
         } else {
            WinNT.TOKEN_PRIMARY_GROUP primaryGroup = new WinNT.TOKEN_PRIMARY_GROUP(tokenInformationLength.getValue());
            if (!Advapi32.INSTANCE.GetTokenInformation(hToken, 5, primaryGroup, tokenInformationLength.getValue(), tokenInformationLength)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               Account group;
               try {
                  group = getAccountBySid((WinNT.PSID)primaryGroup.PrimaryGroup);
               } catch (Exception var6) {
                  group = new Account();
                  group.sid = primaryGroup.PrimaryGroup.getBytes();
                  group.sidString = convertSidToStringSid(primaryGroup.PrimaryGroup);
                  group.name = group.sidString;
                  group.fqn = group.sidString;
                  group.accountType = 2;
               }

               return group;
            }
         }
      }
   }

   public static Account getTokenAccount(WinNT.HANDLE hToken) {
      IntByReference tokenInformationLength = new IntByReference();
      if (Advapi32.INSTANCE.GetTokenInformation(hToken, 1, (Structure)null, 0, tokenInformationLength)) {
         throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
      } else {
         int rc = Kernel32.INSTANCE.GetLastError();
         if (rc != 122) {
            throw new Win32Exception(rc);
         } else {
            WinNT.TOKEN_USER user = new WinNT.TOKEN_USER(tokenInformationLength.getValue());
            if (!Advapi32.INSTANCE.GetTokenInformation(hToken, 1, user, tokenInformationLength.getValue(), tokenInformationLength)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               return getAccountBySid((WinNT.PSID)user.User.Sid);
            }
         }
      }
   }

   public static Account[] getCurrentUserGroups() {
      WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
      Win32Exception err = null;
      boolean var13 = false;

      Account[] var18;
      WinNT.HANDLE hToken;
      try {
         var13 = true;
         WinNT.HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
         if (!Advapi32.INSTANCE.OpenThreadToken(threadHandle, 10, true, phToken)) {
            int rc = Kernel32.INSTANCE.GetLastError();
            if (rc != 1008) {
               throw new Win32Exception(rc);
            }

            hToken = Kernel32.INSTANCE.GetCurrentProcess();
            if (!Advapi32.INSTANCE.OpenProcessToken(hToken, 10, phToken)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
         }

         var18 = getTokenGroups(phToken.getValue());
         var13 = false;
      } catch (Win32Exception var16) {
         err = var16;
         throw var16;
      } finally {
         if (var13) {
            WinNT.HANDLE hToken = phToken.getValue();
            if (!WinBase.INVALID_HANDLE_VALUE.equals(hToken)) {
               try {
                  Kernel32Util.closeHandle(hToken);
               } catch (Win32Exception var14) {
                  if (err == null) {
                     err = var14;
                  } else {
                     err.addSuppressedReflected(var14);
                  }
               }
            }

            if (err != null) {
               throw err;
            }

         }
      }

      hToken = phToken.getValue();
      if (!WinBase.INVALID_HANDLE_VALUE.equals(hToken)) {
         try {
            Kernel32Util.closeHandle(hToken);
         } catch (Win32Exception var15) {
            if (err == null) {
               err = var15;
            } else {
               err.addSuppressedReflected(var15);
            }
         }
      }

      if (err != null) {
         throw err;
      } else {
         return var18;
      }
   }

   public static boolean registryKeyExists(WinReg.HKEY root, String key) {
      return registryKeyExists(root, key, 0);
   }

   public static boolean registryKeyExists(WinReg.HKEY root, String key, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
      switch (rc) {
         case 0:
            Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            return true;
         case 2:
            return false;
         default:
            throw new Win32Exception(rc);
      }
   }

   public static boolean registryValueExists(WinReg.HKEY root, String key, String value) {
      return registryValueExists(root, key, value, 0);
   }

   public static boolean registryValueExists(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
      switch (rc) {
         case 0:
            try {
               IntByReference lpcbData = new IntByReference();
               IntByReference lpType = new IntByReference();
               rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (Pointer)((Pointer)null), lpcbData);
               boolean var8;
               switch (rc) {
                  case 0:
                  case 122:
                  case 234:
                     var8 = true;
                     return var8;
                  case 2:
                     var8 = false;
                     return var8;
                  default:
                     throw new Win32Exception(rc);
               }
            } finally {
               if (phkKey.getValue() != WinBase.INVALID_HANDLE_VALUE) {
                  rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
                  if (rc != 0) {
                     throw new Win32Exception(rc);
                  }
               }

            }
         case 2:
            return false;
         default:
            throw new Win32Exception(rc);
      }
   }

   public static String registryGetStringValue(WinReg.HKEY root, String key, String value) {
      return registryGetStringValue(root, key, value, 0);
   }

   public static String registryGetStringValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         String var6;
         try {
            var6 = registryGetStringValue(phkKey.getValue(), value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var6;
      }
   }

   public static String registryGetStringValue(WinReg.HKEY hKey, String value) {
      IntByReference lpcbData = new IntByReference();
      IntByReference lpType = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)((Pointer)null), lpcbData);
      if (rc != 0 && rc != 122) {
         throw new Win32Exception(rc);
      } else if (lpType.getValue() != 1 && lpType.getValue() != 2) {
         throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ or REG_EXPAND_SZ");
      } else if (lpcbData.getValue() == 0) {
         return "";
      } else {
         Memory mem = new Memory((long)(lpcbData.getValue() + Native.WCHAR_SIZE));
         mem.clear();
         rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)mem, lpcbData);
         if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
         } else {
            return W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? mem.getWideString(0L) : mem.getString(0L);
         }
      }
   }

   public static String registryGetExpandableStringValue(WinReg.HKEY root, String key, String value) {
      return registryGetExpandableStringValue(root, key, value, 0);
   }

   public static String registryGetExpandableStringValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         String var6;
         try {
            var6 = registryGetExpandableStringValue(phkKey.getValue(), value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var6;
      }
   }

   public static String registryGetExpandableStringValue(WinReg.HKEY hKey, String value) {
      IntByReference lpcbData = new IntByReference();
      IntByReference lpType = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[])((char[])null), lpcbData);
      if (rc != 0 && rc != 122) {
         throw new Win32Exception(rc);
      } else if (lpType.getValue() != 2) {
         throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ");
      } else if (lpcbData.getValue() == 0) {
         return "";
      } else {
         Memory mem = new Memory((long)(lpcbData.getValue() + Native.WCHAR_SIZE));
         mem.clear();
         rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)mem, lpcbData);
         if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
         } else {
            return W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? mem.getWideString(0L) : mem.getString(0L);
         }
      }
   }

   public static String[] registryGetStringArray(WinReg.HKEY root, String key, String value) {
      return registryGetStringArray(root, key, value, 0);
   }

   public static String[] registryGetStringArray(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         String[] var6;
         try {
            var6 = registryGetStringArray(phkKey.getValue(), value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var6;
      }
   }

   public static String[] registryGetStringArray(WinReg.HKEY hKey, String value) {
      IntByReference lpcbData = new IntByReference();
      IntByReference lpType = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[])((char[])null), lpcbData);
      if (rc != 0 && rc != 122) {
         throw new Win32Exception(rc);
      } else if (lpType.getValue() != 7) {
         throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_SZ");
      } else {
         Memory data = new Memory((long)(lpcbData.getValue() + 2 * Native.WCHAR_SIZE));
         data.clear();
         rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)data, lpcbData);
         if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
         } else {
            ArrayList<String> result = new ArrayList();
            int offset = 0;

            while((long)offset < data.size()) {
               String s;
               if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                  s = data.getWideString((long)offset);
                  offset += s.length() * Native.WCHAR_SIZE;
                  offset += Native.WCHAR_SIZE;
               } else {
                  s = data.getString((long)offset);
                  offset += s.length();
                  ++offset;
               }

               if (s.length() == 0) {
                  break;
               }

               result.add(s);
            }

            return (String[])result.toArray(new String[0]);
         }
      }
   }

   public static byte[] registryGetBinaryValue(WinReg.HKEY root, String key, String value) {
      return registryGetBinaryValue(root, key, value, 0);
   }

   public static byte[] registryGetBinaryValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         byte[] var6;
         try {
            var6 = registryGetBinaryValue(phkKey.getValue(), value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var6;
      }
   }

   public static byte[] registryGetBinaryValue(WinReg.HKEY hKey, String value) {
      IntByReference lpcbData = new IntByReference();
      IntByReference lpType = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)((Pointer)null), lpcbData);
      if (rc != 0 && rc != 122) {
         throw new Win32Exception(rc);
      } else if (lpType.getValue() != 3) {
         throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_BINARY");
      } else {
         byte[] data = new byte[lpcbData.getValue()];
         rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (byte[])data, lpcbData);
         if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
         } else {
            return data;
         }
      }
   }

   public static int registryGetIntValue(WinReg.HKEY root, String key, String value) {
      return registryGetIntValue(root, key, value, 0);
   }

   public static int registryGetIntValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         int var6;
         try {
            var6 = registryGetIntValue(phkKey.getValue(), value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var6;
      }
   }

   public static int registryGetIntValue(WinReg.HKEY hKey, String value) {
      IntByReference lpcbData = new IntByReference();
      IntByReference lpType = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[])((char[])null), lpcbData);
      if (rc != 0 && rc != 122) {
         throw new Win32Exception(rc);
      } else if (lpType.getValue() != 4) {
         throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_DWORD");
      } else {
         IntByReference data = new IntByReference();
         rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (IntByReference)data, lpcbData);
         if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
         } else {
            return data.getValue();
         }
      }
   }

   public static long registryGetLongValue(WinReg.HKEY root, String key, String value) {
      return registryGetLongValue(root, key, value, 0);
   }

   public static long registryGetLongValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 131097 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         long var6;
         try {
            var6 = registryGetLongValue(phkKey.getValue(), value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var6;
      }
   }

   public static long registryGetLongValue(WinReg.HKEY hKey, String value) {
      IntByReference lpcbData = new IntByReference();
      IntByReference lpType = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[])((char[])null), lpcbData);
      if (rc != 0 && rc != 122) {
         throw new Win32Exception(rc);
      } else if (lpType.getValue() != 11) {
         throw new RuntimeException("Unexpected registry type " + lpType.getValue() + ", expected REG_QWORD");
      } else {
         LongByReference data = new LongByReference();
         rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (LongByReference)data, lpcbData);
         if (rc != 0 && rc != 122) {
            throw new Win32Exception(rc);
         } else {
            return data.getValue();
         }
      }
   }

   public static Object registryGetValue(WinReg.HKEY hkKey, String subKey, String lpValueName) {
      Object result = null;
      IntByReference lpType = new IntByReference();
      IntByReference lpcbData = new IntByReference();
      int rc = Advapi32.INSTANCE.RegGetValue(hkKey, subKey, lpValueName, 65535, lpType, (Pointer)((Pointer)null), lpcbData);
      if (lpType.getValue() == 0) {
         return null;
      } else if (rc != 0 && rc != 122) {
         throw new Win32Exception(rc);
      } else {
         Memory byteData = new Memory((long)(lpcbData.getValue() + Native.WCHAR_SIZE));
         byteData.clear();
         rc = Advapi32.INSTANCE.RegGetValue(hkKey, subKey, lpValueName, 65535, lpType, (Pointer)byteData, lpcbData);
         if (rc != 0) {
            throw new Win32Exception(rc);
         } else {
            if (lpType.getValue() == 4) {
               result = byteData.getInt(0L);
            } else if (lpType.getValue() == 11) {
               result = byteData.getLong(0L);
            } else if (lpType.getValue() == 3) {
               result = byteData.getByteArray(0L, lpcbData.getValue());
            } else if (lpType.getValue() == 1 || lpType.getValue() == 2) {
               if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                  result = byteData.getWideString(0L);
               } else {
                  result = byteData.getString(0L);
               }
            }

            return result;
         }
      }
   }

   public static boolean registryCreateKey(WinReg.HKEY hKey, String keyName) {
      return registryCreateKey(hKey, keyName, 0);
   }

   public static boolean registryCreateKey(WinReg.HKEY hKey, String keyName, int samDesiredExtra) {
      WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();
      IntByReference lpdwDisposition = new IntByReference();
      int rc = Advapi32.INSTANCE.RegCreateKeyEx(hKey, keyName, 0, (String)null, 0, 131097 | samDesiredExtra, (WinBase.SECURITY_ATTRIBUTES)null, phkResult, lpdwDisposition);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         rc = Advapi32.INSTANCE.RegCloseKey(phkResult.getValue());
         if (rc != 0) {
            throw new Win32Exception(rc);
         } else {
            return 1 == lpdwDisposition.getValue();
         }
      }
   }

   public static boolean registryCreateKey(WinReg.HKEY root, String parentPath, String keyName) {
      return registryCreateKey(root, parentPath, keyName, 0);
   }

   public static boolean registryCreateKey(WinReg.HKEY root, String parentPath, String keyName, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, parentPath, 0, 4 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         boolean var6;
         try {
            var6 = registryCreateKey(phkKey.getValue(), keyName);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var6;
      }
   }

   public static void registrySetIntValue(WinReg.HKEY hKey, String name, int value) {
      byte[] data = new byte[]{(byte)(value & 255), (byte)(value >> 8 & 255), (byte)(value >> 16 & 255), (byte)(value >> 24 & 255)};
      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 4, (byte[])data, 4);
      if (rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetIntValue(WinReg.HKEY root, String keyPath, String name, int value) {
      registrySetIntValue(root, keyPath, name, value, 0);
   }

   public static void registrySetIntValue(WinReg.HKEY root, String keyPath, String name, int value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetIntValue(phkKey.getValue(), name, value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetLongValue(WinReg.HKEY hKey, String name, long value) {
      byte[] data = new byte[]{(byte)((int)(value & 255L)), (byte)((int)(value >> 8 & 255L)), (byte)((int)(value >> 16 & 255L)), (byte)((int)(value >> 24 & 255L)), (byte)((int)(value >> 32 & 255L)), (byte)((int)(value >> 40 & 255L)), (byte)((int)(value >> 48 & 255L)), (byte)((int)(value >> 56 & 255L))};
      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 11, (byte[])data, 8);
      if (rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetLongValue(WinReg.HKEY root, String keyPath, String name, long value) {
      registrySetLongValue(root, keyPath, name, value, 0);
   }

   public static void registrySetLongValue(WinReg.HKEY root, String keyPath, String name, long value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetLongValue(phkKey.getValue(), name, value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetStringValue(WinReg.HKEY hKey, String name, String value) {
      if (value == null) {
         value = "";
      }

      Memory data;
      if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
         data = new Memory((long)((value.length() + 1) * Native.WCHAR_SIZE));
         data.setWideString(0L, value);
      } else {
         data = new Memory((long)(value.length() + 1));
         data.setString(0L, value);
      }

      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 1, (Pointer)data, (int)data.size());
      if (rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetStringValue(WinReg.HKEY root, String keyPath, String name, String value) {
      registrySetStringValue(root, keyPath, name, value, 0);
   }

   public static void registrySetStringValue(WinReg.HKEY root, String keyPath, String name, String value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetStringValue(phkKey.getValue(), name, value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetExpandableStringValue(WinReg.HKEY hKey, String name, String value) {
      Memory data;
      if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
         data = new Memory((long)((value.length() + 1) * Native.WCHAR_SIZE));
         data.setWideString(0L, value);
      } else {
         data = new Memory((long)(value.length() + 1));
         data.setString(0L, value);
      }

      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 2, (Pointer)data, (int)data.size());
      if (rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetExpandableStringValue(WinReg.HKEY root, String keyPath, String name, String value) {
      registrySetExpandableStringValue(root, keyPath, name, value, 0);
   }

   public static void registrySetExpandableStringValue(WinReg.HKEY root, String keyPath, String name, String value, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetExpandableStringValue(phkKey.getValue(), name, value);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetStringArray(WinReg.HKEY hKey, String name, String[] arr) {
      int charwidth = W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE ? Native.WCHAR_SIZE : 1;
      int size = 0;
      String[] var5 = arr;
      int var6 = arr.length;

      int rc;
      for(rc = 0; rc < var6; ++rc) {
         String s = var5[rc];
         size += s.length() * charwidth;
         size += charwidth;
      }

      size += charwidth;
      int offset = 0;
      Memory data = new Memory((long)size);
      data.clear();
      String[] var13 = arr;
      int var14 = arr.length;

      for(int var9 = 0; var9 < var14; ++var9) {
         String s = var13[var9];
         if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            data.setWideString((long)offset, s);
         } else {
            data.setString((long)offset, s);
         }

         offset += s.length() * charwidth;
         offset += charwidth;
      }

      rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 7, (Pointer)data, size);
      if (rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetStringArray(WinReg.HKEY root, String keyPath, String name, String[] arr) {
      registrySetStringArray(root, keyPath, name, arr, 0);
   }

   public static void registrySetStringArray(WinReg.HKEY root, String keyPath, String name, String[] arr, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetStringArray(phkKey.getValue(), name, arr);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registrySetBinaryValue(WinReg.HKEY hKey, String name, byte[] data) {
      int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 3, (byte[])data, data.length);
      if (rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registrySetBinaryValue(WinReg.HKEY root, String keyPath, String name, byte[] data) {
      registrySetBinaryValue(root, keyPath, name, data, 0);
   }

   public static void registrySetBinaryValue(WinReg.HKEY root, String keyPath, String name, byte[] data, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registrySetBinaryValue(phkKey.getValue(), name, data);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registryDeleteKey(WinReg.HKEY hKey, String keyName) {
      int rc = Advapi32.INSTANCE.RegDeleteKey(hKey, keyName);
      if (rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registryDeleteKey(WinReg.HKEY root, String keyPath, String keyName) {
      registryDeleteKey(root, keyPath, keyName, 0);
   }

   public static void registryDeleteKey(WinReg.HKEY root, String keyPath, String keyName, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registryDeleteKey(phkKey.getValue(), keyName);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static void registryDeleteValue(WinReg.HKEY hKey, String valueName) {
      int rc = Advapi32.INSTANCE.RegDeleteValue(hKey, valueName);
      if (rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static void registryDeleteValue(WinReg.HKEY root, String keyPath, String valueName) {
      registryDeleteValue(root, keyPath, valueName, 0);
   }

   public static void registryDeleteValue(WinReg.HKEY root, String keyPath, String valueName, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131103 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         try {
            registryDeleteValue(phkKey.getValue(), valueName);
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

      }
   }

   public static String[] registryGetKeys(WinReg.HKEY hKey) {
      IntByReference lpcSubKeys = new IntByReference();
      IntByReference lpcMaxSubKeyLen = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, (char[])null, (IntByReference)null, (IntByReference)null, lpcSubKeys, lpcMaxSubKeyLen, (IntByReference)null, (IntByReference)null, (IntByReference)null, (IntByReference)null, (IntByReference)null, (WinBase.FILETIME)null);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         ArrayList<String> keys = new ArrayList(lpcSubKeys.getValue());
         char[] name = new char[lpcMaxSubKeyLen.getValue() + 1];

         for(int i = 0; i < lpcSubKeys.getValue(); ++i) {
            IntByReference lpcchValueName = new IntByReference(lpcMaxSubKeyLen.getValue() + 1);
            rc = Advapi32.INSTANCE.RegEnumKeyEx(hKey, i, name, lpcchValueName, (IntByReference)null, (char[])null, (IntByReference)null, (WinBase.FILETIME)null);
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

            keys.add(Native.toString(name));
         }

         return (String[])keys.toArray(new String[0]);
      }
   }

   public static String[] registryGetKeys(WinReg.HKEY root, String keyPath) {
      return registryGetKeys(root, keyPath, 0);
   }

   public static String[] registryGetKeys(WinReg.HKEY root, String keyPath, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131097 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         String[] var5;
         try {
            var5 = registryGetKeys(phkKey.getValue());
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var5;
      }
   }

   public static WinReg.HKEYByReference registryGetKey(WinReg.HKEY root, String keyPath, int samDesired) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, samDesired, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         return phkKey;
      }
   }

   public static void registryCloseKey(WinReg.HKEY hKey) {
      int rc = Advapi32.INSTANCE.RegCloseKey(hKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      }
   }

   public static TreeMap<String, Object> registryGetValues(WinReg.HKEY hKey) {
      IntByReference lpcValues = new IntByReference();
      IntByReference lpcMaxValueNameLen = new IntByReference();
      IntByReference lpcMaxValueLen = new IntByReference();
      int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, (char[])null, (IntByReference)null, (IntByReference)null, (IntByReference)null, (IntByReference)null, (IntByReference)null, lpcValues, lpcMaxValueNameLen, lpcMaxValueLen, (IntByReference)null, (WinBase.FILETIME)null);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         TreeMap<String, Object> keyValues = new TreeMap();
         char[] name = new char[lpcMaxValueNameLen.getValue() + 1];
         Memory byteData = new Memory((long)(lpcMaxValueLen.getValue() + 2 * Native.WCHAR_SIZE));

         for(int i = 0; i < lpcValues.getValue(); ++i) {
            byteData.clear();
            IntByReference lpcchValueName = new IntByReference(lpcMaxValueNameLen.getValue() + 1);
            IntByReference lpcbData = new IntByReference(lpcMaxValueLen.getValue());
            IntByReference lpType = new IntByReference();
            rc = Advapi32.INSTANCE.RegEnumValue(hKey, i, name, lpcchValueName, (IntByReference)null, lpType, (Pointer)byteData, lpcbData);
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

            String nameString = Native.toString(name);
            if (lpcbData.getValue() == 0) {
               switch (lpType.getValue()) {
                  case 0:
                     keyValues.put(nameString, (Object)null);
                     break;
                  case 1:
                  case 2:
                     keyValues.put(nameString, new char[0]);
                     break;
                  case 3:
                     keyValues.put(nameString, new byte[0]);
                     break;
                  case 4:
                  case 5:
                  case 6:
                  default:
                     throw new RuntimeException("Unsupported empty type: " + lpType.getValue());
                  case 7:
                     keyValues.put(nameString, new String[0]);
               }
            } else {
               switch (lpType.getValue()) {
                  case 1:
                  case 2:
                     if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                        keyValues.put(nameString, byteData.getWideString(0L));
                     } else {
                        keyValues.put(nameString, byteData.getString(0L));
                     }
                     break;
                  case 3:
                     keyValues.put(nameString, byteData.getByteArray(0L, lpcbData.getValue()));
                     break;
                  case 4:
                     keyValues.put(nameString, byteData.getInt(0L));
                     break;
                  case 5:
                  case 6:
                  case 8:
                  case 9:
                  case 10:
                  default:
                     throw new RuntimeException("Unsupported type: " + lpType.getValue());
                  case 7:
                     ArrayList<String> result = new ArrayList();
                     int offset = 0;

                     while((long)offset < byteData.size()) {
                        String s;
                        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
                           s = byteData.getWideString((long)offset);
                           offset += s.length() * Native.WCHAR_SIZE;
                           offset += Native.WCHAR_SIZE;
                        } else {
                           s = byteData.getString((long)offset);
                           offset += s.length();
                           ++offset;
                        }

                        if (s.length() == 0) {
                           break;
                        }

                        result.add(s);
                     }

                     keyValues.put(nameString, result.toArray(new String[0]));
                     break;
                  case 11:
                     keyValues.put(nameString, byteData.getLong(0L));
               }
            }
         }

         return keyValues;
      }
   }

   public static TreeMap<String, Object> registryGetValues(WinReg.HKEY root, String keyPath) {
      return registryGetValues(root, keyPath, 0);
   }

   public static TreeMap<String, Object> registryGetValues(WinReg.HKEY root, String keyPath, int samDesiredExtra) {
      WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
      int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 131097 | samDesiredExtra, phkKey);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         TreeMap var5;
         try {
            var5 = registryGetValues(phkKey.getValue());
         } finally {
            rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
            if (rc != 0) {
               throw new Win32Exception(rc);
            }

         }

         return var5;
      }
   }

   public static InfoKey registryQueryInfoKey(WinReg.HKEY hKey, int lpcbSecurityDescriptor) {
      InfoKey infoKey = new InfoKey(hKey, lpcbSecurityDescriptor);
      int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, infoKey.lpClass, infoKey.lpcClass, (IntByReference)null, infoKey.lpcSubKeys, infoKey.lpcMaxSubKeyLen, infoKey.lpcMaxClassLen, infoKey.lpcValues, infoKey.lpcMaxValueNameLen, infoKey.lpcMaxValueLen, infoKey.lpcbSecurityDescriptor, infoKey.lpftLastWriteTime);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         return infoKey;
      }
   }

   public static EnumKey registryRegEnumKey(WinReg.HKEY hKey, int dwIndex) {
      EnumKey enumKey = new EnumKey(hKey, dwIndex);
      int rc = Advapi32.INSTANCE.RegEnumKeyEx(hKey, enumKey.dwIndex, enumKey.lpName, enumKey.lpcName, (IntByReference)null, enumKey.lpClass, enumKey.lpcbClass, enumKey.lpftLastWriteTime);
      if (rc != 0) {
         throw new Win32Exception(rc);
      } else {
         return enumKey;
      }
   }

   public static String getEnvironmentBlock(Map<String, String> environment) {
      StringBuilder out = new StringBuilder(environment.size() * 32);
      Iterator var2 = environment.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, String> entry = (Map.Entry)var2.next();
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         if (value != null) {
            out.append(key).append("=").append(value).append('\u0000');
         }
      }

      return out.append('\u0000').toString();
   }

   public static WinNT.ACE_HEADER[] getFileSecurity(String fileName, boolean compact) {
      int infoType = 4;
      int nLength = 1024;

      boolean repeat;
      Memory memory;
      do {
         repeat = false;
         memory = new Memory((long)nLength);
         IntByReference lpnSize = new IntByReference();
         boolean succeded = Advapi32.INSTANCE.GetFileSecurity(fileName, infoType, memory, nLength, lpnSize);
         int lengthNeeded;
         if (!succeded) {
            lengthNeeded = Kernel32.INSTANCE.GetLastError();
            memory.clear();
            if (122 != lengthNeeded) {
               throw new Win32Exception(lengthNeeded);
            }
         }

         lengthNeeded = lpnSize.getValue();
         if (nLength < lengthNeeded) {
            repeat = true;
            nLength = lengthNeeded;
            memory.clear();
         }
      } while(repeat);

      WinNT.SECURITY_DESCRIPTOR_RELATIVE sdr = new WinNT.SECURITY_DESCRIPTOR_RELATIVE(memory);
      WinNT.ACL dacl = sdr.getDiscretionaryACL();
      WinNT.ACE_HEADER[] aceStructures = dacl.getACEs();
      if (compact) {
         List<WinNT.ACE_HEADER> result = new ArrayList();
         Map<String, WinNT.ACCESS_ACEStructure> aceMap = new HashMap();
         WinNT.ACE_HEADER[] var11 = aceStructures;
         int var12 = aceStructures.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            WinNT.ACE_HEADER aceStructure = var11[var13];
            if (aceStructure instanceof WinNT.ACCESS_ACEStructure) {
               WinNT.ACCESS_ACEStructure accessACEStructure = (WinNT.ACCESS_ACEStructure)aceStructure;
               boolean inherted = (aceStructure.AceFlags & 31) != 0;
               String key = accessACEStructure.getSidString() + "/" + inherted + "/" + aceStructure.getClass().getName();
               WinNT.ACCESS_ACEStructure aceStructure2 = (WinNT.ACCESS_ACEStructure)aceMap.get(key);
               if (aceStructure2 != null) {
                  int accessMask = aceStructure2.Mask;
                  accessMask |= accessACEStructure.Mask;
                  aceStructure2.Mask = accessMask;
               } else {
                  aceMap.put(key, accessACEStructure);
                  result.add(aceStructure2);
               }
            } else {
               result.add(aceStructure);
            }
         }

         return (WinNT.ACE_HEADER[])result.toArray(new WinNT.ACE_HEADER[0]);
      } else {
         return aceStructures;
      }
   }

   private static Memory getSecurityDescriptorForFile(String absoluteFilePath) {
      int infoType = true;
      IntByReference lpnSize = new IntByReference();
      boolean succeeded = Advapi32.INSTANCE.GetFileSecurity(absoluteFilePath, 7, (Pointer)null, 0, lpnSize);
      int lastError;
      if (!succeeded) {
         lastError = Kernel32.INSTANCE.GetLastError();
         if (122 != lastError) {
            throw new Win32Exception(lastError);
         }
      }

      lastError = lpnSize.getValue();
      Memory securityDescriptorMemoryPointer = new Memory((long)lastError);
      succeeded = Advapi32.INSTANCE.GetFileSecurity(absoluteFilePath, 7, securityDescriptorMemoryPointer, lastError, lpnSize);
      if (!succeeded) {
         securityDescriptorMemoryPointer.clear();
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return securityDescriptorMemoryPointer;
      }
   }

   public static Memory getSecurityDescriptorForObject(String absoluteObjectPath, int objectType, boolean getSACL) {
      int infoType = 7 | (getSACL ? 8 : 0);
      PointerByReference ppSecurityDescriptor = new PointerByReference();
      int lastError = Advapi32.INSTANCE.GetNamedSecurityInfo(absoluteObjectPath, objectType, infoType, (PointerByReference)null, (PointerByReference)null, (PointerByReference)null, (PointerByReference)null, ppSecurityDescriptor);
      if (lastError != 0) {
         throw new Win32Exception(lastError);
      } else {
         int nLength = Advapi32.INSTANCE.GetSecurityDescriptorLength(ppSecurityDescriptor.getValue());
         Memory memory = new Memory((long)nLength);
         Pointer secValue = ppSecurityDescriptor.getValue();

         Memory var10;
         try {
            byte[] data = secValue.getByteArray(0L, nLength);
            memory.write(0L, (byte[])data, 0, nLength);
            var10 = memory;
         } finally {
            Kernel32Util.freeLocalMemory(secValue);
         }

         return var10;
      }
   }

   public static void setSecurityDescriptorForObject(String absoluteObjectPath, int objectType, WinNT.SECURITY_DESCRIPTOR_RELATIVE securityDescriptor, boolean setOwner, boolean setGroup, boolean setDACL, boolean setSACL, boolean setDACLProtectedStatus, boolean setSACLProtectedStatus) {
      WinNT.PSID psidOwner = securityDescriptor.getOwner();
      WinNT.PSID psidGroup = securityDescriptor.getGroup();
      WinNT.ACL dacl = securityDescriptor.getDiscretionaryACL();
      WinNT.ACL sacl = securityDescriptor.getSystemACL();
      int infoType = 0;
      if (setOwner) {
         if (psidOwner == null) {
            throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain owner");
         }

         if (!Advapi32.INSTANCE.IsValidSid(psidOwner)) {
            throw new IllegalArgumentException("Owner PSID is invalid");
         }

         infoType |= 1;
      }

      if (setGroup) {
         if (psidGroup == null) {
            throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain group");
         }

         if (!Advapi32.INSTANCE.IsValidSid(psidGroup)) {
            throw new IllegalArgumentException("Group PSID is invalid");
         }

         infoType |= 2;
      }

      if (setDACL) {
         if (dacl == null) {
            throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain DACL");
         }

         if (!Advapi32.INSTANCE.IsValidAcl(dacl.getPointer())) {
            throw new IllegalArgumentException("DACL is invalid");
         }

         infoType |= 4;
      }

      if (setSACL) {
         if (sacl == null) {
            throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain SACL");
         }

         if (!Advapi32.INSTANCE.IsValidAcl(sacl.getPointer())) {
            throw new IllegalArgumentException("SACL is invalid");
         }

         infoType |= 8;
      }

      if (setDACLProtectedStatus) {
         if ((securityDescriptor.Control & 4096) != 0) {
            infoType |= Integer.MIN_VALUE;
         } else if ((securityDescriptor.Control & 4096) == 0) {
            infoType |= 536870912;
         }
      }

      if (setSACLProtectedStatus) {
         if ((securityDescriptor.Control & 8192) != 0) {
            infoType |= 1073741824;
         } else if ((securityDescriptor.Control & 8192) == 0) {
            infoType |= 268435456;
         }
      }

      int lastError = Advapi32.INSTANCE.SetNamedSecurityInfo(absoluteObjectPath, objectType, infoType, setOwner ? psidOwner.getPointer() : null, setGroup ? psidGroup.getPointer() : null, setDACL ? dacl.getPointer() : null, setSACL ? sacl.getPointer() : null);
      if (lastError != 0) {
         throw new Win32Exception(lastError);
      }
   }

   public static boolean accessCheck(File file, AccessCheckPermission permissionToCheck) {
      Memory securityDescriptorMemoryPointer = getSecurityDescriptorForFile(file.getAbsolutePath().replace('/', '\\'));
      WinNT.HANDLEByReference openedAccessToken = new WinNT.HANDLEByReference();
      WinNT.HANDLEByReference duplicatedToken = new WinNT.HANDLEByReference();
      Win32Exception err = null;

      boolean var14;
      try {
         int desireAccess = 131086;
         WinNT.HANDLE hProcess = Kernel32.INSTANCE.GetCurrentProcess();
         if (!Advapi32.INSTANCE.OpenProcessToken(hProcess, desireAccess, openedAccessToken)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

         if (!Advapi32.INSTANCE.DuplicateToken(openedAccessToken.getValue(), 2, duplicatedToken)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

         WinNT.GENERIC_MAPPING mapping = new WinNT.GENERIC_MAPPING();
         mapping.genericRead = new WinDef.DWORD(1179785L);
         mapping.genericWrite = new WinDef.DWORD(1179926L);
         mapping.genericExecute = new WinDef.DWORD(1179808L);
         mapping.genericAll = new WinDef.DWORD(2032127L);
         WinDef.DWORDByReference rights = new WinDef.DWORDByReference(new WinDef.DWORD((long)permissionToCheck.getCode()));
         Advapi32.INSTANCE.MapGenericMask(rights, mapping);
         WinNT.PRIVILEGE_SET privileges = new WinNT.PRIVILEGE_SET(1);
         privileges.PrivilegeCount = new WinDef.DWORD(0L);
         WinDef.DWORDByReference privilegeLength = new WinDef.DWORDByReference(new WinDef.DWORD((long)privileges.size()));
         WinDef.DWORDByReference grantedAccess = new WinDef.DWORDByReference();
         WinDef.BOOLByReference result = new WinDef.BOOLByReference();
         if (!Advapi32.INSTANCE.AccessCheck(securityDescriptorMemoryPointer, duplicatedToken.getValue(), rights.getValue(), mapping, privileges, privilegeLength, grantedAccess, result)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

         var14 = result.getValue().booleanValue();
      } catch (Win32Exception var23) {
         err = var23;
         throw var23;
      } finally {
         try {
            Kernel32Util.closeHandleRefs(openedAccessToken, duplicatedToken);
         } catch (Win32Exception var22) {
            if (err == null) {
               err = var22;
            } else {
               err.addSuppressedReflected(var22);
            }
         }

         if (securityDescriptorMemoryPointer != null) {
            securityDescriptorMemoryPointer.clear();
         }

         if (err != null) {
            throw err;
         }

      }

      return var14;
   }

   public static WinNT.SECURITY_DESCRIPTOR_RELATIVE getFileSecurityDescriptor(File file, boolean getSACL) {
      Memory securityDesc = getSecurityDescriptorForObject(file.getAbsolutePath().replaceAll("/", "\\"), 1, getSACL);
      WinNT.SECURITY_DESCRIPTOR_RELATIVE sdr = new WinNT.SECURITY_DESCRIPTOR_RELATIVE(securityDesc);
      return sdr;
   }

   public static void setFileSecurityDescriptor(File file, WinNT.SECURITY_DESCRIPTOR_RELATIVE securityDescriptor, boolean setOwner, boolean setGroup, boolean setDACL, boolean setSACL, boolean setDACLProtectedStatus, boolean setSACLProtectedStatus) {
      setSecurityDescriptorForObject(file.getAbsolutePath().replaceAll("/", "\\"), 1, securityDescriptor, setOwner, setGroup, setDACL, setSACL, setDACLProtectedStatus, setSACLProtectedStatus);
   }

   public static void encryptFile(File file) {
      String lpFileName = file.getAbsolutePath();
      if (!Advapi32.INSTANCE.EncryptFile(lpFileName)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      }
   }

   public static void decryptFile(File file) {
      String lpFileName = file.getAbsolutePath();
      if (!Advapi32.INSTANCE.DecryptFile(lpFileName, new WinDef.DWORD(0L))) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      }
   }

   public static int fileEncryptionStatus(File file) {
      WinDef.DWORDByReference status = new WinDef.DWORDByReference();
      String lpFileName = file.getAbsolutePath();
      if (!Advapi32.INSTANCE.FileEncryptionStatus(lpFileName, status)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return status.getValue().intValue();
      }
   }

   public static void disableEncryption(File directory, boolean disable) {
      String dirPath = directory.getAbsolutePath();
      if (!Advapi32.INSTANCE.EncryptionDisable(dirPath, disable)) {
         throw new Win32Exception(Native.getLastError());
      }
   }

   public static void backupEncryptedFile(File src, File destDir) {
      if (!destDir.isDirectory()) {
         throw new IllegalArgumentException("destDir must be a directory.");
      } else {
         WinDef.ULONG readFlag = new WinDef.ULONG(0L);
         WinDef.ULONG writeFlag = new WinDef.ULONG(1L);
         if (src.isDirectory()) {
            writeFlag.setValue(3L);
         }

         String srcFileName = src.getAbsolutePath();
         PointerByReference pvContext = new PointerByReference();
         if (Advapi32.INSTANCE.OpenEncryptedFileRaw(srcFileName, readFlag, pvContext) != 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            WinBase.FE_EXPORT_FUNC pfExportCallback = new WinBase.FE_EXPORT_FUNC() {
               public WinDef.DWORD callback(Pointer pbData, Pointer pvCallbackContext, WinDef.ULONG ulLength) {
                  byte[] arr = pbData.getByteArray(0L, ulLength.intValue());

                  try {
                     outputStream.write(arr);
                  } catch (IOException var6) {
                     throw new RuntimeException(var6);
                  }

                  return new WinDef.DWORD(0L);
               }
            };
            if (Advapi32.INSTANCE.ReadEncryptedFileRaw(pfExportCallback, (Pointer)null, pvContext.getValue()) != 0) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            } else {
               try {
                  outputStream.close();
               } catch (IOException var11) {
                  throw new RuntimeException(var11);
               }

               Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());
               String destFileName = destDir.getAbsolutePath() + File.separator + src.getName();
               pvContext = new PointerByReference();
               if (Advapi32.INSTANCE.OpenEncryptedFileRaw(destFileName, writeFlag, pvContext) != 0) {
                  throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
               } else {
                  final IntByReference elementsReadWrapper = new IntByReference(0);
                  WinBase.FE_IMPORT_FUNC pfImportCallback = new WinBase.FE_IMPORT_FUNC() {
                     public WinDef.DWORD callback(Pointer pbData, Pointer pvCallbackContext, WinDef.ULONGByReference ulLength) {
                        int elementsRead = elementsReadWrapper.getValue();
                        int remainingElements = outputStream.size() - elementsRead;
                        int length = Math.min(remainingElements, ulLength.getValue().intValue());
                        pbData.write(0L, outputStream.toByteArray(), elementsRead, length);
                        elementsReadWrapper.setValue(elementsRead + length);
                        ulLength.setValue(new WinDef.ULONG((long)length));
                        return new WinDef.DWORD(0L);
                     }
                  };
                  if (Advapi32.INSTANCE.WriteEncryptedFileRaw(pfImportCallback, (Pointer)null, pvContext.getValue()) != 0) {
                     throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                  } else {
                     Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());
                  }
               }
            }
         }
      }
   }

   public static class Privilege implements Closeable {
      private boolean currentlyImpersonating = false;
      private boolean privilegesEnabled = false;
      private final WinNT.LUID[] pLuids;

      public Privilege(String... privileges) throws IllegalArgumentException, Win32Exception {
         this.pLuids = new WinNT.LUID[privileges.length];
         int i = 0;
         String[] var3 = privileges;
         int var4 = privileges.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String p = var3[var5];
            this.pLuids[i] = new WinNT.LUID();
            if (!Advapi32.INSTANCE.LookupPrivilegeValue((String)null, p, this.pLuids[i])) {
               throw new IllegalArgumentException("Failed to find privilege \"" + privileges[i] + "\" - " + Kernel32.INSTANCE.GetLastError());
            }

            ++i;
         }

      }

      public void close() {
         this.disable();
      }

      public Privilege enable() throws Win32Exception {
         if (this.privilegesEnabled) {
            return this;
         } else {
            WinNT.HANDLEByReference phThreadToken = new WinNT.HANDLEByReference();

            try {
               phThreadToken.setValue(this.getThreadToken());
               WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);

               for(int i = 0; i < this.pLuids.length; ++i) {
                  tp.Privileges[i] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[i], new WinDef.DWORD(2L));
               }

               if (!Advapi32.INSTANCE.AdjustTokenPrivileges(phThreadToken.getValue(), false, tp, 0, (WinNT.TOKEN_PRIVILEGES)null, (IntByReference)null)) {
                  throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
               }

               this.privilegesEnabled = true;
            } catch (Win32Exception var8) {
               if (this.currentlyImpersonating) {
                  Advapi32.INSTANCE.SetThreadToken((WinNT.HANDLEByReference)null, (WinNT.HANDLE)null);
                  this.currentlyImpersonating = false;
               } else if (this.privilegesEnabled) {
                  WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);

                  for(int i = 0; i < this.pLuids.length; ++i) {
                     tp.Privileges[i] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[i], new WinDef.DWORD(0L));
                  }

                  Advapi32.INSTANCE.AdjustTokenPrivileges(phThreadToken.getValue(), false, tp, 0, (WinNT.TOKEN_PRIVILEGES)null, (IntByReference)null);
                  this.privilegesEnabled = false;
               }

               throw var8;
            } finally {
               if (phThreadToken.getValue() != WinBase.INVALID_HANDLE_VALUE && phThreadToken.getValue() != null) {
                  Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
                  phThreadToken.setValue((WinNT.HANDLE)null);
               }

            }

            return this;
         }
      }

      public void disable() throws Win32Exception {
         WinNT.HANDLEByReference phThreadToken = new WinNT.HANDLEByReference();

         try {
            phThreadToken.setValue(this.getThreadToken());
            if (this.currentlyImpersonating) {
               Advapi32.INSTANCE.SetThreadToken((WinNT.HANDLEByReference)null, (WinNT.HANDLE)null);
            } else if (this.privilegesEnabled) {
               WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);

               for(int i = 0; i < this.pLuids.length; ++i) {
                  tp.Privileges[i] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[i], new WinDef.DWORD(0L));
               }

               Advapi32.INSTANCE.AdjustTokenPrivileges(phThreadToken.getValue(), false, tp, 0, (WinNT.TOKEN_PRIVILEGES)null, (IntByReference)null);
               this.privilegesEnabled = false;
            }
         } finally {
            if (phThreadToken.getValue() != WinBase.INVALID_HANDLE_VALUE && phThreadToken.getValue() != null) {
               Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
               phThreadToken.setValue((WinNT.HANDLE)null);
            }

         }

      }

      private WinNT.HANDLE getThreadToken() throws Win32Exception {
         WinNT.HANDLEByReference phThreadToken = new WinNT.HANDLEByReference();
         WinNT.HANDLEByReference phProcessToken = new WinNT.HANDLEByReference();

         try {
            if (!Advapi32.INSTANCE.OpenThreadToken(Kernel32.INSTANCE.GetCurrentThread(), 32, false, phThreadToken)) {
               int lastError = Kernel32.INSTANCE.GetLastError();
               if (1008 != lastError) {
                  throw new Win32Exception(lastError);
               }

               if (!Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 2, phProcessToken)) {
                  throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
               }

               if (!Advapi32.INSTANCE.DuplicateTokenEx(phProcessToken.getValue(), 36, (WinBase.SECURITY_ATTRIBUTES)null, 2, 2, phThreadToken)) {
                  throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
               }

               if (!Advapi32.INSTANCE.SetThreadToken((WinNT.HANDLEByReference)null, phThreadToken.getValue())) {
                  throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
               }

               this.currentlyImpersonating = true;
            }
         } catch (Win32Exception var7) {
            if (phThreadToken.getValue() != WinBase.INVALID_HANDLE_VALUE && phThreadToken.getValue() != null) {
               Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
               phThreadToken.setValue((WinNT.HANDLE)null);
            }

            throw var7;
         } finally {
            if (phProcessToken.getValue() != WinBase.INVALID_HANDLE_VALUE && phProcessToken.getValue() != null) {
               Kernel32.INSTANCE.CloseHandle(phProcessToken.getValue());
               phProcessToken.setValue((WinNT.HANDLE)null);
            }

         }

         return phThreadToken.getValue();
      }
   }

   public static enum AccessCheckPermission {
      READ(Integer.MIN_VALUE),
      WRITE(1073741824),
      EXECUTE(536870912);

      final int code;

      private AccessCheckPermission(int code) {
         this.code = code;
      }

      public int getCode() {
         return this.code;
      }
   }

   public static class EventLogIterator implements Iterable<EventLogRecord>, Iterator<EventLogRecord> {
      private WinNT.HANDLE _h;
      private Memory _buffer;
      private boolean _done;
      private int _dwRead;
      private Pointer _pevlr;
      private int _flags;

      public EventLogIterator(String sourceName) {
         this((String)null, sourceName, 4);
      }

      public EventLogIterator(String serverName, String sourceName, int flags) {
         this._buffer = new Memory(65536L);
         this._done = false;
         this._dwRead = 0;
         this._pevlr = null;
         this._flags = flags;
         this._h = Advapi32.INSTANCE.OpenEventLog(serverName, sourceName);
         if (this._h == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }
      }

      private boolean read() {
         if (!this._done && this._dwRead <= 0) {
            IntByReference pnBytesRead = new IntByReference();
            IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
            if (!Advapi32.INSTANCE.ReadEventLog(this._h, 1 | this._flags, 0, this._buffer, (int)this._buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
               int rc = Kernel32.INSTANCE.GetLastError();
               if (rc != 122) {
                  this.close();
                  if (rc != 38) {
                     throw new Win32Exception(rc);
                  }

                  return false;
               }

               this._buffer = new Memory((long)pnMinNumberOfBytesNeeded.getValue());
               if (!Advapi32.INSTANCE.ReadEventLog(this._h, 1 | this._flags, 0, this._buffer, (int)this._buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
                  throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
               }
            }

            this._dwRead = pnBytesRead.getValue();
            this._pevlr = this._buffer;
            return true;
         } else {
            return false;
         }
      }

      public void close() {
         this._done = true;
         if (this._h != null) {
            if (!Advapi32.INSTANCE.CloseEventLog(this._h)) {
               throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            this._h = null;
         }

      }

      public Iterator<EventLogRecord> iterator() {
         return this;
      }

      public boolean hasNext() {
         this.read();
         return !this._done;
      }

      public EventLogRecord next() {
         this.read();
         EventLogRecord record = new EventLogRecord(this._pevlr);
         this._dwRead -= record.getLength();
         this._pevlr = this._pevlr.share((long)record.getLength());
         return record;
      }

      public void remove() {
      }
   }

   public static class EventLogRecord {
      private WinNT.EVENTLOGRECORD _record;
      private String _source;
      private byte[] _data;
      private String[] _strings;

      public WinNT.EVENTLOGRECORD getRecord() {
         return this._record;
      }

      public int getInstanceId() {
         return this._record.EventID.intValue();
      }

      /** @deprecated */
      @Deprecated
      public int getEventId() {
         return this._record.EventID.intValue();
      }

      public String getSource() {
         return this._source;
      }

      public int getStatusCode() {
         return this._record.EventID.intValue() & '\uffff';
      }

      public int getRecordNumber() {
         return this._record.RecordNumber.intValue();
      }

      public int getLength() {
         return this._record.Length.intValue();
      }

      public String[] getStrings() {
         return this._strings;
      }

      public EventLogType getType() {
         switch (this._record.EventType.intValue()) {
            case 0:
            case 4:
               return Advapi32Util.EventLogType.Informational;
            case 1:
               return Advapi32Util.EventLogType.Error;
            case 2:
               return Advapi32Util.EventLogType.Warning;
            case 3:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
               throw new RuntimeException("Invalid type: " + this._record.EventType.intValue());
            case 8:
               return Advapi32Util.EventLogType.AuditSuccess;
            case 16:
               return Advapi32Util.EventLogType.AuditFailure;
         }
      }

      public byte[] getData() {
         return this._data;
      }

      public EventLogRecord(Pointer pevlr) {
         this._record = new WinNT.EVENTLOGRECORD(pevlr);
         this._source = pevlr.getWideString((long)this._record.size());
         if (this._record.DataLength.intValue() > 0) {
            this._data = pevlr.getByteArray((long)this._record.DataOffset.intValue(), this._record.DataLength.intValue());
         }

         if (this._record.NumStrings.intValue() > 0) {
            ArrayList<String> strings = new ArrayList();
            int count = this._record.NumStrings.intValue();

            for(long offset = (long)this._record.StringOffset.intValue(); count > 0; --count) {
               String s = pevlr.getWideString(offset);
               strings.add(s);
               offset += (long)(s.length() * Native.WCHAR_SIZE);
               offset += (long)Native.WCHAR_SIZE;
            }

            this._strings = (String[])strings.toArray(new String[0]);
         }

      }
   }

   public static enum EventLogType {
      Error,
      Warning,
      Informational,
      AuditSuccess,
      AuditFailure;
   }

   public static class EnumKey {
      public WinReg.HKEY hKey;
      public int dwIndex = 0;
      public char[] lpName = new char[255];
      public IntByReference lpcName = new IntByReference(255);
      public char[] lpClass = new char[255];
      public IntByReference lpcbClass = new IntByReference(255);
      public WinBase.FILETIME lpftLastWriteTime = new WinBase.FILETIME();

      public EnumKey() {
      }

      public EnumKey(WinReg.HKEY hKey, int dwIndex) {
         this.hKey = hKey;
         this.dwIndex = dwIndex;
      }
   }

   public static class InfoKey {
      public WinReg.HKEY hKey;
      public char[] lpClass = new char[260];
      public IntByReference lpcClass = new IntByReference(260);
      public IntByReference lpcSubKeys = new IntByReference();
      public IntByReference lpcMaxSubKeyLen = new IntByReference();
      public IntByReference lpcMaxClassLen = new IntByReference();
      public IntByReference lpcValues = new IntByReference();
      public IntByReference lpcMaxValueNameLen = new IntByReference();
      public IntByReference lpcMaxValueLen = new IntByReference();
      public IntByReference lpcbSecurityDescriptor = new IntByReference();
      public WinBase.FILETIME lpftLastWriteTime = new WinBase.FILETIME();

      public InfoKey() {
      }

      public InfoKey(WinReg.HKEY hKey, int securityDescriptor) {
         this.hKey = hKey;
         this.lpcbSecurityDescriptor = new IntByReference(securityDescriptor);
      }
   }

   public static class Account {
      public String name;
      public String domain;
      public byte[] sid;
      public String sidString;
      public int accountType;
      public String fqn;
   }
}
